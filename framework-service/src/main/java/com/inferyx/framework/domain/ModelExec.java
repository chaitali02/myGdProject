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

@Document(collection = "modelexec")
public class ModelExec extends BaseEntity  {
	private List<Status> statusList;
	MetaIdentifierHolder dependsOn;	
	private ExecParams execParams;
	List<MetaIdentifier> refKeyList;
	private String exec;
	private MetaIdentifierHolder result; // Datastore info
	public List<Status> getStatusList() {
		return statusList;
	}
	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
	public ExecParams getExecParams() {
		return execParams;
	}
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}
	public List<MetaIdentifier> getRefKeyList() {
		return refKeyList;
	}
	public void setRefKeyList(List<MetaIdentifier> refKeyList) {
		this.refKeyList = refKeyList;
	}
	public String getExec() {
		return exec;
	}
	public void setExec(String exec) {
		this.exec = exec;
	}
	public MetaIdentifierHolder getResult() {
		return result;
	}
	public void setResult(MetaIdentifierHolder result) {
		this.result = result;
	}
	
	
}
