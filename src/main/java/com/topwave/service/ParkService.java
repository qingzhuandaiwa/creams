package com.topwave.service;

import com.jfinal.kit.Prop;
import com.jfinal.plugin.activerecord.Record;
import com.topwave.bean.ParkModel;
import com.topwave.utils.DbHelper;

import java.util.Date;

public class ParkService {
//	public static boolean savePark() {
//		String sql = "select * from park";
//		List<Object> paramsList = new ArrayList<Object>();
//		List<Park> parks = Park.dao.find(sql);
//		
//		for (Iterator<Park> iter= parks.iterator(); iter.hasNext();) {
//			Park rd = iter.next();
//			System.out.println(rd.getParkName());
//		}
//		return false;
//		
//	}
	
	public static Date getLastUpdateDate(Prop prop) {
//		Park lastData = Park.dao.findFirst("SELECT * from park ORDER BY utime DESC LIMIT 1");
//		Date date = lastData.getUtime();
//		return date;
		String sql = "SELECT * from park ORDER BY utime DESC LIMIT 1";
		Record rcd = DbHelper.getDb(prop).findFirst(sql);
		if(rcd!=null) {
			return rcd.getDate("utime");
		}else {
			return null;
		}
	}
	
	
	public static void save(Prop prop,ParkModel park) throws Exception{
		System.out.println(park);
		String sql = "insert into park (park_id,park_name,is_del,ctime,utime) VALUES(123,'name1',0,NOW(),NOW())";
		DbHelper.getDb(prop).update(sql);
	}
	
	public static void update(Prop prop,ParkModel park) throws Exception{
		System.out.println(park);
		String sql = "insert into park (park_id,park_name,is_del,ctime,utime) VALUES(1234,'name2',0,'11',NOW())";
		DbHelper.getDb(prop).update(sql);
		
	}
	
	
	
	
}
