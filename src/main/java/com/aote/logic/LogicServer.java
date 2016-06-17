package com.aote.logic;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aote.entity.EntityServer;
import com.aote.sql.SqlServer;
import com.aote.util.ExpressionHelper;
import com.aote.util.JsonHelper;
import com.aote.util.ResourceHelper;

@Component
public class LogicServer {
	static Logger log = Logger.getLogger(LogicServer.class);

	@Autowired
	private EntityServer entityServer;
	
	@Autowired
	private SqlServer sqlServer;
		
	// 执行业务逻辑处理过程
	public Object run(String name, String str) {
		// 获取源程序内容
		String source = ResourceHelper.getString("/logics/" + name);
		// 处理回车换行
		source = source.replace("\r\n", "\n");
		// 执行源程序
		try {
			JSONObject param = new JSONObject(str);
			// 把传递过来的参数，放到data里，以便跟entity，sql等对象区别开来
			HashMap<String, Object> data = (HashMap<String, Object>)JsonHelper.toMap(param);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("data", data);
			// 附加entityServer, sqlServer等对象到参数中
			params.put("log", log);
			params.put("entity", entityServer);
			params.put("sql", sqlServer);
			Object result = ExpressionHelper.run(source, params);
			return result;
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}	
}
