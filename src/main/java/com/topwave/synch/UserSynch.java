package com.topwave.synch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.topwave.app.SynchExecutor;
import com.topwave.bean.UserModel;
import com.topwave.kit.HttpHelper;
import com.topwave.service.UserService;
import com.topwave.utils.DbHelper;
import com.topwave.utils.EhCacheUtil;
import com.topwave.utils.EhcacheKey;
import com.topwave.utils.RedisUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserSynch implements Runnable {

	private Date lastUptDate = null;

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	Logger logger = Logger.getLogger(UserSynch.class);

	public List<UserModel> insertDatas = new ArrayList<UserModel>();
	public List<UserModel> updateDatas = new ArrayList<UserModel>();

	private Integer pageSize = 20;// 一页的个数

	private Integer totalCount = 0;// 总条数

	private Integer totalPage;// 计算出的总页数

	private Integer pageIndex = 0;// 当前的页数

	private Date lastDate;// 最近一次更新的时间

	private String token;

	private Prop prop;//配置

	private Boolean isLoged = false;

	Integer insertSize = 0;
	Integer updateSize = 0;

	public UserSynch(String token,Prop prop) {
		this.token = token;
		this.prop = prop;
	}

	/**
	 * 从webapi中获取数据
	 * 
	 * @param token
	 * @param lastUptDate 最近一次更新时间
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	public String fetchFromWebApi(String token, Date lastUptDate) throws ParseException {
		Date lastDate = lastUptDate;
		// 日期转字符串
		if (lastDate == null) {
			SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
			lastDate = formatter.parse("0000-00-00 00:00:00");
		}

		this.lastUptDate = lastDate;

		Map<String, Object> params = new HashMap<String, Object>();
//		int[] buildingIds = new int[0];
		params.put("queryDateFrom", new SimpleDateFormat(timeFormat).format(lastDate));
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);

		if (pageIndex>0){
// add time limit
			try {
				System.out.println("============timeDelay"+prop.getInt("requestTimeLimit"));
				Thread.sleep(prop.getInt("requestTimeLimit"));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Map<String, String> headers = new HashMap<String, String>();
//		headers.put("Content-Type", "application/json; charset=utf-8");
		headers.put("Content-Type", "text/html; charset=utf-8");
		headers.put("Authorization", token);
		String result = null;
		try {
			System.out.println("===========================begin");

			result = HttpHelper.get(prop.get("userUrl"), params, headers);
			System.out.println("===========================end");
			System.out.println(result);
//			logger.info("----------第"+ pageIndex+"user页接口返回的数据:" + result);
//			logger.info(result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 处理返回的数据
	 * 
	 * @param result
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean ProcessDataS(String result) {
//		System.out.println(result);
		JSONObject jsonResult = null;
		try {
			jsonResult = JSONObject.parseObject(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("!!!!!!!!!  环境："+ prop.get("config.name") +" 第"+ pageIndex +"页user数据反序列化失败！",e);
			return false;
		}

		if (jsonResult.getIntValue("code") != 200) {
			logger.error("!!!!!!!!!!  环境："+ prop.get("config.name") +" 第" + pageIndex+ "页user网络接口调用出现问题,code: "
					+ jsonResult.getIntValue("code") + ", message=" + jsonResult.getString("message") + " ！");
			return false;
		}

		JSONArray arr = jsonResult.getJSONArray("data");// 获取的结果集合转换成数组
		String js = JSONObject.toJSONString(arr);// 将array数组转换成字符串
		List<UserModel> users = JSONObject.parseArray(js, UserModel.class);// 把字符串转换成集合
//		List<ParkModel> parkS = JSON.parseArray(jsonResult.getString("data"),ParkModel.class);
//		List<ParkModel> parkS = jsonResult.getData();

		if (users == null || users.size() <= 0) {
			logger.info("---------- 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页user 数据为空！");
			return false;
		}

		totalCount = jsonResult.getInteger("count");
		if (!isLoged){
			logger.info("----------  环境："+ prop.get("config.name") +" user 接口返回的数据总条数:" + totalCount);
			isLoged = true;
		}

		StringBuilder sql = new StringBuilder("select * from ( ");

		try {
			for (Iterator<UserModel> it = users.iterator(); it.hasNext();) {
				UserModel user = it.next();

				//---------------------------判断数据是否重复start
				String configName = prop.get("config.name");
				String key = configName + ":" + "user:" + user.getId();
				Boolean isExisted = RedisUtil.getInstance(prop).keys().exists(key);
				if (!isExisted){
//					EhCacheUtil.getInstance().put(cacheName,
//							EhcacheKey.USER_CACHE.appendStr(user.getId()),user.getId());
					RedisUtil.getInstance(prop).strings().set(key,user.getId());
				}else {
					it.remove();
					continue;
				}
				//---------------------------判断数据是否重复end

				sql.append("select '" + user.getId() + "' id UNION ");
			}

			int lastUnIndx = sql.lastIndexOf("UNION");
			int length = sql.length();
			if (sql.lastIndexOf("UNION") > 0) {
				sql.delete(lastUnIndx, length - 1);
			}

			sql.append("from dual) temp where EXISTS (SELECT 1 from user where temp.id = user_id)");
			List<String> rcdS = DbHelper.getDb(prop).query(sql.toString());

			for (Iterator<UserModel> it = users.iterator(); it.hasNext();) {
				UserModel userModel = it.next();
				if (rcdS.contains(userModel.getId())) {
					this.updateDatas.add(userModel);
				} else {
					this.insertDatas.add(userModel);
				}
			}
		} catch (Exception e) {
			logger.error("!!!!!!!!!!  环境："+ prop.get("config.name") +" 第" + pageIndex +  "页users处理失败",e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 同步数据
	 * @throws Exception 
	 */
	public void SynchDatas() throws Exception {
		System.out.println("user current page: "+ (pageIndex + 1) +",total pages: " + totalPage);
		long startTime = System.currentTimeMillis();
		if (insertDatas != null && insertDatas.size() > 0) {
			UserService.insertDataS(prop,insertDatas);
			insertSize += insertDatas.size();
		}
		if (updateDatas != null && updateDatas.size() > 0) {
			UserService.updateDataS(prop,updateDatas);
			updateSize += updateDatas.size();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("user page "+ (pageIndex + 1) +" Synchronous Data success, time: " + (endTime - startTime) + "ms");
		
	}

	public void run() {
		System.out.println("UserSynch thread is running ...");
		String strPageSize = prop.get("pageSize");
		pageSize = Integer.parseInt(strPageSize);
		int backTime = prop.getInt("backTime");
		lastDate = UserService.getLastUpdateDate(prop);
		if (lastDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lastDate);
			calendar.add(Calendar.SECOND, -backTime);//
			lastDate = calendar.getTime();
		}

		pageIndex = 0;

		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				try {
					do {
						insertDatas.clear();
						updateDatas.clear();
						String result = fetchFromWebApi(token, lastDate);
						if (StrKit.isBlank(result)) {
							logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :user 网络接口调用异常！");
							return false;
						}
						Boolean isSuccess = ProcessDataS(result);

						totalPage = (totalCount + (pageSize - 1)) / pageSize;

						if (isSuccess) {
							SynchDatas();
						}
						pageIndex++;
					} while (pageIndex <= totalPage - 1);
//					System.out.println("同步user成功！");
//					logger.info("同步user成功！");
					logger.info(" 环境："+ prop.get("config.name") +" 同步user成功！新增" + insertSize + "条，修改" + updateSize + "条！");
					return true;
				} catch (Exception e) {
					logger.error(" 环境："+ prop.get("config.name") +" 同步user失败！",e);
					return false;
				}
			}
		});

	}

}
