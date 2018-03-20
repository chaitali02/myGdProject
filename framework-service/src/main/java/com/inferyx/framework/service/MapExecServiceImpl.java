package com.inferyx.framework.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.dao.IMapExecDao;
import com.inferyx.framework.dao.IRuleGroupExecDao;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;
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
	private IMapExecDao iMapExecDao;
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
	@Autowired
	MetadataUtil daoRegister;
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
	/*public List<MapExec> findAllLatest()	{		
		//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Aggregation mapExecAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<MapExec> MapExecResults = mongoTemplate.aggregate(mapExecAggr, "mapexec", MapExec.class);
		List<MapExec> MapExecList = MapExecResults.getMappedResults();
		// Fetch the VizExec details for each id
		List<MapExec> result = new ArrayList<MapExec>();
		for (MapExec v : MapExecList) {
			MapExec mapExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
			//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();;
				mapExecLatest = iMapExecDao.findOneByUuidAndVersion(appUuid,v.getId(), v.getVersion());
			}
			else
			{
				mapExecLatest = iMapExecDao.findOneByUuidAndVersion(v.getId(), v.getVersion());
			}
			//logger.debug("datapodLatest is " + datapodLatest.getName());
			result.add(mapExecLatest);
		}	
		return result;
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
	/*public List<MapExec> resolveName(List<MapExec> MapExec) {
		List<MapExec> mapExecList = new ArrayList<>();
		for(MapExec ruleE : MapExec)
		{
		MapExec mapExecLatest = findOneByUuidAndVersion(ruleE.getUuid(), ruleE.getVersion()); 
		String createdByRefUuid = ruleE.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		mapExecLatest.getCreatedBy().getRef().setName(user.getName());		
		mapExecList.add(mapExecLatest);
		}
		return mapExecList;
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
	 * Kill meta thread if In Progress
	 * @param uuid
	 * @param version
	 * @throws JsonProcessingException 
	 */
	public void kill (String uuid, String version) throws JsonProcessingException {
		MetaIdentifier mapExecIdentifier = new MetaIdentifier(MetaType.mapExec, uuid, version);
		MapExec mapExec = (MapExec) daoRegister.getRefObject(mapExecIdentifier);
		if (mapExec == null) {
			logger.info("Nothing to kill. Aborting ... ");
			return;
		}
		if (!Helper.getLatestStatus(mapExec.getStatusList()).equals(new Status(Status.Stage.InProgress, new Date()))) {
			logger.info(" Status is not in progress. So aborting ... ");
		}
		try {
			commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.Terminating);
			FutureTask futureTask = (FutureTask) taskThreadMap.get("Map_" + mapExec.getUuid());
			if (futureTask != null && !futureTask.isDone()) {
				futureTask.cancel(true);
			}
			taskThreadMap.remove("Map_" + mapExec.getUuid());
			commonServiceImpl.setMetaStatus(mapExec, MetaType.mapExec, Status.Stage.Killed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}			
}
