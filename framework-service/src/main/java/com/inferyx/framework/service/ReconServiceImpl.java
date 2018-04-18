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
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.ReconInfo;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.BaseRuleGroupExec;
import com.inferyx.framework.domain.DagExec;

import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Message;

import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.factory.ProfileOperatorFactory;
import com.inferyx.framework.operator.ReconOperator;
import com.inferyx.framework.register.DatapodRegister;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ReconServiceImpl extends RuleTemplate {

	@Autowired
	GraphRegister<?> registerGraph;
	/*
	 * @Autowired JavaSparkContext javaSparkContext;
	 */

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
	private DatapodRegister datapodRegister;
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
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
			List<String> datapodList, DagExec dagExec, Mode runMode) throws Exception {

		logger.info("Inside dataQualServiceImpl.parse");
		Recon recon = null;
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		ReconExec reconExec = (ReconExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.reconExec.toString());
		recon = (Recon) commonServiceImpl.getOneByUuidAndVersion(reconExec.getDependsOn().getRef().getUuid(), reconExec.getDependsOn().getRef().getVersion(), MetaType.recon.toString());
		try {
			reconExec.setExec(reconOperator.generateSql(recon, reconExec, datapodList, dagExec, refKeyMap, null, usedRefKeySet, runMode));
			reconExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			
			synchronized (reconExec.getUuid()) {
				ReconExec newReconExec = (ReconExec) daoRegister.getRefObject(
						new MetaIdentifier(MetaType.reconExec, reconExec.getUuid(), reconExec.getVersion()));
				newReconExec.setExec(reconExec.getExec());
				newReconExec.setRefKeyList(reconExec.getRefKeyList());
				commonServiceImpl.save(MetaType.reconExec.toString(), newReconExec);
				newReconExec = null;
			}
		}catch (Exception e) {
			commonServiceImpl.setMetaStatus(reconExec, MetaType.reconExec, Status.Stage.Failed);
			e.printStackTrace();
			throw new Exception("ReconExec not created.");
		}
		return reconExec;
	}

	@Override
	public BaseRuleExec execute(String uuid, String version, ThreadPoolTaskExecutor metaExecutor,
			BaseRuleExec baseRuleExec, BaseRuleGroupExec baseGroupExec, MetaIdentifier datapodKey,
			List<FutureTask<TaskHolder>> taskList, Mode runMode) throws Exception {
		return execute(uuid, version, metaExecutor, (ReconExec)baseRuleExec, (ReconGroupExec)baseGroupExec, taskList, runMode);
	}
	
	public ReconExec create(String reconUuid, String reconVersion, Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception{
		return (ReconExec) super.create(reconUuid, reconVersion, MetaType.recon, MetaType.reconExec, null, refKeyMap, datapodList, dagExec);
	}

	
	public ReconExec create(String reconUuid, String reconVersion, ReconExec reconExec,
			Map<String, MetaIdentifier> refKeyMap, List<String> datapodList, DagExec dagExec) throws Exception {
		return (ReconExec) super.create(reconUuid, reconVersion, MetaType.recon, MetaType.reconExec, reconExec, refKeyMap, datapodList, dagExec);
	}
	
	public ReconExec execute(String reconUuid, String reconVersion,
			ThreadPoolTaskExecutor metaExecutor, ReconExec reconExec, ReconGroupExec reconGroupExec, List<FutureTask<TaskHolder>> taskList, Mode runMode) throws Exception {
		logger.info("Inside reconServiceImpl.execute");
		try {
			Datapod targetDatapod = (Datapod) daoRegister
					.getRefObject(new MetaIdentifier(MetaType.datapod, reconInfo.getReconTargetUUID(), null));
			MetaIdentifier targetDatapodKey = new MetaIdentifier(MetaType.datapod, targetDatapod.getUuid(),
					targetDatapod.getVersion());
			reconExec = (ReconExec) super.execute(reconUuid, reconVersion, MetaType.recon, MetaType.reconExec, metaExecutor, reconExec, reconGroupExec, targetDatapodKey, taskList, runMode);
		} catch (Exception e) {
			synchronized (reconExec.getUuid()) {
				commonServiceImpl.setMetaStatus(reconExec, MetaType.reconExec, Status.Stage.Failed);
			}
		}
		return reconExec;
	}
	
	public ReconExec execute(String reconUuid, String reconVersion, ReconExec reconExec,
			ReconGroupExec reconGroupExec, Mode runMode) throws Exception {
		execute(reconUuid, reconVersion, null, reconExec, reconGroupExec, null, runMode);
		return reconExec;
	}
	
	public String getTableName(Datapod datapod, Mode runMode) throws Exception {
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
	public HttpServletResponse download(String reconExecUUID, String reconExecVersion, String format, String download, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
			Mode runMode) throws Exception {
		
		List<Map<String, Object>> results =getReconResults(reconExecUUID,reconExecVersion,offset,limit,sortBy,order,requestId, runMode);
		response = commonServiceImpl.download(reconExecUUID, reconExecVersion, format, offset, limit, response, rowLimit, sortBy, order, requestId, runMode, results,MetaType.downloadExec,new MetaIdentifierHolder(new MetaIdentifier(MetaType.reconExec,reconExecUUID,reconExecVersion)));
	
		return response;

	}
	
	public List<Map<String, Object>> getReconResults(String reconExecUUID, String reconExecVersion, int offset, int limit, String sortBy, String order, String requestId, Mode runMode) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, SQLException, JSONException {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset+limit;
			offset = offset+1;

			DataStore datastore = dataStoreServiceImpl.findDatastoreByExec(reconExecUUID, reconExecVersion);
			
			data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order);
			
			/*String appUuid = null;
			boolean requestIdExistFlag = false;
			StringBuilder orderBy = new StringBuilder();
			DataStore datastore = dataStoreServiceImpl.findDatastoreByExec(reconExecUUID, reconExecVersion);
			dataStoreServiceImpl.setRunMode(runMode);
			String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(), runMode);
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			ExecContext execContext = null;
			IExecutor exec = null;
			String sql= null;
			if (runMode.equals(Mode.ONLINE)) {
				execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark") || engine.getExecEngine().equalsIgnoreCase("livy_spark"))
						? helper.getExecutorContext(engine.getExecEngine()) : helper.getExecutorContext(ExecContext.spark.toString());
				appUuid = commonServiceImpl.getApp().getUuid();
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			exec = execFactory.getExecutor(execContext.toString());
			appUuid = commonServiceImpl.getApp().getUuid();
			if(requestId == null || requestId.equals("null") || requestId.isEmpty())	{
				if(datasource.getType().toLowerCase().toLowerCase().contains(ExecContext.spark.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
					data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over(ORDER BY version DESC) AS rownum, * FROM " + tableName + ") AS tab WHERE rownum >= " +offset+ " AND rownum <= " + limit, appUuid);
				}else {
					if(datasource.getType().toLowerCase().toLowerCase().contains("oracle"))
						if(runMode.equals(Mode.ONLINE))
							data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
						else	
							data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum< " + limit, appUuid);
					else
						data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
				}
			} else {
				List<String> orderList = Arrays.asList(order.split("\\s*,\\s*"));
				List<String> sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));		
				
				for(int i=0; i<sortList.size(); i++) 
						orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));			
				if (requestId != null) {
					String tabName = null;
					for(Map.Entry<String, String> entry : requestMap.entrySet()) {
						String id = entry.getKey();
						if(id.equals(requestId)) {
							requestIdExistFlag = true;
						}					
					}
					if(requestIdExistFlag) {						
						tabName = requestMap.get(requestId);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
							data = exec.executeAndFetch("SELECT * FROM "+tabName+" WHERE rownum >= " + offset + " AND rownum <= " + limit, appUuid);
						} else {
							if(datasource.getType().toLowerCase().toLowerCase().contains("oracle"))
								if(runMode.equals(Mode.ONLINE))
									data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
								else	
									data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum< " + limit, appUuid);
							else{
								data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
							}
						}
					}else { 
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
									|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
								data = exec.executeAndFetch("SELECT * FROM (SELECT Row_Number() Over(ORDER BY 1) AS rownum, * FROM (SELECT * FROM "
										+tableName+" ORDER BY "+ orderBy.toString() +") AS tab) AS tab1", appUuid);
							}else {
								if(datasource.getType().toLowerCase().toLowerCase().contains("oracle"))
									if(runMode.equals(Mode.ONLINE))
										data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
									else	
										data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum< " + limit, appUuid);
								else{
									data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
								}
							}
						tabName = requestId.replace("-", "_");
						requestMap.put(requestId, tabName);
						if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.HIVE.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.IMPALA.toString())) {
							data = exec.executeAndFetch("SELECT * FROM " + tabName + " WHERE rownum >= " + offset + " AND rownum <= " + limit, null);
						}else {
							if(datasource.getType().toLowerCase().toLowerCase().contains("oracle"))
								if(runMode.equals(Mode.ONLINE))
									data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
								else	
									data = exec.executeAndFetch("SELECT * FROM " + tableName + " WHERE rownum< " + limit, appUuid);
							else{
								data = exec.executeAndFetch("SELECT * FROM " + tableName + " LIMIT " + limit, appUuid);
							}
						}
					}
				}
			}*/
		}catch (Exception e) {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if(requestAttributes != null) {
				HttpServletResponse response = requestAttributes.getResponse();
				if(response != null) {
					response.setContentType("application/json");
					Message message = new Message("404", MessageStatus.FAIL.toString(), "Table not found.");
					Message savedMessage = messageServiceImpl.save(message);
					ObjectMapper mapper = new ObjectMapper();
					String messageJson = mapper.writeValueAsString(savedMessage);
					response.setContentType("application/json");
					response.setStatus(404);
					response.getOutputStream().write(messageJson.getBytes());
					response.getOutputStream().close();
				}else
					logger.info("HttpServletResponse response is \""+null+"\"");
			}else
				logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");	
		}
		return data;
	}

	public void restart(String type, String uuid, String version, Mode runMode) throws JsonProcessingException {
		ReconExec reconExec = (ReconExec) commonServiceImpl.getOneByUuidAndVersion(uuid,version, MetaType.reconExec.toString());
		try {
			reconExec = (ReconExec) parse(uuid,version, null, null, null, runMode);
			execute(reconExec.getDependsOn().getRef().getUuid(),reconExec.getDependsOn().getRef().getVersion(),reconExec,null, runMode);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public String getReconExecByRGExec(String reconGroupExecUuid, String reconGroupExecVersion) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(reconExecServiceImpl.findReconExecByReconGroupExec(reconGroupExecUuid, reconGroupExecVersion));
		return result;
	}

}
