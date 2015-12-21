package com.vip.sdk.videolib.autoplay;

import com.vip.sdk.videolib.TinyVideo;

/**
 *
 * 视频自动加载播放策略。
 *
 * <br/>
 *
 * 因为有可能是需要适时判断的，抽象出该接口，让外部实现想要的策略
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public interface AutoLoadStrategy {

    /**
     * @return 指定的<code>video</code>是否能自动加载播放
     */
    boolean autoLoad(TinyVideo video);

}
