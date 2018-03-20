package com.inferyx.framework.domain;

public class FeatureRefHolder extends MetaIdentifierHolder {
	private String featureId;
	private String featureName;
	/**
	 * @Ganesh
	 *
	 * @return the featureId
	 */
	public String getFeatureId() {
		return featureId;
	}
	/**
	 * @Ganesh
	 *
	 * @param featureId the featureId to set
	 */
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	/**
	 * @Ganesh
	 *
	 * @return the featureName
	 */
	public String getFeatureName() {
		return featureName;
	}
	/**
	 * @Ganesh
	 *
	 * @param featureName the featureName to set
	 */
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	
}