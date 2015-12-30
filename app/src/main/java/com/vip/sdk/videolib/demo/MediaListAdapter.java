package com.vip.sdk.videolib.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vip.sdk.uilib.media.video.VideoListController;
import com.vip.sdk.uilib.media.video.VIPVideo;
import com.vip.sdk.uilib.media.video.widget.VideoPanelView;
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
public class MediaListAdapter extends BaseAdapter implements VideoListController.VideoListCallback {

    public static final int VIEW_TYPE_IMAGE = 0;
    public static final int VIEW_TYPE_VIDEO = 1;
    public static final int VIEW_TYPE_COUNT = 2;

    private Context mContext;
    private LayoutInflater mInflater;
    private AQuery mAQuery;
    private List<MediaListInfo> mContent = new ArrayList<MediaListInfo>(20);
    private VideoListController mVideoListController;

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

    public void setListVideoController(VideoListController controller) {
        mVideoListController = controller;
        mVideoListController.videoListCallback(this);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        MediaListInfo mediaListInfo = getItem(position);
        return getItemViewType(mediaListInfo);
    }

    protected int getItemViewType(MediaListInfo mediaListInfo) {
        switch (mediaListInfo.type) {
            case MediaListInfo.TYPE_VIDEO:
                return VIEW_TYPE_VIDEO;
            case MediaListInfo.TYPE_IMAGE:
            default:
                return VIEW_TYPE_IMAGE;
        }
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
        MediaListInfo mediaListInfo = getItem(position);
        switch (getItemViewType(mediaListInfo)) {
            case VIEW_TYPE_VIDEO:
                return getVideoView(position, mediaListInfo, convertView, parent);
            case VIEW_TYPE_IMAGE:
                return getImageView(position, mediaListInfo, convertView, parent);
        }
        return null;

    }

    protected View getImageView(int position, MediaListInfo info, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.image_list_item, parent, false);
            convertView.setTag(new ImageHolder(convertView));
        }
        final ImageHolder holder = (ImageHolder) convertView.getTag();
        FrescoImageUtil.displayImgFromNetwork(holder.imageIv, info.previewImage);
        return convertView;
    }

    protected View getVideoView(int position, MediaListInfo info, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.video_list_item, parent, false);
            convertView.setTag(new VideoHolder(convertView));
        }

        final VideoHolder holder = (VideoHolder) convertView.getTag();

        holder.name_tv.setText(info.title);

        // 加载预览图片
        holder.video.setData(mVideoListController, info.videoUrl, info.previewImage);
        return convertView;
    }

    @Override
    public VIPVideo getTinyVideo(int position, View convertView) {
        Object tag = convertView.getTag();
        if (tag instanceof VideoHolder) {
            return ((VideoHolder) tag).video.getVideo();
        }
        return null;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'image_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ImageHolder {
        @InjectView(R.id.image_iv)
        SimpleDraweeView imageIv;

        ImageHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'video_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class VideoHolder {
        @InjectView(R.id.video)
        VideoPanelView video;
        @InjectView(R.id.name_tv)
        TextView name_tv;

        VideoHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
