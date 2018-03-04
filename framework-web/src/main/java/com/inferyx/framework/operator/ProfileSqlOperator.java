package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;

@Component
public class ProfileSqlOperator extends ProfileOperator {

	public ProfileSqlOperator() {
	}
	
	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, Mode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String sql = "";

		sql = "Select \'" + profile.getDependsOn().getRef().getUuid() + "\' as datapodUUID, \'"
				+ profile.getDependsOn().getRef().getVersion() + "\' as datapodVersion, " + attrId
				+ " as AttributeId, min(cast(" + attrName + " as SIGNED)) as minVal, max(cast(" + attrName
				+ " as SIGNED)) as maxVal, avg(" + attrName + ") as avgVal,cast(" + getMedianVal(attrName)
				+ " as decimal) as medianVal, stddev(" + attrName + ") as stdDev, count(distinct " + attrName
				+ ") as numDistinct, count(distinct " + attrName + ")/count(" + attrName
				+ ")*100 as perDistinct, count(if(" + attrName + " is null,1,0)) as numNull,count(if(" + attrName
				+ "" + " is null,1,0)) / count(" + attrName + ")*100 as perNull, count(if(" + attrName
				+ " is null,1,0)) / count(" + attrName + ") as sixSigma, '" + profileExec.getVersion()
				+ "' as version from " + profileTableName;
    	logger.info("query is : " + sql);
		return sql;
	}
	
	private String getMedianVal( String attrName) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String str = " SUBSTRING_INDEX(\n" + 
				"        SUBSTRING_INDEX(\n" + 
				"            GROUP_CONCAT(" + attrName +" ),\n" + 
				"            ',',\n" + 
				"            ((\n" + 
				"                ROUND(\n" + 
				"                    LENGTH(GROUP_CONCAT( " + attrName +" )) - \n" + 
				"                    LENGTH(\n" + 
				"                        REPLACE(\n" + 
				"                            GROUP_CONCAT( " + attrName +" ),\n" + 
				"                            ',',\n" + 
				"                            ''\n" + 
				"                        )\n" + 
				"                    )\n" + 
				"                ) / 2) + 1\n" + 
				"            )),\n" + 
				"            ',',\n" + 
				"            -1\n" + 
				"        )";
		
		return str;
	}

}
