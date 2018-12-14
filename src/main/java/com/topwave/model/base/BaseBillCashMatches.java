package com.topwave.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseBillCashMatches<M extends BaseBillCashMatches<M>> extends Model<M> implements IBean {

	public M setId(java.lang.String id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public M setBillId(java.lang.String billId) {
		set("bill_id", billId);
		return (M)this;
	}
	
	public java.lang.String getBillId() {
		return getStr("bill_id");
	}

	public M setMatchedAmount(java.math.BigDecimal matchedAmount) {
		set("matched_amount", matchedAmount);
		return (M)this;
	}
	
	public java.math.BigDecimal getMatchedAmount() {
		return get("matched_amount");
	}

	public M setCreatedDate(java.util.Date createdDate) {
		set("created_date", createdDate);
		return (M)this;
	}
	
	public java.util.Date getCreatedDate() {
		return get("created_date");
	}

}
