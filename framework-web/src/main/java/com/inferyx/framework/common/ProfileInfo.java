package com.inferyx.framework.common;

import org.springframework.stereotype.Component;

@Component
public class ProfileInfo {
	private String profileTargetUUID;

	public String getProfileTargetUUID() {
		return profileTargetUUID;
	}
	public void setProfileTargetUUID(String profileTargetUUID) {
		this.profileTargetUUID = profileTargetUUID;
	}	
}
