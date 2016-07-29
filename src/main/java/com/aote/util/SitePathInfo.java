/**
 * 
 */
package com.aote.util;

import java.io.File;

/**
 * 提供站点的路径信息
 * 
 * @author qb
 * 
 */
public final class SitePathInfo {

	private static SitePathInfo instance;

	public static SitePathInfo getInstance() {
		if (instance == null) {
			instance = new SitePathInfo();
		}
		return instance;
	}

	private SitePathInfo() {

		// web site project environment value example:
		// /D:/Program%20Files/Apache%20Software%20Foundation/Tomcat%205.5/webapps/java-web-tester/WEB-INF/classes/
		// java project environment value example:
		// /F:/JavaProjects/tester-demo/java-tester/bin/

		// classes path
		String claPath = SitePathInfo.class.getClassLoader().getResource("").getPath();
		claPath = claPath.replaceAll("%20", " ");

		// try get root index as web site
		int rootDestIndex = claPath.lastIndexOf("/WEB-INF/");

		// try get root index as app
		if (rootDestIndex <= 0) {
			rootDestIndex = claPath.lastIndexOf("/bin/");
		}

		// get by default
		if (rootDestIndex <= 0) {
			rootDestIndex = claPath.lastIndexOf("/");
		}

		String root = claPath.substring(1, rootDestIndex);

		this.rootPhysicalPath = root + "/";
		this.contextPath = root.substring(root.lastIndexOf("/"));
		this.tomcatAppsPath = root.substring(0, root.lastIndexOf("/"));
	}

	private String rootPhysicalPath;

	private String contextPath;

	private String tomcatAppsPath;

	/**
	 * 获取站点的根目录的物理路径
	 * 
	 * @return eg: {TomcatInstallationDir}/webapps/java-web-tester/
	 */
	public String getRootPhysicalPath() {
		return this.rootPhysicalPath;
	}

	/**
	 * 获取站点的上下文路径
	 * 
	 * @return eg: /java-web-tester
	 */
	public String getContextPath() {
		return this.contextPath;
	}

	/**
	 * 获取 tomcat  webapps 目录的物理路径
	 * 
	 * @return eg: {TomcatInstallationDir}/webapps
	 */
	public String getTomcatAppsPath() {
		return this.tomcatAppsPath;
	}

	/**
	 * 获取站点  WEB-INF 目录的物理路径
	 * 
	 * @return eg: {TomcatInstallationDir}/webapps/java-web-tester/WEB-INF/
	 */
	public String getWebInfPath() {
		return getVerifiedPath(this.getRootPhysicalPath() + "WEB-INF/");
	}

	/**
	 * 获取站点下的用于存储文件数据的目录的物理路径，此目录不能直接通过 url浏览器下载
	 * 
	 * @return eg: {TomcatInstallationDir}/webapps/java-web-tester/WEB-INF/data/
	 */
	public String getWebDataPath() {
		return getVerifiedPath(this.getRootPhysicalPath() + "WEB-INF/data/");
	}

	/**
	 * 获取合法的路径，意味着如果指定的路径不存在，则自动创建此目录，并返回此路径；如果已存在，则直接返回此路径
	 * 
	 * @param path
	 * @return
	 */
	protected String getVerifiedPath(String path) {
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		return path;
	}

}
