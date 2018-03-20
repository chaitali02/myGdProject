package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;

@Component
public class ProfileHiveOperator extends ProfileOperator {

	public ProfileHiveOperator() {
		// TODO Auto-generated constructor stub
	}

	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, Mode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String sql = "";
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), MetaType.datapod.toString());
		sql = "SELECT \'" + profile.getDependsOn().getRef().getUuid() + "\' AS datapodUUID, \'"
				+ profile.getDependsOn().getRef().getVersion() + "\' AS datapodVersion, '"+ datapod.getName() + "' AS datapodName, " + attrId
				+ " AS AttributeId, '" + attrName + "' AS attributeName, min(" + attrName + ") as minVal, max(" + attrName + ") AS maxVal, avg(cast("
				+ attrName + " AS int)) AS avgVal, percentile(cast(" + attrName
				+ " as BIGINT), 0.5) AS medianVal, stddev(" + attrName + ") AS stdDev, count(distinct " + attrName
				+ ") AS numDistinct, count(distinct " + attrName + ")/count(" + attrName
				+ ")*100 AS perDistinct, count(if(" + attrName + " is null,1,0)) AS numNull,count(if(" + attrName
				+ "" + " is null,1,0)) / count(" + attrName + ")*100 AS perNull, count(if(" + attrName
				+ " is null,1,0)) / count(" + attrName + ") AS sixSigma, '" + profileExec.getVersion()
				+ "' AS version from " + profileTableName;
		logger.info("query is : " + sql);
		return sql;
	}
	
}
