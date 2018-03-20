/**
 *
 * @Author Ganesh
 *
 */
package com.inferyx.framework.domain;

/**
 * @author Ganesh
 *
 */
public class FeatureAttrMap {
	private String featureMapId;
	private FeatureRefHolder feature;
	private AttributeRefHolder attribute;
	/**
	 * @Ganesh
	 *
	 * @return the featureMapId
	 */
	public String getFeatureMapId() {
		return featureMapId;
	}
	/**
	 * @Ganesh
	 *
	 * @param featureMapId the featureMapId to set
	 */
	public void setFeatureMapId(String featureMapId) {
		this.featureMapId = featureMapId;
	}
	/**
	 * @Ganesh
	 *
	 * @return the feature
	 */
	public FeatureRefHolder getFeature() {
		return feature;
	}
	/**
	 * @Ganesh
	 *
	 * @param feature the feature to set
	 */
	public void setFeature(FeatureRefHolder feature) {
		this.feature = feature;
	}
	/**
	 * @Ganesh
	 *
	 * @return the attribute
	 */
	public AttributeRefHolder getAttribute() {
		return attribute;
	}
	/**
	 * @Ganesh
	 *
	 * @param attribute the attribute to set
	 */
	public void setAttribute(AttributeRefHolder attribute) {
		this.attribute = attribute;
	}
		
}
