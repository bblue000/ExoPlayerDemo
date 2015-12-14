package com.androidquery.util;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.Transformer;
import com.google.gson.Gson;
import com.vip.sdk.base.utils.VSLog;

import java.io.UnsupportedEncodingException;

public class AQueryTransformer implements Transformer {

    private static final String TAG = AQueryTransformer.class.getSimpleName();

    @Override
    public <T> T transform(String url, Class<T> type, String encoding,
                           byte[] data, AjaxStatus status) {
        try {
            return new Gson().fromJson(new String(data, encoding), type);
        } catch (UnsupportedEncodingException e) {
            VSLog.error("transform Exception: " + e.getMessage());
            e.printStackTrace();
            return new Gson().fromJson(new String(data), type);
        }
    }

}
