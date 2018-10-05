/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.Map;
import java.util.Properties;

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
	private Properties props;

	/**
	 * @return the props
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}

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
