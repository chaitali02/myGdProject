/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ganesh
 *
 */
@Document(collection = "operatorexec")
public class OperatorExec extends BaseEntity {
	private MetaIdentifierHolder dependsOn;
	private List<Status> statusList;
	private MetaIdentifierHolder result; // Datastore info

	/**
	 * @Ganesh
	 *
	 * @return the dependsOn
	 */
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	/**
	 * @Ganesh
	 *
	 * @param dependsOn the dependsOn to set
	 */
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	/**
	 * @Ganesh
	 *
	 * @return the statusList
	 */
	public List<Status> getStatusList() {
		return statusList;
	}

	/**
	 * @Ganesh
	 *
	 * @param statusList the statusList to set
	 */
	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}

	/**
	 * @Ganesh
	 *
	 * @return the result
	 */
	public MetaIdentifierHolder getResult() {
		return result;
	}

	/**
	 * @Ganesh
	 *
	 * @param result the result to set
	 */
	public void setResult(MetaIdentifierHolder result) {
		this.result = result;
	}
}
