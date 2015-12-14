package com.vip.sdk.base.io;

/**
 * IO 操作中的相关常量
 * 
 * @author Yin Yong
 *
 */
public interface IOConstants {
	/**
	 * 默认的字符大小（用于传输/缓存等）
	 */
	int DEF_BUFFER_SIZE = 512;
	
	/**
	 * 默认的编码——UTF-8
	 */
	String DEF_CHARSET = "UTF-8";
	
	/**
	 * 默认的超时时间——15s
	 */
	int DEF_TIMEOUT = 15 * 1000;
}
