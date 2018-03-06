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

@Document(collection="meta")
public class Meta extends BaseEntity{
	
	private String menu;
	//private String desc;

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	
	/**
	 * 
	 * @return The name
	 */
	/*public String getName() {
		return name;
	}

	*//**
	 * 
	 * @param name
	 *            The name
	 *//*
	public void setName(String name) {
		this.name = name;
	}

	

	

	*//**
	 * 
	 * @return The desc
	 *//*
	public String getDesc() {
		return desc;
	}

	*//**
	 * 
	 * @param desc
	 *            The desc
	 *//*
	public void setDesc(String desc) {
		this.desc = desc;
	}

	*/

	
	
	

}
