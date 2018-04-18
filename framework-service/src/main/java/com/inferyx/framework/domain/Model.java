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

@Document(collection = "model")
public class Model extends BaseEntity {

	private String type;
	private MetaIdentifierHolder dependsOn;
	private String label;
	private List<Feature> features = new ArrayList<Feature>();
	private String scriptName;
	private String customFlag;


	/**
	 * @Ganesh
	 *
	 * @return the dependsOn
	 */
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	/**
	 * @Ganesh
	 *
	 * @param dependsOn the dependsOn to set
	 */
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	/**
	 * @Ganesh
	 *
	 * @return the customFlag
	 */
	public String getCustomFlag() {
		return customFlag;
	}

	/**
	 * @Ganesh
	 *
	 * @param customFlag the customFlag to set
	 */
	public void setCustomFlag(String customFlag) {
		this.customFlag = customFlag;
	}

	/**
	 * @Ganesh
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @Ganesh
	 *
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @Ganesh
	 *
	 * @return the scriptName
	 */
	public String getScriptName() {
		return scriptName;
	}

	/**
	 * @Ganesh
	 *
	 * @param scriptName
	 *            the scriptName to set
	 */
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
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
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

}
