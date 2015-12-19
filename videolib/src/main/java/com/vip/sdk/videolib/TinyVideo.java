package com.vip.sdk.videolib;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.VideoView;

import java.lang.reflect.Field;

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

    protected Field mPrivateFlags;
    protected int PFLAG_SKIP_DRAW;

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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        // this is important
        // setZOrderOnTop(true);
        // setZOrderMediaOverlay(true);
        // 这个可以让UI hierarchy截不了屏幕
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setSecure(true);
        }
        try {
            mPrivateFlags = View.class.getDeclaredField("mPrivateFlags");
            mPrivateFlags.setAccessible(true);
        } catch (Exception e) { }
        try {
            Field PFLAG_SKIP_DRAW_F = View.class.getDeclaredField("PFLAG_SKIP_DRAW");
            PFLAG_SKIP_DRAW_F.setAccessible(true);
            PFLAG_SKIP_DRAW = PFLAG_SKIP_DRAW_F.getInt(null);
        } catch (Exception e) { }
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

//    @Override
//    public void draw(Canvas canvas) {
//        int oldType = reflectGetPrivateFlags();
//        reflectSetPrivateFlags(oldType | PFLAG_SKIP_DRAW);
//        super.draw(canvas);
//        reflectSetPrivateFlags(oldType);
//    }
//
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        int oldType = reflectGetPrivateFlags();
//        reflectSetPrivateFlags(oldType & (~PFLAG_SKIP_DRAW));
//        super.dispatchDraw(canvas);
//        reflectSetPrivateFlags(oldType);
//    }
//
//    protected int reflectGetPrivateFlags() {
//        if (null == mPrivateFlags) {
//            return 0;
//        }
//        try {
//            return (Integer) mPrivateFlags.get(this);
//        } catch (Exception e) { }
//        return 0;
//    }
//
//    protected void reflectSetPrivateFlags(int value) {
//        if (null == mPrivateFlags) {
//            return;
//        }
//        try {
//            mPrivateFlags.set(this, value);
//        } catch (Exception e) { }
//    }

}
