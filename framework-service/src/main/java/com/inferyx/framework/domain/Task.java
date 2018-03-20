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

import java.util.ArrayList;
import java.util.List;

public class Task {
	
	public Task() {}
	
	public Task (Task task) {
		taskId = task.getTaskId();
		dependsOn = task.getDependsOn();
		name = task.getName();
		operators = task.getOperators();
		xPos = task.getxPos();
		yPos = task.getyPos();
		active = task.getActive();
		statusList.addAll(task.getStatusList());
	}

	private String taskId;
	private List<String> dependsOn = new ArrayList<>(0);
	private String name;
	private List<Operator> operators = new ArrayList<Operator>();
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
	/**
	 * 
	 * @return The id
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * 
	 * @param id
	 *            The id
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * 
	 * @return The depdendsOn
	 */
	public List<String> getDependsOn() {
		return dependsOn;
	}

	/**
	 * 
	 * @param depdendsOn
	 *            The depdendsOn
	 */
	public void setDependsOn(List<String> depdendsOn) {
		this.dependsOn = depdendsOn;
	}

	/**
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return The operators
	 */
	public List<Operator> getOperators() {
		return operators;
	}

	/**
	 * 
	 * @param operators
	 *            The operators
	 */
	public void setOperators(List<Operator> operators) {
		this.operators = operators;
	}

	public List<Status> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}
	
}
