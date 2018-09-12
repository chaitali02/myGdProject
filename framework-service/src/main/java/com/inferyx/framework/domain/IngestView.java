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

/**
 * @author Ganesh
 *
 */
public class IngestView extends BaseEntity {
	private String sourceType;
	private MetaIdentifierHolder sourceName;
	private MetaIdentifierHolder sourceDetail;
	private String targetType;
	private MetaIdentifierHolder targetName;
	private MetaIdentifierHolder targetDetail;
	private String runParams;
	private String ingestChg;
	private Filter filter;	
	private String filterChg;
	private String sourceChg;
	
	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceChg
	 */
	public String getSourceChg() {
		return sourceChg;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceChg the sourceChg to set
	 */
	public void setSourceChg(String sourceChg) {
		this.sourceChg = sourceChg;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceName
	 */
	public MetaIdentifierHolder getSourceName() {
		return sourceName;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceName the sourceName to set
	 */
	public void setSourceName(MetaIdentifierHolder sourceName) {
		this.sourceName = sourceName;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceDetail
	 */
	public MetaIdentifierHolder getSourceDetail() {
		return sourceDetail;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceDetail the sourceDetail to set
	 */
	public void setSourceDetail(MetaIdentifierHolder sourceDetail) {
		this.sourceDetail = sourceDetail;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetType
	 */
	public String getTargetType() {
		return targetType;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param targetType the targetType to set
	 */
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetName
	 */
	public MetaIdentifierHolder getTargetName() {
		return targetName;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param targetName the targetName to set
	 */
	public void setTargetName(MetaIdentifierHolder targetName) {
		this.targetName = targetName;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetDetail
	 */
	public MetaIdentifierHolder getTargetDetail() {
		return targetDetail;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param targetDetail the targetDetail to set
	 */
	public void setTargetDetail(MetaIdentifierHolder targetDetail) {
		this.targetDetail = targetDetail;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the runParams
	 */
	public String getRunParams() {
		return runParams;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param runParams the runParams to set
	 */
	public void setRunParams(String runParams) {
		this.runParams = runParams;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the ingestChg
	 */
	public String getIngestChg() {
		return ingestChg;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param ingestChg the ingestChg to set
	 */
	public void setIngestChg(String ingestChg) {
		this.ingestChg = ingestChg;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the filter
	 */
	public Filter getFilter() {
		return filter;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param filter the filter to set
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the filterChg
	 */
	public String getFilterChg() {
		return filterChg;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param filterChg the filterChg to set
	 */
	public void setFilterChg(String filterChg) {
		this.filterChg = filterChg;
	}
}
