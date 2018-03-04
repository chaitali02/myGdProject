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
