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
package com.inferyx.framework.distribution;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

/**
 * @author joy
 *
 */
public class DoubleToRowFunction implements Function<Double, Row> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6950520933079824252L;

	/**
	 * 
	 */
	public DoubleToRowFunction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Row call(Double value) throws Exception {
		return RowFactory.create(value);
	}

}
