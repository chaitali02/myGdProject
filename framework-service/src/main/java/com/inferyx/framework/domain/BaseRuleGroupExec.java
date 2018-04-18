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

/**
 * @author joy
 *
 */
public class BaseRuleGroupExec extends BaseEntity {
	
	private List<MetaIdentifierHolder> execList;
	private MetaIdentifierHolder dependsOn;	
	private List<Status> statusList;

	/**
	 * 
	 */
	public BaseRuleGroupExec() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the execList
	 */
	public List<MetaIdentifierHolder> getExecList() {
		return execList;
	}

	/**
	 * @param execList the execList to set
	 */
	public void setExecList(List<MetaIdentifierHolder> execList) {
		this.execList = execList;
	}

	/**
	 * @return the dependsOn
	 */
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	/**
	 * @param dependsOn the dependsOn to set
	 */
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	/**
	 * @return the status
	 */
	public List<Status> getStatusList() {
		return statusList;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}
	
}
