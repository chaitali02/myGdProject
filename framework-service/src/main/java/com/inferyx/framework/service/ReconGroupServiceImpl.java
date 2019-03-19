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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;

/**
 * @author Ganesh
 *
 */
@Service
public class ReconGroupServiceImpl extends RuleGroupTemplate {

	public Object getMetaIdByExecId(String execUuid, String execVersion) throws JsonProcessingException {
		ReconGroupExec reconGroupExec = (ReconGroupExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.recongroupExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.recongroup);
		mi.setUuid(reconGroupExec.getDependsOn().getRef().getUuid());
		mi.setVersion(reconGroupExec.getDependsOn().getRef().getVersion());
		return mi;
	}

	public void restart(String type, String uuid, String version, RunMode runMode) throws Exception {
		ReconGroupExec reconGroupExec = (ReconGroupExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.recongroupExec.toString());
		reconGroupExec = parse(reconGroupExec.getRef(MetaType.recongroupExec), null, null, null, null, runMode);
		execute(reconGroupExec.getDependsOn().getRef().getUuid(),reconGroupExec.getDependsOn().getRef().getVersion(),null,reconGroupExec, runMode);
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.recongroupExec, MetaType.reconExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	public ReconGroupExec parse(MetaIdentifier reconGroupExec, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode) {
		try {
			return (ReconGroupExec) super.parse(reconGroupExec.getUuid(), reconGroupExec.getVersion(), MetaType.recongroup, MetaType.recongroupExec, MetaType.recon, MetaType.reconExec, refKeyMap, otherParams, datapodList, dagExec, runMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public MetaIdentifier execute(String reconGroupUUID, String reconGroupVersion, ExecParams execParams, ReconGroupExec reconGroupExec, RunMode runMode) throws Exception {
		return super.execute(reconGroupUUID, reconGroupVersion, MetaType.recongroup, MetaType.recongroupExec, MetaType.recon, MetaType.reconExec, execParams, reconGroupExec, runMode);
	}
	
	public ReconGroupExec create(String reconGroupUUID, String reconGroupVersion, ExecParams execParams,
			List<String> datapodList, ReconGroupExec reconGroupExec, DagExec dagExec) throws Exception {

		return (ReconGroupExec) super.create(reconGroupUUID, reconGroupVersion, MetaType.recongroup, MetaType.recongroupExec,
				MetaType.recon, MetaType.reconExec, execParams, datapodList, reconGroupExec, dagExec);
	}

	public ReconGroupExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		return (ReconGroupExec) super.parse(execUuid, execVersion, MetaType.recongroup, MetaType.recongroupExec,
				MetaType.recon, MetaType.reconExec, refKeyMap, otherParams, datapodList, dagExec, runMode);
	}
	
	/**
	 * Override Executable.execute()
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), execParams, (ReconGroupExec) baseExec, runMode);
		return null;
	}

	/**
	 * Override Parsable.parse()
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), MetaType.recongroup, MetaType.recongroupExec, MetaType.recon, MetaType.reconExec, 
				DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), null, null, null, runMode);
	}


}
