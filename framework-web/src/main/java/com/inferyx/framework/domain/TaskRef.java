/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

import java.util.List;

/**
 * Keeps the task reference details. To be used in TaskExec
 * @author joy
 *
 */
public class TaskRef {

	private MetaIdentifier dagRef;
	private String taskId;
	private String stageId;
	private String taskName;
	private List<String> dependsOn;
	
	/*public TaskRef(MetaIdentifier dagRef, String taskId, String taskName) {
		super();
		this.dagRef = dagRef;
		this.taskId = taskId;
		this.taskName = taskName;
	}*/
	
	public TaskRef(MetaIdentifier dagRef,List<String> dependsOn,String taskId, String stageId, String taskName) {
		super();
		this.dagRef = dagRef;
		this.taskId = taskId;
		this.stageId = stageId;
		this.taskName = taskName;
		this.dependsOn=dependsOn;
	}

	public List<String> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(List<String> dependsOn) {
		this.dependsOn = dependsOn;
	}

	public TaskRef() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MetaIdentifier getDagRef() {
		return dagRef;
	}
	public void setDagRef(MetaIdentifier dagRef) {
		this.dagRef = dagRef;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
	
	
}

