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
package com.inferyx.framework.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.util.CharArrayMap.EntrySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Activity;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.AppConfig;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Condition;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.Dashboard;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroup;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Dimension;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.DownloadExec;
import com.inferyx.framework.domain.Export;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.Group;
import com.inferyx.framework.domain.Import;
import com.inferyx.framework.domain.Key;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.Log;
import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.Measure;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.Meta;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ModelExec;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamSet;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.Privilege;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroup;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroup;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Role;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.RuleGroup;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.RunStatusHolder;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.domain.UploadExec;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.domain.Vizpod;
import com.inferyx.framework.enums.ParamDataType;
import com.inferyx.framework.executor.ExecContext;

@Component
public class Helper {
	static Logger logger=Logger.getLogger(Helper.class);
	@Autowired
	Engine engine;

	public static String getNextUUID(){
		return UUID.randomUUID().toString();
	}
	
	public static String getVersion(){
		logger.info("Instant.now().getEpochSecond()"+Instant.now().getEpochSecond());
		return (Long.toString(Instant.now().getEpochSecond()));
	}
	
	public static String getCurrentTimeStamp(){
		String timestamp = new java.text.SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z").format(new Date());
		return timestamp;
	}

	public static Date getCurrentDate(){
		Date date = new Date();
		return date;
	}
	
	public static Key getKeyFromPath(String filePath) {
		if (filePath ==  null) {
			logger.error("file path is null");
			return new OrderKey();
		}
		
		String[] list = filePath.split("/");
		
		return new OrderKey(list[list.length-3], list[list.length-2]);
	}
	
	public static String genTableName(String filePath) {
		if (filePath ==  null) {
			logger.error("file path is null");
			return "Path was null";
		}
		
		String[] list = filePath.split("/");
		
		return String.format("%s_%s_%s", list[list.length-3].replaceAll("-", "_"), list[list.length-2], list[list.length-1]);
	}
	
	public static String genTableName(String datapodUUID, String datapodVersion) 
	{
		String tableName = null;
		String datapod = datapodUUID.replace("-", "_");
		tableName = datapod+"_"+datapodVersion;
		logger.info("Hive Table name is :: "+tableName);
		return tableName;
	}
	
	public static OrderKey getFirstKey(Set<OrderKey> keysets, String machingUUID) {
		for(OrderKey key : keysets) {
			if (key.getUUID().equalsIgnoreCase(machingUUID))
				return key;
		}
		
		logger.error("Key not found");
		return new OrderKey();
	}
	
	public static String getFileName(String csvFilePath){
		File f = new File(csvFilePath);
		String fileName = f.getName();
		if(fileName.indexOf(".")>0){
			fileName = fileName.substring(0,fileName.lastIndexOf("."));
		}
		return fileName;
	}	

	public static String getDaoClass(MetaType type) throws NullPointerException {
		if(type == null)
			return null;
			switch(type) {
				case map :
					return "iMapDao";
				case model :
					return "iModelDao";	
				case meta : return "iMetaDao";
				case relation: return "iRelationDao";
				case dashboard: return "iDashboardDao";
				case datapod: return "iDatapodDao";
				case dag: return "iDagDao";
				case filter: return "iFilterDao";
				case expression: return "iExpressionDao";
				case formula: return "iFormulaDao";
				case condition: return "iConditionDao";
				case dagExec: return "iDagExecDao";
				case vizpod: return "iVizpodDao";
				case datastore: return "iDataStoreDao";
				case session: return "iSessionDao";
				case user: return "iUserDao";
				case activity: return "iActivityDao";
				case role: return "iRoleDao";
				case group: return "iGroupDao";
				case privilege: return "iPrivilegeDao";
				case dimension: return "iDimensionDao";
				case measure: return "iMeasureDao";
				case vizExec: return "iVizpodExecDao";
				case dataset: return "iDatasetDao";
				case application: return "iApplicationDao";
				case datasource: return "iDatasourceDao";
				case load: return "iLoadDao";
				case dq: return "iDataQualDao";
				case dqgroup: return "iDataQualGroupDao";
				case dqExec: return "iDataQualExecDao";
				case dqgroupExec: return "iDataQualGroupExecDao";
				case rule: return "iRuleDao";
				case ruleExec: return "iRuleExecDao";		
				case rulegroup: return "iRuleGroupDao";
				case rulegroupExec: return "iRuleGroupExecDao";
				case loadExec: return "iLoadExecDao";
				case function: return "iFunctionDao";
				case profile: return "iProfileDao";
				case profileExec: return "iProfileExecDao";
				case profilegroup: return "iProfileGroupDao";
				case profilegroupExec: return "iProfileGroupExecDao";
				case algorithm: return "iAlgorithmDao";
				case modelExec: return "iModelExecDao";
				case paramlist: return "iParamListDao";
				case paramset: return "iParamSetDao";
				case mapExec: return "iMapExecDao";
				case Import: return "iImportDao";
				case export: return "iExportDao";
				case message: return "iMessageDao";
				case log : return "iLogDao";
				case downloadExec : return "iDownloadDao";
				case uploadExec : return "iUploadDao";
				case predict : return "iPredictDao";
				case predictExec : return "iPredictExecDao";
				case simulate : return "iSimulateDao";
				case simulateExec : return "iSimulateExecDao";
				case train : return "iTrainDao";
				case trainExec : return "iTrainExecDao";
				case recon : return "iReconDao";
				case reconExec : return "iReconExecDao";
				case recongroup : return "iReconGroupDao";
				case recongroupExec : return "iReconGroupExecDao";
				case distribution : return "iDistributionDao";
				case appConfig : return "iAppConfigDao";
				default:
					return null;
			}
		}
	
	public static String getServiceClass(MetaType type) throws NullPointerException {
		if(type == null)
			return null;
		switch(type) {
		case map : return "MapServiceImpl";
		case mapExec : return "MapExecServiceImpl";
		case dq : return "DataQualServiceImpl";
		case dqExec : return "DataQualExecServiceImpl";
		case dqgroup : return "DataQualGroupServiceImpl";
		case dqgroupExec : return "DataQualGroupExecServiceImpl";
		case rule : return "RuleServiceImpl";
		case ruleExec : return "RuleExecServiceImpl";
		case rulegroup : return "RuleGroupServiceImpl";
		case rulegroupExec : return "RuleGroupExecServiceImpl";
		case profile : return "ProfileServiceImpl"; 
		case profileExec : return "ProfileExecServiceImpl";
		case profilegroupExec : return "ProfileGroupExecServiceImpl";
		case profilegroup : return "ProfileGroupServiceImpl";	
		case recon : return "ReconServiceImpl";	
		case reconExec : return "ReconExecServiceImpl";	
		case recongroup : return "ReconGroupServiceImpl";	
		case recongroupExec : return "ReconGroupExecServiceImpl";	
		default:
			return null;
		}
	}
	
	public static Class<?> getDomainClass(MetaType type) throws NullPointerException {
		if(type == null)
			return null;
		switch(type) {
		case map :
			return Map.class;
		case model :
			return Model.class;			
		case meta : return Meta.class;
		case relation: return Relation.class;
		case dashboard: return Dashboard.class;
		case datapod: return Datapod.class;
		case dag: return Dag.class;
		case filter: return Filter.class;
		case expression: return Expression.class;
		case formula: return Formula.class;
		case condition: return Condition.class;
		case dagExec: return DagExec.class;
		case vizpod: return Vizpod.class;
		case datastore: return DataStore.class;
		case session: return Session.class;
		case user: return User.class;
		case activity: return Activity.class;
		case role: return Role.class;
		case group: return Group.class;
		case privilege: return Privilege.class;
		case dimension: return Dimension.class;
		case measure: return Measure.class;
		case vizExec: return VizExec.class;
		case dataset: return DataSet.class; 
		case application: return Application.class;
		case datasource: return Datasource.class;
		case load: return Load.class;
		case dq: return DataQual.class;
		case dqgroup: return DataQualGroup.class;
		case dqExec: return DataQualExec.class;
		case dqgroupExec: return DataQualGroupExec.class;
		case rule: return Rule.class;
		case ruleExec: return RuleExec.class;		
		case rulegroup: return RuleGroup.class;
		case rulegroupExec: return RuleGroupExec.class;
		case loadExec: return LoadExec.class;
		case function: return Function.class;
		case profile: return Profile.class;
		case profileExec: return ProfileExec.class;
		case profilegroup: return ProfileGroup.class;
		case profilegroupExec: return ProfileGroupExec.class;
		case algorithm: return Algorithm.class;
		case modelExec: return ModelExec.class;
		case paramlist: return ParamList.class;
		case paramset: return ParamSet.class;
		case mapExec: return MapExec.class;
		case Import: return Import.class;
		case export: return Export.class;
		case message: return Message.class;
		case log : return Log.class;
		case downloadExec : return DownloadExec.class;
		case uploadExec : return UploadExec.class;
		case predict : return Predict.class;
		case predictExec : return PredictExec.class;
		case simulate : return Simulate.class;
		case simulateExec : return SimulateExec.class;
		case train : return Train.class;
		case trainExec : return TrainExec.class;
		case recon : return Recon.class;
		case reconExec : return ReconExec.class;
		case recongroup : return ReconGroup.class;
		case recongroupExec : return ReconGroupExec.class;
		case distribution : return Distribution.class;
		case appConfig : return AppConfig.class;

		default:
			return null;
		}
	}
	
	public static MetaType getMetaType(String type) throws NullPointerException {
		if(type == null)
			return null;
			switch(type.toLowerCase()){
				case "map": return MetaType.map;
				case "model": return MetaType.model;
				case "relation": return MetaType.relation;
				case "dashboard": return MetaType.dashboard;
				case "datapod": return MetaType.datapod;
				case "dag": return MetaType.dag;
				case "filter": return MetaType.filter;
				case "expression": return MetaType.expression;
				case "formula": return MetaType.formula;
				case "condition": return MetaType.condition;
				case "dagexec": return MetaType.dagExec;
				case "meta": return MetaType.meta;
				case "vizpod": return MetaType.vizpod;
				case "datastore": return MetaType.datastore;
				case "session": return MetaType.session;
				case "user": return MetaType.user;
				case "activity": return MetaType.activity;
				case "role": return MetaType.role;
				case "group": return MetaType.group;
				case "privilege": return MetaType.privilege;
				case "dimension": return MetaType.dimension;
				case "measure": return MetaType.measure;
				case "vizexec": return MetaType.vizExec;
				case "dataset": return MetaType.dataset;
				case "application": return MetaType.application;
				case "datasource": return MetaType.datasource;
				case "load": return MetaType.load;
				case "dq": return MetaType.dq;
				case "dqgroup": return MetaType.dqgroup;
				case "dqexec": return MetaType.dqExec;
				case "dqgroupexec": return MetaType.dqgroupExec;
				case "rule": return MetaType.rule;
				case "ruleexec": return MetaType.ruleExec;		
				case "rulegroup": return MetaType.rulegroup;
				case "rulegroupexec": return MetaType.rulegroupExec;
				case "loadexec": return MetaType.loadExec;
				case "function": return MetaType.function;
				case "profile": return MetaType.profile;
				case "profileexec": return MetaType.profileExec;
				case "profilegroup": return MetaType.profilegroup;
				case "profilegroupexec": return MetaType.profilegroupExec;
				case "algorithm": return MetaType.algorithm;
				case "modelexec": return MetaType.modelExec;
				case "paramlist": return MetaType.paramlist;
				case "paramset": return MetaType.paramset;
				case "mapexec": return MetaType.mapExec;
				case "import": return MetaType.Import;
				case "export": return MetaType.export;
				case "message": return MetaType.message;
				case "log" : return MetaType.log;
				case "downloadexec" : return MetaType.downloadExec;
				case "uploadexec" : return MetaType.uploadExec;
				case "predict" : return MetaType.predict;
				case "predictexec" : return MetaType.predictExec;
				case "simulate" : return MetaType.simulate;
				case "simulateexec" : return MetaType.simulateExec;
				case "train" : return MetaType.train;
				case "trainexec" : return MetaType.trainExec;
				case "recon" : return MetaType.recon;
				case "reconexec" : return MetaType.reconExec;
				case "recongroup" : return MetaType.recongroup;
				case "recongroupexec" : return MetaType.recongroupExec;
				case "distribution" : return MetaType.distribution;
				case "appconfig" : return MetaType.appConfig;

				default : return null;
			}
		}
	
	public static Status getLatestStatus (List<Status> statusList) {
		if (statusList == null || statusList.isEmpty()) {
			logger.info("No status in statusList. Aborting. ");
			return null;
		}
		//Collections.sort(statusList);
		return statusList.get(statusList.size()-1);
	}
	
	public static MetaIdentifier getMetaIdentifier (BaseEntity baseEntity, MetaType type) {
		if (baseEntity == null) {
			return null;
		}
		return new MetaIdentifier(type, baseEntity.getUuid(), baseEntity.getVersion(), baseEntity.getName());
	}
	
	public static String getPropertyValue(String key) throws FileNotFoundException, IOException {
		Properties property = new Properties();
		InputStream stream = Helper.class.getClassLoader().getResourceAsStream("framework.properties");
		property.load(stream);
		//logger.info(property.getProperty(key));
		return property.getProperty(key);
	}
	
	public static Set<Entry<Object, Object>>  getPropertiesList() throws FileNotFoundException, IOException {
		Properties property = new Properties();
		InputStream stream = Helper.class.getClassLoader().getResourceAsStream("framework.properties");
		property.load(stream);
		//logger.info(property.getProperty(key));
		return  property.entrySet();
	}
	
	/*public HttpServletResponse setResponse(String code, String status, String msg) throws JSONException, ParseException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(requestAttributes != null) {
			HttpServletResponse response = requestAttributes.getResponse();
			if(response != null) {
					Message message = new Message(code, status, msg);
					Message savedMessage = messageServiceImpl.save(message);
					
					//PrintWriter out = response.getWriter();
					ObjectMapper mapper = new ObjectMapper();
					String messageJson = mapper.writeValueAsString(savedMessage);
					response.setContentType("application/json");
					//response.setCharacterEncoding("UTF-8");
					response.setStatus(404);
					//out.print(messageJson);
					///out.flush();
					response.getOutputStream().write(messageJson.getBytes());
					//response.getOutputStream().flush();
					response.getOutputStream().close();
					System.out.println("\n\n");
					return response;					
			}else
				logger.info("HttpServletResponse response is \""+null+"\"");
		}else
			logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");	
		return null;
	}*/
	
	public static void updateRunStatus (Status.Stage latestStatus, RunStatusHolder statusHolder) {
		if (statusHolder == null) {
			statusHolder = new RunStatusHolder(Boolean.TRUE, 
												Boolean.FALSE, 
												Boolean.FALSE, 
												Boolean.FALSE, 
												Boolean.FALSE);
		}
		if (latestStatus.equals(Status.Stage.InProgress)) {
			statusHolder.setCompleted(Boolean.FALSE);
		} else if (latestStatus.equals(Status.Stage.Failed)) {
			statusHolder.setFailed(Boolean.TRUE);
		} else if (latestStatus.equals(Status.Stage.Killed)) {
			statusHolder.setKilled(Boolean.TRUE);
		} else if (latestStatus.equals(Status.Stage.Resume)) {
			statusHolder.setResume(Boolean.TRUE);
			statusHolder.setCompleted(Boolean.FALSE);
		} else if (latestStatus.equals(Status.Stage.OnHold)) {
			statusHolder.setOnHold(Boolean.TRUE);
			statusHolder.setCompleted(Boolean.FALSE);
		} else if (latestStatus.equals(Status.Stage.InProgress) 
					|| latestStatus.equals(Status.Stage.NotStarted) 
					|| latestStatus.equals(Status.Stage.Terminating)) {
			statusHolder.setCompleted(Boolean.FALSE);
		}
	}	
	public static Status.Stage getStatus(String stage){
		if(stage !=null && !StringUtils.isBlank(stage)) {
			switch(stage.toLowerCase()) {
				case "onhold": return Status.Stage.OnHold;
				case "killed": return Status.Stage.Killed;
				case "inprogress": return Status.Stage.InProgress;
				case "failed": return Status.Stage.Failed;
				case "completed": return Status.Stage.Completed;
				case "notstarted": return Status.Stage.NotStarted;
				case "active": return Status.Stage.active;
				case "expired": return Status.Stage.expired;
				case "inactive": return Status.Stage.Inactive;
				case "login": return Status.Stage.login;
				case "logout": return Status.Stage.logout;
				case "Offhold": return Status.Stage.OffHold;
				case "resume": return Status.Stage.Resume;
				case "suspend": return Status.Stage.Suspend;
				case "terminating": return Status.Stage.Terminating;
				default : return null;
			}
		}else
			return null;
	}
	public ExecContext getExecutorContext(String executorContext){
		String executionEngine = engine.getExecEngine();
		if(executionEngine != null && !StringUtils.isBlank(executionEngine) && executionEngine.equalsIgnoreCase("livy-spark"))
			executionEngine = "livy_spark";
		if(executorContext != null && !StringUtils.isBlank(executorContext))
			switch(executorContext.toLowerCase()){
				case "spark": return (executionEngine != null && executionEngine == "livy_spark") ? ExecContext.livy_spark : ExecContext.spark;
				case "livy-spark": return ExecContext.livy_spark;
				case "hive": return ExecContext.HIVE;
				case "impala": return ExecContext.IMPALA;
				case "oracle": return ExecContext.ORACLE;
				case "mysql": return ExecContext.MYSQL;
				case "file": return (executionEngine != null && executionEngine == "livy_spark") ? ExecContext.livy_spark : ExecContext.FILE;
				case "r" : return ExecContext.R;
				case "python" : return ExecContext.PYTHON;
				default : return null;
			}
		else
			return null;
	}	
	public static Mode getExecutionMode(String mode) {
		switch(mode.toLowerCase()) {
		case "batch": return Mode.BATCH;
		case "online": return Mode.ONLINE;
		default: return null;
		}
	}
	
	/**
	 * Get partition clause
	 * @param datapod
	 * @return
	 */
	public static String getPartitionColumns(Datapod datapod) {
		StringBuilder partitionColls = new StringBuilder();
		List<Attribute> datapodAttributes = datapod.getAttributes();
		for(Attribute attribute : datapodAttributes) {
			if(attribute.getPartition().equalsIgnoreCase("y")) {
				partitionColls.append(attribute.getName()+",");
			}
		}
		if(partitionColls.lastIndexOf(",")>partitionColls.length()) {
			partitionColls.deleteCharAt(partitionColls.lastIndexOf(","));
		}
		partitionColls.deleteCharAt(partitionColls.length()-1);
		logger.info("Partition collumns: "+partitionColls);
		return partitionColls.toString();
	}
	
	/**
	 * Create and insert query to insert into target datapod provided as parameter datapod
	 * @param executionContext
	 * @param tableName
	 * @param datapod
	 * @param sql
	 * @return
	 */
	public String buildInsertQuery(String executionContext, String tableName, Datapod datapod, String sql) {
		if(executionContext.equalsIgnoreCase(ExecContext.HIVE.toString())
				|| executionContext.equalsIgnoreCase(ExecContext.IMPALA.toString())) {
			sql = "INSERT OVERWRITE table " + tableName + " PARTITION ( " + Helper.getPartitionColumns(datapod) +" ) " + " " + sql;
		} else {	
				sql = "INSERT INTO " + tableName + " " + sql;
		}
		return sql;
	}
	public static String getPersistModeFromRunMode(String runMode){
		if(runMode != null && !StringUtils.isBlank(runMode))
			switch(runMode.toLowerCase()) {
			case "batch" : return ConstantsUtil.PERSIST_MODE_DISK_ONLY;
			case "online": return ConstantsUtil.PERSIST_MODE_MEMORY_ONLY;
		}
		return null;
	}
	public static java.util.Map<String, String> getPropertiesByFileType(FileType fileType) throws FileNotFoundException, IOException {
		java.util.Map<String, String> properties = new HashMap<>();
		String regex = null;
		if(fileType != null)
			switch (fileType) {
				case SCRIPT : regex = "script";
								break;
				case CSV : regex = "csv";
				 				break;
				case LOG : regex = "log";
				 				break;
				case ZIP : regex = "zip";
			 					break;
				case XLS : regex = "xls";
					break;
			}
		
		for (Entry<Object, Object> entry : Helper.getPropertiesList()) {
			if(entry.getKey().toString().contains(regex)) {
				properties.put(entry.getKey().toString(), entry.getValue().toString());
			}
		}
		return properties;
	}
	public static String getFileCustomNameByFileType(FileType fileType, String extension) {
		if(fileType != null)
			switch (fileType) {
				case SCRIPT : return Helper.getNextUUID()+"_"+Helper.getVersion()+"."+extension;
				case CSV : return Helper.getNextUUID()+"_"+Helper.getVersion()+"."+extension;
				case LOG : return Helper.getNextUUID()+"_"+Helper.getVersion()+"."+extension;
				case ZIP : return Helper.getNextUUID()+"_"+Helper.getVersion()+"."+extension;		
				case XLS : return Helper.getNextUUID()+"_"+Helper.getVersion()+"."+extension;		
			}
		return null;
	}
	public static FileType getFileType(String fileType) {
		if(fileType != null)
			switch (fileType.toLowerCase()) {
				case "script" : return FileType.SCRIPT;
				case "csv" : return FileType.CSV;
				case "zip" : return FileType.ZIP;
				case "log" : return FileType.LOG;
				case "xsl" : return FileType.XLS;
			}
		return null;
	}
	public static String getFileDirectoryByFileType(FileType fileType) throws FileNotFoundException, IOException {
		if(fileType != null)
			switch (fileType) {
				case SCRIPT : return getPropertyValue("framework.model.script.path");				
				case CSV : return getPropertyValue("framework.file.upload.path");
				case LOG : return getPropertyValue("framework.model.log.path");
				case ZIP : return getPropertyValue("framework.file.zip.location");		
				case XLS : return getPropertyValue("framework.file.download.path");		
			}
		return null;
	}
	
	public static ParamDataType resolveParamDataType(String paramDataType) {
		if(paramDataType != null && !StringUtils.isBlank(paramDataType))
			switch(paramDataType.toLowerCase()) {
			case "twodarray" : return ParamDataType.TWODARRAY;
			case "onedarray" : return ParamDataType.ONEDARRAY;
			case "double" : return ParamDataType.DOUBLE;
			case "integer" : return ParamDataType.INTEGER;
			case "long" : return ParamDataType.LONG;
			case "string" : return ParamDataType.STRING;
			case "date" : return ParamDataType.DATE;
			}
		return null;
	}
}