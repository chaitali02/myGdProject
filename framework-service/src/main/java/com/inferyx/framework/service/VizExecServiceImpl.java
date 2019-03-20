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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.inferyx.framework.dao.IVizpodExecDao;
import com.inferyx.framework.register.GraphRegister;

@Service
public class VizExecServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private IVizpodExecDao iVizpodExec;	
	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	VizpodServiceImpl vizpodServiceImpl;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	
	static final Logger logger = Logger.getLogger(VizExecServiceImpl.class);	

	/********************** UNUSED **********************/
	/*public List<VizExec> findLatestVizpodExec(String vizpodUUID, String vizpodVersion) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iVizpodExec.findLatestVizpodExec(appUuid,vizpodUUID, vizpodVersion);
	}*/

	/********************** UNUSED **********************/
	/*public List<VizExec> findOneByvizpod(String vizpodUUID) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iVizpodExec.findOneByvizpod(appUuid,vizpodUUID);
	}*/

	/********************** UNUSED **********************/
	/*public VizExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
		return iVizpodExec.findOneById(appUuid,id);
		}
		else
			return iVizpodExec.findOne(id);
	}*/
	
	/********************** UNUSED **********************/
	/*public VizExec findLatestByUuid(String vizExecUUID, Sort sort) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iVizpodExec.findLatestByUuid(vizExecUUID,new Sort(Sort.Direction.DESC, "version"));
		}
		return iVizpodExec.findLatestByUuid(appUuid,vizExecUUID,new Sort(Sort.Direction.DESC, "version"));	
	}*/



	/********************** UNUSED **********************/
	/*public List<VizExec> findAllLatestActive() 	
	{	  
	  Aggregation vizExecAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	  AggregationResults<VizExec> vizExecResults = mongoTemplate.aggregate(vizExecAggr,"vizexec", VizExec.class);	  
	  List<VizExec> vizExecList = vizExecResults.getMappedResults();

	  // Fetch the vizExec details for each id
	  List<VizExec> result=new  ArrayList<VizExec>();
	  for(VizExec v : vizExecList)
	  {   
		  VizExec vizExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				vizExecLatest = iVizpodExec.findOneByUuidAndVersion(appUuid,v.getId(), v.getVersion());
			}
			else
			{
				vizExecLatest = iVizpodExec.findOneByUuidAndVersion(v.getId(), v.getVersion());
			}
		
			result.add(vizExecLatest);
	  }
	  return result;
	}*/

	/********************** UNUSED **********************/
/*	public VizExec resolveName(VizExec vizExec) {		
		try {		
			if(vizExec.getCreatedBy() != null)
			{
			String createdByRefUuid = vizExec.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			vizExec.getCreatedBy().getRef().setName(user.getName());
			}
			if (vizExec.getAppInfo() != null) {
				for (int i = 0; i < vizExec.getAppInfo().size(); i++) {
					String appUuid = vizExec.getAppInfo().get(i).getRef().getUuid();
					Application application = applicationServiceImpl.findLatestByUuid(appUuid);
					String appName = application.getName();
					vizExec.getAppInfo().get(i).getRef().setName(appName);
				}
				String dependsOnUuid=vizExec.getDependsOn().getRef().getUuid();
				Vizpod vizpod=vizpodServiceImpl.findLatestByUuid(dependsOnUuid);
				vizExec.getDependsOn().getRef().setName(vizpod.getName());
				for(int i=0;i<vizExec.getRefKeyList().size();i++){
						MetaType type=vizExec.getRefKeyList().get(i).getType();
						if(type.toString().equals(MetaType.relation.toString())){
							String relationUuid=vizExec.getRefKeyList().get(i).getUuid();
							Relation relation=relationServiceImpl.findLatestByUuid(relationUuid);
							vizExec.getRefKeyList().get(i).setName(relation.getName());
						}
						else if(type.toString().equals(MetaType.datapod.toString())){
							String datapodUuid=vizExec.getRefKeyList().get(i).getUuid();
							Datapod datapod=datapodServiceImpl.findLatestByUuid(datapodUuid);
							vizExec.getRefKeyList().get(i).setName(datapod.getName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vizExec;
	}*/

	/********************** UNUSED **********************/
	/*public VizExec findLatestByUuid(String uuid) {		
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if (appUuid == null) {
			return iVizpodExec.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iVizpodExec.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public VizExec getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iVizpodExec.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iVizpodExec.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/



}