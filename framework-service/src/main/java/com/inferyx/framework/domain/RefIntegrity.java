/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

/**
 * @author Ganesh
 *
 */
public class RefIntegrity {
	private MetaIdentifierHolder dependsOn;
	private AttributeRefHolder targetAttr;

	public RefIntegrity() {
		// TODO Auto-generated constructor stub
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
	 * @return the targetAttr
	 */
	public AttributeRefHolder getTargetAttr() {
		return targetAttr;
	}

	/**
	 * @Ganesh
	 *
	 * @param targetAttr the targetAttr to set
	 */
	public void setTargetAttr(AttributeRefHolder targetAttr) {
		this.targetAttr = targetAttr;
	}
}
