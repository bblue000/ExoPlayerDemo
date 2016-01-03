package com.vip.sdk.uilib.media.video.widget;

import android.view.View;

import com.vip.sdk.base.utils.ViewUtils;
import com.vip.sdk.uilib.media.video.VIPVideo;
import com.vip.sdk.uilib.video.R;

/**
* 视频加载过程的浮层。
*
* 该类浮层不需要对播放、暂停等状态进行特别的处理，
* 只需要对未完成加载（包括未加载和加载中两个状态下）和停止时的状态进行自身的显示逻辑
*
* Created by Yin Yong on 15/12/31.
*/
public class SimplePreparingOverlay extends VideoOverlay.SimpleVideoOverlay {

    // overlay
    protected View mOverlayPlayV;
    protected View mOverlayLoadingV;

    @Override
    public void init(VideoPanelView parent, VIPVideo video) {
        mOverlayPlayV = parent.findViewById(R.id.lite_overlay_play_iv);
        mOverlayLoadingV = parent.findViewById(R.id.lite_overlay_loading_pb);
    }

    @Override
    public void showNormalState(VideoPanelView parent, VIPVideo video) {
        ViewUtils.setViewVisible(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);
    }

    public void showLoadingState(VideoPanelView parent, VIPVideo video) {
        ViewUtils.setViewGone(mOverlayPlayV);
        ViewUtils.setViewVisible(mOverlayLoadingV);
    }

    @Override
    public void onStateLoading(VideoPanelView parent, VIPVideo video) {
        showLoadingState(parent, video);
    }

    @Override
    public void onLoadingProgress(VideoPanelView parent, VIPVideo video, long current, long total) {
        // 可能需要做类似微信朋友圈小视频的加载进度

    }

    @Override
    public void onEnterPlaybackState(VideoPanelView parent, VIPVideo video) {
        // 进入播放状态时，我们需要隐藏掉该层
        ViewUtils.setViewGone(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);
    }

    @Override
    public void onExitPlaybackState(VideoPanelView parent, VIPVideo video) {
        // showNormalState(parent, video);
    }
}
