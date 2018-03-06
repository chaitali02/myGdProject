package com.inferyx.framework.domain;

import java.util.HashMap;
import java.util.List;

public class DagStatusHolder {

	private MetaIdentifierHolder dependsOn;
	private HashMap<String,StageStatusHolder> stages;
	private List<Status> status;	
	
	public HashMap<String, StageStatusHolder> getStages() {
		return stages;
	}
	public void setStages(HashMap<String, StageStatusHolder> stages) {
		this.stages = stages;
	}
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}	
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}	
	public List<Status> getStatus() {
		return status;
	}
	public void setStatus(List<Status> status) {
		this.status = status;
	}
}
