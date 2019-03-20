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

import com.inferyx.framework.enums.AbortConditionType;

/**
 * @author joy
 *
 */
public class BaseRule extends BaseEntity {

	/**
	 * 
	 */
	public BaseRule() {
		// TODO Auto-generated constructor stub
	}
	
	private AbortConditionType abortCondition;

	/**
	 * @return the abortCondition
	 */
	public AbortConditionType getAbortCondition() {
		return abortCondition;
	}

	/**
	 * @param abortCondition the abortCondition to set
	 */
	public void setAbortCondition(AbortConditionType abortCondition) {
		this.abortCondition = abortCondition;
	}


}
