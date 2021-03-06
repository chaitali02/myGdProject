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

import org.apache.log4j.Logger;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;

@Service
public class DataQualGroupServiceImpl extends RuleGroupTemplate {	
	
	static final Logger logger = Logger.getLogger(DataQualGroupServiceImpl.class);
	
	
	public DataQualGroupExec create(String dataQualGroupUUID, 
										String dataQualGroupVersion, 
										ExecParams execParams,
										List<String> datapodList, 
										DataQualGroupExec dataQualGroupExec, 
										DagExec dagExec, RunMode runmode) throws Exception {

		return (DataQualGroupExec) super.create(dataQualGroupUUID, dataQualGroupVersion, MetaType.dqgroup, MetaType.dqgroupExec, MetaType.dq, MetaType.dqExec, execParams, datapodList, dataQualGroupExec, dagExec ,runmode); 
	}
	
	public DataQualGroupExec parse(MetaIdentifier dqGroupExec, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		DataQualGroupExec dataQualGroupExec=null;
		try {
			dataQualGroupExec=(DataQualGroupExec) commonServiceImpl.getOneByUuidAndVersion(dqGroupExec.getUuid(), dqGroupExec.getVersion(), MetaType.dqgroupExec.toString());
			return (DataQualGroupExec) super.parse(dqGroupExec.getUuid(), dqGroupExec.getVersion(), MetaType.dqgroup, MetaType.dqgroupExec, MetaType.dq, MetaType.dqExec, refKeyMap, otherParams, datapodList, dagExec, runMode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
				commonServiceImpl.setMetaStatus(dataQualGroupExec, MetaType.dqgroupExec, Status.Stage.FAILED);
				e.printStackTrace();
		}
		return null;
	}
	
	public MetaIdentifier execute(String dataQualGroupUUID, String dataQualGroupVersion, ExecParams execParams, DataQualGroupExec dataQualGroupExec, RunMode runMode) throws Exception {
				return super.execute(dataQualGroupUUID, dataQualGroupVersion, MetaType.dqgroup, MetaType.dqgroupExec, MetaType.dq, MetaType.dqExec, execParams, dataQualGroupExec, runMode);
	}	

	
	
	public void restart(String type,String uuid,String version, RunMode runMode) throws Exception{
		//DataQualGroupExec dataQualGroupExec= dataQualGroupExecServiceImpl.findOneByUuidAndVersion(uuid,version);
		DataQualGroupExec dataQualGroupExec = (DataQualGroupExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dqgroupExec.toString());
		//dataQualGroupExec = create(dataQualGroupExec.getDependsOn().getRef().getUuid(),dataQualGroupExec.getDependsOn().getRef().getVersion(), null, null, dataQualGroupExec, null);
		dataQualGroupExec = parse(dataQualGroupExec.getRef(MetaType.dqgroupExec), null, null, null, null, runMode);
		execute(dataQualGroupExec.getDependsOn().getRef().getUuid(),dataQualGroupExec.getDependsOn().getRef().getVersion(),null,dataQualGroupExec, runMode);
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.dqgroupExec, MetaType.dqExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	/**
	 * Override Executable.execute()
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), execParams, (DataQualGroupExec) baseExec, runMode);
		return null;
	}

	/**
	 * Override Parsable.parse()
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), MetaType.dqgroup, MetaType.dqgroupExec, MetaType.dq, MetaType.dqExec, 
				DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), null, null, null, runMode);
	}
	
	
	
}
