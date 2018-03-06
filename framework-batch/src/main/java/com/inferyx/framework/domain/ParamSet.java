package com.inferyx.framework.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="paramset")
public class ParamSet extends BaseEntity{

	MetaIdentifierHolder dependsOn;
	List<ParamInfo> paramInfo;

	
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	public List<ParamInfo> getParamInfo() {
		return paramInfo;
	}

	public void setParamInfo(List<ParamInfo> paramInfo) {
		this.paramInfo = paramInfo;
	}
	
}
