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

/**
 * @author Ganesh
 *
 */
public class DashboardExecView extends BaseEntity {
	private List<SectionView> sectionViewInfo;
	private List<AttributeRefHolder> filterInfo;
	private MetaIdentifierHolder dependsOn;
	private Dashboard dashboard;
	
	/**
	 * @Ganesh
	 *
	 * @return the dashboard
	 */
	public Dashboard getDashboard() {
		return dashboard;
	}
	/**
	 * @Ganesh
	 *
	 * @param dashboard the dashboard to set
	 */
	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}
	/**
	 * @Ganesh
	 *
	 * @return the sectionViewInfo
	 */
	public List<SectionView> getSectionViewInfo() {
		return sectionViewInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param sectionViewInfo the sectionViewInfo to set
	 */
	public void setSectionViewInfo(List<SectionView> sectionViewInfo) {
		this.sectionViewInfo = sectionViewInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @return the filterInfo
	 */
	public List<AttributeRefHolder> getFilterInfo() {
		return filterInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param filterInfo the filterInfo to set
	 */
	public void setFilterInfo(List<AttributeRefHolder> filterInfo) {
		this.filterInfo = filterInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @return the dependsOn
	 */
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	/**
	 * @Ganesh
	 *
	 * @param dependsOn the dependsOn to set
	 */
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
	
}
