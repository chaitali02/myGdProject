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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Key {
	private String uuid;
	private String version;
	private String name;
	
	public Key() {}
	
	public Key(String uuid, String version) {
		this.uuid = uuid;
		this.version = version;
	}
	
	public String getUUID() {
		return uuid;
	}
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Key other = (Key) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			
		}
		
		return super.toString();
	}


}
