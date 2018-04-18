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
package com.inferyx.framework.view.metadata;

import java.util.List;

import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.SectionView;

public class DashboardView extends BaseEntity{
	
	private String srcChg;	
	private List<SectionView> sectionInfo;
	private List<AttributeRefHolder> filterInfo;
	private MetaIdentifierHolder dependsOn;
	
	public String getSrcChg() {
		return srcChg;
	}
	public void setSrcChg(String srcChg) {
		this.srcChg = srcChg;
	}
	public List<SectionView> getSectionInfo() {
		return sectionInfo;
	}
	public void setSectionInfo(List<SectionView> sectionInfo) {
		this.sectionInfo = sectionInfo;
	}
	public List<AttributeRefHolder> getFilterInfo() {
		return filterInfo;
	}
	public void setFilterInfo(List<AttributeRefHolder> filterInfo) {
		this.filterInfo = filterInfo;
	}
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

}
