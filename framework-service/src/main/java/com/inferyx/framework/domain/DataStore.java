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

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="datastore")
public class DataStore extends BaseEntity{
	
	
	//private String name;
	//private String desc;
	private MetaIdentifierHolder metaId;
	private MetaIdentifierHolder execId;
	private String location;
	private String persistMode;
	private List<MetaIdentifierHolder> dimInfo;
	private long numRows;
	private String runMode;
	
//	public static class AttributeDetails {
//		
//		private MetaIdentifier ref;
//		private Integer attributeId;
//		private String value;
//	
//		public MetaIdentifier getRef() {
//			return ref;
//		}
//		public void setRef(MetaIdentifier ref) {
//			this.ref = ref;
//		}
//		public Integer getAttributeId() {
//			return attributeId;
//		}
//		public void setAttributeId(Integer attributeId) {
//			this.attributeId = attributeId;
//		}
//		public String getValue() {
//			return value;
//		}
//		public void setValue(String value) {
//			this.value = value;
//		}
//	
//	}	
	
	//Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	public long getNumRows() {
		return numRows;
	}

	/**
	 * @return the runMode
	 */
	public String getRunMode() {
		return runMode;
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}

	public void setNumRows(long numRows) {
		this.numRows = numRows;
	}

	/*public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
*/
	public MetaIdentifierHolder getMetaId() {
		return metaId;
	}

	public void setMetaId(MetaIdentifierHolder metaId) {
		this.metaId = metaId;
	}

	public MetaIdentifierHolder getExecId() {
		return execId;
	}

	public void setExecId(MetaIdentifierHolder execId) {
		this.execId = execId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<MetaIdentifierHolder> getDimInfo() {
		return dimInfo;
	}

	public void setDimInfo(List<MetaIdentifierHolder> dimInfo) {
		this.dimInfo = dimInfo;
	}

	public String getPersistMode() {
		return persistMode;
	}

	public void setPersistMode(String persistMode) {
		this.persistMode = persistMode;
	}
	
	
}
