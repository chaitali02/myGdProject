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

/**
 * @author Ganesh
 *
 */
public class ReportView extends BaseEntity {
	private String title;
	private String header; 
	private String headerAlign; 
	private String footer; 
	private String footerAlign;
	private String sourceChg;	
	private List<AttributeSource> attributeInfo;
	private String srcChg;
	private Filter filter;	
	private String filterChg;
	private MetaIdentifierHolder dependsOn;
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
	 * @return the sourceChg
	 */
	public String getSourceChg() {
		return sourceChg;
	}
	/**
	 * @Ganesh
	 *
	 * @param sourceChg the sourceChg to set
	 */
	public void setSourceChg(String sourceChg) {
		this.sourceChg = sourceChg;
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
	/**
	 * @Ganesh
	 *
	 * @return the srcChg
	 */
	public String getSrcChg() {
		return srcChg;
	}
	/**
	 * @Ganesh
	 *
	 * @param srcChg the srcChg to set
	 */
	public void setSrcChg(String srcChg) {
		this.srcChg = srcChg;
	}
	/**
	 * @Ganesh
	 *
	 * @return the filter
	 */
	public Filter getFilter() {
		return filter;
	}
	/**
	 * @Ganesh
	 *
	 * @param filter the filter to set
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	/**
	 * @Ganesh
	 *
	 * @return the filterChg
	 */
	public String getFilterChg() {
		return filterChg;
	}
	/**
	 * @Ganesh
	 *
	 * @param filterChg the filterChg to set
	 */
	public void setFilterChg(String filterChg) {
		this.filterChg = filterChg;
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
}
