package com.vip.sdk.videolib.demo;

import android.net.Uri;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;


/**
 * 使用fresco时,对不同类型的图片来源做uri封装
 */
public class FrescoImageUtil {

    public static Uri getUriFromNet(String url) {
        if (TextUtils.isEmpty(url)) {
            return Uri.parse("");
        } else {
            return Uri.parse(url);
        }
    }

    public static Uri getUriFromAsset(String path) {
        if (TextUtils.isEmpty(path)) {
            return Uri.parse("");
        } else {
            return Uri.parse("asset:///" + path);
        }
    }

    public static Uri getUriFromFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return Uri.parse("");
        } else {
            return Uri.parse("file://" + path);
        }
    }

    public static Uri getUriFromRes(int resid) {
        if (resid == -1) {
            return Uri.parse("");
        } else {
            return Uri.parse("res:///" + resid);
        }
    }

    /**
     * 使用渐进式加载网络图片.
     * 只能用于网络图片加载
     *
     * @param simpleDraweeView 图片控件
     * @param url
     */
    public static void displayImgWithProgressive(SimpleDraweeView simpleDraweeView, String url) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(FrescoImageUtil.getUriFromNet(url))
                .setProgressiveRenderingEnabled(true)
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);
    }

    /**
     * 从网络加载图片
     *
     * @param simpleDraweeView
     * @param url
     */
    public static void displayImgFromNetwork(SimpleDraweeView simpleDraweeView, String url) {
        simpleDraweeView.setImageURI(getUriFromNet(url));
    }

}