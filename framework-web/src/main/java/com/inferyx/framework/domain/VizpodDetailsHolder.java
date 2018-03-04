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
 
public class VizpodDetailsHolder {
	
	private List<Map<String, Object>> vizpodDetailsDataList;
	
	
	private VizExec vizExec;
	
	public VizpodDetailsHolder() {
	}

	
	public VizpodDetailsHolder(List<Map<String, Object>> vizpodDetailsDataList) {
		this.vizpodDetailsDataList = vizpodDetailsDataList;
	}
	
	
	public VizpodDetailsHolder(VizExec vizExec) {
		this.vizExec = vizExec;
	}
	
	
	public VizpodDetailsHolder(List<Map<String, Object>> vizpodDetailsDataList, VizExec vizExec) {
		this.vizExec = vizExec;
		this.vizpodDetailsDataList = vizpodDetailsDataList;
	}
	
	public List<Map<String, Object>> getVizpodDetailsDataList() {
		return vizpodDetailsDataList;
	}
	
	public void setGetAttributeDetailsDataList(List<Map<String, Object>> vizpodDetailsDataList) {
		this.vizpodDetailsDataList = vizpodDetailsDataList;
	}
/*	public List<Map<String, Object>> attributeDetailsDataList() {
		return attributeDetailsDataList;
	}*/

	

	
	
	public VizExec getVizExec() {
		return vizExec;
	}
	

	public void setVizExec(VizExec vizExec) {
		this.vizExec = vizExec;
	}
	
	
	
	

	
}
