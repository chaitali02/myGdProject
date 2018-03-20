/**
 *
 * @Author Ganesh
 *
 */
package com.inferyx.framework.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

/**
 * @author Ganesh
 *
 */
@Component
@Document(collection = "predictexec")
public class PredictExec extends BaseEntity {
	private MetaIdentifierHolder dependsOn;
	private String location;
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
}
