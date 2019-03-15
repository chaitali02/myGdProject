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

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "function")
public class Function extends BaseEntity {

	private String inputReq;
//	private FuncType funcType;
	private List<FunctionInfo> functionInfo;
	private String category;
    private DataType returnType;
	
	public String getInputReq() {
		return inputReq;
	}

	public void setInputReq(String inputReq) {
		this.inputReq = inputReq;
	}

	public DataType getReturnType() {
		return returnType;
	}

	public void setReturnType(DataType returnType) {
		this.returnType = returnType;
	}

	public String getCategory() {
		return category;

	}

	public void setCategory(String category) {
		this.category = category;
	}

//	public FuncType getFuncType() {
//		return funcType;
//	}
//
//	public void setFuncType(FuncType funcType) {
//		this.funcType = funcType;
//	}

	public List<FunctionInfo> getFunctionInfo() {
		return functionInfo;
	}

	public void setFunctionInfo(List<FunctionInfo> functionInfo) {
		this.functionInfo = functionInfo;
	}

	
}