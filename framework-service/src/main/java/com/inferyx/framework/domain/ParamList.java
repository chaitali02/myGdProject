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

@Document(collection="paramlist")
public class ParamList extends BaseEntity{
	List<Param> params;
	MetaType paramListType; 
    private String templateFlg = "N";
    private MetaIdentifierHolder templateInfo;
    
	/**
	 * @Ganesh
	 *
	 * @return the templateInfo
	 */
	public MetaIdentifierHolder getTemplateInfo() {
		return templateInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param templateInfo the templateInfo to set
	 */
	public void setTemplateInfo(MetaIdentifierHolder templateInfo) {
		this.templateInfo = templateInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @return the templateFlg
	 */
	public String getTemplateFlg() {
		return templateFlg;
	}

	/**
	 * @Ganesh
	 *
	 * @param templateFlg the templateFlg to set
	 */
	public void setTemplateFlg(String templateFlg) {
		this.templateFlg = templateFlg;
	}

	public MetaType getParamListType() {
		return paramListType;
	}

	public void setParamListType(MetaType paramListType) {
		this.paramListType = paramListType;
	}

	public List<Param> getParams() {
		return params;
	}

	public void setParams(List<Param> params) {
		this.params = params;
	}

	
	
}
