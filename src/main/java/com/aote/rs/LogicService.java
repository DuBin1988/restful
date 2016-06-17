package com.aote.rs;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aote.logic.LogicServer;

@Path("logic")
@Singleton
@Component
@Transactional
public class LogicService {
	static Logger log = Logger.getLogger(LogicService.class);

	@Autowired
	private LogicServer logicServer;
	
	/**
	 * 执行业务逻辑
	 * @param logicName
	 * @param values
	 * @return
	 */
	@POST
	@Path("{logic}")
	public String xtSave(@PathParam("logic") String logicName, String values) {
		log.debug("logic:" + logicName + ", values:" + values);
		try {
			Object result = logicServer.run(logicName, values);
			if (result == null) {
				return null;
			}
			return result.toString();
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
}
