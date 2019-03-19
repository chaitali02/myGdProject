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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.datascience.MonteCarloSimulation;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.enums.SimulationType;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.GenerateDataOperator;
import com.inferyx.framework.operator.SimulateMLOperator;

/**
 * @author Ganesh
 *
 */
public class RunSimulateServiceImpl implements Callable<TaskHolder> {
	public static Logger logger = Logger.getLogger(RunSimulateServiceImpl.class); 
	
	private SimulateExec simulateExec;
	private Simulate simulate;
	private Model model;
	private SessionContext sessionContext;
	private String appUuid;
	private String name;
	private HDFSInfo hdfsInfo;
	private CommonServiceImpl<?> commonServiceImpl;
	private SparkExecutor<?> sparkExecutor;
	private RunMode runMode;
	private ModelServiceImpl modelServiceImpl;
	private ModelExecServiceImpl modelExecServiceImpl;
	private DatapodServiceImpl datapodServiceImpl;
	private ExecParams execParams;
	private ExecutorFactory execFactory;
	private Distribution distribution;
	private MonteCarloSimulation monteCarloSimulation;
	private GenerateDataOperator generateDataOperator;
	private SimulateMLOperator simulateMLOperator; 	
	
	/**
	 * @Ganesh
	 *
	 * @return the simulateExec
	 */
	public SimulateExec getSimulateExec() {
		return simulateExec;
	}

	/**
	 * @Ganesh
	 *
	 * @param simulateExec the simulateExec to set
	 */
	public void setSimulateExec(SimulateExec simulateExec) {
		this.simulateExec = simulateExec;
	}

	/**
	 * @Ganesh
	 *
	 * @return the simulate
	 */
	public Simulate getSimulate() {
		return simulate;
	}

	/**
	 * @Ganesh
	 *
	 * @param simulate the simulate to set
	 */
	public void setSimulate(Simulate simulate) {
		this.simulate = simulate;
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
	 * @return the datapodServiceImpl
	 */
	public DatapodServiceImpl getDatapodServiceImpl() {
		return datapodServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param datapodServiceImpl the datapodServiceImpl to set
	 */
	public void setDatapodServiceImpl(DatapodServiceImpl datapodServiceImpl) {
		this.datapodServiceImpl = datapodServiceImpl;
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
	 * @return the distribution
	 */
	public Distribution getDistribution() {
		return distribution;
	}

	/**
	 * @Ganesh
	 *
	 * @param distribution the distribution to set
	 */
	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

	/**
	 * @Ganesh
	 *
	 * @return the monteCarloSimulation
	 */
	public MonteCarloSimulation getMonteCarloSimulation() {
		return monteCarloSimulation;
	}

	/**
	 * @Ganesh
	 *
	 * @param monteCarloSimulation the monteCarloSimulation to set
	 */
	public void setMonteCarloSimulation(MonteCarloSimulation monteCarloSimulation) {
		this.monteCarloSimulation = monteCarloSimulation;
	}

	/**
	 * @Ganesh
	 *
	 * @return the generateDataOperator
	 */
	public GenerateDataOperator getGenerateDataOperator() {
		return generateDataOperator;
	}

	/**
	 * @Ganesh
	 *
	 * @param generateDataOperator the generateDataOperator to set
	 */
	public void setGenerateDataOperator(GenerateDataOperator generateDataOperator) {
		this.generateDataOperator = generateDataOperator;
	}

	/**
	 * @Ganesh
	 *
	 * @return the simulateMLOperator
	 */
	public SimulateMLOperator getSimulateMLOperator() {
		return simulateMLOperator;
	}

	/**
	 * @Ganesh
	 *
	 * @param simulateMLOperator the simulateMLOperator to set
	 */
	public void setSimulateMLOperator(SimulateMLOperator simulateMLOperator) {
		this.simulateMLOperator = simulateMLOperator;
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
			
			throw new RuntimeException((message != null) ? message : "Simulate execution FAILED.");
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()));
		return taskHolder;
	}
	
	public SimulateExec execute() throws Exception {
		try {
			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec,
					Status.Stage.RUNNING);

			MetaIdentifierHolder targetHolder = simulate.getTarget();
			Datapod targetDp = null;
			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod))
				targetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(),
						targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());

			String modelName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(),
					simulateExec.getVersion());
			String filePath = String.format("/%s/%s/%s", model.getUuid().replace("-", "_"), model.getVersion(),
					simulateExec.getVersion());
			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(),
					Helper.getPropertyValue("framework.model.simulate.path"), filePath);

			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			Object result = null;
			String[] fieldArray = modelExecServiceImpl.getAttributeNames(simulate);

			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());

			ExecParams distExecParam = new ExecParams();
			ExecParams simExecParam = new ExecParams();

			List<ParamListHolder> distParamHolderList = new ArrayList<>();
			List<ParamListHolder> simParamHolderList = new ArrayList<>();

			String tableName = null;
			List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
			for (ParamListHolder holder : paramListInfo) {
				if (simulate.getParamList() != null
						&& holder.getRef().getUuid().equalsIgnoreCase(simulate.getParamList().getRef().getUuid())) {
					simParamHolderList.add(holder);
				} else if (holder.getRef().getUuid().equalsIgnoreCase(distribution.getParamList().getRef().getUuid())) {
					distParamHolderList.add(holder);
				}
				if (holder.getParamName().equalsIgnoreCase("saveLocation")) {
					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
							holder.getParamValue().getRef().getUuid(), holder.getParamValue().getRef().getVersion(),
							holder.getParamValue().getRef().getType().toString());
					tableName = datapodServiceImpl.genTableNameByDatapod(datapod, simulateExec.getVersion(), null, null, null, runMode, false);
				}
			}
			distExecParam.setParamListInfo(distParamHolderList);
			simExecParam.setParamListInfo(simParamHolderList);

			/*
			 * New ParamListHolder for distribution
			 */
			ParamListHolder distributionInfo = new ParamListHolder();
			distributionInfo.setParamId("0");
			distributionInfo.setParamName("distribution");
			distributionInfo.setParamType("distribution");
			MetaIdentifier distIdentifier = new MetaIdentifier(MetaType.distribution, distribution.getUuid(),
					distribution.getVersion());
			MetaIdentifierHolder distHolder = new MetaIdentifierHolder(distIdentifier);
			distributionInfo.setParamValue(distHolder);
			distributionInfo.setRef(new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()));

			/*
			 * New ParamListHolder for numIterations
			 */
			ParamListHolder numIterationsInfo = new ParamListHolder();
			numIterationsInfo.setParamId("1");
			numIterationsInfo.setParamName("numIterations");
			distributionInfo.setParamType("integer");
			MetaIdentifierHolder numIterHolder = new MetaIdentifierHolder(null, "" + simulate.getNumIterations());
			numIterationsInfo.setParamValue(numIterHolder);
			numIterationsInfo.setRef(new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()));

			List<ParamListHolder> paramListInfo2 = execParams.getParamListInfo();
			paramListInfo2.add(distributionInfo);
			paramListInfo2.add(numIterationsInfo);
			execParams.setParamListInfo(paramListInfo2);

			long count = 0;
			if (simulate.getType().equalsIgnoreCase(SimulationType.MONTECARLO.toString())) {
				result = monteCarloSimulation.simulateMonteCarlo(simulate, simExecParam, distExecParam, filePathUrl);
			} else if (simulate.getType().equalsIgnoreCase(SimulationType.DEFAULT.toString())) {
				if (model.getDependsOn().getRef().getType().equals(MetaType.formula)) {

					HashMap<String, String> otherParams = execParams.getOtherParams();
					if (otherParams == null)
						otherParams = new HashMap<>();
					otherParams = (HashMap<String, String>) generateDataOperator.customCreate(simulateExec, execParams,
							runMode);

					// tableName = generateDataOperator.execute(null, execParams, new
					// MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(),
					// simulateExec.getVersion()), null, otherParams, null, runMode);

					String tabName_2 = null;
					String tableName_3 = null;
					if (distribution.getClassName().contains("UniformRealDistribution")) {
						List<Feature> features = model.getFeatures();
						for (int i = 0; i < fieldArray.length; i++) {
							List<ParamListHolder> paramListHolderes = distExecParam.getParamListInfo();
							Feature feature = features.get(i);
							for (ParamListHolder holder : paramListHolderes) {
								if (holder.getParamName().equalsIgnoreCase("upper")) {
									holder.getParamValue().setValue("" + feature.getMaxVal());
								}
								if (holder.getParamName().equalsIgnoreCase("lower")) {
									holder.getParamValue().setValue("" + feature.getMinVal());
								}
							}
//							
							tableName = generateDataOperator.execute(simulateExec, execParams, runMode);
//							String[] customFldArr = new String[] {fieldArray[i]};
//							tabName_2 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);
							tabName_2 = exec.renameColumn(tableName, 1, fieldArray[i], appUuid);
							String sql = simulateMLOperator.generateSql(simulate, tabName_2);
							result = exec.executeAndRegister(sql, tabName_2, appUuid);// (sql, tabName_2, filePath,
																						// null,
																						// SaveMode.Append.toString(),
																						// appUuid);

							if (i == 0)
								tableName_3 = tabName_2;
							if (i > 0)
								tableName_3 = exec.joinDf(tableName_3, tabName_2, i, appUuid);
						}

						String sql = "SELECT * FROM " + tableName_3;
						if (simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath,
									targetDp, SaveMode.APPEND.toString(), true, appUuid);
							result = rsHolder;
							count = rsHolder.getCountRows();
							modelServiceImpl.createDatastore(filePath, simulate.getName(),
									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()),
									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(),
											simulateExec.getVersion()),
									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(),
									resultRef, count, Helper.getPersistModeFromRunMode(runMode.toString()), runMode);
						}

						tableName_3 = exec.assembleRandomDF(fieldArray, tableName_3, false, appUuid);
					} else {
						tableName = generateDataOperator.execute(simulateExec, execParams, runMode);

						String sql = "SELECT * FROM " + tableName;
						tableName_3 = tableName;
						if (simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath,
									targetDp, SaveMode.APPEND.toString(), true, appUuid);
							result = rsHolder;
							count = rsHolder.getCountRows();
							modelServiceImpl.createDatastore(filePath, simulate.getName(),
									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()),
									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(),
											simulateExec.getVersion()),
									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(),
									resultRef, count, Helper.getPersistModeFromRunMode(runMode.toString()), runMode);
						}

						String[] customFldArr = new String[] { fieldArray[0] };
						tableName_3 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);
					}

					String sql = "SELECT * FROM " + tableName_3;
					ResultSetHolder rsHolder = sparkExecutor.writeResult(sql, null, filePathUrl, null,
							SaveMode.APPEND.toString(), tableName_3, "false", appUuid);
//					ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePathUrl, targetDp, SaveMode.Append.toString(), false, appUuid);	
					result = rsHolder;
					count = rsHolder.getCountRows();
				} else if (model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {

					HashMap<String, String> otherParams = execParams.getOtherParams();
					if (otherParams == null)
						otherParams = new HashMap<>();
					otherParams = (HashMap<String, String>) generateDataOperator.customCreate(simulateExec, execParams,
							runMode);

//					tableName = generateDataOperator.execute(null, execParams, new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()), null, otherParams, null, runMode);

					String tabName_2 = null;
					String tableName_3 = null;
					if (distribution.getClassName().contains("UniformRealDistribution")) {
						List<Feature> features = model.getFeatures();
						for (int i = 0; i < fieldArray.length; i++) {
							List<ParamListHolder> paramListHolderes = distExecParam.getParamListInfo();
							Feature feature = features.get(i);
							for (ParamListHolder holder : paramListHolderes) {
								if (holder.getParamName().equalsIgnoreCase("upper")) {
									holder.getParamValue().setValue("" + feature.getMaxVal());
								}
								if (holder.getParamName().equalsIgnoreCase("lower")) {
									holder.getParamValue().setValue("" + feature.getMinVal());
								}
							}

							tableName = generateDataOperator.execute(simulateExec, execParams, runMode);
//							String[] customFldArr = new String[] {fieldArray[i]};
//							tabName_2 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);
							tabName_2 = exec.renameColumn(tableName, 1, fieldArray[i], appUuid);
							if (i == 0)
								tableName_3 = tabName_2;
							if (i > 0)
								tableName_3 = exec.joinDf(tableName_3, tabName_2, i, appUuid);
						}

						String sql = "SELECT * FROM " + tableName_3;
						if (simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath,
									targetDp, SaveMode.APPEND.toString(), true, appUuid);
							result = rsHolder;
							count = rsHolder.getCountRows();
							modelServiceImpl.createDatastore(filePath, simulate.getName(),
									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()),
									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(),
											simulateExec.getVersion()),
									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(),
									resultRef, count, Helper.getPersistModeFromRunMode(runMode.toString()), runMode);
						}

						tableName_3 = exec.assembleRandomDF(fieldArray, tableName_3, false, appUuid);
					} else {
						tableName = generateDataOperator.execute(simulateExec, execParams, runMode);

						String sql = "SELECT * FROM " + tableName;
						tableName_3 = tableName;
						if (simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath,
									targetDp, SaveMode.APPEND.toString(), true, appUuid);
							result = rsHolder;
							count = rsHolder.getCountRows();
							modelServiceImpl.createDatastore(filePath, simulate.getName(),
									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()),
									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(),
											simulateExec.getVersion()),
									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(),
									resultRef, count, Helper.getPersistModeFromRunMode(runMode.toString()), runMode);
						}

						String[] customFldArr = new String[] { fieldArray[0] };
						tableName_3 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);
					}

					String sql = "SELECT * FROM " + tableName_3;
					ResultSetHolder rsHolder = sparkExecutor.writeResult(sql, null, filePathUrl, null,
							SaveMode.APPEND.toString(), tableName_3, null, appUuid);
//					ResultSetHolder rsHolder = exec.cczexecuteRegisterAndPersist(sql, tableName_3, filePathUrl, targetDp, SaveMode.Append.toString(), false, appUuid);	
					result = rsHolder;
					count = rsHolder.getCountRows();
				}
			}

			modelServiceImpl.createDatastore(filePathUrl, modelName,
					new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()),
					new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()),
					simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef,
					count, Helper.getPersistModeFromRunMode(runMode.toString()), runMode);

			simulateExec.setLocation(filePathUrl);
			simulateExec.setResult(resultRef);
			commonServiceImpl.save(MetaType.simulateExec.toString(), simulateExec);
			if (result != null) {
				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec,
						Status.Stage.COMPLETED);
			} else {
				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec,
						Status.Stage.FAILED);
			}

			return simulateExec;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}

			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec,
					Status.Stage.FAILED);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(
					new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Simulate execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Simulate execution FAILED.");
		}
	}
}
