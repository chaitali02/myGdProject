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

import com.inferyx.framework.enums.CheckType;

/**
 * @author Ganesh
 *
 */
public class DQIntelligence {
	private AttributeRefHolder attributeName;
	private CheckType checkType;
	private MetaIdentifierHolder checkValue;
	private boolean isCreated;
	private double sampleScore;

	/**
	 * @Ganesh
	 *
	 * @return the attributeName
	 */
	public AttributeRefHolder getAttributeName() {
		return attributeName;
	}

	/**
	 * @Ganesh
	 *
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(AttributeRefHolder attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @Ganesh
	 *
	 * @return the checkType
	 */
	public CheckType getCheckType() {
		return checkType;
	}

	/**
	 * @Ganesh
	 *
	 * @param checkType the checkType to set
	 */
	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}

	/**
	 * @Ganesh
	 *
	 * @return the checkValue
	 */
	public MetaIdentifierHolder getCheckValue() {
		return checkValue;
	}

	/**
	 * @Ganesh
	 *
	 * @param checkValue the checkValue to set
	 */
	public void setCheckValue(MetaIdentifierHolder checkValue) {
		this.checkValue = checkValue;
	}

	/**
	 * @Ganesh
	 *
	 * @return the isCreated
	 */
	public boolean isCreated() {
		return isCreated;
	}

	/**
	 * @Ganesh
	 *
	 * @param isCreated the isCreated to set
	 */
	public void setCreated(boolean isCreated) {
		this.isCreated = isCreated;
	}

	/**
	 * @Ganesh
	 *
	 * @return the sampleScore
	 */
	public double getSampleScore() {
		return sampleScore;
	}

	/**
	 * @Ganesh
	 *
	 * @param sampleScore the sampleScore to set
	 */
	public void setSampleScore(double sampleScore) {
		this.sampleScore = sampleScore;
	}
}
