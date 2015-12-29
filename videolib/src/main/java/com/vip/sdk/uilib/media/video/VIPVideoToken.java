package com.vip.sdk.uilib.media.video;

import android.net.Uri;

import com.vip.sdk.base.utils.ObjectUtils;

import java.util.Map;

/**
 * 封装被{@link com.vip.sdk.uilib.media.video.VideoController}管理的视频
 * （{@link com.vip.sdk.uilib.media.video.VIPVideo}）及其资源等信息
 *
 *
 * Created by Yin Yong on 15/12/27.
 */
public class VIPVideoToken {

    /**
     * 视频信息中存储的当前设置的视频资源地址
     */
    public Uri uri;
    public Map<String, String> headers;

    /**
     * 视频组件{@link VIPVideo}
     */
    public final VIPVideo video;

    /**
     * 管理该视频信息的{@link VideoController}
     */
    public final VideoController controller;

    public VIPVideoToken(VideoController controller, VIPVideo video) {
        this.controller = controller;
        this.video = video;
    }

    /**
     * <code>uri</code>是否跟当前信息中的{@link #uri}一致。
     *
     * <br/>
     *
     * 由于获取和设置可能在不同线程，增加同步关键字
     */
    public synchronized boolean matchUri(Uri uri) {
        return ObjectUtils.equals(this.uri, uri);
    }

    /**
     * <code>uri</code>是否跟当前信息中的{@link #uri}一致
     *
     * <br/>
     *
     * 由于可能获取和设置在不同线程，增加同步关键字
     */
    public synchronized boolean matchUri(String uri) {
        return (null == this.uri && null == uri) || ObjectUtils.equals(String.valueOf(this.uri), uri);
    }

    /**
     * 判断是否仍在被使用着
     */
    public boolean using() {
        return controller.videoAttached(video);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VIPVideoToken)) {
            return false;
        }
        VIPVideoToken another = (VIPVideoToken) o;
        return ObjectUtils.equals(this.video, another.video)
                &&
                ObjectUtils.equals(this.controller, another.controller);
    }

    @Override
    public String toString() {
        return "video token { " + video + " }";
    }

    // a token just like a real video, can do whatever videos are able
    // all possible internal states
    /*package*/ static final int STATE_ERROR              = -1;
    /*package*/ static final int STATE_IDLE               = 0;
    /*package*/ static final int STATE_PREPARING          = 1;
    /*package*/ static final int STATE_PREPARED           = 2;
    /*package*/ static final int STATE_PLAYING            = 3;
    /*package*/ static final int STATE_PAUSED             = 4;
    /*package*/ static final int STATE_PLAYBACK_COMPLETED = 5;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    /*package*/ int currentState = STATE_IDLE;
    /*package*/ int targetState  = STATE_IDLE;

    // temp data start
    /*package*/ int seekWhenPrepared;
    /**
     * 视频信息中当前设置的视频资源的本地资源地址
     */
    /*package*/ Uri playUri;

    /*package*/ VideoControlCallback stateCb;

    // temp data end

    /**
     * 判断是否在可以播放的状态
     */
    /*package*/ boolean isInPlaybackState() {
        return (currentState != STATE_ERROR &&
                currentState != STATE_IDLE &&
                currentState != STATE_PREPARING);
    }

    /**
     * 更新信息
     *
     * <br/>
     *
     * 由于获取和设置可能在不同线程，增加同步关键字
     */
    /*package*/ synchronized void setVideoURI(Uri uri, Map<String, String> headers) {
        this.headers = headers;
        if (!matchUri(uri)) { // 如果URL不一样
            this.uri = uri;
            this.playUri = null;
        }
    }

}
