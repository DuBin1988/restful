package com.aote.rs;

import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aote.logic.LogicServer;
import com.aote.util.ExceptionHelper;
import com.aote.util.JsonTransfer;

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
	 * 
	 * @param logicName
	 * @param values
	 * @return
	 */
	@POST
	@Path("{logic}")
	public String xtSave(@PathParam("logic") String logicName, String values)
			throws Exception {
		log.debug("logic:" + logicName + ", values:" + values);
		try {
			Object result = logicServer.run(logicName, values);
			if (result == null) {
				return "";
			}
			// 如果执行结果为Map，转换成JSON串
			if (result instanceof Map<?, ?>) {
				Map<String, Object> map = (Map<String, Object>)result;
				JSONObject json = (JSONObject) new JsonTransfer().MapToJson(map);
				return json.toString();
			}
			return result.toString();
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}
}
