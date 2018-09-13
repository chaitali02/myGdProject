/**
 * 
 */
package com.inferyx.framework.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
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
import com.inferyx.framework.executor.SparkExecutor;

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
		Ingest ingest = (Ingest) commonServiceImpl.getOneByUuidAndVersion(ingestUuid, ingestVersion, MetaType.ingest.toString());
		ingestExec = (IngestExec) commonServiceImpl.setMetaStatus(ingestExec, MetaType.ingestExec, Status.Stage.InProgress);
		String appUuid = commonServiceImpl.getApp().getUuid();
		MetaIdentifier sourceDSMI = ingest.getSourceDatasource().getRef();
		Datasource sourceDS = (Datasource) commonServiceImpl.getLatestByUuid(sourceDSMI.getUuid(), sourceDSMI.getType().toString());
		MetaIdentifier targetDSMI = ingest.getTargetDatasource().getRef();
		Datasource targetDS = (Datasource) commonServiceImpl.getLatestByUuid(targetDSMI.getUuid(), targetDSMI.getType().toString());
		
		IngestionType ingestionType = Helper.getIngestionType(ingest.getType());

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
				Datapod targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
				String targetFilePathUrl = hdfsInfo.getHdfsURL() + targetDS.getPath();// + "/" + fileName;
				rsHolder = sparkExecutor.persistDataframe(rsHolder, targetDp, "append", targetFilePathUrl, tableName, false);
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
				Datapod targetDp = (Datapod) commonServiceImpl.getLatestByUuid(targetDpMI.getUuid(), targetDpMI.getType().toString());
			}
		} else if(ingestionType.equals(IngestionType.TABLETOFILE)) { 
			
		} else if(ingestionType.equals(IngestionType.TABLETOTABLE)) { 
			
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
}
