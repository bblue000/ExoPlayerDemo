package com.vip.sdk.uilib.media.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.vip.sdk.videolib.R;

import java.util.Map;

/**
 * 包装一些逻辑。
 *
 * <br/>
 *
 * 用一层ViewGroup包装并提供给外部使用，有较好的兼容性；
 * 使用具体的控件（如{@link android.widget.VideoView}或者{@link android.view.SurfaceView}），不利于以后的替换、修改。
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/25.
 *
 * @since 1.0
 */
public class VIPVideo extends RelativeLayout implements VideoPlayer {

    private static final String TAG = VIPVideo.class.getSimpleName();
    private static final boolean DEBUG = VIPVideoDebug.VIEW;

    // all possible internal states
    private static final int STATE_ERROR              = -1;
    private static final int STATE_IDLE               = 0;
    private static final int STATE_PREPARING          = 1;
    private static final int STATE_PREPARED           = 2;
    private static final int STATE_PLAYING            = 3;
    private static final int STATE_PAUSED             = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int mCurrentState = STATE_IDLE;
    private int mTargetState  = STATE_IDLE;

    private VideoView mVideoView;
    private LayoutParams mVideoViewLP;
    {
        mVideoViewLP = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mVideoViewLP.addRule(CENTER_IN_PARENT);
    }

    private int mSeekWhenPrepared;  // recording the seek position while preparing

    private float mSizeRatio = -1.0f;

    // 防止代码创建对象，并设置Uri的情况
    public VIPVideo(Context context) {
        this(context, null);
    }

    public VIPVideo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VIPVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTinyVideoContainer(context, attrs, defStyleAttr);
    }

    // 处理一些初始化的属性
    private void initTinyVideoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VIPVideo, defStyleAttr, 0);
        mSizeRatio = a.getFloat(R.styleable.VIPVideo_sizeRatio, -1.0f);
        a.recycle();
    }

    // 负责创建VideoView
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void checkInitVideoView() {
        if (null == mVideoView) {
            mVideoView = new VideoView(getContext());
            // 这个可以让UI hierarchy截不了屏幕
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mVideoView.setSecure(true);
            }
            mVideoView.getHolder().addCallback(mSurfaceCallback);
            mVideoView.setBackgroundColor(0); // this is important
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        removeAllViews(); // 我们不允许有其他的子View存在
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSizeRatio > 0f) sizeRatioMeasuring : {
            // 如果设置了宽高比
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);

            boolean exactWidth = widthMode == MeasureSpec.EXACTLY;
            boolean exactHeight = heightMode == MeasureSpec.EXACTLY;

            if (!(exactWidth ^ exactHeight)) {
                break sizeRatioMeasuring;
            }
            int widthSize;
            int heightSize;
            if (exactWidth) {
                widthSize = MeasureSpec.getSize(widthMeasureSpec);
                heightSize = (int) (widthSize / mSizeRatio + 0.5f);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
            } else {
                heightSize = MeasureSpec.getSize(heightMeasureSpec);
                widthSize = (int) (heightSize * mSizeRatio);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, heightMode);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public String toString() {
        return TAG + "@" + Integer.toHexString(hashCode());
    }

    /**
     * 设置宽高比
     * @param ratio 宽高比例（宽:高）
     */
    public void setSizeRatio(float ratio) {
        if (ratio <= 0 || mSizeRatio == ratio) {
            return;
        }
        mSizeRatio = ratio;
        // 重新测量
        requestLayout();
    }

    /**
     * Interface definition for a callback to be invoked when the media
     * source is ready for playback.
     */
    public interface OnPreparedListener {
        /**
         * Called when the media file is ready for playback.
         *
         * @param video the VIPVideo that is ready for playback
         * @param mp the MediaPlayer that is ready for playback
         */
        void onPrepared(VIPVideo video, MediaPlayer mp);
    }
    private OnPreparedListener mOnPreparedListener;
    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Interface definition for a callback to be invoked when playback of
     * a media source has completed.
     */
    public interface OnCompletionListener {
        /**
         * Called when the end of a media source is reached during playback.
         *
         * @param video the VIPVideo that is ready for playback
         * @param mp the MediaPlayer that reached the end of the file
         */
        void onCompletion(VIPVideo video, MediaPlayer mp);
    }
    private OnCompletionListener mOnCompletionListener;
    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Interface definition of a callback to be invoked when there
     * has been an error during an asynchronous operation (other errors
     * will throw exceptions at method call time).
     */
    public interface OnErrorListener {
        /**
         * Called to indicate an error.
         *
         * @param mp      the MediaPlayer the error pertains to
         * @param what    the type of error that has occurred:
         * <ul>
         * <li>{@link android.media.MediaPlayer#MEDIA_ERROR_UNKNOWN}
         * <li>{@link android.media.MediaPlayer#MEDIA_ERROR_SERVER_DIED}
         * </ul>
         * @param extra an extra code, specific to the error. Typically
         * implementation dependent.
         * <ul>
         * <li>{@link android.media.MediaPlayer#MEDIA_ERROR_IO}
         * <li>{@link android.media.MediaPlayer#MEDIA_ERROR_MALFORMED}
         * <li>{@link android.media.MediaPlayer#MEDIA_ERROR_UNSUPPORTED}
         * <li>{@link android.media.MediaPlayer#MEDIA_ERROR_TIMED_OUT}
         * </ul>
         * @return True if the method handled the error, false if it didn't.
         * Returning false, or not having an OnErrorListener at all, will
         * cause the OnCompletionListener to be called.
         */
        boolean onError(VIPVideo video, MediaPlayer mp, int what, int extra);
    }
    private OnErrorListener mOnErrorListener;
    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    // 当设置资源的时候再添加该组件
    private void addTinyVideo() {
        removeVideoView();

        checkInitVideoView();
        ViewGroup.LayoutParams parentLp = getLayoutParams();
        if (null != parentLp) { // 如果父容器的宽高不是绝对值，从父容器中赋值宽高
            if (parentLp.width < 0) {
                mVideoViewLP.width = parentLp.width;
            }
            if (parentLp.height < 0) {
                mVideoViewLP.height = parentLp.height;
            }
        }
        // config
        mVideoView.setOnPreparedListener(mPreparedListener);
        mVideoView.setOnErrorListener(mErrorListener);
        mVideoView.setOnCompletionListener(mCompletionListener);
        addView(mVideoView, mVideoViewLP);
    }

    private void removeVideoView() {
        final int childCount = getChildCount();
        if (null != mVideoView) {
            mVideoView.setOnPreparedListener(null);
            mVideoView.setOnErrorListener(null);
            mVideoView.setOnCompletionListener(null);
        }
        if (childCount > 0) {
            removeAllViews();
        }
    }

    @Override
    public void setVideoPath(String url) {
        setVideoURI(Uri.parse(url));
    }

    @Override
    public void setVideoURI(Uri uri) {
        setVideoURICompat(uri, null);
    }

    @Override
    public void setVideoURICompat(Uri uri, Map<String, String> headers) {
        // send message
        mSeekWhenPrepared = 0;
        if (!isVideoAdded()) {
            addTinyVideo();
        }
        mCurrentState = STATE_PREPARING;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mVideoView.setVideoURI(uri, headers);
        } else {
            if (DEBUG) Log.d(TAG, "headers are ignoreds");
            mVideoView.setVideoURI(uri);
        }
    }

    /**
     * VideoView是否已添加
     */
    protected boolean isVideoAdded() {
        return getChildCount() > 0 && null != mVideoView;
    }

    /**
     * 判断是否在可以播放的状态
     */
    /*package*/ boolean isInPlaybackState() {
        return (isVideoAdded() &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mVideoView.isPlaying();
    }

    @Override
    public void start() {
        if (!isInPlaybackState()) {
            mVideoView.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mVideoView.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    @Override
    public void stop() {
        if (isVideoAdded()) {
            mVideoView.stopPlayback();
            removeVideoView();
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    @Override
    public int getDuration() {
        return isInPlaybackState() ? Math.max(0, mVideoView.getDuration()) : 0;
    }

    @Override
    public int getCurrentPosition() {
        return isInPlaybackState() ? Math.max(0, mVideoView.getCurrentPosition()) : 0;
    }

    @Override
    public int getBufferPercentage() {
        return isInPlaybackState() ? Math.max(0, mVideoView.getBufferPercentage()) : 0;
    }

    // ==================================================
    // ==================================================
    // ==================================================
    // for inner APIs
    private OnPreparedListener mInnerAPIOnPreparedListener;
    private OnCompletionListener mInnerAPIOnCompletionListener;
    private OnErrorListener mInnerAPIOnErrorListener;
    protected void setInnerAPIOnPreparedListener(OnPreparedListener l) {
        mInnerAPIOnPreparedListener = l;
    }
    protected void setInnerAPIOnCompletionListener(OnCompletionListener l) {
        mInnerAPIOnCompletionListener = l;
    }
    protected void setInnerAPIOnErrorListener(OnErrorListener l) {
        mInnerAPIOnErrorListener = l;
    }

    private VIPVideoToken mToken;
    protected void setToken(VIPVideoToken token) {
        mToken = token;
    }
    protected VIPVideoToken getToken() {
        return mToken;
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (DEBUG) Log.d("yytest", this + " prepared");

            mCurrentState = STATE_PREPARED;

            if (null != mInnerAPIOnPreparedListener) {
                mInnerAPIOnPreparedListener.onPrepared(VIPVideo.this, mp);
            }
            if (null != mOnPreparedListener) {
                mOnPreparedListener.onPrepared(VIPVideo.this, mp);
            }

            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            // We don't know the video size yet, but should start anyway.
            // The video size might be reported to us later.
            if (mTargetState == STATE_PLAYING) {
                start();
            } else if (!isPlaying() &&
                    (seekToPosition != 0 || getCurrentPosition() > 0)) {
                // do sth.
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (null != mInnerAPIOnCompletionListener) {
                mInnerAPIOnCompletionListener.onCompletion(VIPVideo.this, mp);
            }
            if (null != mOnCompletionListener) {
                mOnCompletionListener.onCompletion(VIPVideo.this, mp);
            }
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            /* If an error handler has been supplied, use it and finish. */
            if (DEBUG) Log.d(TAG, "Error: " + what + "," + extra);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            boolean ret = false;
            if (null != mInnerAPIOnErrorListener) {
                ret = mInnerAPIOnErrorListener.onError(VIPVideo.this, mp, what, extra);
            }
            if (null != mOnErrorListener) {
                ret = mOnErrorListener.onError(VIPVideo.this, mp, what, extra) || ret;
            }
            return ret;
        }
    };

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (DEBUG) Log.d(TAG, "surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (DEBUG) Log.d(TAG, "surfaceChanged");
            boolean isValidState =  (mTargetState == STATE_PLAYING);
            if (isVideoAdded() && isValidState) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (DEBUG) Log.d(TAG, "surfaceDestroyed");
        }
    };

}
