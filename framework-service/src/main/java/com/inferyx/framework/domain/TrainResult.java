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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ganesh
 *
 */
@Document(collection = "trainresult")
public class TrainResult extends BaseEntity {
	private MetaIdentifierHolder dependsOn;
	private MetaIdentifierHolder paramList;
	private Long totalRecords;
	private Long trainingSet;
	private Long validationSet;
	private String timeTaken;	
	private List<Double> featureImportance;
	private double accuracy;
	private double precision;
	private double recall;
	private double f1Score;
	private double costMatrixGain;
	private double logLoss;
	private List<Double> rocAUC;
	private double lift;
	private Object confusionMatrix;
	private String algorithm;
	private long numFeatures;
	private String algoType;
	private List<Map<String, Object>> rocCurve;
	private String trainClass;
	private Date startTime;
	private Date endTime;
	
	/**
	 * @Ganesh
	 *
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @Ganesh
	 *
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * @Ganesh
	 *
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}
	/**
	 * @Ganesh
	 *
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getTrainClass() {
		return trainClass;
	}
	public void setTrainClass(String trainClass) {
		this.trainClass = trainClass;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the algoType
	 */
	public String getAlgoType() {
		return algoType;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param algoType the algoType to set
	 */
	public void setAlgoType(String algoType) {
		this.algoType = algoType;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the numFeatures
	 */
	public long getNumFeatures() {
		return numFeatures;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param numFeatures the numFeatures to set
	 */
	public void setNumFeatures(long numFeatures) {
		this.numFeatures = numFeatures;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the confusionMatrix
	 */
	public Object getConfusionMatrix() {
		return confusionMatrix;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param confusionMatrix the confusionMatrix to set
	 */
	public void setConfusionMatrix(Object confusionMatrix) {
		this.confusionMatrix = confusionMatrix;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the dependsOn
	 */
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param dependsOn the dependsOn to set
	 */
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the paramList
	 */
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param paramList the paramList to set
	 */
	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the totalRecords
	 */
	public Long getTotalRecords() {
		return totalRecords;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param totalRecords the totalRecords to set
	 */
	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the trainingSet
	 */
	public Long getTrainingSet() {
		return trainingSet;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param trainingSet the trainingSet to set
	 */
	public void setTrainingSet(Long trainingSet) {
		this.trainingSet = trainingSet;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the validationSet
	 */
	public Long getValidationSet() {
		return validationSet;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param validationSet the validationSet to set
	 */
	public void setValidationSet(Long validationSet) {
		this.validationSet = validationSet;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the timeTaken
	 */
	public String getTimeTaken() {
		return timeTaken;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param timeTaken the timeTaken to set
	 */
	public void setTimeTaken(String timeTaken) {
		this.timeTaken = timeTaken;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the featureImportance
	 */
	public List<Double> getFeatureImportance() {
		return featureImportance;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param featureImportance the featureImportance to set
	 */
	public void setFeatureImportance(List<Double> featureImportance) {
		this.featureImportance = featureImportance;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the accuracy
	 */
	public double getAccuracy() {
		return accuracy;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param accuracy the accuracy to set
	 */
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the precision
	 */
	public double getPrecision() {
		return precision;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param precision the precision to set
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the recall
	 */
	public double getRecall() {
		return recall;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param recall the recall to set
	 */
	public void setRecall(double recall) {
		this.recall = recall;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the f1Score
	 */
	public double getF1Score() {
		return f1Score;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param f1Score the f1Score to set
	 */
	public void setF1Score(double f1Score) {
		this.f1Score = f1Score;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the costMatrixGain
	 */
	public double getCostMatrixGain() {
		return costMatrixGain;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param costMatrixGain the costMatrixGain to set
	 */
	public void setCostMatrixGain(double costMatrixGain) {
		this.costMatrixGain = costMatrixGain;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the logLoss
	 */
	public double getLogLoss() {
		return logLoss;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param logLoss the logLoss to set
	 */
	public void setLogLoss(double logLoss) {
		this.logLoss = logLoss;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the rocAUC
	 */
	public List<Double> getRocAUC() {
		return rocAUC;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param rocAUC the rocAUC to set
	 */
	public void setRocAUC(List<Double> rocAUC) {
		this.rocAUC = rocAUC;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the lift
	 */
	public double getLift() {
		return lift;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param lift the lift to set
	 */
	
	public void setLift(double lift) {
		this.lift = lift;
	}
	public List<Map<String, Object>> getRocCurve() {
		return rocCurve;
	}
	public void setRocCurve(List<Map<String, Object>> rocCurve) {
		this.rocCurve = rocCurve;
	}
}
