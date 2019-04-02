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

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.domain.Vizpod;
import com.inferyx.framework.enums.PersistMode;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;

/**
 * @author Ganesh
 *
 */
public class RunVizpodServiceImpl implements Callable<TaskHolder> {

	public static Logger logger = Logger.getLogger(RunVizpodServiceImpl.class); 
	
	private VizExec vizExec;
	private Vizpod vizpod;
	private	RunMode runMode;
	private	CommonServiceImpl<?> commonServiceImpl;
	private	VizpodServiceImpl vizpodServiceImpl;
	private SessionContext sessionContext;
	private String appUuid;
	private String name;
	private SparkExecutor<?> sparkExecutor;
	private DataStoreServiceImpl dataStoreServiceImpl;
	private ExecutorFactory execFactory;
	private String saveOnRefresh;
	private HDFSInfo hdfsInfo;
	private Datasource appDs;
	
	/**
	 * @Ganesh
	 *
	 * @return the appDs
	 */
	public Datasource getAppDs() {
		return appDs;
	}

	/**
	 * @Ganesh
	 *
	 * @param appDs the appDs to set
	 */
	public void setAppDs(Datasource appDs) {
		this.appDs = appDs;
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
	 * @return the saveOnRefresh
	 */
	public String getSaveOnRefresh() {
		return saveOnRefresh;
	}

	/**
	 * @Ganesh
	 *
	 * @param saveOnRefresh the saveOnRefresh to set
	 */
	public void setSaveOnRefresh(String saveOnRefresh) {
		this.saveOnRefresh = saveOnRefresh;
	}

	/**
	 * @Ganesh
	 *
	 * @return the vizExec
	 */
	public VizExec getVizExec() {
		return vizExec;
	}

	/**
	 * @Ganesh
	 *
	 * @param vizExec the vizExec to set
	 */
	public void setVizExec(VizExec vizExec) {
		this.vizExec = vizExec;
	}

	/**
	 * @Ganesh
	 *
	 * @return the vizpod
	 */
	public Vizpod getVizpod() {
		return vizpod;
	}

	/**
	 * @Ganesh
	 *
	 * @param vizpod the vizpod to set
	 */
	public void setVizpod(Vizpod vizpod) {
		this.vizpod = vizpod;
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
	 * @return the vizpodServiceImpl
	 */
	public VizpodServiceImpl getVizpodServiceImpl() {
		return vizpodServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param vizpodServiceImpl the vizpodServiceImpl to set
	 */
	public void setVizpodServiceImpl(VizpodServiceImpl vizpodServiceImpl) {
		this.vizpodServiceImpl = vizpodServiceImpl;
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
	 * @return the dataStoreServiceImpl
	 */
	public DataStoreServiceImpl getDataStoreServiceImpl() {
		return dataStoreServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @param dataStoreServiceImpl the dataStoreServiceImpl to set
	 */
	public void setDataStoreServiceImpl(DataStoreServiceImpl dataStoreServiceImpl) {
		this.dataStoreServiceImpl = dataStoreServiceImpl;
	}

	/**
	 * @Ganesh
	 *
	 * @return the execFactory
	 */
	public ExecutorFactory getExecFactory() {
		return execFactory;
	}

	/**
	 * @Ganesh
	 *
	 * @param execFactory the execFactory to set
	 */
	public void setExecFactory(ExecutorFactory execFactory) {
		this.execFactory = execFactory;
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
			
			throw new RuntimeException((message != null) ? message : "Vizpod execution FAILED.");
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(MetaType.vizExec, vizExec.getUuid(), vizExec.getVersion()));
		return taskHolder;
	}

	public VizExec execute() throws Exception {
		try {		
			logger.info("executing vizpod.");			
			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.RUNNING);
			
			String tableName = String.format("%s_%s_%s", vizpod.getUuid().replaceAll("-", "_"), vizpod.getVersion(), vizExec.getVersion());
			String defautlDir = String.format("%s%s", hdfsInfo.getHdfsURL(), commonServiceImpl.getConfigValue("framework.dashboard.Path"));
			defautlDir = defautlDir.endsWith("/") ? defautlDir : defautlDir.concat("/");
			String filePath = String.format("%s/%s/%s", vizpod.getUuid(), vizpod.getVersion(), vizExec.getVersion());
			String filePathUrl = defautlDir.concat(filePath);
			
			long count = -1L;
			
			String persistMode = null;
			if(saveOnRefresh != null && saveOnRefresh.equalsIgnoreCase("Y")) {
				persistMode = PersistMode.DISK_AND_MEMORY_ONLY.toString();
			} else {
				persistMode = PersistMode.MEMORY_ONLY.toString();
			}
			
			MetaIdentifier vizpodSourceMI = vizpod.getSource().getRef();
			Object sourceObj = commonServiceImpl.getOneByUuidAndVersion(vizpodSourceMI.getUuid(), vizpodSourceMI.getVersion(), vizpodSourceMI.getType().toString(), "N");
			Datasource vizpodDs = commonServiceImpl.getDatasourceByObject(sourceObj);

			Datasource appDs = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(appDs.getType());
			ResultSetHolder rsHolder = exec.executeAndRegisterByDatasource(vizExec.getSql(), tableName, vizpodDs, appUuid);
			if(saveOnRefresh != null && saveOnRefresh.equalsIgnoreCase("Y")) {
				rsHolder = sparkExecutor.registerAndPersistDataframe(rsHolder, null, SaveMode.APPEND.toString(), filePathUrl, null, "true", false);
			} 
			
			count = rsHolder.getCountRows();
			
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			dataStoreServiceImpl.setRunMode(runMode);
			dataStoreServiceImpl.create(filePathUrl, vizExec.getName()
					, new MetaIdentifier(MetaType.vizpod, vizpod.getUuid(), vizpod.getVersion())
					, new MetaIdentifier(MetaType.vizExec, vizExec.getUuid(), vizExec.getVersion())
					, vizExec.getAppInfo()
					, vizExec.getCreatedBy()
					, SaveMode.APPEND.toString()
					, resultRef
					, count
					, persistMode
					, "Vizpod exec for vizpod "+vizpod.getName());
			vizExec.setResult(resultRef);
			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.COMPLETED);
		} catch (Exception e) {
			e.printStackTrace();
			vizExec = (VizExec) commonServiceImpl.setMetaStatus(vizExec, MetaType.vizExec, Status.Stage.FAILED);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.vizExec, vizExec.getUuid(), vizExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Vizpod execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Vizpod execution FAILED.");	
		}
		
		return vizExec;
	}
}
