package com.inferyx.framework.domain2;

public enum Stages {
	NotStarted("Not Started"), InProgress("In Progress"), Completed("Completed"), Failed("Failed"), Suspend(
			"Suspend"), Killed("Killed"), login("login"), logout("logout"), expired("expired"), active(
					"active"), Inactive("inactive"), OnHold(
							"OnHold"), OffHold("OffHold"), Resume("Resume"), Terminating("Terminating");
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
