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

public class RunStatusHolder {
	
	private Boolean COMPLETED = true;
	private Boolean KILLED = false;
	private Boolean FAILED = false;
	private Boolean PAUSE = false;
	private Boolean RESUME = false;
	private Boolean ABORTED = false;
	
	public RunStatusHolder() {
		super();
	}

	public RunStatusHolder(Boolean COMPLETED, Boolean KILLED, Boolean FAILED, Boolean PAUSE, Boolean RESUME, Boolean ABORTED) {
		super();
		this.COMPLETED = COMPLETED;
		this.KILLED = KILLED;
		this.FAILED = FAILED;
		this.PAUSE = PAUSE;
		this.RESUME = RESUME;
		this.ABORTED = ABORTED;
	}

	/**
	 * @return the COMPLETED
	 */
	public Boolean getCOMPLETED() {
		return COMPLETED;
	}

	/**
	 * @param COMPLETED the COMPLETED to set
	 */
	public void setCompleted(Boolean COMPLETED) {
		this.COMPLETED = COMPLETED;
	}

	/**
	 * @return the KILLED
	 */
	public Boolean getKILLED() {
		return KILLED;
	}

	/**
	 * @param KILLED the KILLED to set
	 */
	public void setKilled(Boolean KILLED) {
		this.KILLED = KILLED;
	}

	/**
	 * @return the FAILED
	 */
	public Boolean getFAILED() {
		return FAILED;
	}

	/**
	 * @param FAILED the FAILED to set
	 */
	public void setFailed(Boolean FAILED) {
		this.FAILED = FAILED;
	}

	/**
	 * @return the PAUSE
	 */
	public Boolean getPAUSE() {
		return PAUSE;
	}

	/**
	 * @param PAUSE the PAUSE to set
	 */
	public void setPause(Boolean PAUSE) {
		this.PAUSE = PAUSE;
	}

	/**
	 * @return the RESUME
	 */
	public Boolean getRESUME() {
		return RESUME;
	}

	/**
	 * @param RESUME the RESUME to set
	 */
	public void setResume(Boolean RESUME) {
		this.RESUME = RESUME;
	}

	/**
	 * @return the ABORTED
	 */
	public Boolean getABORTED() {
		return ABORTED;
	}

	/**
	 * @param ABORTED the ABORTED to set
	 */
	public void setAborted(Boolean ABORTED) {
		this.ABORTED = ABORTED;
	}

}
