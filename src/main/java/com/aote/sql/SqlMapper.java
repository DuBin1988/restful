package com.aote.sql;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * sql映射器
 * @author LGY
 *
 */
public class SqlMapper {

	static Logger log = Logger.getLogger(SqlMapper.class);
	
	private static Map<String, String> map;
	
	public static synchronized String getSql(String alias)
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
		InputStream input = SqlMapper.class.getClassLoader().getResourceAsStream("sql.xml");
		try {
			Document document = reader.read(input);
			Element root = document.getRootElement();
			for (Iterator it = root.elementIterator("sql"); it.hasNext();) {
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
		SqlMapper.loadMap();
		for(String key : map.keySet())
			System.out.println(key);
	}
}
