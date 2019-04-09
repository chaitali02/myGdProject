/**
 * 
 */
package com.inferyx.framework.operator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.DatasetServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Component
public class DataSamplingOperator implements IOperator, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9151826149221877585L;
	
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
	private DatasetServiceImpl datasetServiceImpl;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	
	static final Logger logger = Logger.getLogger(DataSamplingOperator.class);
	/**
	 * 
	 */
	public DataSamplingOperator() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.IParsable#parse(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.IExecutable#execute(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		//Collect data
		String execUuid = baseExec.getUuid();
		String execVersion = baseExec.getVersion();
		HashMap<String, String> otherParams = execParams.getOtherParams();
		
		/*******************************************************************************/
		/************************ Define Parameters - START ****************************/
		/*******************************************************************************/
		// Get the parameters
		ParamListHolder sourceDatapodInfo = paramSetServiceImpl.getParamByName(execParams, "sourceDatapod");	// The source datapod
		ParamListHolder numSamplesInfo = paramSetServiceImpl.getParamByName(execParams, "sampleSize");	// Sample Size - Optional
		ParamListHolder percentOfSamplesInfo = paramSetServiceImpl.getParamByName(execParams, "samplePercent");	// Sample Percent of the source datapod - Optional
		// In case both are provided, sampleSize is considered
		// In case none is provided, sampleSize of 100 is considered 
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");	// Location for datapod save
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
		/*******************************************************************************/
		/************************ Define Parameters - END   ****************************/
		/*******************************************************************************/
		
		/*******************************************************************************/
		/************************ Get Source Data - START ******************************/
		/*******************************************************************************/
		MetaIdentifier sourceDataIdentifier = sourceDatapodInfo.getParamValue().getRef();
		Datapod sourceData = null;
		DataSet sourceDataset = null;
		String sourceTableSql = null;
		String sourceTableName = null;
		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(appDatasource.getType());
		
		sourceTableName = otherParams.get("datapodUuid_" + sourceDataIdentifier.getUuid() + "_tableName");
		if (sourceDataIdentifier.getType() == MetaType.datapod) {
			sourceData = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
			exec = execFactory.getExecutor(commonServiceImpl.getDatasourceByDatapod(sourceData).getType());
			sourceTableSql = sourceTableName;
		} else if (sourceDataIdentifier.getType() == MetaType.dataset) {
			sourceDataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
			exec = execFactory.getExecutor(commonServiceImpl.getDatasourceByObject(sourceDataset).getType());
			sourceTableSql = "(" + datasetServiceImpl.generateSql(sourceDataset, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), otherParams, new HashSet<>(), execParams, runMode, new HashMap<String, String>()) + ") "
					+ sourceDataset.getName();
		}
		Dataset<Row> dataDf = exec.executeSql(" SELECT * FROM " + sourceTableSql).getDataFrame();
		/*****************************************************************************/
		/************************ Get Source Data - END ******************************/
		/*****************************************************************************/
		
		/*****************************************************************************/
		/************************ Do data sampling - START ***************************/
		/*****************************************************************************/
		long numSamples = (StringUtils.isBlank(numSamplesInfo.getParamValue().getValue())) 
							? -1 
							: Long.parseLong(numSamplesInfo.getParamValue().getValue());
		
		float percentSamples = (StringUtils.isBlank(percentOfSamplesInfo.getParamValue().getValue())) 
								? -1 
								: Float.parseFloat(percentOfSamplesInfo.getParamValue().getValue());
		percentSamples /= 100;
		
		if (percentSamples < 0 && numSamples < 0) {
			numSamples = 100;
		}
		
		long srcDataCount = -1L;
		if (numSamples > -1) {
			// Calculate percentSamples
			srcDataCount = dataDf.count();
			logger.info("Data count : " + srcDataCount);
			percentSamples = numSamples/srcDataCount;
		}
	
		// Find destination Dataframe
		Dataset<Row> destDf = dataDf.sample(Boolean.TRUE, percentSamples);
		logger.info(" Sample count after retrieval : " + destDf.count());
		/***************************************************************************/
		/************************ Do data sampling - END ***************************/
		/***************************************************************************/
		
		/***************************************************************************/
		/************************ Save data to destination - START *****************/
		/***************************************************************************/
		String filePath = "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
		String fileName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		ResultSetHolder rsHolder = new ResultSetHolder();
		rsHolder.setDataFrame(destDf);
		rsHolder.setCountRows(destDf.count());
		
		Datasource destDS = commonServiceImpl.getDatasourceByObject(locationDatapod);	

//		String tableName = getTableName(baseExec, locationDatapod, destDS, runMode);	
		
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, baseExec.getVersion(), null, null, null, runMode, false);
		rsHolder.setTableName(tableName);
		if(destDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())
				|| destDS.getType().equalsIgnoreCase(ExecContext.spark.toString())) {
			String defaultPath = "file://".concat(commonServiceImpl.getConfigValue("framework.schema.Path"));
			defaultPath = defaultPath.endsWith("/") ? defaultPath : defaultPath.concat("/");
			String filePathUrl = String.format("%s/%s/%s/%s", defaultPath, locationDatapod.getUuid(), locationDatapod.getVersion(), baseExec.getVersion());
			sparkExecutor.registerAndPersistDataframe(rsHolder, locationDatapod, SaveMode.Append.toString(), filePathUrl, null, "true", false);
		} else {
			exec.persistDataframe(rsHolder, destDS, locationDatapod, SaveMode.Append.toString());
		}
		/*************************************************************************/
		/************************ Save data to destination - END *****************/
		/*************************************************************************/
		
		/*************************************************************************/
		/************************ Create dataStore and save meta - START *********/
		/*************************************************************************/
		Object metaExec = commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(), baseExec.getVersion(), MetaType.operatorExec.toString());
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) metaExec.getClass().getMethod("getCreatedBy").invoke(metaExec);
		@SuppressWarnings("unchecked")
		List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) metaExec.getClass().getMethod("getAppInfo").invoke(metaExec);
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, fileName, 
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, execUuid, execVersion) ,
				appInfo, createdBy, SaveMode.Append.toString(), resultRef, rsHolder.getCountRows(), null, null);
		
		metaExec.getClass().getMethod("setResult", MetaIdentifierHolder.class).invoke(metaExec, resultRef);
		commonServiceImpl.save(MetaType.operatorExec.toString(), metaExec);
		/*************************************************************************/
		/************************ Create dataStore and save meta - END ***********/
		/*************************************************************************/
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.IOperator#create(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.IOperator#customCreate(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode)
			throws Exception {
		ParamListHolder sourceDatapodInfo = paramSetServiceImpl.getParamByName(execParams, "sourceDatapod");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		if (sourceDatapodInfo == null) {
			return execParams.getOtherParams();
		}
		
		HashMap<String, String> otherParams = execParams.getOtherParams();
		if (otherParams == null) {
			otherParams = new HashMap<String, String>();
			execParams.setOtherParams(otherParams);
		}
		String execVersion = baseExec.getVersion();
		String sourceTableName = null;
		Datapod sourceData = null;
		DataSet sourceDataset = null;
		/*******************************************************************************/
		/************************ Retrieve source - START ******************************/
		/*******************************************************************************/
		MetaIdentifier sourceDataIdentifier = sourceDatapodInfo.getParamValue().getRef();
//		MetaIdentifier sourceDataIdentifier = sourceDatapodInfo.getAttributeInfo().get(0).getRef();
		if (sourceDataIdentifier.getType() == MetaType.datapod) {
			sourceData = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
		} else if (sourceDataIdentifier.getType() == MetaType.dataset) {
			sourceDataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
		}
		
		if (otherParams.containsKey("datapodUuid_" + sourceDataIdentifier.getUuid() + "_tableName")) {
			sourceTableName = otherParams.get("datapodUuid_" + sourceDataIdentifier.getUuid() + "_tableName");
		} else {
			if (sourceDataIdentifier.getType() == MetaType.datapod) {
				sourceTableName = commonServiceImpl.getTableNameBySource(sourceData, runMode);
			} else if (sourceDataIdentifier.getType() == MetaType.dataset) {
				sourceTableName = sourceDataset.getName();
			}
			otherParams.put("datapodUuid_" + sourceDataIdentifier.getUuid() + "_tableName", sourceTableName);
		}
		/*******************************************************************************/
		/************************ Retrieve source - END ********************************/
		/*******************************************************************************/
		
		/*******************************************************************************/
		/************************ Retrieve destination - START *************************/
		/*******************************************************************************/
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
//				String newVersion = Helper.getVersion();
//				locationDatapod.setVersion(newVersion);
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, null, null, null, runMode, false);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		/*******************************************************************************/
		/************************ Retrieve destination - END ***************************/
		/*******************************************************************************/
		logger.info("otherParams in SparkPCAOperator : "+ otherParams);
		return otherParams;
	}

}
