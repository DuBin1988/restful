package com.aote.entity;

import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aote.util.JsonHelper;

@Component
@Transactional
public class EntityServer {
	@Autowired
	private SessionFactory sessionFactory;

	// 保存实体
	public String save(String entityName, String values) {
		Session session = sessionFactory.getCurrentSession();
		try {
			JSONObject object = new JSONObject(values);
			JSONObject result = save(session, entityName, object);
			return result.toString();
		} catch (JSONException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	// 执行内部保存实体过程
	private JSONObject save(Session session, String entityName,
			JSONObject object) {
		JSONObject result = new JSONObject();
		// 把json对象转换成map
		Map<String, Object> map = JsonHelper.toMap(result, entityName, sessionFactory);
		session.saveOrUpdate(entityName, map);
		return result;
	}
}
