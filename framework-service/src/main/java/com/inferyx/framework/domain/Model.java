package com.inferyx.framework.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="model")
public class Model extends BaseEntity {

	private String type;
	private MetaIdentifierHolder algorithm;
	private String label ;
	private List<Feature> features = new ArrayList<Feature>();
	private String scriptName;
	private double trainPercent;
	private double valPercent;

	private MetaIdentifierHolder source;
	
	/**
	 * @Ganesh
	 *
	 * @return the source
	 */
	public MetaIdentifierHolder getSource() {
		return source;
	}
	/**
	 * @Ganesh
	 *
	 * @param source the source to set
	 */
	public void setSource(MetaIdentifierHolder source) {
		this.source = source;
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
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @Ganesh
	 *
	 * @return the trainPercent
	 */
	public double getTrainPercent() {
		return trainPercent;
	}
	/**
	 * @Ganesh
	 *
	 * @param trainPercent the trainPercent to set
	 */
	public void setTrainPercent(double trainPercent) {
		this.trainPercent = trainPercent;
	}
	/**
	 * @Ganesh
	 *
	 * @return the valPercent
	 */
	public double getValPercent() {
		return valPercent;
	}
	/**
	 * @Ganesh
	 *
	 * @param valPercent the valPercent to set
	 */
	public void setValPercent(double valPercent) {
		this.valPercent = valPercent;
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
	 * @param scriptName the scriptName to set
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
	 * @param type the type to set
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
	
	public MetaIdentifierHolder getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(MetaIdentifierHolder algorithm) {
		this.algorithm = algorithm;
	}
}
