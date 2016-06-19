package com.aote.rs.sqlservice;

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

public class TestError extends TestCase {
	public void testOne(){
		sql("error");
	}
	
	private void sql(String name) {
		try {
			String path="http://localhost:8081/restful/rs/sql/" + URLEncoder.encode(name).replace("+", "%20");
			// 创建POSTMethod
			HttpPost postMethod =new HttpPost(path);/*建立HTTP Post连线*/
			StringEntity se = new StringEntity("{data: {condition: '1=1'}}","UTF-8");
			postMethod.setEntity(se);
			// 执行POSTMethod
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(postMethod);
			int code = response.getStatusLine().getStatusCode();
			String actual = EntityUtils.toString(response.getEntity(), "UTF8");
			System.out.println(actual);
			assertEquals(500, code);
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
