package com.vip.sdk.base.utils;

public class ReflectUtils {

	private ReflectUtils() { }
	
	/**
	 * 判断一个类是target的子类实例
	 */
	public static boolean isTypeOf(Class<?> target, Object instance) {
		return target.isAssignableFrom(instance.getClass());
//		Class<?> clzOfInstance = instance.getClass();
//		boolean find = false;
//		while (null != clzOfInstance && Object.class != clzOfInstance) {
//			if (clzOfInstance == target) {
//				find = true;
//				break;
//			}
//			clzOfInstance = clzOfInstance.getSuperclass();
//		}
//		return find;
	}
	
}
