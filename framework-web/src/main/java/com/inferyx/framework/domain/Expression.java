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



import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="expression")
public class Expression extends BaseEntity {
	
	private MetaIdentifierHolder dependsOn;
	private String condition;
	private AttributeRefHolder match;
	private AttributeRefHolder noMatch;	
	private List<FilterInfo> expressionInfo;	
	
	public AttributeRefHolder getNoMatch() {
		return noMatch;
	}

	public void setNoMatch(AttributeRefHolder noMatch) {
		this.noMatch = noMatch;
	}

	public AttributeRefHolder getMatch() {
		return match;
	}

	public void setMatch(AttributeRefHolder match) {
		this.match = match;
	}
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}	
	
	/*public MetaIdentifierHolder getNotMet() {
		return notMet;
	}

	public void setNotMet(MetaIdentifierHolder notMet) {
		this.notMet = notMet;
	}

	public MetaIdentifierHolder getMet() {
		return met;
	}

	/*public void setMet(MetaIdentifierHolder met) {
		this.met = met;
	}*/
	public List<FilterInfo> getExpressionInfo() {
		return expressionInfo;
	}

	public void setExpressionInfo(List<FilterInfo> expressionInfo) {
		this.expressionInfo = expressionInfo;
	}

	/**
	 * 
	 * @return The expression
	 */
	
	/**getExpressionInfo().get(0).
	 * 
	 * @return The condition
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * 
	 * @param condition
	 *            The condition
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	
/*	public String sql(MetaIdentifierUtil commonActivity) {
		return String.format(" %s",this.getExpressionInfo());
	}*/
}