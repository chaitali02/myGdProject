package com.inferyx.framework.domain2;

public enum Stages {
	PENDING("PENDING"), RUNNING("RUNNING"), COMPLETED("COMPLETED"), FAILED("FAILED"), Suspend(
			"Suspend"), KILLED("KILLED"), login("login"), logout("logout"), expired("expired"), active(
					"active"), Inactive("inactive"), PAUSE(
							"PAUSE"), OffHold("OffHold"), RESUME("RESUME"), TERMINATING("TERMINATING");
	private String displayName;

	public String displayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

	Stages(String displayName) {
		this.displayName = displayName;
	}
}
