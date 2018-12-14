package com.topwave.synch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.topwave.app.SynchExecutor;
import com.topwave.bean.ParkModel;
import com.topwave.kit.HttpHelper;
import com.topwave.service.ParkService;
import com.topwave.utils.DbHelper;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ParkSynch implements Runnable {
	Logger logger = Logger.getLogger(ParkSynch.class);
//    private static final int JsonResult = 0;
	private String timeFormat = "yyyy-MM-dd HH:mm:ss";

	public List<ParkModel> insertDatas = new ArrayList<ParkModel>();
	public List<ParkModel> updateDatas = new ArrayList<ParkModel>();

	private Date lastUptDate = null;

	private String token;

	private Prop prop;

	public ParkSynch(String token) {
		this.token = token;
		this.prop = prop;
	}

	/**
	 * 从webapi中获取数据
	 * 
	 * @param token
	 * @param lastUptDate 最近一次更新时间
	 */
	@SuppressWarnings("deprecation")
	public String fetchFromWebApi(String token, Date lastUptDate) {
		Date lastDate = lastUptDate;
		// 日期转字符串
		if (lastDate == null) {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MONTH, -1);// 月份减1
			lastDate = now.getTime();
		}

		this.lastUptDate = lastDate;

		Map<String, Object> params = new HashMap<String, Object>();
		int[] buildingIds = new int[0];
		params.put("queryDateFrom", new SimpleDateFormat(timeFormat).format(lastDate));
//        String paras = JSONObject.toJSON(params).toString();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json; charset=utf-8");
		headers.put("Authorization", token);
		String result = null;
		try {
//			result = HttpHelper.post(PropKit.use("config.properties").get("parkUrl"),paras,headers);
			result = HttpHelper.get(prop.get("parkUrl"), params, headers);
			System.out.println(result);

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
		JSONObject jsonResult = null;

//		List<Student> studentList1 = JSON.parseArray(JSON.parseObject(json).getString("studentList"), Student.class);

		try {
			jsonResult = JSONObject.parseObject(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :park 解析数据内容失败！");
			return false;
		}

		if (jsonResult.getIntValue("code") != 200) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :park 网络接口调用出现问题,code: "
					+ jsonResult.getIntValue("code") + ", message=" + jsonResult.getString("message") + " ！");
			return false;
		}

		JSONArray arr = jsonResult.getJSONArray("data");// 获取的结果集合转换成数组
		String js = JSONObject.toJSONString(arr);// 将array数组转换成字符串
		List<ParkModel> parkS = JSONObject.parseArray(js, ParkModel.class);// 把字符串转换成集合
//		List<ParkModel> parkS = JSON.parseArray(jsonResult.getString("data"),ParkModel.class);
//		List<ParkModel> parkS = jsonResult.getData();

		if (parkS == null || parkS.size() <= 0) {
			logger.warn(new SimpleDateFormat(timeFormat).format(new Date()) + " :park 数据为空！");
			return false;
		}

		try {
			for (Iterator<ParkModel> it = parkS.iterator(); it.hasNext();) {
				ParkModel park = it.next();
				if (park.getCtime().getTime() == park.getUtime().getTime()
						|| park.getCtime().getTime() > lastUptDate.getTime()) {
					this.insertDatas.add(park);
				} else {
					this.updateDatas.add(park);
				}
			}
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + "遍历parks失败");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 同步数据
	 */
	public void SynchDatas() {
		if (insertDatas != null && insertDatas.size() > 0) {
			insertDataS(insertDatas);
		}
		if (updateDatas != null && updateDatas.size() > 0) {
			updateDataS(updateDatas);
		}
	}

	/**
	 * 插入数据
	 * 
	 * @param parks
	 * @return
	 */
	private boolean insertDataS(List<ParkModel> parks) {
		int[] count = null;
		boolean isInsertSuccess = false;
		List<String> sqlList = new ArrayList<String>();

		try {
			for (Iterator<ParkModel> it = parks.iterator(); it.hasNext();) {
				ParkModel park = it.next();
				String sql = "INSERT INTO park (park_id,park_name,is_del,ctime,utime) VALUES (" + park.getId() + ", '"
						+ park.getParkName() + "'," + park.getIsDel() + ",'" + park.getCtime() + "','" + park.getUtime()
						+ "');";
				sqlList.add(sql);
			}
			count = DbHelper.getDb(prop).batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.info(new SimpleDateFormat(timeFormat).format(new Date()) + " :park 批量插入失败！");
			e.printStackTrace();
		}

		if (count != null && count.length > 0) {
			isInsertSuccess = true;
		}
		return isInsertSuccess;
	}

	/**
	 * 修改数据
	 * 
	 * @param parks
	 * @return
	 */
	private boolean updateDataS(List<ParkModel> parks) {
		int[] count = null;
		boolean isUptSuccess = false;
		List<String> sqlList = new ArrayList<String>();
		try {
			for (Iterator<ParkModel> it = parks.iterator(); it.hasNext();) {
				ParkModel park = it.next();
				String sql = "update park set park_name = '" + park.getParkName() + "',is_del = '" + park.getIsDel()
						+ "' ,ctime = '" + park.getCtime() + "', utime= '" + park.getUtime() + "' where park_id = "
						+ park.getId() + ";";
				sqlList.add(sql);
			}

			count = DbHelper.getDb(prop).batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.info(new SimpleDateFormat(timeFormat).format(new Date()) + " : park 批量更新失败！");
			e.printStackTrace();
		}
		if (count != null && count.length > 0) {
			isUptSuccess = true;
		}
		return isUptSuccess;
	}

	public void run() {
//		// TODO Auto-generated method stub
//		System.out.println("parkSynch thread is running ...");
		Date lastDate = ParkService.getLastUpdateDate(prop);
//		String result = fetchFromWebApi(token,lastDate);
//		if(StrKit.isBlank(result)) {
//			logger.info(new SimpleDateFormat(timeFormat).format(new Date()) + " :park 网络接口调用异常！");
//			return;
//		}
//		Boolean isSuccess = ProcessDataS(result);
//		if(isSuccess) {
//			SynchDatas();
//		}
		final ParkModel park = new ParkModel();
		park.setId("123");

		Db.tx(new IAtom() {
			public boolean run() throws SQLException {

				try {
					ParkService.save(prop,park);
					ParkService.update(prop,park);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

				return true;
			}
		});

	}

}
