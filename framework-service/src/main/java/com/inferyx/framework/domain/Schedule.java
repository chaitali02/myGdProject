/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.List;

/**
 * @author Ganesh
 *
 */
public class Schedule {
	private String name;
	private String startDate;
	private String endDate;
	private String frequencyType; //Once/Daily/Weekly/Monthly/Yearly
	private List<String> frequencyDetail;//if weekly then days/if monthly then date
	private String recurring = "N";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getFrequencyType() {
		return frequencyType;
	}
	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}
	public List<String> getFrequencyDetail() {
		return frequencyDetail;
	}
	public void setFrequencyDetail(List<String> frequencyDetail) {
		this.frequencyDetail = frequencyDetail;
	}
	public String getRecurring() {
		return recurring;
	}
	public void setRecurring(String recurring) {
		this.recurring = recurring;
	}
	
	
}
