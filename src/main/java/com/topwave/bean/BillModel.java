package com.topwave.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class BillModel {
	
	private List<BillCashMatchesModel> billCashMatches;
	
	private List<BillRoomModel> rooms;
	
	private String id;
	
	private String billNo;
	
	private String action;
	
	private BigDecimal adjustedPrimeAmount;
	
	private BigDecimal actualAmount;
	
	private BigDecimal upperLimitRate;
	
	private BigDecimal rate;
	//先写成String类型
	private BigDecimal overDueFineTheoryAmount;
	
	private String billType;
	
	private String buildingId;
	
	private String buildingName;
	
	private String closedStatus;

	private String closedStatusName;
	
	private Timestamp createdDate;
	
	private String dateScope;
	
	private String dueStatus;
	
	private String dueStatusName;
	
	private Timestamp endDate;
	
	private Integer expiredDay;
	
	private Integer generatedReminderCount;
	
	private String handler;
	
	private String invoiceAmount;
	
	private String invoiceStatus;
	
	private String invoiceStatusName;
	
	private Integer issueReceiptCount;
	
	private String matchId;
	
	private BigDecimal matchedAmount;
	
	private String monetaryUnit;
	
	private Integer objectId;
	
	private String objectType;
	
	private String orderNo;
	
	private String other;
	
	private Timestamp payDate;
	
	private BigDecimal payedAmount;
	
	private BigDecimal primeAmount;
	
	private BigDecimal receiptAmount;

	private BigDecimal remainingAmount;
	
	private String roomNumber;
	
	private String settleStatus;
	
	private String settleStatusName;
	
	private Timestamp settledDate;
	
	private Timestamp startDate;
	
	private Integer tenantId;
	
	private Integer termId;
	
	private BigDecimal terminationIncomeAdjust;

	private BigDecimal theoryAmount;
	
	private BigDecimal transferAmount;
	
	private BigDecimal transferToOtherBillAmount;
	
	private String typeName;
	
	private String overDueFineStatusName;
	
	private String overDueFineStatus;
	
	private String isDel;
	
	private Timestamp ctime;

	private Timestamp utime;

	public List<BillCashMatchesModel> getBillCashMatches() {
		return billCashMatches;
	}

	public void setBillCashMatches(List<BillCashMatchesModel> billCashMatches) {
		this.billCashMatches = billCashMatches;
	}

	public List<BillRoomModel> getRooms() {
		return rooms;
	}

	public void setRooms(List<BillRoomModel> rooms) {
		this.rooms = rooms;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	
	public BigDecimal getAdjustedPrimeAmount() {
		return adjustedPrimeAmount;
	}

	public void setAdjustedPrimeAmount(BigDecimal adjustedPrimeAmount) {
		this.adjustedPrimeAmount = adjustedPrimeAmount;
	}

	
	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}


	public BigDecimal getUpperLimitRate() {
		return upperLimitRate;
	}

	public void setUpperLimitRate(BigDecimal upperLimitRate) {
		this.upperLimitRate = upperLimitRate;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getOverDueFineTheoryAmount() {
		return overDueFineTheoryAmount;
	}

	public void setOverDueFineTheoryAmount(BigDecimal overDueFineTheoryAmount) {
		this.overDueFineTheoryAmount = overDueFineTheoryAmount;
	}

	public void setMatchedAmount(BigDecimal matchedAmount) {
		this.matchedAmount = matchedAmount;
	}

	public void setPayedAmount(BigDecimal payedAmount) {
		this.payedAmount = payedAmount;
	}

	public void setPrimeAmount(BigDecimal primeAmount) {
		this.primeAmount = primeAmount;
	}

	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public void setTerminationIncomeAdjust(BigDecimal terminationIncomeAdjust) {
		this.terminationIncomeAdjust = terminationIncomeAdjust;
	}

	public void setTheoryAmount(BigDecimal theoryAmount) {
		this.theoryAmount = theoryAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public void setTransferToOtherBillAmount(BigDecimal transferToOtherBillAmount) {
		this.transferToOtherBillAmount = transferToOtherBillAmount;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getClosedStatus() {
		return closedStatus;
	}

	public void setClosedStatus(String closedStatus) {
		this.closedStatus = closedStatus;
	}

	public String getClosedStatusName() {
		return closedStatusName;
	}

	public void setClosedStatusName(String closedStatusName) {
		this.closedStatusName = closedStatusName;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getDateScope() {
		return dateScope;
	}

	public void setDateScope(String dateScope) {
		this.dateScope = dateScope;
	}

	public String getDueStatus() {
		return dueStatus;
	}

	public void setDueStatus(String dueStatus) {
		this.dueStatus = dueStatus;
	}

	public String getDueStatusName() {
		return dueStatusName;
	}

	public void setDueStatusName(String dueStatusName) {
		this.dueStatusName = dueStatusName;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Integer getExpiredDay() {
		return expiredDay;
	}

	public void setExpiredDay(Integer expiredDay) {
		this.expiredDay = expiredDay;
	}

	public Integer getGeneratedReminderCount() {
		return generatedReminderCount;
	}

	public void setGeneratedReminderCount(Integer generatedReminderCount) {
		this.generatedReminderCount = generatedReminderCount;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public String getInvoiceStatusName() {
		return invoiceStatusName;
	}

	public void setInvoiceStatusName(String invoiceStatusName) {
		this.invoiceStatusName = invoiceStatusName;
	}

	public Integer getIssueReceiptCount() {
		return issueReceiptCount;
	}

	public void setIssueReceiptCount(Integer issueReceiptCount) {
		this.issueReceiptCount = issueReceiptCount;
	}

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public BigDecimal getMatchedAmount() {
		return matchedAmount;
	}

	public String getMonetaryUnit() {
		return monetaryUnit;
	}

	public void setMonetaryUnit(String monetaryUnit) {
		this.monetaryUnit = monetaryUnit;
	}

	public Integer getObjectId() {
		return objectId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public Timestamp getPayDate() {
		return payDate;
	}

	public void setPayDate(Timestamp payDate) {
		this.payDate = payDate;
	}

	
	public BigDecimal getPayedAmount() {
		return payedAmount;
	}

	public BigDecimal getPrimeAmount() {
		return primeAmount;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getSettleStatus() {
		return settleStatus;
	}

	public void setSettleStatus(String settleStatus) {
		this.settleStatus = settleStatus;
	}

	public String getSettleStatusName() {
		return settleStatusName;
	}

	public void setSettleStatusName(String settleStatusName) {
		this.settleStatusName = settleStatusName;
	}

	public Timestamp getSettledDate() {
		return settledDate;
	}

	public void setSettledDate(Timestamp settledDate) {
		this.settledDate = settledDate;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	
	public BigDecimal getTerminationIncomeAdjust() {
		return terminationIncomeAdjust;
	}

	public BigDecimal getTheoryAmount() {
		return theoryAmount;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public BigDecimal getTransferToOtherBillAmount() {
		return transferToOtherBillAmount;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getOverDueFineStatusName() {
		return overDueFineStatusName;
	}

	public void setOverDueFineStatusName(String overDueFineStatusName) {
		this.overDueFineStatusName = overDueFineStatusName;
	}

	public String getOverDueFineStatus() {
		return overDueFineStatus;
	}

	public void setOverDueFineStatus(String overDueFineStatus) {
		this.overDueFineStatus = overDueFineStatus;
	}

	public String getIsDel() {
		return isDel;
	}

	public void setIsDel(String isDel) {
		this.isDel = isDel;
	}

	public Timestamp getCtime() {
		return ctime;
	}

	public void setCtime(Timestamp ctime) {
		this.ctime = ctime;
	}

	public Timestamp getUtime() {
		return utime;
	}

	public void setUtime(Timestamp utime) {
		this.utime = utime;
	}
	

}
