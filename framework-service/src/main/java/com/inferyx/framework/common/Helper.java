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
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.spark.sql.SaveMode;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.layers.ActivationLayer;
import org.deeplearning4j.nn.conf.layers.AutoEncoder;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.Convolution1DLayer;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.DropoutLayer;
import org.deeplearning4j.nn.conf.layers.EmbeddingLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.GravesBidirectionalLSTM;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.conf.layers.LocalResponseNormalization;
import org.deeplearning4j.nn.conf.layers.LossLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RBM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.layers.Subsampling1DLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.layers.misc.FrozenLayer;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.*;
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.enums.OperatorType;
import com.inferyx.framework.enums.ParamDataType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.MetadataServiceImpl;

@Component
public class Helper {
	static Logger logger=Logger.getLogger(Helper.class);
	@Autowired
	Engine engine;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;

	public static String getNextUUID(){
		return UUID.randomUUID().toString();
	}
	
	public static String getVersion(){
		logger.info("Instant.now().getEpochSecond()"+Instant.now().getEpochSecond());
		return (Long.toString(Instant.now().getEpochSecond()));
	}
	
	public static String getCurrentTimeStamp(){
		String timestamp = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(new Date());
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

	
	/*public static String genTableName(String datapodUUID, String datapodVersion, String execVersion) {
		String tableName = null;
		String datapod = datapodUUID.replace("-", "_");
		tableName = datapod+"_"+datapodVersion+"_"+execVersion;
		logger.info("Table name is :: "+tableName);
		return tableName;
	}*/
	
	public static String getFileName(String datapodUUID, String datapodVersion, String execVersion) {
		logger.info("File name is :: "+String.format("/%s/%s/%s", datapodUUID, datapodVersion, execVersion));
		return String.format("/%s/%s/%s", datapodUUID, datapodVersion, execVersion);
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
//				case operatortype : return "iOperatorTypeDao";
				case operatorExec : return "iOperatorExecDao";
				case operator : return "iOperatorDao";
				case comment : return "iCommentDao";
				case tag : return "iTagDao";
				case lov : return "iLovDao";
				case graphpod : return "iGraphpodDao";
				case graphExec : return "iGraphpodExecDao";
				case report : return "iReportDao";
				case reportExec : return "iReportExecDao";
				case batch : return "iBatchDao";
				case batchExec : return "iBatchExecDao";
				case schedule : return "iScheduleDao";	
				case ingest : return "iIngestDao";
				case ingestExec : return "iIngestExecDao";
				case ingestgroup : return "iIngestGroupDao";
				case ingestgroupExec : return "iIngestGroupExecDao";
				case trainresult : return "iTrainResultDao";
				case deployExec : return "iDeployExecDao";
				case processExec : return "iProcessExecDao";
				case organization : return "iOrganizationDao";
				case dashboardExec : return "iDashboardExecDao";
				case rule2: return "iRule2Dao";
				case rule2Exec: return "iRule2ExecDao";	
				case domain: return "iAttributeDomainDao";
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
		case load : return "LoadExecServiceImpl";	
		case ingest : return "IngestServiceImpl";
		case ingestExec : return "IngestExecServiceImpl";
		case ingestgroup : return "IngestGroupServiceImpl";
		case train : return "TrainServiceImpl";
		case trainExec : return "TrainExecServiceImpl";
		case predict : return "PredictServiceImpl";
		case predictExec : return "PredictExecServiceImpl";
		case simulateExec : return "ModelExecServiceImpl";
		case trainresult : return "TrainResultServiceImpl";
		case deployExec : return "DeployServiceImpl";
		case processExec : return "ProcessServiceImpl";
		case dashboardExec : return " DashboardServiceImpl";
		case graphExec : return " GraphServiceImpl";
		case rule2 : return "Rule2ServiceImpl";
		case rule2Exec : return "Rule2ExecServiceImpl";
		default: return null;
		}
	}
	
	public static Class<?> getDomainClass(MetaType type) throws NullPointerException {
		if(type == null)
			return null;
		switch(type) {
		case map : return Map.class;
		case model : return Model.class;			
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
//		case operatortype : return Operator.class;
		case operatorExec : return OperatorExec.class;
		case operator : return Operator.class;
		case comment : return Comment.class;
		case tag : return Tag.class;
		case lov : return Lov.class;
		case graphpod : return Graphpod.class;
		case graphExec : return GraphExec.class;
		case report : return Report.class;
		case reportExec : return ReportExec.class;
		case batch : return Batch.class;
		case batchExec : return BatchExec.class;
		case schedule : return Schedule.class;
		case ingest : return Ingest.class;
		case ingestExec : return IngestExec.class;
		case ingestgroup : return IngestGroup.class;
		case ingestgroupExec : return IngestGroupExec.class;
		case trainresult : return TrainResult.class;
		case deployExec : return DeployExec.class;
		case processExec : return ProcessExec.class;
		case organization : return Organization.class;
		case dashboardExec : return DashboardExec.class;
		case rule2 : return Rule2.class;
		case rule2Exec : return RuleExec.class;
		case domain: return AttributeDomain.class;
		default: return null;
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
//				case "operatortype" : return MetaType.operatortype;
				case "operatorexec" : return MetaType.operatorExec;
				case "operator" : return MetaType.operator;
				case "comment" : return MetaType.comment;
				case "tag" : return MetaType.tag;
				case "lov" : return MetaType.lov;
//				case "generatedata" : return MetaType.GenerateData;
//				case "transpose" : return MetaType.Transpose;
//				case "clonedata" : return MetaType.CloneData;
//				case "gendataattr" : return MetaType.GenDataAttr;
//				case "gendatavallist" : return MetaType.GenDataValList;
				case "graphpod" : return MetaType.graphpod;
				case "graphexec" : return MetaType.graphExec;
				case "report" : return MetaType.report;
				case "reportexec" : return MetaType.reportExec;
				case "batch" : return MetaType.batch;
				case "batchexec" : return MetaType.batchExec;
				case "schedule" : return MetaType.schedule;
				case "ingest" : return MetaType.ingest;
				case "ingestexec" : return  MetaType.ingestExec; 
				case "ingestgroup" : return MetaType.ingestgroup;
				case "ingestgroupexec" : return  MetaType.ingestgroupExec; 
				case "trainresult" : return MetaType.trainresult; 
				case "deployexec" : return MetaType.deployExec;
				case "processexec" : return MetaType.processExec;
				case "organization" : return MetaType.organization;
				case "dashboardexec" : return MetaType.dashboardExec;
				case "rule2" : return MetaType.rule2;
				case "rule2exec" : return MetaType.rule2Exec;
				case "domain" : return MetaType.domain;
				default : return null;
			}
		}

	public static OperatorType getOperatorType(String type) throws NullPointerException {
		if(type == null)
			return null;
			switch(type.toLowerCase()){
				case "generatedata" : return OperatorType.generateData;
				case "transpose" : return OperatorType.transpose;
				case "clonedata" : return OperatorType.cloneData;
				case "gendataattr" : return OperatorType.genDataAttr;
				case "gendatavallist" : return OperatorType.genDataValList;
				case "matrix" : return OperatorType.matrix;
				case "histogram" : return OperatorType.HISTOGRAM;
				case "pca"       : return OperatorType.PCA;
				case "datasampling" : return OperatorType.DATASAMPLING;
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
	
	/**
	 * Returns whether status is present in statusList
	 * @param status
	 * @param statusList
	 * @return
	 */
	public static Boolean isStatusPresent (Status status, List<Status> statusList) {
		if (statusList == null || statusList.isEmpty()) {
			return Boolean.FALSE;
		}
		if (statusList.contains(status)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * 
	 * @param stage1
	 * @param stage2
	 * @return
	 */
	public static Status.Stage getPriorStatus (Status.Stage stage1, Status.Stage stage2) {
		if (stage1.ordinal() < stage2.ordinal()) {
			return stage1;
		}
		return stage2;
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
	
	public static short getStylesheetAlignment(String alignment) {
		switch (alignment.toLowerCase()) {
		case "center":	return CellStyle.ALIGN_CENTER;
		case "right":	return CellStyle.ALIGN_RIGHT;
		case "left":	return CellStyle.ALIGN_LEFT;

		default: return CellStyle.ALIGN_CENTER;
		}
	}
	
	public static void updateRunStatus (Status.Stage latestStatus, RunStatusHolder statusHolder) {
		if (statusHolder == null) {
			statusHolder = new RunStatusHolder(Boolean.TRUE, 
												Boolean.FALSE, 
												Boolean.FALSE, 
												Boolean.FALSE, 
												Boolean.FALSE,
												Boolean.FALSE);
		}
		if (latestStatus.equals(Status.Stage.RUNNING)) {
			statusHolder.setCompleted(Boolean.FALSE);
		} else if (latestStatus.equals(Status.Stage.FAILED)) {
			statusHolder.setFailed(Boolean.TRUE);
		} else if (latestStatus.equals(Status.Stage.ABORTED)) {
			statusHolder.setAborted(Boolean.TRUE);
		} else if (latestStatus.equals(Status.Stage.KILLED)) {
			statusHolder.setKilled(Boolean.TRUE);
		} else if (latestStatus.equals(Status.Stage.RESUME)) {
			statusHolder.setResume(Boolean.TRUE);
			statusHolder.setCompleted(Boolean.FALSE);
		} else if (latestStatus.equals(Status.Stage.PAUSE)) {
			statusHolder.setPause(Boolean.TRUE);
			statusHolder.setCompleted(Boolean.FALSE);
		} else if (latestStatus.equals(Status.Stage.RUNNING) 
					|| latestStatus.equals(Status.Stage.PENDING) 
					|| latestStatus.equals(Status.Stage.TERMINATING) 
					|| latestStatus.equals(Status.Stage.STARTING) 
					|| latestStatus.equals(Status.Stage.READY)) {
			statusHolder.setCompleted(Boolean.FALSE);
		}
	}	
	public static Status.Stage getStatus(String stage){
		if(stage !=null && !StringUtils.isBlank(stage)) {
			switch(stage.toLowerCase()) {
				case "pause": return Status.Stage.PAUSE;
				case "killed": return Status.Stage.KILLED;
				case "running": return Status.Stage.RUNNING;
				case "failed": return Status.Stage.FAILED;
				case "completed": return Status.Stage.COMPLETED;
				case "pending": return Status.Stage.PENDING;
				case "active": return Status.Stage.active;
				case "expired": return Status.Stage.expired;
				case "inactive": return Status.Stage.Inactive;
				case "login": return Status.Stage.login;
				case "logout": return Status.Stage.logout;
				case "offhold": return Status.Stage.OffHold;
				case "resume": return Status.Stage.RESUME;
				case "suspend": return Status.Stage.Suspend;
				case "terminating": return Status.Stage.TERMINATING;
				case "ready": return Status.Stage.READY;
				case "starting": return Status.Stage.STARTING;
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
				case "postgres" : return ExecContext.POSTGRES;
				case "sqoop" : return ExecContext.SQOOP;
				default : return null;
			}
		else
			return null;
	}	
	public static RunMode getExecutionMode(String mode) {
		switch(mode.toLowerCase()) {
		case "batch": return RunMode.BATCH;
		case "online": return RunMode.ONLINE;
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
		if(partitionColls.length() > 0)
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
			String partitionClos = getPartitionColumns(datapod);
			sql = "INSERT OVERWRITE table " + tableName + (partitionClos.length() > 0 ? " PARTITION ( " + partitionClos +" ) " : " ") + " " + sql;
		} else {	
				sql = "INSERT INTO " + tableName + " " + sql;
		}
		return sql;
	}
	public static String getPersistModeFromRunMode(String runMode){
		if(runMode != null && !StringUtils.isBlank(runMode))
			switch(runMode.toLowerCase()) {
			case "batch" : return ConstantsUtil.PERSIST_MODE_DISK_ONLY;
//			case "online": return ConstantsUtil.PERSIST_MODE_MEMORY_ONLY;
			case "online": return ConstantsUtil.PERSIST_MODE_DISK_ONLY;
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
			default:
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
			default:
				break;					
			}
		return null;
	}
	public static String getFileCustomNameByFileType(FileType fileType, String extension,String type) {
		return Helper.getNextUUID()+"_"+Helper.getVersion()+"."+extension;
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
			//	case COMMENT :return getPropertyValue("framework.file.comment.upload.path");	
			default:
				break;
			}
		return null;
	}
	public static String getFileDirectoryByFileType(String fileType,String type) throws FileNotFoundException, IOException {
		if(fileType != null || type!=null )
			if(type!=null&&type.equalsIgnoreCase(MetaType.comment.toString()) )	
			{
				return getPropertyValue("framework.file.comment.upload.path");
			}else if(fileType.equalsIgnoreCase("script")){
				return getPropertyValue("framework.model.script.path");
			}else if(fileType.equalsIgnoreCase("csv")){
				return getPropertyValue("framework.file.upload.path");
			}else if(fileType.equalsIgnoreCase("zip")){
				return getPropertyValue("framework.file.zip.location");
			}else if(fileType.equalsIgnoreCase("log")){
				return getPropertyValue("framework.model.log.path");
			}else if(fileType.equalsIgnoreCase("xsl")){
				return getPropertyValue("framework.file.download.path");
			}
		return null;
	}
	/*public static String getFileDirectoryByFileType1(FileType fileType,String exension) throws FileNotFoundException, IOException {
		if(fileType != null)
			if(fileType.toString().equalsIgnoreCase("MODEL")) {
				if(exension.equalsIgnoreCase("SCRIPT")) {
					return getPropertyValue("framework.model.script.path");	
				}
				if(exension.equalsIgnoreCase("LOG")) {
					return getPropertyValue("framework.model.log.path");	
				}
			}
			else if(fileType.toString().equalsIgnoreCase("CSV")) {
				return getPropertyValue("framework.file.upload.pafileTypeth");
			}
			else if(fileType.toString().equalsIgnoreCase("XLS")) {
				return getPropertyValue("framework.file.upload.path");
			}else {
				return null;

			}
		
		return null;
	}*/
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
				case "attribute" : return ParamDataType.ATTRIBUTE;
				case "attributes" : return ParamDataType.ATTRIBUTES;
				case "datapod" : return ParamDataType.DATAPOD;
				case "distribution" : return ParamDataType.DISTRIBUTION;
				
			}
		return null;
	}
	
	public static java.util.Map mergeMap (java.util.Map sourceMap, java.util.Map destMap) {
		if (sourceMap == null || sourceMap.isEmpty()) {
			if (destMap == null || destMap.isEmpty()) {
				return new HashMap<>();
			}
			return destMap;
		}
		if (destMap == null || destMap.isEmpty()) {
			return sourceMap;
		}
		for (Object key : sourceMap.keySet()) {
			destMap.put(key, sourceMap.get(key));
		}
		return destMap;
	}

	
	/**
	 * 
	 * @param type
	 * @return MetaType
	 */
	public MetaType getExecType (MetaType type) {
		if(type == null)
			return null;
		switch(type) {
		case map : return MetaType.mapExec;
		case load : return MetaType.loadExec;
		case rule : return MetaType.ruleExec;
		case rulegroup : return MetaType.rulegroupExec;
		case dq : return MetaType.dqExec;
		case dqgroup : return MetaType.dqgroupExec;
		case profile : return MetaType.profileExec;
		case profilegroup : return MetaType.profilegroupExec;
		case recon : return MetaType.reconExec;
		case recongroup : return MetaType.recongroupExec;
		case train : return MetaType.trainExec;
		case simulate : return MetaType.simulateExec;
		case predict : return MetaType.predictExec;
		case operator : return MetaType.operatorExec;
		case report : return MetaType.reportExec;
		case batch : return MetaType.batchExec;
		case ingest : return MetaType.ingestExec;
		case ingestgroup : return MetaType.ingestgroupExec;
		case dashboard : return MetaType.dashboardExec;
		case rule2 : return MetaType.ruleExec;
		default : return null;
		}
	}
	
	/**
	 * 
	 * @param type
	 * @return MetaType
	 */
	public static MetaType getMetaTypeByExecType (MetaType type) {
		if(type == null)
			return null;
		switch(type) {
		case mapExec : return MetaType.map;
		case loadExec : return MetaType.load;
		case ruleExec : return MetaType.rule;
		case rulegroupExec : return MetaType.rulegroup;
		case dqExec : return MetaType.dq;
		case dqgroupExec : return MetaType.dqgroup;
		case profileExec : return MetaType.profile;
		case profilegroupExec : return MetaType.profilegroup;
		case reconExec : return MetaType.recon;
		case recongroupExec : return MetaType.recongroup;
		case trainExec : return MetaType.train;
		case simulateExec : return MetaType.simulate;
		case predictExec : return MetaType.predict;
		case operatorExec : return MetaType.operator;
		case reportExec : return MetaType.report;
		case batchExec : return MetaType.batch;
		case ingestExec : return MetaType.ingest;
		case ingestgroupExec : return MetaType.ingestgroup;
		case dashboardExec : return MetaType.dashboard;
		case rule2Exec : return MetaType.rule2;		
		default : return null;
		}
	}
	
	/**
	 * 
	 * @param type
	 * @return MetaType
	 */
	public MetaType getGroupExecTypeByRuleExecType (MetaType type) {
		if(type == null)
			return null;
		switch(type) {
		case rulegroupExec : return MetaType.ruleExec;
		case dqgroupExec : return MetaType.dqExec;
		case profilegroupExec : return MetaType.profileExec;
		case recongroupExec : return MetaType.reconExec;
		default : return null;
		}
	}
	
	/**
	 * 
	 * @param type
	 * @return MetaType
	 */
	public MetaType getRuleExecTypeByGroupExecType (MetaType type) {
		if(type == null)
			return null;
		switch(type) {
		case ruleExec : return MetaType.rulegroupExec;
		case dqExec : return MetaType.dqgroupExec;
		case profileExec : return MetaType.profilegroupExec;
		case reconExec : return MetaType.recongroupExec;
		default : return null;
		}
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public BaseExec createExec(MetaType type) {
		if(type == null)
			return null;
		switch(type) {
		case mapExec : return new MapExec();
		case loadExec : return new LoadExec();
		case ruleExec : return new RuleExec();
		case rulegroupExec : return new RuleGroupExec();
		case dqExec : return new DataQualExec();
		case dqgroupExec : return new DataQualGroupExec();
		case profileExec : return new ProfileExec();
		case profilegroupExec : return new ProfileGroupExec();
		case reconExec : return new ReconExec();
		case recongroupExec : return new ReconGroupExec();
		case trainExec : return new TrainExec();
		case simulateExec : return new SimulateExec();
		case predictExec : return new PredictExec();
		case operatorExec : return new OperatorExec();
		case graphExec : return new GraphExec();
		case ingestExec : return new IngestExec();
		case ingestgroupExec : return new IngestGroupExec();
		case reportExec : return new ReportExec();
		case dashboardExec : return new DashboardExec();
		case rule2Exec : return new RuleExec();
		default : return null;
		}
	}

	public static String genUrlByDatasource(Datasource datasource) {
		switch(datasource.getType().toLowerCase()) {
			case "hive":  return "jdbc:hive2://" + datasource.getHost() + ":" + datasource.getPort() + "/" + datasource.getDbname();
			  			  
			case "impala": return "jdbc:impala://" + datasource.getHost() + ":" + datasource.getPort() + "/" + datasource.getDbname();
			  			   
			case "mysql": return "jdbc:mysql://" + datasource.getHost() + ":" + datasource.getPort() + "/" + datasource.getDbname();
			  			  
			case "oracle": return "jdbc:oracle:thin:@" + datasource.getHost() + ":" + datasource.getPort() + ":" + datasource.getSid();
			
			case "postgres": return "jdbc:postgresql://" + datasource.getHost() + ":" + datasource.getPort() + "/" + datasource.getDbname();
			  			   
			default: return null;
		}		
	}
	

	
	public static boolean isNumber(String str) {
		return NumberUtils.isCreatable(str);
	}
	
	public static boolean isDate(String str) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		try {
			dateFormat.parse(str);
			return true;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
//				e.printStackTrace();
			return false;
		}
	}
	
	public static IngestionType getIngestionType(String ingestionType) {
		if(ingestionType != null) {
			switch(ingestionType.toLowerCase()) {
			case "file-file" : return IngestionType.FILETOFILE;
			case "file-table" : return IngestionType.FILETOTABLE;
			case "table-file" : return IngestionType.TABLETOFILE;
			case "table-table" : return IngestionType.TABLETOTABLE;
			case "stream-table" : return IngestionType.STREAMTOTABLE;
			case "stream-file" : return IngestionType.STREAMTOFILE;
			}
		}
		return null;
	}
	
	public static String getDelimetrByFormat(String format) {
		if(format != null) {
			switch(format.toLowerCase()) {
			case "csv" : return ",";
			case "tsv" : return "\t";
			case "psv" : return "|";
			case "parquet" : return "parquet";
			}
		}
		return null;
	}
	
	public static SaveMode getSparkSaveMode(com.inferyx.framework.enums.SaveMode saveMode) {
		switch(saveMode) {
		case APPEND : return SaveMode.Append;
		case OVERWRITE : return SaveMode.Overwrite;
		default : return null;
		}
	}
	
	public String getPathByDataSource(Datasource datasource) {
		return String.format("%s/%s", hdfsInfo.getHdfsURL(), datasource.getPath());
	}
	
	public static Pattern getRegexByFileInfo(String fileName, String fileExtn, String fileFormat, boolean isCaseSensitive) {
		// Make regex compatible
		fileName = fileName.replace(".","\\.").replace("*",".*");
		
		// Replace tokens
		int occurences = StringUtils.countMatches(fileName,"[");
		for (int i=0 ; i < occurences ; i++) {
			String result = fileName.substring(fileName.indexOf("[") + 1, fileName.indexOf("]"));
			SimpleDateFormat smplDateFormat = new SimpleDateFormat(result);
			String dateFormat = smplDateFormat.format(new Date());
			fileName = fileName.replaceAll("\\["+result+"\\]",dateFormat);
		}
			
		if(fileName.contains("{") && fileName.contains("}")) {
			fileName = fileName.replaceAll("\\{", "[").replaceAll("\\}", "]");
		}
		
		if (fileExtn != null) {
			fileExtn = fileExtn.startsWith(".") ? fileExtn.substring(1) : fileExtn;
			fileName = fileName + (fileName.toLowerCase().endsWith("." + fileExtn.toLowerCase()) ? ""
					: "\\." + fileExtn.toLowerCase());
		} else {
			fileFormat = fileFormat.startsWith(".") ? fileFormat.substring(1) : fileFormat;
			fileName = fileName + (fileName.toLowerCase().endsWith("." + fileFormat.toLowerCase()) ? ""
					: "\\." + fileFormat.toLowerCase());

		}

		
		//Apply Regex
		Pattern regex = null;
		if(isCaseSensitive) {
			regex = Pattern.compile("^"+fileName+"$");
		} else {
			regex = Pattern.compile("^"+fileName+"$", Pattern.CASE_INSENSITIVE);
		}
		return regex;
	}
	
	/********************* Data Science Methods *********************************/
	
	/**
	 * 
	 * @param optimizationAlgoStr
	 * @return
	 */
	public OptimizationAlgorithm getOptimizationAlgorithm(String optimizationAlgoStr) {
		if (StringUtils.isBlank(optimizationAlgoStr)) {
			return null;
		}
		return OptimizationAlgorithm.valueOf(optimizationAlgoStr);
	}
	
	/**
	 * 
	 * @param layerName
	 * @return
	 */
	public Layer.Builder getLayerBuilders(String layerName) {
		switch(layerName) {
		 case "autoEncoder":  return new AutoEncoder.Builder();
		 case "convolution":  return new ConvolutionLayer.Builder();
		 case "convolution1d":  return new Convolution1DLayer.Builder();
		 case "gravesLSTM":  return new GravesLSTM.Builder();
		 case "LSTM":  return new LSTM.Builder();
		 case "gravesBidirectionalLSTM":  return new GravesBidirectionalLSTM.Builder();
		 case "output":  return new OutputLayer.Builder();
		 case "rnnoutput":  return new RnnOutputLayer.Builder();
		 case "loss":  return new LossLayer.Builder();
		 case "RBM":  return new RBM.Builder();
		 case "dense":  return new DenseLayer.Builder();
		 case "subsampling":  return new SubsamplingLayer.Builder();
		 case "subsampling1d":  return new Subsampling1DLayer.Builder();
		 case "batchNormalization":  return new BatchNormalization.Builder();
		 case "localResponseNormalization":  return new LocalResponseNormalization.Builder();
		 case "embedding":  return new EmbeddingLayer.Builder();
		 case "activation":  return new ActivationLayer.Builder();
		 case "VariationalAutoencoder":  return new VariationalAutoencoder.Builder();
		 case "dropout":  return new DropoutLayer.Builder();
		 case "GlobalPooling":  return new GlobalPoolingLayer.Builder();
		 case "FrozenLayer":  return new FrozenLayer.Builder();
		 default : return new DenseLayer.Builder();
		}
	}

	public static String mapMySQLDTypeByDatapodDtype(String dataType) {
		switch(dataType.toLowerCase()) {
			case "varchar" : return "STRING";
			case "char" : return "VARCHAR";
			case "text" : return "STRING";
			case "float" : return "FLOAT";
			case "bigint" : return "BIGINT";
			case "double" : return "DOUBLE";
			case "timestamp" : return "TIMESTAMP";
			case "int" : return "INTEGER";
			case "decimal" : return "DECIMAL";
			default : return "STRING";
		}
	}
	
	public static List<String> getMappedDataTypes(String dataType) {
		List<String> mappedDTypes = new ArrayList<>();
		switch (dataType.toUpperCase()) {
		case "STRING":
			mappedDTypes.add("varchar");
			mappedDTypes.add("char");
			mappedDTypes.add("text");
			return mappedDTypes;
		case "VARCHAR":
			mappedDTypes.add("varchar");
			mappedDTypes.add("char");
			mappedDTypes.add("text");
			return mappedDTypes;
		case "CHAR":
			mappedDTypes.add("varchar");
			mappedDTypes.add("char");
			mappedDTypes.add("text");
		case "TEXT":
			mappedDTypes.add("varchar");
			mappedDTypes.add("char");
			mappedDTypes.add("text");
			return mappedDTypes;
		case "FLOAT":
			mappedDTypes.add("float");
			mappedDTypes.add("double");
			return mappedDTypes;
		case "DOUBLE":
			mappedDTypes.add("double");
			mappedDTypes.add("float");
			return mappedDTypes;
		case "BIGINT":
			mappedDTypes.add("bigint");
			return mappedDTypes;
		case "INTEGER":
			mappedDTypes.add("integer");
			mappedDTypes.add("int");
			return mappedDTypes;
		case "INT":
			mappedDTypes.add("integer");
			mappedDTypes.add("int");
			return mappedDTypes;
		case "LONG":
			mappedDTypes.add("long");
			mappedDTypes.add("int");
			mappedDTypes.add("integer");
//			mappedDTypes.add("bigint");
//			mappedDTypes.add("tinyint");
//			mappedDTypes.add("smallint");
			return mappedDTypes;
		case "TIMESTAMP":
			mappedDTypes.add("timestamp");
			return mappedDTypes;
		case "DECIMAL":
			mappedDTypes.add("decimal");
			return mappedDTypes;
		case "VECTOR":
			mappedDTypes.add("vector");
			mappedDTypes.add("array");
			return mappedDTypes;
		default:
			return mappedDTypes;
		}
	}
	
	public static String getDocumentFilePath(String defaultFilePath, String metaObjUuid, String metaObjVersion,
			String execVersion, String metaObjName, String fileExtension, boolean useDocDir)
			throws FileNotFoundException, IOException {
		
		defaultFilePath = defaultFilePath.endsWith("/") ? defaultFilePath : defaultFilePath.concat("/");

		String metFilePath = null;
		if(useDocDir) {
			metFilePath = String.format("%s/%s/%s/%s/", metaObjUuid, metaObjVersion, execVersion, "doc");
		} else {
			metFilePath = String.format("%s/%s/%s/", metaObjUuid, metaObjVersion, execVersion);
		}

		String fileName = getDocumentFileName(metaObjName, execVersion, fileExtension);

		return defaultFilePath.concat(metFilePath).concat(fileName);
	}
	
	public static String getDocumentFileName(String metaName, String execVersion, String fileExtension) {
		return String.format("%s_%s.%s", metaName, execVersion, fileExtension.toLowerCase());		
	}
	
	public static String mapFileFormat(String format) {
		switch(format.toLowerCase()) {
		case "excel" : return FileType.XLS.toString();
		case "pdf" : return FileType.PDF.toString();
		default : return null;
		}
	}
	
	public static String getSqlTypeName(int type) {
	    switch (type) {
	    case Types.BIT:
	        return "BIT";
	    case Types.TINYINT:
	        return "TINYINT";
	    case Types.SMALLINT:
	        return "SMALLINT";
	    case Types.INTEGER:
	        return "INTEGER";
	    case Types.BIGINT:
	        return "BIGINT";
	    case Types.FLOAT:
	        return "FLOAT";
	    case Types.REAL:
	        return "REAL";
	    case Types.DOUBLE:
	        return "DOUBLE";
	    case Types.NUMERIC:
	        return "NUMERIC";
	    case Types.DECIMAL:
	        return "DECIMAL";
	    case Types.CHAR:
	        return "CHAR";
	    case Types.VARCHAR:
	        return "VARCHAR";
	    case Types.LONGVARCHAR:
	        return "VARCHAR";
	    case Types.DATE:
	        return "DATE";
	    case Types.TIME:
	        return "TIME";
	    case Types.TIMESTAMP:
	        return "TIMESTAMP";
	    case Types.BINARY:
	        return "BINARY";
	    case Types.VARBINARY:
	        return "VARBINARY";
	    case Types.LONGVARBINARY:
	        return "LONGVARBINARY";
	    case Types.NULL:
	        return "NULL";
	    case Types.OTHER:
	        return "OTHER";
	    case Types.JAVA_OBJECT:
	        return "JAVA_OBJECT";
	    case Types.DISTINCT:
	        return "DISTINCT";
	    case Types.STRUCT:
	        return "STRUCT";
	    case Types.ARRAY:
	        return "ARRAY";
	    case Types.BLOB:
	        return "BLOB";
	    case Types.CLOB:
	        return "CLOB";
	    case Types.REF:
	        return "REF";
	    case Types.DATALINK:
	        return "DATALINK";
	    case Types.BOOLEAN:
	        return "BOOLEAN";
	    case Types.ROWID:
	        return "ROWID";
	    case Types.NCHAR:
	        return "NCHAR";
	    case Types.NVARCHAR:
	        return "NVARCHAR";
	    case Types.LONGNVARCHAR:
	        return "LONGNVARCHAR";
	    case Types.NCLOB:
	        return "NCLOB";
	    case Types.SQLXML:
	        return "SQLXML";
	    }

	    return "?";
	}
}
