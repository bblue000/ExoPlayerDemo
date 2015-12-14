package com.vip.sdk.base.io;

import com.vip.sdk.base.utils.NumberUtils;


public class FileUtils {

	private FileUtils() { /* no instance */ }
	
	/**
	 * 以M为单位
	 * @added 1.0
	 */
	public static String calFileSizeString(double bytes) {
		if (0D >= bytes) {
			bytes = 0D;
		}
		return NumberUtils.formatByUnit(bytes, 1024D, 900D, 2, "B", "KB", "M", "G", "T");
	}
	
}
