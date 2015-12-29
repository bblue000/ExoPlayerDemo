package com.vip.test.exoplayerdemo;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.vip.sdk.base.BaseApplication;

/**
 * something
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/16.
 *
 * @since 1.0
 */
public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .build();
        Fresco.initialize(this, imagePipelineConfig);
    }
}
