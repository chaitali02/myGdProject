/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
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
