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
public class ProfileSqlOperator extends ProfileOperator {

	public ProfileSqlOperator() {
	}
	
	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, Mode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String sql = "";

		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), MetaType.datapod.toString());
		sql = "SELECT \'" + profile.getDependsOn().getRef().getUuid() + "\' AS datapodUUID, \'"
				+ profile.getDependsOn().getRef().getVersion() + "\' AS datapodVersion, "+datapod.getName()+" AS datapodName, " + attrId
				+ " AS AttributeId, "+attrName+" AS attributeName, min(cast(" + attrName + " AS SIGNED)) AS minVal, max(cast(" + attrName
				+ " AS SIGNED)) AS maxVal, avg(" + attrName + ") AS avgVal,cast(" + getMedianVal(attrName)
				+ " AS decimal) AS medianVal, stddev(" + attrName + ") AS stdDev, count(distinct " + attrName
				+ ") AS numDistinct, count(distinct " + attrName + ")/count(" + attrName
				+ ")*100 AS perDistinct, count(if(" + attrName + " is null,1,0)) AS numNull,count(if(" + attrName
				+ "" + " is null,1,0)) / count(" + attrName + ")*100 AS perNull, count(if(" + attrName
				+ " is null,1,0)) / count(" + attrName + ") AS sixSigma, '" + profileExec.getVersion()
				+ "' AS version from " + profileTableName;
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
