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
public class ProfileMySQLOperator extends ProfileOperator {

	public ProfileMySQLOperator() {
	}
	
	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String sql = "";

		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), MetaType.datapod.toString());
		sql = "SELECT '" + profile.getDependsOn().getRef().getUuid() + "' AS datapodUUID, '"
				+ profile.getDependsOn().getRef().getVersion() + "' AS datapodVersion, '"
				+ datapod.getName()+"' AS datapodName, " 
				+ attrId + " AS AttributeId, "
				+ attrName+" AS attributeName, " 
				+ "(SELECT COUNT(*) FROM " + profileTableName +" tab) AS numRows, "
				+ "min(cast(" + attrName + " AS SIGNED)) AS minVal, "
				+ "max(cast(" + attrName + " AS SIGNED)) AS maxVal, "
				+ "avg(" + attrName + ") AS avgVal,"
				+ "cast(" + getMedianVal(attrName) + " AS decimal) AS medianVal, "
				+ "stddev(" + attrName + ") AS stdDev, "
				+ "count(distinct " + attrName + ") AS numDistinct, "
				+ "count(distinct " + attrName + ")/count(" + attrName + ")*100 AS perDistinct, "
				+ "count(if(" + attrName + " is null,1,0)) AS numNull,"
				+ "count(if(" + attrName + " is null,1,0)) / count(" + attrName + ")*100 AS perNull, "
				+ "count(if(" + attrName + " is null,1,0)) / count(" + attrName + ") AS sixSigma, "
				+ "CURDATE() AS load_date, "
				+ "unix_timestamp() AS load_id, '" 
				+ profileExec.getVersion() + "' AS version from " + profileTableName;
    	logger.info("\n query is : " + sql);
		return sql;
	}
	
	private String getMedianVal( String attrName) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String str = "SUBSTRING_INDEX( SUBSTRING_INDEX( GROUP_CONCAT("+attrName+"), ',', "
				+ "((ROUND( LENGTH( GROUP_CONCAT("+attrName+")) - LENGTH"
				+ "(REPLACE(GROUP_CONCAT("+attrName+"), ',', '')))/ 2 )+ 1 ) ), ',', -1)";
		
		return str;
	}

}
