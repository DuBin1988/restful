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
	 * 
	 * @param name
	 *            : sql语句名
	 * @param str
	 *            : sql语句执行参数
	 * @return JSON格式Sql语句执行结果
	 * @throws JSONException
	 */
	public JSONObject queryTotal(String name, String str) throws Exception {
		// 获取参数，求和字段等内容
		JSONObject param = null;
		JSONArray sums = null;
		if (str != null && !str.isEmpty()) {
			JSONObject json = new JSONObject(str);
			if (json.has("data")) {
				param = json.getJSONObject("data");
			}
			if (json.has("sums")) {
				sums = json.getJSONArray("sums");
			}
		}
		Map<String, Object> params = JsonHelper.toMap(param);
		// 产生sql语句编译后的结果
		String sql = this.call(name, params);

		// 求和时，order by会导致sql错误，过滤掉order by部分。
		sql = filterOutOrderBy(sql, sums);

		Session session = sessionFactory.getCurrentSession();
		JSONArray array = SqlHelper.query(session, sql);
		return array.getJSONObject(0);
	}

	/**
	 * 执行sql分页查询
	 */
	public JSONArray query(String name, int pageNo, int pageSize, String str)
			throws Exception {
		// pageNo小于0， 纠正成1
		if (pageNo <= 0) {
			pageNo = 1;
		}

		// pageSize小于0，纠正成1
		if (pageSize < 1 || pageSize > 1000) {
			pageSize = 1000;
		}

		// 拿到json对象参数
		JSONObject param = null;
		if (str != null && !str.isEmpty()) {
			param = new JSONObject(str);
			param = param.getJSONObject("data");
		}
		Map<String, Object> params = JsonHelper.toMap(param);

		// 产生SQL语句编译后的结果
		String sql = this.call(name, params);

		Session session = sessionFactory.getCurrentSession();
		JSONArray array = SqlHelper.query(session, sql, pageNo - 1, pageSize);
		log.debug(array.toString());
		return array;
	}

	// 执行sql语句
	public void run(String sql) {
		Session session = sessionFactory.getCurrentSession();
		SqlHelper.bulkUpdate(session, sql);
	}

	// 调用其它sql语句, 产生sql语句经过参数处理的结果串
	public String call(String sqlName, Map<String, Object> params) {
		// 获取原始sql语句
		String path = SqlMapper.getSql(sqlName);
		String sql = ResourceHelper.getString("/sqls/" + path);

		// 获取编译后的sql语句
		sql = "$" + sql;
		// 把自身注册到执行环境中
		params.put("this", this);
		sql = ExpressionHelper.run(sql, params).toString();

		return sql;
	}

	// 过滤order by子句，产生求和结果
	private String filterOutOrderBy(String source, JSONArray sums)
			throws Exception {
		int idx = source.toLowerCase().lastIndexOf("order by");
		String sql = "select ";
		// 如果有求和部分，产生求和部分的语句
		if (sums != null) {
			for (int i = 0; i < sums.length(); i++) {
				String name = (String) sums.get(i);
				sql += "sum(" + name + ") " + name + ", ";
			}
		}
		if (idx != -1)
			sql += "count(*) n, '1' placeholder from ( "
					+ source.substring(0, idx) + ") ___t___";
		return sql;
	}
}
