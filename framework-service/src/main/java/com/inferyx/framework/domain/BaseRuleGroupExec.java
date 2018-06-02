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
public class BaseRuleGroupExec extends BaseExec {
	
	private List<MetaIdentifierHolder> execList;
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

}
