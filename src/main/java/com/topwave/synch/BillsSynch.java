package com.topwave.synch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.topwave.app.SynchExecutor;
import com.topwave.bean.BillCashFlowModel;
import com.topwave.bean.BillCashMatchesModel;
import com.topwave.bean.BillModel;
import com.topwave.bean.BillRoomModel;
import com.topwave.kit.HttpHelper;
import com.topwave.service.BillService;
import com.topwave.utils.DbHelper;
import com.topwave.utils.EhCacheUtil;
import com.topwave.utils.EhcacheKey;
import com.topwave.utils.RedisUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BillsSynch implements Runnable {

	private Date lastUptDate = null;

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	Logger logger = Logger.getLogger(BillsSynch.class);

	public List<BillModel> insertDatas = new ArrayList<BillModel>();
	public List<BillModel> updateDatas = new ArrayList<BillModel>();

	public List<BillCashMatchesModel> insertCashMatchDatas = new ArrayList<BillCashMatchesModel>();
	public List<BillRoomModel> insertRoomDatas = new ArrayList<BillRoomModel>();
	public List<BillCashFlowModel> insertCashFlowDatas = new ArrayList<BillCashFlowModel>();

	private Integer pageSize = 20;// 一页的个数

	private Integer totalCount = 0;// 总条数

	private Integer totalPage;// 计算出的总页数

	private Integer pageIndex = 0;// 当前的页数

	private Date lastDate;// 最近一次更新的时间

	private String token;

	private Prop prop;//配置

	private Boolean isLoged = false;

	public BillsSynch(String token,Prop prop) {
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
//			Calendar now = Calendar.getInstance();
//			now.add(Calendar.MONTH, -1);// 月份减1
//			lastDate = now.getTime();

			SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
			lastDate = formatter.parse("0000-00-00 00:00:00");
		}

		this.lastUptDate = lastDate;

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
			result = HttpHelper.get(prop.get("billUrl"), params, headers);
//			logger.info("----------第"+ pageIndex+"页bill接口返回的数据:" + result);
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

		try {
			jsonResult = JSONObject.parseObject(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("!!!!!!!!!  环境："+ prop.get("config.name") +" 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页bills 数据内容反序列化失败！",e);
			return false;
		}

		if (jsonResult.getIntValue("code") != 200) {
			logger.error("!!!!!!!!!!  环境："+ prop.get("config.name") +" 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页bill 网络接口调用出现问题,code: "
					+ jsonResult.getIntValue("code") + ", message=" + jsonResult.getString("message") + " ！");
			return false;
		}

		JSONArray arr = jsonResult.getJSONArray("data");// 获取的结果集合转换成数组
		String js = JSONObject.toJSONString(arr);// 将array数组转换成字符串
		List<BillModel> bills = JSONObject.parseArray(js, BillModel.class);// 把字符串转换成集合
		if (bills == null || bills.size() <= 0) {
			logger.info("!!!!!!!!!!   环境："+ prop.get("config.name") +" 环境："+ prop.get("config.name") +" 第"+ pageIndex +"页bill 数据为空！");
			return false;
		}

		totalCount = jsonResult.getInteger("count");
		if (!isLoged){
			logger.info("----------  环境："+ prop.get("config.name") +" bill 接口返回的数据总条数:" + totalCount);
			isLoged = true;
		}
//		StringBuilder sql = new StringBuilder("select * from ( ");

		List<String> ids = new ArrayList<String>();
		try {
			for (Iterator<BillModel> it = bills.iterator(); it.hasNext();) {
				BillModel billModel = it.next();

				//---------------------------判断数据是否重复start
//				String cacheName = prop.get("ehcache.name");//缓存名称
//				Object id = EhCacheUtil.getInstance().get(cacheName,
//						EhcacheKey.BILL_CACHE.appendStr(billModel.getId()));

				String configName = prop.get("config.name");
				String key = configName + ":" + "bill:" + billModel.getId();
				Boolean isExisted = RedisUtil.getInstance(prop).keys().exists(key);
				if (!isExisted){
//					EhCacheUtil.getInstance().put(cacheName,
//							EhcacheKey.BILL_CACHE.appendStr(billModel.getId()),billModel.getId());
					RedisUtil.getInstance(prop).strings().set(key,billModel.getId());
				}else {
					it.remove();
					continue;
				}
				//---------------------------判断数据是否重复end


//				sql.append("select '" + billModel.getId() + "' id UNION ");
				ids.add(billModel.getId());
			}
			List<String> rcdS = new ArrayList<String>();

			if (ids != null && ids.size() > 0){
//				Integer size = ids.size();
//				int totalPageNum = (size  - 1) / 500 + 1;
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

					sql.append("from dual) temp where EXISTS (SELECT 1 from bill where temp.id = bill_id)");
					rcdS = DbHelper.getDb(prop).query(sql.toString());
				}else {//需要分批次处理
					StringBuilder sql = new StringBuilder("select * from ( ");
//					for (int i=0; i < ids.size(); i++){
//						sql.append("select '" + ids.get(i) + "' id UNION ");
//						if (i%500 == 0){
//							int lastUnIndx = sql.lastIndexOf("UNION");
//							int length = sql.length();
//							if (sql.lastIndexOf("UNION") > 0) {
//								sql.delete(lastUnIndx, length - 1);
//							}
//
//							sql.append("from dual) temp where EXISTS (SELECT 1 from bill where temp.id = bill_id)");
//
//							List<String> temps = DbHelper.getDb().query(sql.toString());
//							rcdS.addAll(temps);
//							sql = new StringBuilder("select * from ( ");
//						}
//					}

					int i = 0;
					for(; i <= ids.size() - 1; i++){
						sql.append("select '" + ids.get(i) + "' id UNION ");
						if (i%500 == 0){
							int lastUnIndx = sql.lastIndexOf("UNION");
							int length = sql.length();
							if (sql.lastIndexOf("UNION") > 0) {
								sql.delete(lastUnIndx, length - 1);
							}

							sql.append("from dual) temp where EXISTS (SELECT 1 from bill where temp.id = bill_id)");

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

						sql.append("from dual) temp where EXISTS (SELECT 1 from bill where temp.id = bill_id)");

						List<String> temps = DbHelper.getDb(prop).query(sql.toString());
						rcdS.addAll(temps);
					}
//					if (!ids.isEmpty()){//剩余未处理的数据
//						StringBuilder sql1 = new StringBuilder("select * from ( ");
//						for (int i=0; i < ids.size(); i++){
//							sql1.append("select '" + ids.get(i) + "' id UNION ");
//						}
//						int lastUnIndx = sql1.lastIndexOf("UNION");
//						int length = sql1.length();
//						if (sql1.lastIndexOf("UNION") > 0) {
//							sql1.delete(lastUnIndx, length - 1);
//						}
//
//						sql1.append("from dual) temp where EXISTS (SELECT 1 from bill where temp.id = bill_id)");
//						rcdS = DbHelper.getDb().query(sql1.toString());
//					}
				}
			}
//			int lastUnIndx = sql.lastIndexOf("UNION");
//			int length = sql.length();
//			if (sql.lastIndexOf("UNION") > 0) {
//				sql.delete(lastUnIndx, length - 1);
//			}
//
//			sql.append("from dual) temp where EXISTS (SELECT 1 from bill where temp.id = bill_id)");
//
//			List<String> rcdS = DbHelper.getDb().query(sql.toString());




			for (Iterator<BillModel> it = bills.iterator(); it.hasNext();) {
				BillModel billModel = it.next();
				if (rcdS.contains(billModel.getId())) {
					this.updateDatas.add(billModel);
				} else {
					this.insertDatas.add(billModel);
				}
//				this.updateDatas.add(billModel);
				List<BillCashMatchesModel> billCashMatches = billModel.getBillCashMatches();
				List<BillRoomModel> billRooms = billModel.getRooms();

				if (billCashMatches != null && billCashMatches.size() > 0) {
					insertCashMatchDatas.addAll(billCashMatches);
					for (Iterator<BillCashMatchesModel> billCashMatchIt = billCashMatches.iterator(); billCashMatchIt
							.hasNext();) {
						BillCashMatchesModel billCashMatch = billCashMatchIt.next();
//						Integer id = billCashMatch.getBillId();
						Integer id = billCashMatch.getId();
						BillCashFlowModel cashFlowModel = billCashMatch.getCashFlow();
						cashFlowModel.setBillCashMatchId(id);
						insertCashFlowDatas.add(cashFlowModel);
					}
				}
				if (billRooms != null && billRooms.size() > 0) {
					insertRoomDatas.addAll(billRooms);
				}
			}

//			for(Iterator<BillCashMatchesModel> billCashMatchIt = insertCashMatchDatas.iterator(); billCashMatchIt.hasNext();) {
//				BillCashMatchesModel billCashMatch = billCashMatchIt.next();
//				
//				BillCashFlowModel CashFlowS = billCashMatch.getCashFlow();
//				for(CashFlowS) {
//					
//				}
//			}

		} catch (Exception e) {
			logger.error("！！！！！！！！！ 环境："+ prop.get("config.name") +"  第"+ pageIndex + "页bill处理失败",e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

//	/**
//	 * 同步数据
//	 */
//	public void SynchDatas() {
//
//		Db.tx(new IAtom() {
//			public boolean run() throws SQLException {
//				try {
//					if (insertDatas != null && insertDatas.size() > 0) {
//						BillService.insertDataS(insertDatas);
//					}
//					if (updateDatas != null && updateDatas.size() > 0) {
//						BillService.updateDataS(updateDatas);
//					}
//					if (insertCashMatchDatas != null && insertCashMatchDatas.size() > 0) {
//						BillService.insertBillCashMatches(insertCashMatchDatas);
//					}
//					if (insertRoomDatas != null && insertRoomDatas.size() > 0) {
//						BillService.insertBillRooms(insertRoomDatas);
//					}
//					if (insertCashFlowDatas != null && insertCashFlowDatas.size() > 0) {
//						BillService.insertCashFlows(insertCashFlowDatas);
//					}
//					logger.error("同步bill成功");
//					return true;
//				} catch (Exception e) {
//					logger.error("同步bill失败");
//					return false;
//				}
//
//			}
//
//		});
//	}

	Integer insertSize = 0;
	Integer updateSize = 0;

	/**
	 * 同步数据
	 * 
	 * @throws Exception
	 */
	public void SynchDatas() throws Exception {
		System.out.println("bill current page: "+ (pageIndex + 1) +",total pages: " + totalPage);
		long startTime = System.currentTimeMillis();
		if (insertDatas != null && insertDatas.size() > 0) {
			BillService.insertDataS(prop,insertDatas);
			insertSize += insertDatas.size();
		}
		if (updateDatas != null && updateDatas.size() > 0) {
			BillService.updateDataS(prop,updateDatas);
			updateSize += updateDatas.size();
		}
		if (insertCashMatchDatas != null && insertCashMatchDatas.size() > 0) {
			BillService.insertBillCashMatches(prop,insertCashMatchDatas);
		}
		if (insertRoomDatas != null && insertRoomDatas.size() > 0) {
			BillService.insertBillRooms(prop,insertRoomDatas);
		}
		if (insertCashFlowDatas != null && insertCashFlowDatas.size() > 0) {
			BillService.insertCashFlows(prop,insertCashFlowDatas);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("bill page "+ (pageIndex + 1) +" Synchronous Data success, time: " + (endTime - startTime) + "ms");
	}

	public void run() {
		System.out.println("BillsSynch thread is running ...");
		String strPageSize = prop.get("pageSize");
		pageSize = Integer.parseInt(strPageSize);
		int backTime = prop.getInt("backTime");
		lastDate = BillService.getLastUpdateDate(prop);

		if (lastDate != null) {
//			Calendar calendar = Calendar.getInstance();
//			calendar.setTime(lastDate);
//			calendar.add(Calendar.MINUTE, -1);//
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
						insertCashMatchDatas.clear();
						insertCashMatchDatas.clear();
						insertRoomDatas.clear();
						insertCashFlowDatas.clear();

						String result = fetchFromWebApi(token, lastDate);
						if (StrKit.isBlank(result)) {
							logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : bill 网络接口调用异常！");
							return false;
						}
						Boolean isSuccess = ProcessDataS(result);
						totalPage = (totalCount + (pageSize - 1)) / pageSize;

						if (isSuccess) {
							SynchDatas();
						}

						pageIndex++;

					} while (pageIndex <= totalPage - 1);
					logger.info(" 环境："+ prop.get("config.name") +" 同步bill成功！新增" + insertSize + "条，修改" + updateSize + "条！");
					return true;

				} catch (Exception e) {
					logger.error(" 环境："+ prop.get("config.name") +" 同步bill失败！",e);
					return false;
				}
			}

		});

	}

}
