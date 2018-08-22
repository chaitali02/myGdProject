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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.spark.sql.SaveMode;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitosync.model.Dataset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.WorkbookUtil;
import com.inferyx.framework.domain.BaseRule;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.DownloadExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.ReportOperator;

/**
 * @author Ganesh
 *
 */
@Service
public class ReportServiceImpl {
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private ReportOperator reportOperator;
	@Autowired
	private Engine engine;
	@Autowired
	private Helper helper;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	HDFSInfo hdfsInfo;
	
	static final Logger logger = Logger.getLogger(ReportServiceImpl.class);
	
	public ReportExec create(String reportUuid, String reportVersion, ExecParams execParams, ReportExec reportExec, RunMode runMode) throws Exception {
		try {
			Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(reportUuid, reportVersion, MetaType.report.toString());
			if (reportExec == null) {
				MetaIdentifierHolder trainRef = new MetaIdentifierHolder();
				reportExec = new ReportExec();
				trainRef.setRef(new MetaIdentifier(MetaType.report, reportUuid, reportVersion));
				reportExec.setDependsOn(trainRef);
				reportExec.setBaseEntity();
			}
			
			reportExec.setExecParams(execParams);
			
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = reportExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			reportExec.setName(report.getName());
			reportExec.setAppInfo(report.getAppInfo());	
			
			commonServiceImpl.save(MetaType.reportExec.toString(), reportExec);
			
			reportExec.setRefKeyList(new ArrayList<>(usedRefKeySet));

			reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.NotStarted);
		} catch (Exception e) {
			logger.error(e);
			reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.Failed);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable report.");
			throw new RuntimeException((message != null) ? message : "Can not create executable report.");
		}		
		
		return reportExec;
	}
	
	public ReportExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, DagExec dagExec, RunMode runMode) throws Exception {
		ReportExec reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.reportExec.toString());
		try {			
			logger.info("Inside reportServiceImpl.parse");
			Report report = null;
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			
			ExecParams execParams = reportExec.getExecParams();
			if(execParams != null) {
				otherParams = execParams.getOtherParams();
			}
			report = (Report) commonServiceImpl.getLatestByUuid(reportExec.getDependsOn().getRef().getUuid(), MetaType.report.toString());
			reportExec.setExec(reportOperator.generateSql(report, refKeyMap, otherParams, usedRefKeySet, reportExec.getExecParams(), runMode));
			reportExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			logger.info("sql_generated: " + reportExec.getExec());
			commonServiceImpl.save(MetaType.reportExec.toString(), reportExec);
		} catch (Exception e) {
			logger.error(e);
			reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.Failed);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable report.");
			throw new RuntimeException((message != null) ? message : "Can not create executable report.");
		}
		return reportExec;
	}
	
	public ReportExec execute(String reportUuid, String reportVersion, ExecParams execParams, ReportExec reportExec, RunMode runMode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException, IOException {
		
		try {
			Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(reportUuid, reportVersion, MetaType.report.toString());
			
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			long countRows = -1L;
			
			reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.InProgress);
			String filePath = getFileName(report, reportExec);
			String tableName = null;
			
			if (StringUtils.isBlank(reportExec.getExec())) {
				throw new RuntimeException("sql not generated");
			}
			
			IExecutor exec = null;
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			ExecContext execContext = null;
			String appUuid = null;
			
			if (runMode == null || runMode.equals(RunMode.ONLINE)) {
				execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark") || engine.getExecEngine().equalsIgnoreCase("livy_spark"))
						? helper.getExecutorContext(engine.getExecEngine()) : helper.getExecutorContext(ExecContext.spark.toString());
				appUuid = commonServiceImpl.getApp().getUuid();
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			exec = execFactory.getExecutor(execContext.toString());
			
			tableName = getTableName(report, reportExec, execContext);
			appUuid = commonServiceImpl.getApp().getUuid();
			ResultSetHolder rsHolder = null;
			if (runMode!= null && runMode.equals(RunMode.BATCH)) {
				if(execContext.equals(ExecContext.FILE)
						|| execContext.equals(ExecContext.livy_spark)
						|| execContext.equals(ExecContext.spark))
					//exec.executeRegisterAndPersist(reportExec.getExec(), tableName, filePath, null, "overwrite", appUuid);
					exec.executeAndRegister(reportExec.getExec(), tableName, appUuid);
				else {
					String sql = helper.buildInsertQuery(execContext.toString(), tableName, null, reportExec.getExec());
					exec.executeSql(sql, appUuid);
				}
			} else {
				rsHolder = exec.executeAndRegister(reportExec.getExec(), tableName, appUuid);
				countRows = rsHolder.getCountRows();
			}
			
			persistDatastore(reportExec, tableName, filePath, resultRef, new MetaIdentifier(MetaType.report, report.getUuid(), report.getVersion()), countRows, runMode);

			reportExec.setResult(resultRef);
			reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.Completed);
		} catch (IOException e) { 
			e.printStackTrace();
			// Set status to Failed
			try {
				reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.Failed);
			} catch (Exception e1) {
				e1.printStackTrace();
			}			
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Report execution failed.");
			throw new RuntimeException((message != null) ? message : "Report execution failed.");
		} catch (Exception e) { 
			e.printStackTrace();
			// Set status to Failed
			try {
				reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.Failed);
			} catch (Exception e1) {
				e1.printStackTrace();
			}			
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Report execution failed.");
			throw new RuntimeException((message != null) ? message : "Report execution failed.");
		}
		
		
		
		return reportExec;
	}
	
	protected String getFileName(Report report, ReportExec reportExec) {
		return String.format("/%s/%s/%s", report.getUuid(), report.getVersion(), reportExec.getVersion());
	}
	
	protected String getTableName(Report report, ReportExec reportExec, ExecContext execContext) throws JsonProcessingException {
		if (execContext == null || execContext.equals(ExecContext.spark) || execContext.equals(ExecContext.FILE) 
				|| execContext.equals(ExecContext.livy_spark)) {
			return String.format("%s_%s_%s", report.getUuid().replace("-", "_"), report.getVersion(), reportExec.getVersion());
		}
		
		MetaIdentifier dependsOn = report.getDependsOn().getRef();
		if(dependsOn.getType().equals(MetaType.datapod)) {
			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getUuid(), dependsOn.getVersion(), dependsOn.getType().toString());
			return "temp"+datapod.getName().trim();
		} else if(report.getDependsOn().getRef().getType().equals(MetaType.dataset)) {
			DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getUuid(), dependsOn.getVersion(), dependsOn.getType().toString());
			return "temp"+dataSet.getName().trim();
		} else if(report.getDependsOn().getRef().getType().equals(MetaType.relation)) {
			Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getUuid(), dependsOn.getVersion(), dependsOn.getType().toString());
			return "temp"+relation.getName().trim();
		}
		return null;
	}
	
	protected void persistDatastore(ReportExec reportExec, String tableName, String filePath, MetaIdentifierHolder resultRef, MetaIdentifier metaId, long countRows, RunMode runMode) throws Exception {
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, tableName, metaId, reportExec.getRef(MetaType.reportExec), reportExec.getAppInfo(), reportExec.getCreatedBy(), SaveMode.Append.toString(), resultRef, countRows, Helper.getPersistModeFromRunMode(runMode.toString()), null);
	}

	public List<Map<String, Object>> getReportSample(String reportExecUuid, String reportExecVersion, int rows,
			ExecParams execParams, RunMode runMode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException, IOException {
		ReportExec reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(reportExecUuid, reportExecVersion, MetaType.reportExec.toString());
		DataStore datastore = dataStoreServiceImpl.findDatastoreByExec(reportExec.getResult().getRef().getUuid(), reportExec.getResult().getRef().getVersion());
		
		return dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, rows, null, null);
	}
	
	public HttpServletResponse download(String reportExecUuid, String reportExecVersion, HttpServletResponse response, RunMode runMode) throws Exception {
		datastoreServiceImpl.setRunMode(runMode);
		DataStore datastore = datastoreServiceImpl.findDataStoreByMeta(reportExecUuid, reportExecVersion);
		if (datastore == null) {
			logger.error("Datastore is not available for this datapod");
			throw new Exception("Datastore is not available.");
		}

		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		String tableName = datastoreServiceImpl.getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(), runMode);
		String sql = "SELECT * FROM " + tableName;
		List<Map<String, Object>> data = exec.executeAndFetch(sql, commonServiceImpl.getApp().getUuid());		

	    DownloadExec downloadExec = new DownloadExec();
		
	    String downloadPath = Helper.getPropertyValue("framework.file.download.path");
	    String filename = downloadExec.getUuid() + "_" + downloadExec.getVersion() + ".pdf";
	    String fileLocation = downloadPath + "/" + filename;
	    
		downloadExec.setBaseEntity();
		downloadExec.setLocation(fileLocation);
		downloadExec.setDependsOn(datastore.getMetaId());
		try {
			FileOutputStream fileOut = null;
			HSSFWorkbook workbook = WorkbookUtil.getWorkbook(data);
			response.setContentType("application/xml charset=utf-16");
			response.setHeader("Content-disposition", "attachment");
			response.setHeader("filename", "" + reportExecUuid+"_"+reportExecUuid + ".pdf");
			ServletOutputStream os = response.getOutputStream();
			workbook.write(os);

			fileOut = new FileOutputStream(fileLocation);
			workbook.write(fileOut);
			os.write(workbook.getBytes());
			commonServiceImpl.save(MetaType.downloadExec.toString(), downloadExec);			
			fileOut.close();

		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Can not download file.");
			response.setStatus(300);
        	throw new FileNotFoundException("Can not download file.");
		}
		return response;
	}
}
