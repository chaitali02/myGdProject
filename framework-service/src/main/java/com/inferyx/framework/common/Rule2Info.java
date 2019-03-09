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
package com.inferyx.framework.common;

import org.springframework.stereotype.Component;

@Component
public class Rule2Info {
	private String rule_result_details;

	private String rule_result_summary;

	public String getRule_result_details() {
		return rule_result_details;
	}

	public void setRule_result_details(String rule_result_details) {
		this.rule_result_details = rule_result_details;
	}

	public String getRule_result_summary() {
		return rule_result_summary;
	}

	public void setRule_result_summary(String rule_result_summary) {
		this.rule_result_summary = rule_result_summary;
	}

}
