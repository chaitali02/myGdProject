package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;

@Component
public class ProfileOracleOperator extends ProfileOperator {

	public ProfileOracleOperator() {
	}

	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, Mode runMode)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String sql = "";
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
		sql = "Select \'" + profile.getDependsOn().getRef().getUuid() + "\' as datapodUUID, \'"
				+ profile.getDependsOn().getRef().getVersion() + "\' as datapodVersion,  " + attrId
				+ "  as AttributeId, min(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, " + attrName
				+ ", 1)as int)) as minVal, max(cast(decode( translate(" + attrName + ",' 0123456789',' '), null, "
				+ attrName + ", 1)as int)) as maxVal, avg(decode(translate(" + attrName + ",' 0123456789',' '), null, "
				+ attrName + ", '0')) as avgVal ,median(cast(decode( translate(" + attrName
				+ ",' 0123456789',' '), null, " + attrName + ", 1)as int)) as mediaVal, " + "stddev(decode( translate("
				+ attrName + ",' 0123456789',' '), null, " + attrName + ", '0')) as stdDev, cast(count(distinct "
				+ attrName + ") as decimal) as numDistinct, count(distinct " + attrName + ")/count(REPLACE(nvl("
				+ attrName + ",'0'),0 , 1))*100  as perDistinct, cast(count(" + attrName
				+ ") as decimal) as numNull,count(" + attrName + ") / count(REPLACE(nvl(" + attrName
				+ ",'0'),0 , 1))*100 as perNull, count(" + attrName + ") / count(REPLACE(nvl(" + attrName
				+ ",'0'),0 , 1)) as sixSigma, " + profileExec.getVersion() + " as version from " + profileTableName;
		//}
		logger.info("query is : " + sql);
		return sql;
	}

}