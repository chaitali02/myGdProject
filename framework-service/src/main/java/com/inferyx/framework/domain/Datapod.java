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

@Document(collection = "datapod")
public class Datapod extends BaseEntity {

	// private String name;
	private MetaIdentifierHolder datasource;
	private String cache;
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private String prefix;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public MetaIdentifierHolder getDatasource() {
		return datasource;
	}

	public void setDatasource(MetaIdentifierHolder datasource) {
		this.datasource = datasource;
	}

	/*
	 * public String getName() { return name; }
	 * 
	 * public void setName(String name) { this.name = name; }
	 */

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public Attribute getAttribute(Integer attributeId) {

		for (Attribute attr : getAttributes()) {
			if (attr.getAttributeId().equals(attributeId)) {
				return attr;
			}
		}

		return null;
	}

	public String getAttributeName(Integer attrId) {
		return getAttribute(attrId).getName();
	}

	public String sql(Integer attributeId) {
		Attribute attr = getAttribute(attributeId);
		//return  attr.getName();
		return String.format("%s.%s", getName(), attr.getName());
	}
	
	public Integer getAttributeId(String attributeName) {
		for (Attribute attr : getAttributes()) {
			if (attr.getName().equals(attributeName)) {
				return attr.getAttributeId();
			}
		}

		return null;
	}
}
