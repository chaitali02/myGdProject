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

@Document(collection="user")
public class User extends BaseEntity
{
	private String firstName;
	private String middleName;
	private String lastName;
	private String emailId;
	private String password;	
	private List<MetaIdentifierHolder> groupInfo = new ArrayList<>();
	/*private List<MetaIdentifierHolder> roleInfo = new ArrayList<>();*/
	private MetaIdentifierHolder defaultGroup;
	
	/**
	 * @Ganesh
	 *
	 * @return the defaultGroup
	 */
	public MetaIdentifierHolder getDefaultGroup() {
		return defaultGroup;
	}
	/**
	 * @Ganesh
	 *
	 * @param defaultGroup the defaultGroup to set
	 */
	public void setDefaultGroup(MetaIdentifierHolder defaultGroup) {
		this.defaultGroup = defaultGroup;
	}
	public List<MetaIdentifierHolder> getGroupInfo() {
		return groupInfo;
	}
	public void setGroupInfo(List<MetaIdentifierHolder> groupInfo) {
		this.groupInfo = groupInfo;
	}
	/*public List<MetaIdentifierHolder> getRoleInfo() {
		return roleInfo;
	}
	public void setRoleInfo(List<MetaIdentifierHolder> roleInfo) {
		this.roleInfo = roleInfo;
	}*/
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
}
