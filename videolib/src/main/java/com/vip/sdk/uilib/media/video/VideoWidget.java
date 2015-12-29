package com.vip.sdk.uilib.media.video;

import android.net.Uri;

import java.util.Map;

/**
 *
 * 视频控件需要支持的功能的接口，只在此做统一的声明
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/24.
 *
 * @since 1.0
 */
/*package*/ interface VideoWidget {

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
     * 跳到指定时间点
     */
    void seekTo(int msec);

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
     * 获取数据加载进度
     */
    int getBufferPercentage();
}


/**
 *
 * 视频控件委派执行需要支持的功能的接口，只在此做统一的声明
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/24.
 *
 * @since 1.0
 */
/*package*/ interface VideoWidgetDelegate {
    /**
     * 为指定Video设置播放源
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
    void setVideoPath(VIPVideo video, String url);

    /**
     * 为指定Video设置播放源
     *
     * @see #setVideoPath(VIPVideo, String)
     */
    void setVideoURI(VIPVideo video, Uri uri);

    /**
     * 为指定Video设置播放源
     *
     * @see #setVideoPath(VIPVideo, String)
     * @see #setVideoURI(VIPVideo, Uri)
     */
    void setVideoURI(VIPVideo video, Uri uri, Map<String, String> headers) ;

    /**
     * 视频是否正在播放
     */
    boolean isPlaying(VIPVideo video);

    /**
     * 让指定的视频控件开始播放（设置URI之后，并不一定会自动播放，调用该方法开始播放视频；
     * 或者暂停时，调用该方法继续播放）
     */
    void start(VIPVideo video);

    /**
     * 跳到指定时间点
     */
    void seekTo(VIPVideo video, int msec);

    /**
     * 让指定的视频控件暂停播放
     */
    void pause(VIPVideo video);

    // 暂时不提供
    // void suspend(VIPVideo video);

    /**
     * 让指定的视频控件停止播放
     */
    void stop(VIPVideo video);

    /**
     * 给指定的视频控件设置状态回调接口
     */
    void setControlCallback(VIPVideo video, VideoControlCallback callback);
}