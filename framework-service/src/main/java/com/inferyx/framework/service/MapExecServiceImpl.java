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


import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IRuleGroupExecDao;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.register.GraphRegister;

@Service
public class MapExecServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;	
	@Autowired
	SecurityServiceImpl securityServiceImpl;	
	@Autowired
	protected IRuleGroupExecDao iRuleGroupExecDao;	
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	RelationServiceImpl relationServiceImpl;	
	@Autowired
	DatapodServiceImpl datapodServiceImpl;	
	@Autowired
	MapServiceImpl mapServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	
	static final Logger logger = Logger.getLogger(MapExecServiceImpl.class);

	/********************** UNUSED **********************/
	/*public List<MapExec> findOneByrule(String ruleUUID) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iMapExecDao.findOneByrule(appUuid,ruleUUID);
	}*/

	/********************** UNUSED **********************/
	/*public MapExec findLatest() {
		MapExec mapexec=null;
		if(iMapExecDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			mapexec=resolveName(iMapExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return mapexec ;
	}	*/

	/********************** UNUSED **********************/
	/*public List<MapExec> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iMapExecDao.findAll(); 
		}
		return iMapExecDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public MapExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iMapExecDao.findOneById(appUuid,id);
		}
		return iMapExecDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public MapExec save(MapExec mapExec) throws Exception {
		if(mapExec.getAppInfo() == null)
		{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		mapExec.setAppInfo(metaIdentifierHolderList);
		}
	
		mapExec.setBaseEntity();
		
		MapExec mapExecq1=iMapExecDao.save(mapExec);
		registerGraph.updateGraph((Object) mapExecq1, MetaType.mapExec);
		return mapExecq1;		
	}*/

	/********************** UNUSED **********************/
	/*public MapExec findLatestByUuid(String mapExecUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iMapExecDao.findLatestByUuid(mapExecUUID,new Sort(Sort.Direction.DESC, "version"));
		}
		return iMapExecDao.findLatestByUuid(appUuid,mapExecUUID,new Sort(Sort.Direction.DESC, "version"));		
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		MapExec mapExec = iMapExecDao.findOneById(appUuid,id);
		String ID=mapExec.getId();
		iMapExecDao.delete(ID);		
	}*/

	/********************** UNUSED **********************/
	/*public MapExec findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iMapExecDao.findAllByUuid(appUuid,uuid);
		
	}*/

	/********************** UNUSED **********************/
	/*public List<MapExec> findAllLatestActive() 	
	{	   
	   Aggregation mapExecAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<MapExec> MapExecResults = mongoTemplate.aggregate(mapExecAggr,"mapexec", MapExec.class);	   
	   List<MapExec> mapExecList = MapExecResults.getMappedResults();

	   // Fetch the MapExec details for each id
	   List<MapExec> result=new  ArrayList<MapExec>();
	   for(MapExec r : mapExecList)
	   {   
		   MapExec mapExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				mapExecLatest = iMapExecDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				mapExecLatest = iMapExecDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
		
			result.add(mapExecLatest);
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public MapExec findOneByUuidAndVersion(String uuid, String version){
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if (appUuid != null) {
			return iMapExecDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else {
			return iMapExecDao.findOneByUuidAndVersion(uuid, version);
		}
	}*/

	/********************** UNUSED **********************/
	/*public MapExec resolveName(MapExec mapExec) {
		try {		
				String createdByRefUuid = mapExec.getCreatedBy().getRef().getUuid();
				User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
				mapExec.getCreatedBy().getRef().setName(user.getName());
				
				String dependsOnUuid=mapExec.getDependsOn().getRef().getUuid();
				Map map=mapServiceImpl.findLatestByUuid(dependsOnUuid);
				mapExec.getDependsOn().getRef().setName(map.getName());
				if(mapExec.getResult() != null){
					String dataStoreUuid = mapExec.getResult().getRef().getUuid();
					com.inferyx.framework.domain.DataStore datastore=dataStoreServiceImpl.findLatestByUuid(dataStoreUuid);
					mapExec.getResult().getRef().setName(datastore.getName());
			}			
			   
			if(mapExec.getRefKeyList() !=null){
				for(int i=0;i<mapExec.getRefKeyList().size();i++){
					MetaType type=mapExec.getRefKeyList().get(i).getType();
					if(type.toString().equals(MetaType.relation.toString())){
						String relationUuid=mapExec.getRefKeyList().get(i).getUuid();
						com.inferyx.framework.domain.Relation relation=relationServiceImpl.findLatestByUuid(relationUuid);
						mapExec.getRefKeyList().get(i).setName(relation.getName());
					}  
					else if(type.toString().equals(MetaType.datapod.toString())){
						String datapodUuid=mapExec.getRefKeyList().get(i).getUuid();
						com.inferyx.framework.domain.Datapod datapod=datapodServiceImpl.findLatestByUuid(datapodUuid);
						mapExec.getRefKeyList().get(i).setName(datapod.getName());
					}
				}
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mapExec;
	}
*/
	/********************** UNUSED **********************/
	/*public List<MapExec> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iMapExecDao.findAllVersion(appUuid, uuid);
		}
		else
		return iMapExecDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public List<MapExec> findMapExecByRule(String ruleUuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		List<MapExec> mapExecList = null;
		if (appUuid == null) {
			mapExecList = iMapExecDao.findOneByrule(ruleUuid);
		} else {
			mapExecList = iMapExecDao.findOneByrule(appUuid, ruleUuid);
		}
		List<MapExec> resolvedMapExecList = new ArrayList<>();
		for(MapExec mapExec : mapExecList)
		{
			resolveName(mapExec);
			resolvedMapExecList.add(mapExec);
		}
		return resolvedMapExecList;
	}*/

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//MapExec mapExec = iMapExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		MapExec mapExec = (MapExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.mapExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.map);
		mi.setUuid(mapExec.getDependsOn().getRef().getUuid());
		mi.setVersion(mapExec.getDependsOn().getRef().getVersion());
		return mi;
	}
	
	/**
	 * Kill meta thread if RUNNING
	 * @param uuid
	 * @param version
	 * @throws JsonProcessingException 
	 */
	public void kill (String uuid, String version) throws JsonProcessingException {
		MetaIdentifier mapExecMI = new MetaIdentifier(MetaType.mapExec, uuid, version);
//		MapExec mapExec = (MapExec) daoRegister.getRefObject(mapExecMI);
		MapExec mapExec = (MapExec) commonServiceImpl.getOneByUuidAndVersion(mapExecMI.getUuid(), mapExecMI.getVersion(), mapExecMI.getType().toString(), "N");
		if (mapExec == null) {
			logger.info("Nothing to kill. Aborting ... ");
			return;
		}
		
		try {
			mapExec = (MapExec) commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.TERMINATING);
			if (!Helper.getLatestStatus(mapExec.getStatusList()).equals(new Status(Status.Stage.TERMINATING, new Date()))) {
				logger.info(" Status is not TERMINATING. So aborting ... ");
				return;
			}
			FutureTask futureTask = (FutureTask) taskThreadMap.get("Map_" + mapExec.getUuid());
			if (futureTask != null && !futureTask.isDone()) {
				futureTask.cancel(true);
			}
			taskThreadMap.remove("Map_" + mapExec.getUuid());
			commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.KILLED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}			

	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion, String type) throws Exception {

		Object exec = commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		MetaIdentifierHolder resultHolder = (MetaIdentifierHolder) exec.getClass().getMethod("getResult").invoke(exec);
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
				resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion(), MetaType.datastore.toString());
		MetaIdentifier mi = new MetaIdentifier();
		ExecStatsHolder execHolder = new ExecStatsHolder();
		mi.setType(MetaType.datastore);
		mi.setUuid(resultHolder.getRef().getUuid());
		mi.setVersion(resultHolder.getRef().getVersion());
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}

}
