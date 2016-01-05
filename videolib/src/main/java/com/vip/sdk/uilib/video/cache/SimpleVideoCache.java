package com.vip.sdk.uilib.video.cache;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.vip.sdk.uilib.video.VIPVideoToken;

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
        Uri uri = video.uri;
        if (null == uri) return;

        final String url = String.valueOf(uri);
        final String scheme = uri.getScheme();

        // 过滤掉一些系统本就支持的项
        if (ContentResolver.SCHEME_FILE.equals(scheme)
                || ContentResolver.SCHEME_CONTENT.equals(scheme)
                || ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            callback.onCacheSuccess(video, url, uri);
            return;
        }

        // do HTTP/HTTPS \, etc. cache
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
