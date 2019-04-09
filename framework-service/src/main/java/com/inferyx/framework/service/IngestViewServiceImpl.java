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

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IIngestDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.Ingest;
import com.inferyx.framework.domain.IngestView;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.register.GraphRegister;

/**
 * @author Ganesh
 *
 */
@Service
public class IngestViewServiceImpl {
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private SecurityServiceImpl securityServiceImpl;
    @Autowired
    private GraphRegister<?> registerGraph;
    @Autowired
    private IIngestDao iIngestDao;

	
	static final Logger logger = Logger.getLogger(IngestViewServiceImpl.class);
	
	public IngestView findOneByUuidAndVersion(String ingestUuid, String ingestVersion) throws JsonProcessingException {
		logger.info("Inside findOneByUuidAndVersion.");
		IngestView ingestView = new IngestView();
		Ingest ingest = (Ingest) commonServiceImpl.getOneByUuidAndVersion(ingestUuid, ingestVersion, MetaType.ingest.toString());
		//setting ingestView properties specific to baseEntity
		ingestView.setId(ingest.getId());
		ingestView.setUuid(ingest.getUuid());
		ingestView.setVersion(ingest.getVersion());
		ingestView.setName(ingest.getName());
		ingestView.setActive(ingest.getActive());
		ingestView.setAppInfo(ingest.getAppInfo());
		ingestView.setCreatedBy(ingest.getCreatedBy());
		ingestView.setCreatedOn(ingest.getCreatedOn());
		ingestView.setDesc(ingest.getDesc());
		ingestView.setPublished(ingest.getPublished());
		ingestView.setTags(ingest.getTags());
		
		//setting ingestView properties specific to ingest
		ingestView.setType(ingest.getType());
		ingestView.setSourceDatasource(ingest.getSourceDatasource());
		ingestView.setSourceDetail(ingest.getSourceDetail());
		ingestView.setSourceFormat(ingest.getSourceFormat());
		ingestView.setTargetDatasource(ingest.getTargetDatasource());
		ingestView.setTargetDetail(ingest.getTargetDetail());
		ingestView.setTargetFormat(ingest.getTargetFormat());
		ingestView.setRunParams(ingest.getRunParams());
		ingestView.setSourceHeader(ingest.getSourceHeader());
		ingestView.setIncrAttr(ingest.getIncrAttr());
		ingestView.setSplitBy(ingest.getSplitBy());
		ingestView.setSaveMode(ingest.getSaveMode());
		ingestView.setIgnoreCase(ingest.getIgnoreCase());
		ingestView.setSourceExtn(ingest.getSourceExtn());
		ingestView.setTargetExtn(ingest.getTargetExtn());
		ingestView.setTargetHeader(ingest.getTargetHeader());
//		List<AttributeRefHolder> filterInfo = ingest.getFilterInfo();
//		Filter resolvedFilter = null;
//		if(filterInfo != null)		{
//			for (int i = 0; i < filterInfo.size(); i++) {
//				Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(), ingest.getVersion(), MetaType.filter.toString());
//				resolvedFilter = filterServiceImpl.resolveName(filter);
//			}
//		}
//		ingestView.setFilter(resolvedFilter);		
		ingestView.setAttributeMap(ingest.getAttributeMap());
		return ingestView;
	}

	public BaseEntity save(IngestView ingestView) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		Ingest ingest = null;
		List<AttributeRefHolder> filterInfo = null;
		if(ingestView.getIngestChg().equalsIgnoreCase("Y") && ingestView.getUuid() == null) {
			ingest = new Ingest();
			//setting ingest baseEntity
			ingest.setName(ingestView.getName());
			ingest.setTags(ingestView.getTags());
			ingest.getFilterInfo();
			ingest.setBaseEntity();
			if(ingestView.getFilter() != null) {
				filterInfo = processFilter(ingestView, ingest.getUuid());
			}
		} else if(ingestView.getIngestChg().equalsIgnoreCase("Y") & ingestView.getFilterChg().equalsIgnoreCase("N") ) {
			//setting ingest baseEntity
			ingest = setIngestBaseEntity(ingestView);
			filterInfo = getFilterInfoByFilter(ingestView.getFilter());
		} else if(ingestView.getFilterChg().equalsIgnoreCase("Y")) {
			//setting ingest baseEntity
			ingest = setIngestBaseEntity(ingestView);
			if(ingestView.getFilter() != null) {
				filterInfo = processFilter(ingestView, ingest.getUuid());
			}
		}
		
		//setting ingest specific properties
		ingest.setType(ingestView.getType());
		ingest.setSourceDatasource(ingestView.getSourceDatasource());
		ingest.setSourceDetail(ingestView.getSourceDetail());
		ingest.setSourceFormat(ingestView.getSourceFormat());
		ingest.setTargetDatasource(ingestView.getTargetDatasource());
		ingest.setTargetDetail(ingestView.getTargetDetail());
		ingest.setTargetFormat(ingestView.getTargetFormat());
		ingest.setRunParams(ingestView.getRunParams());
		ingest.setSourceHeader(ingestView.getSourceHeader());
		ingest.setIncrAttr(ingestView.getIncrAttr());
		ingest.setSplitBy(ingestView.getSplitBy());
		ingest.setSaveMode(ingestView.getSaveMode());
		ingest.setIgnoreCase(ingestView.getIgnoreCase());
		ingest.setSourceExtn(ingestView.getSourceExtn());
		ingest.setTargetExtn(ingestView.getTargetExtn());
		ingest.setAttributeMap(ingestView.getAttributeMap());
		ingest.setTargetHeader(ingestView.getTargetHeader());
//		if(ingestView.getFilter() != null) {
//			ingest.setFilterInfo(filterInfo);
//		}
		return save(ingest);
	}
	
	public List<AttributeRefHolder> getFilterInfoByFilter(Filter filter) {
		List<AttributeRefHolder> filterList = null;
		if(filter != null) {
			filterList = new ArrayList<>();
			AttributeRefHolder filterHolder = new AttributeRefHolder();
			MetaIdentifier filterMeta = new MetaIdentifier(MetaType.filter, filter.getUuid(), null);
			filterHolder.setRef(filterMeta);
			filterList.add(filterHolder);
		}
		
		return filterList;
	}
	
	public List<AttributeRefHolder> processFilter(IngestView ingestView, String ingestUuid) throws JsonProcessingException, JSONException, ParseException {
		Filter filter = new Filter();
		filter.setName(ingestView.getName());
		filter.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.datapod, ingestView.getSourceDetail().getRef().getUuid(), null)));
		filter.setTags(ingestView.getTags());
		filter.setDesc(ingestView.getDesc());
		filter.setFilterInfo(ingestView.getFilter().getFilterInfo());
		commonServiceImpl.save(MetaType.filter.toString(), filter);
		List<AttributeRefHolder> filterList = new ArrayList<>();
		AttributeRefHolder filterHolder = new AttributeRefHolder();
		MetaIdentifier filterMeta = new MetaIdentifier(MetaType.filter, filter.getUuid(), null);
		filterHolder.setRef(filterMeta);
		filterList.add(filterHolder);
		return filterList;
	}
	
	public Ingest save(Ingest ingest) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException {
		List<MetaIdentifierHolder> appInfos = new ArrayList<>();
		appInfos.add(securityServiceImpl.getAppInfo());
		ingest.setAppInfo(appInfos);
//		ingest.setCreatedOn("");
		ingest.setCreatedBy(null);
		ingest.setBaseEntity();
		Ingest savedIngest = iIngestDao.save(ingest);
		registerGraph.updateGraph((Object) savedIngest, MetaType.ingest);
		return savedIngest;
	}
	
	private Ingest setIngestBaseEntity(IngestView ingestView) {
		Ingest ingest = new Ingest();
		//setting ingest baseEntitiy
		ingest.setActive(ingestView.getActive());
		ingest.setAppInfo(ingestView.getAppInfo());
		ingest.setCreatedBy(ingestView.getCreatedBy());
//		batch.setCreatedOn(ingestView.getCreatedOn());
		ingest.setDesc(ingestView.getDesc());
		ingest.setName(ingestView.getName());
		ingest.setPublished(ingestView.getPublished());
		ingest.setTags(ingestView.getTags());
		ingest.setUuid(ingestView.getUuid());
		ingest.setVersion(Helper.getVersion());
		return ingest;
	}
}
