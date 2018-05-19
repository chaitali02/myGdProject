/**
 * 
 */
package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.enums.RunMode;

/**
 * @author Ganesh
 *
 */
public class ProfilePostGresOperator extends ProfileOperator {
	
	public ProfilePostGresOperator() {
		
	}
	
	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
			String attrName, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String sql = "";

		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), MetaType.datapod.toString());
		sql = "SELECT \'" + profile.getDependsOn().getRef().getUuid() + "\' AS datapodUUID, "
				+ "\'" + profile.getDependsOn().getRef().getVersion() + "\' AS datapodVersion, "
				+ "\'" + datapod.getName()+"\' AS datapodName, "
				+ attrId + " AS AttributeId, "
				+ "\'" + attrName+"\' AS attributeName, "
				+ "(SELECT COUNT(*) FROM " + profileTableName +" tab) AS numRows, "
				+ "MIN(" + attrName + ") AS minVal, "
				+ "MAX(" + attrName + ") AS maxVal, "
				+ "AVG( CAST(" + attrName + " AS INT8)) AS avgVal, "
				+ "PERCENTILE_CONT(" + 0.5 + ") WITHIN GROUP (ORDER BY CAST(" + attrName + " AS INT8)) AS medianVal, "
				+ "STDDEV(" + attrName + ") AS stdDev, "
				+ "COUNT(distinct " + attrName + ") AS numDistinct, "
				+ "COUNT(distinct " + attrName + ")/COUNT(" + attrName + ")*100 AS perDistinct, "
				+ "COUNT(if(" + attrName + " is null,1,0)) AS numNull,"
				+ "COUNT(if(" + attrName + "" + " is null,1,0)) / COUNT(" + attrName + ")*100 AS perNull, "
				+ "COUNT(if(" + attrName + " is null,1,0)) / COUNT(" + attrName + ") AS sixSigma, " 
				+ "CURRENT_DATE AS load_date, " 
				+ "UNIX_TIMESTAMP() AS load_id, "
				+ "'" + profileExec.getVersion() + "' AS version"
				+ " FROM " + profileTableName;
		
    	logger.info("query is : " + sql);
		return sql;
	}
}
