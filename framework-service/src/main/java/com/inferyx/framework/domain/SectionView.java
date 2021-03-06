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

public class SectionView {
	
	private String sectionId;
	private String name;
	private Vizpod vizpodInfo;
	private VizExec vizExecInfo;
	private Integer rowNo;
    private Integer colNo;
	 
	/**
	 * @Ganesh
	 *
	 * @return the vizExecInfo
	 */
	public VizExec getVizExecInfo() {
		return vizExecInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param vizExecInfo the vizExecInfo to set
	 */
	public void setVizExecInfo(VizExec vizExecInfo) {
		this.vizExecInfo = vizExecInfo;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Vizpod getVizpodInfo() {
		return vizpodInfo;
	}
	public void setVizpodInfo(Vizpod vizpodInfo) {
		this.vizpodInfo = vizpodInfo;
	}
	public Integer getRowNo() {
		return rowNo;
	}
	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
	}
	public Integer getColNo() {
		return colNo;
	}
	public void setColNo(Integer colNo) {
		this.colNo = colNo;
	}
	 
}
