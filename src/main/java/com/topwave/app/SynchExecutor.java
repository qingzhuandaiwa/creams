package com.topwave.app;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.Redis;
import com.topwave.config.Config;
import com.topwave.synch.*;
import com.topwave.utils.AccessToken;
import com.topwave.utils.DbHelper;
import com.topwave.utils.EhCacheUtil;
import com.topwave.utils.RedisUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SynchExecutor {

	private static Logger logger = Logger.getLogger(SynchExecutor.class);

//	private static String token;
	
//	private static File configFile = null;
//
//
//	public static File getFile() {
//		return configFile;
//	}

//	public static Map<String,Map<String,String>> cache = new HashMap<String, Map<String, String>>();
	
	private static Map<String,List<Integer>> parkIds = new HashMap<String, List<Integer>>();

	public static List<Integer> getParkIds(String configName) {
		return parkIds.get(configName);
	}

	public static void main(String[] args){
		String filePath = "C:\\configtest.properties";
//		executeSysch(filePath);
		
		
//        String path = Thread.currentThread().getContextClassLoader ().getResource("").getPath();
//        //System.out.println("path = " + path);
//        String filename = path + "/config.properties";
//        if (args.length > 0) {
//            filename = args[0];
//        }
        executeSysch(filePath);


	}

	private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);


	/**
	 * 瀵拷婵鎮撳锟�
	 */
	public static void executeSysch(String filePath) {
//				RedisUtil.getInstance().strings().set("guojinghah:test","guojingtest");

		logger.info("**********************************************************执行同步任务开始************************************************************************");
		File file = new File(filePath);
//		configFile = file;
//		Config.prop = PropKit.use(file);
		Prop prop = PropKit.use(file);
		DbHelper.getDb(prop);
		
		String token = getToken(prop);

		getParkIdSInWorkgo(prop);


		try {
			String configName = prop.get("config.name");//配置名称
//			EhCacheUtil.getInstance().flush(cacheName);//清空缓存
			logger.info("*********************************清空 " + configName +" 的cache缓存**********************************");
			RedisUtil.getInstance(prop).keys().flushAll(configName);
//			cache.put(configName,null);
			BuildingSynch buildingSynch = new BuildingSynch(token,prop);
			FloorSynch floorSynch = new FloorSynch(token,prop);
			RoomSynch roomSynch = new RoomSynch(token,prop);
			TenantSynch tenantSynch = new TenantSynch(token,prop);
			ParksSynch parksSynch = new ParksSynch(token,prop);
			ContractSynch contractSynch = new ContractSynch(token,prop);
			BillsSynch billSynch = new BillsSynch(token,prop);
			UserSynch userSynch = new UserSynch(token,prop);

//			fixedThreadPool.execute(parksSynch);
//			fixedThreadPool.execute(buildingSynch);
//			fixedThreadPool.execute(floorSynch);
//			fixedThreadPool.execute(roomSynch);
//			fixedThreadPool.execute(tenantSynch);
			fixedThreadPool.execute(userSynch);
//			fixedThreadPool.execute(billSynch);
//			fixedThreadPool.execute(contractSynch);
		}catch (Exception ex){
			logger.error(ex.getMessage());
		}

	}

	public static String getToken(Prop prop) {
		String token = AccessToken.getToken(prop);
		System.out.println("token is : " + token);
//		logger.info("token is : " + token);
		return token;
	}
	
	/**
	 * 获取workgo中parkid
	 */
	public static void getParkIdSInWorkgo(Prop prop) {
		String configName = prop.get("config.name");
		List<Integer> Ids = ParksSynch.getParkIdSInWorkgo(prop);
//		parkIds = Ids;
		parkIds.put(configName,Ids);
	}
}
