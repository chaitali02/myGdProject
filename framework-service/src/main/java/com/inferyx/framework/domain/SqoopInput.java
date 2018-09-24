package com.inferyx.framework.domain;

import com.cloudera.sqoop.SqoopOptions.FileLayout;
import com.inferyx.framework.enums.SqoopIncrementalMode;

public class SqoopInput {
	
	private String table;
	private Datasource sourceDs;
	private Datasource targetDs;
	private String sourceDirectory;
	private String targetDirectory;
	private boolean hiveImport;
	private String partitionKey;
	private String partitionValue;
	private String whereClause;
	private String compressionCodec;
	private String checkColumn;
    private String lastVale;
    private boolean appendMode;
    private SqoopIncrementalMode incrementalMode;
    private String incrementalTestColumn;
    private String incrementalLastValue;
    private String exportDir;
	private int numMappers;
	private char linesTerminatedBy;
	private char fieldsTerminatedBy;
	private boolean explicitInputDelims;
	private boolean  explicitOutputDelims;
	private boolean importIntended;	// True - Sqoop Import; False - Sqoop Export
	private String hiveTableName;
	private String hCatTableName;
	private FileLayout fileLayout;
	private String overwriteHiveTable; 
	private String hiveDatabaseName;
	
	/**
	 *
	 * @Ganesh
	 *
	 * @return the hiveDatabaseName
	 */
	public String getHiveDatabaseName() {
		return hiveDatabaseName;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param hiveDatabaseName the hiveDatabaseName to set
	 */
	public void setHiveDatabaseName(String hiveDatabaseName) {
		this.hiveDatabaseName = hiveDatabaseName;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the overwriteHiveTable
	 */
	public String getOverwriteHiveTable() {
		return overwriteHiveTable;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param overwriteHiveTable the overwriteHiveTable to set
	 */
	public void setOverwriteHiveTable(String overwriteHiveTable) {
		this.overwriteHiveTable = overwriteHiveTable;
	}

	public SqoopInput() {
		// TODO Auto-generated constructor stub
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the fileLayout
	 */
	public FileLayout getFileLayout() {
		return fileLayout;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param fileLayout the fileLayout to set
	 */
	public void setFileLayout(FileLayout fileLayout) {
		this.fileLayout = fileLayout;
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
	public boolean getHiveImport() {
		return hiveImport;
	}

	/**
	 * @param hiveImport the hiveImport to set
	 */
	public void setHiveImport(boolean hiveImport) {
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
	public boolean getAppendMode() {
		return appendMode;
	}

	/**
	 * @param appendMode the appendMode to set
	 */
	public void setAppendMode(boolean appendMode) {
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

	/**
	 * @return the exportDir
	 */
	public String getExportDir() {
		return exportDir;
	}

	/**
	 * @param exportDir the exportDir to set
	 */
	public void setExportDir(String exportDir) {
		this.exportDir = exportDir;
	}

	/**
	 * @return the numMappers
	 */
	public int getNumMappers() {
		return numMappers;
	}

	/**
	 * @param numMappers the numMappers to set
	 */
	public void setNumMappers(int numMappers) {
		this.numMappers = numMappers;
	}

	/**
	 * @return the linesTerminatedBy
	 */
	public char getLinesTerminatedBy() {
		return linesTerminatedBy;
	}

	/**
	 * @param linesTerminatedBy the linesTerminatedBy to set
	 */
	public void setLinesTerminatedBy(char linesTerminatedBy) {
		this.linesTerminatedBy = linesTerminatedBy;
	}

	/**
	 * @return the fieldsTerminatedBy
	 */
	public char getFieldsTerminatedBy() {
		return fieldsTerminatedBy;
	}

	/**
	 * @param fieldsTerminatedBy the fieldsTerminatedBy to set
	 */
	public void setFieldsTerminatedBy(char fieldsTerminatedBy) {
		this.fieldsTerminatedBy = fieldsTerminatedBy;
	}

	/**
	 * @return the explicitInputDelims
	 */
	public boolean getExplicitInputDelims() {
		return explicitInputDelims;
	}

	/**
	 * @param explicitInputDelims the explicitInputDelims to set
	 */
	public void setExplicitInputDelims(boolean explicitInputDelims) {
		this.explicitInputDelims = explicitInputDelims;
	}

	/**
	 * @return the explicitOutputDelims
	 */
	public boolean getExplicitOutputDelims() {
		return explicitOutputDelims;
	}

	/**
	 * @param explicitOutputDelims the explicitOutputDelims to set
	 */
	public void setExplicitOutputDelims(boolean explicitOutputDelims) {
		this.explicitOutputDelims = explicitOutputDelims;
	}

	/**
	 * @return the importIntended
	 */
	public boolean getImportIntended() {
		return importIntended;
	}

	/**
	 * @param importIntended the importIntended to set
	 */
	public void setImportIntended(boolean importIntended) {
		this.importIntended = importIntended;
	}

	/**
	 * @return the hiveTableName
	 */
	public String getHiveTableName() {
		return hiveTableName;
	}

	/**
	 * @param hiveTableName the hiveTableName to set
	 */
	public void setHiveTableName(String hiveTableName) {
		this.hiveTableName = hiveTableName;
	}

	/**
	 * @return the hCatTableName
	 */
	public String gethCatTableName() {
		return hCatTableName;
	}

	/**
	 * @param hCatTableName the hCatTableName to set
	 */
	public void sethCatTableName(String hCatTableName) {
		this.hCatTableName = hCatTableName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SqoopInput [table=" + table + ", sourceDs=" + sourceDs + ", targetDs=" + targetDs + ", sourceDirectory="
				+ sourceDirectory + ", targetDirectory=" + targetDirectory + ", hiveImport=" + hiveImport
				+ ", partitionKey=" + partitionKey + ", partitionValue=" + partitionValue + ", whereClause="
				+ whereClause + ", compressionCodec=" + compressionCodec + ", checkColumn=" + checkColumn
				+ ", lastVale=" + lastVale + ", appendMode=" + appendMode + ", incrementalMode=" + incrementalMode
				+ ", incrementalTestColumn=" + incrementalTestColumn + ", incrementalLastValue=" + incrementalLastValue
				+ ", exportDir=" + exportDir + ", numMappers=" + numMappers + ", linesTerminatedBy=" + linesTerminatedBy
				+ ", fieldsTerminatedBy=" + fieldsTerminatedBy + ", explicitInputDelims=" + explicitInputDelims
				+ ", explicitOutputDelims=" + explicitOutputDelims + ", importIntended=" + importIntended
				+ ", hiveTableName=" + hiveTableName + ", hCatTableName=" + hCatTableName + "]";
	}
	
}
