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
package com.inferyx.framework.operator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataFrameHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.FeatureAttrMap;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;
import com.inferyx.framework.writer.IWriter;

/**
 * @author joy
 *
 */
@Component
public class PredictMLOperator {
	
	@Autowired
	private SparkContext sparkContext;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private SQLContext sqlContext;
	@Autowired
	private MetadataUtil daoRegister;
	@Autowired
	private DataSourceFactory datasourceFactory;
	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private ModelServiceImpl modelServiceImpl; 
	@Autowired
	private FormulaOperator formulaOperator;
	
	static final Logger LOGGER = Logger.getLogger(PredictMLOperator.class);
	
	/**
	 * 
	 */
	public PredictMLOperator() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public Object execute(Predict predict, Model model, Algorithm algorithm, Datapod targetDp, Dataset<Row> df, String[] fieldArray, TrainExec latestTrainExec,
			String targetType, String tableName, String filePathUrl, String filePath, String clientContext) throws Exception {

			Vector features = null; // extract feature column as vector from df
			String modelName = algorithm.getModelName();
			Class<?> modelClass = Class.forName(modelName);

			MetaIdentifierHolder datastoreHolder = latestTrainExec.getResult();
			DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreHolder.getRef().getUuid(),
					datastoreHolder.getRef().getVersion(), datastoreHolder.getRef().getType().toString());
			if (dataStore == null)
				throw new NullPointerException("No datastore available");
			String location = dataStore.getLocation();

			if (location.contains("/data"))
				location = location.replaceAll("/data", "");
			
			location = hdfsInfo.getHdfsURL() + location;

			Object trainedModel = modelClass.getMethod("load", String.class).invoke(modelClass, location);
			Dataset<Row> predictionDf = (Dataset<Row>) trainedModel.getClass().getMethod("transform", Dataset.class)
					.invoke(trainedModel, df);
			predictionDf.show();

			String uid = (String) trainedModel.getClass().getMethod("uid").invoke(trainedModel);

			//if (targetType.equalsIgnoreCase(MetaType.datapod.toString())) {
				Datasource datasource = commonServiceImpl.getDatasourceByApp();

				df.createOrReplaceGlobalTempView("tempPredictResult");
				IConnector connector = connectionFactory.getConnector(datasource.getType().toLowerCase());
				ConnectionHolder conHolder = connector.getConnection();
				if (conHolder.getStmtObject() instanceof SparkSession) {
					SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
					predictionDf.persist(StorageLevel.MEMORY_AND_DISK());
					sparkSession.sqlContext().registerDataFrameAsTable(predictionDf, "tempPredictResult");
				}
				IExecutor exec = execFactory.getExecutor(datasource.getType());
				String columns = "";
				for (String col : predictionDf.columns())
					columns = columns.concat(col).concat(" AS ").concat(col).concat(",");
				columns = columns.substring(0, columns.length() - 2);
				String sql = "SELECT " + columns + " FROM " + "tempPredictResult";
				ResultSetHolder rsHolder = exec.executeSql(sql, commonServiceImpl.getApp().getUuid());

				Dataset<Row> dfTask = rsHolder.getDataFrame();
				dfTask.cache();

				sqlContext.registerDataFrameAsTable(dfTask, tableName);

				dfTask.printSchema();
				IWriter datapodWriter = datasourceFactory.getDatapodWriter(targetDp, daoRegister);
				datapodWriter.write(dfTask, filePathUrl + "/data", targetDp, SaveMode.Append.toString());
				return filePathUrl + "/data";
			/*} else {
				if (modelServiceImpl.save(modelName, trainedModel, sparkContext, filePathUrl))
					return filePathUrl + "/data";
				else
					return null;
			}	*/
	}

	public Object execute(String sql, String tableName,
			String filePathUrl, String filePath, String uuid) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		ResultSetHolder rsHolder = exec.executeSql(sql);
		Dataset<Row> resultDf = rsHolder.getDataFrame();
		resultDf.printSchema();
		resultDf.show();
		IWriter datapodWriter = datasourceFactory.getDatapodWriter(null, daoRegister);
		datapodWriter.write(resultDf, filePathUrl + "/data", null, SaveMode.Append.toString());
		return filePathUrl  + "/data";
	}
	
	public String parse(Predict predict, Model model, Dataset<Row> df, String[] fieldArray, String tableName,
			String filePathUrl, String filePath) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		String aliaseName = "";
		builder.append("SELECT ");
		MetaIdentifierHolder dependsOn = model.getDependsOn();
		Object object = commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
		if(object instanceof Formula) {
			
			Formula formula = (Formula) object;
			
			List<Feature> modelFeatures = model.getFeatures();
			
			Formula dumyFormula = formula;
			
			List<SourceAttr> formulaInfo = dumyFormula.getFormulaInfo();
			List<FeatureAttrMap> predictFeatures = predict.getFeatureAttrMap();
			
			for(SourceAttr attr : formulaInfo) {
				if(attr.getRef().getType().equals(MetaType.paramlist))
					for(FeatureAttrMap featureAttrMap : predictFeatures) {
						boolean flag = false;
						for(Feature feature : modelFeatures) {
							if(featureAttrMap.getFeatureMapId().equalsIgnoreCase(feature.getFeatureId())) {
								ParamListHolder paramListHolder = feature.getParamListInfo();
								if(paramListHolder.getParamId().equals(attr.getAttributeId().toString())) {
									attr.getRef().setUuid(featureAttrMap.getAttribute().getRef().getUuid());
									attr.getRef().setType(featureAttrMap.getAttribute().getRef().getType());
									attr.setAttributeId(Integer.parseInt(featureAttrMap.getAttribute().getAttrId()));
									
									if(attr.getRef().getType().equals(MetaType.datapod)) {
										Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(attr.getRef().getUuid(), attr.getRef().getVersion(), attr.getRef().getType().toString());
										aliaseName = datapod.getName();
										String attrName = datapod.getAttributes().get(attr.getAttributeId()).getName();
										builder.append(attrName).append(" AS ").append(feature.getName()).append(", ");
									}
									
									flag = true;
									break;
								}
							}
						}
						if(flag)
							break;
					}
			}
			
			builder.append(formulaOperator.generateSql(dumyFormula, null, null, null)).append(" AS ").append(model.getLabel());
			builder.append(" FROM ");
			builder.append(tableName).append(" ").append(aliaseName);

			LOGGER.info("query : "+builder);
		}
		return builder.toString();
	}

}
