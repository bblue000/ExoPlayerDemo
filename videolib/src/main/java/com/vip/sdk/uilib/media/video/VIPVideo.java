package com.vip.sdk.uilib.media.video;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.vip.sdk.base.utils.AndroidUtils;
import com.vip.sdk.videolib.R;

import java.util.Map;

/**
 * 包装一些逻辑。
 *
 * <br/>
 *
 * 用一层ViewGroup包装并提供给外部使用，有较好的兼容性；
 * 使用具体的控件（如{@link android.widget.VideoView}或者{@link android.view.SurfaceView}），不利于以后的替换、修改
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
    private StateCallback mStateCallback;

    // 防止代码创建对象，并设置Uri的情况
    private Uri mUri;
    private Map<String, String> mHeaders;

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

    private void initTinyVideoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VIPVideo, defStyleAttr, 0);
        mSizeRatio = a.getFloat(R.styleable.VIPVideo_sizeRatio, -1.0f);
        a.recycle();
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

//    private MediaPlayer.OnCompletionListener mOnCompletionListener;
//    private MediaPlayer.OnPreparedListener mOnPreparedListener;
//    private MediaPlayer.OnErrorListener mOnErrorListener;
//    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
//        mOnPreparedListener = l;
//    }
//
//    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
//        mOnErrorListener = l;
//    }
//
//    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
//        mOnCompletionListener = l;
//    }

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

    // 当设置资源的时候再添加该组件
    protected void addTinyVideo() {
        removeVideoView();

        if (null == mVideoView) {
            mVideoView = new VideoView(getContext());
            mVideoView.getHolder().addCallback(mSurfaceCallback);
            mVideoView.setBackgroundColor(0); // this is important
        }
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

    protected void removeVideoView() {
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

    /**
     * VideoView是否已添加
     */
    protected boolean isVideoAdded() {
        return getChildCount() > 0 && null != mVideoView;
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
        mUri = uri;
        mHeaders = headers;
        // send message
        if (!isVideoAdded()) {
            addTinyVideo();
        }
        mCurrentState = STATE_PREPARING;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mVideoView.setVideoURI(uri, null);
        } else {
            mVideoView.setVideoURI(uri);
        }
    }

    private boolean isInPlaybackState() {
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
            removeVideoView();
        }
    }

    @Override
    public int getDuration() {
        return isVideoAdded() ? Math.max(0, mVideoView.getDuration()) : 0;
    }

    @Override
    public int getCurrentPosition() {
        return isVideoAdded() ? Math.max(0, mVideoView.getCurrentPosition()) : 0;
    }

    protected void dispatchLoading() {
        postDo(StateCallback.STATE_LOADING, null);
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (DEBUG) Log.d("yytest", mUri + " prepared");
            if (null != mTinyController) {
                mTinyController.dispatchFromVideoPrepared(myInfo());
            }
            checkAndSend(STATEPREPARED, null);
            if (null != mOnPreparedListener) {
                mOnPreparedListener.onPrepared(mp);
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            postDo(StateCallback.STATE_COMPLETION, null);
            if (null != mOnCompletionListener) {
                mOnCompletionListener.onCompletion(mp);
            }
            postDo(StateCallback.STATE_STOP, null);
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            /* If an error handler has been supplied, use it and finish. */
//            if (null != mOnErrorListener) {
//                if (mOnErrorListener.onError(mp, what, extra)) {
//                    return true;
//                }
//            }
            return false;
        }
    };

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (DEBUG) Log.d("yytest", "surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (DEBUG) Log.d("yytest", "surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (DEBUG) Log.d("yytest", "surfaceDestroyed");
        }
    };

    protected void postDo(int what, Object param) {
        Message msg = Message.obtain(mHandler, what, param);
        if (AndroidUtils.isMainThread()) {
            mHandler.handleMessage(msg);
            msg.recycle();
        } else {
            msg.sendToTarget();
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StateCallback.STATE_LOADING:
                    break;
                case StateCallback.STATE_LOAD_ERR:
                    break;
                case StateCallback.STATE_PREPARED:
                    break;
                case StateCallback.STATE_START:
                    break;
                case StateCallback.STATE_PAUSE:
                    break;
                case StateCallback.STATE_COMPLETION:
                    break;
                case StateCallback.STATE_STOP:
                    break;
                case StateCallback.STATE_ERR:
                    if (DEBUG) Log.e(TAG, "err");
                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;
                    break;
            }
            mStateCallback.onStateChanged(VIPVideo.this, msg.what, (VideoState) msg.obj);
        }
    };
}
