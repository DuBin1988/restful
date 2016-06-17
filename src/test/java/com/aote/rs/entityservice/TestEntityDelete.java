package com.aote.rs.entityservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class TestEntityDelete extends TestCase {
	public void testOne(){
		try {
			String path="http://127.0.0.1:8081/restful/rs/entity/t_project/10";
			// POSTMethod  
			HttpDelete postMethod =new HttpDelete(path);
			// 调用POST方法
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(postMethod);
			int code = response.getStatusLine().getStatusCode();
			assertEquals(200, code);
			// 返回成功
			if (code == 200) {
				String actual = EntityUtils.toString(response.getEntity(), "UTF8");
			}
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
