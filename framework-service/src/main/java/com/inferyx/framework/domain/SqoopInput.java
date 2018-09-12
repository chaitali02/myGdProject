package com.inferyx.framework.domain;

import com.inferyx.framework.enums.SqoopIncrementalMode;

public class SqoopInput {
	
	private String table;
	private Datasource sourceDs;
	private Datasource targetDs;
	private String sourceDirectory;
	private String targetDirectory;
	private Boolean hiveImport;
	private String partitionKey;
	private String partitionValue;
	private String whereClause;
	private String compressionCodec;
	private String checkColumn;
    private String lastVale;
    private Boolean appendMode;
    private SqoopIncrementalMode incrementalMode;
    private String incrementalTestColumn;
    private String incrementalLastValue;

	public SqoopInput() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * @return the sourceDs
	 */
	public Datasource getSourceDs() {
		return sourceDs;
	}

	/**
	 * @param sourceDs the sourceDs to set
	 */
	public void setSourceDs(Datasource sourceDs) {
		this.sourceDs = sourceDs;
	}

	/**
	 * @return the targetDs
	 */
	public Datasource getTargetDs() {
		return targetDs;
	}

	/**
	 * @param targetDs the targetDs to set
	 */
	public void setTargetDs(Datasource targetDs) {
		this.targetDs = targetDs;
	}

	/**
	 * @return the sourceDirectory
	 */
	public String getSourceDirectory() {
		return sourceDirectory;
	}

	/**
	 * @param sourceDirectory the sourceDirectory to set
	 */
	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	/**
	 * @return the targetDirectory
	 */
	public String getTargetDirectory() {
		return targetDirectory;
	}

	/**
	 * @param targetDirectory the targetDirectory to set
	 */
	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	/**
	 * @return the hiveImport
	 */
	public Boolean getHiveImport() {
		return hiveImport;
	}

	/**
	 * @param hiveImport the hiveImport to set
	 */
	public void setHiveImport(Boolean hiveImport) {
		this.hiveImport = hiveImport;
	}

	/**
	 * @return the partitionKey
	 */
	public String getPartitionKey() {
		return partitionKey;
	}

	/**
	 * @param partitionKey the partitionKey to set
	 */
	public void setPartitionKey(String partitionKey) {
		this.partitionKey = partitionKey;
	}

	/**
	 * @return the partitionValue
	 */
	public String getPartitionValue() {
		return partitionValue;
	}

	/**
	 * @param partitionValue the partitionValue to set
	 */
	public void setPartitionValue(String partitionValue) {
		this.partitionValue = partitionValue;
	}

	/**
	 * @return the whereClause
	 */
	public String getWhereClause() {
		return whereClause;
	}

	/**
	 * @param whereClause the whereClause to set
	 */
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	/**
	 * @return the compressionCodec
	 */
	public String getCompressionCodec() {
		return compressionCodec;
	}

	/**
	 * @param compressionCodec the compressionCodec to set
	 */
	public void setCompressionCodec(String compressionCodec) {
		this.compressionCodec = compressionCodec;
	}

	/**
	 * @return the checkColumn
	 */
	public String getCheckColumn() {
		return checkColumn;
	}

	/**
	 * @param checkColumn the checkColumn to set
	 */
	public void setCheckColumn(String checkColumn) {
		this.checkColumn = checkColumn;
	}

	/**
	 * @return the lastVale
	 */
	public String getLastVale() {
		return lastVale;
	}

	/**
	 * @param lastVale the lastVale to set
	 */
	public void setLastVale(String lastVale) {
		this.lastVale = lastVale;
	}

	/**
	 * @return the appendMode
	 */
	public Boolean getAppendMode() {
		return appendMode;
	}

	/**
	 * @param appendMode the appendMode to set
	 */
	public void setAppendMode(Boolean appendMode) {
		this.appendMode = appendMode;
	}

	/**
	 * @return the incrementalMode
	 */
	public SqoopIncrementalMode getIncrementalMode() {
		return incrementalMode;
	}

	/**
	 * @param incrementalMode the incrementalMode to set
	 */
	public void setIncrementalMode(SqoopIncrementalMode incrementalMode) {
		this.incrementalMode = incrementalMode;
	}

	/**
	 * @return the incrementalTestColumn
	 */
	public String getIncrementalTestColumn() {
		return incrementalTestColumn;
	}

	/**
	 * @param incrementalTestColumn the incrementalTestColumn to set
	 */
	public void setIncrementalTestColumn(String incrementalTestColumn) {
		this.incrementalTestColumn = incrementalTestColumn;
	}

	/**
	 * @return the incrementalLastValue
	 */
	public String getIncrementalLastValue() {
		return incrementalLastValue;
	}

	/**
	 * @param incrementalLastValue the incrementalLastValue to set
	 */
	public void setIncrementalLastValue(String incrementalLastValue) {
		this.incrementalLastValue = incrementalLastValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SqoopInput [table=" + table + ", sourceDs=" + sourceDs + ", targetDs=" + targetDs + ", sourceDirectory="
				+ sourceDirectory + ", targetDirectory=" + targetDirectory + ", hiveImport=" + hiveImport
				+ ", partitionKey=" + partitionKey + ", partitionValue=" + partitionValue + ", whereClause="
				+ whereClause + ", compressionCodec=" + compressionCodec + ", checkColumn="
				+ checkColumn + ", lastVale=" + lastVale + ", appendMode=" + appendMode + ", incrementalMode="
				+ incrementalMode + ", incrementalTestColumn=" + incrementalTestColumn + ", incrementalLastValue="
				+ incrementalLastValue + "]";
	}
	
}
