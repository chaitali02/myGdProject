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
package com.inferyx.framework.enums;

/**
 * @author Ganesh
 *
 */
public enum FrequencyType {
	ONCE("once"), HOURLY("hourly"), DAILY("daily"), WEEKLY("weekly"), BIWEEKLY("biweekly"), MONTHLY("monthly"), QUARTERLY("quarterly"), YEARLY("yearly");
	
	private FrequencyType(String frequencyType) {
		FrequencyType.valueOf(frequencyType);
	}
	
	public FrequencyType getFrequencyType(String frequencyType) {
		return FrequencyType.valueOf(frequencyType);
	}
}
