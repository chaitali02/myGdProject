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

@Document(collection="algorithm")
public class Algorithm extends BaseEntity {

	private String type; // clustering, regression
	private String library; //sparkML, R, tensorflow
	private String trainName; //org.apache.spark.ml.classification.LogisticRegression;
	private String modelName; //org.apache.spark.ml.classification.LogisticRegressionModel;
	private String labelRequired;
	private String returnType; 
	private MetaIdentifierHolder paramList;
	private String savePmml;

	/**
	 * @Ganesh
	 *
	 * @return the savePmml
	 */
	public String getSavePmml() {
		return savePmml;
	}
	/**
	 * @Ganesh
	 *
	 * @param savePmml the savePmml to set
	 */
	public void setSavePmml(String savePmml) {
		this.savePmml = savePmml;
	}
	public String getLabelRequired() {
		return labelRequired;
	}
	public void setLabelRequired(String labelRequired) {
		this.labelRequired = labelRequired;
	}
	public String getTrainName() {
		return trainName;
	}
	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLibrary() {
		return library;
	}
	public void setLibrary(String library) {
		this.library = library;
	}

	public MetaIdentifierHolder getParamList() {
		return paramList;
	}
	public void setParamList(MetaIdentifierHolder paramInfo) {
		this.paramList = paramInfo;
	}
}
