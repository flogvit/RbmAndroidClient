package com.cellarlabs.rbmandroidclient;


import android.content.Context;

public class RbmFactory {

    private static RbmAndroidClient sRbmAndroidClient;
    private static String uri = "wss://api.puzzleall.com:8680";
    private static Context ctx = null;
    private static String module = "";

    public static RbmAndroidClient getRbmClient(){
        if(sRbmAndroidClient == null){
            sRbmAndroidClient = new RbmAndroidClient(ctx);
            sRbmAndroidClient.setModule(module);
            sRbmAndroidClient.withServer(uri);
        }
        return sRbmAndroidClient;
    }

    public static void setURI(String uri) {
        RbmFactory.uri = uri;
        if (sRbmAndroidClient!=null)
            sRbmAndroidClient.withServer(uri);
    }

    public static void setApplicationContext(Context ctx) {
        RbmFactory.ctx = ctx;
        if (sRbmAndroidClient!=null)
            sRbmAndroidClient.setApplicationContext(ctx);
    }

    public static void setModule(String module) {
        RbmFactory.module = module;
    }

    public static void onStop() {
        if (sRbmAndroidClient!=null)
            sRbmAndroidClient.onStop();
        sRbmAndroidClient = null;
    }
}
