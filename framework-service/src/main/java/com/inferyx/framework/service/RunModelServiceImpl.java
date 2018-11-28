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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.sql.SaveMode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FeatureAttrMap;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.domain.TrainResult;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.DL4JExecutor;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;

public class RunModelServiceImpl implements Callable<TaskHolder> {

	private static final Logger logger = Logger.getLogger(RunModelServiceImpl.class);
	private HDFSInfo hdfsInfo;

	private String algorithmUUID;
	private String algorithmVersion;
	private AlgorithmServiceImpl algorithmServiceImpl;
	/*private ModelExec modelExec;*/
	private DataStoreServiceImpl dataStoreServiceImpl;
	private Model model;
	private ModelExecServiceImpl modelExecServiceImpl;
	private ParamSetServiceImpl paramSetServiceImpl;
	private ParamListServiceImpl paramListServiceImpl;
	private ParamMap paramMap;
	private CommonServiceImpl<?> commonServiceImpl;
	private RunMode runMode;
	private DataFrameService dataFrameService;
	private SessionContext sessionContext;
	private CustomLogger customLogger;
	private String modelType;
	private ModelServiceImpl modelServiceImpl;
	private String logPath;
	private ExecParams execParams;
	private ExecutorFactory execFactory;
	private SecurityServiceImpl securityServiceImpl;
	private TrainExec trainExec;
	private Train train;
	private Object algoclass;
	private DL4JExecutor dl4jExecutor;
	

	private String name;
	private MetaType execType;
	
	public Object getAlgoclass() {
		return this.algoclass;
	}

	public void setAlgoclass(Object algoclass) {
		this.algoclass = algoclass;
	}
	MetadataServiceImpl metadataServiceImpl;


	/**
	 * @Ganesh
	 *
	 * @return the metadataServiceImpl
	 */
	public MetadataServiceImpl getMetadataServiceImpl() {
		return metadataServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param metadataServiceImpl the metadataServiceImpl to set
	 */
	public void setMetadataServiceImpl(MetadataServiceImpl metadataServiceImpl) {
		this.metadataServiceImpl = metadataServiceImpl;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the execType
	 */
	public MetaType getExecType() {
		return execType;
	}

	/**
	 * @param execType the execType to set
	 */
	public void setExecType(MetaType execType) {
		this.execType = execType;
	}

	/**
	 * @Ganesh
	 *
	 * @return the train
	 */
	public Train getTrain() {
		return train;
	}

	/**
	 * @Ganesh
	 *
	 * @param train the train to set
	 */
	public void setTrain(Train train) {
		this.train = train;
	}

	/**
	 * @Ganesh
	 *
	 * @return the trainExec
	 */
	public TrainExec getTrainExec() {
		return trainExec;
	}

	/**
	 * @Ganesh
	 *
	 * @param trainExec the trainExec to set
	 */
	public void setTrainExec(TrainExec trainExec) {
		this.trainExec = trainExec;
	}

	/**
	 * @Ganesh
	 *
	 * @return the securityServiceImpl
	 */
	public SecurityServiceImpl getSecurityServiceImpl() {
		return securityServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param securityServiceImpl
	 *            the securityServiceImpl to set
	 */
	public void setSecurityServiceImpl(SecurityServiceImpl securityServiceImpl) {
		this.securityServiceImpl = securityServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the execFactory
	 */
	public ExecutorFactory getExecFactory() {
		return execFactory;
	}

	/**
	 * @Ganesh
	 *
	 * @param execFactory
	 *            the execFactory to set
	 */
	public void setExecFactory(ExecutorFactory execFactory) {
		this.execFactory = execFactory;
	}

	/**
	 * @Ganesh
	 *
	 * @return the logPath
	 */
	public String getLogPath() {
		return logPath;
	}

	/**
	 * @Ganesh
	 *
	 * @param logPath
	 *            the logPath to set
	 */
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	/**
	 * @Ganesh
	 *
	 * @return the modelServiceImpl
	 */
	public ModelServiceImpl getModelServiceImpl() {
		return modelServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param modelServiceImpl
	 *            the modelServiceImpl to set
	 */
	public void setModelServiceImpl(ModelServiceImpl modelServiceImpl) {
		this.modelServiceImpl = modelServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the modelType
	 */
	public String getModelType() {
		return modelType;
	}

	/**
	 * @Ganesh
	 *
	 * @param modelType
	 *            the modelType to set
	 */
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	/**
	 * @Ganesh
	 *
	 * @return the customLogger
	 */
	public CustomLogger getCustomLogger() {
		return customLogger;
	}

	/**
	 * @Ganesh
	 *
	 * @param customLogger
	 *            the customLogger to set
	 */
	public void setCustomLogger(CustomLogger customLogger) {
		this.customLogger = customLogger;
	}

	/**
	 * @Ganesh
	 *
	 * @return the sessionContext
	 */
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	/**
	 * @Ganesh
	 *
	 * @param sessionContext
	 *            the sessionContext to set
	 */
	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 * @Ganesh
	 *
	 * @return the dataFrameService
	 */
	public DataFrameService getDataFrameService() {
		return dataFrameService;
	}

	/**
	 * @Ganesh
	 *
	 * @param dataFrameService
	 *            the dataFrameService to set
	 */
	public void setDataFrameService(DataFrameService dataFrameService) {
		this.dataFrameService = dataFrameService;
	}

	/**
	 * @Ganesh
	 *
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}

	/**
	 * @Ganesh
	 *
	 * @param runMode
	 *            the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	public AlgorithmServiceImpl getAlgorithmServiceImpl() {
		return algorithmServiceImpl;
	}

	public void setAlgorithmServiceImpl(AlgorithmServiceImpl algorithmServiceImpl) {
		this.algorithmServiceImpl = algorithmServiceImpl;
	}

	public ParamMap getParamMap() {
		return paramMap;
	}

	public void setParamMap(ParamMap paramMap) {
		this.paramMap = paramMap;
	}

	public ParamListServiceImpl getParamListServiceImpl() {
		return paramListServiceImpl;
	}

	public void setParamListServiceImpl(ParamListServiceImpl paramListServiceImpl) {
		this.paramListServiceImpl = paramListServiceImpl;
	}

	public ParamSetServiceImpl getParamSetServiceImpl() {
		return paramSetServiceImpl;
	}

	public void setParamSetServiceImpl(ParamSetServiceImpl paramSetServiceImpl) {
		this.paramSetServiceImpl = paramSetServiceImpl;
	}

	public ExecParams getExecParams() {
		return execParams;
	}

	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}

	public HDFSInfo getHdfsInfo() {
		return hdfsInfo;
	}

	public void setHdfsInfo(HDFSInfo hdfsInfo) {
		this.hdfsInfo = hdfsInfo;
	}

	public ModelExecServiceImpl getModelExecServiceImpl() {
		return modelExecServiceImpl;
	}

	public void setModelExecServiceImpl(ModelExecServiceImpl modelExecServiceImpl) {
		this.modelExecServiceImpl = modelExecServiceImpl;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public DataStoreServiceImpl getDataStoreServiceImpl() {
		return dataStoreServiceImpl;
	}

	public void setDataStoreServiceImpl(DataStoreServiceImpl dataStoreServiceImpl) {
		this.dataStoreServiceImpl = dataStoreServiceImpl;
	}

	/*public ModelExec getModelExec() {
		return modelExec;
	}

	public void setModelExec(ModelExec modelExec) {
		this.modelExec = modelExec;
	}*/

	public String getAlgorithmUUID() {
		return algorithmUUID;
	}

	public void setAlgorithmUUID(String algorithmUUID) {
		this.algorithmUUID = algorithmUUID;
	}

	public String getAlgorithmVersion() {
		return algorithmVersion;
	}

	public void setAlgorithmVersion(String algorithmVersion) {
		this.algorithmVersion = algorithmVersion;
	}

	public CommonServiceImpl<?> getCommonServiceImpl() {
		return commonServiceImpl;
	}

	public void setCommonServiceImpl(CommonServiceImpl<?> commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	/*@Override
	public void run() {
		try {
			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			execute();
			modelExec = (ModelExec) commonServiceImpl.setMetaStatus(modelExec, MetaType.modelExec,
					Status.Stage.Completed);
			if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
					|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(),
						modelExec.getStatusList().size() > 0
								? "Latest status: "
										+ modelExec.getStatusList().get(modelExec.getStatusList().size() - 1).getStage()
								: "Status list is empty",
						logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (modelType.equalsIgnoreCase(ExecContext.R.toString())
						|| modelType.equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					customLogger.writeErrorLog(this.getClass(),
							StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				}
				modelExec = (ModelExec) commonServiceImpl.setMetaStatus(modelExec, MetaType.modelExec,
						Status.Stage.Failed);
			} catch (Exception e1) {
				e1.printStackTrace();

				if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
						|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					customLogger.writeErrorLog(this.getClass(),
							StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				}
			}
		}
	}*/

	/*@SuppressWarnings({ "unused" })
	public void execute() throws Exception {
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		List<Status> statusList = modelExec.getStatusList();
		modelExec = (ModelExec) commonServiceImpl.setMetaStatus(modelExec, MetaType.modelExec, Status.Stage.InProgress);
		if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
				|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
			customLogger.writeLog(this.getClass(), modelExec.getStatusList().size() > 0
					? "Latest status: " + modelExec.getStatusList().get(modelExec.getStatusList().size() - 1).getStage()
					: "Status list is empty", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
		}
		boolean isSuccess = false;
		Object result = null;
		if (!modelType.equalsIgnoreCase(ExecContext.R.toString())
				&& !modelType.equalsIgnoreCase(ExecContext.PYTHON.toString())) {
			Algorithm algorithm = null;
			if (algorithmVersion != null) {
				algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(algorithmUUID, algorithmVersion,
						MetaType.algorithm.toString());
			} else {
				algorithm = (Algorithm) commonServiceImpl.getLatestByUuid(algorithmUUID, MetaType.algorithm.toString());
			}

//			
//			 * Dataset<Row> df = null;
//			 * if(model.getSource().getRef().getType().toString().equals(MetaType.datapod.
//			 * toString())){ df =
//			 * dataFrameService.getDataFrameByDatapod(model.getSource().getRef().getUuid(),
//			 * model.getSource().getRef().getVersion()); } else
//			 * if(model.getSource().getRef().getType().toString().equals(MetaType.dataset.
//			 * toString())){ df =
//			 * dataFrameService.getDataFrameByDataset(model.getSource().getRef().getUuid(),
//			 * model.getSource().getRef().getVersion(), runMode); }
//			 

			String[] fieldArray = modelExecServiceImpl.getAttributeNames(model);
//			
//			 * VectorAssembler va = new VectorAssembler(); Dataset<Row> training = null;
//			 * 
//			 * if (algorithm.getTrainName().contains("LinearRegression") ||
//			 * algorithm.getTrainName().contains("LogisticRegression")){ va = (new
//			 * VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
//			 * Dataset<Row> trainingTmp = va.transform(df); //training =
//			 * trainingTmp.withColumnRenamed(fieldArray[0],"label"); training =
//			 * trainingTmp.withColumn("label",
//			 * trainingTmp.col(modelExecServiceImpl.getLabelName(model)).cast("Double")).
//			 * select("label","features");
//			 * 
//			 * 
//			 * logger.info("DataFrame count for training: "+training.count());
//			 * 
//			 * } else if (algorithm.getTrainName().contains("DecisionTreeClassifier")){ va =
//			 * (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
//			 * Dataset<Row> trainingTmp = va.transform(df); training =
//			 * trainingTmp.withColumnRenamed(fieldArray[0],"label"); } else { va = (new
//			 * VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
//			 * training = va.transform(df); }
//			 
			String modelName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(),
					modelExec.getVersion());
			String filePath = String.format("/%s/%s/%s", model.getUuid().replace("-", "_"), model.getVersion(),
					modelExec.getVersion());

//			
//			 * logger.info("tableName--Algo:"+modelName); String filePathUrl =
//			 * String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(),
//			 * filePath); sparkMLOperator =new SparkMLOperator();
//			 * sparkMLOperator.setParamSetServiceImpl(paramSetServiceImpl);
//			 * sparkMLOperator.setSparkContext(sparkContext);
//			 * sparkMLOperator.setFilePathUrl(filePathUrl); sparkMLOperator.setModel(model);
//			 * isSuccess =sparkMLOperator.train(algorithm.getTrainName(),
//			 * algorithm.getModelName(), training, paramMap);
//			 
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			result = exec.fetchAndTrainModel(model, fieldArray, algorithm, modelName, filePath, paramMap,
					securityServiceImpl.getAppInfo().getRef().getUuid());
			dataStoreServiceImpl.setRunMode(Mode.BATCH);
			dataStoreServiceImpl.create((String) result, modelName,
					new MetaIdentifier(MetaType.model, model.getUuid(), model.getVersion()),
					new MetaIdentifier(MetaType.modelExec, modelExec.getUuid(), modelExec.getVersion()),
					modelExec.getAppInfo(), modelExec.getCreatedBy(), SaveMode.Append.toString(), resultRef);
			modelExec.setResult(resultRef);
			if (result != null)
				isSuccess = true;
			else
				isSuccess = false;
		}
		if (modelType != null && (modelType.equalsIgnoreCase(ExecContext.R.toString())
				|| modelType.equalsIgnoreCase(ExecContext.PYTHON.toString()))) {
			isSuccess = modelServiceImpl.executeScript(model.getType(), model.getScriptName(), modelExec.getUuid(),
					modelExec.getVersion(), null);
			// customLogger.writeLog(this.getClass(), "Script executed ....", logPath);
		}

		if (!isSuccess) {
			modelExec = (ModelExec) commonServiceImpl.setMetaStatus(modelExec, MetaType.modelExec, Status.Stage.Failed);
			if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
					|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(),
						modelExec.getStatusList().size() > 0
								? "Latest status: "
										+ modelExec.getStatusList().get(modelExec.getStatusList().size() - 1).getStage()
								: "Status list is empty",
						logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
		}
	}*/
	/*
	 * public void save(){
	 * 
	 * }
	 */
	
	/**
	 * @return the dl4jExecutor
	 */
	public DL4JExecutor getDl4jExecutor() {
		return dl4jExecutor;
	}

	/**
	 * @param dl4jExecutor the dl4jExecutor to set
	 */
	public void setDl4jExecutor(DL4JExecutor dl4jExecutor) {
		this.dl4jExecutor = dl4jExecutor;
	}

	@Override
	public TaskHolder call() {
		try {
			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			execute();
			trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec,
					Status.Stage.Completed);
			if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
					|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(),
						trainExec.getStatusList().size() > 0
								? "Latest status: "
										+ trainExec.getStatusList().get(trainExec.getStatusList().size() - 1).getStage()
								: "Status list is empty",
						logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (modelType.equalsIgnoreCase(ExecContext.R.toString())
						|| modelType.equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					customLogger.writeErrorLog(this.getClass(),
							StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				}
				trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec,
						Status.Stage.Failed);
			} catch (Exception e1) {
				e1.printStackTrace();

				if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
						|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					customLogger.writeErrorLog(this.getClass(),
							StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				}
			}
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(execType, trainExec.getUuid(), trainExec.getVersion()));
		return taskHolder;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public void execute() throws Exception {
		try {
			TrainResult trainResult = new TrainResult();
			trainResult.setName(train.getName());
			trainResult.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion())));
			trainResult.setParamList(new MetaIdentifierHolder(execParams.getParamListInfo().get(0).getRef()));
			trainResult.setBaseEntity();
			
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = null;
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			Map<String,String> trainOtherParam = new HashMap<>();
			List<Status> statusList = trainExec.getStatusList();
			trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.InProgress);
			if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
					|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(), trainExec.getStatusList().size() > 0
						? "Latest status: " + trainExec.getStatusList().get(trainExec.getStatusList().size() - 1).getStage()
						: "Status list is empty", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
			boolean isSuccess = false;
			Object result = null;
			String[] fieldArray = modelExecServiceImpl.getAttributeNames(train);
			String trainName = String.format("%s_%s_%s", train.getUuid().replace("-", "_"), train.getVersion(), trainExec.getVersion());
			String filePath = String.format("/%s/%s/%s", train.getUuid(), train.getVersion(), trainExec.getVersion());
			String tableName = String.format("%s_%s_%s", train.getUuid().replace("-", "_"), train.getVersion(), trainExec.getVersion());
			String appUuid = commonServiceImpl.getApp().getUuid();
			
			if (!modelType.equalsIgnoreCase(ExecContext.R.toString())
					&& !modelType.equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				Algorithm algorithm = null;
				if (algorithmVersion != null) {
					algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(algorithmUUID, algorithmVersion,
							MetaType.algorithm.toString());
				} else {
					algorithm = (Algorithm) commonServiceImpl.getLatestByUuid(algorithmUUID, MetaType.algorithm.toString());
				}

				trainResult.setAlgorithm(algorithm.getName());
				trainResult.setAlgoType(algorithm.getType());

				String filePathUrl = String.format("%s%s%s", Helper.getPropertyValue("framework.hdfs.URI"), Helper.getPropertyValue("framework.model.train.path"), filePath);
				trainOtherParam.put("confusionMatrixTableName",trainName+"confusionMatrix");
				datasource = commonServiceImpl.getDatasourceByApp();
				exec = execFactory.getExecutor(datasource.getType());

				Object source = (Object) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
						train.getSource().getRef().getVersion(), train.getSource().getRef().getType().toString());
				
				String label = commonServiceImpl.resolveLabel(train.getLabelInfo());
//				String sql = modelServiceImpl.generateSQLBySource(source, execParams);
//				exec.executeAndRegister(sql, (tableName+"_train_data"), appUuid);
				
				String featureMappedSQL = modelServiceImpl.generateFeatureSQLBySource(train.getFeatureAttrMap(), source, execParams, fieldArray, label, (tableName+"_train_data"));
				ResultSetHolder sourceRsHolder = exec.executeAndRegister(featureMappedSQL, (tableName+"_train_data"), appUuid);
				long rowCount = sourceRsHolder.getCountRows();
				
//				trainResult.setTotalRecords(rowCount);
				
				//Object va = exec.assembleDF(fieldArray, (tableName+"_train_data"), algorithm.getTrainName(), model.getLabel(), appUuid);
				Map<String, String> mappingList = new LinkedHashMap<>();
				for(FeatureAttrMap featureAttrMap : train.getFeatureAttrMap()) {
					mappingList.put(featureAttrMap.getAttribute().getAttrName(), featureAttrMap.getFeature().getFeatureName());
				}

				exec.renameDfColumnName((tableName+"_train_data"), mappingList, appUuid);
				
				Object trndModel = null;
			    
				if(train.getUseHyperParams().equalsIgnoreCase("N") && !model.getType().equalsIgnoreCase(ExecContext.DL4J.toString())) {
					//Without hypertuning
					trndModel = exec.train(paramMap, fieldArray, label, algorithm.getTrainClass(), train.getTrainPercent()
							, train.getValPercent(), (tableName+"_train_data"), appUuid, algoclass, trainOtherParam, trainResult);
				} else if (!model.getType().equalsIgnoreCase(ExecContext.DL4J.toString())) {		
					//With hypertuning
					List<ParamListHolder> paramListHolderList = null;
					if(execParams != null) {
						if(execParams.getParamInfo() != null) {
							for(ParamSetHolder paramSetHolder : execParams.getParamInfo()){
								paramListHolderList = metadataServiceImpl.getParamListHolder(paramSetHolder);
								for(ParamListHolder paramListHolder : paramListHolderList) {
									MetaIdentifier hyperParamMI = paramListHolder.getRef();
									ParamList hyperParamList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(hyperParamMI.getUuid(), hyperParamMI.getVersion(), hyperParamMI.getType().toString());
									trndModel = exec.trainCrossValidation(paramMap, fieldArray, label, algorithm.getTrainClass()
											, train.getTrainPercent(), train.getValPercent(), (tableName+"_train_data")
											, hyperParamList.getParams(), appUuid, trainOtherParam, trainResult);
								}
							}
						} else if(execParams.getParamListInfo() != null) {
								paramListHolderList = execParams.getParamListInfo();
							
							for(ParamListHolder paramListHolder : paramListHolderList) {
								MetaIdentifier hyperParamMI = paramListHolder.getRef();
								ParamList hyperParamList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(hyperParamMI.getUuid(), hyperParamMI.getVersion(), hyperParamMI.getType().toString());
								trndModel = exec.trainCrossValidation(paramMap, fieldArray, label, algorithm.getTrainClass()
										, train.getTrainPercent(), train.getValPercent(), (tableName+"_train_data")
										, hyperParamList.getParams(), appUuid, trainOtherParam, trainResult);
							}
						}
					} else {
						ParamListHolder plHolder = new ParamListHolder();
						plHolder.setRef(algorithm.getParamListWH().getRef());
						paramListHolderList = new ArrayList<>();
						paramListHolderList.add(plHolder);
						
						for(ParamListHolder paramListHolder : paramListHolderList) {
							MetaIdentifier hyperParamMI = paramListHolder.getRef();
							ParamList hyperParamList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(hyperParamMI.getUuid()
									, hyperParamMI.getVersion(), hyperParamMI.getType().toString());
							trndModel = exec.trainCrossValidation(paramMap, fieldArray, label, algorithm.getTrainClass()
									, train.getTrainPercent(), train.getValPercent(), (tableName+"_train_data")
									, hyperParamList.getParams(), appUuid, trainOtherParam, trainResult);
						}
					}
				} else {
					trndModel = dl4jExecutor.trainDL(execParams, fieldArray, label, algorithm.getTrainClass()
							, train.getTrainPercent(), train.getValPercent(), (tableName+"_train_data")
							, appUuid, algoclass, trainOtherParam);
				}
								
				result = trndModel;				
				List<String> customDirectories = exec.getCustomDirsFromTrainedModel(trndModel);

				boolean isModelSved = modelServiceImpl.save(algorithm.getModelClass(), trndModel, filePathUrl+"/model");
				String defaultDir = null;
				if (algorithm.getSavePmml().equalsIgnoreCase("Y")) {
					try {
						String filePathUrl_2 = null;
						if(filePathUrl.contains(hdfsInfo.getHdfsURL()))
							filePathUrl_2 = filePathUrl.replaceAll(hdfsInfo.getHdfsURL(), "");
						defaultDir = filePathUrl_2;
						String pmmlLocation = filePathUrl_2 + "/" + train.getUuid() + "_" + train.getVersion() + "_"
								+ (filePathUrl_2.substring(filePathUrl_2.lastIndexOf("/") + 1)) + ".pmml";
						boolean isSaved = exec.savePMML(trndModel, "trainedDataSet", pmmlLocation, appUuid);
						if(isSaved)
							logger.info("PMML saved at location: "+pmmlLocation);
						else
							logger.info("PMML not saved.");
					 }catch (JAXBException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (isModelSved) {	
					defaultDir = filePathUrl.replaceAll(hdfsInfo.getHdfsURL(), "");
					if(trndModel instanceof CrossValidatorModel) {
						filePathUrl = filePathUrl+"/model" + "/bestModel" + "/stages/" + customDirectories.get(1) + "/data/";
						Map<String, Object> summary = exec.summary(trndModel, algorithm.getSummaryMethods(), appUuid);
						
//						if(train.getFeatureImportance() != null && train.getFeatureImportance().equalsIgnoreCase("Y")) {
//							try {
//								Transformer[] transformer = ((PipelineModel) trndModel).stages();
//								List<Double> featureWeightList = exec.featureImportance(transformer[1], appUuid);
//								if(!featureWeightList.isEmpty()) {
//									summary.put("featureImportances", featureWeightList);
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}							
//						}
						String fileName = tableName+".result";
						summary = exec.calculateConfusionMatrixAndRoc(summary,trainOtherParam.get("confusionMatrixTableName"),appUuid);
						
						double[] featureimportancesArr = (double[])summary.get("featureimportances");						
						if(featureimportancesArr != null) {
							List<Double> featureImportance = Arrays.asList(ArrayUtils.toObject(featureimportancesArr));
							trainResult.setFeatureImportance(featureImportance);
						}
						trainResult.setAccuracy((double) summary.get("accuracy"));
						trainResult.setRecall((double) summary.get("recall"));
						trainResult.setF1Score((double) summary.get("f1Score"));
						trainResult.setPrecision((double) summary.get("precision"));
//						trainResult.setRocAUC((List<Double>) summary.get("roc"));
						
						writeSummaryToFile(summary, defaultDir, fileName);
					} else if(trndModel instanceof PipelineModel) {
						filePathUrl = filePathUrl+"/model" + "/stages/" + customDirectories.get(1) + "/data/";
						Map<String, Object> summary = exec.summary(trndModel, algorithm.getSummaryMethods(), appUuid);
						
//						if(train.getFeatureImportance() != null && train.getFeatureImportance().equalsIgnoreCase("Y")) {
//							try {
//								Transformer[] transformer = ((PipelineModel) trndModel).stages();
//								List<Double> featureWeightList = exec.featureImportance(transformer[1], appUuid);
//								if(!featureWeightList.isEmpty()) {
//									summary.put("featureImportances", featureWeightList);
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}							
//						}
						
						String fileName = tableName+".result";
						summary = exec.calculateConfusionMatrixAndRoc(summary,trainOtherParam.get("confusionMatrixTableName"),appUuid);
						double[] featureimportancesArr = (double[])summary.get("featureimportances");	
						if(featureimportancesArr != null) {
							List<Double> featureImportance = Arrays.asList(ArrayUtils.toObject(featureimportancesArr));
							trainResult.setFeatureImportance(featureImportance);
						}						
						trainResult.setAccuracy((double) summary.get("accuracy"));
						trainResult.setRecall((double) summary.get("recall"));
						trainResult.setF1Score((double) summary.get("f1Score"));
						trainResult.setPrecision((double) summary.get("precision"));
						trainResult.setConfusionMatrix((double[][]) summary.get("confusionMatrix"));
//						trainResult.setRocAUC((List<Double>) summary.get("roc"));
						
						writeSummaryToFile(summary, defaultDir, fileName);
					} else {
						filePathUrl = null;
					}
				} else {
					filePathUrl = null;
				}
				commonServiceImpl.save(MetaType.trainresult.toString(), trainResult);
				//result = exec.fetchAndTrainModel(train, model, fieldArray, algorithm, trainName, filePath, paramMap, securityServiceImpl.getAppInfo().getRef().getUuid());
				dataStoreServiceImpl.setRunMode(RunMode.BATCH);
				dataStoreServiceImpl.create(filePathUrl, trainName,
						new MetaIdentifier(MetaType.train, train.getUuid(), train.getVersion()),
						new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
						trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.Append.toString(), resultRef);
				trainExec.setResult(resultRef);
				if (result != null)
					isSuccess = true;
				else
					isSuccess = false;
			}
			if (modelType != null && (modelType.equalsIgnoreCase(ExecContext.R.toString())
					|| modelType.equalsIgnoreCase(ExecContext.PYTHON.toString()))) {
				// Save the data as csv
				String saveFileName = Helper.getPropertyValue("framework.model.train.path")+"/csv/"+tableName;
				String modelFileName = Helper.getPropertyValue("framework.model.train.path")+"/"+model.getName();
				exec = execFactory.getExecutor(datasource.getType());
				exec.saveTrainFile(fieldArray, trainName, train.getTrainPercent(), train.getValPercent(), tableName, appUuid, saveFileName);
				List<ParamListHolder> paramInfoList = execParams.getParamListInfo();
				List<String> argList = Arrays.asList(paramInfoList.stream().map(p -> p.getParamName() + "~\"" + p.getParamValue().getValue() + "\"")
										.collect(Collectors.joining("~")).split("~"));
				argList.add("filename");
				argList.add(saveFileName);
				argList.add("modelFileName");
				argList.add(modelFileName);
				List<String> scriptPrintedMsgs = modelServiceImpl.executeScript(model.getType(), model.getScriptName(), trainExec.getUuid(),
						trainExec.getVersion(), argList);
				if(scriptPrintedMsgs.isEmpty()) {
					isSuccess = false;
				}
				// customLogger.writeLog(this.getClass(), "Script executed ....", logPath);
			}

//			List<String> tempTableList = new ArrayList<>();
//			tempTableList.add((tableName+"_pred_data"));
//			sparkExecutor.dropTempTable(tempTableList);
			
			if (!isSuccess) {
				trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.Failed);
				if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
						|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					customLogger.writeLog(this.getClass(),
							trainExec.getStatusList().size() > 0
									? "Latest status: "
											+ trainExec.getStatusList().get(trainExec.getStatusList().size() - 1).getStage()
									: "Status list is empty",
							logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.Failed);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Train execution failed.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Train execution failed.");			 		
		}		
	}
	
	public String writeSummaryToFile(Map<String, Object> summary, String directory, String fileName) throws IOException {		
		String filePath = directory + "/" + fileName;		
		String resultJson = new ObjectMapper().writeValueAsString(summary);
		PrintWriter printWriter = new PrintWriter(filePath);
		printWriter.write(resultJson);
		printWriter.close();
		return null;
	}
}
