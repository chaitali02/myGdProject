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

/**
 * @author Ganesh
 *
 */
@Component
public class ReconInfo {
	private String reconTargetUUID;

	/**
	 * @Ganesh
	 *
	 * @return the reconTargetUUID
	 */
	public String getReconTargetUUID() {
		return reconTargetUUID;
	}

	/**
	 * @Ganesh
	 *
	 * @param reconTargetUUID the reconTargetUUID to set
	 */
	public void setReconTargetUUID(String reconTargetUUID) {
		this.reconTargetUUID = reconTargetUUID;
	}

}
