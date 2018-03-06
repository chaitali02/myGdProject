package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
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
		logger.info("query is : " + sql);
		return sql;
	}
	
}
