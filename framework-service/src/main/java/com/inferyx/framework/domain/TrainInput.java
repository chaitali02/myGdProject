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
public class TrainInput {
	String sourceFilePath;
	String modelFilePath;	
	String optimizationAlgo;
	int weightInit;
	String updater;
	int numInput;
	int numOutputs;
	int numHidden;
	int numLayers;
	String activation;
	String lossFunction;
	String layerNames;
	String tableName;
	String operation;
	String url;
	String isSuccessful = "True";
	String hostName;
	String dbName;
	String userName;
	String password;
	String port;
	String query;	
	java.util.Map<String, Object> otherParams;
	java.util.Map<String, Object> sourceDsDetails;
	java.util.Map<String, Object> targetDsDetails;
	String sourceDsType;
	String targetDsType;
	String targetTableName;
	String testSetPath;
	
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the testSetPath
	 */
	public String getTestSetPath() {
		return this.testSetPath;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param testSetPath the testSetPath to set
	 */
	public void setTestSetPath(String testSetPath) {
		this.testSetPath = testSetPath;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the targetTableName
	 */
	public String getTargetTableName() {
		return this.targetTableName;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param targetTableName the targetTableName to set
	 */
	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}	
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the sourceDsType
	 */
	public String getSourceDsType() {
		return this.sourceDsType;
	}
	/**
	 * 
	 * @param sourceDsType the sourceDsType to set
	 */
	public void setSourceDsType(String sourceDsType) {
		this.sourceDsType = sourceDsType;
	}
	/**
	 * 
	 * @ Ganesh
	 *  
	 * @return the targetDsType
	 */
	public String getTargetDsType() {
		return this.targetDsType;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * 
	 * @param targetDsType the targetDsType to set
	 */
	public void setTargetDsType(String targetDsType) {
		this.targetDsType = targetDsType;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the sourceDsDetails
	 */
	public java.util.Map<String, Object> getSourceDsDetails() {
		return this.sourceDsDetails;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param sourceDsDetails the sourceDsDetails to set
	 */
	public void setSourceDsDetails(java.util.Map<String, Object> sourceDsDetails) {
		this.sourceDsDetails = sourceDsDetails;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the targetDsDetails
	 */
	public java.util.Map<String, Object> getTargetDsDetails() {
		return this.targetDsDetails;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param targetDsDetails the targetDsDetails to set
	 */
	public void setTargetDsDetails(java.util.Map<String, Object> targetDsDetails) {
		this.targetDsDetails = targetDsDetails;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the otherParams
	 */
	public java.util.Map<String, Object> getOtherParams() {
		return this.otherParams;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param otherParams the otherParams to set
	 */
	public void setOtherParams(java.util.Map<String, Object> otherParams) {
		this.otherParams = otherParams;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the sourceFilePath
	 */
	public String getSourceFilePath() {
		return this.sourceFilePath;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param sourceFilePath the sourceFilePath to set
	 */
	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the modelFilePath
	 */
	public String getModelFilePath() {
		return this.modelFilePath;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param modelFilePath the modelFilePath to set
	 */
	public void setModelFilePath(String modelFilePath) {
		this.modelFilePath = modelFilePath;
	}
	
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the optimizationAlgo
	 */
	public String getOptimizationAlgo() {
		return this.optimizationAlgo;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param optimizationAlgo the optimizationAlgo to set
	 */
	public void setOptimizationAlgo(String optimizationAlgo) {
		this.optimizationAlgo = optimizationAlgo;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the weightInit
	 */
	public int getWeightInit() {
		return this.weightInit;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param weightInit the weightInit to set
	 */
	public void setWeightInit(int weightInit) {
		this.weightInit = weightInit;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the updater
	 */
	public String getUpdater() {
		return this.updater;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param updater the updater to set
	 */
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the numInput
	 */
	public int getNumInput() {
		return this.numInput;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param numInput the numInput to set
	 */
	public void setNumInput(int numInput) {
		this.numInput = numInput;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the numOutputs
	 */
	public int getNumOutputs() {
		return this.numOutputs;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param numOutputs the numOutputs to set
	 */
	public void setNumOutputs(int numOutputs) {
		this.numOutputs = numOutputs;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the numHidden
	 */
	public int getNumHidden() {
		return this.numHidden;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param numHidden the numHidden to set
	 */
	public void setNumHidden(int numHidden) {
		this.numHidden = numHidden;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the numLayers
	 */
	public int getNumLayers() {
		return this.numLayers;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param numLayers the numLayers to set
	 */
	public void setNumLayers(int numLayers) {
		this.numLayers = numLayers;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the activation
	 */
	public String getActivation() {
		return this.activation;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param activation the activation to set
	 */
	public void setActivation(String activation) {
		this.activation = activation;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the lossFunction
	 */
	public String getLossFunction() {
		return this.lossFunction;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param lossFunction the lossFunction to set
	 */
	public void setLossFunction(String lossFunction) {
		this.lossFunction = lossFunction;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the layerNames
	 */
	public String getLayerNames() {
		return this.layerNames;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param layerNames the layerNames to set
	 */
	public void setLayerNames(String layerNames) {
		this.layerNames = layerNames;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the tableName
	 */
	public String getTableName() {
		return this.tableName;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the operation
	 */
	public String getOperation() {
		return this.operation;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the isSuccessful
	 */
	public String getIsSuccessful() {
		return this.isSuccessful;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param isSuccessful the isSuccessful to set
	 */
	public void setIsSuccessful(String isSuccessful) {
		this.isSuccessful = isSuccessful;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the hostName
	 */
	public String getHostName() {
		return this.hostName;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the dbName
	 */
	public String getDbName() {
		return this.dbName;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param dbName the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the port
	 */
	public String getPort() {
		return this.port;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the query
	 */
	public String getQuery() {
		return this.query;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	/*
	 * 
	 * @ Ganesh
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TrainInput [sourceFilePath=" + this.sourceFilePath + ", modelFilePath=" + this.modelFilePath
				+ ", optimizationAlgo=" + this.optimizationAlgo + ", weightInit=" + this.weightInit + ", updater="
				+ this.updater + ", numInput=" + this.numInput + ", numOutputs=" + this.numOutputs + ", numHidden="
				+ this.numHidden + ", numLayers=" + this.numLayers + ", activation=" + this.activation
				+ ", lossFunction=" + this.lossFunction + ", layerNames=" + this.layerNames + ", tableName="
				+ this.tableName + ", operation=" + this.operation + ", url=" + this.url + ", isSuccessful="
				+ this.isSuccessful + ", hostName=" + this.hostName + ", dbName=" + this.dbName + ", userName="
				+ this.userName + ", password=" + this.password + ", port=" + this.port + ", query=" + this.query
				+ ", otherParams=" + this.otherParams + ", sourceDsDetails=" + this.sourceDsDetails
				+ ", targetDsDetails=" + this.targetDsDetails + ", sourceDsType=" + this.sourceDsType
				+ ", targetDsType=" + this.targetDsType + ", targetTableName=" + this.targetTableName + "]";
	}
	
}
