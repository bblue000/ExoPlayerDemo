package com.vip.sdk.videolib.autoplay;

import com.vip.sdk.videolib.TinyVideo;

/**
 *
 * 自动播放策略
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public interface AutoPlayStrategy {

    /**
     * @return 指定的<code>video</code>是否能自动播放
     */
    boolean autoPlay(TinyVideo video);

}
