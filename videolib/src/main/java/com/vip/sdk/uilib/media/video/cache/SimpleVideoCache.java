package com.vip.sdk.uilib.media.video.cache;

import android.content.Context;

import com.vip.sdk.uilib.media.video.VIPVideoToken;

import java.io.File;

/**
 *
 * 默认缓存
 *
 * Created by Yin Yong on 15/12/27.
 */
public class SimpleVideoCache implements VideoCache {

    @Override
    public void load(VIPVideoToken video, CacheCallback callback) {
        VideoAjaxCallback.download(video, callback);
    }

    @Override
    public File getCacheDir(Context context) {
        return VideoAjaxCallback.getVideoCacheDir(context);
    }

    @Override
    public void clearCache(Context context) {
        VideoAjaxCallback.clearCache(context);
    }
}
