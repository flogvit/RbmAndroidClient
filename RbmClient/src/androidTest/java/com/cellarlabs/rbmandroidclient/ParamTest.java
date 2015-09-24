package com.cellarlabs.rbmandroidclient;

import android.test.InstrumentationTestCase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by vhanssen on 21/09/15.
 */
public class ParamTest extends InstrumentationTestCase {
    public void testCreate() {
        Param params = new Param("params");
        Param test = params.add("test");
        test.add("1");
        assertEquals("test", params.get("test").getKey());
        assertEquals("1", params.get("test").getString());
        assertEquals("1", params.getString("test"));
    }

    public void testCreate2() {
        Param params = new Param("params");
        params.add("test", "1");
        assertEquals("1", params.getString("test"));
    }

    public void testIntegerIn() {
        Param params = new Param("params");
        params.add("test", 1);
        assertEquals("1", params.getString("test"));
    }

    public void testIntegerOut() {
        Param params = new Param("params");
        params.add("test", "1");
        assertEquals(1, params.getInteger("test"));
    }

    public void testJSON() throws JSONException {
        JSONObject obj = new JSONObject("{\"test\": \"1\"}");
        Param params = new Param("params");
        params.add(obj);
        assertEquals("1", params.getString("test"));
    }

    public void testJSON2() throws JSONException {
        JSONObject obj = new JSONObject("{\"test\": \"1\", \"test2\": {\"test\": \"2\"}}");
        Param params = new Param("params");
        params.add(obj);
        assertEquals("1", params.getString("test"));
        assertEquals("2", params.get("test2").getString("test"));
    }

    public void testIterator() throws JSONException {
        JSONObject obj = new JSONObject("{\"test\": \"1\", \"test2\": {\"test\": \"2\"}}");
        Param params = new Param("params");
        params.add(obj);
        String test = "";
        for(Param param: params) {
            test += param.getKey();
        }
        assertEquals("testtest2", test);
    }

    public void testGetJSON() {
        Param params = new Param("params");
        params.add("test", "1");
        ArrayList<String> t = new ArrayList<>();
        t.add("2");
        t.add("3");
        params.add("test2", t);
        params.add("test3");
        params.get("test3").add("t1");
        params.get("test3").add("t2");
        params.get("test3").get("t1").add("10");
        params.get("test3").get("t1").add("11");
        params.get("test3").get("t2").add("20");
        params.get("test3").get("t2").add("21");
        params.add("test4", "t3");
        params.add("test4", "t4");
        params.add("test4", "t5");
        params.get("test4").get("t3").add("20");
        params.get("test4").get("t3").add("21");
        params.get("test4").get("t5").add("31");
        assertEquals("{\"test4\":{\"t4\":{},\"t3\":[\"20\",\"21\"],\"t5\":\"31\"},\"test\":\"1\",\"test2\":[\"2\",\"3\"],\"test3\":{\"t2\":[\"20\",\"21\"],\"t1\":[\"10\",\"11\"]}}", params.getJSON().toString());
    }

    public void testToString() {
        Param params = new Param("params");
        assertEquals("params", params.toString());
    }

    public void testIntegerArray() throws JSONException {
        JSONObject obj = new JSONObject("{\"uid\": [1,2,3,4]}");
        Param params = new Param("params");
        params.add(obj);
        ArrayList<Integer> list = params.get("uid").getArrayInteger();
        Collections.sort(list);
        assertEquals(1, (int) list.get(0));
        assertEquals(2, (int) list.get(1));
        assertEquals(3, (int) list.get(2));
        assertEquals(4, (int) list.get(3));
    }

    public void testSubJSONString() throws JSONException {
        Param params = new Param("params");
        params.add("test", "{\"test2\": \"2\"}");
        Param p2 = new Param("params");
        p2.add(new JSONObject(params.getJSON().toString()));
        Log.d("RBM", params.getJSON().toString());
        Log.d("RBM", p2.getJSON().toString());

        JSONObject t2 = new JSONObject("{\"test2\": \"2\"}");
        JSONObject t = new JSONObject();
        t.put("test", t2.toString());
        Log.d("RBM", t.toString());

        Param p3 = new Param("params");
        p3.add(t);
        Log.d("RBM", "S: " + p3.getString("test"));
        Log.d("RBM", p3.getJSON().toString());

        Object t3 = t.get("test");
        if (t3 instanceof JSONObject) {
            Log.d("RBM", "t3 is object");
        } else if (t3 instanceof String) {
            Log.d("RBM", "t3 is string");
        }
        Object json = new JSONTokener((String) t.get("test")).nextValue();
        Log.d("RBM", json.toString());
        if (json instanceof JSONObject) {
            Log.d("RBM", "Is JSONObject");
        }
    }
}
