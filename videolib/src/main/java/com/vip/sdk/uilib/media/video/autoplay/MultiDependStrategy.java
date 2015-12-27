package com.vip.sdk.uilib.media.video.autoplay;

import com.vip.sdk.uilib.media.video.VIPVideo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 混合策略
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/15.
 *
 * @since 1.0
 */
public class MultiDependStrategy implements AutoLoadable {

    private List<AutoLoadable> mOthers;
    public MultiDependStrategy() {

    }

    public MultiDependStrategy with(AutoLoadable one) {
        checkList();
        mOthers.add(one);
        return this;
    }

    public MultiDependStrategy(AutoLoadable... others) {
        if (null != others && others.length > 0) {
            checkList();
            for (AutoLoadable other: mOthers) {
                mOthers.add(other);
            }
        }
    }

    private void checkList() {
        if (null == mOthers) {
            mOthers = new ArrayList<AutoLoadable>(2);
        }
    }

    @Override
    public boolean autoLoad(VIPVideo video) {
        if (null == mOthers) {
            return false;
        }
        boolean flag = true;
        for (int i = 0; i < mOthers.size(); i++) {
            flag &= mOthers.get(i).autoLoad(video);
        }
        return flag;
    }
}
