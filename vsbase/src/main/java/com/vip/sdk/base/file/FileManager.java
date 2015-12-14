package com.vip.sdk.base.file;

import java.io.File;
import java.util.Set;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.vip.sdk.base.BaseApplication;

/**
 * 基础模块之一：文件管理模块
 * 
 * Android中主要分为两种存储路径：
 * <ul>
 * 	<li>/data/data/[packageName]/</li>
 * 	<li>SD卡</li>
 * </ul>
 * 
 * 相应地，本管理模块中也分别提供了，可以通过以下方式获取：
 * <ul>
 * 	<li>{@link FileManager#getDataFileManager()}</li>
 * 	<li>{@link FileManager#getSDcardFileManager()}</li>
 * </ul>
 * 
 * 一般情况下，应用程序会设置默认的存储方式，所以本模块默认为SD卡。
 * <br/>
 * 应用可以通过调用{@link FileManager#initAppConfig(Context, StorageType)}，
 * 在应用程序的<code>Application</code>初始化时进行设置。
 * 
 * @author Yin Yong
 */
public abstract class FileManager {

	public static final int FILE_BUFFER_SIZE = 512;
	
	private static Context sApplicationContext;
	private static FileManager sDataInstance;
	private static FileManager sSDcardInstance;
	// application
	private static StorageType sPreferredStorageType;
	private static StorageType sCurrentStorageType;
	private static FileManager sCurrentInstance;
	@SuppressWarnings("unused")
	private static Set<StorageListener> sStorageListeners;
	/**
	 * 初始化文件管理器
	 * 
	 * @param context 应用的Context对象
	 * @param defType 设置默认的存储类型
	 * @see {@link StorageType}
	 */
	public static void initAppConfig(StorageType defType) {
		sApplicationContext = BaseApplication.getAppContext();
		getAppFileManager();
		if (null != defType) {
			sPreferredStorageType = defType;
		}
		obtainJITStorageState(true);
	}
	
	/**
	 * 获得应用当前使用的的文件管理类
	 */
	public synchronized static FileManager getAppFileManager() {
		if (null == sApplicationContext) {
			sApplicationContext = BaseApplication.getAppContext();
//			throw new UnsupportedOperationException("FileManager hasn't initialized!");
		}
		if (null == sDataInstance) {
			sDataInstance = new DataFileManager(sApplicationContext);
		}
		if (null == sSDcardInstance) {
			sSDcardInstance = new SDcardFileManager(sApplicationContext);
		}
		
		// default prefer SD card storage
		if (null == sPreferredStorageType) {
			sPreferredStorageType = StorageType.SDCard;
			obtainJITStorageState(true);
		}
		obtainJITStorageState(false);
		return sCurrentInstance;
	}
	
	/**
	 * 获得内存储的文件管理类
	 */
	public synchronized static FileManager getDataFileManager() {
		getAppFileManager();
		return sDataInstance;
	}
	
	/**
	 * 获得SD卡存储的文件管理类
	 */
	public synchronized static FileManager getSDcardFileManager() {
		getAppFileManager();
		return sSDcardInstance;
	}
	
	private Context mContext;
	/*package*/ FileManager(Context context) {
		mContext = context;
	}
	
	/**
	 * 获取相应FileManager的根目录地址
	 */
	public abstract File getRootDirPath() ;
	
	/**
	 * 获取相应FileManager的Context
	 */
	/*package*/ Context getContext() {
		return mContext;
	}
	
	/**
	 * 获取相应FileManager的StorageType
	 */
	abstract StorageType getStorageType() ;
	
	/**
	 * 从所有的FileManager中查找，一旦找到返回存储类型
	 * 
	 * @param context
	 * @param fileNameCompositor
	 * @return null if not exist in all file systems
	 */
	public static StorageType existsWithinAllManagers(
			FileCompositor fileCompositor) {
		final StorageType originalType = fileCompositor.getStorageType();
		try {
			StorageType types[] = StorageType.values();
			for (int i = 0; i < types.length; i++) {
				fileCompositor.setStorageType(types[i]);
				if (fileCompositor.exists()) {
					return types[i];
				}
			}
			return null;
		} finally {
			fileCompositor.setStorageType(originalType);
		}
	}
	
	private static long sLastObtainTime = 0;
	private static final long TIME_OFFSET = 1 * 60 * 1000;
	/**
	 * 即时检查当前存储状态
	 * @param force 强制执行一次当前存储的检查
	 */
	private synchronized static void obtainJITStorageState(boolean force) {
		long time = System.currentTimeMillis();
		if (!force) {
			if (time - sLastObtainTime < TIME_OFFSET) {
				return ;
			}
		} else {
			sLastObtainTime = time;
		}
		StorageType oldStorageType = sCurrentStorageType;
		switch (sPreferredStorageType) {
		case Data:
			if (sCurrentInstance != sDataInstance) {
				sCurrentInstance = sDataInstance;
			}
			sCurrentStorageType = StorageType.Data;
			break;
		case SDCard:
		default:
			if (existSDcard()) {
				sCurrentInstance = sSDcardInstance;
				sCurrentStorageType = StorageType.SDCard;
			} else {
				sCurrentInstance = sDataInstance;
				sCurrentStorageType = StorageType.Data;
			}
			break;
		}
		if (oldStorageType != sCurrentStorageType) {
			// has changed
			
		}
	}
	
	// simple utilities, but not recommend frequent use
	/**
	 * @return 如果存在SD卡，则返回TRUE
	 */
	public static boolean existSDcard() {
		return Environment.MEDIA_MOUNTED
				.equals(Environment.getExternalStorageState());
	}
	
	/**
	 * 文件夹总大小
	 * @param path 文件路径（必须为文件夹）
	 */
	public static long sizeOfByAndroidStatFs(String path) {
		StatFs statFs = new StatFs(path);
		long blockSize = statFs.getBlockSize();
		long aBlocks = statFs.getBlockCount();
		long aBlockSum = blockSize * aBlocks;
		return aBlockSum;
	}
	
	/**
	 * 文件夹可用空间大小
	 * @param path 文件路径（必须为文件夹）
	 */
	public static long sizeOfFreeByAndroidStatFs(String path) {
		StatFs statFs = new StatFs(path);
		long blockSize = statFs.getBlockSize();
		long aBlocks = statFs.getAvailableBlocks();
		long aBlockSum = blockSize * aBlocks;
		return aBlockSum;
	}
	
	/**
	 * @param path absolute path
	 * @param size given enough size
	 * @return 如果指定路径的可用存储空间大于size，返回TURE
	 */
	public static boolean hasEnoughSize(String path, long size) {
		return sizeOfFreeByAndroidStatFs(path) >= size;
	}
	
}