package com.vip.sdk.uilib.media.video;

import android.app.Activity;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.vip.sdk.base.utils.AndroidUtils;
import com.vip.sdk.base.utils.ObjectUtils;
import com.vip.sdk.uilib.media.video.autoplay.AutoLoadable;
import com.vip.sdk.uilib.media.video.autoplay.NetDependStrategy;
import com.vip.sdk.uilib.media.video.cache.SimpleVideoCache;
import com.vip.sdk.uilib.media.video.cache.VideoCache;

import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * 视频加载、播放的逻辑控制器、管理器。
 *
 * The controller to load video sources, control videos(set uri, play, pause, stop etc.)
 *
 * <p/>
 *
 * 主要过程：
 * set uri---try download---(download success)--set uri to video---(when prepared)---try start
 * --->play video
 *
 * <p/>
 * Created by Yin Yong on 15/12/25.
 *
 * @since 1.0
 */
public abstract class VideoController implements VideoWidgetDelegate {

    static final String TAG = VIPVideoDebug.TAG; // VideoController.class.getSimpleName();
    static final boolean DEBUG = VIPVideoDebug.CONTROLLER;

    private WeakHashMap<VIPVideo, Object> mVideoMap = new WeakHashMap<VIPVideo, Object>(4);
    private VideoCache mVideoCache;
    private AutoLoadable mAutoLoadable;
    protected final TargetPlayInfo mPlaying = new TargetPlayInfo();
    private long mLeastLoadingDelay = 1 * 1000;
    // video operation API start
    /**
     * 自动确定播放哪一项，该方法不推荐在界面刚初始化时调用，
     * 因为初始化时还不能确定视频控件在窗口中的位置。
     *
     * @see #findCurrentPlayVideo()
     */
    public void determinePlay() {
        VIPVideo video = findCurrentPlayVideo();
        VIPVideoToken token = null;
        if (null != video) {
            attachVideo(video);
            token = video.getToken();
        }

        if (DEBUG) Log.d(TAG, "determine playing: " + mPlaying);
        updateCurrentPlayVideo(token, false);
    }

    /**
     * 如{@link Activity#onPause()}时，调用该方法，将保存当前播放的状态并暂停
     */
    public void pauseControl() {
        mPlaying.saveState();
    }

    /**
     * 由外部调用，如{@link Activity#onResume()}时，调用该方法，将相应地重置状态
     */
    public void resumeControl() {
        mPlaying.restoreState();
    }

    /**
     * 由子类实现，查找当前能够播放的项
     */
    protected abstract VIPVideo findCurrentPlayVideo();

    @Override
    public void setVideoPath(VIPVideo video, String path) {
        setVideoURI(video, Uri.parse(path));
    }

    @Override
    public void setVideoURI(VIPVideo video, Uri uri) {
        setVideoURI(video, uri, null);
    }

    @Override
    public void setVideoURI(VIPVideo video, Uri uri, Map<String, String> headers) {
        attachVideo(video);
        VIPVideoToken token = video.getToken();
        // 重新设置时，检测是否是当前播放项，如果是，则重置其状态
        mPlaying.resetIfMatch(token, uri);
        if (null != token) {
            token.setVideoURI(uri, headers);
            token.seekWhenPrepared = 0; // reset
            token.currentState = VIPVideoToken.STATE_PREPARING;
            dispatchDownload(token, false);
        }
    }

    @Override
    public void start(VIPVideo video) {
        attachVideo(video);
        VIPVideoToken token = video.getToken();
        updateCurrentPlayVideo(token, true);
    }

    @Override
    public boolean isPlaying(VIPVideo video) {
        attachVideo(video);
        return mPlaying.token == video.getToken() && video.isPlaying();
    }

    @Override
    public void seekTo(VIPVideo video, int msec) {
        attachVideo(video);
        VIPVideoToken token = video.getToken();
        if (token.isInPlaybackState()) {
            token.seekWhenPrepared = 0;
            doVideoSeekTo(video.getToken(), msec);
        } else {
            token.seekWhenPrepared = msec;
        }
    }

    @Override
    public void pause(VIPVideo video) {
        attachVideo(video);
        VIPVideoToken token = video.getToken();
        if (token.isInPlaybackState()) {
            token.currentState = VIPVideoToken.STATE_PAUSED;
        }
        token.targetState = VIPVideoToken.STATE_PAUSED;
        doVideoPause(token);
    }

    @Override
    public void stop(VIPVideo video) {
        attachVideo(video);
        VIPVideoToken token = video.getToken();
        token.currentState = VIPVideoToken.STATE_IDLE;
        token.targetState = VIPVideoToken.STATE_IDLE;
        doVideoStop(video.getToken());
    }

    @Override
    public void setControlCallback(VIPVideo video, VideoControlCallback callback) {
        attachVideo(video);
        VIPVideoToken token = video.getToken();
        token.stateCb = callback;
        if (null != token.stateCb && token.video.isPlaying()) {
            postState(VideoControlCallback.STATE_START, token, null);
        }
    }
    // video operation API end

    // settings API start
    /**
     * 至少需要加载的时间，为了让已经缓存的视频仍然显得有加载过程。
     */
    public VideoController leastLoadingDelay(long msec) {
        mLeastLoadingDelay = msec;
        return this;
    }

    /**
     * 设置自动播放策略，默认为{@link NetDependStrategy}。
     */
    public VideoController setAutoLoadStrategy(AutoLoadable strategy) {
        mAutoLoadable = strategy;
        return this;
    }

    /**
     * 设置缓存管理
     */
    public VideoController setCache(VideoCache cache) {
        this.mVideoCache = cache;
        return this;
    }

    public AutoLoadable getAutoPlayStrategy() {
        if (null == mAutoLoadable) {
            mAutoLoadable = createDefaultAutoPlayStrategy();
        }
        return mAutoLoadable;
    }

    protected AutoLoadable createDefaultAutoPlayStrategy() {
        return new NetDependStrategy();
    }

    /**
     * 获取当前使用的缓存管理
     */
    public VideoCache getCache() {
        if (null == mVideoCache) {
            mVideoCache = createDefaultCache();
        }
        return mVideoCache;
    }

    /**
     * 创建默认的
     */
    protected VideoCache createDefaultCache() {
        return new SimpleVideoCache();
    }

    /**
     * 销毁中间数据，一般是在界面关闭时调用
     */
    public void destroy() {
        mVideoMap.clear();
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    // inner implements start
    /**
     * 该方法只用于确定，如果不需要加载播放，则调用相应方法
     */
    protected void updateCurrentPlayVideo(VIPVideoToken token, boolean manual) {
        if (mPlaying.isset()) { // 如果之前有播放项
            // 如果已经设置了，且URL改变了，
            if (mPlaying.match(token)) { // 如果什么都没有改变
                if (mPlaying.prepared) { // 已经处于准备好播放的状态
                    startTargetVideo(mPlaying.token);
                    return;
                }
                // 否则，也是走加载流程
            } else {
                stopPrevious(mPlaying.token);
            }
        }
        mPlaying.set(token, manual);
        if (dispatchDownload(token, manual)) {
            postState(VideoControlCallback.STATE_LOADING, token, null);
        }
    }

    protected void startTargetVideo(VIPVideoToken token) {
        if (token.isInPlaybackState()) {
            token.currentState = VIPVideoToken.STATE_PLAYING;
        }
        token.targetState = VIPVideoToken.STATE_PLAYING;
        doVideoStart(token); // FIXME 是否在这里做，还是确定控件没有开始播放时才start
    }

    /**
     * 在加载完成后调用，看是否能够播放当前项
     */
    protected void tryPlayCurrent() {
        if (!mPlaying.isset()) return; // 如果没有可播放项，则直接返回

        final VIPVideoToken current = mPlaying.token;
        if (null == current.playUri) return;// 如果当前的播放Uri尚未加载完成，直接返回

        if (DEBUG) Log.e(TAG, current.video + " start uri : " + current.uri);
        if (DEBUG) Log.w(TAG, current.video + " start play uri: " + getPlayUriFileName(current.playUri));

        doSetVideoUri(current, current.playUri, current.headers);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_DELAY_PLAY, current), mLeastLoadingDelay);
    }

    protected void playCurrentDelayed(VIPVideoToken current) {
        if (!mPlaying.match(current)) return; // 再次验证

        if (mPlaying.prepared && null != mPlaying.token.playUri) {
            if (DEBUG) Log.d(TAG, current.video + " start...");
            if (current.seekWhenPrepared != 0) {
                doVideoSeekTo(current, current.seekWhenPrepared);
                current.seekWhenPrepared = 0;
            }
            startTargetVideo(current);
        } else {
            if (DEBUG) Log.d(TAG, current.video + " no play uri");
            // 这种情况不执行播放
        }
    }

    protected void stopPrevious(VIPVideoToken previous) {
        mHandler.removeMessages(MSG_DELAY_PLAY); // 不能忘记移除所有的播放message
        stop(previous.video);
    }

    // 直接调用video的方法，并相应地处理各自的回调（如果有的话）
    /**
     * 此处根据设置的Uri和headers尝试下载
     */
    protected void doSetVideoUri(VIPVideoToken token, Uri uri, Map<String, String> headers) {
        token.video.setVideoURICompat(uri, headers);
    }

    protected void doVideoStart(VIPVideoToken token) {
        token.video.start();
        postState(VideoControlCallback.STATE_START, token, null);
    }

    protected void doVideoSeekTo(VIPVideoToken token, int msec) {
        token.video.seekTo(msec);
    }

    protected void doVideoPause(VIPVideoToken token) {
        boolean isPlaying = token.video.isPlaying();
        token.video.pause(); // just do pause
        if (isPlaying ^ token.video.isPlaying()) {
            postState(VideoControlCallback.STATE_PAUSE, token, null);
        }
    }

    protected void doVideoStop(VIPVideoToken token) {
        token.video.stop();
        // 无论如何都发送一个停止的回调
        postState(VideoControlCallback.STATE_STOP, token, null);
    }
    // end

    /**
     * {@link #doSetVideoUri(VIPVideoToken, Uri, Map)}操作中，发现相应的uri还没有加载到playUri时将调用该方法
     *
     * @param force 如果视频资源尚未加载成功，是否强制加载（即使{@link #getAutoPlayStrategy()#autoLoad(VIPVideo)}返回false）
     *
     * @return 返回true表明需要下载，后续将会通过{@link #onVideoDownloaded(VIPVideoToken, String, android.net.Uri)}
     * 或者{@link #onVideoLoadFailed(VIPVideoToken, String, VideoControlCallback.VideoStatus)}
     * 等相关方法返回结果。
     */
    protected boolean dispatchDownload(VIPVideoToken token, boolean force) {
        if (null != token) {
            if (null == token.playUri) { // 如果还没有下载资源
                if (force || getAutoPlayStrategy().autoLoad(token.video)) { // 如果允许自动加载播放，则往下执行
                    getCache().load(token, mCacheCallback);
                    return true;
                }
            } else {
                onVideoDownloaded(token, String.valueOf(token.uri), token.playUri);
                return true;
            }
        }
        return false;
    }

    /**
     * 当设置了本地播放资源，视频控件反馈可以播放时调用
     */
    protected void onVideoPrepared(VIPVideoToken token) {
        token.currentState = VIPVideoToken.STATE_PREPARED;
        postState(VideoControlCallback.STATE_PREPARED, token, null);
        // 看看是否是当前项加载好了
        if (mPlaying.match(token) && null != mPlaying.token.playUri) {
            mPlaying.prepared = true;
            // try play current
            mHandler.removeMessages(MSG_DELAY_PLAY);
            // 特别处理一下预设，如果目标的状态是暂停或者停止，特别处理一下
            if (token.targetState > VIPVideoToken.STATE_PLAYING ) {
//                switch (token.targetState) {
//                    case VIPVideoToken.STATE_PAUSED:
//                        pause(token.video);
//                        break;
//                    case VIPVideoToken.STATE_PLAYBACK_COMPLETED:
//                        stop(token.video);
//                        break;
//                }
            } else {
                playCurrentDelayed(token);
            }
        }
    }

    /**
     * 当设置了本地播放资源，视频控件反馈播放完成时调用
     */
    protected void onVideoPlayCompleted(VIPVideoToken token) {
        token.currentState = VIPVideoToken.STATE_PLAYBACK_COMPLETED;
        token.targetState = VIPVideoToken.STATE_PLAYBACK_COMPLETED;
        postState(VideoControlCallback.STATE_COMPLETION, token, null);
    }

    /**
     * 当设置了本地播放资源，视频控件反馈播放错误时调用
     *
     * @return 如果做了一定的处理，则返回true，否则返回false
     */
    protected boolean onVideoPlayError(VIPVideoToken token, VideoControlCallback.VideoStatus state) {
        if (DEBUG) Log.e(TAG, String.valueOf(state));
        token.currentState = VIPVideoToken.STATE_ERROR;
        token.targetState = VIPVideoToken.STATE_ERROR;
        postState(VideoControlCallback.STATE_ERR, token, state);
        onVideoPlayCompleted(token);
        return true;
    }

    // 设置必要的token信息
    protected void attachVideo(VIPVideo video) {
        if (!videoAttached(video)) {
            // mVideoInfoMap.put(video, new VIPVideoToken(this, video));
            video.setToken(new VIPVideoToken(this, video));
            mVideoMap.put(video, this);
            video.setInnerAPIOnPreparedListener(mOnPreparedListener);
            video.setInnerAPIOnCompletionListener(mOnCompletionListener);
            video.setInnerAPIOnErrorListener(mOnErrorListener);
        }
    }

    // 从管理器中删除制定的控件及其相应的token，不再对其进行管理
    protected void detachVideo(VIPVideo video) {
        mVideoMap.remove(video);
        video.setToken(null);
        video.setInnerAPIOnPreparedListener(null);
        video.setInnerAPIOnCompletionListener(null);
        video.setInnerAPIOnErrorListener(null);
    }

    protected boolean videoAttached(VIPVideo video) {
        return null != video && null != video.getToken() && this == video.getToken().controller;
    }

    // =============================================
    // 下载的统一管理
    // =============================================
    /**
     * 视频缓存好时调用
     */
    protected void onVideoDownloaded(VIPVideoToken token, String uri, Uri target) {
        if (token.matchUri(uri)) {
            token.playUri = target;
        }
        if (mPlaying.match(token, uri)) {
            // 如果是当前播放项，且URL相同，则播放
            tryPlayCurrent();
        }
    }

    /**
     * 视频缓存出错时调用
     */
    protected void onVideoLoadFailed(VIPVideoToken token, String uri, VideoControlCallback.VideoStatus status) {
        if (mPlaying.match(token, uri) && null == mPlaying.token.playUri) {
            // 如果是当前播放项没有改变起始的URL，且没有下载完成（playUri is null）
            if (DEBUG) Log.e(TAG, "onVideoLoadFailed");
            // send message
            postState(VideoControlCallback.STATE_LOAD_ERR, token, status);
        }
    }

    private VideoCache.CacheCallback mCacheCallback = new VideoCache.CacheCallback() {

        @Override
        public void onCacheProgress(VIPVideoToken token, String uri, long current, long total) {
            // do nothing here
        }

        @Override
        public void onCacheSuccess(VIPVideoToken token, String uri, Uri target) {
            if (DEBUG) Log.d(TAG, uri.substring(uri.lastIndexOf("/") + 1) + "下载好了");
            onVideoDownloaded(token, uri, target);
        }

        @Override
        public void onCacheFailed(VIPVideoToken token, String uri, VideoControlCallback.VideoStatus status) {
            if (DEBUG) Log.d(TAG, uri.substring(uri.lastIndexOf("/") + 1) + "下载失败了");
            onVideoLoadFailed(token, uri, status);
        }

    };

    private VIPVideo.OnPreparedListener mOnPreparedListener = new VIPVideo.OnPreparedListener() {
        @Override
        public void onPrepared(VIPVideo video, MediaPlayer mp) {
            if (videoAttached(video)) {
                onVideoPrepared(video.getToken());
            }
        }
    };

    private VIPVideo.OnCompletionListener mOnCompletionListener = new VIPVideo.OnCompletionListener() {
        @Override
        public void onCompletion(VIPVideo video, MediaPlayer mp) {
            if (videoAttached(video)) {
                onVideoPlayCompleted(video.getToken());
            }
        }
    };

    private VIPVideo.OnErrorListener mOnErrorListener = new VIPVideo.OnErrorListener() {

        @Override
        public boolean onError(VIPVideo video, MediaPlayer mp, int what, int extra) {
            if (videoAttached(video)) {
                return onVideoPlayError(video.getToken(), new VideoControlCallback.VideoStatus(what, "error").extraCode(extra));
            }
            return false;
        }

    };

    protected void postState(int what, VIPVideoToken token, VideoControlCallback.VideoStatus status) {
        Message msg;
        if (null == status) {
            msg = Message.obtain(mHandler, MSG_STATE, what, 0, token);
        } else {
            msg = Message.obtain(mHandler, MSG_STATE, what, 0, new MergeToken(token, status));
        }
        if (AndroidUtils.isMainThread()) {
            mHandler.handleMessage(msg);
            msg.recycle();
        } else {
            msg.sendToTarget();
        }
    }

    private class MergeToken {
        public final VIPVideoToken target;
        public final VideoControlCallback.VideoStatus param;

        private MergeToken(VIPVideoToken target, VideoControlCallback.VideoStatus param) {
            this.target = target;
            this.param = param;
        }
    }

    private final int MSG_DELAY_PLAY = -1;
    private final int MSG_STATE = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STATE:
                    VIPVideoToken token;
                    VideoControlCallback.VideoStatus status = null;
                    if (msg.obj instanceof MergeToken) {
                        MergeToken mergeToken = (MergeToken) msg.obj;
                        token = mergeToken.target;
                        status = mergeToken.param;
                    } else {
                        token = (VIPVideoToken) msg.obj;
                    }
                    final VideoControlCallback controlCallback = token.stateCb;
                    if (null != controlCallback) {
                        controlCallback.onStateChanged(token.video, msg.arg1, status);
                    }
                    break;
                case MSG_DELAY_PLAY:
                    playCurrentDelayed((VIPVideoToken) msg.obj);
                    break;
            }
        }
    };

    /**
     * 当前准备/正在播放的项
     */
    public class TargetPlayInfo {
        public VIPVideoToken token; // 当前的视频项
        public String url; // 指定播放时的URL
        public boolean prepared; // playUri有没有设置过，如果设置过，之后的播放暂停等操作我们不再需要过多地涉及
        public boolean manual; // 是否是用户手动触发的

        /**
         * 是否设置有当前播放项
         */
        public boolean isset() {
            return null != token;
        }

        public boolean match(VIPVideoToken token, String uri) {
            return isset() && this.token == token && ObjectUtils.equals(uri, this.url);
        }

        public boolean match(VIPVideoToken token) {
            return isset() && this.token == token && token.matchUri(this.url);
        }

        public void resetIfMatch(VIPVideoToken token, Uri uri) {
            if (isset() && this.token == token/* && !ObjectUtils.equals(String.valueOf(uri), this.url)*/) {
                if (prepared) {
                    stopPrevious(this.token);
                }
                reset();
            }
        }

        public void reset() {
            token = null;
            url = null;
            prepared = false;
            manual = false;
            isPlayingWhenPauseControl = false;
        }

        public void set(VIPVideoToken token, boolean manual) {
            reset();
            if (null != token) {
                this.token = token;
                this.url = String.valueOf(this.token.uri); // 即时地保存url到临时字段
                this.manual = manual;
            }
        }

        // 保存/恢复状态 start
        private boolean isPlayingWhenPauseControl;
        private int posWhenPauseControl;
        public void saveState() {
            if (isset()) {
                isPlayingWhenPauseControl = token.video.isPlaying();
                posWhenPauseControl = token.video.getCurrentPosition();
            }
        }

        public void restoreState() {
            if (isset()) {
                if (posWhenPauseControl != 0) {
                    seekTo(token.video, posWhenPauseControl);
                    if (isPlayingWhenPauseControl) {
                        start(token.video);
                    } else {
                        pause(token.video);
                    }
                } else {
                    if (isPlayingWhenPauseControl) {
                        start(token.video);
                    }
                }
            }
            isPlayingWhenPauseControl = false;
            posWhenPauseControl = 0;
        }
        // end

        @Override
        public String toString() {
            return "token = " + token + ", url = " + url;
        }
    }

    // =============================================
    // util
    // =============================================
    protected Rect mTempRect = new Rect();
    protected int[] mTempLoc = new int[2];
    /**
     * 是否在指定父控件的可视范围之内
     */
    protected boolean isInViewport(VIPVideo video, ViewGroup parent) {
        if (null == video || null == parent || !parent.getGlobalVisibleRect(mTempRect)) { // 没有可显示的区域
            return false;
        }
        final int top = mTempRect.top;
        video.getLocationOnScreen(mTempLoc);
        return mTempLoc[1] + video.getHeight() > top;
    }

    /**
     * 判断是否在其直接父控件的可是范围内
     */
    protected boolean isInParentViewPort(VIPVideo video) {
        if (null == video || null == video.getParent()) {
            return false;
        }
        ViewParent parent = video.getParent();
        if (parent instanceof ViewGroup) {
            return isInViewport(video, (ViewGroup) parent);
        }
        return false;
    }

    /**
     * 获取uri中的文件名
     */
    public static String getPlayUriFileName(Uri uri) {
        return null == uri ? null : uri.getLastPathSegment();
    }
}
