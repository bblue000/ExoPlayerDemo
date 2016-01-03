package com.vip.sdk.uilib.media.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vip.sdk.base.utils.ViewUtils;
import com.vip.sdk.uilib.media.video.VIPVideo;
import com.vip.sdk.uilib.video.R;

/**
 * Created by Yin Yong on 16/1/3.
 */
public class FullScreenVideoPanelView extends BaseVideoPanelView<FullScreenVideoPanelView> {

    protected VIPVideo mVideo;
    // overlay
    protected View mOverlayPlayV;
    protected View mOverlayLoadingV;
    // control
    protected View mControlLayout;
    protected View mSeekControlLayout;
    protected ImageView mControlPlayPauseIv;
    protected SeekBar mControlSeekBar;
    protected TextView mCurrentTimeTv;
    protected TextView mTotalTimeTv;
    protected ProgressBar mTinyProgressBar;

    public FullScreenVideoPanelView(Context context) {
        super(context);
    }

    public FullScreenVideoPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.lite_video_panel, this);
        mVideo = (VIPVideo) findViewById(R.id.lite_video);
        // overlay
        mOverlayPlayV = findViewById(R.id.lite_overlay_play_iv);
        mOverlayLoadingV = findViewById(R.id.lite_overlay_loading_pb);
        // control
        mControlLayout = findViewById(R.id.lite_control_layout);
        mSeekControlLayout = mControlLayout.findViewById(R.id.lite_media_seek_control_layout);
        mControlPlayPauseIv = (ImageView) mSeekControlLayout.findViewById(R.id.vc_play_pause_iv);
        mControlSeekBar = (SeekBar) mSeekControlLayout.findViewById(R.id.vc_slider);
        mCurrentTimeTv = (TextView) mSeekControlLayout.findViewById(R.id.vc_current_time_tv);
        mTotalTimeTv = (TextView) mSeekControlLayout.findViewById(R.id.vc_total_time_tv);
        mTinyProgressBar = (ProgressBar) mControlLayout.findViewById(R.id.lite_tiny_progress_pb);

        initListener();
        showNormalState();
    }

    protected void initListener() {
        ViewUtils.setOnClickListener(mOverlayPlayV, this);
        ViewUtils.setOnClickListener(mVideo, this);
        ViewUtils.setOnClickListener(mControlPlayPauseIv, this);
        mControlSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public VIPVideo getVideo() {
        return mVideo;
    }

    @Override
    protected void showNormalState() {
        ViewUtils.setViewVisible(mVideo);
        ViewUtils.setViewVisible(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);
        ViewUtils.setViewGone(mControlLayout);
    }

    @Override
    protected void updatePlayProgress(FullScreenVideoPanelView parent, VIPVideo video, int position, int progress) {
        final int duration = mVideo.getDuration();
        if (duration > 0) {
            mControlSeekBar.setProgress(progress);
            mTinyProgressBar.setProgress(progress);
        }
        mCurrentTimeTv.setText(formatTime(position));
        mTotalTimeTv.setText(formatTime(duration));
    }

    @Override
    protected boolean isControlBarShowing(FullScreenVideoPanelView parent, VIPVideo video) {
        return !ViewUtils.isVisible(mSeekControlLayout);
    }

    @Override
    protected void showControlBar(FullScreenVideoPanelView parent, VIPVideo video) {
        ViewUtils.setViewVisible(mControlLayout);
        ViewUtils.setViewVisible(mSeekControlLayout);
        ViewUtils.setViewGone(mTinyProgressBar);
    }

    @Override
    protected void hideControlBar(FullScreenVideoPanelView parent, VIPVideo video) {
        // 仅仅是隐藏控制bar，
        ViewUtils.setViewVisible(mControlLayout);
        ViewUtils.setViewGone(mSeekControlLayout);
        ViewUtils.setViewVisible(mTinyProgressBar);
    }
    // progress update end

    @Override
    public void onClick(View v) {
        if (v == mOverlayPlayV) {
            start();
        } else if (v == mVideo) {
            performVideoPanelClick();
        } else if (v == mControlPlayPauseIv) {
            togglePlayPause(true);
        }
    }

    @Override
    protected void onStateLoading(FullScreenVideoPanelView parent, VIPVideo video) {
        super.onStateLoading(parent, video);
        ViewUtils.setViewGone(mOverlayPlayV);
        ViewUtils.setViewVisible(mOverlayLoadingV);
    }

    @Override
    protected void onStateStart(FullScreenVideoPanelView parent, VIPVideo video) {
        super.onStateStart(parent, video);
        updateVideoControl();
    }

    @Override
    protected void onStatePause(FullScreenVideoPanelView parent, VIPVideo video) {
        super.onStatePause(parent, video);
        ViewUtils.setViewGone(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);
        updateVideoControl();
    }

    @Override
    protected void onStateStop(FullScreenVideoPanelView parent, VIPVideo video) {
        super.onStateStop(parent, video);
        ViewUtils.setViewVisible(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);
        updateVideoControl();
    }

    @Override
    protected void onEnterPlaybackState(FullScreenVideoPanelView parent, VIPVideo video) {
        super.onEnterPlaybackState(parent, video);
        ViewUtils.setViewGone(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);

        // 进度UI的展示
        ViewUtils.setViewVisible(mControlLayout);
        ViewUtils.setViewVisible(mSeekControlLayout);
        ViewUtils.setViewGone(mTinyProgressBar);
    }

    @Override
    protected void onExitPlaybackState(FullScreenVideoPanelView parent, VIPVideo video) {
        super.onExitPlaybackState(parent, video);
    }

    /**
     * 根据当前播放状态，显示播放/暂停按钮图标
     */
    protected void updateVideoControl() {
        if (mVideo.isPlaying()) {
            mControlPlayPauseIv.setImageResource(R.drawable.vc_video_pause);
        } else {
            mControlPlayPauseIv.setImageResource(R.drawable.vc_video_play);
        }
    }

    @Override
    public void onLoadProgress(VIPVideo video, String url, long current, long total) {

    }

}
