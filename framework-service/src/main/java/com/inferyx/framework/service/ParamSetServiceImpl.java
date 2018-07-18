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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.param.DoubleParam;
import org.apache.spark.ml.param.IntParam;
import org.apache.spark.ml.param.LongParam;
import org.apache.spark.ml.param.Param;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.param.ParamPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.dao.IAlgorithmDao;
import com.inferyx.framework.dao.IModelDao;
import com.inferyx.framework.dao.IParamListDao;
import com.inferyx.framework.dao.IParamSetDao;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamInfo;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSet;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ParamSetServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IParamSetDao iParamSetDao;
	@Autowired
	IParamListDao IParamListDao;
	@Autowired
	IModelDao iModelDao;
	@Autowired
	IAlgorithmDao iAlgorithmDao;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ModelServiceImpl modelServiceImpl;
	@Autowired
	AlgorithmServiceImpl algorithmServiceImpl;
	@Autowired
	ParamListServiceImpl paramListServiceImpl;
	@Autowired
	MetadataUtil daoRegister;
	@Autowired
	RuleServiceImpl ruleServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(ParamSetServiceImpl.class);

	/********************** UNUSED **********************/
	/*public ParamSet findLatest() {
		return resolveName(iParamSetDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public List<ParamSet> findAllByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iParamSetDao.findAllVersion(uuid);
		}
		return iParamSetDao.findAllVersion(appUuid,uuid);
	}*/

	public List<ParamSet> findLatestByDependsOn(MetaIdentifierHolder dependsOn){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iParamSetDao.findLatestByDependsOn(dependsOn.getRef().getUuid(),new Sort(Sort.Direction.DESC, "version"));	
		}
		return iParamSetDao.findLatestByDependsOn(appUuid,dependsOn.getRef().getUuid(),new Sort(Sort.Direction.DESC, "version"));	
	}

	public List<ParamSet> findOneByDependsOn(MetaIdentifierHolder dependsOn){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iParamSetDao.findOneByDependsOn(dependsOn.getRef().getUuid(),dependsOn.getRef().getVersion());	
		}
		return iParamSetDao.findOneByDependsOn(appUuid,dependsOn.getRef().getUuid(),dependsOn.getRef().getVersion());	
	}

	/********************** UNUSED **********************/
	/*public ParamSet findLatestByUuid(String uuid){
		return iParamSetDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public ParamSet findOneByUuidAndVersion(String uuid,String version){
		return iParamSetDao.findOneByUuidAndVersion(uuid,version);
	}*/
	/********************** UNUSED **********************/
	/*public ParamSet findOneById(String id){
		return iParamSetDao.findOne(id);
	}*/
	/********************** UNUSED **********************/
	/*public List<ParamSet> findAll(){
		return iParamSetDao.findAll();
	}*/
	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		ParamSet paramlist = iParamSetDao.findOne(Id);
		paramlist.setActive("N");
		iParamSetDao.save(paramlist);
	}*/

	/********************** UNUSED **********************/
	/*public ParamSet save(ParamSet paramSet) throws Exception{
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		paramSet.setAppInfo(metaIdentifierHolderList);
		paramSet.setBaseEntity();
		ParamSet app=iParamSetDao.save(paramSet);
		registerGraph.updateGraph((Object) app, MetaType.paramset);
		return app;
	}*/
	
	public List<ParamSet> resolveName(List<ParamSet> paramSetList) throws JsonProcessingException {
		List<ParamSet> paramSet = new ArrayList<ParamSet>(); 
		for(ParamSet paraml : paramSetList)
		{
			String createdByRefUuid = paraml.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			paraml.getCreatedBy().getRef().setName(user.getName());
			paraml = resolveName(paraml);
			paramSet.add(paraml);
		}
		return paramSet;
	}
	
	public ParamSet resolveName(ParamSet paramSet) throws JsonProcessingException {
		if (paramSet.getCreatedBy() != null) {
			String createdByRefUuid = paramSet.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			paramSet.getCreatedBy().getRef().setName(user.getName());
		} 
		if (paramSet.getAppInfo() != null) {
			for (int i = 0; i < paramSet.getAppInfo().size(); i++) {
				String appUuid = paramSet.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				paramSet.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		if(paramSet.getParamInfo() !=null){
			String paramListUuid=paramSet.getDependsOn().getRef().getUuid();
			//ParamList paramList = paramListServiceImpl.findLatestByUuid(paramListUuid);
			ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(paramListUuid, MetaType.paramlist.toString());
			Map<String,String> paramMap = new HashMap<>();
			for(int j=0;j<paramList.getParams().size();j++){
				paramMap.put(paramList.getParams().get(j).getParamId(), paramList.getParams().get(j).getParamName());
			}
			for(int j=0;j<paramSet.getParamInfo().size();j++){
				for(int i=0;i<paramSet.getParamInfo().get(j).getParamSetVal().size(); i++){
					paramSet.getParamInfo().get(j).getParamSetVal().get(i).setParamName(paramMap.get(paramSet.getParamInfo().get(j).getParamSetVal().get(i).getParamId()));
				}
			}
		}
		return paramSet;
	}	

	/********************** UNUSED **********************/
	/*public List<ParamSet> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iParamSetDao.findAllVersion(appUuid, uuid);
		} else
			return iParamSetDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public List<ParamSet> findAllLatest() {	
			   Aggregation ParamSetAggr = newAggregation(group("uuid").max("version").as("version"));
			   AggregationResults<ParamSet> ParamResults = mongoTemplate.aggregate(ParamSetAggr,"paramset", ParamSet.class);	   
			   List<ParamSet> paramSetList = ParamResults.getMappedResults();

			   // Fetch the relation details for each id
			   List<ParamSet> result=new  ArrayList<ParamSet>();
			   for(ParamSet a :paramSetList)
			   {   
				   ParamSet paramLatest = iParamSetDao.findOneByUuidAndVersion(a.getId(),a.getVersion());
				   result.add(paramLatest);
			   }
			   return result;			
	}*/

	/********************** UNUSED **********************/
	/*public List<ParamSet> findAllLatestActive() 	
	{ 
		Aggregation appAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<ParamSet> appResults = mongoTemplate.aggregate(appAggr,"paramset", ParamSet.class);	   
	   List<ParamSet> appList = appResults.getMappedResults();

	   // Fetch the application details for each id
	   List<ParamSet> result=new  ArrayList<ParamSet>();
	   for(ParamSet a : appList)
	   {   		     
		   ParamSet appLatest = iParamSetDao.findOneByUuidAndVersion(a.getId(), a.getVersion());  		   
		   result.add(appLatest);
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/	
	/*public MetaIdentifierHolder saveAs(ParamSet paramset) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		ParamSet appNew = new ParamSet();
		appNew.setName(paramset.getName()+"_copy");
		appNew.setActive(paramset.getActive());		
		appNew.setDesc(paramset.getDesc());		
		appNew.setTags(paramset.getTags());	
		save(appNew);
		ref.setType(MetaType.paramset);
		ref.setUuid(appNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> paramSetList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity paramSet : paramSetList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = paramSet.getId();
			String uuid = paramSet.getUuid();
			String version = paramSet.getVersion();
			String name = paramSet.getName();
			String desc = paramSet.getDesc();
			String published=paramSet.getPublished();
			MetaIdentifierHolder createdBy = paramSet.getCreatedBy();
			String createdOn = paramSet.getCreatedOn();
			String[] tags = paramSet.getTags();
			String active = paramSet.getActive();
			List<MetaIdentifierHolder> appInfo = paramSet.getAppInfo();
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
	
   public List<ParamSet> getParamSetByAlgorithm (String algorithmUUID, String algorithmVersion) throws JsonProcessingException {		
		Algorithm algo = (Algorithm) commonServiceImpl.getLatestByUuid(algorithmUUID, MetaType.algorithm.toString());
		//ParamList paramList = paramListServiceImpl.findLatestByUuid(algo.getParamList().getRef().getUuid());
		ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(algo.getParamList().getRef().getUuid(), MetaType.paramlist.toString());
		MetaIdentifier dependsOnRef = new MetaIdentifier();
		dependsOnRef.setType(MetaType.paramlist);
		dependsOnRef.setUuid(paramList.getUuid());
		dependsOnRef.setVersion(paramList.getVersion());		
		MetaIdentifierHolder dependsOnRefHolder = new MetaIdentifierHolder();
		dependsOnRefHolder.setRef(dependsOnRef);
		List<ParamSet> paramSetList = findLatestByDependsOn(dependsOnRefHolder);
		paramSetList = resolveName(paramSetList);
		return paramSetList;		
	}

	public List<ParamSet> getParamSetByModel (String modelUUID, String modelVersion) throws JsonProcessingException {		
		//Model model = modelServiceImpl.findOneByUuidAndVersion(modelUUID,modelVersion);
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUUID,modelVersion, MetaType.model.toString());
		Algorithm algo = (Algorithm) commonServiceImpl.getLatestByUuid(model.getDependsOn().getRef().getUuid(), MetaType.algorithm.toString());
		//ParamList paramList = paramListServiceImpl.findLatestByUuid(algo.getParamList().getRef().getUuid());
		ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(algo.getParamList().getRef().getUuid(), MetaType.paramlist.toString());
		MetaIdentifier dependsOnRef = new MetaIdentifier();
		dependsOnRef.setType(MetaType.paramlist);
		dependsOnRef.setUuid(paramList.getUuid());
		dependsOnRef.setVersion(paramList.getVersion());		
		MetaIdentifierHolder dependsOnRefHolder = new MetaIdentifierHolder();
		dependsOnRefHolder.setRef(dependsOnRef);
		List<ParamSet> paramSetList = findLatestByDependsOn(dependsOnRefHolder);
		paramSetList = resolveName(paramSetList);
		return paramSetList;		
	}
	
	public List<ParamSet> getParamSetByTrain (String trainUUID, String trainVersion) throws JsonProcessingException {		
		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainUUID,trainVersion, MetaType.train.toString());
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(),train.getDependsOn().getRef().getVersion(), MetaType.model.toString());

		Algorithm algo = (Algorithm) commonServiceImpl.getLatestByUuid(model.getDependsOn().getRef().getUuid(), MetaType.algorithm.toString());
		ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(algo.getParamList().getRef().getUuid(), MetaType.paramlist.toString());
		MetaIdentifier dependsOnRef = new MetaIdentifier();
		dependsOnRef.setType(MetaType.paramlist);
		dependsOnRef.setUuid(paramList.getUuid());
		dependsOnRef.setVersion(paramList.getVersion());		
		MetaIdentifierHolder dependsOnRefHolder = new MetaIdentifierHolder();
		dependsOnRefHolder.setRef(dependsOnRef);
		List<ParamSet> paramSetList = findLatestByDependsOn(dependsOnRefHolder);
		paramSetList = resolveName(paramSetList);
		return paramSetList;		
	}
   
  public List<ParamSet> getParamSetByRule (String ruleUUID, String ruleVersion) throws JsonProcessingException {
	    List<ParamSet> paramSetList = new ArrayList<>();
	    //com.inferyx.framework.domain.Rule rule = ruleServiceImpl.findLatestByUuid(ruleUUID);
	    com.inferyx.framework.domain.Rule rule = (Rule) commonServiceImpl.getLatestByUuid(ruleUUID, MetaType.rule.toString());
	    if(rule.getParamList() !=null){
		MetaIdentifier dependsOnRef = new MetaIdentifier();
		dependsOnRef.setType(MetaType.paramlist);
		dependsOnRef.setUuid(rule.getParamList().getRef().getUuid());
		dependsOnRef.setVersion(rule.getParamList().getRef().getVersion());		
		MetaIdentifierHolder dependsOnRefHolder = new MetaIdentifierHolder();
		dependsOnRefHolder.setRef(dependsOnRef);
		if(dependsOnRef.getVersion() == null)
			paramSetList = findLatestByDependsOn(dependsOnRefHolder);
		else
			paramSetList = findOneByDependsOn(dependsOnRefHolder);
		paramSetList = resolveName(paramSetList);
	    }
		return paramSetList;		
	}
	public List<ParamSet> getParamSetByParamList (String paramListUUID, String paramListVersion) throws JsonProcessingException {
		
		MetaIdentifier dependsOnRef = new MetaIdentifier();
		dependsOnRef.setType(MetaType.paramlist);
		dependsOnRef.setUuid(paramListUUID);
		dependsOnRef.setVersion(paramListVersion);		
		MetaIdentifierHolder dependsOnRefHolder = new MetaIdentifierHolder();
		dependsOnRefHolder.setRef(dependsOnRef);
		
		List<ParamSet> paramSetList = new ArrayList<>();
		if (paramListVersion == null)
			paramSetList = findLatestByDependsOn(dependsOnRefHolder);
		else
			paramSetList = findOneByDependsOn(dependsOnRefHolder);
		paramSetList = resolveName(paramSetList);
		return paramSetList;		
	}
	
	public List<ParamListHolder> getParamListHolder(ParamSetHolder paramSetHolder) throws JsonProcessingException{
		ParamSet paramSet = (ParamSet) commonServiceImpl.getOneByUuidAndVersion(paramSetHolder.getRef().getUuid(), paramSetHolder.getRef().getVersion(), MetaType.paramset.toString());
				
		List<ParamListHolder> paramListHolderList = null;
		if(null != paramSet){
			for(ParamInfo paramInfo : paramSet.getParamInfo()){
				if(paramSetHolder.getParamSetId().equalsIgnoreCase(paramInfo.getParamSetId())){
					paramListHolderList = paramInfo.getParamSetVal();
					break;
				}
			}
		}
		return paramListHolderList;
	}
	
	public List<ParamMap> getParamMap(ExecParams execParams, String modelUUID, String modelVersion) throws Exception{
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUUID, modelVersion, MetaType.model.toString());
		Algorithm algo = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(), MetaType.algorithm.toString());
		
		String algoClassName = algo.getTrainClass();
		
		List<ParamMap> paramMapList = new ArrayList<>();
		if(null!= execParams) {
			if(execParams.getParamInfo() != null) {
				for(ParamSetHolder paramSetHolder : execParams.getParamInfo()){
					List<ParamListHolder> paramListHolder = getParamListHolder(paramSetHolder);
					ParamMap paramMap = getParamMapByPLHolder(paramListHolder, algoClassName, true);					
					paramMapList.add(paramMap);
				}
			} else if(execParams.getParamListInfo() != null) {
				List<ParamListHolder> paramListHolder = execParams.getParamListInfo();
				ParamMap paramMap = getParamMapByPLHolder(paramListHolder, algoClassName, false);						
				paramMapList.add(paramMap);
			}					
		}

		return paramMapList;
	}

	public ParamMap getParamMapByPLHolder(List<ParamListHolder> paramListHolder, String algoClassName, boolean filterParams) throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, JsonProcessingException {
		ParamMap paramMap = new ParamMap();
		try {
			for(ParamListHolder plh : paramListHolder) {
				ParamList paramList= (ParamList) commonServiceImpl.getLatestByUuid(plh.getRef().getUuid(), MetaType.paramlist.toString());
				if(filterParams) {						
					for(com.inferyx.framework.domain.Param param : paramList.getParams()){
						if(param.getParamId().equals(plh.getParamId())){
							plh.setParamType(param.getParamType());
							plh.setParamName(param.getParamName());
							break;
						}
					}
					paramMap.put(getParamPair(algoClassName, plh.getParamName(), plh.getParamType(), plh.getValue()));
				} else {
					for(com.inferyx.framework.domain.Param param : paramList.getParams()){
						paramMap.put(getParamPair(algoClassName, param.getParamName(), param.getParamType(), param.getParamValue().getValue()));
					}
				}
				
//				Class<?> dynamicClass = Class.forName(algoClassName);						
//				Method method = dynamicClass.getMethod(plh.getParamName() );
//				Object obj = method.invoke(dynamicClass.newInstance());
//				
//				if(plh.getParamType().equalsIgnoreCase("integer")){
//					Class<?>[] param = new Class[1];
//					param[0] = int.class;
//					Method method1 = obj.getClass().getMethod("w", param);
//					paramMap.put((ParamPair<Object>)method1.invoke((IntParam)obj,Integer.parseInt(plh.getValue())));
//				} 
//				else if(plh.getParamType().equalsIgnoreCase("String")) {
//					Class<?>[] param = new Class[1];
//					param[0] = String.class;
//					obj = (Param<String>)obj;
//					Method method1 = obj.getClass().getMethod("w", param);
//					paramMap.put((ParamPair<Object>)method1.invoke((Param<String>)obj,plh.getValue()));
//				} 
//				else if(plh.getParamType().equalsIgnoreCase("long")) {
//					Class<?>[] param = new Class[1];
//					param[0] = long.class;
//					Method method1 = obj.getClass().getMethod("w", param);
//					paramMap.put((ParamPair<Object>)method1.invoke((LongParam)obj,Long.parseLong(plh.getValue())));
//				}
//				else if(plh.getParamType().equalsIgnoreCase("double")) {
//					Class<?>[] param = new Class[1];
//					param[0] = double.class;
//					Method method1 = obj.getClass().getMethod("w", param);
//					paramMap.put((ParamPair<Object>)method1.invoke((DoubleParam)obj,Double.parseDouble(plh.getValue())));
//				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} 		
		return paramMap;
	}
	
	@SuppressWarnings("unchecked")
	public ParamPair<Object> getParamPair(String algoClassName, String paramName, String paramType, String paramValue) throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException{
		Class<?> dynamicClass = Class.forName(algoClassName);						
		Method method = dynamicClass.getMethod(paramName);
		Object obj = method.invoke(dynamicClass.newInstance());
		
		if(paramType.equalsIgnoreCase("integer")){
			Class<?>[] param = new Class[1];
			param[0] = int.class;
			Method method1 = obj.getClass().getMethod("w", param);
			return (ParamPair<Object>)method1.invoke((IntParam)obj,Integer.parseInt(paramValue));
		} 
		else if(paramType.equalsIgnoreCase("String")) {
			Class<?>[] param = new Class[1];
			param[0] = String.class;
			obj = (Param<String>)obj;
			Method method1 = obj.getClass().getMethod("w", param);
			return (ParamPair<Object>)method1.invoke((Param<String>)obj, paramValue);
		} 
		else if(paramType.equalsIgnoreCase("long")) {
			Class<?>[] param = new Class[1];
			param[0] = long.class;
			Method method1 = obj.getClass().getMethod("w", param);
			return (ParamPair<Object>)method1.invoke((LongParam)obj,Long.parseLong(paramValue));
		}
		else if(paramType.equalsIgnoreCase("double")) {
			Class<?>[] param = new Class[1];
			param[0] = double.class;
			Method method1 = obj.getClass().getMethod("w", param);
			return (ParamPair<Object>)method1.invoke((DoubleParam)obj,Double.parseDouble(paramValue));
		}
		return null;
	}
	
	public ParamMap getParamMapCombined(ExecParams execParams, String modelUUID, String modelVersion) throws Exception {
		List<ParamMap> paramMapList = getParamMap(execParams, modelUUID, modelVersion);
		ParamMap paramMapCombined = null;
		int i=0;
		if(null != paramMapList){
			for(ParamMap paramMap:paramMapList){
				if(i==0) {
					paramMapCombined = paramMap;
				} else {
					paramMapCombined = paramMapCombined.$plus$plus(paramMap);
				}
				i++;
			}
		}
		return paramMapCombined;	
	}
	/**
	 * 
	 * @param execParams
	 * @param attributeId
	 * @param ref
	 * @return
	 * @throws JsonProcessingException
	 */
	public String getParamValue(ExecParams execParams,  
									Integer attributeId, 
									MetaIdentifier ref) throws JsonProcessingException {
		ParamSetHolder paramSetHolder = null;
		String value = null;
		if (execParams != null) {
			paramSetHolder = execParams.getParamSetHolder();
		}
		if (paramSetHolder == null) {
			ParamList paramList = (ParamList)daoRegister.getRefObject(ref);
			for (com.inferyx.framework.domain.Param param : paramList.getParams()) {
				if (param.getParamId().equals(attributeId+"")) {
					value = param.getParamValue().getValue();
					return value;
				}
			}
		}
		ParamSet paramSet = (ParamSet) daoRegister.getRefObject(paramSetHolder.getRef());
		ParamInfo paramInfo = paramSet.getParamInfo().get(Integer.parseInt(paramSetHolder.getParamSetId()));
		for (ParamListHolder paramListHolder : paramInfo.getParamSetVal()) {
			if (paramListHolder.getParamId().equals(attributeId.toString())) {
				if (StringUtils.isNotBlank(paramListHolder.getValue())) {
					value = paramListHolder.getValue();
					return value;
				} else {
					ParamList paramList = (ParamList)daoRegister.getRefObject(paramListHolder.getRef());
					for (com.inferyx.framework.domain.Param param : paramList.getParams()) {
						if (param.getParamId().equals(attributeId+"")) {
							value = param.getParamValue().getValue();
							return value;
						}
					}
				}
			}
		}
		return "''";
	}// End method	
	
	/**
	 * 
	 * @param execParams
	 * @param paramName
	 * @return
	 */
	public ParamListHolder getParamByName (ExecParams execParams, String paramName) {
		if (execParams == null || execParams.getParamListInfo() == null || execParams.getParamListInfo().isEmpty() || StringUtils.isBlank(paramName)) {
			return null;
		}
		for (ParamListHolder param : execParams.getParamListInfo()) {
			if (param.getParamName().equalsIgnoreCase(paramName)) {
				return param;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param execParams
	 * @param paramName
	 * @return
	 */
	public ParamListHolder getParamById (ExecParams execParams, String paramId) {
		if (execParams == null || execParams.getParamListInfo() == null || execParams.getParamListInfo().isEmpty() || StringUtils.isBlank(paramId)) {
			return null;
		}
		for (ParamListHolder param : execParams.getParamListInfo()) {
			if (param.getParamId().equals(paramId)) {
				return param;
			}
		}
		return null;
	}

}
