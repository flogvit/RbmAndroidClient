package com.cellarlabs.rbmandroidclient;

/**
 * Created by vhanssen on 02/06/15.
 */
public class Listener {
    private int tag = 0;
    public Listener() {

    }

    public Listener(final int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return this.tag;
    }

    public void onResponse(Request req) {};
}
