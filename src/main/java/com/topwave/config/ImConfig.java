package com.topwave.config;


public  class ImConfig {
	private static String fileName = "";
	private static final Constants constants = new Constants();//常量信息
//	private static final Config config = new Config(fileName);//配置信息
	private static Config config = null;//配置信息
	private ImConfig() {
	}
	
	public static final Config getConfig() {
		return config;
	}
	
//	public static final void setFileName(String FilePath) {
//		fileName = FilePath;
//		config = new Config(fileName);
//	}
	
	/**
	 * 获取常量对象
	 * @return
	 */
	public static final Constants getConstants() {
		return constants;
	}
	
	
}
