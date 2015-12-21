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

import com.vip.sdk.base.BaseApplication;
import com.vip.sdk.base.utils.ObjectUtils;

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

    static final boolean DEBUG = TinyDebug.LIST_CONTROLLER;

    /**
     * 封装针对ListView视图类型等方面的支持API，以便跟踪处理
     */
    public interface TinyListCallback {
        /**
         * 如果不是含有视频的项，返回null；如果是还有视频的项，则返回{@link TinyVideoImpl}
         */
        TinyVideo getTinyVideo(int position, View convertView);
    }

    protected class PlayInfo {
        public TinyVideoInfo info;
        public String url; // 指定播放时的URL
        public boolean playUriSet; // playUri有没有设置过，如果设置过，之后的播放暂停等操作我们不再需要过多地涉及
        public boolean manual; // 是否是用户手动触发的

        public boolean isset() {
            return null != info;
        }

        public boolean match(TinyVideoInfo tinyVideoInfo, String uri) {
            return isset() && this.info == tinyVideoInfo && ObjectUtils.equals(uri, this.url);
        }

        public boolean match(TinyVideoInfo tinyVideoInfo) {
            return isset() && this.info == tinyVideoInfo && tinyVideoInfo.matchUri(this.url);
        }

        public void resetIfMatch(TinyVideoInfo tinyVideoInfo, Uri uri) {
            if (isset() && info == tinyVideoInfo/* && !ObjectUtils.equals(String.valueOf(uri), this.url)*/) {
                if (playUriSet) {
                    stopPrevious(info);
                }
                reset();
            }
        }

        public void reset() {
            info = null;
            url = null;
            playUriSet = false;
            manual = false;
        }

        public void set(TinyVideoInfo info, boolean manual) {
            reset();
            if (null != info) {
                this.info = info;
                this.url = String.valueOf(this.info.uri); // 即时地保存url到临时字段
                this.manual = manual;
            }
        }

        @Override
        public String toString() {
            return "info = " + info + ", url = " + url;
        }
    }

    private ListView mListView;
    protected TinyListCallback mTinyListCallback;
    // 正在播放的项
    protected final PlayInfo mPlaying = new PlayInfo();
    protected boolean mIsFling;
    protected boolean mFlingLoad; // 快速滑动时是否加载视频，默认为false
    protected boolean mIsTouchScrolling;
    protected boolean mTouchScrollingLoad = true; // 触屏滑动时是否加载视频，默认为true

    protected final long MIN_LOADING_DELAY = 1 * 1000;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            playCurrentDelayed((TinyVideoInfo) msg.obj);
        }
    };
    public TinyListController(ListView view) {
        mListView = view;
    }

    // 可以直接设置当前对象作为ListView的OnScrollListener，
    // 也可以调用dispatchXXX方法转发外部的OnScrollListener
    public void dispatchOnScrollStateChanged(AbsListView view, int scrollState) {
        // if (DEBUG) Log.d("yytest", "dispatch scroll....");
        mIsFling = false;
        mIsTouchScrolling = false;
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                determinePlay();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                // if (DEBUG) Log.d("yytest", "touch scrolling....");
                mIsTouchScrolling = true;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                // if (DEBUG) Log.d("yytest", "fling....");
                mIsFling = true;
                break;
        }
    }

    public void dispatchOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // if (DEBUG) Log.d("yytest", "dispatch scroll....");
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

    /**
     * 快速滑动时是否预先加载视频
     */
    public TinyListController flingLoad(boolean flingLoad) {
        mFlingLoad = flingLoad;
        return this;
    }

    /**
     * 手势触屏滑动时是否预先加载视频
     */
    public TinyListController touchScrollingLoad(boolean touchScrollingLoad) {
        mTouchScrollingLoad = touchScrollingLoad;
        return this;
    }

    public void determinePlay() {
        // 如果没有设置该回调，无法实现下面的功能
        if (null == mTinyListCallback) {
            return;
        }

        TinyVideo video = findCurrentPlayVideo(mListView);
        TinyVideoInfo newToPlay = null;
        if (null != video) {
            newToPlay = video.myInfo();
        }

        if (DEBUG) Log.d("yytest", "determine playing: " + mPlaying);
        startVideo(newToPlay, false);
    }

    /**
     * 该方法只用于确定，如果不需要加载播放，则调用相应方法
     */
    protected void startVideo(TinyVideoInfo newToPlay, boolean manual) {
        if (mPlaying.isset()) { // 如果之前有播放项
            // 如果已经设置了，且URL改变了，
            if (mPlaying.match(newToPlay)) { // 如果什么都没有改变
                if (mPlaying.playUriSet) {
                    dispatchToVideoStart(newToPlay);
                    return;
                }
                // 否则，也是走加载流程
            } else {
                stopPrevious(mPlaying.info);
            }
        }
        mPlaying.set(newToPlay, manual);
        dispatchDownload(newToPlay, manual);
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

    /**
     * 在加载完成后调用
     */
    protected void playCurrent() {
        if (!mPlaying.isset()) { // 如果没有可播放项，则直接返回
            return;
        }

        final TinyVideoInfo current = mPlaying.info;
        if (null == current.playUri) { // 如果当前的播放Uri尚未加载完成，直接返回
            return;
        }
        // 如果已经下载完成，直接播放
        current.video.dispatchLoading();
        if (DEBUG) Log.e("yytest", current.video + " start uri : " + current.uri);
        if (DEBUG) Log.w("yytest", current.video + " start play uri: " + getPlayUriFileName(current.playUri));

        mHandler.sendMessageDelayed(Message.obtain(mHandler, 0, current), MIN_LOADING_DELAY);
    }

    protected void playCurrentDelayed(TinyVideoInfo current) {
        if (!mPlaying.match(current)) { // 再次验证
            return;
        }
        if (null == current.playUri) {
            if (DEBUG) Log.d("yytest", current.video + " no play uri");
            // 这种情况不执行播放
        } else {
            if (DEBUG) Log.e("yytest", current.video + " start true uri : " + getPlayUriFileName(current.playUri));
            dispatchToVideoSetVideoURI(current, current.playUri);
            dispatchToVideoStart(current);
            // 设置已经开始的标识
            mPlaying.playUriSet = true;
        }
    }

    protected void stopPrevious(TinyVideoInfo previous) {
        mHandler.removeMessages(0); // 不能忘记移除所有的播放message
        dispatchToVideoStop(previous);
    }

    public ViewGroup getContainer() {
        return mListView;
    }

    // =========================================
    // 针对列表专属的继承覆写
    // =========================================
    @Override
    protected void dispatchFromVideoSetUri(TinyVideoInfo info, Uri uri, Map<String, String> headers) {
        // 重新设置时，检测是否是当前播放项，如果是，则重置其状态
        mPlaying.resetIfMatch(info, uri);
        super.dispatchFromVideoSetUri(info, uri, headers);
    }

    @Override
    protected void dispatchDownload(TinyVideoInfo info, boolean force) {
        if (!mTouchScrollingLoad && mIsTouchScrolling) { // 如果触屏滑动，则不下载
            // if (DEBUG) Log.d("yytest", "touch scrolling....不下载");
            return;
        }
        if (!mFlingLoad && mIsFling) { // 如果快速滑动，则不下载
            // if (DEBUG) Log.d("yytest", "fling....不下载");
            return;
        }
        super.dispatchDownload(info, force);
    }

    @Override
    protected void dispatchFromVideoStart(TinyVideoInfo info) {
        startVideo(info, true);
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
            // 如果是当前播放项没有改变起始的URL，且没有下载完成（playUri is null）
            dispatchToVideoLoadErr(mPlaying.info, uri, status);
        }
    }

    // util
    /**
     * 获取uri中的文件名
     */
    protected String getPlayUriFileName(Uri uri) {
        return null == uri ? null : uri.getLastPathSegment();
    }

}
