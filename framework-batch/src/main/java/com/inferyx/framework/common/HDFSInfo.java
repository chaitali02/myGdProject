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
package com.inferyx.framework.common;

public class HDFSInfo {
	private String hdfsURL;
	private String schemaPath;
	
	public String getHdfsURL() {
		return hdfsURL;
	}
	public void setHdfsURL(String hdfsURL) {
		this.hdfsURL = hdfsURL;
	}
	public String getSchemaPath() {
		return schemaPath;
	}
	public void setSchemaPath(String schemaPath) {
		this.schemaPath = schemaPath;
	}

}
