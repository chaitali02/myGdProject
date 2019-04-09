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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.ProfileInfo;
import com.inferyx.framework.dao.IProfileDao;
import com.inferyx.framework.dao.IProfileExecDao;
import com.inferyx.framework.dao.IProfileGroupExecDao;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.ProfileOperator;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ProfileServiceImpl extends RuleTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
	/*
	 * @Autowired JavaSparkContext javaSparkContext;
	 */
	@Autowired
	IProfileDao iProfileDao;
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
	HDFSInfo hdfsInfo;
	@Autowired
	protected DataSourceFactory datasourceFactory;
	@Autowired
	ThreadPoolTaskExecutor metaExecutor;
	@Autowired
	ProfileExecServiceImpl profileExecServiceImpl;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	IProfileExecDao iProfileExecDao;
	@Autowired
	IProfileGroupExecDao iProfileGroupExecDao;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ProfileInfo profileInfo;
	@Autowired
	ConnectionFactory connFactory;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	@Autowired
	Engine engine;
	@Autowired
	ProfileOperator profileOperator; 
	@Resource(name = "taskThreadMap")
	ConcurrentHashMap<?, ?> taskThreadMap;
	@Autowired
	private DownloadServiceImpl downloadServiceImpl;

	Map<String, String> requestMap = new HashMap<String, String>();

	static final Logger logger = Logger.getLogger(ProfileServiceImpl.class);

	/*
	 * public Profile findLatest() { return resolveName(iProfileDao.findLatest(new
	 * Sort(Sort.Direction.DESC, "version"))); }
	 */

	/**************************Unused***************************/
	/*public Profile findAllByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		return iProfileDao.findAllByUuid(appUuid, uuid);
	}*/

	/*public Profile findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid == null) {
			return iProfileDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iProfileDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/*public Profile findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;

		if (appUuid != null) {
			return iProfileDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else
			return iProfileDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/*public Profile findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iProfileDao.findOneById(appUuid, id);
		} else
			return iProfileDao.findOne(id);

	}*/

	
	/*******************Unused********************/
	/*public List<Profile> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid == null) {
			return iProfileDao.findAll();
		}
		return iProfileDao.findAll(appUuid);
	}*/

	public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Profile Profile = iProfileDao.findOneById(appUuid, Id);
		String ID = Profile.getId();
		iProfileDao.delete(appUuid, ID);
		Profile.setBaseEntity();
	}

	public Profile save(Profile profile) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		profile.setAppInfo(metaIdentifierHolderList);
		profile.setBaseEntity();
		Profile ProfileDet = iProfileDao.save(profile);
		registerGraph.updateGraph((Object) ProfileDet, MetaType.profile);
		return ProfileDet;
	}

	
	/*********************Unused********************/
	/*public List<Profile> findAllLatest() {
		{
			Aggregation ProfileAggr = newAggregation(group("uuid").max("version").as("version"));
			AggregationResults<Profile> ProfileResults = mongoTemplate.aggregate(ProfileAggr, "profile", Profile.class);
			List<Profile> ProfileList = ProfileResults.getMappedResults();

			// Fetch the profile details for each id
			List<Profile> result = new ArrayList<Profile>();
			for (Profile s : ProfileList) {
				Profile ProfileLatest;
				String appUuid = (securityServiceImpl.getAppInfo() != null
						&& securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid()
								: null;
				if (appUuid == null) {
					ProfileLatest = iProfileDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
				} else {
					ProfileLatest = iProfileDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
				}
				if (ProfileLatest != null) {
					result.add(ProfileLatest);
				}
			}
			return result;
		}
	}
*/
	/*************************Unused***********************/
	/*public List<Profile> findAllLatestActive() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		Aggregation profileAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Profile> profileResults = mongoTemplate.aggregate(profileAggr, "profile", Profile.class);
		List<Profile> profileList = profileResults.getMappedResults();

		// Fetch the Profile details for each id
		List<Profile> result = new ArrayList<Profile>();
		for (Profile p : profileList) {
			Profile profileLatest;
			if (appUuid != null) {
				profileLatest = iProfileDao.findOneByUuidAndVersion(appUuid, p.getId(), p.getVersion());
			} else {
				profileLatest = iProfileDao.findOneByUuidAndVersion(p.getId(), p.getVersion());
			}
			if (profileLatest != null) {
				result.add(profileLatest);
			}
		}
		return result;
	}*/

	/********************Unused******************/
	/*public List<Profile> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iProfileDao.findAllVersion(appUuid, uuid);
		} else
			return iProfileDao.findAllVersion(uuid);
	}*/

	public Profile getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iProfileDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iProfileDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}

	/*************************Unused******************/
	/*
	public MetaIdentifierHolder saveAs(Profile profile) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();
		Profile profileNew = new Profile();
		profileNew.setName(profile.getName() + "_copy");
		profileNew.setActive(profile.getActive());
		profileNew.setDesc(profile.getDesc());
		profileNew.setTags(profile.getTags());
		profileNew.setDependsOn(profile.getDependsOn());
		profileNew.setAttributeInfo(profile.getAttributeInfo());
		save(profileNew);
		ref.setType(MetaType.profile);
		ref.setUuid(profileNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	
	/***********************Unused********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> ProfileList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for (BaseEntity Profile : ProfileList) {
			BaseEntity baseEntity = new BaseEntity();
			String id = Profile.getId();
			String uuid = Profile.getUuid();
			String version = Profile.getVersion();
			String name = Profile.getName();
			String desc = Profile.getDesc();
			String published = Profile.getPublished();
			MetaIdentifierHolder createdBy = Profile.getCreatedBy();
			String createdOn = Profile.getCreatedOn();
			String[] tags = Profile.getTags();
			String active = Profile.getActive();
			List<MetaIdentifierHolder> appInfo = Profile.getAppInfo();
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
	}
*/
	public ProfileExec create(String profileUUID, String profileVersion, ProfileExec profileExec,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws NumberFormatException, Exception {
		try {
			return (ProfileExec) super.create(profileUUID, profileVersion, MetaType.profile, MetaType.profileExec,
					profileExec, refKeyMap, datapodList, dagExec, runMode);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.profileExec, profileExec.getUuid(), profileExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create ProfileExec.", dependsOn);
			throw new NumberFormatException((message != null) ? message : "Can not create ProfileExec.");
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.profileExec, profileExec.getUuid(), profileExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create ProfileExec.", dependsOn);
			throw new Exception((message != null) ? message : "Can not create ProfileExec.");
		}		
	}

	public ProfileExec execute(String profileUUID, String profileVersion, ProfileExec profileExec,
			ThreadPoolTaskExecutor metaExecutor, ProfileGroupExec profileGroupexec, ExecParams execParams, RunMode runMode) throws Exception {
		return execute(profileUUID, profileVersion, profileExec, null, profileGroupexec, null, execParams, runMode);
	}

	public ProfileExec execute(String profileUUID, String profileVersion, ProfileExec profileExec,
			ThreadPoolTaskExecutor metaExecutor, ProfileGroupExec profileGroupExec,
			List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		return (ProfileExec) execute(metaExecutor, profileExec, null, taskList, execParams, runMode);
	}

	public List<Map<String, Object>> getResults(String profileExecUUID, String profileExecVersion, int offset,
			int limit, String sortBy, String order, String requestId, RunMode runMode) throws Exception {
//		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset + limit;
			offset = offset + 1;
			
			ProfileExec profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(profileExecUUID,
					profileExecVersion, MetaType.profileExec.toString());
			DataStore datastore = dataStoreServiceImpl.getDatastore(profileExec.getResult().getRef().getUuid(),
					profileExec.getResult().getRef().getVersion());
			return dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order, profileExec.getVersion(),runMode);
			
			/*dataStoreServiceImpl.setRunMode(runMode);
			String tableName = dataStoreServiceImpl.getTableNameByDatastoreKey(datastore.getUuid(), datastore.getVersion(),
					runMode);
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			ExecContext execContext = null;
			IExecutor exec = null;
			// String sql = null;
			String appUuid = null;
			if (runMode.equals(Mode.ONLINE)) {
				execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark")
						|| engine.getExecEngine().equalsIgnoreCase("livy_spark"))
								? helper.getExecutorContext(engine.getExecEngine())
								: helper.getExecutorContext(ExecContext.spark.toString());
				appUuid = commonServiceImpl.getApp().getUuid();
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			exec = execFactory.getExecutor(execContext.toString());
			appUuid = commonServiceImpl.getApp().getUuid();
			if (datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
					|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
					|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
					|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
				data = exec
						.executeAndFetch(
								"SELECT * FROM (SELECT Row_Number() Over(ORDER BY version DESC) AS rownum, * FROM "
										+ tableName + ") AS tab WHERE rownum >= " + offset + " AND rownum <= " + limit,
								appUuid);
			} else {
				if (datasource.getType().toLowerCase().contains("oracle"))
					if (runMode.equals(Mode.ONLINE))
						data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
					else
						data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum<= " + limit, appUuid);
				else {
					data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.profileExec, profileExecUUID, profileExecVersion));
			commonServiceImpl.sendResponse("402", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", dependsOn);
			throw new Exception((message != null) ? message : "Table not found.");
		}
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	
	/**********************Unused**********************/
	/*public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.profileExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}*/


	public void restart(String type, String uuid, String version, ExecParams execParams, RunMode runMode)
			throws Exception {
		// ProfileExec profileExec= profileExecServiceImpl.findOneByUuidAndVersion(uuid,
		// version);
		ProfileExec profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version,
				MetaType.profileExec.toString());
		// try {
		// profileExec = create(profileExec.getDependsOn().getRef().getUuid(),
		// profileExec.getDependsOn().getRef().getVersion(), profileExec,null,null,
		// null);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		try {	
			HashMap<String, String> otherParams = null;
			if(execParams != null) 
			//if(execParams.getOtherParams() != null)
				otherParams = execParams.getOtherParams();
			
			profileExec = (ProfileExec) parse(profileExec.getUuid(), profileExec.getVersion(), null, otherParams, null, null,
					runMode);
			execute(profileExec.getDependsOn().getRef().getUuid(), profileExec.getDependsOn().getRef().getVersion(),
					profileExec, null, null, execParams, runMode);
			
		} catch (Exception e) {
			synchronized (profileExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(profileExec, MetaType.profileExec, Status.Stage.FAILED);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.profileExec, profileExec.getUuid(), profileExec.getVersion()));
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Profile.", dependsOn);
					throw new Exception((message != null) ? message : "Can not parse Profile.");
				}
			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.profileExec, profileExec.getUuid(), profileExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Profile.", dependsOn);
			throw new Exception((message != null) ? message : "Can not parse Profile.");
		}

	}

	@Override
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		ProfileExec profileExec =null;
		try {
			Profile profile = null;
//			ProfileOperator profileOperator = null;
			StringBuilder sbProfileSelect = new StringBuilder();
			profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
					MetaType.profileExec.toString());
			synchronized (execUuid) {
				commonServiceImpl.setMetaStatus(profileExec, MetaType.profileExec, Status.Stage.STARTING);
			}
			profile = (Profile) commonServiceImpl.getOneByUuidAndVersion(profileExec.getDependsOn().getRef().getUuid(),
					profileExec.getDependsOn().getRef().getVersion(), MetaType.profile.toString());
//			Datasource datasource = commonServiceImpl.getDatasourceByObject(profile);
			for (int i = 0; i < profile.getAttributeInfo().size(); i++) {
//				profileOperator = profileOperatorFactory.getOperator(runMode, datasource);
				String sql = profileOperator.generateSql(profile, profileExec,
						profile.getAttributeInfo().get(i).getAttrId(), datapodList, dagExec, otherParams, runMode);
				if(sql != null)
					sbProfileSelect.append(sql).append(" UNION ALL ");
			}
			String unionSql = sbProfileSelect.substring(0, sbProfileSelect.length() - 10);
//			if(profile.getFilterInfo() != null && !profile.getFilterInfo().isEmpty()) {
//				MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.profile, profile.getUuid(), profile.getVersion()));
//				Datasource mapSourceDS = commonServiceImpl.getDatasourceByObject(profile);
//				String filter = filterOperator2.generateSql(profile.getFilterInfo(), refKeyMap, filterSource, otherParams, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);
//				Datapod datapod = commonServiceImpl.getDatapodByObject(profile);
//				unionSql = "SELECT * FROM ("+unionSql+") ".concat(datapod.getName()).concat(" WHERE 1=1").concat(filter);
//			}
			profileExec.setExec(unionSql);
			synchronized (profileExec.getUuid()) {
//				ProfileExec profileExec1 = (ProfileExec) daoRegister.getRefObject(new MetaIdentifier(MetaType.profileExec, profileExec.getUuid(), profileExec.getVersion()));
				ProfileExec profileExec1 = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(profileExec.getUuid(), profileExec.getVersion(), MetaType.profileExec.toString(), "N");
				profileExec1.setExec(profileExec.getExec());
				iProfileExecDao.save(profileExec1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			synchronized (profileExec.getUuid()) {
				commonServiceImpl.setMetaStatus(profileExec, MetaType.profileExec, Status.Stage.FAILED);
			}
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.profileExec, profileExec.getUuid(), profileExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Profile.", dependsOn);
			throw new Exception((message != null) ? message : "Can not parse Profile.");
		}	
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(profileExec, MetaType.profileExec, Status.Stage.READY);
		}
		return profileExec;
	}
	
	/**
	 * 
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NullPointerException
	 * @throws ParseException
	 * @throws IOException 
	 */
	protected MetaIdentifier getTargetResultDp () throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		return getSummaryOrDetail("framework.profile.datapod.uuid");
	}

	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor,
			BaseRuleExec baseRuleExec, MetaIdentifier datapodKey,
			List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		try {
			return super.execute(MetaType.profile, MetaType.profileExec, metaExecutor, baseRuleExec,
								getTargetResultDp(), taskList, execParams, runMode);			
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.profileExec, baseRuleExec.getUuid(), baseRuleExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Profile execution FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "Profile execution FAILED.");
		}
	}

	public HttpServletResponse download(String profileExecUuid, String profileExecVersion, String format,
			String download, int offset, int limit, HttpServletResponse response, int rowLimit, String sortBy,
			String order, String requestId, RunMode runMode, Layout layout) throws Exception {

		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.download.maxrows"));
		if (rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of " + maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					"Requested rows exceeded the limit of " + maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
		}

		ProfileExec profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(profileExecUuid, profileExecVersion, MetaType.profileExec.toString(), "N");
		
		List<Map<String, Object>> results = getResults(profileExecUuid, profileExecVersion, offset, limit,
				sortBy, order, requestId, runMode);
		response = downloadServiceImpl.download(format, response, runMode, results, new MetaIdentifierHolder(
				new MetaIdentifier(MetaType.profileExec, profileExecUuid, profileExecVersion)), layout,
				null, false, "framework.file.download.path", null, profileExec.getDependsOn(), null);
		return response;
	}

	public List<Map<String, Object>> getProfileResults(String datapodUuid, String datapodVersion, String attributeId,
			String profileAttrType, int numDays, String startDate, String endDate)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {
//		List<Map<String, Object>> data = null;
//		List<Map<String, Object>> dataList = new ArrayList<>(); 
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodUuid, datapodVersion,
				MetaType.datapod.toString());
		String attributeName = datapod.getAttributeName(Integer.parseInt(attributeId));

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy z");

		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("dependsOn");
		query.fields().include("createdOn");
		query.fields().include("appInfo");
		query.fields().include(attributeName);

		try {
			if ((datapodUuid != null && !StringUtils.isEmpty(datapodUuid)))
				query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(datapodUuid));
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Profile> profileObjectList = (List<Profile>) mongoTemplate.find(query, Profile.class);

		Query query2 = new Query();
		query2.fields().include("uuid");
		query2.fields().include("version");
		query2.fields().include("name");
		query2.fields().include("type");
		query2.fields().include("exec");
		query2.fields().include("dependsOn");
		query2.fields().include("createdOn");
		query2.fields().include("result");
		query2.fields().include("statusList");
		query2.fields().include("appInfo");

//		List<ProfileExec> profileExecObjListNew = new ArrayList<>();

		for (Profile profile : profileObjectList) {
			try {
				try {
				String utcTimeZoneId = commonServiceImpl.getConfigValue("framework.utc.timezone.id");
				Calendar cal = Calendar
						.getInstance(TimeZone.getTimeZone(utcTimeZoneId != null && !StringUtils.isBlank(utcTimeZoneId) ? utcTimeZoneId : "1230"));
				if (startDate == null && StringUtils.isEmpty(startDate)) {
					cal.add(Calendar.DATE, 0);
					startDate = simpleDateFormat.format(cal.getTime());
				}

				if ((endDate == null && StringUtils.isEmpty(endDate))) {
					cal.add(Calendar.DATE, -numDays);
					endDate = simpleDateFormat.format(cal.getTime());
				}
				// logger.info("start date: " + startDate + "\tend date: " + endDate);
				if ((startDate != null	&& !StringUtils.isEmpty(startDate))
						&& (endDate != null	&& !StringUtils.isEmpty(endDate)))
						query2.addCriteria(Criteria.where("createdOn").gt(simpleDateFormat.parse(endDate))
								.lte(simpleDateFormat.parse(startDate)));
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				query2.addCriteria(Criteria.where("statusList.stage").in(Status.Stage.COMPLETED.toString()));
				query2.addCriteria(Criteria.where("dependsOn.ref.uuid").is(profile.getUuid()));
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Datasource datasource = commonServiceImpl.getDatasourceByDatapod(datapod);
			ExecContext execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			IExecutor exec = execFactory.getExecutor(execContext.toString());
			String appUuid = commonServiceImpl.getApp().getUuid();
			
			StringBuilder unionBuilder = new StringBuilder();
			List<ProfileExec> profileExecObjList = new ArrayList<>();
			profileExecObjList = (List<ProfileExec>) mongoTemplate.find(query2, ProfileExec.class);
			for (ProfileExec profileExec : profileExecObjList) {

//				profileExecObjListNew.add(profileExec);
//				DataStore profileExecDatastore = null;
//				MetaIdentifierHolder resultHolder = profileExec.getResult();
//				String runMode = "";
//				try {
////					profileExecDatastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
////							resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion(),
////							resultHolder.getRef().getType().toString());
////					runMode = profileExecDatastore.getRunMode();
//				} catch (Exception e) {
//					// TODO: handle exception
//					continue;
//				}
				
				
				
//				Datasource datasource = commonServiceImpl.getDatasourceByApp();
				
				// String sql = null;
				
//				if (runMode.equals(RunMode.ONLINE)) {
//					execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark")
//							|| engine.getExecEngine().equalsIgnoreCase("livy_spark"))
//									? helper.getExecutorContext(engine.getExecEngine())
//									: helper.getExecutorContext(ExecContext.spark.toString());
////					appUuid = commonServiceImpl.getApp().getUuid();
//				} else {
//					execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
//				}				
				
				try {
					String tableName = "dp_result_summary";
					
					if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
						MetaIdentifier dataStoreMI = profileExec.getResult().getRef();
						DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(dataStoreMI.getUuid(), dataStoreMI.getVersion(), dataStoreMI.getType().toString(), "N");
						tableName = dataStoreServiceImpl.getTableNameByDatastore(dataStore, Helper.getExecutionMode(dataStore.getRunMode()));
					}
					
//					String profileAttrType_new = null;
//					switch(profileAttrType) {
//					case "minval" : profileAttrType_new = "min_val";
//					case "maxval" : profileAttrType_new = "max_val";
//					case "avgval" : profileAttrType_new = "avg_val";
//					case "medianVal" : profileAttrType_new = "median_val";
//					case "stdDev" : profileAttrType_new = "std_dev";
//					case "numDistinct" : profileAttrType_new = "num_distinct";
//					case "perDistinct" : profileAttrType_new = "perc_distinct";
//					case "numNull" : profileAttrType_new = "num_null";
//					case "perNull" : profileAttrType_new = "perc_null";
//					default : profileAttrType_new = "min_val";
//					}
					
					String sql = "SELECT " + profileAttrType + " as profile_attribute, rule_exec_time as profile_exec_time FROM " + tableName + 
								" WHERE datapod_uuid = '" + datapodUuid + "' AND attribute_id = '" + attributeId + "'" +
								" AND version = " + profileExec.getVersion();
					
					unionBuilder.append(sql).append(" ").append(" UNION ALL ");
					
//					data = exec.executeAndFetchByDatasource(sql, datasource, appUuid);
//					data = exec.executeAndFetch(profileExec.getExec(), appUuid);
//					for(Map<String, Object> object : data ) {
//						if(object.containsKey("attribute_id")) {
//							Object value = object.get("attribute_id");
//							if(value.toString().equalsIgnoreCase(attributeId)) {
//								object.put("createdOn", profileExec.getCreatedOn());
//								dataList.add(object);								
//								break;
//							}
//						}
//					}
				} catch(Exception e) {
					// TODO: handle exception
					continue;
				}
			}
			
			String unionQuery = unionBuilder.toString();
			unionQuery = unionQuery.substring(0, unionQuery.lastIndexOf(" UNION ALL "));

			return exec.executeAndFetchByDatasource(unionQuery, datasource, appUuid);
		}
		return new ArrayList<>();
	}
	
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		ThreadPoolTaskExecutor metaExecutor = (execParams != null && execParams.getExecutionContext() != null && execParams.getExecutionContext().containsKey("EXECUTOR")) ? (ThreadPoolTaskExecutor)(execParams.getExecutionContext().get("EXECUTOR")) : null;
		List<FutureTask<TaskHolder>> taskList = (execParams != null && execParams.getExecutionContext() != null && execParams.getExecutionContext().containsKey("TASKLIST")) ? (List<FutureTask<TaskHolder>>)(execParams.getExecutionContext().get("TASKLIST")) : null;
		execute(metaExecutor, (ProfileExec)baseExec, null, taskList, execParams, runMode);
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), null, null, runMode);
	}

}
