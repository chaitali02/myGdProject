package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;

@Component
public class ProfileImpalaOperator extends ProfileOperator {

	public ProfileImpalaOperator() {
		// TODO Auto-generated constructor stub
	}
	
	
	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, Mode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String sql = "";

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
		logger.info("query is : " + sql);
		return sql;
	}


}
