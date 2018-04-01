package com.inferyx.framework.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.dao.IProfileExecDao;
import com.inferyx.framework.dao.IProfileGroupExecDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ProfileExecServiceImpl extends BaseRuleExecTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IProfileExecDao iProfileExecDao;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	ProfileServiceImpl profileServiceImpl;
	@Autowired
	IProfileGroupExecDao iProfileGroupExecDao;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	MetadataUtil daoRegister;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(ProfileExecServiceImpl.class);

	/********************** UNUSED **********************/
	/*public ProfileExec findLatest() {
		ProfileExec profileexec=null;
		if(iProfileExecDao.findLatest(new Sort(Sort.Direction.DESC, "version"))!=null){
			profileexec=resolveName(iProfileExecDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
		}
		return profileexec ;
	}*/
	
	/********************** UNUSED **********************/
	/*public ProfileExec findAllByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		return iProfileExecDao.findAllByUuid(appUuid, uuid);
	}*/

	/********************** UNUSED **********************/
	/*public ProfileExec findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iProfileExecDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iProfileExecDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public ProfileExec findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iProfileExecDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else
			return iProfileExecDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public ProfileExec findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iProfileExecDao.findOneById(appUuid, id);
		} else
			return iProfileExecDao.findOne(id);

	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileExec> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iProfileExecDao.findAll();
		}
		return iProfileExecDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		ProfileExec ProfileExec = iProfileExecDao.findOneById(appUuid, Id);
		String ID = ProfileExec.getId();
		iProfileExecDao.delete(appUuid, ID);
		ProfileExec.setBaseEntity();
	}*/

	public ProfileExec resolveName(ProfileExec ProfileExec) {
		try {
			if (ProfileExec.getCreatedBy() != null) {
				String createdByRefUuid = ProfileExec.getCreatedBy().getRef().getUuid();
				User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
				ProfileExec.getCreatedBy().getRef().setName(user.getName());
			}
			if (ProfileExec.getAppInfo() != null) {
				for (int i = 0; i < ProfileExec.getAppInfo().size(); i++) {
					String appUuid = ProfileExec.getAppInfo().get(i).getRef().getUuid();
					Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
					String appName = application.getName();
					ProfileExec.getAppInfo().get(i).getRef().setName(appName);
				}
			}
			String dependsOnRefUuid = ProfileExec.getDependsOn().getRef().getUuid();
			Profile profileDO = (Profile) commonServiceImpl.getLatestByUuid(dependsOnRefUuid, MetaType.profile.toString());
			String profileName = profileDO.getName();
			ProfileExec.getDependsOn().getRef().setName(profileName);
			
			if(ProfileExec.getResult() != null){
				String dataStoreUuid = ProfileExec.getResult().getRef().getUuid();
				DataStore datastore = (DataStore) commonServiceImpl.getLatestByUuid(dataStoreUuid, MetaType.datastore.toString());
				ProfileExec.getResult().getRef().setName(datastore.getName());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ProfileExec;
	}

	/********************** UNUSED **********************/
	/*public ProfileExec save(ProfileExec ProfileExec) throws Exception {
		if(ProfileExec.getAppInfo() == null)
		{
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		ProfileExec.setAppInfo(metaIdentifierHolderList);
		}
		System.out.println(" Profile exec App info : " + ProfileExec.getAppInfo());
		ProfileExec.setBaseEntity();
		ProfileExec app=iProfileExecDao.save(ProfileExec);
		registerGraph.updateGraph((Object) app, MetaType.profileExec);
		return app;		
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileExec> findAllLatest() {
		{
			Aggregation ProfileExecAggr = newAggregation(group("uuid").max("version").as("version"));
			AggregationResults<ProfileExec> ProfileExecResults = mongoTemplate.aggregate(ProfileExecAggr, "profileexec", ProfileExec.class);
			List<ProfileExec> ProfileExecList = ProfileExecResults.getMappedResults();

			// Fetch the ProfileExec details for each id
			List<ProfileExec> result = new ArrayList<ProfileExec>();
			for (ProfileExec s : ProfileExecList) {
				ProfileExec ProfileExecLatest;
				String appUuid = (securityServiceImpl.getAppInfo() != null
						&& securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
				if (appUuid == null) {					
					ProfileExecLatest = iProfileExecDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
				} else {
					ProfileExecLatest = iProfileExecDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
				}
				if (ProfileExecLatest != null) {
					result.add(ProfileExecLatest);
				}
			}
			return result;
		}
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileExec> findAllLatestActive() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Aggregation ProfileExecAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<ProfileExec> ProfileExecResults = mongoTemplate.aggregate(ProfileExecAggr, "profileexec", ProfileExec.class);
		List<ProfileExec> ProfileExecList = ProfileExecResults.getMappedResults();

		// Fetch the ProfileExec details for each id
		List<ProfileExec> result = new ArrayList<ProfileExec>();
		for (ProfileExec p : ProfileExecList) {
			ProfileExec ProfileExecLatest;
			if (appUuid != null) {
				ProfileExecLatest = iProfileExecDao.findOneByUuidAndVersion(appUuid, p.getId(), p.getVersion());
			} else {
				ProfileExecLatest = iProfileExecDao.findOneByUuidAndVersion(p.getId(), p.getVersion());
			}
			if (ProfileExecLatest != null) {
				result.add(ProfileExecLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileExec> resolveName(List<ProfileExec> ProfileExec) {
		List<ProfileExec> ProfileExecList = new ArrayList<ProfileExec>();
		for (ProfileExec con : ProfileExec) {
			String createdByRefUuid = con.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			con.getCreatedBy().getRef().setName(user.getName());
			ProfileExecList.add(con);
		}
		return ProfileExecList;
	}*/

	/********************** UNUSED **********************/
	/*public List<ProfileExec> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iProfileExecDao.findAllVersion(appUuid, uuid);
		} else
			return iProfileExecDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public ProfileExec getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iProfileExecDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iProfileExecDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(ProfileExec ProfileExec) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();
		ProfileExec ProfileExecNew = new ProfileExec();
		ProfileExecNew.setName(ProfileExec.getName() + "_copy");
		ProfileExecNew.setActive(ProfileExec.getActive());
		ProfileExecNew.setDesc(ProfileExec.getDesc());
		ProfileExecNew.setTags(ProfileExec.getTags());
		ProfileExecNew.setDependsOn(ProfileExec.getDependsOn());	
		ProfileExecNew.setExec(ProfileExec.getExec());
		ProfileExecNew.setExecParams(ProfileExec.getExecParams());
		ProfileExecNew.setStatusList(ProfileExec.getStatusList());
		ProfileExecNew.setResult(ProfileExec.getResult());
		save(ProfileExecNew);
		ref.setType(MetaType.profileExec);
		ref.setUuid(ProfileExecNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED 
	 * @throws ParseException 
	 * @throws JsonProcessingException **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> ProfileExecList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for (BaseEntity ProfileExec : ProfileExecList) {
			BaseEntity baseEntity = new BaseEntity();
			String id = ProfileExec.getId();
			String uuid = ProfileExec.getUuid();
			String version = ProfileExec.getVersion();
			String name = ProfileExec.getName();
			String desc = ProfileExec.getDesc();
			String published=ProfileExec.getPublished();
			MetaIdentifierHolder createdBy = ProfileExec.getCreatedBy();
			String createdOn = ProfileExec.getCreatedOn();
			String[] tags = ProfileExec.getTags();
			String active = ProfileExec.getActive();
			List<MetaIdentifierHolder> appInfo = ProfileExec.getAppInfo();
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

	@SuppressWarnings({ "unchecked", "null" })
	public List<ProfileExec> findProfileExecByDatapod(String datapodUUID,String type) throws JsonProcessingException, ParseException {

		List<String> profileUUIDlist = new ArrayList<String>();
		List<ProfileExec> result = new ArrayList<ProfileExec>();

		List<Profile> profileobj = (List<Profile>) commonServiceImpl
				.getAllLatestCompleteObjects(MetaType.profile.toString(), null);
		for (Profile profile : profileobj) {
			if (datapodUUID.equalsIgnoreCase(profile.getDependsOn().getRef().getUuid())) {
				profileUUIDlist.add(profile.getUuid().toString());
			}
		}

		List<ProfileExec> profileexecobj = (List<ProfileExec>) commonServiceImpl
				.getAllLatestCompleteObjects(MetaType.profileExec.toString(), null);

		for (ProfileExec profileexec : profileexecobj) {

			for (String profileuuid : profileUUIDlist) {
				if (profileuuid.equalsIgnoreCase(profileexec.getDependsOn().getRef().getUuid())) {
					result.add(profileexec);
				}
			}
		}

		return result;
	}
	
	
	
	public List<ProfileExec> findProfileExecByProfile(String profileUuid, String startDate, String endDate, String type, String action){		
		Query query = new Query();
		query.fields().include("statusList");
		query.fields().include("dependsOn");
		query.fields().include("exec");
		query.fields().include("result");
		query.fields().include("refKeyList");
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("createdBy");
		query.fields().include("createdOn");
		query.fields().include("active");
		query.fields().include("published");
		query.fields().include("appInfo");
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("EEE MMM dd hh:mm:ss yyyy z");// Tue Mar 13 04:15:00 2018 UTC
			
		
		try {
			if ((startDate != null	&& !StringUtils.isEmpty(startDate))
				&& (endDate != null	&& !StringUtils.isEmpty(endDate))) {
			query.addCriteria(Criteria.where("createdOn").gte(simpleDateFormat.parse(startDate))
						.lte(simpleDateFormat.parse(endDate)));
			}
			else if((startDate != null && !startDate.isEmpty()) && StringUtils.isEmpty(endDate)) 
					{
				query.addCriteria(Criteria.where("createdOn").gte(simpleDateFormat.parse(startDate)));
					}
			else if (endDate != null && !endDate.isEmpty())
				query.addCriteria(Criteria.where("createdOn").lte(simpleDateFormat.parse(endDate)));
			
			query.addCriteria(Criteria.where("statusList.stage").in("Completed"));
			query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(profileUuid));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<ProfileExec> profileExecObjList = new ArrayList<>();
		profileExecObjList = (List<ProfileExec>) mongoTemplate.find(query, ProfileExec.class);
		
		return profileExecObjList;		
	}
	
	public List<ProfileExec> findProfileExecByDatapod(String datapodUUID, String startDate, String endDate, String type){
		List<ProfileExec> profileExecObjList = new ArrayList<>();
		List<ProfileExec> execObjList = new ArrayList<>();

		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("dependsOn");
		query.fields().include("createdOn");
		query.fields().include("appInfo");

		try {
			if ((datapodUUID != null && !StringUtils.isEmpty(datapodUUID)))
				query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(datapodUUID));
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Profile> profileList = new ArrayList<>();
		profileList = (List<Profile>) mongoTemplate.find(query, Profile.class);
		
		for (Profile profile : profileList) {
			System.out.println("UUID : "+profile.getUuid());
			profileExecObjList = findProfileExecByProfile(profile.getUuid(), startDate, endDate, null, null);
			if(!profileExecObjList.isEmpty()) {
				execObjList.addAll(profileExecObjList);
			}
			
			}		
		return execObjList;	
	}
	

	public List<ProfileExec> findProfileExecByProfile(String profileUUID) {
		List<ProfileExec> profileExecList=null;
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if (appUuid != null) {
			profileExecList = iProfileExecDao.findOneByProfile(appUuid, profileUUID);
		}else{
			profileExecList = iProfileExecDao.findOneByProfile(profileUUID);
		}
		List<ProfileExec> resolvedProfileExecList = new ArrayList<>();
		for(ProfileExec profileExec : profileExecList) {
			resolveName(profileExec);
			resolvedProfileExecList.add(profileExec);
		}
		return resolvedProfileExecList;
	}
	
	public List<ProfileExec> findProfileExecByProfile(String profileUUID, String startDate, String endDate, String type){
		List<ProfileExec> profileExecObjList = new ArrayList<>();
		List<ProfileExec> execObjList = new ArrayList<>();
		
			profileExecObjList = findProfileExecByProfile(profileUUID, startDate, endDate, null, null);
			if(!profileExecObjList.isEmpty()) {
				execObjList.addAll(profileExecObjList);
			}
			
					
		return execObjList;	
	}

	public List<ProfileExec> getProfileExecByProfileGroupExec(String profileGroupExecUuid, String profileGroupExecVersion) throws JsonProcessingException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		ProfileGroupExec profileGroupExec = null;
		if (appUuid == null) {
			profileGroupExec = iProfileGroupExecDao.findOneByUuidAndVersion(profileGroupExecUuid, profileGroupExecVersion);
		} else {
			profileGroupExec = iProfileGroupExecDao.findOneByUuidAndVersion(appUuid, profileGroupExecUuid, profileGroupExecVersion);
		}*/
		ProfileGroupExec profileGroupExec = (ProfileGroupExec) commonServiceImpl.getOneByUuidAndVersion(profileGroupExecUuid, profileGroupExecVersion, MetaType.profilegroupExec.toString());
		List<MetaIdentifierHolder> profileExecHolderList=profileGroupExec.getExecList();
		ProfileExec profileexec=null;
		List<ProfileExec> profileExecList=new ArrayList<ProfileExec>();
		for(MetaIdentifierHolder profileExecHolder:profileExecHolderList){
			//profileexec=iProfileExecDao.findOneByUuidAndVersion(profileExecHolder.getRef().getUuid(), profileExecHolder.getRef().getVersion());
			profileexec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(profileExecHolder.getRef().getUuid(), profileExecHolder.getRef().getVersion(), MetaType.profileExec.toString());
			resolveName(profileexec);
			profileExecList.add(profileexec);
		}
		return profileExecList;
	}

	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//ProfileExec profileExec = iProfileExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		ProfileExec profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.profileExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.profile);
		mi.setUuid(profileExec.getDependsOn().getRef().getUuid());
		mi.setVersion(profileExec.getDependsOn().getRef().getVersion());
		return mi;
	}
    
	public void onHold (String uuid, String version) throws JsonProcessingException {
	MetaIdentifier profileExecIdentifier = new MetaIdentifier(MetaType.profileExec, uuid, version);
	ProfileExec profileExec = (ProfileExec) daoRegister.getRefObject(profileExecIdentifier);
	if (profileExec == null) {
		logger.info("ProfileExec not found. Exiting...");
		return;
	}
	if (!Helper.getLatestStatus(profileExec.getStatusList()).equals(new Status(Status.Stage.NotStarted, new Date()))) {
		logger.info("Latest Status is not in NotStarted. Exiting...");
	}
	try {
		commonServiceImpl.setMetaStatus(profileExec, MetaType.profileExec, Status.Stage.OnHold);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
	
	public void resume (String uuid, String version) throws JsonProcessingException {
		MetaIdentifier profileExecIdentifier = new MetaIdentifier(MetaType.profileExec, uuid, version);
		ProfileExec profileExec = (ProfileExec) daoRegister.getRefObject(profileExecIdentifier);
		if (profileExec == null) {
			logger.info("ProfileExec not found. Exiting...");
			return;
		}
		if (!Helper.getLatestStatus(profileExec.getStatusList()).equals(new Status(Status.Stage.OnHold, new Date()))) {
			logger.info("Latest Status is not in OnHold. Exiting...");
		}
		try {
			commonServiceImpl.setMetaStatus(profileExec, MetaType.profileExec, Status.Stage.Resume);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion) throws Exception {
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//ProfileExec profileExec = iProfileExecDao.findOneByUuidAndVersion(appUuid, execUuid, execVersion);
		ProfileExec profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.profileExec.toString());
		//com.inferyx.framework.domain.DataStore dataStore=dataStoreServiceImpl.findOneByUuidAndVersion(profileExec.getResult().getRef().getUuid(), profileExec.getResult().getRef().getVersion());
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(profileExec.getResult().getRef().getUuid(), profileExec.getResult().getRef().getVersion(), MetaType.datastore.toString());
		MetaIdentifier mi = new MetaIdentifier();
		ExecStatsHolder execHolder=new ExecStatsHolder();
		mi.setType(MetaType.datastore);
		mi.setUuid(profileExec.getResult().getRef().getUuid());
		mi.setVersion(profileExec.getResult().getRef().getVersion());
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}
	
	/**
	 * Kill meta thread if In Progress
	 * @param uuid
	 * @param version
	 */
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.profileExec);
	}
}
