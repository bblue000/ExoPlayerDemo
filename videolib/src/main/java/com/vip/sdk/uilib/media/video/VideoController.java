package com.vip.sdk.uilib.media.video;

import android.net.Uri;

import java.util.Map;

/**
 *
 * 视频加载、播放的逻辑控制器、管理器。
 *
 * The controller to load video sources, control videos(set uri, play, pause, stop etc.)
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/25.
 *
 * @since 1.0
 */
public abstract class VideoController {

    public abstract void setPath(VIPVideo vipVideo, String path);
    public abstract void setUri(VIPVideo vipVideo, Uri uri);
    public abstract void setUri(VIPVideo vipVideo, Uri uri, Map<String, String> headers);
    public abstract void start(VIPVideo vipVideo);
    public abstract void pause(VIPVideo vipVideo);
    public abstract void stop(VIPVideo vipVideo);

}
