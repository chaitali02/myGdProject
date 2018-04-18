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

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="uploadexec")
public class UploadExec extends BaseEntity
{
	private String location;
	private String fileName;
	private MetaIdentifierHolder dependsOn;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
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
