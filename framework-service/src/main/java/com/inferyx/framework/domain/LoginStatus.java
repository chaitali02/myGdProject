package com.inferyx.framework.domain;

public class LoginStatus{
	
	private String userName;
	private String UserUuid;
	private String SessionId;
	private String Status;
	private String message;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserUuid() {
		return UserUuid;
	}
	public void setUserUuid(String userUuid) {
		UserUuid = userUuid;
	}
	public String getSessionId() {
		return SessionId;
	}
	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	
	
}
