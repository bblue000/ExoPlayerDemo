package com.vip.sdk.videolib.demo;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.vip.sdk.base.utils.ToastUtils;
import com.vip.sdk.videolib.LoadErrInfo;
import com.vip.sdk.videolib.TinyListController;
import com.vip.sdk.videolib.TinyVideo;
import com.vip.sdk.videolib.demo.entity.MediaListInfo;
import com.vip.test.exoplayerdemo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/17.
 *
 * @since 1.0
 */
public class MediaListAdapter extends BaseAdapter implements TinyListController.TinyListCallback {

    private Context mContext;
    private LayoutInflater mInflater;
    private AQuery mAQuery;
    private List<MediaListInfo> mContent = new ArrayList<MediaListInfo>(20);
    private TinyListController mTinyListController;
    public MediaListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mAQuery = new AQuery(mContext);
    }

    public void setData(MediaListInfo[] data) {
        mContent.clear();
        if (null != data) {
            for (int i = 0; i < data.length; i++) {
                mContent.add(data[i]);
            }
        }
        notifyDataSetInvalidated();
    }

    public void setTinyListController(TinyListController controller) {
        mTinyListController = controller;
        mTinyListController.tinyListCallback(this);
    }

    @Override
    public int getCount() {
        return mContent.size();
    }

    @Override
    public MediaListInfo getItem(int position) {
        return mContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.video_list_item, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        MediaListInfo info = getItem(position);

        holder.name_tv.setText(info.title);

        // 加载预览图片
        holder.overlayPreviewIv.setVisibility(View.VISIBLE);
        mAQuery.id(holder.overlayPreviewIv).image(info.previewImage, true, true);

        holder.overlayPlayIv.setVisibility(View.VISIBLE);

        holder.overlayLoadingPb.setVisibility(View.GONE);

        holder.video.setVisibility(View.GONE);
        holder.video.setTinyController(mTinyListController);
        holder.video.setVideoPath(info.videoUrl);
//        holder.video.setMediaController(new MediaController(mContext));

        holder.video.setStateCallback(new TinyVideo.StateCallback() {
            @Override
            public void onStateChanged(TinyVideo video, int state) {
                switch (state) {
                    case STATE_LOADING:
                        holder.overlayLoadingPb.setVisibility(View.VISIBLE);
                        holder.overlayPlayIv.setVisibility(View.GONE);
                        break;
                    case STATE_START:
                        holder.overlayLoadingPb.setVisibility(View.GONE);
                        holder.overlayPlayIv.setVisibility(View.GONE);
                        holder.overlayPreviewIv.setVisibility(View.GONE);
                        holder.video.setVisibility(View.VISIBLE);
                        break;
                    case STATE_STOP:
                        holder.overlayPreviewIv.setVisibility(View.VISIBLE);
                        holder.video.setVisibility(View.GONE);
                    case STATE_PAUSE:
                        holder.overlayLoadingPb.setVisibility(View.GONE);
                        holder.overlayPlayIv.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onLoadErr(TinyVideo video, LoadErrInfo status) {
                ToastUtils.showToast(status.message);
            }
        });

        holder.video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("yytest", "err {" + what + ", " + extra + "}");
                return true;
            }
        });
        return convertView;
    }

    @Override
    public TinyVideo getTinyVideo(int position, View convertView) {
        return ((ViewHolder) convertView.getTag()).video;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'video_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.video)
        TinyVideo video;
        @InjectView(R.id.overlay_preview_iv)
        ImageView overlayPreviewIv;
        @InjectView(R.id.overlay_play_iv)
        ImageView overlayPlayIv;
        @InjectView(R.id.name_tv)
        TextView name_tv;
        @InjectView(R.id.overlay_loading_pb)
        ProgressBar overlayLoadingPb;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
