package com.vip.sdk.videolib.autoplay;

import com.vip.sdk.videolib.TinyVideo;

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
public class MultiDependStrategy implements AutoPlayStrategy {

    private List<AutoPlayStrategy> mOthers;
    public MultiDependStrategy() {

    }

    public MultiDependStrategy with(AutoPlayStrategy one) {
        checkList();
        mOthers.add(one);
        return this;
    }

    public MultiDependStrategy(AutoPlayStrategy... others) {
        if (null != others && others.length > 0) {
            checkList();
            for (AutoPlayStrategy other: mOthers) {
                mOthers.add(other);
            }
        }
    }

    private void checkList() {
        if (null == mOthers) {
            mOthers = new ArrayList<AutoPlayStrategy>(2);
        }
    }

    @Override
    public boolean autoPlay(TinyVideo video) {
        if (null == mOthers) {
            return false;
        }
        boolean flag = true;
        for (int i = 0; i < mOthers.size(); i++) {
            flag &= mOthers.get(i).autoPlay(video);
        }
        return flag;
    }
}
