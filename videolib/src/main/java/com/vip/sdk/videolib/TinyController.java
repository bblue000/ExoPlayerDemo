package com.vip.sdk.videolib;

import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup;

import com.vip.sdk.videolib.autoplay.AutoPlayStrategy;
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

    protected static final boolean DEBUG = TinyDebug.CONTROLLER;

    private AutoPlayStrategy mAutoPlayStrategy;
    private TinyCache mTinyCache;
    protected LinkedHashMap<TinyVideoContainer, TinyVideoInfo> mVideoInfoMap = new LinkedHashMap<TinyVideoContainer, TinyVideoInfo>();

    protected TinyController() {
    }

    // =============================================
    // 外部设置
    // =============================================
    /**
     * 设置自动播放策略，默认为{@link NetDependStrategy}。
     */
    public TinyController autoPlayStrategy(AutoPlayStrategy strategy) {
        mAutoPlayStrategy = strategy;
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
     * 根据播放策略、子类逻辑等，决定播放项
     */
    public abstract void determinePlay() ;

    /**
     * 获取容器控件对象
     */
    public abstract ViewGroup getContainer();

    /**
     * 销毁中间数据，一般是在界面关闭时调用
     */
    public void destroy() {
        synchronized (mVideoInfoMap) {
            mVideoInfoMap.clear();
        }
    }

    /**
     * 视频缓存好时调用
     */
    protected abstract void onVideoPrepared(TinyVideoInfo videoInfo, String uri) ;

    /**
     * 视频缓存出错时调用
     */
    protected abstract void onVideoLoadFailed(TinyVideoInfo info, String uri, LoadErrInfo status) ;

    /**
     * 决定是否需要下载
     */
    protected boolean determineLoad(TinyVideoInfo info, Uri uri, Map<String, String> headers) {
        if (null != info) { // 多余的判断不能避免下载出错，导致一直不下载的情况；且会一定程度地引起乱窜
            info.uri = uri;
            info.playUri = null;
            info.headers = headers;
            // info.video.stopPlayback();
            getCache().load(info, mTinyCacheCallback);
            return true;
        }
        return false;
    }

    // internal
    // 转发来自TinyVideo的操作，确保形成闭环
    /*package*/ void dispatchAttachVideo(TinyVideoContainer video) {
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
    /*package*/ void dispatchDetachVideo(TinyVideoContainer video) {
        synchronized (mVideoInfoMap) {
            mVideoInfoMap.remove(video);
        }
    }

    /**
     * 转发来自视频的设置，由该处统一管理
     */
    /*package*/ void dispatchSetUri(TinyVideoContainer video, Uri uri, Map<String, String> headers) {
        TinyVideoInfo info;
        synchronized (mVideoInfoMap) {
            info = mVideoInfoMap.get(video);
        }
        determineLoad(info, uri, headers);
    }
    // end

    /**
     * 指定视频组件是否仍被管理
     */
    /*package*/ boolean isVideoAttached(TinyVideoInfo info) {
        synchronized (mVideoInfoMap) {
            return mVideoInfoMap.containsValue(info);
        }
    }

    protected void dispatchOnDownloadSuccess(TinyVideoInfo info, String uri, long current, long total) {
        //TODO 分配下载进度
    }

    protected void dispatchOnDownloadSuccess(TinyVideoInfo info, String uri, Uri target) {
        if (DEBUG) Log.d("yytest", uri.substring(uri.lastIndexOf("/") + 1) + "下载好了");
        if (info.attached() && info.matchUri(uri)) { // 过滤掉部分
            info.playUri = target;
            onVideoPrepared(info, uri);
        }
    }

    protected void dispatchOnDownloadFailed(TinyVideoInfo info, String uri, LoadErrInfo status) {
        if (DEBUG) Log.d("yytest", uri.substring(uri.lastIndexOf("/") + 1) + "下载失败了");
        onVideoLoadFailed(info, uri, status);
    }

    protected TinyCache.TinyCacheCallback mTinyCacheCallback = new TinyCache.TinyCacheCallback() {

        @Override
        public void onProgress(TinyVideoInfo info, String uri, long current, long total) {
            dispatchOnDownloadSuccess(info, uri, current, total);
        }

        @Override
        public void onSuccess(TinyVideoInfo info, String uri, Uri target) {
            dispatchOnDownloadSuccess(info, uri, target);
        }

        @Override
        public void onFailed(TinyVideoInfo info, String uri, LoadErrInfo status) {
            dispatchOnDownloadFailed(info, uri, status);
        }

    };

    public AutoPlayStrategy getAutoPlayStrategy() {
        if (null == mAutoPlayStrategy) {
            synchronized (this) {
                if (null == mAutoPlayStrategy) { // 使用默认的
                    mAutoPlayStrategy = createDefaultAutoPlayStrategy();
                }
            }
        }
        return mAutoPlayStrategy;
    }

    protected AutoPlayStrategy createDefaultAutoPlayStrategy() {
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
