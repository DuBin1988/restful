package com.aote.rs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.aote.file.FileCheck;
import com.aote.file.FileServer;
import com.aote.util.ExceptionHelper;
import com.aote.util.SitePathInfo;
import com.aote.util.WebFileConfigInfo;

@Path("file")
@Singleton
@Component
@Transactional
public class FileService {
	static Logger log = Logger.getLogger(FileService.class);

	@Autowired
	private FileServer fileServer;
	
	@GET
	@Path("getpath/{fileType}")
	//读取相关文件类型根目录
	public String xtRootPath(@PathParam("fileType") String fileType) {
		log.debug("获取根目录:" + fileType);
		if (fileType.endsWith(" ")) {
			return SitePathInfo.getInstance().getRootPhysicalPath();
		} else if (fileType != null && fileType.length() > 0) {
			return WebFileConfigInfo.getSettingValue(fileType);
		} else {
			return "";
		}
	}
	
	@GET
	@Path("readfile/{filePath}")
	// 读取文件
	public byte[] xtRead(@PathParam("filePath") String filePath)
			throws Exception {
		//传入要求两次加密
		filePath = URLDecoder.decode(filePath);
		log.debug("读取文件:" + filePath);
		try {
			return fileServer.read(filePath);
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}
	
	@GET
	@Path("readfolder/{path}/{readChild}")
	//读取文件夹
	public String xtRead(@PathParam("path") String path, @PathParam("readChild")boolean readChild)
			throws Exception {
		//传入要求两次加密
		path = URLDecoder.decode(path);
		log.debug("读取文件夹:" + path + "|" + readChild);
		try {
			return fileServer.read(path, readChild).toString();
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}

	@POST
	@Path("write/{filePath}")
	//写入内容到制定文件
	public String xtWrite(@PathParam("filePath") String filePath, @RequestParam("values") byte[] values)
			throws Exception {
		//传入要求两次加密
		filePath = URLDecoder.decode(filePath);
		log.debug("写入文件内容:" + new String(values) + "|" + filePath);
		try {
			return fileServer.write(values, values.length, filePath, false);
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}
	
	@POST
	@Path("append/{filePath}")
	//写入内容到制定文件
	public String xtAppend(@PathParam("filePath") String filePath, @RequestParam("values") byte[] values)
			throws Exception {
		//传入要求两次加密
		filePath = URLDecoder.decode(filePath);
		log.debug("追加文件内容:" + new String(values) + "|" + filePath);
		try {
			return fileServer.write(values, values.length, filePath, true);
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}

	@GET
	@Path("delete/{path}")
	//查找文件
	public String xtDelete(@PathParam("path") String path)
			throws Exception {
		//传入要求两次加密
		path = URLDecoder.decode(path);
		log.debug("删除文件:" + path);
		try {
			return fileServer.delete(path);
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}
	
	@GET
	@Path("findfile/{path}/{fileName}")
	//查找文件
	public String xtFindFile(@PathParam("path") String path, @PathParam("fileName") String fileName)
			throws Exception {
		//传入要求两次加密
		path = URLDecoder.decode(path);
		log.debug("查找文件:" + fileName + ", path:" + path);
		try {
			return fileServer.find(path, true, new FileCheck(fileName)).toString();
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}
	
	@GET
	@Path("find/{path}/{checkName}")
	//查找文件
	public String xtFind(@PathParam("path") String path, @PathParam("checkName") String checkName)
			throws Exception {
		//传入要求两次加密
		path = URLDecoder.decode(path);
		log.debug("查找文件:" + checkName + ", path:" + path);
		try {
			fileServer.setChecker(checkName);
			return fileServer.find(path, true, null).toString();
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}
	
	@POST
	@Path("upload/{fileType}/{fileName}")
	//上传文件
	public String xtUpload(@PathParam("fileType") String fileType, @PathParam("fileName") String fileName, InputStream uploadedInputStream)
			throws Exception {
		String filePath = WebFileConfigInfo.getSettingValue(fileType) + fileName;
		log.debug("上传文件:" + filePath + "(" + fileType + "|" + fileName + ")");
		try {
			
			int bytesRead = 0;
			boolean append = false;
			byte[] buffer = new byte[1024];
			String result = null;
			
			while ((bytesRead = uploadedInputStream.read(buffer, 0, 1024)) != -1) {
				result = fileServer.write(buffer, bytesRead, filePath, append);
				append = true;
			}
			
			uploadedInputStream.close();
			
			return result;
		} catch (Exception ex) {
			log.error(ExceptionHelper.stackToString(ex));
			throw ex;
		}
	}
	
	@GET
	@Path("download/{fileType}/{fileName}")
	//下载文件
	public Response xtDownload(@PathParam("fileType") String fileType, @PathParam("fileName") String fileName)
			throws Exception {
		String filePath = WebFileConfigInfo.getSettingValue(fileType) + fileName;
		
		log.debug("下载文件:" + filePath + "(" + fileType + "|" + fileName + ")");

		try {
			//下载文件
			File file = new File(filePath);
			long fileLength = file.length();
			
			ResponseBuilder responseBuilder = Response.ok(file);
			
			responseBuilder.type(new MimetypesFileTypeMap().getContentType(file));//根据文件确定MIME-Type
			responseBuilder.header("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
			responseBuilder.header("Content-Length", Long.toString(fileLength));
			
			Response response = responseBuilder.build();
			return response;
		} catch (IOException ex) {            
			log.error(ExceptionHelper.stackToString(ex));
			String content = "无法找到资源 " + filePath + " ，请检查服务器是否存在此资源！";
			Response response = Response.serverError().status(Status.NOT_FOUND).header("", content).build();
			return response;
		} catch (SecurityException ex) {
			log.error(ExceptionHelper.stackToString(ex));
			String content = "无法读取资源 " + filePath + " 对应的资源数据，出现了安全异常：" + ex.getLocalizedMessage() + "，可能是由于文件系统不允许当前进程读取文件造成的！ ";
			Response response = Response.serverError().status(Status.NOT_FOUND).header("", content).build();
			return response;
		}
	}
	
	
}
