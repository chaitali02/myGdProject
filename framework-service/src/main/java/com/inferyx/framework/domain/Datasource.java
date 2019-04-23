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

import com.inferyx.framework.enums.FormatType;

@Document(collection = "datasource")
public class Datasource extends BaseEntity {
	private String type;
	private String access;
	private String driver;
	private String host;
	private String dbname;
	private String port;
	private String username;
	private String password;
	private String path;
	private String subType;
	private String sessionParameters;
	private String sid;
	private String url;
	private FormatType format; // JSON, CSV, PSV, TSV, XML, EXCEL

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getSessionParameters() {
		return sessionParameters;
	}

	public void setSessionParameters(String sessionParameters) {
		this.sessionParameters = sessionParameters;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @Ganesh
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @Ganesh
	 *
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @Ganesh
	 *
	 * @return the format
	 */
	public FormatType getFormat() {
		return format;
	}

	/**
	 * @Ganesh
	 *
	 * @param format the format to set
	 */
	public void setFormat(FormatType format) {
		this.format = format;
	}
}
