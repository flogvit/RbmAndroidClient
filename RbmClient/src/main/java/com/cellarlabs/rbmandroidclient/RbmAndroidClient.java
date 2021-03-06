package com.cellarlabs.rbmandroidclient;

import android.content.Context;
import android.util.Log;

import com.github.nkzawa.engineio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
    private AckStore ackstore = null;

    private int retry = 0;
    private String server = "";
    private String module = "";
    private boolean inshutdown = false;
    private boolean ininit = false;
    private boolean open = false;
    private boolean stopped = true;

    private Authenticate auth = null;
    final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
    private ScheduledFuture execRun;

    HashMap<Integer, ArrayList<Listener>> tags = new HashMap<>();

    private Context ctx = null;

    public RbmAndroidClient() {
        setup();
    }

    public RbmAndroidClient(Context ctx) {
        setup();
        if (ctx!=null)
            setApplicationContext(ctx);
    }

    public void setup() {
        Log.d("RBM", "Doing setup");
        ackstore = AckStore.getAckStore();
        ackstore.setContext(ctx);
        on("server.reconnect", new Listener() {
            @Override
            public void onResponse(Request req) {
                withServer(req.getString("server"));
            }
        });
    }

    public RbmAndroidClient withServer(String server) {
        this.server = server;
        doInit();
        return this;
    }

    protected void doInit() {
        inshutdown = false;
        if (execRun!=null)
            execRun.cancel(true);
        Log.d("RBM", "Doing doInit");
        if (++retry < 5) {
            try {
                init();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
             execRun = exec.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        init();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }, 5, TimeUnit.SECONDS);
        }

    }

    protected void init() throws URISyntaxException {
        if (ininit) return;
        ininit = true;
        Log.d("RBM", "Doing init with server "+this.server);
        if (socket!=null) socket.close();
        socket = new Socket(this.server);
        socket.on(Socket.EVENT_OPEN, new com.github.nkzawa.emitter.Emitter.Listener() {
            @Override
            public void call(Object... args) {
                retry = 0;
                ininit = false;
                emitter.emit("server.open", null);
                Log.d("RBM", "socket open");
                open = true;
            }
        });
        socket.on(Socket.EVENT_MESSAGE, new com.github.nkzawa.emitter.Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("RBM", "<-- "+(String) args[0]);
                Request req = new Request((String) args[0]);
                if (req.hasReqid()) {
                    emitter.emit("_:" + req.getReqid(), req);
                    ackstore.remove(req);
                }
                emitter.emit(req.getCommand(), req);
            }
        });
        socket.on(Socket.EVENT_CLOSE, new com.github.nkzawa.emitter.Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ininit = false;
                Log.d("RBM", "Got socket close");
                emitter.emit("server.closed", null);
                open = false;
                if (!inshutdown)
                    doInit();
            }
        });
        socket.on(Socket.EVENT_ERROR, new com.github.nkzawa.emitter.Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("RBM", "Got socket error");
                emitter.emit("server.error", null);
//                doInit();
            }
        });
        Log.d("RBM", "Trying to open socket");
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

    public void off(String command, final Listener fn) { emitter.off(command, fn); }

    protected int getNextUniqueId() {
        int id = 0;
        do {
            nextreqid.compareAndSet(this.MAXREQID, 1);
            id = nextreqid.getAndIncrement();
        } while (emitter.hasListeners("_:" + id) || ackstore.hasId(id));
        return id;
    }

    public void send(Request req) {
        Log.d("RBM", "--> "+req.data());
        socket.send(req.data());
    }

    public void send(Request req, final Listener fn) {
        if (fn != null) {
            if (!req.hasReqid())
                req.setReqid(getNextUniqueId());
            once("_:" + req.getReqid(), fn);
        }
        send(req);
    }

    public void sendAcknowledge(Request req, final Listener fn) {
        if (!req.hasReqid())
            req.setReqid(getNextUniqueId());
        ackstore.add(req);
        send(req, fn);
    }

    public void send(String req) {
        Log.d("RBM", "--> " + req);
        socket.send(req);
    }

    public Integer getUniqueTag() {
        int id = 0;
        do {
            nexttagid.compareAndSet(this.MAXTAGID, 1);
            id = nexttagid.getAndIncrement();
        } while (tags.containsKey(id));

        ArrayList<Listener> list = new ArrayList<Listener>();
        tags.put(id, list);
        return id;
    }

    /**
     * Remove all callbacks associated with a tag
     * <p/>
     * This should always be called in the onStop() method of an Activity
     *
     * @param tag
     */
    public void cancelCallbacks(int tag) {
        if (!tags.containsKey(tag)) return;
        for (Listener fn : tags.get(tag)) {
            emitter.off(fn);
        }
        tags.remove(tag);
    }

    /**
     * Authentication object
     */
    public void setAuthClass(Authenticate auth) {
        if (this.auth != null)
            this.auth.removeListeners(this);
        this.auth = auth;
        auth.addListeners();
        auth.onAuthenticate();
    }

    public Authenticate getAuthClass() {
        return this.auth;
    }

    public boolean isAuthenticated() {
        if (this.auth==null) return false;
        return this.auth.isAuthenticated();
    }

    public Integer getUid() {
        if (this.auth==null) return null;
        if (!this.auth.isAuthenticated()) return null;
        return this.auth.getUid();
    }

    public void close() {
        inshutdown = true;
        if (execRun!=null)
            execRun.cancel(true);
        socket.close();
    }

    public void onStop() {
        inshutdown = true;
        if (execRun!=null)
            execRun.cancel(true);
        emitter.off();
        tags.clear();
        if (this.auth != null)
            auth.onStop();
        if (this.ackstore!=null)
            this.ackstore.onStop();
        socket.close();
        stopped = true;
    }

    public void onStart() {
        if (stopped) {
            stopped = false;
            setup();
            doInit();
        }
    }

    public void setApplicationContext(Context ctx) {
        if (ctx==null) return;
        this.ctx = ctx.getApplicationContext();
        ackstore.setContext(this.ctx);
    }

    public Context getApplicationContext() {
        return ctx;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModule() {
        return this.module;
    }

    public boolean isOpen() {
        return this.open;
    }
}
