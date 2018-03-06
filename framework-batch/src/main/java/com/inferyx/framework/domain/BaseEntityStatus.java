package com.inferyx.framework.domain;

import java.util.List;

public class BaseEntityStatus extends BaseEntity{

	List<Status> status;
	String type;
	
	public BaseEntityStatus() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Status> getStatus() {
		return status;
	}

	public void setStatus(List<Status> status) {
		this.status = status;
	}
}
