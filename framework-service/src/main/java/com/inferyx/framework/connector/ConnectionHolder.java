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
package com.inferyx.framework.connector;


public class ConnectionHolder {
	
	private String type;
	private Object conObject;
	private Object stmtObject;
	
	public Object getStmtObject() {
		return stmtObject;
	}
	public void setStmtObject(Object stmtObject) {
		this.stmtObject = stmtObject;
	}
	public Object getConObject() {
		return conObject;
	}
	public void setConObject(Object conObject) {
		this.conObject = conObject;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
