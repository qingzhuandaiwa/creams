package com.topwave.synch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.topwave.app.SynchExecutor;
import com.topwave.bean.RoomModel;
import com.topwave.kit.HttpHelper;
import com.topwave.service.RoomService;
import com.topwave.utils.DbHelper;
import com.topwave.utils.EhCacheUtil;
import com.topwave.utils.EhcacheKey;
import com.topwave.utils.RedisUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RoomSynch implements Runnable {

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	Logger logger = Logger.getLogger(RoomSynch.class);

	public List<RoomModel> insertDatas = new ArrayList<RoomModel>();
	public List<RoomModel> updateDatas = new ArrayList<RoomModel>();

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

	public RoomSynch(String token,Prop prop) {
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

		Map<String, Object> params = new HashMap<String, Object>();
		int[] buildingIds = new int[0];
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
//        String paras = JSONObject.toJSON(params).toString();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json; charset=utf-8");
		headers.put("Authorization", token);
		String result = null;
		try {
//			result = HttpHelper.post(PropKit.use("config.properties").get("parkUrl"),paras,headers);
			result = HttpHelper.get(prop.get("roomUrl"), params, headers);
//			System.out.println(result);
//			logger.info("----------第"+ pageIndex+"页room接口返回的数据:" + result);
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
		//是否需判断数据中的parkid存在于workgo数据库中
		boolean isCheckParkExistInWorkgo = prop.getBoolean("isCheckParkExistInWorkgo");
//		List<Student> studentList1 = JSON.parseArray(JSON.parseObject(json).getString("studentList"), Student.class);

		try {
			jsonResult = JSONObject.parseObject(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("!!!!!!!!!!  环境："+ prop.get("config.name") +" 第"+ pageIndex +"页room数据反序列化失败！",e);
			return false;
		}

		if (jsonResult.getIntValue("code") != 200) {
			logger.error("  环境："+ prop.get("config.name") +" room 网络接口调用出现问题,code: "
					+ jsonResult.getIntValue("code") + ", message=" + jsonResult.getString("message") + " ！");
			return false;
		}

		JSONArray arr = jsonResult.getJSONArray("data");// 获取的结果集合转换成数组
		String js = JSONObject.toJSONString(arr);// 将array数组转换成字符串
		List<RoomModel> rooms = JSONObject.parseArray(js, RoomModel.class);// 把字符串转换成集合
//		List<ParkModel> parkS = JSON.parseArray(jsonResult.getString("data"),ParkModel.class);
//		List<ParkModel> parkS = jsonResult.getData();

		if (rooms == null || rooms.size() <= 0) {
			logger.info(" 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页room 数据为空！");
			return false;
		}

		totalCount = jsonResult.getInteger("count");
		if (!isLoged){
			logger.info("---------- 环境："+ prop.get("config.name") +"  room 接口返回的数据总条数:" + totalCount);
			isLoged = true;
		}
//		StringBuilder sql = new StringBuilder("select * from ( ");
		List<Integer> pardIds = SynchExecutor.getParkIds(prop.get("config.name"));
		List<String> ids = new ArrayList<String>();
		try {
			for (Iterator<RoomModel> it = rooms.iterator(); it.hasNext();) {
				RoomModel room = it.next();

				//---------------------------判断数据是否重复start
//				EhCacheUtil.getInstance().put("ehcachetest","guojing",123);
				String configName = prop.get("config.name");
//				String id = RedisUtil.getInstance().strings().get(configName + ":" + "building:" + buildingModel.getId());
				String key = configName + ":" + "room:" + room.getId();
				Boolean isExisted = RedisUtil.getInstance(prop).keys().exists(key);
//				String cacheName = prop.get("ehcache.name");//缓存名称
//				Object id = EhCacheUtil.getInstance().get(cacheName,
//						EhcacheKey.ROOM_CACHE.appendStr(room.getId()));
				if (!isExisted){
//					EhCacheUtil.getInstance().put(cacheName,
//							EhcacheKey.ROOM_CACHE.appendStr(room.getId()),room.getId());
					RedisUtil.getInstance(prop).strings().set(key,room.getId());
				}else {
					it.remove();
					continue;
				}
				//---------------------------判断数据是否重复end

				if(isCheckParkExistInWorkgo) {
					if(!pardIds.contains(Integer.parseInt(room.getParkId()))) {
						it.remove();
						continue;
					}
				}
//				if(room.getCtime().getTime() == room.getUtime().getTime() || room.getCtime().getTime() > lastUptDate.getTime()) {
//					this.insertDatas.add(room);
//				}else {
//					this.updateDatas.add(room);
//				}
//				sql.append("select '" + room.getId() + "' id UNION ");
				ids.add(room.getId());
			}

			List<String> rcdS = new ArrayList<String>();
			if (ids != null && ids.size() > 0){
				if (ids.size() < 500){//不需要分批次处理
					StringBuilder sql = new StringBuilder("select * from ( ");
					for (int i=0; i < ids.size(); i++){
						sql.append("select '" + ids.get(i) + "' id UNION ");
					}
					int lastUnIndx = sql.lastIndexOf("UNION");
					int length = sql.length();
					if (sql.lastIndexOf("UNION") > 0) {
						sql.delete(lastUnIndx, length - 1);
					}

					sql.append("from dual) temp where EXISTS (SELECT 1 from room where temp.id = room_id)");
					rcdS = DbHelper.getDb(prop).query(sql.toString());
				}else {//需要分批次处理
					StringBuilder sql = new StringBuilder("select * from ( ");
					int i = 0;
					for(; i <= ids.size() - 1; i++){
						sql.append("select '" + ids.get(i) + "' id UNION ");
						if (i%500 == 0){
							int lastUnIndx = sql.lastIndexOf("UNION");
							int length = sql.length();
							if (sql.lastIndexOf("UNION") > 0) {
								sql.delete(lastUnIndx, length - 1);
							}
							sql.append("from dual) temp where EXISTS (SELECT 1 from room where temp.id = room_id)");
							List<String> temps = DbHelper.getDb(prop).query(sql.toString());
							rcdS.addAll(temps);
							sql = new StringBuilder("select * from ( ");
						}
					}
					if (ids.size() % 500 != 0){//表示还有未处理的数据
						int lastUnIndx = sql.lastIndexOf("UNION");
						int length = sql.length();
						if (sql.lastIndexOf("UNION") > 0) {
							sql.delete(lastUnIndx, length - 1);
						}

						sql.append("from dual) temp where EXISTS (SELECT 1 from room where temp.id = room_id)");

						List<String> temps = DbHelper.getDb(prop).query(sql.toString());
						rcdS.addAll(temps);
					}
				}
			}


			if(rooms.size() > 0) {
//				List<String> rcdS = DbHelper.getDb().query(sql.toString());

				for (Iterator<RoomModel> it = rooms.iterator(); it.hasNext();) {
					RoomModel roomModel = it.next();
					if (rcdS.contains(roomModel.getId())) {
						this.updateDatas.add(roomModel);
					} else {
						this.insertDatas.add(roomModel);
					}
				}
			}
		} catch (Exception e) {
			logger.error("!!!!!!!!!!  环境："+ prop.get("config.name") +" 第"+ pageIndex +"页rooms内容处理失败!",e);
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
		
		System.out.println("room current page: "+ (pageIndex + 1) +",total pages: " + totalPage);
		long startTime = System.currentTimeMillis();
		if (insertDatas != null && insertDatas.size() > 0) {
			RoomService.insertDataS(prop,insertDatas);
			insertSize += insertDatas.size();
		}
		if (updateDatas != null && updateDatas.size() > 0) {
			RoomService.updateDataS(prop,updateDatas);
			updateSize += updateDatas.size();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("room page "+ (pageIndex + 1) +" Synchronous Data success, time: " + (endTime - startTime) + "ms");
	}

	public void run() {
		// TODO Auto-generated method stub
		System.out.println("RoomSynch thread is running ...");
		String strPageSize = prop.get("pageSize");
		pageSize = Integer.parseInt(strPageSize);
		int backTime = prop.getInt("backTime");
		lastDate = RoomService.getLastUpdateDate(prop);
//		Calendar calendar = Calendar.getInstance();
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
							logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " 环境："+ prop.get("config.name") +"  :room 网络接口调用异常！");
							return false;
						}
						Boolean isSuccess = ProcessDataS(result);

						totalPage = (totalCount + (pageSize - 1)) / pageSize;
						if (isSuccess) {
							SynchDatas();
						}

						pageIndex++;
					} while (pageIndex <= totalPage - 1);
//					logger.info("同步room成功！");
					logger.info(" 环境："+ prop.get("config.name") +" 同步room成功！新增" + insertSize + "条，修改" + updateSize + "条！");
					return true;
				} catch (Exception e) {
					logger.error(" 环境："+ prop.get("config.name") +" 同步room失败！",e);
					return false;
				}
			}
		});

	}

}
