package com.cellarlabs.rbmandroidclient;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by vhanssen on 02/06/15.
 */
public class Request {
    private String command = "";
    private Integer reqid = 0;
    private JSONObject json = null;
    private String version = "";
    private JSONObject params = new JSONObject();

    public Request() {

    }

    public Request(String data) {
        try {
            json = new JSONObject(data);
            this.command = json.has("command") ? json.getString("command") : "";
            this.reqid = json.has("reqid") ? json.getInt("reqid") : 0;
            this.version = json.has("version") ? json.getString("version") : "";
            this.params = json.has("params") ? json.getJSONObject("params") : new JSONObject();
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

    public Request setParams(String json) {
        try {
            params = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Request addParam(String key, String value) {
        try {
            params.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Request addParams(JSONObject json) {
        Iterator it = json.keys();
        while(it.hasNext()) {
            String key = (String) it.next();
            try {
                params.put(key, json.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public Request addParam(String key, Integer value) {
        return addParam(key, ""+value);
    }

    public String get(String key) {
        String result = "";
        try {
            result = params.getString(key);
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
            object.put("params", params);
            if (hasReqid())
                object.put("reqid", this.reqid);
            if (hasVersion())
                object.put("version", this.version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public String getVersion() {
        return version;
    }

    public Request setVersion(String version) {
        this.version = version;
        return this;
    }

    public boolean hasVersion() {
        return !this.version.equals("");
    }
}
