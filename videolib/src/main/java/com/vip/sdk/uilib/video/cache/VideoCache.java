package com.vip.sdk.uilib.video.cache;

import android.content.Context;
import android.net.Uri;

import com.vip.sdk.uilib.video.VIPVideoToken;
import com.vip.sdk.uilib.video.VideoControlCallback;

import java.io.File;

/**
 * 视频缓存的接口。
 *
 * 主要包括加载视频，获取缓存文件夹路径，清除缓存等操作。
 *
 * Created by Yin Yong on 15/12/27.
 */
public interface VideoCache {

    /**
     * 根据视频信息下载视频
     */
    void load(VIPVideoToken video, CacheCallback callback);

    /**
     * 获取缓存文件夹路径
     */
    File getCacheDir(Context context);

    /**
     * 清除缓存
     */
    void clearCache(Context context);

    /**
     *
     * 下载器下载视频的回调
     *
     * <p/>
     * <p/>
     * Created by Yin Yong on 15/12/14.
     *
     * @since 1.0
     */
    interface CacheCallback {

        /**
         * 下载进度的回调
         * @param token 视频信息
         * @param current 当前已下载的大小
         * @param total 总大小
         */
        void onCacheProgress(VIPVideoToken token, String uri, long current, long total) ;

        /**
         * 下载成功时的回调
         * @param token 视频信息
         * @param target 目标文件
         */
        void onCacheSuccess(VIPVideoToken token, String uri, Uri target);

        /**
         * 下载失败时的回调
         * @param token 视频信息
         * @param status 失败信息
         */
        void onCacheFailed(VIPVideoToken token, String uri, VideoControlCallback.VideoStatus status) ;

    }

    /**
     * {@link com.vip.sdk.uilib.video.cache.VideoCache.CacheCallback}的缺省实现——所有方法都是默认空实现
     */
    class SimpleCacheCallback implements CacheCallback {
        @Override public void onCacheProgress(VIPVideoToken token, String uri, long current, long total) { }
        @Override public void onCacheSuccess(VIPVideoToken token, String uri, Uri target) { }
        @Override public void onCacheFailed(VIPVideoToken token, String uri, VideoControlCallback.VideoStatus status) { }
    }
}
