/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

import java.util.ArrayList;
import java.util.List;

public class FilterInfo {
	
	private String logicalOperator;
	private String operator;
	private List<SourceAttr> operand = new ArrayList<>(0); 
	//private MetaIdentifierHolder notMet;
	//private MetaIdentifierHolder met;
	
	public String getLogicalOperator() {
		return logicalOperator;
	}
	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public List<SourceAttr> getOperand() {
		return operand;
	}
	public void setOperand(List<SourceAttr> operand) {
		this.operand = operand;
	}
	
	/*public String sql(MetaIdentifierUtil commonActivity) {
		List<String> operandValue = new ArrayList<>(2);
		for(SourceAttr sourceAttr : getOperand()) {
			System.out.println(String.format("Processing metaIdentifier %s", sourceAttr.getRef().toString()));
			if(sourceAttr.getRef().getType() == MetaType.simple) {
				operandValue.add(sourceAttr.getValue());
			}
			if(sourceAttr.getRef().getType() == MetaType.datapod) {
				// Bhanu - JIRA FW-3 - Remove cache
				// Datapod datapod = (Datapod) sourceAttr.getRef().getCollectionObject(loader);
				Datapod datapod = (Datapod) commonActivity.getRefObject(sourceAttr.getRef());
				// End JIRA FW-3 Changes
				operandValue.add(datapod.sql(sourceAttr.getAttributeId()));
			}
		}
		
		return String.format("(%s %s %s)", operandValue.get(0), getOperator(), operandValue.get(1));
	}*/
	
/*	@Transient
	@JsonIgnore
	public static String sql(MetaIdentifierUtil commonActivity, List<FilterInfo> filters) {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(" ");
		Iterator<FilterInfo> itr = filters.iterator();
		
		// builder.append(itr.next().sql(commonActivity)); - FW-3 Bhanu -Removed as part of the new code changes
		while(itr.hasNext()) {
			FilterInfo filterInfo = itr.next();
			builder.append(" ").append(filterInfo.getLogicalOperator()).append(" ");
			builder.append(filterInfo.sql(commonActivity)).append(" ");
		}
		builder.append(")");
		
		System.out.println(String.format("Final filter %s", builder.toString()));	   
		return builder.toString();
	}*/
	
	/*public MetaIdentifierHolder getMet() {
		return met;
	}
	public void setMet(MetaIdentifierHolder met) {
		this.met = met;
	}*/
	/*public MetaIdentifierHolder getNotMet() {
		return notMet;
	}
	public void setNotMet(MetaIdentifierHolder notMet) {
		this.notMet = notMet;
	}*/

	
}