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

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="load")
public class Load extends BaseEntity {

	private String header;
	private String append;
	//private String name;
	private MetaIdentifierHolder source;
	private MetaIdentifierHolder target;
	
	/*public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}*/
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getAppend() {
		return append;
	}
	public void setAppend(String append) {
		this.append = append;
	}
	public MetaIdentifierHolder getSource() {
		return source;
	}
	public void setSource(MetaIdentifierHolder source) {
		this.source = source;
	}
	public MetaIdentifierHolder getTarget() {
		return target;
	}
	public void setTarget(MetaIdentifierHolder target) {
		this.target = target;
	}
	
}
