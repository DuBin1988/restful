package com.aote.sqlmapper;

import junit.framework.TestCase;

import com.aote.sql.SqlMapper;

public class TestSqlMapper extends TestCase {
	public void testOne(){
		try {
			String item = SqlMapper.getSql("test");
			assertEquals(item, "sqls/test.sql"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
