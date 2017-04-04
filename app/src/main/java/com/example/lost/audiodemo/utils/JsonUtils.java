package com.example.lost.audiodemo.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Json解析工具类 基于Gson
 * Created by wuchanghe on 2017/3/2 10:07.
 */

public class JsonUtils {

    /**
     * */
    public static Gson getGson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    private static Gson gson;
    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private JsonUtils() {
    }

    /**
     * @return
     */
    public static String objectToJson(Object ts) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }

    /***
     * @param jsonStr
     *
     * @param tt
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <T> List<T> jsonToList(String jsonStr, TypeToken tt) {
        List<T> objList = null;
        if (gson != null) {
            java.lang.reflect.Type type = tt.getType();
            objList = gson.fromJson(jsonStr, type);
        }
        return objList;
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map<?, ?> objMap = null;
        if (gson != null) {
            java.lang.reflect.Type type = new TypeToken<Map<?, ?>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }

    /***
     * @param jsonStr
     * @return
     */
    public static Object jsonToBean(String jsonStr, Class<?> cl) {
        Object obj = null;
        if (gson != null) {
            obj = gson.fromJson(jsonStr, cl);
        }
        return obj;
    }

 /*   *//**
     *
     * @param jsonStr
     * @param key
     * @return
     *//*
    public static Object getJsonValue(String jsonStr, String key) {
        Object rulsObj = null;
        Map<?, ?> rulsMap = jsonToMap(jsonStr);
        if (rulsMap != null && rulsMap.size() > 0) {
            rulsObj = rulsMap.get(key);
        }
        return rulsObj;
    }
*/

    /**
     * @param theString
     * @return
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        // 絋���絖�膃�顕筝榊�鐚���菴���腥�
        if (theString == null) {
            return "";
        }
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }






    /**
     * @param s
     * @param tt
     * @param <T>
     * @return
     * @throws JSONException
     */
    public static <T> List<T> getListFromJson(String s, TypeToken tt) throws JSONException {
        String json = "";
        JSONObject jsonObject = new JSONObject(s);
        if (jsonObject.has("data")) {
            json = jsonObject.getJSONArray("data").toString();
            return jsonToList(json, tt);
        }
        return null;
    }

    /**
     * @param s
     * @return
     * @throws JSONException
     */
    public static Object getValueFromData(String s, String key) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        JSONObject data = null;

        if (jsonObject.has("data")){
            data =  jsonObject.getJSONObject("data");
        }else {
            data =  jsonObject;
        }

        return data.get(key);
    }


    /**
     * @param s
     * @param key
     * @return
     * @throws JSONException
     */
    public static Object getValueFromJson(String s, String key) throws JSONException {

        JSONObject jsonObject = new JSONObject(s);

        return jsonObject.get(key);

    }

}
