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

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.enums.RunMode;

@Component
public class ProfileOracleOperator extends ProfileOperator {

	public ProfileOracleOperator() {
	}

	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, RunMode runMode)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String sql = "";
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), MetaType.datapod.toString());
		/*Datapod dp = (Datapod) daoRegister.getRefObject(profile.getDependsOn().getRef());
		if (dp.getAttribute(Integer.parseInt(attrId)).getType()!=null 
					&& dp.getAttribute(Integer.parseInt(attrId)).getType().equalsIgnoreCase("string")) {
			sql = "Select \'" + profile.getDependsOn().getRef().getUuid() + "\' as datapodUUID, \'"
					+ profile.getDependsOn().getRef().getVersion() + "\' as datapodVersion,  " + attrId
					+ "  as AttributeId, null as minVal, null as maxVal, null as avgVal ,null as mediaVal, " + "null as stdDev, "
					+ "cast(count(distinct "+ attrName + ") as decimal) as numDistinct, count(distinct " + attrName + ")/count(REPLACE(nvl("
					+ attrName + ",'0'),0 , 1))*100  as perDistinct, cast(count(" + attrName
					+ ") as decimal) as numNull,count(" + attrName + ") / count(REPLACE(nvl(" + attrName
					+ ",'0'),0 , 1))*100 as perNull, count(" + attrName + ") / count(REPLACE(nvl(" + attrName
					+ ",'0'),0 , 1)) as sixSigma, " + profileExec.getVersion() + " as version from " + profileTableName;
		} else {*/
		sql = "SELECT \'" + profile.getDependsOn().getRef().getUuid() + "\' AS datapodUUID, \'"
				+ profile.getDependsOn().getRef().getVersion() + "\' AS datapodVersion,  " 
				+datapod.getName()+" AS datapodName, "
				+ attrId + "  AS AttributeId, "+attrName+" AS attributeName, " 
				+ "(SELECT COUNT(1) FROM "+ profileTableName + " tab) AS numRows, "
				+ "min(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, " + attrName + ", 1)AS int)) AS minVal, "
				+ "max(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, "+ attrName + ", 1)AS int)) AS maxVal, "
				+ "avg(decode(translate(" + attrName + ",' 0123456789',' '), null, "+ attrName + ", '0')) AS avgVal ,"
				+ "median(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, " + attrName + ", 1)AS int)) AS mediaVal, " 
				+ "stddev(decode( translate(" + attrName + ",' 0123456789',' '), null, " + attrName + ", '0')) AS stdDev, "
				+ "cast(count(distinct " + attrName + ") AS decimal) AS numDistinct, "
				+ "count(distinct " + attrName + ")/count(1)*100  AS perDistinct, "
				+ "cast(count(" + attrName + ") as decimal) AS numNull,"				
				+ "sum(if(" + attrName + "" + " is null,1,0)) / count(1)*100 AS perNull, "				
//				+ "count(" + attrName + ") / sum(REPLACE(nvl(" + attrName + ",'0'),0 , 1))*100 AS perNull, "
				+ "min(length(cast(" + attrName + " as CHAR))) as minLength, "
				+ "max(length(cast(" + attrName + " as CHAR))) as maxLength, "
				+ "avg(length(cast(" + attrName + " as CHAR))) as avgLength, "
				+ "(  select count(1) from (SELECT " + attrName + " ,COUNT(1) "  
				+ " FROM " + profileTableName  
				+ " GROUP by " +  attrName 
				+ " HAVING COUNT(" + attrName + ") > 1) t) AS numDuplicates, "  
				+ profileExec.getVersion() + " AS version from " + profileTableName;
		//}
		logger.info("query is : " + sql);
		return sql;
	}

}