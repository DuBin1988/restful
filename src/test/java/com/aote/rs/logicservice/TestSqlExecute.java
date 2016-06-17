package com.aote.rs.logicservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import junit.framework.TestCase;

public class TestSqlExecute extends TestCase {
	public void testOne(){
		test("执行SQL");
	}

	private void test(String name) {
		try {
			String path="http://127.0.0.1:8081/restful/rs/logic/"+ URLEncoder.encode(name).replace("+", "%20");
			// POSTMethod
			HttpPost postMethod =new HttpPost(path);
			StringEntity se = new StringEntity("{f_name:'测试2成'}", "UTF-8");
			postMethod.setEntity(se);
			// 发送Post请求
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(postMethod);
			String actual = EntityUtils.toString(response.getEntity(), "UTF8");
			System.out.println(actual);
			int code = response.getStatusLine().getStatusCode();
			assertEquals(200, code);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
