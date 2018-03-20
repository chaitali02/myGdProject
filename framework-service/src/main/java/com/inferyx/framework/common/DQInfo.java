package com.inferyx.framework.common;

import org.springframework.stereotype.Component;

@Component
public class DQInfo {

	private String dqTargetUUID;

	public String getDqTargetUUID() {
		return dqTargetUUID;
	}

	public void setDqTargetUUID(String dqTargetUUID) {
		this.dqTargetUUID = dqTargetUUID;
	}

}
