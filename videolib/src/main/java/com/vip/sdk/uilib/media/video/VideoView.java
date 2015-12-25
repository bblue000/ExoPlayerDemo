package com.vip.sdk.uilib.media.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

/**
 * 继承自{@link android.widget.VideoView}，不公开，以便以后有定制修改
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/11.
 *
 * @since 1.0
 */
/*package*/ class VideoView extends android.widget.VideoView {

    public VideoView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    // 初始化自定义的属性
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        // 这个可以让UI hierarchy截不了屏幕
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setSecure(true);
        }
    }

}
