package com.vip.sdk.videolib;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * 继承自{@link android.widget.VideoView}，以防以后有定制修改的地方
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/11.
 *
 * @since 1.0
 */
/*package*/ class TinyVideoImpl extends VideoView {

    public TinyVideoImpl(Context context) {
        this(context, null);
    }

    public TinyVideoImpl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TinyVideoImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    // 初始化自定义的属性
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        // this is important
        // setZOrderOnTop(true);
        // setZOrderMediaOverlay(true);
        // 这个可以让UI hierarchy截不了屏幕
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setSecure(true);
        }
    }

}
