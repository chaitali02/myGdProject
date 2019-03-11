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
public class DQInfo {
	private String dqTargetUUID;
	private String dq_result_detail;
	private String dq_result_summary;
	
	public String getDq_result_detail() {
		return dq_result_detail;
	}

	public void setDq_result_detail(String dq_result_detail) {
		this.dq_result_detail = dq_result_detail;
	}

	public String getDq_result_summary() {
		return dq_result_summary;
	}

	public void setDq_result_summary(String dq_result_summary) {
		this.dq_result_summary = dq_result_summary;
	}

	public String getDqTargetUUID() {
		return dqTargetUUID;
	}

	public void setDqTargetUUID(String dqTargetUUID) {
		this.dqTargetUUID = dqTargetUUID;
	}

}
