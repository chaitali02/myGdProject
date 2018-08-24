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

@Document(collection = "batch")
public class Batch extends BaseEntity {

	private List<MetaIdentifierHolder> metaList;
	private String inParallel;

	public String getInParallel() {
		return inParallel;
	}

	public void setInParallel(String inParallel) {
		this.inParallel = inParallel;
	}

	public List<MetaIdentifierHolder> getMetaList() {
		return metaList;
	}

	public void setMetaList(List<MetaIdentifierHolder> metaList) {
		this.metaList = metaList;
	}

}