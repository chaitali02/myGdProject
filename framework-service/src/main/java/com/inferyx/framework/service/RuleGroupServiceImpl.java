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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.dao.IRuleGroupDao;
import com.inferyx.framework.dao.IRuleGroupExecDao;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.register.GraphRegister;

@Service
public class RuleGroupServiceImpl extends RuleGroupTemplate {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IRuleGroupDao iRuleGroupDao;
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired 
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	protected RuleServiceImpl ruleServiceImpl;
	@Autowired
	protected RuleExecServiceImpl ruleExecServiceImpl;
	@Autowired
	protected IRuleGroupExecDao iRuleGroupExecDao;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	ThreadPoolTaskExecutor metaGroupExecutor;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired 
	RuleGroupExecServiceImpl ruleGroupExecServiceImpl ;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	
	static final Logger logger = Logger.getLogger(RuleGroupServiceImpl.class);

	/********************** UNUSED **********************/
	/*public RuleGroup findLatest() {
		return resolveName(iRuleGroupDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public RuleGroup findOneById(String id)	{		
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)	{
			return iRuleGroupDao.findOneById(appUuid,id);
		}
		return iRuleGroupDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public RuleGroup save(RuleGroup ruleGroup) throws Exception{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		ruleGroup.setAppInfo(metaIdentifierHolderList);
		ruleGroup.setBaseEntity();
		RuleGroup rulegroup=iRuleGroupDao.save(ruleGroup);
		registerGraph.updateGraph((Object) rulegroup, MetaType.rulegroup);
		return rulegroup;
	}*/

	/********************** UNUSED **********************/
	/*public List<RuleGroup> findAll(){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iRuleGroupDao.findAll(); 
		}
		return iRuleGroupDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		RuleGroup ruleGroup = iRuleGroupDao.findOneById(appUuid,Id);
		ruleGroup.setActive("N");
		iRuleGroupDao.save(ruleGroup);
	}*/

	/********************** UNUSED **********************/
	/*public List<RuleGroup> findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iRuleGroupDao.findAllByUuid(appUuid,uuid);
		
	}*/

	/********************** UNUSED **********************/
	/*public RuleGroup findOneByUuidAndVersion(String uuid, String version){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iRuleGroupDao.findOneByUuidAndVersion(appUuid,uuid,version);
		}
		return iRuleGroupDao.findOneByUuidAndVersion(uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public RuleGroup findLatestByUuid(String uuid){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iRuleGroupDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));
		}
		return iRuleGroupDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));			
	}*/
	
	/********************** UNUSED **********************/
	/*public List<RuleGroup> findAllLatest() 
	
	{	   
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
	   Aggregation ruleGroupAggr = newAggregation(group("uuid").max("version").as("version"));
	   AggregationResults<RuleGroup> dagResults = mongoTemplate.aggregate(ruleGroupAggr,"rulegroup", RuleGroup.class);	   
	   List<RuleGroup> RuleList = dagResults.getMappedResults();

	   // Fetch the rulegroup details for each id
	   List<RuleGroup> result=new  ArrayList<RuleGroup>();
	   for(RuleGroup rg :RuleList)
	   {   
		   RuleGroup ruleGroupLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
			//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();;
				ruleGroupLatest = iRuleGroupDao.findOneByUuidAndVersion(appUuid,rg.getId(), rg.getVersion());
			}
			else
			{
				ruleGroupLatest = iRuleGroupDao.findOneByUuidAndVersion(rg.getId(), rg.getVersion());
			}
			//logger.debug("datapodLatest is " + datapodLatest.getName());
			result.add(ruleGroupLatest);
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<RuleGroup> findAllLatestActive() 	
	{	   
	   Aggregation ruleGroupAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<RuleGroup> ruleGroupResults = mongoTemplate.aggregate(ruleGroupAggr,"rulegroup", RuleGroup.class);	   
	   List<RuleGroup> ruleGroupList = ruleGroupResults.getMappedResults();

	   // Fetch the ruleGroup details for each id
	   List<RuleGroup> result=new  ArrayList<RuleGroup>();
	   for(RuleGroup r : ruleGroupList)
	   {   
		   RuleGroup ruleGroupLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				ruleGroupLatest = iRuleGroupDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				ruleGroupLatest = iRuleGroupDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
		
			result.add(ruleGroupLatest);
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<RuleGroup> resolveName(List<RuleGroup> ruleGroup) {
		List<RuleGroup> ruleGroupList = new ArrayList<RuleGroup>();
		for(RuleGroup rGroup : ruleGroup)
		{
		String createdByRefUuid = rGroup.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		rGroup.getCreatedBy().getRef().setName(user.getName());
		ruleGroupList.add(rGroup);
		}
		return ruleGroupList;
	}*/

	/********************** UNUSED **********************/
	/*public RuleGroup resolveName(RuleGroup ruleGroup) {
		if(ruleGroup.getCreatedBy() != null)
		{
		String createdByRefUuid = ruleGroup.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		ruleGroup.getCreatedBy().getRef().setName(user.getName());
		}
		if (ruleGroup.getAppInfo() != null) {
		for (int i = 0; i < ruleGroup.getAppInfo().size(); i++) {
			if(ruleGroup.getAppInfo().get(i)!=null)
			{
		String appUuid = ruleGroup.getAppInfo().get(i).getRef().getUuid();
		Application application = applicationServiceImpl.findLatestByUuid(appUuid);
		String appName = application.getName();
		ruleGroup.getAppInfo().get(i).getRef().setName(appName);
		}
		}
		}
		List<MetaIdentifierHolder> ruleInfo = ruleGroup.getRuleInfo();
		for(int i=0; i<ruleInfo.size();i++)
		{
		String ruleUuid = ruleInfo.get(i).getRef().getUuid();
		Rule rule = ruleServiceImpl.findLatestByUuid(ruleUuid);
		String ruleName = rule.getName();
		ruleInfo.get(i).getRef().setName(ruleName);
		}
		ruleGroup.setRuleInfo(ruleInfo);
		return ruleGroup;
		}
*/
	/********************** UNUSED **********************/
	/*public List<RuleGroup> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iRuleGroupDao.findAllVersion(appUuid, uuid);
		}
		else
		return iRuleGroupDao.findAllVersion(uuid);
	}*/

	public RuleGroupExec create(String ruleGroupUUID, 
										String ruleGroupVersion, 
										ExecParams execParams,
										List<String> datapodList, 
										RuleGroupExec ruleGroupExec, 
										DagExec dagExec) throws Exception {

		return (RuleGroupExec) super.create(ruleGroupUUID, ruleGroupVersion, MetaType.rulegroup, MetaType.rulegroupExec, MetaType.rule, 
							MetaType.ruleExec, execParams, datapodList, ruleGroupExec, dagExec);
	}	
	
	public MetaIdentifier execute(String ruleGroupUUID, String ruleGroupVersion, ExecParams execParams, RuleGroupExec ruleGroupExec, RunMode runMode) throws Exception {
		return super.execute(ruleGroupUUID, ruleGroupVersion, MetaType.rulegroup, MetaType.rulegroupExec, MetaType.rule, MetaType.ruleExec, execParams, ruleGroupExec, runMode);
	}

	
	
	public RuleGroupExec fetchRuleGroupExec(String ruleGroupExecUUID, String ruleGroupExecVersion) throws JsonProcessingException {
		//return iRuleGroupExecDao.findOneByUuidAndVersion(ruleGroupExecUUID, ruleGroupExecVersion);
		return (RuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(ruleGroupExecUUID, ruleGroupExecVersion, MetaType.rulegroupExec.toString());
	}

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(RuleGroup ruleGroup) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		RuleGroup ruleGroupNew = new RuleGroup();
		ruleGroupNew.setName(ruleGroup.getName()+"_copy");
		ruleGroupNew.setActive(ruleGroup.getActive());		
		ruleGroupNew.setDesc(ruleGroup.getDesc());		
		ruleGroupNew.setTags(ruleGroup.getTags());	
		ruleGroupNew.setRuleInfo(ruleGroup.getRuleInfo());
		ruleGroupNew.setInParallel(ruleGroup.getInParallel());
		save(ruleGroupNew);
		ref.setType(MetaType.rulegroup);
		ref.setUuid(ruleGroupNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/
	
	public void restart(String type,String uuid,String version, RunMode runMode) throws Exception{
		//RuleGroupExec ruleGroupExec= ruleGroupExecServiceImpl.findOneByUuidAndVersion(uuid, version);
		RuleGroupExec ruleGroupExec = (RuleGroupExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.rulegroupExec.toString());
//		try {
//			ruleGroupExec = create(ruleGroupExec.getDependsOn().getRef().getUuid(), ruleGroupExec.getDependsOn().getRef().getVersion(), null, null, ruleGroupExec, null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		ruleGroupExec = parse(ruleGroupExec.getUuid(), ruleGroupExec.getVersion(), null, null, null, null, runMode);
		execute(ruleGroupExec.getDependsOn().getRef().getUuid(), ruleGroupExec.getDependsOn().getRef().getVersion(), null,ruleGroupExec, runMode);
		
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.rulegroupExec, MetaType.ruleExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	public RuleGroupExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws Exception {
		return (RuleGroupExec) super.parse(execUuid, execVersion, MetaType.rulegroup, MetaType.rulegroupExec, MetaType.rule, MetaType.ruleExec, refKeyMap, otherParams, datapodList, dagExec, runMode);
	}

	/**
	 * Override Executable.execute()
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), execParams, (RuleGroupExec) baseExec, runMode);
		return null;
	}

	/**
	 * Override Parsable.parse()
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), MetaType.rulegroup, MetaType.rulegroupExec, MetaType.rule, MetaType.ruleExec, 
				DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), null, null, null, runMode);
	}
}
