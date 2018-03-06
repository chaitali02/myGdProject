package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeMap;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.parser.TaskParser;
import com.inferyx.framework.service.DataStoreServiceImpl;
@Component
public class RuleOperator implements Operator {
	
	@Autowired
	AttributeMapOperator attributeMapOperator;
	@Autowired
	RelationOperator relationOperator;
	@Autowired
	MetadataUtil daoRegister;
	@Autowired
	DatasetOperator datasetOperator;
	@Autowired
	FilterOperator filterOperator;
	@Autowired
	DataStoreServiceImpl datastoreServiceImpl;
	
	public String generateSql(Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap,HashMap<String, String> otherParams, 
								Set<MetaIdentifier> usedRefKeySet,	ExecParams execParams, Mode runMode) throws Exception {
		return generateSelect(rule, refKeyMap, otherParams, execParams, runMode)
				.concat(getFrom())
				.concat(generateFrom(rule, refKeyMap, otherParams, usedRefKeySet, execParams, runMode))
				.concat(generateWhere())
				.concat(generateFilter(rule, refKeyMap, otherParams, usedRefKeySet, execParams))
				.concat(selectGroupBy(rule, refKeyMap, otherParams, execParams));
	}
	
	private List<AttributeMap> createAttrMap (Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap) {
		// Create AttributeMap
		List<AttributeMap> attrMapList = new ArrayList<>();
		AttributeMap attrMap = null; 
		AttributeRefHolder sourceAttr = null;
		for (AttributeSource sourceAttribute : rule.getAttributeInfo()) {
			sourceAttr = new AttributeRefHolder();
			sourceAttr.setAttrId(sourceAttribute.getSourceAttr().getAttrId());
			sourceAttr.setValue(sourceAttribute.getSourceAttr().getValue());
			sourceAttr.setAttrName(sourceAttribute.getAttrSourceName());
			sourceAttr.setRef(sourceAttribute.getSourceAttr().getRef());
			attrMap = new AttributeMap();
			attrMap.setSourceAttr(sourceAttr);
			attrMap.setAttrMapId(sourceAttribute.getAttrSourceId());
			//attrMap.setDesc(sourceAttribute.getName());
			attrMapList.add(attrMap);
		}
		return attrMapList;
	}
	
	public String generateSelect(Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams, Mode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		// return ConstantsUtil.SELECT.concat("row_number() over (partition by 1) as rownum, ").concat(attributeMapOperator.generateSql(createAttrMap (rule, refKeyMap), rule.getSource(), refKeyMap, null));
		attributeMapOperator.setRunMode(runMode);
		return ConstantsUtil.SELECT.concat(attributeMapOperator.generateSql(createAttrMap (rule, refKeyMap), rule.getSource(), refKeyMap, otherParams, execParams));
	}
	
	public String getFrom() {
		return ConstantsUtil.FROM;
	}
 	
	public String generateFrom(Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
								Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, Mode runMode) throws Exception {
		StringBuilder builder = new StringBuilder();
		Relation relation = null;
		usedRefKeySet.add(rule.getSource().getRef());
		if (rule.getSource().getRef().getType() == MetaType.relation) {
			relation = (Relation) daoRegister.getRefObject(rule.getSource().getRef()); 
			builder.append(relationOperator.generateSql(relation, refKeyMap, otherParams, null, usedRefKeySet, runMode));
		} else if (rule.getSource().getRef().getType() == MetaType.datapod) {
			Datapod datapod = (Datapod) daoRegister.getRefObject(TaskParser.populateRefVersion(rule.getSource().getRef(), refKeyMap));
			String table = null;
			if (otherParams == null	|| otherParams.get("datapod_".concat(datapod.getUuid())) == null) {
				table = datastoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else {
				String tableKey = "datapod_".concat(datapod.getUuid());
				table = otherParams.get(tableKey);
			}
			builder.append(String.format(table, datapod.getName())).append("  ").append(datapod.getName()).append(" ");
		} else if (rule.getSource().getRef().getType() == MetaType.dataset) {
			DataSet dataset = (DataSet) daoRegister.getRefObject(rule.getSource().getRef());
			builder.append("(").append(datasetOperator.generateSql(dataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode)).append(")  ").append(dataset.getName());
		} else if (rule.getSource().getRef().getType() == MetaType.rule) {
			Rule innerRule = (Rule) daoRegister.getRefObject(rule.getSource().getRef());
			builder.append("(").append(generateSql(innerRule, refKeyMap, otherParams, usedRefKeySet, execParams, runMode)).append(")  ").append(innerRule.getName());
		}
		return builder.toString();
	}
	
	public String generateWhere () {
		return ConstantsUtil.WHERE_1_1;
	}
	
	public String selectGroupBy (Rule rule, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		return attributeMapOperator.selectGroupBy(attributeMapOperator.createAttrMap(rule.getAttributeInfo()), refKeyMap, otherParams, execParams);
	}
	
	public String generateFilter (Rule rule, 
									java.util.Map<String, MetaIdentifier> refKeyMap, 
									HashMap<String, String> otherParams, 
									Set<MetaIdentifier> usedRefKeySet, 
									ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if (rule.getFilterInfo() != null && !rule.getFilterInfo().isEmpty()) {
			return filterOperator.generateSql(rule.getFilterInfo(), refKeyMap, otherParams, usedRefKeySet, execParams);
		}
		return ConstantsUtil.BLANK;
	} 

}