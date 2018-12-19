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

@Document(collection="application")
public class Application extends BaseEntity{
	private MetaIdentifierHolder dataSource;
	private MetaIdentifierHolder paramList;
	private String deployPort;
	
	/**
	 * @Ganesh 
	 *
	 * @return the deployPort
	 */
	public String getDeployPort() {
		return deployPort;
	}

	/**
	 * @Ganesh 
	 *
	 * @param deployPort the deployPort to set
	 */
	public void setDeployPort(String deployPort) {
		this.deployPort = deployPort;
	}

	public MetaIdentifierHolder getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(MetaIdentifierHolder dataSource) {
		this.dataSource = dataSource;
	}
	
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}

	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}

	
}
