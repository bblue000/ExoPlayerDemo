package com.vip.sdk.videolib;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.VideoView;

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
     * 播放状态的回调
     */
    public interface StateCallback {
        /**
         * 资源已加载（全部或部分），已经可以播放
         */
        void onPrepared();

        /**
         * 开始播放（从停止或者暂停状态变为播放状态）
         */
        void onStart();

        /**
         * 由播放状态变为暂停状态
         */
        void onPaused();

        /**
         * 进入停止状态（已播放完成）
         */
        void onStop();
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;

    protected boolean mAutoPlay = false;
    protected float mSizeRatio = -1.0f;
    protected TinyMediaOverlay mTinyMediaOverlay;
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
        mAutoPlay = a.getBoolean(R.styleable.TinyVideo_autoPlay, false);
        a.recycle();

        setOnPreparedListener(mPreparedListener);
        setOnErrorListener(mErrorListener);
        setOnCompletionListener(mCompletionListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();


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

    /**
     * 设置是否自动播放
     */
    public void setAutoPlay(boolean autoPlay) {
        mAutoPlay = autoPlay;
    }

    /**
     * 是否自动播放
     */
    public boolean isAutoPlay() {
        return mAutoPlay;
    }

    /**
     * 设置浮层
     */
    public void setTinyMediaOverlay(TinyMediaOverlay overlay) {
        if (null != mTinyMediaOverlay) {
            mTinyMediaOverlay.unbind(this);
        }
        mTinyMediaOverlay = overlay;
        attachTinyMediaOverlay();
    }

    protected void attachTinyMediaOverlay() {
        if (null != mTinyMediaOverlay) {
            mTinyMediaOverlay.bind(this);
        }
    }

    /**
     * 获取浮层
     */
    public TinyMediaOverlay getTinyMediaOverlay() {
        return mTinyMediaOverlay;
    }


    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (null != mTinyMediaOverlay) {
                mTinyMediaOverlay.onPrepared();
            }

            if (null != mOnPreparedListener) {
                mOnPreparedListener.onPrepared(mp);
            }
        }
    };
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (null != mTinyMediaOverlay) {
                mTinyMediaOverlay.onCompletion();
            }
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
