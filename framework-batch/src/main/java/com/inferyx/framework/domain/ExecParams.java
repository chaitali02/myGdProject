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

import java.util.List;

public class ExecParams {
	
	private List<MetaIdentifier> refKeyList;

	private List<ParamSetHolder> paramInfo;

	private List<MetaIdentifierHolder> dimInfo;
	
	private List<AttributeRefHolder> filterInfo;	

	private List<String> stageInfo;
	
	private ParamSetHolder paramSetHolder;
	
	public List<ParamSetHolder> getParamInfo() {
		return paramInfo;
	}

	public void setParamInfo(List<ParamSetHolder> paramInfo) {
		this.paramInfo = paramInfo;
	}
	
	public ParamSetHolder getParamSetHolder() {
		return paramSetHolder;
	}

	public void setParamSetHolder(ParamSetHolder paramSetHolder) {
		this.paramSetHolder = paramSetHolder;
	}

	public List<AttributeRefHolder> getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(List<AttributeRefHolder> filterInfo) {
		this.filterInfo = filterInfo;
	}

	public List<MetaIdentifier> getRefKeyList() {
		return refKeyList;
	}

	public void setRefKeyList(List<MetaIdentifier> refKeyList) {
		this.refKeyList = refKeyList;
	}

	public List<MetaIdentifierHolder> getDimInfo() {
		return dimInfo;
	}

	public void setDimInfo(List<MetaIdentifierHolder> filterList) {
		this.dimInfo = filterList;
	}

	public List<String> getStageInfo() {
		return stageInfo;
	}

	public void setStageInfo(List<String> stageInfo) {
		this.stageInfo = stageInfo;
	}
	
}
