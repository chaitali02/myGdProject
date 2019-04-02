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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.param.ParamMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.inferyx.framework.domain.Report;
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
	RuleServiceImpl ruleServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
	
	static final Logger logger = Logger.getLogger(ParamSetServiceImpl.class);

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

		
   public List<ParamSet> getParamSetByAlgorithm (String algorithmUUID, String algorithmVersion, String isHyperParam) throws JsonProcessingException {		
		Algorithm algo = (Algorithm) commonServiceImpl.getLatestByUuid(algorithmUUID, MetaType.algorithm.toString());
		//ParamList paramList = paramListServiceImpl.findLatestByUuid(algo.getParamList().getRef().getUuid());
		MetaIdentifier plMI = null;
		if(isHyperParam.equalsIgnoreCase("Y")) {
			plMI = algo.getParamListWH().getRef();
		} else {
			plMI = algo.getParamListWoH().getRef();
		}
		ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(plMI.getUuid(), MetaType.paramlist.toString());
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
		MetaIdentifier plMI = null;
		if(train.getUseHyperParams().equalsIgnoreCase("Y")) {
			plMI = algo.getParamListWH().getRef();
		} else {
			plMI = algo.getParamListWoH().getRef();
		}
		ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(plMI.getUuid(), MetaType.paramlist.toString());
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
  public List<ParamSet> getParamSetByReport (String ruleUUID, String ruleVersion) throws JsonProcessingException {
	    List<ParamSet> paramSetList = new ArrayList<>();
	    //com.inferyx.framework.domain.Rule rule = ruleServiceImpl.findLatestByUuid(ruleUUID);
	    Report report = (Report) commonServiceImpl.getLatestByUuid(ruleUUID, MetaType.report.toString());
	    if(report.getParamList() !=null){
		MetaIdentifier dependsOnRef = new MetaIdentifier();
		dependsOnRef.setType(MetaType.paramlist);
		dependsOnRef.setUuid(report.getParamList().getRef().getUuid());
		dependsOnRef.setVersion(report.getParamList().getRef().getVersion());		
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
	
	public ParamMap getParamMapCombined(ExecParams execParams, String trainUuid, String trainVersion) throws Exception {
		Object paramMapList1 = null;
		List<ParamMap> paramMapList = metadataServiceImpl.getParamMap(execParams, trainUuid, trainVersion, paramMapList1);
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
		ParamSetHolder paramSetHolder = null;
		if (execParams != null) {
			paramSetHolder = execParams.getCurrParamSet();
		}
		
		if (paramSetHolder == null) {
//			ParamList paramList = (ParamList)daoRegister.getRefObject(ref);
			ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString(), "N");
			for (com.inferyx.framework.domain.Param param : paramList.getParams()) {
				if (param.getParamId().equals(attributeId+"")) {
					return param.getParamValue().getValue();
				}
			}
		}
		
//		ParamSet paramSet = (ParamSet) daoRegister.getRefObject(paramSetHolder.getRef());
		ParamSet paramSet = (ParamSet) commonServiceImpl.getOneByUuidAndVersion(paramSetHolder.getRef().getUuid(), paramSetHolder.getRef().getVersion(), paramSetHolder.getRef().getType().toString(), "N");
		
		ParamInfo paramInfo = paramSet.getParamInfo().get(Integer.parseInt(paramSetHolder.getParamSetId()));
		for (ParamListHolder paramListHolder : paramInfo.getParamSetVal()) {
			if (paramListHolder.getParamId().equals(attributeId.toString())) {
				if (StringUtils.isNotBlank(paramListHolder.getValue())) {
					return paramListHolder.getValue();
				} else {
//					ParamList paramList = (ParamList)daoRegister.getRefObject(paramListHolder.getRef());
					ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(paramListHolder.getRef().getUuid(), paramListHolder.getRef().getVersion(), paramListHolder.getRef().getType().toString(), "N");
					
					for (com.inferyx.framework.domain.Param param : paramList.getParams()) {
						if (param.getParamId().equals(attributeId+"")) {
							return param.getParamValue().getValue();
						}
					}
				}
			}
		}
		return "''";
	}// End method	
}
