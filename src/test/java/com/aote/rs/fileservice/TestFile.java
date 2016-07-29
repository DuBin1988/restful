package com.aote.rs.fileservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class TestFile extends TestCase {
	public void testOne(){
		testGetRootPath(" ");
		testGetRootPath("txt");
		testReadFile("D:\\1\\2\\2.txt");
		testReadFolder("D:\\1", true);
		testWriteFile("000000\r\n".getBytes(), "D:\\1\\2\\2.txt");
		testAppendFile("aaaa\r\n".getBytes(), "D:\\1\\2\\2.txt");
		testAppendFile("qqqqq\r\n".getBytes(), "D:\\1\\2\\2.txt");
		testFindFile("2", "D:\\1");
		testDeleteFile("D:\\1\\2\\2 - 副本 (2).txt");
		testDownloadFile("checkplanroot", "2.txt");
		testUploadFile("checkplanroot", "t1.bin", "E:\\t1.bin");
	}
	
	private void testGetRootPath(String fileType) {
		try {
			String postPath="http://127.0.0.1:8081/restful/rs/file/getpath/" + URLEncoder.encode(fileType).replace("+", "%20");
			// POSTMethod
			HttpGet postMethod =new HttpGet(postPath);
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
	
	private void testReadFile(String path) {
		try {
			String postPath="http://127.0.0.1:8081/restful/rs/file/readfile/"+URLEncoder.encode(URLEncoder.encode(path));
			// POSTMethod
			HttpGet postMethod =new HttpGet(postPath);
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
	
	private void testReadFolder(String path, boolean readChild) {
		try {
			String postPath="http://127.0.0.1:8081/restful/rs/file/readfolder/"+URLEncoder.encode(URLEncoder.encode(path))+"/"+readChild;
			// POSTMethod
			HttpGet postMethod =new HttpGet(postPath);
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
	
	private void testWriteFile(byte[] values, String path) {
		try {
			String postPath = "http://127.0.0.1:8081/restful/rs/file/write/"+URLEncoder.encode(URLEncoder.encode(path));
			// POSTMethod
			HttpPost postMethod =new HttpPost(postPath);
			ByteArrayEntity se = new ByteArrayEntity(values);
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
	
	private void testAppendFile(byte[] values, String path) {
		try {
			String postPath="http://127.0.0.1:8081/restful/rs/file/append/"+URLEncoder.encode(URLEncoder.encode(path));
			// POSTMethod
			HttpPost postMethod =new HttpPost(postPath);
			ByteArrayEntity se = new ByteArrayEntity(values);
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
	
	private void testFindFile(String fileName, String path) {
		try {
			String postPath="http://127.0.0.1:8081/restful/rs/file/findfile/"+URLEncoder.encode(URLEncoder.encode(path))+"/"+fileName;
			// POSTMethod
			HttpGet postMethod = new HttpGet(postPath);
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
	
	private void testDeleteFile(String path) {
		try {
			String postPath="http://127.0.0.1:8081/restful/rs/file/delete/"+URLEncoder.encode(URLEncoder.encode(path));
			// POSTMethod
			HttpGet postMethod =new HttpGet(postPath);
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
	
	private void testUploadFile(String fileType, String fileName, String filePath) {
		try {
			String postPath="http://127.0.0.1:8081/restful/rs/file/upload/"+fileType+"/"+fileName;
			// POSTMethod
			HttpPost postMethod =new HttpPost(postPath);
			File file = new File(filePath);
			InputStreamEntity se = new InputStreamEntity(new FileInputStream(file), file.length());
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
	
	private void testDownloadFile(String fileType, String fileName) {
		try {
			String postPath="http://127.0.0.1:8081/restful/rs/file/download/"+fileType+"/"+fileName;
			// POSTMethod
			HttpGet postMethod =new HttpGet(postPath);
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
