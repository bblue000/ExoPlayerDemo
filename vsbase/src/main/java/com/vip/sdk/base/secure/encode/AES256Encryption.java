package com.vip.sdk.base.secure.encode;

import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AES256Encryption {

	
	
	public static final String KEY_ALGORITHM = "AES";
	
	// 原先的ECB网上说是有小缺点的（具体是什么没去研究），所以我想你们那边IOS应该考虑到了，看下面的说明，很详细了：
	// @see http://www.metsky.com/archives/585.html
	
	// 所以换成了CBC，而CBC是需要一个叫做initialization vector（初始化向量）的，
	// 估计你们的IOSer们也是网上看到了一些东西，我找到上见面的连接，一试就中...
	// 下面是得到“初始化向量”这个概念的地址：
	// @see http://bouncy-castle.1462172.n4.nabble.com/java-lang-IllegalArgumentException-no-IV-set-when-one-expected-td1465335.html
	public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";//"AES/ECB/PKCS7Padding";

	// 这个“初始化向量”你需要与IOS保持一致，就像initkey方法的实现和CIPHER_ALGORITHM算法一样，IOS是什么，我们也得是什么。
	// 用法在这里
	// @see http://www.eoeandroid.com/thread-63284-1-1.html
	public static final byte[] ivBytes = {
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
	};
	
	public static byte[] initkey(String key) throws Exception {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			String text = key;
			md.update(text.getBytes("UTF-8"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return md.digest();
	}

	/**
	 * 
	 * 转换密钥
	 * 
	 * @param key
	 *            二进制密钥
	 * @return Key 密钥
	 * */
	public static Key toKey(byte[] key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
		return secretKey;
	}

	/**
	 * 加密数据
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 加密后的数据
	 * */
	public static String encrypt(byte[] data, byte[] key) throws Exception {
		Key k = toKey(key);
		/**
		 * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现
		 * Cipher.getInstance(CIPHER_ALGORITHM,"BC")
		 */
//		Security.removeProvider("BC");
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");

		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		// 初始化，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k, ivSpec);
		// 执行操作
		return new String(Base64.encode(cipher.doFinal(data)));
	}

	/**
	 * 解密数据
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @return byte[] 解密后的数据
	 * */
	public static byte[] decrypt(String data, byte[] key) throws Exception {
		byte[] content = Base64.decode(data);
		// 欢迎密钥
		Key k = toKey(key);
//		Security.removeProvider("BC");
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		cipher.init(Cipher.DECRYPT_MODE, k, ivSpec);
		// 执行操作
		return cipher.doFinal(content);
	}

	public static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}
}
