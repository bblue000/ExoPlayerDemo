package com.vip.sdk.base.file;

import java.io.File;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

/**
 * SD卡下的文件管理
 * 
 * @author Yin Yong
 */
class SDcardFileManager extends FileManager {

	private File mRootDirPath;
	public SDcardFileManager(Context context) {
		super(context);
		
		//TODO check relative permission in mainfest.xml 
		if (PackageManager.PERMISSION_GRANTED != 
				getContext().getPackageManager().checkPermission(
					permission.WRITE_EXTERNAL_STORAGE,
					getContext().getPackageName())) {
			throw new IllegalStateException("please check you've set " 
					+ "'android.permission.WRITE_EXTERNAL_STORAGE' permission "
					+ "in manifest file!");
		}
	}
	
	@Override
	public synchronized File getRootDirPath() {
		if (null == mRootDirPath) {
			mRootDirPath = new File(Environment.getExternalStorageDirectory(), 
					getContext().getPackageName());
		}
		if (mRootDirPath.isFile()) {
			try {
				mRootDirPath.delete();
			} catch (Exception ignore) { }
		}
		if (!mRootDirPath.exists()) {
			mRootDirPath.mkdirs();
		}
		return mRootDirPath;
	}
	
	@Override
	StorageType getStorageType() {
		return StorageType.SDCard;
	}

}