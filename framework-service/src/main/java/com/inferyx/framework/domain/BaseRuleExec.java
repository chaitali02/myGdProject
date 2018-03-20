/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.List;

/**
 * @author joy
 *
 */
public class BaseRuleExec extends BaseEntity {
	
	private List<Status> statusList;
	private MetaIdentifierHolder dependsOn;
	private String exec;
	private MetaIdentifierHolder result;
	private List<MetaIdentifier> refKeyList;

	/**
	 * 
	 */
	public BaseRuleExec() {
	}

	/**
	 * @return the status
	 */
	public List<Status> getStatusList() {
		return statusList;
	}

	/**
	 * @param status the status to set
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseRuleExec [statusList=" + statusList + ", dependsOn=" + dependsOn + ", exec=" + exec + ", result=" + result
				+ ", refKeyList=" + refKeyList + "]";
	}

}
