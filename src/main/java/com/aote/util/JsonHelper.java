package com.aote.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.ListType;
import org.hibernate.type.LongType;
import org.hibernate.type.SetType;
import org.hibernate.type.TimeType;
import org.hibernate.type.Type;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {
	/**
	 * 把JSON对象按照hibernate的配置转换成map
	 * 
	 * @param object
	 *            : JSON对象
	 * @param entityType
	 *            : 实体类型
	 * @param sessionFactory
	 * @return：map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(JSONObject object,
			String entityType, SessionFactory sessionFactory)
			throws ParseException, JSONException {
		// 获得实体元数据
		ClassMetadata classData = sessionFactory.getClassMetadata(entityType);

		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iter = object.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = null;
			try {
				value = object.get(key);
			} catch (JSONException e) {
				throw new RuntimeException("数据错误，key: " + key, e);
			}

			// 获取字段类型，如果字段不存在，继续看下一个
			Type propType = null;
			try {
				propType = classData.getPropertyType(key);
			} catch (HibernateException e) {
				continue;
			}

			// id号字段为空，不放到数据里，以便当做插入处理
			if (object.isNull(key)) {
				if (!key.equals("id")) {
					map.put(key, null);
				}
			} else if (value instanceof JSONArray
					&& propType instanceof SetType) {
				// 把JSON集合转换成集合
				Set<Map<String, Object>> set = saveSet((JSONArray) value,
						sessionFactory);
				map.put(key, set);
			} else if (value instanceof JSONArray
					&& propType instanceof ListType) {
				// 把JSON集合转换成List
				List<Map<String, Object>> set = saveList((JSONArray) value,
						sessionFactory);
				map.put(key, set);
			} else if (value instanceof JSONObject) {
				JSONObject obj = (JSONObject) value;
				String type = null;
				try {
					type = (String) obj.get("EntityType");
				} catch (JSONException e) {
					throw new RuntimeException("缺少EntityType", e);
				}
				Map<String, Object> set = toMap((JSONObject) value, type,
						sessionFactory);
				map.put(key, set);
			} else if (propType != null
					&& (propType instanceof DateType || propType instanceof TimeType)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(value.toString());
				map.put(key, date);
			} else if (value instanceof Integer
					&& propType instanceof DoubleType) {
				// 整形转double
				Integer v = (Integer) value;
				map.put(key, v.doubleValue());
			} else if (value instanceof Integer && propType instanceof LongType) {
				// 整形转long
				Long v = Long.valueOf(value.toString());
				map.put(key, v.longValue());
			} else {
				map.put(key, value);
			}
		}

		return map;
	}

	// 把参数格式的JSON转换成map
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(JSONObject object) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		// null对象转换成空map
		if (object == null) {
			return map;
		}

		Iterator<String> iter = object.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = null;
			try {
				value = object.get(key);
			} catch (JSONException e) {
				throw new RuntimeException("数据错误，key: " + key, e);
			}

			if (value instanceof JSONArray) {
				// 把JSON集合转换成List
				List<Map<String, Object>> set = saveList((JSONArray) value);
				map.put(key, set);
			} else if (value instanceof JSONObject) {
				Map<String, Object> set = toMap((JSONObject) value);
				map.put(key, set);
			} else {
				map.put(key, value);
			}
		}

		return map;
	}

	// 把Map对象内容按照hibernate配置进行转换，重点是要把日期格式字符串转换成日期
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> toMap(Map<String, Object> object,
			String entityType, SessionFactory sessionFactory) throws Exception {
		// 获得实体元数据
		ClassMetadata classData = sessionFactory.getClassMetadata(entityType);

		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iter = object.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = object.get(key);

			// 获取字段类型，如果字段不存在，继续看下一个
			Type propType = null;
			try {
				propType = classData.getPropertyType(key);
			} catch (HibernateException e) {
				continue;
			}

			// id号字段为空，不放到数据里，以便当做插入处理
			if (value == null) {
				if (!key.equals("id")) {
					map.put(key, null);
				}
			} else if (value instanceof List && propType instanceof SetType) {
				// 把JSON集合转换成集合
				Set<Map<String, Object>> set = saveSet((List) value,
						sessionFactory);
				map.put(key, set);
			} else if (value instanceof List && propType instanceof ListType) {
				// 把JSON集合转换成List
				List<Map<String, Object>> set = saveList((List) value,
						sessionFactory);
				map.put(key, set);
			} else if (value instanceof Map) {
				Map<String, Object> obj = (Map<String, Object>) value;
				String type = (String) obj.get("EntityType");
				if (type == null) {
					throw new RuntimeException("缺少EntityType");
				}
				Map<String, Object> set = toMap(obj, type, sessionFactory);
				map.put(key, set);
			} else if (propType != null
					&& (propType instanceof DateType || propType instanceof TimeType)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(value.toString());
				map.put(key, date);
			} else if (value instanceof Integer
					&& propType instanceof DoubleType) {
				// 整形转double
				Integer v = (Integer) value;
				map.put(key, v.doubleValue());
			} else if (value instanceof Integer && propType instanceof LongType) {
				// 整形转long
				Long v = Long.valueOf(value.toString());
				map.put(key, v.longValue());
			} else {
				map.put(key, value);
			}
		}

		return map;
	}

	// 把Json集合转换成set
	private static Set<Map<String, Object>> saveSet(JSONArray array,
			SessionFactory sessionFactory) throws JSONException, ParseException {
		Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String type = (String) obj.get("EntityType");
			Map<String, Object> map = toMap(obj, type, sessionFactory);
			set.add(map);
		}
		return set;
	}

	// 把Json集合转换成列表
	private static List<Map<String, Object>> saveList(JSONArray array,
			SessionFactory sessionFactory) throws JSONException, ParseException {
		List<Map<String, Object>> set = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String type = (String) obj.get("EntityType");
			Map<String, Object> map = toMap(obj, type, sessionFactory);
			set.add(map);
		}
		return set;
	}

	// 把参数方式的Json集合转换成列表
	private static List<Map<String, Object>> saveList(JSONArray array)
			throws Exception {
		List<Map<String, Object>> set = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			Map<String, Object> map = toMap(obj);
			set.add(map);
		}
		return set;
	}

	// 把列表中的Map转换成Set
	private static Set<Map<String, Object>> saveSet(
			List<Map<String, Object>> array, SessionFactory sessionFactory)
			throws Exception {
		Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
		for (Map<String, Object> map : set) {
			String type = (String) map.get("EntityType");
			set.add(toMap(map, type, sessionFactory));
		}
		return set;
	}

	// 把列表中的Map转换List
	private static List<Map<String, Object>> saveList(
			List<Map<String, Object>> array, SessionFactory sessionFactory)
			throws Exception {
		List<Map<String, Object>> set = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : set) {
			String type = (String) map.get("EntityType");
			set.add(toMap(map, type, sessionFactory));
		}
		return set;
	}
}
