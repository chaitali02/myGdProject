/**
 *
 * @Author Ganesh
 *
 */
package com.inferyx.framework.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ganesh
 *
 */
@Document(collection = "predictexec")
public class PredictExec extends BaseEntity {
	private MetaIdentifierHolder dependsOn;
	private String location;
	private MetaIdentifierHolder result; // Datastore info
	private List<Status> statusList;

	
	public List<Status> getStatusList() {
		return statusList;
	}
	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}
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
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @Ganesh
	 *
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	public MetaIdentifierHolder getResult() {
		return result;
	}
	public void setResult(MetaIdentifierHolder result) {
		this.result = result;
	}
}
