package com.aote.sqlmapper;

import junit.framework.TestCase;

import com.aote.util.SqlItem;
import com.aote.util.SqlMapper;

public class TestSqlMapper extends TestCase {
	public void testOne(){
		try {
			SqlItem item = SqlMapper.getSql("test");
			assertEquals(item.alias, "test"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
