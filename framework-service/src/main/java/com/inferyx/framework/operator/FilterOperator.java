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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
 
@Component
public class FilterOperator {
	
	@Autowired protected MetadataUtil daoRegister;
	@Autowired protected JoinKeyOperator joinKeyOperator;
	private final String COMMA = ", ";
	
	public String generateSql(List<AttributeRefHolder> filterIdentifierList
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams
			, Set<MetaIdentifier> usedRefKeySet) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return generateSql(filterIdentifierList, refKeyMap, otherParams, usedRefKeySet, null);
	}
	
	public String generateSql(List<AttributeRefHolder> filterIdentifierList
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams
			, Set<MetaIdentifier> usedRefKeySet
			, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		if (filterIdentifierList == null || filterIdentifierList.size() <= 0) {
			return "";
		}
		// Append Filter(s)
		for (int i = 0; i < filterIdentifierList.size(); i++) {
				AttributeRefHolder filterIdentifier = filterIdentifierList.get(i);
			// Determine type of ref. If type is filter then parse filter. If type is datapod then append a clause where column = value
			switch (filterIdentifier.getRef().getType()) {
			case filter : 
				OrderKey filterKey = filterIdentifier.getRef().getKey();
				Filter filter = null;
				if (null == filterKey.getVersion()) {
					filter = daoRegister.getFilterDao().findLatestByUuid(filterKey.getUUID(),
							new Sort(Sort.Direction.DESC, "version"));
				} else {
					filter = daoRegister.getFilterDao().findOneByUuidAndVersion(filterKey.getUUID(),
							filterKey.getVersion());
				}


					MetaIdentifier filterRef = new MetaIdentifier(MetaType.filter, filter.getUuid(), filter.getVersion());
					usedRefKeySet.add(filterRef);
					builder.append(" AND (").append(joinKeyOperator.generateSql(filter.getFilterInfo(), filter.getDependsOn(), refKeyMap, otherParams, usedRefKeySet, execParams)).append(")");
					break;
			case datapod:
				OrderKey datapodKey = filterIdentifier.getRef().getKey();
				Datapod datapod = null;
				if (null == datapodKey.getVersion()) {
					datapod = daoRegister.getDatapodDao().findLatestByUuid(datapodKey.getUUID(),
							new Sort(Sort.Direction.DESC, "version"));
				}else {
					datapod = daoRegister.getDatapodDao().findOneByUuidAndVersion(datapodKey.getUUID(),
							datapodKey.getVersion());
				}
				builder.append(" AND (").append(generateDatapodFilterSql(datapod, filterIdentifier.getAttrId(), filterIdentifier.getValue())).append(")");
				MetaIdentifier datapodRef = new MetaIdentifier(MetaType.datapod, datapod.getUuid(), datapod.getVersion());
				usedRefKeySet.add(datapodRef);
				break;
			default:
				builder.append("");
				break;
			}// End switch
		}
		return builder.toString();
	}
	
	/**
	 * Generate select query with expression with filter
	 * @param filterIdentifierList
	 * @param usedRefKeySet 
	 * @return
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws JsonProcessingException 
	 */
	public String generateSelectWithFilter(List<AttributeRefHolder> filterIdentifierList, Set<MetaIdentifier> usedRefKeySet,
											ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		StringBuilder builder = new StringBuilder();
		if (filterIdentifierList == null || filterIdentifierList.isEmpty()) {
			return "";
		}
		
		
		
		for (AttributeRefHolder filterIdentifier : filterIdentifierList) {
			
			switch (filterIdentifier.getRef().getType()) {
			case expression :
			break;
			case filter : 
				OrderKey filterKey = filterIdentifier.getRef().getKey();
				com.inferyx.framework.domain.Filter filter = null;
				if (null == filterKey.getVersion()) {
					filter = daoRegister.getFilterDao().findLatestByUuid(filterKey.getUUID(),
							new Sort(Sort.Direction.DESC, "version"));
				} else {
					filter = daoRegister.getFilterDao().findOneByUuidAndVersion(filterKey.getUUID(),
							filterKey.getVersion());
				}
					builder.append(" (").append(joinKeyOperator.generateSql(filter.getFilterInfo(),filter.getDependsOn(), null, null, usedRefKeySet, execParams)).append(")");
					builder.append(" as ").append(filter.getName()).append(COMMA);
					break;
			case datapod:
				OrderKey datapodKey = filterIdentifier.getRef().getKey();
				Datapod datapod = null;
				if (null == datapodKey.getVersion()) {
					datapod = daoRegister.getDatapodDao().findLatestByUuid(datapodKey.getUUID(),
							new Sort(Sort.Direction.DESC, "version"));
				}else {
					datapod = daoRegister.getDatapodDao().findOneByUuidAndVersion(datapodKey.getUUID(),
							datapodKey.getVersion());
				}
				builder.append(" (").append(generateDatapodFilterSql(datapod, filterIdentifier.getAttrId(), filterIdentifier.getValue())).append(")");
				builder.append(" as ").append(datapod.getName()).append("_filter").append(COMMA);
				break;
			default:
				builder.append("");
				break;
			}// End switch
		}// End for
		
		

		/*for (MetaIdentifierHolder filterIdentifier : filterIdentifierList) {
			if(filterIdentifier.getRef().getType().equals(MetaType.filter))
			{
				OrderKey filterKey = filterIdentifier.getRef().getKey();
				com.inferyx.framework.metadata.Filter filter = null;
				if (null == filterKey.getVersion()) {
					filter = daoRegister.getFilterDao().findLatestByUuid(filterKey.getUUID(),
							new Sort(Sort.Direction.DESC, "version"));
				} else {
					filter = daoRegister.getFilterDao().findOneByUuidAndVersion(filterKey.getUUID(),
							filterKey.getVersion());
				}
					builder.append(" (").append(jfor (MetaIdentifierHolder filterIdentifier : filterIdentifierList) {
			if(filterIdentifier.getRef().getType().equals(MetaType.filter))
			{
				OrderKey filterKey = filterIdentifier.getRef().getKey();
				com.inferyx.framework.metadata.Filter filter = null;
				if (null == filterKey.getVersion()) {
					filter = daoRegister.getFilterDao().findLatestByUuid(filterKey.getUUID(),
							new Sort(Sort.Direction.DESC, "version"));
				} else {
					filter = daoRegister.getFilterDao().findOneByUuidAndVersion(filterKey.getUUID(),
							filterKey.getVersion());
				}
					builder.append(" (").append(joinKeyOperator.generateSql(filter.getFilterInfo(), null, null)).append(")");
					builder.append(" as ").append(filter.getName());
					
			}
			else if(filterIdentifier.getRef().getType().equals(MetaType.datapod))
					{
				OrderKey datapodKey = filterIdentifier.getRef().getKey();
				Datapod datapod = null;
				if (null == datapodKey.getVersion()) {
					datapod = daoRegister.getDatapodDao().findLatestByUuid(datapodKey.getUUID(),
							new Sort(Sort.Direction.DESC, "version"));
				}else {
					datapod = daoRegister.getDatapodDao().findOneByUuidAndVersion(datapodKey.getUUID(),
							datapodKey.getVersion());
				}
				builder.append(" (").append(generateDatapodFilterSql(datapod, filterIdentifier.getAttributeId(), filterIdentifier.getValue())).append(")");
				builder.append(" as ").append(datapod.getName()).append("_filter").append(COMMA);
				
					}
			else
				builder.append("");
			
		}oinKeyOperator.generateSql(filter.getFilterInfo(), null, null)).append(")");
					builder.append(" as ").append(filter.getName());
					
			}
			else if(filterIdentifier.getRef().getType().equals(MetaType.datapod))
					{
				OrderKey datapodKey = filterIdentifier.getRef().getKey();
				Datapod datapod = null;
				if (null == datapodKey.getVersion()) {
					datapod = daoRegister.getDatapodDao().findLatestByUuid(datapodKey.getUUID(),
							new Sort(Sort.Direction.DESC, "version"));
				}else {
					datapod = daoRegister.getDatapodDao().findOneByUuidAndVersion(datapodKey.getUUID(),
							datapodKey.getVersion());
				}
				builder.append(" (").append(generateDatapodFilterSql(datapod, filterIdentifier.getAttributeId(), filterIdentifier.getValue())).append(")");
				builder.append(" as ").append(datapod.getName()).append("_filter").append(COMMA);
				
					}
			else
				builder.append("");
			
		}*/
		
		
		return builder.toString();
	}

	
	private String generateDatapodFilterSql(Datapod datapod, String attributeId, String value) {
		if (!NumberUtils.isDigits(attributeId)) {
			return "";
		}
		if (value != null && value.contains(",")) {
			return String.format("%s IN (%s)", datapod.sql(Integer.parseInt(attributeId)),value);
		}
		return String.format("%s = %s", datapod.sql(Integer.parseInt(attributeId)),value);
	}

}
