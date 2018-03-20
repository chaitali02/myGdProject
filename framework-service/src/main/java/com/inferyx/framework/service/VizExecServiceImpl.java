/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IVizpodExecDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.domain.Vizpod;
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
	VizExecServiceImpl vizExecServiceImpl;
	@Autowired
	VizpodServiceImpl vizpodServiceImpl;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	
	static final Logger logger = Logger.getLogger(VizExecServiceImpl.class);	

	/********************** UNUSED **********************/
	/*public VizExec findLatest() {
		VizExec vizexec=null;
		if(iVizpodExec.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			vizexec=resolveName(iVizpodExec.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return vizexec ;
	}*/

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
	/*public List<VizExec> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iVizpodExec.findAll(); 
		}
		return iVizpodExec.findAll(appUuid);
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
	/*public VizExec save(VizExec vizExec) throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		vizExec.setAppInfo(metaIdentifierHolderList);
		vizExec.setBaseEntity();
		VizExec vizExecDet = iVizpodExec.save(vizExec);		
		registerGraph.updateGraph((Object) vizExecDet, MetaType.vizExec);		
		return iVizpodExec.save(vizExec);		
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
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		VizExec vizExec = iVizpodExec.findOneById(appUuid,id);
		vizExec.setActive("N");
		iVizpodExec.save(vizExec);
		String ID=vizExec.getId();
		iVizpodExec.delete(ID);		
	}*/

	/********************** UNUSED **********************/
	/*public List<VizExec> findAllLatest()

	{		
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Aggregation vizExecAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<VizExec> vizExecResults = mongoTemplate.aggregate(vizExecAggr, "vizexec", VizExec.class);
		List<VizExec> vizExecList = vizExecResults.getMappedResults();
		// Fetch the VizExec details for each id
		List<VizExec> result = new ArrayList<VizExec>();
		for (VizExec v : vizExecList) {
			VizExec vizExecLatest;
				String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
				if(appUuid != null)
				{
				//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();;
					vizExecLatest = iVizpodExec.findOneByUuidAndVersion(appUuid,v.getId(), v.getVersion());
				}
				else
				{
					vizExecLatest = iVizpodExec.findOneByUuidAndVersion(v.getId(), v.getVersion());
				}
				//logger.debug("datapodLatest is " + datapodLatest.getName());
				result.add(vizExecLatest);
		}	
		return result;
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
	/*public List<VizExec> resolveName(List<VizExec> vizExec) {
		List<VizExec> vizExecList = new ArrayList<VizExec>();
		for(VizExec v : vizExec)
		{
			String createdByRefUuid = v.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			v.getCreatedBy().getRef().setName(user.getName());
			vizExecList.add(v);
		}
		
		return vizExecList;
	}*/

	/********************** UNUSED **********************/
	/*public VizExec findOneByUuidAndVersion(String uuid, String version) {		
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if (appUuid == null) {
			return iVizpodExec.findOneByUuidAndVersion(uuid, version);
		}
		return iVizpodExec.findOneByUuidAndVersion(appUuid,uuid, version);
		
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
	/*public List<VizExec> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iVizpodExec.findAllVersion(appUuid, uuid);
		}
		else
		return iVizpodExec.findAllVersion(uuid);
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

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(VizExec vizExec) throws JsonProcessingException, JSONException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		VizExec vizExecNew = new VizExec();
		vizExecNew.setName(vizExec.getName()+"_copy");
		vizExecNew.setActive(vizExec.getActive());		
		vizExecNew.setDesc(vizExec.getDesc());		
		vizExecNew.setTags(vizExec.getTags());	
		vizExecNew.setDependsOn(vizExec.getDependsOn());
		vizExecNew.setExecParams(vizExec.getExecParams());
		vizExecNew.setSql(vizExec.getSql());
		save(vizExecNew);
		ref.setType(MetaType.vizExec);
		ref.setUuid(vizExecNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> vizExecList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity vizExec : vizExecList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = vizExec.getId();
			String uuid = vizExec.getUuid();
			String version = vizExec.getVersion();
			String name = vizExec.getName();
			String desc = vizExec.getDesc();
			String published=vizExec.getPublished();
			MetaIdentifierHolder createdBy = vizExec.getCreatedBy();
			String createdOn = vizExec.getCreatedOn();
			String[] tags = vizExec.getTags();
			String active = vizExec.getActive();
			List<MetaIdentifierHolder> appInfo = vizExec.getAppInfo();
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

}