package com.vip.sdk.videolib.autoplay;

import com.vip.sdk.videolib.VIPVideo;

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
public class MultiDependStrategy implements AutoLoadStrategy {

    private List<AutoLoadStrategy> mOthers;
    public MultiDependStrategy() {

    }

    public MultiDependStrategy with(AutoLoadStrategy one) {
        checkList();
        mOthers.add(one);
        return this;
    }

    public MultiDependStrategy(AutoLoadStrategy... others) {
        if (null != others && others.length > 0) {
            checkList();
            for (AutoLoadStrategy other: mOthers) {
                mOthers.add(other);
            }
        }
    }

    private void checkList() {
        if (null == mOthers) {
            mOthers = new ArrayList<AutoLoadStrategy>(2);
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
