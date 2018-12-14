package com.topwave.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseContractLeaseTerm<M extends BaseContractLeaseTerm<M>> extends Model<M> implements IBean {

	public M setLeaseId(java.lang.String leaseId) {
		set("lease_id", leaseId);
		return (M)this;
	}
	
	public java.lang.String getLeaseId() {
		return getStr("lease_id");
	}

	public M setTermBeginDate(java.util.Date termBeginDate) {
		set("term_begin_date", termBeginDate);
		return (M)this;
	}
	
	public java.util.Date getTermBeginDate() {
		return get("term_begin_date");
	}

	public M setTermEndDate(java.util.Date termEndDate) {
		set("term_end_date", termEndDate);
		return (M)this;
	}
	
	public java.util.Date getTermEndDate() {
		return get("term_end_date");
	}

	public M setContractPayEnum(java.lang.String contractPayEnum) {
		set("contract_pay_enum", contractPayEnum);
		return (M)this;
	}
	
	public java.lang.String getContractPayEnum() {
		return getStr("contract_pay_enum");
	}

	public M setDayNumberForYear(java.lang.String dayNumberForYear) {
		set("day_number_for_year", dayNumberForYear);
		return (M)this;
	}
	
	public java.lang.String getDayNumberForYear() {
		return getStr("day_number_for_year");
	}

	public M setCalculateEnum(java.lang.String calculateEnum) {
		set("calculate_enum", calculateEnum);
		return (M)this;
	}
	
	public java.lang.String getCalculateEnum() {
		return getStr("calculate_enum");
	}

	public M setMonthPriceConvertRoleEnum(java.lang.String monthPriceConvertRoleEnum) {
		set("month_price_convert_role_enum", monthPriceConvertRoleEnum);
		return (M)this;
	}
	
	public java.lang.String getMonthPriceConvertRoleEnum() {
		return getStr("month_price_convert_role_enum");
	}

	public M setLeaseDivideRoleEnum(java.lang.String leaseDivideRoleEnum) {
		set("lease_divide_role_enum", leaseDivideRoleEnum);
		return (M)this;
	}
	
	public java.lang.String getLeaseDivideRoleEnum() {
		return getStr("lease_divide_role_enum");
	}

	public M setIntervalMonth(java.lang.String intervalMonth) {
		set("interval_month", intervalMonth);
		return (M)this;
	}
	
	public java.lang.String getIntervalMonth() {
		return getStr("interval_month");
	}

	public M setPayInAdvanceday(java.lang.String payInAdvanceday) {
		set("pay_in_advanceDay", payInAdvanceday);
		return (M)this;
	}
	
	public java.lang.String getPayInAdvanceday() {
		return getStr("pay_in_advanceDay");
	}

	public M setPaymentDateEnum(java.lang.String paymentDateEnum) {
		set("payment_date_enum", paymentDateEnum);
		return (M)this;
	}
	
	public java.lang.String getPaymentDateEnum() {
		return getStr("payment_date_enum");
	}

	public M setPrice(java.lang.String price) {
		set("price", price);
		return (M)this;
	}
	
	public java.lang.String getPrice() {
		return getStr("price");
	}

	public M setPriceUnitEnum(java.lang.String priceUnitEnum) {
		set("price_unit_enum", priceUnitEnum);
		return (M)this;
	}
	
	public java.lang.String getPriceUnitEnum() {
		return getStr("price_unit_enum");
	}

	public M setMonetaryUnit(java.lang.String monetaryUnit) {
		set("monetary_unit", monetaryUnit);
		return (M)this;
	}
	
	public java.lang.String getMonetaryUnit() {
		return getStr("monetary_unit");
	}

	public M setContractId(java.lang.String contractId) {
		set("contract_id", contractId);
		return (M)this;
	}
	
	public java.lang.String getContractId() {
		return getStr("contract_id");
	}

}