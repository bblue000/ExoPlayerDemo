package com.vip.sdk.uilib.video.autoplay;

import com.vip.sdk.uilib.video.VIPVideo;

/**
 *
 * 针对需要网络加载的视频，自动加载播放策略。
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
public interface AutoLoadable {

    /**
     * @return 指定的<code>video</code>是否能自动加载播放
     */
    boolean autoLoad(VIPVideo video);

}
