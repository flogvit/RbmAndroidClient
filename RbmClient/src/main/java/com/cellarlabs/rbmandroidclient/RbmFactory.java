package com.cellarlabs.rbmandroidclient;


public class RbmFactory {

    private static RbmAndroidClient sRbmAndroidClient;
    private static String uri = "wss://api.puzzleall.com:8680";

    public static RbmAndroidClient getRbmClient(){
        if(sRbmAndroidClient == null){
            sRbmAndroidClient = new RbmAndroidClient();
            sRbmAndroidClient.withServer(uri);
        }
        return sRbmAndroidClient;
    }

    public static void setURI(String uri) {
        RbmFactory.uri = uri;
        sRbmAndroidClient.withServer(uri);
    }
}
