package com.aote;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import com.aote.rs.mapper.GeneralMapper;
import com.aote.rs.mapper.WebExceptionMapper;

@ApplicationPath("rs")
public class RestConfig extends ResourceConfig {
	public RestConfig() {
		packages(false, "com.aote.rs");
		// 注册错误处理机制
		this.register(WebExceptionMapper.class);
		this.register(GeneralMapper.class);
	}
}
