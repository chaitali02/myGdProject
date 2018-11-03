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

@Document(collection = "batchexec")
public class BatchExec extends BaseExec {

	private List<MetaIdentifierHolder> execList;

	public List<MetaIdentifierHolder> getExecList() {
		return execList;
	}

	public void setExecList(List<MetaIdentifierHolder> execList) {
		this.execList = execList;
	}

}