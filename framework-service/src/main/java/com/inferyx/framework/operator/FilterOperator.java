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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.FilterInfo;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
 
@Component
public class FilterOperator {
	
	@Autowired protected MetadataUtil daoRegister;
	@Autowired protected CommonServiceImpl<?> commonServiceImpl;
	@Autowired protected JoinKeyOperator joinKeyOperator;
	private final String COMMA = ", ";
	
	public String generateSql(List<AttributeRefHolder> filterIdentifierList
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams
			, Set<MetaIdentifier> usedRefKeySet
			, Boolean isAggrAllowed
			, Boolean isAggrReqd, RunMode runMode) throws Exception {
		return generateSql(filterIdentifierList, refKeyMap, otherParams, usedRefKeySet, null, isAggrAllowed, isAggrReqd, runMode);
	}
	
	public String generateSql(List<AttributeRefHolder> filterIdentifierList
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, HashMap<String, String> otherParams
			, Set<MetaIdentifier> usedRefKeySet
			, ExecParams execParams
			, Boolean isAggrAllowed
			, Boolean isAggrReqd, RunMode runMode) throws Exception {
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
				filter = (Filter) commonServiceImpl.getOneByUuidAndVersion(filterKey.getUUID(), filterKey.getVersion(), MetaType.filter.toString());
				MetaIdentifier filterRef = new MetaIdentifier(MetaType.filter, filter.getUuid(), filter.getVersion());
				usedRefKeySet.add(filterRef);
				String filterStr = joinKeyOperator.generateSql(filter.getFilterInfo(), filter.getDependsOn(), refKeyMap, otherParams, usedRefKeySet, execParams,isAggrAllowed, isAggrReqd, runMode);
				if (StringUtils.isBlank(filterStr)) {
					builder.append(ConstantsUtil.BLANK);
				} /*else if (isAggrReqd) {
					builder.append(" (").append(filterStr).append(")");
				}*/ else {
					builder.append(" AND (").append(filterStr).append(")");
				}
				break;
			case datapod:
				OrderKey datapodKey = filterIdentifier.getRef().getKey();
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodKey.getUUID(), datapodKey.getVersion(), MetaType.datapod.toString());
				builder.append(" AND (").append(generateDatapodFilterSql(datapod, filterIdentifier.getAttrId(), filterIdentifier.getValue())).append(")");
				MetaIdentifier datapodRef = new MetaIdentifier(MetaType.datapod, datapod.getUuid(), datapod.getVersion());
				usedRefKeySet.add(datapodRef);
				break;
			case dataset:
				MetaIdentifier ref = filterIdentifier.getRef();
				DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), MetaType.dataset.toString());
				builder.append(" AND (").append(generateDataSetFilterSql(dataSet, filterIdentifier.getAttrId(), filterIdentifier.getValue())).append(")");
				MetaIdentifier dataSetRef = new MetaIdentifier(MetaType.dataset, dataSet.getUuid(), dataSet.getVersion());
				usedRefKeySet.add(dataSetRef);
				break;
			default:
				builder.append("");
				break;
			}// End switch
		}
		return builder.toString();
	}
	
	public String generateSql(List<FilterInfo> filterInfo
			, java.util.Map<String, MetaIdentifier> refKeyMap
			, MetaIdentifierHolder filterSource
			, HashMap<String, String> otherParams
			, Set<MetaIdentifier> usedRefKeySet
			, ExecParams execParams
			, Boolean isAggrAllowed
			, Boolean isAggrReqd, RunMode runMode) throws Exception {
		StringBuilder builder = new StringBuilder();
		if (filterInfo == null || filterInfo.size() <= 0) {
			return "";
		}
		
		String filterStr = joinKeyOperator.generateSql(filterInfo, filterSource, refKeyMap, otherParams, usedRefKeySet, execParams,isAggrAllowed, isAggrReqd, runMode);
		if (StringUtils.isBlank(filterStr)) {
			builder.append(ConstantsUtil.BLANK);
		} else {
			builder.append(" AND (").append(filterStr).append(")");
		}		
		return builder.toString();
	}
	
	/**
	 * Generate select query with expression with filter
	 * @param filterIdentifierList
	 * @param usedRefKeySet 
	 * @return
	 * @throws Exception 
	 */
	public String generateSelectWithFilter(List<AttributeRefHolder> filterIdentifierList, Set<MetaIdentifier> usedRefKeySet,
											ExecParams execParams) throws Exception {
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
					com.inferyx.framework.domain.Filter filter = (Filter) commonServiceImpl.getOneByUuidAndVersion(filterKey.getUUID(), filterKey.getVersion(), MetaType.filter.toString());
					builder.append(" (").append(joinKeyOperator.generateSql(filter.getFilterInfo(),filter.getDependsOn(), null, null, usedRefKeySet, execParams, true, false, null)).append(")");
					builder.append(" as ").append(filter.getName()).append(COMMA);
					break;
				case datapod:
					OrderKey datapodKey = filterIdentifier.getRef().getKey();
					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodKey.getUUID(), datapodKey.getVersion(), MetaType.datapod.toString());
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
		if(value != null) {
			boolean isNumber = Helper.isNumber(value);			
			if(!isNumber) {
				if (value.contains(",")) {
					value = value.substring(0, value.length()-1);
					value = "'"+value+"'"+",";
					return String.format("%s IN (%s)", datapod.sql(Integer.parseInt(attributeId)), value);
				} else {				
					value = "'"+value+"'";
				}
			}
		}
		return String.format("%s = %s", datapod.sql(Integer.parseInt(attributeId)), value);
	}

	private String generateDataSetFilterSql(DataSet dataSet, String attributeId, String value) {
		if (!NumberUtils.isDigits(attributeId)) {
			return "";
		}
		
		String attrName = null;
		if(value != null) {
			for(AttributeSource attributeSource : dataSet.getAttributeInfo()) {
				if(attributeSource.getAttrSourceId().equalsIgnoreCase(attributeId)) {
					attrName = attributeSource.getAttrSourceName();
				}
			}
			boolean isNumber = Helper.isNumber(value);			
			if(!isNumber) {
				if (value.contains(",")) {
					value = value.substring(0, value.length()-1);
					value = "'"+value+"'"+",";
					return String.format("%s IN (%s)", dataSet.sql(attrName), value);
				} else {				
					value = "'"+value+"'";
				}
			}
		}
		return String.format("%s = %s", dataSet.sql(attrName), value);
	}
}
