package com.aote.util;

import org.json.JSONObject;

import com.aote.rs.mapper.WebException;

// 为业务逻辑提供各种辅助功能
public class Util {
	// 抛异常
	public void error(JSONObject param) {
		int status = Integer.parseInt(param.get("status").toString());
		String msg = (String)param.get("msg");
		throw new WebException(status, msg);
	}
}
