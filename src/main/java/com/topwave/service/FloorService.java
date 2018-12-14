package com.topwave.service;

import com.jfinal.kit.Prop;
import com.jfinal.plugin.activerecord.Record;
import com.topwave.bean.FloorModel;
import com.topwave.model.Floor;
import com.topwave.synch.FloorSynch;
import com.topwave.utils.DbHelper;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class FloorService {
	static Logger logger = Logger.getLogger(FloorService.class);
	
	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	public static Date getLastUpdateDate(Prop prop) {
		String sql = "SELECT * from floor ORDER BY utime DESC LIMIT 1";
		Record rcd = DbHelper.getDb(prop).findFirst(sql);
		if(rcd!=null) {
			return rcd.getDate("utime");
//			rcd.getTimestamp("utime");
		}else {
			return null;
		}
	}
	
	/**
	 * 插入数据
	 * @param parks
	 * @throws Exception 
	 *//*
	public static void insertDataS(List<FloorModel> floors) throws Exception {
		List<String> sqlList = new ArrayList<String>();

		try {
			for(Iterator<FloorModel> it = floors.iterator(); it.hasNext();) {
				FloorModel floor = it.next();
				String sql = "INSERT INTO floor "
						+ "(floor_id,floor,building_id,building_name,park_id,park_name,remark,is_del,ctime,utime) VALUES "
						+ "(" +
						floor.getId() + ", '"+ floor.getFloor() +"', '" + floor.getBuildingId() + "', '" + floor.getBuildingName() + "', '" +  
						floor.getParkId() + "', '" + floor.getParkName() + "', '" + floor.getRemark() +"', " + 
						floor.getIsDel() + ",'" + floor.getCtime() + "','"+ floor.getUtime() +"')";
				sqlList.add(sql);
			}
			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :floor 批量插入失败！");
			e.printStackTrace();
			throw e;
		}
	}
	
	*//**
	 * 修改数据
	 * @param parks
	 * @throws Exception 
	 *//*
	public static void updateDataS(List<FloorModel> floors) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		try {
			for(Iterator<FloorModel> it = floors.iterator(); it.hasNext();) {
				FloorModel floor = it.next();
				
				String sql = "update floor set floor = '" + floor.getFloor() + "' ,building_id='" + floor.getBuildingId() + 
						"', building_name='" + floor.getBuildingName() +  "', park_id='" + floor.getParkId() + "', park_name='" + 
						floor.getParkName() + "', remark='" + 
						floor.getRemark() +"',is_del = '"+ floor.getIsDel()
				+"' ,ctime = '"+ floor.getCtime() +"', utime= '"+ floor.getUtime() +"' where floor_id = " + floor.getId() + ";";
				sqlList.add(sql);
			}
			
			
			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : floor 批量更新失败！");
			e.printStackTrace();
			throw e;
		}
	}*/
	
	/**
	 * 插入数据
	 * @param models
	 * @throws Exception 
	 */
	public static void insertDataS(Prop prop, List<FloorModel> models) throws Exception {
		List<Floor> floors = new ArrayList<Floor>();
		try {
			for(Iterator<FloorModel> it = models.iterator(); it.hasNext();) {
				FloorModel model = it.next();
//				String sql = "INSERT INTO floor "
//						+ "(floor_id,floor,building_id,building_name,park_id,park_name,remark,is_del,ctime,utime) VALUES "
//						+ "(" +
//						floor.getId() + ", '"+ floor.getFloor() +"', '" + floor.getBuildingId() + "', '" + floor.getBuildingName() + "', '" +  
//						floor.getParkId() + "', '" + floor.getParkName() + "', '" + floor.getRemark() +"', " + 
//						floor.getIsDel() + ",'" + floor.getCtime() + "','"+ floor.getUtime() +"')";
//				sqlList.add(sql);
				Floor floor  = new Floor();
				floor.setFloorId(model.getId());
				floor.setFloor(model.getFloor());
				floor.setBuildingId(model.getBuildingId());
				floor.setBuildingName(model.getBuildingName());
				floor.setParkId(model.getParkId());
				floor.setParkName(model.getParkName());
				floor.setRemark(model.getRemark());
				floor.setIsDel(model.getIsDel());
				floor.setCtime(model.getCtime());
				floor.setUtime(model.getUtime());
				
				floors.add(floor);
			}
//			DbHelper.getDb().batch(sqlList, sqlList.size());
			DbHelper.getDb(prop).batchSave(floors, floors.size());
		} catch (Exception e) {
			logger.error("floor 批量插入失败！",e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 修改数据
	 * @param models
	 * @throws Exception 
	 */
	public static void updateDataS(Prop prop,List<FloorModel> models) throws Exception {
		List<Floor> floors = new ArrayList<Floor>();
		try {
			for(Iterator<FloorModel> it = models.iterator(); it.hasNext();) {
				FloorModel model = it.next();
				
				Floor floor  = new Floor();
				floor.setFloorId(model.getId());
				floor.setFloor(model.getFloor());
				floor.setBuildingId(model.getBuildingId());
				floor.setBuildingName(model.getBuildingName());
				floor.setParkId(model.getParkId());
				floor.setParkName(model.getParkName());
				floor.setRemark(model.getRemark());
				floor.setIsDel(model.getIsDel());
				floor.setCtime(model.getCtime());
				floor.setUtime(model.getUtime());
				
				floors.add(floor);
			}
			
			DbHelper.getDb(prop).batchUpdate(floors, floors.size());
		} catch (Exception e) {
			logger.error(" : floor 批量更新失败！",e);
			e.printStackTrace();
			throw e;
		}
	}
}
