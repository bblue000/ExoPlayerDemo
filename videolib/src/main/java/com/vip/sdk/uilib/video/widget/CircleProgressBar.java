package com.vip.sdk.uilib.video.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

import com.vip.sdk.uilib.video.R;

/**
 *
 * 仿微信视频加载进度控件
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 16/1/4.
 *
 * @since 1.0
 */
public class CircleProgressBar extends View {

    private int mNormalRadius;
    private int mNormalColor;
    private int mProgressRadius;
    private int mProgressColor;

    private int mBorderWidth;
    private int mBorderColor;
    private int mBorderOffset;

    private int mProgress;
    private int mTargetProgress;
    private int mMaxProgress = 100;

    private Paint mPaint;
    private RectF mRectF;

    private Scroller mScroller;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        setWillNotDraw(false);
        mScroller = new Scroller(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr,
                R.style.CircleProgressBar);

        // radius
        int commonRadius, normalRadius, progressRadius;
        commonRadius = a.getDimensionPixelSize(R.styleable.CircleProgressBar_commonCircleRadius, 0);
        normalRadius = a.getDimensionPixelSize(R.styleable.CircleProgressBar_normalCircleRadius, 0);
        progressRadius = a.getDimensionPixelSize(R.styleable.CircleProgressBar_progressCircleRadius, 0);
        if (commonRadius > 0 && normalRadius <= 0) {
            normalRadius = commonRadius;
        }
        if (commonRadius > 0 && progressRadius <= 0) {
            progressRadius = commonRadius;
        }
        setNormalRadius(normalRadius);
        setProgressRadius(progressRadius);

        // color
        int normalColor = a.getColor(R.styleable.CircleProgressBar_normalSwingColor, 0xFFaaaaa);
        int progressColor = a.getColor(R.styleable.CircleProgressBar_progressSwingColor, 0xFF);
        setNormalSwingColor(normalColor);
        setProgressSwingColor(progressColor);

        // border
        int borderWidth = a.getDimensionPixelSize(R.styleable.CircleProgressBar_borderStrokeWidth, -1);
        int borderColor = a.getColor(R.styleable.CircleProgressBar_borderStrokeColor, 0);
        int borderOffset = a.getDimensionPixelSize(R.styleable.CircleProgressBar_borderOffset, 0);
        setBorderStrokeWidth(borderWidth);
        setBorderStrokeColor(borderColor);
        setBorderOffset(borderOffset);

        // 进度
        int max = a.getInteger(R.styleable.CircleProgressBar_android_max, 100);
        int progress = a.getInteger(R.styleable.CircleProgressBar_android_progress, 0);
        setMaxProgress(max);
        setProgress(progress);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingHor = getPaddingLeft() + getPaddingRight();
        int paddingVer = getPaddingTop() + getPaddingBottom();

        int contentSize = Math.max(mNormalRadius, mProgressRadius) * 2 /*直径*/
                        + mBorderOffset * 2 /*border offset*/
                        + mBorderWidth * 2 /*border width*/
                ;
        int targetWidth = paddingHor + contentSize;
        int targetHeight = paddingVer + contentSize;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            setMeasuredDimension(resolveSize(targetWidth, widthMeasureSpec),
                    resolveSize(targetHeight, heightMeasureSpec));
        } else {
            setMeasuredDimension(resolveSizeAndState(targetWidth, widthMeasureSpec, 0),
                    resolveSizeAndState(targetHeight, heightMeasureSpec, 0));
        }
    }

    /**
     * 同时设置未加载、已加载进度的显示半径
     */
    public void setCommonRadius(int radius) {
        setNormalRadius(radius);
        setProgressRadius(radius);
    }

    /**
     * 单独设置未加载进度的显示半径
     */
    public void setNormalRadius(int radius) {
        mNormalRadius = Math.max(0, radius);
        requestLayout();
        invalidate();
    }

    /**
     * 单独设置已加载进度的显示半径
     */
    public void setProgressRadius(int radius) {
        mProgressRadius = Math.max(0, radius);
        requestLayout();
        invalidate();
    }

    /**
     * 未加载进度部分弧形的颜色
     */
    public void setNormalSwingColor(int color) {
        mNormalColor = color;
        invalidate();
    }

    /**
     * 已加载进度部分弧形的颜色
     */
    public void setProgressSwingColor(int color) {
        mProgressColor = color;
        invalidate();
    }

    /**
     * 外边缘圆圈的粗细
     */
    public void setBorderStrokeWidth(int width) {
        mBorderWidth = Math.max(0, width);
        requestLayout();
        invalidate();
    }

    /**
     * 外边缘圆圈的颜色
     */
    public void setBorderStrokeColor(int color) {
        mBorderColor = color;
        invalidate();
    }

    /**
     * 外边缘圆圈与内部圆圈之间的距离
     */
    public void setBorderOffset(int offset) {
        mBorderOffset = Math.max(0, offset);
        requestLayout();
        invalidate();
    }

    /**
     * 设置最大进度，默认为100
     */
    public void setMaxProgress(int max) {
        if (max > 0) {
            mMaxProgress = max;
            invalidate();
        }
    }

    /**
     * 设置当前进度
     */
    public void setProgress(int progress) {
        progress = Math.max(0, progress);
        progress = Math.min(progress, mMaxProgress);
        if (mProgress != progress) {
            mTargetProgress = mProgress = progress;
            invalidate();
        }
    }

    /**
     * Smoothly set progress
     */
    public void smoothSetProgress(int progress) {
        progress = Math.max(0, progress);
        progress = Math.min(progress, mMaxProgress);
        mScroller.abortAnimation();
        if (progress != mProgress) {
            mTargetProgress = progress;
//            float percent = (float) Math.abs(mTargetProgress - mProgress) / (float) mMaxProgress;
//            float percentAngle = percent * 360;
//            int timeInMillis = (int) (percentAngle * 10);
            int timeInMillis = 300; // 使用常量
            mScroller.startScroll(mProgress, 0, mTargetProgress - mProgress, 0, (int) timeInMillis);
            invalidate();
        } else {
            setProgress(progress);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mProgress = mScroller.getCurrX();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mPaint) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mRectF = new RectF();
        }

        // clip padding
        int clipLeft = getPaddingLeft();
        int clipTop = getPaddingTop();
        int clipRight = Math.max(clipLeft, getWidth() - getPaddingRight());
        int clipBottom = Math.max(clipTop, getHeight() - getPaddingBottom());

        int clipWidth = clipRight - clipLeft;
        int clipHeight = clipBottom - clipTop;
        if (clipWidth <= 0 || clipHeight <= 0) return; // 不需要绘制

        canvas.save();

        canvas.translate(clipLeft, clipTop); // 先移到“起始位置”
        canvas.clipRect(0, 0, clipWidth, clipHeight);

        canvas.translate(clipWidth / 2, clipHeight / 2); // 再移动到中心位置

        float percent = (float) mProgress / (float) mMaxProgress;
        float percentAngle = percent * 360;
        mPaint.setColor(mNormalColor);
        mPaint.setStyle(Paint.Style.FILL);
        mRectF.set(-mNormalRadius, -mNormalRadius, mNormalRadius, mNormalRadius);
        canvas.drawArc(mRectF, -90 + percentAngle, 360 - percentAngle, true, mPaint);

        mPaint.setColor(mProgressColor);
        mPaint.setStyle(Paint.Style.FILL);
        mRectF.set(-mProgressRadius, -mProgressRadius, mProgressRadius, mProgressRadius);
        canvas.drawArc(mRectF, -90, percentAngle, true, mPaint);

        if (mBorderOffset > 0) {
            float borderRadius = Math.max(mNormalRadius, mProgressRadius) + mBorderOffset + ((float) mBorderWidth) / 2;
            mPaint.setColor(mBorderColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mBorderWidth);
            canvas.drawCircle(0, 0, borderRadius, mPaint);
        }

        canvas.restore();
    }
}
