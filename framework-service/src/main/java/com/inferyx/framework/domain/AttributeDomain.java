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

import java.util.Arrays;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "domain")

public class AttributeDomain extends BaseEntity {	
	private String regEx;
	
	public String getRegEx() {
		return regEx;
	}

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
