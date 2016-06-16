
package com.aote.rs;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aote.sql.SqlServer;

/**
 * 提供sql查询服务
 */
@Path("sql")
@Component
public class SqlService {
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private SqlServer sqlServer;
	
	static Logger log = Logger.getLogger(SqlService.class);

	/**
	 * 获取SQL语句的合计执行结果
	 * @param name: sql语句名
	 * @param str: sql语句执行参数
	 * @return JSON格式Sql语句执行结果
	 * @throws JSONException
	 */
	@POST 
	@Path("{name}/n")
	public String txgetTotalCnt(@PathParam("name") String name, String str) throws JSONException 
	{
		JSONObject result = sqlServer.queryTotal(name, str); 
		return result.toString();
	}
	
	/**
	 * 执行sql,对sql中的参数进行替换
	 * pageNo - 页号，默认为1
	 * pageSize - 每页个数，默认为1000
	 */
	@POST
	@Path("{name}")
	public String txExecute(@PathParam("name") String name, @QueryParam("pageNo") int pageNo, @QueryParam("pageSize") int pageSize, String str) {
		JSONArray result = sqlServer.query(name, pageNo, pageSize, str); 
		return result.toString();
	}
}
