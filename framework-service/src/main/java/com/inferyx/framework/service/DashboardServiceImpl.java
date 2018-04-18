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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.inferyx.framework.dao.IDashboardDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Dashboard;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;
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

	