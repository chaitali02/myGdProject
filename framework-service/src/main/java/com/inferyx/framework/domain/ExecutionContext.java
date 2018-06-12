/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author joy
 *
 */
public class ExecutionContext extends HashMap<String, Object> {

	/**
	 * 
	 */
	public ExecutionContext() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param initialCapacity
	 */
	public ExecutionContext(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param m
	 */
	public ExecutionContext(Map<? extends String, ? extends Object> m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public ExecutionContext(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		// TODO Auto-generated constructor stub
	}

}
