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
 * @author joy
 *
 */
/**
 * Framework's custom ThreadLocal class
 * @author joy
 *
 */
public class FrameworkThreadLocal {
	
	private static ThreadLocal<SessionContext> sessionContext = new ThreadLocal<>();

	/**
	 * 
	 */
	public FrameworkThreadLocal() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the sessionContext
	 */
	public static ThreadLocal<SessionContext> getSessionContext() {
		return sessionContext;
	}

}
