package com.vip.sdk.uilib.media.video.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.vip.sdk.base.utils.ToastUtils;
import com.vip.sdk.base.utils.ViewUtils;
import com.vip.sdk.uilib.media.video.VIPVideo;
import com.vip.sdk.uilib.media.video.VideoControlCallback;
import com.vip.sdk.uilib.media.video.VideoController;
import com.vip.sdk.uilib.video.R;

/**
 *
 * 简单封装的组件
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/29.
 *
 * @since 1.0
 */
public class VideoPanelView extends RelativeLayout implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, VideoControlCallback {

    protected VideoController mVideoController;

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

    protected boolean mInPlaybackState;
    protected boolean mUserSeeking;
    private Animation mHideCoverAnim;
    {
        mHideCoverAnim = new AlphaAnimation(1.0f, 0f);
        mHideCoverAnim.setDuration(500);
    }
    protected Runnable mHideSeekControlRun = new Runnable() {
        @Override
        public void run() {
            if (mUserSeeking) {
                delayHideSeekControl();
            } else {
                ViewUtils.setViewGone(mSeekControlLayout);
                ViewUtils.setViewVisible(mTinyProgressBar);
            }
        }
    };

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
    public Handler getHandler() {
        return AQUtility.getHandler();
    }

    private void initView(Context context) {
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

    public void showNormalState() {
        ViewUtils.setViewVisible(mVideo);
        ViewUtils.setViewVisible(mCoverLayout);
        ViewUtils.setViewVisible(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);
        ViewUtils.setViewGone(mControlLayout);
    }

    public void setData(VideoController videoController, String videoPath, String coverPath) {
        setVideoController(videoController);
        setVideoPath(videoPath);
        setCoverPath(coverPath);
        showNormalState();
    }

    /**
     * 设置{@link VideoController}
     */
    protected void setVideoController(VideoController videoController) {
        if (mVideoController == videoController) {
            return;
        }
        if (null != mVideoController) {
            mVideoController.setControlCallback(mVideo, null);
        }
        mVideoController = videoController;
        if (null != mVideoController) {
            mVideoController.setControlCallback(mVideo, this);
        }
    }

    protected void setVideoPath(String path) {
        if (null != mVideoController) {
            mVideoController.setVideoPath(mVideo, path);
        }
    }

    protected AQuery mAQuery;
    /**
     * 设置封面图片
     * @param path 封面图片地址
     */
    public void setCoverPath(String path) {
        if (null == mAQuery) {
            mAQuery = new AQuery(getContext());
        }
        mAQuery.id(mCoverIv).image(path, true, true);
    }

    public VIPVideo getVideo() {
        return mVideo;
    }

    // progress update start
    protected Runnable mPlayTimeRun = new Runnable() {
        @Override
        public void run() {
            onProgressChanged(mControlSeekBar, calculateProgress() , true);
            getHandler().postDelayed(this, 1000L);
        }
    };

    protected void startPlayTimer() {
        onProgressChanged(mControlSeekBar, calculateProgress(), true);
        int len = mVideo.getDuration();
        int millisec = len % 1000;
        if (millisec < 100) {
            millisec = 0;
        }
        getHandler().postDelayed(mPlayTimeRun, millisec);
    }

    protected void cancelPlayTimer() {
        getHandler().removeCallbacks(mPlayTimeRun);
    }

    protected void updatePlayProgress(int position) {
        updatePlayBarProgress(position);
        updatePlayProgressText(position);
    }

    protected void updatePlayBarProgress(int position) {
        if (mVideo.getDuration() <= 0) {
            return;
        }
        int progress = (position * 100) / mVideo.getDuration();
        mControlSeekBar.setProgress(progress);
        mTinyProgressBar.setProgress(progress);
    }

    protected void updatePlayProgressText(int currentPos) {
        mCurrentTimeTv.setText(formatTime(currentPos) + "/");
        mTotalTimeTv.setText(formatTime(mVideo.getDuration()));
    }

    protected int calculateProgress() {
        int total = mVideo.getDuration();
        if (total <= 0) {
            return 0;
        }
        int cur = mVideo.getCurrentPosition();
        return (cur * 100) / total;
    }

    protected String formatTime(int timeInMillis) {
        int timeInSec = timeInMillis / 1000;
        if (timeInMillis < 1000 && timeInMillis > 0) {
            timeInSec = 1;
        }
        int totalMin = timeInSec / 60;
        int hour = totalMin / 60;
        int min = totalMin % 60;
        int sec = timeInSec % 60;

        if (hour == 0) {
            return String.format("%02d:%02d", min, sec);
        }
        return String.format("%02d:%02d:%02d", min, sec);
    }
    // progress update end

    @Override
    public void onClick(View v) {
        if (v == mOverlayPlayV) {
            onOverlayPlayClicked();
        } else if (v == mVideo) {
            onVideoViewClicked();
        } else if (v == mControlPlayPauseIv) {
            onControlPlayPauseClicked();
        } else if (v == mControlFullScreenV) {
            onFullScreenClicked();
        }
    }

    protected void onOverlayPlayClicked() {
        if (null != mVideoController) {
            mVideoController.start(mVideo);
        }
    }

    protected void onVideoViewClicked() {
        if (mInPlaybackState) { // 如果处于可以播放的状态之中，才处理这样的逻辑
            if (ViewUtils.isGone(mSeekControlLayout)) {
                ViewUtils.setViewVisible(mSeekControlLayout);
                ViewUtils.setViewGone(mTinyProgressBar);
                delayHideSeekControl();
            } else {
                cancelHideSeekControl();
                mHideSeekControlRun.run();
            }
        }
    }

    protected void onControlPlayPauseClicked() {
        if (null != mVideoController) {
            if (mVideo.isPlaying()) {
                mVideoController.pause(mVideo);
                cancelPlayTimer(); // 不再计时，稍微优化
            } else {
                mVideoController.start(mVideo);
                startPlayTimer();
            }
            updateVideoControl();
            refreshHideSeekControl();
        }
    }

    protected void onFullScreenClicked() {

    }

    @Override
    public void onStateChanged(VIPVideo video, int state, VideoStatus status) {
        switch (state) {
            case STATE_LOADING:
                onStateLoading(video);
                break;
            case STATE_START:
                onStateStart(video);
                break;
            case STATE_PAUSE:
                onStatePause(video);
                break;
            case STATE_COMPLETION:
            case STATE_STOP:
                onStateStop(video);
                break;
            case STATE_LOAD_ERR:
                onStateLoadErr(video, status);
                break;
            case STATE_ERR:
                onStateErr(video, status);
                break;
        }
    }

    protected void onStateLoading(VIPVideo video) {
        if (mInPlaybackState) {
            onExitPlaybackState();
            mInPlaybackState = false;
        }
        ViewUtils.setViewVisible(mOverlayLoadingV);
        ViewUtils.setViewGone(mOverlayPlayV);
    }

    protected void onStateStart(VIPVideo video) {
        if (!mInPlaybackState) {
            onEnterPlaybackState();
            mInPlaybackState = true;
        }
        updateVideoControl();
    }

    protected void onStatePause(VIPVideo video) {
        ViewUtils.setViewGone(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);
        updateVideoControl();
    }

    protected void onStateStop(VIPVideo video) {
        onExitPlaybackState();
        mInPlaybackState = false;
    }

    protected void onStateLoadErr(VIPVideo video, VideoStatus status) {
        onStateStop(video); // 相当于执行stop
        ToastUtils.showToast(status.message);
    }

    protected void onStateErr(VIPVideo video, VideoStatus status) {
        ToastUtils.showToast(status.message);
    }

    protected void refreshHideSeekControl() {
        cancelHideSeekControl();
        delayHideSeekControl();
    }

    protected void delayHideSeekControl() {
        getHandler().postDelayed(mHideSeekControlRun, 3000L);
    }

    protected void cancelHideSeekControl() {
        getHandler().removeCallbacks(mHideSeekControlRun);
    }

    /**
     * 进入状态可播放状态
     */
    protected void onEnterPlaybackState() {
        ViewUtils.setViewGone(mOverlayPlayV);
        ViewUtils.setViewGone(mOverlayLoadingV);

        mCoverLayout.startAnimation(mHideCoverAnim);
        ViewUtils.setViewGone(mCoverLayout);
        // 进度UI的展示
        ViewUtils.setViewVisible(mControlLayout);
        ViewUtils.setViewVisible(mSeekControlLayout);
        ViewUtils.setViewGone(mTinyProgressBar);
        delayHideSeekControl();
        startPlayTimer();
    }

    /**
     * 退出状态可播放状态
     */
    protected void onExitPlaybackState() {
        cancelHideSeekControl();
        cancelPlayTimer();
        updateVideoControl();
        showNormalState();
    }

    /**
     * 根据当前播放状态，显示播放暂停
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int position = (mVideo.getDuration() * progress) / 100;
            updatePlayProgress(position);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mUserSeeking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mUserSeeking = false;
        if (mInPlaybackState) {
            int position = (mVideo.getDuration() * seekBar.getProgress()) / 100;
            updatePlayProgress(position);
            if (null != mVideoController) {
                mVideoController.seekTo(mVideo, position);
                mVideoController.start(mVideo); // 如果原先不在播放的话
            }
            refreshHideSeekControl();
        }
    }
}
