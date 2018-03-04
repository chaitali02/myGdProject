/**
 * 
 */
package com.inferyx.framework.service;

import com.inferyx.framework.domain.MetaIdentifier;

/**
 * @author joy
 *
 */
public class TaskHolder {
	
	/**
	 * Name of the futureTask
	 */
	private String name;
	/**
	 * Object that the FutureTask refers to
	 */
	private MetaIdentifier ref;

	/**
	 * 
	 */
	public TaskHolder() {
		// TODO Auto-generated constructor stub
	}
	
	public TaskHolder(String name, MetaIdentifier ref) {
		super();
		this.name = name;
		this.ref = ref;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ref
	 */
	public MetaIdentifier getRef() {
		return ref;
	}

	/**
	 * @param ref the ref to set
	 */
	public void setRef(MetaIdentifier ref) {
		this.ref = ref;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TaskHolder [name=" + name + ", ref=" + ref + "]";
	}
	
}
