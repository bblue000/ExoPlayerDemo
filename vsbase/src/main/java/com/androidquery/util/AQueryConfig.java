package com.androidquery.util;

import com.androidquery.callback.AjaxCallback;

public class AQueryConfig {

    public static final int TIME_OUT = 15 * 1000;
    public static final int RETRY = 0;

    public AQueryConfig() {
        config();
    }

    public void config() {
        AQUtility.setDebug(true);
        AjaxCallback.setTransformer(new AQueryTransformer());
        AjaxCallback.setTimeout(TIME_OUT);
    }
}
