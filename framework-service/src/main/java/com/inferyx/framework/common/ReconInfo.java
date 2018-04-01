/**
 *
 * @Author Ganesh
 *
 */
package com.inferyx.framework.common;

import org.springframework.stereotype.Component;

/**
 * @author Ganesh
 *
 */
@Component
public class ReconInfo {
	private String reconTargetUUID;

	/**
	 * @Ganesh
	 *
	 * @return the reconTargetUUID
	 */
	public String getReconTargetUUID() {
		return reconTargetUUID;
	}

	/**
	 * @Ganesh
	 *
	 * @param reconTargetUUID the reconTargetUUID to set
	 */
	public void setReconTargetUUID(String reconTargetUUID) {
		this.reconTargetUUID = reconTargetUUID;
	}

}
