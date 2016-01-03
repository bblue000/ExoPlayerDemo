package com.vip.sdk.uilib.media.video.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import com.vip.sdk.uilib.media.video.controller.SimpleVideoController;
import com.vip.sdk.uilib.video.R;

/**
 * Created by Yin Yong on 15/12/31.
 */
public class FullScreenVideoActivity extends Activity {

    public static final String EXTRA_URI = "uri";
    public static final String EXTRA_AUTOPLAY = "autoPlay";
    public static final String EXTRA_POSITION = "position";

    public interface Callback {

    }
    public static void start(Context context, Uri uri, Callback callback) {
        context.startActivity(new Intent(context, FullScreenVideoActivity.class)
                .putExtra(EXTRA_URI, uri)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void start(Context context, Uri uri, boolean autoPlay, Callback callback) {
        context.startActivity(new Intent(context, FullScreenVideoActivity.class)
                .putExtra(EXTRA_URI, uri)
                .putExtra(EXTRA_AUTOPLAY, autoPlay)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void start(Context context, Uri uri, boolean autoPlay, int position, Callback callback) {
        context.startActivity(new Intent(context, FullScreenVideoActivity.class)
                .putExtra(EXTRA_URI, uri)
                .putExtra(EXTRA_AUTOPLAY, autoPlay)
                .putExtra(EXTRA_POSITION, position)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    protected FullScreenVideoPanelView mVideoPanel;

    private Uri mUri;
    private boolean mAutoPlay;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //设置全屏
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
//
//            }
//        }, 500L);

        mUri = getIntent().getParcelableExtra(EXTRA_URI);
        if (null == mUri) {
            finish();
            return;
        }
        mAutoPlay = getIntent().getBooleanExtra(EXTRA_POSITION, false);
        mPosition = getIntent().getIntExtra(EXTRA_POSITION, 0);

        setContentView(R.layout.fullscreen_video_activity);

        mVideoPanel = (FullScreenVideoPanelView) findViewById(R.id.video_panel_v);

        SimpleVideoController controller = new SimpleVideoController();

        controller.setVideoURI(mVideoPanel.getVideo(), mUri);

        if (mPosition > 0) {
            controller.seekTo(mVideoPanel.getVideo(), mPosition);
        }

        if (mAutoPlay) {
            controller.start(mVideoPanel.getVideo());
            // mVideoPanel.start();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
