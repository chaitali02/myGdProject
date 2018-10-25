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

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rule")
public class Rule extends BaseRule {
	
	private List<MetaIdentifierHolder> expressionInfo;
	private List<FilterInfo> filterInfo;	
	private boolean debugMode;
	private MetaIdentifierHolder source;	// May be a relation, datapod or dataset
	private List<AttributeSource> attributeInfo = new ArrayList<AttributeSource>();
	private String persistFlag;
	private MetaIdentifierHolder datasource;
	private MetaIdentifierHolder paramList;
	
	
	/**
	 *
	 * @Ganesh
	 *
	 * @return the filterInfo
	 */
	public List<FilterInfo> getFilterInfo() {
		return filterInfo;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param filterInfo the filterInfo to set
	 */
	public void setFilterInfo(List<FilterInfo> filterInfo) {
		this.filterInfo = filterInfo;
	}
	public List<MetaIdentifierHolder> getExpressionInfo() {
		return expressionInfo;
	}
	public void setExpressionInfo(List<MetaIdentifierHolder> expressionInfo) {
		this.expressionInfo = expressionInfo;
	}	
	public boolean isDebugMode() {
		return debugMode;
	}
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	public MetaIdentifierHolder getSource() {
		return source;
	}
	public void setSource(MetaIdentifierHolder source) {
		this.source = source;
	}	
	public List<AttributeSource> getAttributeInfo() {
		return attributeInfo;
	}
	public void setAttributeInfo(List<AttributeSource> attributeInfo) {
		this.attributeInfo = attributeInfo;
	}
	public String getPersistFlag() {
		return persistFlag;
	}
	public void setPersistFlag(String persistFlag) {
		this.persistFlag = persistFlag;
	}
	public MetaIdentifierHolder getDatasource() {
		return datasource;
	}
	public void setDatasource(MetaIdentifierHolder datasource) {
		this.datasource = datasource;
	}
	public String sql(String attrName) {
		return String.format("%s.%s", this.getName(), attrName);
	}
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}
	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}
	
	public String getAttributeName(Integer attrId) {
		if (getAttribute(attrId) == null) {
			return null;
		}
		return getAttribute(attrId).getAttrSourceName();
	}
	
	public AttributeSource getAttribute(Integer attrId) {
		for (AttributeSource sourceAttr : getAttributeInfo()) {
			if (sourceAttr.getSourceAttr() != null && sourceAttr.getAttrSourceId() != null
					&& sourceAttr.getAttrSourceId().equals(attrId.toString())) {
				return sourceAttr;
			}
		}
		return null;
	}
}
