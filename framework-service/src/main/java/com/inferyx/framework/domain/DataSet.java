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

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dataset")
public class DataSet extends BaseEntity {
	private MetaIdentifierHolder dependsOn;
	private List<FilterInfo> filterInfo;	
	private List<AttributeSource> attributeInfo = new ArrayList<AttributeSource>();
	private MetaIdentifierHolder groupBy;
	private int limit;

	/**
	 * @Ganesh
	 *
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @Ganesh
	 *
	 * @param limit
	 *            the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<FilterInfo> getFilterInfo() {
		return filterInfo;
	}
	
	public void setFilterInfo(List<FilterInfo> filterInfo) {
		this.filterInfo = filterInfo;
	}

	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	public MetaIdentifierHolder getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(MetaIdentifierHolder groupBy) {
		this.groupBy = groupBy;
	}

	public String sql(String attrName) {
		return String.format("%s.%s", this.getName(), attrName);
	}

	public List<AttributeSource> getAttributeInfo() {
		return attributeInfo;
	}

	public void setAttributeInfo(List<AttributeSource> attributeInfo) {
		this.attributeInfo = attributeInfo;
	}

	public AttributeSource getAttribute(Integer attributeId) {

		for (AttributeSource attr : getAttributeInfo()) {
			if (attr.getAttrSourceId().equals(attributeId.toString())) {
				return attr;
			}
		}

		return null;
	}
	
	public String getAttributeName(Integer attributeId) {
		List<AttributeSource> sourceAttrs = getAttributeInfo();
		for (AttributeSource sourceAttr : sourceAttrs) {
			if (sourceAttr.getSourceAttr() != null && sourceAttr.getAttrSourceId() != null
					&& sourceAttr.getAttrSourceId().equals(attributeId.toString())) {
				return sourceAttr.getAttrSourceName();
			}
		}
		return null;
	}
}
