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

import java.util.ArrayList;
import java.util.List;

public class Stage {
	
	public Stage() {}
	
	public Stage (Stage stage) {
		stageId = stage.getStageId();
		dependsOn = stage.getDependsOn();
		name = stage.getName();
		xPos = stage.getxPos();
		yPos = stage.getyPos();
		tasks = stage.getTasks();
		active = stage.getActive();
		statusList.addAll(stage.getStatusList());
	}

	private String stageId;
	private List<String> dependsOn = new ArrayList<>(0);
	private List<Task> tasks = new ArrayList<Task>();
	private String name;
    private Double xPos;
	private Double yPos;
	private String active = "Y";
	private List<Status> statusList = new ArrayList<>();
	
	
	/**
	 * @Ganesh
	 *
	 * @return the active
	 */
	public String getActive() {
		return active;
	}

	/**
	 * @Ganesh
	 *
	 * @param active the active to set
	 */
	public void setActive(String active) {
		this.active = active;
	}

	public List<Status> getStatusList() {
		return statusList;
	}
	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

	public List<String> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(List<String> dependsOn) {
		this.dependsOn = dependsOn;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getxPos() {
		return xPos;
	}

	public void setxPos(Double xPos) {
		this.xPos = xPos;
	}

	public Double getyPos() {
		return yPos;
	}

	public void setyPos(Double yPos) {
		this.yPos = yPos;
	}
	
}