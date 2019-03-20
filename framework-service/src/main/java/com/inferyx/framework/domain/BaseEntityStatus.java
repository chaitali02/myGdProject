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
package com.inferyx.framework.domain;

import java.util.List;

public class BaseEntityStatus extends BaseEntity{

	List<Status> status;
	String type;
	String execCreated;
	private long numRows;
	private double sizeMB;
	
	
	
	public String getExecCreated() {
		return execCreated;
	}

	public void setExecCreated(String execCreated) {
		this.execCreated = execCreated;
	}

	public BaseEntityStatus() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Status> getStatus() {
		return status;
	}

	public void setStatus(List<Status> status) {
		this.status = status;
	}
	/**
	 * @Ganesh
	 *
	 * @return the numRows
	 */
	public long getNumRows() {
		return numRows;
	}
	/**
	 * @Ganesh
	 *
	 * @param numRows the numRows to set
	 */
	public void setNumRows(long numRows) {
		this.numRows = numRows;
	}
	/**
	 * @Ganesh
	 *
	 * @return the sizeMB
	 */
	public double getSizeMB() {
		return sizeMB;
	}
	/**
	 * @Ganesh
	 *
	 * @param sizeMB the sizeMB to set
	 */
	public void setSizeMB(double sizeMB) {
		this.sizeMB = sizeMB;
	}
}
