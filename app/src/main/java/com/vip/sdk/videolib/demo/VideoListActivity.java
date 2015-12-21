package com.vip.sdk.videolib.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ListView;

import com.vip.sdk.videolib.TinyListController;
import com.vip.sdk.videolib.demo.entity.MediaListInfo;
import com.vip.test.exoplayerdemo.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * something
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/15.
 *
 * @since 1.0
 */
public class VideoListActivity extends Activity {
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @InjectView(R.id.list)
    ListView list;

    private TinyListController mController;

    private MediaListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_list);
        ButterKnife.inject(this);

        initView();
        request();
    }

    public void initView() {
        mController = new TinyListController(list);

        list.setOnScrollListener(mController);

        mAdapter = new MediaListAdapter(this);
        mAdapter.setTinyListController(mController);

        list.setAdapter(mAdapter);
    }

    public void request() {
        MediaListManager.request(this, new MediaListManager.Callback() {
            @Override
            public void onSuccess(MediaListInfo[] object) {
                mAdapter.setData(object);
                mController.determinePlay();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mController) {
            mController.destroy();
        }
    }
}
