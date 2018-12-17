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
public class ProfileImpalaOperator extends ProfileOperator {

	public ProfileImpalaOperator() {
		// TODO Auto-generated constructor stub
	}
	
	
	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, String attrType, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String sql = "";

		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), MetaType.datapod.toString());
		sql = "SELECT "
				+ "'" + profile.getDependsOn().getRef().getUuid() + "' AS datapodUUID, "
				+ "'" + profile.getDependsOn().getRef().getVersion() + "' AS datapodVersion, "
				+ "'" + datapod.getName()+ "' AS datapodName,"
				+ "cast(" + attrId + " AS string) AS AttributeId, "
				+ "'" + attrName + "' AS attributeName, "
				+ "(SELECT COUNT(1) FROM " + profileTableName + " tab) AS numRows, "
				+ "min(cast(" + attrName + " AS int)) AS minVal, "
				+ "max(cast(" + attrName + " AS int)) AS maxVal, "
				+ "avg(cast(" + attrName + " AS int)) AS avgVal, "
				+ "appx_median(cast(" + attrName + " AS DOUBLE)) AS mediaVal, " 
				+ "stddev(cast(" + attrName + " AS int)) AS stdDev, "
				+ "cast(count(distinct " + attrName + ") AS INT) AS numDistinct, "
				+ "count(distinct " + attrName + ")/count(1)*100 AS perDistinct, "
				+ "cast(count(" + attrName + ") AS INT) AS numNull, "
				+ "sum(if(" + attrName + "" + " is null,1,0)) / count(1)*100 AS perNull, "
				//				+ "count(" + attrName + ") / count(1)*100 AS perNull, "
				+ "null AS sixSigma, " 
				+ "'"+ profileExec.getVersion() + "' AS version from " + profileTableName;
		logger.info("query is : " + sql);
		return sql;
	}


}
