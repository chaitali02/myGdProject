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

import java.util.List;

/**
 * Keeps the stage reference details. To be used in StageExec
 * @author joy
 *
 */
public class StageRef {
	
	private MetaIdentifier dagRef;
	private String stageId;
	private String stageName;
	private List<String> dependsOn;
	
	public StageRef(){}
	
	public StageRef(MetaIdentifier dagRef, List<String> dependsOn ,String stageId, String stageName) {
		super();
		this.dagRef = dagRef;
		this.dependsOn = dependsOn;
		this.stageId = stageId;
		this.stageName = stageName;
	}

	public List<String> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(List<String> dependsOn) {
		this.dependsOn = dependsOn;
	}

	public MetaIdentifier getDagRef() {
		return dagRef;
	}
	public void setDagRef(MetaIdentifier dagRef) {
		this.dagRef = dagRef;
	}
	public String getStageId() {
		return stageId;
	}
	public void setStageId(String stageId) {
		this.stageId = stageId;
	}
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	
}

