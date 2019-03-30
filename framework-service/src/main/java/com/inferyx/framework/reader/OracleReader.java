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
package com.inferyx.framework.reader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;

@Service
public class OracleReader implements IReader {
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;

	Logger logger=Logger.getLogger(OracleReader.class);
	
	@Override
	public ResultSetHolder read(Datapod datapod, DataStore datastore, Object conObject, Datasource dataSource)
			throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		ResultSetHolder rsHolder = null;
		try {
			Datasource execDatasource = commonServiceImpl.getDatasourceByApp();
			Datasource tableDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(datapod.getDatasource().getRef().getUuid(), 
																				datapod.getDatasource().getRef().getVersion(), 
																				datapod.getDatasource().getRef().getType().toString());
			IExecutor executor = execFactory.getExecutor(execDatasource.getType());
			String databaseName = tableDatasource.getDbname();
			rsHolder = executor.executeSql("SELECT * FROM "+databaseName+"."+datapod.getName());
			rsHolder.setTableName(Helper.genTableName(datastore.getLocation()));
		} catch (IllegalArgumentException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return rsHolder;
	}

}
