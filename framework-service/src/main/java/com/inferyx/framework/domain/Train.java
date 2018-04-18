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

@Document(collection = "train")
public class Train extends BaseEntity {
	private MetaIdentifierHolder dependsOn;
	private MetaIdentifierHolder source;
	private List<FeatureAttrMap> featureAttrMap;
	private double trainPercent;
	private double valPercent;

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

	public List<FeatureAttrMap> getFeatureAttrMap() {
		return featureAttrMap;
	}

	public void setFeatureAttrMap(List<FeatureAttrMap> featureAttrMap) {
		this.featureAttrMap = featureAttrMap;
	}

	public double getTrainPercent() {
		return trainPercent;
	}

	public void setTrainPercent(double trainPercent) {
		this.trainPercent = trainPercent;
	}

	public double getValPercent() {
		return valPercent;
	}

	public void setValPercent(double valPercent) {
		this.valPercent = valPercent;
	}

}
