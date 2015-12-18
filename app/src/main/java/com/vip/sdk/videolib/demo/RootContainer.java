package com.vip.sdk.videolib.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * something
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/18.
 *
 * @since 1.0
 */
public class RootContainer extends FrameLayout {

    private WindowManager mWindowManager;
    private View mWallpaper;
    public RootContainer(Context context) {
        super(context);
        initView();
    }

    public RootContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RootContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (null == mWallpaper) {
            mWallpaper = new View(getContext());
            mWallpaper.setBackgroundColor(0xffffffff);
        }
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                // Media window sits behind the main application window
                WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA,
                // Avoid default to software format RGBA
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                android.graphics.PixelFormat.TRANSLUCENT);
        params.token = getWindowToken();
        mWindowManager.addView(mWallpaper, params);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mWallpaper) {
            mWindowManager.removeView(mWallpaper);
        }
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
    }
}
