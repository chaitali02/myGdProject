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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.SqoopInput;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SqoopIncrementalMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.executor.SqoopExecutor;
import com.inferyx.framework.factory.ExecutorFactory;

/**
 * @author Ganesh
 *
 */
@Service
public class IngestServiceImpl extends RuleTemplate {
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private SqoopExecutor sqoopExecutor;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private Helper helper;
	@Autowired
	private IngestExecServiceImpl ingestExecServiceImpl;
	
	static final Logger logger = Logger.getLogger(IngestServiceImpl.class);
	
	public IngestExec create(String ingestUuid, String ingestVersion, ExecParams execParams, IngestExec ingestExec, RunMode runMode) throws Exception {
		try {
			Ingest ingest = (Ingest) commonServiceImpl.getOneByUuidAndVersion(ingestUuid, ingestVersion, MetaType.ingest.toString());
			if(ingestExec == null) {
				MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.ingest, ingestUuid, ingestVersion));
				ingestExec = new IngestExec();
				ingestExec.setDependsOn(dependsOn);
				ingestExec.setBaseEntity();
			}
			
			ingestExec.setExecParams(execParams);
			
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = ingestExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			ingestExec.setName(ingest.getName());
			ingestExec.setAppInfo(ingest.getAppInfo());	
			
			commonServiceImpl.save(MetaType.ingestExec.toString(), ingestExec);
			
			ingestExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
	
			ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.NotStarted);
		} catch (Exception e) {
			logger.error(e);
			ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.Failed);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable ingest.");
			throw new RuntimeException((message != null) ? message : "Can not create executable ingest.");
		}			
		return ingestExec;
	}

	public IngestExec execute(String ingestUuid, String ingestVersion, IngestExec ingestExec, ExecParams execParams, String type, RunMode runMode) throws Exception {
		try {
			Ingest ingest = (Ingest) commonServiceImpl.getOneByUuidAndVersion(ingestUuid, ingestVersion, MetaType.ingest.toString());
			ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.InProgress);
			String appUuid = commonServiceImpl.getApp().getUuid();
			MetaIdentifier sourceDSMI = ingest.getSourceDatasource().getRef();
			Datasource sourceDS = (Datasource) commonServiceImpl.getLatestByUuid(sourceDSMI.getUuid(), sourceDSMI.getType().toString());
			MetaIdentifier targetDSMI = ingest.getTargetDatasource().getRef();
			Datasource targetDS = (Datasource) commonServiceImpl.getLatestByUuid(targetDSMI.getUuid(), targetDSMI.getType().toString());
			long countRows = -1L;
			
			String targetFilePathUrl = helper.getPathByDataSource(targetDS);
			String sourceFilePathUrl = helper.getPathByDataSource(sourceDS);
			
			IngestionType ingestionType = Helper.getIngestionType(ingest.getType());

			MetaIdentifier sourceDpMI = ingest.getSourceDetail().getRef();
			Datapod sourceDp = null;
			String incrLastValue = null;
			String latestIncrLastValue = null;
			String incrColName = null;
			if(sourceDpMI.getUuid() != null) {
				sourceDp = (Datapod) commonServiceImpl.getLatestByUuid(sourceDpMI.getUuid(), sourceDpMI.getType().toString());

				//finding incremental column name
				incrColName = getIncrColName(sourceDp, ingest.getIncrAttr());
				
				//finding last incremental value
				incrLastValue = getLastIncrValue(ingestExec.getUuid());
				
				//finding latest incremental value
				latestIncrLastValue = getNewIncrValue(sourceDp, sourceDS, ingest.getIncrAttr());
			}			
			
			MetaIdentifier targetDpMI = ingest.getTargetDetail().getRef();
			Datapod targetDp = null;
			if(targetDpMI.getUuid() != null) {
				targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
			}
					
			String tableName = null;
			if(ingestionType.equals(IngestionType.FILETOFILE)) { 			
				tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
				List<String> fileNameList = getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());;
				if(fileNameList == null || fileNameList.isEmpty()) {
					throw new RuntimeException("File \'"+ingest.getSourceDetail().getValue()+"\' not exist.");
				}
				
				targetFilePathUrl = String.format("%s%s/%s/%s/%s", targetFilePathUrl, ingest.getUuid(), ingest.getVersion(), ingestExec.getVersion(), ingest.getTargetDetail().getValue());
//				targetFilePathUrl = String.format("%s%s", targetFilePathUrl, ingest.getTargetDetail().getValue());
				for(String fileName : fileNameList) {
					String fileName2 = fileName.substring(0, fileName.lastIndexOf("."));
					sourceFilePathUrl = sourceFilePathUrl + fileName;
					
					String header = resolveHeader(ingest.getHeader());
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, sourceFilePathUrl, Helper.getDelimetrByFormat(ingest.getSourceFormat()), header, appUuid, true);
					
					//adding version column to data
					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
					
					//applying target schema to df
					if(header.equalsIgnoreCase("false") && targetDp != null) {
						rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, tableName, true);
					}
					
					//writing to target				
					rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, targetFilePathUrl, fileName2, tableName, "append", ingest.getTargetFormat());
					countRows = rsHolder.getCountRows();
				}
//				targetFilePathUrl = null;
			} else if(ingestionType.equals(IngestionType.FILETOTABLE)) { 
				if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString()) 
						&& targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					//this is export block from HDFS to HIVE
					tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
					String fileName = ingest.getSourceDetail().getValue();			
					
					sourceFilePathUrl = String.format("%s/%s/%s", Helper.getPropertyValue("hive.fs.default.name"), sourceDS.getPath(), fileName);
					String header = resolveHeader(ingest.getHeader());
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, sourceFilePathUrl, Helper.getDelimetrByFormat(ingest.getSourceFormat()), header, appUuid, true);
					rsHolder.setTableName(targetDS.getDbname()+"."+targetDp.getName());
					
					//adding version column data
					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
					
					//applying target schema to df
					if(header.equalsIgnoreCase("false")) {
						rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, tableName, true);
					}
					
					//writing to target
					sparkExecutor.persistDataframe(rsHolder, targetDS, targetDp);
					countRows = rsHolder.getCountRows();
				} else if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					//this is export block from HDFS to Table
					
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
					sqoopInput.setExportDir(sourceDir);
					tableName = targetDp.getName();					
					sqoopInput.setIncrementalMode(SqoopIncrementalMode.AppendRows);
//					if(incrLastValue != null) {
//						sqoopInput.setIncrementalLastValue(incrLastValue);
//					}
					targetFilePathUrl = targetFilePathUrl+ingest.getSourceDetail().getValue();
					Map<String, String> inputParams = null;
					if(ingest.getRunParams() != null) {
						inputParams = getRunParams(ingest.getRunParams());
					}
					sqoopExecutor.execute(sqoopInput, inputParams);
				} else {
					//this is export block from local file to Table
					tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
					List<String> fileNameList = getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());
					if(fileNameList == null || fileNameList.isEmpty()) {
						throw new RuntimeException("File \'"+ingest.getSourceDetail().getValue()+"\' not exist.");
					}
					
					for(String fileName : fileNameList) {
						sourceFilePathUrl = sourceFilePathUrl + fileName;					
						String header = resolveHeader(ingest.getHeader());
						//reading from source
						ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, sourceFilePathUrl, Helper.getDelimetrByFormat(ingest.getSourceFormat()), header, appUuid, true);
						rsHolder.setTableName(targetDS.getDbname()+"."+targetDp.getName());
						
						//adding version column data
						rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
						
						//applying target schema to df
						if(header.equalsIgnoreCase("false")) {
							rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, tableName, true);
						}
						
						//writing to target
						sparkExecutor.persistDataframe(rsHolder, targetDS, targetDp);
						countRows = rsHolder.getCountRows();
					}
				}
			} else if(ingestionType.equals(IngestionType.TABLETOFILE)) { 								
				if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString()) 
						&& targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					//this is export block from Hive to HDFS
					String sourceDir = String.format("%s/%s", sourceDS.getPath(), sourceDp.getName());
//					if(sourceDir.contains(".db")) {
//						sourceDir = sourceDir.replaceAll(".db", "");
//					}
					
					targetFilePathUrl = String.format("%s/%s/%s/%s/%s/%s", Helper.getPropertyValue("hive.fs.default.name"), targetDS.getPath(), ingest.getUuid(), ingest.getVersion(), ingestExec.getVersion(), ingest.getTargetDetail().getValue());
//					targetFilePathUrl = String.format("%s/%s/%s", Helper.getPropertyValue("hive.fs.default.name"), targetDS.getPath(), ingest.getTargetDetail().getValue());
					logger.info("sourceDir : " + sourceDir);
					logger.info("targetDir : " + targetFilePathUrl);
					
					String sourceTableName = sourceDS.getDbname() +"."+sourceDp.getName();
					
					tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
					
					String sql = generateSqlByDatasource(targetDS, sourceTableName, incrColName, incrLastValue, 0);
					ResultSetHolder rsHolder = sparkExecutor.executeSqlByDatasource(sql, sourceDS, appUuid);
					
					//registering temp table of source
					sparkExecutor.registerDataFrameAsTable(rsHolder, tableName);
					
					//adding version column data
					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());

					//writing to target				
					rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, targetFilePathUrl, ingest.getTargetDetail().getValue(), tableName, "append", ingest.getTargetFormat());
					countRows = rsHolder.getCountRows();
//					targetFilePathUrl = null;
				} else if(targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					//this is import block from Table to HDFS
					SqoopInput sqoopInput = new SqoopInput();
					sqoopInput.setSourceDs(sourceDS);
					sqoopInput.setTargetDs(targetDS);
					sqoopInput.setIncrementalMode(SqoopIncrementalMode.AppendRows);
					sqoopInput.setHiveImport(false);
					sqoopInput.setImportIntended(true);
					String sourceDir = String.format("%s/%s", sourceDS.getPath(), sourceDp.getName());
					String targetDir = String.format("%s/%s", targetDS.getPath(), targetDp.getName());
					if(targetDir.contains(".db")) {
						targetDir = targetDir.replaceAll(".db", "");
					}
					logger.info("sourceDir : " + sourceDir);
					logger.info("targetDir : " + targetDir);
					sqoopInput.setExportDir(targetDir);
					sqoopInput.setSourceDirectory(sourceDir);
					sqoopInput.setTargetDirectory(targetDir);
					sqoopInput.setTable(ingest.getTargetDetail().getValue());
					sqoopInput.setFileLayout(sqoopExecutor.getFileLayout(ingest.getTargetFormat()));
					if(incrLastValue != null) {
						sqoopInput.setIncrementalLastValue(incrLastValue);
					}
					targetFilePathUrl = targetFilePathUrl+sourceDp.getName();
					Map<String, String> inputParams = null;
					if(ingest.getRunParams() != null) {
						inputParams = getRunParams(ingest.getRunParams());
					}
					tableName = sourceDp.getName();
					sqoopExecutor.execute(sqoopInput, inputParams);
				} else {
					//this is export block from Hive table to local file
					String sourceDir = String.format("%s/%s", sourceDS.getPath(), sourceDp.getName());
//					if(sourceDir.contains(".db")) {
//						sourceDir = sourceDir.replaceAll(".db", "");
//					}
					targetFilePathUrl = String.format("%s%s", targetFilePathUrl, String.format("%s/%s/%s", ingest.getUuid(), ingest.getVersion(), ingestExec.getVersion()));
//					if(targetFilePathUrl.startsWith("hdfs")) {
//						targetFilePathUrl = "file"+targetFilePathUrl.substring(4);
//					}
					logger.info("sourceDir : " + sourceDir);
					logger.info("targetDir : " + targetFilePathUrl);
					
//					String targetTableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
					String sourceTableName = sourceDS.getDbname() +"."+sourceDp.getName();

					tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
					
					String sql = generateSqlByDatasource(targetDS, sourceTableName, incrColName, incrLastValue, 0);
					ResultSetHolder rsHolder = sparkExecutor.executeSqlByDatasource(sql, sourceDS, appUuid);
					
					//registering temp table of source
					sparkExecutor.registerDataFrameAsTable(rsHolder, tableName);
					
					//adding version column data
					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());

					//writing to target				
					rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, targetFilePathUrl, ingest.getTargetDetail().getValue(), tableName, "append", ingest.getTargetFormat());
					countRows = rsHolder.getCountRows();
//					targetFilePathUrl = null;
				}
			} else if(ingestionType.equals(IngestionType.TABLETOTABLE)) { 
				SqoopInput sqoopInput = new SqoopInput();
				sqoopInput.setSourceDs(sourceDS);
				sqoopInput.setTargetDs(targetDS);
				String targetDir = targetDS.getPath();
				String sourceDir = sourceDS.getPath();
				logger.info("targetDir : " + targetDir);
				logger.info("sourceDir : " + sourceDir);
				sqoopInput.setIncrementalMode(SqoopIncrementalMode.AppendRows);
				if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					//this is export block from Hive to other table
					sourceDir = String.format("%s/%s", sourceDir, sourceDp.getName());
					logger.info("sourceDir : " + sourceDir);
					sqoopInput.setExportDir(sourceDir);
					sqoopInput.setSourceDirectory(sourceDir);
					sqoopInput.setHiveImport(false);
					sqoopInput.setImportIntended(false);
					sqoopInput.setTable(targetDp.getName());
					tableName = targetDp.getName();
					
//					sqoopInput.setHiveTableName(sourceDp.getName());
//					sqoopInput.setOverwriteHiveTable("Y");
//					sqoopInput.setHiveDatabaseName(sourceDS.getDbname());
//					sqoopInput.sethCatTableName(sourceDp.getName());
				} else if(targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					//this is import block from other table to Hive
					sqoopInput.setTable(sourceDp.getName());
					sqoopInput.setHiveImport(true);
					sqoopInput.setImportIntended(true);
					sqoopInput.setTargetDirectory(targetDir);
					sqoopInput.setHiveTableName(targetDp.getName());
					sqoopInput.setOverwriteHiveTable("Y");
					sqoopInput.setHiveDatabaseName(targetDS.getDbname());
					tableName = targetDp.getName();
				}
				if(incrLastValue != null) {
					sqoopInput.setIncrementalLastValue(incrLastValue);
				}
				targetFilePathUrl = targetFilePathUrl+sourceDp.getName();
				Map<String, String> inputParams = null;
				if(ingest.getRunParams() != null) {
					inputParams = getRunParams(ingest.getRunParams());
				}
				sqoopExecutor.execute(sqoopInput, inputParams);
			} 
			
			if(latestIncrLastValue != null) {
				ingestExec.setLastIncrValue(latestIncrLastValue);
				commonServiceImpl.save(MetaType.ingestExec.toString(), ingestExec);
			}

			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			MetaIdentifier datapodKey = null;
			if(targetDp != null) {
				datapodKey = new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion());
			} else {
				datapodKey = new MetaIdentifier(MetaType.ingest, ingest.getUuid(), ingest.getVersion());
			}
			persistDatastore(tableName, targetFilePathUrl, resultRef, datapodKey, ingestExec, countRows, runMode);
			
			ingestExec.setResult(resultRef);
			ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.Completed);
		} catch (Exception e) {
			e.printStackTrace();
			ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.Failed);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			if(message != null && message.toLowerCase().contains("duplicate entry")) {
				message = "Duplicate entry/entries found for primary key(s).";
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Ingest execution failed.");
			throw new RuntimeException((message != null) ? message : "Ingest execution failed.");
		}
		
		return ingestExec;
	}

	private String getIncrColName(Datapod datapod, AttributeRefHolder incrAttrHolder) {
		String attrName = null;
		for(Attribute attribute : datapod.getAttributes()) {
			if(attribute.getAttributeId().equals(Integer.parseInt(incrAttrHolder.getAttrId()))) {
				attrName = attribute.getName();
				break;
			}
		}
		return attrName;
	}

	public List<String> getFileDetailsByFileName(String filePath, String fileName, String fileFormat) throws JsonProcessingException {
		logger.info("filePath : fileName : fileFormat : " + filePath + ":" + fileName + ":" + fileFormat);
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		List<String> fileNameList = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String dirFileName = listOfFiles[i].getName();
				if(fileName.endsWith("*")) {
					Pattern regex = Pattern.compile("^.*"+fileName+".*$", Pattern.CASE_INSENSITIVE);
					Matcher mtch = regex.matcher(dirFileName);
			        if(mtch.matches()){
			        	fileNameList.add(dirFileName);	
			        }
				} else {
					Pattern regex = Pattern.compile("^"+fileName+"$", Pattern.CASE_INSENSITIVE);
					Matcher mtch = regex.matcher(dirFileName);
			        if(mtch.matches()){
			        	fileNameList.add(dirFileName);	
			        }
				}
			} else if (listOfFiles[i].isDirectory()) {
				logger.info("Directory " + listOfFiles[i].getName());
			}
		}		
		return fileNameList;
	}
	
	public List<Map<String, Object>> getResults(String execUuid, String execVersion, int offset,
			int limit, String sortBy, String order, String requestId, RunMode runMode) throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			limit = offset + limit;
			offset = offset + 1;
			
			IngestExec ingestExec = (IngestExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.ingestExec.toString());
			DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(ingestExec.getResult().getRef().getUuid(), ingestExec.getResult().getRef().getVersion(), MetaType.datastore.toString());
			MetaIdentifier ingestMI = ingestExec.getDependsOn().getRef();
			Ingest ingest = (Ingest) commonServiceImpl.getOneByUuidAndVersion(ingestMI.getUuid(), ingestMI.getVersion(), ingestMI.getType().toString());
			String appUuid = commonServiceImpl.getApp().getUuid();
			MetaIdentifier targetDpMI = ingest.getTargetDetail().getRef();
			Datapod targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
			MetaIdentifier targetDSMI = ingest.getTargetDatasource().getRef();
			Datasource targetDS = (Datasource) commonServiceImpl.getLatestByUuid(targetDSMI.getUuid(), targetDSMI.getType().toString());
			
			if(ingest.getTargetFormat() != null && !ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
				data = sparkExecutor.fetchIngestResult(targetDp, datastore.getName(), datastore.getLocation(), Helper.getDelimetrByFormat(ingest.getTargetFormat()), resolveHeader(ingest.getHeader()), Integer.parseInt(""+datastore.getNumRows()), appUuid);
			} else if(ingest.getTargetFormat() != null && ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
				data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, limit, sortBy, order);
			} else {
				Datasource datasource = commonServiceImpl.getDatasourceByApp();
				if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
			//		data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, limit, sortBy, order);
					data = dataStoreServiceImpl.getDatapodResults(datastore.getUuid(), datastore.getVersion(), null, 0, limit, null, limit, sortBy, order, null, runMode);
				} else {
					IExecutor exec = execFactory.getExecutor(targetDS.getType());
					String tableName = targetDS.getDbname()+"."+targetDp.getName();
					String sql = generateSqlByDatasource(targetDS, tableName, null, null, limit);
					data = exec.executeAndFetch(sql, appUuid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("402", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.");
			throw new Exception((message != null) ? message : "Table not found.");
		}
		return data;
	}
	
	protected void persistDatastore(String tableName, String filePath, MetaIdentifierHolder resultRef, MetaIdentifier datapodKey, IngestExec ingestExec, long countRows, RunMode runMode) throws Exception {
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, tableName, datapodKey, ingestExec.getRef(MetaType.ingestExec), ingestExec.getAppInfo(), ingestExec.getCreatedBy(), SaveMode.Append.toString(), resultRef, countRows, Helper.getPersistModeFromRunMode(runMode.toString()), null);
	}
	
	public String resolveHeader(String header) {
		if(header != null && !header.isEmpty()) {
			switch(header) {
			case "Y" : return "true";
			case "N" : return "false";
			}
		}
		return null;
	}
	
	public String generateSqlByDatasource(Datasource  datasource, String tableName, String incrColName, String incrLastValue, int limit) {
		if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {				
				return "SELECT * FROM " + tableName + " WHERE "
						+ (limit == 0 ? "" : "rownum< " + limit) 
						+ (incrLastValue == null ? "" : " AND "+incrColName+" > "+incrLastValue);
		} else {
			return "SELECT * FROM " + tableName  
					+ (incrLastValue == null ? "" : incrColName+" > "+incrLastValue) 
					+ (limit == 0 ? "" : " LIMIT " + limit);
		}
	}
	
	public String getIngestExecByRGExec(String ingestGroupExecUuid, String ingestGroupExecVersion) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(ingestExecServiceImpl.findReconExecByReconGroupExec(ingestGroupExecUuid, ingestGroupExecVersion));
		return result;
	}
	
	public Object getMetaIdByExecId(String execUuid, String execVersion) throws JsonProcessingException {
		IngestExec ingestExec = (IngestExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.ingestExec.toString());
		MetaIdentifier mi = new MetaIdentifier();
		mi.setType(MetaType.ingest);
		mi.setUuid(ingestExec.getDependsOn().getRef().getUuid());
		mi.setVersion(ingestExec.getDependsOn().getRef().getVersion());
		return mi;
	}
	
	public String getLastIncrValue(String igstExecUuid) throws JsonProcessingException {
		//first getting latest ingest exec then obtaining last incremental value from it
		IngestExec latestIngExec = (IngestExec) commonServiceImpl.getLatestByUuid(igstExecUuid, MetaType.ingestExec.toString());
		return latestIngExec.lastIncrValue;
	}

	private String getNewIncrValue(Datapod datapod, Datasource datasource, AttributeRefHolder incrAttrHolder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException, SQLException {
		String attrName = getIncrColName(datapod, incrAttrHolder);
//		for(Attribute attribute : datapod.getAttributes()) {
//			if(attribute.getAttributeId().equals(Integer.parseInt(incrAttrHolder.getAttrId()))) {
//				attrName = attribute.getName();
//				break;
//			}
//		}
		
		if(attrName == null || attrName.isEmpty() || attrName.equalsIgnoreCase("null")) {
			throw new RuntimeException("Incremental attribute with attr id '"+incrAttrHolder.getAttrId()+"' not found in datapod '"+datapod.getName()+"' attributes");
		}
		String appUuid = commonServiceImpl.getApp().getUuid();
		String sql = "SELECT MAX("+attrName+") FROM " + datasource.getDbname() + "." + datapod.getName();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		ResultSetHolder rsHolder = exec.executeSqlByDatasource(sql, datasource, appUuid);
		return exec.getIncrementalLastValue(rsHolder, appUuid);
	}
	
	public Map<String, String> getRunParams(String runParams) {
		Map<String, String> inputParams = null;
		if(runParams != null && !runParams.isEmpty()) {
			inputParams = new HashMap<>();
			String[] splits = runParams.split(",");
			for(String split : splits) {
				String[] property = split.split("=");
				inputParams.put(property[0], property[1]);
			}
		}
		return inputParams;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), (IngestExec)baseExec, execParams, null, runMode);
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return baseExec; 
	}

	@Override
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws Exception {		
		return (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.ingestExec.toString());
	}

	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec,
			MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode)
			throws Exception {
		return execute(baseRuleExec.getDependsOn().getRef().getUuid(), baseRuleExec.getDependsOn().getRef().getVersion(), (IngestExec)baseRuleExec, execParams, null, runMode);
	}
	
	public void restart(String type, String uuid, String version, ExecParams  execParams, RunMode runMode) throws Exception {
		IngestExec ingestExec = (IngestExec) commonServiceImpl.getOneByUuidAndVersion(uuid,version, MetaType.ingestExec.toString());
		try {
			HashMap<String, String> otherParams = null;
			if(execParams != null) 
				otherParams = execParams.getOtherParams();
			
			ingestExec = (IngestExec) parse(uuid,version, null, otherParams, null, null, runMode);
			execute(ingestExec.getDependsOn().getRef().getUuid(),ingestExec.getDependsOn().getRef().getVersion(), ingestExec, execParams, null, runMode);
		
		} catch (Exception e) {
			synchronized (ingestExec.getUuid()) {
				try {
					commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.Failed);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not restart Ingest.");
					throw new Exception((message != null) ? message : "Can not restart Ingest.");
				}
			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not restart Ingest.");
			throw new Exception((message != null) ? message : "Can not restart Ingest.");
		}
	}
}
