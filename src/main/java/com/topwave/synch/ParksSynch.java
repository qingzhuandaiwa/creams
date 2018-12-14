package com.topwave.synch;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.topwave.app.SynchExecutor;
import com.topwave.kit.HttpUtils;
import com.topwave.utils.Db;
import com.topwave.utils.DbHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParksSynch implements Runnable{
	private static Logger logger = Logger.getLogger(ParksSynch.class);
	
	private String token;

	private Prop prop;//配置
	
	public ParksSynch(String token,Prop prop) {
		this.prop = prop;
		this.token = token;
	}
	
	

	public void run() {
//		File file = SynchExecutor.getFile();
		Boolean isSynchPark = prop.getBoolean("isSynchPark");
		if(isSynchPark) {
			System.out.println("ParksSynch thread is running ...");
			logger.info("ParksSynch thread is running ...");
			String url = prop.get("workgo.url");
			String username = prop.get("workgo.username");
			String password = prop.get("workgo.password");
			String parkUrl = prop.get("parkUrl");
			
			
			Connection conn = null;
			try {
				conn = getConn(url, username, password);
			
				List list = Db.query(conn, "select parkId, parkName from t_park where collectId is null");
				for(int i=0;i<list.size(); i++){
					Object[] temp =  (Object[]) list.get(i);
					String parkName = (String)temp[1];
					//plist.add(new Park((int)temp[0], (String)temp[1], 0));
					String collectId = createPark(parkUrl, parkName,  token);
					if(!collectId.equals("")){
						Db.update(conn, "update t_park set collectId = "+ collectId + ", uTime = now() where parkId = " + temp[0]);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("<==== 执行...根据WORKG园区新建集合数据出现错误",e);
			}finally{
				System.out.println("<==== 执行...根据WORKG园区新建集合数据--END");
				logger.info("<==== 执行...根据WORKG园区新建集合数据--END");
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}  
	
	public static Connection getConn(String url, String username, String password) throws SQLException{
        
        String driver = "com.mysql.jdbc.Driver";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
	}
	
	public static String createPark(String parkUrl, String parkName, String token){
		Map<String, Object> params = new HashMap<String, Object>();
		int[] buildingIds = new int[0];
        params.put("buildingIds", buildingIds);
        params.put("name", parkName);
        String paras = JSONObject.toJSON(params).toString();
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Content-Type", "application/json; charset=utf-8");
		headers.put("Authorization", token);
		String result = "";
		try {
			result = HttpUtils.post(parkUrl,paras,headers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("-------新建园区--失败---"+parkName+"-");
			logger.error("-------新建园区--失败---"+parkName+"-",e);
		}finally{
			return result;
		}
	}
	
	/**
	 * 获取workgo中所有的园区id
	 */
	public static List<Integer> getParkIdSInWorkgo(Prop prop) {
//		File file = SynchExecutor.getFile();
		String url = prop.get("workgo.url");
		String username = prop.get("workgo.username");
		String password = prop.get("workgo.password");
		Connection conn = null;
		List<Integer> parkIdS = new ArrayList<Integer>();
		try {
			conn = getConn(url, username, password);
			parkIdS = Db.query(conn, "SELECT collectId from t_park");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取workgo中所有的园区id失败",e);
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		return parkIdS;
	}
	

}
