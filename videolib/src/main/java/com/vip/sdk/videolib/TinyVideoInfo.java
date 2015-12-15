package com.vip.sdk.videolib;

import android.net.Uri;

import com.vip.sdk.base.utils.ObjectUtils;

import java.util.Map;

/**
 *
 * 控制器中管理的视频信息
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public class TinyVideoInfo {

    /**
     * 视频信息中存储的当前设置的视频资源地址
     */
    public Uri uri;

    public Map<String, String> headers;

    public Uri playUri;

    /**
     * 视频组件{@link TinyVideo}
     */
    public final TinyVideo video;

    /**
     * 管理该视频信息的{@link TinyController}
     */
    public final TinyController controller;

    public TinyVideoInfo(TinyController controller, TinyVideo video) {
        this.controller = controller;
        this.video = video;
    }

    public boolean matchUri(Uri uri) {
        return ObjectUtils.equals(this.uri, uri);
    }

    public boolean matchUri(String uri) {
        return ObjectUtils.equals(String.valueOf(this.uri), uri);
    }

    public boolean matchPlayUri(Uri uri) {
        return ObjectUtils.equals(this.playUri, uri);
    }

    public boolean matchPlayUri(String uri) {
        return ObjectUtils.equals(String.valueOf(this.playUri), uri);
    }

    public boolean attached() {
        return controller.isVideoAttached(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TinyVideoInfo)) {
            return false;
        }
        TinyVideoInfo another = (TinyVideoInfo) o;
        return ObjectUtils.equals(this.video, another.video);
    }
}
