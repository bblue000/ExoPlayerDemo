package com.vip.sdk.base.utils;

/**
 * 
 * utils for objects
 * 
 * @author Yin Yong
 *
 */
public class ObjectUtils {

	private ObjectUtils() { }
	
	public static boolean checkNull(Object obj) {
		return null == obj;
	}
	
	public static void checkNullAndThrow(Object obj, String tag) {
		if (checkNull(obj)) {
			throw new NullPointerException(tag + " is null");
		}
	}
	
	public static boolean equals(Object obj1, Object obj2) {
		if (null == obj1) {
			return null == obj2;
		} else {
			return obj1.equals(obj2);
		}
	}
}
