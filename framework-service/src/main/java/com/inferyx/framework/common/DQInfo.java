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

	public String getDqTargetUUID() {
		return dqTargetUUID;
	}

	public void setDqTargetUUID(String dqTargetUUID) {
		this.dqTargetUUID = dqTargetUUID;
	}

}
