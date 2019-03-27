/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.ArrayList;
import java.util.List;

import com.inferyx.framework.enums.RunMode;

/**
 * @author joy
 *
 */
public class BaseExec extends BaseEntity {
	
	protected List<Status> statusList;
	protected MetaIdentifierHolder dependsOn;
	protected String exec;
	protected String summaryExec;
	protected String abortExec;
	protected MetaIdentifierHolder result;
	protected MetaIdentifierHolder summaryResult;
	protected List<MetaIdentifier> refKeyList;
	protected ExecParams execParams;	
//	protected Message messageInfo;
	protected RunMode runMode;

	public RunMode getRunMode() {
		return runMode;
	}

	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	public MetaIdentifierHolder getSummaryResult() {
		return summaryResult;
	}

	public void setSummaryResult(MetaIdentifierHolder summaryResult) {
		this.summaryResult = summaryResult;
	}

	
	/**
	 *
	 * @Ganesh
	 *
	 * @return the messageInfo
	 */
//	public Message getMessageInfo() {
//		return messageInfo;
//	}

	/**
	 *
	 * @Ganesh
	 *
	 * @param messageInfo the messageInfo to set
	 */
//	public void setMessageInfo(Message messageInfo) {
//		this.messageInfo = messageInfo;
//	}

	/**
	 * 
	 */
	public BaseExec() {
		refKeyList = new ArrayList<>();
		statusList = new ArrayList<>();
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
	/**
	 * @return the statusList
	 */
	public List<Status> getStatusList() {
		return statusList;
	}

	/**
	 * @param statusList the statusList to set
	 */
	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}

	/**
	 * @return the dependsOn
	 */
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	/**
	 * @param dependsOn the dependsOn to set
	 */
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	/**
	 * @return the exec
	 */
	public String getExec() {
		return exec;
	}

	/**
	 * @param exec the exec to set
	 */
	public void setExec(String exec) {
		this.exec = exec;
	}

	/**
	 * @return the result
	 */
	public MetaIdentifierHolder getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(MetaIdentifierHolder result) {
		this.result = result;
	}

	/**
	 * @return the refKeyList
	 */
	public List<MetaIdentifier> getRefKeyList() {
		return refKeyList;
	}

	/**
	 * @param refKeyList the refKeyList to set
	 */
	public void setRefKeyList(List<MetaIdentifier> refKeyList) {
		this.refKeyList = refKeyList;
	}


	/**
	 * @return the summaryExec
	 */
	public String getSummaryExec() {
		return summaryExec;
	}

	/**
	 * @param summaryExec the summaryExec to set
	 */
	public void setSummaryExec(String summaryExec) {
		this.summaryExec = summaryExec;
	}

	/**
	 * @return the abortExec
	 */
	public String getAbortExec() {
		return abortExec;
	}

	/**
	 * @param abortExec the abortExec to set
	 */
	public void setAbortExec(String abortExec) {
		this.abortExec = abortExec;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseExec [statusList=" + statusList + ", dependsOn=" + dependsOn + ", exec=" + exec + ", summaryExec="
				+ summaryExec + ", abortExec=" + abortExec + ", result=" + result + ", summaryResult=" + summaryResult
				+ ", refKeyList=" + refKeyList + ", execParams=" + execParams + "]";
	}
	
}
