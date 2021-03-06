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

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.inferyx.framework.domain.DQIntelligence;
import com.inferyx.framework.domain.DQRecExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.ResultType;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.intelligence.DQRecommender;

/**
 * @author Ganesh
 *
 */
public class RunDQRecServiceImpl implements Callable<TaskHolder> {
	
	private CommonServiceImpl<?> commonServiceImpl;
	private ExecutorFactory execFactory;
	private SparkExecutor<?> sparkExecutor;
	private MetadataServiceImpl metadataServiceImpl;
	private DQRecExec dqRecExec;
	private ExecParams execParams;
	private RunMode runMode;
	private String name;
	private SessionContext sessionContext;
	private DQRecommender dqRecommender;

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
	 * @return the metadataServiceImpl
	 */
	public MetadataServiceImpl getMetadataServiceImpl() {
		return metadataServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param metadataServiceImpl the metadataServiceImpl to set
	 */
	public void setMetadataServiceImpl(MetadataServiceImpl metadataServiceImpl) {
		this.metadataServiceImpl = metadataServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the dqRecExec
	 */
	public DQRecExec getDqRecExec() {
		return dqRecExec;
	}

	/**
	 * @Ganesh
	 *
	 * @param dqRecExec the dqRecExec to set
	 */
	public void setDqRecExec(DQRecExec dqRecExec) {
		this.dqRecExec = dqRecExec;
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
	 * @return the dqRecommender
	 */
	public DQRecommender getDqRecommender() {
		return dqRecommender;
	}

	/**
	 * @Ganesh
	 *
	 * @param dqRecommender the dqRecommender to set
	 */
	public void setDqRecommender(DQRecommender dqRecommender) {
		this.dqRecommender = dqRecommender;
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
			
			throw new RuntimeException((message != null) ? message : "Data Quality recommendation execution FAILED.");
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(MetaType.dqrecExec, dqRecExec.getUuid(), dqRecExec.getVersion()));
		return taskHolder;
	}
	public List<DQIntelligence> execute() throws Exception {
		try {
			List<DQIntelligence> recommendationList = new ArrayList<>();
			try {
				synchronized (dqRecExec.getUuid()) {
					dqRecExec = (DQRecExec) commonServiceImpl.setMetaStatus(dqRecExec, MetaType.dqrecExec,
							Status.Stage.RUNNING);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			String appUuid = commonServiceImpl.getApp().getUuid();
			float minThreshold = 0.0F;
			minThreshold = Float.parseFloat(commonServiceImpl.getConfigValue("framework.dataqual.sample.score.minthreshold"));
			
			MetaIdentifier execDependsOnMI = dqRecExec.getDependsOn().getRef();
			Datapod sourceDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(execDependsOnMI.getUuid(), execDependsOnMI.getVersion(), execDependsOnMI.getType().toString(), "N");
			
			Datasource sourceDS = commonServiceImpl.getDatasourceByObject(sourceDp);
			
			//***************** getting source data *****************//
			ExecContext execContext = commonServiceImpl.getExecContext(runMode);
			IExecutor exec = execFactory.getExecutor(execContext.toString());
			ResultSetHolder rsHolder = exec.executeSqlByDatasource(dqRecExec.getExec(), sourceDS, appUuid);
			if(rsHolder.getType().equals(ResultType.resultset)) {
				rsHolder = sparkExecutor.convertResultsetToDataframe(rsHolder);
			}
			
			List<DataQual> latestDQList = metadataServiceImpl.getAllDQByDatapod(sourceDp.getUuid());
			
			String samplePercent = dqRecExec.getSamplePercent();
			if(StringUtils.isBlank(samplePercent)) {
				samplePercent = commonServiceImpl.getConfigValue("framework.dataqual.sample.percent");
			} 
			
			dqRecExec.setSamplePercent(samplePercent);
			
			//***************** gettting sample *****************//
			Dataset<Row> sampleDf = null;
			if(samplePercent.contains("%")) {
				String samplePerc = samplePercent.substring(0, samplePercent.lastIndexOf("%"));
				double sample = Double.parseDouble(samplePerc);
				Dataset<Row>[] splits = rsHolder.getDataFrame().randomSplit(new double[] {sample/100, (100 - sample)/100}, 12345);
				sampleDf = splits[0];
			} else {
				sampleDf = rsHolder.getDataFrame().limit(Integer.parseInt(samplePercent));
			}

			//***************** setting default properties *****************//
			String defaultPath = commonServiceImpl.getConfigValue("framework.schema.Path");
			defaultPath = defaultPath.endsWith("/") ? defaultPath : defaultPath.concat("/");
			String filePath = String.format("%s/%s/%s/", sourceDp.getUuid(), sourceDp.getVersion(), dqRecExec.getVersion());
			String tempTableName = String.format("%s_%s_%s", sourceDp.getUuid().replaceAll("-", "_"), sourceDp.getVersion(), dqRecExec.getVersion());
			
			String sampleFilePathUrl = defaultPath.concat(filePath).concat("sample");
			ResultSetHolder sampleHolder = new ResultSetHolder();
			sampleHolder.setDataFrame(sampleDf);
			sampleHolder.setType(ResultType.dataframe);

			//***************** saving sample *****************//
			String sampleTempTableName = String.format("%s_%s", tempTableName, "sample");
			sampleHolder = dqRecommender.save(sampleHolder, sampleFilePathUrl, sampleTempTableName, true);
			sampleHolder.setCountRows(sampleHolder.getDataFrame().count());
			
			sampleHolder.setTableName(sampleTempTableName);
			
			//***************** domain check for recommendation *****************//
			recommendationList.addAll(dqRecommender.domainCheck(sampleHolder, defaultPath, filePath, tempTableName, sourceDp, latestDQList, minThreshold));

			//***************** null check for recommendation *****************//
			recommendationList.addAll(dqRecommender.nullCheck(sampleHolder, defaultPath, filePath, tempTableName, sourceDp, latestDQList, minThreshold));
			
			//***************** duplication (primary key) check for recommendation *****************//
			recommendationList.addAll(dqRecommender.duplicateCheck(sampleHolder, defaultPath, filePath, tempTableName, sourceDp, latestDQList, minThreshold));

			//***************** range check for recommendation *****************//
			recommendationList.addAll(dqRecommender.rangeCheck(sampleHolder, defaultPath, filePath, tempTableName, sourceDp, latestDQList, minThreshold));
			
			//***************** value check for recommendation *****************//
			recommendationList.addAll(dqRecommender.valueCheck(sampleHolder, defaultPath, filePath, tempTableName, sourceDp, latestDQList, minThreshold));
			
			dqRecExec.setIntelligenceResult(recommendationList);
			
			synchronized (dqRecExec.getUuid()) {
				dqRecExec = (DQRecExec) commonServiceImpl.setMetaStatus(dqRecExec, MetaType.dqrecExec,
						Status.Stage.COMPLETED);
			}
			
			List<String> tempTableList = new ArrayList<>();
			tempTableList.add(sampleTempTableName);
			sparkExecutor.dropTempTable(tempTableList);
			return recommendationList;
		}catch (Exception e) {
			e.printStackTrace();
			// Set status to FAILED
			try {
				synchronized (dqRecExec.getUuid()) {
					dqRecExec = (DQRecExec) commonServiceImpl.setMetaStatus(dqRecExec, MetaType.dqrecExec,
							Status.Stage.FAILED);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			String message = null;
			try {
				message = e.getMessage();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqrecExec, dqRecExec.getUuid(), dqRecExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					(message != null) ? message : "Data Qual recommendation failed ...", dependsOn);
			throw new java.lang.Exception((message != null) ? message : "Data Qual recommendation failed ...");
		}
	}

//	public List<DQIntelligence> domainCheck(ResultSetHolder rsHolder, String defaultPath, String filePath,
//			String defaultTempTableName, Datapod datapod, List<DataQual> latestDQList, float minThreshold)
//			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
//			SecurityException, NullPointerException, ParseException, IOException {
//		String appUuid = commonServiceImpl.getApp().getUuid();
//
//		List<String> tempTableList = new ArrayList<>();
//		
//		String[] dfColumns = rsHolder.getDataFrame().columns();
//		List<AttributeDomain> attrDomainList = metadataServiceImpl.getDomainByApp(appUuid);
//
//		List<DQIntelligence> domainRecommendation = new ArrayList<>();
//		String domainTempTableName = defaultTempTableName.concat("_").concat("domain_union");
//		long sampleTotalRows = rsHolder.getCountRows();
//		
//		//***************** applying regex for each domain vs each column of sample *****************//
//		if(attrDomainList != null && !attrDomainList.isEmpty()) {
//			for(AttributeDomain domain : attrDomainList) { //domain loop
//				StringBuilder regextQuery = new StringBuilder();
//				regextQuery.append("SELECT ");
//				regextQuery.append("'").append(domain.getName()).append("'").append(" AS ").append("domain_name").append(", ");
//				regextQuery.append("'").append(domain.getUuid()).append("'").append(" AS ").append("domain_uuid").append(", ");
//				regextQuery.append("'").append(datapod.getUuid()).append("'").append(" AS ").append("datapod_uuid").append(", ");
//				
//				int i = 0;
//				for(String colName : dfColumns) { //column loop
//					String colCheck = colName.concat(" RLIKE (").concat("'").concat(domain.getRegEx()).concat("') ");
//					regextQuery.append(caseWrapper(colCheck, colName));
//					if(i < dfColumns.length - 1) {
//						regextQuery.append(", ");
//					}
//					i++;
//				}
//				regextQuery.append(" FROM ").append(rsHolder.getTableName()).append(" ");
//				sparkExecutor.executeAndRegisterByTempTable(regextQuery.toString(), domainTempTableName, true,appUuid);
//				
//				//***************** calculating score for each column, formula: ((no. of 'Y' in a column)/sampleTotalRows) *****************//
//				if(sampleTotalRows > 0) {
//					for(String colName : dfColumns) {	
//						StringBuilder outerSqlBuilder = new StringBuilder("SELECT * FROM (");
//						StringBuilder scoreCountBuilder = new StringBuilder();
//						scoreCountBuilder.append("SELECT domain_name, domain_uuid, datapod_uuid, ");
//						scoreCountBuilder.append("(COUNT(").append(colName).append(") / ").append(sampleTotalRows).append(") * 100 AS score_count").append(", ");
//						scoreCountBuilder.append("'").append(colName).append("'").append(" AS score_column");
//						
//						scoreCountBuilder.append(" FROM ").append(domainTempTableName);
//						scoreCountBuilder.append(" WHERE ").append(colName).append(" = 'Y' ");
//						scoreCountBuilder.append(" GROUP BY ").append("domain_name, domain_uuid, datapod_uuid");
//												
//						outerSqlBuilder.append(scoreCountBuilder).append(") WHERE score_count >= "+minThreshold);
//						
//						ResultSetHolder scoreCountHolder = sparkExecutor.executeAndRegisterByTempTable(outerSqlBuilder.toString(), null, false, appUuid);
////						scoreCountHolder.getDataFrame().show(false);
//						
//						domainRecommendation.addAll(getCheckTypeListForDomain(scoreCountHolder, attrDomainList, datapod, latestDQList));
//					}					
//				}
//			}
//					
//			tempTableList.add(domainTempTableName);
//			
////			//***************** saving score *****************//
////			String scoreFilePathUrl = defaultPath.concat(filePath).concat("score");
////			save(scoreCountHolder, scoreFilePathUrl, null, false);
////			
////			List<DQIntelligence> domainRecommendation = getCheckTypeListForDomain(scoreCountHolder, attrDomainList, datapod, latestDQList);
//			
//			//***************** finding max score of a columns *****************//
////			String maxScoreCountSql = "SELECT domain_name, domain_uuid, datapod_uuid, score_count, score_column FROM "
////					+ colScoreTempTable+" WHERE score_count = (SELECT MAX(score_count) FROM "+colScoreTempTable+")";
////			sparkExecutor.executeAndRegisterByTempTable(maxScoreCountSql, null, false, appUuid);
////			maxScoreHolder.getDataFrame().show(false);
//			
//			//***************** dropping temporary tables created *****************//
//			sparkExecutor.dropTempTable(tempTableList);
//			return domainRecommendation;
//		}
//		return new ArrayList<>();
//	}
//	
//	public Dataset<Row> addRowNumberToDF(Dataset<Row> df){
//		return df.withColumn("rowNum", functions.row_number().over(Window.orderBy(df.columns()[df.columns().length-1])));
//	}
//	
//	public String caseWrapper(String check, String colName) {
//		StringBuilder caseBuilder = new StringBuilder(" CASE WHEN ").append(check).append(" THEN 'Y' ELSE 'N' END AS ").append(colName)
//				.append(" ");
//		return caseBuilder.toString();
//	}
//	
//	public List<DQIntelligence> getCheckTypeListForDomain(ResultSetHolder rsHolder, List<AttributeDomain> attrDomainList,
//			Datapod datapod, List<DataQual> latestDQList) {
//		List<DQIntelligence> checkTypeList = new ArrayList<>();
//		
//		Dataset<Row> df = rsHolder.getDataFrame();
//		Row[] rows = (Row[]) df.head(Integer.parseInt(""+df.count()));
//
//		for(Row row : rows) {
//			try {
//				if(row.getDouble(3) > 0.0) {
//					//********* 0: domain_name, 1: domain_uuid, 2: datapod_uuid, 3: score_count, 4: score_column *********//
//		
//					DQIntelligence dqIntelligence = new DQIntelligence();
//					
//					dqIntelligence.setCheckType(CheckType.DOMAIN);
//					dqIntelligence.setSampleScore(Float.parseFloat(row.getDouble(3)+""));
//					
//					//********* setting attribute name *********//
//					dqIntelligence.setAttributeName(getAttributeName(datapod, row.getString(4)));
//					
//					//********* setting check value  *********//
//					List<MetaIdentifierHolder> checkValueList = new ArrayList<>();
//					for(AttributeDomain attributeDomain : attrDomainList) {
//						if(attributeDomain.getUuid().equals(row.get(1).toString())) {
//							MetaIdentifier ref = attributeDomain.getRef(MetaType.domain);
//							ref.setName(attributeDomain.getName());
//							checkValueList.add(new MetaIdentifierHolder(ref, attributeDomain.getName()));
//							
//							break;
//						}
//					}
//					dqIntelligence.setCheckValue(checkValueList);
//					
//					
//					//********* setting dq creation check  *********//
//					dqIntelligence.setCreated(getDQCreationCheck(latestDQList, CheckType.DOMAIN, row.getString(2), row.getString(4)));
//				
//					checkTypeList.add(dqIntelligence);
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//		}
//		
//		return checkTypeList;
//	}
//	
//	public AttributeRefHolder getAttributeName(Datapod datapod, String attrName) {
//		for(Attribute attribute : datapod.getAttributes()) {
//			if(attribute.getName().equals(attrName)) {
//				AttributeRefHolder attrRefHolder = new AttributeRefHolder();
//				attrRefHolder.setRef(datapod.getRef(MetaType.datapod));
//				attrRefHolder.setAttrId(attribute.getAttributeId().toString());
//				attrRefHolder.setAttrName(attribute.getName());				
//				return attrRefHolder;
//			}
//		}
//		
//		return null;
//	}
//	
//	public boolean getDQCreationCheck(List<DataQual> latestDQList, CheckType checkType, String datapodUuid, String columnName) throws JsonProcessingException {
//		if(latestDQList != null && !latestDQList.isEmpty()) {
//			for(DataQual dq : latestDQList) {
//				try {
//					if(dq.getDependsOn().getRef().getUuid().equals(datapodUuid)) {
//						Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodUuid, null, MetaType.datapod.toString(), "N");
//						Object checkValue = checkCheckType(dq, checkType);
//						if(checkValue == null) {
//							return false;
//						} else {
//							switch(checkType) {
//							case DOMAIN: 								
//								if(dq.getDomainCheck() == null) {
//									return false;
//								} else {
//									return true;
//								}
//								
//							case NULL:
//								if(datapod.getAttributeName(Integer.parseInt(dq.getAttribute().getAttrId())).equals(columnName)) {
//									if(dq.getNullCheck() == null) {
//										return false;
//									} else {
//										return true;
//									}
//								} else {
//									return false;
//								}
//								
//							case RANGE:
//								if(datapod.getAttributeName(Integer.parseInt(dq.getAttribute().getAttrId())).equals(columnName)) {
//									if(dq.getRangeCheck() == null) {
//										return false;
//									} else {
//										return true;
//									}
//								} else {
//									return false;
//								}
//								
//							case VALUE:
//								if(datapod.getAttributeName(Integer.parseInt(dq.getAttribute().getAttrId())).equals(columnName)) {
//									if(dq.getValueCheck() == null) {
//										return false;
//									} else {
//										return true;
//									}
//								} else {
//									return false;
//								}
//								
//							case DUPLICATE:
//								return false;
//								
//							default: return false;
//							}
//						}
//					} else {
//						return false;
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//		} else {
//			return false;
//		}
//
//		return false;
//	}
//	
//	public Object checkCheckType(DataQual dq, CheckType checkType) {
//		switch(checkType) {
//		case DOMAIN: return dq.getDomainCheck();
//		case NULL: return dq.getNullCheck();
//		case DUPLICATE: return dq.getDuplicateKeyCheck();
//		case RANGE: return dq.getRangeCheck();
//		case VALUE: return dq.getValueCheck();
//			
//		default: return null;
//		}
//	}
//	
//	public ResultSetHolder save(ResultSetHolder rsHolder, String filePathUrl, String tempTableName, boolean registerTempTable) throws IOException {		
//		try {
//			return sparkExecutor.registerAndPersistDataframe(rsHolder, null, SaveMode.OVERWRITE.toString(), filePathUrl, tempTableName, "true", registerTempTable);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	/**
//	 * @param rsHolder
//	 * @param defaultPath
//	 * @param filePath
//	 * @param tempTableName
//	 * @param datapod
//	 * @param latestDQList
//	 * @return
//	 * @throws IOException 
//	 * @throws ParseException 
//	 * @throws NullPointerException 
//	 * @throws SecurityException 
//	 * @throws NoSuchMethodException 
//	 * @throws InvocationTargetException 
//	 * @throws IllegalArgumentException 
//	 * @throws IllegalAccessException 
//	 */
//	public List<DQIntelligence> nullCheck(ResultSetHolder rsHolder, String defaultPath,
//			String filePath, String defaultTempTableName, Datapod datapod, List<DataQual> latestDQList, float minThreshold) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		
//		List<String> tempTableList = new ArrayList<>();
//		String appUuid = commonServiceImpl.getApp().getUuid();
//		
////		rsHolder.getDataFrame().show(false);
//		
//		boolean anyNull = false;
//		StringBuilder nullCheckBuilder = new StringBuilder("SELECT ");	
//		
//		for(Attribute attribute : datapod.getAttributes()) {
//			if(attribute.getNullFlag() != null && attribute.getNullFlag().equalsIgnoreCase("N")) {
//				String check = attribute.getName().concat(" IS NOT NULL ");
//				nullCheckBuilder.append(caseWrapper(check, attribute.getName()));
//				
//				nullCheckBuilder.append(", ");
//				
//				anyNull = true;
//			} 
//		}
//		
//		if(anyNull) {
//			String tempSql = nullCheckBuilder.substring(0, nullCheckBuilder.lastIndexOf(","));
//			nullCheckBuilder = new StringBuilder(tempSql);
//			nullCheckBuilder.append(" FROM ").append(rsHolder.getTableName());
//			
//			String nullCheckTempTableName = defaultTempTableName.concat("_").concat("null_check");
//			
//			tempTableList.add(nullCheckTempTableName);
//			
//			ResultSetHolder nullCheckHolder = sparkExecutor.executeAndRegisterByTempTable(nullCheckBuilder.toString(), nullCheckTempTableName, true, appUuid);
////			nullCheckHolder.getDataFrame().show(false);
//
//			long sampleTotalRows = rsHolder.getCountRows();
//			if(nullCheckHolder.getCountRows() > 0) {
//				StringBuilder outerSqlBuilder = new StringBuilder("SELECT * FROM (");
//				StringBuilder scoreBuilder = new StringBuilder();
//				String[] dfCols = nullCheckHolder.getDataFrame().columns();				
//				
//				int j = 0;
//				for(String col : dfCols) {
//					scoreBuilder.append("SELECT ");
//					scoreBuilder.append("'").append(datapod.getUuid()).append("' AS ").append("datapod_uuid").append(", ");					
//					scoreBuilder.append(" '").append(col).append("' AS ").append("null_col").append(", ");
//					scoreBuilder.append("(COUNT(").append(col).append(") / "+sampleTotalRows+") * 100 AS ").append("score_count");
//					scoreBuilder.append(" FROM ").append(nullCheckTempTableName);
//					scoreBuilder.append(" WHERE ").append(col).append(" = 'Y'").append(" GROUP BY datapod_uuid, null_col");
//					
//					if(j < dfCols.length - 1) {
//						scoreBuilder.append(" UNION ALL ");
//					}
//					j++;
//				}
//				
//				outerSqlBuilder.append(scoreBuilder.toString()).append(") WHERE score_count >= ").append(minThreshold);
//				ResultSetHolder distinctValHolder = sparkExecutor.executeAndRegisterByTempTable(outerSqlBuilder.toString(), null, false, appUuid);
////				distinctValHolder.getDataFrame().show(false);
//				
//				//***************** dropping temporary tables created *****************//
//				sparkExecutor.dropTempTable(tempTableList);
//				
//				return getCheckTypeListForNullCheck(distinctValHolder, datapod, latestDQList);
//			}
//			
//			//***************** dropping temporary tables created *****************//
//			sparkExecutor.dropTempTable(tempTableList);
//		}
//		
//		return new ArrayList<>();
//	}
//	
//	public List<DQIntelligence> getCheckTypeListForNullCheck(ResultSetHolder rsHolder, Datapod datapod, List<DataQual> latestDQList) {
//		//********* 0: datapod_uuid, 1: null_col, 2: score_count *********//
//		
//		List<DQIntelligence> checkTypeList = new ArrayList<>();
//		
//		Dataset<Row> df = rsHolder.getDataFrame();
//		Row[] rows = (Row[]) df.head(Integer.parseInt(""+df.count()));
//
//		for (Row row : rows) {
//			try {
//				DQIntelligence dqIntelligence = new DQIntelligence();
//				dqIntelligence.setCheckType(CheckType.NULL);
//				dqIntelligence.setSampleScore(row.getDouble(2));
//
//				//***************** setting attribute name *****************//
//					dqIntelligence.setAttributeName(getAttributeName(datapod, row.getString(1)));
//				
//				//***************** setting check value  *****************//
//				List<MetaIdentifierHolder> checkValueList = new ArrayList<>();
//				checkValueList.add(new MetaIdentifierHolder(new MetaIdentifier(MetaType.simple, null, null), CheckType.NULL.toString()));
//				dqIntelligence.setCheckValue(checkValueList);
//						
//				
//				//***************** setting dq creation check  *****************//
//				dqIntelligence.setCreated(getDQCreationCheck(latestDQList, CheckType.NULL,row.getString(1), row.getString(1)));
//				
//				checkTypeList.add(dqIntelligence);
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//		
//		return checkTypeList;
//	}
//
//	/**
//	 * @param sampleHolder
//	 * @param defaultPath
//	 * @param filePath
//	 * @param tempTableName
//	 * @param sourceDp
//	 * @param latestDQList
//	 * @param minThreshold
//	 * @return
//	 * @throws ParseException 
//	 * @throws NullPointerException 
//	 * @throws SecurityException 
//	 * @throws NoSuchMethodException 
//	 * @throws InvocationTargetException 
//	 * @throws IllegalArgumentException 
//	 * @throws IllegalAccessException 
//	 * @throws IOException 
//	 */
//	public List<DQIntelligence> duplicateCheck(ResultSetHolder rsHolder, String defaultPath,
//			String filePath, String defaultTempTableName, Datapod datapod, List<DataQual> latestDQList, float minThreshold) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
//		
//		List<Attribute> keyAttributeList = metadataServiceImpl.getKeyAttributeList(datapod);
//		if(keyAttributeList != null && !keyAttributeList.isEmpty()) {
////			List<String> tempTableList = new ArrayList<>();
//			String appUuid = commonServiceImpl.getApp().getUuid();
//			
////			String dupCheckTempTableName = defaultTempTableName.concat("_").concat("duplicate_check");		
////			tempTableList.add(dupCheckTempTableName);
//			
//			StringBuilder outerSqlBuilder = new StringBuilder("SELECT * FROM (");
//			StringBuilder distinctBuilder = new StringBuilder();
//			long totalRows = rsHolder.getCountRows();
//			for(Attribute attribute : keyAttributeList) {
//				distinctBuilder.append("SELECT ");
//				distinctBuilder.append("'").append(datapod.getUuid()).append("' AS ").append("datapod_uuid").append(", ");
//				distinctBuilder.append("'").append(attribute.getName()).append("' AS ").append("dup_col").append(", ");
//				distinctBuilder.append("(COUNT(DISTINCT(").append(attribute.getName()).append(") ").append(") / ")
//						.append(totalRows).append(") * ").append("100").append(" AS score_col");
//				distinctBuilder.append(" FROM ").append(rsHolder.getTableName());
//				distinctBuilder.append(" UNION ALL ");
//			}
//			
//			outerSqlBuilder.append(distinctBuilder.substring(0, distinctBuilder.lastIndexOf(" UNION ALL "))).append(")");
//			outerSqlBuilder.append(" WHERE score_col >= ").append(minThreshold);
//			
//			ResultSetHolder distinctValHolder = sparkExecutor.executeAndRegisterByTempTable(outerSqlBuilder.toString(), null, false, appUuid);
////			distinctValHolder.getDataFrame().show(false);
//
//			//***************** dropping temporary tables created *****************//
////			sparkExecutor.dropTempTable(tempTableList);
//			
//			return getCheckTypeListForDupCheck(distinctValHolder, datapod, latestDQList);
//		}
//		
//		return new ArrayList<>();
//	}
//	
//	public List<DQIntelligence> getCheckTypeListForDupCheck(ResultSetHolder rsHolder, Datapod datapod, List<DataQual> latestDQList) {
//		//********* 0: datapod_uuid, 1: dup_col, 2: score_count *********//
//		
//		List<DQIntelligence> checkTypeList = new ArrayList<>();
//		
//
//		DQIntelligence dqIntelligence = new DQIntelligence();
//		dqIntelligence.setCheckType(CheckType.DUPLICATE);
//		dqIntelligence.setSampleScore(100);
//		
//		//***************** setting attribute name *****************//
//		dqIntelligence.setAttributeName(null);
//		
//		Dataset<Row> df = rsHolder.getDataFrame();
//		Row[] rows = (Row[]) df.head(Integer.parseInt(""+df.count()));
//
//		List<MetaIdentifierHolder> checkValueList = new ArrayList<>();
//		for (Row row : rows) {
//			try {
//				//***************** setting check value  *****************//
//				checkValueList.add(new MetaIdentifierHolder(new MetaIdentifier(MetaType.simple, null, null), row.getString(1)));
//				
//				//***************** setting dq creation check  *****************//
////				dqIntelligence.setCreated(getDQCreationCheck(latestDQList, CheckType.DUPLICATE, row.getString(1)));
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//		}
//		dqIntelligence.setCheckValue(checkValueList);
//		checkTypeList.add(dqIntelligence);
//		
//		return checkTypeList;
//	}
//
//	/**
//	 * @param sampleHolder
//	 * @param defaultPath
//	 * @param filePath
//	 * @param tempTableName
//	 * @param sourceDp
//	 * @param latestDQList
//	 * @param minThreshold
//	 * @return
//	 * @throws ParseException 
//	 * @throws NullPointerException 
//	 * @throws SecurityException 
//	 * @throws NoSuchMethodException 
//	 * @throws InvocationTargetException 
//	 * @throws IllegalArgumentException 
//	 * @throws IllegalAccessException 
//	 * @throws IOException 
//	 */
//	public List<DQIntelligence> rangeCheck(ResultSetHolder rsHolder, String defaultPath,
//			String filePath, String defaultTempTableName, Datapod datapod, List<DataQual> latestDQList, float minThreshold) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
//
//		List<String> tempTableList = new ArrayList<>();
//		String appUuid = commonServiceImpl.getApp().getUuid();
//		
////		String rangeCheckTempTableName = defaultTempTableName.concat("_").concat("range_check");		
////		tempTableList.add(rangeCheckTempTableName);
//		
//		boolean anyNumericCol = false;
//		StringBuilder rangeCheckBuilder = new StringBuilder();
//		for(Attribute attribute : datapod.getAttributes()) {
//			if(attribute.getType().equalsIgnoreCase(DataType.DECIMAL.toString())
//					|| attribute.getType().equalsIgnoreCase(DataType.DOUBLE.toString())
//					|| attribute.getType().equalsIgnoreCase(DataType.FLOAT.toString())
//					|| attribute.getType().equalsIgnoreCase(DataType.INTEGER.toString())
//					|| attribute.getType().equalsIgnoreCase(DataType.LONG.toString())
//					|| attribute.getType().equalsIgnoreCase(DataType.SHORT.toString())) {
//				rangeCheckBuilder.append("SELECT ");
//				rangeCheckBuilder.append("MAX(").append(attribute.getName()).append(") AS ").append("max_val").append(", ");
//				rangeCheckBuilder.append("MIN(").append(attribute.getName()).append(") AS ").append("min_val").append(", ");
//				rangeCheckBuilder.append("'").append(attribute.getName()).append("' AS ").append("range_col").append(", ");
//				rangeCheckBuilder.append(100).append(" AS ").append("score_col").append(", ");
//				rangeCheckBuilder.append("'").append(attribute.getName()).append("' AS ").append("datapod_uuid");
//				rangeCheckBuilder.append(" FROM ").append(rsHolder.getTableName());
//				rangeCheckBuilder.append(" GROUP BY range_col, score_col");
//				rangeCheckBuilder.append(" UNION ALL ");
//				
//				anyNumericCol = true;
//			}
//		}
//		
//		if(anyNumericCol) {
//			String rangeCheckSql = rangeCheckBuilder.substring(0, rangeCheckBuilder.lastIndexOf(" UNION ALL "));
//			
//			ResultSetHolder rangeCheckHolder = sparkExecutor.executeAndRegisterByTempTable(rangeCheckSql, null, false, appUuid);
////			rangeCheckHolder.getDataFrame().show(false);
//
//			//***************** dropping temporary tables created *****************//
//			sparkExecutor.dropTempTable(tempTableList);
//			
//			return getCheckTypeListForRangeCheck(rangeCheckHolder, datapod, latestDQList);
//		}
//		
//		return new ArrayList<>();
//	}
//
//	/**
//	 * @param rangeCheckHolder
//	 * @param datapod
//	 * @param latestDQList
//	 * @return
//	 */
//	public List<DQIntelligence> getCheckTypeListForRangeCheck(ResultSetHolder rsHolder,
//			Datapod datapod, List<DataQual> latestDQList) {
//		// ********* 0: max_val, 1: min_val, 2: range_col, 3: score_col, 4: datapod_uuid  *********//
//
//		List<DQIntelligence> checkTypeList = new ArrayList<>();
//
//		Dataset<Row> df = rsHolder.getDataFrame();
//		Row[] rows = (Row[]) df.head(Integer.parseInt("" + df.count()));
//
//		for (Row row : rows) {
//			try {
//				DQIntelligence dqIntelligence = new DQIntelligence();
//				dqIntelligence.setCheckType(CheckType.RANGE);
//				dqIntelligence.setSampleScore(Double.parseDouble(row.get(3).toString()));
//
//				// ***************** setting attribute name *****************//
//				dqIntelligence.setAttributeName(getAttributeName(datapod, row.getString(2)));
//
//				// ***************** setting check value *****************//
//				List<MetaIdentifierHolder> checkValueList = new ArrayList<>();
//				checkValueList.add(new MetaIdentifierHolder(
//						new MetaIdentifier(MetaType.simple, null, null, "Lower Bound"), row.get(1).toString()));
//				checkValueList.add(new MetaIdentifierHolder(
//						new MetaIdentifier(MetaType.simple, null, null, "Upper Bound"), row.get(0).toString()));
//				dqIntelligence.setCheckValue(checkValueList);
//
//				// ***************** setting dq creation check *****************//
//				dqIntelligence.setCreated(getDQCreationCheck(latestDQList, CheckType.RANGE, row.getString(4), row.getString(2)));
//
//				checkTypeList.add(dqIntelligence);
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//		}
//
//		return checkTypeList;
//	}
//
//	/**
//	 * @param sampleHolder
//	 * @param defaultPath
//	 * @param filePath
//	 * @param tempTableName
//	 * @param sourceDp
//	 * @param latestDQList
//	 * @param minThreshold
//	 * @return
//	 * @throws ParseException 
//	 * @throws NullPointerException 
//	 * @throws SecurityException 
//	 * @throws NoSuchMethodException 
//	 * @throws InvocationTargetException 
//	 * @throws IllegalArgumentException 
//	 * @throws IllegalAccessException 
//	 * @throws IOException 
//	 */
//	public List<DQIntelligence> valueCheck(ResultSetHolder rsHolder, String defaultPath,
//			String filePath, String defaultTempTableName, Datapod datapod, List<DataQual> latestDQList, float minThreshold) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
//		List<String> tempTableList = new ArrayList<>();
//		String appUuid = commonServiceImpl.getApp().getUuid();
//		
//		String valueCheckTempTableName = defaultTempTableName.concat("_").concat("dist_value_check");		
//		tempTableList.add(valueCheckTempTableName);
//		
//		StringBuilder distValueCheckBuilder = new StringBuilder("SELECT DISTINCT ");
//		int i = 0;
//		for(Attribute attribute : datapod.getAttributes()) {
//			distValueCheckBuilder.append(attribute.getName());
//			
//			if(i < datapod.getAttributes().size() - 1) {
//				distValueCheckBuilder.append(", ");
//			}
//			
//			i++;
//		}
//		distValueCheckBuilder.append(" FROM ").append(rsHolder.getTableName()).append(" ");
//		
//		ResultSetHolder distinctValHolder = sparkExecutor.executeAndRegisterByTempTable(distValueCheckBuilder.toString(), valueCheckTempTableName, true, appUuid);
////		distinctValHolder.getDataFrame().show(false);
//		
//		StringBuilder outerSqlBuilder = new StringBuilder("SELECT * FROM (");
//		StringBuilder valueCheckBuilder = new StringBuilder();
//		long totalRows = rsHolder.getCountRows();
//		for(String col : distinctValHolder.getDataFrame().columns()) {
//			valueCheckBuilder.append("SELECT ");
//			valueCheckBuilder.append("'").append(datapod.getUuid()).append("' AS ").append("datapod_uuid").append(", ");
//			valueCheckBuilder.append("'").append(col).append("' AS ").append("val_col").append(", ");
//			valueCheckBuilder.append("(COUNT(DISTINCT(").append(col).append(")) / ")
//					.append(totalRows).append(") * ").append("100").append(" AS score_col").append(", ");
//			valueCheckBuilder.append("COUNT(DISTINCT(").append(col).append(")) AS dist_count_col");
//			valueCheckBuilder.append(" FROM ").append(valueCheckTempTableName);
//			valueCheckBuilder.append(" UNION ALL ");
//		}
//
//			outerSqlBuilder.append(valueCheckBuilder.substring(0, valueCheckBuilder.lastIndexOf(" UNION ALL "))).append(")");
//			outerSqlBuilder.append(" WHERE score_col >= ").append(minThreshold);
//			
//			ResultSetHolder scoreHolder = sparkExecutor.executeAndRegisterByTempTable(outerSqlBuilder.toString(), null, false, appUuid);
////			scoreHolder.getDataFrame().show(false);
//			
//			List<DQIntelligence> checkTypeList = getCheckTypeListForValueCheck(distinctValHolder, scoreHolder, datapod, latestDQList);
//			
//			//***************** dropping temporary tables created *****************//
//			sparkExecutor.dropTempTable(tempTableList);
//			
//			return checkTypeList;
//	}
//
//	/**
//	 * @param distinctValHolder
//	 * @param datapod
//	 * @param latestDQList
//	 * @return
//	 * @throws IOException 
//	 * @throws ParseException 
//	 * @throws NullPointerException 
//	 * @throws SecurityException 
//	 * @throws NoSuchMethodException 
//	 * @throws InvocationTargetException 
//	 * @throws IllegalArgumentException 
//	 * @throws IllegalAccessException 
//	 * @throws NumberFormatException 
//	 */
//	public List<DQIntelligence> getCheckTypeListForValueCheck(ResultSetHolder distinctValHolder, ResultSetHolder scoreHolder,
//			Datapod datapod, List<DataQual> latestDQList) throws NumberFormatException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
//		// ********* 0: datapod_uuid, 1: val_col, 2: score_col 3: dist_count_col *********//
//
//		List<DQIntelligence> checkTypeList = new ArrayList<>();
//
//		Dataset<Row> df = scoreHolder.getDataFrame();
//		Row[] rows = (Row[]) df.head(Integer.parseInt("" + df.count()));
//
//		int lovThreshold = Integer.parseInt(commonServiceImpl.getConfigValue("framework.dataqual.sample.value.threshold"));
//		for (Row row : rows) {
//			try {
//				if(Integer.parseInt(row.get(3).toString()) <= lovThreshold) {
//					DQIntelligence dqIntelligence = new DQIntelligence();
//					dqIntelligence.setCheckType(CheckType.VALUE);
//					dqIntelligence.setSampleScore(Double.parseDouble(row.get(2).toString()));
//
//					// ***************** setting attribute name *****************//
//					dqIntelligence.setAttributeName(getAttributeName(datapod, row.getString(1)));
//
//					// ***************** setting check value *****************//
//					String sql = "SELECT DISTINCT "+row.getString(1)+" FROM "+distinctValHolder.getTableName();
//					Dataset<Row> distValColDf = distinctValHolder.getDataFrame().sqlContext().sql(sql);
//					distValColDf = distValColDf.select(distValColDf.col(row.getString(1)));
//					
//					Row[] distRowList = (Row[]) distValColDf.head(Integer.parseInt("" + distValColDf.count()));
//					
//					List<MetaIdentifierHolder> checkValueList = new ArrayList<>();
//					for(Row distRow : distRowList) {
//						checkValueList.add(new MetaIdentifierHolder(
//								new MetaIdentifier(MetaType.simple, null, null, null), distRow.get(0).toString()));
//					}
//					
//					dqIntelligence.setCheckValue(checkValueList);
//
//					// ***************** setting dq creation check *****************//
//					dqIntelligence.setCreated(getDQCreationCheck(latestDQList, CheckType.VALUE, row.getString(0), row.getString(1)));
//
//					checkTypeList.add(dqIntelligence);
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//		}
//
//		return checkTypeList;
//	}

}
