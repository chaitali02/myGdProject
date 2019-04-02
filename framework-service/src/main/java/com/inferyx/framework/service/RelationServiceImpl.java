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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IRelationDao;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.RelationOperator;
import com.inferyx.framework.register.GraphRegister;

@Service
public class RelationServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	IRelationDao iRelationDao;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;
    @Autowired
    private RelationOperator relationOperator;
	@Autowired
	private ExecutorFactory execFactory;
	
	static final Logger logger = Logger.getLogger(RelationServiceImpl.class);

	public List<Relation> findRelationByDatapod(String datapodUUID) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		List<Relation> relationList = iRelationDao.findRelationByDatapod(appUuid, datapodUUID);
		return relationList;
	}

	

	public List<Datapod> findDatapodByRelation(String relationUuid,String version) throws JSONException, JsonProcessingException {
		List<String> datapodUuid = new ArrayList<String>();
		List<Datapod> finalDatapodList = new ArrayList<Datapod>();
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		Relation relationDetails = null;
		if(version.equals(null) || version.isEmpty() || version.equals(""))	{
		/*if (appUuid != null) {			
			relationDetails = iRelationDao.findLatestByUuid(appUuid, relationUuid,
					new Sort(Sort.Direction.DESC, "version"));
		} else {
			relationDetails = iRelationDao.findLatestByUuid(relationUuid, new Sort(Sort.Direction.DESC, "version"));
		}*/
			relationDetails = (Relation) commonServiceImpl.getLatestByUuid(relationUuid, MetaType.relation.toString());
		}else{
			/*if (appUuid != null) {			
				relationDetails = iRelationDao.findOneByUuidAndVersion(appUuid, relationUuid, version);
			} else {
				relationDetails = iRelationDao.findOneByUuidAndVersion(relationUuid, version);
			}*/
			relationDetails = (Relation) commonServiceImpl.getOneByUuidAndVersion(relationUuid, version, MetaType.relation.toString());
		}
		String sourceUUId = relationDetails.getDependsOn().getRef().getUuid();
		datapodUuid.add(sourceUUId);
		if (relationDetails.getRelationInfo().size() > 0) {
			for (int i = 0; i < relationDetails.getRelationInfo().size(); i++) {
				String joinInfoUUId = relationDetails.getRelationInfo().get(i).getJoin().getRef().getUuid();
				datapodUuid.add(joinInfoUUId);
			}
		}
		logger.info("datapoduuid : " + datapodUuid);
		for (int j = 0; j < datapodUuid.size(); j++) {
			//Datapod datapod = datapodServiceImpl.findLatestByUuid(datapodUuid.get(j));
			Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(datapodUuid.get(j), MetaType.datapod.toString());
			finalDatapodList.add(datapod);
		}
		return finalDatapodList;
	}

	public List<Map<String, Object>> getSample(String relUuid, String relVersion, int rows, ExecParams execParams,
			RunMode runMode) throws FileNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		long startTime  = System.currentTimeMillis();
		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.sample.maxrows"));
		if(rows > maxRows) {
			logger.error("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.relation, relUuid, relVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Number of rows "+rows+" exceeded. Max row allow "+maxRows, dependsOn);
			throw new RuntimeException("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
		}
		
		Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(relUuid, relVersion, MetaType.relation.toString());
		
		HashMap<String, String> otherParams = null;
		if(execParams != null) {
			otherParams = execParams.getOtherParams();
		}
		
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		Datasource relDs = commonServiceImpl.getDatasourceByObject(relation);
		try {
			String sql = relationOperator.generateSampleDataSql(relation, null, otherParams, execParams, new HashSet<>(), rows, runMode);
			List<Map<String, Object>> data = exec.executeAndFetchByDatasource(sql, relDs, commonServiceImpl.getApp().getUuid());	
			logger.info("Time elapsed in getSample : " + (System.currentTimeMillis() - startTime)/1000 + " s");
			return data;	
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), message != null ? message : "No data found for relation "+relation.getName()+".", new MetaIdentifierHolder(new MetaIdentifier(MetaType.relation, relUuid, relVersion)));
			throw new RuntimeException(message != null ? message : "No data found for relation "+relation.getName()+".");
		}
	}

}