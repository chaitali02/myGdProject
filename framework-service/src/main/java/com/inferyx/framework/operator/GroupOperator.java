/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.operator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.MetadataUtil;

@Component
public class GroupOperator {
	
	@Autowired protected MetadataUtil daoRegister;

	/*public String generateSql(Map map,
			java.util.Map<String, MetaIdentifier> refKeyMap, 
			HashMap<String, String> otherParams) {
		StringBuilder builder = new StringBuilder();
		// Add groupBy
	//	Group group;
		Datapod datapod;
		if (map.getGroupBy() == null) {
			return "";
		}
		List<AttributeRefHolder> groupList = map.getGroupBy();	
		for(int i=0; i<groupList.size(); i++)
		{
			String groupUuid = groupList.get(i).getRef().getKey().getUUID();
			if (null == groupList.get(i).getRef().getKey().getVersion()) {
				datapod = daoRegister.getDatapodDao().findLatestByUuid(groupUuid, new Sort(Sort.Direction.DESC, "version"));
			} else {
				String version = groupList.get(i).getRef().getKey().getVersion();
				datapod = daoRegister.getDatapodDao().findOneByUuidAndVersion(groupUuid, version);
			}
					}
		builder.append(" GROUP BY ").append(groupSql(map, refKeyMap));

		return builder.toString();
	}
	
	public String groupSql(Map map, java.util.Map<String, MetaIdentifier> refKeyMap) {
		StringBuilder builder = new StringBuilder();
		String finalBuilder = null;
		for (AttributeRefHolder sourceAttr : map.getGroupBy()) {
			System.out.println("sourceAttr ::: " + sourceAttr);
			if (sourceAttr.getRef().getType() == MetaType.simple) {
				String prefix = "";
				builder.append(prefix);
				builder.append(sourceAttr.getValue());
				prefix = ",";
				builder.append(prefix);
			}
			if (sourceAttr.getRef().getType() == MetaType.datapod) {

				Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(sourceAttr.getRef(), refKeyMap));
				String prefix = "";
				builder.append(prefix);
				builder.append(datapod.sql(Integer.parseInt(sourceAttr.getAttrId())));
				prefix = ",";
				builder.append(prefix);
			}
			if (builder.charAt(builder.length() - 1) == ',') {
				finalBuilder = builder.substring(0, builder.length() - 1);
			}
		}

		System.out.println(String.format("Generalize groupBy %s", builder.toString()));
		return finalBuilder;
	}*/


}
