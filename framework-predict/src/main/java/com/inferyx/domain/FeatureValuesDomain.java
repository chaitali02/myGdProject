/**
 * 
 */
package com.inferyx.domain;

import java.util.List;
import java.util.Map;

/**
 * @author joy
 *
 */
public class FeatureValuesDomain {
	
	private List<Map<String, String>> featureList;
	private List<String> predictionList;

	/**
	 * 
	 */
	public FeatureValuesDomain() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the featureList
	 */
	public List<Map<String, String>> getFeatureList() {
		return featureList;
	}

	/**
	 * @param featureList the featureList to set
	 */
	public void setFeatureList(List<Map<String, String>> featureList) {
		this.featureList = featureList;
	}

	/**
	 * @return the predictionList
	 */
	public List<String> getPredictionList() {
		return predictionList;
	}

	/**
	 * @param predictionList the predictionList to set
	 */
	public void setPredictionList(List<String> predictionList) {
		this.predictionList = predictionList;
	}
	
}
