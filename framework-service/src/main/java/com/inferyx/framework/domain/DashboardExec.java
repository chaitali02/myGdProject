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

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ganesh
 *
 */
@Document(collection = "dashboardexec")
public class DashboardExec extends BaseRuleExec {
	private List<MetaIdentifierHolder> vizExecInfo;

	/**
	 * @Ganesh
	 *
	 * @return the vizExecInfo
	 */
	public List<MetaIdentifierHolder> getVizExecInfo() {
		return vizExecInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param vizExecInfo the vizExecInfo to set
	 */
	public void setVizExecInfo(List<MetaIdentifierHolder> vizExecInfo) {
		this.vizExecInfo = vizExecInfo;
	}
	
}
