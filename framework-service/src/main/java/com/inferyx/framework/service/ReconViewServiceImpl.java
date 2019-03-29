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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IReconDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.register.GraphRegister;
import com.inferyx.framework.view.metadata.ReconView;

@Service
public class ReconViewServiceImpl {

	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	IReconDao iReconDao;
	@Autowired
	MapServiceImpl mapServiceImpl;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	ReconServiceImpl reconServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;    
    @Autowired
	GraphRegister<?> registerGraph;


	static final Logger logger = Logger.getLogger(ReconViewServiceImpl.class);


	public ReconView findLatestByUuid(String uuid) throws JsonProcessingException {
		ReconView reconView = new ReconView();	
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Rule rule;
		if (appUuid != null) {
			rule = iRuleDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
		} else {
			rule = iRuleDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}*/
		Recon recon = (Recon) commonServiceImpl.getLatestByUuid(uuid, MetaType.recon.toString());
		Recon resolvedRule = reconServiceImpl.resolveName(recon);
		reconView.setUuid(resolvedRule.getUuid());
		reconView.setVersion(resolvedRule.getVersion());
		reconView.setName(resolvedRule.getName());
		reconView.setDesc(resolvedRule.getDesc());
		reconView.setAppInfo(resolvedRule.getAppInfo());
		reconView.setCreatedBy(resolvedRule.getCreatedBy());
		reconView.setTags(resolvedRule.getTags());
		reconView.setActive(resolvedRule.getActive());
		reconView.setCreatedOn(resolvedRule.getCreatedOn());
		reconView.setPublished(resolvedRule.getPublished());
		reconView.setSourceFunc(resolvedRule.getSourceFunc());
	    reconView.setTargetFunc(resolvedRule.getTargetFunc());
		
		/*if(resolvedRule.getSourceFilter() != null) {
		List<AttributeRefHolder> filterInfo = resolvedRule.getSourceFilter();
		Filter resolvedFilter = null;
		for (int i = 0; i < filterInfo.size(); i++) {
			Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(),recon.getVersion(), MetaType.filter.toString());
			resolvedFilter = filterServiceImpl.resolveName(filter);
			
		}
		reconView.setSourcefilter(resolvedFilter);
		}*/
		
		
		/*if(resolvedRule.getTargetFilter() != null) {
			List<AttributeRefHolder> filterInfo = resolvedRule.getTargetFilter();
			Filter resolvedFilter = null;
			for (int i = 0; i < filterInfo.size(); i++) {
				Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(),recon.getVersion(), MetaType.filter.toString());
				resolvedFilter = filterServiceImpl.resolveName(filter);
				
			}
			reconView.setTargetfilter(resolvedFilter);
			}*/
		    
		return reconView;
	}

	public ReconView findOneByUuidAndVersion(String uuid, String version) throws JsonProcessingException {		
		ReconView reconView = new ReconView();
		/*List<AttributeRefHolder> sourceAttributes = new ArrayList<AttributeRefHolder>();*/
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Rule rule;
		if (appUuid != null) {
			rule = iRuleDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else {
			rule = iRuleDao.findOneByUuidAndVersion(uuid, version);
		}*/
		Recon recon = (Recon) commonServiceImpl.getOneByUuidAndVersion(uuid,version,MetaType.recon.toString());
		Recon resolvedRule = reconServiceImpl.resolveName(recon);
		reconView.setUuid(resolvedRule.getUuid());
		reconView.setVersion(resolvedRule.getVersion());
		reconView.setName(resolvedRule.getName());
		reconView.setDesc(resolvedRule.getDesc());
		reconView.setAppInfo(resolvedRule.getAppInfo());
		reconView.setCreatedBy(resolvedRule.getCreatedBy());
		reconView.setTags(resolvedRule.getTags());
		reconView.setActive(resolvedRule.getActive());
		reconView.setCreatedOn(resolvedRule.getCreatedOn());
		reconView.setPublished(resolvedRule.getPublished());
		reconView.setSourceAttr(resolvedRule.getSourceAttr());
		reconView.setTargetAttr(resolvedRule.getTargetAttr());
		reconView.setSourceFunc(resolvedRule.getSourceFunc());
		reconView.setTargetFunc(resolvedRule.getTargetFunc());
		reconView.setSourceDistinct(resolvedRule.getSourceDistinct());	   
		reconView.setTargetDistinct(resolvedRule.getTargetDistinct());
		
		/*if(resolvedRule.getSourceFilter() != null) {
		List<AttributeRefHolder> sourceFilter = resolvedRule.getSourceFilter();
		Filter resolvedFilter = null;
		for (int i = 0; i < sourceFilter.size(); i++) {
			//Filter filter = filterServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(),rule.getVersion());
			Filter filter = (Filter) commonServiceImpl.getAsOf(sourceFilter.get(i).getRef().getUuid(),recon.getVersion(), MetaType.filter.toString());
			resolvedFilter = filterServiceImpl.resolveName(filter);
			
		}
		reconView.setSourcefilter(resolvedFilter);
		}*/
		
		
		/*if(resolvedRule.getTargetFilter() != null) {
			List<AttributeRefHolder> targetFilter = resolvedRule.getTargetFilter();
			Filter resolvedFilter = null;
			for (int i = 0; i < targetFilter.size(); i++) {
				//Filter filter = filterServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(),rule.getVersion());
				Filter filter = (Filter) commonServiceImpl.getAsOf(targetFilter.get(i).getRef().getUuid(),recon.getVersion(), MetaType.filter.toString());
				resolvedFilter = filterServiceImpl.resolveName(filter);
				
			}
			reconView.setTargetfilter(resolvedFilter);
			}*/
		    
		return reconView;
	}	
	
	
	
	
	public Recon save(ReconView reconView) throws Exception {
		List<AttributeRefHolder> sourcefilterList = new ArrayList<AttributeRefHolder>();
		List<AttributeRefHolder> targetfilterList = new ArrayList<AttributeRefHolder>();
		/* List<AttributeMap> attrMapList = new ArrayList<AttributeMap>(); */
		
		AttributeRefHolder sourceAttr = new AttributeRefHolder();
		AttributeRefHolder targetAttr = new AttributeRefHolder();

		MetaIdentifierHolder sourceFilterdepndsOn = new MetaIdentifierHolder();
		MetaIdentifierHolder targetFilterdepndsOn = new MetaIdentifierHolder();
		if (reconView == null)
			return null;
		Recon recon = new Recon();
		if (StringUtils.isNotBlank(reconView.getUuid()))
			recon.setUuid(reconView.getUuid());
		// save(rule);
		// rule.exportBaseProperty();
		if (reconView.getTags() != null)
			recon.setTags(reconView.getTags());
		if (StringUtils.isNotBlank(reconView.getName()))
			recon.setName(reconView.getName());
		if (StringUtils.isNotBlank(reconView.getDesc()))
			recon.setDesc(reconView.getDesc());
		recon.setSourceDistinct(reconView.getSourceDistinct());
		recon.setTargetDistinct(reconView.getTargetDistinct());
		Filter sourcefilter = null;
//		Filter sourcefilterdet = null;		
		Filter targetfilter = null;
//		Filter targetfilterdet = null;
		
		if(reconView.getSourceFunc()!=null) {
			MetaIdentifierHolder sourceFunc = reconView.getSourceFunc();
			sourceFunc.getRef().setVersion(null);
			recon.setSourceFunc(sourceFunc);
		}
		
		if(reconView.getTargetFunc()!=null) {
			MetaIdentifierHolder targetFunc = reconView.getTargetFunc();
			targetFunc.getRef().setVersion(null);
			recon.setTargetFunc(targetFunc);
		}		
		
		if(reconView.getSourceAttr()!=null) {		
			sourceFilterdepndsOn.setRef(reconView.getSourceAttr().getRef());
			sourceAttr.setAttrId(reconView.getSourceAttr().getAttrId());
			sourceAttr.setRef(reconView.getSourceAttr().getRef());
			recon.setSourceAttr(sourceAttr);
		}

		if(reconView.getTargetAttr()!=null) {
			targetFilterdepndsOn.setRef(reconView.getTargetAttr().getRef());
			targetAttr.setAttrId(reconView.getTargetAttr().getAttrId());
			targetAttr.setRef(reconView.getTargetAttr().getRef());
			recon.setTargetAttr(targetAttr);
		}
		
		if (reconView.getSourcefilter() != null) {
			sourcefilter = reconView.getSourcefilter();
			sourcefilter.setDependsOn(sourceFilterdepndsOn);
			sourcefilter.setName(reconView.getName());
			
			if (reconView.getSourcefilterChg().equalsIgnoreCase("y") && sourcefilter != null) {
				try {
					// filterdet = filterServiceImpl.save(filter);
					 commonServiceImpl.save(MetaType.filter.toString(), sourcefilter);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if (sourcefilter != null) {
			MetaIdentifier filterMeta = new MetaIdentifier(MetaType.filter, sourcefilter.getUuid(), null);
			AttributeRefHolder filterInfo = new AttributeRefHolder();
			filterInfo.setRef(filterMeta);
			sourcefilterList.add(filterInfo);
			//recon.setSourceFilter(sourcefilterList);	
		}
		
		if (reconView.getTargetfilter() != null) {
			targetfilter = reconView.getTargetfilter();
			targetfilter.setDependsOn(targetFilterdepndsOn);
			targetfilter.setName(reconView.getName());
			targetfilter.setDesc(reconView.getDesc());
			targetfilter.setTags(reconView.getTags());
			if (reconView.getTargetfilterChg().equalsIgnoreCase("y") && targetfilter != null) {
				try {
					// filterdet = filterServiceImpl.save(filter);
				  commonServiceImpl.save(MetaType.filter.toString(), targetfilter);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
		}
		if (targetfilter != null) {
			MetaIdentifier filterMeta = new MetaIdentifier(MetaType.filter, targetfilter.getUuid(), null);
			AttributeRefHolder filterInfo = new AttributeRefHolder();
			filterInfo.setRef(filterMeta);
			targetfilterList.add(filterInfo);
			//recon.setTargetFilter(targetfilterList);
		}
	
		recon.setPublished(reconView.getPublished());
		recon = save(recon);
		return recon;
	}

	public Recon save(Recon recon) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		recon.setAppInfo(metaIdentifierHolderList);
		recon.setBaseEntity();
		Recon reconDet = iReconDao.save(recon);
		registerGraph.updateGraph((Object) reconDet, MetaType.recon);
		return reconDet;
	}

}