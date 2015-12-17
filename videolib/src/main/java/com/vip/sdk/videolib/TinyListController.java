package com.vip.sdk.videolib;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

        public boolean match(TinyVideoInfo tinyVideoInfo, String uri) {
            return isset() && info == tinyVideoInfo && info.matchUri(uri);
        }

        public boolean match(TinyVideoInfo tinyVideoInfo, Uri uri) {
            return isset() && info == tinyVideoInfo && info.matchUri(uri);
        }

        public void resetIfUnMatch(TinyVideoInfo tinyVideoInfo, Uri uri) {
            if (isset() && info == tinyVideoInfo && !info.matchUri(uri)) {
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
            if (null != info) {
                this.info = info;
                this.url = String.valueOf(this.info.uri);
            }
        }
    }

    private ListView mListView;
    protected TinyListCallback mTinyListCallback;
    // 正在播放的项
    protected final PlayInfo mPlaying = new PlayInfo();
    protected boolean mIsFling;

    protected final long MIN_LOADING_DELAY = 1 * 1000;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            playCurrentDelayed(msg);
        }
    };
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

        if (mPlaying.isset()) { // 如果之前有播放项
            // 如果已经设置了，且URL改变了，
            if (mPlaying.match(newToPlay, newToPlay.uri)) { // 如果什么都没有改变
                if (!mPlaying.started) {
                    playCurrent();
                }
            } else {
                stopPrevious(mPlaying.info);
                mPlaying.set(newToPlay);
                playCurrent();
            }
        } else { // 如果没有设置，则直接设置新的播放项，并播放
            mPlaying.set(newToPlay);
            playCurrent();
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

    protected void playCurrent() {
        if (!mPlaying.isset()) { // 如果没有可播放项，则直接返回
            return;
        }
        final TinyVideoInfo current = mPlaying.info;
        // 如果已经下载完成，直接播放
        current.video.dispatchLoading();

        mHandler.removeMessages(0);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, 0, current), MIN_LOADING_DELAY);
    }

    protected void playCurrentDelayed(Message msg) {
        TinyVideoInfo current = (TinyVideoInfo) msg.obj;
        if (!mPlaying.match(current, current.uri)) {
            return;
        }
        if (null == current.playUri) {
            // 还没有下载完成，则去下载
            determineLoad(current, current.uri, current.headers);
        } else {
            current.video.superSetVideoURI(current.playUri);
            current.video.start();
            // 设置已经开始的标识
            mPlaying.started = true;
        }
    }

    protected void stopPrevious(TinyVideoInfo previous) {
        mHandler.removeMessages(0);

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
    protected void onVideoPrepared(TinyVideoInfo videoInfo, String uri) {
        if (mPlaying.match(videoInfo, uri)) {
            // 如果是当前播放项，且URL相同，则播放
            playCurrent();
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
