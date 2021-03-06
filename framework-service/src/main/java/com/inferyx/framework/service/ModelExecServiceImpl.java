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

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.spark.ml.param.ParamMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.FeatureRefHolder;
import com.inferyx.framework.domain.FeatureAttrMap;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;

@Service
public class ModelExecServiceImpl extends BaseRuleExecTemplate {
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ModelServiceImpl modelServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	Helper helper;
	@Autowired
	MetadataServiceImpl metadataServiceImpl;
	@Autowired
	private DownloadServiceImpl downloadServiceImpl;

	static final Logger logger = Logger.getLogger(ModelExecServiceImpl.class);


	public String[] getAttributeNames(Model model) throws JsonProcessingException {
		model = modelServiceImpl.resolveName(model);
		List<Feature> listAttrSource = model.getFeatures();
		List<String> listStr = new ArrayList<String>();
		for (Feature attrSource : listAttrSource) {
			listStr.add(attrSource.getName());
		}
		return listStr.toArray(new String[listStr.size()]);
	}

	public String[] getAttributeNames(Predict predict) throws JsonProcessingException {
		//predict = modelServiceImpl.resolveName(predict);
		List<FeatureAttrMap> featureAttrMapList = predict.getFeatureAttrMap();
		List<String> listStr = new ArrayList<String>();
		for (FeatureAttrMap featureAttrMap : featureAttrMapList) {
			listStr.add(featureAttrMap.getAttribute().getAttrName());
		}
		return listStr.toArray(new String[listStr.size()]);
	}

	public String[] getAttributeNames(Simulate simulate) throws JsonProcessingException {
		simulate = modelServiceImpl.resolveName(simulate);
		List<FeatureRefHolder> featureMapList = simulate.getFeatureInfo();
		List<String> listStr = new ArrayList<String>();
		for (FeatureRefHolder featureRefHolder : featureMapList) {
			listStr.add(featureRefHolder.getFeatureName());
		}
		return listStr.toArray(new String[listStr.size()]);
	}

	public String[] getMappedFeatureNames(Train train) throws JsonProcessingException {
		train = modelServiceImpl.resolveName(train);
		List<FeatureAttrMap> listAttrSource = train.getFeatureAttrMap();
		List<String> listStr = new ArrayList<String>();
		for (FeatureAttrMap attrSource : listAttrSource) {
			listStr.add(attrSource.getFeature().getFeatureName());
		}
		return listStr.toArray(new String[listStr.size()]);
	}

	

	public String getModelScript(String modelUuid, String modelVersion) throws Exception {

		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUuid, modelVersion,
				MetaType.model.toString());
		String fileName = model.getScriptName();
		String directoryLocation = helper.getFileDirectoryByFileType(FileType.SCRIPT);
		String filePath = directoryLocation + "/" + fileName;
		File file = new File(filePath);
		String fileContent = null;

		if (file.exists()) {
			logger.info("File " + file + " found.");
			byte[] encoded = Files.readAllBytes(Paths.get(filePath));
			fileContent = new String(encoded, Charset.defaultCharset());
		} else {
			logger.info("File " + file + " not found.");
			throw new FileNotFoundException();
		}
		return fileContent;
	}

	public String exportAsPMML(String execUuid, String execVersion) throws Exception {
		/* String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid(); */
		// ModelExec modelExec = iModelExecDao.findOneByUuidAndVersion(appUuid,
		// execUuid, execVersion);
		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
				MetaType.trainExec.toString());
		// DataStore datastore =
		// dataStoreServiceImpl.findOneByUuidAndVersion(modelExec.getResult().getRef().getUuid(),
		// modelExec.getResult().getRef().getVersion());
		DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
				trainExec.getResult().getRef().getUuid(), trainExec.getResult().getRef().getVersion(),
				MetaType.datastore.toString());
		/*
		 * org.apache.spark.ml.clustering.KMeans kmeansModel =
		 * org.apache.spark.ml.clustering.KMeans .load(datastore.getLocation());
		 */

		// Model model =
		// iModelDao.findOneByUuidAndVersion(modelExec.getDependsOn().getRef().getUuid(),
		// modelExec.getDependsOn().getRef().getVersion());
		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainExec.getDependsOn().getRef().getUuid(),
				trainExec.getDependsOn().getRef().getVersion(), MetaType.train.toString());
		//
		// datapodServiceImpl.findOneByUuidAndVersion(model.getSource().getRef().getUuid(),
		// model.getSource().getRef().getVersion());
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
				train.getSource().getRef().getVersion(), MetaType.datapod.toString());
		if (null == datapod) {
			// datapod =
			// datapodServiceImpl.findLatestByUuid(model.getSource().getRef().getUuid());
			datapod = (Datapod) commonServiceImpl.getLatestByUuid(train.getSource().getRef().getUuid(),
					MetaType.datapod.toString());
		}
		/*
		 * Dataset<Row> df =
		 * dataFrameService.getDataFrameByDataStore(modelExec.getResult().getRef().
		 * getUuid(), modelExec.getResult().getRef().getVersion(), datapod);
		 */

		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		String result = exec.fetchAndCreatePMML(datastore, datapod,
				securityServiceImpl.getAppInfo().getRef().getUuid());
		/*
		 * RFormula formula = new RFormula().setFormula("Species ~ .");
		 * DecisionTreeClassifier classifier = new
		 * DecisionTreeClassifier().setLabelCol(formula.getLabelCol())
		 * .setFeaturesCol(formula.getFeaturesCol()); Pipeline pipeline = new
		 * Pipeline().setStages(new PipelineStage[] { formula, classifier });
		 * PipelineModel pipelineModel = pipeline.fit(df);
		 * 
		 * PMML pmml = ConverterUtil.toPMML(df.schema(), pipelineModel);
		 * JAXBUtil.marshalPMML(pmml, new StreamResult(System.out));
		 */
		return result;

	}
	
	public Algorithm getAlgorithmByTrainExec(String trainExecUUID, String trainExecVersion)
			throws JsonProcessingException {

		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUUID, trainExecVersion,
				MetaType.trainExec.toString());

		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainExec.getDependsOn().getRef().getUuid(),
				trainExec.getDependsOn().getRef().getVersion(), MetaType.train.toString());
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(),
				train.getDependsOn().getRef().getVersion(), MetaType.model.toString());

		return (Algorithm) commonServiceImpl.getOneByUuidAndVersion(model.getDependsOn().getRef().getUuid(),
				model.getDependsOn().getRef().getVersion(), MetaType.algorithm.toString());
	}

	/*************************Unused************************************/
	/*public TrainExec getLatestTrainExecByModel(String modelUuid, String modelVersion) throws Exception {

		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("dependsOn");
		query.fields().include("createdOn");
		query.fields().include("active");
		query.fields().include("appInfo");
		query.fields().include("createdBy");

		try {
			if (modelUuid != null && !StringUtils.isBlank(modelUuid))
				query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(modelUuid));
			query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
			query.addCriteria(Criteria.where("active").is("Y"));
			query.with(new Sort(Sort.Direction.DESC, "version"));
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
			e.printStackTrace();
		}

		List<Train> trainList = mongoTemplate.find(query, Train.class);

		if (trainList.size() > 0) {
			Query query2 = new Query();
			query2.fields().include("uuid");
			query2.fields().include("version");
			query2.fields().include("name");
			query2.fields().include("statusList");
			query2.fields().include("dependsOn");
			query2.fields().include("result");
			query2.fields().include("createdOn");
			query2.fields().include("active");
			query2.fields().include("appInfo");
			query2.fields().include("createdBy");

			try {
				query2.addCriteria(Criteria.where("dependsOn.ref.uuid").is(trainList.get(0).getUuid()));
				query2.addCriteria(Criteria.where("statusList.stage").is("COMPLETED"));
				query2.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
				query2.addCriteria(Criteria.where("active").is("Y"));
				query2.with(new Sort(Sort.Direction.DESC, "version"));
			} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException | NullPointerException
					| ParseException e) {
				e.printStackTrace();
			}

			List<TrainExec> trainExecList = mongoTemplate.find(query2, TrainExec.class);
			if (trainExecList.size() > 0) {
				return trainExecList.get(0);
			} else {
				throw new Exception("No executed train collection available.");
			}
		} else {
			throw new Exception("No train collection available.");
		}
	}*/

	public List<Map<String, Object>> getPredictResults(String execUuid, String execVersion, int rowLimit) throws Exception {
		try {
			PredictExec predictExec = (PredictExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
					MetaType.predictExec.toString());
			Predict predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(
					predictExec.getDependsOn().getRef().getUuid(), predictExec.getDependsOn().getRef().getVersion(),
					MetaType.predict.toString());
			Datapod targetDp = null;
			if(predict.getTarget().getRef().getType().equals(MetaType.datapod))
				targetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(predict.getTarget().getRef().getUuid(),
					predict.getTarget().getRef().getVersion(), MetaType.datapod.toString());
			
			DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
					predictExec.getResult().getRef().getUuid(), predictExec.getResult().getRef().getVersion(),
					MetaType.datastore.toString());

			ExecContext execContext = commonServiceImpl.getExecContext();
			IExecutor exec = execFactory.getExecutor(execContext.toString());
			
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
//			IExecutor exec = execFactory.getExecutor(datasource.getType());
			
			String targetTable = null;
			Datasource targetDS = null;
			if(targetDp != null) {
				targetTable = datasource.getDbname()+"."+targetDp.getName();
				targetDS = commonServiceImpl.getDatasourceByObject(targetDp);
			} else {
				targetDS = datasource;
			}
			
			String appUuid = commonServiceImpl.getApp().getUuid();
			List<Map<String, Object>> strList = null;
			if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())
					&& !targetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())
					&& targetDp != null) {
				String tableName = null;
				if(targetDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
					tableName = targetDS.getSid().concat(".").concat(targetDp.getName());
				} else {
					tableName = targetDS.getDbname().concat(".").concat(targetDp.getName());					
				}
				String sql = "SELECT * FROM "+tableName;
				strList = exec.executeAndFetchByDatasource(sql, targetDS, appUuid);
			} else {
				strList = exec.fetchResults(datastore, targetDp, rowLimit, targetTable, appUuid);
			}
				
			return strList;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.predictExec, execUuid, execVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "No data found.", dependsOn);
			throw new RuntimeException((message != null) ? message : "No data found.");
		}
	}

	public List<Map<String, Object>> getSimulateResults(String execUuid, String execVersion, int rowLimit) throws Exception {
		try {
			SimulateExec simulateExec = (SimulateExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
					MetaType.simulateExec.toString());
	
			DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
					simulateExec.getResult().getRef().getUuid(), simulateExec.getResult().getRef().getVersion(),
					MetaType.datastore.toString());
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			Simulate simulate = (Simulate) commonServiceImpl.getOneByUuidAndVersion(simulateExec.getDependsOn().getRef().getUuid(), simulateExec.getDependsOn().getRef().getVersion(), simulateExec.getDependsOn().getRef().getType().toString());
			String targetTable = null;
			Datapod targetDp = null;
			if(simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
				targetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(simulate.getTarget().getRef().getUuid(), simulate.getTarget().getRef().getVersion(), simulate.getTarget().getRef().getType().toString());
				targetTable = datasource.getDbname()+"."+targetDp.getName();
			}
			List<Map<String, Object>> strList = exec.fetchResults(datastore, targetDp, rowLimit, targetTable, commonServiceImpl.getApp().getUuid());
	
			return strList;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.predictExec, execUuid, execVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "No data found.", dependsOn);
			throw new RuntimeException((message != null) ? message : "No data found.");
		}
	}

	public List<String> getModelResults(Train train, String execUuid, String execVersion, int rowLimit) throws Exception {
		try {
			TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
					MetaType.trainExec.toString());
	
			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
					train.getSource().getRef().getVersion(), MetaType.datapod.toString());
			if (null == datapod) {
				datapod = (Datapod) commonServiceImpl.getLatestByUuid(train.getSource().getRef().getUuid(),
						MetaType.datapod.toString());
			}
	
			DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(
					trainExec.getResult().getRef().getUuid(), trainExec.getResult().getRef().getVersion(),
					MetaType.datastore.toString());
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			List<String> strList = exec.fetchModelResults(datastore, datapod, rowLimit, securityServiceImpl.getAppInfo().getRef().getUuid());
	
			return strList;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.predictExec, execUuid, execVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "No data found.", dependsOn);
			throw new RuntimeException((message != null) ? message : "No data found.");
		}
	}
	
	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion, String type) throws Exception {

		Object exec = commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		MetaIdentifier mi = new MetaIdentifier();
		String parentType = type.substring(0, type.indexOf("exec"));
		mi.setType(Helper.getMetaType(parentType));
		MetaIdentifierHolder miHolder = (MetaIdentifierHolder) exec.getClass().getMethod("getDependsOn").invoke(exec);
		mi.setUuid(miHolder.getRef().getUuid());
		mi.setVersion(miHolder.getRef().getVersion());
		return mi;
	}
	
	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion, String type) throws Exception {
		
		Object exec = commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		MetaIdentifierHolder resultHolder = (MetaIdentifierHolder) exec.getClass().getMethod("getResult").invoke(exec);
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion(), MetaType.datastore.toString());
		MetaIdentifier mi = new MetaIdentifier();
		ExecStatsHolder execHolder=new ExecStatsHolder();
		mi.setType(MetaType.datastore);
		mi.setUuid(resultHolder.getRef().getUuid());
		mi.setVersion(resultHolder.getRef().getVersion());
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}

	
	
	public HttpServletResponse download(String execUuid, String execVersion, String format, String download, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String type, String order,
			String requestId, RunMode runMode, Layout layout) throws Exception {

		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.download.maxrows"));
		if (rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of " + maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					"Requested rows exceeded the limit of " + maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
		}
		
		if (type.equalsIgnoreCase(MetaType.predictExec.toString())) {
			PredictExec predictExec = (PredictExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.predictExec.toString(), "N");
			List<Map<String, Object>> results = getPredictResults(execUuid, execVersion, rowLimit);
			response = downloadServiceImpl.download(format, response, runMode, results,
					new MetaIdentifierHolder(new MetaIdentifier(MetaType.predictExec, execUuid, execVersion)), layout,
					null, false, "framework.file.download.path", null, predictExec.getDependsOn(), null);
		} else {
			SimulateExec simulateExec = (SimulateExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.simulateExec.toString(), "N");
			List<Map<String, Object>> results = getSimulateResults(execUuid, execVersion, rowLimit);
			response = downloadServiceImpl.download(format, response, runMode, results,
					new MetaIdentifierHolder(new MetaIdentifier(MetaType.simulateExec, execUuid, execVersion)), layout,
					null, false, "framework.file.download.path", null, simulateExec.getDependsOn(), null);
		}
		return response;
	}
	
	public Model getModelByTrainExec(String trainExecUUID, String trainExecVersion) throws JsonProcessingException {
		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUUID, trainExecVersion,
				MetaType.trainExec.toString());

		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainExec.getDependsOn().getRef().getUuid(),
				trainExec.getDependsOn().getRef().getVersion(), MetaType.train.toString());

		return (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(),
				train.getDependsOn().getRef().getVersion(), MetaType.model.toString());

	}

	public TrainExec getLatestTrainExecByTrain(String trainUuid, String trainVersion) throws Exception {

		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainUuid, trainVersion, MetaType.train.toString());
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("statusList");
		query.fields().include("dependsOn");
		query.fields().include("result");
		query.fields().include("createdOn");
		query.fields().include("active");
		query.fields().include("appInfo");
		query.fields().include("createdBy");

		try {
			query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(train.getUuid()));
			query.addCriteria(Criteria.where("statusList.stage").is("COMPLETED"));
			query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
			query.addCriteria(Criteria.where("active").is("Y"));
			query.with(new Sort(Sort.Direction.DESC, "version"));
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException | NullPointerException
				| ParseException e) {
			e.printStackTrace();
		}

		List<TrainExec> trainExecList = mongoTemplate.find(query, TrainExec.class);
		if (trainExecList.size() > 0) {
			return trainExecList.get(0);
		} else {
			throw new Exception("No executed train collection available.");
		}
	}
	
	/**
	 * Kill meta thread if RUNNING
	 * @param uuid
	 * @param version
	 */
	/*****************************Unused******************************/
	public void kill (String uuid, String version) {
		super.kill(uuid, version, MetaType.trainExec);
	}
	
	public void setStatus (String type, String uuid, String version,String status){
		if(status.toLowerCase().equalsIgnoreCase(Status.Stage.PAUSE.toString().toLowerCase())){
			super.PAUSE(uuid, version, Helper.getMetaType(type));
		}
		else if(status.toLowerCase().equalsIgnoreCase(Status.Stage.RESUME.toString().toLowerCase())){
			super.RESUME(uuid,version, Helper.getMetaType(type));
		}
		else if(status.toLowerCase().equalsIgnoreCase(Status.Stage.KILLED.toString().toLowerCase())){
		      kill(uuid, version,Helper.getMetaType(type));
		}
		
	}
	
	public void kill (String uuid, String version, MetaType execType) {
		BaseExec baseExec = null;
		try {
			baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString());
		} catch (JsonProcessingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (baseExec == null) {
			logger.info("RuleExec not found. Exiting...");
			return;
		}
		if (!Helper.getLatestStatus(baseExec.getStatusList()).equals(new Status(Status.Stage.RUNNING, new Date()))) {
			logger.info("Latest Status is not in RUNNING. Exiting...");
		}
		try {
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.TERMINATING);
			}
			@SuppressWarnings("unchecked")
			FutureTask<TaskHolder> futureTask = (FutureTask<TaskHolder>) taskThreadMap.get(execType+"_"+baseExec.getUuid()+"_"+baseExec.getVersion());
				futureTask.cancel(true);
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.KILLED);
			}
		} catch (Exception e) {
			logger.info("FAILED to kill. uuid : " + uuid + " version : " + version);
			try {
				synchronized (baseExec.getUuid()) {
					commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.KILLED);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			taskThreadMap.remove(execType+"_"+baseExec.getUuid()+"_"+baseExec.getVersion());
			e.printStackTrace();
		}
	}
	
	public void restartTrain(String type, String uuid, String version, ExecParams execParams, RunMode runMode)
			throws Exception {
		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.trainExec.toString());
		synchronized (trainExec.getUuid()) {
			trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.READY);
		}
		try {
			Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainExec.getDependsOn().getRef().getUuid(), trainExec.getDependsOn().getRef().getVersion(), trainExec.getDependsOn().getRef().getType().toString());
			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), train.getDependsOn().getRef().getType().toString());
			Algorithm algorithm= null;
			if (model.getDependsOn().getRef().getVersion() != null) {
				algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(), MetaType.algorithm.toString());
			} else {
				algorithm = (Algorithm) commonServiceImpl.getLatestByUuid(model.getDependsOn().getRef().getUuid(), MetaType.algorithm.toString());
			}
			
			if(algorithm.getLibraryType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				modelServiceImpl.prepareTrain(train.getUuid(), train.getVersion(), trainExec, trainExec.getExecParams(), runMode);
			} else {
				String algoClassName = algorithm.getTrainClass();
				Object algoClass = Class.forName(algoClassName).newInstance();
				List<ParamMap> paramMapList = null;
				if (!model.getType().equalsIgnoreCase(ExecContext.R.toString())
						&& !model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					if(execParams == null) {
						execParams = trainExec.getExecParams();
					}
					paramMapList = metadataServiceImpl.getParamMap(execParams, train.getUuid(), train.getVersion(), algoClass);
				}
				if (paramMapList.size() > 0) {
					for (ParamMap paramMap : paramMapList) {
						Thread.sleep(1000); // Should be parameterized in a class
						modelServiceImpl.train(train, model, trainExec, execParams, paramMap, runMode,algoClass);
						trainExec = null;
					}
				} else {
					modelServiceImpl.train(train, model, trainExec, execParams, null, runMode,algoClass);
				}
			}			
		} catch (Exception e) {
			synchronized (trainExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.FAILED);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.trainExec, uuid, version));
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Train.", dependsOn);
					throw new Exception((message != null) ? message : "Can not parse Train.");
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
			dependsOn.setRef(new MetaIdentifier(MetaType.trainExec, uuid, version));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not parse Train.", dependsOn);
			throw new Exception((message != null) ? message : "Can not parse Train.");
		}
	}
	
	public void restartPredict(String type, String uuid, String version, ExecParams execParams, RunMode runMode)
			throws Exception {
		PredictExec predictExec = (PredictExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.predictExec.toString());
		synchronized (predictExec.getUuid()) {
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.READY);
		}
		try {
			Predict predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(predictExec.getDependsOn().getRef().getUuid(), predictExec.getDependsOn().getRef().getVersion(), predictExec.getDependsOn().getRef().getType().toString());
			modelServiceImpl.predict(predict, execParams, predictExec, runMode);
		} catch (Exception e) {
			synchronized (predictExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.FAILED);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.predictExec, uuid, version));
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Predict restart operation FAILED.", dependsOn);
					throw new Exception((message != null) ? message : "Predict restart operation FAILED.");
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
			dependsOn.setRef(new MetaIdentifier(MetaType.predictExec, uuid, version));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Predict restart operation FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "Predict restart operation FAILED.");
		}
	}
	
	public void restartSimulate(String type, String uuid, String version, ExecParams execParams, RunMode runMode)
			throws Exception {
		SimulateExec simulateExec = (SimulateExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.simulateExec.toString());
		try {
			Simulate simulate = (Simulate) commonServiceImpl.getOneByUuidAndVersion(simulateExec.getDependsOn().getRef().getUuid(), simulateExec.getDependsOn().getRef().getVersion(), simulateExec.getDependsOn().getRef().getType().toString());
			modelServiceImpl.simulate(simulate, execParams, simulateExec, runMode);
		} catch (Exception e) {
			synchronized (simulateExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.FAILED);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.simulateExec, uuid, version));
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Simulate restart operation FAILED.", dependsOn);
					throw new Exception((message != null) ? message : "Simulate restart operation FAILED.");
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
			dependsOn.setRef(new MetaIdentifier(MetaType.simulateExec, uuid, version));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Simulate restart operation FAILED.", dependsOn);
			throw new Exception((message != null) ? message : "Simulate restart operation FAILED.");
		}
	}
	
}
