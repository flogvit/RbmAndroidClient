package com.cellarlabs.rbmandroidclient;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by vhanssen on 02/06/15.
 */
public class Request {
    private String command = "";
    private int reqid = 0;
    private JSONObject json = null;
    private String version = "";
    private Param params = new Param("params");
    private int errorId = 0;
    private String errorText = "";
    private int count = 0;
    private long now = -1;
    private double resendAfter = 0;

    public Request() {

    }

    public Request(String data) {
        try {
            json = new JSONObject(data);
            this.command = json.has("command") ? json.getString("command") : this.command;
            this.reqid = json.has("reqid") ? json.getInt("reqid") : this.reqid;
            this.version = json.has("version") ? json.getString("version") : this.version;
            if (json.has("params"))
                params.add(json.getJSONObject("params"));
            this.errorId = json.has("error") ? json.getInt("error") : this.errorId;
            this.errorText = json.has("errorText") ? json.getString("errorText") : this.errorText;
            this.now = json.has("now") ? json.getLong("now") : -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Request withCommand(String command) {
        this.command = command;
        return this;
    }

    public String getCommand() {
        return this.command;
    }

    public Request withVersion(String version) {
        this.version = version;
        return this;
    }


    public Request withParams(JSONObject params) {
        this.params.add(params);
        return this;
    }

    public Request withParam(Param param) {
        params.add(param);
        return this;
    }

    public Request withParam(String key, String value) {
        params.add(key, value);
        return this;
    }

    public Request withParam(String key, int value) {
        return withParam(key, Integer.toString(value));
    }

    public Request withParam(String key, long value) {
        return withParam(key, Long.toString(value));
    }

    public Request withParam(String key, JSONArray value) {
        params.add(key, value);
        return this;
    }

    public Request withParam(String key, JSONObject value) {
        params.add(key, value);
        return this;
    }

    public Request withParam(String key, Boolean value) {
        return withParam(key, ""+value);
    }

    public boolean has(String key) {
        return params.has(key);
    }

    public Param get(String key) {
        return params.get(key);
    }

    public String getString(String key) {
        return params.getString(key);
    }

    public int getInteger(String key) {
        return params.getInteger(key);
    }

    public long getLong(String key) {
        return params.getLong(key);
    }

    public boolean getBoolean(String key) {
        return params.getBoolean(key);
    }

    public Param getParams() {
        return this.params;
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

    protected JSONObject dataCore() {
        JSONObject object = new JSONObject();
        try {
            object.put("command", this.command);
            object.put("params", this.params.getJSON());
//            buildParams(object);
            if (hasReqid())
                object.put("reqid", this.reqid);
            if (hasVersion())
                object.put("version", this.version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public String data() {
        return dataCore().toString();
    }

    public String getVersion() {
        return version;
    }

    public boolean hasVersion() {
        return !this.version.equals("");
    }

    public boolean isError() {
        return errorId > 0;
    }

    public int getErrorId() {
        return this.errorId;
    }

    public String getErrorText() {
        return this.errorText;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public void setResendAfter(double resendAfter) {
        this.resendAfter = resendAfter;
    }

    public double getResendAfter() {
        return this.resendAfter;
    }

    public long getNow() {
        return this.now;
    }
}
