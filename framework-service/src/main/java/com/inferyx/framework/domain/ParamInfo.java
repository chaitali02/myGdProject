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

public class ParamInfo {
	String paramSetId;
	List<ParamListHolder> paramSetVal;
	
	public String getParamSetId() {
		return paramSetId;
	}
	public void setParamSetId(String paramSetId) {
		this.paramSetId = paramSetId;
	}
	public List<ParamListHolder> getParamSetVal() {
		return paramSetVal;
	}
	public void setParamSetVal(List<ParamListHolder> paramSetVal) {
		this.paramSetVal = paramSetVal;
	}
}
