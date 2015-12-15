package com.vip.test.exoplayerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

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

    @InjectView(R.id.list)
    ListView list;
    private String[] urls = new String[]{
        "http://10.101.54.106/public/vip1.mp4",
        "http://10.101.54.106/public/vip2.mp4",
        "http://10.101.54.106/public/vip3.mp4",

        "http://10.101.54.106/public/vip1.mp4",
        "http://10.101.54.106/public/vip2.mp4",
        "http://10.101.54.106/public/vip3.mp4",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_list);
        ButterKnife.inject(this);

        list = (ListView) findViewById(R.id.list);

        final TinyListController controller = new TinyListController(list);

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
            public View getView(int position, View convertView, ViewGroup parent) {
                if (null == convertView) {
                    convertView = getLayoutInflater().inflate(R.layout.video_list_item, parent, false);
                    convertView.setTag(new ViewHolder(convertView));
                }
                ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.video.setTinyController(controller);
                holder.video.setVideoPath(getItem(position));
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

        public ViewHolder(View convertView) {
            video = (TinyVideo) convertView.findViewById(R.id.video);
        }
    }

}
