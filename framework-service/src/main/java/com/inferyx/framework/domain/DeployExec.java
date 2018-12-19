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

/**
 * @author Ganesh *
 */
@Document(collection = "deployexec")
public class DeployExec extends BaseExec {
	String url;

	/**
	 * @Ganesh 
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @Ganesh 
	 *
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
}
