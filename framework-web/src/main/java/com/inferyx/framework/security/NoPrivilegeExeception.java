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
package com.inferyx.framework.security;

public class NoPrivilegeExeception extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String str1;
    NoPrivilegeExeception(String str2) {
       str1=str2;
    }
    public String toString(){ 
       return ("Output String = "+str1) ;
    }
}
