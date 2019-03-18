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

public class Criteria {

	private String criteriaId;
	private String criteriaName;
	private String activeFlag;
	private List<FilterInfo> criteriaFilter;
	private int score=1;
	private String filter_critria;
	private double criteriaWeight;
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public String getFilter_critria() {
		return filter_critria;
	}

	public void setFilter_critria(String filter_critria) {
		this.filter_critria = filter_critria;
	}

	public double getCriteriaWeight() {
		return criteriaWeight;
	}

	public void setCriteriaWeight(double criteriaWeight) {
		this.criteriaWeight = criteriaWeight;
	}

	public String getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(String criteriaId) {
		this.criteriaId = criteriaId;
	}

	public String getCriteriaName() {
		return criteriaName;
	}

	public void setCriteriaName(String criteriaName) {
		this.criteriaName = criteriaName;
	}

	public String getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}

	public List<FilterInfo> getCriteriaFilter() {
		return criteriaFilter;
	}

	public void setCriteriaFilter(List<FilterInfo> criteriaFilter) {
		this.criteriaFilter = criteriaFilter;
	}

	

	
}