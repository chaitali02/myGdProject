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
package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.hive.metastore.api.FunctionType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.FuncType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;

@Component
public class ProfileOperator {

	@Autowired
	MetadataUtil daoRegister;
	@Autowired
	DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	protected CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(ProfileOperator.class);
	Datapod dp;
	RunMode runMode;

	/**
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}

	/**
	 * @param runMode
	 *            the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

	public String generateSql(Profile profile, ProfileExec profileExec, String attrId, List<String> datapodList,
			DagExec dagExec, HashMap<String, String> otherParams, RunMode runMode) throws NumberFormatException, Exception {
		if (profile == null) {
			return null;
		}
		dp = (Datapod) daoRegister.getRefObject(profile.getDependsOn().getRef());
		if (profile.getAttributeInfo() != null) {
			String tableName = getTableName(dp, profileExec, datapodList, dagExec, otherParams, runMode);
			logger.info("getProfileTableName(DP) : " + tableName);
			return generateSql(profile, profileExec, tableName, attrId, 
					dp.getAttribute(Integer.parseInt(attrId)).getName(), runMode);
		} else {
			return generateSql(profile, profileExec, getTableName(dp, profileExec, datapodList, dagExec, otherParams, runMode), null,
					dagExec, otherParams, runMode);
		}
	}

	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, RunMode runMode)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		String datasourceName = datasource.getType();
		String sql = "";
		if (runMode.equals(RunMode.ONLINE)) {
			if (datasourceName.equalsIgnoreCase(ExecContext.MYSQL.toString())) {
				datasourceName = ExecContext.MYSQL.toString();
			} else if (datasourceName.equalsIgnoreCase(ExecContext.ORACLE.toString())) {
				datasourceName = ExecContext.ORACLE.toString();
			} else {
				datasourceName = ExecContext.spark.toString();
			}
		}

		if (datasourceName.equalsIgnoreCase(ExecContext.IMPALA.toString())) {

			sql = "Select \'" + profile.getDependsOn().getRef().getUuid() + "\' as datapodUUID, \'"
					+ profile.getDependsOn().getRef().getVersion() + "\' as datapodVersion, cast(" + attrId
					+ " as string) as AttributeId, min(cast(" + attrName + " as int)) as minVal, max(cast(" + attrName
					+ " as int)) as maxVal, avg(cast(" + attrName + " as int)) as avgVal,appx_median(cast(" + attrName
					+ " as DOUBLE)) as mediaVal, " + "stddev(cast(" + attrName
					+ " as int)) as stdDev, cast(count(distinct " + attrName
					+ ") as INT) as numDistinct, count(distinct " + attrName + ")/count(" + attrName
					+ ")*100 as perDistinct, cast(count(" + attrName + ") as INT) as numNull,count(" + attrName
					+ ") / count(" + attrName + ")*100 as perNull, count(" + attrName + ") / count(" + attrName
					+ ") as sixSigma, " + profileExec.getVersion() + " as version from " + profileTableName;
		} else if (datasourceName.equalsIgnoreCase(ExecContext.MYSQL.toString())) {
			sql = "Select \'" + profile.getDependsOn().getRef().getUuid() + "\' as datapodUUID, \'"
					+ profile.getDependsOn().getRef().getVersion() + "\' as datapodVersion, " + attrId
					+ " as AttributeId, min(cast(" + attrName + " as SIGNED)) as minVal, max(cast(" + attrName
					+ " as SIGNED)) as maxVal, avg(" + attrName + ") as avgVal,cast(" + getMedianVal(attrName)
					+ " as decimal) as medianVal, stddev(" + attrName + ") as stdDev, count(distinct " + attrName
					+ ") as numDistinct, count(distinct " + attrName + ")/count(" + attrName
					+ ")*100 as perDistinct, count(if(" + attrName + " is null,1,0)) as numNull,count(if(" + attrName
					+ "" + " is null,1,0)) / count(" + attrName + ")*100 as perNull, count(if(" + attrName
					+ " is null,1,0)) / count(" + attrName + ") as sixSigma, '" + profileExec.getVersion()
					+ "' as version from " + profileTableName;

		} else if (datasourceName.equalsIgnoreCase(ExecContext.ORACLE.toString())) {
			sql = "Select \'" + profile.getDependsOn().getRef().getUuid() + "\' as datapodUUID, \'"
					+ profile.getDependsOn().getRef().getVersion() + "\' as datapodVersion, cast(" + attrId
					+ " as varchar2(70)) as AttributeId, min(cast(" + attrName + " as number)) as minVal, max(cast("
					+ attrName + " as number)) as maxVal, avg(cast(" + attrName + " as number)) as avgVal,median(cast("
					+ attrName + " as double precision)) as mediaVal, " + "stddev(cast(" + attrName
					+ " as number)) as stdDev, cast(count(distinct " + attrName
					+ ") as number) as numDistinct, count(distinct " + attrName + ")/count(" + attrName
					+ ")*100 as perDistinct, cast(count(" + attrName + ") as number) as numNull,count(" + attrName
					+ ") / count(" + attrName + ")*100 as perNull, count(" + attrName + ") / count(" + attrName
					+ ") as sixSigma, " + profileExec.getVersion() + " as version from " + profileTableName;
		} else {
			sql = "Select \'" + profile.getDependsOn().getRef().getUuid() + "\' as datapodUUID, \'"
					+ profile.getDependsOn().getRef().getVersion() + "\' as datapodVersion, " + attrId
					+ " as AttributeId, min(" + attrName + ") as minVal, max(" + attrName + ") as maxVal, avg(cast("
					+ attrName + " as int)) as avgVal, percentile(cast(" + attrName
					+ " as BIGINT), 0.5) as medianVal, stddev(" + attrName + ") as stdDev, count(distinct " + attrName
					+ ") as numDistinct, count(distinct " + attrName + ")/count(" + attrName
					+ ")*100 as perDistinct, count(if(" + attrName + " is null,1,0)) as numNull,count(if(" + attrName
					+ "" + " is null,1,0)) / count(" + attrName + ")*100 as perNull, count(if(" + attrName
					+ " is null,1,0)) / count(" + attrName + ") as sixSigma, '" + profileExec.getVersion()
					+ "' as version from " + profileTableName;

		}
		logger.info("query is : " + sql);
		return sql;
	}

	private String getMedianVal(String attrName)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String str = " SUBSTRING_INDEX(\n" + "        SUBSTRING_INDEX(\n" + "            GROUP_CONCAT(" + attrName
				+ " ),\n" + "            ',',\n" + "            ((\n" + "                ROUND(\n"
				+ "                    LENGTH(GROUP_CONCAT( " + attrName + " )) - \n" + "                    LENGTH(\n"
				+ "                        REPLACE(\n" + "                            GROUP_CONCAT( " + attrName
				+ " ),\n" + "                            ',',\n" + "                            ''\n"
				+ "                        )\n" + "                    )\n" + "                ) / 2) + 1\n"
				+ "            )),\n" + "            ',',\n" + "            -1\n" + "        )";

		return str;
	}

	private String getTableName(Datapod dp, ProfileExec profileExec, List<String> datapodList, DagExec dagExec,
			HashMap<String, String> otherParams, RunMode runMode) throws Exception {
		logger.info("otherParams in profile getTablename : " + otherParams);
		if (runMode.equals(RunMode.ONLINE) && datapodList != null && datapodList.contains(dp.getUuid())) {
			return String.format("%s_%s_%s", dp.getUuid().replaceAll("-", "_"), dp.getVersion(), dagExec.getVersion());
		} else if (otherParams.containsKey("datapodUuid_" + dp.getUuid() + "_tableName")) {
			return otherParams.get("datapodUuid_" + dp.getUuid() + "_tableName");
		}
		logger.info(" runMode : " + runMode.toString() + " : datapod : " + dp.getUuid() + " : datapodList.contains(datapod.getUuid()) : " + datapodList.contains(dp.getUuid()));
		datastoreServiceImpl.setRunMode(runMode);
		return datastoreServiceImpl.getTableNameByDatapod(new OrderKey(dp.getUuid(), dp.getVersion()), runMode);
	}
}
