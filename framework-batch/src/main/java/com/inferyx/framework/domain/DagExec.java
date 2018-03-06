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

}