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
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.SenderInfo;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.SparkExecutor;

/**
 * @author Ganesh
 *
 */
public class RunReportServiceImpl implements Callable<TaskHolder> {

	public static Logger logger = Logger.getLogger(RunReportServiceImpl.class); 
	
	private ReportExec reportExec;
	private Report report;
	private SessionContext sessionContext;
	private String appUuid;
	private String name;
	private HDFSInfo hdfsInfo;
	private CommonServiceImpl<?> commonServiceImpl;
	private SparkExecutor<?> sparkExecutor;
	private RunMode runMode;
	private ReportServiceImpl reportServiceImpl;

	/**
	 * @Ganesh
	 *
	 * @return the reportExec
	 */
	public ReportExec getReportExec() {
		return reportExec;
	}

	/**
	 * @Ganesh
	 *
	 * @param reportExec the reportExec to set
	 */
	public void setReportExec(ReportExec reportExec) {
		this.reportExec = reportExec;
	}

	/**
	 * @Ganesh
	 *
	 * @return the report
	 */
	public Report getReport() {
		return report;
	}

	/**
	 * @Ganesh
	 *
	 * @param report the report to set
	 */
	public void setReport(Report report) {
		this.report = report;
	}

	/**
	 * @Ganesh
	 *
	 * @return the sessionContext
	 */
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	/**
	 * @Ganesh
	 *
	 * @param sessionContext the sessionContext to set
	 */
	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 * @Ganesh
	 *
	 * @return the appUuid
	 */
	public String getAppUuid() {
		return appUuid;
	}

	/**
	 * @Ganesh
	 *
	 * @param appUuid the appUuid to set
	 */
	public void setAppUuid(String appUuid) {
		this.appUuid = appUuid;
	}

	/**
	 * @Ganesh
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @Ganesh
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @Ganesh
	 *
	 * @return the hdfsInfo
	 */
	public HDFSInfo getHdfsInfo() {
		return hdfsInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param hdfsInfo the hdfsInfo to set
	 */
	public void setHdfsInfo(HDFSInfo hdfsInfo) {
		this.hdfsInfo = hdfsInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @return the commonServiceImpl
	 */
	public CommonServiceImpl<?> getCommonServiceImpl() {
		return commonServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param commonServiceImpl the commonServiceImpl to set
	 */
	public void setCommonServiceImpl(CommonServiceImpl<?> commonServiceImpl) {
		this.commonServiceImpl = commonServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the sparkExecutor
	 */
	public SparkExecutor<?> getSparkExecutor() {
		return sparkExecutor;
	}

	/**
	 * @Ganesh
	 *
	 * @param sparkExecutor the sparkExecutor to set
	 */
	public void setSparkExecutor(SparkExecutor<?> sparkExecutor) {
		this.sparkExecutor = sparkExecutor;
	}

	/**
	 * @Ganesh
	 *
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}

	/**
	 * @Ganesh
	 *
	 * @param runMode the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	/**
	 * @Ganesh
	 *
	 * @return the reportServiceImpl
	 */
	public ReportServiceImpl getReportServiceImpl() {
		return reportServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param reportServiceImpl the reportServiceImpl to set
	 */
	public void setReportServiceImpl(ReportServiceImpl reportServiceImpl) {
		this.reportServiceImpl = reportServiceImpl;
	}

	@Override
	public TaskHolder call() throws Exception {
		try {
			FrameworkThreadLocal.getSessionContext().set(sessionContext);
			execute();
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			
			throw new RuntimeException((message != null) ? message : "Report execution FAILED.");
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(MetaType.vizExec, reportExec.getUuid(), reportExec.getVersion()));
		return taskHolder;
	}
	
	public ReportExec execute() throws Exception {
		String tableName = null;
		SenderInfo senderInfo = report.getSenderInfo();
		try {
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			long countRows = -1L;
			
			reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.RUNNING);
			
			if (StringUtils.isBlank(reportExec.getExec())) {
				throw new RuntimeException("sql not generated");
			}

			Datasource reportDS = commonServiceImpl.getDatasourceByObject(report);
//			String tableName = getTableName(report, reportExec, execContext, reportDS);
			tableName = String.format("%s_%s_%s", report.getUuid().replace("-", "_"), report.getVersion(), reportExec.getVersion());
					
			String reportPath = String.format("%s/%s/%s", report.getUuid(), report.getVersion(), reportExec.getVersion());
			String reportDefaultPath = Helper.getPropertyValue("framework.report.Path");
			reportDefaultPath = reportDefaultPath.endsWith("/") ? reportDefaultPath : reportDefaultPath.concat("/");
			String filePathUrl = String.format("%s%s", reportDefaultPath, reportPath);

			ResultSetHolder rsHolder = sparkExecutor.executeAndRegisterByDatasource(reportExec.getExec(), tableName, reportDS, appUuid);
			if(report.getSaveOnRefresh().equalsIgnoreCase("Y")) {
				sparkExecutor.registerAndPersistDataframe(rsHolder, null, com.inferyx.framework.enums.SaveMode.APPEND.toString(), hdfsInfo.getHdfsURL().concat(filePathUrl), tableName, "true", false);
				
				//getting file size
				String srcFilePath = commonServiceImpl.getFileNameFromDir(filePathUrl, FileType.PARQUET.toString(), FileType.PARQUET.toString());
				File reportFile = new File(srcFilePath);
				if(reportFile.exists()) {
					reportExec.setSizeMB(Double.parseDouble(String.format("%.2f", (reportFile.length()/(1024.0 * 1024.0)))));
				}
			}
			
			countRows = rsHolder.getCountRows();
			reportExec.setNumRows(countRows);
			
			reportServiceImpl.persistDatastore(reportExec, tableName, filePathUrl, resultRef, new MetaIdentifier(MetaType.report, report.getUuid(), report.getVersion()), countRows, runMode);

			reportExec.setResult(resultRef);
			commonServiceImpl.save(MetaType.reportExec.toString(), reportExec);
			
			//sending report execution status through email
			if(senderInfo != null && senderInfo.getNotifyOnSuccess().equalsIgnoreCase("Y")) {
				synchronized (reportExec.getUuid()) {
					if(!reportServiceImpl.sendSuccessNotification(senderInfo, report, reportExec, runMode)) {
						throw new RuntimeException("Can not send email notification.");
					}
				}
			}
			reportExec = (ReportExec) commonServiceImpl.setMetaStatus(reportExec, MetaType.reportExec, Status.Stage.COMPLETED);
		} catch (Exception e) { 
			e.printStackTrace();
			
			//sending report execution status through email 
			try {
				if(senderInfo != null && senderInfo.getNotifyOnFailure().equalsIgnoreCase("Y")) {
					reportServiceImpl.sendFailureNotification(senderInfo, report, reportExec);
				}
			} catch (Exception exc) {
				// TODO: handle exception
				exc.printStackTrace();
			}
			
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
}
