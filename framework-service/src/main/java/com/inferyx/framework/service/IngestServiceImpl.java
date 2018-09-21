/**
 * 
 */
package com.inferyx.framework.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
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
public class IngestServiceImpl {
	
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private SqoopExecutor sqoopExecutor;
	@Autowired
	private ExecutorFactory execFactory;
	
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
			
			String targetFilePathUrl = String.format("%s%s", hdfsInfo.getHdfsURL(), targetDS.getPath());
			//String targetFilePathUrl = String.format("%s/%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
			
			IngestionType ingestionType = Helper.getIngestionType(ingest.getType());

			MetaIdentifier sourceDpMI = ingest.getSourceDetail().getRef();
			Datapod sourceDp = null;
			if(sourceDpMI.getUuid() != null) {
				sourceDp = (Datapod) commonServiceImpl.getLatestByUuid(sourceDpMI.getUuid(), sourceDpMI.getType().toString());
			}
			MetaIdentifier targetDpMI = ingest.getTargetDetail().getRef();
			Datapod targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
			String tableName = null;
			if(ingestionType.equals(IngestionType.FILETOFILE)) { 			
				tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
				List<String> fileNameList = getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());;
				if(fileNameList == null || fileNameList.isEmpty()) {
					throw new RuntimeException("File \'"+ingest.getSourceDetail().getValue()+"\' not exist.");
				}
				
				targetFilePathUrl = String.format("%s%s/%s/%s", targetFilePathUrl, targetDp.getUuid(), targetDp.getVersion(), ingestExec.getVersion());
				for(String fileName : fileNameList) {
					String fileName2 = fileName.substring(0, fileName.lastIndexOf("."));
					String sourceFilePathUrl = hdfsInfo.getHdfsURL() + sourceDS.getPath() + fileName;
					
					String header = resolveHeader(ingest.getHeader());
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, sourceFilePathUrl, Helper.getDelimetrByFormat(ingest.getSourceFormat()), header, appUuid, true);
					
					//adding version column to data
					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
					
					//applying source schema to df
					if(header.equalsIgnoreCase("false")) {
						rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, tableName, true);
					}
					
					//writing to target				
					rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, targetFilePathUrl, fileName2, tableName, "append", ingest.getTargetFormat());
					countRows = rsHolder.getCountRows();
				}
			} else if(ingestionType.equals(IngestionType.FILETOTABLE)) { 
				tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
				List<String> fileNameList = getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());
				if(fileNameList == null || fileNameList.isEmpty()) {
					throw new RuntimeException("File \'"+ingest.getSourceDetail().getValue()+"\' not exist.");
				}
				
				for(String fileName : fileNameList) {
//					String fileName2 = fileName.substring(0, fileName.lastIndexOf("."));
					String sourceFilePathUrl = hdfsInfo.getHdfsURL() + sourceDS.getPath() + "/" + fileName;
					
					String header = resolveHeader(ingest.getHeader());
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, sourceFilePathUrl, Helper.getDelimetrByFormat(ingest.getSourceFormat()), header, appUuid, true);
					rsHolder.setTableName(targetDS.getDbname()+"."+targetDp.getName());
					
					//adding version column data
					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
					
					//applying source schema to df
					if(header.equalsIgnoreCase("false")) {
						rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, tableName, true);
					}
					
					//writing to target
					sparkExecutor.persistDataframe(rsHolder, targetDS, targetDp);
					countRows = rsHolder.getCountRows();
				}
			} else if(ingestionType.equals(IngestionType.TABLETOFILE)) { 
				SqoopInput sqoopInput = new SqoopInput();
				sqoopInput.setSourceDs(sourceDS);
				sqoopInput.setTargetDs(targetDS);
				String targetDir = String.format("%s/%s/%s", hdfsInfo.getHdfsURL(), targetDS.getHost(), targetDS.getPath());
				String sourceDir = String.format("%s/%s/%s", hdfsInfo.getHdfsURL(), sourceDS.getHost(), sourceDS.getPath());
				sqoopInput.setSourceDirectory(sourceDir);
				sqoopInput.setTargetDirectory(targetDir);
//				String targetTable = String.format("%s/%s/%s", targetDp.getUuid().replaceAll("-", "_"), targetDp.getVersion(), ingestExec.getVersion());
//				sqoopInput.setTable(targetTable);
				sqoopInput.setTable(sourceDp.getName());
				sqoopInput.setIncrementalMode(SqoopIncrementalMode.AppendRows);
				sqoopInput.setExportDir(targetDir);
				if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					sqoopInput.setHiveImport(false);
					sqoopInput.setImportIntended(false);
				} else {
					sqoopInput.setExportDir(null);
					sqoopInput.setImportIntended(true);
				}
				sqoopExecutor.execute(sqoopInput);
			} else if(ingestionType.equals(IngestionType.TABLETOTABLE)) { 
				SqoopInput sqoopInput = new SqoopInput();
				sqoopInput.setSourceDs(sourceDS);
				sqoopInput.setTargetDs(targetDS);
				String targetDir = String.format("%s/%s/%s", hdfsInfo.getHdfsURL(), targetDS.getHost(), targetDS.getPath());
				String sourceDir = String.format("%s/%s/%s", hdfsInfo.getHdfsURL(), sourceDS.getHost(), sourceDS.getPath());
				logger.info("targetDir : " + targetDir);
				logger.info("sourceDir : " + sourceDir);
				sqoopInput.setSourceDirectory(sourceDir);
				sqoopInput.setTargetDirectory(targetDir);
				sqoopInput.setTable(sourceDp.getName());
				sqoopInput.setIncrementalMode(SqoopIncrementalMode.AppendRows);
				sqoopInput.setExportDir(targetDir);
				if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					sqoopInput.setHiveImport(false);
					sqoopInput.setImportIntended(false);
				} else if(targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					sqoopInput.setHiveImport(true);
					sqoopInput.setImportIntended(true);
				}
				sqoopExecutor.execute(sqoopInput);
			} 
			
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			MetaIdentifier datapodKey = new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion());
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
				IExecutor exec = execFactory.getExecutor(targetDS.getType());
				String tableName = targetDS.getDbname()+"."+targetDp.getName();
				String sql = generateSqlByDatasource(targetDS, tableName, limit);
				data = exec.executeAndFetch(sql, appUuid);
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
	
	public String generateSqlByDatasource(Datasource  datasource, String tableName, int limit) {
		if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {				
				return "SELECT * FROM " + tableName + " WHERE rownum< " + limit;
		} else {
			return "SELECT * FROM " + tableName + " LIMIT " + limit;
		}
	}
}
