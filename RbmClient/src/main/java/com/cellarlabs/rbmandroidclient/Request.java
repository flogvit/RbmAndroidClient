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

/*    protected void buildParams(JSONObject obj) {
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
*/

    /**
     * Parse the incoming params
     * <p/>
     * TODO: Need to implement arrays etc later on
     *
     * @param
     */
/*    protected void parseParams(JSONObject obj) {
        this.params = new ArrayList<>();
        Iterator<?> keys = obj.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();

            Param param;

            try {
                String data = obj.getString(key);

                if(data == null || data.length() == 0){
                    param = new Param().set(key, data);
                }else {
                    Object json = new JSONTokener(data).nextValue();
                    if (json instanceof JSONObject) {
                        param = new Param().set(key, (JSONObject) json);
                    } else if (json instanceof JSONArray) {
                        param = new Param().set(key, (JSONArray) json);
                    } else {
                        param = new Param().set(key, data);
                    }
                }
                params.add(param);
            } catch (JSONException e) {
                Log.e("RBM", "Error parsing key " + key);
                e.printStackTrace();
            }
        }
    }
*/
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
}
