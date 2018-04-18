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

public class Thread extends BaseEntity {
	String name;
	MetaIdentifierHolder execInfo;
	public Thread() {
		super();
	}
	public Thread(String name, MetaIdentifierHolder execInfo) {
		super();
		this.name = name;
		this.execInfo = execInfo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MetaIdentifierHolder getExecInfo() {
		return execInfo;
	}
	public void setExecInfo(MetaIdentifierHolder execInfo) {
		this.execInfo = execInfo;
	}
	@Override
	public String toString() {
		return "Thread {\n name: " + name + ", execInfo: " + execInfo + " \n}";
	}
}
