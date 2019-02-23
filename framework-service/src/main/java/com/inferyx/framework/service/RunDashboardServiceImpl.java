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

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Dashboard;
import com.inferyx.framework.domain.DashboardExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.enums.RunMode;

/**
 * @author Ganesh
 *
 */
public class RunDashboardServiceImpl implements Callable<TaskHolder> {

	public static Logger logger = Logger.getLogger(RunDashboardServiceImpl.class); 
	
	private DashboardExec dashboardExec;
	private Dashboard dashboard;
	private String name;
	private SessionContext sessionContext;
	private CommonServiceImpl<?> commonServiceImpl;
	private VizpodServiceImpl vizpodServiceImpl;
	private RunMode runMode;
	private ExecParams execParams;
	
	
	
	/**
	 * @Ganesh
	 *
	 * @return the dashboardExec
	 */
	public DashboardExec getDashboardExec() {
		return dashboardExec;
	}

	/**
	 * @Ganesh
	 *
	 * @param dashboardExec the dashboardExec to set
	 */
	public void setDashboardExec(DashboardExec dashboardExec) {
		this.dashboardExec = dashboardExec;
	}

	/**
	 * @Ganesh
	 *
	 * @return the dashboard
	 */
	public Dashboard getDashboard() {
		return dashboard;
	}

	/**
	 * @Ganesh
	 *
	 * @param dashboard the dashboard to set
	 */
	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
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
	 * @return the execParams
	 */
	public ExecParams getExecParams() {
		return execParams;
	}

	/**
	 * @Ganesh
	 *
	 * @param execParams the execParams to set
	 */
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
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
			
			throw new RuntimeException((message != null) ? message : "Dashboard execution failed.");
		}
		TaskHolder taskHolder = new TaskHolder(name, new MetaIdentifier(MetaType.dashboardExec, dashboardExec.getUuid(), dashboardExec.getVersion()));
		return taskHolder;
	}

	public DashboardExec execute() throws Exception {
		try {
			logger.info("executing dashboard.");
			dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.InProgress);
			
			for(MetaIdentifierHolder vixExecInfo : dashboardExec.getVizExecInfo()) {
				try {
					MetaIdentifier vizExecMI = vixExecInfo.getRef();
					VizExec vizExec = (VizExec) commonServiceImpl.getOneByUuidAndVersion(vizExecMI.getUuid(), vizExecMI.getVersion(), vizExecMI.getType().toString(), "N");
					if(vizExec.getStatusList() != null 
							&& !vizExec.getStatusList().isEmpty()
							&& !Helper.getLatestStatus(vizExec.getStatusList()).equals(new Status(Status.Stage.Failed, new Date()))) {
						ExecParams vixExecParams = vizExec.getExecParams();
						if(vixExecParams == null) {
							vixExecParams = new ExecParams();
							HashMap<String, String> otherParams = new HashMap<>();
							vixExecParams.setOtherParams(otherParams);
						}
						if(vixExecParams.getOtherParams() == null) {
							HashMap<String, String> otherParams = new HashMap<>();
							vixExecParams.setOtherParams(otherParams);
						}
						HashMap<String, String> otherParams = vixExecParams.getOtherParams();
						otherParams.put("saveOnRefresh", dashboard.getSaveOnRefresh());
						vixExecParams.setOtherParams(otherParams);
						commonServiceImpl.save(MetaType.vizExec.toString(), vizExec);
						vizExec = vizpodServiceImpl.execute(vizExec.getUuid(), vizExec.getVersion(), execParams, dashboard.getSaveOnRefresh(), runMode);
					}	
				} catch (Exception e) {
					logger.info("vizExec execution failed <<<< :: >>>> execUuid: "+vixExecInfo.getRef().getUuid()+" :::: execVersion: "+vixExecInfo.getRef().getVersion());
					e.printStackTrace();
				}
			}
			
			dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.Completed);
		} catch (Exception e) {
			e.printStackTrace();
			dashboardExec = (DashboardExec) commonServiceImpl.setMetaStatus(dashboardExec, MetaType.dashboardExec, Status.Stage.Failed);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.dashboardExec, dashboardExec.getUuid(), dashboardExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Dashboard execution failed.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Dashboard execution failed.");	
		}
		return dashboardExec;
	}
}
