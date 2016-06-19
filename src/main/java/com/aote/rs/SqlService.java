package com.aote.rs;

import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aote.sql.SqlServer;
import com.aote.util.ExceptionHelper;

/**
 * 提供sql查询服务
 */
@Path("sql")
@Component
@Transactional
public class SqlService {
	static Logger log = Logger.getLogger(SqlService.class);

	@Autowired
	private SqlServer sqlServer;

	/**
	 * 获取SQL语句的合计执行结果
	 * 
	 * @param name
	 *            : sql语句名
	 * @param str
	 *            : sql语句执行参数
	 */
	@POST
	@Path("{name}/n")
	public String txgetTotalCnt(@PathParam("name") String name, String str)
			throws Exception {
		try {
			JSONObject result = sqlServer.queryTotal(name, str);
			return result.toString();
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}

	/**
	 * 执行sql,对sql中的参数进行替换 pageNo - 页号，默认为1 pageSize - 每页个数，默认为1000
	 */
	@POST
	@Path("{name}")
	public String txExecute(@PathParam("name") String name,
			@QueryParam("pageNo") int pageNo,
			@QueryParam("pageSize") int pageSize, String str) throws Exception {
		try {
			JSONArray result = sqlServer.query(name, pageNo, pageSize, str);
			return result.toString();
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}
}
