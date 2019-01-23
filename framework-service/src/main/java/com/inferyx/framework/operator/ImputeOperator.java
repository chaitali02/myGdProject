/**
 * 
 */
package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.FeatureAttrMap;
import com.inferyx.framework.domain.FeatureRefHolder;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.Key;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
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
@Component
public class ImputeOperator implements IOperator {
	
	private static String SELECT = " SELECT ";
	private static String FROM = " FROM ";
	private static String AS = " AS ";
	
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
	private RuleOperator ruleOperator;
	
	static final Logger logger = Logger.getLogger(GenerateDataOperator.class);

	/**
	 * 
	 */
	public ImputeOperator() {
		
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		Train train = null;
		Predict predict = null;
		List<FeatureAttrMap> featureAttrMapList = null;
		FeatureRefHolder imputeRefHolder = null;
		Function function = null;
		String featureName = null;
		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		StringBuilder sb = new StringBuilder(SELECT);
		String sql = null;
		if (baseExec == null) {
			return baseExec;
		}
		MetaIdentifier baseEntityRef = baseExec.getDependsOn().getRef();
		if (baseEntityRef!= null && baseEntityRef.getType() == MetaType.train) {
			train = (Train) commonServiceImpl.getOneByUuidAndVersion(baseEntityRef.getUuid(), baseEntityRef.getVersion(), baseEntityRef.getType().toString());
			featureAttrMapList = train.getFeatureAttrMap();
		} else {
			predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(baseEntityRef.getUuid(), baseEntityRef.getVersion(), baseEntityRef.getType().toString());
			featureAttrMapList = predict.getFeatureAttrMap();
		}
		// Get the impute attributes and parse
		if (featureAttrMapList == null || featureAttrMapList.size() <= 0) {
			return null;
		}
		for (FeatureAttrMap featureAttrMap : featureAttrMapList) {
			imputeRefHolder = featureAttrMap.getImputeMethod();
			featureName = featureAttrMap.getFeature().getFeatureName();
			sb.append("'").append(featureName).append("' AS feature_name, ");
			if (imputeRefHolder.getRef().getType() == MetaType.simple) {
				sb.append("'").append(imputeRefHolder.getValue()).append("' ").append(AS).append(featureName).append(", ");
			} else if (imputeRefHolder.getRef().getType() == MetaType.function) {
				function = (Function) commonServiceImpl.getOneByUuidAndVersion(imputeRefHolder.getRef().getUuid(), imputeRefHolder.getRef().getVersion(), imputeRefHolder.getRef().getType().toString());
				sb.append(functionOperator.generateSql(function, null, execParams.getOtherParams(), appDatasource)).append("' ").append(AS).append(featureName).append("_").append("impute, ");
			}
		}
		sb.append(FROM);
		MetaIdentifier attrRef = featureAttrMapList.get(0).getAttribute().getRef();
		if (attrRef.getType() == MetaType.datapod) {
			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(attrRef.getUuid(), attrRef.getVersion(), attrRef.getType().toString());
			sb.append(datapod.getName());
		} else if (attrRef.getType() == MetaType.dataset) {
			DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(attrRef.getUuid(), attrRef.getVersion(), attrRef.getType().toString());
			sb.append("(").append(datasetOperator.generateSql(dataset, null, execParams.getOtherParams(), null, execParams, runMode)).append(") ").append(dataset.getName());
		}
		sql = sb.toString().replaceAll(",  FROM ", FROM);
		baseExec.setExec(sql);
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
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, runMode);
		logger.info(" tableName : " + tableName);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		logger.info("otherParams in imputeOperator : "+ otherParams);
		return otherParams;
	}

	public LinkedHashMap<String, Object> getAttributeImputeValue(List<FeatureAttrMap> featureAttrMapList, Object sourceObj, Model model, ExecParams execParams, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		LinkedHashMap<String, Object> attributeImputeValues = new LinkedHashMap<>();
		
		Datasource appDs = commonServiceImpl.getDatasourceByApp();
		String appUuid = commonServiceImpl.getApp().getUuid();
		
		for(FeatureAttrMap featureAttrMap : featureAttrMapList) {
			for(Feature feature : model.getFeatures()) {
				try {
					if(feature.getFeatureId().equalsIgnoreCase(featureAttrMap.getFeatureMapId())) {
						
						if(feature.getImputeMethod() != null 
								&& feature.getImputeMethod().getRef().getType().equals(MetaType.simple)) {
							attributeImputeValues.put(feature.getFeatureId(), feature.getImputeMethod().getValue());
							logger.info("impute value for attribute "+feature.getName()+": "+feature.getImputeMethod().getValue());
						} else if(feature.getImputeMethod() != null 
								&& feature.getImputeMethod().getRef().getType().equals(MetaType.function)) {
							
							MetaIdentifier functionMI = feature.getImputeMethod().getRef();
							Function function = (Function) commonServiceImpl.getOneByUuidAndVersion(functionMI.getUuid(), functionMI.getVersion(), functionMI.getType().toString(), "N");
							Datasource sourceDs = commonServiceImpl.getDatasourceByObject(sourceObj);
							String resolvedFunction = functionOperator.generateSql(function, null, execParams != null ? execParams.getOtherParams() : null, sourceDs);
							
							IExecutor exec = execFactory.getExecutor(appDs.getType());
							String attrSql = null;
							String attrName = null;
							if(sourceObj instanceof Datapod) {
								Datapod datapod = (Datapod)sourceObj;
								attrName = datapod.getAttributeName(Integer.parseInt(featureAttrMap.getAttribute().getAttrId()));
								dataStoreServiceImpl.setRunMode(runMode);
								String tableName = dataStoreServiceImpl.getTableNameByDatapod(new Key(datapod.getUuid(), datapod.getVersion()), runMode);
								String generatedFunction = generateFunction(resolvedFunction, attrName);
								attrSql = "SELECT "+generatedFunction+" FROM "+tableName+" "+datapod.getName();
							} else if(sourceObj instanceof DataSet) {
								DataSet dataSet = (DataSet)sourceObj;
								attrName = dataSet.getAttributeName(Integer.parseInt(featureAttrMap.getAttribute().getAttrId()));
								String generatedFunction = generateFunction(resolvedFunction, attrName);
								String innerSql = datasetOperator.generateSql(dataSet, null, execParams != null ? execParams.getOtherParams() : null, new HashSet<>(), execParams, runMode);
								attrSql = "SELECT "+generatedFunction+" FROM ("+innerSql+") "+dataSet.getName();								
							} else if(sourceObj instanceof Rule) {
								Rule rule = (Rule)sourceObj;
								attrName = rule.getAttributeName(Integer.parseInt(featureAttrMap.getAttribute().getAttrId()));
								String generatedFunction = generateFunction(resolvedFunction, attrName);
								String innerSql = ruleOperator.generateSql(rule, null, execParams != null ? execParams.getOtherParams() : null, new HashSet<>(), execParams, runMode);
								attrSql = "SELECT "+generatedFunction+" FROM ("+innerSql+") "+rule.getName();								
							}
							
							ResultSetHolder rsHolder = exec.executeSqlByDatasource(attrSql, sourceDs, appUuid);
							Object attrValue = exec.getImputeValue(rsHolder);
							attributeImputeValues.put(feature.getFeatureId(), attrValue);
							logger.info("impute value for attribute "+attrName+": "+attrValue);						
						}
						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}				
			} 
		}
		return attributeImputeValues;
	}
	
	public String generateFunction(String resolvedFunction, String attributeName) {
		return resolvedFunction.endsWith("(") ? resolvedFunction.concat(attributeName).concat(")") : resolvedFunction.concat("(").concat(attributeName).concat(")");
	}
}
