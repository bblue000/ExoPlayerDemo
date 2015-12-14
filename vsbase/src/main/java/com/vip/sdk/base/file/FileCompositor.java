package com.vip.sdk.base.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * FileManager机制中，组合File Name/Path的工具类
 * 
 * @author Yin Yong
 */
public class FileCompositor implements Parcelable, IFileOperations {

	private static final int MAX_POOL_SIZE = 3;
	private static final LinkedList<FileCompositor> sPoolList
		= new LinkedList<FileCompositor>();
	
	private static FileCompositor obtainFileCompositor() {
		synchronized (sPoolList) {
			if (!sPoolList.isEmpty()) {
				return sPoolList.remove().releaseRecycleState();
			}
			return new FileCompositor();
		}
	}
	
	/**
	 * 获得根目录的FileNameCompositor对象
	 */
	public static FileCompositor obtainRootDir() {
		FileCompositor instance = obtainFileCompositor();
		setParams(instance, null, null);
		return instance;
	}
	
	/**
	 * @param dir 单纯的文件夹名称（两端不需要“/”）
	 */
	public static FileCompositor obtainDir(String dir) {
		FileCompositor instance = obtainFileCompositor();
		checkDirAccuracy(dir);
		setParams(instance, dir, null);
		return instance;
	}
	
	/**
	 * @param fileName 单纯的文件名称（不要含有路径）
	 */
	public static FileCompositor obtainFile(String fileName) {
		FileCompositor instance = obtainFileCompositor();
		checkFileNameAccuracy(fileName);
		setParams(instance, null, fileName);
		return instance;
	}
	
	/**
	 * @param dir 指定文件的父文件夹（两端不需要“/”）
	 * @param fileName 单纯的文件名称
	 */
	public static FileCompositor obtainFile(String dir, String fileName) {
		FileCompositor instance = obtainFileCompositor();
		checkDirAccuracy(dir);
		checkFileNameAccuracy(fileName);
		setParams(instance, dir, fileName);
		return instance;
	}
	
	private static void setParams(FileCompositor instance,
			String dir, String fileName) {
		instance.empty();
		instance.setStorageType(FileManager.getAppFileManager().getStorageType());
		instance.setPath(dir);
		instance.setFileName(fileName);
	}
	
	
	/**
	 * TODO 检测-文件夹，文件名-参数是否正确
	 */
	private static void checkDirAccuracy(String dir) {
		if (!isEmptyString(dir)) {
			return ;
		}
		throw new IllegalArgumentException("invalid dir name = " + dir);
	}
	
	/**
	 * TODO 检测-文件夹，文件名-参数是否正确
	 */
	private static void checkFileNameAccuracy(String fileName) {
		if (!isEmptyString(fileName) 
				&& fileName.indexOf("/") < 0
				&& fileName.indexOf("\\") < 0) {
			return ;
		}
		throw new IllegalArgumentException("invalid file name = " + fileName);
	}
	
	private static boolean isEmptyString(String name) {
		return null == name || "".equals(name);
	}
	
	// >>>>>>>>>>>>>>
	// internal implements
	private FileCompositor() {}
	
	private String mPath;
	private String mFileName;
	private String mCompositedPath;
	private StorageType mStorageType;
	// cache size
	private final Map<StorageType, File> mAbsoluteFileMap
		= new HashMap<StorageType, File>(StorageType.values().length);
	
	/*package*/ int flags;
	static final int FLAG_IN_USE = 1 << 0;
	static final int FLAG_RECYCLED = 1 << 1;
	
	/**
	 * 设置当前 {@code FileCompositor} 对象所使用的StorageType
	 */
	public FileCompositor setStorageType(StorageType type) {
		checkIfRecycled();
		if (null != type) {
			mStorageType = type; 
		}
		return this;
	}
	
	public StorageType getStorageType() {
		checkIfRecycled();
		return mStorageType;
	}
	
	/**
	 * 设置当前 {@code FileCompositor} 的父文件夹
	 */
	/*package*/ FileCompositor setPath(String path) {
		mPath = path;
		return this;
	}
	
	/**
	 * 设置当前 {@code FileCompositor} 的文件名
	 */
	/*package*/ FileCompositor setFileName(String fileName) {
		mFileName = fileName;
		return this;
	}
	
	/**
	 * 根据指定的StorageType获得文件最终全路径文件
	 */
	public File getAbsoluteFile(StorageType type) {
		checkIfRecycled();
		File file = mAbsoluteFileMap.get(type);
		if (null == file) {
			String compositedFileName = getCompositedFileName();
			if (isEmptyString(compositedFileName)) {
				file = type.getFileManager().getRootDirPath();
			} else {
				file = new File(type.getFileManager().getRootDirPath(),
						getCompositedFileName());
			}
			mAbsoluteFileMap.put(type, file);
		}
		return file;
	}
	
	/**
	 * 根据指定的FileManager获得文件最终全路径文件
	 */
	public File getAbsoluteFile(FileManager fileManager) {
		return getAbsoluteFile(fileManager.getStorageType());
	}
	
	/**
	 * 根据{@link #setStorageType(StorageType)} 中
	 * （如果没有设置，使用 {@link FileManager#getAppFileManager()} 对应的存储类型）,
	 * 
	 * 获得文件最终全路径文件
	 */
	public File getAbsoluteFile() {
		return getAbsoluteFile(mStorageType);
	}
	
	/**
	 * 根据{@link #setStorageType(StorageType)} 中
	 * （如果没有设置，使用 {@link FileManager#getAppFileManager()} 对应的存储类型）,
	 * 
	 * 获得文件最终全路径
	 */
	public String getAbsolutePath() {
		return getAbsoluteFile(mStorageType).getAbsolutePath();
	}
	
	/**
	 * 获取组合完成的文件名称（包括之指定的父文件夹（如果指定了）和文件名）。
	 * 
	 * <p>
	 * 	<b>注意：</b>该方法并非返回全路径
	 * </p>
	 */
	public String getCompositedFileName() {
		checkIfRecycled();
		if (isEmptyString(mCompositedPath)
				&& !(isEmptyString(mPath) && isEmptyString(mFileName))) {
			StringBuilder sb = new StringBuilder();
			if (!isEmptyString(mPath)) {
				sb.append(mPath);
				sb.append(File.separator);
			}
			if (!isEmptyString(mFileName)) {
				sb.append(mFileName);
			}
			return mCompositedPath = sb.toString();
		}
		return mCompositedPath;
	}
	
	@Override
	public String toString() {
		if (isRecycled()) {
			return "{ recycled instance }";
		}
		return "{ path = " + getAbsolutePath() + " }";
	}
	
	/**
	 * 回收对象
	 * <br/><br/>
	 * 使用对象后，调用该方法是个习惯^_^
	 */
	public void recycle() {
		if (isRecycled()) {
			return ;
		}
		empty();
		synchronized (sPoolList) {
			if (sPoolList.size() < MAX_POOL_SIZE) {
				sPoolList.add(this.setRecycleState());	
			}
		}
	}
	
	private void empty() {
		mPath = null;
		mFileName = null;
		mStorageType = null;
		
		mCompositedPath = null;
		mAbsoluteFileMap.clear();
	}
	
	private boolean isRecycled() {
		return 0 != (flags & FLAG_RECYCLED);
	}
	
	private FileCompositor setRecycleState() {
		flags |= FLAG_RECYCLED;
		return this;
	}
	
	private FileCompositor releaseRecycleState() {
		flags &= ~FLAG_RECYCLED;
		return this;
	}
	
	private void checkIfRecycled() {
		if (isRecycled()) {
			throw new IllegalStateException("this object is recycled!");
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mPath);
		dest.writeString(mFileName);
		dest.writeSerializable(mStorageType);
	}
	
	public static final Parcelable.Creator<FileCompositor> CREATOR
		= new Parcelable.Creator<FileCompositor>() {
		public FileCompositor createFromParcel(Parcel in) {
			return FileCompositor.obtainFileCompositor().readFromParcel(in);
		}

		public FileCompositor[] newArray(int size) {
			return new FileCompositor[size];
		}
	};

	/**
	 * 替代 {@link android.os.Parcelable} 中建议的Constructor(Parcel)，使用该方式，
	 */
	private FileCompositor readFromParcel(Parcel in) {
		mPath = in.readString();
		mFileName = in.readString();
		mStorageType = (StorageType) in.readSerializable();
		return this;
	}

	@Override
	public boolean exists() {
		return FileManagerUtils.exists(getAbsoluteFile());
	}

	@Override
	public boolean createNewFile() throws IOException {
		return FileManagerUtils.createFile(getAbsoluteFile());
	}

	@Override
	public boolean mkdirs() throws IOException {
		return FileManagerUtils.createDir(getAbsoluteFile());
	}

	@Override
	public boolean mkParentDirs() throws IOException {
		File myFile = getAbsoluteFile();
		File parentFile = myFile.getParentFile();
		if (null == parentFile) {
			return true;
		}
		return FileManagerUtils.createDir(parentFile);
	}

	@Override
	public boolean deleteFile(boolean deleteRoot) {
		return FileManagerUtils.deleteFile(getAbsoluteFile(), deleteRoot);
	}

	@Override
	public InputStream openFileInput() throws IOException {
		return FileManagerUtils.openFileInput(getAbsoluteFile());
	}

	@Override
	public OutputStream openFileOutput(boolean createIfUnExists)
			throws IOException {
		return FileManagerUtils.openFileOutput(getAbsoluteFile(), createIfUnExists);
	}

	@Override
	public OutputStream openFileOutput(boolean createIfUnExists, boolean append)
			throws IOException {
		return FileManagerUtils.openFileOutput(getAbsoluteFile(), createIfUnExists, append);
	}

	@Override
	public boolean save(InputStream ins) throws IOException {
		return FileManagerUtils.save(getAbsoluteFile(), ins);
	}

	@Override
	public boolean save(InputStream ins, boolean append) throws IOException {
		return FileManagerUtils.save(getAbsoluteFile(), ins, append);
	}

	@Override
	public boolean save(InputStream ins, boolean append,
			boolean closeIns) throws IOException {
		return FileManagerUtils.save(getAbsoluteFile(), ins, append, closeIns);
	}

	@Override
	public long size() {
		return FileManagerUtils.caculateFileSize(getAbsoluteFile());
	}
	
}