/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

public class DependsOnExp {
	
	
	private MetaIdentifier ref;
	
	private OrderKey condition;


	public MetaIdentifier getRef() {
		return ref;
	}

	public void setRef(MetaIdentifier ref) {
		this.ref = ref;
	}

	public OrderKey getCondition() {
		return condition;
	}

	public void setCondition(OrderKey condition) {
		this.condition = condition;
	}
	
	

}
