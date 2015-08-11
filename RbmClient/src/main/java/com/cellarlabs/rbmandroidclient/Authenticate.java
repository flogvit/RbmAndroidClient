package com.cellarlabs.rbmandroidclient;

import android.util.Log;

/**
 * Created by vhanssen on 04/08/15.
 */
abstract public class Authenticate {
    protected boolean authenticated = false;
    protected Integer RBM_TAG = null;
    protected RbmAndroidClient client = null;
    protected Integer uid = null;

    abstract public boolean onAuthenticate();

    public Authenticate(RbmAndroidClient client) {
        this.client = client;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void addListeners() {
        removeListeners(client);
        RBM_TAG = client.getUniqueTag();

        client.on("user.authenticated", new Listener(RBM_TAG) {
            @Override
            public void onResponse(Request req) {
                authenticated = req.has("auth") ? req.getBoolean("auth") : false;
                uid = req.has("uid")  ? req.getInteger("uid") : null;
                Log.d("RBM", "Setting uid: " + uid);
            }
        });
        client.on("user.authenticate", new Listener(RBM_TAG) {
            @Override
            public void onResponse(Request req) {
                authenticated = false;
                onAuthenticate();
            }
        });
    }

    public void removeListeners(RbmAndroidClient client) {
        if (RBM_TAG!=null)
            client.cancelCallbacks(RBM_TAG);
    }

    public void onTerminate() {
        client = null;
    }
}
