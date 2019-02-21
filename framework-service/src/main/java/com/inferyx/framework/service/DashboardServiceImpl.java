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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IDashboardDao;
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
public class DashboardServiceImpl {
	
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
		dashboardExec.setExecParams(execParams);
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		dashboardExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		
		List<MetaIdentifierHolder> vizExecInfo = new ArrayList<>();
		for(Section section : dashboard.getSectionInfo()) {
			try {
				MetaIdentifier vizInfoMI = section.getVizpodInfo().getRef();
				VizExec vizExec = vizpodServiceImpl.create(vizInfoMI.getUuid(), vizInfoMI.getVersion(), null, execParams, runMode);
				vizExecInfo.add(new MetaIdentifierHolder(new MetaIdentifier(MetaType.vizExec, vizExec.getUuid(), vizExec.getVersion())));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		dashboardExec.setVizExecInfo(vizExecInfo);
		dashboardExec.setName(dashboard.getName());
		dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.NotStarted);
	
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
	public DashboardExec execute(String dashboardUuid, String dashboardVersion, DashboardExec dashboardExec,
			ExecParams execParams, RunMode runMode) throws Exception {
		try {
			dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.InProgress);
			
			Dashboard dashboard = (Dashboard) commonServiceImpl.getOneByUuidAndVersion(dashboardUuid, dashboardVersion, MetaType.dashboard.toString(), "N");
			
			for(MetaIdentifierHolder vixExecInfo : dashboardExec.getVizExecInfo()) {
				try {
					MetaIdentifier vizExecMI = vixExecInfo.getRef();
					VizExec vizExec = (VizExec) commonServiceImpl.getOneByUuidAndVersion(vizExecMI.getUuid(), vizExecMI.getVersion(), vizExecMI.getType().toString(), "N");
					MetaIdentifier vizMI = vizExec.getDependsOn().getRef();
					vizExec = vizpodServiceImpl.execute(vizMI.getUuid(), vizMI.getVersion(), execParams, vizExec, dashboard.getSaveOnRefresh(), runMode);	
				} catch (Exception e) {
					logger.info("vizExec execution failed <<<< :: >>>> execUuid: "+vixExecInfo.getRef().getUuid()+" :::: execVersion: "+vixExecInfo.getRef().getVersion());
					e.printStackTrace();
				}
			}
			
			dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.Completed);
		} catch (Exception e) {
			e.printStackTrace();
			dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.Failed);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.dashboardExec, dashboardExec.getUuid(), dashboardExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Dashboard execution failed.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Dashboard execution failed.");	
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

	/********************** UNUSED **********************/
	/*public Dashboard findOneById(String id) {
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDashboardDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<Dashboard> findAllByUuid(String uuid) {
	//	String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDashboardDao.findAllByUuid(uuid);

	}*/

	/********************** UNUSED **********************/
	/*public Dashboard findOneByUuidAndVersion(String uuid, String version) {
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDashboardDao.findOneByUuidAndVersion(uuid, version);
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
	/*public List<Dashboard> findAllLatest() {
		{
			// String appUuid =
			// securityServiceImpl.getAppInfo().getRef().getUuid();;
			Aggregation dashboardAggr = newAggregation(group("uuid").max("version").as("version"));
			AggregationResults<Dashboard> dashboardResults = mongoTemplate.aggregate(dashboardAggr, "dashboard", Dashboard.class);
			List<Dashboard>dashboardList = dashboardResults.getMappedResults();

			// Fetch the dashboard details for each id
			List<Dashboard> result = new ArrayList<Dashboard>();
			for (Dashboard d : dashboardList) {
				Dashboard dashboardLatest = iDashboardDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
				result.add(dashboardLatest);
				String appUuid = (securityServiceImpl.getAppInfo() != null
						&& securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
				if (appUuid != null) {
					// String appUuid =
					// securityServiceImpl.getAppInfo().getRef().getUuid();;
					dagExecLatest = iDagExec.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
				} else {
					dagExecLatest = iDagExec.findOneByUuidAndVersion(s.getId(), s.getVersion());
				}
				// logger.debug("datapodLatest is " + datapodLatest.getName());
				
			}
			return result;
		}
	}
	*/

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
	/*public List<Dashboard> resolveName(List<Dashboard> dashboards) {
		List<Dashboard> dashboardList = new ArrayList<Dashboard>(); 
		for(Dashboard dashboard : dashboards)
		{
			String createdByRefUuid = dashboard.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			dashboard.getCreatedBy().getRef().setName(user.getName());
			dashboardList.add(dashboard);
		}
		return dashboardList;
	}
*/
	/********************** UNUSED **********************/
	/*public Dashboard resolveName(Dashboard dashboards) {
		String createdByRefUuid = dashboards.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		dashboards.getCreatedBy().getRef().setName(user.getName());
		for (int i = 0; i < dashboards.getAppInfo().size(); i++) {
			String appUuid=dashboards.getAppInfo().get(i).getRef().getUuid();
			Application app=applicationServiceImpl.findLatestByUuid(appUuid);
			dashboards.getAppInfo().get(i).getRef().setName(app.getName());
		}
		for(int i=0;i<dashboards.getFilterInfo().size();i++)
		{
			String datapodUuid=dashboards.getFilterInfo().get(i).getRef().getUuid();
			Datapod datapod=datapodServiceImpl.findLatestByUuid(datapodUuid);
			dashboards.getFilterInfo().get(i).getRef().setName(datapod.getName());
			String attrId=dashboards.getFilterInfo().get(i).getAttrId();
			dashboards.getFilterInfo().get(i).setAttrName(datapod.getAttribute(Integer.parseInt(attrId)).getName());
		}
		
		return dashboards;
	}*/

	/********************** UNUSED **********************/
	/*public List<Dashboard> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iDashboardDao.findAllVersion(appUuid, uuid);
		}
		else
		return iDashboardDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public Dashboard getLatestDashboardByName(String name) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)	{
			return iDashboardDao.findLatestByName(appUuid, name, new Sort(Sort.Direction.DESC, "version"));
		} else {
			return iDashboardDao.findLatestByName(name, new Sort(Sort.Direction.DESC, "version"));
		}
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Dashboard dashboard) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Dashboard dashboardNew = new Dashboard();
		dashboardNew.setName(dashboard.getName()+"_copy");
		dashboardNew.setActive(dashboard.getActive());		
		dashboardNew.setDesc(dashboard.getDesc());		
		dashboardNew.setTags(dashboard.getTags());
		dashboardNew.setDependsOn(dashboard.getDependsOn());
		save(dashboardNew);
		ref.setType(MetaType.dashboard);
		ref.setUuid(dashboardNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/
}

	