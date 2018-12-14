package com.topwave.service;

import com.jfinal.kit.Prop;
import com.jfinal.plugin.activerecord.Record;
import com.topwave.bean.ContractContactModel;
import com.topwave.bean.ContractLeaseTermModel;
import com.topwave.bean.ContractModel;
import com.topwave.bean.ContractRoomModel;
import com.topwave.model.Contract;
import com.topwave.model.ContractContact;
import com.topwave.model.ContractLeaseTerm;
import com.topwave.model.ContractRoom;
import com.topwave.synch.FloorSynch;
import com.topwave.utils.DbHelper;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ContractService {
	static Logger logger = Logger.getLogger(ContractService.class);

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	public static Date getLastUpdateDate(Prop prop) {
		String sql = "SELECT * from contract ORDER BY utime DESC LIMIT 1";
		Record rcd = DbHelper.getDb(prop).findFirst(sql);
		if (rcd != null) {
			return rcd.getDate("utime");
		} else {
			return null;
		}
	}

	/**
	 * 插入数据
	 * 
	 * @param parks
	 * @return
	 */
	/*
	 * public static void insertDataS(List<ContractModel> contracts) throws
	 * Exception { List<String> sqlList = new ArrayList<String>(); StringBuilder
	 * sqlForDel = new StringBuilder("(");
	 * 
	 * try { for (Iterator<ContractModel> it = contracts.iterator(); it.hasNext();)
	 * { ContractModel contract = it.next();
	 * 
	 * sqlForDel.append(contract.getId() + ",");
	 * 
	 * String leaseBeginDate = null; if (contract.getLeaseBeginDate() != null) {
	 * leaseBeginDate = "'" + contract.getLeaseBeginDate() + "'"; } String
	 * leaseEndDate = null; if (contract.getLeaseEndDate() != null) { leaseEndDate =
	 * "'" + contract.getLeaseEndDate() + "'"; }
	 * 
	 * String signDate = null; if (contract.getSignDate() != null) { signDate = "'"
	 * + contract.getSignDate() + "'"; }
	 * 
	 * String sql = "INSERT INTO contract " +
	 * "(contract_id,contract_no,enterprise_id,enterprise_name,contract_state,building_id,building_name,"
	 * + "building_type,deposit,depositUnitEnum,lease_begin_date,lease_end_date," +
	 * "sign_date,is_del,ctime,utime) VALUES " + "(" + contract.getId() + ", '" +
	 * contract.getContractNo() + "', '" + contract.getEnterpriseId() + "', '" +
	 * contract.getEnterpriseName() + "', '" + contract.getContractState() + "', '"
	 * + contract.getBuildingId() + "', '" + contract.getBuildingName() + "', '" +
	 * contract.getBuildingType() + "', '" + contract.getDeposit() + "', '" +
	 * contract.getDepositUnitEnum() + "', " + leaseBeginDate + ", " + leaseEndDate
	 * + "," + signDate + ",'" + contract.getIsDel() + "', '" + contract.getCtime()
	 * + "','" + contract.getUtime() + "');"; sqlList.add(sql); } int lastIndx =
	 * sqlForDel.lastIndexOf(","); int length = sqlForDel.length(); if (lastIndx >
	 * 0) { // sqlForDel.delete(lastIndx, length - 1);
	 * sqlForDel.deleteCharAt(lastIndx); } sqlForDel.append(")"); String delRoomSql
	 * = "delete from contract_room where contract_id in " + sqlForDel.toString();
	 * String delContactSql = "delete from contract_contact where contract_id in " +
	 * sqlForDel.toString(); String delLeaseTermSql =
	 * "delete from contract_lease_term where contract_id in " +
	 * sqlForDel.toString();
	 * 
	 * DbHelper.getDb().batch(sqlList, sqlList.size());//
	 * DbHelper.getDb().update(delRoomSql); DbHelper.getDb().update(delContactSql);
	 * DbHelper.getDb().update(delLeaseTermSql); } catch (Exception e) {
	 * logger.error(new SimpleDateFormat(timeFormat).format(new Date()) +
	 * " :contract 批量插入失败！"); e.printStackTrace(); throw e; } }
	 * 
	 * 
	 *//**
		 * 修改数据
		 * 
		 * @param parks
		 * @return
		 *//*
			 * public static void updateDataS(List<ContractModel> contracts) throws
			 * Exception { List<String> sqlList = new ArrayList<String>(); StringBuilder
			 * sqlForDel = new StringBuilder("("); try { for (Iterator<ContractModel> it =
			 * contracts.iterator(); it.hasNext();) { ContractModel contract = it.next();
			 * 
			 * sqlForDel.append(contract.getId() + ",");
			 * 
			 * String leaseBeginDate = null; if (contract.getLeaseBeginDate() != null) {
			 * leaseBeginDate = "'" + contract.getLeaseBeginDate() + "'"; } String
			 * leaseEndDate = null; if (contract.getLeaseEndDate() != null) { leaseEndDate =
			 * "'" + contract.getLeaseEndDate() + "'"; }
			 * 
			 * String signDate = null; if (contract.getSignDate() != null) { signDate = "'"
			 * + contract.getSignDate() + "'"; }
			 * 
			 * 
			 * String sql = "update contract set contract_no = '" + contract.getContractNo()
			 * + "' ,enterprise_id='" + contract.getEnterpriseId() + "', enterprise_name='"
			 * + contract.getEnterpriseName() + "', contract_state='" +
			 * contract.getContractState() + "', building_id='" + contract.getBuildingId() +
			 * "', building_name='" + contract.getBuildingName() + "', building_type='" +
			 * contract.getBuildingType() + "', deposit='" + contract.getDeposit() +
			 * "', depositUnitEnum='" + contract.getDepositUnitEnum() +
			 * "', lease_begin_date=" + leaseBeginDate + ", lease_end_date=" + leaseEndDate
			 * + ",sign_date=" + signDate + ",is_del = '" + contract.getIsDel() +
			 * "' ,ctime = '" + contract.getCtime() + "', utime= '" + contract.getUtime() +
			 * "' where contract_id = " + contract.getId() + ";"; sqlList.add(sql); } int
			 * lastIndx = sqlForDel.lastIndexOf(","); int length = sqlForDel.length(); if
			 * (lastIndx > 0) { // sqlForDel.delete(lastIndx, length - 1);
			 * sqlForDel.deleteCharAt(lastIndx); } sqlForDel.append(")"); String delRoomSql
			 * = "delete from contract_room where contract_id in " + sqlForDel.toString();
			 * String delContactSql = "delete from contract_contact where contract_id in " +
			 * sqlForDel.toString(); String delLeaseTermSql =
			 * "delete from contract_lease_term where contract_id in " +
			 * sqlForDel.toString();
			 * 
			 * DbHelper.getDb().batch(sqlList, sqlList.size());//
			 * DbHelper.getDb().update(delRoomSql); DbHelper.getDb().update(delContactSql);
			 * DbHelper.getDb().update(delLeaseTermSql); } catch (Exception e) {
			 * logger.error(new SimpleDateFormat(timeFormat).format(new Date()) +
			 * " : contract 批量更新失败！"); e.printStackTrace(); throw e; } }
			 */

	/**
	 * 插入数据
	 * 
	 * @param parks
	 * @return
	 */
	public static void insertDataS(Prop prop,List<ContractModel> models) throws Exception {
		List<Contract> contractS = new ArrayList<Contract>();
		StringBuilder sqlForDel = new StringBuilder("(");

		try {
			for (Iterator<ContractModel> it = models.iterator(); it.hasNext();) {
				ContractModel model = it.next();

				sqlForDel.append(model.getId() + ",");

				Contract contract = new Contract();
				contract.setContractId(model.getId());
				contract.setContractNo(model.getContractNo());
				contract.setEnterpriseId(model.getEnterpriseId());
				contract.setEnterpriseName(model.getEnterpriseName());
				contract.setContractState(model.getContractState());
				contract.setBuildingId(model.getBuildingId());
				contract.setBuildingName(model.getBuildingName());

				contract.setBuildingType(model.getBuildingType());
				contract.setDeposit(model.getDeposit());
				contract.setDepositUnitEnum(model.getDepositUnitEnum());
				contract.setLeaseBeginDate(model.getLeaseBeginDate());
				contract.setLeaseEndDate(model.getLeaseEndDate());

				contract.setSignDate(model.getSignDate());
				contract.setIsDel(model.getIsDel());
				contract.setCtime(model.getCtime());
				contract.setUtime(model.getUtime());

				contractS.add(contract);

			}
			int lastIndx = sqlForDel.lastIndexOf(",");
			int length = sqlForDel.length();
			if (lastIndx > 0) {
//				sqlForDel.delete(lastIndx, length - 1);
				sqlForDel.deleteCharAt(lastIndx);
			}
			sqlForDel.append(")");
			String delRoomSql = "delete from contract_room where contract_id in " + sqlForDel.toString();
			String delContactSql = "delete from contract_contact where contract_id in " + sqlForDel.toString();
			String delLeaseTermSql = "delete from contract_lease_term where contract_id in " + sqlForDel.toString();

			DbHelper.getDb(prop).batchSave(contractS, contractS.size());//
			DbHelper.getDb(prop).update(delRoomSql);
			DbHelper.getDb(prop).update(delContactSql);
			DbHelper.getDb(prop).update(delLeaseTermSql);
		} catch (Exception e) {
			logger.error("contract 批量插入失败！",e);
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 修改数据
	 * 
	 * @param parks
	 * @return
	 */
	public static void updateDataS(Prop prop,List<ContractModel> models) throws Exception {
		List<Contract> contractS = new ArrayList<Contract>();
		StringBuilder sqlForDel = new StringBuilder("(");
		try {
			for (Iterator<ContractModel> it = models.iterator(); it.hasNext();) {
				ContractModel model = it.next();

				sqlForDel.append(model.getId() + ",");

				Contract contract = new Contract();
				contract.setContractId(model.getId());
				contract.setContractNo(model.getContractNo());
				contract.setEnterpriseId(model.getEnterpriseId());
				contract.setEnterpriseName(model.getEnterpriseName());
				contract.setContractState(model.getContractState());
				contract.setBuildingId(model.getBuildingId());
				contract.setBuildingName(model.getBuildingName());

				contract.setBuildingType(model.getBuildingType());
				contract.setDeposit(model.getDeposit());
				contract.setDepositUnitEnum(model.getDepositUnitEnum());
				contract.setLeaseBeginDate(model.getLeaseBeginDate());
				contract.setLeaseEndDate(model.getLeaseEndDate());

				contract.setSignDate(model.getSignDate());
				contract.setIsDel(model.getIsDel());
				contract.setCtime(model.getCtime());
				contract.setUtime(model.getUtime());

				contractS.add(contract);

			}
			int lastIndx = sqlForDel.lastIndexOf(",");
			int length = sqlForDel.length();
			if (lastIndx > 0) {
//				sqlForDel.delete(lastIndx, length - 1);
				sqlForDel.deleteCharAt(lastIndx);
			}
			sqlForDel.append(")");
			String delRoomSql = "delete from contract_room where contract_id in " + sqlForDel.toString();
			String delContactSql = "delete from contract_contact where contract_id in " + sqlForDel.toString();
			String delLeaseTermSql = "delete from contract_lease_term where contract_id in " + sqlForDel.toString();

			DbHelper.getDb(prop).batchUpdate(contractS, contractS.size());//
			DbHelper.getDb(prop).update(delRoomSql);
			DbHelper.getDb(prop).update(delContactSql);
			DbHelper.getDb(prop).update(delLeaseTermSql);
		} catch (Exception e) {
			logger.error("contract 批量更新失败！",e);
			e.printStackTrace();
			throw e;
		}
	}

	public static void insertRoomDataS(Prop prop, List<ContractRoomModel> models) throws Exception {
		List<ContractRoom> roomS = new ArrayList<ContractRoom>();

		try {
			for (Iterator<ContractRoomModel> it = models.iterator(); it.hasNext();) {
				ContractRoomModel model = it.next();
				ContractRoom contractRoom = new ContractRoom();
				contractRoom.setId(model.getId());
				contractRoom.setRoomId(model.getRoomId());
				contractRoom.setRoomNumber(model.getRoomNumber());
				contractRoom.setFloor(model.getFloor());
				contractRoom.setPrice(model.getPrice());
				contractRoom.setAreaSize(model.getAreaSize());
				contractRoom.setContractId(model.getContractId());

				roomS.add(contractRoom);
			}
			DbHelper.getDb(prop).batchSave(roomS, roomS.size());//
		} catch (Exception e) {
			logger.error("contract_room 批量添加失败！",e);
			e.printStackTrace();
			throw e;
		}
	}

	public static void insertContactDataS(Prop prop,List<ContractContactModel> models) throws Exception {
		List<ContractContact> contactS = new ArrayList<ContractContact>();

		try {
			for (Iterator<ContractContactModel> it = models.iterator(); it.hasNext();) {
				ContractContactModel model = it.next();
				ContractContact contact = new ContractContact();
//				String sql = "INSERT INTO contract_contact "
//						+ "(contact_id,address,company_name,email,name,tel,contract_id) VALUES " + "(" + contact.getId()
//						+ ", '" + contact.getAddress() + "', '" + contact.getCompanyName() + "', '" + contact.getEmail()
//						+ "', '" + contact.getName() + "', '" + contact.getTel() + "', '" + contact.getContractId()
//						+ "');";
//				sqlList.add(sql);
				contact.setContactId(model.getId());
				contact.setAddress(model.getAddress());
				contact.setCompanyName(model.getCompanyName());
				contact.setEmail(model.getEmail());
				contact.setName(model.getName());
				contact.setTel(model.getTel());
				contact.setContractId(model.getContractId());
				contactS.add(contact);
			}
			DbHelper.getDb(prop).batchSave(contactS, contactS.size());//
		} catch (Exception e) {
			logger.error("contract_contact 批量添加失败！",e);
			e.printStackTrace();
			throw e;
		}
	}

	public static void insertLeaseTermDataS(Prop prop,List<ContractLeaseTermModel> models) throws Exception {
		List<ContractLeaseTerm> termS = new ArrayList<ContractLeaseTerm>();

		try {
			for (Iterator<ContractLeaseTermModel> it = models.iterator(); it.hasNext();) {
				ContractLeaseTermModel model = it.next();
				ContractLeaseTerm term = new ContractLeaseTerm();
				term.setLeaseId(model.getId());
				term.setTermBeginDate(model.getTermBeginDate());
				term.setTermEndDate(model.getTermEndDate());
				term.setContractPayEnum(model.getContractPayEnum());
				term.setDayNumberForYear(model.getDayNumberForYear());
				term.setCalculateEnum(model.getCalculateEnum());
				term.setMonthPriceConvertRoleEnum(model.getMonthPriceConvertRoleEnum());
				term.setLeaseDivideRoleEnum(model.getLeaseDivideRoleEnum());
				term.setIntervalMonth(model.getIntervalMonth());
				term.setPayInAdvanceday(model.getPayInAdvanceDay());
				term.setPaymentDateEnum(model.getPaymentDateEnum());
				term.setPrice(model.getPrice());
				term.setPriceUnitEnum(model.getPriceUnitEnum());
				term.setMonetaryUnit(model.getMonetaryUnit());
				term.setContractId(model.getContractId());

				termS.add(term);
			}
			DbHelper.getDb(prop).batchSave(termS, termS.size());//
		} catch (Exception e) {
			logger.error("contract_lease_term 批量添加失败！",e);
			e.printStackTrace();
			throw e;
		}
	}
}
