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

import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class Param {

	String paramId; // 0,1,2
	String paramName; // param1, param2
	String paramType; // string, date, double,datapod,attribute,attributes
	String paramDispName;	
	String paramDesc;
	MetaIdentifierHolder paramValue;
	MetaIdentifier paramRef;
	
	
	
	/**
	 * @return the paramDispName
	 */
	public String getParamDispName() {
		return this.paramDispName;
	}
	/**
	 * @param paramDispName the paramDispName to set
	 */
	public void setParamDispName(String paramDispName) {
		this.paramDispName = paramDispName;
	}
	/**
	 * @return the paramDesc
	 */
	public String getParamDesc() {
		return this.paramDesc;
	}
	/**
	 * @param paramDesc the paramDesc to set
	 */
	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}
	public String getParamId() {
		return paramId;
	}
	public void setParamId(String paramId) {
		this.paramId = paramId;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	
	/**
	 * @return the paramRef
	 */
	public MetaIdentifier getParamRef() {
		return paramRef;
	}
	/**
	 * @param paramRef the paramRef to set
	 */
	public void setParamRef(MetaIdentifier paramRef) {
		this.paramRef = paramRef;
	}
	public MetaIdentifierHolder getParamValue() {
		return paramValue;
	}
	public void setParamValue(MetaIdentifierHolder paramValue) {
		this.paramValue = paramValue;
	}
}
