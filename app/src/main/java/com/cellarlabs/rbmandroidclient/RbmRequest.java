package com.cellarlabs.rbmandroidclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vhanssen on 02/06/15.
 */
public class RbmRequest {
    private String command = "";
    private Integer reqid = 0;
    private JSONObject json = null;

    public RbmRequest() {

    }

    public RbmRequest(String data) {
        try {
            json = new JSONObject(data);
            this.command = json.has("command") ? json.getString("command") : "";
            this.reqid = json.has("reqid") ? json.getInt("reqid") : 0;
            System.out.println(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RbmRequest setCommand(String command) {
        this.command = command;
        return this;
    }

    public String getCommand() {
        return this.command;
    }

    public RbmRequest setParams(String params) {

        return this;
    }

    public String get(String key) {
        String result = "";
        try {
            result = json.getJSONObject("params").getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Integer getInteger(String key) {
        return Integer.parseInt(get(key));
    }

    public RbmRequest addPopulate(RbmRequest req, HashMap<String,String> map) {


        return this;
    }

    public boolean hasReqid() {
        return this.reqid>0;
    }

    public RbmRequest setReqid(int id) {
        this.reqid = id;
        return this;
    }

    public int getReqid() {
        return this.reqid;
    }

    public String data() {
        JSONObject object = new JSONObject();
        try {
            object.put("command", this.command);
            JSONObject params = new JSONObject();
            params.put("a", 2);
            params.put("b", 3);
            object.put("params", params);
            if (this.reqid>0)
                object.put("reqid", this.reqid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
