package com.inferyx.framework.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="log")
public class Log extends BaseEntity{
	
	private String requestUrl;
	private Date startTime;
	private Date endTime;
	private String totalTime;	
	
	public Log() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Log(String requestUrl, Date startTime, Date endTime, String totalTime) {
		super();
		this.requestUrl = requestUrl;
		this.startTime = startTime;
		this.endTime = endTime;
		this.totalTime = totalTime;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	@Override
	public String toString() {
		return "Log { requestUrl: " + requestUrl + ", startTime: " + startTime + ", endTime: " + endTime + ", totalTime: "
				+ totalTime + " }";
	}
	
	
}
