package com.btt.wxservice.util;

public class LoadConfig {
	/**
	 * 硬性加载配置文件 文件位于class根目录
	 * @return Properties
	 */
	public static java.util.Properties loadFromFile(String fileName) {
		String path = LoadConfig.class.getResource("/".concat(fileName)).getPath();
		java.util.Properties p = new java.util.Properties();
		try {
			p.load(new java.io.FileReader(path));
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return p;
	}
}
