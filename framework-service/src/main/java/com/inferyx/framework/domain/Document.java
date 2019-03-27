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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.inferyx.framework.enums.Layout;

/**
 * @author Ganesh
 *
 */
public class Document {
	private String location;
	private String header;
	private String headerAlignment;
	private String footer;
	private String footerAlignment;
	private String title;
	private LinkedHashMap<String, Object> otherFields;
	private Layout layout;
	private List<Map<String, Object>> data;
	private String metaObjType;
	private Object metExecObject;
	private String documentType;
	private String name;
	private String description;
	private String parameters;
	private String generationDate;
	private String fileName;
	
	/**
	 * @Ganesh
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @Ganesh
	 *
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
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
	 * @return the otherFields
	 */
	public LinkedHashMap<String, Object> getOtherFields() {
		return otherFields;
	}

	/**
	 * @Ganesh
	 *
	 * @param otherFields the otherFields to set
	 */
	public void setOtherFields(LinkedHashMap<String, Object> otherFields) {
		this.otherFields = otherFields;
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
	 * @return the data
	 */
	public List<Map<String, Object>> getData() {
		return data;
	}

	/**
	 * @Ganesh
	 *
	 * @param data the data to set
	 */
	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	/**
	 * @Ganesh
	 *
	 * @return the metaObjType
	 */
	public String getMetaObjType() {
		return metaObjType;
	}

	/**
	 * @Ganesh
	 *
	 * @param metaObjType the metaObjType to set
	 */
	public void setMetaObjType(String metaObjType) {
		this.metaObjType = metaObjType;
	}

	/**
	 * @Ganesh
	 *
	 * @return the metExecObject
	 */
	public Object getMetExecObject() {
		return metExecObject;
	}

	/**
	 * @Ganesh
	 *
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}

	/**
	 * @Ganesh
	 *
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	/**
	 * @Ganesh
	 *
	 * @param metExecObject the metExecObject to set
	 */
	public void setMetExecObject(Object metExecObject) {
		this.metExecObject = metExecObject;
	}

	/**
	 * @Ganesh
	 *
	 * @return the headerAlignment
	 */
	public String getHeaderAlignment() {
		return headerAlignment;
	}

	/**
	 * @Ganesh
	 *
	 * @param headerAlignment the headerAlignment to set
	 */
	public void setHeaderAlignment(String headerAlignment) {
		this.headerAlignment = headerAlignment;
	}

	/**
	 * @Ganesh
	 *
	 * @return the footerAlignment
	 */
	public String getFooterAlignment() {
		return footerAlignment;
	}

	/**
	 * @Ganesh
	 *
	 * @param footerAlignment the footerAlignment to set
	 */
	public void setFooterAlignment(String footerAlignment) {
		this.footerAlignment = footerAlignment;
	}

	/**
	 * @Ganesh
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @Ganesh
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @Ganesh
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @Ganesh
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @Ganesh
	 *
	 * @return the parameters
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * @Ganesh
	 *
	 * @param parameters the parameters to set
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	/**
	 * @Ganesh
	 *
	 * @return the generationDate
	 */
	public String getGenerationDate() {
		return generationDate;
	}

	/**
	 * @Ganesh
	 *
	 * @param generationDate the generationDate to set
	 */
	public void setGenerationDate(String generationDate) {
		this.generationDate = generationDate;
	}

	/**
	 * @Ganesh
	 *
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @Ganesh
	 *
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
