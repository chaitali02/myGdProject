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

	
	/********************** UNUSED **********************/
	/*public Relation findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRelationDao.findOneById(appUuid, id);
		} else
			return iRelationDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public Relation getOneByUuidAndVersion(String uuid, String version) {

		return iRelationDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Relation findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iRelationDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iRelationDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/*public Relation update(Relation relation) throws IOException {
		relation.exportBaseProperty();
		Relation relationDet=iRelationDao.save(relation);
		registerService.createGraph();
		return relationDet;
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iRelationDao.exists(id);
	}*/


	public List<Relation> findRelationByDatapod(String datapodUUID) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		List<Relation> relationList = iRelationDao.findRelationByDatapod(appUuid, datapodUUID);
		return relationList;
	}

	/********************** UNUSED **********************/
	/*public List<Relation> test(String param1) {
		return iRelationDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public List<Relation> findAllLatestActive() {
		Aggregation relationAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Relation> relationResults = mongoTemplate.aggregate(relationAggr, "relation",
				Relation.class);
		List<Relation> relationList = relationResults.getMappedResults();

		// Fetch the relation details for each id
		List<Relation> result = new ArrayList<Relation>();
		for (Relation r : relationList) {
			Relation relationLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				relationLatest = iRelationDao.findOneByUuidAndVersion(appUuid, r.getId(), r.getVersion());
			} else {
				relationLatest = iRelationDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}

			if(relationLatest != null)
			{
			result.add(relationLatest);
			}
		}
		return result;
	}*/

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
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.sample.maxrows"));
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


	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Relation relation) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Relation relNew = new Relation();
		relNew.setName(relation.getName()+"_copy");
		relNew.setActive(relation.getActive());
		relNew.setDependsOn(relation.getDependsOn());
		relNew.setDesc(relation.getDesc());
		relNew.setRelationInfo(relation.getRelationInfo());
		relNew.setTags(relation.getTags());
		save(relNew);
		ref.setType(MetaType.relation);
		ref.setUuid(relNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/


}