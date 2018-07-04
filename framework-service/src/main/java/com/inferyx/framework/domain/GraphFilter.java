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

/**
 * @author vaibhav
 *
 */
public class GraphFilter {

	List<NodeFilter> nodeFilter;
	List<EdgeFilter> edgeFilter;
	/**
	 * @return
	 */
	public List<NodeFilter> getNodeFilter() {
		return this.nodeFilter;
	}

	/**
	 * @param nodeFilter
	 */
	public void setNodeFilter(List<NodeFilter> nodeFilter) {
		this.nodeFilter = nodeFilter;
	}

	/**
	 * @return
	 */
	public List<EdgeFilter> getEdgeFilter() {
		return this.edgeFilter;
	}

	/**
	 * @param edgeFilter
	 */
	public void setEdgeFilter(List<EdgeFilter> edgeFilter) {
		this.edgeFilter = edgeFilter;
	}

	
	public class NodeFilter {
		
		private String logicalOperator;
		private String operator;
		private Property operand;

		public String getLogicalOperator() {
			return logicalOperator;
		}

		public void setLogicalOperator(String logicalOperator) {
			this.logicalOperator = logicalOperator;
		}

		public Property getOperand() {
			return this.operand;
		}

		public void setOperand(Property operand) {
			this.operand = operand;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}
	}


	public class EdgeFilter {
		
		private String logicalOperator;
		private String operator;
		private Property operand;

		public String getLogicalOperator() {
			return logicalOperator;
		}

		public void setLogicalOperator(String logicalOperator) {
			this.logicalOperator = logicalOperator;
		}

		public Property getOperand() {
			return this.operand;
		}

		public void setOperand(Property operand) {
			this.operand = operand;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}
	}
	
}