package com.aote.rs;

// 服务异常，当服务出现问题时，抛出此异常
public class ServiceException extends RuntimeException {
	private int status;
	
	public ServiceException(int status, String msg, Throwable cause) {
		super(msg, cause);
		this.status = status;
	}
	
	public int getStatus() {
		return this.status;
	}
}
