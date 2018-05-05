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

import org.springframework.stereotype.Component;

@Component
public class ParamListHolder extends MetaIdentifierHolder{	
	
	private String paramId;
	private String paramName;
	private String paramType;
	private MetaIdentifierHolder paramValue;
	private List<AttributeRefHolder> attributeInfo;
	
	/**
	 * @Ganesh
	 *
	 * @return the attributeInfo
	 */

	public List<AttributeRefHolder> getAttributeInfo() {
		return attributeInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param attributeInfo the attributeInfo to set
	 */

	public void setAttributeInfo(List<AttributeRefHolder> attributeInfo) {
		this.attributeInfo = attributeInfo;
	}

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
