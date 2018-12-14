package com.topwave.synch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.topwave.app.SynchExecutor;
import com.topwave.bean.ContractContactModel;
import com.topwave.bean.ContractLeaseTermModel;
import com.topwave.bean.ContractModel;
import com.topwave.bean.ContractRoomModel;
import com.topwave.kit.HttpHelper;
import com.topwave.service.ContractService;
import com.topwave.utils.DbHelper;
import com.topwave.utils.EhCacheUtil;
import com.topwave.utils.EhcacheKey;
import com.topwave.utils.RedisUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ContractSynch implements Runnable {

	private Date lastUptDate = null;

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	Logger logger = Logger.getLogger(ContractSynch.class);

	public List<ContractModel> insertDatas = new ArrayList<ContractModel>();
	public List<ContractModel> updateDatas = new ArrayList<ContractModel>();

	public List<ContractRoomModel> insertRoomDatas = new ArrayList<ContractRoomModel>();
//    public List<ContractRoomModel> updateRoomDatas = new ArrayList<ContractRoomModel>();
//    
	public List<ContractContactModel> insertContactDatas = new ArrayList<ContractContactModel>();
//    public List<ContractContactModel> updateContactDatas = new ArrayList<ContractContactModel>();
//    
	public List<ContractLeaseTermModel> insertLeaseTermDatas = new ArrayList<ContractLeaseTermModel>();
//    public List<ContractLeaseTermModel> updateLeaseTermDatas = new ArrayList<ContractLeaseTermModel>();

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

	public ContractSynch(String token,Prop prop) {
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
//			now.add(Calendar.HOUR, -200);//
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
//			result = HttpHelper.post(PropKit.use("config.properties").get("parkUrl"),paras,headers);
//			long startTime = System.currentTimeMillis();
			result = HttpHelper.get(prop.get("contractUrl"), params, headers);
//			System.out.println(result);
//			logger.info("----------第"+ pageIndex+"页contract接口返回的数据:" + result);
//			long endTime = System.currentTimeMillis();

//			System.out.println("程序运行时间： " + (endTime - startTime) + "ms");

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
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
			logger.error("!!!!!!!!!!  环境："+ prop.get("config.name") +" 第"+ pageIndex +"页contract数据反序列化失败！",e);
			return false;
		}

		if (jsonResult.getIntValue("code") != 200) {
			logger.error("!!!!!!!!!!  环境："+ prop.get("config.name") +" 第"+ pageIndex +"页contract网络接口调用出现问题,code: "
					+ jsonResult.getIntValue("code") + ", message=" + jsonResult.getString("message") + " ！");
			return false;
		}

		JSONArray arr = jsonResult.getJSONArray("data");// 获取的结果集合转换成数组
		String js = JSONObject.toJSONString(arr);// 将array数组转换成字符串
		List<ContractModel> contracts = JSONObject.parseArray(js, ContractModel.class);// 把字符串转换成集合

		if (contracts == null || contracts.size() <= 0) {
			logger.info("!!!!!!!!!!  环境："+ prop.get("config.name") +" 第"+ pageIndex +"页contract 数据为空！");
			return false;
		}

		totalCount = jsonResult.getInteger("count");
		if (!isLoged){
			logger.info("----------  环境："+ prop.get("config.name") +" contract 接口返回的数据总条数:" + totalCount);
			isLoged = true;
		}
//		StringBuilder sql = new StringBuilder("select * from ( ");
		List<String> ids = new ArrayList<String>();
		try {
			for (Iterator<ContractModel> it = contracts.iterator(); it.hasNext();) {
				ContractModel contract = it.next();

				//---------------------------判断数据是否重复start
//				EhCacheUtil.getInstance().put("ehcachetest","guojing",123);
				String configName = prop.get("config.name");
				String key = configName + ":" + "contract:" + contract.getId();
				Boolean isExisted = RedisUtil.getInstance(prop).keys().exists(key);
				if (!isExisted){
//					EhCacheUtil.getInstance().put(cacheName,
//							EhcacheKey.TENANT_CACHE.appendStr(contract.getId()),contract.getId());
					RedisUtil.getInstance(prop).strings().set(key,contract.getId());
				}else {
					it.remove();
					continue;
				}
				//---------------------------判断数据是否重复end

				String contractNo = contract.getContractNo();
				if(StrKit.isBlank(contractNo)) {
					contract.setContractNo(UUID.randomUUID().toString());
				}
//				sql.append("select '" + contract.getId() + "' id UNION ");
				ids.add(contract.getId());
			}

//			int lastUnIndx = sql.lastIndexOf("UNION");
//			int length = sql.length();
//			if (sql.lastIndexOf("UNION") > 0) {
//				sql.delete(lastUnIndx, length - 1);
//			}


//			List<String> rcdS = DbHelper.getDb().query(sql.toString());
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

//					sql.append("from dual) temp where EXISTS (SELECT 1 from bill where temp.id = bill_id)");
					sql.append("from dual) temp where EXISTS (SELECT 1 from contract where temp.id = contract_id)");
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
//							sql.append("from dual) temp where EXISTS (SELECT 1 from bill where temp.id = bill_id)");
							sql.append("from dual) temp where EXISTS (SELECT 1 from contract where temp.id = contract_id)");

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
						sql.append("from dual) temp where EXISTS (SELECT 1 from contract where temp.id = contract_id)");
						List<String> temps = DbHelper.getDb(prop).query(sql.toString());
						rcdS.addAll(temps);
					}
				}
			}


			for (Iterator<ContractModel> it = contracts.iterator(); it.hasNext();) {
				ContractModel contractModel = it.next();
				if (rcdS.contains(contractModel.getId())) {
					this.updateDatas.add(contractModel);
				} else {
					this.insertDatas.add(contractModel);
				}
				List<ContractRoomModel> rooms = contractModel.getRooms();
				List<ContractContactModel> contacts = contractModel.getContacts();
				List<ContractLeaseTermModel> terms = contractModel.getLeaseTerms();
				StringBuilder sqlRoom = new StringBuilder("select * from ( ");
				if (rooms != null && rooms.size() > 0) {
					insertRoomDatas.addAll(rooms);
				}
				if (contacts != null && contacts.size() > 0) {
					insertContactDatas.addAll(contacts);
				}

				if (terms != null && terms.size() > 0) {
					insertLeaseTermDatas.addAll(terms);
				}

			}
		} catch (Exception e) {
			logger.error("!!!!!!!!!!  环境："+ prop.get("config.name") +" 第"+ pageIndex +"页contract处理失败",e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

//	/**
//	 * 同步数据
//	 */
//	public void SynchDatas() {
//		Db.tx(new IAtom() {
//			public boolean run() throws SQLException {
//				try {
//					if (insertDatas != null && insertDatas.size() > 0) {
//						ContractService.insertDataS(insertDatas);
//					}
//					if (updateDatas != null && updateDatas.size() > 0) {
//						ContractService.updateDataS(updateDatas);
//					}
//					if(insertRoomDatas != null && insertRoomDatas.size() > 0) {
//						ContractService.insertRoomDataS(insertRoomDatas);
//					}
//					if(insertContactDatas != null && insertContactDatas.size() > 0) {
//						ContractService.insertContactDataS(insertContactDatas);
//					}
//					if(insertLeaseTermDatas != null && insertLeaseTermDatas.size() > 0) {
//						ContractService.insertLeaseTermDataS(insertLeaseTermDatas);
//					}
//					
//					logger.error("同步contract成功");
//					return true;
//				} catch (Exception e) {
//					logger.error("同步contract失败");
//					return false;
//				}
//			}
//		});
//	}

	/**
	 * 同步数据
	 * 
	 * @throws Exception
	 */
	public void SynchDatas() throws Exception {
		System.out.println("contract current page: "+ (pageIndex + 1) +",total pages: " + totalPage);
		long startTime = System.currentTimeMillis();
		if (insertDatas != null && insertDatas.size() > 0) {
			ContractService.insertDataS(prop,insertDatas);
			insertSize += insertDatas.size();
		}
		if (updateDatas != null && updateDatas.size() > 0) {
			ContractService.updateDataS(prop,updateDatas);
			updateSize += updateDatas.size();
		}
		if (insertRoomDatas != null && insertRoomDatas.size() > 0) {
			ContractService.insertRoomDataS(prop,insertRoomDatas);
		}
		if (insertContactDatas != null && insertContactDatas.size() > 0) {
			ContractService.insertContactDataS(prop,insertContactDatas);
		}
		if (insertLeaseTermDatas != null && insertLeaseTermDatas.size() > 0) {
			ContractService.insertLeaseTermDataS(prop,insertLeaseTermDatas);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("contract page "+ (pageIndex + 1) +" Synchronous Data success, time: " + (endTime - startTime) + "ms");
	}

	public void run() {
		System.out.println("ContractSynch thread is running ...");

		String strPageSize = prop.get("pageSize");
		pageSize = Integer.parseInt(strPageSize);
		int backTime = prop.getInt("backTime");
		lastDate = ContractService.getLastUpdateDate(prop);
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
						insertRoomDatas.clear();
						insertContactDatas.clear();
						insertLeaseTermDatas.clear();
						
						String result = fetchFromWebApi(token, lastDate);
						if (StrKit.isBlank(result)) {
							logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :contract 网络接口调用异常！");
							return false;
						}

						Boolean isSuccess = ProcessDataS(result);

						totalPage = (totalCount + (pageSize - 1)) / pageSize;
						if (isSuccess) {
							SynchDatas();
						}
						pageIndex++;
					} while (pageIndex <= totalPage - 1);
//					System.out.println("同步contract成功！");
//					logger.info("同步contract成功！");
					logger.info(" 环境："+ prop.get("config.name") +" 同步contract成功！新增" + insertSize + "条，修改" + updateSize + "条！");
					return true;
				} catch (Exception e) {
//					System.out.println("同步contract失败！");
					logger.error(" 环境："+ prop.get("config.name") +" 同步contract失败",e);
					return false;
				}
			}

		});

	}

}
