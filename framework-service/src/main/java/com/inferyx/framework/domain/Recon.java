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

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "recon")
public class Recon extends BaseRule {

	private AttributeRefHolder sourceAttr;
	private MetaIdentifierHolder sourceFunc;
	private AttributeRefHolder targetAttr;
	private MetaIdentifierHolder targetFunc;
	private List<AttributeRefHolder> sourceFilter;
	private List<AttributeRefHolder> targetFilter;
	private String sourceDistinct = "N";
	private String targetDistinct = "N";
	private List<AttributeRefHolder> sourceGroup;
	private List<AttributeRefHolder> targetGroup;

	public List<AttributeRefHolder> getSourceGroup() {
		return this.sourceGroup;
	}

	public void setSourceGroup(List<AttributeRefHolder> sourceGroup) {
		this.sourceGroup = sourceGroup;
	}

	public List<AttributeRefHolder> getTargetGroup() {
		return this.targetGroup;
	}

	public void setTargetGroup(List<AttributeRefHolder> targetGroup) {
		this.targetGroup = targetGroup;
	}

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

	public List<AttributeRefHolder> getSourceFilter() {
		return sourceFilter;
	}

	public void setSourceFilter(List<AttributeRefHolder> sourceFilter) {
		this.sourceFilter = sourceFilter;
	}

	public List<AttributeRefHolder> getTargetFilter() {
		return targetFilter;
	}

	public void setTargetFilter(List<AttributeRefHolder> targetFilter) {
		this.targetFilter = targetFilter;
	}

}
