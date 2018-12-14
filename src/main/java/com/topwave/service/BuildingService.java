package com.topwave.service;

import com.jfinal.kit.Prop;
import com.jfinal.plugin.activerecord.Record;
import com.topwave.utils.DbHelper;

import java.util.Date;

public class BuildingService {

	
	public static Date getLastUpdateDate(Prop prop) {
		String sql = "SELECT * from building ORDER BY utime DESC LIMIT 1";
		Record rcd = DbHelper.getDb(prop).findFirst(sql);
		if(rcd!=null) {
			return rcd.getDate("utime");
		}else {
			return null;
		}
	}
}
