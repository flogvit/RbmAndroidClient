package com.cellarlabs.rbmandroidclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vhanssen on 06/06/15.
 */
public class Param {
    private String key = "";
    private String value = "";
    private JSONArray valueArray = null;
    private JSONObject valueObject = null;

    private Request req = null;
    private HashMap<String,String> paramMap = null;

    public Param set(Request req) {
        this.req = req;
        return this;
    }

    public Param addMapping(String from, String to) {
        if (paramMap==null)
            paramMap = new HashMap<>();
        paramMap.put(to, from);

        return this;
    }

    public Param set(Request req, HashMap<String,String> paramMap) {
        this.req = req;
        this.paramMap = paramMap;
        return this;
    }

    public Param set(String key, String[] values) {

        return this;
    }

    public Param set(String key, String value) {
        this.key = key;
        this.value = value;
        this.valueArray = null;
        this.valueObject = null;
        return this;
    }

    public Param set(String key, JSONObject value) {
        this.valueObject = value;
        this.value = "";
        this.valueArray = null;
        this.key = key;
        return this;
    }

    public Param set(String key, JSONArray value) {
        this.valueArray = value;
        this.valueObject = null;
        this.value = "";
        this.key = key;

        return this;
    }

    public boolean is(String key) {
        return this.key.equals(key);
    }

    public boolean isRequest() {
        return this.req!=null;
    }

    public boolean isValueObject() {
        return this.valueObject!=null;
    }

    public boolean isValueArray() {
        return this.valueArray!=null;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public JSONObject getValueObject() {
        return this.valueObject;
    }

    public JSONArray getValueArray() {
        return this.valueArray;
    }

    public ArrayList<Integer> getIntegerArray() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        if (!isValueArray()) return result;
        JSONArray values = getValueArray();
        if(values == null || values.length() > 0) return result;

        for(int i=0,len = values.length(); i<len; i++){
            try {
                result.add(values.getInt(i));
            }catch(JSONException e){
            }
        }
        return result;
    }

    public ArrayList<String> getStringArray() {
        ArrayList<String> result = new ArrayList<String>();
        if (!isValueArray()) return result;
        JSONArray values = getValueArray();
        if(values == null || values.length() > 0) return result;

        for(int i=0,len = values.length(); i<len; i++){
            try {
                result.add(values.getString(i));
            }catch(JSONException e){
            }
        }
        return result;
    }

/*    public String get(String key) {
        return this.value;
    }
*/
    public Request getRequest() {
        return this.req;
    }

    public JSONObject getMap() {
        JSONObject result = new JSONObject();
        for(String key: paramMap.keySet()) {
            try {
                result.put(key, paramMap.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
