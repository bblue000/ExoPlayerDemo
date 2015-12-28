package com.vip.sdk.uilib.media.video;

import android.net.Uri;
import android.view.View;

import java.util.Map;

/**
 *
 * 管理单个视频的实现类。
 *
 * <br/>
 *
 * 创建对象时已经传入了管理的视频对象，所以提供了一些简洁方法（如{@link #start()}等），
 * 调用处无需重复传入{@link VIPVideo}
 *
 * Created by Yin Yong on 15/12/27.
 */
public class SingleVideoController extends VideoController {

    private VIPVideo mSingle;
    public SingleVideoController(VIPVideo video) {
        mSingle = video;
    }

    @Override
    protected VIPVideo findCurrentPlayVideo() {
        if (null == mSingle) {
            return null;
        }
        if (mSingle.getVisibility() == View.GONE) {
            return null;
        }
        return isInParentViewPort(mSingle) ? mSingle : null;
    }

    public void setVideoPath(String url) {
        super.setVideoPath(mSingle, url);
    }

    public void setVideoURI(Uri uri) {
        super.setVideoURI(mSingle, uri);
    }

    public void setVideoURICompat(Uri uri, Map<String, String> headers) {
        super.setVideoURI(mSingle, uri, headers);
    }

    public void start() {
        super.start(mSingle);
    }

    public boolean isPlaying() {
        return super.isPlaying(mSingle);
    }

    public void seekTo(int msec) {
        super.seekTo(mSingle, msec);
    }

    public void pause() {
        super.pause(mSingle);
    }

    public void stop() {
        super.stop(mSingle);
    }

    public void setStateCallback(VideoControlCallback callback) {
        super.setStateCallback(mSingle, callback);
    }
}
