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
package com.inferyx.framework.domain;

import java.util.HashMap;

public class StageStatusHolder extends DagStatusHolder{

	private String stageId;
//	private List<Status> stageStatus;
	private HashMap<String,TaskStatusHolder> tasks;
	
	
	public HashMap<String, TaskStatusHolder> getTasks() {
		return tasks;
	}
	public void setTasks(HashMap<String, TaskStatusHolder> tasks) {
		this.tasks = tasks;
	}
	public String getStageId() {
		return stageId;
	}
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
}
