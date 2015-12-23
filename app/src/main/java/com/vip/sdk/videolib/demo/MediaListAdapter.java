package com.vip.sdk.videolib.demo;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

    public static final int VIEW_TYPE_IMAGE = 0;
    public static final int VIEW_TYPE_VIDEO = 1;
    public static final int VIEW_TYPE_COUNT = 2;

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
        mAQuery.id(holder.imageIv).image(info.previewImage, true, true, 0, 0, null,
                AQuery.FADE_IN, AQuery.RATIO_PRESERVE);
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
        holder.overlayPreviewIv.setVisibility(View.VISIBLE);
        // mAQuery.id(holder.overlayPreviewIv).image(info.previewImage, true, true);
        mAQuery.id(holder.overlayPreviewIv).image(info.previewImage, true, true, 0, 0, null,
                AQuery.FADE_IN, AQuery.RATIO_PRESERVE);

        holder.overlayPlayIv.setVisibility(View.VISIBLE);
        holder.overlayPauseIv.setVisibility(View.GONE);

        holder.overlayLoadingPb.setVisibility(View.GONE);
        holder.overlayProgressPb.setVisibility(View.GONE);

        holder.video.setTinyController(mTinyListController);
        holder.video.setVideoPath(info.videoUrl);
//        holder.video.setMediaController(new MediaController(mContext));
        // Log.d("yytest", holder.video + "------ getView: " + info.videoUrl);

        holder.video.setStateCallback(new TinyVideo.StateCallback() {
            private Animation mAnim;
            {
                mAnim = new AlphaAnimation(1.0f, 0f);
                mAnim.setDuration(500);
            }
            private CountDownTimer mTimer;

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
                        holder.overlayPreviewIv.startAnimation(mAnim);
                        holder.overlayPreviewIv.setVisibility(View.GONE);
                        holder.overlayPauseIv.setVisibility(View.VISIBLE);
                        holder.overlayProgressPb.setVisibility(View.VISIBLE);
                        holder.overlayProgressPb.setMax(100);
                        if (null != mTimer) {
                            mTimer.cancel();
                        }
                        mTimer = new CountDownTimer(holder.video.getDuration(), 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                holder.overlayProgressPb.setProgress((int)
                                        (((float) holder.video.getCurrentPosition() / (float) holder.video.getDuration()) * 100));
                            }

                            @Override
                            public void onFinish() {

                            }
                        };
                        mTimer.start();
                        break;
                    case STATE_STOP:
                        holder.overlayPreviewIv.setVisibility(View.VISIBLE);
                        holder.overlayProgressPb.setVisibility(View.GONE);
                        if (null != mTimer) {
                            mTimer.cancel();
                        }
                    case STATE_PAUSE:
                        holder.overlayLoadingPb.setVisibility(View.GONE);
                        holder.overlayPauseIv.setVisibility(View.GONE);
                        holder.overlayPlayIv.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onLoadErr(TinyVideo video, LoadErrInfo status) {
                ToastUtils.showToast(status.message);
            }
        });

        holder.overlayPlayIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.video.start();
            }
        });

        holder.overlayPauseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.video.pause();
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
        Object tag = convertView.getTag();
        if (tag instanceof VideoHolder) {
            return ((VideoHolder) tag).video;
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
        ImageView imageIv;

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
        TinyVideo video;
        @InjectView(R.id.overlay_preview_iv)
        ImageView overlayPreviewIv;
        @InjectView(R.id.overlay_play_iv)
        ImageView overlayPlayIv;
        @InjectView(R.id.overlay_pause_iv)
        ImageView overlayPauseIv;
        @InjectView(R.id.name_tv)
        TextView name_tv;
        @InjectView(R.id.overlay_loading_pb)
        ProgressBar overlayLoadingPb;
        @InjectView(R.id.overlay_progress_pb)
        ProgressBar overlayProgressPb;

        VideoHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
