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
    protected Listener needAuthenticateListener = null;
    protected Listener authenticatedListener = null;
    protected Listener failedAuthenticationListener = null;

    abstract public boolean onAuthenticate();

    public Authenticate(RbmAndroidClient client) {
        this.client = client;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Integer getUid() {
        return uid;
    }

    public void addListeners() {
        removeListeners(client);
        RBM_TAG = client.getUniqueTag();

        client.on("server.closed", new Listener(RBM_TAG) {
            @Override
            public void onResponse(Request req) {
                authenticated = false;
            }
        });
        client.on("user.authenticated", new Listener(RBM_TAG) {
            @Override
            public void onResponse(Request req) {
                authenticated = req.has("auth") ? req.getBoolean("auth") : false;
                uid = req.has("uid")  ? req.getInteger("uid") : null;
                Log.d("RBM", "Setting uid: " + uid);
                if (authenticated)
                    callAuthenticated(req);
            }
        });
        client.on("user.authenticate", new Listener(RBM_TAG) {
            @Override
            public void onResponse(Request req) {
                authenticated = false;
                if (!onAuthenticate()) {
                    callNeedAuthentication(req);
                }
            }
        });
    }

    public void removeListeners(RbmAndroidClient client) {
        if (RBM_TAG!=null)
            client.cancelCallbacks(RBM_TAG);
    }

    public void onStop() {
        client = null;
    }

    public void setNeedAuthenticateListener(Listener fn) {
        this.needAuthenticateListener = fn;
    }

    public void setAuthenticated(Listener fn) {
        this.authenticatedListener = fn;
    }

    public void setFailedAuthentication(Listener fn) {
        this.failedAuthenticationListener = fn;
    }

    protected void callNeedAuthentication(Request req) {
        if (this.needAuthenticateListener!=null)
            this.needAuthenticateListener.onResponse(req);
    }

    protected void callAuthenticated(Request req) {
        if (this.authenticatedListener!=null)
            this.authenticatedListener.onResponse(req);
    }

    protected void callFailedAuthentication(Request req) {
        if (this.failedAuthenticationListener!=null)
            this.failedAuthenticationListener.onResponse(req);
    }
}
