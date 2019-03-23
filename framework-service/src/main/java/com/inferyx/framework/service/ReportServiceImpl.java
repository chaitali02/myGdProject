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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.spark.sql.SaveMode;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.PDFUtil;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.common.WorkbookUtil;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntityStatus;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.DownloadExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Notification;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.domain.ReportExecView;
import com.inferyx.framework.domain.SenderInfo;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.operator.ReportOperator;

/**
 * @author Ganesh
 *
 */
@Service
public class ReportServiceImpl extends RuleTemplate {
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired 
	private MetadataServiceImpl metadataServiceImpl;
	@Autowired
	private ReportOperator reportOperator;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	private WorkbookUtil workbookUtil;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private NotificationServiceImpl notificationServiceImpl;
	@Autowired
	private PDFUtil pdfUtil;
	@Autowired
	private DocumentGenServiceImpl documentGenServiceImpl;
	
	static final Logger logger = Logger.getLogger(ReportServiceImpl.class);
	
	public ReportExec create(String reportUuid, String reportVersion, ExecParams execParams, ReportExec reportExec, RunMode runMode) throws Exception {
		try {
			Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(reportUuid, reportVersion, MetaType.report.toString());
			if (reportExec == null) {
				MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.report, reportUuid, reportVersion));
				reportExec = new ReportExec();
				reportExec.setDependsOn(dependsOn);
				reportExec.setBaseEntity();
			}
			
			reportExec.setExecParams(execParams);
			
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = reportExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			if (Helper.getLatestStatus(statusList) != null
					&& Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.READY, new Date()))) {
				logger.info(" If status is in READY state then no need to start and parse again. ");
				return reportExec;
			}
			reportExec.setName(report.getName());
			reportExec.setAppInfo(report.getAppInfo());	
			
			commonServiceImpl.save(MetaType.reportExec.toString(), reportExec);
			
			reportExec.setRefKeyList(new ArrayList<>(usedRefKeySet));

			reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.PENDING);
		} catch (Exception e) {
			logger.error(e);
			reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.FAILED);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reportExec, reportUuid, reportVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable report.", dependsOn);
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
			report = (Report) commonServiceImpl.getLatestByUuid(reportExec.getDependsOn().getRef().getUuid(), MetaType.report.toString(), "N");
			synchronized (execUuid) {
				reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.STARTING);
			}
			reportExec.setExec(reportOperator.generateSql(report, refKeyMap, otherParams, usedRefKeySet, reportExec.getExecParams(), runMode));
			synchronized (execUuid) {
				reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.READY);
			}
			reportExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			logger.info("sql_generated: " + reportExec.getExec());
			commonServiceImpl.save(MetaType.reportExec.toString(), reportExec);
		} catch (Exception e) {
			logger.error(e);
			reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.FAILED);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable report.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Can not create executable report.");
		}
		return reportExec;
	}
	
	public ReportExec execute(String execUuid, String execVersion, ExecParams execParams, RunMode runMode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException, IOException {
		ReportExec reportExec =  null;
		try {			
			reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.reportExec.toString());
			MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
			Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString());

			String appUuid = commonServiceImpl.getApp().getUuid();
			
			RunReportServiceImpl runReportServiceImpl = new RunReportServiceImpl();
			runReportServiceImpl.setReportExec(reportExec);
			runReportServiceImpl.setReport(report);
			runReportServiceImpl.setCommonServiceImpl(commonServiceImpl);
			runReportServiceImpl.setReportServiceImpl(this);
			runReportServiceImpl.setSparkExecutor(sparkExecutor);
			runReportServiceImpl.setRunMode(runMode);
			runReportServiceImpl.setAppUuid(appUuid);
			runReportServiceImpl.setHdfsInfo(hdfsInfo);
			runReportServiceImpl.setSessionContext(sessionHelper.getSessionContext());
			runReportServiceImpl.setName(MetaType.reportExec+"_"+reportExec.getUuid()+"_"+reportExec.getVersion());
			runReportServiceImpl.call();
		} catch (Exception e) { 
			e.printStackTrace();
			// Set status to FAILED
			try {
				reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.FAILED);
			} catch (Exception e1) {
				e1.printStackTrace();
			}			
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Report execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Report execution FAILED.");
		} 	
		
		return reportExec;
	}
	
	protected String getFileName(Report report, ReportExec reportExec) {
		return String.format("/%s/%s/%s", report.getUuid(), report.getVersion(), reportExec.getVersion());
	}

	/********************** UNUSED **********************/
//	protected String getTableName(Report report, ReportExec reportExec, ExecContext execContext, Datasource reportDS) throws JsonProcessingException {
//		if (execContext == null || (execContext != null && (execContext.equals(ExecContext.spark) || execContext.equals(ExecContext.FILE) 
//				|| execContext.equals(ExecContext.livy_spark)))) {
//			return String.format("%s_%s_%s", report.getUuid().replace("-", "_"), report.getVersion(), reportExec.getVersion());
//		}
//		
//		MetaIdentifier dependsOn = report.getDependsOn().getRef();
//		if(dependsOn.getType().equals(MetaType.datapod)) {
//			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getUuid(), dependsOn.getVersion(), dependsOn.getType().toString());
//			return "temp"+datapod.getName().trim();
//		} else if(report.getDependsOn().getRef().getType().equals(MetaType.dataset)) {
//			DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getUuid(), dependsOn.getVersion(), dependsOn.getType().toString());
//			return "temp"+dataSet.getName().trim();
//		} else if(report.getDependsOn().getRef().getType().equals(MetaType.relation)) {
//			Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getUuid(), dependsOn.getVersion(), dependsOn.getType().toString());
//			return "temp"+relation.getName().trim();
//		}
//		return null;
//	}
	
	protected void persistDatastore(ReportExec reportExec, String tableName, String filePath, MetaIdentifierHolder resultRef, MetaIdentifier metaId, long countRows, RunMode runMode) throws Exception {
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, tableName, metaId, reportExec.getRef(MetaType.reportExec), reportExec.getAppInfo(), reportExec.getCreatedBy(), SaveMode.Append.toString(), resultRef, countRows, Helper.getPersistModeFromRunMode(runMode.toString()), null);
	}

	public List<Map<String, Object>> getReportSample(String reportExecUuid, String reportExecVersion, int rows,
			ExecParams execParams, RunMode runMode) throws Exception {
		ReportExec reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(reportExecUuid, reportExecVersion,
				MetaType.reportExec.toString());
		DataStore datastore = dataStoreServiceImpl.getDatastore(reportExec.getResult().getRef().getUuid(),
				reportExec.getResult().getRef().getVersion());
		
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.sample.maxrows"));
		if(rows > maxRows) {
			logger.error("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Number of rows "+rows+" exceeded. Max row allow "+maxRows, dependsOn);
			throw new RuntimeException("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
		}
		
		MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
		
		String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(), runMode);
		
		if(report.getSaveOnRefresh().equalsIgnoreCase("Y")) {
			String appUuid = commonServiceImpl.getApp().getUuid();
			List<String> filePathList = new ArrayList<>();
			filePathList.add(datastore.getLocation());
			sparkExecutor.readAndRegisterFile(tableName, filePathList, FileType.PARQUET.toString(), null, appUuid, true);
		}
		return dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, rows, null, null, null);
	}
	
	public boolean createPDF() {
		try {
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createXLS() {
		try {
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	
	public HttpServletResponse download(String reportExecUuid, String reportExecVersion, String format, int offset,
			int limit, HttpServletResponse response, String sortBy, String order, String requestId,
			RunMode runMode, boolean skipLimitCheck) throws Exception {

		ReportExec reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(reportExecUuid, reportExecVersion,
				MetaType.reportExec.toString(), "N");
		
		MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(),
				dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
		format = report.getFormat();
		
		String defaultDownloadPath = Helper.getPropertyValue("framework.file.download.path"); 
		defaultDownloadPath = defaultDownloadPath.endsWith("/") ? defaultDownloadPath : defaultDownloadPath.concat("/");
		String reportFilePath = String.format("%s/%s/%s/%s/", report.getUuid(), report.getVersion(), reportExec.getVersion(), "doc");
		
		File reportDocDir = new File(defaultDownloadPath.concat(reportFilePath));
		if(!reportDocDir.exists()) {
			reportDocDir.mkdirs();
		}
		String reportFileName = null;
		if(format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
			reportFileName = String.format("%s_%s.%s", report.getName(), reportExec.getVersion(), FileType.PDF.toString().toLowerCase());
		} else {
			reportFileName = String.format("%s_%s.%s", report.getName(), reportExec.getVersion(), FileType.XLS.toString().toLowerCase());
		}
		
		String filePathUrl = defaultDownloadPath.concat(reportFilePath).concat(reportFileName);		

		Workbook workbook = null;
		PDDocument doc = null;
		
		File reportFile = new File(filePathUrl);
		if(reportFile.exists()) {
			if(format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
				doc = PDDocument.load(reportFile);
			} else {
				 workbook = WorkbookFactory.create(reportFile);
			}
		} else {
			DataStore datastore = dataStoreServiceImpl.getDatastore(reportExec.getResult().getRef().getUuid(),
					reportExec.getResult().getRef().getVersion());
			if (datastore == null) {
				logger.error("Datastore is not available.");
				throw new Exception("Datastore is not available.");
			}
			
			int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
			if (!skipLimitCheck && limit > maxRows) {
				logger.error("Requested rows exceeded the limit of " + maxRows);
				MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
				dependsOn.setRef(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
				commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
						"Requested rows exceeded the limit of " + maxRows, dependsOn);
				throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
			}

			datastoreServiceImpl.setRunMode(runMode);
			List<Map<String, Object>> data = null;
			try {
				data = datastoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, limit, null, null, null);	
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			//checking whether data is available or not
			if(data == null || (data != null && data.isEmpty())) {
				data = new ArrayList<>();
				
				Map<String,Object> dataMap = new LinkedHashMap<>();
				int i = 0;
				for(AttributeSource attributeSource : report.getAttributeInfo()) {
					if(i == 0) {
						dataMap.put(attributeSource.getAttrSourceName(), "no data available.");
					} else {
						dataMap.put(attributeSource.getAttrSourceName(), "");
					}
					i++;
				}

				data.add(dataMap);
			}
			
//			//writting as per provided format
			if(format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
				doc = pdfUtil.getPDFDocForReport(data, reportExec);
//				FileOutputStream fileOutPDF = new FileOutputStream(new File(filePathUrl));
//				doc.save(fileOutPDF);
//				fileOutPDF.close();
			} else {
				workbook = workbookUtil.getWorkbookForReport(data, reportExec);
//				FileOutputStream fileOut = new FileOutputStream(filePathUrl);
//				workbook.write(fileOut);		
//				fileOut.close();
			}
			
			DownloadExec downloadExec = new DownloadExec();
			downloadExec.setBaseEntity();
			downloadExec.setLocation(filePathUrl);
			downloadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion())));
			
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.PENDING);
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.STARTING);
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.READY);
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.RUNNING);
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.COMPLETED);
			
			commonServiceImpl.save(MetaType.downloadExec.toString(), downloadExec);			
		}
		
		if(response != null) {
			try {
				ServletOutputStream servletOutputStream = response.getOutputStream();
				if(format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
					response.setContentType("application/pdf");
					response.setHeader("Content-disposition", "attachment");
					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
							.concat(".").concat(FileType.PDF.toString().toLowerCase()));
					doc.save(servletOutputStream);
					doc.close();
				} else {
					response.setContentType("application/xml");
					response.setHeader("Content-disposition", "attachment");
					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
							.concat(".").concat(FileType.XLS.toString().toLowerCase()));
					workbook.write(servletOutputStream);
					workbook.close();
				}				
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Can not download file.");
				response.setStatus(300);
	        	throw new FileNotFoundException("Can not download file.");
			}
		}
		
		return response;
	}
	
	public Report getReportByReportExec(String reportExecUuid) throws JsonProcessingException {
		Report report =null;
		ReportExec reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(reportExecUuid,null, MetaType.reportExec.toString(), "N");
		report=(Report) commonServiceImpl.getOneByUuidAndVersion(reportExec.getDependsOn().getRef().getUuid(),reportExec.getDependsOn().getRef().getVersion(), MetaType.report.toString(), "Y");
		return report;
	}

	/**
	 * @param execUuid
	 * @param execVersion
	 * @return ExecStatsHolder
	 * @throws JsonProcessingException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public ExecStatsHolder getNumRowsbyExec(String execUuid, String execVersion, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object exec = commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		MetaIdentifierHolder resultHolder = (MetaIdentifierHolder) exec.getClass().getMethod("getResult").invoke(exec);
		com.inferyx.framework.domain.DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion(), MetaType.datastore.toString(), "N");
		MetaIdentifier mi = new MetaIdentifier(MetaType.datastore, resultHolder.getRef().getUuid(), resultHolder.getRef().getVersion());
		ExecStatsHolder execHolder = new ExecStatsHolder();
		execHolder.setRef(mi);
		execHolder.setNumRows(dataStore.getNumRows());
		execHolder.setPersistMode(dataStore.getPersistMode());
		execHolder.setRunMode(dataStore.getRunMode());
		return execHolder;
	}

	/**
	 * @param execUuid
	 * @param execVersion
	 * @param type
	 * @return MetaIdentifier
	 * @throws JsonProcessingException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public MetaIdentifier getMetaIdByExecId(String execUuid, String execVersion, String type) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object exec = commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, type);
		MetaIdentifierHolder miHolder = (MetaIdentifierHolder) exec.getClass().getMethod("getDependsOn").invoke(exec);
		return miHolder.getRef();
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		execute(baseExec.getUuid(), baseExec.getVersion(), execParams, runMode);
		return null;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		synchronized (baseExec.getUuid()) {
			baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.reportExec, Status.Stage.STARTING);
		}
		synchronized (baseExec.getUuid()) {
			baseExec = (BaseExec) commonServiceImpl.setMetaStatus(baseExec, MetaType.reportExec, Status.Stage.READY);
		}
		return baseExec;
	}

	@Override
	public BaseRuleExec parse(String execUuid, String execVersion, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws Exception {
		BaseRuleExec baseRuleExec = (BaseRuleExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion, MetaType.reportExec.toString(), "N");
		synchronized (execUuid) {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, MetaType.reportExec, Status.Stage.STARTING);
		}
		synchronized (execUuid) {
			baseRuleExec = (BaseRuleExec) commonServiceImpl.setMetaStatus(baseRuleExec, MetaType.reportExec, Status.Stage.READY);
		}
		return baseRuleExec;
	}

	@Override
	public BaseRuleExec execute(ThreadPoolTaskExecutor metaExecutor, BaseRuleExec baseRuleExec,
			MetaIdentifier datapodKey, List<FutureTask<TaskHolder>> taskList, ExecParams execParams, RunMode runMode)
			throws Exception {
		return execute(baseRuleExec.getUuid(), baseRuleExec.getVersion(), execParams, runMode);
	}
	
	public boolean sendSuccessNotification(SenderInfo senderInfo, Report report,
			ReportExec reportExec, RunMode runMode) throws Exception {
		logger.info("sending success notification...");
		Notification notification = new Notification();

		String subject = Helper.getPropertyValue("framework.email.subject");
		subject = MessageFormat.format(subject, "SUCCESS", "Report", report.getName(), "COMPLETED");
		notification.setSubject(subject);

		String roleUuid = sessionHelper.getSessionContext().getRoleInfo().getRef().getUuid();
		String appUuid = sessionHelper.getSessionContext().getAppInfo().getRef().getUuid();

		String contextPath = Helper.getPropertyValue("framework.webserver.contextpath");
		if(contextPath.startsWith("")) {
			contextPath = "";
		} else {
			contextPath = contextPath.startsWith("/") ? contextPath : "/".concat(contextPath);
			contextPath = contextPath.endsWith("/") ? contextPath.substring(contextPath.lastIndexOf("/")) : contextPath;	
		}
		
		String resultUrl = Helper.getPropertyValue("framework.url.report.result.success");
		resultUrl = MessageFormat.format(resultUrl, Helper.getPropertyValue("framework.webserver.host"),
				Helper.getPropertyValue("framework.webserver.port"), contextPath, reportExec.getUuid(), reportExec.getVersion(), roleUuid, appUuid);

		String message = Helper.getPropertyValue("framework.email.body");
		message = MessageFormat.format(message, resultUrl);
		notification.setMessage(message);

		if (senderInfo.getSendAttachment().equalsIgnoreCase("Y")) {			
			try {
				String format = report.getFormat();
				boolean isDocCreated = true;
				List<Map<String, Object>> data = prepareDocumentData(reportExec, report, runMode, report.getLimit(), true);
				if(format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
					String layout = report.getLayout() != null ? report.getLayout().toString() : null;					
					isDocCreated = documentGenServiceImpl.createPDF(MetaType.report.toString(), report, reportExec, data, layout);
				} else {
					isDocCreated = documentGenServiceImpl.createXLS(MetaType.report.toString(), report, reportExec, data);
				}
				
				if(!isDocCreated) {
					throw new RuntimeException((format != null ? format.toUpperCase() : "Document")+" creation failed...");
				}
				
				String defaultDownloadPath = Helper.getPropertyValue("framework.report.Path"); 
				defaultDownloadPath = defaultDownloadPath.endsWith("/") ? defaultDownloadPath : defaultDownloadPath.concat("/");
				String reportFilePath = String.format("%s/%s/%s/%s/", report.getUuid(), report.getVersion(), reportExec.getVersion(), "doc");
				
				String reportFileName = null;
				if(format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
					reportFileName = String.format("%s_%s.%s", report.getName(), reportExec.getVersion(), FileType.PDF.toString().toLowerCase());
				} else {
					reportFileName = String.format("%s_%s.%s", report.getName(), reportExec.getVersion(), FileType.XLS.toString().toLowerCase());
				}
				
				String filePathUrl = defaultDownloadPath.concat(reportFilePath).concat(reportFileName);		

				Map<String, String> emailAttachment = new HashMap<>();
				if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
					emailAttachment.put(report.getName().concat("_").concat(reportExec.getVersion()).concat(".")
							.concat(FileType.PDF.toString().toLowerCase()), filePathUrl);
				} else {
					emailAttachment.put(report.getName().concat("_").concat(reportExec.getVersion()).concat(".")
							.concat(FileType.XLS.toString().toLowerCase()), filePathUrl);
				}
				senderInfo.setEmailAttachment(emailAttachment);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		notification.setSenderInfo(senderInfo);
		return notificationServiceImpl.prepareAndSendNotification(notification);
	}
	
	public boolean sendFailureNotification(SenderInfo senderInfo, Report report, ReportExec reportExec)
			throws FileNotFoundException, IOException {
		logger.info("sending fail notification...");
		Notification notification = new Notification();

		String subject = Helper.getPropertyValue("framework.email.subject");
		subject = MessageFormat.format(subject, "FAILURE", "Report", report.getName(), "FAILED");
		notification.setSubject(subject);

		String roleUuid = sessionHelper.getSessionContext().getRoleInfo().getRef().getUuid();
		String appUuid = sessionHelper.getSessionContext().getAppInfo().getRef().getUuid();

		String contextPath = Helper.getPropertyValue("framework.webserver.contextpath");
		if(contextPath.startsWith("")) {
			contextPath = "";
		} else {
			contextPath = contextPath.startsWith("/") ? contextPath : "/".concat(contextPath);
			contextPath = contextPath.endsWith("/") ? contextPath.substring(contextPath.lastIndexOf("/")) : contextPath;	
		}
		
		String resultUrl = Helper.getPropertyValue("framework.url.report.result.failure");
		resultUrl = MessageFormat.format(resultUrl, Helper.getPropertyValue("framework.webserver.host"),
				Helper.getPropertyValue("framework.webserver.port"), contextPath, roleUuid, appUuid);

		String message = Helper.getPropertyValue("framework.email.body");
		message = MessageFormat.format(message, resultUrl);
		notification.setMessage(message);

		notification.setSenderInfo(senderInfo);
		return notificationServiceImpl.prepareAndSendNotification(notification);
	}

	/**
	 * @param reportExecUuid
	 * @param reportExecVersion
	 * @param senderInfos
	 * @param runMode
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws JSONException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public boolean sendNotification(String reportExecUuid, String reportExecVersion, SenderInfo senderInfo,
			RunMode runMode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException, IOException {
		ReportExec reportExec = null;
		try {
			logger.info("sending notification...");
			reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(reportExecUuid, reportExecVersion,
					MetaType.reportExec.toString(), "N");

			MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
			Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(),
					dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
			Status status = Helper.getLatestStatus(reportExec.getStatusList());
			if(status.getStage().equals(Status.Stage.COMPLETED)) {								
				return sendSuccessNotification(senderInfo, report, reportExec, runMode);		
			} else if(status.getStage().equals(Status.Stage.FAILED)) {
				return sendFailureNotification(senderInfo, report, reportExec);
			} else {
				throw new RuntimeException("Report execution status is not "+Status.Stage.COMPLETED+", latest status is "+status.getStage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(
					new MetaIdentifier(MetaType.reportExec, reportExecUuid, reportExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), e.getMessage(), dependsOn);
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<ReportExecView> getReportExecViewByCriteria(String role, String appUuid, String type, String name,
			String userName, String startDate, String endDate, String tags, String active, String status) {
		List<ReportExecView> listReportExecView = new ArrayList<>();

		Report report = null;
		ReportExec reportExec = null;
		List<BaseEntityStatus> baseEntityStatusList;
		try {
			baseEntityStatusList = metadataServiceImpl.getBaseEntityStatusByCriteria(role, appUuid, type, name,
					userName, startDate, endDate, tags, active, status);
			for (BaseEntityStatus baseEntityStatus : baseEntityStatusList) {
				reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(baseEntityStatus.getUuid(),
						baseEntityStatus.getVersion(), MetaType.reportExec.toString(), "N");

				MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
				report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(),
						dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
				ReportExecView reportExecView = new ReportExecView();
				reportExecView.setStatusList(baseEntityStatus.getStatus());
				reportExecView.setNumRows(baseEntityStatus.getNumRows());
				reportExecView.setSizeMB(baseEntityStatus.getSizeMB());
				reportExecView.setSenderInfo(report.getSenderInfo());
				reportExecView.setSaveOnRefresh(report.getSaveOnRefresh());
				reportExecView.setFormat(report.getFormat());
				reportExecView.setName(baseEntityStatus.getName());
				reportExecView.setActive(baseEntityStatus.getActive());
				reportExecView.setUuid(baseEntityStatus.getUuid());
				reportExecView.setVersion(baseEntityStatus.getVersion());
				reportExecView.setCreatedOn(baseEntityStatus.getCreatedOn());
				reportExecView.setCreatedBy(baseEntityStatus.getCreatedBy());
				listReportExecView.add(reportExecView);
			}

		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listReportExecView;
	}
	
	public HttpServletResponse downloadReport(String reportExecUuid, String reportExecVersion, String format,
			int offset, int limit, HttpServletResponse response, String sortBy, String order, String requestId,
			RunMode runMode, boolean skipLimitCheck) throws Exception {

		ReportExec reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(reportExecUuid, reportExecVersion,
				MetaType.reportExec.toString(), "N");

		MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(),
				dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
		format = report.getFormat();

		String defaultDownloadPath = Helper.getPropertyValue("framework.report.Path");
		defaultDownloadPath = defaultDownloadPath.endsWith("/") ? defaultDownloadPath : defaultDownloadPath.concat("/");
		String reportFilePath = String.format("%s/%s/%s/%s/", report.getUuid(), report.getVersion(),
				reportExec.getVersion(), "doc");

		File reportDocDir = new File(defaultDownloadPath.concat(reportFilePath));
		if (!reportDocDir.exists()) {
			reportDocDir.mkdirs();
		}
		String reportFileName = null;
		if(format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
			reportFileName = String.format("%s_%s.%s", report.getName(), reportExec.getVersion(),
					FileType.PDF.toString().toLowerCase());
		} else {
			reportFileName = String.format("%s_%s.%s", report.getName(), reportExec.getVersion(),
					FileType.XLS.toString().toLowerCase());
		}

		String filePathUrl = defaultDownloadPath.concat(reportFilePath).concat(reportFileName);

		Workbook workbook = null;
		PDDocument doc = null;

		File reportFile = new File(filePathUrl);
		if (reportFile.exists()) {
			if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
				doc = PDDocument.load(reportFile);
			} else {
				workbook = WorkbookFactory.create(reportFile);
			}
		} else {
//			DataStore datastore = dataStoreServiceImpl.getDatastore(reportExec.getResult().getRef().getUuid(),
//					reportExec.getResult().getRef().getVersion());
//			if (datastore == null) {
//				logger.error("Datastore is not available.");
//				throw new Exception("Datastore is not available.");
//			}
//			
//			int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
//			if (!skipLimitCheck && limit > maxRows) {
//				logger.error("Requested rows exceeded the limit of " + maxRows);
//				MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
//				dependsOn.setRef(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
//				commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
//						"Requested rows exceeded the limit of " + maxRows, dependsOn);
//				throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
//			}
//
//			datastoreServiceImpl.setRunMode(runMode);
//			List<Map<String, Object>> data = null;
//			try {
//				data = datastoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, limit, null, null, null);	
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			//checking whether data is available or not
//			if(data == null || (data != null && data.isEmpty())) {
//				data = new ArrayList<>();
//				
//				Map<String,Object> dataMap = new LinkedHashMap<>();
//				int i = 0;
//				for(AttributeSource attributeSource : report.getAttributeInfo()) {
//					if(i == 0) {
//						dataMap.put(attributeSource.getAttrSourceName(), "no data available.");
//					} else {
//						dataMap.put(attributeSource.getAttrSourceName(), "");
//					}
//					i++;
//				}
//
//				data.add(dataMap);
//			}
			
			List<Map<String, Object>> data = prepareDocumentData(reportExec, report, runMode, limit, false);
//			//writting as per provided format
//			if(format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
//				doc = pdfUtil.getPDFDocForReport(data, reportExec);
//			} else {j
//				workbook = workbookUtil.getWorkbookForReport(data, reportExec);
//			}
			
			boolean isDocCreated = true;
			if(format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
				String layout = report.getLayout() != null ? report.getLayout().toString() : null;					
				isDocCreated = documentGenServiceImpl.createPDF(MetaType.report.toString(), report, reportExec, data, layout);
				if(isDocCreated) {
					doc = PDDocument.load(reportFile);
				}
			} else {
				isDocCreated = documentGenServiceImpl.createXLS(MetaType.report.toString(), report, reportExec, data);
				if(isDocCreated) {
					workbook = 	WorkbookFactory.create(reportFile);
				}
			}
			
			if(!isDocCreated) {
				throw new RuntimeException((format != null ? format.toUpperCase() : "Document")+" creation failed...");
			}
			
			DownloadExec downloadExec = new DownloadExec();
			downloadExec.setBaseEntity();
			downloadExec.setLocation(filePathUrl);
			downloadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion())));
			
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.PENDING);
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.STARTING);
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.READY);
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.RUNNING);
			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.COMPLETED);
			
			
			commonServiceImpl.save(MetaType.downloadExec.toString(), downloadExec);			
		}

		if (response != null) {
			try {
				ServletOutputStream servletOutputStream = response.getOutputStream();
				if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
					response.setContentType("application/pdf");
					response.setHeader("Content-disposition", "attachment");
					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
							.concat(".").concat(FileType.PDF.toString().toLowerCase()));
					doc.save(servletOutputStream);
					doc.close();
				} else {
					response.setContentType("application/xml");
					response.setHeader("Content-disposition", "attachment");
					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
							.concat(".").concat(FileType.XLS.toString().toLowerCase()));
					workbook.write(servletOutputStream);
					workbook.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Can not download file.");
				response.setStatus(300);
				throw new FileNotFoundException("Can not download file.");
			}
		}

		return response;
	}

	public HttpServletResponse downloadSample(String reportExecUuid, String reportExecVersion, String format,
			int offset, int limit, HttpServletResponse response, String sortBy, String order, String requestId,
			RunMode runMode, boolean skipLimitCheck) throws Exception {
		ReportExec reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(reportExecUuid, reportExecVersion,
				MetaType.reportExec.toString(), "N");

		MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(),
				dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");

		String defaultDownloadPath = Helper.getPropertyValue("framework.file.download.path");
		defaultDownloadPath = defaultDownloadPath.endsWith("/") ? defaultDownloadPath : defaultDownloadPath.concat("/");
		String reportFilePath = String.format("%s/%s/%s/%s/", report.getUuid(), report.getVersion(),
				reportExec.getVersion(), "doc");

		File reportDocDir = new File(defaultDownloadPath.concat(reportFilePath));
		if (!reportDocDir.exists()) {
			reportDocDir.mkdirs();
		}
		String reportFileName = null;
		if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
			reportFileName = String.format("%s_%s.%s", report.getName(), reportExec.getVersion(),
					FileType.PDF.toString().toLowerCase());
		} else {
			reportFileName = String.format("%s_%s.%s", report.getName(), reportExec.getVersion(),
					FileType.XLS.toString().toLowerCase());
		}

		String filePathUrl = defaultDownloadPath.concat(reportFilePath).concat(reportFileName);

		Workbook workbook = null;
		PDDocument doc = null;

		File reportFile = new File(filePathUrl);
		if (reportFile.exists()) {
			if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
				doc = PDDocument.load(reportFile);
			} else {
				workbook = WorkbookFactory.create(reportFile);
			}
		}

		DataStore datastore = dataStoreServiceImpl.getDatastore(reportExec.getResult().getRef().getUuid(),
				reportExec.getResult().getRef().getVersion());
		if (datastore == null) {
			logger.error("Datastore is not available.");
			throw new Exception("Datastore is not available.");
		}

		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if (!skipLimitCheck && limit > maxRows) {
			logger.error("Requested rows exceeded the limit of " + maxRows);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					"Requested rows exceeded the limit of " + maxRows, dependsOn);
			throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
		}

		datastoreServiceImpl.setRunMode(runMode);
		List<Map<String, Object>> data = null;
		try {
			data = datastoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0,
					limit, null, null, null);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// checking whether data is available or not
		if (data == null || (data != null && data.isEmpty())) {
			data = new ArrayList<>();

			Map<String, Object> dataMap = new LinkedHashMap<>();
			int i = 0;
			for (AttributeSource attributeSource : report.getAttributeInfo()) {
				if (i == 0) {
					dataMap.put(attributeSource.getAttrSourceName(), "no data available.");
				} else {
					dataMap.put(attributeSource.getAttrSourceName(), "");
				}
				i++;
			}

			data.add(dataMap);
		}

		// writting as per provided format
		if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
			doc = pdfUtil.getPDFDocForReport(data, reportExec);
		} else {
			workbook = workbookUtil.getWorkbookForReport(data, reportExec);
		}

		DownloadExec downloadExec = new DownloadExec();
		downloadExec.setBaseEntity();
		downloadExec.setLocation(filePathUrl);
		downloadExec.setDependsOn(datastore.getExecId());
		
		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.PENDING);
		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.STARTING);
		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.READY);
		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.RUNNING);
		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.COMPLETED);
		try {
			commonServiceImpl.save(MetaType.downloadExec.toString(), downloadExec);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (response != null) {
			try {
				ServletOutputStream servletOutputStream = response.getOutputStream();
				if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
					response.setContentType("application/pdf");
					response.setHeader("Content-disposition", "attachment");
					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
							.concat(".").concat(FileType.PDF.toString().toLowerCase()));
					doc.save(servletOutputStream);
					doc.close();
				} else {
					response.setContentType("application/xml");
					response.setHeader("Content-disposition", "attachment");
					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
							.concat(".").concat(FileType.XLS.toString().toLowerCase()));
					workbook.write(servletOutputStream);
					workbook.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Can not download file.");
				response.setStatus(300);
				throw new FileNotFoundException("Can not download file.");
			}
		}

		return response;
	}
	
	public List<Map<String, Object>> prepareDocumentData(ReportExec reportExec, Report report, RunMode runMode, int limit, boolean skipLimitCheck) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException, IOException{		
		DataStore datastore = dataStoreServiceImpl.getDatastore(reportExec.getResult().getRef().getUuid(),
				reportExec.getResult().getRef().getVersion());
		if (datastore == null) {
			logger.error("Datastore is not available.");
			throw new RuntimeException("Datastore is not available.");
		}
		
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if (!skipLimitCheck && limit > maxRows) {
			logger.error("Requested rows exceeded the limit of " + maxRows);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					"Requested rows exceeded the limit of " + maxRows, dependsOn);
			throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
		}

		datastoreServiceImpl.setRunMode(runMode);
		List<Map<String, Object>> data = null;
		try {
			data = datastoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, limit, null, null, null);	
		}catch (Exception e) {
			e.printStackTrace();
			try {
				data = getReportSample(reportExec.getUuid(), reportExec.getVersion(), limit, null, runMode);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e); 
			}
		}
		
		//checking whether data is available or not
		if(data == null || (data != null && data.isEmpty())) {
			data = new ArrayList<>();
			
			Map<String,Object> dataMap = new LinkedHashMap<>();
			int i = 0;
			for(AttributeSource attributeSource : report.getAttributeInfo()) {
				if(i == 0) {
					dataMap.put(attributeSource.getAttrSourceName(), "no data available.");
				} else {
					dataMap.put(attributeSource.getAttrSourceName(), "");
				}
				i++;
			}

			data.add(dataMap);
		}
		
		return data;
	}
}
