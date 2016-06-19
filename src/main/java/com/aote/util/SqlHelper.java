package com.aote.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.proxy.map.MapProxy;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;

public class SqlHelper {
	@SuppressWarnings("unchecked")
	public static JSONArray query(Session session, String sql) throws Exception {
		JSONArray array = new JSONArray();
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql, 0, 9999999);
		sqlCall.transformer = Transformers.ALIAS_TO_ENTITY_MAP;
		List<Map<String, Object>> list = (List<Map<String, Object>>) sqlCall
				.doInHibernate(session);
		for (Map<String, Object> map : list) {
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray query(Session session, String sql, int pageNo,
			int pageSize) throws Exception {
		JSONArray array = new JSONArray();
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql, pageNo, pageSize);
		sqlCall.transformer = Transformers.ALIAS_TO_ENTITY_MAP;
		List<Map<String, Object>> list = (List<Map<String, Object>>) sqlCall
				.doInHibernate(session);
		for (Map<String, Object> map : list) {
			JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
			array.put(json);
		}
		return array;
	}

	/**
	 * 执行sql
	 *
	 * @param session
	 * @param sql
	 * @return
	 */
	public static int bulkUpdate(Session session, String sql) {
		Query queryObject = session.createQuery(sql);
		return new Integer(queryObject.executeUpdate()).intValue();
	}

	@SuppressWarnings("rawtypes")
	static class HibernateSQLCall implements HibernateCallback {
		String sql;
		int page;
		int rows;
		public ResultTransformer transformer = null;

		public HibernateSQLCall(String sql, int page, int rows) {
			this.sql = sql;
			this.page = page;
			this.rows = rows;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createSQLQuery(sql);
			if (transformer != null) {
				q.setResultTransformer(transformer);
			}
			try {
				List result = q.setFirstResult(page * rows).setMaxResults(rows)
						.list();
				return result;
			} catch (SQLGrammarException ex) {
				// 把sql语句添加到异常信息中
				String msg = "sql:\n" + sql + "\n" + ex.getMessage();
				throw new SQLGrammarException(msg, ex.getSQLException());
			}
		}
	}

	static class JsonTransfer {
		private List<Map<String, Object>> transed = new ArrayList<Map<String, Object>>();

		@SuppressWarnings("unchecked")
		public Object MapToJson(Map<String, Object> map) throws Exception {
			if (contains(map))
				return JSONObject.NULL;
			transed.add(map);
			JSONObject json = new JSONObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (value == null) {
					value = JSONObject.NULL;
				} else if (value instanceof HashMap) {
					value = MapToJson((Map<String, Object>) value);
				} else if (value instanceof PersistentSet) {
					PersistentSet set = (PersistentSet) value;
					value = ToJson(set);
				} else if (value instanceof PersistentList) {
					PersistentList set = (PersistentList) value;
					value = ToJson(set);
				}
				if (key.equals("$type$")) {
					json.put("EntityType", value);
				} else if (value instanceof Date) {
					Date date = (Date) value;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String str = sdf.format(date);
					json.put(key, str);
				} else if (value instanceof MapProxy) {
				} else {
					json.put(key, value);
				}
			}
			return json;
		}

		@SuppressWarnings("unchecked")
		public Object ToJson(PersistentSet set) throws Exception {
			if (!set.wasInitialized()) {
				return JSONObject.NULL;
			}
			JSONArray array = new JSONArray();
			for (Object obj : set) {
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = (JSONObject) MapToJson(map);
				array.put(json);
			}
			return array;
		}

		@SuppressWarnings("unchecked")
		private Object ToJson(PersistentList list) throws Exception {
			if (!list.wasInitialized()) {
				return JSONObject.NULL;
			}
			JSONArray array = new JSONArray();
			for (Object obj : list) {
				if (obj == null)
					continue;
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = (JSONObject) MapToJson(map);
				array.put(json);
			}
			return array;
		}

		public boolean contains(Map<String, Object> obj) {
			for (Map<String, Object> map : this.transed) {
				if (obj == map) {
					return true;
				}
			}
			return false;
		}
	}
}
