package com.inferyx.framework.domain;

import java.util.Arrays;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="domain")
public class DataQualDomain extends BaseEntity {
	
	private String regEx;

	public DataQualDomain() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the regEx
	 */
	public String getRegEx() {
		return regEx;
	}

	/**
	 * @param regEx the regEx to set
	 */
	public void setRegEx(String regEx) {
		this.regEx = regEx;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DataQualDomain [regEx=" + regEx + ", getPublicFlag()=" + getPublicFlag() + ", getLocked()="
				+ getLocked() + ", getPublished()=" + getPublished() + ", getAppInfo()=" + getAppInfo() + ", getId()="
				+ getId() + ", getUuid()=" + getUuid() + ", getVersion()=" + getVersion() + ", getName()=" + getName()
				+ ", getDesc()=" + getDesc() + ", getCreatedOn()=" + getCreatedOn() + ", getTags()="
				+ Arrays.toString(getTags()) + ", getActive()=" + getActive() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getClass()=" + getClass() + "]";
	}
	
}
