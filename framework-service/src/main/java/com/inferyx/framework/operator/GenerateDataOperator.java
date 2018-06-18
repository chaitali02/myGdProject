/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.datascience.distribution.Math3RandDistribution;
import com.inferyx.framework.datascience.distribution.RandomDistribution;
import com.inferyx.framework.datascience.distribution.RandomDistributionFactory;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.RowObj;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class GenerateDataOperator implements Operator {
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private RandomDistributionFactory randomDistributionFactory;
	
	static final Logger logger = Logger.getLogger(GenerateDataOperator.class);

	/**
	 * 
	 */
	public GenerateDataOperator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, String> create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		HashMap<String, String> otherParams = execParams.getOtherParams();
		if (otherParams == null) {
			otherParams = new HashMap<String, String>();
			execParams.setOtherParams(otherParams);
		}
		String execVersion = baseExec.getVersion();
		// Set destination
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
//		String newVersion = Helper.getVersion();
//		locationDatapod.setVersion(newVersion);
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, runMode);
		logger.info(" tableName : " + tableName);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		logger.info("otherParams in generateDataOperator : "+ otherParams);
		return otherParams;
	}

	/**
	 * 
	 * @param numIterations
	 * @param numRepetitions
	 * @return
	 */
	protected int getResolvedIterations (int numIterations, int numRepetitions) {
		if (numIterations < 1 ) {
			return numRepetitions;
		} 
		if (numRepetitions < 1) {
			return numIterations;
		}
		return numIterations*numRepetitions;
	}
	
	/**
	 * 
	 * @param execParams
	 * @param numIterations
	 * @param execVersion
	 * @param otherParams
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws Exception
	 */
	protected Object getDistributionObject(ExecParams execParams, int numIterations, String execVersion, Map<String, String> otherParams) throws InterruptedException, ExecutionException, Exception {
		ParamListHolder distributionInfo = paramSetServiceImpl.getParamByName(execParams, "distribution");
		Distribution distribution = (Distribution) commonServiceImpl.getOneByUuidAndVersion(distributionInfo.getParamValue().getRef().getUuid(), distributionInfo.getParamValue().getRef().getVersion(), distributionInfo.getParamValue().getRef().getType().toString());
		List<ParamListHolder> distParamHolderList = new ArrayList<>();
		ExecParams distExecParam = new ExecParams();

		List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
		for(ParamListHolder holder : paramListInfo) {
			if(holder.getRef().getUuid().equalsIgnoreCase(distribution.getParamList().getRef().getUuid())) {
				distParamHolderList.add(holder);
			}
		}
		distExecParam.setParamListInfo(distParamHolderList);
		RandomDistribution randomDistribution = randomDistributionFactory.getRandomDistribution(distribution.getLibrary());
		Object distributionObject = randomDistribution.getDistribution(distribution, distExecParam);
		return distributionObject;
	}
	
	/**
	 * 
	 * @param execParams
	 * @return
	 * @throws JsonProcessingException
	 */
	protected String getMethodName(ExecParams execParams) throws JsonProcessingException {
		ParamListHolder distributionInfo = paramSetServiceImpl.getParamByName(execParams, "distribution");
		Distribution distribution = (Distribution) commonServiceImpl.getOneByUuidAndVersion(distributionInfo.getParamValue().getRef().getUuid(), distributionInfo.getParamValue().getRef().getVersion(), distributionInfo.getParamValue().getRef().getType().toString());
		return distribution.getMethodName();
	}
	
	protected String getFilePath (Datapod locationDatapod, String execVersion) {
		return "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
	}
	
	protected String getFileName (Datapod locationDatapod, String execVersion) {
		return String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);
	}

	/**
	 * 
	 * @param exec
	 * @param resultSetHolder
	 * @param tableName
	 * @param locationDatapod
	 * @param execIdentifier
	 * @param runMode
	 * @throws Exception
	 */
	protected void save (IExecutor exec, 
						List<RowObj> rowObjList, 
						String tableName, 
						Datapod locationDatapod, 
						MetaIdentifier execIdentifier, 
						RunMode runMode) throws Exception {
		String execVersion = execIdentifier.getVersion();
		String execUuid = execIdentifier.getUuid();
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		
		List<Attribute> attributes = locationDatapod.getAttributes();
		ResultSetHolder resultSetHolder = exec.createRegisterAndPersist(rowObjList, attributes, tableName, getFilePath(locationDatapod, execVersion), locationDatapod, SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		rowObjList = null;
//		exec.registerAndPersist(resultSetHolder, tableName, getFilePath(locationDatapod, execVersion), locationDatapod, SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		
		Object metaExec = commonServiceImpl.getOneByUuidAndVersion(execIdentifier.getUuid(), execIdentifier.getVersion(), execIdentifier.getType().toString());
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) metaExec.getClass().getMethod("getCreatedBy").invoke(metaExec);
		@SuppressWarnings("unchecked")
		List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) metaExec.getClass().getMethod("getAppInfo").invoke(metaExec);
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(getFilePath(locationDatapod, execVersion), getFileName(locationDatapod, execVersion), 
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, execUuid, execVersion) ,
				appInfo, createdBy, SaveMode.Append.toString(), resultRef, resultSetHolder.getCountRows(), null);
		
		metaExec.getClass().getMethod("setResult", MetaIdentifierHolder.class).invoke(metaExec, resultRef);
		commonServiceImpl.save(execIdentifier.getType().toString(), metaExec);
		
	}

	/**
	 * 
	 * @param exec
	 * @param resultSetHolder
	 * @param tableName
	 * @param locationDatapod
	 * @param execIdentifier
	 * @param runMode
	 * @throws Exception
	 */
	protected void save (IExecutor exec, 
						ResultSetHolder resultSetHolder,  
						String tableName, 
						Datapod locationDatapod, 
						MetaIdentifier execIdentifier, 
						RunMode runMode) throws Exception {
		String execVersion = execIdentifier.getVersion();
		String execUuid = execIdentifier.getUuid();
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		
		List<Attribute> attributes = locationDatapod.getAttributes();
		exec.registerAndPersist(resultSetHolder, tableName, getFilePath(locationDatapod, execVersion), locationDatapod, SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		logger.info("execIdentifier : " + execIdentifier.getUuid() +":"+ execIdentifier.getVersion() +":"+ execIdentifier.getType());
		Object metaExec = commonServiceImpl.getOneByUuidAndVersion(execIdentifier.getUuid(), execIdentifier.getVersion(), execIdentifier.getType().toString());
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) metaExec.getClass().getMethod("getCreatedBy").invoke(metaExec);
		@SuppressWarnings("unchecked")
		List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) metaExec.getClass().getMethod("getAppInfo").invoke(metaExec);
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(getFilePath(locationDatapod, execVersion), getFileName(locationDatapod, execVersion), 
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, execUuid, execVersion) ,
				appInfo, createdBy, SaveMode.Append.toString(), resultRef, resultSetHolder.getCountRows(), null);
		
		metaExec.getClass().getMethod("setResult", MetaIdentifierHolder.class).invoke(metaExec, resultRef);
		logger.info("After setResult : " + ((OperatorExec)metaExec).getResult().getRef().getUuid());
		commonServiceImpl.save(execIdentifier.getType().toString(), metaExec);
		
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		String execUuid = baseExec.getUuid();
		String execVersion = baseExec.getVersion();
		Map<String, String> otherParams = execParams.getOtherParams();
		ParamListHolder distributionInfo = paramSetServiceImpl.getParamByName(execParams, "distribution");
		Distribution distribution = (Distribution) commonServiceImpl.getOneByUuidAndVersion(distributionInfo.getParamValue().getRef().getUuid(), distributionInfo.getParamValue().getRef().getVersion(), distributionInfo.getParamValue().getRef().getType().toString());
		ParamListHolder numIterationsInfo = paramSetServiceImpl.getParamByName(execParams, "numIterations");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		List<ParamListHolder> distParamHolderList = new ArrayList<>();
		ExecParams distExecParam = new ExecParams();

		List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
		for(ParamListHolder holder : paramListInfo) {
			if(holder.getRef().getUuid().equalsIgnoreCase(distribution.getParamList().getRef().getUuid())) {
				distParamHolderList.add(holder);
			}
		}
		distExecParam.setParamListInfo(distParamHolderList);
		
		int numIterations = Integer.parseInt(numIterationsInfo.getParamValue().getValue());
		int resolvedIterations = getResolvedIterations(numIterations, 0);
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		// Get exec
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
																																		
		Object distributionObject = getDistributionObject(execParams, resolvedIterations, execVersion, otherParams);
		
		String tableName = otherParams.get("datapodUuid_" + locationDatapod.getUuid() + "_tableName");
		RandomDistribution randomDistribution = randomDistributionFactory.getRandomDistribution(distribution.getLibrary());
		
		if (randomDistribution instanceof Math3RandDistribution) {
			List<RowObj> rowObjList = randomDistribution.generateData(distribution, distributionObject, getMethodName(execParams), locationDatapod.getAttributes(), numIterations, execVersion, tableName);
			// Save result
			save(exec, rowObjList, tableName, locationDatapod, baseExec.getRef(MetaType.operatorExec), runMode);
		} else {
			Object[] objList = randomDistribution.getParamObjList(distExecParam.getParamListInfo());
			Class<?>[] paramTypeList = randomDistribution.getParamTypeList(distExecParam.getParamListInfo());
			ResultSetHolder resultSetHolder = exec.generateData(distribution, distributionObject, getMethodName(execParams), objList, paramTypeList, locationDatapod.getAttributes(), numIterations, execVersion, tableName);			
			// Save result
			save(exec, resultSetHolder, tableName, locationDatapod, baseExec.getRef(MetaType.operatorExec), runMode);
		}
		return tableName;
	}

	
}
