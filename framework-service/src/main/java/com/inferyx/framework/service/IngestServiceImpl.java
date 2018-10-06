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
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
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
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.KafkaExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.executor.SparkStreamingExecutor;
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
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private KafkaExecutor<?, ?> kafkaExecutor;
	@Autowired
	private SparkStreamingExecutor<?, ?> sparkStreamingExecutor;
	
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
			commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable ingest.");
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
//			long countRows = -1L;
//			
//			String targetFilePathUrl = helper.getPathByDataSource(targetDS);
//			String sourceFilePathUrl = helper.getPathByDataSource(sourceDS);
//			
//			IngestionType ingestionType = Helper.getIngestionType(ingest.getType());

			MetaIdentifier sourceDpMI = ingest.getSourceDetail().getRef();
			Datapod sourceDp = null;
//			String incrLastValue = null;
//			String latestIncrLastValue = null;
//			String incrColName = null;
			if(sourceDpMI.getUuid() != null) {
				sourceDp = (Datapod) commonServiceImpl.getLatestByUuid(sourceDpMI.getUuid(), sourceDpMI.getType().toString());
			}			
			
			MetaIdentifier targetDpMI = ingest.getTargetDetail().getRef();
			Datapod targetDp = null;
			if(targetDpMI.getUuid() != null) {
				targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
			}

			RunIngestServiceImpl<?, ?> runIngestServiceImpl = new RunIngestServiceImpl<>();
			runIngestServiceImpl.setCommonServiceImpl(commonServiceImpl);
			runIngestServiceImpl.setExecParams(execParams);
			runIngestServiceImpl.setHelper(helper);
			runIngestServiceImpl.setIngest(ingest);
			runIngestServiceImpl.setIngestExec(ingestExec);
			runIngestServiceImpl.setIngestServiceImpl(this);
			runIngestServiceImpl.setName(MetaType.ingestExec+"_"+ingestExec.getUuid()+"_"+ingestExec.getVersion());
			runIngestServiceImpl.setRunMode(runMode);
			runIngestServiceImpl.setSessionContext(sessionHelper.getSessionContext());
			runIngestServiceImpl.setSourceDp(sourceDp);
			runIngestServiceImpl.setSparkExecutor(sparkExecutor);
			runIngestServiceImpl.setSqoopExecutor(sqoopExecutor);
			runIngestServiceImpl.setTargetDp(targetDp);
			runIngestServiceImpl.setAppUuid(appUuid);
			runIngestServiceImpl.setSourceDS(sourceDS);
			runIngestServiceImpl.setTargetDS(targetDS);
			runIngestServiceImpl.setKafkaExecutor(kafkaExecutor);
			runIngestServiceImpl.setSparkStreamingExecutor(sparkStreamingExecutor);
			
			if(sourceDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				//check whether target file already exist (when save mode is null)
				if(targetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())
						&& ingest.getSaveMode() == null) {
					String targetFileName = generateFileName(ingest.getTargetDetail().getValue(), ingest.getTargetFormat());
					List<String> targetFileNameList = getMatchingFileNames(targetDS.getPath(), targetFileName, ingest.getTargetFormat(), ingest.getIgnoreCase());
					for(String fileName : targetFileNameList) {
						if(fileName.equalsIgnoreCase(targetFileName)) {
							throw new RuntimeException("Target file \'"+targetFileName+"\' already exists.");								
						}
					}
				}
				
				//get source files matching the criteria
				List<String> fileNameList = getMatchingFileNames(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat(), ingest.getIgnoreCase());
				if(fileNameList == null || fileNameList.isEmpty()) {
					throw new RuntimeException("File(s) \'"+ingest.getSourceDetail().getValue()+"\' not exist.");
				}
				
				List<String> location = new ArrayList<>();
				String sourceDir = "file://".concat(sourceDS.getPath());
				String sourceFileLocation = "";
				for(String fileName : fileNameList) {
					String fileLocation = sourceDir.endsWith("/") ? sourceDir.concat(fileName) : sourceDir.concat("/").concat(fileName);
					location.add(fileLocation);
					sourceFileLocation = sourceFileLocation.concat(fileLocation).concat(",");
				}
				sourceFileLocation = sourceFileLocation.substring(0, sourceFileLocation.lastIndexOf(","));
				ingestExec.setLocation(sourceFileLocation);
				runIngestServiceImpl.setLocation(location);
			} 
			runIngestServiceImpl.call();
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
				ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.Completed);
				commonServiceImpl.sendResponse("300", MessageStatus.SUCCESS.toString(), "No change in incremental param hence skipping execution.");
				throw new RuntimeException("No change in incremental param hence skipping execution.");
			} else {
				ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.Failed);
				commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Ingest execution failed.");
				throw new RuntimeException((message != null) ? message : "Ingest execution failed.");				
			}
		}
		
		return ingestExec;
	}

	public String generateFileName(String fileName, String fileFormat) {

		Pattern regex = Helper.getRegexByFileName(fileName, fileFormat, false);
		fileName = regex.pattern();
		fileName = fileName.substring(1, fileName.length()-1);
		fileName = fileName.replaceAll(Pattern.quote("\\."), ".");
		return fileName;
		/*if(fileName != null && fileName.toLowerCase().contains("mmddyyyy_hhmmss")) {
			String pattern = null;
			if(fileName.contains("MMddyyyy_HHmmss")) {
				pattern = "MMddyyyy_HHmmss";
			} else if(fileName.contains("mmddyyyy_HHmmss")) {
				pattern = "mmddyyyy_HHmmss";
			} else if(fileName.contains("MMddyyyy_hhmmss")) {
				pattern = "MMddyyyy_hhmmss";
			} else {
				pattern = "mmddyyyy_hhmmss";
			}	
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy_HHmmss");
			String formatedDate = dateFormat.format(new Date());
			return fileName.replaceAll(pattern, formatedDate);
		} else if(fileName != null && fileName.toLowerCase().contains("mmddyyyy")) {
			String pattern = null;
			if(fileName.contains("MMddyyyy")) {
				pattern = "MMddyyyy";
			} else {
				pattern = "mmddyyyy";
			}			
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
			String formatedDate = dateFormat.format(new Date());
			return fileName.replaceAll(pattern, formatedDate);
		} else {
			return fileName;
		}*/
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

	public String getIncrColName(Datapod datapod, AttributeRefHolder incrAttrHolder) {
		String attrName = null;
		for(Attribute attribute : datapod.getAttributes()) {
			if(attribute.getAttributeId().equals(Integer.parseInt(incrAttrHolder.getAttrId()))) {
				attrName = attribute.getName();
				break;
			}
		}
		return attrName;
	}

	public List<String> getMatchingFileNames(String filePath, String fileName, String fileFormat, String ignoreCase) throws JsonProcessingException, ParseException {
		logger.info("filePath : fileName : fileFormat : " + filePath + ":" + fileName + ":" + fileFormat);
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		List<String> fileNameList = new ArrayList<String>();
		boolean isCaseSensitive = getCaseSensitivity(ignoreCase);

		Pattern regex = null;
		regex = Helper.getRegexByFileName(fileName, fileFormat, isCaseSensitive);
		logger.info("Final regex : " + regex);

//		// Make regex compatible
//		fileName = fileName.replace(".","\\.").replace("*",".*");
//		
//		// Replace tokens
//		int occurences = StringUtils.countMatches(fileName,"[");
//		for (int i=0 ; i < occurences ; i++) {
//			String result = fileName.substring(fileName.indexOf("[") + 1, fileName.indexOf("]"));
//			SimpleDateFormat smplDateFormat = new SimpleDateFormat(result);
//			String dateFormat = smplDateFormat.format(new Date());
//			fileName = fileName.replaceAll("\\["+result+"\\]",dateFormat);
//		}
//		
//		//Apply Regex
//		if(isCaseSensitive) {
//			regex = Pattern.compile("^"+fileName+(fileName.toLowerCase().endsWith("."+fileFormat.toLowerCase()) ? "" : "\\."+fileFormat.toLowerCase())+"$");
//		} else {
//			regex = Pattern.compile("^"+fileName+(fileName.toLowerCase().endsWith("."+fileFormat.toLowerCase()) ? "" : "\\."+fileFormat.toLowerCase())+"$", Pattern.CASE_INSENSITIVE);
//		}

//		if(fileName.startsWith("*") && fileName.endsWith("*")) {
//			if(isCaseSensitive) {
//				regex = Pattern.compile("^.*"+fileName+".*$");
//			} else {
//				regex = Pattern.compile("^.*"+fileName+".*$", Pattern.CASE_INSENSITIVE);
//			}
//		} else if(fileName.endsWith("*")) { 
//			if(isCaseSensitive) {
//				regex = Pattern.compile("^"+fileName+".*$");
//			} else {
//				regex = Pattern.compile("^"+fileName+".*$", Pattern.CASE_INSENSITIVE);
//			}
//		} else if(fileName.toLowerCase().contains("%mmddyyyy%_%hhmmss%")) {
//			//e.g. account_%mmddyyyy%_%hhmmss%.csv
//			String[] fileNameSplit = fileName.split("%");
//			SimpleDateFormat smplDateFormat = new SimpleDateFormat("MMddyyyy"+"_"+"HHmmss");
//			String dateFormat = smplDateFormat.format(new Date());
//			fileName = fileName.replaceAll("%", "");
//			
//			if(isCaseSensitive) {
//				regex = Pattern.compile("^"+fileNameSplit[0]+dateFormat+fileNameSplit[4]+"$");
//			} else {
//				regex = Pattern.compile("^"+fileNameSplit[0]+dateFormat+fileNameSplit[4]+"$", Pattern.CASE_INSENSITIVE);
//			}
//		} else if(fileName.toLowerCase().contains("%mmddyyyy%")) {
//			//e.g. account_%mmddyyyy%.csv
//			String[] fileNameSplit = fileName.split("%");
//			SimpleDateFormat smplDateFormat = new SimpleDateFormat("MMddyyyy");
//			String dateFormat = smplDateFormat.format(new Date());
//			fileName = fileName.replaceAll("%", "");
//			
//			if(isCaseSensitive) {
//				regex = Pattern.compile("^"+fileNameSplit[0]+dateFormat+fileNameSplit[2]+"$");
//			} else {
//				regex = Pattern.compile("^"+fileNameSplit[0]+dateFormat+fileNameSplit[2]+"$", Pattern.CASE_INSENSITIVE);
//			}
//		} else {
//			if(isCaseSensitive) {
//				regex = Pattern.compile("^"+fileName+"$");
//			} else {
//				regex = Pattern.compile("^"+fileName+"$", Pattern.CASE_INSENSITIVE);
//			}					
//		}

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
				data = sparkExecutor.fetchIngestResult(targetDp, datastore.getName(), filePathUrl, Helper.getDelimetrByFormat(ingest.getTargetFormat()), resolveHeader(ingest.getHeader()), Integer.parseInt(""+datastore.getNumRows()), appUuid);
			} else if(ingest.getTargetFormat() != null && ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
				data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, limit, sortBy, order);
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
			commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.");
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
		List<IngestExec> ingestExecList = getIngestExecByIngest(ingestUuid, ingestVersion, 2);//(IngestExec) commonServiceImpl.getLatestByUuid(igstExecUuid, MetaType.ingestExec.toString());
		if(ingestExecList != null && (ingestExecList.isEmpty() || ingestExecList.size() <=1)) {
			return null;
		} else {
			return ingestExecList.get(1).getLastIncrValue();
		}
	}

	public List<IngestExec> getIngestExecByIngest(String ingestUuid, String ingestVersion, long linit) throws JsonProcessingException {
		MatchOperation dependsOnFilter = null;
		if(ingestVersion != null && !ingestVersion.isEmpty()) {
			dependsOnFilter = match(new Criteria("dependsOn.ref.uuid").is(ingestUuid).andOperator(new Criteria("dependsOn.ref.version").is(ingestVersion)));
		} else {
			dependsOnFilter = match(new Criteria("dependsOn.ref.uuid").is(ingestUuid));
		}
		GroupOperation groupByUuid = group("uuid").max("version").as("version"); 
		SortOperation sortByVersion = sort(new Sort(Direction.DESC, "version"));
		LimitOperation limitToOnlyFirst = limit(2);
		Aggregation ingestAggr = newAggregation(dependsOnFilter, groupByUuid, sortByVersion, limitToOnlyFirst);
		AggregationResults<IngestExec> ingestAggrResults = mongoTemplate.aggregate(ingestAggr, MetaType.ingestExec.toString().toLowerCase(), IngestExec.class);
		List<IngestExec> ingestExecList = ingestAggrResults.getMappedResults();
		List<IngestExec> resolvedIngestExecList = new ArrayList<>();
		for(IngestExec ingestExec : ingestExecList) {
			IngestExec resolvedIngestExec = (IngestExec) commonServiceImpl.getOneByUuidAndVersion(ingestExec.getId(), ingestExec.getVersion(), MetaType.ingestExec.toString());
			resolvedIngestExecList.add(resolvedIngestExec);
		}
		return resolvedIngestExecList;
	}
	
	public String getNewIncrValue(Datapod datapod, Datasource datasource, AttributeRefHolder incrAttrHolder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException, SQLException {
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
					commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not restart Ingest.");
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
			commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not restart Ingest.");
			throw new Exception((message != null) ? message : "Can not restart Ingest.");
		}
	}
	
	public void moveFile(String srcLocation, String targetLocation) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		logger.info("source location: "+srcLocation);
		logger.info("target location: "+targetLocation);
		try {
			FileUtils.moveFile(new File(srcLocation), new File(targetLocation));
		} catch (Exception e) {
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			if(message != null 
					&& message.contains("Destination '"+targetLocation+"' already exists")) {
				message = "Destination '"+targetLocation+"' already exists.";
				deleteFileOrDirectory(targetLocation, false);
				FileUtils.moveFile(new File(srcLocation), new File(targetLocation));
			} else {
				message = e.toString();
				commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), message);
				throw new RuntimeException(message);
			}
		}
	}
	
	public String getCSVFileNameFromDir(String directory) {
		File folder = new File(directory);
		for (File file : folder.listFiles()) {
			String dirFileName = file.getName();
			if (file.isFile() && dirFileName.toLowerCase().endsWith(".csv")) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}
	
	public void deleteFileOrDirectory(String absolutePath, boolean isDirectory) throws IOException {
		File file = new File(absolutePath);
		FileUtils.forceDelete(file);
	}
	
	public List<String> getTopicList(String dsUuid, String dsVersion, RunMode runMode) throws JsonProcessingException {
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
}
