package com.aote.file;

import java.io.File;
import java.util.Map;

public interface IFile {
	/**
     * 读取文件属性
     * @param file 将要读取的文件
     * @return Map<String, Object>
     */
	public Map<String, Object> readParam(File file);
	/**
     * 查找文件回调函数
     * @param file 将要读取的文件
     * @return boolean
     */
	public boolean isFile(File file);
}
