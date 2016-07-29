package com.aote.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileCheck implements IFile {
	private String fileName;//文件名
	public FileCheck(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public Map<String, Object> readParam(File file) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
    	
    	if (file.isDirectory()) {
    		try {
    			map.put("path", file.getAbsolutePath());
        		map.put("size", file.list().length);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
    	} else {
    		map.put("path", file.getAbsolutePath());
    		map.put("size", file.length());
    	}
    	
        return map;
	}

	@Override
	public boolean isFile(File file) {
		// TODO Auto-generated method stub
		if (fileName == null || fileName.length() <= 0 || file.getName().indexOf(fileName) >= 0) {
			return true;
		} else {
			return false;
		}
	}

}
