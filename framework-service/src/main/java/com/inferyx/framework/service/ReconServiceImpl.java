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



import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.ReconInfo;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.ReconOperator;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ReconServiceImpl extends RuleTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
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
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ConnectionFactory connFactory;
	@Autowired
	MessageServiceImpl messageServiceImpl;
	@Autowired
	Engine engine;
	@Autowired
	private ReconOperator reconOperator;
	@Autowired
	private ReconInfo reconInfo;
	@Autowired
	private ReconExecServiceImpl reconExecServiceImpl;

	@Resource(name = "taskThreadMap")
	ConcurrentHashMap<?, ?> taskThreadMap;

	Map<String, String> requestMap = new HashMap<String, String>();

	static final Logger logger = Logger.getLogger(ProfileServiceImpl.class);

	public Recon resolveName(Recon recon) throws JsonProcessingException {
		String createdByRefUuid = recon.getCreatedBy().getRef().getUuid();
		// User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
		User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
		recon.getCreatedBy().getRef().setName(user.getName());
		return recon;
	}

	@Override
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			List<String> datapodList, DagExec dagExec, RunMode runMode) throws Exception {

		logger.info("Inside dataQualServiceImpl.parse");
		Recon recon = null;
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		ReconExec reconExec = (ReconExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.reconExec.toString());
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(reconExec, MetaType.reconExec, Status.Stage.STARTING);
		}
		recon = (Recon) commonServiceImpl.getOneByUuidAndVersion(reconExec.getDependsOn().getRef().getUuid(), reconExec.getDependsOn().getRef().getVersion(), MetaType.recon.toString());
		try {
			reconExec.setExec(reconOperator.generateSql(recon, reconExec, datapodList, dagExec, refKeyMap, otherParams, usedRefKeySet, runMode));
			reconExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			
			synchronized (reconExec.getUuid()) {
//				ReconExec newReconExec = (ReconExec) daoRegister.getRefObject(
//						new MetaIdentifier(MetaType.reconExec, reconExec.getUuid(), reconExec.getVersion()));
				ReconExec newReconExec = (ReconExec) commonServiceImpl.getOneByUuidAndVersion(reconExec.getUuid(), reconExec.getVersion(), MetaType.reconExec.toString());
				newReconExec.setExec(reconExec.getExec());
				newReconExec.setRefKeyList(reconExec.getRefKeyList());
				commonServiceImpl.save(MetaType.reconExec.toString(), newReconExec);
				newReconExec = null;
			}
		}catch (Exception e) {
			commonServiceImpl.setMetaStatus(reconExec, MetaType.reconExec, Status.Stage.FAILED);
			e.printStackTrace();
			throw new Exception("ReconExec not created.");
		}
		synchronized (execUuid) {
			commonServiceImpl.setMetaStatus(reconExec, MetaType.reconExec, Status.Stage.READY);
		}
		return reconExec;
	}

	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor,
			BaseRuleExec baseRuleExec, MetaIdentifier datapodKey,
			List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		return execute(baseRuleExec.getDependsOn().getRef().getUuid(), baseRuleExec.getDependsOn().getRef().getVersion()
				, metaExecutor, (ReconExec) baseRuleExec, null, taskList, execParams, runMode);
	}
	
	public ReconExec create(String reconUuid, String reconVersion, Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception{
		return (ReconExec) super.create(reconUuid, reconVersion, MetaType.recon, MetaType.reconExec, null, refKeyMap, datapodList, dagExec);
	}

	
	public ReconExec create(String reconUuid, String reconVersion, ReconExec reconExec,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		return (ReconExec) super.create(reconUuid, reconVersion, MetaType.recon, MetaType.reconExec, reconExec, refKeyMap, datapodList, dagExec);
	}
	
	public ReconExec execute(String reconUuid, String reconVersion,
			ThreadPoolTaskExecutor metaExecutor, ReconExec reconExec, ReconGroupExec reconGroupExec, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Inside reconServiceImpl.execute");
		try {
//			Datapod targetDatapod = (Datapod) daoRegister.getRefObject(new MetaIdentifier(MetaType.datapod, reconInfo.getReconTargetUUID(), null));
			Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(reconInfo.getReconTargetUUID(), null, MetaType.datapod.toString());
			
			MetaIdentifier targetDatapodKey = new MetaIdentifier(MetaType.datapod, targetDatapod.getUuid(),
					targetDatapod.getVersion());
			reconExec = (ReconExec) super.execute(MetaType.recon, MetaType.reconExec, metaExecutor, reconExec, targetDatapodKey, taskList, execParams, runMode);
		} catch (Exception e) {
			synchronized (reconExec.getUuid()) {
				commonServiceImpl.setMetaStatus(reconExec, MetaType.reconExec, Status.Stage.FAILED);
			}
		}
		return reconExec;
	}
	
	public ReconExec execute(String reconUuid, String reconVersion, ReconExec reconExec,
			ReconGroupExec reconGroupExec, ExecParams  execParams, RunMode runMode) throws Exception {
		execute(reconUuid, reconVersion, null, reconExec, reconGroupExec, null, execParams, runMode);
		return reconExec;
	}
	
	public String getTableName(Datapod datapod, RunMode runMode) throws Exception {
		return dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
	}

	public Object getMetaIdByExecId(String execUuid, String execVersion) throws JsonProcessingException {
		ReconExec reconExec = (ReconExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.reconExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.recon);
		mi.setUuid(reconExec.getDependsOn().getRef().getUuid());
		mi.setVersion(reconExec.getDependsOn().getRef().getVersion());
		return mi;
	}

	public HttpServletResponse download(String reconExecUuid, String reconExecVersion, String format, String download,
			int offset, int limit, HttpServletResponse response, int rowLimit, String sortBy, String order,
			String requestId, RunMode runMode) throws Exception {

		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if (rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of " + maxRows);

			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reconExec, reconExecUuid, reconExecVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					"Requested rows exceeded the limit of " + maxRows, dependsOn);
			throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
		}

		List<Map<String, Object>> results = getReconResults(reconExecUuid, reconExecVersion, offset, limit, sortBy,
				order, requestId, runMode);
		response = commonServiceImpl.download(format, response, runMode, results,
				new MetaIdentifierHolder(new MetaIdentifier(MetaType.reconExec, reconExecUuid, reconExecVersion)));
		return response;
	}
	
	public List<Map<String, Object>> getReconResults(String reconExecUUID, String reconExecVersion, int offset, int limit, String sortBy, String order, String requestId, RunMode runMode) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, SQLException, JSONException {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset+limit;
			offset = offset+1;
			ReconExec reconExec = (ReconExec) commonServiceImpl.getOneByUuidAndVersion(reconExecUUID, reconExecVersion,
					MetaType.reconExec.toString());
			DataStore datastore = dataStoreServiceImpl.getDatastore(reconExec.getResult().getRef().getUuid(),
					reconExec.getResult().getRef().getVersion());
			data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order);
			
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reconExec, reconExecUUID, reconExecVersion));
			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Table not found.");
		}
		return data;
	}

	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.reconExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	public void restart(String type, String uuid, String version, ExecParams  execParams, RunMode runMode) throws Exception {
		ReconExec reconExec = (ReconExec) commonServiceImpl.getOneByUuidAndVersion(uuid,version, MetaType.reconExec.toString());
		try {
			HashMap<String, String> otherParams = null;
			if(execParams != null) 
				otherParams = execParams.getOtherParams();
			
			reconExec = (ReconExec) parse(uuid,version, null, otherParams, null, null, runMode);
			execute(reconExec.getDependsOn().getRef().getUuid(),reconExec.getDependsOn().getRef().getVersion(),reconExec,null, execParams, runMode);
		
		} catch (Exception e) {
			synchronized (reconExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(reconExec, MetaType.reconExec, Status.Stage.FAILED);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.reconExec, reconExec.getUuid(), reconExec.getVersion()));
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not restart Recon.", dependsOn);
					throw new Exception((message != null) ? message : "Can not restart Recon.");
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
			dependsOn.setRef(new MetaIdentifier(MetaType.reconExec, reconExec.getUuid(), reconExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not restart Recon.",dependsOn);
			throw new Exception((message != null) ? message : "Can not restart Recon.");
		}
	}

	public String getReconExecByRGExec(String reconGroupExecUuid, String reconGroupExecVersion) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(reconExecServiceImpl.findReconExecByReconGroupExec(reconGroupExecUuid, reconGroupExecVersion));
		return result;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		ThreadPoolTaskExecutor metaExecutor = (execParams != null && execParams.getExecutionContext() != null && execParams.getExecutionContext().containsKey("EXECUTOR")) ? (ThreadPoolTaskExecutor)(execParams.getExecutionContext().get("EXECUTOR")) : null;
		List<FutureTask<TaskHolder>> taskList = (execParams != null && execParams.getExecutionContext() != null && execParams.getExecutionContext().containsKey("TASKLIST")) ? (List<FutureTask<TaskHolder>>)(execParams.getExecutionContext().get("TASKLIST")) : null;
		execute(metaExecutor, (ReconExec)baseExec, null, taskList, execParams, runMode);
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return parse(baseExec.getUuid(), baseExec.getVersion(), DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(), null, null, runMode);
	}

	public List<ReconExec> findReconExecByRecon(String reconExecUuid, String startDate, String endDate, String type,
			String action) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
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

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy z");// Tue Mar 13 04:15:00 2018 UTC

		try {
			if ((startDate != null && !StringUtils.isEmpty(startDate))
					&& (endDate != null && !StringUtils.isEmpty(endDate))) {
				query.addCriteria(Criteria.where("createdOn").gte(simpleDateFormat.parse(startDate))
						.lte(simpleDateFormat.parse(endDate)));
			} else if ((startDate != null && !startDate.isEmpty()) && StringUtils.isEmpty(endDate)) {
				query.addCriteria(Criteria.where("createdOn").gte(simpleDateFormat.parse(startDate)));
			} else if (endDate != null && !endDate.isEmpty())
				query.addCriteria(Criteria.where("createdOn").lte(simpleDateFormat.parse(endDate)));

			query.addCriteria(Criteria.where("statusList.stage").in(Status.Stage.COMPLETED.toString()));
			query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(reconExecUuid));
			query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
			query.addCriteria(Criteria.where("active").is("Y"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<ReconExec> ReconExecObjList = new ArrayList<>();
		ReconExecObjList = (List<ReconExec>) mongoTemplate.find(query, ReconExec.class);

		return ReconExecObjList;
	}

	public List<ReconExec> findReconExecByDatapod(String datapodUuid, String startDate, String endDate, String type)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException {
		List<ReconExec> ReconExecObjList = new ArrayList<>();
		List<ReconExec> ExecObjList = new ArrayList<>();

		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("dependsOn");
		query.fields().include("createdOn");
		query.fields().include("appInfo");

		try {
			if ((datapodUuid != null && !StringUtils.isEmpty(datapodUuid)))
			query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(datapodUuid));
			query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
			query.addCriteria(Criteria.where("active").is("Y"));
			
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Recon> ReconList = new ArrayList<>();
		ReconList = (List<Recon>) mongoTemplate.find(query, Recon.class);

		for (Recon dq : ReconList) {
			ReconExecObjList = findReconExecByRecon(dq.getUuid(), startDate, endDate, null, null);
			
			if (!ReconExecObjList.isEmpty()) {
				ExecObjList.addAll(ReconExecObjList);
			}

		}
		return ExecObjList;
	}
	
	/*@Override
	public Datasource getDatasource(BaseRule baseRule) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		MetaIdentifier datapodRef = ((Recon)baseRule).getSourceAttr().getRef();
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodRef.getUuid(), datapodRef.getVersion(), datapodRef.getType().toString());
		return commonServiceImpl.getDatasourceByDatapod(datapod);
	}*/
}
