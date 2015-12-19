package com.vip.sdk.videolib;

import android.annotation.TargetApi;
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
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Map;

/**
 *
 * 包装一些逻辑。
 *
 *
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/18.
 *
 * @since 1.0
 */
public class TinyVideoContainer extends RelativeLayout implements VideoViewDelegate {

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
        int STATE_PREPARED = 1;
        /**
         * 开始播放（从停止或者暂停状态变为播放状态）
         */
        int STATE_START = 2;
        /**
         * 由播放状态变为暂停状态
         */
        int STATE_PAUSE = 3;
        /**
         * 进入停止状态（已播放完成）
         */
        int STATE_STOP = 4;

        void onStateChanged(TinyVideoContainer video, int state);

        /**
         * 资源加载失败
         */
        void onLoadErr(TinyVideoContainer video, LoadErrInfo status);
    }

    /**
     * {@link com.vip.sdk.videolib.TinyVideoContainer.StateCallback}的缺省子类，所有方法都是空实现
     */
    public static class SimpleStateCallback implements StateCallback {
        @Override public void onStateChanged(TinyVideoContainer video, int state) { }
        @Override public void onLoadErr(TinyVideoContainer video, LoadErrInfo status) { }
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;

    protected float mSizeRatio = -1.0f;
    protected TinyController mTinyController;
    protected StateCallback mStateCallback;
    protected Uri mUri;
    protected Map<String, String> mHeaders;

    protected boolean mAttachedToWindow;
    private TinyVideo mVideoView;
    private LayoutParams mVideoViewLP;
    public TinyVideoContainer(Context context) {
        this(context, null);
    }

    public TinyVideoContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TinyVideoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTinyVideoContainer(context, attrs, defStyleAttr);
    }

    private void initTinyVideoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TinyVideo, defStyleAttr, 0);
        mSizeRatio = a.getFloat(R.styleable.TinyVideo_sizeRatio, -1.0f);
        a.recycle();
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

    protected void addTinyVideo() {
        removeVideoView();

        if (null == mVideoView) {
            mVideoView = new TinyVideo(getContext());
            mVideoViewLP = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mVideoViewLP.addRule(CENTER_IN_PARENT);
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
            // 告诉
        }
        if (childCount > 0) {
            removeAllViews();
        }
    }

    /**
     * VideoView是否已添加
     */
    protected boolean isVideoAdded() {
        return getChildCount() >0 && null != mVideoView;
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
    public void setVideoURICompat(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        if (null != mTinyController) {
            mTinyController.dispatchSetUri(this, uri, mHeaders);
        }
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
                controller.dispatchAttachVideo(this);
            }
            if (null != mUri) {
                controller.dispatchSetUri(this, mUri, mHeaders);
            }
        }
    }

    private void detachTinyController(TinyController controller) {
        if (null != controller) {
            controller.dispatchDetachVideo(this);
        }
    }

    @Override
    public boolean isPlaying() {
        return isVideoAdded() ? mVideoView.isPlaying() : false;
    }

    /**
     * 异步进行，即调用此方法后，不能直接根据{@link #isPlaying()}判断是否已经在播放
     */
    @Override
    public void start() {
        checkAndSend(MSG_START, null);
    }

    @Override
    public void pause() {
        checkAndSend(MSG_PAUSE, null);
    }

    @Override
    public void suspend() {
        checkAndSend(MSG_SUSPEND, null);
    }

    /**
     * 异步进行，即调用此方法后，不能直接根据{@link #isPlaying()}判断是否已经在播放
     */
    @Override
    public void stopPlayback() {
        checkAndSend(MSG_STOP, null);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superSetVideoURI(Uri uri) {
        if (null == mTinyController) {
            throw new UnsupportedOperationException("non TinyController supplied");
        }
        checkAndSend(MSG_SETURI, uri);
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
    private static final int MSG_PREPARED = 1;
    private static final int MSG_START = 2;
    private static final int MSG_PAUSE = 3;
    private static final int MSG_STOP = 4;
    private static final int MSG_SUSPEND = 5;
    private static final int MSG_LOADEER = 6;
    private static final int MSG_SETURI = 7;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOADING:
                    if (null != mStateCallback) {
                        mStateCallback.onStateChanged(TinyVideoContainer.this, StateCallback.STATE_LOADING);
                    }
                    break;
                case MSG_PREPARED:
                    if (null != mStateCallback) {
                        mStateCallback.onStateChanged(TinyVideoContainer.this, StateCallback.STATE_PREPARED);
                    }
                    break;
                case MSG_START:
                    if (!isVideoAdded()) {
                        addTinyVideo();
                    }
                    mVideoView.start();
                    if (null != mStateCallback) {
                        mStateCallback.onStateChanged(TinyVideoContainer.this, StateCallback.STATE_START);
                    }
                    break;
                case MSG_PAUSE:
                    if (isVideoAdded()) {
                        boolean isPlaying = isPlaying();
                        mVideoView.pause();
                        if (isPlaying ^ isPlaying()) {
                            if (null != mStateCallback) {
                                mStateCallback.onStateChanged(TinyVideoContainer.this, StateCallback.STATE_PAUSE);
                            }
                        }
                    }
                    break;
                case MSG_SUSPEND:
                    if (isVideoAdded()) {
                        mVideoView.suspend();
                        removeVideoView();
                    }
                    if (null != mStateCallback) {
                        mStateCallback.onStateChanged(TinyVideoContainer.this, StateCallback.STATE_STOP);
                    }
                    break;
                case MSG_STOP:
                    if (isVideoAdded()) {
                        mVideoView.stopPlayback();
                        removeVideoView();
                    }
                    if (null != mStateCallback) {
                        mStateCallback.onStateChanged(TinyVideoContainer.this, StateCallback.STATE_STOP);
                    }
                    break;
                case MSG_LOADEER:
                    if (null != mStateCallback) {
                        mStateCallback.onLoadErr(TinyVideoContainer.this, (LoadErrInfo) msg.obj);
                    }
                    break;
                case MSG_SETURI:
                    if (!isVideoAdded()) {
                        addTinyVideo();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mVideoView.setVideoURI((Uri) msg.obj, null);
                    } else {
                        mVideoView.setVideoURI((Uri) msg.obj);
                    }
                    break;
            }
        }
    };

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (DEBUG) Log.d("yytest", mUri + " prepared");
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
}

interface VideoViewDelegate {

    void setVideoPath(String url);

    void setVideoURI(Uri uri);

    public void setVideoURICompat(Uri uri, Map<String, String> headers) ;

    boolean isPlaying();

    void start();

    void pause();

    void suspend();

    void stopPlayback();

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) ;

    public void setOnErrorListener(MediaPlayer.OnErrorListener l) ;

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) ;

}