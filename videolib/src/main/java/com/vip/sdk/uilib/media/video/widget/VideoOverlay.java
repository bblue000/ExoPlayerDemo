package com.vip.sdk.uilib.media.video.widget;

import com.vip.sdk.uilib.media.video.VIPVideo;

/**
 * 视频组件上的浮层逻辑处理接口
 *
 * Created by Yin Yong on 15/12/31.
 */
public interface VideoOverlay {

    /**
     * 初始化
     */
    void init(VideoPanelView parent, VIPVideo video);

    /**
     * 正常状态下的显示逻辑，即可视为未播放、也未加载状态时呈现的UI
     */
    void showNormalState(VideoPanelView parent, VIPVideo video);

    /**
     * 视频开始加载时触发
     */
    void onStateLoading(VideoPanelView parent, VIPVideo video);

    /**
     * 视频加载过程中触发
     */
    void onLoadingProgress(VideoPanelView parent, VIPVideo video, int current, int total);

    /**
     * 每次调用播放时都会触发。
     *
     * 如果只对刚开始播放感兴趣，则可以使用{@link #onEnterPlaybackState()}
     */
    void onStateStart(VideoPanelView parent, VIPVideo video);

    /**
     * 每次调用暂停播放时触发。
     */
    void onStatePause(VideoPanelView parent, VIPVideo video);

    /**
     * 每次调用停止播放、播放完成、播放出错时触发。
     */
    void onStateStop(VIPVideo video);

    /**
     * 进入播放状态（从未开始播放状态（或者停止状态）首次进入播放状态时触发）
     */
    void onEnterPlaybackState();

    /**
     * 退出播放状态（从最近一次开始播放停止时触发）。
     *
     * 暂停不会触发该状态的回调
     */
    void onExitPlaybackState();

    /**
     * 缺省实现类
     */
    class SimpleVideoOverlay implements VideoOverlay {
        @Override public void init(VideoPanelView parent, VIPVideo video) { }
        @Override public void showNormalState(VideoPanelView parent, VIPVideo video) { }
        @Override public void onStateLoading(VideoPanelView parent, VIPVideo video) { }
        @Override public void onLoadingProgress(VideoPanelView parent, VIPVideo video, int current, int total) { }
        @Override public void onStateStart(VideoPanelView parent, VIPVideo video) { }
        @Override public void onStatePause(VideoPanelView parent, VIPVideo video) { }
        @Override public void onStateStop(VIPVideo video) { }
        @Override public void onEnterPlaybackState() { }
        @Override public void onExitPlaybackState() { }
    }

}
