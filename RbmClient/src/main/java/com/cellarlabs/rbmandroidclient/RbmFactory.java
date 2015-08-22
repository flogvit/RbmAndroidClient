package com.cellarlabs.rbmandroidclient;


public class RbmFactory {

    private static RbmAndroidClient sRbmAndroidClient;

    public static RbmAndroidClient getRbmClient(){
        if(sRbmAndroidClient == null){
            sRbmAndroidClient = new RbmAndroidClient();
            sRbmAndroidClient.withServer("wss://api.puzzleall.com:8680");
        }
        return sRbmAndroidClient;
    }

}
