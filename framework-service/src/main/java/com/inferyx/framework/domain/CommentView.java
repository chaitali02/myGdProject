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

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="CommentView")
public class CommentView extends BaseEntity
{
	List<MetaIdentifierHolder> uploadExecInfo;

	public List<MetaIdentifierHolder> getUploadExecInfo() {
		return uploadExecInfo;
	}

	public void setUploadExecInfo(List<MetaIdentifierHolder> uploadExecInfo) {
		this.uploadExecInfo = uploadExecInfo;
	}

	

	
}
