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

import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.MetaType;

/**
 * @author Ganesh
 *
 */
@Service
public class ReconGroupExecServiceImpl extends BaseGroupExecTemplate {
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.recongroupExec, MetaType.reconExec);
	}
}
