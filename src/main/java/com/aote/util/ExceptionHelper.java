package com.aote.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHelper {

	// 把异常堆栈转换成字符串
	public static String stackToString(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter w = new PrintWriter(sw);
		ex.printStackTrace(w);
		w.close();
		// 字符串关闭，产生的异常可以不管。
		try {
			sw.close();
		} catch (Exception e) {
		}
		return sw.toString();
	}
}
