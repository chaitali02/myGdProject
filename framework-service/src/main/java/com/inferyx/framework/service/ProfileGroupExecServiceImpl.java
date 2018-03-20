package com.inferyx.framework.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.dao.IProfileExecDao;
import com.inferyx.framework.dao.IProfileGroupExecDao;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroup;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ProfileGroupExecServiceImpl extends BaseGroupExecTemplate{

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	MongoTemplate mongoTemplate;
	/*@Autowired
	private IProfileExecDao iProfileExecDao;*/
	@Autowired
	UserServiceImpl userServiceImpl;	
	@Autowired
	SecurityServiceImpl securityServiceImpl;	
	@Autowired
	protected IProfileGroupExecDao iProfileGroupExecDao;
	@Autowired
	ProfileGroupServiceImpl profileGroupServiceImpl;
	@Autowired
	ProfileExecServiceImpl profileExecServiceImpl;
	@Autowired
	MetadataUtil daoRegister;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Resource(name="taskThreadMap")
	ConcurrentHashMap taskThreadMap;
	
	static final Logger logger = Logger.getLogger(ProfileGroupExecServiceImpl.class);

	/********************** UNUSED **********************/
	/*public ProfileGroupExec findLatest() {
		ProfileGroupExec profilegroupexec=null;
		if(iProfileGroupExecDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			profilegroupexec=resolveName(iProfileGroupExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return profilegroupexec ;
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileGroupExec> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iProfileGroupExecDao.findAll(); 
		}
		return iProfileGroupExecDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroupExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iProfileGroupExecDao.findOneById(appUuid,id);
		}
		return iProfileGroupExecDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroupExec save(ProfileGroupExec ProfileGroupExec) throws Exception {
		if(ProfileGroupExec.getAppInfo() == null)
		{
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		ProfileGroupExec.setAppInfo(metaIdentifierHolderList);
		}
		ProfileGroupExec.setBaseEntity();
		ProfileGroupExec profileExecGroup=iProfileGroupExecDao.save(ProfileGroupExec);		
		registerGraph.updateGraph((Object) profileExecGroup, MetaType.profilegroupExec);
		return profileExecGroup;		
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroupExec findLatestByUuid(String ProfileGroupExecUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iProfileGroupExecDao.findLatestByUuid(ProfileGroupExecUUID,new Sort(Sort.Direction.DESC, "version"));
		}
		return iProfileGroupExecDao.findLatestByUuid(appUuid,ProfileGroupExecUUID,new Sort(Sort.Direction.DESC, "version"));		
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		ProfileGroupExec ProfileGroupExec = iProfileGroupExecDao.findOneById(appUuid,id);
		String ID=ProfileGroupExec.getId();
		iProfileGroupExecDao.delete(ID);		
	}*/
	/********************** UNUSED **********************/
	/*public List<ProfileGroupExec> findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iProfileGroupExecDao.findAllByUuid(appUuid,uuid);
		
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileGroupExec> findAllLatest()
	{	
		Aggregation profileGroupExecAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<ProfileGroupExec> profileExecResults = mongoTemplate.aggregate(profileGroupExecAggr, "profilegroupexec", ProfileGroupExec.class);
		List<ProfileGroupExec> ProfileGroupExecList = profileExecResults.getMappedResults();
		// Fetch the ProfileGroupExec details for each id
		List<ProfileGroupExec> result = new ArrayList<ProfileGroupExec>();
		for (ProfileGroupExec v : ProfileGroupExecList) {
			ProfileGroupExec ProfileGroupExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{			
				ProfileGroupExecLatest = iProfileGroupExecDao.findOneByUuidAndVersion(appUuid,v.getId(), v.getVersion());
			}
			else
			{
				ProfileGroupExecLatest = iProfileGroupExecDao.findOneByUuidAndVersion(v.getId(), v.getVersion());
			}
			
			if(ProfileGroupExecLatest != null)
			{
			result.add(ProfileGroupExecLatest);
			}
		}	
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileGroupExec> findAllLatestActive() 	
	{	   
	   Aggregation profileGroupExecAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<ProfileGroupExec> profileGroupExecResults = mongoTemplate.aggregate(profileGroupExecAggr,"profilegroupexec", ProfileGroupExec.class);	   
	   List<ProfileGroupExec> profileGroupExecList = profileGroupExecResults.getMappedResults();

	   // Fetch the ProfileGroupExec details for each id
	   List<ProfileGroupExec> result=new  ArrayList<ProfileGroupExec>();
	   for(ProfileGroupExec r : profileGroupExecList)
	   {   
		   ProfileGroupExec ProfileGroupExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				ProfileGroupExecLatest = iProfileGroupExecDao.findOneByUuidAndVersion(appUuid,r.getId(), r.getVersion());
			}
			else
			{
				ProfileGroupExecLatest = iProfileGroupExecDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}
			if(ProfileGroupExecLatest != null)
			{
			result.add(ProfileGroupExecLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public ProfileGroupExec findOneByUuidAndVersion(String uuid, String version){
		 ProfileGroupExec ProfileGroupExecLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				return ProfileGroupExecLatest = iProfileGroupExecDao.findOneByUuidAndVersion(appUuid,uuid,version);
			}			
				return ProfileGroupExecLatest = iProfileGroupExecDao.findOneByUuidAndVersion(uuid,version);		
	}*/

	public List<ProfileGroupExec> resolveName(List<ProfileGroupExec> ProfileGroupExec) throws JsonProcessingException {
		List<ProfileGroupExec> ProfileGroupExecList = new ArrayList<>();
		for(ProfileGroupExec profileGroupE : ProfileGroupExec)
		{
		String createdByRefUuid = profileGroupE.getCreatedBy().getRef().getUuid();
		//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		profileGroupE.getCreatedBy().getRef().setName(user.getName());
		ProfileGroupExecList.add(profileGroupE);
		}
		return ProfileGroupExecList;
	}

	/********************** UNUSED **********************/
	/*public ProfileGroupExec resolveName(ProfileGroupExec ProfileGroupExec) {
		String createdByRefUuid = ProfileGroupExec.getCreatedBy().getRef().getUuid();
		User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		ProfileGroupExec.getCreatedBy().getRef().setName(user.getName());
		
		String dependsOnUuid=ProfileGroupExec.getDependsOn().getRef().getUuid();
		ProfileGroup profilegroup=profileGroupServiceImpl.findLatestByUuid(dependsOnUuid);
		ProfileGroupExec.getDependsOn().getRef().setName(profilegroup.getName());

		if(ProfileGroupExec.getExecList() !=null){
		for(int i=0;i<ProfileGroupExec.getExecList().size();i++){
		String profileExecUuid=ProfileGroupExec.getExecList().get(i).getRef().getUuid();
		ProfileExec profileExec=profileExecServiceImpl.findLatestByUuid(profileExecUuid);
		ProfileGroupExec.getExecList().get(i).getRef().setName(profileExec.getName());

		}

		}
		
		return ProfileGroupExec;
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileGroupExec> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iProfileGroupExecDao.findAllVersion(appUuid, uuid);
		}
		else
		return iProfileGroupExecDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public ProfileGroupExec getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iProfileGroupExecDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iProfileGroupExecDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(ProfileGroupExec profileGroupExec) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		ProfileGroupExec profileGroupExecNew = new ProfileGroupExec();
		profileGroupExecNew.setName(profileGroupExec.getName()+"_copy");
		profileGroupExecNew.setActive(profileGroupExec.getActive());		
		profileGroupExecNew.setDesc(profileGroupExec.getDesc());		
		profileGroupExecNew.setTags(profileGroupExec.getTags());
		profileGroupExecNew.setStatusList(profileGroupExec.getStatusList());
		profileGroupExecNew.setDependsOn(profileGroupExec.getDependsOn());
		profileGroupExecNew.setExecList(profileGroupExec.getExecList());		
		save(profileGroupExecNew);
		ref.setType(MetaType.profilegroupExec);
		ref.setUuid(profileGroupExecNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> profileGroupExecList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity profilegroup : profileGroupExecList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = profilegroup.getId();
			String uuid = profilegroup.getUuid();
			String version = profilegroup.getVersion();
			String name = profilegroup.getName();
			String desc = profilegroup.getDesc();
			String published=profilegroup.getPublished();
			MetaIdentifierHolder createdBy = profilegroup.getCreatedBy();
			String createdOn = profilegroup.getCreatedOn();
			String[] tags = profilegroup.getTags();
			String active = profilegroup.getActive();
			List<MetaIdentifierHolder> appInfo = profilegroup.getAppInfo();
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

	public List<ProfileGroupExec> findProfileGroupExecByProfileGroup(String profileGroupUUID,
			String profileGroupVersion) throws JsonProcessingException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		List<ProfileGroupExec> profileGroupExecList = null;
		if(appUuid != null)
		{
		profileGroupExecList = iProfileGroupExecDao.findProfileGroupExecByProfileGroup(profileGroupUUID, profileGroupVersion);
		}
		else{
			profileGroupExecList = iProfileGroupExecDao.findProfileGroupExecByProfileGroup(profileGroupUUID, profileGroupVersion);
		}		
		return resolveName(profileGroupExecList);
	}

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//ProfileGroupExec profileGroupExec = iProfileGroupExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		ProfileGroupExec profileGroupExec = (ProfileGroupExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.profilegroupExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.profilegroup);
		mi.setUuid(profileGroupExec.getDependsOn().getRef().getUuid());
		mi.setVersion(profileGroupExec.getDependsOn().getRef().getVersion());
		return mi;
	}
	
	/**
	 * Kill group
	 * @param uuid
	 * @param version
	 */
	/*public void kill (String uuid, String version) {
		MetaIdentifier profileGroupExecIdentifier = new MetaIdentifier(MetaType.profilegroupExec, uuid, version);
		ProfileGroupExec profileGroupExec = (ProfileGroupExec) daoRegister.getRefObject(profileGroupExecIdentifier);
		if (profileGroupExec == null) {
			logger.info("Nothing to kill. Aborting ... ");
			return;
		}
		
		if (!Helper.getLatestStatus(profileGroupExec.getStatus()).equals(new Status(Status.Stage.InProgress, new Date()))) {
			logger.info(" Status is not in progress. So aborting ... ");
		}
		try {
			commonServiceImpl.setMetaStatus(profileGroupExec, MetaType.profilegroupExec, Status.Stage.Terminating);
			for (MetaIdentifierHolder profileExecHolder : profileGroupExec.getProfileExecList()) {
				ProfileExec profileExec = (ProfileExec) daoRegister.getRefObject(profileExecHolder.getRef());
				if (profileExec == null) {
					continue;
				}
				profileExecServiceImpl.kill (profileExec.getUuid(), profileExec.getVersion());
			}
			commonServiceImpl.setMetaStatus(profileGroupExec, MetaType.profilegroupExec, Status.Stage.Killed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.profilegroupExec, MetaType.profileExec);
	}
	


}
