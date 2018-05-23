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

import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.ProfileInfo;
import com.inferyx.framework.dao.IProfileDao;
import com.inferyx.framework.dao.IProfileExecDao;
import com.inferyx.framework.dao.IProfileGroupExecDao;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.BaseRuleGroupExec;
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
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.factory.ProfileOperatorFactory;
import com.inferyx.framework.operator.ProfileOperator;
import com.inferyx.framework.register.DatapodRegister;
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
	ProfileOperatorFactory profileOperatorFactory;
	@Autowired
	MetadataUtil daoRegister;
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
	private DatapodRegister datapodRegister;

	@Resource(name = "taskThreadMap")
	ConcurrentHashMap<?, ?> taskThreadMap;

	Map<String, String> requestMap = new HashMap<String, String>();

	static final Logger logger = Logger.getLogger(ProfileServiceImpl.class);

	/*
	 * public Profile findLatest() { return resolveName(iProfileDao.findLatest(new
	 * Sort(Sort.Direction.DESC, "version"))); }
	 */

	public Profile findAllByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		return iProfileDao.findAllByUuid(appUuid, uuid);
	}

	public Profile findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid == null) {
			return iProfileDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iProfileDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}

	public Profile findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;

		if (appUuid != null) {
			return iProfileDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else
			return iProfileDao.findOneByUuidAndVersion(uuid, version);
	}

	public Profile findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iProfileDao.findOneById(appUuid, id);
		} else
			return iProfileDao.findOne(id);

	}

	public List<Profile> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid == null) {
			return iProfileDao.findAll();
		}
		return iProfileDao.findAll(appUuid);
	}

	public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Profile Profile = iProfileDao.findOneById(appUuid, Id);
		String ID = Profile.getId();
		iProfileDao.delete(appUuid, ID);
		Profile.setBaseEntity();
	}

	/*
	 * public Profile resolveName(Profile profile) { if (profile.getCreatedBy() !=
	 * null) { String createdByRefUuid = profile.getCreatedBy().getRef().getUuid();
	 * User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
	 * profile.getCreatedBy().getRef().setName(user.getName()); } if
	 * (profile.getAppInfo() != null) { for (int i = 0; i <
	 * profile.getAppInfo().size(); i++) { String appUuid =
	 * profile.getAppInfo().get(i).getRef().getUuid(); Application application =
	 * applicationServiceImpl.findLatestByUuid(appUuid); String appName =
	 * application.getName(); profile.getAppInfo().get(i).getRef().setName(appName);
	 * } } if (profile.getDependsOn().getRef().getType().equals("datapod")) { String
	 * dependsOnRefUuid = profile.getDependsOn().getRef().getUuid(); Datapod
	 * datapodDO = datapodServiceImpl.findLatestByUuid(dependsOnRefUuid); String
	 * datapodName = datapodDO.getName();
	 * profile.getDependsOn().getRef().setName(datapodName); }
	 * 
	 * for (int i = 0; i < profile.getAttributeInfo().size(); i++) { String
	 * attributeId = profile.getAttributeInfo().get(i).getAttrId(); Datapod
	 * datapodDO =
	 * datapodServiceImpl.findLatestByUuid(profile.getAttributeInfo().get(i).getRef(
	 * ).getUuid()); String datapodName = datapodDO.getName();
	 * profile.getAttributeInfo().get(i).getRef().setName(datapodName);
	 * List<Attribute> attributeList = datapodDO.getAttributes();
	 * profile.getAttributeInfo().get(i).setAttrName(attributeList.get(Integer.
	 * parseInt(attributeId)).getName()); } return profile; }
	 */
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

	public List<Profile> findAllLatest() {
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

	public List<Profile> findAllLatestActive() {
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
	}

	/*
	 * public List<Profile> resolveName(List<Profile> Profile) { List<Profile>
	 * ProfileList = new ArrayList<Profile>(); for (Profile con : Profile) { String
	 * createdByRefUuid = con.getCreatedBy().getRef().getUuid(); User user =
	 * userServiceImpl.findLatestByUuid(createdByRefUuid);
	 * con.getCreatedBy().getRef().setName(user.getName()); ProfileList.add(con); }
	 * return ProfileList; }
	 */
	public List<Profile> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iProfileDao.findAllVersion(appUuid, uuid);
		} else
			return iProfileDao.findAllVersion(uuid);
	}

	public Profile getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid()
				: null;
		if (appUuid != null) {
			return iProfileDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iProfileDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}

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
	}

	public List<BaseEntity> findList(List<? extends BaseEntity> ProfileList) {
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

	public ProfileExec create(String profileUUID, String profileVersion, ProfileExec profileExec,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec)
			throws NumberFormatException, Exception {
		try {
			return (ProfileExec) super.create(profileUUID, profileVersion, MetaType.profile, MetaType.profileExec,
					profileExec, refKeyMap, datapodList, dagExec);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create ProfileExec.");
			throw new NumberFormatException((message != null) ? message : "Can not create ProfileExec.");
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create ProfileExec.");
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
		return (ProfileExec) execute(profileUUID, profileVersion, metaExecutor, profileExec, profileGroupExec, null,
				taskList, execParams, runMode);
	}

	public List<Map<String, Object>> getProfileResults(String profileExecUUID, String profileExecVersion, int offset,
			int limit, String sortBy, String order, String requestId, RunMode runMode) throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset + limit;
			offset = offset + 1;
			DataStore datastore = dataStoreServiceImpl.findDatastoreByExec(profileExecUUID, profileExecVersion);
			
			data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order);
			
			/*dataStoreServiceImpl.setRunMode(runMode);
			String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(),
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
			commonServiceImpl.sendResponse("402", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.");
			throw new Exception((message != null) ? message : "Table not found.");
		}
		return data;
	}

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
			profileExec = (ProfileExec) parse(profileExec.getUuid(), profileExec.getVersion(), null, null, null,
					runMode);
			execute(profileExec.getDependsOn().getRef().getUuid(), profileExec.getDependsOn().getRef().getVersion(),
					profileExec, null, null, execParams, runMode);
		} catch (Exception e) {
			synchronized (profileExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(profileExec, MetaType.profileExec, Status.Stage.Failed);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Profile.");
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
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Profile.");
			throw new Exception((message != null) ? message : "Can not parse Profile.");
		}

	}

	@Override
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
			List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {
		ProfileExec profileExec =null;
		try {
			Profile profile = null;
			ProfileOperator profileOperator = null;
			StringBuilder sbProfileSelect = new StringBuilder();
			profileExec = (ProfileExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
					MetaType.profileExec.toString());
			profile = (Profile) commonServiceImpl.getOneByUuidAndVersion(profileExec.getDependsOn().getRef().getUuid(),
					profileExec.getDependsOn().getRef().getVersion(), MetaType.profile.toString());
			
			for (int i = 0; i < profile.getAttributeInfo().size(); i++) {
				profileOperator = profileOperatorFactory.getOperator(runMode);
				String sql = profileOperator.generateSql(profile, profileExec,
						profile.getAttributeInfo().get(i).getAttrId(), datapodList, dagExec, runMode);
				if(sql != null)
					sbProfileSelect.append(sql).append(" UNION ALL ");
			}

			profileExec.setExec(sbProfileSelect.substring(0, sbProfileSelect.length() - 10).toString());
			synchronized (profileExec.getUuid()) {
				ProfileExec profileExec1 = (ProfileExec) daoRegister.getRefObject(
						new MetaIdentifier(MetaType.profileExec, profileExec.getUuid(), profileExec.getVersion()));
				profileExec1.setExec(profileExec.getExec());
				iProfileExecDao.save(profileExec1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Profile.");
			throw new Exception((message != null) ? message : "Can not parse Profile.");
		}	
		return profileExec;
	}

	@Override
	public BaseRuleExec execute(String uuid, String version, ThreadPoolTaskExecutor metaExecutor,
			BaseRuleExec baseRuleExec, BaseRuleGroupExec baseGroupExec, MetaIdentifier datapodKey,
			List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		try {
			Datapod targetDatapod = (Datapod) daoRegister
					.getRefObject(new MetaIdentifier(MetaType.datapod, profileInfo.getProfileTargetUUID(), null));
			MetaIdentifier targetDatapodKey = new MetaIdentifier(MetaType.datapod, targetDatapod.getUuid(),
					targetDatapod.getVersion());
			return super.execute(uuid, version, MetaType.profile, MetaType.profileExec, metaExecutor, baseRuleExec,
					baseGroupExec, targetDatapodKey, taskList, execParams, runMode);			
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Profile execution failed.");
			throw new Exception((message != null) ? message : "Profile execution failed.");
		}
	}

	public HttpServletResponse download(String uuid, String version, String format, String download, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
			RunMode runMode) throws Exception {

		List<Map<String, Object>> results = getProfileResults(uuid, version, offset, limit, sortBy, order, requestId,
				runMode);
		response = commonServiceImpl.download(uuid, version, format, offset, limit, response, rowLimit, sortBy, order,
				requestId, runMode, results, MetaType.downloadExec,
				new MetaIdentifierHolder(new MetaIdentifier(MetaType.profileExec, uuid, version)));

		/*
		 * try { FileOutputStream fileOut = null;
		 * response.setContentType("application/xml charset=utf-16");
		 * response.setHeader("Content-type", "application/xml"); HSSFWorkbook workbook
		 * = WorkbookUtil.getWorkbook(results);
		 * 
		 * String downloadPath =
		 * Helper.getPropertyValue("framework.file.download.path");
		 * response.addHeader("Content-Disposition", "attachment; filename=" + uuid +
		 * ".xlsx"); ServletOutputStream os = response.getOutputStream();
		 * workbook.write(os);
		 * 
		 * fileOut = new FileOutputStream(downloadPath + "/" + uuid + "_" + version +
		 * ".xlsx"); workbook.write(fileOut); os.write(workbook.getBytes()); os.close();
		 * fileOut.close();
		 * 
		 * } catch (IOException e1) { e1.printStackTrace();
		 * logger.info("exception caught while download file"); }
		 */
		return response;

	}

	@SuppressWarnings("unlikely-arg-type")
	public List<Map<String, Object>> getProfileResults(String datapodUuid, String datapodVersion, String attributeId,
			String profileAttrType, int numDays, String startDate, String endDate)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, java.text.ParseException {
		List<Map<String, Object>> data = new ArrayList<>();
		List<Map<String, Object>> dataList = new ArrayList<>(); 
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

		List<Profile> profileObjectList = new ArrayList<>();
		profileObjectList = (List<Profile>) mongoTemplate.find(query, Profile.class);

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

		List<ProfileExec> profileExecObjListNew = new ArrayList<>();

		for (Profile profile : profileObjectList) {
			try {
				try {
				String utcTimeZoneId = Helper.getPropertyValue("framework.utc.timezone.id");
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

				query2.addCriteria(Criteria.where("statusList.stage").in(Status.Stage.Completed.toString()));
				query2.addCriteria(Criteria.where("dependsOn.ref.uuid").is(profile.getUuid()));
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<ProfileExec> profileExecObjList = new ArrayList<>();
			profileExecObjList = (List<ProfileExec>) mongoTemplate.find(query2, ProfileExec.class);
			for (ProfileExec profileExec : profileExecObjList) {

				profileExecObjListNew.add(profileExec);
				MetaIdentifierHolder resultHolder = profileExec.getResult();
				String runMode = "";
				try {
					DataStore profileExecDatastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
							resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion(),
							resultHolder.getRef().getType().toString());
					runMode = profileExecDatastore.getRunMode();
				} catch (Exception e) {
					// TODO: handle exception
					continue;
				}
				
				Datasource datasource = commonServiceImpl.getDatasourceByApp();
				ExecContext execContext = null;
				IExecutor exec = null;
				// String sql = null;
				String appUuid = null;
				if (runMode.equals(RunMode.ONLINE)) {
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
				
				try {
					data = exec.executeAndFetch(profileExec.getExec(), appUuid);
					for(Map<String, Object> object : data ) {
						if(object.containsKey("AttributeId")) {
							Object value = object.get("AttributeId");
							if(value.toString().equalsIgnoreCase(attributeId)) {
								object.put("createdOn", profileExec.getCreatedOn());
								dataList.add(object);								
								break;
							}
						}
					}
				}catch (Exception e) {
					// TODO: handle exception
					continue;
				}
				
			}
		}
		return dataList;
	}
}
