package com.vip.sdk.videolib.download;

import android.net.Uri;

import com.androidquery.callback.AjaxStatus;
import com.vip.sdk.videolib.LoadErrInfo;
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
public class SimpleTinyDownloader implements TinyDownloader {

    @Override
    public void download(final TinyVideoInfo video, final TinyDownloadCallback callback) {
        new VideoAjaxCallback() {
            @Override
            public void callback(String url, File object, AjaxStatus status) {
                super.callback(url, object, status);
                if (status.getCode() == 200) {
                    callback.onSuccess(video, url, null == object ? null : Uri.fromFile(object));
                } else {
                    callback.onFailed(video, url, new LoadErrInfo(status.getCode(), status.getMessage()));
                }
            }

            @Override
            protected void skip(String url, File object, AjaxStatus status) {
                super.skip(url, object, status);
                callback.onCanceled(video, url, null == object ? null : Uri.fromFile(object));
            }

        }.async(video.video.getContext().getApplicationContext());
    }

}
