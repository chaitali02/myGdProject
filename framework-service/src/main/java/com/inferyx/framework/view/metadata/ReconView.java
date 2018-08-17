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
package com.inferyx.framework.view.metadata;


import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifierHolder;

public class ReconView extends BaseEntity {
	
	private AttributeRefHolder sourceAttr;
	
	private MetaIdentifierHolder sourceFunc;
	
	private AttributeRefHolder targetAttr;
	
	private MetaIdentifierHolder targetFunc;
	
	
	private Filter sourcefilter;

	private Filter targetfilter;
	
	private String sourcefilterChg;
	
	private String targetfilterChg;
	
	private String sourceDistinct="N";
	private String targetDistinct="N";

	
	
	public String getSourceDistinct() {
		return sourceDistinct;
	}

	public void setSourceDistinct(String sourceDistinct) {
		this.sourceDistinct = sourceDistinct;
	}

	public String getTargetDistinct() {
		return targetDistinct;
	}

	public void setTargetDistinct(String targetDistinct) {
		this.targetDistinct = targetDistinct;
	}

	
	
	public AttributeRefHolder getSourceAttr() {
		return sourceAttr;
	}

	public void setSourceAttr(AttributeRefHolder sourceAttr) {
		this.sourceAttr = sourceAttr;
	}


	public MetaIdentifierHolder getSourceFunc() {
		return sourceFunc;
	}

	public void setSourceFunc(MetaIdentifierHolder sourceFunc) {
		this.sourceFunc = sourceFunc;
	}

	public AttributeRefHolder getTargetAttr() {
		return targetAttr;
	}

	public void setTargetAttr(AttributeRefHolder targetAttr) {
		this.targetAttr = targetAttr;
	}

	public MetaIdentifierHolder getTargetFunc() {
		return targetFunc;
	}

	public void setTargetFunc(MetaIdentifierHolder targetFunc) {
		this.targetFunc = targetFunc;
	}

	public Filter getSourcefilter() {
		return sourcefilter;
	}

	public void setSourcefilter(Filter sourcefilter) {
		this.sourcefilter = sourcefilter;
	}

	public Filter getTargetfilter() {
		return targetfilter;
	}

	public void setTargetfilter(Filter targetfilter) {
		this.targetfilter = targetfilter;
	}

	public String getSourcefilterChg() {
		return sourcefilterChg;
	}

	public void setSourcefilterChg(String sourcefilterChg) {
		this.sourcefilterChg = sourcefilterChg;
	}

	public String getTargetfilterChg() {
		return targetfilterChg;
	}

	public void setTargetfilterChg(String targetfilterChg) {
		this.targetfilterChg = targetfilterChg;
	}


	
}
