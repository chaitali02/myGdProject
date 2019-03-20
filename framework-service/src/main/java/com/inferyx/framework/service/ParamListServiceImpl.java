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
import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IParamListDao;
import com.inferyx.framework.domain.Application;
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
	
	static final Logger logger = Logger.getLogger(ParamListServiceImpl.class);

	/********************** UNUSED **********************/
	/*public ParamList findLatest() {
		return resolveName(iParamListDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
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
			if(param.getParamId().equalsIgnoreCase(paramId.toString())) {
				//return String.format("%s.%s", paramList.getName(), param.getParamName());
				return param.getParamName();
			}
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
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public String getParamValue(ExecParams execParams,  
									Integer attributeId, 
									MetaIdentifier ref) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {	
		logger.info("Inside ParamListServiceImpl.getParamValue(); "); 
		ParamListHolder paramListHolder = null;
		String paramName = null;
		String refParamValue = null;
//		ParamList paramListRef = (ParamList)daoRegister.getRefObject(ref);
		ParamList paramListRef = (ParamList) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
		Application application = commonServiceImpl.getApp(); 
//		ParamList appParamList = (ParamList)daoRegister.getRefObject(application.getParamList().getRef()); 
		ParamList appParamList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(application.getParamList().getRef().getUuid(), application.getParamList().getRef().getVersion(), application.getParamList().getRef().getType().toString(), "N");
		
		if (execParams != null) {
			paramListHolder = execParams.getParamListHolder();
			/*if (!paramListHolder.getRef().getUuid().equals(ref.getUuid())) {
				paramListHolder = null;
			}*/
		}
		
		// Get param from ref
		for (com.inferyx.framework.domain.Param param : paramListRef.getParams()) {
			if (param.getParamId().equalsIgnoreCase(attributeId.toString())) {
				if (paramListHolder == null && appParamList == null) {
					return param.getParamValue().getValue();	// Nothing in execParams. Send from ref
				} else {	// ExecParams has data. Wait and watch
					paramName = param.getParamName();
					refParamValue = param.getParamValue().getValue();
				}
			}
		}	
		
		logger.info("Param name : " + paramName);
		logger.info("Param value : " + refParamValue);
		
		for(Param param : appParamList.getParams()) {
			if((StringUtils.isBlank(paramName) && param.getParamId().equalsIgnoreCase(attributeId.toString())) 
					|| param.getParamName().equals(paramName)) {
				logger.info("Param name from app paramlist : " + param.getParamName());
				return param.getParamValue().getValue();
			}
		}
		
//		ParamList paramList = (ParamList) daoRegister.getRefObject(paramListHolder.getRef());
		ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(paramListHolder.getRef().getUuid(), paramListHolder.getRef().getVersion(), paramListHolder.getRef().getType().toString(), "N");
		
		for(Param param : paramList.getParams()) {
			if((StringUtils.isBlank(paramName) && param.getParamId().equalsIgnoreCase(attributeId.toString())) 
					|| param.getParamName().equals(paramName)) {
				logger.info("Param name from execParams : " + param.getParamName());
				return param.getParamValue().getValue();
			} 
		}
		if (StringUtils.isNotBlank(paramName)) {
			return refParamValue;
		}
		return "''";
	}// End method	
}
