package com.cellarlabs.rbmandroidclient;

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
    private ArrayList<Param> params = new ArrayList<>();
    private int errorId = 0;
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
            this.errorId = json.has("error") ? json.getInt("error") : this.errorId;
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

    public Request withVersion(String version) {
        this.version = version;
        return this;
    }


    public Request withParams(JSONObject params) {
        Iterator<?> keys = params.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            try {
                Object value = params.get(key);
                if (value instanceof String)
                    withParam(key, params.getString(key));
                else if (value instanceof Integer)
                    withParam(key, params.getInt(key));
                else if (value instanceof JSONArray)
                    withParam(key, params.getJSONArray(key));
                else if (value instanceof JSONObject)
                    withParam(key, params.getJSONObject(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public Request withParam(Param param) {
        params.add(param);
        return this;
    }

    public Request withParam(String key, String value) {
        return withParam(new Param().set(key, value));
    }

    public Request withParam(String key, int value) {
        return withParam(key, "" + value);
    }

    public Request withParam(String key, JSONArray value) {
        return withParam(new Param().set(key, value));
    }

    public Request withParam(String key, JSONObject value) {
        return withParam(new Param().set(key, value));
    }

    public Request withParam(String key, Boolean value) {
        return withParam(key, ""+value);
    }

    public boolean has(String key) {
        for (Param param : params) {
            if (param.is(key))
                return true;
        }
        return false;
    }

    public Param get(String key) {
        for (Param param : params) {
            if (param.is(key))
                return param;
        }
        return null;
    }

    public String  getString(String key) {
        Param param = get(key);
        if (param == null)
            return null;
        return param.getValue();
    }

    public int getInteger(String key) {
        return Integer.parseInt(getString(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
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

    protected void buildParams(JSONObject obj) {
        JSONObject jsonparams = new JSONObject();
        JSONArray populate = null;

        try {
            for (Param param : this.params) {
                if (param.isRequest()) {
                    JSONObject entry = new JSONObject();
                    entry.put("request", param.getRequest().dataCore());
                    entry.put("returns", param.getMap());
                    if (populate == null)
                        populate = new JSONArray();
                    populate.put(entry);
                } else {
                    if (param.isValueArray())
                        jsonparams.put(param.getKey(), param.getValueArray());
                    else if (param.isValueObject())
                        jsonparams.put(param.getKey(), param.getValueObject());
                    else
                        jsonparams.put(param.getKey(), param.getValue());
                }
            }
            if (populate != null)
                obj.put("populate", populate);
            obj.put("params", jsonparams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the incoming params
     * <p/>
     * TODO: Need to implement arrays etc later on
     *
     * @param obj
     */
    protected void parseParams(JSONObject obj) {
        this.params = new ArrayList<>();
        Iterator<?> keys = obj.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();


            Param param;

            try {
                String data = obj.getString(key);
                Object json = new JSONTokener(data).nextValue();
                if (json instanceof JSONObject){
                    param = new Param().set(key, (JSONObject)json);
                }

                else if (json instanceof JSONArray){
                    param = new Param().set(key, (JSONArray)json);
                }
                else{
                    param = new Param().set(key, data);
                }

                params.add(param);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
