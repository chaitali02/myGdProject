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

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "simulate")
public class Simulate extends BaseEntity {

	private MetaIdentifierHolder dependsOn;
	private MetaIdentifierHolder target;
    private int numIterations;
	private List<FeatureRefHolder> featureInfo  = new ArrayList<FeatureRefHolder>();
	private MetaIdentifierHolder distributionTypeInfo;
	private MetaIdentifierHolder source;
	
   	/**
	 * @Ganesh
	 *
	 * @return the distDataset
	 */


	/**
	 * @Ganesh
	 *
	 * @return the distributionTypeInfo
	 */
	public MetaIdentifierHolder getDistributionTypeInfo() {
		return distributionTypeInfo;
	}

	public MetaIdentifierHolder getSource() {
		return source;
	}

	public void setSource(MetaIdentifierHolder source) {
		this.source = source;
	}

	/**
	 * @Ganesh
	 *
	 * @param distributionTypeInfo the distributionTypeInfo to set
	 */
	public void setDistributionTypeInfo(MetaIdentifierHolder distributionTypeInfo) {
		this.distributionTypeInfo = distributionTypeInfo;
	}

	public int getNumIterations() {
		return numIterations;
	}

	public void setNumIterations(int numIterations) {
		this.numIterations = numIterations;
	}

	public MetaIdentifierHolder getTarget() {
		return target;
	}

	public void setTarget(MetaIdentifierHolder target) {
		this.target = target;
	}

	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
	public List<FeatureRefHolder> getFeatureInfo() {
		return featureInfo;
	}

	public void setFeatureInfo(List<FeatureRefHolder> featureInfo) {
		this.featureInfo = featureInfo;
	}

}
