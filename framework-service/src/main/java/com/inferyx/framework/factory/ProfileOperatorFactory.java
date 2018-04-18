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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.operator.ProfileHiveOperator;
import com.inferyx.framework.operator.ProfileImpalaOperator;
import com.inferyx.framework.operator.ProfileOperator;
import com.inferyx.framework.operator.ProfileOracleOperator;
import com.inferyx.framework.operator.ProfileMySQLOperator;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;

/**
 * @author joy
 *
 */
@Component
public class ProfileOperatorFactory /*extends OperatorFactory*/ {
	
	@Autowired
	MetadataUtil daoRegister;
	@Autowired
	DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	protected CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private ProfileMySQLOperator profileMySQLOperator;
	@Autowired
	private ProfileImpalaOperator profileImpalaOperator;
	@Autowired
	private ProfileHiveOperator profileHiveOperator;
	@Autowired
	private ProfileOracleOperator profileOracleOperator;
	
	static final Logger logger = Logger.getLogger(ProfileOperatorFactory.class);
	Datapod dp;

	/**
	 * 
	 */
	public ProfileOperatorFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public ProfileOperator getOperator(Mode runMode) {
		
		Datasource datasource = null;
		try {
			datasource = commonServiceImpl.getDatasourceByApp();
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String datasourceName = datasource.getType();
		String sql = "";
		if (runMode.equals(Mode.ONLINE)) {
			if(datasourceName.equalsIgnoreCase(ExecContext.MYSQL.toString())) {
				datasourceName = ExecContext.MYSQL.toString();
			}
			
			else if(datasourceName.equalsIgnoreCase(ExecContext.ORACLE.toString()))
			{
				datasourceName=ExecContext.ORACLE.toString();
			}
			else {
				datasourceName = ExecContext.spark.toString();
			}
		}

		if (datasourceName.equalsIgnoreCase(ExecContext.IMPALA.toString())) {
			return profileImpalaOperator;
		} else if (datasourceName.equalsIgnoreCase(ExecContext.HIVE.toString()) || datasourceName.equalsIgnoreCase(ExecContext.spark.toString()) || datasourceName.equalsIgnoreCase(ExecContext.FILE.toString())) {
			return profileHiveOperator;
		} else if (datasourceName.equalsIgnoreCase(ExecContext.MYSQL.toString())) {
			return profileMySQLOperator;
		} else {
			return profileOracleOperator;
		}
		
	}

}
