package com.cellarlabs.rbmandroidclient;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by vhanssen on 22/09/15.
 */
public class Param implements Iterable<Param> {

    String data;
    Param parent;
    ArrayList<Param> children;

    public Param(String data) {
        this.data = data;
        this.children = new ArrayList<Param>();
    }

    public Param(String key, String value) {
        this(key);
        add(value);
    }

    /**
     * TODO: Need to traverse on add to get unique values
     * @param param
     * @return
     */
    public Param add(Param param) {
        param.parent = this;
        this.children.add(param);
        return param;
    }

    public Param add(String entry) {
        if (has(entry)) return get(entry);
        Param param = new Param(entry);
        return add(param);
    }


    public Param add(String key, String value) {
        Param param;
        if (has(key))
            param = get(key);
        else
            param = add(key);
        param.add(value);
        return param;
    }

    public Param add(String key, Integer value) {
        return add(key, Integer.toString(value));
    }

    public Param add(String key, Long value) {
        return add(key, Long.toString(value));
    }

    public Param add(String key, JSONArray values) {
        Param param = add(key);
        for(int i=0,len = values.length(); i<len; i++){
            try {
                param.add(values.getString(i));
            }catch(JSONException e){
            }
        }
        return param;
    }

    public Param add(String key, ArrayList<String> values) {
        Param param = add(key);
        for(String v: values) {
            param.add(v);
        }
        return param;
    }

    public Param add(String key, String[] values) {
        Param param = add(key);
        for(String v: values) {
            param.add(v);
        }
        return param;
    }

    public Param add(String key, JSONObject obj) {
        Param param = add(key);
        param.add(obj);
        return param;
    }

    public Param add(JSONObject obj) {
        Iterator<?> keys = obj.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();

            Param param;

            try {
                String data = obj.getString(key);

                if(data == null || data.length() == 0){
                    add(key, data);
                }else {
                    Object json = obj.get(key);
                    if (json instanceof JSONObject) {
                        param = add(key);
                        param.add((JSONObject) json);
                    } else if (json instanceof JSONArray) {
                        add(key, (JSONArray) json);
                    } else {
                        add(key, data);
                    }
                }
            } catch (JSONException e) {
                Log.e("RBM", "Error parsing key " + key);
                e.printStackTrace();
            }
        }
        return this;
    }

    public String getKey() {
        return data;
    }

    public Integer getKeyInteger() {
        try {
            return Integer.parseInt(getKey());
        } catch (Exception e) {
            return 0;
        }
    }

    public Long getKeyLong() {
        try {
            return Long.parseLong(getKey());
        } catch (Exception e) {
            return 0L;
        }
    }

    public boolean getKeyBoolean() {
        try {
            return Boolean.parseBoolean(getKey());
        } catch (Exception e) {
            return false;
        }
    }

    public String getString(String key) {
        if (has(key))
            return get(key).getString();
        return "";
    }

    public String getString() {
        return children.size()==1 ? children.get(0).getKey() : "";
    }

    public long getLong(String key) {
        try {
            return Long.parseLong(getString(key));
        } catch (Exception e) {
            return 0L;
        }
    }

    public long getLong() {
        try {
            return Long.parseLong(getString());
        } catch (Exception e) {
            return 0L;
        }
    }

    public int getInteger(String key) {
        try {
            return Integer.parseInt(getString(key));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getInteger() {
        try {
            return Integer.parseInt(getString());
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean getBoolean(String key) {
        try {
            return Boolean.parseBoolean(getString(key));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean getBoolean() {
        try {
            return Boolean.parseBoolean(getString());
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<Param> getArrray() {
        return children;
    }

    public ArrayList<String> getArrayString() {
        ArrayList<String> result = new ArrayList<>();
        for (Param child: children) {
            result.add(child.getKey());
        }
        return result;
    }

    public ArrayList<Integer> getArrayInteger() {
        ArrayList<Integer> result = new ArrayList<>();
        for (Param child: children) {
            result.add(child.getKeyInteger());
        }
        return result;
    }

    public ArrayList<Long> getArrayLong() {
        ArrayList<Long> result = new ArrayList<>();
        for (Param child: children) {
            result.add(child.getKeyLong());
        }
        return result;
    }

    public boolean isValue() {
        return children.size()==0;
    }

    public boolean isKeyValue() {
        return (children.size()==1 && children.get(0).children.size()==0);
    }

    public boolean isArray() {
        if (children.size()<2) return false;

        for (Param child: children) {
            if (!child.isValue())
                return false;
        }
        return true;
    }

    public Param get(String entry) {
        for(Param child: children) {
            if (child.getKey().equals(entry))
                return child;
        }
        return null;
    }

    public boolean has(String entry) {
        for(Param child: children) {
            if (child.getKey().equals(entry))
                return true;
        }
        return false;
    }

    @Override
    public Iterator<Param> iterator() {
        return children.iterator();
    }

    public JSONObject getJSON() {
        JSONObject result = new JSONObject();
        try {
            for (Param param : children) {
                if (param.isKeyValue()) {
                    result.put(param.getKey(), param.getString());
                } else if (param.isArray()) {
                    result.put(param.getKey(), new JSONArray(param.getArrayString()));
                } else {
                    JSONObject res = param.getJSON();
                    result.put(param.getKey(), res);
                }
            }
        } catch(Exception e) {
            Log.d("RBM", "Something went wrong");
        }
        return result;
    }

    public String toString() {
        return getKey();
    }

}