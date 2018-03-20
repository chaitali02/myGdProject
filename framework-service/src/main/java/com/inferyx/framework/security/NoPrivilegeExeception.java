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
