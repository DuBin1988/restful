package com.aote.sql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.proxy.map.MapProxy;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;

public class SqlHelper {
	public static JSONArray query(Session session, String sql) {
		JSONArray array = new JSONArray();
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql, 0, 9999999);
		sqlCall.transformer = Transformers.ALIAS_TO_ENTITY_MAP;
		List<Map<String, Object>> list =(List<Map<String, Object>>)sqlCall.doInHibernate(session);
		for (Map<String, Object> map : list) {
			JSONObject json = (JSONObject) new JsonTransfer()
					.MapToJson(map);
			array.put(json);
		}
		return array;	
	}

	public static JSONArray query(Session session, String sql, int pageNo, int pageSize) {
		JSONArray array = new JSONArray();
		HibernateSQLCall sqlCall = new HibernateSQLCall(sql, pageNo, pageSize);
		sqlCall.transformer = Transformers.ALIAS_TO_ENTITY_MAP;
		List<Map<String, Object>> list =(List<Map<String, Object>>)sqlCall.doInHibernate(session);
		for (Map<String, Object> map : list) {
			JSONObject json = (JSONObject) new JsonTransfer()
					.MapToJson(map);
			array.put(json);
		}
		return array;	
	}
	
	static class HibernateSQLCall implements HibernateCallback {
		String sql;
		int page;
		int rows;
		// ��ѯ���ת����������ת����Map�ȡ�
		public ResultTransformer transformer = null;

		public HibernateSQLCall(String sql, int page, int rows) {
			this.sql = sql;
			this.page = page;
			this.rows = rows;
		}

		public Object doInHibernate(Session session) {
			Query q = session.createSQLQuery(sql);
			// ��ת����������ת����
			if (transformer != null) {
				q.setResultTransformer(transformer);
			}
			List result = q.setFirstResult(page * rows).setMaxResults(rows)
					.list();
			return result;
		}
	}
	
	// ת��������ת���ڼ��������Ƿ��Ѿ�ת��������������ת����������ѭ��
	static class JsonTransfer {
		// �����Ѿ�ת�����Ķ���
		private List<Map<String, Object>> transed = new ArrayList<Map<String, Object>>();

		// �ѵ���mapת����JSON����
		public Object MapToJson(Map<String, Object> map) {
			// ת���������ؿն���
			if (contains(map))
				return JSONObject.NULL;
			transed.add(map);
			JSONObject json = new JSONObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				try {
					String key = entry.getKey();
					Object value = entry.getValue();
					// ��ֵת����JSON�Ŀն���
					if (value == null) {
						value = JSONObject.NULL;
					} else if (value instanceof HashMap) {
						value = MapToJson((Map<String, Object>) value);
					} else if (value instanceof PersistentSet) {
						PersistentSet set = (PersistentSet) value;
						value = ToJson(set);
					}
					 else if (value instanceof PersistentList) {
						 PersistentList set = (PersistentList) value;
						value = ToJson(set);
					}
					// �����$type$����ʾʵ�����ͣ�ת����EntityType
					if (key.equals("$type$")) {
						json.put("EntityType", value);
					} else if (value instanceof Date) {
						Date date = (Date) value;
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
						String str=sdf.format(date);
						json.put(key, str);
					} else if (value instanceof MapProxy) {
						// MapProxyû�м��أ�����
					} else {
						json.put(key, value);
					}
				} catch (JSONException e) {
					throw new WebApplicationException(400);
				}
			}
			return json;
		}

		// �Ѽ���ת����Json����
		public Object ToJson(PersistentSet set) {
			// û���صļ��ϵ�����
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

		// �������б�ת����Json����
		private Object ToJson(PersistentList list) {
			// û���صļ��ϵ�����
			if (!list.wasInitialized()) {
				return JSONObject.NULL;
			}
			JSONArray array = new JSONArray();
			for (Object obj : list) {
				if(obj == null)
					continue;
				Map<String, Object> map = (Map<String, Object>) obj;
				JSONObject json = (JSONObject)MapToJson(map);
				array.put(json);
			}
			return array;
		}
		
		// �ж��Ѿ�ת�������������Ƿ������������
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
