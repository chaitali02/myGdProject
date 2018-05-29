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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




public class TaskOperator {

	private String operatorId;
	private List<MetaIdentifierHolder> dependsOn = new ArrayList<>(0);
	private String operatorType;
	private MetaIdentifierHolder operatorInfo;
	private HashMap<String, Object> operatorParams = new HashMap<String, Object>();
	/*private List<AttributeRefHolder> filterInfo = new ArrayList<>();
	private MetaIdentifierHolder groupInfo;*/
	
	
	/*public List<AttributeRefHolder> getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(List<AttributeRefHolder> filterInfo) {
		this.filterInfo = filterInfo;
	}*/

	public HashMap<String, Object> getOperatorParams() {
		return operatorParams;
	}

	public void setOperatorParams(HashMap<String, Object> operatorParams) {
		this.operatorParams = operatorParams;
	}

	public String getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}

	/*public MetaIdentifierHolder getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(MetaIdentifierHolder groupInfo) {
		this.groupInfo = groupInfo;
	}*/

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public List<MetaIdentifierHolder> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(List<MetaIdentifierHolder> depdendsOn) {
		this.dependsOn = depdendsOn;
	}

	public MetaIdentifierHolder getOperatorInfo() {
		return operatorInfo;
	}

	public void setOperatorInfo(MetaIdentifierHolder operatorInfo) {
		this.operatorInfo = operatorInfo;
	}
	
}