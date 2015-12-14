package com.vip.sdk.base.utils;

/**
 * Created by richard.zhao on 2015/01/27.
 */
public class BaseConfig {

    // 默认，由外部配置
    public static String DOMAIN = "";//http://kidapi.vipkid.com";
    public static String SOURCE="";
    public static String SESSION_DOMAIN = "";
    public static String SESSION_FDS_DOMAIN = "";

    public static String WALLET_DOMAIN = "http://kidapi.vipkid.com";
    public static String ADDRESS_DOMAIN = "";
    public static String ADDRESS_SAFE_DOMAIN = "";
    public static String API_KEY = "";
    public static String API_SECRET= "c0be1496baf74d24b26d78b68ee94004";

    public static String AD_DOMAIN = DOMAIN;
    public static String APP_NAME;

    public static String APP_VERSION;

    public static String WX_APP_ID = "";
    public static String WX_APP_SCRIPT = "";

    public static String WB_APP_KEY = "";

    public static int AQUERY_RETRY = 0;

    public static boolean USE_FREEISWHEEL_MONITOR = false;   //app performence monitor

    public static boolean USE_NATIVE_SIGN = false;

    /**
     * 设备唯一识别号
     *
     * TODO 支付模块暂时没有更新到master，后续仍需要统一
     */
    public static String MARSCID = AndroidUtils.getDeviceId();
}
