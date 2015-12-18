package com.vip.sdk.videolib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

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
public class TinyVideoContainer extends RelativeLayout {

    private TinyVideo mVideoView;
    public TinyVideoContainer(Context context) {
        super(context);
        initTinyVideoContainer();
    }

    public TinyVideoContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTinyVideoContainer();
    }

    public TinyVideoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTinyVideoContainer();
    }

    private void initTinyVideoContainer() {

    }

    protected void addTinyVideo() {
        if (null != mVideoView) {
            removeView(mVideoView);
        }


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
}
