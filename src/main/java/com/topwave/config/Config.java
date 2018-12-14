package com.topwave.config;

import com.jfinal.kit.Prop;

public class Config {

	public Prop prop = null;
	
	/**********数据库配置********/
//	private boolean dbEnable = prop.getBoolean("db.enable", true);
	private  String dbType = prop.get("dbType", "mysql");
	private  String dbUrl = prop.get("jdbcUrl");
	private  String dbUserame = prop.get("user");
	private  String dbPassword = prop.get("password");
	private  boolean dbShowSql = prop.getBoolean("showSql", false);
	
	public Config(Prop prop){
		this.prop = prop;
//		prop = PropKit.use(ImConfig.getConstants().dbConfigFilePath());
//		prop = PropKit.use(fileName);
	}
	
	
	/**
	 * 配置文件所有属性
	 * @return
	 */
	public Prop getProp() {
		return prop;
	}

//	/**
//	 * 是否启用该数据库配置
//	 * @return
//	 */
//	public boolean getDbEnable() {
//		return dbEnable;
//	}

	/**
	 * 数据库类型
	 * @return
	 */
	public  String getDbType() {
		return prop.get("dbType", "mysql");
	}
	
	/**
	 * 数据库jdbc连接字符串
	 * @return
	 */
	public  String getDbUrl() {
		return prop.get("jdbcUrl");
	}
	
	/**
	 * 数据库用户名
	 * @return
	 */
	public  String getDbUserame() {
		return prop.get("user");
	}
	
	/**
	 * 数据库密码
	 * @return
	 */
	public  String getDbPassword() {
		return prop.get("password");
	}
	
	/**
	 * 是否在控制台打印sql语句
	 * @return
	 */
	public  boolean getDbShowSql() {
		return prop.getBoolean("showSql", false);
	}
	
}
