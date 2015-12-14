package com.vip.sdk.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.URLUtil;

import com.vip.sdk.base.BaseApplication;
/**
 * 定义调用Android系统相关应用的方法
 * @author YinYong
 * @version 1.0
 */
public class AndroidUtils {
	private AndroidUtils() { }
	
	static final String TAG = AndroidUtils.class.getSimpleName();
	private static int sDisplayWidth = 0;
	private static int sDisplayHeight = 0;
	private static String sDeviceId;
	
	// >>>>>>>>>>>>>>>>>>>
	// 获取系统、机器相关的信息
	/**
	 * 获取sdk版本
	 */
	public static int getAndroidSDKVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}
	
	private static void getDisplay() {
		if (sDisplayWidth <= 0 || sDisplayHeight <= 0) {
			WindowManager wm = (WindowManager) BaseApplication.getAppContext()
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			sDisplayWidth = dm.widthPixels;
			sDisplayHeight = dm.heightPixels;
		}
	}
	// 获取屏幕宽度
	public static int getDisplayWidth() {
		getDisplay();
		return sDisplayWidth;
	}

	// 获取屏幕高度
	public static int getDisplayHeight() {
		getDisplay();
		return sDisplayHeight;
	}
	
	/**
	 * 获取客户端的分辨率
	 */
	public static String getDeviceResolution() {
		return getDeviceResolution("x");
	}
	/**
	 * 获取客户端的分辨率
	 * @param linkMark 连接符，{@link #getDeviceResolution()} 使用的是“x”
	 */
	@SuppressLint("DefaultLocale")
	public static String getDeviceResolution(String linkMark) {
		int width = getDisplayWidth();
		int height = getDisplayHeight();
		return String.format("%d%s%d", width, linkMark, height);
	}
	
	// >>>>>>>>>>>>>>>>>>>
	// 获取应用程序相关的信息
	/**
	 * 返回当前程序版本号
	 */
	public static int getAppVersionCode() {
		int versionCode = 0;
		try {
			// ---get the package info---
			PackageManager pm = BaseApplication.getAppContext().getPackageManager();
			// 这里的context.getPackageName()可以换成你要查看的程序的包名
			PackageInfo pi = pm.getPackageInfo(BaseApplication.getAppContext().getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
			FWLog.error("getAppVersionCode Exception: " + e.getMessage());
		}
		return versionCode;
	}
	
	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(String defVersion) {
		String versionName = defVersion;
		try {
			// ---get the package info---
			PackageManager pm = BaseApplication.getAppContext().getPackageManager();
			// 这里的context.getPackageName()可以换成你要查看的程序的包名
			PackageInfo pi = pm.getPackageInfo(BaseApplication.getAppContext().getPackageName(), 0);
			versionName = pi.versionName;
			if (null == versionName || versionName.length() <= 0) {
				return defVersion;
			}
		} catch (Exception e) {
			FWLog.error("getAppVersionName Exception: " + e.getMessage());
		}
		return versionName;
	}

	@SuppressWarnings("unchecked")
	public static <T>T getMetaData(String key) {
		try {
			Context context = BaseApplication.getAppContext();
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			// 这里的context.getPackageName()可以换成你要查看的程序的包名
			ApplicationInfo pi = pm.getApplicationInfo(context.getPackageName(), 
					PackageManager.GET_META_DATA);
			Bundle metaData = pi.metaData;
			if (null == metaData) {
				return null;
			}
			return (T) metaData.get(key);
		} catch (Exception e) {
			FWLog.error("getMetaData Exception: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * 获得设备识别认证码
	 * <p/>
	 * 需要权限：android.permission.READ_PHONE_STATE
	 * @return the unique device ID, 
	 * for example, the IMEI for GSM and the MEID or ESN for CDMA phones. 
	 * Return null if device ID is not available. 
	 */
	public static String getIMEI() {
		requestPermission(android.Manifest.permission.READ_PHONE_STATE);
		TelephonyManager tm = (TelephonyManager) BaseApplication.getAppContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm == null) {
			return null;
		}
		return tm.getDeviceId();
	}
	
	public static String getDeviceId() {
		if (null == sDeviceId) {
			DeviceUuidFactory factory = new DeviceUuidFactory(BaseApplication.getAppContext());
			sDeviceId = factory.getDeviceUuid();
		}
		return sDeviceId;
	}
	
	
	// >>>>>>>>>>>>>>>>>>>
	// 调用系统的组件
	/**
	 * 需要CALL_PHONE权限
	 */
	public static void callPhone(Context context, String number) {
		if (null == context) {
			return ;
		}
		// 系统打电话界面：
		Intent intent = new Intent();
		//系统默认的action，用来打开默认的电话界面
		intent.setAction(Intent.ACTION_DIAL);
		if (!isActivityContext(context)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		//需要拨打的号码
		intent.setData(Uri.parse("tel:" + number));
		context.startActivity(intent);
	}
	
	/**
	 * 启动定位应用
	 * @added 1.0
	 */
	public static void locate(Context context, String chooserTilte,
			String lat, String lng, String addr) {
		if (null == context) {
			return ;
		}
		if (isActivityContext(context)) {
			context = context.getApplicationContext();
		}
		// 系统打电话界面：
		Intent intent = new Intent();
		//系统默认的action，用来打开默认的电话界面
		intent.setAction(Intent.ACTION_VIEW);
		//需要拨打的号码
//		intent.setData(Uri.parse("geo:" + lat + "," + lng + "?q=my+street+address"));
		String uri = "geo:0,0"+ "?q=" + lat + "," + lng;
		if (null != addr && addr.length() > 0) {
			uri += ("(" + addr + ")");
		}
		intent.setData(Uri.parse(uri));
		
		try {
			context.startActivity(Intent.createChooser(intent, chooserTilte)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//			context.startActivity(intent);
		} catch (Exception e) {
			ToastUtils.showToast("没有合适的应用打开位置信息");
		}
	}
	
	/**
	 * 调用系统的HTTP下载
	 */
	public static void callHTTPDownload(Context context, String chooserTilte, String url) {
		if (null == context) {
			return ;
		}
		if (isActivityContext(context)) {
			context = context.getApplicationContext();
		}
		// update v2
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//系统默认的action，用来打开默认的电话界面
//		intent.setAction(Intent.ACTION_VIEW);
//		intent.addCategory(Intent.CATEGORY_BROWSABLE);
		intent.setData(Uri.parse(URLUtil.guessUrl(url)));
//		intent.setDataAndType(Uri.parse(URLUtil.guessUrl(url)), "text/html");
		
		try {
			context.startActivity(Intent.createChooser(intent, chooserTilte)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		} catch (Exception e) {
			ToastUtils.showToast("没有合适的应用打开链接");
		}
	}
	
	// >>>>>>>>>>>>>>>>>>>
	// 一些常用的方法封装及汇总
	/**
	 * 判断当前线程是否是主线程
	 */
	public static boolean isMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}
	
	/**
	 * 给出的context是否是Activity实例
	 */
	public static boolean isActivityContext(Context context) {
		return context instanceof Activity;
	}
	
	/**
	 * 应用需要某种权限（<code>permission</code>），需要检测manifest.xml文件中是否声明了。
	 * 
	 * <p>
	 * 	该方法多在使用某种特定的功能时。
	 * </p>
	 */
	public static void requestPermission(String permission) {
		Context context = BaseApplication.getAppContext();
		if (PackageManager.PERMISSION_GRANTED != 
				context.getPackageManager().checkPermission(permission,
						context.getPackageName())) {
			throw new UnsupportedOperationException(
					"missing permission \""
					+ "android.permission.READ_PHONE_STATE "
					+ "\" in manifest.xml!");
		}
	}
}
