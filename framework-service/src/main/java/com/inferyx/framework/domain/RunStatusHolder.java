package com.inferyx.framework.domain;

public class RunStatusHolder {
	
	private Boolean completed = true;
	private Boolean killed = false;
	private Boolean failed = false;
	private Boolean onHold = false;
	private Boolean resume = false;
	
	public RunStatusHolder() {
		super();
	}

	public RunStatusHolder(Boolean completed, Boolean killed, Boolean failed, Boolean onHold, Boolean resume) {
		super();
		this.completed = completed;
		this.killed = killed;
		this.failed = failed;
		this.onHold = onHold;
		this.resume = resume;
	}

	/**
	 * @return the completed
	 */
	public Boolean getCompleted() {
		return completed;
	}

	/**
	 * @param completed the completed to set
	 */
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	/**
	 * @return the killed
	 */
	public Boolean getKilled() {
		return killed;
	}

	/**
	 * @param killed the killed to set
	 */
	public void setKilled(Boolean killed) {
		this.killed = killed;
	}

	/**
	 * @return the failed
	 */
	public Boolean getFailed() {
		return failed;
	}

	/**
	 * @param failed the failed to set
	 */
	public void setFailed(Boolean failed) {
		this.failed = failed;
	}

	/**
	 * @return the onHold
	 */
	public Boolean getOnHold() {
		return onHold;
	}

	/**
	 * @param onHold the onHold to set
	 */
	public void setOnHold(Boolean onHold) {
		this.onHold = onHold;
	}

	/**
	 * @return the resume
	 */
	public Boolean getResume() {
		return resume;
	}

	/**
	 * @param resume the resume to set
	 */
	public void setResume(Boolean resume) {
		this.resume = resume;
	}

}
