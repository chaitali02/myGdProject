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

import com.inferyx.framework.enums.SaveMode;

/**
 * @author Ganesh
 *
 */
@Document(collection = "ingest")
public class Ingest extends BaseRule {
	private String type; //FILE-FILE, FILE-TABLE, TABLE-FILE, TABLE-TABLE
	private MetaIdentifierHolder sourceDatasource;
	private MetaIdentifierHolder sourceDetail;
	private MetaIdentifierHolder targetDatasource;
	private MetaIdentifierHolder targetDetail;
	private List<FilterInfo> filterInfo;
	private String runParams;
	private String sourceFormat; //CSV, TSV, PSV, PARQUET
	private String targetFormat; //CSV, TSV, PSV, PARQUET
	private AttributeRefHolder incrAttr;
	private SaveMode saveMode;
	private String ignoreCase = "N";
	private String sourceExtn;
	private String targetExtn;
	private List<AttributeMap> attributeMap;
	private AttributeRefHolder splitBy ;
	private String sourceHeader;
	private String targetHeader;
	
	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceHeader
	 */
	public String getSourceHeader() {
		return sourceHeader;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceHeader the sourceHeader to set
	 */
	public void setSourceHeader(String sourceHeader) {
		this.sourceHeader = sourceHeader;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetHeader
	 */
	public String getTargetHeader() {
		return targetHeader;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param targetHeader the targetHeader to set
	 */
	public void setTargetHeader(String targetHeader) {
		this.targetHeader = targetHeader;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceExtn
	 */
	public String getSourceExtn() {
		return sourceExtn;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceExtn the sourceExtn to set
	 */
	public void setSourceExtn(String sourceExtn) {
		this.sourceExtn = sourceExtn;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetExtn
	 */
	public String getTargetExtn() {
		return targetExtn;
	}
	public List<AttributeMap> getAttributeMap() {
		return attributeMap;
	}
	public void setAttributeMap(List<AttributeMap> attributeMap) {
		this.attributeMap = attributeMap;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param targetExtn the targetExtn to set
	 */
	public void setTargetExtn(String targetExtn) {
		this.targetExtn = targetExtn;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the ignoreCase
	 */
	public String getIgnoreCase() {
		return ignoreCase;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param ignoreCase the ignoreCase to set
	 */
	public void setIgnoreCase(String ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the saveMode
	 */
	public SaveMode getSaveMode() {
		return saveMode;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param saveMode the saveMode to set
	 */
	public void setSaveMode(SaveMode saveMode) {
		this.saveMode = saveMode;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the incrAttr
	 */
	public AttributeRefHolder getIncrAttr() {
		return incrAttr;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param incrAttr the incrAttr to set
	 */
	public void setIncrAttr(AttributeRefHolder incrAttr) {
		this.incrAttr = incrAttr;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceDatasource
	 */
	public MetaIdentifierHolder getSourceDatasource() {
		return sourceDatasource;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceDatasource the sourceDatasource to set
	 */
	public void setSourceDatasource(MetaIdentifierHolder sourceDatasource) {
		this.sourceDatasource = sourceDatasource;
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
	 * @return the targetDatasource
	 */
	public MetaIdentifierHolder getTargetDatasource() {
		return targetDatasource;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param targetDatasource the targetDatasource to set
	 */
	public void setTargetDatasource(MetaIdentifierHolder targetDatasource) {
		this.targetDatasource = targetDatasource;
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
	 * @return the filterInfo
	 */
	public List<FilterInfo> getFilterInfo() {
		return filterInfo;
	}
	
	public void setFilterInfo(List<FilterInfo> filterInfo) {
		this.filterInfo = filterInfo;
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
	 * @return the sourceFormat
	 */
	public String getSourceFormat() {
		return sourceFormat;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceFormat the sourceFormat to set
	 */
	public void setSourceFormat(String sourceFormat) {
		this.sourceFormat = sourceFormat;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetFormat
	 */
	public String getTargetFormat() {
		return targetFormat;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param targetFormat the targetFormat to set
	 */
	public void setTargetFormat(String targetFormat) {
		this.targetFormat = targetFormat;
	}
	
	public AttributeRefHolder getSplitBy() {
		return splitBy;
	}
	public void setSplitBy(AttributeRefHolder splitBy) {
		this.splitBy = splitBy;
	}
}
