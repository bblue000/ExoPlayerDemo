package com.vip.sdk.uilib.video;

/**
 *
 * 视频状态的信息
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public class VideoStateInfo {

    public final int code;

    public final String message;

    public int extraCode;

    public VideoStateInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public VideoStateInfo(int code, String message, int extraCode) {
        this.code = code;
        this.message = message;
        this.extraCode = extraCode;
    }

    /**
     * 设置额外的/副的状态信息
     * @return 当前对象
     */
    public VideoStateInfo extraCode(int extraCode) {
        this.extraCode = extraCode;
        return this;
    }
}
