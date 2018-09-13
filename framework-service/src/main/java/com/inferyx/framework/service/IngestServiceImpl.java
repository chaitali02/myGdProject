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
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.executor.SqoopExecutor;

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
			
			String targetFilePathUrl = String.format("%s/%s", hdfsInfo.getHdfsURL(), targetDS.getPath());
			//String targetFilePathUrl = String.format("%s/%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
			
			IngestionType ingestionType = Helper.getIngestionType(ingest.getType());

			Datapod targetDp = null;
			String tableName = null;
			if(ingestionType.equals(IngestionType.FILETOFILE)) { 			
				tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
				List<String> fileNameList = getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());;

				MetaIdentifier targetDpMI = ingest.getTargetDetail().getRef();
				targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
				targetFilePathUrl = String.format("%s/%s/%s/%s", targetFilePathUrl, targetDp.getUuid().replaceAll("-", "_"), targetDp.getVersion(), ingestExec.getVersion());
				for(String fileName : fileNameList) {
					String fileName2 = fileName.substring(0, fileName.lastIndexOf("."));
					String sourceFilePathUrl = hdfsInfo.getHdfsURL() + sourceDS.getPath() + fileName;
					
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, sourceFilePathUrl, Helper.getDelimetrByFormat(ingest.getSourceFormat()), "true", appUuid, true);
					
					//writing to target				
					rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, targetFilePathUrl, fileName2, tableName, "append", ingest.getTargetFormat());
					countRows = rsHolder.getCountRows();
				}
			} else if(ingestionType.equals(IngestionType.FILETOTABLE)) { 
				tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
				List<String> fileNameList = getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());
				MetaIdentifier targetDpMI = ingest.getTargetDetail().getRef();
				targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
				for(String fileName : fileNameList) {
//					String fileName2 = fileName.substring(0, fileName.lastIndexOf("."));
					String sourceFilePathUrl = hdfsInfo.getHdfsURL() + sourceDS.getPath() + "/" + fileName;
					
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, sourceFilePathUrl, Helper.getDelimetrByFormat(ingest.getSourceFormat()), "false", appUuid, true);
					rsHolder.setTableName(targetDS.getDbname()+"."+targetDp.getName());
					//writing to target
					sparkExecutor.persistDataframe(rsHolder, targetDS, targetDp);
					countRows = rsHolder.getCountRows();
				}
			} else if(ingestionType.equals(IngestionType.TABLETOFILE)) { 
				SqoopInput sqoopInput = new SqoopInput();
				sqoopInput.setSourceDs(sourceDS);
				sqoopInput.setTargetDs(targetDS);
				sqoopExecutor.execute(sqoopInput);
			} else if(ingestionType.equals(IngestionType.TABLETOTABLE)) { 
				SqoopInput sqoopInput = new SqoopInput();
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

			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Ingest execution fauled.");
			throw new RuntimeException((message != null) ? message : "Ingest execution fauled.");
		}
		
		return ingestExec;
	}

	public List<String> getFileDetailsByFileName(String filePath, String fileName, String fileFormat) throws JsonProcessingException {
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		List<String> fileNameList = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String dirFileName = listOfFiles[i].getName();
				if(fileName.endsWith("*")) {
					Pattern regex = Pattern.compile("^.*"+fileName+".*\\."+fileFormat+"$", Pattern.CASE_INSENSITIVE);
					Matcher mtch = regex.matcher(dirFileName);
			        if(mtch.matches()){
			        	fileNameList.add(dirFileName);	
			        }
				} else {
					Pattern regex = Pattern.compile("^"+fileName+"\\."+fileFormat+"$", Pattern.CASE_INSENSITIVE);
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
			
			if(ingest.getTargetFormat() != null) {
				data = sparkExecutor.fetchIngestResult(targetDp, datastore.getName(), datastore.getLocation(), Helper.getDelimetrByFormat(ingest.getSourceFormat()), "false", Integer.parseInt(""+datastore.getNumRows()), appUuid);
			} else {
				data = dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), requestId, offset, limit, sortBy, order);
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
}
