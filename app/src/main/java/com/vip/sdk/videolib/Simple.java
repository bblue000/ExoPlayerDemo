package com.vip.sdk.videolib;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.vip.sdk.videolib.LoadErrInfo;
import com.vip.sdk.videolib.TinyController;
import com.vip.sdk.videolib.TinyVideo;
import com.vip.sdk.videolib.TinyVideoInfo;
import com.vip.test.exoplayerdemo.*;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * something
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/17.
 *
 * @since 1.0
 */
public class Simple extends Activity {

    @InjectView(com.vip.test.exoplayerdemo.R.id.vv)
//    VideoView vv;
    TinyVideo vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.vip.test.exoplayerdemo.R.layout.simple);
        ButterKnife.inject(this);

        vv.setTinyController(new TinyController() {
            @Override
            public void determinePlay() {

            }

            @Override
            public ViewGroup getContainer() {
                return null;
            }

            @Override
            protected void onVideoPrepared(TinyVideoInfo videoInfo, String uri) {

            }

            @Override
            protected void onVideoLoadFailed(TinyVideoInfo info, String uri, LoadErrInfo status) {

            }
        });

        vv.superSetVideoURI(Uri.parse("http://10.101.54.106/public/vip3.mp4"));
        vv.start();

//        vv.setVideoPath("http://10.101.54.106/public/vip3.mp4");
//        vv.start();
    }
}
