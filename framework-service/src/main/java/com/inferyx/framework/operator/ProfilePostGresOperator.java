///**
// * 
// */
//package com.inferyx.framework.operator;
//
//import java.util.HashSet;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.inferyx.framework.domain.Attribute;
//import com.inferyx.framework.domain.Datapod;
//import com.inferyx.framework.domain.Datasource;
//import com.inferyx.framework.domain.MetaIdentifier;
//import com.inferyx.framework.domain.MetaIdentifierHolder;
//import com.inferyx.framework.domain.MetaType;
//import com.inferyx.framework.domain.Profile;
//import com.inferyx.framework.domain.ProfileExec;
//import com.inferyx.framework.enums.RunMode;
//
///**
// * @author Ganesh
// *
// */
//@Service
//public class ProfilePostGresOperator extends ProfileOperator {
//
//	@Autowired
//	FilterOperator2 filterOperator2;
//	public ProfilePostGresOperator() {
//		
//	}
//	
//	public String generateSql(Profile profile, ProfileExec profileExec, String profileTableName, String attrId,
//			String attrName, String attrType, RunMode runMode) throws Exception {
//		String sql = "";
//
//		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(profile.getDependsOn().getRef().getUuid(), profile.getDependsOn().getRef().getVersion(), MetaType.datapod.toString());
//		Attribute attribute = datapod.getAttribute(Integer.parseInt(attrId));
//		attrType = attribute.getType();
//		String attrName1 = " cast(regexp_replace(COALESCE(NULLIF(cast(" + attrName + " as text),''),'0'), '[^0-9]+', '0', 'g') as decimal) ";	
//		MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.profile, profile.getUuid(), profile.getVersion()));
//		Datasource mapSourceDS = commonServiceImpl.getDatasourceByObject(profile);
////		if(!attrType.equalsIgnoreCase("string"))			
//			sql = "SELECT '" + profile.getDependsOn().getRef().getUuid() + "' AS datapodUUID, "
//					+ "'" + profile.getDependsOn().getRef().getVersion() + "' AS datapodVersion, '"
//					+ datapod.getName()+"' AS datapodName,"
//					+ attrId + " AS attributeId,'"
//					+ attrName+"' AS attributeName,"
//					+ "(SELECT COUNT(1) FROM " + profileTableName +" tab) AS numRows,"
//					+ "MIN(" + attrName1 + ") AS minVal,"
//					+ "MAX(" + attrName1 + ") AS maxVal,"				
//					+ "AVG(" + attrName1 + ") AS avgVal,"				
//					+ "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY "+ attrName1 + ") AS medianVal,"				
//					+ "STDDEV(" + attrName1 + ") AS stdDev,"				
//					+ "COUNT(" + attrName1 + ") AS numDistinct,"
//					+ "COUNT(distinct " + attrName1 + ")/COUNT(1)*100 AS perDistinct,"		
//					+ "COUNT(" + attrName1 + ") AS numNull,"				
////					+ "sum(if(" + attrName + "" + " is null,1,0)) / count(1)*100 AS perNull, "
//					+ "COUNT(" + attrName + ") / (CASE WHEN COUNT(" + attrName + ") IS NULL THEN 1 ELSE count(" + attrName + ") END)*100 AS perNull,"
//					+ "min(length(" + attrName + " :: varchar)) as minLength, "
//					+ "max(length(" + attrName + " :: varchar)) as maxLength, "
//					+ "avg(length(" + attrName + " :: varchar)) as avgLength, "
//					+ "(  select count(1) from (SELECT " + attrName + " ,COUNT(1) "  
//					+ " FROM " + profileTableName  
//					+ " GROUP by " +  attrName 
//					+ " HAVING COUNT(" + attrName + ") > 1) t) AS numDuplicates, '" 
//					+ profileExec.getVersion() + "' AS version"
//					+ " FROM " + profileTableName
//					+ " " + datapod.getName()
//					+ " WHERE 1=1 "
//					+ filterOperator2.generateSql(profile.getFilterInfo(), null, filterSource, null, new HashSet<>(), profileExec.getExecParams(), false, false, runMode, mapSourceDS);
////		else 
////			sql = null;
//			
//		/*else sql = "SELECT \'" + profile.getDependsOn().getRef().getUuid() + "\' AS datapodUUID, "
//				+ "\'" + profile.getDependsOn().getRef().getVersion() + "\' AS datapodVersion, '"
//				+ datapod.getName()+"' AS datapodName,"
//				+ attrId + " AS AttributeId,'"
//				+ attrName+"' AS attributeName,"
//				+ "(SELECT COUNT(*) FROM " + profileTableName +" tab) AS numRows,"
//				+ "MIN(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS minVal,"
//				+ "MAX(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS maxVal,"				
//				+ "AVG(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS avgVal,"				
//				+ "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS medianVal,"				
//				+ "STDDEV(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS stdDev,"				
//				+ "COUNT(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal)) AS numDistinct,"
//				+ "COUNT(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal))/COUNT(CAST(regexp_replace(" + attrName + ", '[^0-9]+', '0', 'g') as decimal))*100 AS perDistinct,"		
//				+ "COUNT(" + attrName + ") AS numNull,"				
//				+ "COUNT(" + attrName + ") / (CASE WHEN COUNT(" + attrName + ") IS NULL THEN 1 ELSE count(" + attrName + ") END)*100 AS perNull,"
//				+ "COUNT(" + attrName + ") / (CASE WHEN COUNT(" + attrName + ") IS NULL THEN 1 ELSE count(" + attrName + ") END) AS sixSigma," 
//				+ "CURRENT_DATE AS load_date, " 
//				+ "UNIX_TIMESTAMP() AS load_id, '" + profileExec.getVersion() + "' AS version"
//				+ " FROM " + profileTableName;*/
//    	logger.info("query is : " + sql);
//		return sql;
//	}
//}
