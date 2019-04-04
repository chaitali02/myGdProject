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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IDatasetDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.DatasetOperator;
import com.inferyx.framework.register.GraphRegister;
import com.inferyx.framework.view.metadata.DatasetView;

@Service
public class DatasetServiceImpl {
	static final Logger logger = Logger.getLogger(DatasetServiceImpl.class);

	@Autowired
	private static DataSet dataset;	
	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	IDatasetDao iDatasetDao;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired
	DatasetOperator datasetOperator;
	@Autowired
	ExecutorFactory execFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DownloadServiceImpl downloadServiceImpl;
	
	public List<Map<String, Object>> getDatasetSample(String datasetUUID, String datasetVersion, int rows, ExecParams execParams, RunMode runMode) throws Exception {
		//Dataset dataset = iDatasetDao.findOneByUuidAndVersion(datasetUUID, datasetVersion);
		//logger.info(" Start datasetSample ");
		long startTime  = System.currentTimeMillis();
		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.sample.maxrows"));
		if(rows > maxRows) {
			logger.error("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dataset, datasetUUID, datasetVersion));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Number of rows "+rows+" exceeded. Max row allow "+maxRows, dependsOn);
			throw new RuntimeException("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
		}
		
		DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(datasetUUID, datasetVersion, MetaType.dataset.toString());
		List<Map<String, Object>> data = new ArrayList<>();
		String sql = datasetOperator.generateSql(dataset, null, null,new HashSet<>(), execParams, runMode);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		//ResultSetHolder rsHolder = null;
		Datasource dsDatasource = commonServiceImpl.getDatasourceByObject(dataset);
		try {
			data = exec.executeAndFetchByDatasource(sql, dsDatasource, commonServiceImpl.getApp().getUuid());		
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), message != null ? message : "No data found for dataset "+dataset.getName()+".", null);
			throw new RuntimeException(message != null ? message : "No data found for dataset "+dataset.getName()+".");
		}
		logger.info("Time elapsed in getDatasetSample : " + (System.currentTimeMillis() - startTime)/1000 + " s");
		return data;
	}

	
	public DataSet save(DataSet dataset) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		dataset.setAppInfo(metaIdentifierHolderList);
		dataset.setBaseEntity();
		DataSet datasetDet = iDatasetDao.save(dataset);
		registerGraph.updateGraph((Object) datasetDet, MetaType.dataset);
		return datasetDet;
	}	

	public DataSet save(DatasetView datasetView) throws Exception {
		DataSet dataset = new DataSet();
		if (datasetView.getUuid() != null) {
			dataset.setUuid(datasetView.getUuid());
		}
		//Relation relation = datasetView.getSource();		
		Filter filter = datasetView.getFilter();
		List<AttributeRefHolder> filterList = new ArrayList<AttributeRefHolder>();
		AttributeRefHolder filterHolder = new AttributeRefHolder();
		MetaIdentifierHolder relatiPAUSEer = datasetView.getDependsOn();

		/*if (datasetView.getSourceChg().equalsIgnoreCase("y")) {
			// create relation for dataset
			relation = new Relation();
			if(datasetView.getSource() != null)
			{
			relation.setDependsOn(datasetView.getSource().getDependsOn());
			relation.setRelationInfo(datasetView.getSource().getRelationInfo());
			relation.setName(datasetView.getName());
			relation.setTags(datasetView.getTags());
			relation.setDesc(datasetView.getDesc());
			relationServiceImpl.Save(relation);
			}
		}*/
		// create relation meta for dependsOn
		//MetaIdentifier relationMeta = new MetaIdentifier(MetaType.relation, relation.getUuid(), null);
		//relatiPAUSEer.setRef(relationMeta);		

		if ((datasetView.getFilterChg().equalsIgnoreCase("y") && datasetView.getFilter() != null) || (datasetView.getSourceChg().equalsIgnoreCase("y") && datasetView.getFilter() != null)) {
			// create filter for dataset
			filter = new Filter();
			filter.setName(datasetView.getName());
			filter.setDependsOn(relatiPAUSEer);
			filter.setTags(datasetView.getTags());
			filter.setDesc(datasetView.getDesc());
			filter.setFilterInfo(datasetView.getFilter().getFilterInfo());
			//filterServiceImpl.save(filter);
			commonServiceImpl.save(MetaType.filter.toString(), filter);
		}

		if ((datasetView.getSrcChg().equalsIgnoreCase("y") && datasetView.getFilter() != null) || (datasetView.getSourceChg().equalsIgnoreCase("y") && datasetView.getFilter() != null)) {
			// create filterInfo for dataset
			MetaIdentifier filterMeta = new MetaIdentifier(MetaType.filter, filter.getUuid(), null);
			filterHolder.setRef(filterMeta);
			filterList.add(filterHolder);
		}
		dataset.setTags(datasetView.getTags());
		dataset.setDesc(datasetView.getDesc());
	//	dataset.setFilterInfo(filterList);
		dataset.setName(datasetView.getName());
		dataset.setDependsOn(relatiPAUSEer);
		dataset.setAttributeInfo(datasetView.getAttributeInfo());
		dataset.setPublished(datasetView.getPublished());
		dataset.setLimit(datasetView.getLimit());
		DataSet datasetDet = save(dataset);
		return datasetDet;
	}
	
	public String getAttributeSql(DataSet dataset, String attributeId) {
		List<AttributeSource> sourceAttrs = dataset.getAttributeInfo();
		for (AttributeSource sourceAttr : sourceAttrs) {
			if (sourceAttr.getSourceAttr() != null 
					&& sourceAttr.getAttrSourceId() != null 
					&& sourceAttr.getAttrSourceId().equals(attributeId)) {
				/*Datapod datapod = (Datapod) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getSourceAttr().getRef(), null));*/
				return dataset.getName() + "." + sourceAttr.getAttrSourceName();//datapod.getAttribute(Integer.parseInt(sourceAttr.getSourceAttr().getAttributeId())).getName();
			}
		}
		return null;
	}

	public String getAttributeName(String uuid, int attrId) throws JsonProcessingException {
		String alias = null;
		dataset = (DataSet) commonServiceImpl.getLatestByUuid(uuid, MetaType.dataset.toString());
		alias = dataset.getAttribute(attrId).getAttrSourceName();
		return alias;
	}
	
	public String getAttributeName(DataSet dataset, String attributeId) {
		List<AttributeSource> sourceAttrs = dataset.getAttributeInfo();
		for (AttributeSource sourceAttr : sourceAttrs) {
			if (sourceAttr.getSourceAttr() != null 
					&& sourceAttr.getAttrSourceId() != null 
					&& sourceAttr.getAttrSourceId().equals(attributeId)) {
				/*Datapod datapod = (Datapod) daoRegister
						.getRefObject(TaskParser.populateRefVersion(sourceAttr.getSourceAttr().getRef(), null));*/
				//return datapod.getAttribute(Integer.parseInt(sourceAttr.getSourceAttr().getAttributeId())).getName();
				return sourceAttr.getAttrSourceName();
			}
		}
		return null;
	}

	
	public String generateSql (DataSet dataset, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode) throws Exception {
		long startTime = System.currentTimeMillis();
		String sql = datasetOperator.generateSql(dataset, refKeyMap, otherParams, usedRefKeySet, execParams, runMode);
		logger.info("Time elapsed in generateSql : " + (System.currentTimeMillis() - startTime)/1000 + " s");
		return sql;
	}

	public List<Map<String, Object>> getAttributeValues(String datasetUuid, int attributeID, RunMode runMode) throws Exception {
		DataSet dataSet = (DataSet) commonServiceImpl.getLatestByUuid(datasetUuid, MetaType.dataset.toString());
		List<AttributeSource> attributeSources = dataSet.getAttributeInfo();
		List<AttributeSource> tempAttributeSources = new ArrayList<>();
		for(AttributeSource attributeSource : attributeSources) {
			if(attributeSource.getAttrSourceId().equalsIgnoreCase(""+attributeID)) {
				tempAttributeSources.add(attributeSource);
				break;
			}
		}
		dataSet.setAttributeInfo(tempAttributeSources);
		dataSet.setFilterInfo(null);
		String selectQuery = datasetOperator.generateSelectDistinct(dataSet, null, null, null, runMode);
		if(selectQuery.contains(" AS "+tempAttributeSources.get(0))){
			selectQuery = selectQuery.replaceAll(" AS "+tempAttributeSources.get(0), "");
		}
		if(selectQuery.contains(" as "+tempAttributeSources.get(0).getAttrSourceName())){
			selectQuery = selectQuery.replaceAll(" as "+tempAttributeSources.get(0).getAttrSourceName(), "");
		}
		StringBuilder builder = new StringBuilder(selectQuery);
		builder.append(" AS value ");
		builder.append(" FROM ");
		builder.append(datasetOperator.generateFrom(dataSet, null, null, new HashSet<>(), runMode));
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		Datasource datapodDS = commonServiceImpl.getDatasourceByObject(dataSet);
		IExecutor exec = execFactory.getExecutor(datasource.getType());
//		return exec.executeAndFetch(builder.toString(), commonServiceImpl.getApp().getUuid());
		return exec.executeAndFetchByDatasource(builder.toString(), datapodDS, commonServiceImpl.getApp().getUuid());
	}
	
	
	public HttpServletResponse download(String datasetUuid, String datasetVersion, String format,int rows,RunMode runMode, HttpServletResponse response, Layout layout) throws Exception {
		int maxRows = Integer.parseInt(commonServiceImpl.getConfigValue("framework.download.maxrows"));
		if(rows > maxRows) {
			logger.error("Requested rows exceeded the limit of "+maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
		}
//		getDatasetSample(uuid, version, rows, null, runMode);
		List<Map<String, Object>> results = getDatasetSample(datasetUuid, datasetVersion, rows, null, runMode);
		response = downloadServiceImpl.download(format, response, runMode, results,
				new MetaIdentifierHolder(new MetaIdentifier(MetaType.dataset, datasetUuid, datasetVersion)), layout,
				null, false, "framework.file.download.path", null, new MetaIdentifierHolder(new MetaIdentifier(MetaType.dataset, datasetUuid, datasetVersion)), null);
		return response;

	}
}
