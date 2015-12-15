package com.vip.test.exoplayerdemo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.MemoryFile;
import android.widget.VideoView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * something
 * <p/>
 * <p/>
 * Created by Yin Yong on 15/12/9.
 *
 * @since 1.0
 */
public class Test extends Activity {

    private VideoView vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);


        vv = (VideoView) findViewById(R.id.vv);

        final File file = new File(Environment.getExternalStorageDirectory(), "yytest.mp4");
//        vv.setVideoURI(Uri.fromFile(file));
//        vv.start();

        new AsyncTask<Object , Object, Void>() {

            @Override
            protected Void doInBackground(Object... params) {
                //创建okHttpClient对象
                OkHttpClient mOkHttpClient = new OkHttpClient();
                //创建一个Request
                final Request request = new Request.Builder()
                        .url("http://10.101.54.106/public/vip3.mp4")
                        .build();
                //new call
                Call call = mOkHttpClient.newCall(request);
                //请求加入调度
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                    }

                    @Override
                    public void onResponse(final Response response) throws IOException {
                        //String htmlStr =  response.body().string();
                        InputStream ins = response.body().byteStream();

                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream out = new FileOutputStream(file);
                        byte[] buf = new byte[2048];
                        int times = 0;
                        while (times <= 200) {
                            int len = ins.read(buf);
                            if (len == -1) {
                                break;
                            }
                            out.write(buf, 0, len);
                            times++;
                        }
                        publishProgress((Object[]) null);
                        out.close();
                        response.body().close();
                    }
                });

                return null;
            }

            boolean isPlaying;
            @Override
            protected void onProgressUpdate(Object... values) {
                if (isPlaying) {

                } else {
                    vv.setVideoURI(Uri.fromFile(file));
                    vv.start();
                }
                isPlaying = true;
            }
        }.execute();
    }

}