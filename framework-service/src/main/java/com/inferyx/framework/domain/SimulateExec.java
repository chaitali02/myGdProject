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

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "simulateexec")
public class SimulateExec extends BaseEntity {
	private String location;
	private MetaIdentifierHolder dependsOn;
	private MetaIdentifierHolder result; // Datastore info
	private List<Status> statusList;


	public List<Status> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}

	public MetaIdentifierHolder getResult() {
		return result;
	}

	public void setResult(MetaIdentifierHolder result) {
		this.result = result;
	}

	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
