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

@Document(collection="report")
public class Report extends BaseEntity {

	private String title;
	private String titleAline; 
	private String header; 
	private String headerAline; 
	private String footer; 
	private String footerAline;
	private MetaIdentifierHolder dependsOn; 
	private List<Attribute> attributes = new ArrayList<Attribute>();
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleAline() {
		return titleAline;
	}
	public void setTitleAline(String titleAline) {
		this.titleAline = titleAline;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getHeaderAline() {
		return headerAline;
	}
	public void setHeaderAline(String headerAline) {
		this.headerAline = headerAline;
	}
	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}
	public String getFooterAline() {
		return footerAline;
	}
	public void setFooterAline(String footerAline) {
		this.footerAline = footerAline;
	}
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
		
}
