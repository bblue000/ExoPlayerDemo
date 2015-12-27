package com.vip.sdk.uilib.media.video;

import android.view.View;

/**
 *
 * 管理单个视频的实现类
 *
 * Created by Yin Yong on 15/12/27.
 */
public class SingleVideoController extends VideoController {

    private VIPVideo mSingle;
    public SingleVideoController(VIPVideo video) {
        mSingle = video;
    }

    @Override
    protected VIPVideo findCurrentPlayVideo() {
        if (null == mSingle) {
            return null;
        }
        if (mSingle.getVisibility() == View.GONE) {
            return null;
        }
        return isInParentViewPort(mSingle) ? mSingle : null;
    }
}
