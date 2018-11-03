/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.Map;
import java.util.Properties;

import com.inferyx.framework.connector.IConnector;

/**
 * @author joy
 *
 */
public class StreamInput<T, K> {

	/**
	 * 
	 */
	public StreamInput() {
		// TODO Auto-generated constructor stub
	}
	
	private Map<String, Object> runParams;
	private Properties props;
	private Map<String, Object> sourceExtraParams;
	private Map<String, Object> targetExtraParams;
	private String targetTableName;
	private String sourceTableName;
	private String saveMode;
	private String targetType;
	private Datasource sourceDS;
	private Datasource targetDS;
	private Datapod sourceDP;
	private Datapod targetDP;
	private IConnector connector;
	private String topicName;
	private String sourceDir;
	private String targetDir;
	private String fileFormat;
	private String ingestionType;
	private ExecParams execParams;

	/**
	 *
	 * @Ganesh
	 *
	 * @return the ingestionType
	 */
	public String getIngestionType() {
		return ingestionType;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param ingestionType the ingestionType to set
	 */
	public void setIngestionType(String ingestionType) {
		this.ingestionType = ingestionType;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the fileFormat
	 */
	public String getFileFormat() {
		return fileFormat;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param fileFormat the fileFormat to set
	 */
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceDir
	 */
	public String getSourceDir() {
		return sourceDir;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceDir the sourceDir to set
	 */
	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetDir
	 */
	public String getTargetDir() {
		return targetDir;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param targetDir the targetDir to set
	 */
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the topicName
	 */
	public String getTopicName() {
		return topicName;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param topicName the topicName to set
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceExtraParams
	 */
	public Map<String, Object> getSourceExtraParams() {
		return sourceExtraParams;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceExtraParams the sourceExtraParams to set
	 */
	public void setSourceExtraParams(Map<String, Object> sourceExtraParams) {
		this.sourceExtraParams = sourceExtraParams;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetExtraParams
	 */
	public Map<String, Object> getTargetExtraParams() {
		return targetExtraParams;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param targetExtraParams the targetExtraParams to set
	 */
	public void setTargetExtraParams(Map<String, Object> targetExtraParams) {
		this.targetExtraParams = targetExtraParams;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetTableName
	 */
	public String getTargetTableName() {
		return targetTableName;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param targetTableName the targetTableName to set
	 */
	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceTableName
	 */
	public String getSourceTableName() {
		return sourceTableName;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceTableName the sourceTableName to set
	 */
	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the saveMode
	 */
	public String getSaveMode() {
		return saveMode;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param saveMode the saveMode to set
	 */
	public void setSaveMode(String saveMode) {
		this.saveMode = saveMode;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetType
	 */
	public String getTargetType() {
		return targetType;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param targetType the targetType to set
	 */
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceDS
	 */
	public Datasource getSourceDS() {
		return sourceDS;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceDS the sourceDS to set
	 */
	public void setSourceDS(Datasource sourceDS) {
		this.sourceDS = sourceDS;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetDS
	 */
	public Datasource getTargetDS() {
		return targetDS;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param targetDS the targetDS to set
	 */
	public void setTargetDS(Datasource targetDS) {
		this.targetDS = targetDS;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the sourceDP
	 */
	public Datapod getSourceDP() {
		return sourceDP;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param sourceDP the sourceDP to set
	 */
	public void setSourceDP(Datapod sourceDP) {
		this.sourceDP = sourceDP;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the targetDP
	 */
	public Datapod getTargetDP() {
		return targetDP;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param targetDP the targetDP to set
	 */
	public void setTargetDP(Datapod targetDP) {
		this.targetDP = targetDP;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @return the connector
	 */
	public IConnector getConnector() {
		return connector;
	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param connector the connector to set
	 */
	public void setConnector(IConnector connector) {
		this.connector = connector;
	}

	/**
	 * @return the props
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}

	/**
	 * @return the runParams
	 */
	public Map<String, Object> getRunParams() {
		return runParams;
	}

	/**
	 * @param runParams the runParams to set
	 */
	public void setRunParams(Map<String, Object> runParams) {
		this.runParams = runParams;
	}

	/**
	 * @return the execParams
	 */
	public ExecParams getExecParams() {
		return execParams;
	}

	/**
	 * @param execParams the execParams to set
	 */
	public void setExecParams(ExecParams execParams) {
		this.execParams = execParams;
	}
	

}
