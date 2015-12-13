package com.vip.sdk.videolib;

import android.content.Context;
import android.view.WindowManager;

/**
 * Video的浮层
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/11.
 *
 * @since 1.0
 */
public abstract class TinyMediaOverlay {

    protected WindowManager mWindowManager;
    protected TinyVideo mPlayer;
    public TinyMediaOverlay() {

    }

    /**
     * 绑定指定的video
     */
    public void bind(TinyVideo video) {
        mPlayer = video;
        mWindowManager = (WindowManager) video.getContext().getSystemService(Context.WINDOW_SERVICE);
        initOverlay();
    }

    /**
     * 取消绑定指定的video
     */
    public void unbind(TinyVideo video) {
        mPlayer = null;
        mWindowManager = null;
        destroyOverlay();
    }

    /**
     * 获取{@link WindowManager}
     */
    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    /**
     * 获取播放器对象
     */
    public TinyVideo getPlayer() {
        return mPlayer;
    }

    /**
     * 初始化浮层对象
     */
    protected abstract void initOverlay() ;

    /**
     * 销毁浮层对象
     */
    protected abstract void destroyOverlay();

    /**
     * 正在加载
     */
    protected abstract void onLoading();

    /**
     * 视频资源加载好（或者加载了一部分），准备好播放时触发
     */
    protected abstract void onPrepared();

    /**
     * 视频开始/重新开始播放时触发
     */
    protected abstract void onStart();

    /**
     * 视频暂停播放时触发
     */
    protected abstract void onPause();

    /**
     * 视频播放完成时触发
     */
    protected abstract void onCompletion();

}
