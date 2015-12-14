package com.vip.sdk.base.secure.encode;

import java.security.MessageDigest;


public class MD5 {
	static final String algorithm = "MD5";

	public static byte[] digest2Bytes(byte[] bytes) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception localNoSuchAlgorithmException) {
		}
		return md.digest(bytes);
	}

	public static String digest2Str(byte[] bytes) {
		return CByte.bytes2Hex(digest2Bytes(bytes));
	}

	public static String digest2Str(String str) {
		return digest2Str(str.getBytes());
	}
}
