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

import com.inferyx.framework.dao.IDimensionDao;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DimensionServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IDimensionDao  iDimensionDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	FormulaServiceImpl formulaServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	ConditionServiceImpl conditionServiceImpl;
	/*@Autowired
	GroupServiceImpl groupServiceImpl;*/
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(DimensionServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Dimension findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iDimensionDao.findOneById(appUuid,id);
		}
		return iDimensionDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public Dimension findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDimensionDao.findOneByUuidAndVersion(appUuid,uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Dimension save(Dimension dimension) throws Exception{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		dimension.setAppInfo(metaIdentifierHolderList);
		dimension.setBaseEntity();
		Dimension dimensionDet=iDimensionDao.save(dimension);
		registerGraph.updateGraph((Object) dimensionDet, MetaType.dimension);
		return dimensionDet;
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Dimension dimension = iDimensionDao.findOne(id);
		dimension.setActive("N");
		iDimensionDao.save(dimension);
//		String ID=dimension.getId();
//		iDimensionDao.delete(appUuid,ID);		
	}*/

	/********************** UNUSED **********************/
	/*public List<Dimension> findAllLatestActive() {	   
	   Aggregation dimensionAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Dimension> dimensionResults = mongoTemplate.aggregate(dimensionAggr,"dimension", Dimension.class);	   
	   List<Dimension> dimensionList = dimensionResults.getMappedResults();

	   // Fetch the dimension details for each id
	   List<Dimension> result=new  ArrayList<Dimension>();
	   for(Dimension d : dimensionList)
	   {   
		   Dimension dimensionLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				dimensionLatest = iDimensionDao.findOneByUuidAndVersion(appUuid,d.getId(), d.getVersion());
			}
			else
			{
				dimensionLatest = iDimensionDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
			}
			if(dimensionLatest != null)
			{
			result.add(dimensionLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public Dimension findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iDimensionDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));
		}
		return iDimensionDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public Dimension resolveName(Dimension dimension) throws JsonProcessingException {
		if(dimension.getCreatedBy() != null)
		{
		String createdByRefUuid = dimension.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		dimension.getCreatedBy().getRef().setName(user.getName());
		}
		if (dimension.getAppInfo() != null) {
			for (int i = 0; i < dimension.getAppInfo().size(); i++) {
				String appUuid = dimension.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				dimension.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		String dependsOnRefUuid = dimension.getDependsOn().getRef().getUuid();
		Datapod datapoddependsOnDO = datapodServiceImpl.findLatestByUuid(dependsOnRefUuid);
		
		String datapodName = datapoddependsOnDO.getName();
		dimension.getDependsOn().getRef().setName(datapodName);
		
		String dimUUID = dimension.getUuid();
		Dimension dimDO = findLatestByUuid(dimUUID);
		int attrID = Integer.parseInt(dimDO.getDimInfo().getAttrId());
		String datapodUUID = dimDO.getDimInfo().getRef().getUuid();
		Datapod datapodDO = datapodServiceImpl.findLatestByUuid(datapodUUID);
		List<Attribute> attrList = datapodDO.getAttributes();
		dimension.getDimInfo().setAttrName(attrList.get(attrID).getName());		
		dimension.getDimInfo().getRef().setName(datapodDO.getName());
		return dimension;
	}*/

	/********************** UNUSED **********************/
	/*public Dimension getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iDimensionDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iDimensionDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Dimension dimension) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Dimension dimNew = new Dimension();
		dimNew.setName(dimension.getName()+"_copy");
		dimNew.setActive(dimension.getActive());		
		dimNew.setDesc(dimension.getDesc());		
		dimNew.setTags(dimension.getTags());	
		dimNew.setDimInfo(dimension.getDimInfo());		
		save(dimNew);
		ref.setType(MetaType.dimension);
		ref.setUuid(dimNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> dimensionList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity dimension : dimensionList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = dimension.getId();
			String uuid = dimension.getUuid();
			String version = dimension.getVersion();
			String name = dimension.getName();
			String desc = dimension.getDesc();
			String published=dimension.getPublished();
			MetaIdentifierHolder createdBy = dimension.getCreatedBy();
			String createdOn = dimension.getCreatedOn();
			String[] tags = dimension.getTags();
			String active = dimension.getActive();
			List<MetaIdentifierHolder> appInfo = dimension.getAppInfo();
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