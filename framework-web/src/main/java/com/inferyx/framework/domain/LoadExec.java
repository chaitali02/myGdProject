package com.inferyx.framework.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "loadexec")
public class LoadExec extends BaseEntity{
	
	private List<Status> statusList;
	private MetaIdentifierHolder dependsOn;	
	private ExecParams execParams;	
	private String exec;
	private MetaIdentifierHolder result;	//dataStore info
	
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
	public List<Status> getStatusList() {
		return statusList;
	}
	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}	
	public ExecParams getExecParams() {
		return execParams;
	}
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}
	public String getExec() {
		return exec;
	}
	public void setExec(String exec) {
		this.exec = exec;
	}
	public MetaIdentifierHolder getResult() {
		return result;
	}
	public void setResult(MetaIdentifierHolder result) {
		this.result = result;
	}	

}
