package com.topwave.service;

import com.jfinal.kit.Prop;
import com.jfinal.plugin.activerecord.Record;
import com.topwave.bean.UserModel;
import com.topwave.model.User;
import com.topwave.utils.DbHelper;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class UserService {
	
	
	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	static Logger logger = Logger.getLogger(UserService.class);
	
	public static Date getLastUpdateDate(Prop prop) {
		String sql = "SELECT * from user ORDER BY utime DESC LIMIT 1";
		Record rcd = DbHelper.getDb(prop).findFirst(sql);
		if(rcd!=null) {
			return rcd.getDate("utime");
		}else {
			return null;
		}
	}

	/**
	 * 插入数据
	 * @param
	 * @throws Exception 
	 *//*
	public static void insertDataS(List<UserModel> users) throws Exception {
		List<String> sqlList = new ArrayList<String>();

		try {
			for(Iterator<UserModel> it = users.iterator(); it.hasNext();) {
				UserModel user = it.next();
				String dateCreate = null;
				if(user.getDateCreate() != null) {
					dateCreate = "'" + user.getDateCreate()+ "'";
				}
//				System.out.println(user.getName());
				String sql = "INSERT INTO user "
						+ "(user_id,active,is_admin,name,tel,email,date_create,is_del,ctime,utime) VALUES "
						+ "(" +
						user.getId() + ", '"+ user.getActive() +"', '" + 
						user.getAdmin() + "', '" + user.getName() + "', '" + 
						user.getTel() + "', '" + user.getEmail() +  "', " + dateCreate + ", '" + user.getIsDel() + "','" + user.getCtime() + "','"+ user.getUtime() +"');";
				sqlList.add(sql);
			}
			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :user 批量插入失败！");
			e.printStackTrace();
			throw e;
		}
	}
	
	*//**
	 * 修改数据
	 * @param
	 * @throws Exception 
	 *//*
	public static void updateDataS(List<UserModel> users) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		try {
			for(Iterator<UserModel> it = users.iterator(); it.hasNext();) {
				UserModel user = it.next();
				String dateUpdate = null;
				if(user.getDateCreate() != null) {
					dateUpdate = "'" + user.getDateCreate()+ "'";
				}
				
				String sql = "update user set active = '" + user.getActive() + "', is_admin='" + user.getAdmin() + "', name='" + 
				user.getName() + "' , tel='" + user.getTel() + "', email='" + user.getEmail() + "', date_create=" + 
				dateUpdate +  ",is_del = '"+ user.getIsDel()+"' ,ctime = '"+ user.getCtime() +"', utime= '"+ 
				user.getUtime() +"' where user_id = " + user.getId() + ";";
				sqlList.add(sql);
			}
			
			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : user 批量更新失败！");
			e.printStackTrace();
			throw e;
		}
	}*/

	/**
	 * 插入数据
	 * @param
	 * @throws Exception 
	 */
	public static void insertDataS(Prop prop,List<UserModel> models) throws Exception {
		List<User> users = new ArrayList<User>();

		try {
			for(Iterator<UserModel> it = models.iterator(); it.hasNext();) {
				UserModel model = it.next();
				
				User user = new User();
				user.setUserId(model.getId());
				user.setActive(model.getActive());
				user.setIsAdmin(model.getAdmin());
				user.setName(model.getName());
				user.setTel(model.getTel());
				user.setEmail(model.getEmail());
				user.setDateCreate(model.getDateCreate());
				user.setIsDel(model.getIsDel());
				user.setCtime(model.getCtime());
				user.setUtime(model.getUtime());
				users.add(user);
			}
			DbHelper.getDb(prop).batchSave(users, users.size());
		} catch (Exception e) {
			logger.error("user 批量插入失败！",e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 修改数据
	 * @param
	 * @throws Exception 
	 */
	public static void updateDataS(Prop prop,List<UserModel> models) throws Exception {
		List<User> users = new ArrayList<User>();
		try {
			for(Iterator<UserModel> it = models.iterator(); it.hasNext();) {
				UserModel model = it.next();

				User user = new User();
				user.setUserId(model.getId());
				user.setActive(model.getActive());
				user.setIsAdmin(model.getAdmin());
				user.setName(model.getName());
				user.setTel(model.getTel());
				user.setEmail(model.getEmail());
				user.setDateCreate(model.getDateCreate());
				user.setIsDel(model.getIsDel());
				user.setCtime(model.getCtime());
				user.setUtime(model.getUtime());
				users.add(user);
			}
			
			DbHelper.getDb(prop).batchUpdate(users, users.size());
		} catch (Exception e) {
			logger.error("user 批量更新失败！",e);
			e.printStackTrace();
			throw e;
		}
	}
	


}
