package com.cellarlabs.rbmandroidclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by vhanssen on 06/06/15.
 */
public class Populate {
    private Request req = null;
    private HashMap<String,String> mapKeys = new HashMap<>();

    public Populate() {

    }
    public Populate(Request req, HashMap<String,String> map) {
        this.req = req;
        if (map!=null)
            this.mapKeys = map;

    }

    public Populate addRequest(Request req) {
        this.req = req;
        return this;
    }

    public Request getRequest() {
        return this.req;
    }

    public JSONObject getMap() {
        JSONObject result = new JSONObject();
        for(String key: mapKeys.keySet()) {
            try {
                result.put(key, mapKeys.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public Populate addMapping(String key, String value) {
        mapKeys.put(key, value);
        return this;
    }
}
