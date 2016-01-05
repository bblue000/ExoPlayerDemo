package com.vip.sdk.uilib.video.autoplay;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vip.sdk.uilib.video.VIPVideo;

/**
 *
 * 根据网络类型判断的策略。
 *
 * <br/>
 *
 * WIFI连接自动播放，手机数据网络不自动播放。
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public class NetDependStrategy implements AutoLoadable {

    private ConnectivityManager mManager;

    @Override
    public boolean autoLoad(VIPVideo video) {
        if (null == mManager) {
            mManager = (ConnectivityManager) video.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo networkInfo = mManager.getActiveNetworkInfo();
        return null != networkInfo && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

}
