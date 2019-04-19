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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.sql.SaveMode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.controller.TrainResultViewServiceImpl;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Feature;
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
import com.inferyx.framework.enums.EncodingType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.DL4JExecutor;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.StorageContext;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.ImputeOperator;

public class RunTrainServiceImpl implements Callable<TaskHolder> {

	private static final Logger logger = Logger.getLogger(RunTrainServiceImpl.class);
	private HDFSInfo hdfsInfo;

	private String algorithmUUID;
	private String algorithmVersion;
	private AlgorithmServiceImpl algorithmServiceImpl;
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
	private MetadataServiceImpl metadataServiceImpl;
	private TrainResultViewServiceImpl trainResultViewServiceImpl;
	private ImputeOperator imputeOperator;
	

	/**
	 * @Ganesh
	 *
	 * @return the imputeOperator
	 */
	public ImputeOperator getImputeOperator() {
		return imputeOperator;
	}

	/**
	 * @Ganesh
	 *
	 * @param imputeOperator the imputeOperator to set
	 */
	public void setImputeOperator(ImputeOperator imputeOperator) {
		this.imputeOperator = imputeOperator;
	}

	/**
	 * @Ganesh
	 *
	 * @return the algoclass
	 */
	public Object getAlgoclass() {
		return this.algoclass;
	}

	/**
	 * @Ganesh
	 *
	 * @param algoclass the algoclass to set
	 */
	public void setAlgoclass(Object algoclass) {
		this.algoclass = algoclass;
	}

	/**
	 * @Ganesh
	 *
	 * @return the trainResultViewServiceImpl
	 */
	public TrainResultViewServiceImpl getTrainResultViewServiceImpl() {
		return this.trainResultViewServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param trainResultViewServiceImpl the trainResultViewServiceImpl to set
	 */
	public void setTrainResultViewServiceImpl(TrainResultViewServiceImpl trainResultViewServiceImpl) {
		this.trainResultViewServiceImpl = trainResultViewServiceImpl;
	}

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
					Status.Stage.COMPLETED);
//			if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
//					|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//				customLogger.writeLog(this.getClass(),
//						trainExec.getStatusList().size() > 0
//								? "Latest status: "
//										+ trainExec.getStatusList().get(trainExec.getStatusList().size() - 1).getStage()
//								: "Status list is empty",
//						logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
//			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
//				if (modelType.equalsIgnoreCase(ExecContext.R.toString())
//						|| modelType.equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//					customLogger.writeErrorLog(this.getClass(),
//							StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), logPath,
//							Thread.currentThread().getStackTrace()[1].getLineNumber());
//				}
				trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec,
						Status.Stage.FAILED);
			} catch (Exception e1) {
				e1.printStackTrace();

//				if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
//						|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//					customLogger.writeErrorLog(this.getClass(),
//							StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), logPath,
//							Thread.currentThread().getStackTrace()[1].getLineNumber());
//				}
			}
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(execType, trainExec.getUuid(), trainExec.getVersion()));
		return taskHolder;
	}
	
	@SuppressWarnings({ "unused", "unchecked"})
	public void execute() throws Exception {
		try {
			
			TrainResult trainResult = trainResultViewServiceImpl.getTrainResultByTrainExec(trainExec.getUuid(), trainExec.getVersion());
			if(trainResult == null) {
				trainResult = new TrainResult();
			}
			trainResult.setName(train.getName());
			trainResult.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion())));
			trainResult.setParamList(new MetaIdentifierHolder(execParams.getParamListInfo().get(0).getRef()));
			trainResult.setBaseEntity();
			
			Datasource trainSrcDatasource = commonServiceImpl.getDatasourceByObject(train);
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			Map<String, Object> trainOtherParam = new HashMap<>();
			List<Status> statusList = trainExec.getStatusList();
			trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.RUNNING);
//			if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
//					|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//				customLogger.writeLog(this.getClass(), trainExec.getStatusList().size() > 0
//						? "Latest status: " + trainExec.getStatusList().get(trainExec.getStatusList().size() - 1).getStage()
//						: "Status list is empty", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
//			}
			
			MetaIdentifierHolder targetHolder = train.getTrainLocation();			
			Datapod target = null;
			StorageContext storageContext = null;
			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod)) {
				target = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(),
						targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());
				storageContext = commonServiceImpl.getStorageContext(targetHolder.getRef());
			} else {
				storageContext = StorageContext.FILE;
			}
			
			boolean isSuccess = false;
			Object result = null;
			String[] fieldArray = modelExecServiceImpl.getMappedFeatureNames(train);
			String trainName = String.format("%s_%s_%s", train.getUuid().replace("-", "_"), train.getVersion(), trainExec.getVersion());
			String filePath = String.format("/%s/%s/%s", train.getUuid(), train.getVersion(), trainExec.getVersion());
			String tableName = String.format("%s_%s_%s", train.getUuid().replace("-", "_"), train.getVersion(), trainExec.getVersion());
			String appUuid = commonServiceImpl.getApp().getUuid();

			if (!modelType.equalsIgnoreCase(ExecContext.R.toString())
					&& !modelType.equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				Algorithm algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(algorithmUUID, algorithmVersion, MetaType.algorithm.toString(), "N");
				
				trainResult.setTrainClass(algorithm.getTrainClass());
				trainResult.setAlgorithm(algorithm.getName());
				trainResult.setAlgoType(model.getType());

//				String filePathUrl = String.format("%s%s%s", commonServiceImpl.getConfigValue("framework.hdfs.URI"), commonServiceImpl.getConfigValue("framework.model.train.path"), filePath);
				String filePathUrl = String.format("%s%s", commonServiceImpl.getConfigValue("framework.model.train.path"), filePath);
				String testSetPath = filePathUrl.endsWith("/") ? filePathUrl.concat("test_set") : filePathUrl.concat("/").concat("test_set");
				String trainingSetPath = null;
				if(train.getSaveTrainingSet().equalsIgnoreCase("Y")) {
					trainingSetPath = filePathUrl.endsWith("/") ? filePathUrl.concat("train_set") : filePathUrl.concat("/").concat("train_set");
				}
				
				Map<String, EncodingType> encodingDetails = getEncodingDetailsByFeatureAttrMap(train.getFeatureAttrMap(), model.getFeatures());
				
				List<String> vectorFields = new ArrayList<>();
				for(Feature feature : model.getFeatures()) {
					if(feature.getType().equalsIgnoreCase("vector")) {
						vectorFields.add(feature.getName());
					}
				}				
				trainOtherParam.put("vectorFields", vectorFields);
				
				trainOtherParam.put("confusionMatrixTableName", trainName+"confusionMatrix");

				ExecContext execContext = commonServiceImpl.getExecContext();
				IExecutor exec = execFactory.getExecutor(execContext.toString());				
//				exec = execFactory.getExecutor(datasource.getType());

				Object source = (Object) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
						train.getSource().getRef().getVersion(), train.getSource().getRef().getType().toString());
				
				String label = null;
				if(train.getLabelInfo() != null) {
					label = commonServiceImpl.resolveLabel(train.getLabelInfo());
				}
//				String sql = modelServiceImpl.generateSQLBySource(source, execParams);
//				exec.executeAndRegister(sql, (tableName+"_train_data"), appUuid);

				String sourceSql = modelServiceImpl.generateSQLBySource(source, execParams);
				
				ResultSetHolder sourceRsHolder = exec.executeAndRegisterByDatasource(sourceSql, (tableName+"_train_source_data"), trainSrcDatasource, appUuid);
				sourceRsHolder.setTableName((tableName+"_train_source_data"));

				//finding impute values of attribute
				LinkedHashMap<String, Object> resolvedimputeAttributeValues = imputeOperator.resolveAttributeImputeValue(train.getFeatureAttrMap(), source, model, execParams, runMode, (tableName+"_train_source_data"));
				
				//applying imputation valued per column to data
				sourceRsHolder = exec.applyAttrImputeValuesToData(sourceRsHolder, resolvedimputeAttributeValues, true, (tableName+"_train_source_data"));
//				String featureMappedSQL = modelServiceImpl.generateFeatureSQLBySource(train.getFeatureAttrMap(), source, execParams, fieldArray, label, (tableName+"_train_source_data"));
//				ResultSetHolder sourceRsHolder = exec.executeAndRegisterByDatasource(featureMappedSQL, (tableName+"_train_source_data"), trainSrcDatasource, appUuid);
//				sourceRsHolder.setTableName((tableName+"_train_source_data"));

//				sourceRsHolder = exec.replaceNullValByDoubleValFromDF(sourceRsHolder, null, trainSrcDatasource, (tableName+"_train_source_data"), true, appUuid);
				long rowCount = sourceRsHolder.getCountRows();
				
//				trainResult.setTotalRecords(rowCount);
				trainOtherParam.put("sourceDs", trainSrcDatasource);
				trainOtherParam.put("model", model);
				
				//Object va = exec.assembleDF(fieldArray, (tableName+"_train_data"), algorithm.getTrainName(), model.getLabel(), appUuid);
				Map<String, String> mappingList = new LinkedHashMap<>();
				for(FeatureAttrMap featureAttrMap : train.getFeatureAttrMap()) {
					mappingList.put(featureAttrMap.getAttribute().getAttrName(), featureAttrMap.getFeature().getFeatureName());
				}

//				String sql = "SELECT * FROM " + (tableName+"_train_source_data");
//				exec.renameDfColumnName(sql, (tableName+"_train_mapped_data"), mappingList, appUuid);
				
				String tempTrngDfSql = null;
				String tempValDfSql = null;
				if(algorithm.getTrainClass().contains("PCA")) {
					tempTrngDfSql = modelServiceImpl.generateFeatureSQLByTempTable(train.getFeatureAttrMap(), "tempTrngDf", null, tableName.concat("_tempTrngDf"));
					tempValDfSql = modelServiceImpl.generateFeatureSQLByTempTable(train.getFeatureAttrMap(), "tempValDf", null, tableName.concat("_tempValDf"));
				} else {
					tempTrngDfSql = modelServiceImpl.generateFeatureSQLByTempTable(train.getFeatureAttrMap(), "tempTrngDf", label, tableName.concat("_tempTrngDf"));
					tempValDfSql = modelServiceImpl.generateFeatureSQLByTempTable(train.getFeatureAttrMap(), "tempValDf", label, tableName.concat("_tempValDf"));
				}
				
				List<String> rowIdentifierCols = modelServiceImpl.getRowIdentifierCols(train.getRowIdentifier());
				
				Object trndModel = null;
				
				Datapod testSetDP=null;
				Datasource testSetDS=null;
				String testSetTableName = null;
				String testSetFilePathUrl=null;
				
				Datapod trainSetDP=null;
				Datasource trainSetDS=null;
				String trainSetTableName = null;
				String trainSetFilePathUrl=null;
				
				if(train.getTestLocation() != null) {
					
					if(train.getTestLocation().getRef().getType().equals(MetaType.datapod)) {
						String testSetDefaultPath = hdfsInfo.getHdfsURL().concat(commonServiceImpl.getConfigValue("framework.schema.Path"));
						testSetDefaultPath = testSetDefaultPath.endsWith("/") ? testSetDefaultPath : testSetDefaultPath.concat("/");
						
						testSetDP=(Datapod) commonServiceImpl.getOneByUuidAndVersion(train.getTestLocation().getRef().getUuid(),null, train.getTestLocation().getRef().getType().toString());
						testSetDS = commonServiceImpl.getDatasourceByObject(testSetDP);
						String testPath = String.format("/%s/%s/%s", testSetDP.getUuid(), testSetDP.getVersion(), trainExec.getVersion());
						testSetFilePathUrl = String.format("%s%s",testSetDefaultPath, testPath);

						//writing into table
						if(testSetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())
								|| testSetDS.getType().equalsIgnoreCase(ExecContext.spark.toString())) {
							testSetTableName = String.format("%s_%s_%s", testSetDS.getUuid().replaceAll("-", "_"), testSetDS.getVersion(), trainExec.getVersion());
						} else {
							if(testSetDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
								testSetTableName = testSetDS.getSid().concat(".").concat(testSetDP.getName());
							} else {
								testSetTableName = testSetDS.getDbname().concat(".").concat(testSetDP.getName());					
							}
						}
					} else {
						testSetFilePathUrl = filePathUrl.endsWith("/") ? filePathUrl.concat("test_set") : filePathUrl.concat("/").concat("test_set");
					}
				}
				
				if(train.getTrainLocation() != null) {
					
					if(train.getTrainLocation().getRef().getType().equals(MetaType.datapod)) {
						String trainSetDefaultPath = hdfsInfo.getHdfsURL().concat(commonServiceImpl.getConfigValue("framework.schema.Path"));
						
						trainSetDefaultPath = trainSetDefaultPath.endsWith("/") ? trainSetDefaultPath : trainSetDefaultPath.concat("/");
						trainSetDP=(Datapod) commonServiceImpl.getOneByUuidAndVersion(train.getTrainLocation().getRef().getUuid(),null, train.getTrainLocation().getRef().getType().toString());
						trainSetDS = commonServiceImpl.getDatasourceByObject(trainSetDP);
						String trainSetPath = String.format("/%s/%s/%s", trainSetDP.getUuid(), trainSetDP.getVersion(), trainExec.getVersion());
						trainSetFilePathUrl = String.format("%s%s", trainSetDefaultPath, trainSetPath);

						//writing into table
						if(trainSetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())
								|| trainSetDS.getType().equalsIgnoreCase(ExecContext.spark.toString())) {
							trainSetTableName = String.format("%s_%s_%s", trainSetDS.getUuid().replaceAll("-", "_"), trainSetDS.getVersion(), trainExec.getVersion());
						} else {
							if(trainSetDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
								trainSetTableName = trainSetDS.getSid().concat(".").concat(trainSetDP.getName());
							} else {
								trainSetTableName = trainSetDS.getDbname().concat(".").concat(trainSetDP.getName());					
							}
						}
					} else {
						trainSetFilePathUrl = filePathUrl.endsWith("/") ? filePathUrl.concat("train_set") : filePathUrl.concat("/").concat("train_set");
					}
				}
				
				if(train.getUseHyperParams().equalsIgnoreCase("N") && !model.getType().equalsIgnoreCase(ExecContext.DL4J.toString())) {
					//Without hypertuning
					trndModel = exec.train(paramMap, fieldArray, label, algorithm.getTrainClass(), train.getTrainPercent()
							, train.getValPercent(), (tableName+"_train_source_data"), algoclass
							, trainOtherParam, trainResult, rowIdentifierCols, train.getIncludeFeatures()
							, tempTrngDfSql, tempValDfSql, encodingDetails
							, testSetDP, testSetDS, testSetTableName 
							, testSetFilePathUrl, train.getSaveTrainingSet(),trainSetDP 
							, trainSetDS, trainSetTableName
							, trainSetFilePathUrl, appUuid);
					if(testSetDP !=null) {
						dataStoreServiceImpl.setRunMode(runMode);
						dataStoreServiceImpl.create(testSetFilePathUrl, trainName,
							new MetaIdentifier(MetaType.datapod, testSetDP.getUuid(), testSetDP.getVersion()),
							new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
							trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.Append.toString(), null,
							trainResult.getValidationSet(), commonServiceImpl.getPersistMode(storageContext), null);
					 }
					if(trainSetDP !=null && train.getSaveTrainingSet().equalsIgnoreCase("Y")) {
						dataStoreServiceImpl.setRunMode(runMode);
						dataStoreServiceImpl.create(trainSetFilePathUrl, trainName,
							new MetaIdentifier(MetaType.datapod, trainSetDP.getUuid(),trainSetDP.getVersion()),
							new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
							trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.Append.toString(), null,
							trainResult.getTrainingSet(), commonServiceImpl.getPersistMode(storageContext), null);
					 }
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
											, train.getTrainPercent(), train.getValPercent(), (tableName+"_train_source_data")
											, hyperParamList.getParams(), trainOtherParam, trainResult, rowIdentifierCols, train.getIncludeFeatures()
											, tempTrngDfSql, tempValDfSql, encodingDetails
											, algoclass, testSetDP, testSetDS, testSetTableName
											, testSetFilePathUrl, train.getSaveTrainingSet(), trainSetDP, trainSetDS
											, trainSetTableName, trainSetFilePathUrl, appUuid);
									if(testSetDP !=null) {
										dataStoreServiceImpl.setRunMode(runMode);
										dataStoreServiceImpl.create(testSetFilePathUrl, trainName,
										new MetaIdentifier(MetaType.datapod, testSetDP.getUuid(), testSetDP.getVersion()),
										new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
										trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.Append.toString(), null,
										trainResult.getValidationSet(), commonServiceImpl.getPersistMode(storageContext), null);
									}
									
									if(trainSetDP !=null &&  train.getSaveTrainingSet().equalsIgnoreCase("Y")) {
										dataStoreServiceImpl.setRunMode(runMode);
										dataStoreServiceImpl.create(trainSetFilePathUrl, trainName,
											new MetaIdentifier(MetaType.datapod, trainSetDP.getUuid(),trainSetDP.getVersion()),
											new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
											trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.Append.toString(), null,
											trainResult.getTrainingSet(), commonServiceImpl.getPersistMode(storageContext), null);
									 }
								}
							}
						} else if(execParams.getParamListInfo() != null) {
								paramListHolderList = execParams.getParamListInfo();
							
							for(ParamListHolder paramListHolder : paramListHolderList) {
								MetaIdentifier hyperParamMI = paramListHolder.getRef();
								ParamList hyperParamList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(hyperParamMI.getUuid(), hyperParamMI.getVersion(), hyperParamMI.getType().toString());
								trndModel = exec.trainCrossValidation(paramMap, fieldArray, label, algorithm.getTrainClass()
										, train.getTrainPercent(), train.getValPercent(), (tableName+"_train_source_data")
										, hyperParamList.getParams(), trainOtherParam, trainResult, rowIdentifierCols, train.getIncludeFeatures()
										, tempTrngDfSql, tempValDfSql, encodingDetails
										, algoclass, testSetDP, testSetDS, testSetTableName
										, testSetFilePathUrl, train.getSaveTrainingSet(), trainSetDP, trainSetDS
										, trainSetTableName, trainSetFilePathUrl, appUuid);
								if(testSetDP != null) {
									dataStoreServiceImpl.setRunMode(runMode);
									dataStoreServiceImpl.create(testSetFilePathUrl, trainName,
									new MetaIdentifier(MetaType.datapod, testSetDP.getUuid(), testSetDP.getVersion()),
									new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
									trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.Append.toString(), null,
									trainResult.getValidationSet(), commonServiceImpl.getPersistMode(storageContext), null);
								}
								if(trainSetDP != null &&  train.getSaveTrainingSet().equalsIgnoreCase("Y")) {
									dataStoreServiceImpl.setRunMode(runMode);
									dataStoreServiceImpl.create(trainSetFilePathUrl, trainName,
										new MetaIdentifier(MetaType.datapod, trainSetDP.getUuid(),trainSetDP.getVersion()),
										new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
										trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.Append.toString(), null,
										trainResult.getTrainingSet(), commonServiceImpl.getPersistMode(storageContext), null);
								 }
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
									, train.getTrainPercent(), train.getValPercent(), (tableName+"_train_source_data")
									, hyperParamList.getParams(), trainOtherParam, trainResult, rowIdentifierCols, train.getIncludeFeatures()
									, tempTrngDfSql, tempValDfSql, encodingDetails
									, algoclass, testSetDP, testSetDS, testSetTableName
									, testSetFilePathUrl,train.getSaveTrainingSet(), trainSetDP, trainSetDS
									, trainSetTableName, trainSetFilePathUrl, appUuid);
						}
					}
				} else {
					String featureMappedSQL = modelServiceImpl.generateFeatureSQLBySource(train.getFeatureAttrMap(), source, execParams, fieldArray, label, (tableName+"_train_source_data"));
					sourceRsHolder = exec.executeAndRegisterByDatasource(featureMappedSQL, (tableName+"_train_source_data"), trainSrcDatasource, appUuid);
					sourceRsHolder.setTableName((tableName+"_train_source_data"));
					trndModel = dl4jExecutor.trainDL(execParams, fieldArray, label, algorithm.getTrainClass()
							, train.getTrainPercent(), train.getValPercent(), (tableName+"_train_source_data")
							, appUuid, algoclass, trainOtherParam);
				}
								
				result = trndModel;				
				List<String> customDirectories = exec.getCustomDirsFromTrainedModel(trndModel);

				boolean isModelSved = modelServiceImpl.save(algorithm.getModelClass(), trndModel, filePathUrl+"/model");
				String defaultDir = null;
				if (algorithm.getSavePmml().equalsIgnoreCase("Y")) {
//					if(encodingDetails == null || (encodingDetails != null && encodingDetails.isEmpty())) {
						try {
							String filePathUrl_2 = null;
							if(filePathUrl.contains(hdfsInfo.getHdfsURL()))
								filePathUrl_2 = filePathUrl.replaceAll(hdfsInfo.getHdfsURL(), "");
							defaultDir = filePathUrl_2;
							String pmmlLocation = filePathUrl_2 + "/" + train.getUuid() + "_" + train.getVersion() + "_"
									+ (filePathUrl_2.substring(filePathUrl_2.lastIndexOf("/") + 1)) + ".pmml";
							
							File pmmlFile = new File(pmmlLocation);
							if(pmmlFile.exists()) {
							FileUtils.forceDelete(pmmlFile);
							} 
							boolean isSaved = exec.savePMML(trndModel, "trainedDataSet", pmmlLocation, appUuid);
							if(isSaved) {
								logger.info("PMML saved at location: "+pmmlLocation);
							} else {
								logger.info("PMML not saved.");
							}
						 }catch (JAXBException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
//					}					
				}
				if (isModelSved) {	
					logger.info("Inside isModelSaved : trainOtherParam : " + trainOtherParam);
					defaultDir = filePathUrl.replaceAll(hdfsInfo.getHdfsURL(), "");
					if(trndModel instanceof CrossValidatorModel) {
						filePathUrl = filePathUrl+"/model" + "/bestModel" + "/stages/" + customDirectories.get(1) + "/data/";
						Map<String, Object> summary = exec.summary(trndModel, algorithm.getModelClass(), algorithm.getSummaryMethods(), appUuid);
						
						String fileName = tableName+".result";
//						if(encodingDetails == null || (encodingDetails != null && encodingDetails.isEmpty())) {
							summary = exec.calculateConfusionMatrixAndRoc(summary, (String) trainOtherParam.get("confusionMatrixTableName"),appUuid);
//						}
						
//						trainResult.setFeatureImportance(exec.featureImportance(trndModel, null));
						
						if(summary != null && !summary.isEmpty()) {
							double[] featureimportancesArr = (double[])summary.get("featureimportances");						
							if(featureimportancesArr != null) {
								List<Double> featureImportance = Arrays.asList(ArrayUtils.toObject(featureimportancesArr));
								trainResult.setFeatureImportance(featureImportance);
							}
							trainResult.setAccuracy((double) summary.get("accuracy"));
							trainResult.setRecall((double) summary.get("recall"));
							trainResult.setF1Score((double) summary.get("f1Score"));
							trainResult.setPrecision((double) summary.get("precision"));
							trainResult.setRocAUC((List<Double>)summary.get("auroc"));
							trainResult.setRocCurve((List<Map<String, Object>>)summary.get("roc"));
							
							writeSummaryToFile(summary, defaultDir, fileName);
						}
					} else if(trndModel instanceof PipelineModel) {
						filePathUrl = filePathUrl+"/model" + "/stages/" + customDirectories.get(1) + "/data/";
						Map<String, Object> summary = exec.summary(trndModel, algorithm.getModelClass(), algorithm.getSummaryMethods(), appUuid);
												
						String fileName = tableName+".result";
						
//						trainResult.setFeatureImportance(exec.featureImportance(trndModel, null));

//						if(encodingDetails == null || (encodingDetails != null && encodingDetails.isEmpty())) {
							summary = exec.calculateConfusionMatrixAndRoc(summary,(String) trainOtherParam.get("confusionMatrixTableName"),appUuid);
//						}
						double[] featureimportancesArr = (double[])summary.get("featureimportances");	
						if(featureimportancesArr != null) {
							List<Double> featureImportance = Arrays.asList(ArrayUtils.toObject(featureimportancesArr));
							trainResult.setFeatureImportance(featureImportance);
						}	
						if(summary.get("accuracy") != null) {
							trainResult.setAccuracy((double) summary.get("accuracy"));
						}
						if(summary.get("recall") != null) {
							trainResult.setRecall((double) summary.get("recall"));
						}
						if(summary.get("f1Score") != null) {
							trainResult.setF1Score((double) summary.get("f1Score"));
						}
						if(summary.get("precision") != null) {
							trainResult.setPrecision((double) summary.get("precision"));
						}
						if(summary.get("confusionMatrix") != null) {
							trainResult.setConfusionMatrix(summary.get("confusionMatrix"));
						}
						if(summary.get("auroc") != null) {
							trainResult.setRocAUC((List<Double>)summary.get("auroc"));
						}
//						trainResult.setRocAUC((summary.get("roc") == null)?null:(List<Double>) summary.get("roc"));
      					trainResult.setRocCurve((List<Map<String, Object>>)summary.get("roc"));
						
						writeSummaryToFile(summary, defaultDir, fileName);
					} else {
						filePathUrl = null;
					}
				} else {
					filePathUrl = null;
				}
				commonServiceImpl.save(MetaType.trainresult.toString(), trainResult);
				//result = exec.fetchAndTrainModel(train, model, fieldArray, algorithm, trainName, filePath, paramMap, securityServiceImpl.getAppInfo().getRef().getUuid());
				dataStoreServiceImpl.setRunMode(runMode);
				dataStoreServiceImpl.create(filePathUrl, trainName,
						new MetaIdentifier(MetaType.train, train.getUuid(), train.getVersion()),
						new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
						trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.Append.toString(), resultRef,
						trainResult.getTrainingSet(), commonServiceImpl.getPersistMode(storageContext), null);
				trainExec.setResult(resultRef);
				if (result != null)
					isSuccess = true;
				else
					isSuccess = false;
			}
			if (modelType != null && (modelType.equalsIgnoreCase(ExecContext.R.toString())
					|| modelType.equalsIgnoreCase(ExecContext.PYTHON.toString()))) {
				// Save the data as csv
				String saveFileName = commonServiceImpl.getConfigValue("framework.model.train.path")+"/csv/"+tableName;
				String modelFileName = commonServiceImpl.getConfigValue("framework.model.train.path")+"/"+model.getName();
				IExecutor exec = execFactory.getExecutor(datasource.getType());
				exec.saveDataframeAsCSV(tableName, saveFileName, appUuid);
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
				trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.FAILED);
//				if (model.getType().equalsIgnoreCase(ExecContext.R.toString())
//						|| model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//					customLogger.writeLog(this.getClass(),
//							trainExec.getStatusList().size() > 0
//									? "Latest status: "
//											+ trainExec.getStatusList().get(trainExec.getStatusList().size() - 1).getStage()
//									: "Status list is empty",
//							logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.FAILED);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Train execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Train execution FAILED.");			 		
		}		
	}
	
	private Map<String, EncodingType> getEncodingDetailsByFeatureAttrMap(List<FeatureAttrMap> featureAttrMap, List<Feature> features) {
		Map<String, EncodingType> encodingDetails = new LinkedHashMap<>();
		Map<String, Feature> featureModelMap = new HashMap<>();
		for (Feature feature : features) {
			featureModelMap.put(feature.getFeatureId(), feature);
		}
		for(FeatureAttrMap attrMap : featureAttrMap) {
			EncodingType encodingType = (featureModelMap.containsKey(attrMap.getFeature().getFeatureId()))?
					featureModelMap.get(attrMap.getFeature().getFeatureId()).getEncodingType():null;
			if (encodingType != null) {
				encodingDetails.put(attrMap.getFeature().getFeatureName(), encodingType);
			}
		}
		if(!encodingDetails.isEmpty()) {
			return encodingDetails;
		}else {
			return null;
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
