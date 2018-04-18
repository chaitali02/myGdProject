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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author joy
 *
 */
@Service
public class DagVisitorFactory {

	@Autowired
	JsonVisitor jsonVisitor;
	@Autowired
	PrettyPrintVisitor prettyPrintVisitor;
	
	/**
	 * 
	 */
	public DagVisitorFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public DagVisitor getInstance(String type) {
		switch (type) {
		case "JSON":
			return jsonVisitor;
		case "PRETTY":
			return prettyPrintVisitor;
		default:
			return jsonVisitor;
		}
	}

}
