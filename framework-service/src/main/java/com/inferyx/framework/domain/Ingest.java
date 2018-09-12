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

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ganesh
 *
 */
@Document(collection = "ingest")
public class Ingest extends BaseEntity {
	private String sourceType;
	private MetaIdentifierHolder sourceName;
	private MetaIdentifierHolder sourceDetail;
	private String targetType;
	private MetaIdentifierHolder targetName;
	private MetaIdentifierHolder targetDetail;
	private String runParams;
	
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
}
