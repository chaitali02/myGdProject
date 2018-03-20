package com.inferyx.framework.domain;

public class DatapodStatsHolder extends MetaIdentifierHolder {
	
	Long numRows;
	String lastUpdatedOn;
	String dataSource;
	
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public Long getNumRows() {
		return numRows;
	}

	public void setNumRows(Long numRows) {
		this.numRows = numRows;
	}

	public String getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(String lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}
	
	
}
