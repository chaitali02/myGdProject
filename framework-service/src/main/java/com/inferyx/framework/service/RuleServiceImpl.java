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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IRuleDao;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.operator.RuleOperator;
import com.inferyx.framework.register.GraphRegister;

@Service
public class RuleServiceImpl extends RuleTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
	/*
	 * @Autowired JavaSparkContext javaSparkContext;
	 */
	@Autowired
	IRuleDao iRuleDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private RuleOperator ruleOperator;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	MapServiceImpl mapServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	/*
	 * @Autowired private IRuleExecDao iRuleExecDao;
	 */
	/*
	 * @Autowired private IRuleGroupExecDao iRuleGroupExecDao;
	 */
	/*
	 * @Autowired private ParamSetServiceImpl paramSetServiceImpl;
	 */
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	RuleExecServiceImpl ruleExecServiceImpl;
	@Resource(name = "taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	@Autowired
	ConnectionFactory connFactory;
	@Autowired
	MessageServiceImpl messageServiceImpl;

	Map<String, List<Map<String, Object>>> requestMap = new HashMap<>();


	static final Logger logger = Logger.getLogger(RuleServiceImpl.class);

	/********************** UNUSED **********************/
	/*
	 * public Rule findLatest() { return resolveName(iRuleDao.findLatest(new
	 * Sort(Sort.Direction.DESC, "version"))); }
	 */

	/********************** UNUSED **********************/
	/*
	 * public Rule findOneById(String id) { String appUuid =
	 * (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null) ?
	 * securityServiceImpl.getAppInfo().getRef().getUuid() : null; if (appUuid ==
	 * null) { return iRuleDao.findOneById(appUuid, id); } return
	 * iRuleDao.findOne(id); }
	 */

	/********************** UNUSED **********************/
	/*
	 * public List<Rule> findAll() { String appUuid =
	 * (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null) ?
	 * securityServiceImpl.getAppInfo().getRef().getUuid() : null; if (appUuid ==
	 * null) { return iRuleDao.findAll(); } return iRuleDao.findAll(appUuid); }
	 */

	/**********************
	 * UNUSED
	 * 
	 * @throws JsonProcessingException
	 **********************/
	/*
	 * public List<Rule> resolveName(List<Rule> rule) { List<Rule> ruleList = new
	 * ArrayList<>(); for (Rule ruleS : rule) { String createdByRefUuid =
	 * ruleS.getCreatedBy().getRef().getUuid(); User user =
	 * userServiceImpl.findLatestByUuid(createdByRefUuid);
	 * ruleS.getCreatedBy().getRef().setName(user.getName()); ruleList.add(ruleS); }
	 * return ruleList; }
	 */

	public Rule resolveName(Rule rule) throws JsonProcessingException {
		String createdByRefUuid = rule.getCreatedBy().getRef().getUuid();
		// User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		rule.getCreatedBy().getRef().setName(user.getName());
		return rule;
	}

	/********************** UNUSED **********************/
	/*
	 * public Rule update(Rule rule) { rule.setBaseEntity(); return
	 * iRuleDao.save(rule); }
	 */

	/********************** UNUSED **********************/
	/*
	 * public boolean isExists(String id) { return iRuleDao.exists(id); }
	 */

	/********************** UNUSED **********************/
	/*
	 * public void delete(String Id) { String appUuid =
	 * securityServiceImpl.getAppInfo().getRef().getUuid(); Rule rule =
	 * iRuleDao.findOneById(appUuid, Id); rule.setActive("N"); iRuleDao.save(rule);
	 * // // * String ID = rule.getId(); iRuleDao.delete(ID); // *
	 * rule.exportBaseProperty(); // }
	 */

	/********************** UNUSED **********************/
	/*
	 * public List<Rule> test(String param1) { return iRuleDao.test(param1); }
	 */

	/********************** UNUSED **********************/
	/*
	 * public List<Rule> findAllByUuid(String uuid) { String appUuid =
	 * securityServiceImpl.getAppInfo().getRef().getUuid(); return
	 * iRuleDao.findAllByUuid(appUuid, uuid);
	 * 
	 * }
	 */

	/********************** UNUSED **********************/
	/*
	 * public Rule findOneByUuidAndVersion(String uuid, String version) { String
	 * appUuid = (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null) ?
	 * securityServiceImpl.getAppInfo().getRef().getUuid() : null; if (appUuid ==
	 * null) { return iRuleDao.findOneByUuidAndVersion(uuid, version); } return
	 * iRuleDao.findOneByUuidAndVersion(appUuid, uuid, version);
	 * 
	 * }
	 */

	/********************** UNUSED **********************/
	/*
	 * public Rule findLatestByUuid(String uuid) { String appUuid =
	 * (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null) ?
	 * securityServiceImpl.getAppInfo().getRef().getUuid() : null; if (appUuid ==
	 * null) { return iRuleDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC,
	 * "version")); } return iRuleDao.findLatestByUuid(appUuid, uuid, new
	 * Sort(Sort.Direction.DESC, "version")); }
	 */

	/********************** UNUSED **********************/
	/*
	 * public List<Rule> findAllLatest() {
	 * 
	 * Aggregation ruleAggr =
	 * newAggregation(group("uuid").max("version").as("version"));
	 * AggregationResults<Rule> dagResults = mongoTemplate.aggregate(ruleAggr,
	 * "rule", Rule.class); List<Rule> RuleList = dagResults.getMappedResults();
	 * 
	 * // Fetch the datapod details for each id List<Rule> result = new
	 * ArrayList<Rule>(); for (Rule s : RuleList) { String appUuid =
	 * (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null) ?
	 * securityServiceImpl.getAppInfo().getRef().getUuid() : null; Rule ruleLatest;
	 * if (appUuid != null) { ruleLatest = iRuleDao.findOneByUuidAndVersion(appUuid,
	 * s.getId(), s.getVersion());
	 * 
	 * } else { ruleLatest = iRuleDao.findOneByUuidAndVersion(s.getId(),
	 * s.getVersion()); // result.add(ruleLatest); } if (ruleLatest != null) {
	 * result.add(ruleLatest); } } return result; }
	 */

	/********************** UNUSED **********************/
	/*
	 * public List<Rule> findAllLatestActive() { Aggregation ruleAggr =
	 * newAggregation(match(Criteria.where("active").is("Y")),
	 * match(Criteria.where("name").ne(null)),
	 * group("uuid").max("version").as("version")); AggregationResults<Rule>
	 * ruleResults = mongoTemplate.aggregate(ruleAggr, "rule", Rule.class);
	 * List<Rule> ruleList = ruleResults.getMappedResults();
	 * 
	 * // Fetch the rule details for each id List<Rule> result = new
	 * ArrayList<Rule>(); for (Rule r : ruleList) { Rule ruleLatest; String appUuid
	 * = (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null) ?
	 * securityServiceImpl.getAppInfo().getRef().getUuid() : null; if (appUuid !=
	 * null) { ruleLatest = iRuleDao.findOneByUuidAndVersion(appUuid, r.getId(),
	 * r.getVersion()); } else { ruleLatest =
	 * iRuleDao.findOneByUuidAndVersion(r.getId(), r.getVersion()); }
	 * 
	 * result.add(ruleLatest); } return result; }
	 */

	/********************** UNUSED **********************/
//	public Rule save(RuleView ruleView) throws Exception {
//		List<AttributeRefHolder> filterList = new ArrayList<AttributeRefHolder>();
//		/* List<AttributeMap> attrMapList = new ArrayList<AttributeMap>(); */
//		AttributeRefHolder filterInfo = new AttributeRefHolder();
//		if (ruleView == null)
//			return null;
//		Rule rule = new Rule();
//		if (StringUtils.isNotBlank(ruleView.getUuid()))
//			rule.setUuid(ruleView.getUuid());
//		// save(rule);
//		// rule.exportBaseProperty();
//		if (ruleView.getTags() != null)
//			rule.setTags(ruleView.getTags());
//		if (StringUtils.isNotBlank(ruleView.getName()))
//			rule.setName(ruleView.getName());
//		if (StringUtils.isNotBlank(ruleView.getDesc()))
//			rule.setDesc(ruleView.getDesc());
//		Filter filter = null;
//		MetaIdentifierHolder source = ruleView.getSource();
//		source.getRef().setVersion(null);
//		rule.setSource(source);
//		if (ruleView.getFilter() != null) {
//			filter = ruleView.getFilter();
//			filter.setDependsOn(source);
//			filter.setName(ruleView.getName());
//			filter.setDesc(ruleView.getDesc());
//			filter.setTags(ruleView.getTags());
//			if (ruleView.getFilterChg().equalsIgnoreCase("y") && filter != null) {
//				try {
//					// filterdet = filterServiceImpl.save(filter);
//		         commonServiceImpl.save(MetaType.filter.toString(), filter);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//		if (filter != null) {
//			MetaIdentifier filterMeta = new MetaIdentifier(MetaType.filter, filter.getUuid(), null);
//			filterInfo.setRef(filterMeta);
//			filterList.add(filterInfo);
//			rule.setFilterInfo(filterList);
//		}
//		List<AttributeSource> sourceAttr = ruleView.getAttributeInfo();
//		rule.setAttributeInfo(sourceAttr);
//		rule.setParamList(ruleView.getParamList());
//		rule.setPublished(ruleView.getPublished());
//		rule = save(rule);
//		return rule;
//	}

	/********************** UNUSED **********************/
//	public Rule save(Rule rule) throws Exception {
//		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
//		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
//		metaIdentifierHolderList.add(meta);
//		rule.setAppInfo(metaIdentifierHolderList);
//		rule.setBaseEntity();
//		Rule ruleDet = iRuleDao.save(rule);
//		registerGraph.updateGraph((Object) ruleDet, MetaType.rule);
//		return ruleDet;
//	}

	/*
	 * public List<MetaIdentifier> execute(String ruleUUID, String ruleVersion,
	 * ExecParams execParams, RuleExec ruleExec, RuleGroupExec ruleGroupExec,
	 * List<FutureTask> taskList, ThreadPoolTaskExecutor metaExecutor) {
	 * List<MetaIdentifier> ruleExecMetaList = new ArrayList<>();
	 * MetaIdentifierHolder ruleExecMeta = new MetaIdentifierHolder();
	 * MetaIdentifier ruleExecInfo = new MetaIdentifier(MetaType.rule, ruleUUID,
	 * ruleVersion); ruleExecMeta.setRef(ruleExecInfo); RuleExec ruleExec = new
	 * RuleExec(); ruleExec.setDependsOn(ruleExecMeta); ruleExec.setBaseEntity(); if
	 * (execParams != null && execParams.getParamInfo() != null &&
	 * !execParams.getParamInfo().isEmpty()) { for (ParamSetHolder paramSetHolder :
	 * execParams.getParamInfo()) { execParams.setParamSetHolder(paramSetHolder);
	 * //List<ParamListHolder> paramListHolder =
	 * paramSetServiceImpl.getParamListHolder(paramSetHolder); ruleExec =
	 * create(ruleUUID, ruleVersion, null, execParams, ruleExec); ruleExec =
	 * execute(ruleUUID, ruleVersion, ruleExec, null, taskList, metaExecutor);
	 * ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(),
	 * ruleExec.getVersion()); ruleExecMetaList.add(ruleExecInfo); } } else {
	 * ruleExec = create(ruleUUID, ruleVersion, null, null, ruleExec); ruleExec =
	 * execute(ruleUUID, ruleVersion, ruleExec, null, taskList, metaExecutor);
	 * ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(),
	 * ruleExec.getVersion()); ruleExecMetaList.add(ruleExecInfo); } return
	 * ruleExecMetaList; }
	 */

	public String getAttributeName(Rule rule, String attributeId) {
		List<AttributeSource> sourceAttrs = rule.getAttributeInfo();
		for (AttributeSource sourceAttr : sourceAttrs) {
			if (sourceAttr.getSourceAttr() != null 
					&& sourceAttr.getAttrSourceId() != null 
					&& sourceAttr.getAttrSourceId().equals(attributeId)) {
				return sourceAttr.getAttrSourceName();
			}
		}
		return null;
	}
	
	public void restart(String type, String uuid, String version, List<FutureTask<TaskHolder>> taskList,
			ThreadPoolTaskExecutor metaExecutor, ExecParams execParams, RunMode runMode) throws Exception {
		// RuleExec ruleExec= ruleExecServiceImpl.findOneByUuidAndVersion(uuid,
		// version);
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version,
				MetaType.ruleExec.toString());		
		try {
			HashMap<String, String> otherParams = null;
			if(execParams != null) 
				otherParams = execParams.getOtherParams();
			
			ruleExec = parse(ruleExec.getUuid(), ruleExec.getVersion(), null, otherParams, null, null, runMode);			
			execute(metaExecutor, ruleExec, null, taskList, execParams, runMode);
			
		} catch (Exception e) {
			synchronized (ruleExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.Failed);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Business Rule.", dependsOn);
					throw new Exception((message != null) ? message : "Can not parse Business Rule.");
				}
			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Business Rule.", dependsOn);
			throw new Exception((message != null) ? message : "Can not parse Business Rule.");
		}
	}

	/**
	 * Create using template method pattern
	 * 
	 * @param ruleUUID
	 * @param ruleVersion
	 * @param ruleExec
	 * @param refKeyMap
	 * @param execParams
	 * @param datapodList
	 * @param dagExec
	 * @return
	 * @throws Exception 
	 */
	public RuleExec create(String ruleUUID, String ruleVersion, RuleExec ruleExec,
			java.util.Map<String, MetaIdentifier> refKeyMap, ExecParams execParams, List<String> datapodList,
			DagExec dagExec) throws Exception {
		try {			
			ruleExec = (RuleExec) super.create(ruleUUID, ruleVersion, MetaType.rule, MetaType.ruleExec, ruleExec,
					refKeyMap, datapodList, dagExec);
			if(execParams != null) {
				ruleExec.setExecParams(execParams);
				commonServiceImpl.save(MetaType.ruleExec.toString(), ruleExec);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create Business Rule.", dependsOn);
			throw new Exception((message != null) ? message : "Can not create Business Rule.");
		}
		return ruleExec;
	}

	/*
	 * public RuleExec create(String ruleUUID, String ruleVersion,
	 * java.util.Map<String, MetaIdentifier> refKeyMap, ExecParams execParams,
	 * RuleExec ruleExec) { return create(ruleUUID, ruleVersion, refKeyMap,
	 * execParams, null, null, ruleExec); }
	 * 
	 * public RuleExec create(String ruleUUID, String ruleVersion,
	 * java.util.Map<String, MetaIdentifier> refKeyMap, ExecParams execParams,
	 * List<String> datapodList, DagExec dagExec, RuleExec ruleExec) { try {
	 * ruleExec = (RuleExec) super.create(ruleUUID, ruleVersion, MetaType.rule,
	 * MetaType.ruleExec, ruleExec, refKeyMap, datapodList, dagExec); } catch
	 * (Exception e) { try { commonServiceImpl.setMetaStatus(ruleExec,
	 * MetaType.ruleExec, Status.Stage.Failed); } catch (Exception e1) {
	 * e1.printStackTrace(); } e.printStackTrace(); } return ruleExec; }
	 */
	/**
	 * Executes a rule
	 * 
	 * @param uuid
	 * @param version
	 * @param metaExecutor
	 * @param ruleExec
	 * @param ruleGroupExec
	 * @param taskList
	 * @return
	 * @throws Exception
	 */
	public RuleExec execute(ThreadPoolTaskExecutor metaExecutor, RuleExec ruleExec,
							List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Inside ruleServiceImpl.execute");
		try {
			ruleExec = (RuleExec) super.execute(MetaType.rule, MetaType.ruleExec, metaExecutor, ruleExec,
						ruleExec.getDependsOn().getRef(), taskList, execParams, runMode);
		} catch (Exception e) {
			synchronized (ruleExec.getUuid()) {
				commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.Failed);
			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Business Rule execution failed.", dependsOn);
			throw new Exception((message != null) ? message : "Business Rule execution failed.");
		}
		return ruleExec;
	}

	/*
	 * public RuleExec execute(String ruleUUID, String ruleVersion, RuleExec
	 * ruleExec, RuleGroupExec ruleGroupExec, List<FutureTask> taskList,
	 * ThreadPoolTaskExecutor executorService) {
	 * 
	 * //Fetch the Rule object Rule rule = null; if
	 * (StringUtils.isBlank(ruleVersion)) { rule =
	 * iRuleDao.findLatestByUuid(ruleUUID, new Sort(Sort.Direction.DESC,
	 * "version")); ruleVersion = rule.getVersion(); } else { rule =
	 * iRuleDao.findOneByUuidAndVersion(ruleUUID, ruleVersion); }
	 * 
	 * Authentication authentication =
	 * SecurityContextHolder.getContext().getAuthentication();
	 * 
	 * RunRuleServiceImpl runRuleServiceImpl = new RunRuleServiceImpl();
	 * runRuleServiceImpl.setDataStoreServiceImpl(dataStoreServiceImpl);
	 * runRuleServiceImpl.setHdfsInfo(hdfsInfo);
	 * runRuleServiceImpl.setBaseRule(rule);
	 * runRuleServiceImpl.setBaseGroupExec(ruleGroupExec);
	 * runRuleServiceImpl.setExecFactory(execFactory);
	 * runRuleServiceImpl.setBaseRuleExec(ruleExec);
	 * runRuleServiceImpl.setCommonServiceImpl(commonServiceImpl);
	 * runRuleServiceImpl.setName(MetaType.ruleExec+"_"+ruleExec.getUuid()+"_"+
	 * ruleExec.getVersion());
	 * 
	 * if (executorService == null) { runRuleServiceImpl.execute(); } else {
	 * FutureTask<TaskHolder> futureTask = new
	 * FutureTask<TaskHolder>(runRuleServiceImpl);
	 * executorService.execute(futureTask); taskList.add(futureTask);
	 * taskThreadMap.put(MetaType.ruleExec+"_"+ruleExec.getUuid()+"_"+ruleExec.
	 * getVersion(), futureTask); logger.info(" taskThreadMap : " + taskThreadMap);
	 * }
	 * 
	 * return ruleExec; }
	 */
	
	
	
	
	
	public List<Map<String, Object>> getRuleResults(String ruleExecUUID, String ruleExecVersion, int offset, int limit,
			String sortBy, String order, String requestId, RunMode runMode) throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset + limit;
			offset = offset + 1;
			RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExecUUID, ruleExecVersion,
					MetaType.ruleExec.toString());
			DataStore datastore = dataStoreServiceImpl.getDatastore(ruleExec.getResult().getRef().getUuid(),
					ruleExec.getResult().getRef().getVersion());

			data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order);
			
			/*boolean requestIdExistFlag = false;
			StringBuilder orderBy = new StringBuilder();
			dataStoreServiceImpl.setRunMode(runMode);
			String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastore.getUuid(),
					datastore.getVersion(), runMode);
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			ExecContext execContext = null;
			IExecutor exec = null;
			//String sql = null;
			String appUuid = null;
			if (runMode.equals(Mode.ONLINE)) {
				execContext = helper.getExecutorContext(engine.getExecEngine());
				appUuid = commonServiceImpl.getApp().getUuid();
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			exec = execFactory.getExecutor(execContext.toString());
			if (requestId == null|| requestId.equals("null") || requestId.isEmpty()) {
				if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
					data = exec.executeAndFetch("Select * from (Select Row_Number() Over(ORDER BY 1) as rownum, * from "
							+ tableName + ")as tab where rownum >= " + offset + " AND rownum <= " + limit, appUuid);
				} else {
					if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
						if (runMode.equals(Mode.ONLINE))
							data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit, appUuid);
						else
							data = exec.executeAndFetch("Select * from " + tableName + " where rownum< " + limit,
									appUuid);
					else {
						data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit, appUuid);
					}
				}
			} else {
				List<String> orderList = Arrays.asList(order.split("\\s*,\\s*"));
				List<String> sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));

				if (StringUtils.isNotBlank(sortBy) || StringUtils.isNotBlank(order)) {
					for (int i = 0; i < sortList.size(); i++)
						orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
					///if (requestId != null) {
						String tabName = null;
						for (Map.Entry<String, List<Map<String, Object>>> entry : requestMap.entrySet()) {
							String id = entry.getKey();
							if (id.equals(requestId)) {
								requestIdExistFlag = true;
							}
						}
						if (requestIdExistFlag) {
							data = requestMap.get(requestId);
//							if (datasource.getType().toLowerCase().toLowerCase().contains("spark")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("file")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("hive")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("impala")) {
//								data = exec.executeAndFetch("Select * from " + tabName + " where rownum >= " + offset
//										+ " AND rownum <= " + limit, appUuid);
//							} else {
//								if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
//									if (runMode.equals(Mode.ONLINE))
//										data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//												appUuid);
//									else
//										data = exec.executeAndFetch(
//												"Select * from " + tableName + " where  rownum<" + limit, appUuid);
//								else {
//									data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//											appUuid);
//								}
//							}
						} else {
							if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
								data = exec.executeAndFetch(
										"SELECT * FROM (SELECT Row_Number() Over(ORDER BY "+ orderBy.toString()+") AS rownum, * FROM (SELECT * FROM "
												+ tableName +") as tab) as tab1",
												appUuid);
							} else {
								if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
									if (runMode.equals(Mode.ONLINE))
										data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
												appUuid);
									else
										data = exec.executeAndFetch(
												"Select * from " + tableName + " where  rownum<" + limit, appUuid);
								else {
									data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
											appUuid);
								}
							}

							tabName = requestId.replace("-", "_");
							requestMap.put(requestId, data);
//							if (datasource.getType().toLowerCase().toLowerCase().contains("spark")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("file")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("hive")
//									|| datasource.getType().toLowerCase().toLowerCase().contains("impala")) {
//								data = exec.executeAndFetch("Select * from " + tabName + " where rownum >= " + offset
//										+ " AND rownum <= " + limit, appUuid);
//							} else {
//								if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
//									if (runMode.equals(Mode.ONLINE))
//										data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//												appUuid);
//									else
//										data = exec.executeAndFetch(
//												"Select * from " + tableName + " where  rownum<" + limit, appUuid);
//								else {
//									data = exec.executeAndFetch("Select * from " + tableName + " limit " + limit,
//											appUuid);
//								}
//							}
						}
					//}
				}else {
					if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
							|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
						data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum >= " + offset
								+ " AND rownum <= " + limit, appUuid);
					} else {
						if (datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString()))
							if (runMode.equals(Mode.ONLINE))
								data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit,
										appUuid);
							else
								data = exec.executeAndFetch(
										"SELECT * FROM " + tableName + " WHERE  rownum<" + limit, appUuid);
						else {
							data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit,
									appUuid);
						}
					}
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ruleExec, ruleExecUUID, ruleExecVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", dependsOn);
			throw new Exception((message != null) ? message : "Table not found.");
		}
		return data;
	}

	
	
	
	
	
	/********************** UNUSED **********************/
	/*
	 * public List<Rule> findAllByVersion(String uuid) { String appUuid =
	 * (securityServiceImpl.getAppInfo() != null &&
	 * securityServiceImpl.getAppInfo().getRef() != null) ?
	 * securityServiceImpl.getAppInfo().getRef().getUuid() : null; if (appUuid !=
	 * null) { return iRuleDao.findAllVersion(appUuid, uuid); } else return
	 * iRuleDao.findAllVersion(uuid); }
	 */

	/**
	 * Get the rulename.attributename for use of rule as a dependency of map
	 * 
	 * @param daoRegister
	 * @param rule
	 * @param attributeId
	 * @return
	 */
	public String getAttributeSql(Rule rule, String attributeId) {
		List<AttributeSource> sourceAttrs = rule.getAttributeInfo();
		for (AttributeSource sourceAttr : sourceAttrs) {
			if (sourceAttr.getSourceAttr() != null && sourceAttr.getAttrSourceId() != null
					&& sourceAttr.getAttrSourceId().equals(attributeId)) {
				// logger.info("getAttributeSql(): "+rule.getName() + "." +
				// sourceAttr.getAttrSourceName());
				return rule.getName() + "." + sourceAttr.getAttrSourceName();
			}
		}
		return null;
	}

	/********************** UNUSED **********************/
	/*
	 * public MetaIdentifierHolder saveAs(Rule rule) throws Exception {
	 * MetaIdentifierHolder refMeta = new MetaIdentifierHolder(); MetaIdentifier ref
	 * = new MetaIdentifier(); Rule ruleNew = new Rule();
	 * ruleNew.setName(rule.getName() + "_copy");
	 * ruleNew.setActive(rule.getActive()); ruleNew.setDesc(rule.getDesc());
	 * ruleNew.setTags(rule.getTags());
	 * ruleNew.setExpressionInfo(rule.getExpressionInfo());
	 * ruleNew.setFilterInfo(rule.getFilterInfo());
	 * ruleNew.setDebugMode(rule.isDebugMode());
	 * ruleNew.setAttributeInfo(rule.getAttributeInfo());
	 * ruleNew.setSource(rule.getSource());
	 * ruleNew.setPersistFlag(rule.getPersistFlag());
	 * ruleNew.setDatasource(rule.getDatasource());
	 * ruleNew.setPublished(rule.getPublished()); save(ruleNew);
	 * ref.setType(MetaType.rule); ref.setUuid(ruleNew.getUuid());
	 * refMeta.setRef(ref); return refMeta; }
	 */

	/**
	 * This is an override of BaseRuleService.parse for rule
	 */
	@Override
	public RuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		logger.info("Inside ruleServiceImpl.parse");
		Rule rule = null;
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		// List<Status> statusList = null;
		RuleExec ruleExec = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
				MetaType.ruleExec.toString());
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.Initialized);
		}
		// rule = iRuleDao.findLatestByUuid(ruleExec.getDependsOn().getRef().getUuid(),
		// new Sort(Sort.Direction.DESC, "version"));
		rule = (Rule) commonServiceImpl.getLatestByUuid(ruleExec.getDependsOn().getRef().getUuid(),
				MetaType.rule.toString());
		ruleExec.setExec(ruleOperator.generateSql(rule, refKeyMap, otherParams, usedRefKeySet, ruleExec.getExecParams(), runMode));
		if(rule.getParamList() != null) {
			MetaIdentifier mi = rule.getParamList().getRef();
			ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(mi.getUuid(), mi.getVersion(), mi.getType().toString(), "N");
			usedRefKeySet.add(new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
		}
		ruleExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		logger.info("sql_generated: " + ruleExec.getExec());
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.Ready);
		}
		synchronized (ruleExec.getUuid()) {
//			RuleExec ruleExec1 = (RuleExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion()));
			RuleExec ruleExec1 = (RuleExec) commonServiceImpl.getOneByUuidAndVersion(ruleExec.getUuid(), ruleExec.getVersion(), MetaType.ruleExec.toString(), "N");
			ruleExec1.setExec(ruleExec.getExec());
			ruleExec1.setRefKeyList(ruleExec.getRefKeyList());
			// iRuleExecDao.save(ruleExec1);
			commonServiceImpl.save(MetaType.ruleExec.toString(), ruleExec1);
			ruleExec1 = null;
		}
		
		return ruleExec;
	}

	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor,
			BaseRuleExec baseRuleExec, MetaIdentifier datapodKey,
			List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		return execute(metaExecutor, (RuleExec) baseRuleExec, taskList, execParams, 
				runMode);
	}
	
	
	public HttpServletResponse download(String ruleExecUUID, String ruleExecVersion, String format, String download, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
			RunMode runMode) throws Exception {
		
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if(rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of "+maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
		}
		
		List<Map<String, Object>> results =getRuleResults(ruleExecUUID,ruleExecVersion,offset,limit,sortBy,order,requestId, runMode);
		response = commonServiceImpl.download(ruleExecUUID, ruleExecVersion, format, offset, limit, response, rowLimit, sortBy, order, requestId, runMode, results,MetaType.downloadExec,new MetaIdentifierHolder(new MetaIdentifier(MetaType.ruleExec,ruleExecUUID,ruleExecVersion)));
		
		/*try {
			FileOutputStream fileOut = null;
			response.setContentType("application/xml charset=utf-16");
			response.setHeader("Content-type", "application/xml");
			HSSFWorkbook workbook = WorkbookUtil.getWorkbook(results);

			String downloadPath = Helper.getPropertyValue("framework.file.download.path");
			response.addHeader("Content-Disposition", "attachment; filename=" + ruleExecUUID + ".xlsx");
			ServletOutputStream os = response.getOutputStream();
			workbook.write(os);

			fileOut = new FileOutputStream(downloadPath + "/" + ruleExecUUID + "_" + ruleExecVersion + ".xlsx");
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
	
	@SuppressWarnings({ "unchecked"})
	public List<RuleExec> finddqExecByDatapod(String datapodUUID,String type) throws JsonProcessingException, ParseException {

		List<String> ruleUUIDlist = new ArrayList<String>();
		List<RuleExec> result = new ArrayList<RuleExec>();

		List<Rule> ruleobj = (List<Rule>) commonServiceImpl
				.getAllLatestCompleteObjects(MetaType.rule.toString(), null);
		for (Rule rule : ruleobj) {
			if (datapodUUID.equalsIgnoreCase( rule.getSource().getRef().getUuid())) {
				ruleUUIDlist.add(rule.getUuid().toString());
			}
		}

		List<RuleExec> ruleexecobj = (List<RuleExec>) commonServiceImpl
				.getAllLatestCompleteObjects(MetaType.ruleExec.toString(), null);

		for (RuleExec rulexec : ruleexecobj) {

			for (String profileuuid : ruleUUIDlist) {
				if (profileuuid.equalsIgnoreCase(rulexec.getDependsOn().getRef().getUuid())) {
					result.add(rulexec);
				}
			}
		}

		return result;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		ThreadPoolTaskExecutor metaExecutor = (execParams != null && execParams.getExecutionContext() != null && execParams.getExecutionContext().containsKey("EXECUTOR")) ? (ThreadPoolTaskExecutor)(execParams.getExecutionContext().get("EXECUTOR")) : null;
		List<FutureTask<TaskHolder>> taskList = (execParams != null && execParams.getExecutionContext() != null && execParams.getExecutionContext().containsKey("TASKLIST")) ? (List<FutureTask<TaskHolder>>)(execParams.getExecutionContext().get("TASKLIST")) : null;
		execute(metaExecutor, (RuleExec)baseExec, taskList, execParams, runMode);
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), null, null, runMode);
	}
	
	public List<MetaIdentifier> prepareRule(String ruleUuid, String ruleVersion, ExecParams execParams, RuleExec ruleExec, RunMode runMode) {
		List<FutureTask<TaskHolder>> taskList = new ArrayList<FutureTask<TaskHolder>>();
		List<MetaIdentifier> ruleExecMetaList = new ArrayList<>();
		MetaIdentifierHolder ruleExecMeta = new MetaIdentifierHolder();
		MetaIdentifier ruleExecInfo = new MetaIdentifier(MetaType.rule, ruleUuid, ruleVersion);
		ruleExecMeta.setRef(ruleExecInfo);
		try {
			if (execParams != null) {
				if(execParams.getParamInfo() != null && !execParams.getParamInfo().isEmpty()) {
					for (ParamSetHolder paramSetHolder : execParams.getParamInfo()) {
						if(ruleExec == null) {
							MetaIdentifier ref = paramSetHolder.getRef();
							ref.setType(MetaType.paramset);
							paramSetHolder.setRef(ref);
							execParams.setCurrParamSet(paramSetHolder);
							ruleExec = create(ruleUuid, ruleVersion, null, null, execParams, null, null);		
						}
						ruleExec = parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, null, runMode);
						ruleExec = execute(metaExecutor, ruleExec, taskList, execParams, runMode);
						ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion());
						ruleExecMetaList.add(ruleExecInfo);
						ruleExec = null;
					}
				} else if(execParams.getParamListInfo() != null && !execParams.getParamListInfo().isEmpty()) {
					for (ParamListHolder paramListHolder : execParams.getParamListInfo()) {
						if(ruleExec == null) {
							execParams.setParamListHolder(paramListHolder);
							ruleExec = create(ruleUuid, ruleVersion, null, null, execParams, null, null);
						}
						ruleExec = parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, null, runMode);
						ruleExec = execute(metaExecutor, ruleExec, taskList, execParams, runMode);
						ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion());
						ruleExecMetaList.add(ruleExecInfo);
						ruleExec = null;
					}
				} else {
					if(ruleExec == null)
						ruleExec = create(ruleUuid, ruleVersion, null, null, execParams, null, null);			
					ruleExec = parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, null, runMode);
					ruleExec = execute(metaExecutor, ruleExec, taskList, execParams, runMode);
					ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion());
					ruleExecMetaList.add(ruleExecInfo);
					ruleExec = null;
				}				
			} else {
				if(ruleExec == null)
					ruleExec = create(ruleUuid, ruleVersion, null, null, execParams, null, null);			
				ruleExec = parse(ruleExec.getUuid(), ruleExec.getVersion(), null, null, null, null, runMode);
				ruleExec = execute(metaExecutor, ruleExec, taskList, execParams, runMode);
				ruleExecInfo = new MetaIdentifier(MetaType.ruleExec, ruleExec.getUuid(), ruleExec.getVersion());
				ruleExecMetaList.add(ruleExecInfo);
				ruleExec = null;
			}
		} catch (Exception e) {
			try {
				commonServiceImpl.setMetaStatus(ruleExec, MetaType.ruleExec, Status.Stage.Failed);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		commonServiceImpl.completeTaskThread(taskList);
		return ruleExecMetaList;
	}
}