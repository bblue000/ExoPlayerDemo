package com.vip.sdk.base;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.vip.sdk.base.exception.SDKCrashHandler;
import com.vip.sdk.base.utils.BaseConfig;

import java.lang.reflect.InvocationTargetException;

//import com.squareup.MonitorEngine;

/**
 * 应用的基类，封装一些应用全局调用的对象及API（to be continued），
 * 
 * 包括提供全局的Handler对象和Application Context，
 * 
 * 也提供了强杀进程等方法。
 * 
 * @author yong01.yin
 *
 */
public class BaseApplication extends Application {
	
	private static Handler sHandler;
	private static Application sApplication;
    public static final String API_TOKEN_ERROR = "api_token_error";
    @Override
    public void onCreate() {
        super.onCreate();
		initCrashHandler();
		initFreeisWheelMonitor();
    }

    /**
	 * @return 整个APP可以使用的Handler（为主线程）
	 */
	public static Handler getHandler() {
		checkHandler();
		return sHandler;
	}
	
	/**
	 * @return 整个APP可以使用的Context
	 */
	public static Application getAppContext() {
		checkNull(sApplication);
		return sApplication;
	}
	
	private static void checkNull(Object obj) {
		if (null == obj) {
			throw new RuntimeException("check whether the app has a Application "
					+ "class extends BaseApplication ? or forget to " 
					+ "invoke super class's constructor first!");
		}
	}
	
	private static void checkHandler() {
		if (null == sHandler) {
			sHandler = new Handler(Looper.getMainLooper());
		}
	}

	protected void initCrashHandler() {
		SDKCrashHandler.getInstance().init(this);
	}

	protected void initFreeisWheelMonitor(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && BaseConfig.USE_FREEISWHEEL_MONITOR) {

			// use java reflection to init monitorEngine at run time. So the dependency of freeisWheelMonitor.jar
			// can be excluded in releaseCompile.
			String monitorEngineClassName = "com.squareup.MonitorEngine";
			try {
				Class monitorEngineClass = Class.forName(monitorEngineClassName);
				monitorEngineClass.getDeclaredMethod("initEngine").invoke(this);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch (NoSuchMethodException e){
				e.printStackTrace();
			}
			catch(IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e){
				e.printStackTrace();
			}
			//MonitorEngine.initEngine(this);
		}
	}

	public BaseApplication() {
		sApplication = this;
	}
	
	/**
	 * 强杀本进程
	 */
	public static void killProcess() {
		Process.killProcess(Process.myPid());
	}
}
