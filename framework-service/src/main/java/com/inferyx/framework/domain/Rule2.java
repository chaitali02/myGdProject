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

import com.inferyx.framework.enums.ScoringMethod;
import com.inferyx.framework.enums.ScoringMethod;

@Document(collection = "rule2")
public class Rule2 extends BaseRule {

	private MetaIdentifierHolder sourceInfo;
	private List<FilterInfo> filterInfo;
	private String entityType;
	private ScoringMethod  scoringMethod;
	private AttributeRefHolder entityId;
	private List<Criteria> criteriaInfo;
	private MetaIdentifierHolder paramList;


	
	public ScoringMethod getScoringMethod() {
		return scoringMethod;
	}

	public void setScoringMethod(ScoringMethod scoringMethod) {
		this.scoringMethod = scoringMethod;
	}
	
	public MetaIdentifierHolder getSourceInfo() {
		return sourceInfo;
	}

	public void setSourceInfo(MetaIdentifierHolder sourceInfo) {
		this.sourceInfo = sourceInfo;
	}

	public List<FilterInfo> getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(List<FilterInfo> filterInfo) {
		this.filterInfo = filterInfo;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public AttributeRefHolder getEntityId() {
		return entityId;
	}

	public void setEntityId(AttributeRefHolder entityId) {
		this.entityId = entityId;
	}

	public List<Criteria> getCriteriaInfo() {
		return criteriaInfo;
	}

	public void setCriteriaInfo(List<Criteria> criteriaInfo) {
		this.criteriaInfo = criteriaInfo;
	}
	
	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}
	
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}

}
