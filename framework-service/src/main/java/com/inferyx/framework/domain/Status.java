/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

import java.util.Date;

public class Status implements Comparable<Status>{

  public enum Stage {
		PENDING("PENDING"),
		STARTING("STARTING"),
		READY("READY"),
		RUNNING("RUNNING"),
		ABORTED("ABORTED"), 
		COMPLETED("COMPLETED"), 
		FAILED("FAILED"), 
		Suspend("Suspend"),
		KILLED("KILLED"),
		login("login"),
		logout("logout"),
		expired("expired"),
		active("active"), 
		Inactive("inactive"),
		PAUSE("PAUSE"),
		OffHold("OffHold"),
		RESUME("RESUME"),
		TERMINATING("TERMINATING"),
		STARTED("started"),
		STOPPED("stopped");
	  
	  
	private String displayName;
	
	public String displayName() {
		return displayName;
	}
	
	@Override
	public String toString(){
		return displayName;
	}

	Stage(String displayName){
		  this.displayName = displayName;
	  }
	}
	
	private Stage stage;
	private Date createdOn;
	
	public Status() {
		
	}
	
	public Status(Stage stage, Date createdOn) {
		super();
		this.stage = stage;
		this.createdOn = createdOn;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stage == null) ? 0 : stage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Status other = (Status) obj;
		if (stage != other.stage)
			return false;
		return true;
	}

	@Override
	public int compareTo(Status o) {
		// TODO Auto-generated method stub

		if(this.createdOn.after(o.createdOn)){
			return -1;
		} else if (this.createdOn.before(o.createdOn)){
			return 1;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Status [stage=" + stage + ", createdOn=" + createdOn + "]";
	}
	
}
