package com.vip.sdk.videolib.demo;

import android.content.Context;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.vip.sdk.videolib.demo.entity.MediaListInfo;

/**
 * something
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/17.
 *
 * @since 1.0
 */
public class MediaListManager {

    public interface Callback {
        void onSuccess(MediaListInfo[] object) ;
    }

    // 10.101.54.106 // 192.168.1.105
//    $data = array();
//    for ($i=0; $i < 16; $i++) {
//        # code...
//        array_push($data, array(
//                'type'  		=> 1,
//                'title' 		=> '测试视频标题'.($i + 1),
//                'previewImage' 	=> 'http://10.101.54.106/public/vip'.($i % 5).'.jpg',
//                'videoUrl' 		=> 'http://10.101.54.106/public/vip'.($i + 1).'.mp4',
//        )
//        );
//    }
//
//    echo json_encode($data);

    public static void request(Context context, final Callback callback) {
        new AQuery().ajax("http://192.168.1.107/test", MediaListInfo[].class, new AjaxCallback<MediaListInfo[]>() {
            @Override
            public void callback(String url, MediaListInfo[] object, AjaxStatus status) {
                callback.onSuccess(object);
            }
        });
    }

}
