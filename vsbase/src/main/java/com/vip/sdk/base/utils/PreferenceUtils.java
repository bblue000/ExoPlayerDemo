package com.vip.sdk.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * 便于设置/获取shared preference值的工具类
 * 
 * @author yong01.yin
 *
 */
public class PreferenceUtils {

	private PreferenceUtils() { }
	
	public static long getValue(Context context, String node, String key,
			long defaultValue) {
		return context.getSharedPreferences(node, Context.MODE_PRIVATE).getLong(
				key, defaultValue);
	}
	
	public static int getValue(Context context, String node, String key,
			int defaultValue) {
		return context.getSharedPreferences(node, Context.MODE_PRIVATE).getInt(
				key, defaultValue);
	}

	public static String getValue(Context context, String node, String key,
			String defaultValue) {
		return context.getSharedPreferences(node, Context.MODE_PRIVATE)
				.getString(key, defaultValue);
	}

	public static boolean getValue(Context context, String node, String key,
			boolean defaultValue) {
		return context.getSharedPreferences(node, Context.MODE_PRIVATE)
				.getBoolean(key, defaultValue);
	}

	public static void saveValue(Context context, String node, String key,
			String value) {
		SharedPreferences.Editor sp = context.getSharedPreferences(node,
				Context.MODE_PRIVATE).edit();
		sp.putString(key, value);
		sp.commit();
	}

	public static void saveValue(Context context, String node, String key,
			boolean value) {
		SharedPreferences.Editor sp = context.getSharedPreferences(node,
				Context.MODE_PRIVATE).edit();
		sp.putBoolean(key, value);
		sp.commit();
	}

	public static void saveValue(Context context, String node, String key,
			int value) {
		SharedPreferences.Editor sp = context.getSharedPreferences(node,
				Context.MODE_PRIVATE).edit();
		sp.putInt(key, value);
		sp.commit();
	}
	
	public static void saveValue(Context context, String node, String key,
			long value) {
		SharedPreferences.Editor sp = context.getSharedPreferences(node,
				Context.MODE_PRIVATE).edit();
		sp.putLong(key, value);
		sp.commit();
	}
	
}
