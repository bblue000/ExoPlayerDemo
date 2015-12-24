package com.vip.sdk.videolib.download;

import android.content.Context;
import android.net.Uri;

import com.vip.sdk.uilib.video.VideoStateInfo;
import com.vip.sdk.videolib.TinyVideoInfo;

import java.io.File;

/**
 *
 * 视频下载器
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public interface TinyCache {

    /**
     * 根据视频信息下载视频
     */
    void load(TinyVideoInfo video, TinyCacheCallback callback);

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
    interface TinyCacheCallback {

        /**
         * 下载进度的回调
         * @param info 视频信息
         * @param current 当前已下载的大小
         * @param total 总大小
         */
        void onProgress(TinyVideoInfo info, String uri, long current, long total) ;

        /**
         * 下载成功时的回调
         * @param info 视频信息
         * @param target 目标文件
         */
        void onSuccess(TinyVideoInfo info, String uri, Uri target);

        /**
         * 下载失败时的回调
         * @param info 视频信息
         * @param status 失败信息
         */
        void onFailed(TinyVideoInfo info, String uri, VideoStateInfo status) ;

    }

    /**
     * {@link TinyCacheCallback}的缺省实现，
     * 所有方法都是默认空实现
     */
    class SimpleTinyCacheCallback implements TinyCacheCallback {
        @Override public void onProgress(TinyVideoInfo info, String uri, long current, long total) { }
        @Override public void onSuccess(TinyVideoInfo info, String uri, Uri target) { }
        @Override public void onFailed(TinyVideoInfo info, String uri, VideoStateInfo status) { }
    }
}
