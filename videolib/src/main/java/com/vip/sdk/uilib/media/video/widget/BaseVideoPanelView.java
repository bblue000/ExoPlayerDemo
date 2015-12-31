package com.vip.sdk.uilib.media.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.vip.sdk.uilib.media.video.VIPVideo;
import com.vip.sdk.uilib.media.video.VideoControlCallback;

/**
 * Created by Yin Yong on 15/12/31.
 */
public class BaseVideoPanelView extends RelativeLayout implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, VideoControlCallback {

    public BaseVideoPanelView(Context context) {
        super(context);
    }

    public BaseVideoPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseVideoPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStateChanged(VIPVideo video, int state, VideoStatus status) {

    }

    @Override
    public void onLoadProgress(VIPVideo video, String url, long current, long total) {

    }
}
