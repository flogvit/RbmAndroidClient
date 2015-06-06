package com.cellarlabs.rbmandroidclient;

/**
 * Created by vhanssen on 02/06/15.
 */
public class Listener {
    private Integer tag = 0;
    public Listener() {

    }

    public Listener(final Integer tag) {
        this.tag = tag;
    }

    public Integer getTag() {
        return this.tag;
    }

    public void onResponse(Request req) {};
}
