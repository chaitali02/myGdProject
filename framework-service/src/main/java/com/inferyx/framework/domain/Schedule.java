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
	
	/**
	 *
	 * @Ganesh
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the frequencyType
	 */
	public String getFrequencyType() {
		return frequencyType;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param frequencyType the frequencyType to set
	 */
	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the frequencyDetail
	 */
	public List<String> getFrequencyDetail() {
		return frequencyDetail;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param frequencyDetail the frequencyDetail to set
	 */
	public void setFrequencyDetail(List<String> frequencyDetail) {
		this.frequencyDetail = frequencyDetail;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @return the recurring
	 */
	public String getRecurring() {
		return recurring;
	}
	/**
	 *
	 * @Ganesh
	 *
	 * @param recurring the recurring to set
	 */
	public void setRecurring(String recurring) {
		this.recurring = recurring;
	}	
}
