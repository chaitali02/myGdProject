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

import java.util.List;

import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifierHolder;

public class DatasetView extends BaseEntity{	
	//private Relation source;
	private String sourceChg;	
	private List<AttributeSource> attributeInfo;
	private String srcChg;
	//private MetaIdentifierHolder mapInfo;
	private Filter filter;	
	private String filterChg;
	private MetaIdentifierHolder dependsOn;
	private int limit;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	

	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	public List<AttributeSource> getAttributeInfo() {
		return attributeInfo;
	}

	public void setAttributeInfo(List<AttributeSource> attributeInfo) {
		this.attributeInfo = attributeInfo;
	}
	public String getSourceChg() {
		return sourceChg;
	}
	public void setSourceChg(String sourceChg) {
		this.sourceChg = sourceChg;
	}	
	/*public MetaIdentifierHolder getMapInfo() {
		return mapInfo;
	}

	public void setMapInfo(MetaIdentifierHolder mapInfo) {
		this.mapInfo = mapInfo;
	}*/
	
	public String getSrcChg() {
		return srcChg;
	}
	public void setSrcChg(String srcChg) {
		this.srcChg = srcChg;
	}
	public String getFilterChg() {
		return filterChg;
	}

	public void setFilterChg(String filterChg) {
		this.filterChg = filterChg;
	}
	
	public Filter getFilter() {
		return filter;
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	/*public Relation getSource() {
		return source;
	}

	public void setSource(Relation source) {
		this.source = source;
	}
*/
		
}
