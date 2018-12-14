package com.topwave.synch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.redis.Redis;
import com.topwave.app.SynchExecutor;
import com.topwave.bean.BuildingModel;
import com.topwave.kit.HttpHelper;
import com.topwave.model.Building;
import com.topwave.service.BuildingService;
import com.topwave.utils.DbHelper;
import com.topwave.utils.EhCacheUtil;
import com.topwave.utils.EhcacheKey;
import com.topwave.utils.RedisUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BuildingSynch implements Runnable {

	Logger logger = Logger.getLogger(BuildingSynch.class);

	public List<BuildingModel> insertDatas = new ArrayList<BuildingModel>();
	public List<BuildingModel> updateDatas = new ArrayList<BuildingModel>();

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

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

	public BuildingSynch(String token,Prop prop) {
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
	public String fetchFromWebApi(String token, Date lastUptDate) throws ParseException, InterruptedException {
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
		if (pageIndex>0){
// add time limit
			try {
				System.out.println("============timeDelay"+prop.getInt("requestTimeLimit"));
				Thread.sleep(prop.getInt("requestTimeLimit"));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		params.put("pageSize", pageSize);
//        String paras = JSONObject.toJSON(params).toString();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json; charset=utf-8");
		headers.put("Authorization", token);
		String result = null;
		try {
			result = HttpHelper.get(prop.get("buildingUrl"), params, headers);
//			logger.info("----------第"+ pageIndex+"页building接口返回的数据:" + result);
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
			logger.error("!!!!!!!!!! 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页building数据反序列化失败！",e);
			return false;
		}

		if (jsonResult.getIntValue("code") != 200) {
			logger.error("!!!!!!!!!! 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页 building网络接口调用出现问题,code: "
					+ jsonResult.getIntValue("code") + ", message=" + jsonResult.getString("message") + " ！");
			return false;
		}

		JSONArray arr = jsonResult.getJSONArray("data");// 获取的结果集合转换成数组
		String js = JSONObject.toJSONString(arr);// 将array数组转换成字符串
		List<BuildingModel> buildingS = JSONObject.parseArray(js, BuildingModel.class);// 把字符串转换成集合
//		List<ParkModel> parkS = JSON.parseArray(jsonResult.getString("data"),ParkModel.class);
//		List<ParkModel> parkS = jsonResult.getData();

		if (buildingS == null || buildingS.size() <= 0) {
			logger.info(" 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页 building 数据为空！");
			return false;
		}

		totalCount = jsonResult.getInteger("count");
		if (!isLoged){
			logger.info("---------- 环境："+ prop.get("config.name") +" building 接口返回的数据总条数:" + totalCount);
			isLoged = true;
		}

		List<String> ids = new ArrayList<String>();
		StringBuilder sql = new StringBuilder("select * from ( ");
		List<Integer> pardIds = SynchExecutor.getParkIds(prop.get("config.name"));

//		Map<String,Integer> duplicateIdS = new HashMap<String, Integer>();
		try {
			for (Iterator<BuildingModel> it = buildingS.iterator(); it.hasNext();) {
				BuildingModel buildingModel = it.next();
				//---------------------------判断数据是否重复start
//				EhCacheUtil.getInstance().put("ehcachetest","guojing",123);
//				String cacheName = prop.get("ehcache.name");//缓存名称
//				Object id = EhCacheUtil.getInstance().get(cacheName,
//						EhcacheKey.BUILDING_CACHE.appendStr(buildingModel.getId()));
				String configName = prop.get("config.name");
//				String id = RedisUtil.getInstance().strings().get(configName + ":" + "building:" + buildingModel.getId());
				String key = configName + ":" + "building:" + buildingModel.getId();
				Boolean isExisted = RedisUtil.getInstance(prop).keys().exists(key);
				if (!isExisted){
//					EhCacheUtil.getInstance().put(cacheName,
//							EhcacheKey.BUILDING_CACHE.appendStr(buildingModel.getId()),buildingModel.getId());
					RedisUtil.getInstance(prop).strings().set(key,buildingModel.getId());
				}else {
					it.remove();
					continue;
				}
				//---------------------------判断数据是否重复end
				if(isCheckParkExistInWorkgo) {
					if(!pardIds.contains(Integer.parseInt(buildingModel.getParkId()))) {
						it.remove();
						continue;
					}
				}
				
				sql.append("select '" + buildingModel.getId() + "' id UNION ");
//				ids.add();
//				if(buildingModel.getCtime().getTime() == buildingModel.getUtime().getTime() || buildingModel.getCtime().getTime() > lastUptDate.getTime()) {
//					this.insertDatas.add(buildingModel);
//				}else {
//					this.updateDatas.add(buildingModel);
//				}
			}
			int lastUnIndx = sql.lastIndexOf("UNION");
			int length = sql.length();
			if (sql.lastIndexOf("UNION") > 0) {
				sql.delete(lastUnIndx, length - 1);
			}

			sql.append("from dual) temp where EXISTS (SELECT 1 from building where temp.id = building_id)");
			if(buildingS.size() > 0) {
				List<String> rcdS = DbHelper.getDb(prop).query(sql.toString());
				
				for (Iterator<BuildingModel> it = buildingS.iterator(); it.hasNext();) {
					BuildingModel buildModel = it.next();
					if (rcdS.contains(buildModel.getId())) {
						this.updateDatas.add(buildModel);
					} else {
						this.insertDatas.add(buildModel);
					}
				}
			}

		} catch (Exception e) {
			logger.error("!!!!!!!!!! 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页building数据处理失败",e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

/*
	*//**
	 * @param buildings
	 * @throws Exception
	 *//*
	private void insertDataS(List<BuildingModel> buildings) throws Exception {
//		int[] count = null;
//		boolean isInsertSuccess = false;
		List<String> sqlList = new ArrayList<String>();

		try {
			for (Iterator<BuildingModel> it = buildings.iterator(); it.hasNext();) {
				BuildingModel building = it.next();
				String sql = "INSERT INTO building "
						+ "(building_id,building_name,park_id,park_name,address,image_url,remark,is_del,ctime,utime) VALUES "
						+ "(" + building.getId() + ", '" + building.getBuildingName() + "', '" + building.getParkId()
						+ "', '" + building.getParkName() + "', '" + building.getAddress() + "', '"
						+ building.getImageUrl() + "', '" + building.getRemark() + "', " + building.getIsDel() + ",'"
						+ building.getCtime() + "','" + building.getUtime() + "');";
				sqlList.add(sql);
			}
			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
//			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :building 批量插入失败！");
			e.printStackTrace();
			throw e;
		}
//		if(count != null && count.length > 0) {
//			isInsertSuccess = true;
//		}
//		return isInsertSuccess;
	}
	
	


	*//**
	 * @param buildings
	 * @throws Exception
	 *//*
	private void updateDataS(List<BuildingModel> buildings) throws Exception {
//		int[] count = null;
//		boolean isUptSuccess = false;
		List<String> sqlList = new ArrayList<String>();
		try {
			for (Iterator<BuildingModel> it = buildings.iterator(); it.hasNext();) {
				BuildingModel building = it.next();

				String sql = "update building set building_name = '" + building.getBuildingName() + "', park_id='"
						+ building.getParkId() + "', park_name='" + building.getParkName() + "' , address='"
						+ building.getAddress() + "', image_url='" + building.getImageUrl() + "', remark='"
						+ building.getRemark() + "',is_del = '" + building.getIsDel() + "' ,ctime = '"
						+ building.getCtime() + "', utime= '" + building.getUtime() + "' where building_id = "
						+ building.getId() + ";";
				sqlList.add(sql);
			}

			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
//			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : building 批量更新失败！");
			e.printStackTrace();
			throw e;
		}
//		if(count != null && count.length > 0) {
//			isUptSuccess = true;
//		}
//		return isUptSuccess;
	}
	*/
	
	

	/**
	 * @param models
	 * @throws Exception
	 */
	private void insertDataS(List<BuildingModel> models) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		List<Building> buildings = new ArrayList<Building>();
		try {
			for (Iterator<BuildingModel> it = models.iterator(); it.hasNext();) {
				BuildingModel model = it.next();
				Building building = new Building();
				building.setBuildingId(model.getId());
				building.setBuildingName(model.getBuildingName());
				building.setParkId(model.getParkId());
				building.setParkName(model.getParkName());
				building.setAddress(model.getAddress());
				building.setImageUrl(model.getImageUrl());
				building.setRemark(model.getRemark());
				building.setIsDel(model.getIsDel());
				building.setCtime(model.getCtime());
				building.setUtime(model.getUtime());

				buildings.add(building);
			}
			DbHelper.getDb(prop).batchSave(buildings, buildings.size());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("!!!!!!!!! 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页building数据新增失败！",e);
			throw e;
		}
	}


	/**
	 * @param models
	 * @throws Exception
	 */
	private void updateDataS(List<BuildingModel> models) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		List<Building> buildings = new ArrayList<Building>();
		try {
			for (Iterator<BuildingModel> it = models.iterator(); it.hasNext();) {
				BuildingModel model = it.next();
				Building building = new Building();
				building.setBuildingId(model.getId());
				building.setBuildingName(model.getBuildingName());
				building.setParkId(model.getParkId());
				building.setParkName(model.getParkName());
				building.setAddress(model.getAddress());
				building.setImageUrl(model.getImageUrl());
				building.setRemark(model.getRemark());
				building.setIsDel(model.getIsDel());
				building.setCtime(model.getCtime());
				building.setUtime(model.getUtime());

				buildings.add(building);
			}

			DbHelper.getDb(prop).batchUpdate(buildings, buildings.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("!!!!!!!!! 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页building数据修改失败！",e);
			throw e;
		}
	}

	/**
	 * 同步数据
	 * 
	 * @throws Exception
	 */
	public void SynchDatas() throws Exception {
		System.out.println("building current page: "+ (pageIndex + 1) +",total pages: " + totalPage);
		long startTime = System.currentTimeMillis();
		if (insertDatas != null && insertDatas.size() > 0) {
			insertDataS(insertDatas);
			insertSize += insertDatas.size();
		}
		if (updateDatas != null && updateDatas.size() > 0) {
			updateDataS(updateDatas);
			updateSize += updateDatas.size();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("building page "+ (pageIndex + 1) +" Synchronous Data success, time: " + (endTime - startTime) + "ms");
	}

	public void run() {
		System.out.println("BuildingSynch thread is running ...");

		String strPageSize = prop.get("pageSize");
		pageSize = Integer.parseInt(strPageSize);
		int backTime = prop.getInt("backTime");
		
		lastDate = BuildingService.getLastUpdateDate(prop);
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
//							logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :building 网络接口调用异常！");
							return false;
						}
						Boolean isSuccess = ProcessDataS(result);

						totalPage = (totalCount + (pageSize - 1)) / pageSize;
						if (isSuccess) {
							SynchDatas();
						}
						pageIndex++;
					} while (pageIndex <= totalPage - 1);
//					logger.info("同步building成功！");
					logger.info("环境："+ prop.get("config.name") +" 同步building成功！新增" + insertSize + "条，修改" + updateSize + "条！");
					return true;
				} catch (Exception e) {
					logger.error("环境："+ prop.get("config.name") +" 同步building失败！",e);
					return false;
				}
			}
		});
	}

}
