package com.aote.logic;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aote.entity.EntityServer;
import com.aote.sql.SqlServer;
import com.aote.util.ExpressionHelper;
import com.aote.util.JsonHelper;
import com.aote.util.ResourceHelper;
import com.aote.util.Util;

@Component
public class LogicServer {
	static Logger log = Logger.getLogger(LogicServer.class);

	@Autowired
	private EntityServer entityServer;

	@Autowired
	private SqlServer sqlServer;

	// 执行业务逻辑处理过程
	public Object run(String name, String str) throws Exception {
		// 获取源程序内容
		String path = LogicMapper.getLogic(name);
		String source = ResourceHelper.getString("/logics/" + path);
		// 处理回车换行
		source = source.replace("\r\n", "\n");
		// 执行源程序
		Map<String, Object> params = new HashMap<String, Object>();
		// 把传递过来的参数，放到data里，以便跟entity，sql等对象区别开来
		JSONObject param = new JSONObject(str);
		param = param.getJSONObject("data");
		params.put("data", param);
		// 附加entityServer, sqlServer等对象到参数中
		params.put("log", log);
		params.put("entity", entityServer);
		params.put("sql", sqlServer);
		// 附加用户注册的对象到业务逻辑中
		Map<String, Object> plugins = PluginMapper.getPlugins();
		for(String key : plugins.keySet()) {
			params.put(key, plugins.get(key));
		}
		Object result = ExpressionHelper.run(source, params);
		return result;
	}
}
