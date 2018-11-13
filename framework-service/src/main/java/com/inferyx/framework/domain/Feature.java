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


public class Feature {
	private String featureId;
	private String featureDisplaySeq;
	public String getFeatureDisplaySeq() {
		return featureDisplaySeq;
	}
	public void setFeatureDisplaySeq(String featureDisplaySeq) {
		this.featureDisplaySeq = featureDisplaySeq;
	}
	private String name;
	private String type;
	private String desc;
	private int minVal;
	private int maxVal;
	private ParamListHolder paramListInfo;
	
	/**
	 * @Ganesh
	 *
	 * @return the paramListInfo
	 */
	public ParamListHolder getParamListInfo() {
		return paramListInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param paramListInfo the paramListInfo to set
	 */
	public void setParamListInfo(ParamListHolder paramListInfo) {
		this.paramListInfo = paramListInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @return the featureId
	 */
	public String getFeatureId() {
		return featureId;
	}
	/**
	 * @Ganesh
	 *
	 * @param featureId the featureId to set
	 */
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	/**
	 * @Ganesh
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @Ganesh
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @Ganesh
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @Ganesh
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @Ganesh
	 *
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * @Ganesh
	 *
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	/**
	 * @Ganesh
	 *
	 * @return the minVal
	 */
	public int getMinVal() {
		return minVal;
	}
	/**
	 * @Ganesh
	 *
	 * @param minVal the minVal to set
	 */
	public void setMinVal(int minVal) {
		this.minVal = minVal;
	}
	/**
	 * @Ganesh
	 *
	 * @return the maxVal
	 */
	public int getMaxVal() {
		return maxVal;
	}
	/**
	 * @Ganesh
	 *
	 * @param maxVal the maxVal to set
	 */
	public void setMaxVal(int maxVal) {
		this.maxVal = maxVal;
	}
	
}