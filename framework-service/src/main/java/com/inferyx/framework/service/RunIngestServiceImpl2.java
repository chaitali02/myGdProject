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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.apache.spark.streaming.api.java.JavaInputDStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.SqoopInput;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.StreamInput;
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.enums.SqoopIncrementalMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.KafkaExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.executor.SparkStreamingExecutor;
import com.inferyx.framework.executor.SqoopExecutor;
import com.inferyx.framework.operator.DatasetOperator;
import com.inferyx.framework.operator.IngestOperator;

/**
 * @author Ganesh
 *
 */
public class RunIngestServiceImpl2<T, K> implements Callable<TaskHolder> {
	
	public static Logger logger = Logger.getLogger(RunIngestServiceImpl.class); 
	
	private CommonServiceImpl<?> commonServiceImpl;
	private SparkExecutor<?> sparkExecutor;
	private Helper helper;
	private SqoopExecutor sqoopExecutor;
	private IngestServiceImpl ingestServiceImpl;
	private Ingest ingest;
	private IngestExec ingestExec;
	private ExecParams execParams;
	private RunMode runMode;
	private SessionContext sessionContext;
	private Datapod sourceDp;
	private Datapod targetDp;
	private String name;
	private String appUuid;
	private Datasource sourceDS;
	private Datasource targetDS;
//	private String fileName;
	private List<String> location;
	private KafkaExecutor<?, ?> kafkaExecutor;
	private SparkStreamingExecutor<?, ?> sparkStreamingExecutor;
	private DataSet sourceDataSet;
	private IngestOperator ingestOperator;
	private DatasetOperator datasetOperator;

	
	/**
	 *
	 * @Ganesh
	 *
	 * @return the datasetOperator
	 */
	public DatasetOperator getDatasetOperator() {
		return datasetOperator;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param datasetOperator the datasetOperator to set
	 */
	public void setDatasetOperator(DatasetOperator datasetOperator) {
		this.datasetOperator = datasetOperator;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the ingestOperator
	 */
	public IngestOperator getIngestOperator() {
		return ingestOperator;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param ingestOperator the ingestOperator to set
	 */
	public void setIngestOperator(IngestOperator ingestOperator) {
		this.ingestOperator = ingestOperator;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceDataSet
	 */
	public DataSet getSourceDataSet() {
		return sourceDataSet;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceDataSet the sourceDataSet to set
	 */
	public void setSourceDataSet(DataSet sourceDataSet) {
		this.sourceDataSet = sourceDataSet;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the kafkaExecutor
	 */
	public KafkaExecutor<?, ?> getKafkaExecutor() {
		return kafkaExecutor;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param kafkaExecutor the kafkaExecutor to set
	 */
	public void setKafkaExecutor(KafkaExecutor<?, ?> kafkaExecutor) {
		this.kafkaExecutor = kafkaExecutor;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sparkStreamingExecutor
	 */
	public SparkStreamingExecutor<?, ?> getSparkStreamingExecutor() {
		return sparkStreamingExecutor;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sparkStreamingExecutor the sparkStreamingExecutor to set
	 */
	public void setSparkStreamingExecutor(SparkStreamingExecutor<?, ?> sparkStreamingExecutor) {
		this.sparkStreamingExecutor = sparkStreamingExecutor;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the location
	 */
	public List<String> getLocation() {
		return location;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param location the location to set
	 */
	public void setLocation(List<String> location) {
		this.location = location;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the appUuid
	 */
	public String getAppUuid() {
		return appUuid;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param appUuid the appUuid to set
	 */
	public void setAppUuid(String appUuid) {
		this.appUuid = appUuid;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceDS
	 */
	public Datasource getSourceDS() {
		return sourceDS;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceDS the sourceDS to set
	 */
	public void setSourceDS(Datasource sourceDS) {
		this.sourceDS = sourceDS;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetDS
	 */
	public Datasource getTargetDS() {
		return targetDS;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param targetDS the targetDS to set
	 */
	public void setTargetDS(Datasource targetDS) {
		this.targetDS = targetDS;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the commonServiceImpl
	 */
	public CommonServiceImpl<?> getCommonServiceImpl() {
		return commonServiceImpl;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param commonServiceImpl the commonServiceImpl to set
	 */
	public void setCommonServiceImpl(CommonServiceImpl<?> commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sparkExecutor
	 */
	public SparkExecutor<?> getSparkExecutor() {
		return sparkExecutor;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sparkExecutor the sparkExecutor to set
	 */
	public void setSparkExecutor(SparkExecutor<?> sparkExecutor) {
		this.sparkExecutor = sparkExecutor;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the helper
	 */
	public Helper getHelper() {
		return helper;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param helper the helper to set
	 */
	public void setHelper(Helper helper) {
		this.helper = helper;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sqoopExecutor
	 */
	public SqoopExecutor getSqoopExecutor() {
		return sqoopExecutor;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sqoopExecutor the sqoopExecutor to set
	 */
	public void setSqoopExecutor(SqoopExecutor sqoopExecutor) {
		this.sqoopExecutor = sqoopExecutor;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the ingestServiceImpl
	 */
	public IngestServiceImpl getIngestServiceImpl() {
		return ingestServiceImpl;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param ingestServiceImpl the ingestServiceImpl to set
	 */
	public void setIngestServiceImpl(IngestServiceImpl ingestServiceImpl) {
		this.ingestServiceImpl = ingestServiceImpl;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the ingest
	 */
	public Ingest getIngest() {
		return ingest;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param ingest the ingest to set
	 */
	public void setIngest(Ingest ingest) {
		this.ingest = ingest;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the ingestExec
	 */
	public IngestExec getIngestExec() {
		return ingestExec;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param ingestExec the ingestExec to set
	 */
	public void setIngestExec(IngestExec ingestExec) {
		this.ingestExec = ingestExec;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the execParams
	 */
	public ExecParams getExecParams() {
		return execParams;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param execParams the execParams to set
	 */
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param runMode the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sessionContext
	 */
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sessionContext the sessionContext to set
	 */
	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceDp
	 */
	public Datapod getSourceDp() {
		return sourceDp;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceDp the sourceDp to set
	 */
	public void setSourceDp(Datapod sourceDp) {
		this.sourceDp = sourceDp;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetDp
	 */
	public Datapod getTargetDp() {
		return targetDp;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param targetDp the targetDp to set
	 */
	public void setTargetDp(Datapod targetDp) {
		this.targetDp = targetDp;
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

			if(message != null && message.toLowerCase().contains("duplicate entry")) {
				message = "Duplicate entry/entries found for primary key(s).";
			} else if(message != null && message.toLowerCase().contains("no change in incremental param hence skipping execution.")) {
				message = "No change in incremental param hence skipping execution.";
			}
			
			throw new RuntimeException((message != null) ? message : "Ingest execution FAILED.");
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(MetaType.ingestExec, ingestExec.getUuid(), ingestExec.getVersion()));
		return taskHolder;
	}

	public IngestExec execute() throws Exception {
		try {
			long countRows = -1L;
			
			String targetFilePathUrl = targetDS.getPath();
			String sourceFilePathUrl = sourceDS.getPath();
			
			IngestionType ingestionType = Helper.getIngestionType(ingest.getType());

			MetaIdentifier sourceDpOrDsMI = ingest.getSourceDetail().getRef();
			String incrLastValue = null;
			String latestIncrLastValue = null;
			String incrColName = null;
			
			if(sourceDpOrDsMI.getUuid() != null) {
				//finding incremental column name
				incrColName = ingestServiceImpl.getColName(sourceDp, sourceDataSet, ingest.getIncrAttr());
				
				//finding last incremental value
				incrLastValue = ingestServiceImpl.getLastIncrValue(ingest.getUuid(), ingest.getVersion());
				
				//finding latest incremental value
				latestIncrLastValue = ingestServiceImpl.getNewIncrValue(sourceDp, sourceDS, sourceDataSet, ingest.getIncrAttr(), runMode);
				
				ingestExec.setLastIncrValue(latestIncrLastValue);
				commonServiceImpl.save(MetaType.ingestExec.toString(), ingestExec);
			}	

			Map<String, String> resolvedAttrMap = null;
			HashMap<String, String> otherParams = null;
			if(execParams != null) {
				otherParams = execParams.getOtherParams();
			}
			String[] mappedAttrs = null; 
			boolean areAllAttrs = false;
			String query = null;
			List<String> colAliaseNames = null;			
			String whereClause = "";
			if(ingest.getAttributeMap() != null && !ingest.getAttributeMap().isEmpty()) {
				resolvedAttrMap = resolveMappedAttributes(ingest.getAttributeMap(), true);
				mappedAttrs = resolvedAttrMap.keySet().toArray(new String[resolvedAttrMap.keySet().size()]);
				
				areAllAttrs = areAllAttrs(resolvedAttrMap.values());
				
				if(sourceDp != null) {
					if(areAllAttrs) {
						whereClause = whereClause.concat(ingestOperator.generateWhere(ingest, incrColName, incrLastValue));
						whereClause = whereClause.concat(ingestOperator.generateFilter(ingest, null, null, new HashSet<>(), null, runMode));
					}
					
					//removing where
					if(whereClause.contains("WHERE")) {
						whereClause = whereClause.replaceAll("WHERE", "").trim();
					} else if(whereClause.contains("where")) {
						whereClause = whereClause.replaceAll("where", "").trim();
					}
					
					//removing $CONDITIONS
					if(whereClause.contains("AND $CONDITIONS")) {
						whereClause = whereClause.replace("AND $CONDITIONS", "").trim();
					} else if(whereClause.contains("and $conditions")) {
						whereClause = whereClause.replace("and $conditions", "").trim();
					}					
				}
				
				colAliaseNames = getMappedAttrAliaseName(ingest.getAttributeMap(), true);
				logger.info("IngestionType : " + ingestionType);
	//			if(sourceDp != null) {
				String tableName = null;
					if(ingestionType.equals(IngestionType.FILETOFILE)) {
						tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
//						query = ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, null, new HashSet<>(), null, runMode);
					} else if(sourceDataSet != null) {
						if(sourceDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
							tableName = sourceDS.getSid().concat(".").concat(sourceDataSet.getName());
						} else {
							tableName = sourceDS.getDbname().concat(".").concat(sourceDataSet.getName());
						}					
//						query = ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, null, new HashSet<>(), null, runMode);
					} else if(!areAllAttrs && sourceDp != null) {
						if(sourceDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
							tableName = sourceDS.getSid().concat(".").concat(sourceDp.getName());
						} else {
							tableName = sourceDS.getDbname().concat(".").concat(sourceDp.getName());
						}					
//						query = ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, null, new HashSet<>(), null, runMode);
					} else if(!areAllAttrs && targetDp != null) {
//						if(targetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
							tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
//						} else {
//							if(targetDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
//								tableName = targetDS.getSid().concat(".").concat(targetDp.getName());
//							} else {
//								tableName = targetDS.getDbname().concat(".").concat(targetDp.getName());
//							}	
//						}
//						query = ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, null, new HashSet<>(), execParams, runMode);
					} else if(areAllAttrs && sourceDp != null) {
//						if(sourceDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
							tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
//						} else {
//							if(sourceDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
//								tableName = sourceDS.getSid().concat(".").concat(sourceDp.getName());
//							} else {
//								tableName = sourceDS.getDbname().concat(".").concat(sourceDp.getName());
//							}	
//						}
					}
					query = ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, otherParams, new HashSet<>(), execParams, runMode);
//				} else {
//					query = datasetOperator.generateSql(sourceDataSet, null, null, new HashSet<>(), null, runMode);
//				}
			}
			
			if(incrLastValue != null && latestIncrLastValue != null && incrLastValue.equalsIgnoreCase(latestIncrLastValue)) {
//				new Message("300", MessageStatus.FAIL.toString(),"No change in incremental param hence skipping execution.");
//				ingestExec.setMessageInfo(messageInfo);
//				commonServiceImpl.save(MetaType.ingestExec.toString(), ingestExec);
				throw new RuntimeException("No change in incremental param hence skipping execution.");
			} else {				
				logger.info("mode : configuration >> "+ingest.getType()+" : "+sourceDS.getType()+"_2_"+targetDS.getType());
				String tableName = null;
				if(ingestionType.equals(IngestionType.FILETOFILE)) { 	
					tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
					
					String targetFileName = ingestServiceImpl.generateFileName(ingest.getTargetDetail().getValue(), ingest.getTargetExtn(), ingest.getTargetFormat());
					String targetExtension = ingest.getTargetExtn();
					
					if(targetExtension != null) {
						targetExtension = targetExtension.startsWith(".") ? targetExtension.substring(1) : targetExtension;
					} else {
						targetExtension = ingest.getTargetFormat();
					}
					
					if(targetExtension.equalsIgnoreCase(FileType.PARQUET.toString())) {
						targetFilePathUrl = targetDS.getPath();
						if(targetFileName.endsWith("."+FileType.PARQUET)) {
							targetFileName = targetFileName.replace("."+FileType.PARQUET, "");
						} else if(targetFileName.endsWith("."+FileType.PARQUET.toString().toLowerCase())) {
							targetFileName = targetFileName.replace("."+FileType.PARQUET.toString().toLowerCase(), "");
						}
						targetFilePathUrl = String.format("%s%s/%s/%s/%s", targetFilePathUrl, targetFileName, ingest.getUuid(), ingest.getVersion(), ingestExec.getVersion());
					} else {
						if (targetExtension.toLowerCase().equalsIgnoreCase(FileType.CSV.toString())) {
							targetFileName = targetFileName.toLowerCase().endsWith("."+FileType.CSV.toString().toLowerCase()) ? targetFileName : targetFileName.concat("."+FileType.CSV.toString().toLowerCase());
						} else if (targetExtension.toLowerCase().equalsIgnoreCase(FileType.TSV.toString())) {
							targetFileName = targetFileName.toLowerCase().endsWith("."+FileType.TSV.toString().toLowerCase()) ? targetFileName : targetFileName.concat("."+FileType.TSV.toString().toLowerCase());
						}
						else if (targetExtension.toLowerCase().equalsIgnoreCase(FileType.PSV.toString())) {
							targetFileName = targetFileName.toLowerCase().endsWith("."+FileType.PSV.toString().toLowerCase()) ? targetFileName : targetFileName.concat("."+FileType.PSV.toString().toLowerCase());
						} else {
							logger.info("Invalid target format type : "+ingest.getTargetExtn().toString());						
						}
						targetFilePathUrl = targetDS.getPath().concat(targetFileName);
					}		
					
					String sourceHeader = ingestServiceImpl.resolveHeader(ingest.getSourceHeader());
					String targetHeader = ingestServiceImpl.resolveHeader(ingest.getTargetHeader()); 
					
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, location, Helper.getDelimetrByFormat(ingest.getSourceFormat()), sourceHeader, appUuid, true);
					
					//adding version column to data
//					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
										
					//map schema to source mappedAttrs	
					if(colAliaseNames != null) {
						if(sourceHeader.equalsIgnoreCase("false")) {
							rsHolder = sparkExecutor.applyIngestDatapodSchema(rsHolder, targetDp, colAliaseNames.toArray(new String[colAliaseNames.size()]), tableName, true);
						}
						rsHolder = sparkExecutor.mapSchema(rsHolder, query, colAliaseNames, tableName, false);
					} 
					
					//applying target schema to df
					if(targetHeader.equalsIgnoreCase("true") && colAliaseNames != null) {
						Map<String, String> resolvedTargetAttrMap = resolveMappedAttributes(ingest.getAttributeMap(), false);
						String[] targetCols = resolvedTargetAttrMap.keySet().toArray(new String[resolvedTargetAttrMap.keySet().size()]);
						rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, targetCols, tableName, false);
					}					
					
					String saveMode = null;
					if(ingest.getSaveMode() != null) {
						saveMode = ingest.getSaveMode().toString();
					} else {
						saveMode = SaveMode.OVERWRITE.toString();
					}
					
					//writing to target		
					if(targetDS.getAccess().equalsIgnoreCase(ExecContext.S3.toString())) {
						String tempDirName = "temp_dir".concat("/").concat(ingestExec.getUuid());
						String defaultPath = targetDS.getPath();
						defaultPath = defaultPath.endsWith("/") ? defaultPath : defaultPath.concat("/");
						String tempDir = defaultPath.concat(tempDirName).concat("/");
						
						rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, tempDir,
								tableName, saveMode, ingest.getTargetFormat(), targetHeader);
						
						String[] arrOfStr = defaultPath.split("s3n://", 2);
			        	arrOfStr = arrOfStr[1].split("/", 2);
			        	String bucket_name = arrOfStr[0];
			        	String folderPath = arrOfStr[1];
			        	folderPath = folderPath.endsWith("/") ? folderPath : folderPath.concat("/");
			        	String tempDirPath = folderPath.concat(tempDirName).concat("/");
			        	
						String objPath = commonServiceImpl.findS3ObjectByExtension(bucket_name, tempDirPath, ingest.getTargetExtn());
						targetFilePathUrl = folderPath.concat(ingest.getTargetDetail().getValue()).concat(".").concat(ingest.getTargetExtn().toLowerCase());
						if(!StringUtils.isBlank(objPath)) {
							tempDirPath = tempDirPath.endsWith("/") ? tempDirPath.substring(0, tempDirPath.lastIndexOf("/")) : tempDirPath;
							if(commonServiceImpl.copyS3Object(bucket_name, objPath, bucket_name, targetFilePathUrl)) {
								commonServiceImpl.deleteS3Object(bucket_name, tempDirPath);
							} else {
								commonServiceImpl.deleteS3Object(bucket_name, tempDirPath);
								throw new RuntimeException("Can not write into AWS");
							}
						} else {
							commonServiceImpl.deleteS3Object(bucket_name, tempDirPath);
							throw new NullPointerException("Can not write into AWS");
						}
					} else {
						String tempDirPath = commonServiceImpl.getConfigValue("framework.temp.path");
						String tempDirLocation = tempDirPath.endsWith("/")
								? "file://" + tempDirPath + ingestExec.getUuid() + "/" + ingestExec.getVersion() + "/"
								: "file://" + tempDirPath.concat("/") + ingestExec.getUuid() + "/"
										+ ingestExec.getVersion() + "/";
						logger.info("temporary location: " + tempDirLocation);
						rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, tempDirLocation, tableName,
								saveMode, ingest.getTargetFormat(), targetHeader);

						if (!ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
							try {
								tempDirLocation = tempDirPath.endsWith("/")
										? tempDirPath + ingestExec.getUuid() + "/" + ingestExec.getVersion() + "/"
										: tempDirPath.concat("/") + ingestExec.getUuid() + "/" + ingestExec.getVersion()
												+ "/";
								String srcFilePath = ingestServiceImpl.getFileNameFromDir(tempDirLocation,
										ingest.getTargetExtn(), ingest.getTargetFormat());
								ingestServiceImpl.moveFileTOFileOrDir(srcFilePath, targetFilePathUrl, false);
							} catch (Exception e) {
								//e.printStackTrace();
							} finally {
								String dirPathToBeDeleted = tempDirPath.endsWith("/")
										? tempDirPath + ingestExec.getUuid()
										: tempDirPath.concat("/") + ingestExec.getUuid();
								ingestServiceImpl.deleteFileOrDirectory(dirPathToBeDeleted, true);
							}
						} else {
							try {
								String targetDirName = ingest.getTargetDetail().getValue();
								String targetDirPath = targetDS.getPath();
								targetDirPath = targetDirPath.endsWith("/") ? (targetDirPath + targetDirName + "/")
										: (targetDirPath + "/" + targetDirName + "/");

								ingestServiceImpl.deleteFileOrDirectory(targetDirPath, true);
								tempDirLocation = tempDirPath.endsWith("/")
										? tempDirPath + ingestExec.getUuid() + "/" + ingestExec.getVersion() + "/"
										: tempDirPath.concat("/") + ingestExec.getUuid() + "/" + ingestExec.getVersion()
												+ "/";
								String srcFilePath = ingestServiceImpl.getFileNameFromDir(tempDirLocation,
										ingest.getTargetExtn(), ingest.getTargetFormat());
								ingestServiceImpl.moveFileTOFileOrDir(srcFilePath, targetFilePathUrl, true);
							} catch (Exception e) {
// TODO: handle exception
							} finally {
								String dirPathToBeDeleted = tempDirPath.endsWith("/")
										? tempDirPath + ingestExec.getUuid()
										: tempDirPath.concat("/") + ingestExec.getUuid();
								ingestServiceImpl.deleteFileOrDirectory(dirPathToBeDeleted, true);
							}
						}
						
					}
					countRows = rsHolder.getCountRows();
				} else if(ingestionType.equals(IngestionType.FILETOTABLE)) { 
					if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString()) 
							&& targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
						//this is export block from HDFS to HIVE

						logger.info("this is export block from HDFS to HIVE");
						tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
						String fileName = ingest.getSourceDetail().getValue();			
						
						//sourceFilePathUrl = String.format("%s/%s/%s", "hdfs://"+sourceDS.getHost()+":8020", sourceDS.getPath(), fileName);
						sourceFilePathUrl = String.format("%s/%s/%s", commonServiceImpl.getConfigValue("hive.fs.default.name"), sourceDS.getPath(), fileName);
						if(sourceFilePathUrl.contains(".db")) {
							sourceFilePathUrl = sourceFilePathUrl.replaceAll(".db", "");
						}
						logger.info("sourceFilePathUrl: "+sourceFilePathUrl);
						String sourceHeader = ingestServiceImpl.resolveHeader(ingest.getSourceHeader());
						List<String> location = new ArrayList<>();
						location.add(sourceFilePathUrl);
						//reading from source
						ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, location, Helper.getDelimetrByFormat(ingest.getSourceFormat()), sourceHeader, appUuid, true);
						rsHolder.setTableName(targetDS.getDbname()+"."+targetDp.getName());
						
						//adding version column data
//						rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
												
						//map schema to source mappedAttrs	
						if(colAliaseNames != null) {
							if(sourceHeader.equalsIgnoreCase("false")) {
								rsHolder = sparkExecutor.applyIngestDatapodSchema(rsHolder, targetDp, colAliaseNames.toArray(new String[colAliaseNames.size()]), tableName, true);
							}
							rsHolder = sparkExecutor.mapSchema(rsHolder, query, colAliaseNames, tableName, false);
						} 
						
						//applying target schema to df
						rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, null, tableName, false);		
						
						//writing to target
						sparkExecutor.persistDataframe(rsHolder, targetDS, targetDp, ingest.getSaveMode().toString());
						countRows = rsHolder.getCountRows();
					} else if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
						//this is export block from HDFS to Table
						logger.info("this is export block from HDFS to Table");
						tableName = ingest.getSourceDetail().getValue();
						String sourceDir = String.format("%s/%s", sourceDS.getPath(), tableName);					
						if(sourceDir.contains(".db")) {
							sourceDir = sourceDir.replaceAll(".db", "");
						}
						
						SqoopInput sqoopInput = new SqoopInput();
						sqoopInput.setHiveImport(false);
						sqoopInput.setImportIntended(false);
						sqoopInput.setSourceDs(sourceDS);
						sqoopInput.setTargetDs(targetDS);
						sqoopInput.setSourceDirectory(sourceDir);
						sqoopInput.setTable(targetDp.getName());
						sqoopInput.setAppendMode(ingest.getSaveMode().equals(com.inferyx.framework.enums.SaveMode.APPEND));
						sqoopInput.setExportDir(sourceDir);
						tableName = targetDp.getName();					
						sqoopInput.setIncrementalMode(SqoopIncrementalMode.AppendRows);
//						if(incrLastValue != null) {
//							sqoopInput.setIncrementalTestColumn(incrColName);
//							sqoopInput.setIncrementalLastValue(incrLastValue);
//						}
						targetFilePathUrl = targetFilePathUrl+ingest.getSourceDetail().getValue();
						Map<String, String> inputParams = null;
						if(ingest.getRunParams() != null) {
							inputParams = ingestServiceImpl.getRunParams(ingest.getRunParams());
						}
						sqoopExecutor.execute(sqoopInput, inputParams);
					} else if(targetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
						//this is export block from local file to local Table(i.e file)

						logger.info("Query : " + query);
						
						tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
						
//						String targetFileName = ingestServiceImpl.generateFileName(targetDp.getName(), ingest.getTargetExtn(), ingest.getTargetFormat());
//						String targetExtension = ingest.getTargetExtn();
//						
//						if(targetExtension != null) {
//							targetExtension = targetExtension.startsWith(".") ? targetExtension.substring(1) : targetExtension;
//						} else {
//							targetExtension = ingest.getTargetFormat();
//						}
//						
//						if(targetExtension.equalsIgnoreCase(FileType.PARQUET.toString())) {
//							targetFilePathUrl = targetDS.getPath();
//							if(targetFileName.endsWith("."+FileType.PARQUET)) {
//								targetFileName = targetFileName.replace("."+FileType.PARQUET, "");
//							} else if(targetFileName.endsWith("."+FileType.PARQUET.toString().toLowerCase())) {
//								targetFileName = targetFileName.replace("."+FileType.PARQUET.toString().toLowerCase(), "");
//							}
//							targetFilePathUrl = String.format("%s%s/%s/%s/%s", targetFilePathUrl, targetFileName, targetDp.getUuid(), targetDp.getVersion(), ingestExec.getVersion());
//						} else {
//							if (targetExtension.toLowerCase().equalsIgnoreCase(FileType.CSV.toString())) {
//								targetFileName = targetFileName.toLowerCase().endsWith("."+FileType.CSV.toString().toLowerCase()) ? targetFileName : targetFileName.concat("."+FileType.CSV.toString().toLowerCase());
//							} else if (targetExtension.toLowerCase().equalsIgnoreCase(FileType.TSV.toString())) {
//								targetFileName = targetFileName.toLowerCase().endsWith("."+FileType.TSV.toString().toLowerCase()) ? targetFileName : targetFileName.concat("."+FileType.TSV.toString().toLowerCase());
//							}
//							else if (targetExtension.toLowerCase().equalsIgnoreCase(FileType.PSV.toString())) {
//								targetFileName = targetFileName.toLowerCase().endsWith("."+FileType.PSV.toString().toLowerCase()) ? targetFileName : targetFileName.concat("."+FileType.PSV.toString().toLowerCase());
//							} else {
//								logger.info("Invalid target format type : "+ingest.getTargetExtn().toString());						
//							}
//							targetFilePathUrl = targetDS.getPath().concat(targetFileName);
//						}		
						
						targetFilePathUrl = String.format("%s%s/%s/%s", targetFilePathUrl, targetDp.getUuid(), targetDp.getVersion(), ingestExec.getVersion());
						
						String sourceHeader = ingestServiceImpl.resolveHeader(ingest.getSourceHeader());
						String targetHeader = ingestServiceImpl.resolveHeader(ingest.getTargetHeader()); 
						
						//reading from source
						ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, location, Helper.getDelimetrByFormat(ingest.getSourceFormat()), sourceHeader, appUuid, true);
//						query = ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, otherParams, new HashSet<>(), execParams, runMode);
															
						//map schema to source mappedAttrs	
						if(colAliaseNames != null) {
							if(sourceHeader.equalsIgnoreCase("false")) {
								rsHolder = sparkExecutor.applyIngestDatapodSchema(rsHolder, targetDp, colAliaseNames.toArray(new String[colAliaseNames.size()]), tableName, true);
							}
							rsHolder = sparkExecutor.mapSchema(rsHolder, query, colAliaseNames, tableName, false);
						} 
						
						//applying target schema to df
						if(targetHeader.equalsIgnoreCase("true") && colAliaseNames != null) {
							Map<String, String> resolvedTargetAttrMap = resolveMappedAttributes(ingest.getAttributeMap(), false);
							String[] targetCols = resolvedTargetAttrMap.keySet().toArray(new String[resolvedTargetAttrMap.keySet().size()]);
							rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, targetCols, tableName, false);
						}					
						
						/*query = ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, otherParams, new HashSet<>(), execParams, runMode);
						logger.info("Before query : " + query);
						rsHolder = sparkExecutor.executeSql(query);
						logger.info("After query " );*/
						
						String saveMode = null;
						if(ingest.getSaveMode() != null) {
							saveMode = ingest.getSaveMode().toString();
						} else {
							saveMode = SaveMode.OVERWRITE.toString();
						}
						sparkExecutor.registerAndPersistDataframe(rsHolder, targetDp, saveMode, targetFilePathUrl, tableName, targetHeader, false);
//						String tempDirPath = Helper.getPropertyValue("framework.temp.path");
//						String tempDirLocation = tempDirPath.endsWith("/") ? "file://"+tempDirPath+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/" : "file://"+tempDirPath.concat("/")+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/";
//						logger.info("temporary location: "+tempDirLocation);
//						
//						//writing to target				
//						rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, tempDirLocation,
//																	tableName, saveMode, ingest.getTargetFormat(), targetHeader);
//						
//						if(!ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
//							try {
//								tempDirLocation = tempDirPath.endsWith("/") ? tempDirPath+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/" : tempDirPath.concat("/")+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/";
//								String srcFilePath = ingestServiceImpl.getFileNameFromDir(tempDirLocation, ingest.getTargetExtn(), ingest.getTargetFormat());
//								ingestServiceImpl.moveFileTOFileOrDir(srcFilePath, targetFilePathUrl, false);
//							} catch (Exception e) {
////									e.printStackTrace();
//							} finally {
//								String dirPathToBeDeleted = tempDirPath.endsWith("/") ? tempDirPath+ingestExec.getUuid() : tempDirPath.concat("/")+ingestExec.getUuid();
//								ingestServiceImpl.deleteFileOrDirectory(dirPathToBeDeleted, true);
//							}						
//						} else {
//							try {
//								String targetDirName = ingest.getTargetDetail().getValue();
//								String targetDirPath = targetDS.getPath();
//								targetDirPath = targetDirPath.endsWith("/") ? (targetDirPath + targetDirName + "/") : (targetDirPath + "/" + targetDirName + "/");
//					
//								ingestServiceImpl.deleteFileOrDirectory(targetDirPath, true);
//								tempDirLocation = tempDirPath.endsWith("/") ? tempDirPath+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/" : tempDirPath.concat("/")+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/";
//								String srcFilePath = ingestServiceImpl.getFileNameFromDir(tempDirLocation, ingest.getTargetExtn(), ingest.getTargetFormat());
//								ingestServiceImpl.moveFileTOFileOrDir(srcFilePath, targetFilePathUrl, true);
//							} catch (Exception e) {
//								// TODO: handle exception
//							} finally {
//								String dirPathToBeDeleted = tempDirPath.endsWith("/") ? tempDirPath+ingestExec.getUuid() : tempDirPath.concat("/")+ingestExec.getUuid();
//								ingestServiceImpl.deleteFileOrDirectory(dirPathToBeDeleted, true);
//							}
//						}
						countRows = rsHolder.getCountRows();
					} else {
						//this is export block from local file to Table

						logger.info("this is export block from local file to Table");
						tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
//						List<String> fileNameList = ingestServiceImpl.getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());
//						if(fileNameList == null || fileNameList.isEmpty()) {
//							throw new RuntimeException("File \'"+ingest.getSourceDetail().getValue()+"\' not exist.");
//						}
//						
//						for(String fileName : fileNameList) {
//							sourceFilePathUrl = sourceFilePathUrl + fileName;					
							String sourceHeader = ingestServiceImpl.resolveHeader(ingest.getSourceHeader());
							//reading from source
							ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, location, Helper.getDelimetrByFormat(ingest.getSourceFormat()), sourceHeader, appUuid, true);
							
							
							//adding version column data
//							rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
							
							//map schema to source mappedAttrs	
							if(colAliaseNames != null) {
								if(sourceHeader.equalsIgnoreCase("false")) {
									rsHolder = sparkExecutor.applyIngestDatapodSchema(rsHolder, targetDp, colAliaseNames.toArray(new String[colAliaseNames.size()]), tableName, true);
								}
								rsHolder = sparkExecutor.mapSchema(rsHolder, query, colAliaseNames, tableName, false);
							} 
							
							//applying target schema to df
							rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, null, tableName, true);
							
							//setting target table name
							rsHolder.setTableName(targetDS.getDbname()+"."+targetDp.getName());
							
							//writing to target
							sparkExecutor.persistDataframe(rsHolder, targetDS, targetDp, ingest.getSaveMode().toString());
							countRows = rsHolder.getCountRows();
//						}
					}
				} else if(ingestionType.equals(IngestionType.TABLETOFILE)) { 								
					if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString()) 
							&& targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
						//this is export block from Hive to HDFS

						logger.info("this is export block from Hive to HDFS");
						String sourceDir = String.format("%s/%s", sourceDS.getPath(), sourceDp != null ? sourceDp.getName() : sourceDataSet.getName());
						
//						targetFilePathUrl = String.format("%s/%s/%s/%s/%s/%s", Helper.getPropertyValue("hive.fs.default.name"), targetDS.getPath(), ingest.getUuid(), ingest.getVersion(), ingestExec.getVersion(), ingest.getTargetDetail().getValue());
						targetFilePathUrl = String.format("%s/%s/%s", Helper.getPropertyValue("hive.fs.default.name"), targetDS.getPath(), ingest.getTargetDetail().getValue());
						if(targetFilePathUrl.contains(".db")) {
							targetFilePathUrl = targetFilePathUrl.replaceAll(".db", "");
						}
						logger.info("sourceDir : " + sourceDir);
						logger.info("targetDir : " + targetFilePathUrl);
						
//						String sourceTableName = sourceDS.getDbname() +"."+sourceDp.getName();
						
						tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
						
//						String sql = ingestServiceImpl.generateSqlByDatasource(targetDS, sourceTableName, incrColName, incrLastValue, 0);
						colAliaseNames = getMappedAttrAliaseName(ingest.getAttributeMap(), false);
//						String sql = getSqlQuery(mappedAttrs, colAliaseNames.toArray(new String[colAliaseNames.size()]), sourceTableName, incrColName, latestIncrLastValue);;
						ResultSetHolder rsHolder = sparkExecutor.executeSqlByDatasource(query, sourceDS, appUuid);
						
						//registering temp table of source
//						sparkExecutor.registerDataFrameAsTable(rsHolder, tableName);
						
						//adding version column data
//						rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
						
						String targetHeader = ingestServiceImpl.resolveHeader(ingest.getTargetHeader());
						
						//writing to target				
						rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, targetFilePathUrl, tableName, ingest.getSaveMode().toString(), ingest.getTargetFormat(), targetHeader);
						countRows = rsHolder.getCountRows();
//						targetFilePathUrl = null;
					} else if(targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
						//this is import block from Table to HDFS

						logger.info("this is import block from Table to HDFS");
						SqoopInput sqoopInput = new SqoopInput();
						sqoopInput.setSourceDs(sourceDS);
						sqoopInput.setTargetDs(targetDS);
						sqoopInput.setIncrementalMode(SqoopIncrementalMode.AppendRows);
						sqoopInput.setHiveImport(false);
						sqoopInput.setImportIntended(true);
						String sourceDir = String.format("%s/%s", sourceDS.getPath(), sourceDp != null ? sourceDp.getName() : sourceDataSet.getName());
						String targetDir = String.format("%s/%s", targetDS.getPath(), ingest.getTargetDetail().getValue());
						if(targetDir.contains(".db")) {
							targetDir = targetDir.replaceAll(".db", "");
						}
						logger.info("sourceDir : " + sourceDir);
						logger.info("targetDir : " + targetDir);
//						sqoopInput.setExportDir(targetDir);
//						sqoopInput.setSourceDirectory(sourceDir);
						sqoopInput.setWarehouseDirectory(targetDir);
						sqoopInput.setDeleteMode(true);
						if(mappedAttrs != null && areAllAttrs && sourceDp != null) {
							sqoopInput.setTable(sourceDp != null ? sourceDp.getName() : sourceDataSet.getName());
						}
						sqoopInput.setAppendMode(ingest.getSaveMode().equals(com.inferyx.framework.enums.SaveMode.APPEND));
						sqoopInput.setSplitByCol(ingestServiceImpl.getColName(sourceDp, sourceDataSet, ingest.getSplitBy()));
//						if(mappedAttrs != null && areAllAttrs) {
//							sqoopInput.setAttributeMap(mappedAttrs);
//							if(!whereClause.isEmpty()) {
//								sqoopInput.setWhereClause(whereClause);
//							}
//						} else if(mappedAttrs != null && !areAllAttrs) {
//							sqoopInput.setTargetDirectory(targetDir);
//							sqoopInput.setWarehouseDirectory(null);
//							sqoopInput.setSqlQuery(query);
//						}
						setTableOrQuery(sqoopInput, mappedAttrs, areAllAttrs, whereClause, query, targetDir);
//						sqoopInput.setFileLayout(sqoopExecutor.getFileLayout(ingest.getTargetFormat()));
//						if(incrLastValue != null) {
//							sqoopInput.setIncrementalTestColumn(incrColName);
//							sqoopInput.setIncrementalLastValue(incrLastValue);
//						}
						targetFilePathUrl = targetFilePathUrl+(sourceDp != null ? sourceDp.getName() : sourceDataSet.getName());
						Map<String, String> inputParams = null;
						if(ingest.getRunParams() != null) {
							inputParams = ingestServiceImpl.getRunParams(ingest.getRunParams());
						}
						tableName = sourceDp != null ? sourceDp.getName() : sourceDataSet.getName();
						sqoopExecutor.execute(sqoopInput, inputParams);
					} else {
						//this is export block from table to local file

						logger.info("this is export block from Hive table to local file");
						String sourceDir = String.format("%s/%s", sourceDS.getPath(), (sourceDp != null ? sourceDp.getName() : sourceDataSet.getName()));
						
						String targetFileName = ingestServiceImpl.generateFileName(ingest.getTargetDetail().getValue(), ingest.getTargetExtn(), ingest.getTargetFormat());
						String targetExtension = ingest.getTargetExtn();
						
						if(targetExtension != null) {
							targetExtension = targetExtension.startsWith(".") ? targetExtension.substring(1) : targetExtension;
						} else {
							targetExtension = ingest.getTargetFormat();
						}
						
						if(targetExtension.equalsIgnoreCase(FileType.PARQUET.toString())) {
							targetFilePathUrl = targetDS.getPath();
							if(targetFileName.endsWith("."+FileType.PARQUET)) {
								targetFileName = targetFileName.replace("."+FileType.PARQUET, "");
							} else if(targetFileName.endsWith("."+FileType.PARQUET.toString().toLowerCase())) {
								targetFileName = targetFileName.replace("."+FileType.PARQUET.toString().toLowerCase(), "");
							}
							targetFilePathUrl = String.format("%s%s/%s/%s/%s", targetFilePathUrl, targetFileName, ingest.getUuid(), ingest.getVersion(), ingestExec.getVersion());
						} else {
							if (targetExtension.toLowerCase().equalsIgnoreCase(FileType.CSV.toString())) {
								targetFileName = targetFileName.toLowerCase().endsWith("."+FileType.CSV.toString().toLowerCase()) ? targetFileName : targetFileName.concat("."+FileType.CSV.toString().toLowerCase());
							}
							else if (targetExtension.toLowerCase().equalsIgnoreCase(FileType.TSV.toString())) {
								targetFileName = targetFileName.toLowerCase().endsWith("."+FileType.TSV.toString().toLowerCase()) ? targetFileName : targetFileName.concat("."+FileType.TSV.toString().toLowerCase());
							}
							else if (targetExtension.toLowerCase().equalsIgnoreCase(FileType.PSV.toString())) {
								targetFileName = targetFileName.toLowerCase().endsWith("."+FileType.PSV.toString().toLowerCase()) ? targetFileName : targetFileName.concat("."+FileType.PSV.toString().toLowerCase());
							}
							else {
								logger.info("Invalid target format type : "+ingest.getTargetExtn().toString());						
							}						
							targetFilePathUrl = targetFilePathUrl.concat(targetFileName);
							targetFilePathUrl = targetDS.getPath().concat(targetFileName);
						}					
						
						if(targetFilePathUrl.contains(".db")) {
							targetFilePathUrl = targetFilePathUrl.replaceAll(".db", "");
						}
						
						logger.info("sourceDir : " + sourceDir);
						logger.info("targetDir : " + targetFilePathUrl);
						
						String sourceTableName = sourceDS.getDbname() +"."+(sourceDp != null ? sourceDp.getName() : sourceDataSet.getName());

						tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
						
						String sql = ingestServiceImpl.generateSqlByDatasource(sourceDS, sourceTableName, incrColName, incrLastValue, 0);
						ResultSetHolder rsHolder = sparkExecutor.executeAndRegisterByDatasource(sql, tableName, sourceDS, appUuid);
						
						String targetHeader = ingestServiceImpl.resolveHeader(ingest.getTargetHeader()); 
						
//						query = ingestOperator.generateSQL(ingest, sourceTableName, incrColName, incrLastValue, null, otherParams, new HashSet<>(), execParams, runMode);
						//map schema to source mappedAttrs	
						if(colAliaseNames != null) {
							rsHolder = sparkExecutor.mapSchema(rsHolder, null, colAliaseNames, tableName, false);
						}
						
						//applying target schema to df
						if(targetHeader.equalsIgnoreCase("true") && colAliaseNames != null) {
							Map<String, String> resolvedTargetAttrMap = resolveMappedAttributes(ingest.getAttributeMap(), false);
							String[] targetCols = resolvedTargetAttrMap.keySet().toArray(new String[resolvedTargetAttrMap.keySet().size()]);
							rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, targetCols, tableName, false);
						}
						//registering temp table of source
//						sparkExecutor.registerDataFrameAsTable(rsHolder, tableName);
						
						//adding version column data
//						rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());

						String saveMode = null;
						if(ingest.getSaveMode() != null) {
							saveMode = ingest.getSaveMode().toString();
						} else {
							saveMode = SaveMode.OVERWRITE.toString();
						}
						
						if(targetDS.getAccess().equalsIgnoreCase(ExecContext.S3.toString())) {							
							String tempDirName = "temp_dir".concat("/").concat(ingestExec.getUuid());
							String defaultPath = targetDS.getPath();
							defaultPath = defaultPath.endsWith("/") ? defaultPath : defaultPath.concat("/");
							String tempDir = defaultPath.concat(tempDirName).concat("/");
							
							rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, tempDir,
									tableName, saveMode, ingest.getTargetFormat(), targetHeader);
							
							String[] arrOfStr = defaultPath.split("s3n://", 2);
				        	arrOfStr = arrOfStr[1].split("/", 2);
				        	String bucket_name = arrOfStr[0];
				        	String folderPath = arrOfStr[1];
				        	folderPath = folderPath.endsWith("/") ? folderPath : folderPath.concat("/");
				        	String tempDirPath = folderPath.concat(tempDirName).concat("/");
				        	
							String objPath = commonServiceImpl.findS3ObjectByExtension(bucket_name, tempDirPath, ingest.getTargetExtn());
							targetFilePathUrl = folderPath.concat(ingest.getTargetDetail().getValue()).concat(".").concat(ingest.getTargetExtn().toLowerCase());
							if(!StringUtils.isBlank(objPath)) {
								tempDirPath = tempDirPath.endsWith("/") ? tempDirPath.substring(0, tempDirPath.lastIndexOf("/")) : tempDirPath;
								if(commonServiceImpl.copyS3Object(bucket_name, objPath, bucket_name, targetFilePathUrl)) {
									commonServiceImpl.deleteS3Object(bucket_name, tempDirPath);
								} else {
									commonServiceImpl.deleteS3Object(bucket_name, tempDirPath);
									throw new RuntimeException("Can not write into AWS");
								}
							} else {
								commonServiceImpl.deleteS3Object(bucket_name, tempDirPath);
								throw new NullPointerException("Can not write into AWS");
							}
						} else {
							String tempDirPath = Helper.getPropertyValue("framework.temp.path");
							String tempDirLocation = tempDirPath.endsWith("/") ? "file://"+tempDirPath+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/" : "file://"+tempDirPath.concat("/")+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/";
							logger.info("temporary location: "+tempDirLocation);
							
//							rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, 
//									ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString()) ? targetFilePathUrl : tempDirLocation
//											, targetFileName, tableName, saveMode, ingest.getTargetFormat(), targetHeader);
							
							rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, tempDirLocation,
															tableName, saveMode, ingest.getTargetFormat(), targetHeader);
							
							if(!ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
								try {
									tempDirLocation = tempDirPath.endsWith("/") ? tempDirPath+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/" : tempDirPath.concat("/")+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/";
									String srcFilePath = ingestServiceImpl.getFileNameFromDir(tempDirLocation, ingest.getTargetExtn(), ingest.getTargetFormat());
									ingestServiceImpl.moveFileTOFileOrDir(srcFilePath, targetFilePathUrl, false);
								} catch (Exception e) {
//									e.printStackTrace();
								} finally {
									String dirPathToBeDeleted = tempDirPath.endsWith("/") ? tempDirPath+ingestExec.getUuid() : tempDirPath.concat("/")+ingestExec.getUuid();
									ingestServiceImpl.deleteFileOrDirectory(dirPathToBeDeleted, true);
								}						
							} else {
								try {
									String targetDirName = ingest.getTargetDetail().getValue();
									String targetDirPath = targetDS.getPath();
									targetDirPath = targetDirPath.endsWith("/") ? (targetDirPath + targetDirName + "/") : (targetDirPath + "/" + targetDirName + "/");
						
									ingestServiceImpl.deleteFileOrDirectory(targetDirPath, true);
									tempDirLocation = tempDirPath.endsWith("/") ? tempDirPath+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/" : tempDirPath.concat("/")+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/";
									String srcFilePath = ingestServiceImpl.getFileNameFromDir(tempDirLocation, ingest.getTargetExtn(), ingest.getTargetFormat());
									ingestServiceImpl.moveFileTOFileOrDir(srcFilePath, targetFilePathUrl, true);
								} catch (Exception e) {
									// TODO: handle exception
								} finally {
									String dirPathToBeDeleted = tempDirPath.endsWith("/") ? tempDirPath+ingestExec.getUuid() : tempDirPath.concat("/")+ingestExec.getUuid();
									ingestServiceImpl.deleteFileOrDirectory(dirPathToBeDeleted, true);
								}					
							}
						}
						
						
						//writing to target				
//						rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, targetFilePathUrl, ingest.getTargetDetail().getValue(), tableName, ingest.getSaveMode().toString(), ingest.getTargetFormat());
						countRows = rsHolder.getCountRows();
//						targetFilePathUrl = null;
					}
				} else if(ingestionType.equals(IngestionType.TABLETOTABLE)) { 
					SqoopInput sqoopInput = new SqoopInput();
					sqoopInput.setSourceDs(sourceDS);
					sqoopInput.setTargetDs(targetDS);
					String targetDir = targetDS.getPath()+ (targetDS.getPath().endsWith("/") ? targetDp.getName() : "/"+targetDp.getName());
					String sourceDir = sourceDS.getPath();
					logger.info("targetDir : " + targetDir);
					logger.info("sourceDir : " + sourceDir);
					sqoopInput.setIncrementalMode(SqoopIncrementalMode.AppendRows);
					if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
						//this is export block from Hive to other table

						logger.info("this is export block from Hive to other table");
						sourceDir = String.format("%s/%s", sourceDir, (sourceDp != null ? sourceDp.getName() : sourceDataSet.getName()));
						logger.info("sourceDir : " + sourceDir);
						sqoopInput.setExportDir(sourceDir);
						sqoopInput.setSourceDirectory(sourceDir);
						sqoopInput.setHiveImport(false);
						sqoopInput.setImportIntended(false);
						if(targetDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())){
							tableName = targetDp.getName().toUpperCase();
						}else {
							tableName = targetDp.getName();
						}
						sqoopInput.setTable(tableName);
						if (targetDS.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {
							sqoopInput.setConnManagerClassName("org.apache.sqoop.manager.PostgresqlManager");
						}
//						sqoopInput.setHiveTableName(sourceDp.getName());
//						sqoopInput.setOverwriteHiveTable("Y");
//						sqoopInput.setHiveDatabaseName(sourceDS.getDbname());
//						sqoopInput.sethCatTableName(sourceDp.getName());
					} else if(sourceDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())
							|| sourceDS.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {
						//this is import block from ORACLE table to HIVE
						logger.info("this is import block from ORACLE table to HIVE");
						sqoopInput.setOverwriteHiveTable(ingest.getSaveMode().toString());
						if(sourceDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
							if(mappedAttrs != null && areAllAttrs && sourceDp != null) {
								sqoopInput.setTable((sourceDp != null ? sourceDp.getName() : sourceDataSet.getName()).toUpperCase());
							}
						} else if(sourceDS.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {
							sqoopInput.setConnManagerClassName("org.apache.sqoop.manager.PostgresqlManager");
							if(mappedAttrs != null && areAllAttrs && sourceDp != null) {
//								tableName = sourceDS.getDbname() +"."+ sourceDp.getName();
								sqoopInput.setTable(sourceDp != null ? sourceDp.getName() : sourceDataSet.getName());
							}
						}						
//						sqoopInput.setSqlQuery(getSqlQuery(sourceDp.getName(), incrColName, incrLastValue));
						sqoopInput.setHiveImport(true);
						sqoopInput.setImportIntended(true);
						sqoopInput.setWarehouseDirectory(targetDir);
						sqoopInput.setDeleteMode(true);
						sqoopInput.setFieldsTerminatedBy(',');
						sqoopInput.setLinesTerminatedBy('\n');
						sqoopInput.setHiveTableName(targetDp.getName());
						sqoopInput.setHiveDatabaseName(targetDS.getDbname());
//						sqoopInput.setHCatalogTableName(targetDp.getName());
//						sqoopInput.setHCatalogDatabaseName(targetDS.getDbname());
					} /*else if(sourceDS.getType().equalsIgnoreCase(ExecContext.POSTGRES.toString())) {
						//this is import block from POSTGRES to HIVE table

						logger.info("this is export block from POSTGRES to other table");
//						sourceDir = String.format("%s/%s", sourceDir, sourceDp.getName());
//						logger.info("sourceDir : " + sourceDir);
						sqoopInput.setWarehouseDirectory(targetDir);
						sqoopInput.setHiveImport(true);
						sqoopInput.setImportIntended(true);
						sqoopInput.setDeleteMode(true);
						sqoopInput.setFieldsTerminatedBy(',');
						sqoopInput.setLinesTerminatedBy('\n');
						sqoopInput.setConnManagerClassName("org.apache.sqoop.manager.PostgresqlManager");
						if(mappedAttrs != null && areAllAttrs) {
//							tableName = sourceDS.getDbname() +"."+ sourceDp.getName();
							sqoopInput.setTable(sourceDp.getName());
						}
						
						sqoopInput.setHiveTableName(targetDp.getName());
						sqoopInput.setHiveDatabaseName(targetDS.getDbname());
//						sqoopInput.sethCatTableName(sourceDp.getName());
					}*/ else if(targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
						//this is import block from other table to HIVE

						logger.info("this is import block from other table to HIVE");
						sqoopInput.setOverwriteHiveTable(ingest.getSaveMode().toString());
						if(mappedAttrs != null && areAllAttrs && sourceDp != null) {
							sqoopInput.setTable((sourceDp != null ? sourceDp.getName() : sourceDataSet.getName()));
						} 
						sqoopInput.setHiveImport(true);
						sqoopInput.setImportIntended(true);
						sqoopInput.setWarehouseDirectory(targetDir);
						sqoopInput.setHiveTableName(targetDp.getName());
						sqoopInput.setHiveDatabaseName(targetDS.getDbname());
						sqoopInput.setDeleteMode(true);
						sqoopInput.setFieldsTerminatedBy(',');
						sqoopInput.setLinesTerminatedBy('\n');
//						sqoopInput.setHCatalogTableName(targetDp.getName());
//						sqoopInput.setHCatalogDatabaseName(targetDS.getDbname());
//						sqoopInput.sethCatalogPartitionKeys(hCatalogPartitionKeys);
//						sqoopInput.sethCatalogPartitionValues(hCatalogPartitionValues);
						tableName = targetDp.getName();
					} 

					sqoopInput.setSplitByCol(ingestServiceImpl.getColName(sourceDp, sourceDataSet, ingest.getSplitBy()));
//					if(mappedAttrs != null && areAllAttrs) {
//						sqoopInput.setAttributeMap(mappedAttrs);
//						if(!whereClause.isEmpty()) {
//							sqoopInput.setWhereClause(whereClause);
//						}
//					} else if(mappedAttrs != null && !areAllAttrs) {
//						sqoopInput.setTargetDirectory(targetDir);
//						sqoopInput.setWarehouseDirectory(null);
//						sqoopInput.setSqlQuery(query);
//					}
					setTableOrQuery(sqoopInput, mappedAttrs, areAllAttrs, whereClause, query, targetDir);
					sqoopInput.setAppendMode(ingest.getSaveMode().equals(com.inferyx.framework.enums.SaveMode.APPEND));
//					if(incrLastValue != null) {
//						sqoopInput.setIncrementalTestColumn(incrColName);
//						if(!sourceDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
//							sqoopInput.setIncrementalLastValue(incrLastValue);
//						}
//					} else if(incrLastValue == null && sourceDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
//						sqoopInput.setIncrementalTestColumn(incrColName);
//					}
					
					setIncremental(sqoopInput, incrLastValue, incrColName);
					
					targetFilePathUrl = targetFilePathUrl+"/"+(sourceDp != null ? sourceDp.getName() : sourceDataSet.getName());
					Map<String, String> inputParams = null;
					if(ingest.getRunParams() != null) {
						inputParams = ingestServiceImpl.getRunParams(ingest.getRunParams());
					}
					
					sqoopExecutor.execute(sqoopInput, inputParams);
				} else if(ingestionType.equals(IngestionType.STREAMTOTABLE)) { 
						StreamInput streamInput = getKafkaStreamInput();
						streamInput.setIngestionType(IngestionType.STREAMTOTABLE.toString());
						String targetTableName = null;
						if(targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())
								|| targetDS.getType().equalsIgnoreCase(ExecContext.IMPALA.toString())) {
							targetTableName = targetDp.getName();
						} else {
							targetTableName = targetDS.getDbname()+"."+targetDp.getName();
						}
						streamInput.setTargetTableName(targetTableName);
						SaveMode saveMode = ingest.getSaveMode();
						if(saveMode == null) {
							saveMode = SaveMode.APPEND;
						}
						streamInput.setSaveMode(saveMode.toString());
						streamInput.setTargetType(targetDS.getType());
						streamInput.setSourceDS(sourceDS);
						streamInput.setTargetDS(targetDS);
						streamInput.setSourceDP(sourceDp);
						streamInput.setSourceDataSet(sourceDataSet);
						streamInput.setTargetDP(targetDp);
						streamInput.setTopicName(ingest.getSourceDetail().getValue());
						JavaInputDStream stream = sparkStreamingExecutor.stream(sourceDS, streamInput);
						sparkStreamingExecutor.write(streamInput, stream);
						new Thread(new Runnable() {
							@Override
							public void run() {
								sparkStreamingExecutor.start(sourceDS);							
							}
						}).start();
				} else if(ingestionType.equals(IngestionType.STREAMTOFILE)) { 
					String url = null;
					if(targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
						url = "hdfs://";
					} else {
						url = "file://";
					}
					String targetDir = url+targetDS.getPath();
					targetDir = targetDir.endsWith("/") ? targetDir+ingest.getTargetDetail().getValue() : targetDir+"/"+ingest.getTargetDetail().getValue(); 
					if(targetDir.contains(".db")) {
						targetDir = targetDir.replaceAll(".db", "");
					}
					StreamInput streamInput = getKafkaStreamInput();
//					String targetTableName = targetDS.getDbname()+"."+targetDp.getName();
//					streamInput.setTargetTableName(targetTableName);
					streamInput.setIngestionType(IngestionType.STREAMTOFILE.toString());
					SaveMode saveMode = ingest.getSaveMode();
					if(saveMode == null) {
						saveMode = SaveMode.APPEND;
					}
					streamInput.setSaveMode(saveMode.toString());
					streamInput.setTargetType(targetDS.getType());
					streamInput.setSourceDS(sourceDS);
					streamInput.setTargetDS(targetDS);
					streamInput.setSourceDP(sourceDp);
					streamInput.setSourceDataSet(sourceDataSet);
					streamInput.setTargetDP(targetDp);
					streamInput.setTopicName(ingest.getSourceDetail().getValue());
					streamInput.setTargetDir(targetDir);
					streamInput.setFileFormat(ingest.getTargetFormat());
					JavaInputDStream stream = sparkStreamingExecutor.stream(sourceDS, streamInput);
					sparkStreamingExecutor.write(streamInput, stream);
					new Thread(new Runnable() {
						@Override
						public void run() {
							sparkStreamingExecutor.start(sourceDS);							
						}
					}).start();
				}
				
//				if(latestIncrLastValue != null) {
//					ingestExec.setLastIncrValue(latestIncrLastValue);
//					commonServiceImpl.save(MetaType.ingestExec.toString(), ingestExec);
//				}

				MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
				MetaIdentifier datapodKey = null;
				if(targetDp != null) {
					datapodKey = new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion());
				} else {
					datapodKey = new MetaIdentifier(MetaType.ingest, ingest.getUuid(), ingest.getVersion());
				}
				ingestServiceImpl.persistDatastore(tableName, targetFilePathUrl, resultRef, datapodKey, ingestExec, countRows, runMode);
				
				ingestExec.setResult(resultRef);
				ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.COMPLETED);
			}			
		} catch (Exception e) {
			e.printStackTrace();
//			ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.FAILED);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

//			if(message != null && message.toLowerCase().contains("duplicate entry")) {
//				message = "Duplicate entry/entries found for primary key(s).";
//			} else if(message != null && message.toLowerCase().contains("No change in incremental param hence skipping execution.")) {
//				message = "No change in incremental param hence skipping execution.";
//			}
//			commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Ingest execution FAILED.");
			throw new RuntimeException((message != null) ? message : "Ingest execution FAILED.");
		}
		
		return ingestExec;
	}
	
	/**
	 * 
	 * @param ingestionType
	 * @param ingest
	 * @param incrColName
	 * @param incrLastValue
	 * @param areAllAttrs
	 * @return
	 * @throws Exception
	 */
	private String getIngestionQuery (IngestionType ingestionType, 
										Ingest ingest, 
										String incrColName, 
										String incrLastValue, 
										Boolean areAllAttrs) throws Exception {
		if(ingestionType.equals(IngestionType.FILETOFILE)) {
			String tableName = null;
			tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
			return ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, null, new HashSet<>(), null, runMode);
		} else if(sourceDataSet != null) {
			String tableName = sourceDS.getDbname().concat(".").concat(sourceDataSet.getName());					
			return ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, null, new HashSet<>(), null, runMode);
		} else if(!areAllAttrs && sourceDp != null) {
			String tableName = sourceDS.getDbname().concat(".").concat(sourceDp.getName());					
			return ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, null, new HashSet<>(), null, runMode);
		} else if(!areAllAttrs && targetDp != null) {
			String tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());					
			return ingestOperator.generateSQL(ingest, tableName, incrColName, incrLastValue, null, null, new HashSet<>(), null, runMode);
		}
		return null;
	}
	
	/**
	 * 
	 * @param sqoopInput
	 * @param mappedAttrs
	 * @param areAllAttrs
	 * @param whereClause
	 * @param query
	 */
	private void setTableOrQuery(SqoopInput sqoopInput, 
									String[] mappedAttrs, 
									boolean areAllAttrs, 
									String whereClause, 
									String query,
									String targetDir) {
		if(mappedAttrs != null && areAllAttrs && sourceDp !=  null) {
			sqoopInput.setAttributeMap(mappedAttrs);
			if(!whereClause.isEmpty()) {
				sqoopInput.setWhereClause(whereClause);
			}
		} else if(sourceDataSet != null) {
			sqoopInput.setTargetDirectory(targetDir);
			sqoopInput.setWarehouseDirectory(null);
			sqoopInput.setSqlQuery(query);
		} else if(mappedAttrs != null && !areAllAttrs) {
			sqoopInput.setTargetDirectory(targetDir);
			sqoopInput.setWarehouseDirectory(null);
			sqoopInput.setSqlQuery(query);
		}
		
	}
	
	/**
	 * 
	 * @param sqoopInput
	 * @param incrLastValue
	 * @param incrColName
	 */
	private void setIncremental (SqoopInput sqoopInput, 
									String incrLastValue, 
									String incrColName) {
		if(incrLastValue != null) {
			sqoopInput.setIncrementalTestColumn(incrColName);
			if(!sourceDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
				sqoopInput.setIncrementalLastValue(incrLastValue);
			}
		} else if(incrLastValue == null && sourceDS.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
			sqoopInput.setIncrementalTestColumn(incrColName);
		}
	}

	/**
	 * @param values
	 * @return
	 */
	private boolean areAllAttrs(Collection<String> values) {
		boolean isAttribute = false;
		boolean isFormula = false;
		boolean isFunction = false;
		boolean isDatapod = false;
		boolean isDateSet = false;
		
		for(String attrType : values) {
			if(attrType.equalsIgnoreCase(MetaType.attribute.toString())) {
				isAttribute = true;
			} else if(attrType.equalsIgnoreCase(MetaType.formula.toString())) {
				isFormula = true;
			} else if(attrType.equalsIgnoreCase(MetaType.function.toString())) {
				isFunction = true;
			} else if(attrType.equalsIgnoreCase(MetaType.datapod.toString())) {
				isDatapod = true;
			} else if(attrType.equalsIgnoreCase(MetaType.dataset.toString())) {
				isDateSet = true;
			}
		}
		
		if(isAttribute || isFormula || isFunction) {
			return false;
		} else if(isDatapod) {
			return isDatapod;
		} else if(isDateSet) { 
			return isDateSet;
		} else {
			return false;
		}
	}

	/**
	 * @param attributeMaps
	 * @param resolveSource TODO
	 * @return
	 * @throws JsonProcessingException 
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private Map<String, String> resolveMappedAttributes(List<AttributeMap> attributeMaps, boolean resolveSource) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		 Map<String, String> mappedAttributes = new LinkedHashMap<>();
		for(AttributeMap attributeMap : attributeMaps) {
			AttributeRefHolder attrRefHolder = null;
			if(resolveSource) {
				attrRefHolder = attributeMap.getSourceAttr();
			} else{
				attrRefHolder = attributeMap.getTargetAttr();
			}
			String resolvedAttr= ingestServiceImpl.resolveAttribute(attrRefHolder, ingest);
			if(resolvedAttr != null) {
				mappedAttributes.put(resolvedAttr, attrRefHolder.getRef().getType().toString());
			}
		}
		return mappedAttributes;
	}

	private List<String> getMappedAttrAliaseName(List<AttributeMap> attributeMaps, boolean resolveSource) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		 List<String> mappedAttrAlises = new ArrayList<>();
		for(AttributeMap attributeMap : attributeMaps) {
			AttributeRefHolder attrRefHolder = null;
			if(resolveSource) {
				attrRefHolder = attributeMap.getSourceAttr();
			} else{
				attrRefHolder = attributeMap.getTargetAttr();
			}
			String attrAliaseName= ingestServiceImpl.getAttrAliseName(attrRefHolder);
			mappedAttrAlises.add(attrAliaseName);
		}
		return mappedAttrAlises;
	}

	public StreamInput getKafkaStreamInput() {
		// Prepare kafka params
		StreamInput streamInput = new StreamInput<T, K>(); 
		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("key.deserializer", LongDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
		kafkaParams.put("auto.offset.reset", "latest");
		kafkaParams.put("enable.auto.commit", false);
		
		// Prepare run Params 
		Map<String, Object> runParams = new HashMap<>();
		runParams.put("KAFKA_PARAMS", kafkaParams);
		streamInput.setRunParams(runParams);
		return streamInput;
	}
	
	

	public Map<String, String> checkPartitionsByDatapod(Datapod datapod) {
		Map<String, String> partitions = new TreeMap<>();
		try {
			for(Attribute attribute : datapod.getAttributes()) {
				if(attribute.getPartition().equalsIgnoreCase("Y")) {
					partitions.put(attribute.getName(), null);
				}
			}			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return partitions;
	}
}