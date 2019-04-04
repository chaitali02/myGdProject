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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IDatasetDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.view.metadata.DatasetView;

@Service
public class DatasetViewServiceImpl {
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	IDatasetDao iDatasetDao;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	FormulaServiceImpl formulaServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(DatasetViewServiceImpl.class);
	
	public DatasetView findOneById(String id) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonProcessingException {
		DatasetView datasetView = new DatasetView();	
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		/*Dataset dataset;
		if (appUuid != null) {
			dataset = iDatasetDao.findOneById(appUuid, id);
		} else {
			dataset = iDatasetDao.findOne(id);
		}*/
		DataSet dataset = (DataSet) commonServiceImpl.getOneById(id, MetaType.dataset.toString());
		DataSet resolvedDataset = (DataSet) commonServiceImpl.resolveName(dataset, MetaType.dataset);
		datasetView.setUuid(resolvedDataset.getUuid());
		datasetView.setVersion(resolvedDataset.getVersion());
		datasetView.setName(resolvedDataset.getName());
		datasetView.setDesc(resolvedDataset.getDesc());
		datasetView.setAppInfo(resolvedDataset.getAppInfo());
		datasetView.setCreatedBy(resolvedDataset.getCreatedBy());
		datasetView.setTags(resolvedDataset.getTags());
		datasetView.setActive(resolvedDataset.getActive());
		datasetView.setCreatedOn(resolvedDataset.getCreatedOn());
		datasetView.setPublished(resolvedDataset.getPublished());
		MetaIdentifierHolder dependsOn = resolvedDataset.getDependsOn();
//		List<AttributeRefHolder> filterInfo = resolvedDataset.getFilterInfo();
//		Filter resolvedFilter = null;
//		if(filterInfo != null)		{
//			for (int i = 0; i < filterInfo.size(); i++) {
//				//Filter filter = filterServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(), dataset.getVersion());
//				Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(), dataset.getVersion(), MetaType.filter.toString());
//				resolvedFilter = filterServiceImpl.resolveName(filter);
//			}
//		}
//		datasetView.setFilter(resolvedFilter);
	//	Relation relation = relationServiceImpl.getAsOf(dependsOn.getRef().getUuid(), dataset.getVersion());
	//	Relation resolvedRelation = relationServiceImpl.resolveName(relation);
		datasetView.setDependsOn(dependsOn);
		if (resolvedDataset.getAttributeInfo() != null) {

			List<AttributeSource> datasetSourceAttribute = resolvedDataset.getAttributeInfo();
			List<AttributeSource> attrRef = new ArrayList<AttributeSource>();

			for (int i = 0; i < datasetSourceAttribute.size(); i++) {
				AttributeSource ref = new AttributeSource();
				MetaIdentifier SourceAttr = new MetaIdentifier();
				AttributeRefHolder sourceAttr = new AttributeRefHolder();
				SourceAttr = datasetSourceAttribute.get(i).getSourceAttr().getRef();
				sourceAttr.setRef(SourceAttr);
				if (datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.datapod) || datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.dataset)) {
					int attrId = Integer.parseInt(datasetSourceAttribute.get(i).getSourceAttr().getAttrId());
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
					sourceAttr.setAttrId(Integer.toString(attrId));
				} else if (datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.simple)) {
					sourceAttr.setValue(datasetSourceAttribute.get(i).getSourceAttr().getValue());
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				} else {
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				}
				attrRef.add(ref);
			}
			datasetView.setAttributeInfo(attrRef);
		}
		return datasetView;
	}

	public DatasetView findLatestByUuid(String uuid) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonProcessingException {
		DatasetView datasetView = new DatasetView();	
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		/*if (appUuid == null) {
			dataset = iDatasetDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		} else {
			dataset = iDatasetDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
		}*/
		DataSet dataset = (DataSet) commonServiceImpl.getLatestByUuid(uuid, MetaType.dataset.toString());
		DataSet resolvedDataset = (DataSet) commonServiceImpl.resolveName(dataset, MetaType.dataset);
		datasetView.setUuid(resolvedDataset.getUuid());
		datasetView.setVersion(resolvedDataset.getVersion());
		datasetView.setName(resolvedDataset.getName());
		datasetView.setDesc(resolvedDataset.getDesc());
		datasetView.setAppInfo(resolvedDataset.getAppInfo());
		datasetView.setCreatedBy(resolvedDataset.getCreatedBy());
		datasetView.setTags(resolvedDataset.getTags());
		datasetView.setActive(resolvedDataset.getActive());
		datasetView.setCreatedOn(resolvedDataset.getCreatedOn());
		datasetView.setPublished(resolvedDataset.getPublished());
		MetaIdentifierHolder dependsOn = resolvedDataset.getDependsOn();
//		List<AttributeRefHolder> filterInfo = resolvedDataset.getFilterInfo();
//		Filter resolvedFilter = null;
//		if(filterInfo != null)
//		{
//		for (int i = 0; i < filterInfo.size(); i++) {
//			//Filter filter = filterServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(), dataset.getVersion());
//			Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(), dataset.getVersion(), MetaType.filter.toString());
//			resolvedFilter = filterServiceImpl.resolveName(filter);
//		}
//		}
//		
//		datasetView.setFilter(resolvedFilter);
		//Relation relation = relationServiceImpl.getAsOf(dependsOn.getRef().getUuid(), dataset.getVersion());
		//Relation resolvedRelation = relationServiceImpl.resolveName(relation);
		datasetView.setDependsOn(dependsOn);
		if (resolvedDataset.getAttributeInfo() != null) {

			List<AttributeSource> datasetSourceAttribute = resolvedDataset.getAttributeInfo();
			List<AttributeSource> attrRef = new ArrayList<AttributeSource>();

			for (int i = 0; i < datasetSourceAttribute.size(); i++) {
				AttributeSource ref = new AttributeSource();
				MetaIdentifier SourceAttr = new MetaIdentifier();
				AttributeRefHolder sourceAttr = new AttributeRefHolder();
				SourceAttr = datasetSourceAttribute.get(i).getSourceAttr().getRef();
				sourceAttr.setRef(SourceAttr);
				if (datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.datapod) || datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.dataset)) {
					int attrId = Integer.parseInt(datasetSourceAttribute.get(i).getSourceAttr().getAttrId());
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
					sourceAttr.setAttrId(Integer.toString(attrId));
				} else if (datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.simple)) {
					sourceAttr.setValue(datasetSourceAttribute.get(i).getSourceAttr().getValue());
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				} else if (datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.formula))	{
					String formulaUuid = datasetSourceAttribute.get(i).getSourceAttr().getRef().getUuid();
					//Formula formula = formulaServiceImpl.findLatestByUuid(formulaUuid);
					Formula formula = (Formula) commonServiceImpl.getLatestByUuid(formulaUuid, MetaType.formula.toString());
					datasetSourceAttribute.get(i).getSourceAttr().getRef().setName(formula.getName());
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				} 
				else if (datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.expression))	{
					String expressionUuid = datasetSourceAttribute.get(i).getSourceAttr().getRef().getUuid();
					//Expression expression = expressionServiceImpl.findLatestByUuid(expressionUuid);
					Expression expression = (Expression) commonServiceImpl.getLatestByUuid(expressionUuid, MetaType.expression.toString());
					datasetSourceAttribute.get(i).getSourceAttr().getRef().setName(expression.getName());
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				} 
				else {
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				}
				attrRef.add(ref);
			}
			datasetView.setAttributeInfo(attrRef);
		}
		return datasetView;
	}

	public DatasetView findOneByUuidAndVersion(String uuid, String version) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JsonProcessingException {
		DatasetView datasetView = new DatasetView();	
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/		
		/*if (appUuid == null) {
			dataset = iDatasetDao.findOneByUuidAndVersion(uuid, version);
		} else {
			dataset = iDatasetDao.findOneByUuidAndVersion(appUuid, uuid, version);
		}*/
		DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.dataset.toString());
		DataSet resolvedDataset = (DataSet) commonServiceImpl.resolveName(dataset, MetaType.dataset);
		datasetView.setUuid(resolvedDataset.getUuid());
		datasetView.setVersion(resolvedDataset.getVersion());
		datasetView.setName(resolvedDataset.getName());
		datasetView.setDesc(resolvedDataset.getDesc());
		datasetView.setAppInfo(resolvedDataset.getAppInfo());
		datasetView.setCreatedBy(resolvedDataset.getCreatedBy());
		datasetView.setTags(resolvedDataset.getTags());
		datasetView.setActive(resolvedDataset.getActive());
		datasetView.setCreatedOn(resolvedDataset.getCreatedOn());
		datasetView.setPublished(resolvedDataset.getPublished());
		datasetView.setLimit(resolvedDataset.getLimit());
		MetaIdentifierHolder dependsOn = resolvedDataset.getDependsOn();
//		List<AttributeRefHolder> filterInfo = resolvedDataset.getFilterInfo();
//		Filter resolvedFilter = null;
//		if(filterInfo != null)		{
//			for (int i = 0; i < filterInfo.size(); i++) {
//				//Filter filter = filterServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(), dataset.getVersion());
//				Filter filter = (Filter) commonServiceImpl.getAsOf(filterInfo.get(i).getRef().getUuid(), dataset.getVersion(), MetaType.filter.toString());
//				resolvedFilter = filterServiceImpl.resolveName(filter);
//			}
//		}
//		datasetView.setFilter(resolvedFilter);
		//Relation relation = relationServiceImpl.getAsOf(dependsOn.getRef().getUuid(), dataset.getVersion());
		///Relation resolvedRelation = relationServiceImpl.resolveName(relation);
		datasetView.setDependsOn(dependsOn);
		if (resolvedDataset.getAttributeInfo() != null) {

			List<AttributeSource> datasetSourceAttribute = resolvedDataset.getAttributeInfo();
			List<AttributeSource> attrRef = new ArrayList<AttributeSource>();

			for (int i = 0; i < datasetSourceAttribute.size(); i++) {
				AttributeSource ref = new AttributeSource();
				MetaIdentifier SourceAttr = new MetaIdentifier();
				AttributeRefHolder sourceAttr = new AttributeRefHolder();
				SourceAttr = datasetSourceAttribute.get(i).getSourceAttr().getRef();
				sourceAttr.setRef(SourceAttr);
				if (datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.datapod) 
						|| datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.dataset)
						|| datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.paramlist)) {
					int attrId = Integer.parseInt(datasetSourceAttribute.get(i).getSourceAttr().getAttrId());
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
					sourceAttr.setAttrId(Integer.toString(attrId));
					sourceAttr.setAttrType(datasetSourceAttribute.get(i).getSourceAttr().getAttrType());
				} else if (datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.simple)) {
					sourceAttr.setValue(datasetSourceAttribute.get(i).getSourceAttr().getValue());
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				}else if (datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.formula)){
					String formulaUuid = datasetSourceAttribute.get(i).getSourceAttr().getRef().getUuid();
					//Formula formula = formulaServiceImpl.findLatestByUuid(formulaUuid);
					Formula formula = (Formula) commonServiceImpl.getLatestByUuid(formulaUuid, MetaType.formula.toString());
					datasetSourceAttribute.get(i).getSourceAttr().getRef().setName(formula.getName());
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				}  
				else if (datasetSourceAttribute.get(i).getSourceAttr().getRef().getType().equals(MetaType.expression))	{
					String expressionUuid = datasetSourceAttribute.get(i).getSourceAttr().getRef().getUuid();
					//Expression expression = expressionServiceImpl.findLatestByUuid(expressionUuid);
					Expression expression = (Expression) commonServiceImpl.getLatestByUuid(expressionUuid, MetaType.expression.toString());
					datasetSourceAttribute.get(i).getSourceAttr().getRef().setName(expression.getName());
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				} 
				else {
					ref.setAttrSourceId(datasetSourceAttribute.get(i).getAttrSourceId());
					ref.setAttrSourceName(datasetSourceAttribute.get(i).getAttrSourceName());
					ref.setSourceAttr(sourceAttr);
				}
				attrRef.add(ref);
			}
			datasetView.setAttributeInfo(attrRef);
		}
		return datasetView;
	}

}