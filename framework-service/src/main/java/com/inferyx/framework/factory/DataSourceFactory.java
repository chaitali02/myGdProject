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
package com.inferyx.framework.factory;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.reader.HiveReader;
import com.inferyx.framework.reader.IReader;
import com.inferyx.framework.reader.ImpalaReader;
import com.inferyx.framework.reader.MySqlReader;
import com.inferyx.framework.reader.OracleReader;
import com.inferyx.framework.reader.ParquetReader;
import com.inferyx.framework.reader.PostGresReader;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.writer.HiveWriter;
import com.inferyx.framework.writer.IWriter;
import com.inferyx.framework.writer.ImpalaWriter;
import com.inferyx.framework.writer.MySqlWriter;
import com.inferyx.framework.writer.OracleWriter;
import com.inferyx.framework.writer.ParquetWriter;
import com.inferyx.framework.writer.PostGresWriter;

@Component
public class DataSourceFactory {

//	@Autowired
//	private HiveRegister hiveRegister;
//	@Autowired
//	private CSVRegister csvRegister;
	@Autowired
	private HiveReader hiveReader;
	@Autowired
	private ParquetReader parquetReader;
	@Autowired
	private HiveWriter hiveWriter;
	@Autowired
	private ParquetWriter parquetWriter;
	@Autowired
	private ImpalaWriter impalaWriter;
	@Autowired
	private ImpalaReader impalaReader;
	@Autowired
	private OracleWriter oracleWriter;
	@Autowired
	private OracleReader oracleReader;
	@Autowired
	private MySqlReader mySqlReader;
	@Autowired
	private MySqlWriter mySqlWriter;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private PostGresWriter postGresWriter;
	@Autowired
	private PostGresReader postGresReader;



	public IReader getDatapodReader(Datapod dp) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		String dataSourceUUID = dp.getDatasource().getRef().getUuid();
//		String dataSourceVersion = dp.getDatasource().getRef().getVersion();
//
//		Datasource ds = (Datasource) commonActivity
//				.getRefObject(new MetaIdentifier(MetaType.datasource, dataSourceUUID, dataSourceVersion));
//		Datasource ds = commonServiceImpl.getDatasourceByApp();
		Datasource ds = (Datasource) commonServiceImpl.getOneByUuidAndVersion(dp.getDatasource().getRef().getUuid(), 
																			dp.getDatasource().getRef().getVersion(), 
																			dp.getDatasource().getRef().getType().toString(), "N");
		
		
		String dataSourceType = ds.getType();

		switch (dataSourceType.toUpperCase()) {
		case "HIVE":// HiveReader hr = new HiveReader();
			return hiveReader;
		case "FILE":// ParquetReader pr = new ParquetReader();
			return parquetReader;
		case "IMPALA":
			return impalaReader;
		case "ORACLE":
			return oracleReader;
		case "MYSQL":
			return mySqlReader;
		case "POSTGRES":
			return postGresReader;
		// case "spark": return (dp.getName().equalsIgnoreCase("hive")? hiveReader :
		// parquetReader);
		default:
		}
		return null;
	}

	public IWriter getDatapodWriter(Datapod datapod) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// MetaType dataSourceType = dp.getDatasource().getRef().getType();
//		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(datapod.getDatasource().getRef().getUuid(), 
																		datapod.getDatasource().getRef().getVersion(), 
																		datapod.getDatasource().getRef().getType().toString(), "N");
		String dataSourceType = datasource.getType();
		switch (dataSourceType.toUpperCase()) {
		case "HIVE":// HiveWriter hr = new HiveWriter();
//			hiveWriter.setDaoRegister(daoRegister);
			return hiveWriter;
		case "FILE":// ParquetWriter pr = new ParquetWriter();
			return parquetWriter;
		case "IMPALA":
//			impalaWriter.setDaoRegister(daoRegister);
			return impalaWriter;
		case "MYSQL":
//			mySqlWriter.setDaoRegister(daoRegister);
			return mySqlWriter;
		case "ORACLE":
//			oracleWriter.setDaoRegister(daoRegister);
			return oracleWriter;
		case "POSTGRES":
			return postGresWriter;
		default:
		}
		return null;
	}

	public IReader getDatapodReader() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {

		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		
		String dataSourceType = datasource.getType();

		switch (dataSourceType.toUpperCase()) {
		case "HIVE":// HiveReader hr = new HiveReader();
			return hiveReader;
		case "FILE":// ParquetReader pr = new ParquetReader();
			return parquetReader;
		case "IMPALA":
			return impalaReader;
		case "ORACLE":
			return oracleReader;
		case "MYSQL":
			return mySqlReader;
		case "POSTGRES":
			return postGresReader;
		// case "spark": return (dp.getName().equalsIgnoreCase("hive")? hiveReader :
		// parquetReader);
		default:
		}
		return null;
	}

	public IWriter getDatapodWriter()
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {

		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		String dataSourceType = datasource.getType();
		switch (dataSourceType.toUpperCase()) {
		case "HIVE":// HiveWriter hr = new HiveWriter();
//			hiveWriter.setDaoRegister(daoRegister);
			return hiveWriter;
		case "FILE":// ParquetWriter pr = new ParquetWriter();
			return parquetWriter;
		case "IMPALA":
//			impalaWriter.setDaoRegister(daoRegister);
			return impalaWriter;
		case "MYSQL":
//			mySqlWriter.setDaoRegister(daoRegister);
			return mySqlWriter;
		case "ORACLE":
//			oracleWriter.setDaoRegister(daoRegister);
			return oracleWriter;
		case "POSTGRES":
			return postGresWriter;
		default:
		}
		return null;
	}
}