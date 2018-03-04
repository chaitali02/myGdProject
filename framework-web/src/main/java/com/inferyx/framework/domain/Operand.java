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

public class Operand {

private MetaIdentifier ref;
	
	private Attribute attributeId;
	
	private OrderKey condition;

	private Value value; 
	
	private OperatorNew operand;
	
	private LogicalOperand logicaloperand;
	
	private String operator;

	public OrderKey getCondition() {
		return condition;
	}
	public void setCondition(OrderKey condition) {
		this.condition = condition;
	}
		
	public Attribute getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(Attribute attributeId) {
		this.attributeId = attributeId;
	}
	public MetaIdentifier getRef() {
		return ref;
	}
	public void setMeta(MetaIdentifier ref) {
		this.ref = ref;
	}
	public Value getValue() {
		return value;
	}
	public void setValue(Value value) {
		this.value = value;
	}
	public OperatorNew getOperand() {
		return operand;
	}
	public void setOperand(OperatorNew operand) {
		this.operand = operand;
	}
	public LogicalOperand getLogicaloperand() {
		return logicaloperand;
	}
	public void setLogicaloperand(LogicalOperand logicaloperand) {
		this.logicaloperand = logicaloperand;

	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	
	
}
