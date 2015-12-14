package com.vip.sdk.videolib.download;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.vip.sdk.videolib.TinyVideoInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 *
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public class VideoAjaxCallback extends AbstractAjaxCallback<File, VideoAjaxCallback> {

    private static HashMap<String, WeakHashMap<TinyVideoInfo, VideoAjaxCallback>> queueMap = new HashMap<String, WeakHashMap<TinyVideoInfo, VideoAjaxCallback>>();

    public static File CACHE_DIR;

    /**
     * 缓存文件夹
     */
    public static File getVideoCacheDir(Context context){
        if (null == CACHE_DIR) {
            CACHE_DIR = new File(AQUtility.getCacheDir(context), "video");
            if (!CACHE_DIR.exists()) {
                CACHE_DIR.mkdirs();
            }
        }
        return CACHE_DIR;
    }


    /**
     * Clear the disk cache.
     */
    public static void clearCache(){

    }

    protected File mTargetFile;
    protected TinyVideoInfo mTinyVideoInfo;
    /**
     * Instantiates a new bitmap ajax callback.
     */
    public VideoAjaxCallback(){
        type(File.class).url("");
    }

    public VideoAjaxCallback file(File file) {
        mTargetFile = file;
        return this;
    }

    public VideoAjaxCallback videoInfo(TinyVideoInfo videoInfo) {
        mTinyVideoInfo = videoInfo;
        if (mTinyVideoInfo) {

        }
        return url();
    }

    @Override
    protected File getPreFile() {
        return null != mTargetFile ? mTargetFile : AQUtility.getCacheFile(
                getVideoCacheDir(mTinyVideoInfo.video.getContext()), );
    }

    @Override
    protected File makeTempFile(File file) throws IOException {
        return file;
    }

    @Override
    protected void skip(String url, File bm, AjaxStatus status){
        queueMap.remove(url);
    }

    // 是否文件已经存在，存在则返回，不存在则进行后续的网络请求操作
    @Override
    protected File accessFile(File cacheDir, String url){
        if (mTargetFile != null && mTargetFile.exists()) {
            return mTargetFile;
        }
        return null;
    }

    @Override
    protected File fileGet(String url, File file, AjaxStatus status) {
        return file;
    }

    @Override
    protected void copy(InputStream is, OutputStream os, int max, File tempFile, File destFile) throws IOException {
        // 从网络请求的输入流中读取数据
        super.copy(is, os, max, tempFile, destFile);
    }

}
