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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.KafkaExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.executor.SparkStreamingExecutor;
import com.inferyx.framework.executor.SqoopExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.DatasetOperator;
import com.inferyx.framework.operator.FormulaOperator;
import com.inferyx.framework.operator.FunctionOperator;
import com.inferyx.framework.operator.IngestOperator;

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
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private KafkaExecutor<?, ?> kafkaExecutor;
	@Autowired
	private SparkStreamingExecutor<?, ?> sparkStreamingExecutor;
	@Autowired
	private FunctionOperator functionOperator;
	@Autowired
	private FormulaOperator formulaOperator;	
	@Autowired
	private IngestOperator ingestOperator;	
	@Autowired
	private DatasetOperator datasetOperator;
	
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
			
			List<Status> statusList = ingestExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			if (Helper.getLatestStatus(statusList) != null
					&& Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.READY, new Date()))) {
				logger.info(" If status is in READY state then no need to start and parse again. ");
				return ingestExec;
			}
			ingestExec.setExecParams(execParams);
			
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			ingestExec.setName(ingest.getName());
			ingestExec.setAppInfo(ingest.getAppInfo());	
			ingestExec.setRunMode(runMode);
			
			commonServiceImpl.save(MetaType.ingestExec.toString(), ingestExec);
			
			ingestExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			
	
			ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.PENDING);
		} catch (Exception e) {
			logger.error(e);
			ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.FAILED);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ingestExec, ingestExec.getUuid(), ingestExec.getVersion()));
			commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable ingest.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Can not create executable ingest.");
		}			
		return ingestExec;
	}

	public IngestExec execute(String ingestUuid, String ingestVersion, IngestExec ingestExec, ExecParams execParams, String type, RunMode runMode) throws Exception {
		try {
			Ingest ingest = (Ingest) commonServiceImpl.getOneByUuidAndVersion(ingestUuid, ingestVersion, MetaType.ingest.toString());
			ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.RUNNING);
			String appUuid = commonServiceImpl.getApp().getUuid();
			MetaIdentifier sourceDSMI = ingest.getSourceDatasource().getRef();
			Datasource sourceDS = (Datasource) commonServiceImpl.getLatestByUuid(sourceDSMI.getUuid(), sourceDSMI.getType().toString());
			MetaIdentifier targetDSMI = ingest.getTargetDatasource().getRef();
			Datasource targetDS = (Datasource) commonServiceImpl.getLatestByUuid(targetDSMI.getUuid(), targetDSMI.getType().toString());
//			long countRows = -1L;
//			
//			String targetFilePathUrl = helper.getPathByDataSource(targetDS);
//			String sourceFilePathUrl = helper.getPathByDataSource(sourceDS);
//			
//			IngestionType ingestionType = Helper.getIngestionType(ingest.getType());

			MetaIdentifier sourceDpMI = ingest.getSourceDetail().getRef();
			Datapod sourceDp = null;
			DataSet sourceDataSet = null;
//			String incrLastValue = null;
//			String latestIncrLastValue = null;
//			String incrColName = null;
			if(sourceDpMI.getUuid() != null) {
				if(sourceDpMI.getType().equals(MetaType.datapod)) {
					sourceDp = (Datapod) commonServiceImpl.getLatestByUuid(sourceDpMI.getUuid(), sourceDpMI.getType().toString());
				} else if(sourceDpMI.getType().equals(MetaType.dataset)) {
					sourceDataSet = (DataSet) commonServiceImpl.getLatestByUuid(sourceDpMI.getUuid(), sourceDpMI.getType().toString());
				}
			}			
			
			MetaIdentifier targetDpMI = ingest.getTargetDetail().getRef();
			Datapod targetDp = null;
			if(targetDpMI.getUuid() != null) {
				targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
			}
			
//			if(ingest.getAttributeMap() == null || ingest.getAttributeMap().isEmpty()){
//				RunIngestServiceImpl<?, ?> runIngestServiceImpl = new RunIngestServiceImpl<>();
//				runIngestServiceImpl.setCommonServiceImpl(commonServiceImpl);
//				runIngestServiceImpl.setExecParams(execParams);
//				runIngestServiceImpl.setHelper(helper);
//				runIngestServiceImpl.setIngest(ingest);
//				runIngestServiceImpl.setIngestExec(ingestExec);
//				runIngestServiceImpl.setIngestServiceImpl(this);
//				runIngestServiceImpl.setName(MetaType.ingestExec+"_"+ingestExec.getUuid()+"_"+ingestExec.getVersion());
//				runIngestServiceImpl.setRunMode(runMode);
//				runIngestServiceImpl.setSessionContext(sessionHelper.getSessionContext());
//				runIngestServiceImpl.setSourceDp(sourceDp);
//				runIngestServiceImpl.setSparkExecutor(sparkExecutor);
//				runIngestServiceImpl.setSqoopExecutor(sqoopExecutor);
//				runIngestServiceImpl.setTargetDp(targetDp);
//				runIngestServiceImpl.setAppUuid(appUuid);
//				runIngestServiceImpl.setSourceDS(sourceDS);
//				runIngestServiceImpl.setTargetDS(targetDS);
//				runIngestServiceImpl.setKafkaExecutor(kafkaExecutor);
//				runIngestServiceImpl.setSparkStreamingExecutor(sparkStreamingExecutor);
//				
//				if(sourceDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
//					//check whether target file alReady exist (when save mode is null)
//					if(targetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())
//							&& ingest.getSaveMode() == null) {
//						String targetFileOrDirName = generateFileName(ingest.getTargetDetail().getValue(), ingest.getTargetExtn(), ingest.getTargetFormat());
//						List<String> targetFileOrDirList = null;
//						if(ingest.getTargetExtn() == null && ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
//							targetFileOrDirList = getMatchingDirNames(targetDS.getPath(), targetFileOrDirName, ingest.getTargetExtn(), ingest.getIgnoreCase(), ingest.getTargetFormat());
//						} else {
//							targetFileOrDirList = getMatchingFileNames(targetDS.getPath(), targetFileOrDirName, ingest.getTargetExtn(), ingest.getIgnoreCase(), ingest.getTargetFormat());
//						}
//						
//						for(String fileName : targetFileOrDirList) {
//							if(fileName.equalsIgnoreCase(targetFileOrDirName)) {
//								throw new RuntimeException("Target file or directory \'"+targetFileOrDirName+"\' alReady exists.");								
//							}
//						}
//					}
//					
//					//get source files matching the criteria
//					List<String> fileNameList = getMatchingFileNames(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceExtn(), ingest.getIgnoreCase(), ingest.getSourceFormat());
//					if(fileNameList == null || fileNameList.isEmpty()) {
//						throw new RuntimeException("File(s) \'"+ingest.getSourceDetail().getValue()+"\' not exist.");
//					}
//					
//					List<String> fileInfo = new ArrayList<>();
//					String sourceDir = "file://".concat(sourceDS.getPath());
//					String sourceFileLocation = "";
//					for(String fileName : fileNameList) {
//						String fileLocation = sourceDir.endsWith("/") ? sourceDir.concat(fileName) : sourceDir.concat("/").concat(fileName);
//						fileInfo.add(fileLocation);
//						sourceFileLocation = sourceFileLocation.concat(fileLocation).concat(",");
//					}
//					sourceFileLocation = sourceFileLocation.substring(0, sourceFileLocation.lastIndexOf(","));
//					ingestExec.setFileInfo(fileInfo);
//					runIngestServiceImpl.setLocation(fileInfo);
//				} 
//				
//				runIngestServiceImpl.call();
//			} else {
				RunIngestServiceImpl2<?, ?> runIngestServiceImpl2 = new RunIngestServiceImpl2<>();
				runIngestServiceImpl2.setCommonServiceImpl(commonServiceImpl);
				runIngestServiceImpl2.setExecParams(execParams);
				runIngestServiceImpl2.setHelper(helper);
				runIngestServiceImpl2.setIngest(ingest);
				runIngestServiceImpl2.setIngestExec(ingestExec);
				runIngestServiceImpl2.setIngestServiceImpl(this);
				runIngestServiceImpl2.setName(MetaType.ingestExec+"_"+ingestExec.getUuid()+"_"+ingestExec.getVersion());
				runIngestServiceImpl2.setRunMode(runMode);
				runIngestServiceImpl2.setSessionContext(sessionHelper.getSessionContext());
				runIngestServiceImpl2.setSourceDp(sourceDp);
				runIngestServiceImpl2.setSparkExecutor(sparkExecutor);
				runIngestServiceImpl2.setSqoopExecutor(sqoopExecutor);
				runIngestServiceImpl2.setTargetDp(targetDp);
				runIngestServiceImpl2.setAppUuid(appUuid);
				runIngestServiceImpl2.setSourceDS(sourceDS);
				runIngestServiceImpl2.setTargetDS(targetDS);
				runIngestServiceImpl2.setKafkaExecutor(kafkaExecutor);
				runIngestServiceImpl2.setSparkStreamingExecutor(sparkStreamingExecutor);
				runIngestServiceImpl2.setSourceDataSet(sourceDataSet);
				runIngestServiceImpl2.setIngestOperator(ingestOperator);
				runIngestServiceImpl2.setDatasetOperator(datasetOperator);
				
				if(sourceDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
					//check whether target file alReady exist (when save mode is null)
					if(targetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())
							&& ingest.getSaveMode() == null) {
						String targetFileOrDirName = generateFileName(ingest.getTargetDetail().getValue(), ingest.getTargetExtn(), ingest.getTargetFormat());
						List<String> targetFileOrDirList = null;
						if(ingest.getTargetExtn() == null && ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
							targetFileOrDirList = getMatchingDirNames(targetDS.getPath(), targetFileOrDirName, ingest.getTargetExtn(), ingest.getIgnoreCase(), ingest.getTargetFormat());
						} else {
							targetFileOrDirList = getMatchingFileNames(targetDS.getPath(), targetFileOrDirName, ingest.getTargetExtn(), ingest.getIgnoreCase(), ingest.getTargetFormat());
						}
						
						for(String fileName : targetFileOrDirList) {
							if(fileName.equalsIgnoreCase(targetFileOrDirName)) {
								throw new RuntimeException("Target file or directory \'"+targetFileOrDirName+"\' alReady exists.");								
							}
						}
					}
					
					//get source files matching the criteria
					List<String> fileNameList = getMatchingFileNames(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceExtn(), ingest.getIgnoreCase(), ingest.getSourceFormat());
					if(fileNameList == null || fileNameList.isEmpty()) {
						throw new RuntimeException("File(s) \'"+ingest.getSourceDetail().getValue()+ingest.getSourceFormat().toLowerCase()+"\' not exist.");
					}
					
					List<String> fileInfo = new ArrayList<>();
					String sourceDir = "file://".concat(sourceDS.getPath());
					String sourceFileLocation = "";
					for(String fileName : fileNameList) {
						String fileLocation = sourceDir.endsWith("/") ? sourceDir.concat(fileName) : sourceDir.concat("/").concat(fileName);
						fileInfo.add(fileLocation);
						sourceFileLocation = sourceFileLocation.concat(fileLocation).concat(",");
					}
					sourceFileLocation = sourceFileLocation.substring(0, sourceFileLocation.lastIndexOf(","));
					ingestExec.setFileInfo(fileInfo);
					runIngestServiceImpl2.setLocation(fileInfo);
				} 

				runIngestServiceImpl2.call();
//			}
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
			} else if(message != null && message.toLowerCase().contains("No change in incremental param hence skipping execution.")) {
				message = "No change in incremental param hence skipping execution.";
			}

			if(message != null && message.toLowerCase().contains("No change in incremental param hence skipping execution.")) {
				ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.COMPLETED);
				MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
				dependsOn.setRef(new MetaIdentifier(MetaType.ingestExec, ingestExec.getUuid(), ingestExec.getVersion()));
				commonServiceImpl.sendResponse("300", MessageStatus.SUCCESS.toString(), "No change in incremental param hence skipping execution.", dependsOn);
				throw new RuntimeException("No change in incremental param hence skipping execution.");
			} else {
				ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.FAILED);
				MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
				dependsOn.setRef(new MetaIdentifier(MetaType.ingestExec, ingestExec.getUuid(), ingestExec.getVersion()));
				commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Ingest execution FAILED.", dependsOn);
				throw new RuntimeException((message != null) ? message : "Ingest execution FAILED.");				
			}
		}
		
		return ingestExec;
	}

	public String generateFileName(String fileName, String fileExtn, String fileFormat) {
		Pattern regex = Helper.getRegexByFileInfo(fileName, fileExtn, fileFormat, false);
		fileName = regex.pattern();
		fileName = fileName.substring(1, fileName.length()-1);
		fileName = fileName.replaceAll(Pattern.quote("\\."), ".");
		return fileName;
	}
	
	@SuppressWarnings("unused")
	private Map<String, String> checkPartitionsByDatapod(Datapod datapod) {
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

	public String getColName(Datapod datapod, DataSet dataSet, AttributeRefHolder incrAttrHolder) {
		String attrName = null;
		if(datapod != null) {
			attrName = datapod.getAttributeName(Integer.parseInt(incrAttrHolder.getAttrId()));
		} else {
			attrName = dataSet.getAttributeName(Integer.parseInt(incrAttrHolder.getAttrId()));
		}		
		return attrName;
	}

	public List<String> getMatchingFileNames(String filePath, String fileName, String fileExtn, String ignoreCase, String fileFormat) throws JsonProcessingException, ParseException {
		logger.info("filePath : fileName : fileExtn : fileFormat : " + filePath + ":" + fileName + ":" + fileExtn + ":" + fileFormat);
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		List<String> fileNameList = new ArrayList<String>();
		boolean isCaseSensitive = getCaseSensitivity(ignoreCase);

		Pattern regex = null;
		regex = Helper.getRegexByFileInfo(fileName, fileExtn, fileFormat, isCaseSensitive);
		logger.info("Final regex : " + regex);

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String dirFileName = listOfFiles[i].getName();
				Matcher mtch = regex.matcher(dirFileName);
				if(mtch.matches()){
		        	fileNameList.add(dirFileName);	
		        }
			} else if (listOfFiles[i].isDirectory()) {
				logger.info("Directory " + listOfFiles[i].getName());
			}
		}		

		for (String file : fileNameList) {
			logger.info("Found matching file : " + file);									
		}
		
		return fileNameList;		
	}
	
	public List<String> getMatchingDirNames(String filePath, String dirName, String fileExtn, String ignoreCase, String fileFormat) throws JsonProcessingException, ParseException {
		logger.info("filePath : dirName : fileExtn : fileFormat : " + filePath + ":" + dirName + ":" + fileExtn + ":" + fileFormat);
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		List<String> dirNameList = new ArrayList<String>();
		boolean isCaseSensitive = getCaseSensitivity(ignoreCase);

		Pattern regex = null;
		regex = Helper.getRegexByFileInfo(dirName, fileExtn, fileFormat, isCaseSensitive);
		logger.info("Final regex : " + regex);

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				logger.info("Directory " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				String dirFileName = listOfFiles[i].getName();
				Matcher mtch = regex.matcher(dirFileName);
				if(mtch.matches()){
		        	dirNameList.add(dirFileName);	
		        }
			}
		}		

		for (String dir : dirNameList) {
			logger.info("Found matching directory : " + dir);									
		}
		
		return dirNameList;		
	}
	
	private boolean getCaseSensitivity(String ignoreCase) {
		if(ignoreCase.equalsIgnoreCase("Y")) {
			return false;
		} else {
			return true;
		}
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

				String filePathUrl = null;
				if(datastore.getLocation().endsWith("/")) {
					filePathUrl = datastore.getLocation().concat(ingest.getTargetDetail().getValue()).concat(ingest.getTargetDetail().getValue().toLowerCase().endsWith(".csv") ? "" : ".csv");
				} else {
					filePathUrl = datastore.getLocation().concat("/").concat(ingest.getTargetDetail().getValue()).concat(ingest.getTargetDetail().getValue().toLowerCase().endsWith(".csv") ? "" : ".csv");
				}
				data = sparkExecutor.fetchIngestResult(targetDp, datastore.getName(), filePathUrl, Helper.getDelimetrByFormat(ingest.getTargetFormat()), resolveHeader(ingest.getSourceHeader()), Integer.parseInt(""+datastore.getNumRows()), appUuid);
			} else if(ingest.getTargetFormat() != null && ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
				data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, limit, sortBy, order, null,runMode);
			} else {
				Datasource datasource = commonServiceImpl.getDatasourceByApp();
				if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
			//		data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, limit, sortBy, order);
					data = dataStoreServiceImpl.getDatapodResults(datastore.getUuid(), datastore.getVersion(), null, 0, limit, null, limit, sortBy, order, null, runMode);
				} else {
					IExecutor exec = execFactory.getExecutor(targetDS.getType());
					String tableName = targetDS.getDbname()+"."+ targetDp != null ? targetDp.getName() : ingest.getTargetDetail().getValue();
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
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ingestExec, execUuid, execVersion));
			commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", dependsOn);
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
				return "SELECT * FROM " + tableName 
						+ (incrLastValue == null ? "" : " WHERE "+incrColName+" > "+incrLastValue)
						+ (limit == 0 ? "" : "rownum < " + limit) ;
		} else {
			return "SELECT * FROM " + tableName
					+ (incrLastValue == null ? "" : " WHERE " + incrColName+" > "+incrLastValue) 
					+ (limit == 0 ? "" : " LIMIT " + limit);
		}
	}
	
	public String getIngestExecByRGExec(String ingestGroupExecUuid, String ingestGroupExecVersion) throws JsonProcessingException {
		String result = null;
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(ingestExecServiceImpl.findReconExecByReconGroupExec(ingestGroupExecUuid, ingestGroupExecVersion));
		return result;
	}
	public String getIngestByIngestExec(String ingestUuid, String ingestversion) throws JsonProcessingException {
		String result = null;
		IngestExec ingestExec = (IngestExec) commonServiceImpl.getOneByUuidAndVersion(ingestUuid, ingestversion, MetaType.ingestExec.toString());
		Ingest ingest = (Ingest) commonServiceImpl.getOneByUuidAndVersion(ingestExec.getDependsOn().getRef().getUuid(),ingestExec.getDependsOn().getRef().getVersion(), MetaType.ingest.toString());
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		result = ow.writeValueAsString(ingest);
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
	
	public String getLastIncrValue(String ingestUuid, String ingestVersion) throws JsonProcessingException {
		//first getting latest ingest exec then obtaining last incremental value from it
		IngestExec ingestExec = getIngestExecByIngest(ingestUuid, ingestVersion, 2);//(IngestExec) commonServiceImpl.getLatestByUuid(igstExecUuid, MetaType.ingestExec.toString());
		if(ingestExec != null) {
			return ingestExec.getLastIncrValue();
		} else {
			return null;
		}
//		return ingestExec.getLastIncrValue();
	}

	public IngestExec getIngestExecByIngest(String ingestUuid, String ingestVersion, long linit) throws JsonProcessingException {
		MatchOperation dependsOnFilter = null;
		MatchOperation dependsOnFilter2 = null;
		if(ingestVersion != null && !ingestVersion.isEmpty()) {
			//dependsOnFilter = match(new Criteria("dependsOn.ref.uuid").is(ingestUuid).andOperator(new Criteria("dependsOn.ref.version").is(ingestVersion)));
		    dependsOnFilter = match(new Criteria("dependsOn.ref.uuid").is(ingestUuid));
		    dependsOnFilter2 = match(new Criteria("statusList.stage").in(Status.Stage.COMPLETED.toString()));
		} else {
			dependsOnFilter = match(new Criteria("dependsOn.ref.uuid").is(ingestUuid));
		}
		GroupOperation groupByUuid = group("uuid").max("version").as("version"); 
		SortOperation sortByVersion = sort(new Sort(Direction.DESC, "version"));
		LimitOperation limitToOnlyFirst = limit(1);
		Aggregation ingestAggr = newAggregation(dependsOnFilter,dependsOnFilter2, groupByUuid, sortByVersion, limitToOnlyFirst);
		AggregationResults<IngestExec> ingestAggrResults = mongoTemplate.aggregate(ingestAggr, MetaType.ingestExec.toString().toLowerCase(), IngestExec.class);
		IngestExec ingestExec = ingestAggrResults.getUniqueMappedResult();
		if(ingestExec != null) {
			return (IngestExec) commonServiceImpl.getOneByUuidAndVersion(ingestExec.getId(), ingestExec.getVersion(), MetaType.ingestExec.toString());
		} else {
			return null;
		}
//		List<IngestExec> resolvedIngestExecList = new ArrayList<>();
//		for(IngestExec ingestExec : ingestExecList) {
//			IngestExec resolvedIngestExec = (IngestExec) commonServiceImpl.getOneByUuidAndVersion(ingestExec.getId(), ingestExec.getVersion(), MetaType.ingestExec.toString());
//			resolvedIngestExecList.add(resolvedIngestExec);
//		}
//		return resolvedIngestExec;
	}
	
	public String getNewIncrValue(Datapod datapod, Datasource datasource, DataSet dataSet, AttributeRefHolder incrAttrHolder, RunMode runMode) throws Exception {
		String attrName = getColName(datapod, dataSet, incrAttrHolder);
		
		if(attrName == null || attrName.isEmpty() || attrName.equalsIgnoreCase("null")) {
			throw new RuntimeException("Incremental attribute with attr id '"+incrAttrHolder.getAttrId()+"' not found in "+(datapod != null ? "datapod '"+datapod.getName() : "dataset '"+dataSet.getName())+"' attributes");
		}
		
		String appUuid = commonServiceImpl.getApp().getUuid();
		
		String sql = null;
		if(datapod != null) {
			sql = "SELECT MAX("+attrName+") FROM " + datasource.getDbname() + "." + datapod.getName();
		} else {
			sql = "SELECT MAX("+attrName+") FROM (" + datasetOperator.generateSql(dataSet, null, null, new HashSet<>(), null, runMode) + ") " + dataSet.getName();
		}
		 
		
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
		synchronized (baseExec.getUuid()) {
			baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.ingestExec, Status.Stage.STARTING);
		}
		synchronized (baseExec.getUuid()) {
			baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.ingestExec, Status.Stage.READY);
		}
		return baseExec; 
	}

	@Override
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws Exception {	
		BaseRuleExec baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.ingestExec.toString(), "N");
		synchronized (execUuid) {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, MetaType.ingestExec, Status.Stage.STARTING);
		}
		synchronized (execUuid) {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, MetaType.ingestExec, Status.Stage.READY);
		}
		return baseRuleExec;
	}

	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec,
			MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode)
			throws Exception {
		return execute(baseRuleExec.getDependsOn().getRef().getUuid(), baseRuleExec.getDependsOn().getRef().getVersion(), (IngestExec)baseRuleExec, execParams, null, runMode);
	}
	
	/**
	 * 
	 * @param baseExec
	 * @return
	 * @throws Exception
	 */
	public Status restart(BaseExec baseExec) throws Exception {
		try {
			return super.restart(baseExec.getUuid(), baseExec.getVersion(), MetaType.ingestExec);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
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
					commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.FAILED);
				} catch (Exception e1) {
					e1.printStackTrace();
					String message = null;
					try {
						message = e1.getMessage();
					}catch (Exception e2) {
						// TODO: handle exception
					}
					MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
					dependsOn.setRef(new MetaIdentifier(MetaType.ingestExec, ingestExec.getUuid(), ingestExec.getVersion()));
					commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not restart Ingest.", dependsOn);
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
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.ingestExec, ingestExec.getUuid(), ingestExec.getVersion()));
			commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not restart Ingest.", dependsOn);
			throw new Exception((message != null) ? message : "Can not restart Ingest.");
		}
	}
	
	public void moveFileTOFileOrDir(String srcLocation, String targetLocation, boolean moveToDir) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		logger.info("source location: "+srcLocation);
		logger.info("target location: "+targetLocation);
		try {
			if(moveToDir) {
				FileUtils.moveFileToDirectory(new File(srcLocation), new File(targetLocation), true);
			} else {
				File abc=new File(targetLocation);
				FileUtils.moveFile(new File(srcLocation),abc);
				System.out.println("target absolute path: " + abc.getAbsolutePath());
			}			
		} catch (Exception e) {
			String message = null;
			
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			if(message != null 
					&& message.contains("Destination '"+targetLocation+"' alReady exists")) {
				message = "Destination '"+targetLocation+"' alReady exists.";
				deleteFileOrDirectory(targetLocation, false);
				FileUtils.moveFile(new File(srcLocation), new File(targetLocation));
			} else {
				message = e.toString();
				commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), message, null);
				throw new RuntimeException(message);
			}
		}
	}
	
	public String getFileNameFromDir(String directory, String fileExt, String fileFormat) {
		File folder = new File(directory);
		for (File file : folder.listFiles()) {
			String dirFileName = file.getName();
			if(fileExt != null) {
				fileExt = fileExt.startsWith(".") ? fileExt.substring(1) : fileExt;
			} else {
				fileExt = fileFormat;
			}
			
			if(fileFormat.equalsIgnoreCase(FileType.PARQUET.toString())) {
				fileExt = FileType.PARQUET.toString().toLowerCase();
			} else {
				//assigning file extension to csv b'coz as of now we are using this method
				//to get files from the directory where sqpark have saved the csv files
				//and spark saves csv files with extension .csv for for all type csv delimetres
				fileExt = FileType.CSV.toString().toLowerCase();				
			}
			
			if (file.isFile() && dirFileName.toLowerCase().endsWith("."+fileExt)) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}
	
	public void deleteFileOrDirectory(String absolutePath, boolean isDirectory) throws IOException {
		File file = new File(absolutePath);
		if(file.exists()) {
			FileUtils.forceDelete(file);
		}
	}
	
	public List<String> getTopicList(String dsUuid, String dsVersion, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource ds = (Datasource) commonServiceImpl.getOneByUuidAndVersion(dsUuid, dsVersion, MetaType.datasource.toString());
		List<String> topicList = null;
		try {
		 topicList = kafkaExecutor.getTopics(ds);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not get topic(s).");
		}
		return topicList;
	}
	


	/**
	 * @param attributeMapList
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
	public String resolveAttribute(AttributeRefHolder attrRefHolder, Ingest ingest) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
			if(attrRefHolder.getRef().getType().equals(MetaType.attribute)) {
				return attrRefHolder.getValue();
			} else if(attrRefHolder.getRef().getType().equals(MetaType.datapod)) {
				MetaIdentifier sourceDpMI = attrRefHolder.getRef();
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceDpMI.getUuid(), sourceDpMI.getVersion(), sourceDpMI.getType().toString());
				return datapod.getAttributeName(Integer.parseInt(attrRefHolder.getAttrId()));
			} else if(attrRefHolder.getRef().getType().equals(MetaType.dataset)) {
				MetaIdentifier sourceDsMI = attrRefHolder.getRef();
				DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceDsMI.getUuid(), sourceDsMI.getVersion(), sourceDsMI.getType().toString());
				return dataSet.getAttributeName(Integer.parseInt(attrRefHolder.getAttrId()));
			}  else if(attrRefHolder.getRef().getType().equals(MetaType.function)) {
				MetaIdentifier functionMI = attrRefHolder.getRef();
				Function function = (Function) commonServiceImpl.getOneByUuidAndVersion(functionMI.getUuid(), functionMI.getVersion(), functionMI.getType().toString());
				Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(ingest);
				return functionOperator.generateSql(function, null, null, mapSourceDS);
			} else if(attrRefHolder.getRef().getType().equals(MetaType.formula)) {
				MetaIdentifier formulaMI = attrRefHolder.getRef();
				Formula formula = (Formula) commonServiceImpl.getOneByUuidAndVersion(formulaMI.getUuid(), formulaMI.getVersion(), formulaMI.getType().toString());
				Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(ingest);
				return formulaOperator.generateSql(formula, null, null, null, mapSourceDS);
			}
		
		return null;
	}
	
	/**
	 * @param attributeMapList
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
	public String getAttrAliseName(AttributeRefHolder attrRefHolder) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
			if(attrRefHolder.getRef().getType().equals(MetaType.attribute)) {
				return attrRefHolder.getValue();
			} else if(attrRefHolder.getRef().getType().equals(MetaType.datapod)) {
				MetaIdentifier sourceDpMI = attrRefHolder.getRef();
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceDpMI.getUuid(), sourceDpMI.getVersion(), sourceDpMI.getType().toString());
//				datapod.getAttributeName(Integer.parseInt(attrRefHolder.getAttrId()));
				for(Attribute attribute : datapod.getAttributes()) {
					if(attrRefHolder.getAttrId().equalsIgnoreCase(attribute.getAttributeId().toString())) {
						return attribute.getName();
					}
				}
			} else if(attrRefHolder.getRef().getType().equals(MetaType.function)) {
				MetaIdentifier functionMI = attrRefHolder.getRef();
				Function function = (Function) commonServiceImpl.getOneByUuidAndVersion(functionMI.getUuid(), functionMI.getVersion(), functionMI.getType().toString());
				return function.getName();
			} else if(attrRefHolder.getRef().getType().equals(MetaType.formula)) {
				MetaIdentifier formulaMI = attrRefHolder.getRef();
				Formula formula = (Formula) commonServiceImpl.getOneByUuidAndVersion(formulaMI.getUuid(), formulaMI.getVersion(), formulaMI.getType().toString());
				return formula.getName();
			}
		
		return null;
	}
	
	/**
	 * Sends kill message to kill an ingest rule. 
	 * First, it sets the status to TERMINATING in case the status is RUNNING.
	 * Then, it tries to kill the thread. 
	 * Then, it sets the status to KILLED if the latest status is TERMINATING, whether or not it was able to kill the thread.
	 * Even if it was not able to kill a thread (because the thread COMPLETED or cancelled in between), if the status was TERMINATING, the status shall change to KILLED. 
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	public void kill (String uuid, String version, MetaType execType) {
		BaseExec baseExec = null;
		try {
			baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString());
		} catch (JsonProcessingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (baseExec == null) {
			logger.info("IngestExec not found. Exiting...");
			return;
		}
		if (!Helper.getLatestStatus(baseExec.getStatusList()).equals(new Status(Status.Stage.RUNNING, new Date()))) {
			logger.info("Latest Status is not in RUNNING. Exiting...");
		}
		try {
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.TERMINATING);
			}
			@SuppressWarnings("unchecked")
			FutureTask<TaskHolder> futureTask = (FutureTask<TaskHolder>) taskThreadMap.get(execType+"_"+baseExec.getUuid()+"_"+baseExec.getVersion());
				futureTask.cancel(true);
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.KILLED);
			}
		} catch (Exception e) {
			logger.info("FAILED to kill. uuid : " + uuid + " version : " + version);
			try {
				synchronized (baseExec.getUuid()) {
					commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.KILLED);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			taskThreadMap.remove(execType+"_"+baseExec.getUuid()+"_"+baseExec.getVersion());
			e.printStackTrace();
		}
	}
	
	/**
	 * Set status of BaseExec to RESUME if status is PAUSE
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	public void RESUME (String uuid, String version, MetaType execType) {
		BaseExec baseExec = null;
		try {
			baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString());
		} catch (JsonProcessingException e2) {
			e2.printStackTrace();
		}
		if (baseExec == null) {
			logger.info("BaseExec not found. Exiting...");
			return;
		}
		// Pre conditions for RESUME shall be determined by the setMetaStatus
		try {
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.RESUME);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set status of BaseExec to PAUSE if status is KILLED, PENDING, or RESUME 
	 * @param uuid
	 * @param version
	 * @param execType
	 */
	public void PAUSE (String uuid, String version, MetaType execType) {
		BaseExec baseExec = null;
		try {
			baseExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(uuid, version, execType.toString());
		} catch (JsonProcessingException e2) {
			e2.printStackTrace();
		}
		if (baseExec == null) {
			logger.info("BaseExec not found. Exiting...");
			return;
		}
		// Pre conditions for PAUSE shall be determined by the setMetaStatus
		try {
			synchronized (baseExec.getUuid()) {
				commonServiceImpl.setMetaStatus(baseExec, execType, Status.Stage.PAUSE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
