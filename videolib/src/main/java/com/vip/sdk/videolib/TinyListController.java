package com.vip.sdk.videolib;

import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.Map;

/**
 * 列表播放，同时最多只有一个播放。
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/15.
 *
 * @since 1.0
 */
public class TinyListController extends TinyController implements AbsListView.OnScrollListener {

    public interface TinyListCallback {
        /**
         * 如果不是含有视频的项，返回null；如果是还有视频的项，则返回{@link TinyVideo}
         */
        TinyVideo getTinyVideo(int position, View convertView);
    }

    protected class PlayInfo {
        public TinyVideoInfo info;
        public boolean started;
        public String url;

        public boolean isset() {
            return null != info;
        }

        public boolean match(TinyVideoInfo info, String uri) {
            return isset() && info == info && info.matchUri(uri);
        }

        public void resetIfUnMatch(TinyVideoInfo tinyVideoInfo, Uri uri) {
            if (isset() && this.info == tinyVideoInfo && !info.matchUri(uri)) {
                if (started) {
                    dispatchStop(info);
                }
                reset();
            }
        }

        private void reset() {
            info = null;
            started = false;
            url = null;
        }

        public void set(TinyVideoInfo info) {
            reset();
            this.info = info;
            this.url = String.valueOf(this.info.uri);
        }
    }

    private ListView mListView;
    protected TinyListCallback mTinyListCallback;
    // 正在播放的项
    protected final PlayInfo mPlaying = new PlayInfo();
    protected boolean mIsFling;
    public TinyListController(ListView view) {
        mListView = view;
    }

    // 可以直接设置当前对象作为ListView的OnScrollListener，
    // 也可以调用dispatchXXX方法转发外部的OnScrollListener
    public void dispatchOnScrollStateChanged(AbsListView view, int scrollState) {
        if (DEBUG) Log.d("yytest", "dispatch scroll....");
        mIsFling = false;
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                determinePlay();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                if (DEBUG) Log.d("yytest", "fling....");
                mIsFling = (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING);
                break;
        }
    }

    public void dispatchOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        if (DEBUG) Log.d("yytest", "dispatch scroll....");
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        dispatchOnScrollStateChanged(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        dispatchOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    public TinyListController tinyListCallback(TinyListCallback callback) {
        mTinyListCallback = callback;
        return this;
    }

    @Override
    public void determinePlay() {
        // 如果没有设置该回调，无法实现下面的功能
        if (null == mTinyListCallback) {
            return;
        }

        TinyVideo video = findCurrentPlayVideo(mListView);
        TinyVideoInfo newToPlay = null;
        if (null != video) {
            synchronized (mVideoInfoMap) {
                newToPlay = mVideoInfoMap.get(video);
            }
        }
        if (mPlaying.isset()) {
            // 如果已经设置了，
        } else {
            playCurrent(newToPlay);
        }
        mPlaying.set(newToPlay);


        final TinyVideoInfo oldPlaying = mPlaying;

        TinyVideoInfo newToPlay = null;
        if (null != video) {
            synchronized (mVideoInfoMap) {
                newToPlay = mVideoInfoMap.get(video);
            }
        }

        mPlaying = newToPlay;

        if (null != newToPlay) {
            if (newToPlay == oldPlaying) {
                // 如果两次一样
                if (null == newToPlay.playUri) { // not load, to load
                    determineLoad(newToPlay, newToPlay.uri, newToPlay.headers);
                } else {
                    // 如果
                }
            } else {
                stopPrevious(oldPlaying);
            }
            playCurrent(newToPlay);
        } else {
            stopPrevious(oldPlaying);
        }
    }

    protected Rect mTempRect = new Rect();
    protected int[] mTempLoc = new int[2];
    /**
     * 查找当前状态下的可播放项
     */
    protected TinyVideo findCurrentPlayVideo(ListView listView) {
        if (!listView.getGlobalVisibleRect(mTempRect)) { // 没有可显示的区域
            return null;
        }
        final int top = mTempRect.top;
        final int middle = mTempRect.centerY();
        final int firstItemPos = listView.getFirstVisiblePosition();
        for (int i = 0; i < listView.getChildCount(); i++) {
            // 查找top在ListView的[0, height/2]之间的项
            View child = listView.getChildAt(i);
            child.getLocationOnScreen(mTempLoc);
            final int childTop = mTempLoc[1];

            if (childTop < top) continue;
            if (childTop > middle) break;

            TinyVideo video = mTinyListCallback.getTinyVideo(firstItemPos + i, child);
            if (null != video) {
                if (DEBUG) Log.w("yytest", "当前播放的项：" + (firstItemPos + i + 1));
                return video;
            }
        }
        return null;
    }

    protected void playCurrent(TinyVideoInfo current) {
        if (null == current) {
            return;
        }

        if (null == current.playUri) {
            determineLoad(current, current.uri, current.headers);
        } else {

        }
        // 如果播放的是当前的一项，且Uri相同，则不做任何处理
        if (null != current.playUri) { // 说明已经下载好
            if (!current.video.isPlaying()) { // 如果没有在播放
                onVideoPrepared(current);
            }
            return;
        }
        // 否则，去下载
        if (determineLoad(current, current.uri, current.headers)) {
            onVideoPrepared(current);
        }
    }

    protected void stopPrevious(TinyVideoInfo previous) {
        if (null != previous) {
            previous.video.stopPlayback();
        }
    }

    @Override
    public ViewGroup getContainer() {
        return mListView;
    }

    @Override
    protected boolean determineLoad(TinyVideoInfo info, Uri uri, Map<String, String> headers) {
        if (mIsFling) { // 如果快速滑动，则不下载
            // if (DEBUG) Log.d("yytest", "fling....不下载");
            return false;
        }
        if (null != mPlaying) {
            mPlaying.resetIfUnMatch(info, uri);
        }
        return super.determineLoad(info, uri, headers);
    }

    @Override
    protected void onVideoPrepared(TinyVideoInfo videoInfo) {
        if (null == mPlaying) {
            determinePlay();
        } else {
            if (mPlaying == videoInfo) {
                // 如果是当前播放项
                mPlaying.video.superSetVideoURI(videoInfo.playUri);
                mPlaying.video.superStart();
            } else {
                // 如果不是当前播放的，不需要作任何处理
            }
        }
    }

    @Override
    protected void onVideoLoadFailed(TinyVideoInfo info, String uri, LoadErrInfo status) {
        if (mPlaying.match(info, uri) && null == mPlaying.info.playUri) {
            // 如果是当前播放项，且没有下载完成（playUri is null），且没有改变起始的URL
            mPlaying.info.video.dispatchLoadErr(status);
        }
    }

    protected void dispatchStop(TinyVideoInfo info) {
        info.video.stopPlayback();
    }

}
