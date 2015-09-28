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
        assertEquals("{\"test\":\"{\\\"test2\\\": \\\"2\\\"}\"}", params.getJSON().toString());
        assertEquals("{\"test\":\"{\\\"test2\\\": \\\"2\\\"}\"}", p2.getJSON().toString());
    }

    public void testArrayObjects() throws JSONException {
        JSONObject obj = new JSONObject("{\"test\": [{\"test2\": 1},{\"test3\": 2}], \"test2\": [1,2,3]}");
        Param params = new Param("params");
        params.add(obj);
        assertEquals("{\"test\":[{\"test2\":\"1\"},{\"test3\":\"2\"}],\"test2\":[\"1\",\"2\",\"3\"]}", params.getJSON().toString());
    }

    public void testArrayObjects2() throws JSONException {
        JSONObject obj = new JSONObject("{\"test\": [{\"test2\": 2, \"test1\": 1}, {\"test2\": 3, \"test3\": 4}, {\"test3\": {\"test1\": [1,2,3]}}]}");
        Param params = new Param("params");
        params.add(obj);
        assertEquals("{\"test\":[{\"test2\":\"2\",\"test1\":\"1\"},{\"test2\":\"3\",\"test3\":\"4\"},{\"test3\":{\"test1\":[\"1\",\"2\",\"3\"]}}]}", params.getJSON().toString());
    }

    public void testArrayObjects3() throws JSONException {
        JSONObject obj = new JSONObject("{\n" +
                "  \"entries\": [\n" +
                "    {\n" +
                "      \"date\": 100000,\n" +
                "      \"module\": \"sudoku\",\n" +
                "      \"gametype\": \"duel\",\n" +
                "      \"players\": [\n" +
                "        {\n" +
                "          \"uid\": 1,\n" +
                "          \"oldelo\": 1000,\n" +
                "          \"elo\": 1020,\n" +
                "          \"pos\": 1\n" +
                "        },\n" +
                "        {\n" +
                "          \"uid\": 2,\n" +
                "          \"oldelo\": 1000,\n" +
                "          \"elo\": 980,\n" +
                "          \"pos\": 2\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": 200000,\n" +
                "      \"module\": \"sudoku\",\n" +
                "      \"gametype\": \"duel\",\n" +
                "      \"players\": [\n" +
                "        {\n" +
                "          \"uid\": 1,\n" +
                "          \"oldelo\": 1020,\n" +
                "          \"elo\": 1005,\n" +
                "          \"pos\": 2\n" +
                "        },\n" +
                "        {\n" +
                "          \"uid\": 2,\n" +
                "          \"oldelo\": 980,\n" +
                "          \"elo\": 995,\n" +
                "          \"pos\": 1\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        Param params = new Param("params");
        params.add(obj);
        assertEquals("{\"entries\":[{\"date\":\"100000\",\"module\":\"sudoku\",\"gametype\":\"duel\",\"players\":[{\"uid\":\"1\",\"oldelo\":\"1000\",\"elo\":\"1020\",\"pos\":\"1\"},{\"uid\":\"2\",\"oldelo\":\"1000\",\"elo\":\"980\",\"pos\":\"2\"}]},{\"date\":\"200000\",\"module\":\"sudoku\",\"gametype\":\"duel\",\"players\":[{\"uid\":\"1\",\"oldelo\":\"1020\",\"elo\":\"1005\",\"pos\":\"2\"},{\"uid\":\"2\",\"oldelo\":\"980\",\"elo\":\"995\",\"pos\":\"1\"}]}]}", params.getJSON().toString());

        Param entries = params.get("entries");
        int datesum = 0;
        for(Param entry: entries) {
            datesum += entry.getInteger("date");
        }
        assertEquals(300000, datesum);
    }
}
