package com.vip.sdk.videolib;

/**
 *
 * 加载失败的信息
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public class LoadErrInfo {

    public final int code;

    public final String message;

    public LoadErrInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
