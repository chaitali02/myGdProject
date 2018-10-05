/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.Map;

/**
 * @author joy
 *
 */
public class StreamInput<T, K> {

	/**
	 * 
	 */
	public StreamInput() {
		// TODO Auto-generated constructor stub
	}
	
	private Map<String, Object> runParams;

	/**
	 * @return the runParams
	 */
	public Map<String, Object> getRunParams() {
		return runParams;
	}

	/**
	 * @param runParams the runParams to set
	 */
	public void setRunParams(Map<String, Object> runParams) {
		this.runParams = runParams;
	}
	

}
