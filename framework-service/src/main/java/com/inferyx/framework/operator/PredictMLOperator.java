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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.FeatureAttrMap;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.SourceAttr;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author joy
 *
 */
@Component
public class PredictMLOperator {
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private FormulaOperator formulaOperator;
	
	static final Logger LOGGER = Logger.getLogger(PredictMLOperator.class);
	
	/**
	 * 
	 */
	public PredictMLOperator() {
		// TODO Auto-generated constructor stub
	}

	/********************** UNUSED **********************/
	/*@SuppressWarnings({ "unchecked", "unused" })
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
				dfTask.show();
				dfTask.printSchema();
				IWriter datapodWriter = datasourceFactory.getDatapodWriter(targetDp, daoRegister);
				datapodWriter.write(dfTask, filePathUrl + "/data", targetDp, SaveMode.Append.toString());
				return filePathUrl + "/data";
//			} else {
//				if (modelServiceImpl.save(modelName, trainedModel, sparkContext, filePathUrl))
//					return filePathUrl + "/data";
//				else
//					return null;
//			}	
	}*/

	/********************** UNUSED **********************/
	/*public Object execute(String sql, String tableName,
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
	}*/
	
	public String generateSql(Predict predict, String tableName) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		String aliaseName = "";
		builder.append("SELECT ");
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(predict.getDependsOn().getRef().getUuid(),
				predict.getDependsOn().getRef().getVersion(), predict.getDependsOn().getRef().getType().toString());
		MetaIdentifierHolder dependsOn = model.getDependsOn();
		Object object = commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
		if(object instanceof Formula) {
			
			Formula formula = (Formula) object;
			
			List<Feature> modelFeatures = model.getFeatures();
			
			Formula dumyFormula = formula;
			
			List<SourceAttr> formulaInfo = dumyFormula.getFormulaInfo();
			List<FeatureAttrMap> predictFeatures = predict.getFeatureAttrMap();
			
			for(SourceAttr attr : formulaInfo) {
				if(attr.getRef().getType().equals(MetaType.paramlist)) {
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
			}
			
			String label = commonServiceImpl.resolveLabel(predict.getLabelInfo());
			builder.append(formulaOperator.generateSql(dumyFormula, null, null, null)).append(" AS ").append(label);
			builder.append(" FROM ");
			builder.append(tableName).append(" ").append(aliaseName);

			LOGGER.info("query : "+builder);
		}
		return builder.toString();
	}
}
