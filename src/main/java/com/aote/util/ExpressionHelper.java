package com.aote.util;

import java.util.Map;

import com.af.expression.Delegate;
import com.af.expression.Program;

public class ExpressionHelper {

	/**
	 * 带参运行表达式
	 * @param source: 表达式源码
	 * @param params: 表达式参数
	 * @return: 表达式运行结果
	 */
	public static Object run(String source, Map<String, Object> params) {
		Program prog = new Program(source);
		// 解析
		Delegate d = prog.parse();
		Object result = d.invoke(params);
		return result;
	}
}
