package com.cellarlabs.rbmandroidclient;

import android.os.Looper;

import com.badoo.mobile.util.WeakHandler;

/**
 * Created by vhanssen on 02/06/15.
 */
abstract public class Listener {
    private int tag = 0;
    private boolean mainthread = false;
    public Listener() {

    }

    public Listener(boolean mainthread) {
        this.mainthread = mainthread;
    }

    public Listener(final int tag) {
        this.tag = tag;
    }

    public Listener(final int tag, boolean mainthread) {
        this.tag = tag;
        this.mainthread = mainthread;
    }

    public int getTag() {
        return this.tag;
    }

    public void doResponse(Request req) {
        if (mainthread)
            onResponseMain(req);
        else
            onResponse(req);
    }

    public void onResponseMain(final Request req) {
        WeakHandler mainHandler = new WeakHandler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                onResponse(req);
            }
        });
    }

    abstract public void onResponse(Request req);
}
