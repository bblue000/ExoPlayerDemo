package com.vip.sdk.uilib.media.video;

import android.net.Uri;

import com.vip.sdk.base.utils.ObjectUtils;

import java.util.Map;

/**
 * 封装被{@link com.vip.sdk.uilib.media.video.VideoController}管理的视频
 * {@link com.vip.sdk.uilib.media.video.VIPVideo}及其资源等信息
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
     * 视频信息中当前设置的视频资源的本地资源地址
     */
    public Uri playUri;

    /**
     * 视频组件{@link com.vip.sdk.videolib.VIPVideo}
     */
    public final VIPVideo video;

    /**
     * 管理该视频信息的{@link com.vip.sdk.videolib.TinyController}
     */
    public final VideoController controller;

    public VIPVideoToken(VideoController controller, VIPVideo video) {
        this.controller = controller;
        this.video = video;
    }

    public boolean matchUri(Uri uri) {
        return ObjectUtils.equals(this.uri, uri);
    }

    public boolean matchUri(String uri) {
        return ObjectUtils.equals(String.valueOf(this.uri), uri);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VIPVideoToken)) {
            return false;
        }
        VIPVideoToken another = (VIPVideoToken) o;
        return ObjectUtils.equals(this.video, another.video);
    }

    @Override
    public String toString() {
        return "video token { " + video + " }";
    }

}
