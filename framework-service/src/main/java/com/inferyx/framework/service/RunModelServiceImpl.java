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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.SaveMode;

import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.enums.RunMode;
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
	private SparkContext sparkContext;
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
	private String name;
	private MetaType execType;

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

	public SparkContext getSparkContext() {
		return sparkContext;
	}

	public void setSparkContext(SparkContext sparkContext) {
		this.sparkContext = sparkContext;
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
	
	@SuppressWarnings({ "unused" })
	public void execute() throws Exception {
		try {
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
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
			if (!modelType.equalsIgnoreCase(ExecContext.R.toString())
					&& !modelType.equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				Algorithm algorithm = null;
				if (algorithmVersion != null) {
					algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(algorithmUUID, algorithmVersion,
							MetaType.algorithm.toString());
				} else {
					algorithm = (Algorithm) commonServiceImpl.getLatestByUuid(algorithmUUID, MetaType.algorithm.toString());
				}


				String[] fieldArray = modelExecServiceImpl.getAttributeNames(train);
				String trainName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), trainExec.getVersion());
				String filePath = "/train"+String.format("/%s/%s/%s", model.getUuid().replace("-", "_"), model.getVersion(), trainExec.getVersion());
				String tableName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), trainExec.getVersion());

				String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);

				Datasource datasource = commonServiceImpl.getDatasourceByApp();
				IExecutor exec = execFactory.getExecutor(datasource.getType());

				String appUuid = commonServiceImpl.getApp().getUuid();
				
				Object source = (Object) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
						train.getSource().getRef().getVersion(), train.getSource().getRef().getType().toString());
				String sql = modelServiceImpl.generateSQLBySource(source);
				exec.executeAndRegister(sql, (tableName+"_train_data"), appUuid);
				
				//Object va = exec.assembleDF(fieldArray, (tableName+"_train_data"), algorithm.getTrainName(), model.getLabel(), appUuid);
				
				PipelineModel trngModel = exec.trainModel(paramMap, fieldArray, model.getLabel(), algorithm.getTrainName(), train.getTrainPercent(), train.getValPercent(), (tableName+"_train_data"), appUuid);
				result = trngModel;
				
				List<String> customDirectories = new ArrayList<>();
				Transformer[] transformers = trngModel.stages();
				for (int i = 0; i < transformers.length; i++) {
					customDirectories.add(i + "_" + transformers[i].uid());
				}

				boolean isModelSved = modelServiceImpl.save(algorithm.getModelName(), trngModel, sparkContext, filePathUrl);
				if (algorithm.getSavePmml().equalsIgnoreCase("Y")) {
					try {
						String filePathUrl_2 = null;
						if(filePathUrl.contains(hdfsInfo.getHdfsURL()))
							filePathUrl_2 = filePathUrl.replaceAll(hdfsInfo.getHdfsURL(), "");
						String pmmlLocation = filePathUrl_2 + "/" + model.getUuid() + "_" + model.getVersion() + "_"
								+ (filePathUrl_2.substring(filePathUrl_2.lastIndexOf("/") + 1)) + ".pmml";
						boolean isSaved = exec.savePMML(trngModel, "trainedDataSet", pmmlLocation, appUuid);
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
				if (isModelSved)
					filePathUrl = filePathUrl + "/stages/" + customDirectories.get(1) + "/data/";
				else
					filePathUrl = null;
				
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
				isSuccess = modelServiceImpl.executeScript(model.getType(), model.getScriptName(), trainExec.getUuid(),
						trainExec.getVersion(), null);
				// customLogger.writeLog(this.getClass(), "Script executed ....", logPath);
			}

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
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Train execution failed.");
			throw new RuntimeException((message != null) ? message : "Train execution failed.");			 		
		}		
	}
}
