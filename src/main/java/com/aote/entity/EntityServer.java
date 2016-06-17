package com.aote.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aote.util.JsonHelper;
import com.aote.util.SqlHelper;

@Component
@Transactional
public class EntityServer {
	static Logger log = Logger.getLogger(EntityServer.class);

	@Autowired
	private SessionFactory sessionFactory;

	// 保存实体
	public String save(String entityName, String values) {
		Session session = sessionFactory.getCurrentSession();
		try {
			JSONObject object = new JSONObject(values);
			// 把json对象转换成map
			Map<String, Object> map = JsonHelper.toMap(object, entityName, sessionFactory);
			JSONObject result = save(session, entityName, map);
			return result.toString();
		} catch (JSONException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	// 保存实体
	public String save(String entityName, HashMap<String, Object> map) {
		Session session = sessionFactory.getCurrentSession();
		JSONObject result = save(session, entityName, map);
		return result.toString();
	}
	
	// 删除实体
	public String delete(String entityName, int id) {
		String hql = "delete from " + entityName + " where id=" + id;
		log.debug(hql);
		SqlHelper.bulkUpdate(sessionFactory.getCurrentSession(), hql);
		return "ok";
	}
	
	// 执行内部保存实体过程
	private JSONObject save(Session session, String entityName, Map<String, Object> map) {
		JSONObject result = new JSONObject();
		session.saveOrUpdate(entityName, map);
		return result;
	}
	
}
