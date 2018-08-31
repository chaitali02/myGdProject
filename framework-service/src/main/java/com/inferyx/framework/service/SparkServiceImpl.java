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
package com.inferyx.framework.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;

@SuppressWarnings("serial")
@Service
public class SparkServiceImpl implements java.io.Serializable {

	/*@Autowired
	private HiveContext hiveContext;*/
	@Autowired
	SparkExecutor executor;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	
	static final Logger logger = Logger.getLogger(SparkServiceImpl.class);

	public List<Object> submitQuery(String sql, int rowLimit, String format, String header)
			throws org.json.JSONException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		
		/*List<Object> data = new ArrayList<>();
		String finalCsv = null;
		String columnHeaderStr = null;
		StringBuilder columnName = new StringBuilder();*/

///*		sqlContext.udf().register("normSInv",new UDF1<BigDecimal,Double>() {
//			@Override
//			//@Transient
//				public Double call(BigDecimal p) throws Exception {
//					NormalDistribution nd = new NormalDistribution();
//					return nd.inverseCumulativeProbability(p.doubleValue());
//			}
//		}, DataTypes.DoubleType);*/

		// Execute SQL
		logger.info(sql);
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
		return exec.submitQuery(sql, rowLimit, format, header, commonServiceImpl.getApp().getUuid());
		
		/*Dataset<Row> df = sparkSession.sql(sql).coalesce(10);
		df.persist(StorageLevel.MEMORY_AND_DISK());
		//df.cache();
		
		Row[] rows;
		if (rowLimit == 0) {
			rows = (Row[]) df.head(20);
		} else {
			rows = (Row[]) df.limit(rowLimit).collect();
		}
		if (format == null) {
			format = "";
		}
		
		//Create header row
		String[] columns = df.columns();
		for (String columnHeader : columns) {
			columnName.append(columnHeader);
			columnName.append(",");
		}
		if (columnName.charAt(columnName.length() - 1) == ',') {
			columnHeaderStr = columnName.substring(0, columnName.length() - 1);
		}
		if (format != null & format.equalsIgnoreCase("CSV")) {
			if (header.equalsIgnoreCase("Y")) {
				data.add(columnHeaderStr);
			}
		}

		//Generate output format
		for (Row row : rows) {
			java.util.TreeMap<String, Object> object = new TreeMap<String, Object>();
			if (format.equals("") || !format.equalsIgnoreCase("csv")) {
				for (String column : columns) {
					object.put(column, row.getAs(column));
				}
				data.add(object);
			}
			if (format != null & format.equalsIgnoreCase("CSV")) {
				StringBuilder sbr = new StringBuilder();
				for (String column : columns) {
					Object csvRow = row.getAs(column);
					sbr.append(csvRow);
					sbr.append(",");
				}

				if (sbr.charAt(sbr.length() - 1) == ',') {
					finalCsv = sbr.substring(0, sbr.length() - 1);
				}
				data.add(finalCsv);
			}
		}
		return data;*/
	}
}
