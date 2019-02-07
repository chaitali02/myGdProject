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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.inferyx.framework.enums.EncodingType;

/** 
 * @author Ganesh
 *
 */
public class TrainInput {
	String sourceFilePath;
	String modelFilePath;	
	String optimizationAlgo;
	int numInput;
	int numOutputs;
	String activation;
	String tableName;
	String operation;
	String url;
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
//	String testSetPath;
	List<String> rowIdentifier;
	String includeFeatures;
	double trainPercent;
	double testPercent;
	Map<String, EncodingType> encodingDetails;
	LinkedHashMap<String, Object> imputationDetails;
//	String saveTrainingSet;
	java.util.Map<String, String> trainSetDetails;
	java.util.Map<String, String> testSetDetails;
	
	/**
	 * @Ganesh
	 *
	 * @return the trainSetDetails
	 */
	public java.util.Map<String, String> getTrainSetDetails() {
		return trainSetDetails;
	}
	/**
	 * @Ganesh
	 *
	 * @param trainSetDetails the trainSetDetails to set
	 */
	public void setTrainSetDetails(java.util.Map<String, String> trainSetDetails) {
		this.trainSetDetails = trainSetDetails;
	}
	/**
	 * @Ganesh
	 *
	 * @return the testSetDetails
	 */
	public java.util.Map<String, String> getTestSetDetails() {
		return testSetDetails;
	}
	/**
	 * @Ganesh
	 *
	 * @param testSetDetails the testSetDetails to set
	 */
	public void setTestSetDetails(java.util.Map<String, String> testSetDetails) {
		this.testSetDetails = testSetDetails;
	}
	/**
	 * @Ganesh
	 *
	 * @return the encodingDetails
	 */
	public Map<String, EncodingType> getEncodingDetails() {
		return encodingDetails;
	}
	/**
	 * @Ganesh
	 *
	 * @param encodingDetails the encodingDetails to set
	 */
	public void setEncodingDetails(Map<String, EncodingType> encodingDetails) {
		this.encodingDetails = encodingDetails;
	}
	/**
	 * @Ganesh
	 *
	 * @return the imputationDetails
	 */
	public LinkedHashMap<String, Object> getImputationDetails() {
		return imputationDetails;
	}
	/**
	 * @Ganesh
	 *
	 * @param imputationDetails the imputationDetails to set
	 */
	public void setImputationDetails(LinkedHashMap<String, Object> imputationDetails) {
		this.imputationDetails = imputationDetails;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the trainPercent
	 */
	public double getTrainPercent() {
		return this.trainPercent;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param trainPercent the trainPercent to set
	 */
	public void setTrainPercent(double trainPercent) {
		this.trainPercent = trainPercent;
	}
	/**
	 * 
	 * @ Ganesh
	 * *
	 * @return the testPercent
	 */
	public double getTestPercent() {
		return this.testPercent;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param testPercent the testPercent to set
	 */
	public void setTestPercent(double testPercent) {
		this.testPercent = testPercent;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the rowIdentifier
	 */
	public List<String> getRowIdentifier() {
		return this.rowIdentifier;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param rowIdentifier the rowIdentifier to set
	 */
	public void setRowIdentifier(List<String> rowIdentifier) {
		this.rowIdentifier = rowIdentifier;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @return the includeFeatures
	 */
	public String getIncludeFeatures() {
		return this.includeFeatures;
	}
	/**
	 * 
	 * @ Ganesh
	 * 
	 * @param includeFeatures the includeFeatures to set
	 */
	public void setIncludeFeatures(String includeFeatures) {
		this.includeFeatures = includeFeatures;
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
	 * @ Ganesh 
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TrainInput [sourceFilePath=" + sourceFilePath + ", modelFilePath=" + modelFilePath
				+ ", optimizationAlgo=" + optimizationAlgo + ", numInput=" + numInput + ", numOutputs=" + numOutputs
				+ ", activation=" + activation + ", tableName=" + tableName + ", operation=" + operation + ", url="
				+ url + ", hostName=" + hostName + ", dbName=" + dbName + ", userName=" + userName + ", password="
				+ password + ", port=" + port + ", query=" + query + ", otherParams=" + otherParams
				+ ", sourceDsDetails=" + sourceDsDetails + ", targetDsDetails=" + targetDsDetails + ", sourceDsType="
				+ sourceDsType + ", targetDsType=" + targetDsType + ", targetTableName=" + targetTableName
				+ ", rowIdentifier=" + rowIdentifier + ", includeFeatures=" + includeFeatures + ", trainPercent="
				+ trainPercent + ", testPercent=" + testPercent + ", encodingDetails=" + encodingDetails
				+ ", imputationDetails=" + imputationDetails + ", trainSetDetails=" + trainSetDetails
				+ ", testSetDetails=" + testSetDetails + "]";
	}
	
}
