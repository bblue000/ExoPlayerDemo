package com.vip.sdk.videolib.download;

import com.vip.sdk.videolib.TinyVideoInfo;

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
public class SimpleTinyDownloader implements TinyDownloader {

    @Override
    public void download(final TinyVideoInfo tinyVideoInfo, final TinyDownloadCallback callback) {
        VideoAjaxCallback.download(tinyVideoInfo, callback);
    }

}
