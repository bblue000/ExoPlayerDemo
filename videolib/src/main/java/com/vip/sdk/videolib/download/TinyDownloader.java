package com.vip.sdk.videolib.download;

import android.net.Uri;

import com.vip.sdk.api.VipAPIStatus;
import com.vip.sdk.videolib.TinyVideoInfo;

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
public interface TinyDownloader {

    /**
     * 根据视频信息下载视频
     */
    void download(TinyVideoInfo video, TinyDownloadCallback callback);

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
    interface TinyDownloadCallback {

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
        void onFailed(TinyVideoInfo info, String uri, VipAPIStatus status) ;

        /**
         * 取消下载时的回调
         * @param info 视频信息
         * @param target 如果已经下载成功，则也返回目标文件
         */
        void onCanceled(TinyVideoInfo info, String uri, Uri target);

    }
}
