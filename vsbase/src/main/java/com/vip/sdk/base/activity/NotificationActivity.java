package com.vip.sdk.base.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by iceman.xu on 2014/10/27.
 * 封装了一些通用的代码,用来简化绑定广播,接收消息的功能
 */
public abstract class NotificationActivity extends FragmentActivity {
    protected MyBroadCastReceiver mReceiver;

    protected class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadCastReceiver(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        String[] actions = listReceiveActions();
        for (String s : actions) {
            filter.addAction(s);
        }
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mReceiver, filter);
    }

    protected abstract void onBroadCastReceiver(Intent intent);

    protected abstract String[] listReceiveActions();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

}
