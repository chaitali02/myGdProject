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

/**
 * @author Ganesh
 *
 */
@Document(collection="distribution")
public class Distribution extends BaseEntity {
	String library;
	String className;
	MetaIdentifierHolder paramList;
	/**
	 * @Ganesh
	 *
	 * @return the library
	 */
	public String getLibrary() {
		return library;
	}
	/**
	 * @Ganesh
	 *
	 * @param library the library to set
	 */
	public void setLibrary(String library) {
		this.library = library;
	}
	/**
	 * @Ganesh
	 *
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @Ganesh
	 *
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @Ganesh
	 *
	 * @return the paramList
	 */
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}
	/**
	 * @Ganesh
	 *
	 * @param paramList the paramList to set
	 */
	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}
	
}
