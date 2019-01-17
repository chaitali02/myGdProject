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

import java.io.FileOutputStream;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.WorkbookUtil;
import com.inferyx.framework.dao.IVizpodDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.VizpodDetailsHolder;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.domain.Vizpod;
import com.inferyx.framework.domain.Vizpod.AttributeDetails;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.domain.VizpodResultHolder;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.parser.VizpodParser;
import com.inferyx.framework.register.GraphRegister;

@Service
public class VizpodServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	HiveContext hiveContext;*/
	@Autowired
	IVizpodDao iVizpodDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	DagExecServiceImpl dagExecImpl;
	@Autowired
	private VizpodParser vizpodParser;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private FormulaServiceImpl formulaServiceImpl;
	@Autowired
	private ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private RelationServiceImpl relationServiceImpl;
	@Autowired 
	private UserServiceImpl userServiceImpl;
	@Autowired
	private VizExecServiceImpl vizExecServiceImpl;
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
	
	Map<String, String> requestMap = new HashMap<String, String>();
		
	static final Logger logger = Logger.getLogger(VizpodServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Vizpod findLatest() {
		return resolveName(iVizpodDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Vizpod findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iVizpodDao.findAllByUuid(appUuid,uuid);
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

	/********************** UNUSED **********************/
	/*public List<Vizpod> test(String param1) {
		return iVizpodDao.test(param1);
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

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public Vizpod findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null) {
			return iVizpodDao.findOneByUuidAndVersion(uuid, version);
		} else {
			return iVizpodDao.findOneByUuidAndVersion(appUuid,uuid, version);
		}
	}*/
	
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

	/********************** UNUSED **********************/
	/*public List<Vizpod> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iVizpodDao.findAll(); 
		}
		else
		return iVizpodDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Vizpod vizpod = iVizpodDao.findOneById(appUuid,Id);
		vizpod.setActive("N");
		iVizpodDao.save(vizpod);
//		String ID = vizpod.getId();
//		iVizpodDao.delete(ID);
//		vizpod.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public Vizpod save(Vizpod vizpod) throws Exception {
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		vizpod.setAppInfo(metaIdentifierHolderList);
		vizpod.setBaseEntity();
		Vizpod vizpodDet=iVizpodDao.save(vizpod);
		registerGraph.updateGraph((Object) vizpodDet, MetaType.vizpod);
		return vizpodDet;
	}*/

	/********************** UNUSED **********************/
	/*public List<Vizpod> findAllLatest() {
		{
			//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
			Aggregation vizpodAggr = newAggregation(group("uuid").max("version").as("version"));
			AggregationResults<Vizpod> vizpodResults = mongoTemplate.aggregate(vizpodAggr, "vizpod", Vizpod.class);
			List<Vizpod> vizpodList = vizpodResults.getMappedResults();

			// Fetch the relation details for each id
			List<Vizpod> result = new ArrayList<Vizpod>();
			for (Vizpod s : vizpodList) {
				Vizpod vizpodLatest;
				String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
				if(appUuid != null)
				{
				//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();;
					vizpodLatest = iVizpodDao.findOneByUuidAndVersion(appUuid,s.getId(), s.getVersion());
				}
				else
				{
					vizpodLatest = iVizpodDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
				}
				//logger.debug("datapodLatest is " + datapodLatest.getName());
				if(vizpodLatest != null)
				{
				result.add(vizpodLatest);
				}
			}
			return result;
		}
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

	/********************** UNUSED **********************/
	/*public List<java.util.Map<String, Object>> fetchVizpodResults(String vizpodUUID, String vizpodVersion,
			String dagExecUUID, String dagExecVersion, String stageId, String taskId) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		List<java.util.Map<String, Object>> data = new ArrayList<>();
		String tableName = dagExecImpl.getTableName(dagExecUUID, dagExecVersion, stageId, taskId);
		//Vizpod vizpod = iVizpodDao.findOneByUuidAndVersion(appUuid,vizpodUUID, vizpodVersion);
		Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodUUID, vizpodVersion, MetaType.vizpod.toString());
		// Generate SQL
		//DataFrame df = sqlContext.sql(vizpodParser.toSql(vizpod, tableName));
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		try {
			data = exec.executeAndFetch(vizpodParser.toSql(vizpod, tableName, usedRefKeySet), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}*/

	/********************** UNUSED **********************/
	/*public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, String datastoreUUID,
			String datastoreVersion, VizExec vizExec, 
			int rowLimit, int offset, int limit, String sortBy, String order, String requestId) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		//String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb";
		String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastoreUUID, datastoreVersion);
		logger.info("Datastore - Table name:" + tableName);
		List<Map<String, Object>> data = new ArrayList<>();
		
		List<String> orderList = Arrays.asList(order.split("\\s*,\\s*"));
		List<String> sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));
		
		StringBuilder orderBy = new StringBuilder();
		boolean requestIdExistFlag = false;
		
		//Vizpod vizpod = iVizpodDao.findOneByUuidAndVersion(appUuid,vizpodUUID, vizpodVersion);
		Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodUUID, vizpodVersion, MetaType.vizpod.toString());
		*//**** Get sql and update in vizpodexec - START ****//*
		if (vizExec == null) {
			vizExec = new VizExec();
			MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
			vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
			vizExec.setDependsOn(vizpodRef);
			vizExec.setBaseEntity();
		} else if (StringUtils.isBlank(vizExec.getUuid())) {
			MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
			vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
			vizExec.setDependsOn(vizpodRef);
			vizExec.setBaseEntity();
		}
		try {
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			vizExec.setSql(vizpodParser.toSql(vizpod, tableName, usedRefKeySet));
			vizExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		} catch (Exception e) {
			e.printStackTrace();
		}
		*//**** Get sql and update in vizpodexec - END ****//*
		//DataFrame df = sqlContext.sql(vizExec.getSql());
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
		
		if (sortBy.equals("null") || sortBy.isEmpty() && order.equals("null")
				|| order.isEmpty() && requestId.equals("null") || requestId.isEmpty()) {
			data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over() AS rownum, * FROM (" + vizExec.getSql()
					+ ") tn ) AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
		} else {
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
					data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
				} else {
					data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over() AS rownum, * FROM (SELECT * FROM ("
									+ vizExec.getSql() + ") tn ORDER BY " + orderBy.toString() + ") AS tab) AS tab1", null);
					tabName = requestId.replace("-", "_");
					requestMap.put(requestId, tabName);
					data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
				}
			}
		}

		
//		DataFrame df = rsHolder.getDataFrame();
		Row[] rows = dfSorted.head(30);
		String[] columns = dfSorted.columns();
		*//**** Get sql and update in vizpodexec - START ****//*
		hiveContext.registerDataFrameAsTable(dfSorted, vizExec.getUuid());
		MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
		vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
		vizExec.setDependsOn(vizpodRef);
		//vizExecServiceImpl.save(vizExec);
		commonServiceImpl.save(MetaType.vizExec.toString(), vizExec);
		*//**** Get sql and update in vizpodexec - END ****//*
		for (Row row : rows) {
			java.util.TreeMap<String, Object> object = new TreeMap<String, Object>();
			for (String column : columns) {
				object.put(column, row.getAs(column));
			}
			data.add(object);
		}
		VizpodResultHolder resultHolder = new VizpodResultHolder(data, vizExec);
		return resultHolder;
	}
*/
	/********************** UNUSED **********************/
	/*public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, String datastoreUUID,
			String datastoreVersion, 
			int rowLimit, int offset, int limit, String sortBy, String order, String requestId) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return getVizpodResults(vizpodUUID, vizpodVersion, datastoreUUID, datastoreVersion, new VizExec(), rowLimit, offset, limit, sortBy, order, requestId);
	}*/

	/********************** UNUSED **********************/
	/*public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, String datastoreUUID,
			String datastoreVersion, ExecParams execParams, VizExec vizExec, 
			int rowLimit, int offset, int limit, String sortBy, String order, String requestId) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		//String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb";
		String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastoreUUID, datastoreVersion);
		logger.info("Datastore - Table name:" + tableName);
		List<Map<String, Object>> data = new ArrayList<>();
		Vizpod vizpod = iVizpodDao.findOneByUuidAndVersion(appUuid,vizpodUUID, vizpodVersion);
		List<String> orderList = Arrays.asList(order.split("\\s*,\\s*"));
		List<String> sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		
		StringBuilder orderBy = new StringBuilder();
		DataFrame dfSorted = null;
		boolean requestIdExistFlag = false;
		if (execParams != null && execParams.getFilterInfo() != null) {
			for (AttributeRefHolder filterInfo : execParams.getFilterInfo()) {
				vizpod.getFilterInfo().add(filterInfo);
			}
		}
		*//**** Get sql and update in vizpodexec - START ****//*
		if (vizExec == null) {
			vizExec = new VizExec();
			MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
			vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
			vizExec.setDependsOn(vizpodRef);
			vizExec.setBaseEntity();
		}
		vizExec.setExecParams(execParams);
		try {
			vizExec.setSql(vizpodParser.toSql(vizpod, tableName, usedRefKeySet));
			vizExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*//**** Get sql and update in vizpodexec - END ****//*
		//DataFrame df = sqlContext.sql(vizExec.getSql());
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
		
		if (sortBy.equals("null") || sortBy.isEmpty() && order.equals("null")
				|| order.isEmpty() && requestId.equals("null") || requestId.isEmpty()) {
			data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over() AS rownum, * FROM (" + vizExec.getSql()
					+ ") tn ) AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
		} else {
			for (int i = 0; i < sortList.size(); i++) 
				orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
			
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
					data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
				} else {
					data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over() AS rownum, * FROM (SELECT * FROM ("
									+ vizExec.getSql() + ") tn ORDER BY " + orderBy.toString() + ") AS tab) AS tab1", null);
					tabName = requestId.replace("-", "_");
					requestMap.put(requestId, tabName);
					data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
				}
			}
		}
		
		//DataFrame df = rsHolder.getDataFrame();
		Row[] rows = dfSorted.head(limit);
		String[] columns = dfSorted.columns();
		*//**** Get sql and update in vizpodexec - START ****//*
		hiveContext.registerDataFrameAsTable(dfSorted, vizExec.getUuid());
		MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
		vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
		vizExec.setDependsOn(vizpodRef);
		//vizExecServiceImpl.save(vizExec);
		commonServiceImpl.save(MetaType.vizExec.toString(), vizExec);
		*//**** Get sql and update in vizpodexec - END ****//*
		for (Row row : rows) {
			java.util.TreeMap<String, Object> object = new TreeMap<String, Object>();
			for (String column : columns) {
				object.put(column, row.getAs(column));
			}
			data.add(object);
		}
		VizpodResultHolder resultHolder = new VizpodResultHolder(data, vizExec);
		return resultHolder;
	}
*/
	/********************** UNUSED **********************/
	/*public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, ExecParams execParams) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return getVizpodResults(vizpodUUID, vizpodVersion, execParams, new VizExec());
	}*/

	/********************** UNUSED **********************/
	/*public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, ExecParams execParams, String vizExecUUID) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		//VizExec vizExec = vizExecServiceImpl.findLatestByUuid(vizExecUUID, new Sort(Sort.Direction.DESC, "version"));
		VizExec vizExec = (VizExec) commonServiceImpl.getLatestByUuid(vizpodUUID, MetaType.vizExec.toString());
		return getVizpodResults(vizpodUUID, vizpodVersion, execParams, vizExec);
	}*/

	/********************** UNUSED **********************/
	/*public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, ExecParams execParams, VizExec vizExec) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		//String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb";
		List<Map<String, Object>> data = new ArrayList<>();
		//Vizpod vizpod = iVizpodDao.findOneByUuidAndVersion(appUuid,vizpodUUID, vizpodVersion);
		Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodUUID, vizpodVersion, MetaType.vizpod.toString());
		if (vizpod == null) {
			return null;
		}
		if(execParams!=null && execParams.getFilterInfo() != null){
			for (AttributeRefHolder filterInfo : execParams.getFilterInfo()) {
				vizpod.getFilterInfo().add(filterInfo);
				}
			}
		*//**** Get sql and update in vizpodexec - START ****//*
		if (vizExec == null) {
			vizExec = new VizExec();
			vizExec.setBaseEntity();
			MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
			vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
			vizExec.setDependsOn(vizpodRef);
		}
		vizExec.setExecParams(execParams);
		try {
			vizExec.setSql(vizpodParser.toSql(vizpod, "", null));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*//**** Get sql and update in vizpodexec - END ****//*
		//DataFrame df = sqlContext.sql(vizExec.getSql());
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
		ResultSetHolder rsHolder = exec.executeSql(vizExec.getSql());
		DataFrame df = rsHolder.getDataFrame();
		Row[] rows = df.head(30);
		String[] columns = df.columns();
		*//**** Get sql and update in vizpodexec - START ****//*
		hiveContext.registerDataFrameAsTable(df, Helper.genTableName((vizExec.getUuid())));
		df.cache();
		MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
		vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
		vizExec.setDependsOn(vizpodRef);
		//vizExecServiceImpl.save(vizExec);
		commonServiceImpl.save(MetaType.vizExec.toString(), vizExec);
		*//**** Get sql and update in vizpodexec - END ****//*
		for (Row row : rows) {
			java.util.TreeMap<String, Object> object = new TreeMap<String, Object>();
			for (String column : columns) {
				object.put(column, row.getAs(column));
			}
			data.add(object);
		}
		VizpodResultHolder resultHolder = new VizpodResultHolder(data, vizExec);
		return resultHolder;
	}
*/
	/********************** UNUSED **********************/
	/*public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, String datastoreUUID,
			String datastoreVersion, VizExec vizExec) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		//String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb";
		String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastoreUUID, datastoreVersion);
		logger.info("Datastore - Table name:" + tableName);
		List<Map<String, Object>> data = new ArrayList<>();
		//Vizpod vizpod = iVizpodDao.findOneByUuidAndVersion(appUuid,vizpodUUID, vizpodVersion);
		Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodUUID, vizpodVersion, MetaType.vizpod.toString());
		*//**** Get sql and update in vizpodexec - START ****//*
		if (vizExec == null) {
			vizExec = new VizExec();
			MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
			vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
			vizExec.setDependsOn(vizpodRef);
			vizExec.setBaseEntity();
		} else if (StringUtils.isBlank(vizExec.getUuid())) {
			MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
			vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
			vizExec.setDependsOn(vizpodRef);
			vizExec.setBaseEntity();
		}
		try {
			vizExec.setSql(vizpodParser.toSql(vizpod, tableName, null));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*//**** Get sql and update in vizpodexec - END ****//*
		//DataFrame df = sqlContext.sql(vizExec.getSql());
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
		ResultSetHolder rsHolder = exec.executeSql(vizExec.getSql());
		DataFrame df = rsHolder.getDataFrame();
		Row[] rows = df.head(30);
		String[] columns = df.columns();
		*//**** Get sql and update in vizpodexec - START ****//*
		hiveContext.registerDataFrameAsTable(df, vizExec.getUuid());
		MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
		vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
		vizExec.setDependsOn(vizpodRef);
		//vizExecServiceImpl.save(vizExec);
		commonServiceImpl.save(MetaType.vizExec.toString(), vizExec);
		*//**** Get sql and update in vizpodexec - END ****//*
		for (Row row : rows) {
			java.util.TreeMap<String, Object> object = new TreeMap<String, Object>();
			for (String column : columns) {
				object.put(column, row.getAs(column));
			}
			data.add(object);
		}
		VizpodResultHolder resultHolder = new VizpodResultHolder(data, vizExec);
		return resultHolder;
	}*/
	
	
	/**
	 * Overloaded function without vizExec
	 * @param vizpodUUID
	 * @param vizpodVersion
	 * @param datastoreUUID
	 * @param datastoreVersion
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
	/*public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, String datastoreUUID,
			String datastoreVersion, ExecParams execParams, 
			int rowLimit, int offset, int limit, String sortBy, String order, String requestId) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return getVizpodResults(vizpodUUID, vizpodVersion, datastoreUUID,
			datastoreVersion, execParams, null, rowLimit, offset, limit, sortBy, order, requestId);
	}*/
	
	@SuppressWarnings("finally")
	public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, ExecParams execParams, VizExec vizExec, 
			 									int rowLimit, int offset, int limit, String sortBy, String order, String requestId, 
												RunMode runMode) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodUUID, vizpodVersion, MetaType.vizpod.toString());
			int vizpodLimit = Integer.parseInt(vizpod.getLimit() != null ? vizpod.getLimit() : 0+"");
			if(vizpodLimit < limit) {
				limit = vizpodLimit;
			}
			
			List<String> orderList = new ArrayList<>();
			List<String> sortList = new ArrayList<>();
			if(StringUtils.isNotBlank(order)) {	
			 orderList = Arrays.asList(order.split("\\s*,\\s*"));
			}
			if(StringUtils.isNotBlank(sortBy)) {
			 sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));
			}
			
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			StringBuilder orderBy = new StringBuilder();
			boolean requestIdExistFlag = false;
//			boolean flag = true;
			if(execParams!=null && execParams.getFilterInfo() != null){
				for (AttributeRefHolder filterInfo : execParams.getFilterInfo()) {
					vizpod.getFilterInfo().add(filterInfo);
					}
				}
		
			
			/**** Get sql and update in vizpodexec - START ****/
			if (vizExec == null) {
				vizExec = new VizExec();
				vizExec.setBaseEntity();
				MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
				vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
				vizExec.setDependsOn(vizpodRef);
			}
			vizExec.setExecParams(execParams);
			try {
				vizExec.setSql(vizpodParser.toSql(vizpod, "", usedRefKeySet, true, runMode, false));
				logger.info(vizExec.getSql());
				vizExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			} catch (Exception e) {
				e.printStackTrace();
			}

			/**** Get sql and update in vizpodexec - END ****/
			
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
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString()))
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit, vizpodSourceDS, appUuid);
						else
							if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " WHERE rownum <= " + limit, vizpodSourceDS, appUuid);
							else
								data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " LIMIT " + limit, vizpodSourceDS, appUuid);
					} else {
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString()))
							data = exec.executeAndFetchByDatasource("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM (SELECT * FROM ("
										+ vizExec.getSql() + ") tn ORDER BY " + orderBy.toString() + ") AS tab) AS tab1", vizpodSourceDS, appUuid);
						tabName = requestId.replace("-", "_");
						requestMap.put(requestId, tabName);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString()))
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit, vizpodSourceDS, appUuid);
							else
								if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
									data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " WHERE rownum <= " + limit, vizpodSourceDS, appUuid);
								else
									data = exec.executeAndFetchByDatasource("SELECT * FROM " + tabName + " LIMIT " + limit, vizpodSourceDS, appUuid);
					}
				}
			} else {
				if(vizpodSourceDS.getType().toUpperCase().contains(ExecContext.spark.toString())
						|| vizpodSourceDS.getType().toUpperCase().contains(ExecContext.FILE.toString()))
						data = exec.executeAndFetchByDatasource("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM (" + vizExec.getSql()
					+ ") tn ) AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit, vizpodSourceDS, null);
				else
					if(vizpodSourceDS.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
						data = exec.executeAndFetchByDatasource("SELECT * FROM ("+vizExec.getSql() + ") vizpod WHERE rownum <= " + limit, vizpodSourceDS, appUuid);
					else
						data = exec.executeAndFetchByDatasource("SELECT * FROM ("+vizExec.getSql() + ") vizpod LIMIT " + limit, vizpodSourceDS, appUuid);
			}
			/**** Get sql and update in vizpodexec - START ****/
			MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
			vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod,vizpodUUID,vizpodVersion));
			vizExec.setDependsOn(vizpodRef);
			vizExec.setBaseEntity();
			commonServiceImpl.save(MetaType.vizExec.toString(), vizExec);
			/**** Get sql and update in vizpodexec - END ****/
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
			if(message.contains("sparkDriver") )
				message="Communication link failure";
			else
				message="Some error occurred";

			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Table or View does not exists.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Table or View does not exists.");			 		
		} finally {
			VizpodResultHolder resultHolder = new VizpodResultHolder(data, vizExec);
			return resultHolder;
		}		
	}
	
	
	
	
	public VizpodDetailsHolder getVizpodDetails(String vizpodUUID, String vizpodVersion, ExecParams execParams,
			VizExec vizExec, int rowLimit, int offset, int limit, String sortBy, String order, String requestId,
			RunMode runMode)
			throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		/* String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid(); */
		// String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb";
		List<Map<String, Object>> data = new ArrayList<>();
		// Vizpod vizpod = iVizpodDao.findOneByUuidAndVersion(appUuid,vizpodUUID,
		// vizpodVersion);
		Vizpod vizpod = (Vizpod) commonServiceImpl.getOneByUuidAndVersion(vizpodUUID, vizpodVersion,
				MetaType.vizpod.toString());
		List<String> orderList = new ArrayList<>();
		List<String> sortList = new ArrayList<>();
		if (StringUtils.isNotBlank(order)) {
			orderList = Arrays.asList(order.split("\\s*,\\s*"));
		}
		if (StringUtils.isNotBlank(sortBy)) {
			sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));
		}

		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
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

		/**** Get sql and update in vizpodexec - START ****/
		if (vizExec == null) {
			vizExec = new VizExec();
			vizExec.setBaseEntity();
			MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
			vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod, vizpodUUID, vizpodVersion));
			vizExec.setDependsOn(vizpodRef);
		}
		vizExec.setExecParams(execParams);
		try {
			vizExec.setSql(vizpodParser.toSql(vizpod, "", usedRefKeySet, false, runMode, true));
			logger.info(vizExec.getSql());
			vizExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**** Get sql and update in vizpodexec - END ****/
		// DataFrame df = sqlContext.sql(vizExec.getSql());
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		limit = offset + limit;
		offset = offset + 1;

		// if (sortBy.equals(null) || sortBy.isEmpty() && order.equals(null) ||
		// order.isEmpty() && requestId.equals(null) || requestId.isEmpty()) {
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
							|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString()))
						data = exec.executeAndFetch(
								"SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit,
								null);
					else if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
						data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum <= " + limit, null);
					else
						data = exec.executeAndFetch("SELECT * FROM " + tabName + " LIMIT " + limit, null);
				} else {
					if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString()))
						data = exec.executeAndFetch(
								"SELECT * FROM (SELECT Row_Number() Over() AS rownum, * FROM (SELECT * FROM ("
										+ vizExec.getSql() + ") tn ORDER BY " + orderBy.toString()
										+ ") AS tab) AS tab1",
								null);
					tabName = requestId.replace("-", "_");
					requestMap.put(requestId, tabName);
					if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString()))
						data = exec.executeAndFetch(
								"SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit,
								null);
					else if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
						data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum <= " + limit, null);
					else
						data = exec.executeAndFetch("SELECT * FROM " + tabName + " LIMIT " + limit, null);
				}
			}
		} else {
			if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
					|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString()))
				data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM ("
						+ vizExec.getSql() + ") tn ) AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit,
						null);
			else if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
				data = exec.executeAndFetch("SELECT * FROM (" + vizExec.getSql() + ") vizpod WHERE rownum <= " + limit,
						null);
			else
				data = exec.executeAndFetch("SELECT * FROM (" + vizExec.getSql() + ") vizpod LIMIT " + limit, null);
		}
		/**** Get sql and update in vizpodexec - START ****/
		/* df.cache(); */
		MetaIdentifierHolder vizpodRef = new MetaIdentifierHolder();
		vizpodRef.setRef(new MetaIdentifier(MetaType.vizpod, vizpodUUID, vizpodVersion));
		vizExec.setDependsOn(vizpodRef);
		// vizExecServiceImpl.save(vizExec);
		// commonServiceImpl.save(MetaType.vizExec.toString(), vizExec);
		/**** Get sql and update in vizpodexec - END ****/
		VizpodDetailsHolder resultHolder = new VizpodDetailsHolder(data, vizExec);
		return resultHolder;
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
	public VizpodResultHolder getVizpodResults(String vizpodUUID, String vizpodVersion, ExecParams execParams, 
												int rowLimit, int offset, int limit, String sortBy, String order,String requestId, RunMode runMode) throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		return getVizpodResults(vizpodUUID, vizpodVersion, execParams, new VizExec(), 
										rowLimit, offset, limit, sortBy, order, requestId, runMode);
	}
	
	public VizpodDetailsHolder getVizpodDetails(String vizpodUUID, String vizpodVersion, ExecParams execParams,
												int rowLimit, int offset, int limit, String sortBy, String order, String requestId, RunMode runMode)
															throws IOException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {

		return getVizpodDetails(vizpodUUID, vizpodVersion, execParams, new VizExec(), rowLimit, offset, limit,
				sortBy, order, requestId, runMode);
	}
	

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

		/********************** UNUSED **********************/
	/*public List<Vizpod> resolveName(List<Vizpod> vizpod) throws JsonProcessingException {
		List<Vizpod> vizpodList = new ArrayList<>();
		for(Vizpod viz : vizpod){
			String createdByRefUuid = viz.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			viz.getCreatedBy().getRef().setName(user.getName());
			vizpodList.add(viz);
		}
		return vizpodList;
	}

	*/
	/********************** UNUSED **********************/
	/*public List<Vizpod> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iVizpodDao.findAllVersion(appUuid, uuid);
		}
		else
		return iVizpodDao.findAllVersion(uuid);
	}*/

		/********************** UNUSED **********************/
	/*public Vizpod getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iVizpodDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iVizpodDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

		/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Vizpod vizpod) throws Exception{
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Vizpod vizNew = new Vizpod();
		vizNew.setName(vizpod.getName()+"_copy");
		vizNew.setActive(vizpod.getActive());		
		vizNew.setDesc(vizpod.getDesc());		
		vizNew.setTags(vizpod.getTags());	
		vizNew.setDimension(vizpod.getDimension());
		vizNew.setTitle(vizpod.getTitle());
		vizNew.setFilterInfo(vizpod.getFilterInfo());
		vizNew.setGroups(vizpod.getGroups());
		vizNew.setKeys(vizpod.getKeys());
		vizNew.setValues(vizpod.getValues());
		vizNew.setSource(vizpod.getSource());
		vizNew.setLimit(vizpod.getLimit());		
		save(vizNew);
		ref.setType(MetaType.vizpod);
		ref.setUuid(vizNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

		/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> vizpodList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity vizpod : vizpodList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = vizpod.getId();
			String uuid = vizpod.getUuid();
			String version = vizpod.getVersion();
			String name = vizpod.getName();
			String desc = vizpod.getDesc();
			String published=vizpod.getPublished();
			MetaIdentifierHolder createdBy = vizpod.getCreatedBy();
			String createdOn = vizpod.getCreatedOn();
			String[] tags = vizpod.getTags();
			String active = vizpod.getActive();
			List<MetaIdentifierHolder> appInfo = vizpod.getAppInfo();
			baseEntity.setId(id);
			baseEntity.setUuid(uuid);
			baseEntity.setVersion(version);
			baseEntity.setName(name);
			baseEntity.setDesc(desc);
			baseEntity.setCreatedBy(createdBy);
			baseEntity.setCreatedOn(createdOn);
			baseEntity.setPublished(published);
			baseEntity.setTags(tags);
			baseEntity.setActive(active);
			baseEntity.setAppInfo(appInfo);
			baseEntityList.add(baseEntity);
		}
		return baseEntityList;
	}*/
		
		
		
	public HttpServletResponse download(String uuid, String version, String format,ExecParams execParams, String download, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
			RunMode runMode) throws Exception {

		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if(rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of "+maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
		}
		
		VizpodResultHolder resultHolder = getVizpodResults(uuid, version, execParams, rowLimit, offset, limit, sortBy, order, requestId, runMode);
		List<Map<String, Object>> results = resultHolder.getVizpodResultDataList();
		response = commonServiceImpl.download(uuid, version, format, offset, limit, response, rowLimit, sortBy, order, requestId, runMode, results,MetaType.downloadExec,new MetaIdentifierHolder(new MetaIdentifier(MetaType.vizpod,uuid,version)));
		
	/*	try {
			FileOutputStream fileOut = null;
			response.setContentType("application/xml charset=utf-16");
			response.setHeader("Content-type", "application/xml");
			HSSFWorkbook workbook = WorkbookUtil.getWorkbook(results);

			String downloadPath = Helper.getPropertyValue("framework.file.download.path");
			response.addHeader("Content-Disposition", "attachment; filename=" + uuid + ".xlsx");
			ServletOutputStream os = response.getOutputStream();
			workbook.write(os);

			fileOut = new FileOutputStream(downloadPath + "/" + uuid + "_" + version + ".xlsx");
			workbook.write(fileOut);
			os.write(workbook.getBytes());
			os.close();
			fileOut.close();

		} catch (IOException e1) {
			e1.printStackTrace();
			logger.info("exception caught while download file");
		}*/
		return response;

	}

}
