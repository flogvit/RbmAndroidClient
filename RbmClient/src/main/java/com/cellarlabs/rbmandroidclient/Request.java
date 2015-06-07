package com.cellarlabs.rbmandroidclient;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by vhanssen on 02/06/15.
 */
public class Request {
    private String command = "";
    private Integer reqid = 0;
    private JSONObject json = null;
    private String version = "";
    private ArrayList<Param> params = new ArrayList<>();
    private Integer errorId = 0;
    private String errorText = "";

    public Request() {

    }

    public Request(String data) {
        try {
            json = new JSONObject(data);
            this.command = json.has("command") ? json.getString("command") : this.command;
            this.reqid = json.has("reqid") ? json.getInt("reqid") : this.reqid;
            this.version = json.has("version") ? json.getString("version") : this.version;
            if (json.has("params"))
                parseParams(json.getJSONObject("params"));
            this.errorId = json.has("errorId") ? json.getInt("errorId") : this.errorId;
            this.errorText = json.has("errorText") ? json.getString("errorText") : this.errorText;
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

    public Request withParam(Param param) {
        params.add(param);
        return this;
    }

    public Request withParam(String key, String value) {
        return withParam(new Param().set(key, value));
    }

    public Request withParam(String key, Integer value) {
        return withParam(key, ""+value);
    }

    public String get(String key) {
        for(Param param: params) {
            if (param.is(key))
                return param.getValue();
        }
        return null;
    }

    public Integer getInteger(String key) {
        return Integer.parseInt(get(key));
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
            buildParams(object);
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

    public Request setVersion(String version) {
        this.version = version;
        return this;
    }

    public boolean hasVersion() {
        return !this.version.equals("");
    }

    public boolean isError() {
        return errorId>0;
    }

    public Integer getErrorId() {
        return this.errorId;
    }

    public String getErrorText() {
        return this.errorText;
    }

    protected void buildParams(JSONObject obj) {
        JSONObject jsonparams = new JSONObject();
        JSONArray populate = null;

        try {
            for(Param param: this.params) {
                if (param.isRequest()) {
                    JSONObject entry = new JSONObject();
                    entry.put("request", param.getRequest().dataCore());
                    entry.put("returns", param.getMap());
                    if (populate==null)
                        populate = new JSONArray();
                    populate.put(entry);
                } else {
                    jsonparams.put(param.getKey(), param.getValue());
                }
            }
            if (populate!=null)
                obj.put("populate", populate);
            obj.put("params", jsonparams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the incoming params
     *
     * TODO: Need to implement arrays etc later on
     * @param obj
     */
    protected void parseParams(JSONObject obj) {
        this.params = new ArrayList<>();
        Iterator<?> keys = obj.keys();

        while( keys.hasNext() ) {
            String key = (String)keys.next();
            try {
                Param param = new Param().set(key, obj.getString(key));
                params.add(param);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}