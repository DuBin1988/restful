package com.aote.logic;

import java.util.Map;

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
			Map<String, Object> params = JsonHelper.toMap(param);
			// 附加entityServer, sqlServer对象到参数中
			params.put("entity", entityServer);
			params.put("sql", sqlServer);
			Object result = ExpressionHelper.run(source, params);
			return result;
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}	
}
