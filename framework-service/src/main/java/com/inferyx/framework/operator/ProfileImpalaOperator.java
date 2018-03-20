package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.MetaType;
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

		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), MetaType.datapod.toString());
		sql = "SELECT \'" + profile.getDependsOn().getRef().getUuid() + "\' AS datapodUUID, \'"
				+ profile.getDependsOn().getRef().getVersion() + "\' AS datapodVersion, "+datapod.getName()+" AS datapodName, cast(" + attrId
				+ " AS string) AS AttributeId, "+attrName+" AS attributeName, min(cast(" + attrName + " AS int)) AS minVal, max(cast(" + attrName
				+ " AS int)) AS maxVal, avg(cast(" + attrName + " AS int)) AS avgVal,appx_median(cast(" + attrName
				+ " AS DOUBLE)) AS mediaVal, " + "stddev(cast(" + attrName
				+ " AS int)) AS stdDev, cast(count(distinct " + attrName
				+ ") AS INT) AS numDistinct, count(distinct " + attrName + ")/count(" + attrName
				+ ")*100 AS perDistinct, cast(count(" + attrName + ") AS INT) AS numNull,count(" + attrName
				+ ") / count(" + attrName + ")*100 AS perNull, count(" + attrName + ") / count(" + attrName
				+ ") AS sixSigma, " + profileExec.getVersion() + " AS version from " + profileTableName;
		logger.info("query is : " + sql);
		return sql;
	}


}
