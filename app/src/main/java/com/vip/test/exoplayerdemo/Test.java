package com.vip.test.exoplayerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.vip.sdk.uilib.video.controller.SingleVideoController;
import com.vip.sdk.uilib.video.VIPVideo;
import com.vip.sdk.videolib.demo.MediaListManager;

/**
 * something
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/9.
 *
 * @since 1.0
 */
public class Test extends Activity {

    private RelativeLayout rlt;
    private VIPVideo vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);


        rlt = (RelativeLayout) findViewById(R.id.rlt);
        vv = (VIPVideo) findViewById(R.id.vv);

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                vv = new VideoView(Test.this);
//                rlt.addView(vv, new ViewGroup.LayoutParams(-1, -1));
//                vv.setVideoPath("http://192.168.1.107/public/vip1.mp4");
//                vv.start();
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        vv.setVideoPath("http://192.168.1.107/public/vip2.mp4");
//                    }
//                }, 5000L);
//            }
//        }, 1000);

        //vv.setVideoPath("http://192.168.1.111/public/vip1.mp4");
        //vv.start();
        SingleVideoController videoController = new SingleVideoController(vv);
        videoController.setVideoPath("http://" + MediaListManager.IP + "/public/vip1.mp4");

        videoController.start();
        videoController.seekTo(15000);

//        final File file = new File(Environment.getExternalStorageDirectory(), "yytest");
//        vv.setVideoURI(Uri.fromFile(file));
//        vv.start();

//        new AsyncTask<Object , Object, Void>() {
//
//            @Override
//            protected Void doInBackground(Object... params) {
//                //创建okHttpClient对象
//                OkHttpClient mOkHttpClient = new OkHttpClient();
//                //创建一个Request
//                final Request request = new Request.Builder()
//                        .url("http://10.101.54.106/public/vip3.mp4")
//                        .build();
//                //new call
//                Call call = mOkHttpClient.newCall(request);
//                //请求加入调度
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//                    }
//
//                    @Override
//                    public void onResponse(final Response response) throws IOException {
//                        //String htmlStr =  response.body().string();
//                        InputStream ins = response.body().byteStream();
//
//                        if (!file.exists()) {
//                            file.createNewFile();
//                        }
//                        FileOutputStream out = new FileOutputStream(file);
//                        byte[] buf = new byte[4096];
//                        int times = 0;
//                        while (times <= Integer.MAX_VALUE) {
//                            int len = ins.read(buf);
//                            if (len == -1) {
//                                break;
//                            }
//                            out.write(buf, 0, len);
//                            times++;
//                        }
//                        publishProgress((Object[]) null);
//                        out.close();
//                        response.body().close();
//                    }
//                });
//
//                return null;
//            }
//
//            boolean isPlaying;
//            @Override
//            protected void onProgressUpdate(Object... values) {
//                if (isPlaying) {
//
//                } else {
//                    vv.setVideoURI(Uri.fromFile(file));
//                    vv.start();
//                }
//                isPlaying = true;
//            }
//        }.execute();
    }

}
