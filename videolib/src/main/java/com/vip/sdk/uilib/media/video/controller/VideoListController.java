package com.vip.sdk.uilib.media.video.controller;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.vip.sdk.uilib.media.video.VIPVideo;
import com.vip.sdk.uilib.media.video.VIPVideoDebug;
import com.vip.sdk.uilib.media.video.VIPVideoToken;
import com.vip.sdk.uilib.media.video.VideoController;

/**
 * Created by Yin Yong on 15/12/28.
 */
public class VideoListController extends VideoController implements AbsListView.OnScrollListener {

    protected static final boolean DEBUG = VIPVideoDebug.LIST_CONTROLLER;

    /**
     * 封装ListView view type等相关的API，以便跟踪处理
     */
    public interface VideoListCallback {
        /**
         * 如果不是含有视频的项，返回null；如果是含有视频的项，则返回{@link com.vip.sdk.uilib.media.video.VIPVideo}
         */
        VIPVideo getVIPVideo(int position, View convertView);
    }

    private ListView mListView;
    protected boolean mIsFling;
    protected boolean mFlingLoad; // 快速滑动时是否加载视频，默认为false
    protected boolean mIsTouchScrolling;
    protected boolean mTouchScrollingLoad = true; // 触屏滑动时是否加载视频，默认为true
    protected VideoListCallback mVideoListCallback;
    public VideoListController(ListView listView) {
        mListView = listView;
    }

    /**
     * 播放指定位置
     */
    public void start(int position, boolean forceScroll) {
        start(position, 0, forceScroll);
    }

    /**
     * 播放指定位置
     */
    public void start(int position, int msec, boolean forceScroll) {
        ListAdapter adapter = mListView.getAdapter();
        if (null == adapter || mListView.getChildCount() == 0) {
            // do nothing
            return;
        }
        final int firstItemPos = mListView.getFirstVisiblePosition();
        final int lastItemPos = mListView.getLastVisiblePosition();
//        if ((firstItemPos > position || lastItemPos) && !forceScroll) {
//            return;
//        }
        mListView.setSelection(position);

//        VIPVideo video = mVideoListCallback.getVIPVideo(position, child);
    }

    /**
     * 设置回调
     */
    public VideoListController videoListCallback(VideoListCallback callback) {
        mVideoListCallback = callback;
        return this;
    }

    /**
     * 快速滑动时是否预先加载视频，默认为false
     */
    public VideoListController flingLoad(boolean flingLoad) {
        mFlingLoad = flingLoad;
        return this;
    }

    /**
     * 手势触屏滑动时是否预先加载视频，默认为true
     */
    public VideoListController touchScrollingLoad(boolean touchScrollingLoad) {
        mTouchScrollingLoad = touchScrollingLoad;
        return this;
    }

    /**
     * 判断{@link ListView}是否在滚动状态
     */
    public boolean isScrolling() {
        return mIsFling || mIsTouchScrolling;
    }

    // 可以直接设置当前对象作为ListView的OnScrollListener，
    // 也可以调用dispatchXXX方法转发外部的OnScrollListener
    public void dispatchOnScrollStateChanged(AbsListView view, int scrollState) {
        checkCurrentPlayInViewport();
        mIsFling = false;
        mIsTouchScrolling = false;
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                determinePlay();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mIsTouchScrolling = true;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                mIsFling = true;
                break;
        }
    }

    public void dispatchOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
        checkCurrentPlayInViewport();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        dispatchOnScrollStateChanged(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        dispatchOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    protected VIPVideo findCurrentPlayVideo() {
        if (null == mVideoListCallback) return null;
        if (!mListView.getGlobalVisibleRect(mTempRect)) return null;// 没有可显示的区域

        final int top = mTempRect.top;
        final int middle = mTempRect.centerY();
        final int firstItemPos = mListView.getFirstVisiblePosition();
        for (int i = 0; i < mListView.getChildCount(); i++) {
            // 查找top在ListView的[0, height/2]之间的项
            View child = mListView.getChildAt(i);
            child.getLocationOnScreen(mTempLoc);
            final int childTop = mTempLoc[1];

            if (childTop < top) continue;
            if (childTop > middle) break;

            VIPVideo video = mVideoListCallback.getVIPVideo(firstItemPos + i, child);

            if (null != video) if (DEBUG) Log.w(TAG, "当前播放的项：" + (firstItemPos + i + 1));

            return video;
        }
        return null;
    }

    protected void checkCurrentPlayInViewport() {
        if (!mPlaying.isset()) return;

        if (!isInViewport(mPlaying.token.video, mListView)) {
            stopPrevious(mPlaying.token);
            mPlaying.reset();
        }
    }

    @Override
    protected boolean dispatchDownload(VIPVideoToken token, boolean force) {
        if (!mTouchScrollingLoad && mIsTouchScrolling) { // 如果触屏滑动，则不下载
            return false;
        }
        if (!mFlingLoad && mIsFling) { // 如果快速滑动，则不下载
            return false;
        }
        return super.dispatchDownload(token, force);
    }
}
