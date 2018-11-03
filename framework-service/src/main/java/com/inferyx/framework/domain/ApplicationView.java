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

/**
 * @author Ganesh
 *
 */
public class ApplicationView extends BaseEntity {
	private ParamList paramList;
	private String paramlistChg;
	private String applicationChg;
	private MetaIdentifierHolder dataSource;
	
	/**
	 *
	 * @Ganesh
	 *
	 * @return the dataSource
	 */
	public MetaIdentifierHolder getDataSource() {
		return dataSource;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(MetaIdentifierHolder dataSource) {
		this.dataSource = dataSource;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the applicationChg
	 */
	public String getApplicationChg() {
		return applicationChg;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param applicationChg the applicationChg to set
	 */
	public void setApplicationChg(String applicationChg) {
		this.applicationChg = applicationChg;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the paramList
	 */
	public ParamList getParamList() {
		return paramList;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param paramList the paramList to set
	 */
	public void setParamList(ParamList paramList) {
		this.paramList = paramList;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the paramlistChg
	 */
	public String getParamlistChg() {
		return paramlistChg;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param paramlistChg the paramlistChg to set
	 */
	public void setParamlistChg(String paramlistChg) {
		this.paramlistChg = paramlistChg;
	}
}
