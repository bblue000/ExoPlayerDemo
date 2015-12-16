package com.vip.test.exoplayerdemo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vip.sdk.base.utils.ToastUtils;
import com.vip.sdk.videolib.LoadErrInfo;
import com.vip.sdk.videolib.TinyListController;
import com.vip.sdk.videolib.TinyVideo;

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

    public static final String IP = "192.168.1.105"; // 10.101.54.106 // 192.168.1.105

    @InjectView(R.id.list)
    ListView list;
    private String[] urls = new String[]{
        "http://" + IP + "/public/vip1.mp4",
        "http://" + IP + "/public/vip2.mp4",
        "http://" + IP + "/public/vip3.mp4",
        "http://" + IP + "/public/vip4.mp4",
        "http://" + IP + "/public/vip5.mp4",
        "http://" + IP + "/public/vip6.mp4",
        "http://" + IP + "/public/vip7.mp4",
        "http://" + IP + "/public/vip8.mp4",
        "http://" + IP + "/public/vip9.mp4",
        "http://" + IP + "/public/vip10.mp4",
        "http://" + IP + "/public/vip11.mp4",
        "http://" + IP + "/public/vip12.mp4",
        "http://" + IP + "/public/vip13.mp4",
        "http://" + IP + "/public/vip14.mp4",
        "http://" + IP + "/public/vip15.mp4",
        "http://" + IP + "/public/vip16.mp4",
    };

    private TinyListController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_list);
        ButterKnife.inject(this);

        list = (ListView) findViewById(R.id.list);

        controller = new TinyListController(list);

        list.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return urls.length;
            }

            @Override
            public String getItem(int position) {
                return urls[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (null == convertView) {
                    convertView = getLayoutInflater().inflate(R.layout.video_list_item, parent, false);
                    convertView.setTag(new ViewHolder(convertView));
                }
                final ViewHolder holder = (ViewHolder) convertView.getTag();

                String url = getItem(position);

                holder.index_tv.setText(url.substring(url.lastIndexOf('/')));

//                holder.video.setTinyController(controller);
//                holder.video.setVideoPath(url);

                holder.video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.e("yytest", "err {" + what + ", " + extra + "}");
                        return true;
                    }
                });

                holder.video.setStateCallback(new TinyVideo.SimpleStateCallback() {

                    @Override
                    public void onLoadErr(TinyVideo video, LoadErrInfo status) {
                        ToastUtils.showToast(status.message);
                    }

                    @Override
                    public void onStateChanged(TinyVideo video, int state) {
                        if (video.isPlaying()) {
                            holder.play_iv.setImageResource(android.R.drawable.ic_media_pause);
                        } else {
                            holder.play_iv.setImageResource(android.R.drawable.ic_media_play);
                        }
                    }
                });

                return convertView;
            }
        });

        list.setOnScrollListener(controller);

        controller.tinyListCallback(new TinyListController.TinyListCallback() {
            @Override
            public TinyVideo getTinyVideo(int position, View convertView) {
                return ((ViewHolder) convertView.getTag()).video;
            }
        });
    }

    class ViewHolder {
        TinyVideo video;
        ImageView play_iv;
        TextView index_tv;

        public ViewHolder(View convertView) {
            video = (TinyVideo) convertView.findViewById(R.id.video);
            play_iv = (ImageView) convertView.findViewById(R.id.overlay_play_iv);
            index_tv = (TextView) convertView.findViewById(R.id.index_tv);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != controller) {
            controller.destroy();
        }
    }
}
