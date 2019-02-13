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
	private String header; 
	private String headerAlign; 
	private String footer; 
	private String footerAlign;
	private List<AttributeRefHolder> filterInfo;
	private MetaIdentifierHolder dependsOn; 
	private List<AttributeSource> attributeInfo = new ArrayList<AttributeSource>();
	private String saveOnRefresh = "N";
	
	/**
	 * @Ganesh
	 *
	 * @return the saveOnRefresh
	 */
	public String getSaveOnRefresh() {
		return saveOnRefresh;
	}
	/**
	 * @Ganesh
	 *
	 * @param saveOnRefresh the saveOnRefresh to set
	 */
	public void setSaveOnRefresh(String saveOnRefresh) {
		this.saveOnRefresh = saveOnRefresh;
	}
	/**
	 * @Ganesh
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @Ganesh
	 *
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @Ganesh
	 *
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}
	/**
	 * @Ganesh
	 *
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}
	/**
	 * @Ganesh
	 *
	 * @return the headerAlign
	 */
	public String getHeaderAlign() {
		return headerAlign;
	}
	/**
	 * @Ganesh
	 *
	 * @param headerAlign the headerAlign to set
	 */
	public void setHeaderAlign(String headerAlign) {
		this.headerAlign = headerAlign;
	}
	/**
	 * @Ganesh
	 *
	 * @return the footer
	 */
	public String getFooter() {
		return footer;
	}
	/**
	 * @Ganesh
	 *
	 * @param footer the footer to set
	 */
	public void setFooter(String footer) {
		this.footer = footer;
	}
	/**
	 * @Ganesh
	 *
	 * @return the footerAlign
	 */
	public String getFooterAlign() {
		return footerAlign;
	}
	/**
	 * @Ganesh
	 *
	 * @param footerAlign the footerAlign to set
	 */
	public void setFooterAlign(String footerAlign) {
		this.footerAlign = footerAlign;
	}
	/**
	 * @Ganesh
	 *
	 * @return the filterInfo
	 */
	public List<AttributeRefHolder> getFilterInfo() {
		return filterInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param filterInfo the filterInfo to set
	 */
	public void setFilterInfo(List<AttributeRefHolder> filterInfo) {
		this.filterInfo = filterInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @return the dependsOn
	 */
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	/**
	 * @Ganesh
	 *
	 * @param dependsOn the dependsOn to set
	 */
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
	/**
	 * @Ganesh
	 *
	 * @return the attributeInfo
	 */
	public List<AttributeSource> getAttributeInfo() {
		return attributeInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param attributeInfo the attributeInfo to set
	 */
	public void setAttributeInfo(List<AttributeSource> attributeInfo) {
		this.attributeInfo = attributeInfo;
	}
}
