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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.dao.IVizpodDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DashboardExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.domain.Vizpod;
import com.inferyx.framework.domain.Vizpod.AttributeDetails;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.parser.VizpodDetailParser;
import com.inferyx.framework.parser.VizpodParser;
import com.inferyx.framework.register.GraphRegister;

@Service
public class VizpodServiceImpl extends RuleTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	IVizpodDao iVizpodDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	DagExecServiceImpl dagExecImpl;
	@Autowired
	private VizpodParser vizpodParser;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	ExecutorFactory execFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired 
	private SparkExecutor<?> sparkExecutor;
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private VizpodDetailParser vizpodDetailParser;
	
	Map<String, String> requestMap = new HashMap<String, String>();
		
	static final Logger logger = Logger.getLogger(VizpodServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Vizpod findLatest() {
		return resolveName(iVizpodDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/


	/********************** UNUSED **********************/
	/*public Vizpod findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iVizpodDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));
		}
		return iVizpodDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/


	public Vizpod resolveName(Vizpod vizpod) throws JsonProcessingException {
		if(vizpod.getCreatedBy() != null)
		{
		String createdByRefUuid = vizpod.getCreatedBy().getRef().getUuid();
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		vizpod.getCreatedBy().getRef().setName(user.getName());
		}
		if (vizpod.getAppInfo() != null) {
			for (int i = 0; i < vizpod.getAppInfo().size(); i++) {
				String appUuid = vizpod.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				vizpod.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		String sourceRefUuid = vizpod.getSource().getRef().getUuid();
		MetaType sourceType = vizpod.getSource().getRef().getType();

		if (sourceType.toString().equalsIgnoreCase(MetaType.datapod.toString())) {
			Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(sourceRefUuid, MetaType.datapod.toString());
			String datapodName = datapodDO.getName();
			vizpod.getSource().getRef().setName(datapodName);
		}
		if (sourceType.toString().equalsIgnoreCase(MetaType.relation.toString())) {
			Relation relationDO = (Relation) commonServiceImpl.getLatestByUuid(sourceRefUuid, MetaType.relation.toString());
			String relationName = relationDO.getName();
			vizpod.getSource().getRef().setName(relationName);
		}
		if (sourceType.toString().equalsIgnoreCase(MetaType.formula.toString())) {
			Formula formulaDO = (Formula) commonServiceImpl.getLatestByUuid(sourceRefUuid, MetaType.formula.toString());
			String formulaName = formulaDO.getName();
			vizpod.getSource().getRef().setName(formulaName);
		}

		for (int i = 0; i < vizpod.getKeys().size(); i++) {

			String keysRefUuid = vizpod.getKeys().get(i).getRef().getUuid();
			MetaType keyType = vizpod.getKeys().get(i).getRef().getType();

			if (keyType.toString().equalsIgnoreCase(MetaType.datapod.toString())) {
				//Datapod keysDatapodDO = datapodServiceImpl.findLatestByUuid(keysRefUuid);
				Datapod keysDatapodDO = (Datapod) commonServiceImpl.getLatestByUuid(keysRefUuid, MetaType.datapod.toString());
				String keysDatapodName = keysDatapodDO.getName();
				vizpod.getKeys().get(i).getRef().setName(keysDatapodName);
				Integer keysAttributeId = vizpod.getKeys().get(i).getAttributeId();
				List<Attribute> targetAttributeList = keysDatapodDO.getAttributes();
				vizpod.getKeys().get(i).setAttributeName(targetAttributeList.get(keysAttributeId).getName());
			}
			if (keyType.toString().equalsIgnoreCase(MetaType.relation.toString())) {
				//Relation relationDO = relationServiceImpl.findLatestByUuid(keysRefUuid);
				Relation relationDO = (Relation) commonServiceImpl.getLatestByUuid(keysRefUuid, MetaType.relation.toString());
				String relationName = relationDO.getName();
				vizpod.getKeys().get(i).getRef().setName(relationName);
			}
			if (keyType.toString().equalsIgnoreCase(MetaType.formula.toString())) {
				//Formula formulaDO = formulaServiceImpl.findLatestByUuid(keysRefUuid);
				Formula formulaDO = (Formula) commonServiceImpl.getLatestByUuid(keysRefUuid, MetaType.formula.toString());
				String formulaName = formulaDO.getName();
				vizpod.getKeys().get(i).getRef().setName(formulaName);
			}
			if (keyType.toString().equalsIgnoreCase(MetaType.expression.toString())) {
				//Expression expressionDO = expressionServiceImpl.findLatestByUuid(keysRefUuid);
				Expression expressionDO = (Expression) commonServiceImpl.getLatestByUuid(keysRefUuid, MetaType.expression.toString());
				String expressionName = expressionDO.getName();
				vizpod.getKeys().get(i).getRef().setName(expressionName);
			}
		}
		for (int j = 0; j < vizpod.getGroups().size(); j++) {
			String groupRefUuid = vizpod.getGroups().get(j).getRef().getUuid();
			MetaType groupType = vizpod.getGroups().get(j).getRef().getType();
			if (groupType.toString().equalsIgnoreCase(MetaType.datapod.toString())) {
				// Integer groupAttributeId =
				// vizpod.getGroups().get(j).getAttributeId();
				//Datapod groupDatapodDO = datapodServiceImpl.findLatestByUuid(groupRefUuid);
				Datapod groupDatapodDO = (Datapod) commonServiceImpl.getLatestByUuid(groupRefUuid, MetaType.datapod.toString());
				String groupDatapodName = groupDatapodDO.getName();
				vizpod.getGroups().get(j).getRef().setName(groupDatapodName);
				Integer groupAttributeId = vizpod.getGroups().get(j).getAttributeId();
				List<Attribute> targetAttributeList = groupDatapodDO.getAttributes();
				vizpod.getGroups().get(j).setAttributeName(targetAttributeList.get(groupAttributeId).getName());
			}
			if (groupType.toString().equalsIgnoreCase(MetaType.relation.toString())) {
				//Relation relationDO = relationServiceImpl.findLatestByUuid(groupRefUuid);
				Relation relationDO = (Relation) commonServiceImpl.getLatestByUuid(groupRefUuid, MetaType.relation.toString());
				String relationName = relationDO.getName();
				vizpod.getGroups().get(j).getRef().setName(relationName);
			}
			if (groupType.toString().equalsIgnoreCase(MetaType.formula.toString())) {
				//Formula formulaDO = formulaServiceImpl.findLatestByUuid(groupRefUuid);
				Formula formulaDO = (Formula) commonServiceImpl.getLatestByUuid(groupRefUuid, MetaType.formula.toString());
				String formulaName = formulaDO.getName();
				vizpod.getGroups().get(j).getRef().setName(formulaName);
			}
			if (groupType.toString().equalsIgnoreCase(MetaType.expression.toString())) {
				//Expression expressionDO = expressionServiceImpl.findLatestByUuid(groupRefUuid);
				Expression expressionDO = (Expression) commonServiceImpl.getLatestByUuid(groupRefUuid, MetaType.expression.toString());
				String expressionName = expressionDO.getName();
				vizpod.getGroups().get(j).getRef().setName(expressionName);
			}

		}
		for (int k = 0; k < vizpod.getValues().size(); k++) {
			String valuesRefUuid = vizpod.getValues().get(k).getRef().getUuid();
			MetaType valueType = vizpod.getValues().get(k).getRef().getType();
			if (valueType.toString().equalsIgnoreCase(MetaType.datapod.toString())) {
				//Datapod valuesDatapodDO = datapodServiceImpl.findLatestByUuid(valuesRefUuid);
				Datapod valuesDatapodDO = (Datapod) commonServiceImpl.getLatestByUuid(valuesRefUuid, MetaType.datapod.toString());
				String valueDatapodName = valuesDatapodDO.getName();
				vizpod.getValues().get(k).getRef().setName(valueDatapodName);
				Integer groupAttributeId = vizpod.getValues().get(k).getAttributeId();
				List<Attribute> targetAttributeList = valuesDatapodDO.getAttributes();
				vizpod.getValues().get(k).setAttributeName(targetAttributeList.get(groupAttributeId).getName());
			}
			if (valueType.toString().equalsIgnoreCase(MetaType.relation.toString())) {
				//Relation relationDO = relationServiceImpl.findLatestByUuid(valuesRefUuid);
				Relation relationDO = (Relation) commonServiceImpl.getLatestByUuid(valuesRefUuid, MetaType.relation.toString());
				String relationName = relationDO.getName();
				vizpod.getValues().get(k).getRef().setName(relationName);
			}
			if (valueType.toString().equalsIgnoreCase(MetaType.formula.toString())) {
				//Formula formulaDO = formulaServiceImpl.findLatestByUuid(valuesRefUuid);
				Formula formulaDO = (Formula) commonServiceImpl.getLatestByUuid(valuesRefUuid, MetaType.formula.toString());
				String formulaName = formulaDO.getName();
				vizpod.getValues().get(k).getRef().setName(formulaName);
			}
			if (valueType.toString().equalsIgnoreCase(MetaType.expression.toString())) {
				//Expression expressionDO = expressionServiceImpl.findLatestByUuid(valuesRefUuid);
				Expression expressionDO = (Expression) commonServiceImpl.getLatestByUuid(valuesRefUuid, MetaType.expression.toString());
				String expressionName = expressionDO.getName();
				vizpod.getValues().get(k).getRef().setName(expressionName);
			}
		}
		return vizpod;
	}

	public List<Vizpod> findVizpodByType(String uuid) throws JsonProcessingException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null
				&& securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		Aggregation formulaAggr = newAggregation(match(Criteria.where("source.ref.uuid").is(uuid)),
				group("uuid").max("version").as("version"));
		AggregationResults<Vizpod> vizpodResults = mongoTemplate.aggregate(formulaAggr, "vizpod", Vizpod.class);
		List<Vizpod> vizpodList = vizpodResults.getMappedResults();

		// Fetch formula details for each id
		List<Vizpod> result = new ArrayList<Vizpod>();
		for (Vizpod s : vizpodList) {
			//Vizpod vizpodLatest = iVizpodDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			Vizpod vizpodLatest = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.vizpod.toString());
			result.add(vizpodLatest);
		}
		return result;
	}

	/********************** UNUSED **********************/
	/*public Vizpod findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
		return iVizpodDao.findOneById(appUuid,id);
		}
		else
		return iVizpodDao.findOne(id);
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<Vizpod> findAllLatestActive() 	
	{	   
	   Aggregation vizpodAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Vizpod> vizpodResults = mongoTemplate.aggregate(vizpodAggr,"vizpod", Vizpod.class);	   
	   List<Vizpod> vizpodList = vizpodResults.getMappedResults();

	   // Fetch the vizExec details for each id
	   List<Vizpod> result=new  ArrayList<Vizpod>();
	   for(Vizpod v : vizpodList)
	   {   
		   Vizpod vizpodLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				vizpodLatest = iVizpodDao.findOneByUuidAndVersion(appUuid,v.getId(), v.getVersion());
			}
			else
			{
				vizpodLatest = iVizpodDao.findOneByUuidAndVersion(v.getId(), v.getVersion());
			}
			if(vizpodLatest != null)
			{
			result.add(vizpodLatest);
			}
	   }
	   return result;
	}*/

	public List<Vizpod> findVizpodByDatapod(String datapodUUID) throws JsonProcessingException {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb";
		Aggregation vizpodAggr = newAggregation(match(Criteria.where("source.ref.uuid").is(datapodUUID)),
				group("uuid").max("version").as("version"));
		AggregationResults<Vizpod> vizpodResults = mongoTemplate.aggregate(vizpodAggr, "vizpod", Vizpod.class);
		List<Vizpod> vizpodList = vizpodResults.getMappedResults();

		// Fetch the datapod details for each id
		List<Vizpod> result = new ArrayList<Vizpod>();
		for (Vizpod s : vizpodList) {
			//Vizpod vizpodLatest = iVizpodDao.findOneByUuidAndVersion(appUuid,s.getId(), s.getVersion());
			Vizpod vizpodLatest = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.vizpod.toString());
			result.add(vizpodLatest);
		}
		return result;
	}

	
	
	/**
	 * @param vizpodUuid
	 * @param vizpodVersion
	 * @param vizExec
	 * @param execParams
	 * @param runMode
	 * @return VizExec
	 * @throws Exception 
	 */
	public VizExec create(String vizpodUuid, String vizpodVersion, VizExec vizExec, ExecParams execParams, RunMode runMode) throws Exception {
		if(vizExec == null) {
			vizExec = new VizExec();
			vizExec.setExecParams(execParams);
			Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodUuid, vizpodVersion, MetaType.vizpod.toString(), "Y");
			vizExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.vizpod, vizpodUuid, vizpod.getVersion())));
			vizExec.setName(vizpod.getName());
			vizExec.setAppInfo(vizpod.getAppInfo());
			vizExec.setBaseEntity();
		}

		vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.PENDING);
		return vizExec;
	}
	
	/**
	 * @param execUuid
	 * @param execVersion
	 * @param vizExec
	 * @param execParams
	 * @param refKeyMap
	 * @param otherParams
	 * @param datapodList
	 * @param dagExec
	 * @param runMode
	 * @return VizExec
	 * @throws Exception 
	 */
	public VizExec parse(String execUuid, String execVersion, ExecParams execParams, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		logger.info("Inside vizpodServiceImpl.parse");
		if (datapodList != null) {
			logger.info(" Size of datapodList : " + datapodList.size());
		}

		VizExec vizExec = (VizExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.vizExec.toString(), "N");

		vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.STARTING);
		
		MetaIdentifier vizpodMI = vizExec.getDependsOn().getRef();
		Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodMI.getUuid(), vizpodMI.getVersion(), MetaType.vizpod.toString(), "Y");
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		List<AttributeRefHolder> filterInfo = vizpod.getFilterInfo();			
		if (execParams != null && execParams.getFilterInfo() != null) {
			if (filterInfo == null) {
				filterInfo = new ArrayList<>();
			}

			for (AttributeRefHolder attributeRefHolder : execParams.getFilterInfo()) {
				filterInfo.add(attributeRefHolder);
			}

			vizpod.setFilterInfo(filterInfo);
		}

		vizExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		
		try {
			vizExec.setSql(vizpodParser.toSql(vizpod, null, usedRefKeySet, true, runMode, false));
			logger.info(vizExec.getSql());

			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.READY);
		} catch (Exception e) {
			e.printStackTrace();
			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.READY);
			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.FAILED);
			throw new RuntimeException("FAILED to parse vizExec.");
		}
		
		return vizExec;
	}
	
	@SuppressWarnings("finally")
	public List<Map<String, Object>> getVizpodResults(String vizpodUUID, String vizpodVersion, ExecParams execParams, VizExec vizExec, 
			 									int rowLimit, int offset, int limit, String sortBy, String order, String requestId, 
												RunMode runMode) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		List<Map<String, Object>> data = null;
		try {
			Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodUUID, vizpodVersion, MetaType.vizpod.toString());
			
			if(vizpod.getLimit() != -1 && vizpod.getLimit() != 0 && vizpod.getLimit() < limit) {
				limit = vizpod.getLimit();
			}
			
			List<String> orderList = new ArrayList<>();
			List<String> sortList = new ArrayList<>();
			if(StringUtils.isNotBlank(order)) {	
			 orderList = Arrays.asList(order.split("\\s*,\\s*"));
			}
			if(StringUtils.isNotBlank(sortBy)) {
			 sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));
			}
			
			StringBuilder orderBy = new StringBuilder();
			boolean requestIdExistFlag = false;
//			boolean flag = true;
//			if(execParams!=null && execParams.getFilterInfo() != null){
//				for (AttributeRefHolder filterInfo : execParams.getFilterInfo()) {
//					vizpod.getFilterInfo().add(filterInfo);
//					}
//				}
		
			
			/**** Get sql and update in vizpodexec - START ****/
			if (vizExec == null) {
				vizExec = create(vizpodUUID, vizpodVersion, vizExec, execParams, runMode);
				vizExec = parse(vizExec.getUuid(), vizExec.getVersion(), execParams, null, null, null, null, runMode);
			}
			/**** Get sql and update in vizpodexec - END ****/

			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.RUNNING);
			
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			Datasource vizpodSourceDS =  commonServiceImpl.getDatasourceByObject(vizpod);
			String appUuid = commonServiceImpl.getApp().getUuid();
			
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			limit = offset + limit;
			offset = offset + 1;
//			if (sortBy.equals(null) || sortBy.isEmpty() && order.equals(null) || order.isEmpty() && requestId.equals(null) || requestId.isEmpty()) {
			if (StringUtils.isNotBlank(sortBy) || StringUtils.isNotBlank(order) ) {
				for (int i = 0; i < sortList.size(); i++) {
					orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
				}
				if (requestId != null) {
					String tabName = null;
					for (Map.Entry<String, String> entry : requestMap.entrySet()) {
						String id = entry.getKey();
						if (id.equals(requestId)) {
							requestIdExistFlag = true;
						}
					}
					if (requestIdExistFlag) {
						tabName = requestMap.get(requestId);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit, vizpodSourceDS, appUuid);
						} else if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " WHERE rownum <= " + limit, vizpodSourceDS, appUuid);
						} else {
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " LIMIT " + limit, vizpodSourceDS, appUuid);
						}
					} else {
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							data = exec.executeAndFetchByDatasource("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM (SELECT * FROM ("
										+ vizExec.getSql() + ") tn ORDER BY " + orderBy.toString() + ") AS tab) AS tab1", vizpodSourceDS, appUuid);
						}
						tabName = requestId.replace("-", "_");
						requestMap.put(requestId, tabName);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit, vizpodSourceDS, appUuid);
						} else if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " WHERE rownum <= " + limit, vizpodSourceDS, appUuid);
						} else {
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " LIMIT " + limit, vizpodSourceDS, appUuid);
						}
					}
				}
			} else {
				if(vizpodSourceDS.getType().toUpperCase().contains(ExecContext.spark.toString())
						|| vizpodSourceDS.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
						data = exec.executeAndFetchByDatasource("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM (" + vizExec.getSql()
					+ ") tn ) AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit, vizpodSourceDS, null);
				} else if(vizpodSourceDS.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
						data = exec.executeAndFetchByDatasource("SELECT * FROM ("+vizExec.getSql() + ") vizpod WHERE rownum <= " + limit, vizpodSourceDS, appUuid);
				} else {
					data = exec.executeAndFetchByDatasource("SELECT * FROM ("+vizExec.getSql() + ") vizpod LIMIT " + limit, vizpodSourceDS, appUuid);
				}
			}
//			/**** Get sql and update in vizpodexec - START ****/
//			MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
//			vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
//			vizExec.setDependsOn(vizpodRef);
//			vizExec.setBaseEntity();
//			commonServiceImpl.save(MetaType.vizExec.toString(), vizExec);
//			/**** Get sql and update in vizpodexec - END ****/
			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.COMPLETED);
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
				if(message.contains("Table or view not found")) {
					message = "Table or view not found.";
				}
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.vizExec, vizExec.getUuid(), vizExec.getVersion()));
			if(message.contains("sparkDriver")) {
				message="Communication link failure";
			} else {
				message="Some error occurred";
			}

			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.FAILED);
			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Table or View does not exists.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Table or View does not exists.");			 		
		} finally {
			return data;
		}		
	}
	
	
	
	
	@SuppressWarnings("finally")
	public List<Map<String, Object>> getVizpodDetails(String vizpodUUID, String vizpodVersion, ExecParams execParams,
			VizExec vizExec, int rowLimit, int offset, int limit, String sortBy, String order, String requestId,
			RunMode runMode)
			throws Exception {
		List<Map<String, Object>> data = null;
		try {
			Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodUUID, vizpodVersion,
					MetaType.vizpod.toString());
			
			/**** Get sql and update in vizpodexec - START ****/
			if (vizExec == null) {
				vizExec = create(vizpodUUID, vizpodVersion, vizExec, execParams, runMode);
				vizExec = parse(vizExec.getUuid(), vizExec.getVersion(), execParams, null, null, null, null, runMode);
			}
			/**** Get sql and update in vizpodexec - END ****/
			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.RUNNING);
					
			List<String> orderList = new ArrayList<>();
			List<String> sortList = new ArrayList<>();
			if (StringUtils.isNotBlank(order)) {
				orderList = Arrays.asList(order.split("\\s*,\\s*"));
			}
			if (StringUtils.isNotBlank(sortBy)) {
				sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));
			}

			StringBuilder orderBy = new StringBuilder();
			boolean requestIdExistFlag = false;

			if (execParams != null && execParams.getFilterInfo() != null) {
				for (AttributeRefHolder filterInfo : execParams.getFilterInfo()) {
					vizpod.getFilterInfo().add(filterInfo);
				}
			}

			if (vizpod.getDetailAttr() == null || vizpod.getDetailAttr().size() == 0) {

				if ((MetaType.datapod).equals(vizpod.getSource().getRef().getType())) {
					String uuid = vizpod.getSource().getRef().getUuid();
					List<AttributeRefHolder> AttrName = registerService.getAttributesByDatapod(uuid);
					List<AttributeDetails> attributeDetails = new ArrayList<AttributeDetails>();
					for (AttributeRefHolder attr : AttrName) {
						AttributeDetails obj = new AttributeDetails();
						MetaIdentifier ref = new MetaIdentifier();
						if (!attr.getAttrName().equals("load_id")) {
							ref.setUuid(attr.getRef().getUuid());
							ref.setType(attr.getRef().getType());

							obj.setRef(ref);
							obj.setAttributeId(Integer.parseInt(attr.getAttrId()));
							attributeDetails.add(obj);
						}
					}

					vizpod.setDetailAttr(attributeDetails);
				}

				if ((MetaType.relation).equals(vizpod.getSource().getRef().getType())) {
					String uuid = vizpod.getSource().getRef().getUuid();
					List<AttributeRefHolder> AttrName = registerService.getAttributesByRelation(uuid,
							vizpod.getSource().getRef().getType().toString());
					List<AttributeDetails> attributeDetails = new ArrayList<AttributeDetails>();
					for (AttributeRefHolder attr : AttrName) {
						AttributeDetails obj = new AttributeDetails();
						MetaIdentifier ref = new MetaIdentifier();
						if (!attr.getAttrName().equals("load_id")) {
							ref.setUuid(attr.getRef().getUuid());
							ref.setType(attr.getRef().getType());

							obj.setRef(ref);
							obj.setAttributeId(Integer.parseInt(attr.getAttrId()));
							attributeDetails.add(obj);
						}
					}

					vizpod.setDetailAttr(attributeDetails);
				}
			}

			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			limit = offset + limit;
			offset = offset + 1;

			if (StringUtils.isNotBlank(sortBy) || StringUtils.isNotBlank(order)) {
				for (int i = 0; i < sortList.size(); i++) {
					orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
				}
				if (requestId != null) {
					String tabName = null;
					for (Map.Entry<String, String> entry : requestMap.entrySet()) {
						String id = entry.getKey();
						if (id.equals(requestId)) {
							requestIdExistFlag = true;
						}
					}
					if (requestIdExistFlag) {
						tabName = requestMap.get(requestId);
						if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							data = exec.executeAndFetch(
									"SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit,
									null);
						} else if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
							data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum <= " + limit, null);
						} else {
							data = exec.executeAndFetch("SELECT * FROM " + tabName + " LIMIT " + limit, null);
						}
					} else {
						if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							data = exec.executeAndFetch(
									"SELECT * FROM (SELECT Row_Number() Over() AS rownum, * FROM (SELECT * FROM ("
											+ vizExec.getSql() + ") tn ORDER BY " + orderBy.toString()
											+ ") AS tab) AS tab1",
									null);
						}
						tabName = requestId.replace("-", "_");
						requestMap.put(requestId, tabName);
						if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							data = exec.executeAndFetch(
									"SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit,
									null);
						}else if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
							data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum <= " + limit, null);
						} else {
							data = exec.executeAndFetch("SELECT * FROM " + tabName + " LIMIT " + limit, null);
						}
					}
				}
			} else {
				if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
					data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM ("
							+ vizExec.getSql() + ") tn ) AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit,
							null);
				} else if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
						data = exec.executeAndFetch("SELECT * FROM (" + vizExec.getSql() + ") vizpod WHERE rownum <= " + limit,
								null);
				} else {
					data = exec.executeAndFetch("SELECT * FROM (" + vizExec.getSql() + ") vizpod LIMIT " + limit, null);
				}
			}
//			/**** Get sql and update in vizpodexec - START ****/
//			/* df.cache(); */
//			MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
//			vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod, vizpodUUID, vizpodVersion));
//			vizExec.setDependsOn(vizpodRef);
//			// vizExecServiceImpl.save(vizExec);
//			// commonServiceImpl.save(MetaType.vizExec.toString(), vizExec);
//			/**** Get sql and update in vizpodexec - END ****/
			
			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.COMPLETED);
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
				if(message.contains("Table or view not found")) {
					message = "Table or view not found.";
				}
			}catch (Exception e2) {
				// TODO: handle exception
			}
			if(message.contains("sparkDriver")) {
				message="Communication link failure";
			} else {
				message="Some error occurred";
			}

			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.FAILED);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.vizExec, vizExec.getUuid(), vizExec.getVersion()));
			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Table or View does not exists.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Table or View does not exists.");			 		
		} finally {
			return data;
		}	
	}
	
	/**
	 * Overloaded function without vizExec
	 * @param vizpodUUID
	 * @param vizpodVersion
	 * @param execParams
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws JSONException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	/********************** UNUSED **********************/
	/*public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, ExecParams execParams, String vizExecUUID, 
			int rowLimit, int offset, int limit, String sortBy, String order, String requestId) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		//VizExec vizExec = vizExecServiceImpl.findLatestByUuid(vizExecUUID, new Sort(Sort.Direction.DESC, "version"));
		VizExec vizExec = (VizExec) commonServiceImpl.getLatestByUuid(vizExecUUID, MetaType.vizExec.toString());
		return getVizpodResults(vizpodUUID, vizpodVersion, execParams, vizExec, 
				limit, offset, limit, sortBy, order, requestId);
	}*/

	
	/**
	 * Overloaded function without vizExec
	 * @param vizpodUUID
	 * @param vizpodVersion
	 * @param execParams
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws JSONException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	/********************** UNUSED **********************/
//	public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, ExecParams execParams, 
//												int rowLimit, int offset, int limit, String sortBy, String order,String requestId, RunMode runMode) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
//		return getVizpodResults(vizpodUUID, vizpodVersion, execParams, new VizExec(), 
//										rowLimit, offset, limit, sortBy, order, requestId, runMode);
//	}

	/********************** UNUSED **********************/
//	public VizpodDetailsHolder getVizpodDetails(String vizpodUUID, String vizpodVersion, ExecParams execParams,
//												int rowLimit, int offset, int limit, String sortBy, String order, String requestId, RunMode runMode)
//															throws Exception {
//
//		return getVizpodDetails(vizpodUUID, vizpodVersion, execParams, new VizExec(), rowLimit, offset, limit,
//				sortBy, order, requestId, runMode);
//	}
	

	// Find vizpod by relation
		public List<Vizpod> findVizpodByRelation(String relationUUID) throws JsonProcessingException {
			/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
			Aggregation vizpodAggr = newAggregation(match(Criteria.where("source.ref.uuid").is(relationUUID)),
					group("uuid").max("version").as("version"));
			AggregationResults<Vizpod> vizpodResults = mongoTemplate.aggregate(vizpodAggr, "vizpod", Vizpod.class);
			List<Vizpod> vizpodList = vizpodResults.getMappedResults();

			// Fetch relation details for each id
			List<Vizpod> result = new ArrayList<Vizpod>();
			for (Vizpod s : vizpodList) {
				//Vizpod vizpodLatest = iVizpodDao.findOneByUuidAndVersion(appUuid,s.getId(), s.getVersion());
				Vizpod vizpodLatest = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.vizpod.toString());
				Vizpod vizpod=resolveName(vizpodLatest);
				result.add(vizpod);
			}
			return result;
		}

	
	public HttpServletResponse download(String execUuid, String execVersion, String saveOnRefresh, String format,
			ExecParams execParams, String download, int offset, int limit, HttpServletResponse response, int rowLimit,
			String sortBy, String order, String requestId, RunMode runMode, Layout layout) throws Exception {

		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if (rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of " + maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					"Requested rows exceeded the limit of " + maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
		}

		List<Map<String, Object>> results = getVizpodResults(execUuid, execVersion, saveOnRefresh, rowLimit, offset,
				limit, sortBy, order, requestId, runMode);
		response = commonServiceImpl.download(format, response, runMode, results,
				new MetaIdentifierHolder(new MetaIdentifier(MetaType.vizExec, execUuid, execVersion)), layout);
		return response;
	}

	/**
	 * @param vizpodUuid
	 * @param vizpodVersion
	 * @param execParams
	 * @param vizExec
	 * @param runMode
	 * @return VizExec
	 * @throws Exception 
	 */
	public VizExec execute(String execUuid, String execVersion, ExecParams execParams,
			String saveOnRefresh, RunMode runMode) throws Exception {
		VizExec vizExec = null;
		try {		
			vizExec = (VizExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.vizExec.toString(), "N");			
			MetaIdentifier vizpodMI = vizExec.getDependsOn().getRef();
			Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodMI.getUuid(), vizpodMI.getVersion(), vizpodMI.getType().toString(), "Y");
			
			String appUuid = commonServiceImpl.getApp().getUuid();
			
			RunVizpodServiceImpl runVizpodServiceImpl = new RunVizpodServiceImpl();
			runVizpodServiceImpl.setVizExec(vizExec);
			runVizpodServiceImpl.setVizpod(vizpod);
			runVizpodServiceImpl.setRunMode(runMode);
			runVizpodServiceImpl.setCommonServiceImpl(commonServiceImpl);
			runVizpodServiceImpl.setVizpodServiceImpl(this);
			runVizpodServiceImpl.setSparkExecutor(sparkExecutor);
			runVizpodServiceImpl.setDataStoreServiceImpl(dataStoreServiceImpl);
			runVizpodServiceImpl.setHdfsInfo(hdfsInfo);
			runVizpodServiceImpl.setExecFactory(execFactory);
			runVizpodServiceImpl.setSaveOnRefresh(saveOnRefresh);
			runVizpodServiceImpl.setAppUuid(appUuid);
			runVizpodServiceImpl.setName(MetaType.vizExec+"_"+vizExec.getUuid()+"_"+vizExec.getVersion());
			runVizpodServiceImpl.setSessionContext(sessionHelper.getSessionContext());
			runVizpodServiceImpl.setAppDs(commonServiceImpl.getDatasourceByApp());
			runVizpodServiceImpl.call();
		} catch (Exception e) {
			e.printStackTrace();
			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.FAILED);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.vizExec, vizExec.getUuid(), vizExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Vizpod execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Vizpod execution FAILED.");	
		}
		
		return vizExec;
	}
	
		/**
		 * @param vizExecUuid
		 * @param vizExecVersion
		 * @param execParams
		 * @param rows
		 * @param offset
		 * @param limit
		 * @param sortBy
		 * @param order
		 * @param requestId
		 * @param runMode
		 * @throws Exception 
		 * @returnList<Map<String, Object>>
		 */
		public List<Map<String, Object>> getVizpodResults(String vizExecUuid, String vizExecVersion, String saveOnRefresh,
				int rows, int offset, int limit, String sortBy, String order, String requestId,
				RunMode runMode) throws Exception {
			VizExec vizExec = (VizExec) commonServiceImpl.getOneByUuidAndVersion(vizExecUuid, vizExecVersion, MetaType.vizExec.toString());
			MetaIdentifier vizpodMI = vizExec.getDependsOn().getRef();
			Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodMI.getUuid(), vizpodMI.getVersion(), vizpodMI.getType().toString());
			
			if(vizpod.getLimit() != -1 && vizpod.getLimit() != 0 && vizpod.getLimit() < limit) {
				limit = vizpod.getLimit();
			}
			
			limit = offset + limit;
			offset = offset + 1;
			dataStoreServiceImpl.setRunMode(runMode);
			DataStore datastore = dataStoreServiceImpl.getDatastore(vizExec.getResult().getRef().getUuid(),
					vizExec.getResult().getRef().getVersion());	
			String tableName = dataStoreServiceImpl.getTableNameByDatastoreKey(datastore.getUuid(), datastore.getVersion(), runMode);
			if(saveOnRefresh.equalsIgnoreCase("Y")) {
				String appUuid = commonServiceImpl.getApp().getUuid();
				List<String> filePathList = new ArrayList<>();
				filePathList.add(datastore.getLocation());
				sparkExecutor.readAndRegisterFile(tableName, filePathList, FileType.PARQUET.toString(), null, appUuid, true);
			}
			return dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order, null);
		}

		
		@Override
		public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
			execute(baseExec.getUuid(), baseExec.getVersion(), execParams, baseExec.getExecParams().getOtherParams().get("saveOnRefresh"), runMode);
			return null;
		}
		
		@Override
		public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
			synchronized (baseExec.getUuid()) {
				baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.vizExec, Status.Stage.STARTING);
			}
			synchronized (baseExec.getUuid()) {
				baseExec = (DashboardExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.vizExec, Status.Stage.READY);
			}			
			return baseExec;
		}
		
		@Override
		public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
				HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode)
				throws Exception {
			BaseRuleExec baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.vizExec.toString(), "N");
			synchronized (execUuid) {
				baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, MetaType.vizExec, Status.Stage.STARTING);
			}
			synchronized (execUuid) {
				baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, MetaType.vizExec, Status.Stage.READY);
			}
			return baseRuleExec;
		}

		@Override
		public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec,
				MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, ExecParams execParams,
				RunMode runMode) throws Exception {
			return execute(baseRuleExec.getUuid(), baseRuleExec.getVersion(), execParams, baseRuleExec.getExecParams().getOtherParams().get("saveOnRefresh"), runMode);
		}


	public List<Map<String, Object>> getVizpodResultDetails(String vizpodUuid, String vizpodVersion,
			ExecParams execParams, Object vizExec, int rows, int offset, int limit, String sortBy, String order,
			String requestId, RunMode runMode) throws Exception {
		List<Map<String, Object>> data = null;
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		
		Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodUuid, vizpodVersion,
				MetaType.vizpod.toString(), "Y");
		if (execParams != null && execParams.getFilterInfo() != null) {
			vizpod.setFilterInfo(execParams.getFilterInfo());
		}
		String sql = vizpodDetailParser.toSql(vizpod, null, usedRefKeySet, true, runMode, false);

		logger.info("vizpod details quary: "+sql);

		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		limit = offset + limit;
		offset = offset + 1;

		String appUuid = commonServiceImpl.getApp().getUuid();
		
		MetaIdentifier dependsOnMI = vizpod.getSource().getRef();
		Object sourceObj = commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(),
				dependsOnMI.getType().toString(), "N");
		Datasource vizDatasource = commonServiceImpl.getDatasourceByObject(sourceObj);
		if (vizDatasource.getType().toUpperCase().contains(ExecContext.spark.toString())
				|| vizDatasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
			data = exec.executeAndFetchByDatasource("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM (" + sql
					+ ") tn ) AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit, vizDatasource, appUuid);
		} else if (vizDatasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
			data = exec.executeAndFetchByDatasource("SELECT * FROM (" + sql + ") vizpod WHERE rownum <= " + limit, vizDatasource, appUuid);
		} else {
			data = exec.executeAndFetchByDatasource("SELECT * FROM (" + sql + ") vizpod LIMIT " + limit, vizDatasource, appUuid);
		}
		return data;
	}
		
}
