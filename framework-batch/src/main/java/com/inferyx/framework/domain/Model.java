package com.inferyx.framework.domain;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="model")
public class Model extends BaseEntity {

	private MetaIdentifierHolder source;
	private List<AttributeRefHolder> features = new ArrayList<AttributeRefHolder>();
	private AttributeRefHolder label = new AttributeRefHolder();
	private MetaIdentifierHolder algorithm;
	private MetaIdentifierHolder paramList;
	private String labelRequired;
	private String type;
	private Binary customScript;
	private String scriptName;
	
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
	/**
	 * @Ganesh
	 *
	 * @return the customScript
	 */
	public Binary getCustomScript() {
		return customScript;
	}
	/**
	 * @Ganesh
	 *
	 * @param customScript the customScript to set
	 */
	public void setCustomScript(Binary customScript) {
		this.customScript = customScript;
	}
	public String getLabelRequired() {
		return labelRequired;
	}
	public void setLabelRequired(String labelRequired) {
		this.labelRequired = labelRequired;
	}
	public AttributeRefHolder getLabel() {
		return label;
	}
	public void setLabel(AttributeRefHolder label) {
		this.label = label;
	}
	public List<AttributeRefHolder> getFeatures() {
		return features;
	}
	public void setFeatures(List<AttributeRefHolder> features) {
		this.features = features;
	}
	public MetaIdentifierHolder getSource() {
		return source;
	}
	public void setSource(MetaIdentifierHolder source) {
		this.source = source;
	}
	public MetaIdentifierHolder getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(MetaIdentifierHolder algorithm) {
		this.algorithm = algorithm;
	}
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}
	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}
}
