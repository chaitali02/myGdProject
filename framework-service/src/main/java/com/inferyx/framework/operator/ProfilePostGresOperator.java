/**
 * 
 */
package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataType;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource.DataSourceType;
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
		Attribute attribute = datapod.getAttribute(Integer.parseInt(attrId));
		String attrType = attribute.getType();

		if(!attrType.equalsIgnoreCase("string"))			
			sql = "SELECT \'" + profile.getDependsOn().getRef().getUuid() + "\' AS datapodUUID, "
					+ "\'" + profile.getDependsOn().getRef().getVersion() + "\' AS datapodVersion, '"
					+ datapod.getName()+"' AS datapodName,"
					+ attrId + " AS AttributeId,'"
					+ attrName+"' AS attributeName,"
					+ "(SELECT COUNT(*) FROM " + profileTableName +" tab) AS numRows,"
					+ "MIN(" + attrName + ") AS minVal,"
					+ "MAX(" + attrName + ") AS maxVal,"				
					+ "AVG(" + attrName + ") AS avgVal,"				
					+ "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY " + attrName + ") AS medianVal,"				
					+ "STDDEV(" + attrName + ") AS stdDev,"				
					+ "COUNT(" + attrName + ") AS numDistinct,"
					+ "COUNT(" + attrName + ")/COUNT(" + attrName + ")*100 AS perDistinct,"		
					+ "COUNT(" + attrName + ") AS numNull,"				
					+ "COUNT(" + attrName + ") / (CASE WHEN COUNT(" + attrName + ") IS NULL THEN 1 ELSE count(" + attrName + ") END)*100 AS perNull,"
					+ "COUNT(" + attrName + ") / (CASE WHEN COUNT(" + attrName + ") IS NULL THEN 1 ELSE count(" + attrName + ") END) AS sixSigma," 
					+ "CURRENT_DATE AS load_date, " 
					+ "UNIX_TIMESTAMP() AS load_id, '" + profileExec.getVersion() + "' AS version"
					+ " FROM " + profileTableName;
		else sql = null;
		/*else sql = "SELECT \'" + profile.getDependsOn().getRef().getUuid() + "\' AS datapodUUID, "
				+ "\'" + profile.getDependsOn().getRef().getVersion() + "\' AS datapodVersion, '"
				+ datapod.getName()+"' AS datapodName,"
				+ attrId + " AS AttributeId,'"
				+ attrName+"' AS attributeName,"
				+ "(SELECT COUNT(*) FROM " + profileTableName +" tab) AS numRows,"
				+ "MIN(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS minVal,"
				+ "MAX(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS maxVal,"				
				+ "AVG(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS avgVal,"				
				+ "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS medianVal,"				
				+ "STDDEV(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS stdDev,"				
				+ "COUNT(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS numDistinct,"
				+ "COUNT(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal))/COUNT(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal))*100 AS perDistinct,"		
				+ "COUNT(" + attrName + ") AS numNull,"				
				+ "COUNT(" + attrName + ") / (CASE WHEN COUNT(" + attrName + ") IS NULL THEN 1 ELSE count(" + attrName + ") END)*100 AS perNull,"
				+ "COUNT(" + attrName + ") / (CASE WHEN COUNT(" + attrName + ") IS NULL THEN 1 ELSE count(" + attrName + ") END) AS sixSigma," 
				+ "CURRENT_DATE AS load_date, " 
				+ "UNIX_TIMESTAMP() AS load_id, '" + profileExec.getVersion() + "' AS version"
				+ " FROM " + profileTableName;*/
    	logger.info("query is : " + sql);
		return sql;
	}
}
