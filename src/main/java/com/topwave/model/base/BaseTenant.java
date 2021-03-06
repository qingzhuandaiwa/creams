package com.topwave.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseTenant<M extends BaseTenant<M>> extends Model<M> implements IBean {

	public M setEnterpriseId(java.lang.String enterpriseId) {
		set("enterprise_id", enterpriseId);
		return (M)this;
	}
	
	public java.lang.String getEnterpriseId() {
		return getStr("enterprise_id");
	}

	public M setEnterpriseName(java.lang.String enterpriseName) {
		set("enterprise_name", enterpriseName);
		return (M)this;
	}
	
	public java.lang.String getEnterpriseName() {
		return getStr("enterprise_name");
	}

	public M setContacts(java.lang.String contacts) {
		set("contacts", contacts);
		return (M)this;
	}
	
	public java.lang.String getContacts() {
		return getStr("contacts");
	}

	public M setCertificateNumber(java.lang.String certificateNumber) {
		set("certificate_number", certificateNumber);
		return (M)this;
	}
	
	public java.lang.String getCertificateNumber() {
		return getStr("certificate_number");
	}

	public M setLegalPerson(java.lang.String legalPerson) {
		set("legal_person", legalPerson);
		return (M)this;
	}
	
	public java.lang.String getLegalPerson() {
		return getStr("legal_person");
	}

	public M setHasCurrentContract(java.lang.String hasCurrentContract) {
		set("has_current_contract", hasCurrentContract);
		return (M)this;
	}
	
	public java.lang.String getHasCurrentContract() {
		return getStr("has_current_contract");
	}

	public M setTel(java.lang.String tel) {
		set("tel", tel);
		return (M)this;
	}
	
	public java.lang.String getTel() {
		return getStr("tel");
	}

	public M setEmail(java.lang.String email) {
		set("email", email);
		return (M)this;
	}
	
	public java.lang.String getEmail() {
		return getStr("email");
	}

	public M setAddress(java.lang.String address) {
		set("address", address);
		return (M)this;
	}
	
	public java.lang.String getAddress() {
		return getStr("address");
	}

	public M setRemark(java.lang.String remark) {
		set("remark", remark);
		return (M)this;
	}
	
	public java.lang.String getRemark() {
		return getStr("remark");
	}

	public M setApprovalTime(java.util.Date approvalTime) {
		set("approval_time", approvalTime);
		return (M)this;
	}
	
	public java.util.Date getApprovalTime() {
		return get("approval_time");
	}

	public M setBirthCountry(java.lang.String birthCountry) {
		set("birth_country", birthCountry);
		return (M)this;
	}
	
	public java.lang.String getBirthCountry() {
		return getStr("birth_country");
	}

	public M setBusinessAddress(java.lang.String businessAddress) {
		set("business_address", businessAddress);
		return (M)this;
	}
	
	public java.lang.String getBusinessAddress() {
		return getStr("business_address");
	}

	public M setBusinessIndustry(java.lang.String businessIndustry) {
		set("business_industry", businessIndustry);
		return (M)this;
	}
	
	public java.lang.String getBusinessIndustry() {
		return getStr("business_industry");
	}

	public M setBusinessScope(java.lang.String businessScope) {
		set("business_scope", businessScope);
		return (M)this;
	}
	
	public java.lang.String getBusinessScope() {
		return getStr("business_scope");
	}

	public M setBusinessTerm(java.lang.String businessTerm) {
		set("business_term", businessTerm);
		return (M)this;
	}
	
	public java.lang.String getBusinessTerm() {
		return getStr("business_term");
	}

	public M setCompanyType(java.lang.String companyType) {
		set("company_type", companyType);
		return (M)this;
	}
	
	public java.lang.String getCompanyType() {
		return getStr("company_type");
	}

	public M setDistrict(java.lang.String district) {
		set("district", district);
		return (M)this;
	}
	
	public java.lang.String getDistrict() {
		return getStr("district");
	}

	public M setEnglishName(java.lang.String englishName) {
		set("english_name", englishName);
		return (M)this;
	}
	
	public java.lang.String getEnglishName() {
		return getStr("english_name");
	}

	public M setFoundingTime(java.util.Date foundingTime) {
		set("founding_time", foundingTime);
		return (M)this;
	}
	
	public java.util.Date getFoundingTime() {
		return get("founding_time");
	}

	public M setOperatingState(java.lang.String operatingState) {
		set("operating_state", operatingState);
		return (M)this;
	}
	
	public java.lang.String getOperatingState() {
		return getStr("operating_state");
	}

	public M setOrganizationCode(java.lang.String organizationCode) {
		set("organization_code", organizationCode);
		return (M)this;
	}
	
	public java.lang.String getOrganizationCode() {
		return getStr("organization_code");
	}

	public M setRegisteredCapital(java.lang.String registeredCapital) {
		set("registered_capital", registeredCapital);
		return (M)this;
	}
	
	public java.lang.String getRegisteredCapital() {
		return getStr("registered_capital");
	}

	public M setRegistrationAuthority(java.lang.String registrationAuthority) {
		set("registration_authority", registrationAuthority);
		return (M)this;
	}
	
	public java.lang.String getRegistrationAuthority() {
		return getStr("registration_authority");
	}

	public M setRegistrationNumber(java.lang.String registrationNumber) {
		set("registration_number", registrationNumber);
		return (M)this;
	}
	
	public java.lang.String getRegistrationNumber() {
		return getStr("registration_number");
	}

	public M setStaffSize(java.lang.String staffSize) {
		set("staff_size", staffSize);
		return (M)this;
	}
	
	public java.lang.String getStaffSize() {
		return getStr("staff_size");
	}

	public M setTaxpayeridNumber(java.lang.String taxpayeridNumber) {
		set("taxpayerId_number", taxpayeridNumber);
		return (M)this;
	}
	
	public java.lang.String getTaxpayeridNumber() {
		return getStr("taxpayerId_number");
	}

	public M setUnifiedSocialCreditCode(java.lang.String unifiedSocialCreditCode) {
		set("unifiedSocialCreditCode", unifiedSocialCreditCode);
		return (M)this;
	}
	
	public java.lang.String getUnifiedSocialCreditCode() {
		return getStr("unifiedSocialCreditCode");
	}

	public M setIsDel(java.lang.String isDel) {
		set("is_del", isDel);
		return (M)this;
	}
	
	public java.lang.String getIsDel() {
		return getStr("is_del");
	}

	public M setCtime(java.util.Date ctime) {
		set("ctime", ctime);
		return (M)this;
	}
	
	public java.util.Date getCtime() {
		return get("ctime");
	}

	public M setUtime(java.util.Date utime) {
		set("utime", utime);
		return (M)this;
	}
	
	public java.util.Date getUtime() {
		return get("utime");
	}

}
