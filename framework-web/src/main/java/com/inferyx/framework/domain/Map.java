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

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "map")
public class Map extends BaseEntity {
	
	private MetaIdentifierHolder target;
	private MetaIdentifierHolder source;	// May be a relation, datapod or dataset
	private List<AttributeMap> attributeMap = new ArrayList<AttributeMap>();
	//private List<AttributeRefHolder> groupBy;


	/*public List<AttributeRefHolder> getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(List<AttributeRefHolder> groupBy) {
		this.groupBy = groupBy;
	}*/

	public MetaIdentifierHolder getTarget() {
		return target;
	}

	public void setTarget(MetaIdentifierHolder target) {
		this.target = target;
	}

	public MetaIdentifierHolder getSource() {
		return source;
	}

	public void setSource(MetaIdentifierHolder source) {
		this.source = source;
	}

	public List<AttributeMap> getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(List<AttributeMap> attributeMap) {
		this.attributeMap = attributeMap;
	}
	
}
