package com.vip.sdk.videolib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.VideoView;

import java.util.Map;

/**
 * 封装的小组件
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/11.
 *
 * @since 1.0
 */
public class TinyVideo extends VideoView {

    /**
     * 播放状态的回调。
     *
     * <br/>
     *
     * 如果已经开始播放，则在设置时将会调用{@link #STATE_START}。
     */
    public interface StateCallback {

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

    protected float mSizeRatio = -1.0f;
    protected TinyController mTinyController;
    protected StateCallback mStateCallback;
    protected Uri mUri;
    protected Map<String, String> mHeaders;

    protected boolean mAttachedToWindow;
    public TinyVideo(Context context) {
        this(context, null);
    }

    public TinyVideo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TinyVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    // 初始化自定义的属性
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TinyVideo, defStyleAttr, 0);
        mSizeRatio = a.getFloat(R.styleable.TinyVideo_sizeRatio, -1.0f);
        a.recycle();

        superSetOnPreparedListener(mPreparedListener);
        superSetOnErrorListener(mErrorListener);
        superSetOnCompletionListener(mCompletionListener);
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

    /*package*/ void superSetOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        super.setOnPreparedListener(l);
    }

    /*package*/ void superSetOnErrorListener(MediaPlayer.OnErrorListener l) {
        super.setOnErrorListener(l);
    }

    /*package*/ void superSetOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        super.setOnCompletionListener(l);
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

    public Uri getVideoUri() {
        return mUri;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superSetVideoURI(Uri uri) {
        if (null == mTinyController) {
            throw new UnsupportedOperationException("non TinyController supplied");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setVideoURI(uri, null);
        } else {
            super.setVideoURI(uri);
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

    /**
     * do nothing
     */
    @Override
    public void start() {

    }

    public void superStart() {
        super.start();
    }

    @Override
    public void pause() {
        boolean isPlaying = isPlaying();
        super.pause();
        if (isPlaying ^ isPlaying()) {
            checkAndSend(MSG_PAUSE, null);
        }
    }

    @Override
    public void suspend() {
        super.suspend();
        checkAndSend(MSG_STOP, null);
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();
        checkAndSend(MSG_STOP, null);
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
        }
        mHandler.sendEmptyMessage(msg);
    }

    /*package*/ void dispatchError(LoadErrInfo info) {
        checkAndSend(MSG_LOADEER, info);
    }

    private static final int MSG_PREPARED = 1;
    private static final int MSG_START = 2;
    private static final int MSG_PAUSE = 3;
    private static final int MSG_STOP = 4;
    private static final int MSG_LOADEER = 5;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
            }
        }
    };

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
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


    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    /**
     * User interface state that is stored by TextView for implementing
     * {@link View#onSaveInstanceState}.
     */
    public static class SavedState extends BaseSavedState {

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcel in) {
            super(in);
        }
    }
}
