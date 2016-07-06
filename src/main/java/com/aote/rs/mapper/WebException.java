package com.aote.rs.mapper;

// 含返回码及返回信息的异常
public class WebException extends RuntimeException {
	public int status;
	public WebException(int status, String msg) {
		super(msg);
		this.status = status;
	}
}
