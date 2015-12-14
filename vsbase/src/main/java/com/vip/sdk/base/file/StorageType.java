package com.vip.sdk.base.file;

/**
 * 存储类型
 * 
 * @author Yin Yong
 *
 */
public enum StorageType {

	/**
	 * 存储类型：应用内部文件夹中
	 */
	Data {
		@Override
		public FileManager getFileManager() {
			return FileManager.getDataFileManager();
		}
	},
	
	/**
	 * 存储类型：应用内部文件夹中
	 */
	SDCard {
		@Override
		public FileManager getFileManager() {
			return FileManager.getSDcardFileManager();
		}
	};
	
	/**
	 * 获得相应的{@link FileManager}
	 * @return 存储类型相应的{@link FileManager}
	 */
	public abstract FileManager getFileManager() ;
	
}
