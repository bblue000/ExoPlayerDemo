package com.vip.sdk.uilib.media.video.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.vip.sdk.base.utils.ViewUtils;
import com.vip.sdk.uilib.media.video.VIPVideo;
import com.vip.sdk.uilib.media.video.VideoController;
import com.vip.sdk.uilib.video.R;

/**
 *
 * 简单封装的组件。
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/29.
 *
 * @since 1.0
 */
public class VideoPanelView extends BaseVideoPanelView<VideoPanelView> {

    protected VIPVideo mVideo;
    // cover
    protected View mCoverLayout;
    protected ImageView mCoverIv;
    // overlay
    protected View mOverlayPlayV;
    protected View mOverlayLoadingV;
    // control
    protected View mControlLayout;
    protected View mSeekControlLayout;
    protected ImageView mControlPlayPauseIv;
    protected View mControlFullScreenV;
    protected SeekBar mControlSeekBar;
    protected TextView mCurrentTimeTv;
    protected TextView mTotalTimeTv;
    protected ProgressBar mTinyProgressBar;

    private Animation mHideCoverAnim;
    {
        mHideCoverAnim = new AlphaAnimation(1.0f, 0f);
        mHideCoverAnim.setDuration(500);
    }

    public VideoPanelView(Context context) {
        super(context);
        initView(context);
    }

    public VideoPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VideoPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.lite_video_panel, this);
        mVideo = (VIPVideo) findViewById(R.id.lite_video);
        // cover
        mCoverLayout = findViewById(R.id.lite_cover_layout);
        mCoverIv = (ImageView) mCoverLayout.findViewById(R.id.lite_cover_iv);
        // overlay
        mOverlayPlayV = findViewById(R.id.lite_overlay_play_iv);
        mOverlayLoadingV = findViewById(R.id.lite_overlay_loading_pb);
        // control
        mControlLayout = findViewById(R.id.lite_control_layout);
        mSeekControlLayout = mControlLayout.findViewById(R.id.lite_media_seek_control_layout);
        mControlPlayPauseIv = (ImageView) mSeekControlLayout.findViewById(R.id.overlay_control_play_pause_iv);
        mControlSeekBar = (SeekBar) mSeekControlLayout.findViewById(R.id.overlay_control_seek_sb);
        mCurrentTimeTv = (TextView) mSeekControlLayout.findViewById(R.id.overlay_control_current_time_tv);
        mTotalTimeTv = (TextView) mSeekControlLayout.findViewById(R.id.overlay_control_total_time_tv);
        mControlFullScreenV = mSeekControlLayout.findViewById(R.id.overlay_control_full_v);
        mTinyProgressBar = (ProgressBar) mControlLayout.findViewById(R.id.lite_tiny_progress_pb);

        initListener();
        showNormalState();
    }

    protected void initListener() {
        ViewUtils.setOnClickListener(mOverlayPlayV, this);
        ViewUtils.setOnClickListener(mVideo, this);
        ViewUtils.setOnClickListener(mControlPlayPauseIv, this);
        ViewUtils.setOnClickListener(mControlFullScreenV, this);
        mControlSeekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * 将界面重置为初始状态（仅仅针对界面中显示/隐藏元素）
     */
    @Override
    protected void showNormalState() {
        ViewUtils.setViewVisible(mVideo);
        ViewUtils.setViewVisible(mCoverLayout);
        ViewUtils.setViewVisible(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);
        ViewUtils.setViewGone(mControlLayout);
    }

    /**
     * 设置初始数据
     */
    public void setData(VideoController videoController, String videoPath, String coverPath) {
        showNormalState();
        setVideoData(videoController, videoPath);
        setCoverImage(coverPath);
    }

    protected AQuery mAQuery;
    /**
     * 设置封面图片
     * @param path 封面图片地址
     */
    public void setCoverImage(String path) {
        if (null == mAQuery) {
            mAQuery = new AQuery(getContext());
        }
        mAQuery.id(mCoverIv).image(path, true, true);
    }

    /**
     * 设置封面图片
     */
    public void setCoverImage(Drawable drawable) {
        mCoverIv.setImageDrawable(drawable);
    }

    /**
     * 设置封面图片
     */
    public void setCoverImage(Bitmap bm) {
        mCoverIv.setImageBitmap(bm);
    }

    public void setCoverImage(int res) {
        mCoverIv.setImageResource(res);
    }

    /**
     * 获得布局中的{@link VIPVideo}
     */
    @Override
    public VIPVideo getVideo() {
        return mVideo;
    }

    @Override
    protected void updatePlayProgress(VideoPanelView parent, VIPVideo video, int position, int progress) {
        final int duration = mVideo.getDuration();
        if (duration > 0) {
            mControlSeekBar.setProgress(progress);
            mTinyProgressBar.setProgress(progress);
        }
        mCurrentTimeTv.setText(formatTime(position) + "/");
        mTotalTimeTv.setText(formatTime(duration));
    }

    @Override
    protected boolean isControlBarShowing(VideoPanelView parent, VIPVideo video) {
        return !ViewUtils.isVisible(mSeekControlLayout);
    }

    @Override
    protected void showControlBar(VideoPanelView parent, VIPVideo video) {
        ViewUtils.setViewVisible(mControlLayout);
        ViewUtils.setViewVisible(mSeekControlLayout);
        ViewUtils.setViewGone(mTinyProgressBar);
    }

    @Override
    protected void hideControlBar(VideoPanelView parent, VIPVideo video) {
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
        } else if (v == mControlFullScreenV) {
            onFullScreenClicked();
        }
    }

    protected void onFullScreenClicked() {
        FullScreenVideoActivity.startMe(getContext(), null);
    }

    @Override
    protected void onStateLoading(VideoPanelView parent, VIPVideo video) {
        super.onStateLoading(parent, video);
        ViewUtils.setViewGone(mOverlayPlayV);
        ViewUtils.setViewVisible(mOverlayLoadingV);
    }

    @Override
    protected void onStateStart(VideoPanelView parent, VIPVideo video) {
        super.onStateStart(parent, video);
        updateVideoControl();
    }

    @Override
    protected void onStatePause(VideoPanelView parent, VIPVideo video) {
        super.onStatePause(parent, video);
        ViewUtils.setViewGone(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);
        updateVideoControl();
    }

    @Override
    protected void onStateStop(VideoPanelView parent, VIPVideo video) {
        super.onStateStop(parent, video);
        ViewUtils.setViewVisible(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);
        updateVideoControl();
    }

    @Override
    protected void onEnterPlaybackState(VideoPanelView parent, VIPVideo video) {
        super.onEnterPlaybackState(parent, video);
        ViewUtils.setViewGone(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);

        mCoverLayout.startAnimation(mHideCoverAnim);
        ViewUtils.setViewGone(mCoverLayout);
        // 进度UI的展示
        ViewUtils.setViewVisible(mControlLayout);
        ViewUtils.setViewVisible(mSeekControlLayout);
        ViewUtils.setViewGone(mTinyProgressBar);
    }

    @Override
    protected void onExitPlaybackState(VideoPanelView parent, VIPVideo video) {
        super.onExitPlaybackState(parent, video);
    }

    /**
     * 根据当前播放状态，显示播放/暂停按钮图标
     */
    protected void updateVideoControl() {
        if (mVideo.isPlaying()) {
            mControlPlayPauseIv.setImageResource(R.drawable.oc_video_pause);
        } else {
            mControlPlayPauseIv.setImageResource(R.drawable.oc_video_play);
        }
    }

    @Override
    public void onLoadProgress(VIPVideo video, String url, long current, long total) {

    }

}
