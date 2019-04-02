/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.inferyx.framework.enums.AbortConditionType;
import com.inferyx.framework.enums.CaseCheckType;
import com.inferyx.framework.enums.ThresholdType;

@Document(collection="dq")
public class DataQual extends BaseRule{
	private MetaIdentifierHolder target;	
	private AttributeRefHolder attribute;	
	private MetaIdentifierHolder dependsOn;	
	private String duplicateKeyCheck;
	private String nullCheck;
	private List<String> valueCheck;
	private Map<String, String> rangeCheck;
	private String dataTypeCheck;
	private String dateFormatCheck;
	private String customFormatCheck;
	private Map<String, Long> lengthCheck;
	private RefIntegrity refIntegrityCheck;	
	//private String stdDevCheck;
	private List<FilterInfo> filterInfo;	
	private List<MetaIdentifierHolder> userInfo;
	private Threshold thresholdInfo;
	private MetaIdentifierHolder domainCheck;
	private String blankSpaceCheck;
	private MetaIdentifierHolder expressionCheck;
	private CaseCheckType caseCheck;
	private String passFailCheck;
	private MetaIdentifierHolder paramList;

	
	
	public MetaIdentifierHolder getDomainCheck() {
		return domainCheck;
	}
	public void setDomainCheck(MetaIdentifierHolder domainCheck) {
		this.domainCheck = domainCheck;
	}
	public String getBlankSpaceCheck() {
		return blankSpaceCheck;
	}
	public void setBlankSpaceCheck(String blankSpaceCheck) {
		this.blankSpaceCheck = blankSpaceCheck;
	}
	public MetaIdentifierHolder getExpressionCheck() {
		return expressionCheck;
	}
	public void setExpressionCheck(MetaIdentifierHolder expressionCheck) {
		this.expressionCheck = expressionCheck;
	}
	public List<MetaIdentifierHolder> getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(List<MetaIdentifierHolder> userInfo) {
		this.userInfo = userInfo;
	}
	public MetaIdentifierHolder getTarget() {
		return target;
	}
	public void setTarget(MetaIdentifierHolder target) {
		this.target = target;
	}
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
	
	public List<FilterInfo> getFilterInfo() {
		return filterInfo;
	}
	
	public void setFilterInfo(List<FilterInfo> filterInfo) {
		this.filterInfo = filterInfo;
	}
	public RefIntegrity getRefIntegrityCheck() {
		return refIntegrityCheck;
	}
	public void setRefIntegrityCheck(RefIntegrity refIntegrityCheck) {
		this.refIntegrityCheck = refIntegrityCheck;
	}	
	public AttributeRefHolder getAttribute() {
		return attribute;
	}
	public void setAttribute(AttributeRefHolder attribute) {
		this.attribute = attribute;
	}	
	
	public String getDuplicateKeyCheck() {
		return duplicateKeyCheck;
	}
	public void setDuplicateKeyCheck(String duplicateKeyCheck) {
		this.duplicateKeyCheck = duplicateKeyCheck;
	}
	public String getNullCheck() {
		return nullCheck;
	}
	public void setNullCheck(String nullCheck) {
		this.nullCheck = nullCheck;
	}
	public List<String> getValueCheck() {
		return valueCheck;
	}
	public void setValueCheck(List<String> valueCheck) {
		this.valueCheck = valueCheck;
	}
	public Map<String, String> getRangeCheck() {
		return rangeCheck;
	}
	public void setRangeCheck(Map<String, String> rangeCheck) {
		this.rangeCheck = rangeCheck;
	}
	public String getDataTypeCheck() {
		return dataTypeCheck;
	}
	public void setDataTypeCheck(String dataTypeCheck) {
		this.dataTypeCheck = dataTypeCheck;
	}
	public String getDateFormatCheck() {
		return dateFormatCheck;
	}
	public void setDateFormatCheck(String dateFormatCheck) {
		this.dateFormatCheck = dateFormatCheck;
	}
	public String getCustomFormatCheck() {
		return customFormatCheck;
	}
	public void setCustomFormatCheck(String customFormatCheck) {
		this.customFormatCheck = customFormatCheck;
	}
	public Map<String, Long> getLengthCheck() {
		return lengthCheck;
	}
	public void setLengthCheck(Map<String, Long> lengthCheck) {
		this.lengthCheck = lengthCheck;
	}	
	/*public String getStdDevCheck() {
		return stdDevCheck;
	}
	public void setStdDevCheck(String stdDevCheck) {
		this.stdDevCheck = stdDevCheck;
	}*/
	
	public Threshold getThresholdInfo() {
		return thresholdInfo;
	}
	public void setThresholdInfo(Threshold thresholdInfo) {
		this.thresholdInfo = thresholdInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @return the caseCheck
	 */
	public CaseCheckType getCaseCheck() {
		return caseCheck;
	}
	/**
	 * @Ganesh
	 *
	 * @param caseCheck the caseCheck to set
	 */
	public void setCaseCheck(CaseCheckType caseCheck) {
		this.caseCheck = caseCheck;
	}
	/**
	 * @return the passFailCheck
	 */
	public String getPassFailCheck() {
		return passFailCheck;
	}
	/**
	 * @param passFailCheck the passFailCheck to set
	 */
	
	public void setPassFailCheck(String passFailCheck) {
		this.passFailCheck = passFailCheck;
	}
	
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}
	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}
	

}
