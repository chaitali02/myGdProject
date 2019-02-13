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

@Document(collection="dashboard")
public class Dashboard extends BaseEntity {

	private List<Section> sectionInfo;	
	private MetaIdentifierHolder dependsOn;
	private List<AttributeRefHolder> filterInfo;
	private String saveOnRefresh = "N";
	
	/**
	 * @Ganesh
	 *
	 * @return the saveOnRefresh
	 */
	public String getSaveOnRefresh() {
		return saveOnRefresh;
	}
	
	/**
	 * @Ganesh
	 *
	 * @param saveOnRefresh the saveOnRefresh to set
	 */
	public void setSaveOnRefresh(String saveOnRefresh) {
		this.saveOnRefresh = saveOnRefresh;
	}

	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	public List<AttributeRefHolder> getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(List<AttributeRefHolder> filterInfo) {
		this.filterInfo = filterInfo;
	}

	public List<Section> getSectionInfo() {
		return sectionInfo;
	}

	public void setSectionInfo(List<Section> sectionInfo) {
		this.sectionInfo = sectionInfo;
	}
	
	/* 
	 * @Ganesh
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Dashboard [sectionInfo=" + sectionInfo + ", dependsOn=" + dependsOn + ", filterInfo=" + filterInfo
				+ ", saveOnRefresh=" + saveOnRefresh + "]";
	}
}