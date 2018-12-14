package com.topwave.service;

import com.jfinal.kit.Prop;
import com.jfinal.plugin.activerecord.Record;
import com.topwave.bean.BillCashFlowModel;
import com.topwave.bean.BillCashMatchesModel;
import com.topwave.bean.BillModel;
import com.topwave.bean.BillRoomModel;
import com.topwave.model.Bill;
import com.topwave.model.BillCashFlow;
import com.topwave.model.BillCashMatches;
import com.topwave.model.BillRoom;
import com.topwave.synch.FloorSynch;
import com.topwave.utils.DbHelper;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class BillService {

	static Logger logger = Logger.getLogger(BillService.class);

	private static String timeFormat = "yyyy-MM-dd HH:mm:ss";

	public static Date getLastUpdateDate(Prop prop) {
		String sql = "SELECT * from bill ORDER BY utime DESC LIMIT 1";
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
	 * public static void insertDataS(List<BillModel> bills) throws Exception {
	 * List<String> sqlList = new ArrayList<String>(); StringBuilder
	 * billCashMatchDelSql = new StringBuilder("("); StringBuilder billRoomDelSql =
	 * new StringBuilder("("); StringBuilder cashFlowDelSql = new
	 * StringBuilder("(");
	 * 
	 * try { for (Iterator<BillModel> it = bills.iterator(); it.hasNext();) {
	 * BillModel bill = it.next();
	 * 
	 * billCashMatchDelSql.append(bill.getId() + ",");
	 * 
	 * //拼接需要删除的room id billRoomDelSql.append(bill.getId() + ",");
	 * 
	 * List<BillCashMatchesModel> cashMatches = bill.getBillCashMatches();
	 * if(cashMatches != null && cashMatches.size() > 0) {
	 * for(Iterator<BillCashMatchesModel> cashMatchIt = cashMatches.iterator();
	 * cashMatchIt.hasNext();) { BillCashMatchesModel billCashMatch =
	 * cashMatchIt.next(); Integer billCashMatchid = billCashMatch.getId();
	 * cashFlowDelSql.append(billCashMatchid + ","); } }
	 * 
	 * 
	 * String createdDate = null; if (bill.getCreatedDate()!= null) { createdDate =
	 * "'" + bill.getCreatedDate() + "'"; } String endDate = null; if
	 * (bill.getEndDate() != null) { endDate = "'" + bill.getEndDate() + "'"; }
	 * 
	 * String payDate = null; if (bill.getPayDate() != null) { payDate = "'" +
	 * bill.getPayDate() + "'"; }
	 * 
	 * String settledDate = null; if (bill.getSettledDate() != null) { settledDate =
	 * "'" + bill.getSettledDate() + "'"; }
	 * 
	 * String startDate = null; if (bill.getStartDate() != null) { startDate = "'" +
	 * bill.getStartDate() + "'"; }
	 * 
	 * String sql = "INSERT INTO bill " +
	 * "(bill_id,bill_no,action,adjusted_prime_amount,actual_amount,rate,over_due_fine_theory_amount,"
	 * + "bill_type,building_id,building_name,closed_status,closed_status_name," +
	 * "created_date,date_scope,due_status_name,end_date,expired_day,generated_reminder_count,"
	 * +
	 * "handler,invoice_amount,invoice_status,invoice_status_name,issue_receipt_count,match_id,matched_amount,monetary_unit,"
	 * +
	 * "object_id,object_type,order_no,other,over_due_fine_status,overDueFineStatusName,pay_date,"
	 * +
	 * "payed_amount,prime_amount,receipt_amount,remaining_amount,room_number,settle_status,settle_status_name,"
	 * +
	 * "settledDate,start_date,tenant_id,term_id,termination_income_adjust,theory_amount,"
	 * +
	 * "transfer_amount,transfer_to_other_bill_amount,type_nme,is_del,ctime,utime) VALUES "
	 * + "(" + bill.getId() + ", '" + bill.getBillNo() + "', '" + bill.getAction() +
	 * "', " + bill.getAdjustedPrimeAmount() + ", " + bill.getActualAmount() + "," +
	 * bill.getRate() + "," + bill.getOverDueFineTheoryAmount() + ",'" +
	 * bill.getBillType() + "', '" + bill.getBuildingId() + "', '" +
	 * bill.getBuildingName() + "', '" + bill.getClosedStatus() + "','" +
	 * bill.getClosedStatusName() + "'," + createdDate + ",'" + bill.getDateScope()
	 * + "','" + bill.getDueStatusName() + "', " + endDate + ", " +
	 * bill.getExpiredDay() + ", " + bill.getGeneratedReminderCount() + ", '" +
	 * bill.getHandler() + "', '" + bill.getInvoiceAmount() + "', '" +
	 * bill.getInvoiceStatus() + "', '" + bill.getInvoiceStatusName() + "', " +
	 * bill.getIssueReceiptCount() + ", '" + bill.getMatchId() + "', " +
	 * bill.getMatchedAmount() + ", '" + bill.getMonetaryUnit() + "', '" +
	 * bill.getObjectId() + "', '" + bill.getObjectType() + "', '" +
	 * bill.getOrderNo() + "', '" + bill.getOther() + "', '" +
	 * bill.getOverDueFineStatus() + "', '" + bill.getOverDueFineStatusName() +
	 * "', " + payDate + ", " + bill.getPayedAmount() + ", " + bill.getPrimeAmount()
	 * + ", " + bill.getReceiptAmount() + ", " + bill.getRemainingAmount() + ", '" +
	 * bill.getRoomNumber() + "', '" + bill.getSettleStatus() + "', '" +
	 * bill.getSettleStatusName() + "', " + settledDate + ", " + startDate + ", '" +
	 * bill.getTenantId() + "', '" + bill.getTermId() + "', " +
	 * bill.getTerminationIncomeAdjust() + ", " + bill.getTheoryAmount() + ", " +
	 * bill.getTransferAmount() + ", " + bill.getTransferToOtherBillAmount() + ", '"
	 * + bill.getTypeName() + "', '" + bill.getIsDel() + "', '" + bill.getCtime() +
	 * "', '" + bill.getUtime() + "');"; sqlList.add(sql); }
	 * 
	 * int lastIndx0 = billCashMatchDelSql.lastIndexOf(","); // int length =
	 * billCashMatchDelSql.length(); if (lastIndx0 > 0) {
	 * billCashMatchDelSql.deleteCharAt(lastIndx0); }
	 * billCashMatchDelSql.append(")");
	 * 
	 * int lastIndx1 = billRoomDelSql.lastIndexOf(","); // int length =
	 * billRoomDelSql.length(); if (lastIndx1 > 0) {
	 * billRoomDelSql.deleteCharAt(lastIndx1); } billRoomDelSql.append(")");
	 * 
	 * int lastIndx2 = cashFlowDelSql.lastIndexOf(","); if (lastIndx2 > 0) {
	 * cashFlowDelSql.deleteCharAt(lastIndx2); cashFlowDelSql.append(")"); String
	 * delcashFlowSql = "delete from bill_cash_flow where bill_cash_match_id in " +
	 * cashFlowDelSql.toString(); DbHelper.getDb().update(delcashFlowSql); }
	 * 
	 * 
	 * String delBillCatchMatchesSql =
	 * "delete from bill_cash_matches where bill_id in " +
	 * billCashMatchDelSql.toString(); String delBillRoomSql =
	 * "delete from bill_room where bill_id in " + billRoomDelSql.toString();
	 * 
	 * DbHelper.getDb().batch(sqlList, sqlList.size());//
	 * DbHelper.getDb().update(delBillCatchMatchesSql);
	 * DbHelper.getDb().update(delBillRoomSql); } catch (Exception e) {
	 * logger.error(new SimpleDateFormat(timeFormat).format(new Date()) +
	 * " :bill 批量插入失败！"); e.printStackTrace(); throw e; }
	 * 
	 * }
	 * 
	 * 
	 *//**
		 * 修改数据
		 * 
		 * @param parks
		 * @return
		 *//*
			 * public static void updateDataS(List<BillModel> bills) throws Exception {
			 * List<String> sqlList = new ArrayList<String>(); StringBuilder
			 * billCashMatchDelSql = new StringBuilder("("); StringBuilder billRoomDelSql =
			 * new StringBuilder("("); StringBuilder cashFlowDelSql = new
			 * StringBuilder("(");
			 * 
			 * try { for (Iterator<BillModel> it = bills.iterator(); it.hasNext();) {
			 * BillModel bill = it.next();
			 * 
			 * billCashMatchDelSql.append(bill.getId() + ",");
			 * 
			 * //拼接需要删除的room id billRoomDelSql.append(bill.getId() + ",");
			 * 
			 * List<BillCashMatchesModel> cashMatches = bill.getBillCashMatches();
			 * if(cashMatches != null && cashMatches.size() > 0) {
			 * for(Iterator<BillCashMatchesModel> cashMatchIt = cashMatches.iterator();
			 * cashMatchIt.hasNext();) { BillCashMatchesModel billCashMatch =
			 * cashMatchIt.next(); Integer billCashMatchid = billCashMatch.getId();
			 * cashFlowDelSql.append(billCashMatchid + ","); } }
			 * 
			 * 
			 * String createdDate = null; if (bill.getCreatedDate()!= null) { createdDate =
			 * "'" + bill.getCreatedDate() + "'"; } String endDate = null; if
			 * (bill.getEndDate() != null) { endDate = "'" + bill.getEndDate() + "'"; }
			 * 
			 * String payDate = null; if (bill.getPayDate() != null) { payDate = "'" +
			 * bill.getPayDate() + "'"; }
			 * 
			 * String settledDate = null; if (bill.getSettledDate() != null) { settledDate =
			 * "'" + bill.getSettledDate() + "'"; }
			 * 
			 * String startDate = null; if (bill.getStartDate() != null) { startDate = "'" +
			 * bill.getStartDate() + "'"; }
			 * 
			 * String sql = "update bill set bill_no='" + bill.getBillNo() + "', action='" +
			 * bill.getAction() + "',adjusted_prime_amount=" + bill.getAdjustedPrimeAmount()
			 * + ",actual_amount=" + bill.getActualAmount() + ",rate=" + bill.getRate() +
			 * ",over_due_fine_theory_amount=" + bill.getOverDueFineTheoryAmount() +
			 * ",bill_type='" + bill.getBillType() + "', building_id='" +
			 * bill.getBuildingId() + "', building_name='" + bill.getBuildingName() +
			 * "', closed_status='" + bill.getClosedStatus() + "', closed_status_name='" +
			 * bill.getClosedStatusName() + "', created_date=" + createdDate +
			 * ",date_scope='" + bill.getDateScope() + "',due_status_name='" +
			 * bill.getDueStatusName() + "', end_date=" + endDate + ", expired_day=" +
			 * bill.getExpiredDay() + ",generated_reminder_count=" +
			 * bill.getGeneratedReminderCount() + ",handler='" + bill.getHandler() +
			 * "',invoice_amount='" + bill.getInvoiceAmount() + "', invoice_status='" +
			 * bill.getInvoiceStatus() + "', invoice_status_name='" +
			 * bill.getInvoiceStatusName() + "', issue_receipt_count=" +
			 * bill.getIssueReceiptCount() + ",match_id='" + bill.getMatchId() +
			 * "', matched_amount=" + bill.getMatchedAmount() + ",monetary_unit='" +
			 * bill.getMonetaryUnit() + "', object_id='" + bill.getObjectId() +
			 * "', object_type='" + bill.getObjectType() + "', order_no='" +
			 * bill.getOrderNo() + "', other='" + bill.getOther() +
			 * "', over_due_fine_status='" + bill.getOverDueFineStatus() +
			 * "', overDueFineStatusName='" + "', pay_date=" + payDate + ",payed_amount=" +
			 * bill.getPayedAmount() + ",prime_amount=" + bill.getPrimeAmount() +
			 * ",receipt_amount=" + bill.getReceiptAmount() + ", remaining_amount=" +
			 * bill.getRemainingAmount() + ", room_number='" + bill.getRoomNumber() +
			 * "', settle_status='" + bill.getSettleStatus() + "', settle_status_name='" +
			 * bill.getSettleStatusName() + "', settledDate=" + settledDate +
			 * ", start_date=" + startDate + ",tenant_id='" + bill.getTenantId() +
			 * "', term_id='" + bill.getTermId() + "', termination_income_adjust=" +
			 * bill.getTerminationIncomeAdjust() + ", theory_amount=" +
			 * bill.getTheoryAmount() + ",transfer_amount=" + bill.getTransferAmount() +
			 * ",transfer_to_other_bill_amount=" + bill.getTransferToOtherBillAmount() +
			 * ",type_nme='" + bill.getTypeName() + "', is_del='" + bill.getIsDel() +
			 * "', ctime='" + bill.getCtime() + "',utime='" + bill.getUtime() + "';";
			 * 
			 * sqlList.add(sql);
			 * 
			 * }
			 * 
			 * int lastIndx0 = billCashMatchDelSql.lastIndexOf(","); // int length =
			 * billCashMatchDelSql.length(); if (lastIndx0 > 0) {
			 * billCashMatchDelSql.deleteCharAt(lastIndx0); }
			 * billCashMatchDelSql.append(")");
			 * 
			 * int lastIndx1 = billRoomDelSql.lastIndexOf(","); // int length =
			 * billRoomDelSql.length(); if (lastIndx1 > 0) {
			 * billRoomDelSql.deleteCharAt(lastIndx1); } billRoomDelSql.append(")");
			 * 
			 * int lastIndx2 = cashFlowDelSql.lastIndexOf(","); if (lastIndx2 > 0) {
			 * cashFlowDelSql.deleteCharAt(lastIndx2); cashFlowDelSql.append(")"); String
			 * delcashFlowSql = "delete from bill_cash_flow where bill_cash_match_id in " +
			 * cashFlowDelSql.toString(); DbHelper.getDb().update(delcashFlowSql); }
			 * 
			 * 
			 * String delBillCatchMatchesSql =
			 * "delete from bill_cash_matches where bill_id in " +
			 * billCashMatchDelSql.toString(); String delBillRoomSql =
			 * "delete from bill_room where bill_id in " + billRoomDelSql.toString();
			 * 
			 * DbHelper.getDb().batch(sqlList, sqlList.size());//
			 * DbHelper.getDb().update(delBillCatchMatchesSql);
			 * DbHelper.getDb().update(delBillRoomSql); } catch (Exception e) {
			 * logger.error(new SimpleDateFormat(timeFormat).format(new Date()) +
			 * " :bill 批量插入失败！"); e.printStackTrace(); throw e; }
			 * 
			 * }
			 */

	/**
	 * 插入数据
	 * 
	 * @param models
	 * @return
	 */
	public static void insertDataS(Prop prop,List<BillModel> models) throws Exception {
		List<Bill> bills = new ArrayList<Bill>();
		StringBuilder billCashMatchDelSql = new StringBuilder("(");
		StringBuilder billRoomDelSql = new StringBuilder("(");
		StringBuilder cashFlowDelSql = new StringBuilder("(");

		try {
			for (Iterator<BillModel> it = models.iterator(); it.hasNext();) {
				BillModel model = it.next();

				billCashMatchDelSql.append(model.getId() + ",");

				// 拼接需要删除的room id
				billRoomDelSql.append(model.getId() + ",");

				List<BillCashMatchesModel> cashMatches = model.getBillCashMatches();
				if (cashMatches != null && cashMatches.size() > 0) {
					for (Iterator<BillCashMatchesModel> cashMatchIt = cashMatches.iterator(); cashMatchIt.hasNext();) {
						BillCashMatchesModel billCashMatch = cashMatchIt.next();
						Integer billCashMatchid = billCashMatch.getId();
						cashFlowDelSql.append(billCashMatchid + ",");
					}
				}

				Bill bill = new Bill();
				bill.setBillId(model.getId());
				bill.setBillNo(model.getBillNo());
				bill.setAction(model.getAction());
				bill.setAdjustedPrimeAmount(model.getAdjustedPrimeAmount());
				bill.setActualAmount(model.getActualAmount());
				bill.setRate((BigDecimal) model.getRate());
				bill.setOverDueFineTheoryAmount((BigDecimal) model.getOverDueFineTheoryAmount());

				bill.setBillType(model.getBillType());
				bill.setBuildingId(model.getBuildingId());
				bill.setBuildingName(model.getBuildingName());
				bill.setClosedStatus(model.getClosedStatus());
				bill.setClosedStatusName(model.getClosedStatusName());

				bill.setCreatedDate(model.getCreatedDate());
				bill.setDateScope(model.getDateScope());
				bill.setDueStatusName(model.getDueStatusName());
				bill.setEndDate(model.getEndDate());
				bill.setExpiredDay(model.getExpiredDay());
				bill.setGeneratedReminderCount(model.getGeneratedReminderCount());

				bill.setHandler(model.getHandler());
				bill.setInvoiceAmount(model.getInvoiceAmount());
				bill.setInvoiceStatus(model.getInvoiceStatus());
				bill.setInvoiceStatusName(model.getInvoiceStatusName());
				bill.setIssueReceiptCount(model.getIssueReceiptCount());
				bill.setMatchId(model.getMatchId());
				bill.setMatchedAmount((BigDecimal) model.getMatchedAmount());
				bill.setMonetaryUnit(model.getMonetaryUnit());

				bill.setObjectId(model.getObjectId()==null?null:model.getObjectId().toString());
				bill.setObjectType(model.getObjectType());
				bill.setOrderNo(model.getOrderNo());
				bill.setOther(model.getOther());
				bill.setOverDueFineStatus(model.getOverDueFineStatus());
				bill.setOverDueFineStatusName(model.getOverDueFineStatusName());
				bill.setPayDate(model.getPayDate());

				bill.setPayedAmount((BigDecimal) model.getPayedAmount());
				bill.setPrimeAmount((BigDecimal) model.getPrimeAmount());
				bill.setReceiptAmount((BigDecimal) model.getReceiptAmount());
				bill.setRemainingAmount((BigDecimal) model.getRemainingAmount());
				bill.setRoomNumber(model.getRoomNumber());
				bill.setSettleStatus(model.getSettleStatus());
				bill.setSettleStatusName(model.getSettleStatusName());

				bill.setSettledDate(model.getSettledDate());
				bill.setStartDate(model.getStartDate());
				bill.setTenantId(model.getTenantId()==null?null:model.getTenantId().toString());
				bill.setTermId(model.getTermId()==null?null:model.getTermId().toString());
				bill.setTerminationIncomeAdjust((BigDecimal) model.getTerminationIncomeAdjust());
				bill.setTheoryAmount((BigDecimal) model.getTheoryAmount());

				bill.setTransferAmount((BigDecimal) model.getTransferAmount());
				bill.setTransferToOtherBillAmount((BigDecimal) model.getTransferToOtherBillAmount());
				bill.setTypeNme(model.getTypeName());
				bill.setIsDel(model.getIsDel());
				bill.setCtime(model.getCtime());
				bill.setUtime(model.getUtime());

				bills.add(bill);
			}

			int lastIndx0 = billCashMatchDelSql.lastIndexOf(",");
//			int length = billCashMatchDelSql.length();
			if (lastIndx0 > 0) {
				billCashMatchDelSql.deleteCharAt(lastIndx0);
			}
			billCashMatchDelSql.append(")");

			int lastIndx1 = billRoomDelSql.lastIndexOf(",");
//			int length = billRoomDelSql.length();
			if (lastIndx1 > 0) {
				billRoomDelSql.deleteCharAt(lastIndx1);
			}
			billRoomDelSql.append(")");

			int lastIndx2 = cashFlowDelSql.lastIndexOf(",");
			if (lastIndx2 > 0) {
				cashFlowDelSql.deleteCharAt(lastIndx2);
				cashFlowDelSql.append(")");
				String delcashFlowSql = "delete from bill_cash_flow where bill_cash_match_id in "
						+ cashFlowDelSql.toString();
				DbHelper.getDb(prop).update(delcashFlowSql);
			}

			String delBillCatchMatchesSql = "delete from bill_cash_matches where bill_id in "
					+ billCashMatchDelSql.toString();
			String delBillRoomSql = "delete from bill_room where bill_id in " + billRoomDelSql.toString();

//			DbHelper.getDb().batch(sqlList, sqlList.size());//
			Integer size = bills.size();
			int pointsDataLimit = 1000;//限制条数
			if (size < pointsDataLimit){//没有必要分批次保存
				DbHelper.getDb(prop).batchSave(bills, bills.size());
			}else {//需要分批次保存
				int part = size/pointsDataLimit;//分批数
//				logger.info("billl 当前页共有 ： "+size+"条，！"+" 分为 ："+part+"批保存");
				for (int i = 0; i < part; i++) {
					System.out.println("bill 当前页正在执行第"+(i+1)+"批保存");
					List<Bill> elementS = bills.subList(0, pointsDataLimit);
					DbHelper.getDb(prop).batchSave(elementS, elementS.size());
					//剔除
					bills.subList(0, pointsDataLimit).clear();
				}

				if(!bills.isEmpty()){
					DbHelper.getDb(prop).batchSave(bills, bills.size());
//					System.out.println(bills);//保存最后剩下的数据
				}
			}
			DbHelper.getDb(prop).update(delBillCatchMatchesSql);
			DbHelper.getDb(prop).update(delBillRoomSql);
		} catch (Exception e) {
			logger.error("bill 批量插入失败！",e);
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 修改数据
	 * 
	 * @param models
	 * @return
	 */
	public static void updateDataS(Prop prop,List<BillModel> models) throws Exception {
		List<Bill> bills = new ArrayList<Bill>();
		StringBuilder billCashMatchDelSql = new StringBuilder("(");
		StringBuilder billRoomDelSql = new StringBuilder("(");
		StringBuilder cashFlowDelSql = new StringBuilder("(");

		try {
			for (Iterator<BillModel> it = models.iterator(); it.hasNext();) {
				BillModel model = it.next();

				billCashMatchDelSql.append(model.getId() + ",");

				// 拼接需要删除的room id
				billRoomDelSql.append(model.getId() + ",");

				List<BillCashMatchesModel> cashMatches = model.getBillCashMatches();
				if (cashMatches != null && cashMatches.size() > 0) {
					for (Iterator<BillCashMatchesModel> cashMatchIt = cashMatches.iterator(); cashMatchIt.hasNext();) {
						BillCashMatchesModel billCashMatch = cashMatchIt.next();
						Integer billCashMatchid = billCashMatch.getId();
						cashFlowDelSql.append(billCashMatchid + ",");
					}
				}


				Bill bill = new Bill();
				bill.setBillId(model.getId());
				bill.setBillNo(model.getBillNo());
				bill.setAction(model.getAction());
				bill.setAdjustedPrimeAmount(model.getAdjustedPrimeAmount());
				bill.setActualAmount(model.getActualAmount());
				bill.setRate((BigDecimal) model.getRate());
				bill.setOverDueFineTheoryAmount((BigDecimal) model.getOverDueFineTheoryAmount());

				bill.setBillType(model.getBillType());
				bill.setBuildingId(model.getBuildingId());
				bill.setBuildingName(model.getBuildingName());
				bill.setClosedStatus(model.getClosedStatus());
				bill.setClosedStatusName(model.getClosedStatusName());

				bill.setCreatedDate(model.getCreatedDate());
				bill.setDateScope(model.getDateScope());
				bill.setDueStatusName(model.getDueStatusName());
				bill.setEndDate(model.getEndDate());
				bill.setExpiredDay(model.getExpiredDay());
				bill.setGeneratedReminderCount(model.getGeneratedReminderCount());

				bill.setHandler(model.getHandler());
				bill.setInvoiceAmount(model.getInvoiceAmount());
				bill.setInvoiceStatus(model.getInvoiceStatus());
				bill.setInvoiceStatusName(model.getInvoiceStatusName());
				bill.setIssueReceiptCount(model.getIssueReceiptCount());
				bill.setMatchId(model.getMatchId());
				bill.setMatchedAmount((BigDecimal) model.getMatchedAmount());
				bill.setMonetaryUnit(model.getMonetaryUnit());

				bill.setObjectId(model.getObjectId()==null?null:model.getObjectId().toString());
				bill.setObjectType(model.getObjectType());
				bill.setOrderNo(model.getOrderNo());
				bill.setOther(model.getOther());
				bill.setOverDueFineStatus(model.getOverDueFineStatus());
				bill.setOverDueFineStatusName(model.getOverDueFineStatusName());
				bill.setPayDate(model.getPayDate());

				bill.setPayedAmount((BigDecimal) model.getPayedAmount());
				bill.setPrimeAmount((BigDecimal) model.getPrimeAmount());
				bill.setReceiptAmount((BigDecimal) model.getReceiptAmount());
				bill.setRemainingAmount((BigDecimal) model.getRemainingAmount());
				bill.setRoomNumber(model.getRoomNumber());
				bill.setSettleStatus(model.getSettleStatus());
				bill.setSettleStatusName(model.getSettleStatusName());

				bill.setSettledDate(model.getSettledDate());
				bill.setStartDate(model.getStartDate());
				bill.setTenantId(model.getTenantId()==null?null:model.getTenantId().toString());
				bill.setTermId(model.getTermId()==null?null:model.getTermId().toString());
				bill.setTerminationIncomeAdjust((BigDecimal) model.getTerminationIncomeAdjust());
				bill.setTheoryAmount((BigDecimal) model.getTheoryAmount());

				bill.setTransferAmount((BigDecimal) model.getTransferAmount());
				bill.setTransferToOtherBillAmount((BigDecimal) model.getTransferToOtherBillAmount());
				bill.setTypeNme(model.getTypeName());
				bill.setIsDel(model.getIsDel());
				bill.setCtime(model.getCtime());
				bill.setUtime(model.getUtime());

				bills.add(bill);
			}

			int lastIndx0 = billCashMatchDelSql.lastIndexOf(",");
//			int length = billCashMatchDelSql.length();
			if (lastIndx0 > 0) {
				billCashMatchDelSql.deleteCharAt(lastIndx0);
			}
			billCashMatchDelSql.append(")");

			int lastIndx1 = billRoomDelSql.lastIndexOf(",");
//			int length = billRoomDelSql.length();
			if (lastIndx1 > 0) {
				billRoomDelSql.deleteCharAt(lastIndx1);
			}
			billRoomDelSql.append(")");

			int lastIndx2 = cashFlowDelSql.lastIndexOf(",");
			if (lastIndx2 > 0) {
				cashFlowDelSql.deleteCharAt(lastIndx2);
				cashFlowDelSql.append(")");
				String delcashFlowSql = "delete from bill_cash_flow where bill_cash_match_id in "
						+ cashFlowDelSql.toString();
				DbHelper.getDb(prop).update(delcashFlowSql);
			}

			String delBillCatchMatchesSql = "delete from bill_cash_matches where bill_id in "
					+ billCashMatchDelSql.toString();
			String delBillRoomSql = "delete from bill_room where bill_id in " + billRoomDelSql.toString();

			DbHelper.getDb(prop).batchUpdate(bills, bills.size());
			DbHelper.getDb(prop).update(delBillCatchMatchesSql);
			DbHelper.getDb(prop).update(delBillRoomSql);
		} catch (Exception e) {
			logger.error("bill 批量插入失败！",e);
			e.printStackTrace();
			throw e;
		}

	}

	/*public static void insertBillCashMatches(List<BillCashMatchesModel> dataS) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		try {
			for (Iterator<BillCashMatchesModel> it = dataS.iterator(); it.hasNext();) {
				BillCashMatchesModel billCashMatch = it.next();

				String createdDate = null;
				if (billCashMatch.getCreatedDate() != null) {
					createdDate = "'" + billCashMatch.getCreatedDate() + "'";
				}

				String sql = "INSERT INTO bill_cash_matches " + "(id,bill_id,matched_amount,created_date) VALUES " + "("
						+ billCashMatch.getId() + ", '" + billCashMatch.getBillId() + "', "
						+ billCashMatch.getMatchedAmount() + ", " + createdDate + ");";
				sqlList.add(sql);
			}

			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :billCashMatches 批量插入失败！");
			e.printStackTrace();
			throw e;
		}

	}

	public static void insertBillRooms(List<BillRoomModel> dataS) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		try {
			for (Iterator<BillRoomModel> it = dataS.iterator(); it.hasNext();) {
				BillRoomModel billRoom = it.next();

				String sql = "INSERT INTO bill_room " + "(id,room_id,bill_id,room_number,floor,area_size) VALUES " + "("
						+ billRoom.getId() + ", " + billRoom.getRoomId() + ", " + billRoom.getBillId() + ", '"
						+ billRoom.getRoomNumber() + "', '" + billRoom.getFloor() + "', " + billRoom.getAreaSize()
						+ ");";
				sqlList.add(sql);
			}

			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :billRooms 批量插入失败！");
			e.printStackTrace();
			throw e;
		}
	}

	public static void insertCashFlows(List<BillCashFlowModel> dataS) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		try {
			for (Iterator<BillCashFlowModel> it = dataS.iterator(); it.hasNext();) {
				BillCashFlowModel cashFlow = it.next();

				String enterDate = null;
				if (cashFlow.getEnterDate() != null) {
					enterDate = "'" + cashFlow.getEnterDate() + "'";
				}

				String sql = "INSERT INTO bill_cash_flow "
						+ "(id,action,amount,digest,enter_date,flow_no,match_id,matched_amount,other_account,receipt_no,remittance_method,tenant_name,bill_cash_match_id) VALUES "
						+ "(" + cashFlow.getId() + ", '" + cashFlow.getAction() + "', " + cashFlow.getAmount() + ", '"
						+ cashFlow.getDigest() + "', " + enterDate + ", '" + cashFlow.getFlowNo() + "', '"
						+ cashFlow.getMatchId() + "', " + cashFlow.getMatchedAmount() + ", '"
						+ cashFlow.getOtherAccount() + "', '" + cashFlow.getReceiptNo() + "', '"
						+ cashFlow.getRemittanceMethod() + "', '" + cashFlow.getTenantName() + "', '"
						+ cashFlow.getBillCashMatchId() + "');";
				sqlList.add(sql);
			}

			DbHelper.getDb().batch(sqlList, sqlList.size());
		} catch (Exception e) {
			logger.error(new SimpleDateFormat(timeFormat).format(new Date()) + " :billRooms 批量插入失败！");
			e.printStackTrace();
			throw e;
		}
	}
*/
	
	public static void insertBillCashMatches(Prop prop, List<BillCashMatchesModel> dataS) throws Exception {
		List<BillCashMatches> billCashMatchesS = new ArrayList<BillCashMatches>();
		try {
			for (Iterator<BillCashMatchesModel> it = dataS.iterator(); it.hasNext();) {
				BillCashMatchesModel model = it.next();
				BillCashMatches billCashMatches = new BillCashMatches();
				billCashMatches.setId(model.getId().toString());
				billCashMatches.setBillId(model.getBillId()==null?null:model.getBillId().toString());
				billCashMatches.setMatchedAmount(model.getMatchedAmount());
				billCashMatches.setCreatedDate(model.getCreatedDate());
				
				billCashMatchesS.add(billCashMatches);
			}

			DbHelper.getDb(prop).batchSave(billCashMatchesS, billCashMatchesS.size());
		} catch (Exception e) {
			logger.error("billCashMatches 批量插入失败！",e);
			e.printStackTrace();
			throw e;
		}

	}

	public static void insertBillRooms(Prop prop,List<BillRoomModel> dataS) throws Exception {
		List<BillRoom> billRoomS = new ArrayList<BillRoom>();
		try {
			for (Iterator<BillRoomModel> it = dataS.iterator(); it.hasNext();) {
				BillRoomModel model = it.next();
				
				BillRoom billRoom = new BillRoom();
				billRoom.setId(model.getId().toString());
				billRoom.setRoomId(model.getRoomId()==null?null:model.getRoomId().toString());
				billRoom.setBillId(model.getBillId()==null?null:model.getBillId().toString());
				billRoom.setRoomNumber(model.getRoomNumber());
				billRoom.setFloor(model.getFloor());
				billRoom.setAreaSize(model.getAreaSize());
				
				billRoomS.add(billRoom);
			}

			DbHelper.getDb(prop).batchSave(billRoomS, billRoomS.size());
		} catch (Exception e) {
			logger.error("billRooms 批量插入失败！",e);
			e.printStackTrace();
			throw e;
		}
	}

	public static void insertCashFlows(Prop prop,List<BillCashFlowModel> dataS) throws Exception {
		List<BillCashFlow> billCashFlowS = new ArrayList<BillCashFlow>();
		try {
			for (Iterator<BillCashFlowModel> it = dataS.iterator(); it.hasNext();) {
				BillCashFlowModel model = it.next();
				BillCashFlow billCashFlow = new BillCashFlow();
				billCashFlow.setId(model.getId().toString());
				billCashFlow.setAction(model.getAction());
				billCashFlow.setAmount(model.getAmount());
				billCashFlow.setDigest(model.getDigest());
				billCashFlow.setEnterDate(model.getEnterDate());
				billCashFlow.setFlowNo(model.getFlowNo());
				billCashFlow.setMatchId(model.getMatchId());
				billCashFlow.setMatchedAmount(model.getMatchedAmount());
				billCashFlow.setOtherAccount(model.getOtherAccount());
				billCashFlow.setReceiptNo(model.getReceiptNo());
				billCashFlow.setRemittanceMethod(model.getRemittanceMethod());
				billCashFlow.setTenantName(model.getTenantName());
				billCashFlow.setBillCashMatchId(model.getBillCashMatchId().toString());
				billCashFlowS.add(billCashFlow);
			}

			DbHelper.getDb(prop).batchSave(billCashFlowS, billCashFlowS.size());
		} catch (Exception e) {
			logger.error(" :billRooms 批量插入失败！",e);
			e.printStackTrace();
			throw e;
		}
	}

}
