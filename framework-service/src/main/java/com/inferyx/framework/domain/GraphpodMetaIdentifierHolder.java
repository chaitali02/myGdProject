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


public class GraphpodMetaIdentifierHolder {
	private String  id;
	private String lable;


	public GraphpodMetaIdentifierHolder() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public GraphpodMetaIdentifierHolder(String id, String lable) {
		super();
		this.id = id;
		this.lable = lable;
	}
	
	

	
	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLable() {
		return this.lable;
	}

	public void setLable(String lable) {
		this.lable = lable;
	}

	@Override
	public String toString() {
		return "GraphpodMetaIdentifierHolder [id=" + id + ", value=" + lable + "]";
	}

	
	

}
