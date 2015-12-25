package com.vip.sdk.videolib;

import android.net.Uri;
import android.util.Log;

import com.vip.sdk.uilib.media.video.VIPVideoDebug;
import com.vip.sdk.uilib.media.video.VideoState;
import com.vip.sdk.videolib.autoplay.AutoLoadStrategy;
import com.vip.sdk.videolib.autoplay.NetDependStrategy;
import com.vip.sdk.videolib.download.SimpleTinyCache;
import com.vip.sdk.videolib.download.TinyCache;

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
public abstract class TinyController {

    protected static final boolean DEBUG = VIPVideoDebug.CONTROLLER;

    private AutoLoadStrategy mAutoLoadStrategy;
    private TinyCache mTinyCache;
    private LinkedHashMap<VIPVideo, TinyVideoInfo> mVideoInfoMap = new LinkedHashMap<VIPVideo, TinyVideoInfo>();

    protected TinyController() {
    }

    // =============================================
    // 外部设置
    // =============================================
    /**
     * 设置自动播放策略，默认为{@link NetDependStrategy}。
     */
    public TinyController autoLoadStrategy(AutoLoadStrategy strategy) {
        mAutoLoadStrategy = strategy;
        return this;
    }

    /**
     * 设置下载器，默认为{@link SimpleTinyCache}。
     */
    public TinyController cache(TinyCache cache) {
        mTinyCache = cache;
        return this;
    }
    // end

    /**
     * 销毁中间数据，一般是在界面关闭时调用
     */
    public void destroy() {
        synchronized (mVideoInfoMap) {
            mVideoInfoMap.clear();
        }
    }

    // internal
    // 转发来自TinyVideo的操作，确保形成闭环
    // =============================================
    // 接收来自TinyVideo的方法调用，经过处理后再转发到TinyVideo
    // =============================================
    protected TinyVideoInfo dispatchAttachVideo(VIPVideo video) {
        synchronized (mVideoInfoMap) {
            TinyVideoInfo info = mVideoInfoMap.get(video);
            if (null == info) {
                info = new TinyVideoInfo(this, video);
                mVideoInfoMap.put(video, new TinyVideoInfo(this, video));
            }
            return info;
        }
    }

    /**
     * 将指定的video从控制器中移除，不再对其进行管理
     */
    protected void dispatchDetachVideo(VIPVideo video) {
        synchronized (mVideoInfoMap) {
            mVideoInfoMap.remove(video);
        }
    }

    /**
     * 转发来自视频的设置，由该处统一管理；
     * 尝试加载/缓存视频
     */
    protected void dispatchFromVideoSetUri(TinyVideoInfo info, Uri uri, Map<String, String> headers) {
        // dispatchToVideoStop(info); // 无论如何先停止
        if (null != info) {
            info.headers = headers;
            if (!info.matchUri(uri)) { // 如果URL不一样
                info.uri = uri;
                info.playUri = null;
            }
            dispatchDownload(info, false);
        }
    }

    /**
     * {@link #dispatchFromVideoSetUri(TinyVideoInfo, Uri, Map)}操作中，
     * 发现相应的uri还没有加载到playUri时将调用该方法
     */
    protected boolean dispatchDownload(TinyVideoInfo info, boolean force) {
        if (null != info) {
            if (null == info.playUri) {
                if (force || getAutoPlayStrategy().autoLoad(info.video)) { // 如果允许自动加载播放，则往下执行
                    getCache().load(info, mTinyCacheCallback);
                    return true;
                }
            } else {
                dispatchOnDownloadSuccess(info, String.valueOf(info.uri), info.playUri);
                return true;
            }
        }
        return false;
    }

    /**
     * 下载完成，准备播放
     */
    protected abstract void dispatchFromVideoPrepared(TinyVideoInfo info) ;

    /**
     * 来自用户操作
     */
    protected abstract void dispatchFromVideoStart(TinyVideoInfo info) ;

    /**
     * 来自用户操作
     */
    protected void dispatchFromVideoStop(TinyVideoInfo info) {
        dispatchToVideoStop(info);
    }
    // end

    protected void dispatchToVideoSetVideoURI(TinyVideoInfo info, Uri playUri) {
        if (null != info) {
            info.video.innerSetVideoURI(info.playUri);
        }
    }

    protected void dispatchToVideoStart(TinyVideoInfo info) {
        if (null != info) {
            info.video.innerStart();
        }
    }

    protected void dispatchToVideoStop(TinyVideoInfo info) {
        if (null != info) {
            info.video.innerStopPlayback();
        }
    }

    protected void dispatchToVideoLoadErr(TinyVideoInfo info, String uri, VideoState status) {
        if (null != info) {
            info.video.dispatchLoadErr(status);
        }
    }
    // end


    // =============================================
    // 下载的统一管理
    // =============================================
    /**
     * 视频缓存好时调用
     */
    protected abstract void onVideoDownloaded(TinyVideoInfo videoInfo, String uri) ;

    /**
     * 视频缓存出错时调用
     */
    protected abstract void onVideoLoadFailed(TinyVideoInfo info, String uri, VideoState status) ;

    protected void dispatchOnDownloadProgress(TinyVideoInfo info, String uri, long current, long total) {
        //TODO 分配下载进度
    }

    protected void dispatchOnDownloadSuccess(TinyVideoInfo info, String uri, Uri target) {
        if (DEBUG) Log.d("yytest", uri.substring(uri.lastIndexOf("/") + 1) + "下载好了");
        if (info.using() && info.matchUri(uri)) { // 过滤掉部分
            info.playUri = target;
            onVideoDownloaded(info, uri);
        }
    }

    protected void dispatchOnDownloadFailed(TinyVideoInfo info, String uri, VideoState status) {
        if (DEBUG) Log.d("yytest", uri.substring(uri.lastIndexOf("/") + 1) + "下载失败了");
        onVideoLoadFailed(info, uri, status);
    }

    protected TinyCache.TinyCacheCallback mTinyCacheCallback = new TinyCache.TinyCacheCallback() {

        @Override
        public void onProgress(TinyVideoInfo info, String uri, long current, long total) {
            dispatchOnDownloadProgress(info, uri, current, total);
        }

        @Override
        public void onSuccess(TinyVideoInfo info, String uri, Uri target) {
            dispatchOnDownloadSuccess(info, uri, target);
        }

        @Override
        public void onFailed(TinyVideoInfo info, String uri, VideoState status) {
            dispatchOnDownloadFailed(info, uri, status);
        }

    };
    // end

    public AutoLoadStrategy getAutoPlayStrategy() {
        if (null == mAutoLoadStrategy) {
            synchronized (this) {
                if (null == mAutoLoadStrategy) { // 使用默认的
                    mAutoLoadStrategy = createDefaultAutoPlayStrategy();
                }
            }
        }
        return mAutoLoadStrategy;
    }

    protected AutoLoadStrategy createDefaultAutoPlayStrategy() {
        return new NetDependStrategy();
    }

    public TinyCache getCache() {
        if (null == mTinyCache) {
            synchronized (this) {
                if (null == mTinyCache) { // 使用默认的
                    mTinyCache = createDefaultDownloader();
                }
            }
        }
        return mTinyCache;
    }

    protected TinyCache createDefaultDownloader() {
        return new SimpleTinyCache();
    }

}