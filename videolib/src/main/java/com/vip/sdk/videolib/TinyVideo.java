package com.vip.sdk.videolib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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

import java.util.Map;

/**
 *
 * 包装一些逻辑。
 *
 * <br/>
 *
 * 用一层ViewGroup包装并提供给外部使用，有较好的兼容性；
 * 使用具体的控件（如{@link android.widget.VideoView}或者{@link android.view.SurfaceView}），不利于以后的替换、修改
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/18.
 *
 * @since 1.0
 */
public class TinyVideo extends RelativeLayout implements VideoViewDelegate {

    private static final boolean DEBUG = TinyDebug.CONTROLLER;

    /**
     * 播放状态的回调。
     *
     * <br/>
     *
     * 如果已经开始播放，则在设置时将会调用{@link #STATE_START}。
     */
    public interface StateCallback {

        /**
         * 加载资源（全部或部分），此时还不能播放
         */
        int STATE_LOADING = 0;

        /**
         * 资源已加载（全部或部分），已经可以播放。
         */
        int STATE_PREPARED = STATE_LOADING + 1;

        /**
         * 开始播放（一种是从停止或者暂停状态变为播放状态）。
         */
        int STATE_START = STATE_PREPARED + 1;

        /**
         * 由播放状态变为暂停状态
         */
        int STATE_PAUSE = STATE_START + 1;

        /**
         * 进入停止状态（已播放完成）
         */
        int STATE_STOP = STATE_PAUSE + 1;

        /**
         * 当状态改变时回调
         */
        void onStateChanged(TinyVideo video, int state);

        /**
         * 资源加载失败
         */
        void onLoadErr(TinyVideo video, LoadErrInfo status);
    }

    /**
     * {@link com.vip.sdk.videolib.TinyVideo.StateCallback}的缺省子类，所有方法都是空实现
     */
    public static class SimpleStateCallback implements StateCallback {
        @Override public void onStateChanged(TinyVideo video, int state) { }
        @Override public void onLoadErr(TinyVideo video, LoadErrInfo status) { }
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;

    private TinyVideoImpl mVideoView;
    private LayoutParams mVideoViewLP;

    private float mSizeRatio = -1.0f;
    private StateCallback mStateCallback;

    private boolean mAttachedToWindow;
    private TinyController mTinyController;
    private TinyVideoInfo mTinyVideoInfo;

    // 防止代码创建对象，并设置Uri的情况
    private Uri mUri;
    private Map<String, String> mHeaders;
    public TinyVideo(Context context) {
        this(context, null);
    }

    public TinyVideo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TinyVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTinyVideoContainer(context, attrs, defStyleAttr);
    }

    private void initTinyVideoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TinyVideo, defStyleAttr, 0);
        mSizeRatio = a.getFloat(R.styleable.TinyVideo_sizeRatio, -1.0f);
        a.recycle();
    }

    @Override
    public String toString() {
        return "TinyVideo@" + Integer.toHexString(hashCode());
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
    protected void onAttachedToWindow() {
        mAttachedToWindow = true;
        super.onAttachedToWindow();
        attachTinyController(mTinyController);
    }

    @Override
    protected void onDetachedFromWindow() {
        mAttachedToWindow = false;
        super.onDetachedFromWindow();
        detachTinyController(mTinyController);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    protected void addTinyVideo() {
        removeVideoView();

        if (null == mVideoView) {
            mVideoView = new TinyVideoImpl(getContext());
            mVideoViewLP = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mVideoViewLP.addRule(CENTER_IN_PARENT);
            mVideoView.getHolder().addCallback(mSurfaceCallback);
        }
        ViewGroup.LayoutParams parentLp = getLayoutParams();
        if (null != parentLp) {
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
        mVideoView.setVisibility(GONE);
        addView(mVideoView, mVideoViewLP);
    }

    protected void removeVideoView() {
        final int childCount = getChildCount();
        if (null != mVideoView) {
            // 告诉
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

    /**
     * 设置控制器
     */
    public void setTinyController(TinyController controller) {
        if (mTinyController == controller) { // 如果是同一个对象，不进行处理
            return;
        }
        detachTinyController(mTinyController);
        mTinyController = controller;
        attachTinyController(mTinyController);
    }

    private void attachTinyController(TinyController controller) {
        if (null != controller) {
            if (mAttachedToWindow) {
                mTinyVideoInfo = controller.dispatchAttachVideo(this);
            }
            if (null != mUri) {
                controller.dispatchFromVideoSetUri(myInfo(), mUri, mHeaders);
            }
        }
    }

    private void detachTinyController(TinyController controller) {
        if (null != controller) {
            controller.dispatchDetachVideo(this);
        }
        mTinyVideoInfo = null;
    }

    /**
     * 通过该对象与{@link TinyListController}交互
     */
    public TinyVideoInfo myInfo() {
        return mTinyVideoInfo;
    }

    /**
     * {@inheritDoc}
     *
     * <br/>
     *
     * 将转发给控制器进行处理，此时并未准备播放
     */
    @Override
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    /**
     * {@inheritDoc}
     *
     * <br/>
     *
     * 将转发给控制器进行处理，此时并未准备播放
     */
    @Override
    public void setVideoURI(Uri uri) {
        setVideoURICompat(uri, null);
    }

    /**
     * {@inheritDoc}
     *
     * <br/>
     *
     * 将转发给控制器进行处理，此时并未准备播放
     */
    @Override
    public void setVideoURICompat(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        if (null != mTinyController) {
            mTinyController.dispatchFromVideoSetUri(myInfo(), mUri, mHeaders);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    /*package*/ void innerSetVideoURI(Uri uri) {
        if (null == mTinyController) {
            throw new UnsupportedOperationException("non TinyController supplied");
        }
        if (!isVideoAdded()) {
            addTinyVideo();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mVideoView.setVideoURI(uri, null);
        } else {
            mVideoView.setVideoURI(uri);
        }
        mVideoView.setVisibility(VISIBLE);
        checkAndSend(MSG_SETURI, uri);
    }

    @Override
    public boolean isPlaying() {
        return isVideoAdded() ? mVideoView.isPlaying() : false;
    }

    @Override
    public void start() {
        if (null != mTinyController) {
            mTinyController.dispatchFromVideoStart(myInfo());
        }
    }

    /*package*/ void innerStart() {
        if (!isVideoAdded()) {
            addTinyVideo();
        }
        mVideoView.start();
        checkAndSend(MSG_START, null);
    }

    @Override
    public void pause() {
        if (isVideoAdded()) {
            boolean isPlaying = isPlaying();
            mVideoView.pause();
            if (isPlaying ^ isPlaying()) {
                checkAndSend(MSG_PAUSE, null);
            }
        }
    }

//    @Override
//    public void suspend() {
//        if (isVideoAdded()) {
//            mVideoView.suspend();
//            removeVideoView();
//            checkAndSend(MSG_SUSPEND, null);
//        }
//    }

    @Override
    public void stopPlayback() {
        if (null != mTinyController) {
            mTinyController.dispatchFromVideoStop(myInfo());
        }
    }

    /*package*/ void innerStopPlayback() {
        if (isVideoAdded()) {
            mVideoView.stopPlayback();
            removeVideoView();
        }
        checkAndSend(MSG_STOP, null);
    }

    @Override
    public int getDuration() {
        if (isVideoAdded()) {
            return mVideoView.getDuration();
        }

        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isVideoAdded()) {
            return mVideoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    @Override
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    @Override
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * 设置状态回调
     */
    public void setStateCallback(StateCallback callback) {
        mStateCallback = callback;
        if (isPlaying()) {
            checkAndSend(MSG_START, null);
        }
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
     * 获取宽高比，默认为-1
     */
    public float getSizeRatio() {
        return mSizeRatio;
    }

    private void checkAndSend(int msg, Object param) {
        if (null != param) {
            Message message = Message.obtain(mHandler, msg);
            message.obj = param;
            mHandler.sendMessage(message);
        } else {
            mHandler.sendEmptyMessage(msg);
        }
    }

    protected void dispatchLoading() {
        checkAndSend(MSG_LOADING, null);
    }

    protected void dispatchLoadErr(LoadErrInfo info) {
        checkAndSend(MSG_LOADEER, info);
    }

    private static final int MSG_LOADING = 0;
    private static final int MSG_PREPARED = MSG_LOADING + 1;
    private static final int MSG_START = MSG_PREPARED + 1;
    private static final int MSG_PAUSE = MSG_START + 1;
    private static final int MSG_STOP = MSG_PAUSE + 1;
    private static final int MSG_SUSPEND = MSG_STOP + 1;
    private static final int MSG_LOADEER = MSG_SUSPEND + 1;
    private static final int MSG_SETURI = MSG_LOADEER + 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOADING:
                    if (null != mStateCallback) {
                        mStateCallback.onStateChanged(TinyVideo.this, StateCallback.STATE_LOADING);
                    }
                    break;
                case MSG_PREPARED:
                    if (null != mStateCallback) {
                        mStateCallback.onStateChanged(TinyVideo.this, StateCallback.STATE_PREPARED);
                    }
                    break;
                case MSG_START:
                    if (null != mStateCallback) {
                        mStateCallback.onStateChanged(TinyVideo.this, StateCallback.STATE_START);
                    }
                    break;
                case MSG_PAUSE:
                    if (null != mStateCallback) {
                            mStateCallback.onStateChanged(TinyVideo.this, StateCallback.STATE_PAUSE);
                    }
                    break;
                case MSG_SUSPEND:
                case MSG_STOP:
                    if (null != mStateCallback) {
                        mStateCallback.onStateChanged(TinyVideo.this, StateCallback.STATE_STOP);
                    }
                    break;
                case MSG_LOADEER:
                    if (null != mStateCallback) {
                        mStateCallback.onLoadErr(TinyVideo.this, (LoadErrInfo) msg.obj);
                    }
                    break;
                case MSG_SETURI:
                    break;
            }
        }
    };

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (DEBUG) Log.d("yytest", mUri + " prepared");
            if (null != mTinyController) {
                mTinyController.dispatchFromVideoPrepared(myInfo());
            }
            checkAndSend(MSG_PREPARED, null);
            if (null != mOnPreparedListener) {
                mOnPreparedListener.onPrepared(mp);
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            checkAndSend(MSG_STOP, null);
            if (null != mOnCompletionListener) {
                mOnCompletionListener.onCompletion(mp);
            }
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            /* If an error handler has been supplied, use it and finish. */
            if (null != mOnErrorListener) {
                if (mOnErrorListener.onError(mp, what, extra)) {
                    return true;
                }
            }
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
}

interface VideoViewDelegate {

    void setVideoPath(String url);

    void setVideoURI(Uri uri);

    public void setVideoURICompat(Uri uri, Map<String, String> headers) ;

    boolean isPlaying();

    void start();

    void pause();

//    void suspend();

    void stopPlayback();
    public int getDuration() ;
    public int getCurrentPosition();

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) ;

    public void setOnErrorListener(MediaPlayer.OnErrorListener l) ;

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) ;

}