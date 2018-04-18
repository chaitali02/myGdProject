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
package com.inferyx.framework.batch;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.DagStatusHolder;
import com.inferyx.framework.domain.MetaIdentifierHolder;

/**
 * @author joy
 *
 */
@Component
public class JsonVisitor implements DagVisitor {

	/**
	 * 
	 */
	public JsonVisitor() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.batch.DagVisitor#visit(com.inferyx.framework.domain.MetaIdentifierHolder)
	 */
	@Override
	public String visit(MetaIdentifierHolder metaIdentifierHolder) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(System.out, metaIdentifierHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Data Written";
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.batch.DagVisitor#visit(java.util.List)
	 */
	@Override
	public String visit(List<BaseEntity> objectList) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(System.out, objectList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Data Written";
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.batch.DagVisitor#visit(com.inferyx.framework.domain.DagStatusHolder)
	 */
	@Override
	public String visit(DagStatusHolder dagStatusHolder) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(System.out, dagStatusHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Data Written";
	}

}
