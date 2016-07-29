package com.aote.util;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class WebFileConfigInfo {
	
	private static String configPath = SitePathInfo.getInstance().getWebInfPath() + "web.xml";
	
	public WebFileConfigInfo() {
	}
	
	public static String getConfigInfoPath() {
		return configPath;
	}
	
	@SuppressWarnings("unchecked")
	public static String getSettingValue(String param) {
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new File(configPath));
			Element root = document.getRootElement();
			
			for(Iterator<Element> i = root.elementIterator("context-param"); i.hasNext(); ) {
				Element e = i.next();
				if(e.elementText("param-name").equals(param)) {
					return e.elementText("param-value");
				}
			}
			return "";
			
		} catch (DocumentException e) {
			e.printStackTrace();
			return "";
		} 
	}
	
	public static int getSettingIntValue(String param) {
		try {
			String value = getSettingValue(param);
			return Integer.parseInt(value);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} 
	}
	
	public static double getSettingDoubleValue(String param) {
		try {
			String value = getSettingValue(param);
			return Double.parseDouble(value);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} 
	}

}
