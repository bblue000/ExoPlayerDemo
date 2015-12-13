package com.vip.sdk.videolib;

import android.widget.AdapterView;

/**
 *
 * 视频小组件的“上帝类”——单界面的所有视频组件的全局控制器
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/11.
 *
 * @since 1.0
 */
public class TinyVideoController {

    /**
     * 创建一个新的对象
     */
    public static TinyVideoController create() {
        return new TinyVideoController();
    }

    public TinyVideoController tinyMediaOverlay(TinyMediaOverlay overlay) {
        return this;
    }

    public TinyVideoController tinyVideoManager() {
        return this;
    }

    /**
     * 如果fuzujian
     */
    public void dispatchScroll(AdapterView<?> listview) {

    }

    /**
     * 销毁中间数据，一般是在界面关闭时调用
     */
    public void destroy() {

    }

}
