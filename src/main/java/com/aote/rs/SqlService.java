package com.aote.rs;

import java.io.RandomAccessFile;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.af.expression.Delegate;
import com.af.expression.Program;
import com.aote.rs.util.SqlHelper;

/**
 * 提供sql查询服务
 */
@Path("sql")
@Component
public class SqlService {
	@Autowired
	private SessionFactory sessionFactory;
	
	static Logger log = Logger.getLogger(SqlService.class);

	@POST 
	@Path("{name}/n")
	public JSONObject txgetTotalCnt(@PathParam("name") String name, String str) throws JSONException 
	{
		try {
			JSONObject jo = new JSONObject();

			// 解析传递过来的对象属性
			String sql = getSql(name);
			
			sql = "$" + sql;
			
			// 拿到json对象参数
			JSONObject param = null;
			if(str != null && !str.isEmpty()) {
				log.debug(str);				
				param = new JSONObject(str);
				log.debug(param.get("condition"));
			}
			sql = getExecSql(sql, param);
			
			sql = filterOutOrderBy(sql);
			
			Session session = sessionFactory.getCurrentSession();
			JSONArray array = SqlHelper.query(session, sql);
			jo.put("n", array.getJSONObject(0).getInt("n"));
			return jo;
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String filterOutOrderBy(String sql) {
		int idx = sql.toLowerCase().lastIndexOf(" order ");
		if(idx != -1)
			sql = "select count(*) n, '1' placeholder from ( " + sql.substring(0, idx) + ") ___t___";
		return sql;
	}

	/**
	 * 执行sql,对sql中的参数进行替换
	 */
	@POST
	@Path("/fallthrough/{name}")
	public JSONArray txFallThroughExecute(@PathParam("name") String name, String str) {
		try {
			// 解析传递过来的对象属性
			String sql = getSql(name);
			sql = "$" + sql;
			// 拿到json对象参数
			JSONObject param = null;
			if(str != null && !str.isEmpty()) {
				param = new JSONObject(str);
				log.debug(str);
			}
			sql = getExecSql(sql, param);
			Session session = sessionFactory.getCurrentSession();
			JSONArray array = SqlHelper.query(session, sql);
			return array;
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 执行sql,对sql中的参数进行替换
	 */
	@POST
	@Path("{name}")
	public JSONArray txExecute(@PathParam("name") String name, @QueryParam("pageNo") int pageNo, @QueryParam("pageSize") int pageSize, String str) {
		try {
			// 解析传递过来的对象属性
			String sql = getSql(name);
			sql = "$" + sql;
			// 拿到json对象参数
			JSONObject param = null;
			if(str != null && !str.isEmpty()) {
				log.debug(str);				
				param = new JSONObject(str);
				log.debug(param.get("condition"));
			}
			sql = getExecSql(sql, param);
			Session session = sessionFactory.getCurrentSession();
			JSONArray array = SqlHelper.query(session, sql, pageNo-1, pageSize);
			return array;
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 拿到sql字符串
	private String getSql(String str) {
		String sql = null;
		try {
			String path = this.getClass().getClassLoader().getResource("/sqls")
					.getPath();
			path += str;
			RandomAccessFile file = new RandomAccessFile(path, "r");
			byte[] b = new byte[(int) file.length()];
			file.read(b);
			file.close();
			sql = new String(b);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sql;
	}

	// 把字符串转换成可执行sql
	private String getExecSql(String sql, JSONObject params) {
		Program prog = new Program(sql);
		// 解析
		Delegate d = prog.CommaExp().Compile();
		// getParamNames返回所有参数名，把找到的对象用putParam放回
		Set<String> objectNames = d.objectNames.keySet();
		for (String name : objectNames) {
			// 根据name找到对象，需自己编写
			Object obj = getVarValue(name, params);
			// 把对象放回objectNames
			d.objectNames.put(name, obj);
		}
		Object result = d.invoke();
		return result.toString();
	}

	// 根据变量名取变量值
	private Object getVarValue(String name, JSONObject params) {
		try {
			// 如果是this，返回逻辑服务自身
			if (name.equals("this")) {
				return this;
			}
			if (params != null && params.has(name)) {
				return params.get(name);
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}