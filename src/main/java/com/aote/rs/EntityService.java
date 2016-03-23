package com.aote.rs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.ListType;
import org.hibernate.type.LongType;
import org.hibernate.type.SetType;
import org.hibernate.type.TimeType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.aote.expression.ExpressionGenerator;
import com.aote.rs.util.Util;

@Path("entity")
@Scope("prototype")
@Component
public class EntityService {
	static Logger log = Logger.getLogger(EntityService.class);

	@Autowired
	private SessionFactory sessionFactory;

	@POST
	@Path("{entity}")
	public String xtSave(@Context HttpServletResponse response,
			@PathParam("entity") String entityName, String values)
			throws Exception {
		log.debug("entity:" + entityName + ", values:" + values);
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		try {
			JSONObject object = new JSONObject(values);
			save(session, entityName, object);
			session.getTransaction().commit();
			return "ok";
		} catch (Exception e) {
			session.getTransaction().rollback();
			if (e instanceof org.hibernate.StaleObjectStateException) {
				response.setHeader("Warning",
						Util.encode("目前的对象过于陈旧，因为它在其他地方已经被修改。"));
				throw new WebApplicationException(501);
			} else {
				response.setHeader("Warning", Util.encode(e.toString()));
				throw new WebApplicationException(501);
			}
		} finally {
			if (session != null)
				session.close();
		}
	}
	
	@DELETE
	@Path("{entity}/{id}")
	public String txDelete(@PathParam("entity") String entityName,
			@PathParam("id") int id) {
		String hql = "delete from " + entityName + " where id=" + id;
		log.debug(hql);
		bulkUpdate(sessionFactory.getCurrentSession(), hql);
		return "ok";
	}

	/**
	 * 返回updateCount
	 * 
	 * @param session
	 * @param sql
	 * @return
	 */
	private int bulkUpdate(Session session, String sql) {
		Query queryObject = session.createQuery(sql);
		return new Integer(queryObject.executeUpdate()).intValue();
	}
	
	// 内部保存过程，name为界面上传过来的要对象名字，返回的是后台表达式计算后的对象内容
	private JSONObject save(Session session, String entityName,
			JSONObject object) throws Exception {
		// 根据实体名字去除配置属性信息
		ClassMetadata classData = sessionFactory.getClassMetadata(entityName);
		JSONObject result = new JSONObject();
		// 把json对象转换成map
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iter = object.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = object.get(key);
			Type propType = null;
			try {
				propType = classData.getPropertyType(key);
			} catch (Exception e) {

			}

			if (object.isNull(key)) {
				// 空的id号不往属性里放，以便按新增处理
				if (!key.equals("id")) {
					map.put(key, null);
				}
			} else if (value instanceof JSONArray
					&& propType instanceof SetType) {
				// Json数组转换成一对多关系的Set
				Set<Map<String, Object>> set = saveSet(session,
						(JSONArray) value);
				map.put(key, set);
			} else if (value instanceof JSONArray
					&& propType instanceof ListType) {
				// Json数组转换成一对多关系的Set
				List<Map<String, Object>> set = saveList(session,
						(JSONArray) value);
				map.put(key, set);
			} else if (value instanceof JSONObject) {
				JSONObject obj = (JSONObject) value;
				String type = (String) obj.get("EntityType");
				Map<String, Object> set = saveWithoutExp(session, type,
						(JSONObject) value);
				map.put(key, set);
			} else if (propType != null
					&& (propType instanceof DateType || propType instanceof TimeType)) {
				long l = 0;
				if (value instanceof Double) {
					l = ((Double) value).longValue();
				} else if (value instanceof Long) {
					l = ((Long) value).longValue();
				} else if (value instanceof Integer) {
					l = ((Integer) value).intValue();
				}
				Date d = new Date(l);
				map.put(key, d);
			} else if (value instanceof Integer
					&& propType instanceof DoubleType) {
				// int直接转换成double
				Integer v = (Integer) value;
				map.put(key, v.doubleValue());
			} else if (value instanceof Integer && propType instanceof LongType) {
				Long v = Long.valueOf(value.toString());
				map.put(key, v.longValue());
			} else {
				// 有需要后台计算的表达式，后台计算后，把结果返回
				if (value instanceof String
						&& value.toString().indexOf("#") != -1) {
					// 调用表达式运算
					try {
						value = ExpressionGenerator.getExpressionValue(value
								.toString());
					} catch (Exception e) {
						log.debug(value + "表达式处理发生异常,使用self值");
					}
					result.put(key, value);
				}
				map.put(key, value);
			}
		}
		session.saveOrUpdate(entityName, map);
		if (map.containsKey("id"))
			result.put("id", map.get("id"));
		if (map.containsKey("ID"))
			result.put("ID", map.get("ID"));
		return result;
	}
	
	// 保存JSONArray里的对象，并转换为Set
	private Set<Map<String, Object>> saveSet(Session session, JSONArray array)
			throws JSONException {
		Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String type = (String) obj.get("EntityType");
			Map<String, Object> map = saveWithoutExp(session, type, obj);
			set.add(map);
		}
		return set;
	}

	// 保存JSONArray里的对象，并转换为Set
	private List<Map<String, Object>> saveList(Session session, JSONArray array)
			throws JSONException {
		List<Map<String, Object>> set = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String type = (String) obj.get("EntityType");
			Map<String, Object> map = saveWithoutExp(session, type, obj);
			set.add(map);
		}
		return set;
	}

	// 保存JsonObject，并转换为Map，保存时，不做后台表达式运算，用于一对多关系中的子的保存
	private Map<String, Object> saveWithoutExp(Session session,
			String entityName, JSONObject object) throws JSONException {
		// 根据实体名字去除配置属性信息
		ClassMetadata classData = sessionFactory.getClassMetadata(entityName);
		// 把json对象转换成map
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iter = object.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			Type propType = null;
			try {
				propType = classData.getPropertyType(key);
			} catch (Exception e) {

			}

			Object value = object.get(key);
			if (object.isNull(key)) {
				// 空的id号不往属性里放，以便按新增处理
				if (!key.equals("id")) {
					map.put(key, null);
				}
			} else if (value instanceof JSONArray) {
				// Json数组转换成一对多关系的Set
				Set<Map<String, Object>> set = saveSet(session,
						(JSONArray) value);
				map.put(key, set);
			} else if (propType != null
					&& (propType instanceof DateType || propType instanceof TimeType)) {
				long l = 0;
				if (value instanceof Double) {
					l = ((Double) value).longValue();
				} else if (value instanceof Long) {
					l = ((Long) value).longValue();
				}
				Date d = new Date(l);
				map.put(key, d);
			} else if (value instanceof Integer
					&& propType instanceof DoubleType) {
				// int直接转换成double
				Integer v = (Integer) value;
				map.put(key, v.doubleValue());
			} else if (value instanceof JSONObject) {
				JSONObject obj = (JSONObject) value;
				String type = (String) obj.get("EntityType");
				Map<String, Object> set = saveWithoutExp(session, type,
						(JSONObject) value);
				map.put(key, set);
			} else {
				map.put(key, value);
			}
		}
		session.saveOrUpdate(entityName, map);
		return map;
	}
}
