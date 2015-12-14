package com.vip.sdk.base.file.app;

import java.io.File;

import android.text.TextUtils;

import com.vip.sdk.base.file.FileCompositor;
import com.vip.sdk.base.file.FileManager;
import com.vip.sdk.base.file.StorageType;
import com.vip.sdk.base.secure.encode.MD5;

/**
 * 应用程序本地化文件工具，向本应用需要使用文件存储的地方提供方法，获取文件对象，
 * 
 * 后续可以进行增删改等文件操作。
 * 
 * @author Yin Yong
 *
 */
public class LocalFileUtility {

	static final String TAG = LocalFileUtility.class.getSimpleName();
	
	public static final String IMAGE_FILE_SUFFIX = "";
	
	public static final String FILE_TMP_PATH = "tmp";
	public static final String FILE_PATH = "file";
	public static final String FILE_IMG_PATH = "img";
	public static final String FILE_UPLOAD_CACHE = "img_cache";

	/**
	 * @param fileName 文件名称（不包含路径）
	 * 
	 * @see {@link #findFileByName(String, String)}
	 */
	public static FileCompositor getCommonFileByName(String fileName) {
		return getFileByName0(FILE_TMP_PATH, null, fileName);
	}
	
	public static FileCompositor getCommonFileByUrl(String url, String suffix) {
		return getFileByName0(FILE_TMP_PATH, null, transferUrl(url, suffix));
	}
	
	public static FileCompositor getDir(String dir) {
		dir = comFileParentDir(FILE_PATH, dir);
		return FileCompositor.obtainDir(dir);
	}
	
	public static FileCompositor getFileByName(String dir, String fileName) {
		return getFileByName0(FILE_PATH, dir, fileName);
	}
	
	public static FileCompositor getFileByUrl(String dir,
			String url, String suffix) {
		return getFileByName0(FILE_PATH, dir, transferUrl(url, suffix));
	}
	
	
	public static String transferUrl(String url, String suffix) {
		if (TextUtils.isEmpty(url)) {
			return url;
		}
		String cs = MD5.digest2Str(url);
		if (TextUtils.isEmpty(cs)) {
			return url;
		}
		if (!TextUtils.isEmpty(suffix)) {
			cs += suffix;
		}
		return cs;
	}
	
	/**
	 * 获取图片文件路径
	 * 
	 * @param fileName 文件名称（不包含路径）
	 * 
	 * @see {@link #findFileByName(String, String)}
	 */
	public static FileCompositor getImageFileByName(String fileName) {
		return getFileByName0(FILE_PATH, FILE_IMG_PATH, fileName);
	}
	
	public static FileCompositor getImageFileByUrl(String url, String suffix) {
		return getFileByName0(FILE_PATH, FILE_IMG_PATH, 
				transferUrl(url, suffix));
	}
	
	/**
	 * @param catagoryDir 两端不需要有“/”
	 * @param subDir 两端不需要有“/”
	 * @param fileName 文件名称（不包含路径）
	 * 
	 * @see {@link #findFileByName(String, String)}
	 */
	private static FileCompositor getFileByName0(String catagoryDir,
			String subDir, String fileName) {
		String dir = comFileParentDir(catagoryDir, subDir);
		if (TextUtils.isEmpty(dir)) {
			return FileCompositor.obtainFile(fileName);
		} else {
			return FileCompositor.obtainFile(dir, fileName);
		}
	}
	
	/**
	 * 合成父文件夹
	 */
	private static String comFileParentDir(String catagoryDir, String subDir) {
		String dir = "";
		if (!TextUtils.isEmpty(catagoryDir)) {
			dir += catagoryDir;
			dir += File.separator;
		}
		if (!TextUtils.isEmpty(subDir)) {
			dir += subDir;
			dir += File.separator;
		}
		return dir;
	}
	
	static boolean findFile(FileCompositor fileCompositor) {
		StorageType type = FileManager.existsWithinAllManagers(
				fileCompositor);
		if (null != type) {
			fileCompositor.setStorageType(type);
			return true;
		}
		return false;
	}
}