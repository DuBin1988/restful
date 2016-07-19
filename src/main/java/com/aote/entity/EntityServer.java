package com.aote.entity;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aote.util.JsonHelper;
import com.aote.util.JsonTransfer;
import com.aote.util.SqlHelper;

@Component
@Transactional
public class EntityServer {
	static Logger log = Logger.getLogger(EntityServer.class);

	@Autowired
	private SessionFactory sessionFactory;

	// 保存实体
	public String save(String entityName, String values) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		JSONObject object = new JSONObject(values);
		// 获取要保存的数据
		JSONObject data = object.getJSONObject("data");
		// 把json对象转换成map
		Map<String, Object> map = JsonHelper.toMap(data, entityName,
				sessionFactory);
		JSONObject result = save(session, entityName, map);
		return result.toString();
	}

	// 保存实体
	public String save(String entityName, Map<String, Object> map)
			throws Exception {
		Session session = sessionFactory.getCurrentSession();
		Map<String, Object> data = JsonHelper.toMap(map, entityName,
				sessionFactory);
		JSONObject result = save(session, entityName, data);
		return result.toString();
	}

	// 保存json对象格式的实体
	public String save(String entityName, JSONObject json) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		Map<String, Object> data = JsonHelper.toMap(json, entityName,
				sessionFactory);
		JSONObject result = save(session, entityName, data);
		return result.toString();
	}

	// 删除实体
	public String delete(String entityName, int id) {
		String hql = "delete from " + entityName + " where id=" + id;
		SqlHelper.bulkUpdate(sessionFactory.getCurrentSession(), hql);
		return "ok";
	}

	// 加载实体
	public JSONObject load(String entityName, Object id) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		Object result = session.load(entityName, (Serializable) id);
		Map<String, Object> map = (Map<String, Object>)result;
		JSONObject obj = (JSONObject)new JsonTransfer().MapToJson(map);
		return obj;
	}

	// 执行内部保存实体过程
	private JSONObject save(Session session, String entityName,
			Map<String, Object> map) {
		JSONObject result = new JSONObject();
		session.saveOrUpdate(entityName, map);
		return result;
	}

}
