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
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Report;
import com.inferyx.framework.domain.ReportView;

/**
 * @author Ganesh
 *
 */
public class ReportViewServiceImpl {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
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
		
		return reportView;
	}

	public Report save(ReportView reportView) throws JSONException, ParseException, JsonProcessingException {
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
		
		if(reportView.getTitleAlign() != null) {
			report.setTitleAlign(reportView.getTitleAlign());
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
			filter = reportView.getFilter();
			filter.setDependsOn(reportView.getDependsOn());
			filter.setName(reportView.getName());
			filter.setDesc(reportView.getDesc());
			filter.setTags(reportView.getTags());
			if (reportView.getFilterChg().equalsIgnoreCase("y") && filter != null) 
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
		
		return report;
	}
}
