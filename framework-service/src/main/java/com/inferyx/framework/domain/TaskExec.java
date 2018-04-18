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

/**
 * @author joy
 *
 */
//@Document(collection = "task")
public class TaskExec extends Task {
	
	public TaskExec(Task task) {
		super(task);
	}
	
	/*private Map<String, MetaIdentifier> refKeys = new HashMap<>();
	private List<MetaIdentifier> refKeyList = new ArrayList<>();*/
	private MetaIdentifierHolder result;
	
	public MetaIdentifierHolder getResult() {
		return result;
	}
	public void setResult(MetaIdentifierHolder result) {
		this.result = result;
	}
	
}
