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

@Document(collection="algorithm")
public class Algorithm extends BaseEntity {

	private String type; // clustering, regression, classification
	private String libraryType; //sparkML, R, tensorflow, H2O
	private String trainClass; //e.g. org.apache.spark.ml.classification.LogisticRegression;
	private String modelClass; //e.g. org.apache.spark.ml.classification.LogisticRegressionModel;
	private List<String> summaryMethods;
	private String labelRequired;
	private String returnType; 
	private String savePmml;
	private MetaIdentifierHolder paramListWoH;
	private MetaIdentifierHolder paramListWH;
	private String customFlag = "N";
	private String scriptName;

	/**
	 *
	 * @Ganesh
	 *
	 * @return the customFlag
	 */
	public String getCustomFlag() {
		return customFlag;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param customFlag the customFlag to set
	 */
	public void setCustomFlag(String customFlag) {
		this.customFlag = customFlag;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the scriptName
	 */
	public String getScriptName() {
		return scriptName;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param scriptName the scriptName to set
	 */
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
	/**
	 * @Ganesh
	 *
	 * @return the paramListWoH
	 */
	public MetaIdentifierHolder getParamListWoH() {
		return paramListWoH;
	}
	/**
	 * @Ganesh
	 *
	 * @param paramListWoH the paramListWoH to set
	 */
	public void setParamListWoH(MetaIdentifierHolder paramListWoH) {
		this.paramListWoH = paramListWoH;
	}
	/**
	 * @Ganesh
	 *
	 * @return the paramListWH
	 */
	public MetaIdentifierHolder getParamListWH() {
		return paramListWH;
	}
	/**
	 * @Ganesh
	 *
	 * @param paramListWH the paramListWH to set
	 */
	public void setParamListWH(MetaIdentifierHolder paramListWH) {
		this.paramListWH = paramListWH;
	}
	/**
	 * @Ganesh
	 *
	 * @return the libraryType
	 */
	public String getLibraryType() {
		return libraryType;
	}
	/**
	 * @Ganesh
	 *
	 * @param libraryType the libraryType to set
	 */
	public void setLibraryType(String libraryType) {
		this.libraryType = libraryType;
	}
	/**
	 * @Ganesh
	 *
	 * @return the trainClass
	 */
	public String getTrainClass() {
		return trainClass;
	}
	/**
	 * @Ganesh
	 *
	 * @param trainClass the trainClass to set
	 */
	public void setTrainClass(String trainClass) {
		this.trainClass = trainClass;
	}
	/**
	 * @Ganesh
	 *
	 * @return the modelClass
	 */
	public String getModelClass() {
		return modelClass;
	}
	/**
	 * @Ganesh
	 *
	 * @param modelClass the modelClass to set
	 */
	public void setModelClass(String modelClass) {
		this.modelClass = modelClass;
	}
	/**
	 * @Ganesh
	 *
	 * @return the summaryMethods
	 */
	public List<String> getSummaryMethods() {
		return summaryMethods;
	}
	/**
	 * @Ganesh
	 *
	 * @param summaryMethods the summaryMethods to set
	 */
	public void setSummaryMethods(List<String> summaryMethods) {
		this.summaryMethods = summaryMethods;
	}
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
}
