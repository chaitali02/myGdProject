package com.inferyx.framework.domain;

import java.util.List;

public class ParamInfo {
	String paramSetId;
	List<ParamListHolder> paramSetVal;
	
	public String getParamSetId() {
		return paramSetId;
	}
	public void setParamSetId(String paramSetId) {
		this.paramSetId = paramSetId;
	}
	public List<ParamListHolder> getParamSetVal() {
		return paramSetVal;
	}
	public void setParamSetVal(List<ParamListHolder> paramSetVal) {
		this.paramSetVal = paramSetVal;
	}
}
