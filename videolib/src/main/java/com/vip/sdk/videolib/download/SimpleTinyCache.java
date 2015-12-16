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
public class SimpleTinyCache implements TinyCache {

    @Override
    public void load(final TinyVideoInfo tinyVideoInfo, final TinyCacheCallback callback) {
        VideoAjaxCallback.download(tinyVideoInfo, callback);
    }

}
