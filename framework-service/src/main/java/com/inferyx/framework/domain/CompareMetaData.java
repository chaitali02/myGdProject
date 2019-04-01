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
public class CompareMetaData {
	private String sourceAttribute;
	private String sourceType;
	private String sourceLength;
	private String targetAttribute;
	private String targetType;
	private String targetLength;
	private String status;
	private String sourcePrecision;
	private String targetPrecision;
	
	/**
	 * @Ganesh
	 *
	 */
	public CompareMetaData() {
		super();
	}
	
	
	
	/**
	 * @Ganesh
	 *
	 * @param sourceAttribute
	 * @param sourceType
	 * @param sourceLength
	 * @param targetAttribute
	 * @param targetType
	 * @param targetLength
	 * @param status
	 */
	public CompareMetaData(String sourceAttribute, String sourceType, String sourceLength, String targetAttribute, String targetType,
			String targetLength, String status) {
		super();
		this.sourceAttribute = sourceAttribute;
		this.sourceType = sourceType;
		this.sourceLength = sourceLength;
		this.targetAttribute = targetAttribute;
		this.targetType = targetType;
		this.targetLength = targetLength;
		this.status = status;
	}

	/**
	 * @param sourceAttribute
	 * @param sourceType
	 * @param sourceLength
	 * @param targetAttribute
	 * @param targetType
	 * @param targetLength
	 * @param status
	 * @param sourcePrecision
	 * @param targetPrecision
	 */
	public CompareMetaData(String sourceAttribute, String sourceType, String sourceLength, String targetAttribute,
			String targetType, String targetLength, String status, String sourcePrecision, String targetPrecision) {
		super();
		this.sourceAttribute = sourceAttribute;
		this.sourceType = sourceType;
		this.sourceLength = sourceLength;
		this.targetAttribute = targetAttribute;
		this.targetType = targetType;
		this.targetLength = targetLength;
		this.status = status;
		this.sourcePrecision = sourcePrecision;
		this.targetPrecision = targetPrecision;
	}

	/**
	 * @Ganesh
	 *
	 * @return the sourceAttribute
	 */
	public String getSourceAttribute() {
		return sourceAttribute;
	}
	/**
	 * @Ganesh
	 *
	 * @param sourceAttribute the sourceAttribute to set
	 */
	public void setSourceAttribute(String sourceAttribute) {
		this.sourceAttribute = sourceAttribute;
	}
	/**
	 * @Ganesh
	 *
	 * @return the sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}
	/**
	 * @Ganesh
	 *
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	/**
	 * @Ganesh
	 *
	 * @return the sourceLength
	 */
	public String getSourceLength() {
		return sourceLength;
	}
	/**
	 * @Ganesh
	 *
	 * @param sourceLength the sourceLength to set
	 */
	public void setSourceLength(String sourceLength) {
		this.sourceLength = sourceLength;
	}
	/**
	 * @Ganesh
	 *
	 * @return the targetAttribute
	 */
	public String getTargetAttribute() {
		return targetAttribute;
	}
	/**
	 * @Ganesh
	 *
	 * @param targetAttribute the targetAttribute to set
	 */
	public void setTargetAttribute(String targetAttribute) {
		this.targetAttribute = targetAttribute;
	}
	/**
	 * @Ganesh
	 *
	 * @return the targetType
	 */
	public String getTargetType() {
		return targetType;
	}
	/**
	 * @Ganesh
	 *
	 * @param targetType the targetType to set
	 */
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	/**
	 * @Ganesh
	 *
	 * @return the targetLength
	 */
	public String getTargetLength() {
		return targetLength;
	}
	/**
	 * @Ganesh
	 *
	 * @param targetLength the targetLength to set
	 */
	public void setTargetLength(String targetLength) {
		this.targetLength = targetLength;
	}
	/**
	 * @Ganesh
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @Ganesh
	 *
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}



	/**
	 * @Ganesh
	 *
	 * @return the sourcePrecision
	 */
	public String getSourcePrecision() {
		return sourcePrecision;
	}



	/**
	 * @Ganesh
	 *
	 * @param sourcePrecision the sourcePrecision to set
	 */
	public void setSourcePrecision(String sourcePrecision) {
		this.sourcePrecision = sourcePrecision;
	}



	/**
	 * @Ganesh
	 *
	 * @return the targetPrecision
	 */
	public String getTargetPrecision() {
		return targetPrecision;
	}



	/**
	 * @Ganesh
	 *
	 * @param targetPrecision the targetPrecision to set
	 */
	public void setTargetPrecision(String targetPrecision) {
		this.targetPrecision = targetPrecision;
	}
}
