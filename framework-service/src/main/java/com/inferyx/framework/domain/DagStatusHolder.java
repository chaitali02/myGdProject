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
import java.util.List;

public class DagStatusHolder {

	private MetaIdentifierHolder dependsOn;
	private HashMap<String,StageStatusHolder> stages;
	private List<Status> status;	
	
	public HashMap<String, StageStatusHolder> getStages() {
		return stages;
	}
	public void setStages(HashMap<String, StageStatusHolder> stages) {
		this.stages = stages;
	}
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}	
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}	
	public List<Status> getStatus() {
		return status;
	}
	public void setStatus(List<Status> status) {
		this.status = status;
	}
}
