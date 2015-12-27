package com.vip.sdk.uilib.media.video;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.vip.sdk.base.utils.AndroidUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * 视频加载、播放的逻辑控制器、管理器。
 *
 * The controller to load video sources, control videos(set uri, play, pause, stop etc.)
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/25.
 *
 * @since 1.0
 */
public abstract class VideoController {

    static final boolean DEBUG = VIPVideoDebug.CONTROLLER;

    private LinkedHashMap<VIPVideo, VIPVideoToken> mVideoInfoMap = new LinkedHashMap<VIPVideo, VIPVideoToken>();

    /**
     * 销毁中间数据，一般是在界面关闭时调用
     */
    public void destroy() {
        synchronized (mVideoInfoMap) {
            mVideoInfoMap.clear();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    /**
     * 为指定Video设置播放源
     *
     * @see com.vip.sdk.uilib.media.video.VIPVideo#setVideoPath(String)
     */
    public void setPath(VIPVideo vipVideo, String path) {
        setUri(vipVideo, Uri.parse(path));
    }

    /**
     * 为指定Video设置播放源
     *
     * @see com.vip.sdk.uilib.media.video.VIPVideo#setVideoURI(android.net.Uri)
     */
    public void setUri(VIPVideo vipVideo, Uri uri) {
        setUri(vipVideo, uri, null);
    }

    /**
     * 为指定Video设置播放源
     *
     * @param headers 额外的请求头
     *
     * @see com.vip.sdk.uilib.media.video.VIPVideo#setVideoURICompat(android.net.Uri, java.util.Map)
     */
    public abstract void setUri(VIPVideo vipVideo, Uri uri, Map<String, String> headers);

    /**
     * 让指定的视频控件开始播放
     */
    public abstract void start(VIPVideo vipVideo);

    /**
     * 让指定的视频控件暂停播放
     */
    public abstract void pause(VIPVideo vipVideo);

    /**
     * 让指定的视频控件停止播放
     */
    public abstract void stop(VIPVideo vipVideo);

    /**
     * 给指定的视频控件设置状态回调接口
     */
    public abstract void setStateCallback(VIPVideo vipVideo, VideoStateCallback callback) ;

    protected void attachVideo(VIPVideo vipVideo) {
        if (null == vipVideo.getToken()) {
            synchronized (mVideoInfoMap) {
                if (null == vipVideo.getToken()) {
                    mVideoInfoMap.put(vipVideo, new VIPVideoToken(this, vipVideo));
                }
            }
        }
    }

    protected void detachVideo(VIPVideo vipVideo) {
        vipVideo.setToken(null);
        synchronized (mVideoInfoMap) {
            mVideoInfoMap.remove(vipVideo);
        }
    }

    protected void postDo(int what, Object param) {
        Message msg = Message.obtain(mHandler, what, param);
        if (AndroidUtils.isMainThread()) {
            mHandler.handleMessage(msg);
            msg.recycle();
        } else {
            msg.sendToTarget();
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VideoStateCallback.STATE_LOADING:
                    break;
                case VideoStateCallback.STATE_LOAD_ERR:
                    break;
                case VideoStateCallback.STATE_PREPARED:
                    break;
                case VideoStateCallback.STATE_START:
                    break;
                case VideoStateCallback.STATE_PAUSE:
                    break;
                case VideoStateCallback.STATE_COMPLETION:
                    break;
                case VideoStateCallback.STATE_STOP:
                    break;
                case VideoStateCallback.STATE_ERR:
                    break;
            }
//            mStateCallback.onStateChanged(VIPVideo.this, msg.what, (VideoState) msg.obj);
        }
    };
}
