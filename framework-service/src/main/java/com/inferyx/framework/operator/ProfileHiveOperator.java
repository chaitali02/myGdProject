///*******************************************************************************
// * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
// *
// * This unpublished material is proprietary to Inferyx Inc.
// * The methods and techniques described herein are considered  trade 
// * secrets and/or confidential. Reproduction or distribution, in whole or 
// * in part, is forbidden.
// *
// * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
// *******************************************************************************/
//package com.inferyx.framework.operator;
//
//import java.util.HashSet;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.inferyx.framework.domain.Datapod;
//import com.inferyx.framework.domain.Datasource;
//import com.inferyx.framework.domain.MetaIdentifier;
//import com.inferyx.framework.domain.MetaIdentifierHolder;
//import com.inferyx.framework.domain.MetaType;
//import com.inferyx.framework.domain.Profile;
//import com.inferyx.framework.domain.ProfileExec;
//import com.inferyx.framework.enums.RunMode;
//
//@Component
//public class ProfileHiveOperator extends ProfileOperator {
//
//	@Autowired
//	FilterOperator2 filterOperator2;
//	public ProfileHiveOperator() {
//		// TODO Auto-generated constructor stub
//	}
//
//	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
//			String attrName, String attrType, RunMode runMode) throws Exception {
//			String sql = "";
//			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), MetaType.datapod.toString());
//			MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.profile, profile.getUuid(), profile.getVersion()));
//			Datasource mapSourceDS = commonServiceImpl.getDatasourceByObject(profile);
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
//			
//			logger.info("Inside profile HIVE operator query is : " + sql);
//			return sql;
//		}
//	}
