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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.dao.IDashboardDao;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.Dashboard;
import com.inferyx.framework.domain.DashboardExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Section;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DashboardServiceImpl extends RuleTemplate {
	
	static final Logger logger = Logger.getLogger(DashboardServiceImpl.class);
	
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IDashboardDao iDashboardDao;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	RegisterService registerService;
    @Autowired
    FilterServiceImpl filterServiceImpl;
    @Autowired
    DatapodServiceImpl datapodServiceImpl;
    @Autowired
    ApplicationServiceImpl applicationServiceImpl; 
    @Autowired
    RelationServiceImpl relationServiceImpl;
    @Autowired
    private CommonServiceImpl<?> commonServiceImpl;
    @Autowired
    private VizpodServiceImpl vizpodServiceImpl;
	@Autowired
	private SessionHelper sessionHelper;
    
	/********************** UNUSED **********************/
    /*public Dashboard findLatest() {
		return resolveName(iDashboardDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/
    
	public List<Dashboard> findAll() {
		return iDashboardDao.findAll();
	}
	
	public Dashboard save(Dashboard dashboard) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		dashboard.setAppInfo(metaIdentifierHolderList);
		dashboard.setBaseEntity();
		Dashboard dshboard=iDashboardDao.save(dashboard);
		registerGraph.updateGraph((Object) dshboard, MetaType.dashboard);
		return dshboard;
	}

	/**
	 * @param dashboardUuid
	 * @param dashboardVersion
	 * @param dashboardExec
	 * @param execParams
	 * @param runMode
	 * @return DashboardExec
	 * @throws Exception 
	 */
	public DashboardExec create(String dashboardUuid, String dashboardVersion, DashboardExec dashboardExec,
			ExecParams execParams, RunMode runMode) throws Exception {
		if(dashboardExec == null) {
			dashboardExec = new DashboardExec();
			dashboardExec.setBaseEntity();
		}
		
		Dashboard dashboard = (Dashboard) commonServiceImpl.getOneByUuidAndVersion(dashboardUuid, dashboardVersion, MetaType.dashboard.toString(), "N");
		dashboardExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.dashboard, dashboardUuid, dashboard.getVersion())));
		dashboardExec.setName(dashboard.getName());
		dashboardExec.setAppInfo(dashboard.getAppInfo());
		dashboardExec.setExecParams(execParams);
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		dashboardExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.PENDING);
		return dashboardExec;
	}

	public DashboardExec parse(String execUuid, String execVersion, ExecParams execParams, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {

		DashboardExec dashboardExec = (DashboardExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.dashboardExec.toString(), "N");
		MetaIdentifier dependsOnMI = dashboardExec.getDependsOn().getRef();
		Dashboard dashboard = (Dashboard) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
		
		List<MetaIdentifierHolder> vizExecInfo = new ArrayList<>();
		for(Section section : dashboard.getSectionInfo()) {

			VizExec vizExec = null;
			try {
				MetaIdentifier vizInfoMI = section.getVizpodInfo().getRef();
				vizExec = vizpodServiceImpl.create(vizInfoMI.getUuid(), vizInfoMI.getVersion(), null, execParams, runMode);
				vizExec = vizpodServiceImpl.parse(vizExec.getUuid(), vizExec.getVersion(), execParams, null, null, null, null, runMode);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(vizExec != null) {
				vizExecInfo.add(new MetaIdentifierHolder(new MetaIdentifier(MetaType.vizExec, vizExec.getUuid(), vizExec.getVersion())));
			}
		}		
		dashboardExec.setVizExecInfo(vizExecInfo);
		synchronized (dashboardExec.getUuid()) {
			dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.STARTING);
			dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.READY);
		}
		
		return dashboardExec;
	}
	
	/**
	 * @param dashboardUuid
	 * @param dashboardVersion
	 * @param dashboardExec
	 * @param execParams
	 * @param runMode
	 * @return DashboardExec
	 * @throws Exception 
	 */
	public DashboardExec execute(String execUuid, String execVersion,
			ExecParams execParams, RunMode runMode) throws Exception {
		DashboardExec dashboardExec = null;
		try {
			dashboardExec = (DashboardExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.dashboardExec.toString(), "N");
			MetaIdentifier dependsOnMI = dashboardExec.getDependsOn().getRef();
			Dashboard dashboard = (Dashboard) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
			
			RunDashboardServiceImpl runDashboardServiceImpl = new RunDashboardServiceImpl();
			runDashboardServiceImpl.setDashboardExec(dashboardExec);
			runDashboardServiceImpl.setDashboard(dashboard);
			runDashboardServiceImpl.setCommonServiceImpl(commonServiceImpl);
			runDashboardServiceImpl.setVizpodServiceImpl(vizpodServiceImpl);
			runDashboardServiceImpl.setRunMode(runMode);
			runDashboardServiceImpl.setExecParams(execParams);
			runDashboardServiceImpl.setSessionContext(sessionHelper.getSessionContext());
			runDashboardServiceImpl.setName(MetaType.dashboardExec+"_"+dashboardExec.getUuid()+"_"+dashboardExec.getVersion());
			runDashboardServiceImpl.call();
		} catch (Exception e) {
			e.printStackTrace();
			dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.FAILED);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.dashboardExec, dashboardExec.getUuid(), dashboardExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Dashboard execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Dashboard execution FAILED.");	
		}
		return dashboardExec;
	}

	/**
	 * @param execUuid
	 * @param execVersion
	 * @return ExecStatsHolder
	 * @throws JsonProcessingException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object exec = commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		MetaIdentifierHolder resultHolder = (MetaIdentifierHolder) exec.getClass().getMethod("getResult").invoke(exec);
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion(), MetaType.datastore.toString(), "N");
		MetaIdentifier mi = new MetaIdentifier(MetaType.datastore, resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion());
		ExecStatsHolder execHolder = new ExecStatsHolder();
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}

	/**
	 * @param execUuid
	 * @param execVersion
	 * @param type
	 * @return MetaIdentifier
	 * @throws JsonProcessingException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object exec = commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		MetaIdentifierHolder miHolder = (MetaIdentifierHolder) exec.getClass().getMethod("getDependsOn").invoke(exec);
		return miHolder.getRef();
	}

	/**
	 * @param dashboardUuid
	 * @param dashboardVersion
	 * @param saveonRefresh
	 * @return
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws JsonProcessingException 
	 */
	public List<DashboardExec> getDasboardExecBySave(String dashboardUuid, String dashboardVersion, String saveonRefresh) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		MatchOperation uuidFilter = match(new Criteria("dependsOn.ref.uuid").is(dashboardUuid)
				.andOperator(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()), Criteria.where("statusList.stage").in(Status.Stage.COMPLETED.toString())));
		
		MatchOperation versionFilter = null;
		if(dashboardVersion != null && !dashboardVersion.isEmpty()) {
			versionFilter = match(new Criteria("dependsOn.ref.version").is(dashboardVersion));
		}

		GroupOperation groupByUuid = group("uuid").max("version").as("version");
		
		Aggregation aggregation = null;
		if(dashboardVersion != null && !dashboardVersion.isEmpty()) {
			aggregation = newAggregation(uuidFilter, versionFilter, groupByUuid);
		} else {
			aggregation = newAggregation(uuidFilter, groupByUuid);
		}
		AggregationResults<DashboardExec> aggregationResults = mongoTemplate.aggregate(aggregation, MetaType.dashboardExec.toString().toLowerCase(), DashboardExec.class);
		List<DashboardExec> dashExecList = aggregationResults.getMappedResults();
		
		List<DashboardExec> dashExecListBySave = new ArrayList<>();
		for(DashboardExec dashboardExec : dashExecList) {
			DashboardExec resolvedDashExec = (DashboardExec) commonServiceImpl.getOneByUuidAndVersion(dashboardExec.getId(), dashboardExec.getVersion(), MetaType.dashboardExec.toString(), "N");
			if(resolvedDashExec != null) {					
				if(saveonRefresh != null && !saveonRefresh.isEmpty()) {
					MetaIdentifier dependsOnMI = resolvedDashExec.getDependsOn().getRef();
					Dashboard dashboard = (Dashboard) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
					if(saveonRefresh.equalsIgnoreCase(dashboard.getSaveOnRefresh())) {
						dashExecListBySave.add(resolvedDashExec);
					}
				} else {
					dashExecListBySave.add(resolvedDashExec);
				}
			}			
		}
		
		return dashExecListBySave;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(baseExec.getUuid(), baseExec.getVersion(), execParams, runMode);
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		synchronized (baseExec.getUuid()) {
			baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.dashboardExec, Status.Stage.STARTING);
		}
		synchronized (baseExec.getUuid()) {
			baseExec = (DashboardExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.dashboardExec, Status.Stage.READY);
		}
		return baseExec;
	}

	@Override
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws Exception {
		BaseRuleExec baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.dashboardExec.toString(), "N");
		synchronized (execUuid) {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, MetaType.dashboardExec, Status.Stage.STARTING);
		}
		synchronized (execUuid) {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, MetaType.dashboardExec, Status.Stage.READY);
		}
		return baseRuleExec;
	}

	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec,
			MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode)
			throws Exception {
		return execute(baseRuleExec.getUuid(), baseRuleExec.getVersion(), execParams, runMode);
	}

	/********************** UNUSED **********************/
	/*public Dashboard findOneById(String id) {
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDashboardDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public Dashboard findLatestByUuid(String uuid) {
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDashboardDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String Id) {
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Dashboard dashboard = iDashboardDao.findOne(Id);
		String ID = dashboard.getId();
		iDashboardDao.delete(ID);
		dashboard.setBaseEntity();
	}*/

	/********************** UNUSED **********************/
	/*public List<Dashboard> findAllLatestActive() 	
	{	  
		//String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;		
	   Aggregation dagExecAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Dashboard> dagExecResults = mongoTemplate.aggregate(dagExecAggr,"dashboard", Dashboard.class);	   
	   List<Dashboard> dagExecList = dagExecResults.getMappedResults();

	   // Fetch the dashboard details for each id
	   List<Dashboard> result=new  ArrayList<Dashboard>();
	   for(Dashboard s : dagExecList)
	   {   
		   Dashboard  dagExecLatest = iDashboardDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
		   if(appUuid != null)
		   {
			   dagExecLatest = iDagExec.findOneByUuidAndVersion(appUuid,s.getId(),s.getVersion());
		   }
		   else
		   {
			   dagExecLatest = iDagExec.findOneByUuidAndVersion(s.getId(), s.getVersion());
		   }		   
		   result.add(dagExecLatest);
	   }
	   return result;
	}
	*/

	/********************** UNUSED **********************/
	/*public Dashboard getLatestDashboardByName(String name) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)	{
			return iDashboardDao.findLatestByName(appUuid, name, new Sort(Sort.Direction.DESC, "version"));
		} else {
			return iDashboardDao.findLatestByName(name, new Sort(Sort.Direction.DESC, "version"));
		}
	}*/


}

	