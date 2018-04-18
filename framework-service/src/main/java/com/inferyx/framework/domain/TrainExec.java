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

@Document(collection = "trainexec")
public class TrainExec extends BaseEntity {

	private List<Status> statusList;
	MetaIdentifierHolder dependsOn;
	private ExecParams execParams;
	List<MetaIdentifier> refKeyList;
	private String exec;
	private MetaIdentifierHolder result; // Datastore info

	/**
	 * @Ganesh
	 *
	 * @return the statusList
	 */
	public List<Status> getStatusList() {
		return statusList;
	}

	/**
	 * @Ganesh
	 *
	 * @param statusList
	 *            the statusList to set
	 */
	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}

	/**
	 * @Ganesh
	 *
	 * @return the dependsOn
	 */
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	/**
	 * @Ganesh
	 *
	 * @param dependsOn
	 *            the dependsOn to set
	 */
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	/**
	 * @Ganesh
	 *
	 * @return the execParams
	 */
	public ExecParams getExecParams() {
		return execParams;
	}

	/**
	 * @Ganesh
	 *
	 * @param execParams
	 *            the execParams to set
	 */
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}

	/**
	 * @Ganesh
	 *
	 * @return the refKeyList
	 */
	public List<MetaIdentifier> getRefKeyList() {
		return refKeyList;
	}

	/**
	 * @Ganesh
	 *
	 * @param refKeyList
	 *            the refKeyList to set
	 */
	public void setRefKeyList(List<MetaIdentifier> refKeyList) {
		this.refKeyList = refKeyList;
	}

	/**
	 * @Ganesh
	 *
	 * @return the exec
	 */
	public String getExec() {
		return exec;
	}

	/**
	 * @Ganesh
	 *
	 * @param exec
	 *            the exec to set
	 */
	public void setExec(String exec) {
		this.exec = exec;
	}

	/**
	 * @Ganesh
	 *
	 * @return the result
	 */
	public MetaIdentifierHolder getResult() {
		return result;
	}

	/**
	 * @Ganesh
	 *
	 * @param result
	 *            the result to set
	 */
	public void setResult(MetaIdentifierHolder result) {
		this.result = result;
	}

}
