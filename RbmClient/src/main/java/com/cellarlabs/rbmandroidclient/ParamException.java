package com.cellarlabs.rbmandroidclient;

/**
 * Created by vhanssen on 03/08/15.
 */
public class ParamException extends Exception {

    public ParamException() {
        super();
    }

    public ParamException(String message) {
        super(message);
    }

    public ParamException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ParamException(Throwable throwable) {
        super(throwable);
    }
}
