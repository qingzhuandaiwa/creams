package com.topwave.service;

import com.jfinal.kit.Prop;
import com.jfinal.plugin.activerecord.Record;
import com.topwave.bean.RoomModel;
import com.topwave.model.Room;
import com.topwave.utils.DbHelper;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RoomService {
	
	private static Logger logger = Logger.getLogger(RoomService.class);
	
	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static Date getLastUpdateDate(Prop prop) {
//		Park lastData = Park.dao.findFirst("SELECT * from park ORDER BY utime DESC LIMIT 1");
//		Date date = lastData.getUtime();
//		return date;
		String sql = "SELECT * from room ORDER BY utime DESC LIMIT 1";
		Record rcd = DbHelper.getDb(prop).findFirst(sql);
		if(rcd!=null) {
			return rcd.getDate("utime");
		}else {
			return null;
		}
	}
	
	/**
	 * 插入数据
	 * @param parks
	 * @throws Exception 
	 *//*
	public static void insertDataS(List<RoomModel> rooms) throws Exception {
		List<String> sqlList = new ArrayList<String>();

		try {
			for(Iterator<RoomModel> it = rooms.iterator(); it.hasNext();) {
				RoomModel room = it.next();
				String sql = "INSERT INTO room "
						+ "(room_id,room_number,floor_id,floor,building_id,building_name,park_id,park_name,fitment,image_url,remark,is_del,ctime,utime) VALUES "
						+ "(" +
						room.getId() + ", '"+ room.getRoomNumber() +"', '" + 
						room.getFloorId() + "', '" + room.getFloor() + "', '" + 
						room.getBuildingId() + "', '" + room.getBuildingName() +  "', '" + room.getParkId() + "', '" + 
						room.getParkName() + "', '"  + room.getFitment() + "', '"  + room.getImageUrl() + "', '" + room.getRemark() +"', " + 
						room.getIsDel() + ",'" + room.getCtime() + "','"+ room.getUtime() +"');";
				sqlList.add(sql);
			}
			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :room 批量插入失败！");
			e.printStackTrace();
			throw e;
		}
	}
	
	*//**
	 * 修改数据
	 * @param parks
	 * @throws Exception 
	 *//*
	public static void updateDataS(List<RoomModel> rooms) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		try {
			for(Iterator<RoomModel> it = rooms.iterator(); it.hasNext();) {
				RoomModel room = it.next();
				
				String sql = "update room set room_number = '" + room.getRoomNumber() + "', floor_id='" + room.getFloorId() + "', floor='" + 
				room.getFloor() + "' , building_id='" + room.getBuildingId() + "', building_name='" + room.getBuildingName() + "', park_id='" + 
				room.getParkId() +  "', park_name='" + room.getParkName() + "', fitment='" + room.getFitment() + "', image_url='" + room.getImageUrl() + "', remark='" + 
				room.getRemark() +"',is_del = '"+ room.getIsDel()+"' ,ctime = '"+ room.getCtime() +"', utime= '"+ 
				room.getUtime() +"' where room_id = " + room.getId() + ";";
				sqlList.add(sql);
			}
			
			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " : building 批量更新失败！");
			e.printStackTrace();
			throw e;
		}
	}*/
	
	/**
	 * 插入数据
	 * @param parks
	 * @throws Exception 
	 */
	public static void insertDataS(Prop prop,List<RoomModel> models) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		List<Room> rooms = new ArrayList<Room>();
		
		try {
			for(Iterator<RoomModel> it = models.iterator(); it.hasNext();) {
				RoomModel model = it.next();
				
				Room room = new Room();
				room.setRoomId(model.getId());
				room.setRoomNumber(model.getRoomNumber());
				room.setFloorId(model.getFloorId());
				room.setFloor(model.getFloor());
				room.setBuildingId(model.getBuildingId());
				room.setBuildingName(model.getBuildingName());
				room.setParkId(model.getParkId());
				room.setParkName(model.getParkName());
				room.setFitment(model.getFitment());
				room.setImageUrl(model.getImageUrl());
				room.setRemark(model.getRemark());
				room.setIsDel(model.getIsDel());
				room.setCtime(model.getCtime());
				room.setUtime(model.getUtime());
				
				rooms.add(room);
			}
			DbHelper.getDb(prop).batchSave(rooms, rooms.size());
		} catch (Exception e) {
			logger.error("room 批量插入失败！",e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 修改数据
	 * @param parks
	 * @throws Exception 
	 */
	public static void updateDataS(Prop prop,List<RoomModel> models) throws Exception {
		List<Room> rooms = new ArrayList<Room>();
		try {
			for(Iterator<RoomModel> it = models.iterator(); it.hasNext();) {
				RoomModel model = it.next();
				
				Room room = new Room();
				room.setRoomId(model.getId());
				room.setRoomNumber(model.getRoomNumber());
				room.setFloorId(model.getFloorId());
				room.setFloor(model.getFloor());
				room.setBuildingId(model.getBuildingId());
				room.setBuildingName(model.getBuildingName());
				room.setParkId(model.getParkId());
				room.setParkName(model.getParkName());
				room.setFitment(model.getFitment());
				room.setImageUrl(model.getImageUrl());
				room.setRemark(model.getRemark());
				room.setIsDel(model.getIsDel());
				room.setCtime(model.getCtime());
				room.setUtime(model.getUtime());
				
				rooms.add(room);
			}
			
			DbHelper.getDb(prop).batchUpdate(rooms, rooms.size());
		} catch (Exception e) {
			logger.error("room 批量更新失败！",e);
			e.printStackTrace();
			throw e;
		}
	}
}
