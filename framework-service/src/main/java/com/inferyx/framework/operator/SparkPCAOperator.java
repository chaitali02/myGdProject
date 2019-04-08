/**
 * 
 */
package com.inferyx.framework.operator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.PCA;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.types.DataTypes;
import org.springframework.beans.factory.annotation.Autowired;

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

import scala.Tuple2;

/**
 * @author joy
 *
 */
public class SparkPCAOperator implements IOperator, Serializable {
	
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
	
	static final Logger logger = Logger.getLogger(SparkPCAOperator.class);


	/**
	 * 
	 */
	public SparkPCAOperator() {
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
		String execUuid = baseExec.getUuid();
		String execVersion = baseExec.getVersion();
		HashMap<String, String> otherParams = execParams.getOtherParams();
		ParamListHolder sourceDatapodInfo = paramSetServiceImpl.getParamByName(execParams, "sourceDatapod");	// The source datapod
//		ParamListHolder sourcePipelineInfo = paramSetServiceImpl.getParamByName(execParams, "sourcePipeline");
		ParamListHolder numFeatures = paramSetServiceImpl.getParamByName(execParams, "K");	// Final number of features after PCA
//		ParamListHolder inputFeaturesInfo = paramSetServiceImpl.getParamByName(execParams, "inputFeatures");
//		ParamListHolder outputFeaturesInfo = paramSetServiceImpl.getParamByName(execParams, "outputFeatures");
		ParamListHolder inputColListInfo = paramSetServiceImpl.getParamByName(execParams, "inputColList");	// Input feature column list
		ParamListHolder outputFeatureNameInfo = paramSetServiceImpl.getParamByName(execParams, "outputFeatureName");	// Output Feature Name
		ParamListHolder inputKeyInfo = paramSetServiceImpl.getParamByName(execParams, "inputKeyNames");	// Input Key Names
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");	// Location for datapod save
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
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
//			attrList = getColumnNameList(sourceData, sourceDatapodInfo);
			sourceTableSql = sourceTableName;
		} else if (sourceDataIdentifier.getType() == MetaType.dataset) {
			sourceDataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
			exec = execFactory.getExecutor(commonServiceImpl.getDatasourceByObject(sourceDataset).getType());
//			attrList = getColumnNameList(sourceDataset, sourceDatapodInfo);
			sourceTableSql = "(" + datasetServiceImpl.generateSql(sourceDataset, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), otherParams, new HashSet<>(), execParams, runMode, new HashMap<String, String>()) + ") "
					+ sourceDataset.getName();
		}
		
		List<PipelineStage> pipelineStagesTrng = new ArrayList<>();
		
		MetaIdentifier inputColListId = inputColListInfo.getAttributeInfo().get(0).getRef(); 
		
		MetaIdentifier inputKeyListId = inputKeyInfo.getAttributeInfo().get(0).getRef();
		
		int numFeaturesK = (StringUtils.isBlank(numFeatures.getParamValue().getValue())) 
							? 0 
							: Integer.parseInt(numFeatures.getParamValue().getValue());
		
		 MetaIdentifier outputFeatureNameAttr = outputFeatureNameInfo.getAttributeInfo().get(0).getRef();
		 Object outputFeatureCol = commonServiceImpl.getOneByUuidAndVersion(outputFeatureNameAttr.getUuid(), outputFeatureNameAttr.getVersion(), outputFeatureNameAttr.getType().toString());
		 List<String> outputFeatureAttrList = commonServiceImpl.getColumnNameList(outputFeatureCol, outputFeatureNameInfo);
		 String outputFeatureName = outputFeatureAttrList.get(0);
		  
//		String outputFeatureName = "features";

		Object inputCols = commonServiceImpl.getOneByUuidAndVersion(inputColListId.getUuid(), inputColListId.getVersion(), inputColListId.getType().toString());
		List<String> featureAttrList = commonServiceImpl.getColumnNameList(inputCols, inputColListInfo);
		Object inputKeys = commonServiceImpl.getOneByUuidAndVersion(inputKeyListId.getUuid(), inputKeyListId.getVersion(), inputKeyListId.getType().toString());
		List<String> keyAttrList = commonServiceImpl.getColumnNameList(inputKeys, inputKeyInfo);
		
		VectorAssembler vectorAssembler = new VectorAssembler();
		vectorAssembler.setInputCols(featureAttrList.toArray(new String[featureAttrList.size()])).setOutputCol("pcaInputFeatures");
		
		PCA pca = new PCA()
					.setInputCol("pcaInputFeatures")
					.setOutputCol(outputFeatureName)
					.setK(numFeaturesK);
		
		pipelineStagesTrng.add(vectorAssembler);
		pipelineStagesTrng.add(pca);
		
		Pipeline pipeline = new Pipeline().setStages(pipelineStagesTrng.toArray(new PipelineStage[pipelineStagesTrng.size()]));
		Dataset<Row> trngDf = exec.executeSql(" SELECT * FROM " + sourceTableSql).getDataFrame();
		// Cast features to double except Vector fields
		trngDf = castFeatures(trngDf, featureAttrList);
		Dataset<Row> destDf = pipeline.fit(trngDf).transform(trngDf);
		List<Column> colList = new ArrayList<>();
		
		keyAttrList.forEach(col -> colList.add(new Column(col)));
		colList.add(new Column(outputFeatureName));
		
		destDf = destDf.select(colList.toArray(new Column[colList.size()]));
		
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
		
		return null;
	}
	
	/**
	 * 
	 * @param df
	 * @param featureAttrList
	 * @return
	 */
	private Dataset<Row> castFeatures(Dataset<Row> df, List<String> featureAttrList) {
		if (df == null 
				|| featureAttrList == null 
				|| featureAttrList.isEmpty()) {
			return df;
		}	// End if
		
		Map<String, String> colTypeMap = new HashMap<>();
		for (Tuple2<String, String> dtype : df.dtypes()) {
			colTypeMap.put(dtype._1, dtype._2);
		}	// End for
		logger.info("ColTypeMap : " + colTypeMap);
		
		for (String featureName : featureAttrList) {
			if (!colTypeMap.get(featureName).contains("Vector")) {
				df = df.withColumn(featureName, df.col(featureName).cast(DataTypes.DoubleType));
			}
		}	// End for
		
		return df;
	}

	public void persistDatastore() {
		
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
//		String destTableName = null;
		
		if (otherParams == null) {
			otherParams = new HashMap<>();
		}
		
		Datapod sourceData = null;
		DataSet sourceDataset = null;
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
		
		// Set destination
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
//				String newVersion = Helper.getVersion();
//				locationDatapod.setVersion(newVersion);
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, null, null, null, runMode, false);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		logger.info("otherParams in SparkPCAOperator : "+ otherParams);
		return otherParams;
	}

}
