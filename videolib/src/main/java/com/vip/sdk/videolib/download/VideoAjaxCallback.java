package com.vip.sdk.videolib.download;

import android.net.Uri;
import android.os.Environment;

import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.vip.sdk.videolib.TinyVideoInfo;

import java.io.File;
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
public class VideoAjaxCallback extends AbstractAjaxCallback<Uri, VideoAjaxCallback> {

    private static HashMap<String, WeakHashMap<TinyVideoInfo, VideoAjaxCallback>> queueMap = new HashMap<String, WeakHashMap<TinyVideoInfo, VideoAjaxCallback>>();

    public static File CACHE_DIR;

    public static File getCacheDir(){
//        if (null == CACHE_DIR) {
//            synchronized (VideoAjaxCallback.class) {
//                CACHE_DIR = Environment.get
//            }
//        }
//        return CACHE_DIR;
//        File ext = Environment.getE
//        File tempDir = new File(ext, "aquery/temp");
//        tempDir.mkdirs();
//        if(!tempDir.exists() || !tempDir.canWrite()){
//            return null;
//        }
//        return tempDir;
        return null;
    }


    /**
     * Clear the disk cache.
     */
    public static void clearCache(){

    }

    protected File mTargetFile;
    /**
     * Instantiates a new bitmap ajax callback.
     */
    public VideoAjaxCallback(){
        type(Uri.class).fileCache(true).url("");
    }

    public VideoAjaxCallback file(File file) {
        mTargetFile = file;
        return this;
    }

    @Override
    protected void skip(String url, Uri bm, AjaxStatus status){
        queueMap.remove(url);
    }

    // 是否文件已经存在
    @Override
    protected File accessFile(File cacheDir, String url){
        if (mTargetFile != null && mTargetFile.exists()) {
            return mTargetFile;
        }
        return super.accessFile(cacheDir, url);
    }

    @Override
    protected Uri fileGet(String url, File file, AjaxStatus status) {
        return super.fileGet(url, file, status);
    }

    @Override
    protected Uri transform(String url, byte[] data, AjaxStatus status) {

        if (data != null) {

        }
        return super.transform(url, data, status);
    }
}
