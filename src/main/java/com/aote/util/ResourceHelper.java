package com.aote.util;

import java.io.RandomAccessFile;

public class ResourceHelper {

	/**
	 * 获取资源文件里，指定全路径文件的文本内容
	 * 
	 * @param fileName
	 *            : 以'/'开始的资源文件的全路径文件名
	 * @return: 文件的文本内容
	 */
	public static String getString(String fileName) {
		String result = null;
		try {
			String path = ResourceHelper.class.getClassLoader()
					.getResource(fileName).getPath();
			RandomAccessFile file = new RandomAccessFile(path, "r");
			byte[] b = new byte[(int) file.length()];
			file.read(b);
			file.close();
			result = new String(b, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}
