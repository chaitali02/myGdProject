/**
 * 
 */
package com.inferyx.framework.operator;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.FeatureAttrMap;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Component
public class ImputeOperator implements IOperator {
	
//	private static String SELECT = " SELECT ";
//	private static String FROM = " FROM ";
//	private static String AS = " AS ";
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	FunctionOperator functionOperator;
	@Autowired
	DatasetOperator datasetOperator;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private Helper helper;
	@Autowired
	Engine engine;
	@Autowired
	private ModelServiceImpl modelServiceImpl;
	
	static final Logger logger = Logger.getLogger(ImputeOperator.class);

	/**
	 * 
	 */
	public ImputeOperator() {
		
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
//		Train train = null;
//		Predict predict = null;
//		List<FeatureAttrMap> featureAttrMapList = null;
//		FeatureRefHolder imputeRefHolder = null;
//		Function function = null;
//		String featureName = null;
//		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
//		StringBuilder sb = new StringBuilder(SELECT);
//		String sql = null;
//		if (baseExec == null) {
//			return baseExec;
//		}
//		MetaIdentifier baseEntityRef = baseExec.getDependsOn().getRef();
//		if (baseEntityRef!= null && baseEntityRef.getType() == MetaType.train) {
//			train = (Train) commonServiceImpl.getOneByUuidAndVersion(baseEntityRef.getUuid(), baseEntityRef.getVersion(), baseEntityRef.getType().toString());
//			featureAttrMapList = train.getFeatureAttrMap();
//		} else {
//			predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(baseEntityRef.getUuid(), baseEntityRef.getVersion(), baseEntityRef.getType().toString());
//			featureAttrMapList = predict.getFeatureAttrMap();
//		}
//		// Get the impute attributes and parse
//		if (featureAttrMapList == null || featureAttrMapList.size() <= 0) {
//			return null;
//		}
//		for (FeatureAttrMap featureAttrMap : featureAttrMapList) {
//			imputeRefHolder = featureAttrMap.getImputeMethod();
//			featureName = featureAttrMap.getFeature().getFeatureName();
//			sb.append("'").append(featureName).append("' AS feature_name, ");
//			if (imputeRefHolder.getRef().getType() == MetaType.simple) {
//				sb.append("'").append(imputeRefHolder.getValue()).append("' ").append(AS).append(featureName).append(", ");
//			} else if (imputeRefHolder.getRef().getType() == MetaType.function) {
//				function = (Function) commonServiceImpl.getOneByUuidAndVersion(imputeRefHolder.getRef().getUuid(), imputeRefHolder.getRef().getVersion(), imputeRefHolder.getRef().getType().toString());
//				sb.append(functionOperator.generateSql(function, null, execParams.getOtherParams(), appDatasource)).append("' ").append(AS).append(featureName).append("_").append("impute, ");
//			}
//		}
//		sb.append(FROM);
//		MetaIdentifier attrRef = featureAttrMapList.get(0).getAttribute().getRef();
//		if (attrRef.getType() == MetaType.datapod) {
//			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(attrRef.getUuid(), attrRef.getVersion(), attrRef.getType().toString());
//			sb.append(datapod.getName());
//		} else if (attrRef.getType() == MetaType.dataset) {
//			DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(attrRef.getUuid(), attrRef.getVersion(), attrRef.getType().toString());
//			sb.append("(").append(datasetOperator.generateSql(dataset, null, execParams.getOtherParams(), null, execParams, runMode)).append(") ").append(dataset.getName());
//		}
//		sql = sb.toString().replaceAll(",  FROM ", FROM);
//		baseExec.setExec(sql);
		return baseExec;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		String execUuid = baseExec.getUuid();
		String execVersion = baseExec.getVersion();
		Map<String, String> otherParams = execParams.getOtherParams();

		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
//		List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		// Get exec
		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		Datasource locationDpDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(locationDatapod.getDatasource().getRef().getUuid(), 
																					locationDatapod.getDatasource().getRef().getVersion(), 
																					locationDatapod.getDatasource().getRef().getType().toString());
		String filePath = "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
		String fileName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		String sql = baseExec.getExec();
		ResultSetHolder resultSetHolder = null;
		
		IExecutor exec = execFactory.getExecutor(appDatasource.getType());
																																		
		String tableName = otherParams.get("datapodUuid_" + locationDatapod.getUuid() + "_tableName");
		if(locationDpDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())/*
				|| datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.livy_spark.toString())*/) {
			resultSetHolder = exec.executeRegisterAndPersist(sql, tableName, filePath, locationDatapod, SaveMode.Append.toString(), true, commonServiceImpl.getApp().getUuid());
		} else {
			String query = helper.buildInsertQuery(appDatasource.getType(), tableName, locationDatapod, sql);
			resultSetHolder = exec.executeSql(query);
		}
		
		Object metaExec = commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(), baseExec.getVersion(), MetaType.operatorExec.toString());
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) metaExec.getClass().getMethod("getCreatedBy").invoke(metaExec);
		@SuppressWarnings("unchecked")
		List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) metaExec.getClass().getMethod("getAppInfo").invoke(metaExec);
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, fileName, 
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, execUuid, execVersion) ,
				appInfo, createdBy, SaveMode.Append.toString(), resultRef, resultSetHolder.getCountRows(), null, null);
		
		metaExec.getClass().getMethod("setResult", MetaIdentifierHolder.class).invoke(metaExec, resultRef);
		commonServiceImpl.save(MetaType.operatorExec.toString(), metaExec);

		return tableName;
	}

	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return baseExec;
	}

	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode)
			throws Exception {
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
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, null, null, null, runMode, false);
		logger.info(" tableName : " + tableName);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		logger.info("otherParams in imputeOperator : "+ otherParams);
		return otherParams;
	}

	public LinkedHashMap<String, Object> resolveAttributeImputeValue(List<FeatureAttrMap> featureAttrMapList, Object sourceObj, Model model, ExecParams execParams, RunMode runMode, String tableName) throws Exception {
		LinkedHashMap<String, Object> attributeImputeValues = new LinkedHashMap<>();		
		StringBuilder queryUnionBuilder = new StringBuilder();
		Datasource sourceDs = null;
		
		for(FeatureAttrMap featureAttrMap : featureAttrMapList) {
			for(Feature feature : model.getFeatures()) {
				try {
					if(feature.getFeatureId().equalsIgnoreCase(featureAttrMap.getFeature().getFeatureId())) {
						
						String attrName = modelServiceImpl.getAttributeNameByObject(sourceObj, Integer.parseInt(featureAttrMap.getAttribute().getAttrId()));
						if(feature.getImputeMethod() != null 
								&& feature.getImputeMethod().getRef().getType().equals(MetaType.simple)) {
							attributeImputeValues.put(attrName, feature.getImputeMethod().getValue());
							logger.info("impute value for attribute "+feature.getName()+": "+feature.getImputeMethod().getValue());
						} else if(feature.getImputeMethod() != null 
								&& feature.getImputeMethod().getRef().getType().equals(MetaType.function)) {
							
							MetaIdentifier functionMI = feature.getImputeMethod().getRef();
							Function function = (Function) commonServiceImpl.getOneByUuidAndVersion(functionMI.getUuid(), functionMI.getVersion(), functionMI.getType().toString(), "N");
							sourceDs = commonServiceImpl.getDatasourceByObject(sourceObj);
							String resolvedFunction = functionOperator.generateSql(function, null, execParams != null ? execParams.getOtherParams() : null, sourceDs);
							
							String attrSql = generateAttrSqlBySource(sourceObj, attrName, resolvedFunction, execParams, runMode, tableName);
							
							if(attrSql != null && !attrSql.isEmpty()) {
								if(queryUnionBuilder.length() > 0) {
									queryUnionBuilder.append(" UNION ALL ").append(attrSql);
								} else {
									queryUnionBuilder.append(attrSql);
								}
								
								attributeImputeValues.put(attrName, attrSql);
								logger.info("impute query for attribute "+attrName+": "+attrSql);
							}						
						}
						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}				
			} 
		}
		
		if(queryUnionBuilder.length() > 0) {
			logger.info("impute union query: "+queryUnionBuilder);		
			Datasource appDs = commonServiceImpl.getDatasourceByApp();
			String appUuid = commonServiceImpl.getApp().getUuid();
			IExecutor exec = execFactory.getExecutor(appDs.getType());
			try {
				ResultSetHolder rsHolder = exec.executeSqlByDatasource(queryUnionBuilder.toString(), appDs, appUuid);				
				LinkedHashMap<String, Object> resolvedAttrImputeValues = exec.getImputeValue(rsHolder);
				
				Set<String> attrSet = resolvedAttrImputeValues.keySet();
				for(String attrName : attributeImputeValues.keySet()) {						
					if(attrSet.contains(attrName)) {
						attributeImputeValues.put(attrName, resolvedAttrImputeValues.get(attrName));
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return attributeImputeValues;
	}
	
	public String generateAttrSqlBySource(Object sourceObj, String attrName, String resolvedFunction, ExecParams execParams, RunMode runMode, String tableName) throws Exception {
		if(sourceObj instanceof Datapod) {
			Datapod datapod = (Datapod)sourceObj;
			dataStoreServiceImpl.setRunMode(runMode);
			String generatedFunction = generateFunction(resolvedFunction, attrName);
			return "SELECT '"+attrName+"' AS key, "+generatedFunction+" AS value FROM "+tableName+" "+datapod.getName();
		} else if(sourceObj instanceof DataSet) {
			DataSet dataSet = (DataSet)sourceObj;
			String generatedFunction = generateFunction(resolvedFunction, attrName);
			return "SELECT '"+attrName+"' AS key, "+generatedFunction+" AS value FROM "+tableName+" "+dataSet.getName();								
		} else if(sourceObj instanceof Rule) {
			Rule rule = (Rule)sourceObj;
			String generatedFunction = generateFunction(resolvedFunction, attrName);
			return "SELECT '"+attrName+"' AS key, "+generatedFunction+" AS value FROM "+tableName+" "+rule.getName();								
		}
		return null;
	}
	
	public String generateFunction(String resolvedFunction, String attributeName) {
		return resolvedFunction.endsWith("(") ? resolvedFunction.concat(attributeName).concat(")") : resolvedFunction.concat("(").concat(attributeName).concat(")");
	}
}
