package com.inferyx.framework.domain;

import java.util.List;

public class StatusHolder {
	
	private MetaIdentifierHolder metaRef;
	private List<Status> statusList;

	public MetaIdentifierHolder getMetaRef() {
		return metaRef;
	}
	public void setMetaRef(MetaIdentifierHolder metaRef) {
		this.metaRef = metaRef;
	}
	public List<Status> getStatusList() {
		return statusList;
	}
	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}
	
}
