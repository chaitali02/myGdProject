package com.inferyx.framework.domain;

public class Error {

	private String userMessage;
	private String internalMessage;
	private int code;
	private String details;
	
	public String getUserMessage() {
		return userMessage;
	}
	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
	public String getInternalMessage() {
		return internalMessage;
	}
	public void setInternalMessage(String internalMessage) {
		this.internalMessage = internalMessage;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}	
}
