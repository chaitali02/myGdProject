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


public class RelationInfo {
//	@Autowired private MetadataUtil commonActivity;
	private MetaIdentifierHolder join;

	public MetaIdentifierHolder getJoin() {
		return join;
	}

	public void setJoin(MetaIdentifierHolder join) {
		this.join = join;
	}

	private String joinType;
	
	private List<FilterInfo> joinKey;
	
	private LogicalOperand logicaloperand;


	public String getJoinType() {
		return joinType;
	}

	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}

	public  List<FilterInfo>  getJoinKey() {
		return joinKey;
	}

	public void setJoinKey( List<FilterInfo>  joinkey) {
		this.joinKey = joinkey;
	}

	public LogicalOperand getLogicaloperand() {
		return logicaloperand;
	}

	public void setLogicaloperand(LogicalOperand logicaloperand) {
		this.logicaloperand = logicaloperand;
	}
	
/*	public String sql(Loader loader) {
		StringBuilder builder = new StringBuilder();
		if(getJoinType() == null || getJoinType().trim().equals("")) {
			// Bhanu - JIRA FW-3 - Remove cache
			// Datapod datapod = (Datapod) getJoin().get(0).getRef().getCollectionObject(loader);
			Datapod datapod = (Datapod) commonActivity.getRefObject(getJoin().get(0).getRef());
			//====the below getLatestDatapodTable method needs revision====== NEEDS CHANGE =====
			String table= loader.getLatestDatapodTable(new OrderKey(datapod.getUuid(), datapod.getVersion()));
			// End JIRA FW-3 Changes
			builder.append(String.format(table, datapod.getName())).append(" as ").append(datapod.getName());
			return builder.toString();
		}
		
		// Bhanu - JIRA FW-3 - Remove cache
		// Datapod leftDatapod = (Datapod) getJoin().get(0).getRef().getCollectionObject(loader);
		Datapod leftDatapod = (Datapod) commonActivity.getRefObject(getJoin().get(0).getRef());
		//====the below getLatestDatapodTable method needs revision====== NEEDS CHANGE =====
		String leftTable= loader.getLatestDatapodTable(new OrderKey(leftDatapod.getUuid(), leftDatapod.getVersion()));
		
		// Datapod rightDatapod = (Datapod) getJoin().get(1).getRef().getCollectionObject(loader);
		Datapod rightDatapod = (Datapod) commonActivity.getRefObject(getJoin().get(1).getRef());
		//====the below getLatestDatapodTable method needs revision====== NEEDS CHANGE =====
		String rightTable = loader.getLatestDatapodTable(new OrderKey(rightDatapod.getUuid(), rightDatapod.getVersion()));
		// End JIRA FW-3 Changes
		
		builder.append(leftTable).append(" as ").append(leftDatapod.getName()).append(" ");
		builder.append(getJoinType() + " JOIN ").append(" ").append(rightTable).append(" as ").append(rightDatapod.getName()).append(" ")
			   .append(" on ").append(FilterInfo.sql(commonActivity, getJoinKey()));
		
		return builder.toString();
	}*/
	
	

}
