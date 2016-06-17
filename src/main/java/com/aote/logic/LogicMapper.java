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
 * logic映射器
 * @author HNS
 *
 */
public class LogicMapper {

	static Logger log = Logger.getLogger(LogicMapper.class);
	
	private static Map<String, String> map;
	
	public static synchronized String getLogic(String alias)
	{
		if(map == null)
			loadMap();
		if(map.containsKey(alias))
			return map.get(alias);
		else
			return null;
	}

	private static void loadMap() {
		map = new HashMap<String, String>();

		SAXReader reader = new SAXReader();
		InputStream input = SqlMapper.class.getClassLoader().getResourceAsStream("logic.xml");
		try {
			Document document = reader.read(input);
			Element root = document.getRootElement();
			for (Iterator it = root.elementIterator("logic"); it.hasNext();) {
				Element elm = (Element) it.next();
				String alias = elm.attribute("alias").getValue();
				String path = elm.attribute("path").getValue();
				map.put(alias, path);
			}
		} catch (DocumentException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void main(String[] args) {
		LogicMapper.loadMap();
		for(String key : map.keySet())
			System.out.println(key);
	}
}
