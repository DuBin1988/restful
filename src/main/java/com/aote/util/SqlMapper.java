package com.aote.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * sql映射器
 * @author LGY
 *
 */
public class SqlMapper {

	static Logger log = Logger.getLogger(SqlMapper.class);
	
	private static Map<String, SqlItem> map;
	
	public static synchronized SqlItem getSql(String alias)
	{
		if(map == null)
			loadMap();
		if(map.containsKey(alias))
			return map.get(alias);
		else
			return null;
	}

	private static void loadMap() {
		map = new HashMap<String, SqlItem>();
		
		XStream xs = new XStream(new DomDriver("UTF-8"));
		xs.alias("cfg", SqlCfg.class);
		xs.addImplicitCollection(SqlItem.class, "items");
		xs.alias("item", SqlItem.class);
		xs.aliasField("alias", SqlItem.class, "alias");
		xs.aliasField("path", SqlItem.class, "path");
		xs.aliasField("hint", SqlItem.class, "hint");
		SqlCfg cfg = (SqlCfg) xs.fromXML(SqlMapper.class.getClassLoader().getResourceAsStream("sql.xml"));
		
		for(SqlItem item : cfg.items)
		{
			map.put(item.alias, item);
		}
	}
	
	public void main(String[] args) {
		SqlMapper.loadMap();
		for(String key : map.keySet())
			System.out.println(key);
	}
}
