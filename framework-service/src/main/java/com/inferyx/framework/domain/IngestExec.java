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

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ganesh
 *
 */
@Document(collection = "ingestexec")
public class IngestExec extends BaseRuleExec {
	private String lastIncrValue;
	private List<String> fileInfo;

	/**
	 *
	 * @Ganesh
	 *
	 * @return the fileInfo
	 */
	public List<String> getFileInfo() {
		return fileInfo;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param fileInfo the fileInfo to set
	 */
	public void setFileInfo(List<String> fileInfo) {
		this.fileInfo = fileInfo;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the lastIncrValue
	 */
	public String getLastIncrValue() {
		return lastIncrValue;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param lastIncrValue the lastIncrValue to set
	 */
	public void setLastIncrValue(String lastIncrValue) {
		this.lastIncrValue = lastIncrValue;
	}
	
}
