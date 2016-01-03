package com.vip.sdk.uilib.media.video.widget;

import android.net.Uri;

/**
 * Created by Yin Yong on 16/1/4.
 */
public class PlayParam {

    /**
     * 目标资源
     */
    public Uri target = null;

    /**
     * 是否自动播放，默认为false，即不自动播放
     */
    public boolean autoPlay = false;

    /**
     * 开始播放的位置，默认是0，从头开始播放
     */
    public int position = 0;

    /**
     * 回调
     */
    public interface Callback {
        /**
         *
         * @param playOver 如果播放结束了，如果中断了播放
         * @param position 播放到的位置
         */
        void onResult(boolean playOver, int position);
    }


}
