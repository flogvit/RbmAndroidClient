package com.cellarlabs.rbmandroidclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vhanssen on 02/06/15.
 */
public class Request {
    private String command = "";
    private Integer reqid = 0;
    private JSONObject json = null;

    public Request() {

    }

    public Request(String data) {
        try {
            json = new JSONObject(data);
            this.command = json.has("command") ? json.getString("command") : "";
            this.reqid = json.has("reqid") ? json.getInt("reqid") : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Request setCommand(String command) {
        this.command = command;
        return this;
    }

    public String getCommand() {
        return this.command;
    }

    public Request setParams(String params) {

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

    public Request addPopulate(Request req, HashMap<String, String> map) {


        return this;
    }

    public boolean hasReqid() {
        return this.reqid > 0;
    }

    public Request setReqid(int id) {
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
            object.put("params", params);
            if (this.reqid > 0)
                object.put("reqid", this.reqid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
