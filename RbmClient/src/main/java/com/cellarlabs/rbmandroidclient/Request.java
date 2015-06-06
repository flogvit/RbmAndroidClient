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
    private JSONObject params = new JSONObject();
    private Integer errorId = 0;
    private String errorText = "";
    private ArrayList<Populate> populate = null;

    public Request() {

    }

    public Request(String data) {
        try {
            json = new JSONObject(data);
            this.command = json.has("command") ? json.getString("command") : this.command;
            this.reqid = json.has("reqid") ? json.getInt("reqid") : this.reqid;
            this.version = json.has("version") ? json.getString("version") : this.version;
            this.params = json.has("params") ? json.getJSONObject("params") : this.params;
            this.errorId = json.has("errorId") ? json.getInt("errorId") : this.errorId;
            this.errorText = json.has("errorText") ? json.getString("errorText") : this.errorText;
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
        if (populate==null)
            populate = new ArrayList<>();
        populate.add(new Populate(req, map));

        return this;
    }

    public Request addPopulate(Populate populate) {
        if (this.populate==null)
            this.populate = new ArrayList<>();
        this.populate.add(populate);

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

    protected JSONObject dataCore() {
        JSONObject object = new JSONObject();
        try {
            object.put("command", this.command);
            object.put("params", params);
            if (hasReqid())
                object.put("reqid", this.reqid);
            if (hasVersion())
                object.put("version", this.version);
            if (hasPopulate())
                buildPopulate(object);
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

    protected boolean hasPopulate() {
        return this.populate!=null;
    }

    protected void buildPopulate(JSONObject obj) {
        JSONArray build = new JSONArray();
        try {
            for(Populate pop: this.populate) {
                JSONObject entry = new JSONObject();
                entry.put("request", pop.getRequest().dataCore());
                entry.put("returns", pop.getMap());
                build.put(entry);
            }
            obj.put("populate", build);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
