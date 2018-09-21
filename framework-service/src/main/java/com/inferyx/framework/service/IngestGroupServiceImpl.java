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

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.IngestGroupExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.enums.RunMode;

/**
 * @author Ganesh
 *
 */
@Service
public class IngestGroupServiceImpl extends RuleGroupTemplate {

	public Object getMetaIdByExecId(String execUuid, String execVersion) throws JsonProcessingException {
		IngestGroupExec ingestGroupExec = (IngestGroupExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.ingestgroupExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.ingestgroup);
		mi.setUuid(ingestGroupExec.getDependsOn().getRef().getUuid());
		mi.setVersion(ingestGroupExec.getDependsOn().getRef().getVersion());
		return mi;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
	

	
	




}
