/**
 * 
 */
package com.inferyx.framework.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SaveMode;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.athena.model.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
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
	private ExecutorFactory execFactory;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	
	static final Logger logger = Logger.getLogger(IngestServiceImpl.class);
	
	public IngestExec create(String ingestUuid, String ingestVersion, ExecParams execParams, IngestExec ingestExec, RunMode runMode) throws Exception {
		try {
			Ingest ingest = (Ingest) commonServiceImpl.getOneByUuidAndVersion(ingestUuid, ingestUuid, MetaType.ingest.toString());
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
			
			String targetFilePathUrl = hdfsInfo.getHdfsURL() + targetDS.getPath();// + "/" + fileName;
			
			IngestionType ingestionType = Helper.getIngestionType(ingest.getType());

			Datapod targetDp = null;
			String tableName = null;
			if(ingestionType.equals(IngestionType.FILETOFILE)) { 			
				tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
				List<String> fileNameList = getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());;
				for(String fileName : fileNameList) {
					String sourceFilePathUrl = hdfsInfo.getHdfsURL() + sourceDS.getPath() + "/" + fileName;
					
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, sourceFilePathUrl, appUuid);
					
					//writing to target
					MetaIdentifier targetDpMI = ingest.getTargetDetail().getRef();
					targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
					
//					rsHolder = sparkExecutor.persistDataframe(rsHolder, targetDp, "append", targetFilePathUrl, tableName, false);					
					rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetFilePathUrl, tableName, "append", ingest.getTargetFormat());
					countRows = rsHolder.getCountRows();
				}
			} else if(ingestionType.equals(IngestionType.FILETOTABLE)) { 
				tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
				List<String> fileNameList = getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());;
				for(String fileName : fileNameList) {
					String sourceFilePathUrl = hdfsInfo.getHdfsURL() + sourceDS.getPath() + "/" + fileName;
					
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, sourceFilePathUrl, appUuid);
					
					//writing to target
					MetaIdentifier targetDpMI = ingest.getTargetDetail().getRef();
					targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
					sparkExecutor.persistDataframe(rsHolder, targetDS, targetDp);
					countRows = rsHolder.getCountRows();
				}
			} else if(ingestionType.equals(IngestionType.TABLETOFILE)) { 
				
			} else if(ingestionType.equals(IngestionType.TABLETOTABLE)) { 
				
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
				if(dirFileName.matches(fileName) && dirFileName.toLowerCase().endsWith("."+fileFormat.toLowerCase())) {
					fileNameList.add(dirFileName);					
				}
			} else if (listOfFiles[i].isDirectory()) {
				logger.info("Directory " + listOfFiles[i].getName());
			}
		}		
		return fileNameList;
	}

	public List<Map<String, Object>> getResults(String execUuid, String execVersion, int rowLimit) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException, IOException {
		try {
			IngestExec ingestExec = (IngestExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.ingestExec.toString());
			Ingest ingest = (Ingest) commonServiceImpl.getOneByUuidAndVersion(ingestExec.getDependsOn().getRef().getUuid(), ingestExec.getDependsOn().getRef().getVersion(), MetaType.ingest.toString());
			MetaIdentifier targetDpMI = ingest.getTargetDetail().getRef();
			Datapod targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
			
			DataStore datastore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(ingestExec.getResult().getRef().getUuid(), ingestExec.getResult().getRef().getVersion(), MetaType.datastore.toString());
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String targetTable = null;
			if(targetDp != null)
				targetTable = datasource.getDbname()+"."+targetDp.getName();
			List<Map<String, Object>> strList = exec.fetchResults(datastore, targetDp, rowLimit, targetTable, commonServiceImpl.getApp().getUuid());
	
			return strList;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "No data found.");
			throw new RuntimeException((message != null) ? message : "No data found.");
		}
	}
	
	protected void persistDatastore(String tableName, String filePath, MetaIdentifierHolder resultRef, MetaIdentifier datapodKey, IngestExec ingestExec, long countRows, RunMode runMode) throws Exception {
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, tableName, datapodKey, ingestExec.getRef(MetaType.ingestExec), ingestExec.getAppInfo(), ingestExec.getCreatedBy(), SaveMode.Append.toString(), resultRef, countRows, Helper.getPersistModeFromRunMode(runMode.toString()), null);
	}
}
