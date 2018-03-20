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
