package com.cellarlabs.rbmandroidclient;

/**
 * Created by vhanssen on 02/06/15.
 */
public class RbmListener {
    private Integer tag = 0;
    public RbmListener() {

    }

    public RbmListener(final Integer tag) {
        this.tag = tag;
    }

    public Integer getTag() {
        return this.tag;
    }

    public void onResponse(RbmRequest req) {};
}
