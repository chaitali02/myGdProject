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

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntityStatus;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.BaseRuleExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Document;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.ExecStatsHolder;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Notification;
import com.inferyx.framework.domain.Organization;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.domain.ReportExecView;
import com.inferyx.framework.domain.SenderInfo;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.Alignment;
import com.inferyx.framework.enums.Layout;
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
	private SparkExecutor<?> sparkExecutor;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private NotificationServiceImpl notificationServiceImpl;
	@Autowired
	private DownloadServiceImpl downloadServiceImpl;
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
				reportExec.setRunMode(runMode);
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
			reportExec.setExec(reportOperator.generateSql(report, refKeyMap, otherParams, usedRefKeySet, reportExec.getExecParams(), runMode, new HashMap<String, String>()));
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
	
	/*****************************Uused**************************************/
	/*protected String getFileName(Report report, ReportExec reportExec) {
		return String.format("/%s/%s/%s", report.getUuid(), report.getVersion(), reportExec.getVersion());
	}*/

	
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
		
		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.sample.maxrows"));
		if(rows > maxRows) {
			logger.error("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Number of rows "+rows+" exceeded. Max row allow "+maxRows, dependsOn);
			throw new RuntimeException("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
		}
		
		MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");
		
		String tableName = dataStoreServiceImpl.getTableNameByDatastoreKey(datastore.getUuid(), datastore.getVersion(), runMode);
//		String tableName = dataStoreServiceImpl.getTableNameByDatastore(datastore.getUuid(), datastore.getVersion(), runMode);
		if(report.getSaveOnRefresh().equalsIgnoreCase("Y")) {
			String appUuid = commonServiceImpl.getApp().getUuid();
			List<String> filePathList = new ArrayList<>();
			filePathList.add(datastore.getLocation());
			sparkExecutor.readAndRegisterFile(tableName, filePathList, FileType.PARQUET.toString(), null, appUuid, true);
		}
		return dataStoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, rows, null, null, null,runMode);
	}
	

	/******************************Unused**************************/
	/*public boolean createPDF() {
		try {
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}*/
	
	
	/**********************Unused***************************/
	/*public boolean createXLS() {
		try {
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}*/
	
	public HttpServletResponse download(String reportExecUuid, String reportExecVersion, String format, int offset,
			int limit, HttpServletResponse response, String sortBy, String order, String requestId,
			RunMode runMode, boolean skipLimitCheck, Layout layout) throws Exception {

		if(StringUtils.isBlank(format)) {
			throw new RuntimeException("Format not provided ...");
		}
		
		ReportExec reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(reportExecUuid, reportExecVersion,
				MetaType.reportExec.toString(), "N");

		MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(),
				dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");


		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.download.maxrows"));
		if(limit < 1) {
			limit = maxRows;
		}
		if(limit > maxRows) {
			logger.error("Requested rows exceeded the limit of "+maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
		}
		
		
		List<Map<String, Object>> data = prepareDocumentData(reportExec, report, runMode, limit, false);
		
		MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(
				new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
		
		Map<String, String> otherDetails = getOtherDetailsByReport(report);
		
		response = downloadServiceImpl.download(format, response, runMode, data, dependsOn, layout,
				reportExec, false, "framework.report.Path", getReportParametrsForDoc(report, reportExec), reportExec.getDependsOn(), otherDetails);
		
//		format = Helper.mapFileFormat(report.getFormat());
//		
//		String filePathUrl = Helper.getDocumentFilePath(commonServiceImpl.getConfigValue("framework.report.Path"), report.getUuid(), report.getVersion(), reportExec.getVersion(), report.getName(), format, true);
//		String attachmentName = Helper.getDocumentFileName(report.getName(), reportExec.getVersion(), format);
//		
//		String reportDirPath = filePathUrl.replaceAll(attachmentName, "");
//		
//		File reportDocDir = new File(reportDirPath);
//		if (!reportDocDir.exists()) {
//			reportDocDir.mkdirs();
//		}
//
//		Workbook workbook = null;
//		PDDocument doc = null;
//
//		File reportFile = new File(filePathUrl);
//		if (reportFile.exists()) {
//			if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
//				doc = PDDocument.load(reportFile);
//			} else {
//				workbook = WorkbookFactory.create(reportFile);
//			}
//		} else {			
//			
//			Document document = new Document();
//			document.setLocation(filePathUrl);
//			document.setHeader(report.getHeader());
//			document.setHeaderAlignment(report.getHeaderAlign());
//			document.setFooter(report.getFooter());
//			document.setFooterAlignment(report.getFooterAlign());
//			document.setTitle(report.getTitle());
//			document.setLayout(report.getLayout());
//			document.setData(data);
//			document.setMetaObjType(MetaType.report.toString());
//			document.setMetExecObject(reportExec);
//			document.setDocumentType(format);
//			document.setName(report.getName());
//			document.setDescription(!StringUtils.isBlank(report.getDesc()) ? report.getDesc() : "");
//			document.setParameters(getReportParametrsForDoc(report, reportExec));
//			document.setGenerationDate(report.getCreatedOn());
//			
//			boolean isDocCreated = documentGenServiceImpl.createDocument(document);
//			
//			if(!isDocCreated) {
//				throw new RuntimeException((format != null ? format.toUpperCase() : "Document")+" creation failed...");
//			}
//			
//			if(format.equalsIgnoreCase(FileType.PDF.toString())) {
//				doc = PDDocument.load(reportFile);
//			} else if(format.equalsIgnoreCase(FileType.XLS.toString())) {
//				workbook = 	WorkbookFactory.create(reportFile);
//			}
//			
//			DownloadExec downloadExec = new DownloadExec();
//			downloadExec.setBaseEntity();
//			downloadExec.setLocation(filePathUrl);
//			downloadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion())));
//			
//			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.PENDING);
//			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.STARTING);
//			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.READY);
//			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.RUNNING);
//			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.COMPLETED);
//			
//			commonServiceImpl.save(MetaType.downloadExec.toString(), downloadExec);			
//		}
//
//		if (response != null) {
//			try {
//				ServletOutputStream servletOutputStream = response.getOutputStream();
//				if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
//					response.setContentType("application/pdf");
//					response.setHeader("Content-disposition", "attachment");
//					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
//							.concat(".").concat(FileType.PDF.toString().toLowerCase()));
//					doc.save(servletOutputStream);
//					doc.close();
//				} else if (format.equalsIgnoreCase(FileType.XLS.toString())) {
//					response.setContentType("application/xml");
//					response.setHeader("Content-disposition", "attachment");
//					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
//							.concat(".").concat(FileType.XLS.toString().toLowerCase()));
//					workbook.write(servletOutputStream);
//					workbook.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				logger.error("Can not download file.");
//				response.setStatus(300);
//				throw new FileNotFoundException("Can not download file.");
//			}
//		}

		return response;
	}
	
	/**
	 * @param report
	 * @return
	 */
	private Map<String, String> getOtherDetailsByReport(Report report) {
		Map<String, String> otherDetails = new HashMap<>();
		
		otherDetails.put("doc_title", report.getTitle());
		otherDetails.put("doc_header", report.getHeader());
		otherDetails.put("doc_header_align", report.getHeaderAlign());
		otherDetails.put("doc_footer", report.getFooter());
		otherDetails.put("doc_footer_align", report.getFooterAlign());
		
		return otherDetails;
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

		String subject = commonServiceImpl.getConfigValue("framework.email.subject");
		subject = MessageFormat.format(subject, "SUCCESS", "Report", report.getName(), "COMPLETED");
		notification.setSubject(subject);

		String roleUuid = sessionHelper.getSessionContext().getRoleInfo().getRef().getUuid();
		String appUuid = sessionHelper.getSessionContext().getAppInfo().getRef().getUuid();

		String contextPath = commonServiceImpl.getConfigValue("framework.webserver.contextpath");
		if(contextPath.startsWith("")) {
			contextPath = "";
		} else {
			contextPath = contextPath.startsWith("/") ? contextPath : "/".concat(contextPath);
			contextPath = contextPath.endsWith("/") ? contextPath.substring(contextPath.lastIndexOf("/")) : contextPath;	
		}
		
		String resultUrl = commonServiceImpl.getConfigValue("framework.url.report.result.success");
		resultUrl = MessageFormat.format(resultUrl, commonServiceImpl.getConfigValue("framework.webserver.host"),
				commonServiceImpl.getConfigValue("framework.webserver.port"), contextPath, reportExec.getUuid(), reportExec.getVersion(), roleUuid, appUuid);

		String message = commonServiceImpl.getConfigValue("framework.email.body");
		message = MessageFormat.format(message, resultUrl);
		notification.setMessage(message);

		if (senderInfo.getSendAttachment().equalsIgnoreCase("Y")) {			
			try {
				int rowLimit = report.getLimit();
				int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.download.maxrows"));
				if(rowLimit < 1) {
					rowLimit = maxRows;
				}
				if(rowLimit > maxRows) {
					logger.error("Requested rows exceeded the limit of "+maxRows);
					commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows, null);
					throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
				}
				
				List<Map<String, Object>> data = prepareDocumentData(reportExec, report, runMode, rowLimit, true);
				
				String format = Helper.mapFileFormat(report.getFormat());

				if(StringUtils.isBlank(format)) {
					throw new RuntimeException("Format not provided ...");
				}
				
				String filePathUrl = Helper.getDocumentFilePath(commonServiceImpl.getConfigValue("framework.report.Path"), report.getUuid(), report.getVersion(), reportExec.getVersion(), report.getName(), format, true);
				String attachmentName = Helper.getDocumentFileName(report.getName(), reportExec.getVersion(), format);
				
				Map<String, String> emailAttachment = new HashMap<>();
				emailAttachment.put(attachmentName, filePathUrl);
				senderInfo.setEmailAttachment(emailAttachment);
				
				File reportFile = new File(filePathUrl);
				if (!reportFile.exists()) {
					String reportDirPath = filePathUrl.replaceAll(attachmentName, "");
					
					File reportDocDir = new File(reportDirPath);
					if (!reportDocDir.exists()) {
						reportDocDir.mkdirs();
					}
					
					Document document = new Document();
					document.setLocation(filePathUrl);
					document.setHeader(report.getHeader());
					document.setHeaderAlignment(report.getHeaderAlign());
					document.setFooter(report.getFooter());
					document.setFooterAlignment(report.getFooterAlign());
					document.setTitle(report.getTitle());
					document.setLayout(report.getLayout());
					document.setData(data);
					document.setMetaObjType(MetaType.report.toString());
					document.setMetExecObject(reportExec);
					document.setDocumentType(format);
					document.setName(report.getName());
					document.setDescription(!StringUtils.isBlank(report.getDesc()) ? report.getDesc() : "");
					document.setParameters(getReportParametrsForDoc(report, reportExec));
					document.setGenerationDate(report.getCreatedOn());
					document.setFileName(attachmentName);
					document.setLogoAlignment(Alignment.RIGHT);
					document.setTitleAlignment(Alignment.CENTER);
					
					Organization organization = commonServiceImpl.getOrgInfoByCurrentApp();
					document.setOrgName(organization.getName());
					document.setOrgLogoName(organization.getLogoName());
					document.setOrgAddress(commonServiceImpl.getOrganizationAddr(organization.getAddress()));
										
					boolean isDocCreated = documentGenServiceImpl.createDocument(document);				
					
					if(!isDocCreated) {
						throw new RuntimeException((format != null ? format.toUpperCase() : "Document")+" creation failed...");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				senderInfo.setEmailAttachment(null);
				return false;
			}
		}
		notification.setSenderInfo(senderInfo);
		return notificationServiceImpl.prepareAndSendNotification(notification);
	}
	
	public String getReportParametrsForDoc(Report report, ReportExec reportExec) {
		StringBuilder reportParameters = new StringBuilder("");
		if (report.getParamList() != null && reportExec.getExecParams() != null
				&& reportExec.getExecParams().getParamListInfo() != null
				&& !reportExec.getExecParams().getParamListInfo().isEmpty()) {

			for (ParamListHolder paramListHolder : reportExec.getExecParams().getParamListInfo()) {
				String paramName = paramListHolder.getParamName();
				String paramValue =null;
				if(paramListHolder.getParamValue() !=null) {
					paramValue=paramListHolder.getParamValue().getValue();
				}
				reportParameters.append(paramName).append(": ").append(paramValue).append(", ");
			}

			if (reportParameters.length() > 0) {
				reportParameters = new StringBuilder(
						reportParameters.substring(0, reportParameters.toString().lastIndexOf(",")));
			}

		}
		
		return reportParameters.toString();
	}
	
	public boolean sendFailureNotification(SenderInfo senderInfo, Report report, ReportExec reportExec)
			throws FileNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		logger.info("sending fail notification...");
		Notification notification = new Notification();

		String subject = commonServiceImpl.getConfigValue("framework.email.subject");
		subject = MessageFormat.format(subject, "FAILURE", "Report", report.getName(), "FAILED");
		notification.setSubject(subject);

		String roleUuid = sessionHelper.getSessionContext().getRoleInfo().getRef().getUuid();
		String appUuid = sessionHelper.getSessionContext().getAppInfo().getRef().getUuid();

		String contextPath = commonServiceImpl.getConfigValue("framework.webserver.contextpath");
		if(contextPath.startsWith("")) {
			contextPath = "";
		} else {
			contextPath = contextPath.startsWith("/") ? contextPath : "/".concat(contextPath);
			contextPath = contextPath.endsWith("/") ? contextPath.substring(contextPath.lastIndexOf("/")) : contextPath;	
		}
		
		String resultUrl = commonServiceImpl.getConfigValue("framework.url.report.result.failure");
		resultUrl = MessageFormat.format(resultUrl, commonServiceImpl.getConfigValue("framework.webserver.host"),
				commonServiceImpl.getConfigValue("framework.webserver.port"), contextPath, roleUuid, appUuid);

		String message = commonServiceImpl.getConfigValue("framework.email.body");
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
	
	public HttpServletResponse downloadReport(String reportExecUuid, String reportExecVersion,
			int offset, HttpServletResponse response, String sortBy, String order, String requestId,
			RunMode runMode, boolean skipLimitCheck) throws Exception {

		ReportExec reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(reportExecUuid, reportExecVersion,
				MetaType.reportExec.toString(), "N");

		MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(),
				dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");

		String format = report.getFormat();	
		if(StringUtils.isBlank(format)) {
			throw new RuntimeException("Format not provided ...");
		}
		
		int rowLimit = report.getLimit();
		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.download.maxrows"));
		if(rowLimit < 1) {
			rowLimit = maxRows;
		}
		if(rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of "+maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
		}
		
		List<Map<String, Object>> data = prepareDocumentData(reportExec, report, runMode, rowLimit, false);
		
		MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(
				new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
		
		Map<String, String> otherDetails = getOtherDetailsByReport(report);
		
		response = downloadServiceImpl.download(format, response, runMode, data, dependsOn, report.getLayout(),
				reportExec, true, "framework.report.Path", getReportParametrsForDoc(report, reportExec), reportExec.getDependsOn(), otherDetails);
		
//		format = Helper.mapFileFormat(report.getFormat());
//		
//		String filePathUrl = Helper.getDocumentFilePath(commonServiceImpl.getConfigValue("framework.report.Path"), report.getUuid(), report.getVersion(), reportExec.getVersion(), report.getName(), format, true);
//		String attachmentName = Helper.getDocumentFileName(report.getName(), reportExec.getVersion(), format);
//		
//		String reportDirPath = filePathUrl.replaceAll(attachmentName, "");
//		
//		File reportDocDir = new File(reportDirPath);
//		if (!reportDocDir.exists()) {
//			reportDocDir.mkdirs();
//		}
//
//		Workbook workbook = null;
//		PDDocument doc = null;
//
//		File reportFile = new File(filePathUrl);
//		if (reportFile.exists()) {
//			if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
//				doc = PDDocument.load(reportFile);
//			} else {
//				workbook = WorkbookFactory.create(reportFile);
//			}
//		} else {			
//			
//			Document document = new Document();
//			document.setLocation(filePathUrl);
//			document.setHeader(report.getHeader());
//			document.setHeaderAlignment(report.getHeaderAlign());
//			document.setFooter(report.getFooter());
//			document.setFooterAlignment(report.getFooterAlign());
//			document.setTitle(report.getTitle());
//			document.setLayout(report.getLayout());
//			document.setData(data);
//			document.setMetaObjType(MetaType.report.toString());
//			document.setMetExecObject(reportExec);
//			document.setDocumentType(format);
//			document.setName(report.getName());
//			document.setDescription(!StringUtils.isBlank(report.getDesc()) ? report.getDesc() : "");
//			document.setParameters(getReportParametrsForDoc(report, reportExec));
//			document.setGenerationDate(report.getCreatedOn());
//			
//			boolean isDocCreated = documentGenServiceImpl.createDocument(document);
//			
//			if(!isDocCreated) {
//				throw new RuntimeException((format != null ? format.toUpperCase() : "Document")+" creation failed...");
//			}
//			
//			if(format.equalsIgnoreCase(FileType.PDF.toString())) {
//				doc = PDDocument.load(reportFile);
//			} else if(format.equalsIgnoreCase(FileType.XLS.toString())) {
//				workbook = 	WorkbookFactory.create(reportFile);
//			}
//			
//			DownloadExec downloadExec = new DownloadExec();
//			downloadExec.setBaseEntity();
//			downloadExec.setLocation(filePathUrl);
//			downloadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion())));
//			
//			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.PENDING);
//			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.STARTING);
//			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.READY);
//			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.RUNNING);
//			downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.COMPLETED);
//			
//			commonServiceImpl.save(MetaType.downloadExec.toString(), downloadExec);			
//		}
//
//		if (response != null) {
//			try {
//				ServletOutputStream servletOutputStream = response.getOutputStream();
//				if (format != null && !format.isEmpty() && format.equalsIgnoreCase(FileType.PDF.toString())) {
//					response.setContentType("application/pdf");
//					response.setHeader("Content-disposition", "attachment");
//					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
//							.concat(".").concat(FileType.PDF.toString().toLowerCase()));
//					doc.save(servletOutputStream);
//					doc.close();
//				} else if (format.equalsIgnoreCase(FileType.XLS.toString())) {
//					response.setContentType("application/xml");
//					response.setHeader("Content-disposition", "attachment");
//					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
//							.concat(".").concat(FileType.XLS.toString().toLowerCase()));
//					workbook.write(servletOutputStream);
//					workbook.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				logger.error("Can not download file.");
//				response.setStatus(300);
//				throw new FileNotFoundException("Can not download file.");
//			}
//		}

		return response;
	}

	public HttpServletResponse downloadSample(String reportExecUuid, String reportExecVersion, String format,
			int offset, int limit, HttpServletResponse response, String sortBy, String order, String requestId,
			RunMode runMode, boolean skipLimitCheck) throws Exception {
		
		if(StringUtils.isBlank(format)) {
			throw new RuntimeException("Format not provided ...");
		}
		
		ReportExec reportExec = (ReportExec) commonServiceImpl.getOneByUuidAndVersion(reportExecUuid, reportExecVersion,
				MetaType.reportExec.toString(), "N");

		MetaIdentifier dependsOnMI = reportExec.getDependsOn().getRef();
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(),
				dependsOnMI.getVersion(), dependsOnMI.getType().toString(), "N");

		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.download.maxrows"));
		if(limit < 1) {
			limit = maxRows;
		}
		if(limit > maxRows) {
			logger.error("Requested rows exceeded the limit of "+maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
		}
		
		List<Map<String, Object>> data = prepareDocumentData(reportExec, report, runMode, limit, false);

		MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(
				new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion()));
		
		Map<String, String> otherDetails = getOtherDetailsByReport(report);
		
		response = downloadServiceImpl.download(format, response, runMode, data, dependsOn, report.getLayout(),
				null, false, "framework.file.download.path", getReportParametrsForDoc(report, reportExec), reportExec.getDependsOn(), otherDetails);
		
//		format = Helper.mapFileFormat(format);		
//		
//		String filePathUrl = Helper.getDocumentFilePath(commonServiceImpl.getConfigValue("framework.file.download.path"), report.getUuid(), report.getVersion(), reportExec.getVersion(), report.getName(), format, true);
//		String attachmentName = Helper.getDocumentFileName(report.getName(), reportExec.getVersion(), format);
//		
//		String reportDirPath = filePathUrl.replaceAll(attachmentName, "");
//		
//		File reportDocDir = new File(reportDirPath);
//		if (!reportDocDir.exists()) {
//			reportDocDir.mkdirs();
//		}
//
//		Workbook workbook = null;
//		PDDocument doc = null;
//
//		Document document = new Document();
//		document.setLocation(filePathUrl);
//		document.setHeader(report.getHeader());
//		document.setHeaderAlignment(report.getHeaderAlign());
//		document.setFooter(report.getFooter());
//		document.setFooterAlignment(report.getFooterAlign());
//		document.setTitle(report.getTitle());
//		document.setLayout(report.getLayout());
//		document.setData(data);
//		document.setMetaObjType(MetaType.report.toString());
//		document.setMetExecObject(reportExec);
//		document.setDocumentType(format);
//		document.setName(report.getName());
//		document.setDescription(!StringUtils.isBlank(report.getDesc()) ? report.getDesc() : "");
//		document.setParameters(getReportParametrsForDoc(report, reportExec));
//		document.setGenerationDate(report.getCreatedOn());
//
//		File reportFile = new File(filePathUrl);
//		boolean isDocCreated = documentGenServiceImpl.createDocument(document);
//		
//		if(!isDocCreated) {
//			throw new RuntimeException((format != null ? format.toUpperCase() : "Document")+" creation failed...");
//		}
//		
//		if(format.equalsIgnoreCase(FileType.PDF.toString())) {
//			doc = PDDocument.load(reportFile);
//		} else if(format.equalsIgnoreCase(FileType.XLS.toString())) {
//			workbook = 	WorkbookFactory.create(reportFile);
//		}
//		
//		DownloadExec downloadExec = new DownloadExec();
//		downloadExec.setBaseEntity();
//		downloadExec.setLocation(filePathUrl);
//		downloadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.reportExec, reportExec.getUuid(), reportExec.getVersion())));
//		
//		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.PENDING);
//		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.STARTING);
//		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.READY);
//		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.RUNNING);
//		downloadExec = (DownloadExec) commonServiceImpl.setMetaStatus(downloadExec, MetaType.downloadExec, Status.Stage.COMPLETED);
//		
//		commonServiceImpl.save(MetaType.downloadExec.toString(), downloadExec);		
//
//		if (response != null) {
//			try {
//				ServletOutputStream servletOutputStream = response.getOutputStream();
//				if (format.equalsIgnoreCase(FileType.PDF.toString())) {
//					response.setContentType("application/pdf");
//					response.setHeader("Content-disposition", "attachment");
//					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
//							.concat(".").concat(FileType.PDF.toString().toLowerCase()));
//					doc.save(servletOutputStream);
//					doc.close();
//				} else if (format.equalsIgnoreCase(FileType.XLS.toString())) {
//					response.setContentType("application/xml");
//					response.setHeader("Content-disposition", "attachment");
//					response.setHeader("filename", report.getName().concat("_").concat(reportExec.getVersion())
//							.concat(".").concat(FileType.XLS.toString().toLowerCase()));
//					workbook.write(servletOutputStream);
//					workbook.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				logger.error("Can not download file.");
//				response.setStatus(300);
//				throw new FileNotFoundException("Can not download file.");
//			}
//		}

		return response;
	}
	
	public List<Map<String, Object>> prepareDocumentData(ReportExec reportExec, Report report, RunMode runMode, int limit, boolean skipLimitCheck) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException, IOException{		
		DataStore datastore = dataStoreServiceImpl.getDatastore(reportExec.getResult().getRef().getUuid(),
				reportExec.getResult().getRef().getVersion());
		if (datastore == null) {
			logger.error("Datastore is not available.");
			throw new RuntimeException("Datastore is not available.");
		}
		
		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.download.maxrows"));
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
			data = datastoreServiceImpl.getResultByDatastore(datastore.getUuid(), datastore.getVersion(), null, 0, limit, null, null, null,runMode);	
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
