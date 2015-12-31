package com.vip.sdk.uilib.media.video.cache;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.vip.sdk.base.file.FileManagerUtils;
import com.vip.sdk.base.utils.ObjectUtils;
import com.vip.sdk.uilib.media.video.VIPVideoDebug;
import com.vip.sdk.uilib.media.video.VIPVideoToken;
import com.vip.sdk.uilib.media.video.VideoControlCallback;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * {@link #slotSize(long)}控制每下载指定大小进行验证，如果没有等待的视频项，则无需继续下载；
 *
 * <br/>
 *
 * {@link #bufferSize(int)}下载时，每次读取的字节数
 *
 * <p/>
 * <p/>
 *
 * Created by Yin Yong on 15/12/14.
 *
 * @since 1.0
 */
public class VideoAjaxCallback extends AbstractAjaxCallback<File, VideoAjaxCallback> {

    private static final boolean DEBUG = VIPVideoDebug.CACHE;
    private static final String TAG = VIPVideoDebug.TAG;

    public static File CACHE_DIR;
    /**
     * video cache dir
     */
    public static File getVideoCacheDir(Context context){
        if (null == CACHE_DIR) {
            File ext = new File(Environment.getExternalStorageDirectory(), "aquery");
            // File ext = AQUtility.getCacheDir(context);
            CACHE_DIR = new File(ext, "video");
            if (!CACHE_DIR.exists()) {
                CACHE_DIR.mkdirs();
            }
        }
        return CACHE_DIR;
    }

    /**
     * Clear the disk cache.( only video cache)
     */
    public static void clearCache(final Context context) {
        execute(new Runnable() {
            @Override
            public void run() {
                FileManagerUtils.deleteFile(getVideoCacheDir(context), false);
            }
        });
    }

    private static HashMap<String, WeakHashMap<VIPVideoToken, VideoCache.CacheCallback>> queueMap
            = new HashMap<String, WeakHashMap<VIPVideoToken, VideoCache.CacheCallback>>(4);

    private static WeakHashMap<VIPVideoToken, String> videoMap = new WeakHashMap<VIPVideoToken, String>(4);

    /**
     * 下载
     */
    public static void download(VIPVideoToken token, VideoCache.CacheCallback callback) {
        final Uri uri = token.uri;
        final String url = String.valueOf(uri);

        // 文件不存在，则需要下载
        synchronized (queueMap) {
            checkDiffUrlForVideoToken(url, token);

            // 先检查目标文件是否已经存在，如果已经存在就直接返回吧，更快地得到相应
            File targetFile = AQUtility.getCacheFile(getVideoCacheDir(token.video.getContext()), url);
            if (checkMayExistTargetFile(url, token, targetFile)) {
                // 如果文件已经存在，直接返回
                callback(url, token, targetFile, null, callback);
                // callback.onSuccess(tinyVideoInfo, url, Uri.fromFile(targetFile));
                return ;
            }

            if (!queueMap.containsKey(url)) {
                addQueue(url, token, callback);
                new VideoAjaxCallback()
                        .url(url)
                        .videoToken(token)
                        .targetFile(targetFile)
                        .loadCallback(callback)
                        .async(token.video.getContext());
            } else {
                addQueue(url, token, callback);
            }
        }
    }

    /**
     * 检查是否已存在目标文件
     */
    protected static boolean checkMayExistTargetFile(String url, VIPVideoToken token,
                                                     File targetFile) {
        return null != targetFile && (FileManagerUtils.exists(targetFile) && targetFile.length() > 0) ;
    }

    protected static boolean checkDiffUrlForVideoToken(String url, VIPVideoToken token) {
        String oldUrl = videoMap.get(token);
        if (DEBUG) Log.w(TAG , "old url = " + oldUrl);
        if (DEBUG) Log.e(TAG, "new url = " + url);
        if (!ObjectUtils.equals(oldUrl, url)) { // 如果url改变了
            Map<?, ?> urlVideoMap = queueMap.get(oldUrl);
            if (null != urlVideoMap) {
                urlVideoMap.remove(token);
            }
            videoMap.put(token, url);
            return true;
        }
        return false;
    }

    /**
     * 尽量在主线程调用该方法
     */
    protected static void addQueue(String url, VIPVideoToken token,
                                   VideoCache.CacheCallback callback) {
        WeakHashMap<VIPVideoToken, VideoCache.CacheCallback> vs = queueMap.get(url);
        if (vs == null) {
            if (queueMap.containsKey(url)) { // 如果有其他的相同请求过来，加入map中
                if (DEBUG) Log.e(TAG , "count + 1, url = " + url);
                vs = new WeakHashMap<VIPVideoToken, VideoCache.CacheCallback>();
                vs.put(token, callback);
                queueMap.put(url, vs);
            } else {
                if (DEBUG) Log.e(TAG , "first one, url = " + url);
                // 仅仅注册这个url，说明已经有下载队列了
                queueMap.put(url, null);
            }
        } else {
            //add to list of image views
            if (DEBUG) Log.e(TAG , "count + 1, url = " + url);
            vs.put(token, callback);
        }
    }

    /**
     * 检查是否还需要继续下载。
     *
     * <br/>
     *
     * 如下几种情形是需要检查的：
     * <ul>
     *     <li>执行前（同时执行数有限，会在队列中缓存，缓存的项开始执行时检查一下）</li>
     *     <li>执行过程中，按照指定的频率轮询</li>
     *     <li>...</li>
     * </ul>
     */
    protected static boolean checkNeedGoon(String url, VIPVideoToken token) {
        synchronized (queueMap) {
            if (queueMap.containsKey(url)) {
                Map<?, ?> urlVideoMap = queueMap.get(url);
                if (null == urlVideoMap || urlVideoMap.isEmpty()) { // 没有其他等待项
                    if (null == token || !token.matchUri(url)) { // 当前等待项的URL已改变
                        return false;
                    }
                }
            }
            return true;
        }
    }

    protected static boolean isBeakPointDownload(HttpResponse response) {
        if (null == response) return false;
        Header[] headers = response.getAllHeaders();
        if (null == headers || headers.length == 0) return false;
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            if ("Content-Range".equalsIgnoreCase(header.getName())) return true;
        }
        return false;
    }

    // 每下载一段，进行一次检查
    protected int mBufferSize = 4 * 1024;
    protected long mSlotSize = 10 * 1024;
    protected WeakReference<VIPVideoToken> mToken;
    protected VideoCache.CacheCallback mCallback;

    private File mTempFile;
    private File mTargetFile;
    protected VideoAjaxCallback(){
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

    public VideoAjaxCallback videoToken(VIPVideoToken token) {
        mToken = new WeakReference<VIPVideoToken>(token);
        return this;
    }

    public VideoAjaxCallback loadCallback(VideoCache.CacheCallback callback) {
        mCallback = callback;
        return this;
    }

    @Override
    public VideoAjaxCallback targetFile(File file) {
        mTargetFile = file;
        return super.targetFile(file).fileCache(false /*自己判断*/);
    }

    @Override
    public void async(Context context) {
        if (DEBUG) Log.d(TAG , "pending enqueue = " + getUrl());
        super.async(context);
    }

    @Override
    public void run() {
        String url = getUrl();
        VIPVideoToken token = mToken.get();

        // 有可能在队列中存在时间较长，执行时检查是否要直接return
        if (DEBUG) Log.d(TAG , "执行前检测....");
        if (!checkNeedGoon(url, token)) {
            if (DEBUG) Log.w(TAG , "执行前检测 cancel = " + url);
            callback(url, null, status.code(-1).message("canceled; no pending request"));
            return;
        }

        // 这边处理已有文件缓存的逻辑，不依赖于AQuery的文件缓存的处理方式
        if (checkMayExistTargetFile(url, token, mTargetFile)) {
            if (DEBUG) Log.w(TAG , "执行前检测 target file exists = " + url);
            callback(url, mTargetFile, status.code(200).message("OK"));
            return ;
        }

        // 根据目标文件，判断临时文件，放到这边，有IO操作也不怕
        if (DEBUG) Log.d(TAG , "target file ? " + mTargetFile.exists() + ", " + mTargetFile);
        mTempFile = new File(mTargetFile + ".tmp");
        if (DEBUG) Log.d(TAG , "temp file ? " + mTempFile.exists() + ", " + mTempFile);
        if (FileManagerUtils.exists(mTempFile)) {
            // 临时文件仍存在，删除目标文件
            FileManagerUtils.deleteFile(mTargetFile, true);

            // 如果临时文件仍存在，则加入断点续传的字段
            long size = mTempFile.length();
            header("Range", "bytes=" + size + "-");
            if (DEBUG) Log.d(TAG , "temp file size = " + size);
        }

        if (null != token) {
            headersAppend(token.headers);
        }

        if (DEBUG) Log.d(TAG , "pending load = " + getUrl());
        super.run();
    }

    // 用AQuery这套改写真烦躁。。。
    @Override
    protected File getPreFile() {
        return mTargetFile;
    }

    @Override
    protected File makeTempFile(File file) throws IOException {
        if (!mTempFile.exists()) {
            mTempFile.createNewFile();
        }
        return mTempFile;
    }

    @Override
    protected OutputStream makeTempFileOutput(File tempFile, HttpClient client, HttpUriRequest hr,
                                              HttpResponse response) throws IOException {
        boolean isBreakPointSupport = isBeakPointDownload(response);
        if (DEBUG) Log.d(TAG , "makeTempFileOutput isBreakPointSupport = " + isBreakPointSupport);
        return new BufferedOutputStream(new FileOutputStream(tempFile, isBreakPointSupport));
    }

    @Override
    protected void copy(InputStream is, OutputStream os, int max, File tempFile, File destFile,
                        HttpClient client, HttpUriRequest hr, HttpResponse response) throws IOException {
        final long lastFileSize = FileManagerUtils.caculateFileSize(tempFile);
        // 从网络请求的输入流中读取数据
        try {
            byte[] b = new byte[mBufferSize];

            int lastSlot = 0;
            int readCount = 0;

            int read;

            while((read = is.read(b)) != -1){
                os.write(b, 0, read);
                readCount += read;
                // publish progress
                publishProgress(getUrl(), mToken.get(), mCallback, readCount + lastFileSize, max + lastFileSize);

                if (readCount - lastSlot >= mSlotSize) {
                    // 进行一次检测
                    if (DEBUG) Log.d(TAG , "检测....");
                    if (!checkNeedGoon(getUrl(), mToken.get())) {
                        if (DEBUG) Log.w(TAG , "检测 cancel = " + getUrl());
                        return ; // 如果没有等待当前url下载结果的项了，则直接返回，不再下载
                    }
                    lastSlot = readCount;
                }
            }

            tempFile.renameTo(destFile);
        } catch (IOException e) {
            if (DEBUG) Log.d(TAG , "copy failed, deleting files");

            //copy is a failure, delete everything but temp file
            // tempFile.delete();
            FileManagerUtils.deleteFile(destFile, true);
            throw e;
        } finally {
            AQUtility.close(os);

            abortRequest(client, hr, response);

            AQUtility.close(is);
        }
    }

    protected void abortRequest(HttpClient client, HttpUriRequest hr, HttpResponse response) {
        // abort request
        if (null != hr) {
            try {
                hr.abort();
            } catch (Exception e) { }
        }
        // consume entity content
        if (null != response) {
            try {
                response.getEntity().consumeContent();
            } catch (Exception e) { }
        }
    }

    @Override
    public void callback(String url, File object, AjaxStatus status) {
        if (DEBUG) Log.d(TAG , "callback result = " + object);

        VIPVideoToken myToken = mToken.get();
        callback(url, myToken, object, status, mCallback);
    }

    @Override
    protected void skip(String url, File bm, AjaxStatus status){
        queueMap.remove(url);
    }

    // 公共方法封装
    protected static void publishProgress(String url, VIPVideoToken token,
                                          VideoCache.CacheCallback cb,
                                          long current, long total) {
        if (null == token || null == cb) return;

        if (DEBUG) Log.d(TAG, "publishProgress ("+ current + ", " + total + ")");
        cb.onCacheProgress(token, url, Math.max(0, current), Math.max(0, total));
    }

    protected static void callback(String url, VIPVideoToken myToken,
                                   File target, AjaxStatus status,
                                   VideoCache.CacheCallback myCallback) {
        synchronized (queueMap) {
            WeakHashMap<VIPVideoToken, VideoCache.CacheCallback> ivs = queueMap.remove(url);
            videoMap.remove(myToken);

            //check if view queue already contains first view
            if (null == ivs || !ivs.containsKey(myToken)) {
                checkCb(url, myToken, target, status, myCallback);
            }

            if (null != ivs) {
                Set<VIPVideoToken> set = ivs.keySet();
                for (VIPVideoToken token : set) {
                    VideoCache.CacheCallback cb = ivs.get(token);
                    checkCb(url, token, target, status, cb);
                }
            }
        }
    }

    protected static void checkCb(String url, VIPVideoToken token,
                                  File target, AjaxStatus status,
                                  VideoCache.CacheCallback cb) {
        checkCb(url, token, null == target ? null : Uri.fromFile(target), status, cb);
    }

    protected static void checkCb(String url, VIPVideoToken token,
                                  Uri target, AjaxStatus status,
                                  VideoCache.CacheCallback cb) {
        if (null == token || null == cb) return;

        if (DEBUG) Log.d(TAG, "checkCb matchUri = " + token.matchUri(url));
        if (token.matchUri(url)) { // 这边只检查是否是相同的url，不对是否还被管理进行判断
            if (null != target) {
                cb.onCacheSuccess(token, url, target);
            } else {
                cb.onCacheFailed(token, url, new VideoControlCallback.VideoStatus(status.getCode(), status.getMessage()));
            }
        }
    }

}
