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
package com.inferyx.framework.enums;

/**
 * @author Ganesh
 *
 */
public enum Quarter {
	Q1("0"), Q2("1"), Q3("2"), Q4("3");
	
	private Quarter(String index) {
		Quarter.valueOf(index);
	}
	
	public Quarter getQuarter(String index) {
		return Quarter.valueOf(index);
	}
}
