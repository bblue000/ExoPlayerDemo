package com.vip.sdk.videolib;

import android.net.Uri;

import com.vip.sdk.videolib.autoplay.AutoPlayStrategy;
import com.vip.sdk.videolib.autoplay.NetDependStrategy;
import com.vip.sdk.videolib.download.SimpleTinyDownloader;
import com.vip.sdk.videolib.download.TinyDownloader;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * 视频小组件的“上帝类”——单界面的所有视频组件的全局控制器
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/11.
 *
 * @since 1.0
 */
public class TinyController {

    private AutoPlayStrategy mAutoPlayStrategy;
    private TinyDownloader mTinyDownloader;
    private LinkedHashMap<TinyVideo, TinyVideoInfo> mVideoInfoMap = new LinkedHashMap<TinyVideo, TinyVideoInfo>();
    /**
     * 创建一个新的对象
     */
    public static TinyController create() {
        return new TinyController();
    }

    protected TinyController() {

    }

    /**
     * 设置自动播放策略，默认为{@link NetDependStrategy}。
     */
    public TinyController autoPlayStrategy(AutoPlayStrategy strategy) {
        mAutoPlayStrategy = strategy;
        return this;
    }

    /**
     * 设置下载器，默认为{@link NetDependStrategy}。
     */
    public TinyController downloader(TinyDownloader downloader) {
        mTinyDownloader = downloader;
        return this;
    }

    /**
     * 销毁中间数据，一般是在界面关闭时调用
     */
    public void destroy() {
        synchronized (mVideoInfoMap) {
            mVideoInfoMap.clear();
        }
    }

    // internal
    protected AutoPlayStrategy getAutoPlayStrategy() {
        if (null == mAutoPlayStrategy) {
            synchronized (this) {
                if (null == mAutoPlayStrategy) { // 使用默认的
                    mAutoPlayStrategy = new NetDependStrategy();
                }
            }
        }
        return mAutoPlayStrategy;
    }

    protected TinyDownloader getTinyDownloader() {
        if (null == mTinyDownloader) {
            synchronized (this) {
                if (null == mTinyDownloader) { // 使用默认的
                    mTinyDownloader = new SimpleTinyDownloader();
                }
            }
        }
        return mTinyDownloader;
    }

    // 转发来自TinyVideo的操作，确保形成闭环
    /*package*/ void dispatchAttachVideo(TinyVideo video) {
        synchronized (mVideoInfoMap) {
            if (mVideoInfoMap.containsKey(video)) {
                return;
            }
            mVideoInfoMap.put(video, new TinyVideoInfo(this, video));
        }
    }

    /**
     * 将指定的video从控制器中移除，不再对其进行管理
     */
    /*package*/ void dispatchDetachVideo(TinyVideo video) {
        synchronized (mVideoInfoMap) {
            mVideoInfoMap.remove(video);
        }
    }

    /**
     * 转发来自视频的设置，由该处统一管理
     */
    /*package*/ void dispatchSetUri(TinyVideo video, Uri uri, Map<String, String> headers) {
        TinyVideoInfo info;
        synchronized (mVideoInfoMap) {
            info = mVideoInfoMap.get(video);
        }
        if (null != info) {
            synchronized (info) {
                info.uri = uri;
                info.headers = headers;
            }
            getTinyDownloader().download(info, mTinyDownloadCallback);
        }
    }

    /**
     * 指定视频组件是否仍被管理
     */
    /*package*/ boolean isVideoAttached(TinyVideoInfo info) {
        synchronized (mVideoInfoMap) {
            return mVideoInfoMap.containsValue(info);
        }
    }

    protected void dispatchOnDownloadSuccess(TinyVideoInfo info, String uri, Uri target) {
        synchronized (mVideoInfoMap) {
            if (info.attached() && info.matchUri(uri) && null != target) {
                // play
                info.video.superSetVideoURI(target);
                info.video.superStart();
            }
        }
    }

    protected void dispatchOnDownloadFailed(TinyVideoInfo info, String uri, LoadErrInfo status) {
        synchronized (mVideoInfoMap) {
            if (info.attached() && info.matchUri(uri)) {
                info.video.dispatchError(status);
            }
        }
    }

    protected void dispatchOnDownloadCanceled(TinyVideoInfo info, String uri, Uri target) {
        dispatchOnDownloadSuccess(info, uri, target);
    }

    private TinyDownloader.TinyDownloadCallback mTinyDownloadCallback = new TinyDownloader.TinyDownloadCallback() {
        @Override
        public void onSuccess(TinyVideoInfo info, String uri, Uri target) {
            dispatchOnDownloadSuccess(info, uri, target);
        }

        @Override
        public void onFailed(TinyVideoInfo info, String uri, LoadErrInfo status) {
            dispatchOnDownloadFailed(info, uri, status);
        }

        @Override
        public void onCanceled(TinyVideoInfo info, String uri, Uri target) {
            dispatchOnDownloadCanceled(info, uri, target);
        }
    };

}
