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
 * �ṩsql��ѯ����
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

			// �������ݹ����Ķ�������
			String sql = getSql(name);
			
			sql = "$" + sql;
			
			// �õ�json�������
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
	 * ִ��sql,��sql�еĲ��������滻
	 */
	@POST
	@Path("/fallthrough/{name}")
	public JSONArray txFallThroughExecute(@PathParam("name") String name, String str) {
		try {
			// �������ݹ����Ķ�������
			String sql = getSql(name);
			sql = "$" + sql;
			// �õ�json�������
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
	 * ִ��sql,��sql�еĲ��������滻
	 */
	@POST
	@Path("{name}")
	public JSONArray txExecute(@PathParam("name") String name, @QueryParam("pageNo") int pageNo, @QueryParam("pageSize") int pageSize, String str) {
		try {
			// �������ݹ����Ķ�������
			String sql = getSql(name);
			sql = "$" + sql;
			// �õ�json�������
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

	// �õ�sql�ַ���
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

	// ���ַ���ת���ɿ�ִ��sql
	private String getExecSql(String sql, JSONObject params) {
		Program prog = new Program(sql);
		// ����
		Delegate d = prog.CommaExp().Compile();
		// getParamNames�������в����������ҵ��Ķ�����putParam�Ż�
		Set<String> objectNames = d.objectNames.keySet();
		for (String name : objectNames) {
			// ����name�ҵ��������Լ���д
			Object obj = getVarValue(name, params);
			// �Ѷ���Ż�objectNames
			d.objectNames.put(name, obj);
		}
		Object result = d.invoke();
		return result.toString();
	}

	// ���ݱ�����ȡ����ֵ
	private Object getVarValue(String name, JSONObject params) {
		try {
			// �����this�������߼���������
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