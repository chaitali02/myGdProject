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

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.dao.IParamListDao;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ParamListServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	IParamListDao iParamListDao;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	MetadataUtil daoRegister;
	
	static final Logger logger = Logger.getLogger(ParamListServiceImpl.class);

	/********************** UNUSED **********************/
	/*public ParamList findLatest() {
		return resolveName(iParamListDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public List<ParamList> findAllByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iParamListDao.findAllVersion(uuid);
		}
		return iParamListDao.findAllVersion(appUuid,uuid);
	}*/

	/********************** UNUSED **********************/
	/*public ParamList findLatestByUuid(String uuid){
		return iParamListDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public ParamList findOneByUuidAndVersion(String uuid,String version){
		return iParamListDao.findOneByUuidAndVersion(uuid,version);
	}*/
	/********************** UNUSED **********************/
	/*public ParamList findOneById(String id){
		return iParamListDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<ParamList> findAll(){
		return iParamListDao.findAll();
	}*/
	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		ParamList paramlist = iParamListDao.findOne(Id);
		paramlist.setActive("N");
		iParamListDao.save(paramlist);
//		String ID=application.getId();
//		iApplicationDao.delete(ID);
//		application.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public ParamList save(ParamList paramlist) throws Exception{
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		paramlist.setAppInfo(metaIdentifierHolderList);
		paramlist.setBaseEntity();
		ParamList app=iParamListDao.save(paramlist);
		registerGraph.updateGraph((Object) app, MetaType.paramlist);
		return app;
	}*/

	/********************** UNUSED **********************/
	/*public List<ParamList> resolveName(List<ParamList> paramLists) {
		List<ParamList> paramList = new ArrayList<ParamList>(); 
		for(ParamList paraml : paramLists)
		{
			String createdByRefUuid = paraml.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			paraml.getCreatedBy().getRef().setName(user.getName());
			paramList.add(paraml);
		}
		return paramList;
	}*/

	/********************** UNUSED **********************/
	/*public ParamList resolveName(ParamList paramList) throws JsonProcessingException {
		if (paramList.getCreatedBy() != null) {
			String createdByRefUuid = paramList.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			paramList.getCreatedBy().getRef().setName(user.getName());
		}
		if (paramList.getAppInfo() != null) {
			for (int i = 0; i < paramList.getAppInfo().size(); i++) {
				String appUuid = paramList.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				paramList.getAppInfo().get(i).getRef().setName(appName);
			}
		}		
		return paramList;
	}*/	

	/********************** UNUSED **********************/
	/*public List<ParamList> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iParamListDao.findAllVersion(appUuid, uuid);
		} else
			return iParamListDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public List<ParamList> findAllLatest() {	
			   Aggregation ParamListAggr = newAggregation(group("uuid").max("version").as("version"));
			   AggregationResults<ParamList> ParamResults = mongoTemplate.aggregate(ParamListAggr, "paramlist", ParamList.class);	   
			   List<ParamList> paramList = ParamResults.getMappedResults();

			   // Fetch the relation details for each id
			   List<ParamList> result=new  ArrayList<ParamList>();
			   for(ParamList a :paramList)
			   {   
				   ParamList paramLatest = iParamListDao.findOneByUuidAndVersion(a.getId(),a.getVersion());
				   result.add(paramLatest);
			   }
			   return result;			
	}*/

	/********************** UNUSED **********************/
	/*public List<ParamList> findAllLatestActive() 	
	{ 
		Aggregation appAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<ParamList> appResults = mongoTemplate.aggregate(appAggr, "paramlist", ParamList.class);	   
	   List<ParamList> appList = appResults.getMappedResults();

	   // Fetch the application details for each id
	   List<ParamList> result=new  ArrayList<ParamList>();
	   for(ParamList a : appList)
	   {   		     
		   ParamList appLatest = iParamListDao.findOneByUuidAndVersion(a.getId(), a.getVersion());  		   
		   result.add(appLatest);
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/	
	/*public MetaIdentifierHolder saveAs(ParamList paramlist) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		ParamList appNew = new ParamList();
		appNew.setName(paramlist.getName()+"_copy");
		appNew.setActive(paramlist.getActive());		
		appNew.setDesc(paramlist.getDesc());		
		appNew.setTags(paramlist.getTags());	
		save(appNew);
		ref.setType(MetaType.paramlist);
		ref.setUuid(appNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> paramList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity paraml : paramList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = paraml.getId();
			String uuid = paraml.getUuid();
			String version = paraml.getVersion();
			String name = paraml.getName();
			String desc = paraml.getDesc();
			String published=paraml.getPublished();
			MetaIdentifierHolder createdBy = paraml.getCreatedBy();
			String createdOn = paraml.getCreatedOn();
			String[] tags = paraml.getTags();
			String active = paraml.getActive();
			List<MetaIdentifierHolder> appInfo = paraml.getAppInfo();
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

	/********************** UNUSED **********************/
	/*public List<ParamListHolder> getParamListHolder(ParamSetHolder paramSetHolder){
		ParamList paramList= null; 
		if(null != paramSetHolder.getRef() && null != paramSetHolder.getRef().getVersion()){
			paramList= iParamListDao.findOneByUuidAndVersion(paramSetHolder.getRef().getUuid(), paramSetHolder.getRef().getVersion());
		} else {
			paramList = iParamListDao.findLatestByUuid(paramSetHolder.getRef().getUuid(), new Sort(Sort.Direction.DESC, "version"));
		}
		List<ParamListHolder> paramListHolderList = null;
		if(null != paramList){
//			for(ParamInfo paramInfo:paramList.getParamInfo()){
//				if(paramSetHolder.getParamSetId().equalsIgnoreCase(paramInfo.getParamSetId())){
//					paramListHolderList = paramInfo.getParamSetVal();
//					break;
//				}
//			}
		}
		return paramListHolderList;
	}*/

	/********************** UNUSED **********************/
	/*public String getParamName(ParamHolder paramHolder) {
		ParamList paramList = iParamListDao.findOneByUuidAndVersion(paramHolder.getRef().getUuid(), paramHolder.getRef().getVersion());
		for(int j=0;j<paramList.getParams().size();j++){
			if (paramHolder.getParamId().equals(paramList.getParams().get(j).getParamId())) {
				return paramList.getParams().get(j).getParamName();
			}
		}
		return null;
	}*/
	
	public String sql(Integer paramId, ParamList paramList) {
		List<Param> list = paramList.getParams();
		for(Param param : list) {
			if(param.getParamId().equalsIgnoreCase(paramId.toString()))
				//return String.format("%s.%s", paramList.getName(), param.getParamName());
				return param.getParamName();
			break;
		}
		return null;
	}
	
	/**
	 * 
	 * @param execParams
	 * @param attributeId
	 * @param ref
	 * @return value
	 * @throws JsonProcessingException
	 */
	public String getParamValue(ExecParams execParams,  
									Integer attributeId, 
									MetaIdentifier ref) throws JsonProcessingException {	
		logger.info("Inside ParamListServiceImpl.getParamValue(); "); 
		ParamListHolder paramListHolder = null;
		if (execParams != null) {
			paramListHolder = execParams.getParamListHolder();
			if (!paramListHolder.getRef().getUuid().equals(ref.getUuid())) {
				paramListHolder = null;
			}
		}
		
		if (paramListHolder == null) {
			ParamList paramList = (ParamList)daoRegister.getRefObject(ref);
			for (com.inferyx.framework.domain.Param param : paramList.getParams()) {
				if (param.getParamId().equalsIgnoreCase(attributeId+"")) {
					return param.getParamValue().getValue();
				}
			}
		}
		
		ParamList paramList = (ParamList) daoRegister.getRefObject(paramListHolder.getRef());
		for(Param param : paramList.getParams()) {
			if(param.getParamId().equalsIgnoreCase(attributeId+"")) {
				return param.getParamValue().getValue();
			}
		}
		return "''";
	}// End method	
}
