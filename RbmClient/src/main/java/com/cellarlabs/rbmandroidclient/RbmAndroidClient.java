package com.cellarlabs.rbmandroidclient;

import android.util.Log;

import com.github.nkzawa.engineio.client.Socket;

import org.apache.http.auth.AUTH;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vhanssen on 02/06/15.
 */
public class RbmAndroidClient {
    private final Emitter emitter = new Emitter();
    Socket socket = null;
    private AtomicInteger nextreqid = new AtomicInteger(1);
    private AtomicInteger nexttagid = new AtomicInteger(1);
    private static final int MAXREQID = 64000;
    private static final int MAXTAGID = 64000;

    private Authenticate auth = null;

    HashMap<Integer,ArrayList<Listener>> tags = new HashMap<>();

    public RbmAndroidClient() {

    }

    public RbmAndroidClient withServer(String server) {
        try {
            init(server);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    protected void init(String server) throws URISyntaxException {
        socket = new Socket(server);
        final RbmAndroidClient self = this;
        socket.on(Socket.EVENT_OPEN, new com.github.nkzawa.emitter.Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });
        socket.on(Socket.EVENT_MESSAGE, new com.github.nkzawa.emitter.Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("RBM", (String) args[0]);
                Request req = new Request((String) args[0]);
                if (req.hasReqid()) {
                    emitter.emit("_:"+req.getReqid(), req);
                } else {
                    emitter.emit(req.getCommand(), req);
                }
            }
        });
        socket.open();
    }

    public void on(String command, final Listener fn) {
        emitter.on(command, fn);
    }

    public void once(String command, final Listener fn) {
        emitter.once(command, fn);
    }

    public void on(String command, Integer tag, final Listener fn) {
        emitter.on(command, fn);
    }

    protected int getNextUniqueId() {
        int id = 0;
        do {
            nextreqid.compareAndSet(this.MAXREQID, 1);
            id = nextreqid.getAndIncrement();
        } while(emitter.hasListeners("_:" + id));
        return id;
    }

    public void send(Request req) {
        socket.send(req.data());
    }

    public void send(Request req, final Listener fn) {
        if (fn!=null) {
            int id = getNextUniqueId();
            req.setReqid(id);
            once("_:" + id, fn);
        }
        send(req);
    }

    public void send(String req) {
        socket.send(req);
    }

    public Integer getUniqueTag() {
        int id = 0;
        do {
            nexttagid.compareAndSet(this.MAXTAGID, 1);
            id = nexttagid.getAndIncrement();
        } while(tags.containsKey(id));

        ArrayList<Listener> list = new ArrayList<Listener>();
        tags.put(id, list);
        return id;
    }

    /**
     * Remove all callbacks associated with a tag
     *
     * This should always be called in the onStop() method of an Activity
     * @param tag
     */
    public void cancelCallbacks(int tag) {
        for(Listener fn: tags.get(tag)) {
            emitter.off(fn);
        }
        tags.remove(tag);
    }

    /**
     * Authentication object
     */
    public void setAuthClass(Authenticate auth) {
        if (this.auth!=null)
            this.auth.removeListeners(this);
        this.auth = auth;
        auth.addListeners();
        auth.onAuthenticate();
    }

    public Authenticate getAuthClass() {
        return this.auth;
    }

    public void onTerminate() {
        socket.close();
        emitter.off();
        tags.clear();
        if (this.auth!=null)
            auth.onTerminate();
    }
}
