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
