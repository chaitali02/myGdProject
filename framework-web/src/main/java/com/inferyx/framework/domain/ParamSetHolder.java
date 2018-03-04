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

import org.springframework.stereotype.Component;

@Component
public class ParamSetHolder extends MetaIdentifierHolder{	
	
	private String paramSetId;
	public String getParamSetId() {
		return paramSetId;
	}
	public void setParamSetId(String paramSetId) {
		this.paramSetId = paramSetId;
	}
}
