package com.aote.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aote.util.ExceptionHelper;
import com.aote.util.JsonTransfer;

@Component
@Transactional
public class FileServer {
	static Logger log = Logger.getLogger(FileServer.class);
	
	//文件属性读取、文件判断接口
	private IFile fileCheck = null;
	
	public void setChecker(IFile check) {
		this.fileCheck = check;
	}
	
	public void setChecker(String checkName) throws Exception {
		//根据传入的检测类名生实例化对象在读取过程中用获取文件属性，默认为FileCheck
		try {
			Class<?> c = Class.forName(checkName);
			fileCheck = (IFile) c.newInstance();
		} catch (Exception e) {
			log.error(ExceptionHelper.stackToString(e));
			throw e;
		}
	}
	
	/**
     * 读取文件内容
     * @param filePath 文件全路径
     * @return byte
	 * @throws Exception 
     */
	public byte[] read(String filePath) throws Exception {
		if (filePath == null || filePath.length() <= 0) {
			return null;
		}
		
		//格式化路径，替换为当前操作系统下的标准路径
		filePath = formatPath(filePath);
		
		byte[] buffer = null;
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			//创建字节输入流  
			FileInputStream fis = new FileInputStream(file);  
			
			//根据文件大小穿件存储区
			buffer = new byte[(int) file.length()];
			fis.read(buffer);  
			
			fis.close();
			
			log.debug("读取文件" + "（" + filePath + "）内容：" + new String(buffer));
		}
		
		return buffer;
	}
	
	/**
     * 读取文件夹内容
     * @param filePath 文件全路径
     * @param readChild 是否读取子目录
     * @return JSONObject
	 * @throws Exception 
     */
	public JSONObject read(String filePath, boolean readChild) throws Exception {
		if (filePath == null || filePath.length() <= 0) {
			return null;
		}
		
		//格式化路径，替换为当前操作系统下的标准路径
		filePath = formatPath(filePath);
		
		JSONObject obj = null;
		File file = new File(filePath);
		if (file.exists() && file.isDirectory()) {
			
			obj = (JSONObject)new JsonTransfer().MapToJson(readFolder(file, readChild));
			
			log.debug("读取文件夹" + "（" + filePath + "）目录：" + obj.toString());
		}
		
		return obj;
	}
	
	/**
     * 写入内容到制定文件
     * @param buffer 要保存的内容
	 * @param length 要保存的内容长度
	 * @param filePath 文件全路径
	 * @param  append 是否是追加
     * @return String
	 * @throws Exception 
     */
	public String write(byte[] buffer, int length, String filePath, boolean append) throws Exception {
		if (buffer == null || filePath == null || filePath.length() <= 0) {
			return null;
		}
		
		//格式化路径，替换为当前操作系统下的标准路径
		filePath = formatPath(filePath);
		
		//截取父目录路径，并判断目录是否存在，不存在则创建
		String folderPath = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
		File file = new File(folderPath);
		if (!file.exists()) {
			log.debug("创建目录" + (file.mkdirs() ? "成功（" : "失败（") + folderPath + "）");
		}
		
		//给文件写入内容
		FileOutputStream fos = new FileOutputStream(filePath, append);
		fos.write(buffer, 0, length);
		
		fos.close();
		
		log.debug("写入文件内容成功（" + filePath + "）:" + new String(buffer));
		
		return "ok";
	}
	
	/**
     * 删除文件或目录
     * @param filePath 文件全路径
     * @return String
     */
	public String delete(String filePath) {
		if (filePath == null || filePath.length() <= 0) {
			return null;
		}
		
		//格式化路径，替换为当前操作系统下的标准路径
		filePath = formatPath(filePath);
		
		//文件如果存在就删除
		File file = new File(filePath);
		if (file.exists()) {
			if (file.isDirectory()) {
				log.debug("删除目录" + (deleteFolder(file) ? "成功（" : "失败（") + filePath + "）");
	        } else {
	        	log.debug("删除文件" + (file.delete() ? "成功（" : "失败（") + filePath + "）");
	        }
		}
		
		return "ok";
	}
	
	/**
     * 查找文件
     * @param filePath 文件全路径
     * @param findChild 是否读取子目录
     * @param check 查找判断
     * @return JSONObject
	 * @throws Exception 
     */
	public JSONObject find(String filePath, boolean findChild, IFile check) throws Exception {
		if (filePath == null || filePath.length() <= 0) {
			return null;
		}
		
		//格式化路径，替换为当前操作系统下的标准路径
		filePath = formatPath(filePath);
		
		JSONObject obj = null;
		File file = new File(filePath);
		if (file.exists() && file.isDirectory()) {
			if (check != null) {
				fileCheck = check;
			}
	    	
	    	Map<String, Object> map = new HashMap<String, Object>();
			obj = (JSONObject)new JsonTransfer().MapToJson(findFile(file, findChild, map));
			
			log.debug("查找文件夹" + "（" + filePath + "|" + fileCheck.getClass() + "）结果：" + obj.toString());
		}
		
		return obj;
	}
	
	/**
     * 格式化路径，替换为当前操作系统下的标准路径
     * @param path 传入的路径
     * @return String 返回路径
     */
    private String formatPath(String path) {
		if (File.separatorChar == '\\') {
			path = path.replaceAll("/", File.separatorChar+"");
		} else {
			path = path.replaceAll("\\", File.separatorChar+"");
		}
		
		return path;
    }
	
    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean
     */
    private boolean deleteFolder(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteFolder(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        
        // 目录此时为空，可以删除
        return dir.delete();
    }
    
    /**
     * 递归读取目录下的所有文件及子目录下所有文件
     * @param dir 将要读取的文件目录
     * @param readChild 是否读取子目录
     * @return Map<String, Object>
     */
    private Map<String, Object> readFolder(File dir, boolean readChild) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put(".", readFileParam(dir));
    	
        String[] children = dir.list();
    	for (int i = 0; i < children.length; i++) {
        	File f = new File(dir, children[i]);
        		if (f.isDirectory()) {
            		if (readChild) {
            			//递归查找目录中的子目录
            			map.put(children[i], readFolder(f, readChild));
            		} else {
            			map.put(children[i], readFileParam(f));
            		}
            	} else {
            		map.put(children[i], readFileParam(f));
            	}
        }
        
        return map;
    }
    
    /**
     * 递归查找目录下的所有文件及子目录下所有文件
     * @param dir 将要读取的文件目录
     * @param readChild 是否读取子目录
     * @param map 保存查找结果
     * @return Map<String, Object>
     */
    private Map<String, Object> findFile(File dir, boolean readChild, Map<String, Object> map) {
        String[] children = dir.list();
    	for (int i = 0; i < children.length; i++) {
        	File f = new File(dir, children[i]);
        		if (f.isDirectory()) {
            		if (readChild) {
            			//递归查找目录中的子目录
            			findFile(f, readChild, map);
            		}
            	} else if (fileCheck.isFile(f)) {
            		map.put(children[i], readFileParam(f));
            	}
        }
        
        return map;
    }
    
    /**
     * 读取文件属性
     * @param file 将要读取的文件
     * @return Map<String, Object>
     */
    private Map<String, Object> readFileParam(File file) {
    	
    	if (fileCheck == null) {
    		fileCheck = new FileCheck(null);
    	}
    	
    	return fileCheck.readParam(file);
    }
    
	public static void main(String[] args) throws Exception {
		FileServer s = new FileServer();
		s.write("1".getBytes(), 1, "D:\\1\\3\\2.txt", true);
		s.write("1".getBytes(), 1, "D:\\1\\2\\2.txt", true);
		s.write("2".getBytes(), 1, "D:\\1\\2\\2.txt", true);
		
		System.out.println(s.read("D:\\1", true).toString());
		System.out.println(new String(s.read("D:\\1\\2\\2.txt")));
		System.out.println(s.find("E:\\workarea\\workspace\\htapps\\java\\htapps", true, new FileCheck(".xml")).toString());
		//System.out.println(s.read("D:\\Program Files\\android-studio", true, null).toString());
		
		//s.delete("D:\\1.java");
		//s.delete("D:\\1");
	}
}
