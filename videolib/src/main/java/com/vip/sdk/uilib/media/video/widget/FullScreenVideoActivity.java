package com.vip.sdk.uilib.media.video.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.vip.sdk.uilib.media.video.controller.SingleVideoController;
import com.vip.sdk.uilib.video.R;

/**
 * Created by Yin Yong on 15/12/31.
 */
public class FullScreenVideoActivity extends Activity {

    private static Uri sUri;
    public static void startMe(Context context, Uri uri) {
        sUri = uri;
        context.startActivity(new Intent(context, FullScreenVideoActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    protected VideoPanelView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //设置全屏
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

            }
        }, 500L);

        setContentView(R.layout.video_fullscreen);
        mVideoView = (VideoPanelView) findViewById(R.id.video_panel_v);

        SingleVideoController controller = new SingleVideoController(mVideoView.getVideo());
//        mVideoView.setData(controller, sUri, null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sUri = null;
    }
}
