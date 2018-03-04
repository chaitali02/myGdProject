package com.inferyx.framework.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "function")
public class Function extends BaseEntity {

	private String inputReq;
	private FuncType funcType;
	private List<FunctionInfo> functionInfo;
	private String category;
    
	
	public String getInputReq() {
		return inputReq;
	}

	public void setInputReq(String inputReq) {
		this.inputReq = inputReq;
	}

	public String getCategory() {
		return category;

	}

	public void setCategory(String category) {
		this.category = category;
	}

	public FuncType getFuncType() {
		return funcType;
	}

	public void setFuncType(FuncType funcType) {
		this.funcType = funcType;
	}

	public List<FunctionInfo> getFunctionInfo() {
		return functionInfo;
	}

	public void setFunctionInfo(List<FunctionInfo> functionInfo) {
		this.functionInfo = functionInfo;
	}

	
}