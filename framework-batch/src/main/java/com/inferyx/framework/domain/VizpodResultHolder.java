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
import java.util.Map;
 
public class VizpodResultHolder {
	
	private List<Map<String, Object>> vizpodResultDataList;
	
	private VizExec vizExec;
	
	public VizpodResultHolder() {
	}
	
	public VizpodResultHolder(List<Map<String, Object>> vizpodResultDataList) {
		this.vizpodResultDataList = vizpodResultDataList;
	}

	public VizpodResultHolder(VizExec vizExec) {
		this.vizExec = vizExec;
	}
	
	public VizpodResultHolder(List<Map<String, Object>> vizpodResultDataList, VizExec vizExec) {
		this.vizExec = vizExec;
		this.vizpodResultDataList = vizpodResultDataList;
	}
	
	public List<Map<String, Object>> getVizpodResultDataList() {
		return vizpodResultDataList;
	}
	public void setVizpodResultDataList(List<Map<String, Object>> vizpodResultDataList) {
		this.vizpodResultDataList = vizpodResultDataList;
	}
	public VizExec getVizpodExec() {
		return vizExec;
	}
	public void setVizpodExec(VizExec vizExec) {
		this.vizExec = vizExec;
	}

	
	
	
	
}
