package com.vip.sdk.videolib.download;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.vip.sdk.base.file.FileManagerUtils;
import com.vip.sdk.base.utils.ObjectUtils;
import com.vip.sdk.videolib.LoadErrInfo;
import com.vip.sdk.videolib.TinyVideoInfo;

import org.apache.http.Header;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 *
 *
 * <p/>
 * <p/>
 *
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public class VideoAjaxCallback extends AbstractAjaxCallback<File, VideoAjaxCallback> {

    private static final boolean DEBUG = true;

    private static HashMap<String, WeakHashMap<TinyVideoInfo, TinyDownloader.TinyDownloadCallback>> queueMap
            = new HashMap<String, WeakHashMap<TinyVideoInfo, TinyDownloader.TinyDownloadCallback>>(4);

    private static WeakHashMap<TinyVideoInfo, String> videoMap = new WeakHashMap<TinyVideoInfo, String>(4);

    public static File CACHE_DIR;
    /**
     * 缓存文件夹
     */
    public static File getVideoCacheDir(Context context){
        if (null == CACHE_DIR) {
            File ext = Environment.getExternalStorageDirectory(); //AQUtility.getCacheDir(context)
            CACHE_DIR = new File(ext, "aquery/video");
            if (!CACHE_DIR.exists()) {
                CACHE_DIR.mkdirs();
            }
        }
        return CACHE_DIR;
    }

    /**
     * Clear the disk cache.
     */
    public static void clearCache(final Context context) {
        execute(new Runnable() {
            @Override
            public void run() {
                FileManagerUtils.deleteFile(getVideoCacheDir(context), false);
            }
        });
    }

    public static void download(TinyVideoInfo tinyVideoInfo, TinyDownloader.TinyDownloadCallback callback) {
        if (null == tinyVideoInfo || null == tinyVideoInfo.uri) {
            callback.onFailed(tinyVideoInfo, "", new LoadErrInfo(-1, "null"));
            return;
        }

        String url = String.valueOf(tinyVideoInfo.uri);
        synchronized (queueMap) {
            String oldUrl = videoMap.get(tinyVideoInfo);
            if (DEBUG) Log.w("yytest" , "old url = " + oldUrl);
            if (!ObjectUtils.equals(oldUrl, url)) { // 如果url改变了
                Map<?, ?> urlVideoMap = queueMap.get(oldUrl);
                if (null != urlVideoMap) {
                    urlVideoMap.remove(tinyVideoInfo);
                }
                videoMap.put(tinyVideoInfo, url);
            }

            if (!queueMap.containsKey(url)) {
                addQueue(url, tinyVideoInfo, callback);
                new VideoAjaxCallback()
                        .url(url)
                        .videoInfo(tinyVideoInfo)
                        .downloadCallback(callback)
                        .async(tinyVideoInfo.video.getContext());
            } else {
                addQueue(url, tinyVideoInfo, callback);
            }
        }
    }

    /**
     * 尽量在主线程调用该方法
     */
    protected static void addQueue(String url, TinyVideoInfo tinyVideoInfo,
                                   TinyDownloader.TinyDownloadCallback callback) {
        WeakHashMap<TinyVideoInfo, TinyDownloader.TinyDownloadCallback> vs = queueMap.get(url);
        if (vs == null) {
            if (queueMap.containsKey(url)) { // 如果有其他的相同请求过来，加入map中
                if (DEBUG) Log.e("yytest" , "count + 1, url = " + url);
                vs = new WeakHashMap<TinyVideoInfo, TinyDownloader.TinyDownloadCallback>();
                vs.put(tinyVideoInfo, callback);
                queueMap.put(url, vs);
            } else {
                if (DEBUG) Log.e("yytest" , "first one, url = " + url);
                // 仅仅注册这个url，说明已经有下载队列了
                queueMap.put(url, null);
            }
        } else {
            //add to list of image views
            vs.put(tinyVideoInfo, callback);
        }
    }

    /**
     * 检查是否还需要继续下载
     */
    protected static boolean checkNeedGoon(String url, TinyVideoInfo tinyVideoInfo) {
        synchronized (queueMap) {
            if (queueMap.containsKey(url)) {
                Map<?, ?> urlVideoMap = queueMap.get(url);
                if ((null == urlVideoMap || urlVideoMap.isEmpty())
                        && (null == tinyVideoInfo || !tinyVideoInfo.matchUri(url))) {
                    return false;
                }
            }
            return true;
        }
    }

    // 每下载一段，进行一次检查
    protected int mBufferSize = 4 * 1024;
    protected long mSlotSize = 10 * 1024;
    protected WeakReference<TinyVideoInfo> mTinyVideoInfo;
    protected TinyDownloader.TinyDownloadCallback mCallback;

    private File mTempFile;
    private File mTargetFile;
    public VideoAjaxCallback(){
        type(File.class).url("");
    }

    public VideoAjaxCallback slotSize(long slotSize) {
        mSlotSize = slotSize;
        return this;
    }

    public VideoAjaxCallback bufferSize(int bufferSize) {
        mBufferSize = bufferSize;
        return this;
    }

    public VideoAjaxCallback videoInfo(TinyVideoInfo videoInfo) {
        mTinyVideoInfo = new WeakReference<TinyVideoInfo>(videoInfo);
        return this;
    }

    public VideoAjaxCallback downloadCallback(TinyDownloader.TinyDownloadCallback callback) {
        mCallback = callback;
        return this;
    }

    @Override
    public void async(Context context) {
        if (DEBUG) Log.d("yytest" , "pending load = " + getUrl());

        mTargetFile = AQUtility.getCacheFile(getVideoCacheDir(context), getUrl());

        if (DEBUG) Log.d("yytest" , "target file ? " + mTargetFile.exists() + ", " + mTargetFile);
        mTempFile = new File(mTargetFile + ".tmp");
        if (DEBUG) Log.d("yytest" , "temp file ? " + mTempFile.exists() + ", " + mTempFile);
        targetFile(mTargetFile).fileCache(true);
        if (FileManagerUtils.exists(mTempFile)) {
            // 临时文件仍存在，删除目标文件
            FileManagerUtils.deleteFile(mTargetFile, true);

            // 如果临时文件仍存在，则加入断点续传的字段
            long size = mTempFile.length();
            header("Range", "bytes=" + size + "-");
            if (DEBUG) Log.d("yytest" , "temp file size = " + size);
        }

        TinyVideoInfo info = mTinyVideoInfo.get();
        if (null != info) {
            headersAppend(info.headers);
        }

        super.async(context);
    }

    @Override
    protected File accessFile(File cacheDir, String url) {
        if (FileManagerUtils.exists(mTargetFile) && mTargetFile.length() > 0) {
            return mTargetFile;
        }
        return null;
    }

    @Override
    protected File makeTempFile(File file) throws IOException {
        if (!mTempFile.exists()) {
            mTempFile.createNewFile();
        }
        return mTempFile;
    }

    @Override
    protected OutputStream makeTempFileOutput(File tempFile) throws IOException {
        boolean isBreakPointSupport = false;
        List<Header> headers = status.getHeaders();
        if (null != headers && !headers.isEmpty()) {
            for (int i = 0; i < headers.size(); i++) {
                Header header = headers.get(0);
                if ("Content-Range".equalsIgnoreCase(header.getName())) {
                    isBreakPointSupport = true;
                    break;
                }
            }
        }
        if (DEBUG) Log.d("yytest" , "makeTempFileOutput isBreakPointSupport = " + isBreakPointSupport);
        return new BufferedOutputStream(new FileOutputStream(tempFile, isBreakPointSupport));
    }

    @Override
    protected void copy(InputStream is, OutputStream os, int max, File tempFile, File destFile) throws IOException {
        // 从网络请求的输入流中读取数据
        try {
            byte[] b = new byte[mBufferSize];

            int lastSlot = 0;
            int readCount = 0;

            int read;

            while((read = is.read(b)) != -1){
                os.write(b, 0, read);
                readCount += read;
                if (readCount - lastSlot >= mSlotSize) {
                    lastSlot = readCount;
                    // 进行一次检测，如果

                    if (DEBUG) Log.d("yytest" , "检测....");
                    if (!checkNeedGoon(getUrl(), mTinyVideoInfo.get())) {
                        if (DEBUG) Log.w("yytest" , "检测 cancel = " + getUrl());
                        return ; // 如果没有等待当前url下载结果的项了，则直接返回，不再下载
                    }
                }
            }

            tempFile.renameTo(destFile);
        } catch (IOException e) {
            AQUtility.debug("copy failed, deleting files");

            //copy is a failure, delete everything
            // tempFile.delete();
            FileManagerUtils.deleteFile(destFile, true);
            throw e;
        } finally {
            AQUtility.close(is);
            AQUtility.close(os);
        }
    }

    @Override
    public void callback(String url, File object, AjaxStatus status) {
        if (DEBUG) Log.d("yytest" , "callback result = " + object);

        TinyVideoInfo myInfo = mTinyVideoInfo.get();

        synchronized (queueMap) {
            WeakHashMap<TinyVideoInfo, TinyDownloader.TinyDownloadCallback> ivs = queueMap.remove(url);
            videoMap.remove(myInfo);

            //check if view queue already contains first view
            if (ivs == null || !ivs.containsKey(myInfo)) {
                 checkCb(mCallback, url, myInfo, object, status);
            }

            if (ivs != null) {
                Set<TinyVideoInfo> set = ivs.keySet();
                for (TinyVideoInfo info : set) {
                    TinyDownloader.TinyDownloadCallback cb = ivs.get(info);
                    checkCb(cb, url, info, object, status);
                }
            }
        }
    }

    @Override
    protected void skip(String url, File bm, AjaxStatus status){
        queueMap.remove(url);
    }

    private void checkCb(TinyDownloader.TinyDownloadCallback cb, String url, TinyVideoInfo info,
                         File target, AjaxStatus status) {
        if (info == null || cb == null) return;

        if (DEBUG) Log.d("yytest" , "checkCb matchUri = " + info.matchUri(url));
        if (info.matchUri(url)) {
            if (null != target) {
                cb.onSuccess(info, url, Uri.fromFile(target));
            } else {
                cb.onFailed(info, url, new LoadErrInfo(status.getCode(), status.getMessage()));
            }
        }
    }

}
