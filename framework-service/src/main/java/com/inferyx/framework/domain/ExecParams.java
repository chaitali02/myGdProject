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

import java.util.HashMap;
import java.util.List;

public class ExecParams implements Cloneable {

	private List<MetaIdentifier> refKeyList;
	private List<ParamSetHolder> paramInfo;
	private List<AttributeRefHolder> filterInfo;
	private List<String> stageInfo;
	private ParamSetHolder currParamSet;
	private ParamListHolder paramListHolder;
	private List<ParamListHolder> paramListInfo;
	private java.util.Map<String, String> internalVarMap;
	private HashMap<String, String> otherParams;
	private ExecutionContext executionContext;
	private GraphFilter graphFilter;
	private java.util.Map<String, java.util.Map<String, String>> paramValMap;
	private HashMap<SysParamsEnum, String> sysParams;

	

	public HashMap<SysParamsEnum, String> getSysParams() {
		return sysParams;
	}

	public void setSysParams(HashMap<SysParamsEnum, String> sysParams) {
		this.sysParams = sysParams;
	}

	/**
	 * @Ganesh
	 *
	 * @return the paramListHolder
	 */
	public ParamListHolder getParamListHolder() {
		return paramListHolder;
	}

	/**
	 * @Ganesh
	 *
	 * @param paramListHolder the paramListHolder to set
	 */
	public void setParamListHolder(ParamListHolder paramListHolder) {
		this.paramListHolder = paramListHolder;
	}

	/**
	 * @return the executionContext
	 */
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	/**
	 * @param executionContext the executionContext to set
	 */
	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	/**
	 * @Ganesh
	 *
	 * @return the paramListInfo
	 */
	public List<ParamListHolder> getParamListInfo() {
		return paramListInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param paramListInfo the paramListInfo to set
	 */
	public void setParamListInfo(List<ParamListHolder> paramListInfo) {
		this.paramListInfo = paramListInfo;
	}

	public List<ParamSetHolder> getParamInfo() {
		return paramInfo;
	}

	public void setParamInfo(List<ParamSetHolder> paramInfo) {
		this.paramInfo = paramInfo;
	}

	public ParamSetHolder getCurrParamSet() {
		return currParamSet;
	}

	public void setCurrParamSet(ParamSetHolder currParamSet) {
		this.currParamSet = currParamSet;
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

	public List<String> getStageInfo() {
		return stageInfo;
	}

	public void setStageInfo(List<String> stageInfo) {
		this.stageInfo = stageInfo;
	}

	public java.util.Map<String, String> getInternalVarMap() {
		return internalVarMap;
	}

	public void setInternalVarMap(java.util.Map<String, String> internalVarMap) {
		this.internalVarMap = internalVarMap;
	}

	public HashMap<String, String> getOtherParams() {
		return otherParams;
	}

	public void setOtherParams(HashMap<String, String> otherParams) {
		this.otherParams = otherParams;
	}
	
	public GraphFilter getGraphFilter() {
		return this.graphFilter;
	}

	public void setGraphFilter(GraphFilter graphFilter) {
		this.graphFilter = graphFilter;
	}
	
	public Object clone() throws CloneNotSupportedException {
	    return super.clone();
	}

	/**
	 * @return the paramValMap
	 */
	public java.util.Map<String, java.util.Map<String, String>> getParamValMap() {
		return paramValMap;
	}

	/**
	 * @param paramValMap the paramValMap to set
	 */
	public void setParamValMap(java.util.Map<String, java.util.Map<String, String>> paramValMap) {
		this.paramValMap = paramValMap;
	}

}
