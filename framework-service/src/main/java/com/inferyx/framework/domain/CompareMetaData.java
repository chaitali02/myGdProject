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
	private String lmAttribute;
	private String lmType;
	private String lmLength;
	private String smAttribute;
	private String smType;
	private String smLength;
	private String status;
	
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
	 * @param lmAttribute
	 * @param lmType
	 * @param lmLength
	 * @param smAttribute
	 * @param smType
	 * @param smLength
	 * @param status
	 */
	public CompareMetaData(String lmAttribute, String lmType, String lmLength, String smAttribute, String smType,
			String smLength, String status) {
		super();
		this.lmAttribute = lmAttribute;
		this.lmType = lmType;
		this.lmLength = lmLength;
		this.smAttribute = smAttribute;
		this.smType = smType;
		this.smLength = smLength;
		this.status = status;
	}



	/**
	 * @Ganesh
	 *
	 * @return the lmAttribute
	 */
	public String getLmAttribute() {
		return lmAttribute;
	}
	/**
	 * @Ganesh
	 *
	 * @param lmAttribute the lmAttribute to set
	 */
	public void setLmAttribute(String lmAttribute) {
		this.lmAttribute = lmAttribute;
	}
	/**
	 * @Ganesh
	 *
	 * @return the lmType
	 */
	public String getLmType() {
		return lmType;
	}
	/**
	 * @Ganesh
	 *
	 * @param lmType the lmType to set
	 */
	public void setLmType(String lmType) {
		this.lmType = lmType;
	}
	/**
	 * @Ganesh
	 *
	 * @return the lmLength
	 */
	public String getLmLength() {
		return lmLength;
	}
	/**
	 * @Ganesh
	 *
	 * @param lmLength the lmLength to set
	 */
	public void setLmLength(String lmLength) {
		this.lmLength = lmLength;
	}
	/**
	 * @Ganesh
	 *
	 * @return the smAttribute
	 */
	public String getSmAttribute() {
		return smAttribute;
	}
	/**
	 * @Ganesh
	 *
	 * @param smAttribute the smAttribute to set
	 */
	public void setSmAttribute(String smAttribute) {
		this.smAttribute = smAttribute;
	}
	/**
	 * @Ganesh
	 *
	 * @return the smType
	 */
	public String getSmType() {
		return smType;
	}
	/**
	 * @Ganesh
	 *
	 * @param smType the smType to set
	 */
	public void setSmType(String smType) {
		this.smType = smType;
	}
	/**
	 * @Ganesh
	 *
	 * @return the smLength
	 */
	public String getSmLength() {
		return smLength;
	}
	/**
	 * @Ganesh
	 *
	 * @param smLength the smLength to set
	 */
	public void setSmLength(String smLength) {
		this.smLength = smLength;
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
}
