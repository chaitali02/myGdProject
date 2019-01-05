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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IReportDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.ReportView;
import com.inferyx.framework.register.GraphRegister;

/**
 * @author Ganesh
 *
 */
@Service
public class ReportViewServiceImpl {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	IReportDao iReportDao;
	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	
	static final Logger logger = Logger.getLogger(ReportViewServiceImpl.class);
	
	public ReportView findOneById(String id) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		ReportView reportView = new ReportView();
		Report report = (Report) commonServiceImpl.getOneById(id, MetaType.report.toString());
		Report resolvedReport = (Report) commonServiceImpl.resolveName(report, MetaType.report);
		reportView.setUuid(resolvedReport.getUuid());
		reportView.setVersion(resolvedReport.getVersion());
		reportView.setName(resolvedReport.getName());
		reportView.setDesc(resolvedReport.getDesc());
		reportView.setAppInfo(resolvedReport.getAppInfo());
		reportView.setCreatedBy(resolvedReport.getCreatedBy());
		reportView.setTags(resolvedReport.getTags());
		reportView.setActive(resolvedReport.getActive());
		reportView.setCreatedOn(resolvedReport.getCreatedOn());
		reportView.setPublished(resolvedReport.getPublished());
		MetaIdentifierHolder dependsOn = resolvedReport.getDependsOn();
		List<AttributeRefHolder> filterInfo = resolvedReport.getFilterInfo();
		Filter resolvedFilter = null;
		if(filterInfo != null)		{
			for (int i = 0; i < filterInfo.size(); i++) {
				Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(), report.getVersion(), MetaType.filter.toString());
				resolvedFilter = filterServiceImpl.resolveName(filter);
			}
		}
		reportView.setFilter(resolvedFilter);
		reportView.setDependsOn(dependsOn);
		if (resolvedReport.getAttributeInfo() != null) {

			List<AttributeSource> reportSourceAttribute = resolvedReport.getAttributeInfo();
			List<AttributeSource> attrRef = new ArrayList<AttributeSource>();

			for (int i = 0; i < reportSourceAttribute.size(); i++) {
				AttributeSource ref = new AttributeSource();
				MetaIdentifier SourceAttr = new MetaIdentifier();
				AttributeRefHolder sourceAttr = new AttributeRefHolder();
				SourceAttr = reportSourceAttribute.get(i).getSourceAttr().getRef();
				sourceAttr.setRef(SourceAttr);
				if (reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.datapod) || reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.dataset)) {
					int attrId = Integer.parseInt(reportSourceAttribute.get(i).getSourceAttr().getAttrId());
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
					sourceAttr.setAttrId(Integer.toString(attrId));
				} else if (reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.simple)) {
					sourceAttr.setValue(reportSourceAttribute.get(i).getSourceAttr().getValue());
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				} else {
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				}
				attrRef.add(ref);
			}
			reportView.setAttributeInfo(attrRef);
		}
		return reportView;
	}
	
	public ReportView findLatestByUuid(String uuid) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonProcessingException {
		ReportView reportView = new ReportView();
		Report report = (Report) commonServiceImpl.getLatestByUuid(uuid, MetaType.report.toString());
		Report resolvedReport = (Report) commonServiceImpl.resolveName(report, MetaType.report);
		reportView.setUuid(resolvedReport.getUuid());
		reportView.setVersion(resolvedReport.getVersion());
		reportView.setName(resolvedReport.getName());
		reportView.setDesc(resolvedReport.getDesc());
		reportView.setAppInfo(resolvedReport.getAppInfo());
		reportView.setCreatedBy(resolvedReport.getCreatedBy());
		reportView.setTags(resolvedReport.getTags());
		reportView.setActive(resolvedReport.getActive());
		reportView.setCreatedOn(resolvedReport.getCreatedOn());
		reportView.setPublished(resolvedReport.getPublished());
		MetaIdentifierHolder dependsOn = resolvedReport.getDependsOn();
		List<AttributeRefHolder> filterInfo = resolvedReport.getFilterInfo();
		Filter resolvedFilter = null;
		if(filterInfo != null) {
		for (int i = 0; i < filterInfo.size(); i++) {
			Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(), report.getVersion(), MetaType.filter.toString());
			resolvedFilter = filterServiceImpl.resolveName(filter);
		}
		}
		
		reportView.setFilter(resolvedFilter);
		reportView.setDependsOn(dependsOn);
		if (resolvedReport.getAttributeInfo() != null) {
			List<AttributeSource> reportSourceAttribute = resolvedReport.getAttributeInfo();
			List<AttributeSource> attrRef = new ArrayList<AttributeSource>();

			for (int i = 0; i < reportSourceAttribute.size(); i++) {
				AttributeSource ref = new AttributeSource();
				MetaIdentifier SourceAttr = new MetaIdentifier();
				AttributeRefHolder sourceAttr = new AttributeRefHolder();
				SourceAttr = reportSourceAttribute.get(i).getSourceAttr().getRef();
				sourceAttr.setRef(SourceAttr);
				if (reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.datapod) || reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.dataset)) {
					int attrId = Integer.parseInt(reportSourceAttribute.get(i).getSourceAttr().getAttrId());
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
					sourceAttr.setAttrId(Integer.toString(attrId));
				} else if (reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.simple)) {
					sourceAttr.setValue(reportSourceAttribute.get(i).getSourceAttr().getValue());
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				} else if (reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.formula))	{
					String formulaUuid = reportSourceAttribute.get(i).getSourceAttr().getRef().getUuid();
					Formula formula = (Formula) commonServiceImpl.getLatestByUuid(formulaUuid, MetaType.formula.toString());
					reportSourceAttribute.get(i).getSourceAttr().getRef().setName(formula.getName());
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				} 
				else if (reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.expression))	{
					String expressionUuid = reportSourceAttribute.get(i).getSourceAttr().getRef().getUuid();
					Expression expression = (Expression) commonServiceImpl.getLatestByUuid(expressionUuid, MetaType.expression.toString());
					reportSourceAttribute.get(i).getSourceAttr().getRef().setName(expression.getName());
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				} 
				else {
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				}
				attrRef.add(ref);
			}
			reportView.setAttributeInfo(attrRef);
		}
		return reportView;
	}

	public ReportView findOneByUuidAndVersion(String uuid, String version) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonProcessingException {
		ReportView reportView = new ReportView();	
		Report report = (Report) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.report.toString());
		Report resolvedReport = (Report) commonServiceImpl.resolveName(report, MetaType.report);
		reportView.setUuid(resolvedReport.getUuid());
		reportView.setVersion(resolvedReport.getVersion());
		reportView.setName(resolvedReport.getName());
		reportView.setDesc(resolvedReport.getDesc());
		reportView.setAppInfo(resolvedReport.getAppInfo());
		reportView.setCreatedBy(resolvedReport.getCreatedBy());
		reportView.setTags(resolvedReport.getTags());
		reportView.setActive(resolvedReport.getActive());
		reportView.setCreatedOn(resolvedReport.getCreatedOn());
		reportView.setPublished(resolvedReport.getPublished());
		reportView.setTitle(resolvedReport.getTitle());
		reportView.setHeader(resolvedReport.getHeader());
		reportView.setHeaderAlign(resolvedReport.getHeaderAlign());
		reportView.setFooter(resolvedReport.getFooter());
		reportView.setFooterAlign(resolvedReport.getFooterAlign());
		MetaIdentifierHolder dependsOn = resolvedReport.getDependsOn();
		List<AttributeRefHolder> filterInfo = resolvedReport.getFilterInfo();
		Filter resolvedFilter = null;
		if(filterInfo != null)		{
			for (int i = 0; i < filterInfo.size(); i++) {
				Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(), report.getVersion(), MetaType.filter.toString());
				resolvedFilter = filterServiceImpl.resolveName(filter);
			}
		}
		reportView.setFilter(resolvedFilter);
		reportView.setDependsOn(dependsOn);
		if (resolvedReport.getAttributeInfo() != null) {
			List<AttributeSource> reportSourceAttribute = resolvedReport.getAttributeInfo();
			List<AttributeSource> attrRef = new ArrayList<AttributeSource>();

			for (int i = 0; i < reportSourceAttribute.size(); i++) {
				AttributeSource ref = new AttributeSource();
				MetaIdentifier SourceAttr = new MetaIdentifier();
				AttributeRefHolder sourceAttr = new AttributeRefHolder();
				SourceAttr = reportSourceAttribute.get(i).getSourceAttr().getRef();
				sourceAttr.setRef(SourceAttr);
				if (reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.datapod) 
						|| reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.dataset)
						|| reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.paramlist)) {
					int attrId = Integer.parseInt(reportSourceAttribute.get(i).getSourceAttr().getAttrId());
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
					sourceAttr.setAttrId(Integer.toString(attrId));
					sourceAttr.setAttrType(reportSourceAttribute.get(i).getSourceAttr().getAttrType());
				} else if (reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.simple)) {
					sourceAttr.setValue(reportSourceAttribute.get(i).getSourceAttr().getValue());
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				}else if (reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.formula)){
					String formulaUuid = reportSourceAttribute.get(i).getSourceAttr().getRef().getUuid();
					Formula formula = (Formula) commonServiceImpl.getLatestByUuid(formulaUuid, MetaType.formula.toString());
					reportSourceAttribute.get(i).getSourceAttr().getRef().setName(formula.getName());
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				}  
				else if (reportSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.expression))	{
					String expressionUuid = reportSourceAttribute.get(i).getSourceAttr().getRef().getUuid();
					Expression expression = (Expression) commonServiceImpl.getLatestByUuid(expressionUuid, MetaType.expression.toString());
					reportSourceAttribute.get(i).getSourceAttr().getRef().setName(expression.getName());
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				} 
				else {
					ref.setAttrSourceId(reportSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(reportSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				}
				attrRef.add(ref);
			}
			reportView.setAttributeInfo(attrRef);
		}
		return reportView;
	}

	public Report save(ReportView reportView) throws Exception {
		if(reportView == null) 
			return null;
		
		Report report = new Report();
		
		if(!StringUtils.isBlank(reportView.getUuid())) 
			report.setUuid(reportView.getUuid());
		
		if (reportView.getTags() != null)
			report.setTags(reportView.getTags());
		
		if (!StringUtils.isBlank(reportView.getName()))
			report.setName(reportView.getName());
		
		if (!StringUtils.isBlank(reportView.getDesc()))
			report.setDesc(reportView.getDesc());
		
		if (reportView.getDependsOn() != null) {
			report.setDependsOn(reportView.getDependsOn());
		}
		
		if(reportView.getTitle() != null) {
			report.setTitle(reportView.getTitle());
		}
		
		if(reportView.getHeader() != null) {
			report.setHeader(reportView.getHeader());
		}
		
		if(reportView.getHeaderAlign() != null) {
			report.setHeaderAlign(reportView.getHeaderAlign());
		}
		
		if(reportView.getFooter() != null) {
			report.setFooter(reportView.getFooter());
		}
		
		if(reportView.getFooterAlign() != null) {
			report.setFooterAlign(reportView.getFooterAlign());
		}
		
		if(reportView.getAttributeInfo() != null) {
			report.setAttributeInfo(reportView.getAttributeInfo());
		}
		
		Filter filter = null;
		if (reportView.getFilter() != null) {
			filter = new Filter();
			filter.setDependsOn(reportView.getDependsOn());
			filter.setName(reportView.getName());
			filter.setDesc(reportView.getDesc());
			filter.setTags(reportView.getTags());
			filter.setFilterInfo(reportView.getFilter().getFilterInfo());
			if (reportView.getFilterChg().equalsIgnoreCase("Y") && filter != null) 
				commonServiceImpl.save(MetaType.filter.toString(), filter);			
		}
		
		if (filter != null) {
			List<AttributeRefHolder> filterList = new ArrayList<AttributeRefHolder>();
			AttributeRefHolder filterInfo = new AttributeRefHolder();
			MetaIdentifier filterMeta = new MetaIdentifier(MetaType.filter, filter.getUuid(), null);
			filterInfo.setRef(filterMeta);
			filterList.add(filterInfo);
			report.setFilterInfo(filterList);
		}
		report.setPublished(reportView.getPublished());
		return save(report);
	}
	
	public Report save(Report report) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> miHolderList = new ArrayList<MetaIdentifierHolder>();
		miHolderList.add(meta);
		report.setAppInfo(miHolderList);
		report.setBaseEntity();
		Report reportDet = iReportDao.save(report);
		registerGraph.updateGraph((Object) reportDet, MetaType.report);
		return reportDet;
	}
}
