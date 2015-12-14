package com.vip.sdk.base.file;

import java.io.File;

import android.content.Context;

/**
 * /data/data/[packageName]下的文件管理，该实现是将所有的文件下载到应用路径的files下
 * 
 * @author Yin Yong
 */
class DataFileManager extends FileManager {

	private File mRootDirPath;
	public DataFileManager(Context context) {
		super(context);
	}
	
	@Override
	public synchronized File getRootDirPath() {
		if (null == mRootDirPath) {
			mRootDirPath = getContext().getFilesDir();
		}
		return mRootDirPath;
	}

	@Override
	StorageType getStorageType() {
		return StorageType.Data;
	}
}
