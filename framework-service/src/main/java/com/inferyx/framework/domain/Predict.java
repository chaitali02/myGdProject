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

/**
 * @author Ganesh
 *
 */
@Document(collection = "predict")
public class Predict extends BaseEntity {
	private MetaIdentifierHolder dependsOn;
	private MetaIdentifierHolder source;
	private MetaIdentifierHolder target;
	private List<FeatureAttrMap> featureAttrMap;
//	private AttributeRefHolder labelInfo;
	private MetaIdentifierHolder trainInfo;
	private List<AttributeRefHolder> rowIdentifier;
	private String includeFeatures = "N";

	/**
	 *
	 * @Ganesh
	 *
	 * @return the rowIdentifier
	 */
	public List<AttributeRefHolder> getRowIdentifier() {
		return this.rowIdentifier;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param rowIdentifier the rowIdentifier to set
	 */
	public void setRowIdentifier(List<AttributeRefHolder> rowIdentifier) {
		this.rowIdentifier = rowIdentifier;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the includeFeatures
	 */
	public String getIncludeFeatures() {
		return this.includeFeatures;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param includeFeatures the includeFeatures to set
	 */
	public void setIncludeFeatures(String includeFeatures) {
		this.includeFeatures = includeFeatures;
	}

	/**
	 * @Ganesh
	 *
	 * @return the trainInfo
	 */
	public MetaIdentifierHolder getTrainInfo() {
		return trainInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param trainInfo the trainInfo to set
	 */
	public void setTrainInfo(MetaIdentifierHolder trainInfo) {
		this.trainInfo = trainInfo;
	}

//	/**
//	 * @Ganesh
//	 *
//	 * @return the labelInfo
//	 */
//	public AttributeRefHolder getLabelInfo() {
//		return labelInfo;
//	}
//
//	/**
//	 * @Ganesh
//	 *
//	 * @param labelInfo the labelInfo to set
//	 */
//	public void setLabelInfo(AttributeRefHolder labelInfo) {
//		this.labelInfo = labelInfo;
//	}

	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	public MetaIdentifierHolder getSource() {
		return source;
	}

	public void setSource(MetaIdentifierHolder source) {
		this.source = source;
	}

	public MetaIdentifierHolder getTarget() {
		return target;
	}

	public void setTarget(MetaIdentifierHolder target) {
		this.target = target;
	}

	public List<FeatureAttrMap> getFeatureAttrMap() {
		return featureAttrMap;
	}

	public void setFeatureAttrMap(List<FeatureAttrMap> featureAttrMap) {
		this.featureAttrMap = featureAttrMap;
	}

}
