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

public class RolePriv {

	
	private String type;
	private Object privInfo;
	//private List<String> privInfo = new ArrayList<String>();
	
	
	public Object getPrivInfo() {
		return privInfo;
	}
	public void setPrivInfo(Object privInfo) {
		this.privInfo = privInfo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/*public List<String> getPrivInfo() {
		return privInfo;
	}
	public void setPrivInfo(List<String> privInfo) {
		this.privInfo = privInfo;
	}*/
	@Override
	public String toString() {
		return "RolePriv {type: " + type + ", privInfo: " + privInfo + "}";
	}
}
