package com.cellarlabs.rbmandroidclient;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vhanssen on 02/06/15.
 */
public class RbmAndroidClient {
    private final RbmEmitter emitter = new RbmEmitter();
    Socket socket = null;
    private AtomicInteger nextreqid = new AtomicInteger(1);
    private AtomicInteger nexttagid = new AtomicInteger(1);
    private static final int MAXREQID = 64000;
    private static final int MAXTAGID = 64000;

    HashMap<Integer,ArrayList<RbmListener>> tags = new HashMap<>();

    public RbmAndroidClient(String server) {
        try {
            init(server);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    protected void init(String server) throws URISyntaxException {
        socket = new Socket(server);
        final RbmAndroidClient self = this;
        socket.on(Socket.EVENT_OPEN, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });
        socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                RbmRequest req = new RbmRequest((String) args[0]);
                if (req.hasReqid()) {
                    emitter.emit("_:"+req.getReqid(), req);
                } else {
                    emitter.emit(req.getCommand(), req);
                }
            }
        });
        socket.open();
    }

    public void on(String command, final RbmListener fn) {
        emitter.on(command, fn);
    }

    public void once(String command, final RbmListener fn) {
        emitter.once(command, fn);
    }

    public void on(String command, Integer tag, final RbmListener fn) {
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

    public void send(RbmRequest req) {
        socket.send(req.data());
    }

    public void send(RbmRequest req, final RbmListener fn) {
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

        ArrayList<RbmListener> list = new ArrayList<RbmListener>();
        tags.put(id, list);
        return id;
    }

    /**
     * Remove all callbacks associated with a tag
     *
     * This should always be called in the onStop() method of an Activity
     * @param tag
     */
    public void cancelCallbacks(Integer tag) {
        for(RbmListener fn: tags.get(tag)) {
            emitter.off(fn);
        }
        tags.remove(tag);
    }
}
