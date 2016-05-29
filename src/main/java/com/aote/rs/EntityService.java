package com.aote.rs;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.aote.rs.util.Util;
import com.aote.util.JsonHelper;

@Path("entity")
@Scope("prototype")
@Component
public class EntityService {
	static Logger log = Logger.getLogger(EntityService.class);

	@Autowired
	private SessionFactory sessionFactory;

	@POST
	@Path("{entity}")
	// 保存实体
	public String xtSave(@Context HttpServletResponse response,
			@PathParam("entity") String entityName, String values)
			throws Exception {
		log.debug("entity:" + entityName + ", values:" + values);
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		try {
			JSONObject object = new JSONObject(values);
			JSONObject result = save(session, entityName, object);
			session.getTransaction().commit();
			return result.toString();
		} catch (StaleObjectStateException e) {
			session.getTransaction().rollback();
			response.setHeader("Warning", Util.encode("目前对象过于陈旧"));
			throw new WebApplicationException(500);
		} catch (RuntimeException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			response.sendError(500, e.getMessage());
			return e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			response.sendError(500, e.getMessage());
			return e.getMessage();
		} finally {
			if (session != null)
				session.close();
		}
	}

	@DELETE
	@Path("{entity}/{id}")
	// 删除实体
	public String txDelete(@PathParam("entity") String entityName,
			@PathParam("id") int id) {
		String hql = "delete from " + entityName + " where id=" + id;
		log.debug(hql);
		bulkUpdate(sessionFactory.getCurrentSession(), hql);
		return "ok";
	}

	/**
	 * 执行sql
	 *
	 * @param session
	 * @param sql
	 * @return
	 */
	private int bulkUpdate(Session session, String sql) {
		Query queryObject = session.createQuery(sql);
		return new Integer(queryObject.executeUpdate()).intValue();
	}

	// 执行内部保存实体过程
	private JSONObject save(Session session, String entityName,
			JSONObject object) throws Exception {
		JSONObject result = new JSONObject();
		Map<String, Object> map = JsonHelper.toMap(result, entityName,
				sessionFactory);
		// 把json对象转换成map
		session.saveOrUpdate(entityName, map);
		if (map.containsKey("id"))
			result.put("id", map.get("id"));
		if (map.containsKey("ID"))
			result.put("ID", map.get("ID"));
		return result;
	}
}
