package com.topwave.synch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.topwave.app.SynchExecutor;
import com.topwave.bean.FloorModel;
import com.topwave.kit.HttpHelper;
import com.topwave.service.FloorService;
import com.topwave.utils.DbHelper;
import com.topwave.utils.EhCacheUtil;
import com.topwave.utils.EhcacheKey;
import com.topwave.utils.RedisUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FloorSynch implements Runnable {
	Logger logger = Logger.getLogger(FloorSynch.class);

	public List<FloorModel> insertDatas = new ArrayList<FloorModel>();
	public List<FloorModel> updateDatas = new ArrayList<FloorModel>();

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

	public FloorSynch(String token,Prop prop) {
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
			result = HttpHelper.get(prop.get("floorUrl"), params, headers);
//			System.out.println(result);
//			logger.info("----------第"+ pageIndex+"页floor接口返回的数据:" + result);
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
			logger.error("!!!!!!!!!!  环境："+ prop.get("config.name") +" 第"+ pageIndex +"页floor 解析数据内容失败！",e);
			return false;
		}

		if (jsonResult.getIntValue("code") != 200) {
			logger.error("!!!!!!!!!! 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页floor 网络接口调用出现问题,code: "
					+ jsonResult.getIntValue("code") + ", message=" + jsonResult.getString("message") + " ！");
			return false;
		}

		JSONArray arr = jsonResult.getJSONArray("data");// 获取的结果集合转换成数组
		String js = JSONObject.toJSONString(arr);// 将array数组转换成字符串
		List<FloorModel> floors = JSONObject.parseArray(js, FloorModel.class);// 把字符串转换成集合
//		List<ParkModel> parkS = JSON.parseArray(jsonResult.getString("data"),ParkModel.class);
//		List<ParkModel> parkS = jsonResult.getData();

		if (floors == null || floors.size() <= 0) {
			logger.info(" 环境："+ prop.get("config.name") +" 第" + pageIndex + "页floor 数据为空！");
			return false;
		}

		totalCount = jsonResult.getInteger("count");
		if (!isLoged){
			logger.info("---------- 环境："+ prop.get("config.name") +"  floor 接口返回的数据总条数:" + totalCount);
			isLoged = true;
		}
		StringBuilder sql = new StringBuilder("select * from ( ");
		List<Integer> pardIds = SynchExecutor.getParkIds(prop.get("config.name"));
		
		try {
			for (Iterator<FloorModel> it = floors.iterator(); it.hasNext();) {
				FloorModel floorModel = it.next();

				//---------------------------判断数据是否重复start
//				EhCacheUtil.getInstance().put("ehcachetest","guojing",123);
				String configName = prop.get("config.name");
//				String id = RedisUtil.getInstance().strings().get(configName + ":" + "building:" + buildingModel.getId());
				String key = configName + ":" + "floor:" + floorModel.getId();
				Boolean isExisted = RedisUtil.getInstance(prop).keys().exists(key);
//				String cacheName = prop.get("ehcache.name");//缓存名称
//				Object id = EhCacheUtil.getInstance().get(cacheName,
//						EhcacheKey.FLOOR_CACHE.appendStr(floorModel.getId()));
				if (!isExisted){
//					EhCacheUtil.getInstance().put(cacheName,
//							EhcacheKey.FLOOR_CACHE.appendStr(floorModel.getId()),floorModel.getId());
					RedisUtil.getInstance(prop).strings().set(key,floorModel.getId());
				}else {
					it.remove();
					continue;
				}
				//---------------------------判断数据是否重复end

				if(isCheckParkExistInWorkgo) {
					if(!pardIds.contains(Integer.parseInt(floorModel.getParkId()))) {
						it.remove();
						continue;
					}
				}
				sql.append("select '" + floorModel.getId() + "' id UNION ");

//				if(floorModel.getCtime().getTime() == floorModel.getUtime().getTime() || floorModel.getCtime().getTime() > lastUptDate.getTime()) {
//					this.insertDatas.add(floorModel);
//				}else {
//					this.updateDatas.add(floorModel);
//				}
			}

			int lastUnIndx = sql.lastIndexOf("UNION");
			int length = sql.length();
			if (sql.lastIndexOf("UNION") > 0) {
				sql.delete(lastUnIndx, length - 1);
			}

			sql.append("from dual) temp where EXISTS (SELECT 1 from floor where temp.id = floor_id)");
			if(floors.size() > 0) {
				List<String> rcdS = DbHelper.getDb(prop).query(sql.toString());
				for (Iterator<FloorModel> it = floors.iterator(); it.hasNext();) {
					FloorModel floorModel = it.next();
					if (rcdS.contains(floorModel.getId())) {
						this.updateDatas.add(floorModel);
					} else {
						this.insertDatas.add(floorModel);
					}
				}
			}
		} catch (Exception e) {
			logger.error("!!!!!!!!!!  环境："+ prop.get("config.name") +" 第"+ pageIndex + "页floor遍历失败",e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 同步数据
	 * 
	 * @throws Exception
	 */
	public void SynchDatas() throws Exception {
		
		System.out.println("floor current page: "+ (pageIndex + 1) +",total pages: " + totalPage);
		long startTime = System.currentTimeMillis();
		if (insertDatas != null && insertDatas.size() > 0) {
			FloorService.insertDataS(prop,insertDatas);
			insertSize += insertDatas.size();
		}
		if (updateDatas != null && updateDatas.size() > 0) {
			FloorService.updateDataS(prop,updateDatas);
			updateSize += updateDatas.size();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("floor page "+ (pageIndex + 1) +" Synchronous Data success, time: " + (endTime - startTime) + "ms");
		
	}

	public void run() {
		// TODO Auto-generated method stub
		System.out.println("FloorSynch thread is running ...");
		String strPageSize = prop.get("pageSize");
		pageSize = Integer.parseInt(strPageSize);
		int backTime = prop.getInt("backTime");
		lastDate = FloorService.getLastUpdateDate(prop);

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
							logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : floor 网络接口调用异常！");
							return false;
						}
						Boolean isSuccess = ProcessDataS(result);
						totalPage = (totalCount + (pageSize - 1)) / pageSize;
						if (isSuccess) {
							SynchDatas();
						}
						pageIndex++;
					} while (pageIndex <= totalPage - 1);
//					logger.info("同步floor成功！");
					logger.info(" 环境："+ prop.get("config.name") +" 同步floor成功！新增" + insertSize + "条，修改" + updateSize + "条！");
					return true;
				} catch (Exception e) {
					logger.error(" 环境："+ prop.get("config.name") +" 同步floor失败！",e);
					return false;
				}
			}
		});
	}

}
