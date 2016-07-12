package com.aote.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.proxy.map.MapProxy;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonTransfer {
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
