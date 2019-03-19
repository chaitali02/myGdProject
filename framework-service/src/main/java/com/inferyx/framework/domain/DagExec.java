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

@Document(collection = "dagexec")
public class DagExec extends Dag {
	
	public DagExec(){} 
	public DagExec(Dag dag) {
		super(dag);
		MetaIdentifier dagRef = new MetaIdentifier(MetaType.dag, dag.getUuid(), dag.getVersion());
		setDependsOn(dagRef);
	}
	
	private String execCreated = "N";
	
	MetaIdentifier dependsOn;
	
	private ExecParams execParams;
	
	public MetaIdentifier getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifier dependsOn) {
		this.dependsOn = dependsOn;
	}

	public ExecParams getExecParams() {
		return execParams;
	}

	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}
	/**
	 * @return the execCreated
	 */
	public String getExecCreated() {
		return execCreated;
	}
	/**
	 * @param execCreated the execCreated to set
	 */
	public void setExecCreated(String execCreated) {
		this.execCreated = execCreated;
	}

}