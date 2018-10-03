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
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Attribute;
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
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.enums.SqoopIncrementalMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.executor.SqoopExecutor;

/**
 * @author Ganesh
 *
 */
public class RunIngestServiceImpl implements Callable<TaskHolder> {
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
	
	public static Logger logger = Logger.getLogger(RunIngestServiceImpl.class); 

//	/**
//	 *
//	 * @Ganesh
//	 *
//	 * @return the fileName
//	 */
//	public String getFileName() {
//		return fileName;
//	}
//
//	/**
//	 *
//	 * @Ganesh
//	 *
//	 * @param fileName the fileName to set
//	 */
//	public void setFileName(String fileName) {
//		this.fileName = fileName;
//	}

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
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(MetaType.ingestExec, ingestExec.getUuid(), ingestExec.getVersion()));
		return taskHolder;
	}

	public IngestExec execute() throws Exception {
		try {
			long countRows = -1L;
			
			String targetFilePathUrl = helper.getPathByDataSource(targetDS);
			String sourceFilePathUrl = helper.getPathByDataSource(sourceDS);
			
			IngestionType ingestionType = Helper.getIngestionType(ingest.getType());

			MetaIdentifier sourceDpMI = ingest.getSourceDetail().getRef();
			String incrLastValue = null;
			String latestIncrLastValue = null;
			String incrColName = null;
			if(sourceDpMI.getUuid() != null) {
				//finding incremental column name
				incrColName = ingestServiceImpl.getIncrColName(sourceDp, ingest.getIncrAttr());
				
				//finding last incremental value
				incrLastValue = ingestServiceImpl.getLastIncrValue(ingest.getUuid(), ingest.getVersion());
				
				//finding latest incremental value
				latestIncrLastValue = ingestServiceImpl.getNewIncrValue(sourceDp, sourceDS, ingest.getIncrAttr());
			}	

			logger.info("mode : configuration >> "+ingest.getType()+" : "+sourceDS.getType()+"_2_"+targetDS.getType());
			String tableName = null;
			if(ingestionType.equals(IngestionType.FILETOFILE)) { 	
				tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
//				List<String> fileNameList = ingestServiceImpl.getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());;
//				if(fileNameList == null || fileNameList.isEmpty()) {
//					throw new RuntimeException("File \'"+ingest.getSourceDetail().getValue()+"\' not exist.");
//				}
				
				String targetFileName = ingest.getTargetDetail().getValue();
				if(ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
					targetFilePathUrl = String.format("%s%s/%s/%s/%s", targetFilePathUrl, ingest.getUuid(), ingest.getVersion(), ingestExec.getVersion(), ingest.getTargetDetail().getValue());
				} else {
					if(!targetFileName.toLowerCase().endsWith(".csv")) {
						targetFileName = targetFileName.concat(".csv");
					}
//					targetFilePathUrl = targetFilePathUrl.concat(targetFileName);
					targetFilePathUrl = targetDS.getPath().concat(targetFileName);
				}
				
//				targetFilePathUrl = String.format("%s%s", targetFilePathUrl, ingest.getTargetDetail().getValue());
//				for(String fileName : fileNameList) {
//					String targetFileName = ingest.getTargetDetail().getValue();//fileName.substring(0, fileName.lastIndexOf("."));
				
//					sourceFilePathUrl = sourceFilePathUrl + fileName;
					
					String header = ingestServiceImpl.resolveHeader(ingest.getHeader());
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, location, Helper.getDelimetrByFormat(ingest.getSourceFormat()), header, appUuid, false);
					
					//adding version column to data
//					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
					
					//applying target schema to df
					if(header.equalsIgnoreCase("false") && targetDp != null) {
						rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, tableName, false);
					}
					
					String saveMode = null;
					if(ingest.getSaveMode() != null) {
						saveMode = ingest.getSaveMode().toString();
					} else {
						saveMode = SaveMode.OVERWRITE.toString();
					}
					
					String tempDirPath = Helper.getPropertyValue("framework.temp.path");
					String tempDirLocation = tempDirPath.endsWith("/") ? "file://"+tempDirPath+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/" : "file://"+tempDirPath.concat("/")+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/";
					logger.info("temporary location: "+tempDirLocation);
					
					//writing to target				
					rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, 
							ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString()) ? targetFilePathUrl : tempDirLocation
									, targetFileName, tableName, saveMode, ingest.getTargetFormat());
					
					if(!ingest.getTargetFormat().equalsIgnoreCase(FileType.PARQUET.toString())) {
						try {
							tempDirLocation = tempDirPath.endsWith("/") ? tempDirPath+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/" : tempDirPath.concat("/")+ingestExec.getUuid()+"/"+ingestExec.getVersion()+"/";
							String srcFilePath = ingestServiceImpl.getCSVFileNameFromDir(tempDirLocation);
							ingestServiceImpl.moveFile(srcFilePath, targetFilePathUrl);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ingestServiceImpl.deleteFileOrDirectory(tempDirPath.endsWith("/") ? tempDirPath+ingestExec.getUuid() : tempDirPath.concat("/")+ingestExec.getUuid(), true);
						}						
					}
					if(ingestExec.getLocation() != null) {
						String targetFilePathUrl2 = ingestExec.getLocation().concat(","+targetFilePathUrl);
						ingestExec.setLocation(targetFilePathUrl2);
					} else {
						ingestExec.setLocation(targetFilePathUrl);
					}
					countRows = rsHolder.getCountRows();
//				}
//				targetFilePathUrl = null;
			} else if(ingestionType.equals(IngestionType.FILETOTABLE)) { 
				if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString()) 
						&& targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					//this is export block from HDFS to HIVE

					logger.info("this is export block from HDFS to HIVE");
					tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
					String fileName = ingest.getSourceDetail().getValue();			
					
					//sourceFilePathUrl = String.format("%s/%s/%s", "hdfs://"+sourceDS.getHost()+":8020", sourceDS.getPath(), fileName);
					sourceFilePathUrl = String.format("%s/%s/%s", Helper.getPropertyValue("hive.fs.default.name"), sourceDS.getPath(), fileName);
					if(sourceFilePathUrl.contains(".db")) {
						sourceFilePathUrl = sourceFilePathUrl.replaceAll(".db", "");
					}
					logger.info("sourceFilePathUrl: "+sourceFilePathUrl);
					String header = ingestServiceImpl.resolveHeader(ingest.getHeader());
					List<String> location = new ArrayList<>();
					location.add(sourceFilePathUrl);
					//reading from source
					ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, location, Helper.getDelimetrByFormat(ingest.getSourceFormat()), header, appUuid, true);
					rsHolder.setTableName(targetDS.getDbname()+"."+targetDp.getName());
					
					//adding version column data
//					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
					
					//applying target schema to df
					if(header.equalsIgnoreCase("false")) {
						rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, tableName, true);
					}
					
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
					if(incrLastValue != null) {
						sqoopInput.setIncrementalLastValue(incrLastValue);
					}
					targetFilePathUrl = targetFilePathUrl+ingest.getSourceDetail().getValue();
					Map<String, String> inputParams = null;
					if(ingest.getRunParams() != null) {
						inputParams = ingestServiceImpl.getRunParams(ingest.getRunParams());
					}
					sqoopExecutor.execute(sqoopInput, inputParams);
				} else {
					//this is export block from local file to Table

					logger.info("this is export block from local file to Table");
					tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
//					List<String> fileNameList = ingestServiceImpl.getFileDetailsByFileName(sourceDS.getPath(), ingest.getSourceDetail().getValue(), ingest.getSourceFormat());
//					if(fileNameList == null || fileNameList.isEmpty()) {
//						throw new RuntimeException("File \'"+ingest.getSourceDetail().getValue()+"\' not exist.");
//					}
//					
//					for(String fileName : fileNameList) {
//						sourceFilePathUrl = sourceFilePathUrl + fileName;					
						String header = ingestServiceImpl.resolveHeader(ingest.getHeader());
						//reading from source
						ResultSetHolder rsHolder = sparkExecutor.readAndRegisterFile(tableName, location, Helper.getDelimetrByFormat(ingest.getSourceFormat()), header, appUuid, true);
						rsHolder.setTableName(targetDS.getDbname()+"."+targetDp.getName());
						
						//adding version column data
//						rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());
						
						//applying target schema to df
						if(header.equalsIgnoreCase("false")) {
							rsHolder = sparkExecutor.applySchema(rsHolder, targetDp, tableName, true);
						}
						
						//writing to target
						sparkExecutor.persistDataframe(rsHolder, targetDS, targetDp, ingest.getSaveMode().toString());
						countRows = rsHolder.getCountRows();
//					}
				}
			} else if(ingestionType.equals(IngestionType.TABLETOFILE)) { 								
				if(sourceDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString()) 
						&& targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					//this is export block from Hive to HDFS

					logger.info("this is export block from Hive to HDFS");
					String sourceDir = String.format("%s/%s", sourceDS.getPath(), sourceDp.getName());
					
//					targetFilePathUrl = String.format("%s/%s/%s/%s/%s/%s", Helper.getPropertyValue("hive.fs.default.name"), targetDS.getPath(), ingest.getUuid(), ingest.getVersion(), ingestExec.getVersion(), ingest.getTargetDetail().getValue());
					targetFilePathUrl = String.format("%s/%s/%s", Helper.getPropertyValue("hive.fs.default.name"), targetDS.getPath(), ingest.getTargetDetail().getValue());
					if(targetFilePathUrl.contains(".db")) {
						targetFilePathUrl = targetFilePathUrl.replaceAll(".db", "");
					}
					logger.info("sourceDir : " + sourceDir);
					logger.info("targetDir : " + targetFilePathUrl);
					
					String sourceTableName = sourceDS.getDbname() +"."+sourceDp.getName();
					
					tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
					
					String sql = ingestServiceImpl.generateSqlByDatasource(targetDS, sourceTableName, incrColName, incrLastValue, 0);
					ResultSetHolder rsHolder = sparkExecutor.executeSqlByDatasource(sql, sourceDS, appUuid);
					
					//registering temp table of source
//					sparkExecutor.registerDataFrameAsTable(rsHolder, tableName);
					
					//adding version column data
//					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());

					//writing to target				
					rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, targetFilePathUrl, ingest.getTargetDetail().getValue(), tableName, ingest.getSaveMode().toString(), ingest.getTargetFormat());
					countRows = rsHolder.getCountRows();
//					targetFilePathUrl = null;
				} else if(targetDS.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
					//this is import block from Table to HDFS

					logger.info("this is import block from Table to HDFS");
					SqoopInput sqoopInput = new SqoopInput();
					sqoopInput.setSourceDs(sourceDS);
					sqoopInput.setTargetDs(targetDS);
					sqoopInput.setIncrementalMode(SqoopIncrementalMode.AppendRows);
					sqoopInput.setHiveImport(false);
					sqoopInput.setImportIntended(true);
					String sourceDir = String.format("%s/%s", sourceDS.getPath(), sourceDp.getName());
					String targetDir = String.format("%s/%s", targetDS.getPath(), ingest.getTargetDetail().getValue());
					if(targetDir.contains(".db")) {
						targetDir = targetDir.replaceAll(".db", "");
					}
					logger.info("sourceDir : " + sourceDir);
					logger.info("targetDir : " + targetDir);
					sqoopInput.setExportDir(targetDir);
//					sqoopInput.setSourceDirectory(sourceDir);
					sqoopInput.setTargetDirectory(targetDir);
					sqoopInput.setTable(sourceDp.getName());
					sqoopInput.setAppendMode(ingest.getSaveMode().equals(com.inferyx.framework.enums.SaveMode.APPEND));
//					sqoopInput.setFileLayout(sqoopExecutor.getFileLayout(ingest.getTargetFormat()));
					if(incrLastValue != null) {
						sqoopInput.setIncrementalLastValue(incrLastValue);
					}
					targetFilePathUrl = targetFilePathUrl+sourceDp.getName();
					Map<String, String> inputParams = null;
					if(ingest.getRunParams() != null) {
						inputParams = ingestServiceImpl.getRunParams(ingest.getRunParams());
					}
					tableName = sourceDp.getName();
					sqoopExecutor.execute(sqoopInput, inputParams);
				} else {
					//this is export block from Hive table to local file

					logger.info("this is export block from Hive table to local file");
					String sourceDir = String.format("%s/%s", sourceDS.getPath(), sourceDp.getName());
					targetFilePathUrl = String.format("%s%s", targetFilePathUrl, String.format("%s/%s/%s", ingest.getUuid(), ingest.getVersion(), ingestExec.getVersion()));
					if(targetFilePathUrl.contains(".db")) {
						targetFilePathUrl = targetFilePathUrl.replaceAll(".db", "");
					}
					logger.info("sourceDir : " + sourceDir);
					logger.info("targetDir : " + targetFilePathUrl);
					
//					String targetTableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
					String sourceTableName = sourceDS.getDbname() +"."+sourceDp.getName();

					tableName = String.format("%s_%s_%s", ingest.getUuid().replaceAll("-", "_"), ingest.getVersion(), ingestExec.getVersion());
					
					String sql = ingestServiceImpl.generateSqlByDatasource(targetDS, sourceTableName, incrColName, incrLastValue, 0);
					ResultSetHolder rsHolder = sparkExecutor.executeSqlByDatasource(sql, sourceDS, appUuid);
					
					//registering temp table of source
//					sparkExecutor.registerDataFrameAsTable(rsHolder, tableName);
					
					//adding version column data
//					rsHolder = sparkExecutor.addVersionColToDf(rsHolder, tableName, ingestExec.getVersion());

					//writing to target				
					rsHolder = sparkExecutor.writeFileByFormat(rsHolder, targetDp, targetFilePathUrl, ingest.getTargetDetail().getValue(), tableName, ingest.getSaveMode().toString(), ingest.getTargetFormat());
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

					logger.info("this is export block from Hive to other table");
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

					logger.info("this is import block from other table to Hive");
					Map<String, String> partitions = checkPartitionsByDatapod(targetDp);
					if(!partitions.isEmpty()) {
						
					}

					sqoopInput.setOverwriteHiveTable(ingest.getSaveMode().toString());
					sqoopInput.setTable(sourceDp.getName());
					sqoopInput.setHiveImport(true);
					sqoopInput.setImportIntended(true);
					sqoopInput.setTargetDirectory(targetDir);
//					sqoopInput.setHiveTableName(targetDp.getName());
//					sqoopInput.setHiveDatabaseName(targetDS.getDbname());
					sqoopInput.setHCatalogTableName(targetDp.getName());
					sqoopInput.setHCatalogDatabaseName(targetDS.getDbname());
//					sqoopInput.sethCatalogPartitionKeys(hCatalogPartitionKeys);
//					sqoopInput.sethCatalogPartitionValues(hCatalogPartitionValues);
					tableName = targetDp.getName();
				}

				sqoopInput.setAppendMode(ingest.getSaveMode().equals(com.inferyx.framework.enums.SaveMode.APPEND));
				if(incrLastValue != null) {
					sqoopInput.setIncrementalLastValue(incrLastValue);
				}
				targetFilePathUrl = targetFilePathUrl+sourceDp.getName();
				Map<String, String> inputParams = null;
				if(ingest.getRunParams() != null) {
					inputParams = ingestServiceImpl.getRunParams(ingest.getRunParams());
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
			ingestServiceImpl.persistDatastore(tableName, targetFilePathUrl, resultRef, datapodKey, ingestExec, countRows, runMode);
			
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
			commonServiceImpl.sendResponse("500", MessageStatus.FAIL.toString(), (message != null) ? message : "Ingest execution failed.");
			throw new RuntimeException((message != null) ? message : "Ingest execution failed.");
		}
		
		return ingestExec;
	}

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
}
