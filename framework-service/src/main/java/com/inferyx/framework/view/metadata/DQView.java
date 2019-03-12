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
package com.inferyx.framework.view.metadata;

import java.util.List;
import java.util.Map;

import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.RefIntegrity;

public class DQView extends BaseEntity{
	private Filter filter;	
	private String filterChg;	
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
	private String stdDevCheck;
	
	
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
	
	public String getFilterChg() {
		return filterChg;
	}
	public void setFilterChg(String filterChg) {
		this.filterChg = filterChg;
	}	
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
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
	public String getStdDevCheck() {
		return stdDevCheck;
	}
	public void setStdDevCheck(String stdDevCheck) {
		this.stdDevCheck = stdDevCheck;
	}
	
}
