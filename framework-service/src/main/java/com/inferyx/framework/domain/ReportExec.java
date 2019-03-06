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

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="reportexec")
public class ReportExec extends BaseRuleExec {
	private long numRows;
	private double sizeMB;
	
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
