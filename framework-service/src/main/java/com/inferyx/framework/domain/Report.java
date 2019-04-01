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

import com.inferyx.framework.enums.Layout;

@Document(collection="report")
public class Report extends BaseEntity {

	private String title;
	private String header; 
	private String headerAlign; 
	private String footer; 
	private String footerAlign;
	private List<FilterInfo> filterInfo;
	private MetaIdentifierHolder dependsOn; 
	private List<AttributeSource> attributeInfo = new ArrayList<AttributeSource>();
	private String saveOnRefresh = "N";
	private SenderInfo senderInfo;
	private MetaIdentifierHolder paramList;
	private int limit;
	private String format;
	private Layout layout;
	private List<Attribute> attributeRef = new ArrayList<Attribute>();
	private MetaType type;

	public List<Attribute> getAttributeRef() {
		return attributeRef;
	}

	public void setAttributeRef(List<Attribute> attributeRef) {
		this.attributeRef = attributeRef;
	}

	public MetaType getType() {
		return type;
	}

	public void setType(MetaType type) {
		this.type = type;
	}
	/**
	 * @Ganesh
	 *
	 * @return the layout
	 */
	public Layout getLayout() {
		return layout;
	}
	/**
	 * @Ganesh
	 *
	 * @param layout the layout to set
	 */
	public void setLayout(Layout layout) {
		this.layout = layout;
	}
	/**
	 * @Ganesh
	 *
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * @Ganesh
	 *
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
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
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}
	/**
	 * @Ganesh
	 *
	 * @return the senderInfo
	 */
	public SenderInfo getSenderInfo() {
		return senderInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param senderInfo the senderInfo to set
	 */
	public void setSenderInfo(SenderInfo senderInfo) {
		this.senderInfo = senderInfo;
	}
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
	public List<FilterInfo> getFilterInfo() {
		return filterInfo;
	}
	/**
	 * @Ganesh
	 *
	 * @param filterInfo the filterInfo to set
	 */
	public void setFilterInfo(List<FilterInfo> filterInfo) {
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
	
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}
	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}
}
