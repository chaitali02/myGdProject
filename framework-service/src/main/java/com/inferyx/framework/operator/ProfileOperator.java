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
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;

@Component
public class ProfileOperator {
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private FilterOperator2 filterOperator2;
	@Autowired
	private Helper helper;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;

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
//		dp = (Datapod) daoRegister.getRefObject(profile.getDependsOn().getRef());
		dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), profile.getDependsOn().getRef().getType().toString(), "N");
		String tableName = datapodServiceImpl.genTableNameByDatapod(dp, dagExec != null ? dagExec.getVersion(): null, datapodList, otherParams, dagExec, runMode, true);
		if (profile.getAttributeInfo() != null) {
//			String tableName = getTableName(dp, profileExec, datapodList, dagExec, otherParams, runMode);
			
			logger.info("getProfileTableName(DP) : " + tableName);
			return generateSql(profile, profileExec, tableName, attrId, 
					dp.getAttribute(Integer.parseInt(attrId)).getName(), dp.getAttribute(Integer.parseInt(attrId)).getType(), runMode);
		} else {
			return generateSql(profile, profileExec, tableName, null,
					dagExec, otherParams, runMode);
		}
	}

	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, String attrtype, RunMode runMode)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException, Exception {
		MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.profile, profile.getUuid(), profile.getVersion()));
		Datasource mapSourceDS = commonServiceImpl.getDatasourceByObject(profile);
		String datasourceType = mapSourceDS.getType();
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), MetaType.datapod.toString());

		ExecContext execContext = helper.getExecutorContext(datasourceType);
		switch(execContext) {
		case HIVE : 
			return "SELECT \'"
				    + profileExec.getUuid() + "\' AS rule_exec_uuid, \'"
					+ profileExec.getVersion()+ "\' AS rule_exec_version, '"
					+ System.currentTimeMillis() + "' AS rule_exec_time, \'" 
					+ profile.getUuid() + "\' AS rule_uuid, \'"
					+ profile.getVersion()+ "\' AS rule_version, \'"
					+ profile.getName() + "' AS rule_name, \'" 
				    
			        + datapod.getUuid() + "\' AS datapoduuid, \'"
					+ datapod.getVersion() + "\' AS datapodversion, '"
					+ datapod.getName() + "' AS datapodname, " 
					+ attrId + " AS attributeid, "
					+ "'" + attrName + "' AS attributename, " 
					+ "count(1) AS numrows, "
					+ "min(" + attrName + ") AS minval, "
					+ "max(" + attrName + ") AS maxval, "
					+ "avg(cast(" + attrName + " AS int)) AS avgval, "
					+ "percentile(cast(" + attrName + " as BIGINT), 0.5) AS medianval, "
					+ "stddev(" + attrName + ") AS stddev, "
					+ "count(distinct " + attrName + ") AS numdistinct, "
					+ "count(distinct " + attrName + ")/count(1)*100 AS perdistinct, "
					+ "sum(if(" + attrName + " is null,1,0)) AS numnull,"
					+ "sum(if(" + attrName + "" + " is null,1,0)) / count(1)*100 AS pernull, "
					+ "min(length(cast(" + attrName + " as string))) as minlength, "
					+ "max(length(cast(" + attrName + " as string))) as maxlength, "
					+ "avg(length(cast(" + attrName + " as string))) as avglength, "
					+ "(SELECT count(1) FROM (SELECT " + attrName + " , COUNT(1) "
							+ " FROM " + profileTableName  
							+ " GROUP BY " +  attrName 
							+ " HAVING COUNT(" + attrName + ") > 1) t) AS numduplicates, "
					+ "'" + profileExec.getVersion() + "' AS version "
					+ " FROM " + profileTableName 
					+ " " + datapod.getName()
					+ " WHERE 1=1 "
					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);
		case FILE : 
			return "SELECT \'"
				    + profileExec.getUuid() + "\' AS rule_exec_uuid, \'"
					+ profileExec.getVersion()+ "\' AS rule_exec_version, '"
					+ System.currentTimeMillis() + "' AS rule_exec_time, \'" 
					+ profile.getUuid() + "\' AS rule_uuid, \'"
					+ profile.getVersion()+ "\' AS rule_version, \'"
					+ profile.getName() + "' AS rule_name, \'" 
						
		            + datapod.getUuid() + "\' AS datapoduuid, \'"
					+ datapod.getVersion() + "\' AS datapodversion, '"
					+ datapod.getName() + "' AS datapodname, " 
					+ attrId + " AS attributeid, "
					+ "'" + attrName + "' AS attributename, " 
					+ "count(1) AS numrows, "
					+ "min(" + attrName + ") AS minval, "
					+ "max(" + attrName + ") AS maxval, "
					+ "avg(cast(" + attrName + " AS int)) AS avgval, "
					+ "percentile(cast(" + attrName + " as BIGINT), 0.5) AS medianval, "
					+ "stddev(" + attrName + ") AS stddev, "
					+ "count(distinct " + attrName + ") AS numdistinct, "
					+ "count(distinct " + attrName + ")/count(1)*100 AS perdistinct, "
					+ "sum(if(" + attrName + " is null,1,0)) AS numnull,"
					+ "sum(if(" + attrName + "" + " is null,1,0)) / count(1)*100 AS pernull, "
					+ "min(length(cast(" + attrName + " as string))) as minlength, "
					+ "max(length(cast(" + attrName + " as string))) as maxlength, "
					+ "avg(length(cast(" + attrName + " as string))) as avglength, "
					+ "(SELECT count(1) FROM (SELECT " + attrName + " ,COUNT(1) "  
							+ " FROM " + profileTableName  
							+ " GROUP BY " +  attrName 
							+ " HAVING COUNT(" + attrName + ") > 1) t) AS numduplicates, '" 
					+ profileExec.getVersion()+ "' AS version "
					+ " FROM " + profileTableName 
					+ " " + datapod.getName()
					+ " WHERE 1=1 "
					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);
		case IMPALA : 
			return "SELECT "
					+ profileExec.getUuid() + "\' AS rule_exec_uuid, \'"
				    + profileExec.getVersion()+ "\' AS rule_exec_version, '"
					+ System.currentTimeMillis() + "' AS rule_exec_time, \'" 
					+ profile.getUuid() + "\' AS rule_uuid, \'"
					+ profile.getVersion()+ "\' AS rule_version, \'"
					+ profile.getName() + "' AS rule_name, \'" 
					+ "'" + datapod.getUuid() + "' AS datapoduuid, "
					+ "'" + datapod.getVersion() + "' AS datapodVersion, "
					+ "'" + datapod.getName()+ "' AS datapodname,"
					+ "cast(" + attrId + " AS string) AS attributeid, "
					+ "'" + attrName + "' AS attributename, "
					+ "COUNT(1)" +" AS numrows, "
					+ "min(cast(" + attrName + " AS int)) AS minval, "
					+ "max(cast(" + attrName + " AS int)) AS maxval, "
					+ "avg(cast(" + attrName + " AS int)) AS avgval, "
					+ "appx_median(cast(" + attrName + " AS DOUBLE)) AS mediaval, " 
					+ "stddev(cast(" + attrName + " AS int)) AS stddev, "
					+ "cast(count(distinct " + attrName + ") AS INT) AS numdistinct, "
					+ "count(distinct " + attrName + ")/count(1)*100 AS perdistinct, "
					+ "cast(count(" + attrName + ") AS INT) AS numnull, "
					+ "sum(if(" + attrName + "" + " is null,1,0)) / count(1)*100 AS pernull, "
					+ "min(length(cast(" + attrName + " as string))) as minlength, "
					+ "max(length(cast(" + attrName + " as string))) as maxlength, "
					+ "avg(length(cast(" + attrName + " as string))) as avglength, "
					+ "(COUNT(*) - COUNT(DISTINCT "+attrName+")) AS numduplicates, "
					+ "'"+ profileExec.getVersion() + "' AS version "
					+ " FROM " + profileTableName
					+ " " + datapod.getName()
					+ " WHERE 1=1 "
					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);
		case MYSQL : 
			return "SELECT '"   
		            + profileExec.getUuid() + "\' AS rule_exec_uuid, \'"
					+ profileExec.getVersion()+ "\' AS rule_exec_version, '"
					+ System.currentTimeMillis() + "' AS rule_exec_time, \'" 
					+ profile.getUuid() + "\' AS rule_uuid, \'"
					+ profile.getVersion()+ "\' AS rule_version, \'"
					+ profile.getName() + "' AS rule_name, \'" 
					
		            + datapod.getUuid() + "' AS datapoduuid, '"
					+ datapod.getVersion() + "' AS datapodVersion, '"
					+ datapod.getName()+"' AS datapodname, " 
					+ attrId + " AS attributeid, '"
					+ attrName+"' AS attributeName, " 
					+ "(SELECT COUNT(1) FROM " + profileTableName +" tab) AS numrows, "
					+ "min(cast(" + attrName + " AS SIGNED)) AS minval, "
					+ "max(cast(" + attrName + " AS SIGNED)) AS maxval, "
					+ "avg(" + attrName + ") AS avgVal,"
					+ "cast(" + getMedianVal(attrName) + " AS decimal) AS medianval, "
					+ "stddev(" + attrName + ") AS stddev, "
					+ "count(distinct " + attrName + ") AS numdistinct, "
					+ "count(distinct " + attrName + ")/count(1)*100 AS perdistinct, "
					+ "sum(if(" + attrName + " is null,1,0)) AS numnull,"
					+ "sum(if(" + attrName + " is null,1,0)) / count(1)*100 AS pernull, "
					+ "min(length(cast(" + attrName + " as CHAR))) as minlength, "
					+ "max(length(cast(" + attrName + " as CHAR))) as maxlength, "
					+ "avg(length(cast(" + attrName + " as CHAR))) as avglength, "
					+ "(SELECT count(1) FROM (SELECT " + attrName + " , COUNT(1) FROM " + profileTableName + " " + datapod.getName()
							+ " GROUP BY " +  attrName 
							+ " HAVING COUNT(" + attrName + ") > 1) t) AS numduplicates, '"  
					+ profileExec.getVersion() + "' AS version from " + profileTableName
					+ " WHERE 1=1 "
					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);
		case ORACLE : 
			return "SELECT \'" 
				    + profileExec.getUuid() + "\' AS rule_exec_uuid, \'"
					+ profileExec.getVersion()+ "\' AS rule_exec_version, '"
					+ System.currentTimeMillis() + "' AS rule_exec_time, \'" 
					+ profile.getUuid() + "\' AS rule_uuid, \'"
					+ profile.getVersion()+ "\' AS rule_version, \'"
					+ profile.getName() + "' AS rule_name, \'" 
					
		            + datapod.getUuid() + "\' AS datapoduuid, \'"
					+ datapod.getVersion() + "\' AS datapodVersion, "
					+ " '" + datapod.getName()+"' AS datapodname, "
					+ attrId + "  AS attributeid,"
					+ " '"+attrName+"' AS attributename, " 
					+ "(SELECT COUNT(1) FROM "+ profileTableName + " tab) AS numrows, "
					+ "min(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, " + attrName + ", 1)AS int)) AS minval, "
					+ "max(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, "+ attrName + ", 1)AS int)) AS maxval, "
					+ "avg(decode(translate(" + attrName + ",' 0123456789',' '), null, "+ attrName + ", '0')) AS avgval ,"
					+ "median(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, " + attrName + ", 1)AS int)) AS mediaval, " 
					+ "stddev(decode( translate(" + attrName + ",' 0123456789',' '), null, " + attrName + ", '0')) AS stddev, "
					+ "cast(count(distinct " + attrName + ") AS decimal) AS numdistinct, "
					+ "count(distinct " + attrName + ")/count(1)*100  AS perdistinct, "
					+ "cast(count(" + attrName + ") as decimal) AS numnull,"				
					+ "sum(CASE WHEN " + attrName + " IS NULL THEN 0 ELSE 1 END) / count(1)*100 AS pernull, "				
//					+ "count(" + attrName + ") / sum(REPLACE(nvl(" + attrName + ",'0'),0 , 1))*100 AS perNull, "
					+ "min(length(cast(" + attrName + " as CHAR))) as minlength, "
					+ "max(length(cast(" + attrName + " as CHAR))) as maxlength, "
					+ "avg(length(cast(" + attrName + " as CHAR))) as avglength, "
					+ "(SELECT count(1) FROM (SELECT " + attrName + " ,COUNT(1) "  
							+ " FROM " + profileTableName  
							+ " GROUP BY " +  attrName 
							+ " HAVING COUNT(" + attrName + ") > 1) t) AS numduplicates, '"  
					+ profileExec.getVersion() + "' AS version from " + profileTableName
					+ " " + datapod.getName()
					+ " WHERE 1=1 "
					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS)
					+ " GROUP BY "+attrName;
		case POSTGRES : 
			String attrName1 = " cast(regexp_replace(COALESCE(NULLIF(cast(" + attrName + " as text),''),'0'), '[^0-9]+', '0', 'g') as decimal) ";
			return "SELECT '" 
				    + profileExec.getUuid() + "\' AS rule_exec_uuid, \'"
					+ profileExec.getVersion()+ "\' AS rule_exec_version, '"
					+ System.currentTimeMillis() + "' AS rule_exec_time, \'" 
					+ profile.getUuid() + "\' AS rule_uuid, \'"
					+ profile.getVersion()+ "\' AS rule_version, \'"
					+ profile.getName() + "' AS rule_name, \'" 
					
					
					+ datapod.getUuid() + "' AS datapoduuid, "
					+ "'" + datapod.getVersion() + "' AS datapodVersion, '"
					+ datapod.getName()+"' AS datapodname,"
					+ attrId + " AS attributeid,'"
					+ attrName+"' AS attributename,"
					+ "(SELECT COUNT(1) FROM " + profileTableName +" tab) AS numrows,"
					+ "MIN(" + attrName1 + ") AS minval,"
					+ "MAX(" + attrName1 + ") AS maxval,"				
					+ "AVG(" + attrName1 + ") AS avgval,"				
					+ "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY "+ attrName1 + ") AS medianval,"				
					+ "STDDEV(" + attrName1 + ") AS stddev,"				
					+ "COUNT(" + attrName1 + ") AS numdistinct,"
					+ "COUNT(distinct " + attrName1 + ")/COUNT(1)*100 AS perdistinct,"		
					+ "COUNT(" + attrName1 + ") AS numnull,"				
//					+ "sum(if(" + attrName + "" + " is null,1,0)) / count(1)*100 AS perNull, "
					+ "COUNT(" + attrName + ") / (CASE WHEN COUNT(" + attrName + ") IS NULL THEN 1 ELSE count(" + attrName + ") END)*100 AS pernull,"
					+ "min(length(" + attrName + " :: varchar)) as minlength, "
					+ "max(length(" + attrName + " :: varchar)) as maxlength, "
					+ "avg(length(" + attrName + " :: varchar)) as avglength, "
					+ "(SELECT count(1) FROM (SELECT " + attrName + " ,COUNT(1) "  
							+ " FROM " + profileTableName  
							+ " GROUP BY " +  attrName 
							+ " HAVING COUNT(" + attrName + ") > 1) t) AS numduplicates, '" 
					+ profileExec.getVersion() + "' AS version"
					+ " FROM " + profileTableName
					+ " " + datapod.getName()
					+ " WHERE 1=1 "
					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);
		default:
			return null;
		}
//		if(datasourceType.equalsIgnoreCase(ExecContext.HIVE.toString())
//				|| datasourceType.equalsIgnoreCase(ExecContext.FILE.toString())) {
//			sql = "SELECT \'" + profile.getDependsOn().getRef().getUuid() + "\' AS datapodUUID, \'"
//					+ profile.getDependsOn().getRef().getVersion() + "\' AS datapodVersion, '"
//					+ datapod.getName() + "' AS datapodName, " + attrId
//					+ " AS attributeId, '" + attrName + "' AS attributeName, " 
//					+ "count(1) AS numRows, "
//					+ "min(" + attrName + ") AS minVal, "
//					+ "max(" + attrName + ") AS maxVal, "
//					+ "avg(cast(" + attrName + " AS int)) AS avgVal, "
//					+ "percentile(cast(" + attrName + " as BIGINT), 0.5) AS medianVal, "
//					+ "stddev(" + attrName + ") AS stdDev, "
//					+ "count(distinct " + attrName + ") AS numDistinct, "
//					+ "count(distinct " + attrName + ")/count(1)*100 AS perDistinct, "
//					+ "sum(if(" + attrName + " is null,1,0)) AS numNull,"
//					+ "sum(if(" + attrName + "" + " is null,1,0)) / count(1)*100 AS perNull, "
//					+ "min(length(cast(" + attrName + " as string))) as minLength, "
//					+ "max(length(cast(" + attrName + " as string))) as maxLength, "
//					+ "avg(length(cast(" + attrName + " as string))) as avgLength, "
//					+ "(  select count(1) from (SELECT " + attrName + " ,COUNT(1) "  
//					+ " FROM " + profileTableName  
//					+ " GROUP by " +  attrName 
//					+ " HAVING COUNT(" + attrName + ") > 1) t) AS numDuplicates, '" 
//					+ profileExec.getVersion()+ "' AS version from " + profileTableName 
//					+ " " + datapod.getName()
//					+ " WHERE 1=1 "
//					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);
//		} else if(datasourceType.equalsIgnoreCase(ExecContext.IMPALA.toString())) {
//			sql = "SELECT "
//					+ "'" + profile.getDependsOn().getRef().getUuid() + "' AS datapodUUID, "
//					+ "'" + profile.getDependsOn().getRef().getVersion() + "' AS datapodVersion, "
//					+ "'" + datapod.getName()+ "' AS datapodName,"
//					+ "cast(" + attrId + " AS string) AS attributeId, "
//					+ "'" + attrName + "' AS attributeName, "
//					+ "(SELECT COUNT(1) FROM " + profileTableName + " tab) AS numRows, "
//					+ "min(cast(" + attrName + " AS int)) AS minVal, "
//					+ "max(cast(" + attrName + " AS int)) AS maxVal, "
//					+ "avg(cast(" + attrName + " AS int)) AS avgVal, "
//					+ "appx_median(cast(" + attrName + " AS DOUBLE)) AS mediaVal, " 
//					+ "stddev(cast(" + attrName + " AS int)) AS stdDev, "
//					+ "cast(count(distinct " + attrName + ") AS INT) AS numDistinct, "
//					+ "count(distinct " + attrName + ")/count(1)*100 AS perDistinct, "
//					+ "cast(count(" + attrName + ") AS INT) AS numNull, "
//					+ "sum(if(" + attrName + "" + " is null,1,0)) / count(1)*100 AS perNull, "
//					//				+ "count(" + attrName + ") / count(1)*100 AS perNull, "
//					+ "null AS sixSigma, " 
//					+ "'"+ profileExec.getVersion() + "' AS version from " + profileTableName
//					+ " " + datapod.getName()
//					+ " WHERE 1=1 "
//					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);			
//		} else if (datasourceType.equalsIgnoreCase(ExecContext.MYSQL.toString())) {
//			sql = "SELECT '" + profile.getDependsOn().getRef().getUuid() + "' AS datapodUUID, '"
//					+ profile.getDependsOn().getRef().getVersion() + "' AS datapodVersion, '"
//					+ datapod.getName()+"' AS datapodName, " 
//					+ attrId + " AS attributeId, '"
//					+ attrName+"' AS attributeName, " 
//					+ "(SELECT COUNT(1) FROM " + profileTableName +" tab) AS numRows, "
//					+ "min(cast(" + attrName + " AS SIGNED)) AS minVal, "
//					+ "max(cast(" + attrName + " AS SIGNED)) AS maxVal, "
//					+ "avg(" + attrName + ") AS avgVal,"
//					+ "cast(" + getMedianVal(attrName) + " AS decimal) AS medianVal, "
//					+ "stddev(" + attrName + ") AS stdDev, "
//					+ "count(distinct " + attrName + ") AS numDistinct, "
//					+ "count(distinct " + attrName + ")/count(1)*100 AS perDistinct, "
//					+ "sum(if(" + attrName + " is null,1,0)) AS numNull,"
//					+ "sum(if(" + attrName + " is null,1,0)) / count(1)*100 AS perNull, "
//					+ "min(length(cast(" + attrName + " as CHAR))) as minLength, "
//					+ "max(length(cast(" + attrName + " as CHAR))) as maxLength, "
//					+ "avg(length(cast(" + attrName + " as CHAR))) as avgLength, "
//					+ "(  select count(1) from (SELECT " + attrName + " ,COUNT(1) "  
//					+ " FROM " + profileTableName  
//					+ " " + datapod.getName()
//					+ " GROUP by " +  attrName 
//					+ " HAVING COUNT(" + attrName + ") > 1) t) AS numDuplicates, '"  
//					+ profileExec.getVersion() + "' AS version from " + profileTableName
//					+ " WHERE 1=1 "
//					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);
//		} else if (datasourceType.equalsIgnoreCase(ExecContext.ORACLE.toString())) {
//			sql = "SELECT \'" + profile.getDependsOn().getRef().getUuid() + "\' AS datapodUUID, \'"
//					+ profile.getDependsOn().getRef().getVersion() + "\' AS datapodVersion, "
//					+ " '" + datapod.getName()+"' AS datapodName, "
//					+ attrId + "  AS attributeId,"
//					+ " '"+attrName+"' AS attributeName, " 
//					+ "(SELECT COUNT(1) FROM "+ profileTableName + " tab) AS numRows, "
//					+ "min(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, " + attrName + ", 1)AS int)) AS minVal, "
//					+ "max(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, "+ attrName + ", 1)AS int)) AS maxVal, "
//					+ "avg(decode(translate(" + attrName + ",' 0123456789',' '), null, "+ attrName + ", '0')) AS avgVal ,"
//					+ "median(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, " + attrName + ", 1)AS int)) AS mediaVal, " 
//					+ "stddev(decode( translate(" + attrName + ",' 0123456789',' '), null, " + attrName + ", '0')) AS stdDev, "
//					+ "cast(count(distinct " + attrName + ") AS decimal) AS numDistinct, "
//					+ "count(distinct " + attrName + ")/count(1)*100  AS perDistinct, "
//					+ "cast(count(" + attrName + ") as decimal) AS numNull,"				
//					+ "sum(CASE WHEN " + attrName + " IS NULL THEN 0 ELSE 1 END) / count(1)*100 AS perNull, "				
////					+ "count(" + attrName + ") / sum(REPLACE(nvl(" + attrName + ",'0'),0 , 1))*100 AS perNull, "
//					+ "min(length(cast(" + attrName + " as CHAR))) as minLength, "
//					+ "max(length(cast(" + attrName + " as CHAR))) as maxLength, "
//					+ "avg(length(cast(" + attrName + " as CHAR))) as avgLength, "
//					+ "(  select count(1) from (SELECT " + attrName + " ,COUNT(1) "  
//					+ " FROM " + profileTableName  
//					+ " GROUP by " +  attrName 
//					+ " HAVING COUNT(" + attrName + ") > 1) t) AS numDuplicates, '"  
//					+ profileExec.getVersion() + "' AS version from " + profileTableName
//					+ " " + datapod.getName()
//					+ " WHERE 1=1 "
//					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS)
//					+ "GROUP BY "+attrName;
//		} else if (datasourceType.equalsIgnoreCase(ExecContext.POSTGRES.toString())) { 
//			String attrName1 = " cast(regexp_replace(COALESCE(NULLIF(cast(" + attrName + " as text),''),'0'), '[^0-9]+', '0', 'g') as decimal) ";
//			sql = "SELECT '" + profile.getDependsOn().getRef().getUuid() + "' AS datapodUUID, "
//					+ "'" + profile.getDependsOn().getRef().getVersion() + "' AS datapodVersion, '"
//					+ datapod.getName()+"' AS datapodName,"
//					+ attrId + " AS attributeId,'"
//					+ attrName+"' AS attributeName,"
//					+ "(SELECT COUNT(1) FROM " + profileTableName +" tab) AS numRows,"
//					+ "MIN(" + attrName1 + ") AS minVal,"
//					+ "MAX(" + attrName1 + ") AS maxVal,"				
//					+ "AVG(" + attrName1 + ") AS avgVal,"				
//					+ "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY "+ attrName1 + ") AS medianVal,"				
//					+ "STDDEV(" + attrName1 + ") AS stdDev,"				
//					+ "COUNT(" + attrName1 + ") AS numDistinct,"
//					+ "COUNT(distinct " + attrName1 + ")/COUNT(1)*100 AS perDistinct,"		
//					+ "COUNT(" + attrName1 + ") AS numNull,"				
////					+ "sum(if(" + attrName + "" + " is null,1,0)) / count(1)*100 AS perNull, "
//					+ "COUNT(" + attrName + ") / (CASE WHEN COUNT(" + attrName + ") IS NULL THEN 1 ELSE count(" + attrName + ") END)*100 AS perNull,"
//					+ "min(length(" + attrName + " :: varchar)) as minLength, "
//					+ "max(length(" + attrName + " :: varchar)) as maxLength, "
//					+ "avg(length(" + attrName + " :: varchar)) as avgLength, "
//					+ "(  select count(1) from (SELECT " + attrName + " ,COUNT(1) "  
//					+ " FROM " + profileTableName  
//					+ " GROUP by " +  attrName 
//					+ " HAVING COUNT(" + attrName + ") > 1) t) AS numDuplicates, '" 
//					+ profileExec.getVersion() + "' AS version"
//					+ " FROM " + profileTableName
//					+ " " + datapod.getName()
//					+ " WHERE 1=1 "
//					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);
//		} 
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

	/********************** UNUSED **********************/
//	private String getTableName(Datapod dp, ProfileExec profileExec, List<String> datapodList, DagExec dagExec,
//			HashMap<String, String> otherParams, RunMode runMode) throws Exception {
//		logger.info("otherParams in profile getTablename : " + otherParams);
//		if (runMode.equals(RunMode.ONLINE) && datapodList != null && datapodList.contains(dp.getUuid())) {
//			return String.format("%s_%s_%s", dp.getUuid().replaceAll("-", "_"), dp.getVersion(), dagExec.getVersion());
//		} else if (otherParams!= null && otherParams.containsKey("datapodUuid_" + dp.getUuid() + "_tableName")) {
//			return otherParams.get("datapodUuid_" + dp.getUuid() + "_tableName");
//		}
//		//logger.info(" runMode : " + runMode.toString() + " : datapod : " + dp.getUuid() + " : datapodList.contains(datapod.getUuid()) : " + datapodList.contains(dp.getUuid()));
//		datastoreServiceImpl.setRunMode(runMode);
//		return datapodServiceImpl.getTableNameByDatapod(new OrderKey(dp.getUuid(), dp.getVersion()), runMode);
//	}
}
