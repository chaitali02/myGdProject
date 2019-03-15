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
import com.inferyx.framework.domain.Status;
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
		execute(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), execParams, (IngestGroupExec) baseExec, runMode);
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), MetaType.ingestgroup, MetaType.ingestgroupExec, MetaType.ingest, MetaType.ingestExec, 
				DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), null, null, runMode);
	}

	public IngestGroupExec create(String groupUuid, String groupVersion, ExecParams execParams, List<String> datapodList, 
			IngestGroupExec ingestGroupExec, 
			DagExec dagExec) throws Exception {
		return (IngestGroupExec) super.create(groupUuid, groupVersion, MetaType.ingestgroup, MetaType.ingestgroupExec, MetaType.ingest, MetaType.ingestExec, execParams, datapodList, ingestGroupExec, dagExec);
	}

	public IngestGroupExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
			List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		return (IngestGroupExec) super.parse(execUuid, execVersion, MetaType.ingestgroup, MetaType.ingestgroupExec, MetaType.ingest, MetaType.ingestExec, refKeyMap, datapodList, dagExec, runMode);
	}

	public MetaIdentifier execute(String groupUuid, String groupVersion, ExecParams execParams,
			IngestGroupExec ingestGroupExec, RunMode runMode) throws Exception {
		return super.execute(groupUuid, groupVersion, MetaType.ingestgroup, MetaType.ingestgroupExec, MetaType.ingest, MetaType.ingestExec, execParams, ingestGroupExec, runMode);
	}
	
	public void restart(String type, String uuid, String version, RunMode runMode) throws Exception {
		IngestGroupExec ingestGroupExec = (IngestGroupExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.ingestgroupExec.toString());
		execute(ingestGroupExec.getDependsOn().getRef().getUuid(),ingestGroupExec.getDependsOn().getRef().getVersion(),null,ingestGroupExec, runMode);
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.ingestgroupExec, MetaType.ingestExec);
	}
	
}
