package com.vip.sdk.uilib.media.video;

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
public class VideoState {

    /**
     * 基本包含信息，信息码
     */
    public final int code;

    /**
     * 基本包含信息，信息文本
     */
    public final String message;

    /**
     * 额外信息，默认为0
     */
    public int extraCode;

    public VideoState(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public VideoState(int code, String message, int extraCode) {
        this.code = code;
        this.message = message;
        this.extraCode = extraCode;
    }

    /**
     * 设置额外的/副的状态信息
     *
     * @return 当前对象
     */
    public VideoState extraCode(int extraCode) {
        this.extraCode = extraCode;
        return this;
    }

    @Override
    public String toString() {
        return String.format("State {%d, %d, %s}", code, extraCode, message);
    }
}
