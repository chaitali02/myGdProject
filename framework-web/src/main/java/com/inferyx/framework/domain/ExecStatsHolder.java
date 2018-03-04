package com.inferyx.framework.domain;

public class ExecStatsHolder extends MetaIdentifierHolder {
	
	Long numRows;
    String persistMode;
    String runMode; 
	public Long getNumRows() {
		return numRows;
	}

	public void setNumRows(Long numRows) {
		this.numRows = numRows;
	}

	/**
	 * @return the persistMode
	 */
	public String getPersistMode() {
		return persistMode;
	}

	/**
	 * @param persistMode the persistMode to set
	 */
	public void setPersistMode(String persistMode) {
		this.persistMode = persistMode;
	}

	/**
	 * @return the runMode
	 */
	public String getRunMode() {
		return runMode;
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}

	
	

}
