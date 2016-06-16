package com.aote.rs;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aote.entity.EntityServer;

@Path("entity")
@Singleton
@Component
@Transactional
public class EntityService {
	static Logger log = Logger.getLogger(EntityService.class);

	@Autowired
	private EntityServer entityServer;
	
	@POST
	@Path("{entity}")
	// 保存实体
	public String xtSave(@PathParam("entity") String entityName, String values) {
		log.debug("entity:" + entityName + ", values:" + values);
		try {
			String result = entityServer.save(entityName, values);
			return result;
		} catch (RuntimeException ex) {
			StringWriter sw = new StringWriter();
			PrintWriter w = new PrintWriter(sw);
			if (ex.getCause() != null) {
				ex.getCause().printStackTrace(w);
			} else {
				ex.printStackTrace(w);
			}
			log.error(sw.toString());
			throw ex;
		}
	}

	@DELETE
	@Path("{entity}/{id}")
	// 删除实体
	public String txDelete(@PathParam("entity") String entityName, @PathParam("id") int id) {
		return entityServer.delete(entityName, id);
	}
}
