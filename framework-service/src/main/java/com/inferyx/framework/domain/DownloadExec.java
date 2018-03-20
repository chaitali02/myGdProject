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

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="downloadexec")
public class DownloadExec extends BaseEntity
{
	private String location;
	MetaIdentifierHolder dependsOn;
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
}
