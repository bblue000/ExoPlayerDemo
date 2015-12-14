package com.vip.sdk.base.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.vip.sdk.api.BaseMsgResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mark on 14-9-11.
 */
public class JsonUtils {

    /**
     * 将json对象转换成java对象
     */
    public static String getJson2String(String jsonData, String key) throws JSONException {

        if (null == key) {
            return null;
        }

        JSONObject obj = new JSONObject(jsonData.trim());
        String value = obj.get(key).toString();
        return value;
    }

    /**
     * 将java对象转换成json对象
     */
    public static String parseObj2Json(Object obj) {

        if (null == obj) {
            return null;
        }

        Gson gson = new Gson();
        String objstr = gson.toJson(obj);
        return objstr;
    }


    /**
     * 将java对象的属性转换成json的key
     */
    public static String parseObj2JsonOnField(Object obj) {

        if (null == obj) {
            return null;
        }

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
        String objstr = gson.toJson(obj);

        return objstr;
    }

    /**
     * 将json对象转换成java对象
     */
    public static <T> Object parseJson2Obj(String jsonData, Class<T> c) {

        if (null == jsonData) {
            return null;
        }

        Gson gson = new Gson();
        Object obj = gson.fromJson(jsonData.trim(), c);
        return obj;
    }


    /**
     * 将json对象转换成数组对象
     */
    public static <T> ArrayList<T> parseJson2List(String jsonData, Class<T> c)
            throws JSONException {

        if (null == jsonData || "".equals(jsonData)) {
            return null;
        }

        Gson gson = new Gson();
        ArrayList<T> list = new ArrayList<T>();
        JSONArray jsonArray = new JSONArray(jsonData.trim());
        JSONObject objItem;
        T objT;
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            objItem = (JSONObject) jsonArray.get(i);
            if (null != objItem) {
                objT = gson.fromJson(objItem.toString(), c);
                list.add(objT);
            }
        }

        return list;
    }

    /**
     * 将json对象转换成java对象
     */
    public static <T> Object parseBaseMsgJson2Obj(String jsonData, Class<T> c) {

        if (null == jsonData) {
            return null;
        }

//        BaseMsgResult result = (BaseMsgResult) JsonUtils.parseJson2Obj(jsonData, BaseMsgResult.class);
//
//        if (null != result && (result.getCode() == 1)) {
//            return JsonUtils.parseJson2Obj(JsonUtils.parseObj2Json(result.getData()), c);
//        }

        return null;
    }


    /**
     * 将
     * <p/>
     * code int
     * msg String
     * data object
     * <p/>
     * json对象转换成数组对象
     */
    public static <T> ArrayList<T> parseBaseMsgJson2List(String jsonData, Class<T> c) throws JSONException {

        if (null == jsonData) {
            return null;
        }

//        BaseMsgResult result = (BaseMsgResult) JsonUtils.
//                parseJson2Obj(jsonData, BaseMsgResult.class);
//
//        if (null != result && (result.getCode() == 1)) {
//            return JsonUtils.parseJson2List(JsonUtils.parseObj2Json(result.getData()), c);
//        }

        return null;
    }

}
