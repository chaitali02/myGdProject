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

public class Section {
	
	private String sectionId;
	//private List<DashboardSectionMeta> metaId;
	 private String name;
	 private MetaIdentifierHolder vizpodInfo;
	 private Integer rowNo;
	 private Integer colNo;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MetaIdentifierHolder getVizpodInfo() {
		return vizpodInfo;
	}
	public void setVizpodInfo(MetaIdentifierHolder vizpodInfo) {
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
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	/*public List<DashboardSectionMeta> getMetaId() {
		return metaId;
	}
	public void setMetaId(List<DashboardSectionMeta> metaId) {
		this.metaId = metaId;
	}*/
	
	

}