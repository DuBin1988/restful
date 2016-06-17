package com.aote.sql;

import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aote.util.ExpressionHelper;
import com.aote.util.JsonHelper;
import com.aote.util.ResourceHelper;
import com.aote.util.SqlHelper;

@Component
@Transactional
public class SqlServer {
	static Logger log = Logger.getLogger(SqlServer.class);

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 获取SQL语句的合计执行结果
	 * @param name: sql语句名
	 * @param str: sql语句执行参数
	 * @return JSON格式Sql语句执行结果
	 * @throws JSONException
	 */
	public JSONObject queryTotal(String name, String str) throws JSONException 
	{
		try {
			JSONObject jo = new JSONObject();

			// 获取原始sql语句
			String path = SqlMapper.getSql(name);
			String sql = ResourceHelper.getString("/sqls/" + path);
			
			sql = "$" + sql;
			
			// 获取编译后的sql语句
			JSONObject param = null;
			if(str != null && !str.isEmpty()) {
				log.debug(str);				
				param = new JSONObject(str);
				log.debug(param.get("condition"));
			}
			Map<String, Object> params = JsonHelper.toMap(param);
			sql = ExpressionHelper.run(sql, params).toString();
			
			// 求和时，order by会导致sql错误，过滤掉order by部分。
			sql = filterOutOrderBy(sql);
			
			Session session = sessionFactory.getCurrentSession();
			JSONArray array = SqlHelper.query(session, sql);
			jo.put("n", array.getJSONObject(0).getInt("n"));
			return jo;
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
	
	/**
	 * 执行sql分页查询
	 */
	public JSONArray query(String name, int pageNo, int pageSize, String str) {
		try {
			// pageNo小于0， 纠正成1
			if (pageNo <= 0) {
				pageNo = 1;
			}

			// pageSize小于0，纠正成1
			if (pageSize < 1 || pageSize > 1000) {
				pageSize = 1000;
			}

			// 解析传递过来的对象属性
			String path = SqlMapper.getSql(name);
			String sql = ResourceHelper.getString("/sqls/" + path);
			
			sql = "$" + sql;
			// 拿到json对象参数
			JSONObject param = null;
			if(str != null && !str.isEmpty()) {
				log.debug(str);
				param = new JSONObject(str);
				log.debug(param.toString());
			}
			Map<String, Object> params = JsonHelper.toMap(param);
			sql = ExpressionHelper.run(sql, params).toString();
			Session session = sessionFactory.getCurrentSession();
			JSONArray array = SqlHelper.query(session, sql, pageNo-1, pageSize);
			log.debug(array.toString());
			return array;
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// 执行sql语句
	public void run(String sql) {
		Session session = sessionFactory.getCurrentSession();
		SqlHelper.bulkUpdate(session, sql);
	}
	
	// 过滤order by子句
	private String filterOutOrderBy(String sql) {
		int idx = sql.toLowerCase().lastIndexOf(" order ");
		if(idx != -1)
			sql = "select count(*) n, '1' placeholder from ( " + sql.substring(0, idx) + ") ___t___";
		return sql;
	}
}
