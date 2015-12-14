package com.vip.sdk.base.file;

public class StorageListener {
	// default 10KB
	private long mConsideredLowSize = 10L * 1024L * 1024L;
	
	/**
	 * @param size 设置监听者自身认为的“低存储”大小
	 */
	public void setLowStorageSize(long size) {
		if (size > mConsideredLowSize) {
			mConsideredLowSize = size;
		}
	}
	
	/**
	 * 相应的FileManager当前剩余的空间
	 * @param size 当前存储机制剩余的空间大小
	 */
	public void onLowStorageSize(long size) {};
	
	public void onStorageTypeChanged(StorageType oldType, StorageType newType) {};
}
