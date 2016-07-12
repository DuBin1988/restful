package com.aote.logic;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.aote.sql.SqlMapper;

/**
 * 业务插件映射器
 * @author HNS
 *
 */
public class PluginMapper {

	static Logger log = Logger.getLogger(PluginMapper.class);
	
	private static Map<String, Object> map = loadMap();
	
	public static Map<String, Object> getPlugins()
	{
		return map;
	}

	@SuppressWarnings("rawtypes")
	private static Map<String, Object> loadMap() {
		Map<String, Object> map = new HashMap<String, Object>();

		SAXReader reader = new SAXReader();
		InputStream input = SqlMapper.class.getClassLoader().getResourceAsStream("plugins.xml");
		try {
			Document document = reader.read(input);
			Element root = document.getRootElement();
			for (Iterator it = root.elementIterator("plugin"); it.hasNext();) {
				Element elm = (Element) it.next();
				String alias = elm.attribute("alias").getValue();
				String className = elm.attribute("class").getValue();
				Object obj = Class.forName(className).newInstance();
				map.put(alias, obj);
			}
			return map;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void main(String[] args) {
		PluginMapper.loadMap();
		for(String key : map.keySet())
			System.out.println(key);
	}
}
