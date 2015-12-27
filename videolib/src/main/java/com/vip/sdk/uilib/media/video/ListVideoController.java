package com.vip.sdk.uilib.media.video;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Yin Yong on 15/12/28.
 */
public class ListVideoController extends VideoController implements AbsListView.OnScrollListener {

    /**
     * 封装针对ListView视图类型等方面的支持API，以便跟踪处理
     */
    public interface VideoListCallback {
        /**
         * 如果不是含有视频的项，返回null；如果是还有视频的项，则返回{@link com.vip.sdk.uilib.media.video.VIPVideo}
         */
        VIPVideo getTinyVideo(int position, View convertView);
    }

    private ListView mListView;
    protected boolean mIsFling;
    protected boolean mFlingLoad; // 快速滑动时是否加载视频，默认为false
    protected boolean mIsTouchScrolling;
    protected boolean mTouchScrollingLoad = true; // 触屏滑动时是否加载视频，默认为true
    protected VideoListCallback mVideoListCallback;
    public ListVideoController(ListView listView) {
        mListView = listView;
    }

    public ListVideoController videoListCallback(VideoListCallback callback) {
        mVideoListCallback = callback;
        return this;
    }

    /**
     * 快速滑动时是否预先加载视频
     */
    public ListVideoController flingLoad(boolean flingLoad) {
        mFlingLoad = flingLoad;
        return this;
    }

    /**
     * 手势触屏滑动时是否预先加载视频
     */
    public ListVideoController touchScrollingLoad(boolean touchScrollingLoad) {
        mTouchScrollingLoad = touchScrollingLoad;
        return this;
    }

    @Override
    protected VIPVideo findCurrentPlayVideo() {
        if (!mListView.getGlobalVisibleRect(mTempRect)) { // 没有可显示的区域
            return null;
        }
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

            VIPVideo video = mVideoListCallback.getTinyVideo(firstItemPos + i, child);
            if (null != video) {
                if (DEBUG) Log.w(TAG, "当前播放的项：" + (firstItemPos + i + 1));
                return video;
            }
        }
        return null;
    }

    protected void checkCurrentPlayInViewport() {
        if (!mPlaying.isset()) {
            return;
        }
        if (!isInViewport(mPlaying.token.video, mListView)) {
            stopPrevious(mPlaying.token);
            mPlaying.reset();
        }
    }

    // 可以直接设置当前对象作为ListView的OnScrollListener，
    // 也可以调用dispatchXXX方法转发外部的OnScrollListener
    public void dispatchOnScrollStateChanged(AbsListView view, int scrollState) {
        // if (DEBUG) Log.d("yytest", "dispatch scroll....");
        checkCurrentPlayInViewport();
        mIsFling = false;
        mIsTouchScrolling = false;
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                determinePlay();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                // if (DEBUG) Log.d(TAG, "touch scrolling....");
                mIsTouchScrolling = true;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                // if (DEBUG) Log.d(TAG, "fling....");
                mIsFling = true;
                break;
        }
    }

    public void dispatchOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // if (DEBUG) Log.d(TAG, "dispatch scroll....");
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
    protected boolean dispatchDownload(VIPVideoToken token, boolean force) {
        if (!mTouchScrollingLoad && mIsTouchScrolling) { // 如果触屏滑动，则不下载
            // if (DEBUG) Log.d(TAG", "touch scrolling....不下载");
            return false;
        }
        if (!mFlingLoad && mIsFling) { // 如果快速滑动，则不下载
            // if (DEBUG) Log.d(TAG, "fling....不下载");
            return false;
        }
        return super.dispatchDownload(token, force);
    }
}
