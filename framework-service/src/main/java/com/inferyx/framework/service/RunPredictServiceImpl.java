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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.PredictInput;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.enums.EncodingType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.executor.StorageContext;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.ImputeOperator;
import com.inferyx.framework.operator.PredictMLOperator;

/**
 * @author Ganesh
 *
 */
public class RunPredictServiceImpl implements Callable<TaskHolder> {
	public static Logger logger = Logger.getLogger(RunPredictServiceImpl.class); 

	private PredictExec predictExec;
	private Predict predict;
	private Model model;
	private Algorithm algorithm;
	private SessionContext sessionContext;
	private String appUuid;
	private String name;
	private HDFSInfo hdfsInfo;
	private CommonServiceImpl<?> commonServiceImpl;
	private SparkExecutor<?> sparkExecutor;
	private RunMode runMode;
	private ModelServiceImpl modelServiceImpl;
	private ModelExecServiceImpl modelExecServiceImpl;
	private ExecParams execParams;
	private ExecutorFactory execFactory;
	private ImputeOperator imputeOperator;
	private PredictMLOperator predictMLOperator;
	private ConcurrentHashMap<String, Object> trainedModelMap;
	
	/**
	 * @Ganesh
	 *
	 * @return the predictExec
	 */
	public PredictExec getPredictExec() {
		return predictExec;
	}

	/**
	 * @Ganesh
	 *
	 * @param predictExec the predictExec to set
	 */
	public void setPredictExec(PredictExec predictExec) {
		this.predictExec = predictExec;
	}

	/**
	 * @Ganesh
	 *
	 * @return the predict
	 */
	public Predict getPredict() {
		return predict;
	}

	/**
	 * @Ganesh
	 *
	 * @param predict the predict to set
	 */
	public void setPredict(Predict predict) {
		this.predict = predict;
	}

	/**
	 * @Ganesh
	 *
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @Ganesh
	 *
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @Ganesh
	 *
	 * @return the algorithm
	 */
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * @Ganesh
	 *
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
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
	 * @param sessionContext the sessionContext to set
	 */
	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 * @Ganesh
	 *
	 * @return the appUuid
	 */
	public String getAppUuid() {
		return appUuid;
	}

	/**
	 * @Ganesh
	 *
	 * @param appUuid the appUuid to set
	 */
	public void setAppUuid(String appUuid) {
		this.appUuid = appUuid;
	}

	/**
	 * @Ganesh
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @Ganesh
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @Ganesh
	 *
	 * @return the hdfsInfo
	 */
	public HDFSInfo getHdfsInfo() {
		return hdfsInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param hdfsInfo the hdfsInfo to set
	 */
	public void setHdfsInfo(HDFSInfo hdfsInfo) {
		this.hdfsInfo = hdfsInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @return the commonServiceImpl
	 */
	public CommonServiceImpl<?> getCommonServiceImpl() {
		return commonServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param commonServiceImpl the commonServiceImpl to set
	 */
	public void setCommonServiceImpl(CommonServiceImpl<?> commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the sparkExecutor
	 */
	public SparkExecutor<?> getSparkExecutor() {
		return sparkExecutor;
	}

	/**
	 * @Ganesh
	 *
	 * @param sparkExecutor the sparkExecutor to set
	 */
	public void setSparkExecutor(SparkExecutor<?> sparkExecutor) {
		this.sparkExecutor = sparkExecutor;
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
	 * @param runMode the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
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
	 * @param modelServiceImpl the modelServiceImpl to set
	 */
	public void setModelServiceImpl(ModelServiceImpl modelServiceImpl) {
		this.modelServiceImpl = modelServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the modelExecServiceImpl
	 */
	public ModelExecServiceImpl getModelExecServiceImpl() {
		return modelExecServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param modelExecServiceImpl the modelExecServiceImpl to set
	 */
	public void setModelExecServiceImpl(ModelExecServiceImpl modelExecServiceImpl) {
		this.modelExecServiceImpl = modelExecServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the execParams
	 */
	public ExecParams getExecParams() {
		return execParams;
	}

	/**
	 * @Ganesh
	 *
	 * @param execParams the execParams to set
	 */
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
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
	 * @param execFactory the execFactory to set
	 */
	public void setExecFactory(ExecutorFactory execFactory) {
		this.execFactory = execFactory;
	}

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
	 * @return the predictMLOperator
	 */
	public PredictMLOperator getPredictMLOperator() {
		return predictMLOperator;
	}

	/**
	 * @Ganesh
	 *
	 * @param predictMLOperator the predictMLOperator to set
	 */
	public void setPredictMLOperator(PredictMLOperator predictMLOperator) {
		this.predictMLOperator = predictMLOperator;
	}

	/**
	 * @Ganesh
	 *
	 * @return the trainedModelMap
	 */
	public ConcurrentHashMap<String, Object> getTrainedModelMap() {
		return trainedModelMap;
	}

	/**
	 * @Ganesh
	 *
	 * @param trainedModelMap the trainedModelMap to set
	 */
	public void setTrainedModelMap(ConcurrentHashMap<String, Object> trainedModelMap) {
		this.trainedModelMap = trainedModelMap;
	}

	@Override
	public TaskHolder call() throws Exception {
		try {
			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			execute();
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			
			throw new RuntimeException((message != null) ? message : "Predict execution FAILED.");
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()));
		return taskHolder;
	}
	
	public PredictExec execute() throws Exception {
		try {
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.RUNNING);
			
			MetaIdentifierHolder sourceHolder = predict.getSource();
			MetaIdentifierHolder targetHolder = predict.getTarget();

			Object source = (Object) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
					sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
			
			Datapod target = null;
			StorageContext storageContext = null;
			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod)) {
				target = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(),
						targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());
				storageContext = commonServiceImpl.getStorageContext(targetHolder.getRef());
			}
			else
				storageContext = StorageContext.FILE;

			ExecContext execContext = commonServiceImpl.getExecContext();
			IExecutor exec = execFactory.getExecutor(execContext.toString());
			
			String[] fieldArray = modelExecServiceImpl.getAttributeNames(predict);
			Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
			Datasource sourceDS = commonServiceImpl.getDatasourceByObject(predict);
//			IExecutor exec = execFactory.getExecutor(appDatasource.getType());

			String predictName = String.format("%s_%s_%s", predict.getUuid().replace("-", "_"), predict.getVersion(), predictExec.getVersion());
			String filePath = String.format("/%s/%s/%s", predict.getUuid(), predict.getVersion(), predictExec.getVersion());
			String tableName = String.format("%s_%s_%s", predict.getUuid().replace("-", "_"), predict.getVersion(), predictExec.getVersion());

			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), commonServiceImpl.getConfigValue("framework.model.predict.path"), filePath);

			Object result = null;
			long count = -1L;
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			
			if(model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(predict.getTrainInfo().getRef().getUuid(), predict.getTrainInfo().getRef().getVersion(), predict.getTrainInfo().getRef().getType().toString());
				Model trainModel = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), MetaType.model.toString());
				
				if(trainModel.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					TrainExec trainExec = modelExecServiceImpl.getLatestTrainExecByTrain(predict.getTrainInfo().getRef().getUuid(), predict.getTrainInfo().getRef().getVersion());
					if (trainExec == null) {
						throw new Exception("No trained model found.");
					}
					
					MetaIdentifier dataStoreMI = trainExec.getResult().getRef();
					DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(dataStoreMI.getUuid(), dataStoreMI.getVersion(), dataStoreMI.getType().toString());

					Map<String, EncodingType> encodingDetails = modelServiceImpl.getEncodingDetails(predict.getFeatureAttrMap(), model.getFeatures());
//					LinkedHashMap<String, Object> imputationDetails = imputeOperator.resolveAttributeImputeValue(predict.getFeatureAttrMap(), source, model, execParams, runMode, (tableName+"_train_source_data"));
					
					
					List<String> argList = new ArrayList<>();
					PredictInput predictInput = new PredictInput();
					Map<String, Object> otherParams = new HashMap<>();

					predictInput.setEncodingDetails(encodingDetails);
//					predictInput.setImputeDetails(imputationDetails);					
					
					predictInput.setIncludeFeatures(predict.getIncludeFeatures());
					
					String mappedFeatureAttrSql = modelServiceImpl.generateFeatureSQLBySource(predict.getFeatureAttrMap(), source, execParams, fieldArray, null, tableName);	

//					final String URI = commonServiceImpl.getConfigValue("framework.hdfs.URI");
					String defaultDir = commonServiceImpl.getConfigValue("framework.model.predict.path")+filePath+"/";
					String modelFileName = dataStore.getLocation();
					String savePredict = commonServiceImpl.getConfigValue("framework.model.predict.path")+filePath+"/"+"output";
					filePathUrl = savePredict;

					modelServiceImpl.deleteFileOrDirIfExists(defaultDir);
					
					logger.info("Default dir name : " + defaultDir);
					logger.info("Model file name : " + modelFileName);
					logger.info("Saved predict file name : " + savePredict);
										
					String sourceQuery = modelServiceImpl.generateSQLBySource(source, execParams);
					String sourceCustomQuery = null;
					List<String> rowIdentifierCols = null;
					if(predict.getRowIdentifier() != null && !predict.getRowIdentifier().isEmpty()) {
						rowIdentifierCols = modelServiceImpl.getRowIdentifierCols(predict.getRowIdentifier());
						if(predict.getIncludeFeatures().equalsIgnoreCase("Y")
								&& rowIdentifierCols != null 
								&& !rowIdentifierCols.isEmpty()) {
							rowIdentifierCols = modelServiceImpl.removeDuplicateColNames(fieldArray, rowIdentifierCols);
						}
						
						if(rowIdentifierCols != null && !rowIdentifierCols.isEmpty()) {				
							StringBuilder builder = new StringBuilder();
							builder.append("SELECT ");
							int i=0;
							for(String colName : rowIdentifierCols) {
								builder.append(colName);
								if(i<rowIdentifierCols.size()-1) {
									builder.append(", ");
								}
								i++;
							}
							
							builder.append(" FROM ");
							builder.append(" (");
							builder.append(sourceQuery);
							builder.append(") ");
							builder.append(" sourceUniqueData");
							sourceCustomQuery = builder.toString();
						} else {
							rowIdentifierCols = null;
						}
						
						
						predictInput.setRowIdentifier(rowIdentifierCols);
						Datapod sourceDp = commonServiceImpl.getDatapodByObject(source);
						Map<String, String> colDetails = modelServiceImpl.getColumnNameAndDatatypeByAttrList(sourceDp.getAttributes());
						otherParams.put("sourceAttrDetails", colDetails);
					}
										
					String sourceDsType = sourceDS.getType().toLowerCase();
					predictInput.setSourceDsType(sourceDsType);
					
					exec.executeAndRegisterByDatasource(sourceQuery, tableName, sourceDS, appUuid);	
					LinkedHashMap<String, Object> imputationDetails = imputeOperator.resolveAttributeImputeValue(predict.getFeatureAttrMap(), source, model, execParams, runMode, tableName);					
					LinkedHashMap<String, Object> remappedImputationDetails = modelServiceImpl.remapSourceImpueValToFeature(source, predict.getFeatureAttrMap(), model.getFeatures(), imputationDetails);
					predictInput.setImputationDetails(remappedImputationDetails);
					
					if(sourceDsType.equalsIgnoreCase(ExecContext.FILE.toString())) {
						sourceDsType = appDatasource.getType().toLowerCase();
						String saveFileName = commonServiceImpl.getConfigValue("framework.model.predict.path")+filePath+"/"+"input";
						
						File saveFile = new File(saveFileName);
						if(!saveFile.exists()) {
							String mappedAttrSql = modelServiceImpl.generateFeatureSQLByTempTable(predict.getFeatureAttrMap(), tableName, null, tableName);
							ResultSetHolder rsHolder = exec.executeAndRegisterByDatasource(mappedAttrSql, tableName, appDatasource, appUuid);	
							
							if(encodingDetails == null || (encodingDetails != null && encodingDetails.isEmpty())) {
								String doubleCastSql = "SELECT * FROM " + tableName;
								rsHolder = sparkExecutor.castDFCloumnsToDoubleType(null, doubleCastSql, sourceDS, tableName, true, appUuid);	
							}
							
//							exec.saveDataframeAsCSV(tableName, saveFileName, appUuid);
							sparkExecutor.registerAndPersistDataframe(rsHolder, null, SaveMode.APPEND.toString()
									, "file://"+saveFileName, tableName
									, "true", true);
							saveFileName = modelServiceImpl.renameFileAndGetFilePathFromDir(saveFileName, "input_data", FileType.PARQUET.toString().toLowerCase());
//							saveFileName = saveFileName;
							logger.info("Saved file name : " + saveFileName);
						}						
						
						String inputSourceFileName = null;
						if(predict.getRowIdentifier() != null 
								&& !predict.getRowIdentifier().isEmpty()
								&& rowIdentifierCols != null 
								&& !rowIdentifierCols.isEmpty()) {
							inputSourceFileName = commonServiceImpl.getConfigValue("framework.model.predict.path")+filePath+"/"+"input_source";
							logger.info("Saved source file name : " + inputSourceFileName);
							
							File inputSourceFile = new File(inputSourceFileName);
							if(!inputSourceFile.exists()) {
								ResultSetHolder rsHolder = exec.executeAndRegisterByDatasource(sourceCustomQuery, tableName.concat("_sourceQuery"), sourceDS, appUuid);
//								exec.saveDataframeAsCSV(tableName.concat("_sourceQuery"), "file://"+inputSourceFileName, appUuid);
								sparkExecutor.registerAndPersistDataframe(rsHolder, null, SaveMode.APPEND.toString()
										, "file://"+inputSourceFileName, tableName.concat("_sourceQuery")
										, "true", true);
								String inputSourceFilePath = modelServiceImpl.renameFileAndGetFilePathFromDir(inputSourceFileName, "input_source_data", FileType.PARQUET.toString().toLowerCase());
								otherParams.put("inputSourceFileName", "file://"+inputSourceFilePath);
							}		
						}

						predictInput.setSourceFilePath(saveFileName);
					} else {
						predictInput.setQuery(mappedFeatureAttrSql);
						
						java.util.Map<String, Object> sourceDsDetails = new HashMap<>();						
						sourceDsDetails.put("sourceHostName", sourceDS.getHost());
						sourceDsDetails.put("sourceDbName", sourceDS.getDbname());
						if(sourceDsType.equalsIgnoreCase(ExecContext.ORACLE.toString())) {
							sourceDsDetails.put("sourceDbName", sourceDS.getSid());						
						} else {
							sourceDsDetails.put("sourceDbName", sourceDS.getDbname());
						}
						sourceDsDetails.put("sourcePort", sourceDS.getPort());
						sourceDsDetails.put("sourceUserName", sourceDS.getUsername());
						sourceDsDetails.put("sourcePassword", sourceDS.getPassword());
						
						predictInput.setSourceDsDetails(sourceDsDetails);
						
						if(train.getRowIdentifier() != null && !train.getRowIdentifier().isEmpty()) {
							otherParams.put("sourceQuery", sourceCustomQuery);
						}
						//creating default directory to allow script/python-code to store file in this location
						File defaultDirFile = new File(defaultDir);
						if(!defaultDirFile.exists()) {
							defaultDirFile.mkdirs();
						}
					}
					
					String scriptName = algorithm.getScriptName();
					
					MetaIdentifier targetMI = predict.getTarget().getRef();
					if(targetMI.getType().equals(MetaType.datapod)) {
						Datapod targetDP = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetMI.getUuid(), targetMI.getVersion(), targetMI.getType().toString());
						Datasource targetDpDs = commonServiceImpl.getDatasourceByDatapod(targetDP);
						String targetDsType = targetDpDs.getType();
						predictInput.setTargetDsType(targetDsType.toLowerCase());
						
						java.util.Map<String, Object> targetDsDetails = new HashMap<>();
						targetDsDetails.put("targetHostName", targetDpDs.getHost());
						if(targetDsType.equalsIgnoreCase(ExecContext.ORACLE.toString())) {
							targetDsDetails.put("targetDbName", targetDpDs.getSid());						
						} else {
							targetDsDetails.put("targetDbName", targetDpDs.getDbname());
						}
						targetDsDetails.put("targetPort", targetDpDs.getPort());
						targetDsDetails.put("targetUserName", targetDpDs.getUsername());
						targetDsDetails.put("targetPassword", targetDpDs.getPassword());
						targetDsDetails.put("targetDriver", targetDpDs.getDriver());
						
						predictInput.setTargetDsDetails(targetDsDetails);
						predictInput.setTargetTableName(targetDP.getName());
						predictInput.setUrl(Helper.genUrlByDatasource(targetDpDs));
					} else if(targetMI.getType().equals(MetaType.file)) {
						predictInput.setTargetPath(savePredict);
						predictInput.setTargetDsType(MetaType.file.toString().toLowerCase());
					}					
					
					predictInput.setOtherParams(otherParams);
					
					predictInput.setNumInput(fieldArray.length);
					predictInput.setModelFilePath(modelFileName);
					predictInput.setOperation(MetaType.predict.toString().toLowerCase());

					String inputConfigFilePath = defaultDir.concat("input_config.json");
					ObjectMapper mapper = new ObjectMapper();
					mapper.writeValue(new File(defaultDir.concat("input_config.json")), predictInput);
					
					argList.add("inputConfigFilePath");
					argList.add(inputConfigFilePath);

					logger.info("Object PredictInput: "+predictInput.toString());
					result = modelServiceImpl.executeScript(model.getType(), scriptName, trainExec.getUuid(), trainExec.getVersion(), argList);
					List<String> tempTableList = new ArrayList<>();
					tempTableList.add(tableName);
					sparkExecutor.dropTempTable(tempTableList);
				} else {
					throw new Exception("Model type has been changed from \'"+model.getType().toUpperCase()+"\' to \'"+trainModel.getType().toUpperCase()+"\'.");
				}
			} else {					
				if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
					logger.info("Model depends on formula");
					//getting data from source
					String sql = modelServiceImpl.generateSQLBySource(source, execParams);
					ResultSetHolder rsHolder = exec.executeAndRegisterByDatasource(sql, (tableName+"_pred_data"), sourceDS, appUuid);
//					rsHolder = exec.replaceNullValByDoubleValFromDF(rsHolder, null, sourceDS, (tableName+"_pred_data"), true, appUuid);
					
					String predictQuery = predictMLOperator.generateSql(predict, (tableName+"_pred_data"));				
					if(predict.getTarget().getRef().getType().equals(MetaType.datapod)) {
						Datasource targetDatasource = commonServiceImpl.getDatasourceByObject(target);
						if(appDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())
								&& !targetDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
							rsHolder = exec.executeSqlByDatasource(predictQuery, sourceDS, appUuid);
							if(targetDatasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
								tableName = targetDatasource.getSid().concat(".").concat(target.getName());
							} else {
								tableName = targetDatasource.getDbname().concat(".").concat(target.getName());					
							}
							rsHolder.setTableName(tableName);
							rsHolder = exec.persistDataframe(rsHolder, targetDatasource, target, SaveMode.APPEND.toString());
						} else {
							rsHolder = exec.executeRegisterAndPersist(predictQuery, (tableName+"_pred_data"), filePathUrl, target, SaveMode.APPEND.toString(), false, appUuid);
						}																
					} else {
						rsHolder = exec.executeRegisterAndPersist(predictQuery, (tableName+"_pred_data"), filePathUrl, null, SaveMode.APPEND.toString(), false, appUuid);
						result = rsHolder;					
						count = rsHolder.getCountRows();
					}
					
					//generating datastore for datapod
					result = rsHolder;					
					count = rsHolder.getCountRows();
					modelServiceImpl.createDatastore(filePathUrl, predict.getName(), 
							new MetaIdentifier(MetaType.datapod, target.getUuid(), target.getVersion()), 
							new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()),
							predictExec.getAppInfo(), predictExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
							commonServiceImpl.getPersistMode(storageContext),runMode);	
//							Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
					
					List<String> tempTableList = new ArrayList<>();
					tempTableList.add((tableName+"_pred_data"));
					sparkExecutor.dropTempTable(tempTableList);
				} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
					logger.info("Model depends on algorithm");
					TrainExec trainExec = modelExecServiceImpl.getLatestTrainExecByTrain(predict.getTrainInfo().getRef().getUuid(), predict.getTrainInfo().getRef().getVersion());
					if (trainExec == null) {
						throw new Exception("No trained model found.");
					}

					boolean isResultSaved = false;
					
//					rsHolder = exec.replaceNullValByDoubleValFromDF(rsHolder, null, sourceDS, (tableName+"_pred_data"), true, appUuid);
					fieldArray = modelServiceImpl.getMappedAttrs(predict.getFeatureAttrMap());
					
					//getting data from source
					String sourceSql = modelServiceImpl.generateSQLBySource(source, execParams);
					ResultSetHolder rsHolder = exec.executeAndRegisterByDatasource(sourceSql, (tableName+"_pred_data"), sourceDS, appUuid);					
					count = rsHolder.getCountRows();

					//finding impute values of attribute
					LinkedHashMap<String, Object> resolvedimputeAttributeValues = imputeOperator.resolveAttributeImputeValue(predict.getFeatureAttrMap(), source, model, execParams, runMode, (tableName+"_pred_data"));
					
					//applying imputation valued per column to data
					rsHolder = exec.applyAttrImputeValuesToData(rsHolder, resolvedimputeAttributeValues, true, (tableName+"_pred_data"));

					//getting data having only feature columns
					String mappedFeatureAttrSql = modelServiceImpl.generateFeatureSQLByTempTable(predict.getFeatureAttrMap(), (tableName+"_pred_data"), null, (tableName+"_pred_mapped_data"));
					rsHolder = sparkExecutor.readTempTable(mappedFeatureAttrSql, appUuid);
					sparkExecutor.registerTempTable(rsHolder.getDataFrame(), (tableName+"_pred_mapped_data"));	

					Map<String, EncodingType> encodingDetails = modelServiceImpl.getEncodingDetails(predict.getFeatureAttrMap(), model.getFeatures());
					if(encodingDetails != null && !encodingDetails.isEmpty()) {
//						rsHolder = sparkExecutor.preparePredictDfForEncoding(rsHolder, encodingDetails, true, (tableName+"_pred_assembled_data"));
					}
					//assembling the data to for feature vector
//					exec.assembleDF(fieldArray, rsHolder, null, (tableName+"_pred_assembled_data"), sourceDS, true, appUuid);
					
					exec.registerDataFrameAsTable(rsHolder, tableName+"_pred_assembled_data");
					
					String key = String.format("%s_%s", model.getUuid().replaceAll("-", "_"), model.getVersion());
					Object trainedModel = null;
					if(trainedModelMap.get(key) != null) {
						trainedModel = trainedModelMap.get(key);
					} else {
						trainedModel = modelServiceImpl.getTrainedModelByTrainExec(algorithm.getModelClass(), trainExec);
						trainedModelMap.put(key, trainedModel);
					}
					//prediction operation
					logger.info("Trained Model class " + trainedModel.getClass().getName());
					rsHolder =  exec.predict(trainedModel, target, filePathUrl, (tableName+"_pred_assembled_data"), appUuid, encodingDetails);
					logger.info("After predict");
					
					List<String> rowIdentifierCols = modelServiceImpl.getRowIdentifierCols(predict.getRowIdentifier());
					if(predict.getTarget().getRef().getType().equals(MetaType.datapod)) {
						Datasource targetDatasource = commonServiceImpl.getDatasourceByObject(target);
						
					//writing into table
					String targetTableName = null;
					if(targetDatasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
						targetTableName = targetDatasource.getSid().concat(".").concat(target.getName());
					} else {
						targetTableName = targetDatasource.getDbname().concat(".").concat(target.getName());					
					}
					logger.info("Read dataframes : ");
					logger.info("Predicted DF");
					sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_assembled_data"), appUuid).getDataFrame().show();
					logger.info("feature DF");
					sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_mapped_data"), appUuid).getDataFrame().show();
					logger.info("source DF");
					sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_data"), appUuid).getDataFrame().show();
//					if(encodingDetails == null || (encodingDetails != null && encodingDetails.isEmpty())) {
						isResultSaved = sparkExecutor.savePredictionResult(sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_assembled_data"), appUuid).getDataFrame()
								, sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_mapped_data"), appUuid).getDataFrame()
								, sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_data"), appUuid).getDataFrame()
								, filePathUrl, rowIdentifierCols, predict.getIncludeFeatures(), fieldArray, algorithm.getTrainClass()
								, target, targetDatasource, targetTableName, SaveMode.APPEND.toString());		
//					}
						//generating datastore for datapod
//						count = rsHolder.getCountRows();
						modelServiceImpl.createDatastore(filePathUrl, predict.getName(), 
								new MetaIdentifier(MetaType.datapod, target.getUuid(), target.getVersion()), 
								new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()),
								predictExec.getAppInfo(), predictExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
								commonServiceImpl.getPersistMode(storageContext), runMode);		
					} else {
						//writing into file
						logger.info("Read dataframes : ");
						logger.info("Predicted DF");
						sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_assembled_data"), appUuid).getDataFrame().show();
						logger.info("feature DF");
						sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_mapped_data"), appUuid).getDataFrame().show();
						logger.info("source DF");
						sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_data"), appUuid).getDataFrame().show();
//						if(encodingDetails == null || (encodingDetails != null && encodingDetails.isEmpty())) {
							isResultSaved = sparkExecutor.savePredictionResult(sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_assembled_data"), appUuid).getDataFrame()
									, sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_mapped_data"), appUuid).getDataFrame()
									, sparkExecutor.readTempTable("SELECT * FROM "+(tableName+"_pred_data"), appUuid).getDataFrame()
									, filePathUrl, rowIdentifierCols, predict.getIncludeFeatures(), fieldArray, algorithm.getTrainClass()
									, null, null, null, null);
//						}
					}
					logger.info("After writing prediction results");
					//dropping temp table(s)
					List<String> tempTableList = new ArrayList<>();
					tempTableList.add((tableName+"_pred_data"));
					tempTableList.add((tableName+"_pred_mapped_data"));
					tempTableList.add((tableName+"_pred_assembled_data"));
					sparkExecutor.dropTempTable(tempTableList);
					
					if(isResultSaved) {
						result = isResultSaved;
					} else {
						result = null;
					}
				}
			}

			modelServiceImpl.createDatastore(filePathUrl, predictName,
					new MetaIdentifier(MetaType.predict, predict.getUuid(), predict.getVersion()),
					new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()),
					predictExec.getAppInfo(), predictExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
					commonServiceImpl.getPersistMode(storageContext), runMode);
			logger.info("After create Datastore");

			predictExec.setLocation(filePathUrl);
			predictExec.setResult(resultRef);
			commonServiceImpl.save(MetaType.predictExec.toString(), predictExec);
			logger.info("After saving predictExec");
			if (result != null) {
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.COMPLETED);
			}else {
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.FAILED);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Predict execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Predict execution FAILED.");
		}
		
		return predictExec;
	}
}
