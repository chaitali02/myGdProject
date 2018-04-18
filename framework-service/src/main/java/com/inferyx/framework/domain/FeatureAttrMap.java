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
public class FeatureAttrMap {
	private String featureMapId;
	private FeatureRefHolder feature; //source
	private AttributeRefHolder attribute; //target
	/**
	 * @Ganesh
	 *
	 * @return the featureMapId
	 */
	public String getFeatureMapId() {
		return featureMapId;
	}
	/**
	 * @Ganesh
	 *
	 * @param featureMapId the featureMapId to set
	 */
	public void setFeatureMapId(String featureMapId) {
		this.featureMapId = featureMapId;
	}
	/**
	 * @Ganesh
	 *
	 * @return the feature
	 */
	public FeatureRefHolder getFeature() {
		return feature;
	}
	/**
	 * @Ganesh
	 *
	 * @param feature the feature to set
	 */
	public void setFeature(FeatureRefHolder feature) {
		this.feature = feature;
	}
	/**
	 * @Ganesh
	 *
	 * @return the attribute
	 */
	public AttributeRefHolder getAttribute() {
		return attribute;
	}
	/**
	 * @Ganesh
	 *
	 * @param attribute the attribute to set
	 */
	public void setAttribute(AttributeRefHolder attribute) {
		this.attribute = attribute;
	}
		
}
