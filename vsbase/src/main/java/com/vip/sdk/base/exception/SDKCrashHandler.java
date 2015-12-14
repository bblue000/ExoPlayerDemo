package com.vip.sdk.base.exception;

import android.content.Context;

/**
 * Created by richard.zhao on 2015-04-06.
 */
public class SDKCrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static SDKCrashHandler INSTANCE = new SDKCrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private SDKCrashHandler() {
    }

    public static SDKCrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            //android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
        System.out.println("uncaughtException");

//        new Thread() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                new AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false)
//                        .setMessage("程序崩溃了...").setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        System.exit(0);
//                    }
//                })
//                        .create().show();
//                Looper.loop();
//            }
//        }.start();
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        ex.printStackTrace();

        return true;
    }
}