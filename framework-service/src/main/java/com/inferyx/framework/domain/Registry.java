package com.inferyx.framework.domain;

public class Registry {
	private String id;
	private String name;
	private String desc;
	private String registeredOn;
	private String status;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getRegisteredOn() {
		return registeredOn;
	}
	public void setRegisteredOn(String registeredOn) {
		this.registeredOn = registeredOn;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
