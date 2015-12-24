package com.vip.sdk.uilib.video;

import android.net.Uri;

import java.util.Map;

/**
 *
 * 对外的视频组件接口
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/24.
 *
 * @since 1.0
 */
public interface IVideoView {

    /**
     * 设置url
     *
     * <br/>
     *
     * 支持的url格式包括：<sup>[1]</sup>
     * <ul>
     *     <li>http|https|rtsp://{host}/{path}</li>
     *     <li>file://{path}</li>
     *     <li>android.resource://{package}/{res id}</li>
     *     <li>android.resource://{package}/{res dir}/{res file name}<sup>[2]</sup></li>
     * </ul>
     *
     * <p>
     *     <b>[1]</b> 暂不支持file://android_asset/{path}，如需访问assets下文件，可以参见
     *     <a href="http://stackoverflow.com/questions/4820816/how-to-get-uri-from-an-asset-file/4820905#4820905">
     *         How to get URI from an asset File?</a>
     * </p>
     *
     * <p>
     *     <b>[2]</b> 暂不支持file://android_asset/{path}，如需访问assets下文件，可以参见
     *     <a href="http://stackoverflow.com/questions/7976141/get-uri-of-mp3-file-stored-in-res-raw-folder-in-android/7979084#7979084">
     *         Get URI stored in res/raw folder in android</a>
     * </p>
     */
    void setVideoPath(String url);

    /**
     * 设置Uri
     *
     * @see #setVideoPath(String)
     */
    void setVideoURI(Uri uri);

    /**
     * 设置Uri
     *
     * @see #setVideoPath(String)
     * @see #setVideoURI(Uri)
     */
    void setVideoURICompat(Uri uri, Map<String, String> headers) ;

    /**
     * 视频是否正在播放
     */
    boolean isPlaying();

    /**
     * 开始播放（设置URI之后，并不会自动播放，调用该方法开始播放视频；
     * 或者暂停时，调用该方法继续播放）
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    // 暂时不提供
    // void suspend();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 获取总时长。
     *
     * <br/>
     */
    int getDuration() ;

    /**
     * 获取当前播放的位置
     */
    int getCurrentPosition();

    /**
     * 播放状态的回调。
     *
     * <br/>
     *
     * 特别注意：如果已经开始播放，则在设置时将会调用{@link #STATE_START}。
     */
    interface StateCallback {

        /**
         * 加载资源（全部或部分），此时还不能播放
         */
        int STATE_LOADING = 0;

        /**
         * 加载资源失败，包含错误信息
         */
        int STATE_LOAD_ERR = STATE_LOADING + 1;

        /**
         * 资源已加载（全部或部分），已经可以播放。
         */
        int STATE_PREPARED = STATE_LOAD_ERR + 1;

        /**
         * 开始播放（一种是从停止或者暂停状态变为播放状态）。
         */
        int STATE_START = STATE_PREPARED + 1;

        /**
         * 由播放状态变为暂停状态
         */
        int STATE_PAUSE = STATE_START + 1;

        /**
         * 进入停止状态（已播放完成）
         */
        int STATE_STOP = STATE_PAUSE + 1;

        /**
         * {@link android.media.MediaPlayer}内部异步操作时发生错误，包含错误信息
         */
        int STATE_ERR = STATE_STOP + 1;

        /**
         * 当状态改变时回调
         *
         * @param status 有的状态包含状态信息，将在触发相应状态时给出，参见不同状态说明
         */
        void onStateChanged(IVideoView video, int state, VideoStateInfo status);
    }

}
