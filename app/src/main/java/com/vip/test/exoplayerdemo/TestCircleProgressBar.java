package com.vip.test.exoplayerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.vip.sdk.uilib.video.widget.CircleProgressBar;

/**
 * something
 * <p/>
 * <p/>
 * Created by Yin Yong on 16/1/4.
 *
 * @since 1.0
 */
public class TestCircleProgressBar extends Activity {

    public int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);

        final CircleProgressBar progressBar = (CircleProgressBar) findViewById(R.id.pb);
        progressBar.setProgress(0);

        final Handler handler =
        new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.smoothSetProgress(progress += 100);
                handler.postDelayed(this, 200);
            }
        }, 1000);
    }
}
