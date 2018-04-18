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

public class DagWithParamsHolder {
	
	private Dag dag;
	private ExecParams execParams;
	
	public Dag getDag() {
		return dag;
	}
	public void setDag(Dag dag) {
		this.dag = dag;
	}
	public ExecParams getExecParams() {
		return execParams;
	}
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}
	
}
