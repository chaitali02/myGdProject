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
package com.inferyx.framework.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IRuleDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.view.metadata.RuleView;

@Service
public class RuleViewServiceImpl {

	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	IRuleDao iRuleDao;
	@Autowired
	MapServiceImpl mapServiceImpl;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	RuleServiceImpl ruleserviceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(RuleViewServiceImpl.class);

	/********************** UNUSED **********************/
	/*public RuleView findOneById(String id) {
		RuleView ruleView = new RuleView();	
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Rule rule;
		if (appUuid != null) {
			rule = iRuleDao.findOneById(appUuid, id);
		} else {
			rule = iRuleDao.findOne(id);
		}
		
		return ruleView;
	}*/

	public RuleView findLatestByUuid(String uuid) throws JsonProcessingException {
		RuleView ruleView = new RuleView();		
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Rule rule;
		if (appUuid != null) {
			rule = iRuleDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
		} else {
			rule = iRuleDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}*/
		Rule rule = (Rule) commonServiceImpl.getLatestByUuid(uuid, MetaType.rule.toString());
		Rule resolvedRule = ruleserviceImpl.resolveName(rule);
		ruleView.setUuid(resolvedRule.getUuid());
		ruleView.setVersion(resolvedRule.getVersion());
		ruleView.setName(resolvedRule.getName());
		ruleView.setDesc(resolvedRule.getDesc());
		ruleView.setAppInfo(resolvedRule.getAppInfo());
		ruleView.setCreatedBy(resolvedRule.getCreatedBy());
		ruleView.setTags(resolvedRule.getTags());
		ruleView.setActive(resolvedRule.getActive());
		ruleView.setCreatedOn(resolvedRule.getCreatedOn());
		ruleView.setPublished(resolvedRule.getPublished());
		ruleView.setSource(rule.getSource());
		ruleView.setParamList(rule.getParamList());
		
		if(resolvedRule.getFilterInfo() != null) {
		List<AttributeRefHolder> filterInfo = resolvedRule.getFilterInfo();
		Filter resolvedFilter = null;
		for (int i = 0; i < filterInfo.size(); i++) {
			//Filter filter = filterServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(),rule.getVersion());
			Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(),rule.getVersion(), MetaType.filter.toString());
			resolvedFilter = filterServiceImpl.resolveName(filter);
			
		}
		ruleView.setFilter(resolvedFilter);
		}
		if(resolvedRule.getAttributeInfo()!=null)	{			
			List<AttributeSource> ruleSourceAttribute = resolvedRule.getAttributeInfo();
			List<AttributeSource> attrRef=new ArrayList<AttributeSource>();
			
			for(int i=0;i<ruleSourceAttribute.size();i++)	{
				AttributeSource ref=new AttributeSource();
				MetaIdentifier SourceAttr=new MetaIdentifier();
				AttributeRefHolder sourceAttr=new AttributeRefHolder();
				
				SourceAttr=ruleSourceAttribute.get(i).getSourceAttr().getRef();
				sourceAttr.setRef(SourceAttr);
				if(ruleSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.datapod) 
						|| ruleSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.dataset) 
						|| ruleSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.paramlist)
						|| ruleSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.rule))	{
				int attrId=Integer.parseInt(ruleSourceAttribute.get(i).getSourceAttr().getAttrId());
				
				ref.setAttrSourceId(ruleSourceAttribute.get(i).getAttrSourceId());
				ref.setAttrSourceName(ruleSourceAttribute.get(i).getAttrSourceName());
				ref.setSourceAttr(sourceAttr);
				
				sourceAttr.setAttrId(Integer.toString(attrId));
				}else if(ruleSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.simple))		{
				sourceAttr.setValue(ruleSourceAttribute.get(i).getSourceAttr().getValue());
				ref.setAttrSourceId(ruleSourceAttribute.get(i).getAttrSourceId());
				ref.setAttrSourceName(ruleSourceAttribute.get(i).getAttrSourceName());
				ref.setSourceAttr(sourceAttr);						
				}else{
					ref.setAttrSourceId(ruleSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(ruleSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);										
					}													
				attrRef.add(ref);
			}		
			ruleView.setAttributeInfo(attrRef);		
		}	    
		return ruleView;
	}

	public RuleView findOneByUuidAndVersion(String uuid, String version) throws JsonProcessingException {		
		RuleView ruleView = new RuleView();
		/*List<AttributeRefHolder> sourceAttributes = new ArrayList<AttributeRefHolder>();*/
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Rule rule;
		if (appUuid != null) {
			rule = iRuleDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else {
			rule = iRuleDao.findOneByUuidAndVersion(uuid, version);
		}*/
		Rule rule = (Rule) commonServiceImpl.getLatestByUuid(uuid, MetaType.rule.toString());
		Rule resolvedRule = ruleserviceImpl.resolveName(rule);
		ruleView.setUuid(resolvedRule.getUuid());
		ruleView.setVersion(resolvedRule.getVersion());
		ruleView.setName(resolvedRule.getName());
		ruleView.setDesc(resolvedRule.getDesc());
		ruleView.setAppInfo(resolvedRule.getAppInfo());
		ruleView.setCreatedBy(resolvedRule.getCreatedBy());
		ruleView.setTags(resolvedRule.getTags());
		ruleView.setActive(resolvedRule.getActive());
		ruleView.setCreatedOn(resolvedRule.getCreatedOn());
		ruleView.setPublished(resolvedRule.getPublished());
		ruleView.setSource(rule.getSource());
		ruleView.setParamList(rule.getParamList());
		if(resolvedRule.getFilterInfo() != null){
			List<AttributeRefHolder> filterInfo = resolvedRule.getFilterInfo();
			Filter resolvedFilter = null;
			for (int i = 0; i < filterInfo.size(); i++) {
				//Filter filter = filterServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(),rule.getVersion());
				Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(),rule.getVersion(), MetaType.filter.toString());
				resolvedFilter = filterServiceImpl.resolveName(filter);				
			}
			ruleView.setFilter(resolvedFilter);
		}
		if(resolvedRule.getAttributeInfo()!=null){			
			List<AttributeSource> ruleSourceAttribute = resolvedRule.getAttributeInfo();
			List<AttributeSource> attrRef=new ArrayList<AttributeSource>();			
			for(int i=0;i<ruleSourceAttribute.size();i++)	{
				AttributeSource ref=new AttributeSource();
				MetaIdentifier SourceAttr=new MetaIdentifier();
				AttributeRefHolder sourceAttr=new AttributeRefHolder();
				
					SourceAttr=ruleSourceAttribute.get(i).getSourceAttr().getRef();
					sourceAttr.setRef(SourceAttr);
					if(ruleSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.datapod) 
							|| ruleSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.dataset) 
							|| ruleSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.paramlist)
							|| ruleSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.rule)){
					int attrId=Integer.parseInt(ruleSourceAttribute.get(i).getSourceAttr().getAttrId());
					
					ref.setAttrSourceId(ruleSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(ruleSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
					
					sourceAttr.setAttrId(Integer.toString(attrId));
					sourceAttr.setAttrType(ruleSourceAttribute.get(i).getSourceAttr().getAttrType());
					}else if(ruleSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.simple))	{
					sourceAttr.setValue(ruleSourceAttribute.get(i).getSourceAttr().getValue());
					ref.setAttrSourceId(ruleSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(ruleSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);						
					}else{
						ref.setAttrSourceId(ruleSourceAttribute.get(i).getAttrSourceId());
						ref.setAttrSourceName(ruleSourceAttribute.get(i).getAttrSourceName());
						ref.setSourceAttr(sourceAttr);										
						}													
					attrRef.add(ref);
				}		
			ruleView.setAttributeInfo(attrRef);		
		}	    
		return ruleView;
	}	
}