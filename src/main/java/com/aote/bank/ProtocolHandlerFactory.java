package com.aote.bank;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.aote.util.ResourceHelper;


public class ProtocolHandlerFactory 
{
	// json对象格式的配置
	private JSONObject config;
	
	private static ProtocolHandlerFactory instance = new ProtocolHandlerFactory();

	public static ProtocolHandlerFactory getInstance() {
		return instance;
	}

	private ProtocolHandlerFactory() {
		try {
			// 加载bank.json配置文件
			String result = ResourceHelper.getString("/bank.json");
			config = new JSONObject(result);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 获取处理协议的业务逻辑
	public String getProtocol(String id) {
		try {
			return config.getJSONObject(id).getString("logic");
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	// 获取处理协议的协议处理器，协议处理器可以对MD5进行处理
	public String getHandler(String id) {
		try {
			return config.getJSONObject(id).getString("logic");
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
}
