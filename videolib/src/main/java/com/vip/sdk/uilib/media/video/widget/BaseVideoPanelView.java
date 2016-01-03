package com.vip.sdk.uilib.media.video.widget;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.androidquery.util.AQUtility;
import com.vip.sdk.base.utils.ToastUtils;
import com.vip.sdk.base.utils.ViewUtils;
import com.vip.sdk.uilib.media.video.VIPVideo;
import com.vip.sdk.uilib.media.video.VideoControlCallback;
import com.vip.sdk.uilib.media.video.VideoController;

/**
 * Created by Yin Yong on 16/1/2.
 */
public abstract class BaseVideoPanelView<VideoPanelView extends BaseVideoPanelView> extends RelativeLayout
        implements View.OnClickListener, VideoControlCallback, SeekBar.OnSeekBarChangeListener {

    protected VideoController mVideoController;

    protected boolean mInPlaybackState;
    protected boolean mInPlaying;
    protected boolean mUserSeeking; // 用户是否正在拖动
    
    public BaseVideoPanelView(Context context) {
        super(context);
        initView(context);
    }

    public BaseVideoPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BaseVideoPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    public Handler getHandler() {
        return AQUtility.getHandler();
    }

    /**
     * 初始化
     */
    protected abstract void initView(Context context) ;

    /**
     * 获得布局中的{@link com.vip.sdk.uilib.media.video.VIPVideo}
     */
    public abstract VIPVideo getVideo() ;

    private VideoPanelView self() {
        return (VideoPanelView) this;
    }

    /**
     * 设置{@link VideoController}
     */
    public void setVideoData(VideoController videoController, String path) {
        setVideoController(videoController);
        if (null != mVideoController) {
            mVideoController.setVideoPath(getVideo(), path);
        }
    }

    /**
     * 设置{@link VideoController}
     */
    public void setVideoData(VideoController videoController, Uri uri) {
        setVideoController(videoController);
        if (null != mVideoController) {
            mVideoController.setVideoURI(getVideo(), uri);
        }
    }

    /**
     * 设置{@link VideoController}
     */
    private void setVideoController(VideoController videoController) {
        if (mVideoController == videoController) {
            return;
        }
        if (null != mVideoController) {
            mVideoController.setControlCallback(getVideo(), null);
        }
        mVideoController = videoController;
        if (null != mVideoController) {
            mVideoController.setControlCallback(getVideo(), this);
        }
    }

    /**
     * 播放（开始播放，或者）
     */
    public void start() {
        if (null != mVideoController) {
            mVideoController.start(getVideo());
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (null != mVideoController) {
            mVideoController.pause(getVideo());
        }
    }

    /**
     * 切换播放/暂停的状态
     * @param fromUser 如果是用户的操作，将会延迟隐藏进度控制组件
     */
    public void togglePlayPause(boolean fromUser) {
        if (null != mVideoController) {
            if (getVideo().isPlaying()) {
                mVideoController.pause(getVideo());
            } else {
                mVideoController.start(getVideo());
            }
            if (fromUser) {
                delayHideSeekControlBar();
            }
        }
    }

    public void performVideoPanelClick() {
        if (mInPlaybackState) { // 如果处于可以播放的状态之中，才处理这样的逻辑
            if (isControlBarShowing(self(), getVideo())) {
                cancelHideSeekControlBar();
                mHideControlBarRun.run();
            } else {
                showControlBar(self(), getVideo());
                delayHideSeekControlBar();
            }
        }
    }

    // 播放器状态的监听处理 start
    @Override
    public void onStateChanged(VIPVideo video, int state, VideoStatus status) {
        switch (state) {
            case STATE_LOADING:
                onStateLoading(self(), video);
                break;
            case STATE_START:
                onStateStart(self(), video);
                break;
            case STATE_PAUSE:
                onStatePause(self(), video);
                break;
            case STATE_COMPLETION:
            case STATE_STOP:
                onStateStop(self(), video);
                break;
            case STATE_LOAD_ERR:
                onStateLoadErr(self(), video, status);
                break;
            case STATE_ERR:
                onStateErr(self(), video, status);
                break;
        }
    }

    @Override
    public void onLoadProgress(VIPVideo video, String url, long current, long total) {
        // TODO 有没有需要加载进度的，可以覆盖这里
    }

    // 封装之后，对外提供的
    /**
     * 正常状态下的显示逻辑，即可视为未播放、也未加载状态时呈现的UI
     */
    protected abstract void showNormalState() ;

    protected void onStateLoading(VideoPanelView parent, VIPVideo video) {
        if (mInPlaybackState) {
            onExitPlaybackState(parent, video);
            mInPlaybackState = false;
        }
    }

    protected void onStateStart(VideoPanelView parent, VIPVideo video) {
        if (!mInPlaybackState) {
            onEnterPlaybackState(parent, video); // 已经改变了界面的状态
            mInPlaybackState = true;
        }
        if (!mInPlaying) {
            startPlayTimer();
            mInPlaying = true;
        }
    }

    protected void onStatePause(VideoPanelView parent, VIPVideo video) {
        if (mInPlaying) {
            cancelPlayTimer();
            mInPlaying = false;
        }
    }

    protected void onStateStop(VideoPanelView parent, VIPVideo video) {
        if (mInPlaybackState) {
            onExitPlaybackState(parent, video);
            mInPlaybackState = false;
        }
        if (mInPlaying) {
            cancelPlayTimer();
            mInPlaying = false;
        }
    }

    protected void onStateLoadErr(VideoPanelView parent, VIPVideo video, VideoControlCallback.VideoStatus status) {
        ToastUtils.showToast(status.message);
        onStateStop(parent, video); // 相当于执行stop
    }

    protected void onStateErr(VideoPanelView parent, VIPVideo video, VideoControlCallback.VideoStatus status) {
        ToastUtils.showToast(status.message);
    }

    /**
     * 进入播放状态（从未开始播放状态（或者停止状态）首次进入播放状态时触发）
     */
    protected void onEnterPlaybackState(VideoPanelView parent, VIPVideo video) {
        delayHideSeekControlBar();
    }

    /**
     * 退出播放状态（从最近一次开始播放停止时触发）。
     *
     * 暂停不会触发该状态的回调。
     *
     * 该回调之后会立即调用到{@link #showNormalState}
     */
    protected void onExitPlaybackState(VideoPanelView parent, VIPVideo video) {
        cancelHideSeekControlBar();
        showNormalState();
    }
    // end

    // 进度选择的封装 start
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int position = (getVideo().getDuration() * progress) / 100;
            updatePlayProgress(self(), getVideo(), position, progress);
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
            int progress = seekBar.getProgress();
            int position = (getVideo().getDuration() * progress) / 100;
            updatePlayProgress(self(), getVideo(), position, progress);
            if (null != mVideoController) {
                mVideoController.seekTo(getVideo(), position);
                mVideoController.start(getVideo()); // 如果原先不在播放的话
            }
            delayHideSeekControlBar(); // 重新开始延迟隐藏
        }
    }
    // end

    // play time update start
    private Runnable mPlayTimeRun = new Runnable() {
        @Override
        public void run() {
            if (mInPlaybackState) { // 只有在可播放状态下才处理
                updateProgressByCurrentPosition();
                getHandler().postDelayed(this, 1000L);
            }
        }
    };
    private void startPlayTimer() {
        cancelPlayTimer();
        updateProgressByCurrentPosition();
        int len = getVideo().getDuration();
        int millisSec = len % 1000; // 减小一定的误差
        if (millisSec < 100) {
            millisSec = 0;
        }
        getHandler().postDelayed(mPlayTimeRun, millisSec);
    }

    private void cancelPlayTimer() {
        getHandler().removeCallbacks(mPlayTimeRun);
    }

    protected void updateProgressByCurrentPosition() {
        int position = 0, progress = 0;
        int total = getVideo().getDuration();
        if (total > 0) {
            position = getVideo().getCurrentPosition();
            progress = (position * 100) / total;
        }
        updatePlayProgress(self(), getVideo(), position, progress);
    }

    /**
     * 格式化时间，格式为XX:XX——分分:秒秒，或者XX:XX:XX——时时:分分:秒秒
     * @param timeInMillis 毫秒
     */
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

    /**
     * 更新播放进度
     * @param position 当前播放到的位置，单位为毫秒
     * @param progress 当前播放到的位置相应地进度，[0, 100]
     */
    protected abstract void updatePlayProgress(VideoPanelView parent, VIPVideo video, int position, int progress) ;
    // end

    // 隐藏控制组件 start
    private Runnable mHideControlBarRun = new Runnable() {
        @Override
        public void run() {
            if (mUserSeeking) {
                //delayHideSeekControlBar();
            } else {
                hideControlBar(self(), getVideo());
            }
        }
    };
    private void delayHideSeekControlBar() {
        cancelHideSeekControlBar();
        getHandler().postDelayed(mHideControlBarRun, 3000L);
    }

    private void cancelHideSeekControlBar() {
        getHandler().removeCallbacks(mHideControlBarRun);
    }

    /**
     * 控制组件是否正处于显示状态
     */
    protected abstract boolean isControlBarShowing(VideoPanelView parent, VIPVideo video) ;

    /**
     * 如果有可拉动的控制组件，调用该方法来显示控制组件
     */
    protected abstract void showControlBar(VideoPanelView parent, VIPVideo video) ;

    /**
     * 如果有可拉动的控制组件，将延迟一定的时间调用该方法，来隐藏控制组件
     */
    protected abstract void hideControlBar(VideoPanelView parent, VIPVideo video) ;
    // end

}
