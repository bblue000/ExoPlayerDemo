package com.vip.sdk.base.adapter;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by richard.zhao on 2015/1/16.
 */
public class ViewHolderUtil {
    //加入一个泛型的返回类型，这样客户端就可以避免了繁琐的casting工作
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}