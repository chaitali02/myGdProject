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

@Document(collection="comment")
public class Comment extends BaseEntity
{
	MetaIdentifierHolder dependsOn;
	MetaIdentifierHolder uploadExec;

	
	public MetaIdentifierHolder getUploadExec() {
		return uploadExec;
	}

	public void setUploadExec(MetaIdentifierHolder uploadExec) {
		this.uploadExec = uploadExec;
	}

	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}
}
