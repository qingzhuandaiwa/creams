package com.topwave.utils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.topwave.app.SynchExecutor;
import com.topwave.kit.HttpHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccessToken {
	static Log log = Log.getLog(AccessToken.class);
	
	public static String Bearer = "Bearer ";
//	public static String Token = null;
	private static String client_id = "creamsc7336b36591d34eb7";
	private static String client_secret = "bSsJLEM6a0w7XXhlDOBAeXsEMnWBniT735o91seuWkBnEB9lnLAzYGueYj4eqKbK";
	
	
	
	public static String getToken(Prop prop) {
		
		log.info("get Token...");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String tokenUrl = prop.get("tokenUrl") + "?client_id=" + client_id +
		"&client_secret=" + client_secret + 
		"&username="+ prop.get("creamsUser") +"&password=" +
				prop.get("creamsPass");
//		https://staging-api.creams.io/oauth/token/password
		
		String accessToken = AccessToken.getAccessToken(null,null
				, tokenUrl);
		
		if(StrKit.isBlank(accessToken)) {
	    	log.error("-------获取TOKEN失败--任务未执行---");
	    	return null;
		}
		String Token = Bearer + accessToken;
		return Token;
	}
	
	/**
	 * 获取TOKEN
	 * @param username
	 * @param password
	 * @param tokenUrl
	 * @return
	 */
	@SuppressWarnings("finally")
	public static String getAccessToken(String username, String password, String tokenUrl){
//		System.out.println("-------获取TOKEN--START----");
//		System.out.println("-------获取TOKEN--username is : " + username);
//		System.out.println("-------获取TOKEN--password is : " + password);
//		System.out.println("-------获取TOKEN--tokenUrl is : " + tokenUrl);
//		log.info("-------获取TOKEN--START----");
//		log.info("-------获取TOKEN--username is : " + username);
//		log.info("-------获取TOKEN--password is : " + password);
//		log.info("-------获取TOKEN--tokenUrl is : " + tokenUrl);
		Map<String, String> params = new HashMap<String, String>();
//        params.put("client_id", "creamsc7336b36591d34eb7");
//        params.put("client_secret", "bSsJLEM6a0w7XXhlDOBAeXsEMnWBniT735o91seuWkBnEB9lnLAzYGueYj4eqKbK");
//        params.put("password", password);
//        params.put("username", username);
//        String paras = JSONObject.toJSON(params).toString();
        
        Map<String,String> headers = new HashMap<String,String>();
		headers.put("Content-Type", "application/json; charset=utf-8");
		String accessToken = "";
        try {
//        	String result = HttpHelper.post(tokenUrl,paras,headers);
        	String result = HttpHelper.post(tokenUrl,params,headers);
			Map maps = (Map)JSONObject.parse(result);
	        accessToken = (String)maps.get("access_token");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("-------获取TOKEN---ERROR---" + e.getMessage());
			log.error("-------获取TOKEN---ERROR---" + e.getMessage());
		}finally{
			System.out.println("-------获取TOKEN--tokenUrl is : " + tokenUrl);
//			log.info("-------获取TOKEN--tokenUrl is : " + tokenUrl);
			System.out.println("-------获取TOKEN---END---");
//			log.info("-------获取TOKEN---END---");
			return accessToken;
		}
	}
	
	/**
	 * 替换参数
	 * @param list
	 * @param accessToken
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public static void replaceAccessToken(List<Source> list, String accessToken){
//        //extractor.run();
//        for(Source s:list){
//        	//String header = s.getStr("header").replaceAll("XXXXXXXX", accessToken);
//        	((Map)s.getObj("header")).put("Authorization", "Bearer "+accessToken);
//        }
//	}
}
