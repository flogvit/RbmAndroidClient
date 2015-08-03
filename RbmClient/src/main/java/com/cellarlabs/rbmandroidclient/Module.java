package com.cellarlabs.rbmandroidclient;

/**
 * Created by vhanssen on 08/06/15.
 */
abstract public class Module {
    protected String version = "0.0.1";

    protected RbmAndroidClient rbmClient = null;

    public Module(RbmAndroidClient rbmClient, String version) {
        this.version = version;
        this.rbmClient = rbmClient;
    }

    public void send(Request req, Listener fn) {
        req.withVersion(version);
        rbmClient.send(req, fn);
    }
}
