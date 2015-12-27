package com.vip.sdk.videolib.download;

import android.content.Context;

import com.vip.sdk.videolib.TinyVideoInfo;

import java.io.File;

/**
 *
 * 默认的下载器
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public class SimpleVideoCache implements VideoCache {

    @Override
    public void load(final TinyVideoInfo tinyVideoInfo, final TinyCacheCallback callback) {
        VideoAjaxCallback.download(tinyVideoInfo, callback);
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
