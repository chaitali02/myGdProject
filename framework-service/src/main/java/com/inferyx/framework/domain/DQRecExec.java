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
@Document(collection = "dqrecexec")
public class DQRecExec extends BaseRuleExec {
	private List<DQIntelligence> intelligenceResult;
	private String samplePercent;

	/**
	 * @Ganesh
	 *
	 * @return the intelligenceResult
	 */
	public List<DQIntelligence> getIntelligenceResult() {
		return intelligenceResult;
	}

	/**
	 * @Ganesh
	 *
	 * @param intelligenceResult the intelligenceResult to set
	 */
	public void setIntelligenceResult(List<DQIntelligence> intelligenceResult) {
		this.intelligenceResult = intelligenceResult;
	}

	/**
	 * @Ganesh
	 *
	 * @return the samplePercent
	 */
	public String getSamplePercent() {
		return samplePercent;
	}

	/**
	 * @Ganesh
	 *
	 * @param samplePercent the samplePercent to set
	 */
	public void setSamplePercent(String samplePercent) {
		this.samplePercent = samplePercent;
	}
}
