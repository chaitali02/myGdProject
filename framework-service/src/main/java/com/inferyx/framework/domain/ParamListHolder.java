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
public class ParamListHolder extends MetaIdentifierHolder{	
	
	private String paramId;
	private String paramName;
	private String paramType;
	private MetaIdentifierHolder paramValue;

	/**
	 * @Ganesh
	 *
	 * @return the paramValue
	 */
	public MetaIdentifierHolder getParamValue() {
		return paramValue;
	}

	/**
	 * @Ganesh
	 *
	 * @param paramValue the paramValue to set
	 */
	public void setParamValue(MetaIdentifierHolder paramValue) {
		this.paramValue = paramValue;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamId() {
		return paramId;
	}

	public void setParamId(String paramId) {
		this.paramId = paramId;
	}

}
