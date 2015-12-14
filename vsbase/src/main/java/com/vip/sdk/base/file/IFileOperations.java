package com.vip.sdk.base.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 定义 {@link FileCompositor} 支持的一些文件操作
 * 
 * @author Yin Yong
 */
interface IFileOperations {

	/**
	 * 判断 {@link FileCompositor} 指定的文件是否存在
	 * 
	 * @return 如果文件存在，则返回TRUE；否则返回false
	 */
	boolean exists();
	
	/**
	 * 创建 {@link FileCompositor} 指定的文件
	 * 
	 * @return 如果最终文件存在，则返回TRUE；否则返回false
	 */
	boolean createNewFile() throws IOException;
	
	/**
	 * 创建 {@link FileCompositor} 指定的文件夹
	 * 
	 * @return 如果最终文件夹存在，返回TRUE；否则返回false
	 */
	boolean mkdirs() throws IOException ;
	
	/**
	 * 创建 {@link FileCompositor} 指定的文件的所有父文件夹
	 * 
	 * @return 如果最终父文件存在，返回TRUE；否则返回false
	 */
	boolean mkParentDirs() throws IOException;
	
	/**
	 * 删除 {@link FileCompositor} 指定的文件（夹）
	 * 
	 * @param deleteRoot 是否删除根目录
	 * （只有在 {@link FileCompositor} 指定的是文件夹时生效）
	 * 
	 * @return 如果最终文件（夹）不存在，返回TRUE；否则返回false
	 */
	boolean deleteFile(boolean deleteRoot) ;
	
	/**
	 * 打开一个文件输入流
	 * 
	 * @throws FileNotFoundException 指定文件不存在
	 * @throws UnsupportedOperationException 指定路径非文件
	 */
	InputStream openFileInput() throws IOException ;
	
	/**
	 * 根据 {@link FileCompositor} 指定的文件打开一个文件输出流
	 * 
	 * @param createIfUnExists 如果对应的文件不存在，是否需要创建
	 * 
	 * @throws FileNotFoundException 指定文件不存在
	 * @throws UnsupportedOperationException 指定路径非文件
	 * @throws IOException 其他IOException
	 */
	OutputStream openFileOutput(boolean createIfUnExists) throws IOException ;
	
	/**
	 * 根据 {@link FileCompositor} 指定的文件打开一个文件输出流
	 * 
	 * @param createIfUnExists 如果对应的文件不存在，是否需要创建
	 * 
	 * @throws FileNotFoundException 指定文件不存在
	 * @throws UnsupportedOperationException 指定路径非文件
	 * @throws IOException 其他IOException
	 */
	OutputStream openFileOutput(boolean createIfUnExists, boolean append)
			throws IOException ;
	
	/**
	 * 根据 {@link FileCompositor} ，指定输入流写入其中
	 * 
	 * @param ins 指定输入流（内部读取完后不关闭）
	 * 
	 * @return 如果写入成功，则返回TRUE，否则返回false
	 * 
	 * @throws FileNotFoundException 指定文件不存在
	 * @throws UnsupportedOperationException 指定路径非文件
	 * @throws IOException 其他IOException
	 */
	boolean save(InputStream ins) throws IOException ;
	
	/**
	 * 根据 {@link FileCompositor} ，指定输入流写入其中
	 * 
	 * @param ins 指定输入流（内部读取完后不关闭）
	 * @param append 如果对应的文件不存在，是否需要创建
	 * 
	 * @return 如果写入成功，则返回TRUE，否则返回false
	 * 
	 * @throws FileNotFoundException 指定文件不存在
	 * @throws UnsupportedOperationException 指定路径非文件
	 * @throws IOException 其他IOException
	 */
	boolean save(InputStream ins, boolean append) throws IOException ;
	
	/**
	 * 根据 {@link FileCompositor} ，指定输入流写入其中
	 * 
	 * @param ins 指定输入流（内部读取完后不关闭）
	 * @param append 如果对应的文件不存在，是否需要创建
	 * @param closeIns ins完全读取完后，是否关闭
	 * 
	 * @return 如果写入成功，则返回TRUE，否则返回false
	 * 
	 * @throws FileNotFoundException 指定文件不存在
	 * @throws UnsupportedOperationException 指定路径非文件
	 * @throws IOException 其他IOException
	 */
	boolean save(InputStream ins, boolean append, boolean closeIns) throws IOException ;
	
	/**
	 * 获得文件（夹）的大小
	 */
	long size();
}
