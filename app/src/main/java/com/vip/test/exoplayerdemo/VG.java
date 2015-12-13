package com.vip.test.exoplayerdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * something
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/10.
 *
 * @since 1.0
 */
public class VG extends ViewGroup {
    public VG(Context context) {
        super(context);
    }

    public VG(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VG(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(getPaddingLeft(), getPaddingTop(),
                    getPaddingLeft() + view.getMeasuredWidth() / 2,
                    getPaddingTop() + view.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }
}
