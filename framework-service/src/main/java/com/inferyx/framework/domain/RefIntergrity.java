package com.inferyx.framework.domain;

public class RefIntergrity {
	
	private MetaIdentifierHolder dependsOn;
	private AttributeRefHolder targetAttr;

	public RefIntergrity() {
		// TODO Auto-generated constructor stub
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
	 * @return the targetAttr
	 */
	public AttributeRefHolder getTargetAttr() {
		return targetAttr;
	}

	/**
	 * @param targetAttr the targetAttr to set
	 */
	public void setTargetAttr(AttributeRefHolder targetAttr) {
		this.targetAttr = targetAttr;
	}

}
