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
public enum Day {
	SUN("0"), MON("1"), TUE("2"), WED("3"), THU("4"), FRI("5"), SAT("6");
	
	private Day(String index) {
		Day.valueOf(index);
	}
	
	public Day getDay(String index) {
		return Day.valueOf(index);
	}
}
