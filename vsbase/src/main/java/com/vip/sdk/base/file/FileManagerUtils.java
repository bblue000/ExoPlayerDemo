package com.vip.sdk.base.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.vip.sdk.base.utils.FWLog;

import static com.vip.sdk.base.io.IOUtils.close;

public class FileManagerUtils {

	private static final String TAG = FileManagerUtils.class.getSimpleName();
	
	private FileManagerUtils() { }
	
	/**
	 * 文件是否存在
	 */
	public static boolean exists(File file) {
		return file.exists();
	}
	
	/**
	 * 创建指定文件（非文件夹，文件夹的创建）
	 * 
	 * @return 如果文件已存在或者被创建成功，返回TRUE，否则返回false。
	 */
	public static boolean createFile(File file) throws IOException {
		if (exists(file)) {
			if (file.isDirectory()) {
				throw new IOException("target path already exists, but is a dir!");
			}
			return file.isFile();
		} else {
			File parentFile = file.getParentFile();
			if (null != parentFile) {
				createDir(parentFile);
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				FWLog.debug("createFile invoke createNewFile error = "
                        + e.getMessage());
				throw e;
			}
			return exists(file);
		}
	}
	
	/**
	 * 创建指定路径所有的未创建的文件夹
	 * 
	 * @return 如果文件夹已存在或者被创建成功，返回TRUE，否则返回false。
	 * 
	 * @throws IOException 特别地，如果指定的路径是一个已存在的文件，将抛出异常
	 */
	public static boolean createDir(File dir) throws IOException {
		if (exists(dir)) {
			if (dir.isFile()) {
				throw new IOException("target path = { "
					+ dir.getAbsolutePath() + " } is a file, not a directory!");
			}
			return dir.isDirectory();
		}
		dir.mkdirs();
		return exists(dir);
	}
	
	/**
	 * 删除指定的文件
	 * 
	 * @param file 指定的文件对象
	 * @param deleteRoot 如果该文件是文件夹，是否删除该文件夹
	 * @return 如果最终指定的文件不再存在，则返回TRUE
	 */
	public static boolean deleteFile(File file, boolean deleteRoot) {
		if (!exists(file)) {
			return true;
		}
		if (file.isDirectory()) {
			boolean flag = true;
			File[] childFile = file.listFiles();
			if (null != childFile && childFile.length > 0) {
				for (File cFile : childFile) {
					flag &= deleteFile(cFile, deleteRoot);
				}
			}
			if (deleteRoot) {
				flag &= file.delete();
			}
			return flag;
		} else {
			return file.delete();
		}
	}
	
	
	// I/O 操作
	/**
	 * @param file 指定文件
	 * @return file指定的FileInputStream
	 * 
	 * @throws IOException 如果找不到文件，将抛出该异常/访问权限、异常等，
	 * 或者如果对应地址为文件夹，将抛出该异常
	 */
	public static FileInputStream openFileInput(File file) throws IOException {
		if (exists(file)) {
			if (file.isFile()) {
				return new FileInputStream(file);
			} else {
				throw new IOException("path = { " 
						+ file.getAbsolutePath() + " } is not a regular file!");
			}
		} else {
			throw new FileNotFoundException("path = { " + file.getAbsolutePath()
					+ " } is not found!");
		}
	}
	
	/**
	 * {@link #openFileOutput(File, boolean, false)}
	 * 
	 * @param file 指定文件
	 * @param createIfUnExists 如果文件不存在，是否创建
	 * 
	 * @return file指定的FileOutputStream
	 */
	public static FileOutputStream openFileOutput(File file, boolean createIfUnExists)
			throws IOException {
		return openFileOutput(file, createIfUnExists, false);
	}
	
	/**
	 * @param file 指定文件
	 * @param createIfUnExists 如果文件不存在，是否创建
	 * @param append If append is true and the file already exists, 
	 * it will be appended to; otherwise it will be truncated
	 * 
	 * @return path指定的FileOutputStream
	 * 
	 * @throws IOException 文件不存在且createIfUnExists == false，
	 * 或file指定的路径非文件，或其他异常
	 */
	public static FileOutputStream openFileOutput(File file,
			boolean createIfUnExists, boolean append) throws IOException {
		boolean fileExists = false;
		if (exists(file)) {
			fileExists = file.isFile();
		} else {
			if (createIfUnExists) {
				fileExists = createFile(file);
			}
		}
		if (fileExists) {
			return new FileOutputStream(file, append);
		}
		throw new IOException("path = { " + file.getAbsolutePath() + " } "
				+ " is inexistent or unsupport!");
	}
	
	/**
	 * 相当于save(String path, InputStream ins, false)，
	 * 即非叠加模式写入，不关闭参数 <code>InputStream ins</code> （无论成功与否）
	 */
	public static boolean save(File file, InputStream ins) throws IOException {
		return save(file, ins, false);
	}
	
	/**
	 * 相当于save(String path, InputStream ins, boolean append, false)，
	 * 即不关闭参数 <code>InputStream ins</code> （无论成功与否）
	 */
	public static boolean save(File file, InputStream ins, boolean append)
			throws IOException {
		return save(file, ins, append, false);
	}

	/**
	 * 
	 * @param file 目标文件
	 * @param ins 读入流
	 * @param append 内容写入时是否使用叠加模式
	 * @param closeIns 是否关闭参数 <code>InputStream ins</code> （无论成功与否）
	 * @return 如果成功写入，返回TRUE，否则返回false
	 * 
	 * @see {@link #openFileOutput(File, boolean)}
	 * @see {@link #openFileOutput(File, boolean, boolean)}
	 */
	public static boolean save(File file, InputStream ins, boolean append,
			boolean closeIns) throws IOException {
		FileOutputStream out = null;
		try {
			out = openFileOutput(file, true, append);
			byte[] buf = new byte[FileManager.FILE_BUFFER_SIZE];
			int len = -1;
			while (-1 != (len = ins.read(buf))) {
				out.write(buf, 0, len);
			}
			out.flush();
			return true;
		} finally {
			close(out);
			if (closeIns) {
				close(ins);
			}
		}
	}
	
	/**
	 * 计算指定文件/文件夹的大小
	 */
	public static long caculateFileSize(File file) {
		if (null == file || !file.exists()) {
			return 0L;
		}
		if (file.isDirectory()) {
			long size = 0L;
			File[] childFile = file.listFiles();
			if (null != childFile && childFile.length > 0) {
				for (File cFile : childFile) {
					size += caculateFileSize(cFile);
				}
			}
			return size;
		} else {
			return file.length();
		}
	}
	
//	/**
//	 * 以M为单位
//	 * @added 1.0
//	 */
//	public static String calFileSizeString(double bytes) {
//		if (0D >= bytes) {
//			bytes = 0D;
//		}
//		return NumberUtils.formatByUnit(bytes, 1024D, 900D, 2, "B", "KB", "M", "G", "T");
//	}
	
	
	/**
	 * copy a file from srcFile to destFile, return true if succeed, return
	 * false on failure
	 */
    public static boolean copyToFile(File srcFile, File destFile) {
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
            	return copyToFile(in, destFile);
            } finally {
            	close(in);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[FileManager.FILE_BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            } finally {
                try {
                    out.getFD().sync();
                } catch (IOException e) { }
                close(out);
            }
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Copy data from a source stream to destFile, and then delete the source file.
     * Return true if succeed, return false if failed.
     */
    public static boolean cutToFile(File srcFile, File destFile) {
    	return copyToFile(srcFile, destFile) && deleteFile(srcFile, true);
    }
    
    /**
     * Read a text file into a String, optionally limiting the length.
     * @param file to read (will not seek, so things like /proc files are OK)
     * @param max length (positive for head, negative of tail, 0 for no limit)
     * @param ellipsis to add of the file was truncated (can be null)
     * @return the contents of the file, possibly truncated
     * @throws IOException if something goes wrong reading the file
     */
    public static String readTextFile(File file, int max, String ellipsis) throws IOException {
    	try {
	        InputStream input = new FileInputStream(file);
	        try {
	            long size = file.length();
	            if (max > 0 || (size > 0 && max == 0)) {  // "head" mode: read the first N bytes
	                if (size > 0 && (max == 0 || size < max)) max = (int) size;
	                byte[] data = new byte[max + 1];
	                int length = input.read(data);
	                if (length <= 0) return "";
	                if (length <= max) return new String(data, 0, length);
	                if (ellipsis == null) return new String(data, 0, max);
	                return new String(data, 0, max) + ellipsis;
	            } else if (max < 0) {  // "tail" mode: keep the last N
	                int len;
	                boolean rolled = false;
	                byte[] last = null, data = null;
	                do {
	                    if (last != null) rolled = true;
	                    byte[] tmp = last; last = data; data = tmp;
	                    if (data == null) data = new byte[-max];
	                    len = input.read(data);
	                } while (len == data.length);
	
	                if (last == null && len <= 0) return "";
	                if (last == null) return new String(data, 0, len);
	                if (len > 0) {
	                    rolled = true;
	                    System.arraycopy(last, len, last, 0, last.length - len);
	                    System.arraycopy(data, 0, last, last.length - len, len);
	                }
	                if (ellipsis == null || !rolled) return new String(last);
	                return ellipsis + new String(last);
	            } else {  // "cat" mode: size unknown, read it all in streaming fashion
	                ByteArrayOutputStream contents = new ByteArrayOutputStream();
	                int len;
	                byte[] data = new byte[1024];
	                do {
	                    len = input.read(data);
	                    if (len > 0) contents.write(data, 0, len);
	                } while (len == data.length);
	                return contents.toString();
	            }
	        } finally {
	        	close(input);
	        }
		} catch (Exception e) {
			FWLog.error("readTextFile Exception: " + e.getMessage());
			e.printStackTrace();
			return "";
		}
    }

	/**
	 * Writes string to file. Basically same as "echo -n $string > $filename"
	 * @param file
	 * @param string
	 * @return return true if succeed, return false if fail
	 */
    public static boolean stringToFile(File file, String string) {
        try {
        	FileWriter out = new FileWriter(file);
        	try {
        		out.write(string);
			} finally {
				close(out);
			}
            return true;
        } catch (Exception e) {
        	FWLog.error("stringToFile Exception: " + e.getMessage());
        	e.printStackTrace();
        	return false;
        }
    }
}
