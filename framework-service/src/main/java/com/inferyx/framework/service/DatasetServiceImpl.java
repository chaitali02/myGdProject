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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.dao.IDatasetDao;
import com.inferyx.framework.dao.IFilterDao;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Filter;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.AttributeMapOperator;
import com.inferyx.framework.operator.DatasetOperator;
import com.inferyx.framework.register.GraphRegister;
import com.inferyx.framework.view.metadata.DatasetView;

@Service
public class DatasetServiceImpl {
	static final Logger logger = Logger.getLogger(DatasetServiceImpl.class);

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	IDatasetDao iDatasetDao;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	FilterServiceImpl filterServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired 
	IFilterDao iFilterDao;
	@Autowired
	DatasetOperator datasetOperator;
	@Autowired
	ExecutorFactory execFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	AttributeMapOperator attributeMapOperator;

	/********************** UNUSED **********************/
	/*public Dataset findLatest() {
		return resolveName(iDatasetDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/
	
	public List<Map<String, Object>> getDatasetSample(String datasetUUID, String datasetVersion, int rows, ExecParams execParams, RunMode runMode) throws Exception {
		//Dataset dataset = iDatasetDao.findOneByUuidAndVersion(datasetUUID, datasetVersion);
		logger.info(" Start datasetSample ");
		long startTime  = System.currentTimeMillis();
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.sample.maxrows"));
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

			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), "No data found for dataset.", null);
			throw new RuntimeException( "No data found for dataset.");
		}
		/*DataFrame df = rsHolder.getDataFrame();		
			Row[] dfRows = df.limit(rows).collect();
			String[] columns = df.columns();
			for (Row row : dfRows) {
				Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
				for (String column : columns) {
					object.put(column, row.getAs(column));
				}
				data.add(object);
			}*/
		logger.info("Time elapsed in getDatasetSample : " + (System.currentTimeMillis() - startTime)/1000 + " s");
		return data;
	}

	/********************** UNUSED **********************/
	/*public Dataset findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDatasetDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iDatasetDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public List<Dataset> findOneForDelete(String id, String name, String type, String desc) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDatasetDao.findOneForDelete(appUuid, id, name, type, desc);

	}*/

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
		MetaIdentifierHolder relationHolder = datasetView.getDependsOn();

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
		//relationHolder.setRef(relationMeta);		

		if ((datasetView.getFilterChg().equalsIgnoreCase("y") && datasetView.getFilter() != null) || (datasetView.getSourceChg().equalsIgnoreCase("y") && datasetView.getFilter() != null)) {
			// create filter for dataset
			filter = new Filter();
			filter.setName(datasetView.getName());
			filter.setDependsOn(relationHolder);
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
		dataset.setDependsOn(relationHolder);
		dataset.setAttributeInfo(datasetView.getAttributeInfo());
		dataset.setPublished(datasetView.getPublished());
		dataset.setLimit(datasetView.getLimit());
		DataSet datasetDet = save(dataset);
		return datasetDet;
	}

	/********************** UNUSED **********************/
	/*public Dataset findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDatasetDao.findOneByUuidAndVersion(uuid, version);
		}
		return iDatasetDao.findOneByUuidAndVersion(appUuid, uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public List<Dataset> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iDatasetDao.findAll();
		}
		return iDatasetDao.findAll(appUuid);
	}*/	

	/*public Dataset update(Dataset dataset) throws IOException {
		dataset.exportBaseProperty();
		Dataset datasetDet = iDatasetDao.save(dataset);
		registerService.createGraph();
		return datasetDet;
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iDatasetDao.exists(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<Dataset> resolveName(List<Dataset> dataSet) {
		List<Dataset> datasetList = new ArrayList<Dataset>();
		for (Dataset dataset : dataSet)
			if (dataset.getCreatedBy() != null) {
				String createdByRefUuid = dataset.getCreatedBy().getRef().getUuid();
				User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
				dataset.getCreatedBy().getRef().setName(user.getName());
				datasetList.add(dataset);
			}
		return datasetList;

	}*/

	/********************** UNUSED **********************/
	/*public Dataset resolveName(Dataset dataSet) {
		if (dataSet.getCreatedBy() != null) {
			String createdByRefUuid = dataSet.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			dataSet.getCreatedBy().getRef().setName(user.getName());
		}
		if (dataSet.getAppInfo() != null) {
			for (int i = 0; i < dataSet.getAppInfo().size(); i++) {
				String appUuid = dataSet.getAppInfo().get(i).getRef().getUuid();
				Application application = applicationServiceImpl.findLatestByUuid(appUuid);
				String appName = application.getName();
				dataSet.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		return dataSet;
	}*/

	/********************** UNUSED **********************/
	/*public List<Dataset> findAllLatest(){
		// String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Aggregation datasetaggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<Dataset> datasetResults = mongoTemplate.aggregate(datasetaggr, "dataset", Dataset.class);
		List<Dataset> datasetList = datasetResults.getMappedResults();
		// Fetch the dataset details for each id
		List<Dataset> result = new ArrayList<Dataset>();
		for (Dataset s : datasetList) {
			Dataset datasetLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				// String appUuid =
				// securityServiceImpl.getAppInfo().getRef().getUuid();;
				datasetLatest = iDatasetDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			} else {
				datasetLatest = iDatasetDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
			}
			// logger.debug("datapodLatest is " + datapodLatest.getName());
			if (datasetLatest != null) {
				result.add(datasetLatest);
			}
		}
		logger.debug("End of findAllLatest()");

		return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<Dataset> findAllLatestActive() {
		Aggregation datasetAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Dataset> datasetResults = mongoTemplate.aggregate(datasetAggr, "dataset", Dataset.class);
		List<Dataset> datasetList = datasetResults.getMappedResults();

		// Fetch the dataset details for each id
		List<Dataset> result = new ArrayList<Dataset>();
		for (Dataset d : datasetList) {
			Dataset datasetLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				datasetLatest = iDatasetDao.findOneByUuidAndVersion(appUuid, d.getId(), d.getVersion());
			} else {
				datasetLatest = iDatasetDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
			}
			if (datasetLatest != null) {
				result.add(datasetLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Dataset dataset = iDatasetDao.findOneById(appUuid, Id);
		dataset.setActive("N");
		iDatasetDao.save(dataset);
//		String ID = dataset.getId();
//		iDatasetDao.delete(ID);
//		dataset.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public List<Dataset> test(String param1) {
		return iDatasetDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public Dataset findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iDatasetDao.findAllByUuid(appUuid, uuid);

	}*/

	/********************** UNUSED **********************/
	/*public Dataset findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		System.out.print(id);
		if (appUuid != null) {
			logger.info("get" + iDatasetDao.findOneById(appUuid, id));
			return iDatasetDao.findOneById(appUuid, id);
		} else
			return iDatasetDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<Dataset> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iDatasetDao.findAllVersion(appUuid, uuid);
		} else
			return iDatasetDao.findAllVersion(uuid);
	}*/

	/*
	 * public Dataset createDataset(DatasetView datasetHolder) { //create
	 * relation for map Relation relation = new Relation();
	 * relation.setRelationInfo(datasetHolder.getRelationInfo());
	 * relation.setDependsOn(datasetHolder.getDependsOn());
	 * relation.setName(datasetHolder.getRelationName());
	 * relation.setTags(datasetHolder.getRelationTags());
	 * relation.setDesc(datasetHolder.getRelationDesc());
	 * relation.setActive(datasetHolder.getRelationActive());
	 * relationServiceImpl.Save(relation);
	 * 
	 * //create relation meta for map MetaIdentifierHolder relationHolder = new
	 * MetaIdentifierHolder(); MetaIdentifier relationMeta = new
	 * MetaIdentifier(MetaType.relation,relation.getUuid(),relation.getVersion()
	 * ); relationHolder.setRef(relationMeta);
	 * 
	 * //create map for dataset Map map = new Map();
	 * map.setAttributeMap(datasetHolder.getAttributeList());
	 * map.setName(datasetHolder.getMapName()); map.setSource(relationHolder);
	 * map.setTarget(datasetHolder.getTargetDatapod());
	 * map.setTags(datasetHolder.getMapTags());
	 * map.setDesc(datasetHolder.getMapDesc());
	 * map.setActive(datasetHolder.getMapActive()); mapServiceImpl.Save(map);
	 * 
	 * //create mapInfo for dataset MetaIdentifierHolder mapHolder = new
	 * MetaIdentifierHolder(); MetaIdentifier mapMeta = new
	 * MetaIdentifier(MetaType.map,map.getUuid(),map.getVersion());
	 * mapHolder.setRef(mapMeta);
	 * 
	 * //create filter for dataset Filter filter = new Filter();
	 * filter.setDependsOn(relationHolder);
	 * filter.setDesc(datasetHolder.getFilterDesc());
	 * filter.setTags(datasetHolder.getFilterTags());
	 * filter.setActive(datasetHolder.getFilterActive());
	 * filter.setName(datasetHolder.getFilterName());
	 * filter.setFilterInfo(datasetHolder.getFilterInfo());
	 * filterServiceImpl.Save(filter);
	 * 
	 * //create filterInfo for dataset List<MetaIdentifierHolder> filterList =
	 * new ArrayList<MetaIdentifierHolder>(); MetaIdentifierHolder filterHolder
	 * = new MetaIdentifierHolder(); MetaIdentifier filterMeta = new
	 * MetaIdentifier(MetaType.filter,filter.getUuid(),filter.getVersion());
	 * filterHolder.setRef(filterMeta); filterList.add(filterHolder);
	 * 
	 * //create dataset Dataset dataset = new Dataset();
	 * dataset.setFilterInfo(filterList);
	 * dataset.setGroupBy(datasetHolder.getGroupBy());
	 * dataset.setDependsOn(mapHolder);
	 * dataset.setTags(datasetHolder.getDatasetTags());
	 * dataset.setDesc(datasetHolder.getDatasetDesc());
	 * dataset.setActive(datasetHolder.getDatasetActive()); Save(dataset);
	 * return dataset; }
	 */

	public String getAttributeSql(MetadataUtil daoRegister, DataSet dataset, String attributeId) {
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

	public String getAttributeName(MetadataUtil daoRegister, DataSet dataset, String attributeId) {
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

	/********************** UNUSED **********************/
	/*public Dataset getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iDatasetDao.findAsOf(appUuid, uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
		} else
			return iDatasetDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Dataset dataset) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Dataset datasetNew = new Dataset();
		datasetNew.setName(dataset.getName()+"_copy");
		datasetNew.setActive(dataset.getActive());		
		datasetNew.setDesc(dataset.getDesc());		
		datasetNew.setTags(dataset.getTags());
		datasetNew.setAttributeInfo(dataset.getAttributeInfo());
		datasetNew.setDependsOn(dataset.getDependsOn());
		datasetNew.setGroupBy(dataset.getGroupBy());
		datasetNew.setFilterInfo(dataset.getFilterInfo());
		save(datasetNew);
		ref.setType(MetaType.dataset);
		ref.setUuid(datasetNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> datasetList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity dataset : datasetList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = dataset.getId();
			String uuid = dataset.getUuid();
			String version = dataset.getVersion();
			String name = dataset.getName();
			String desc = dataset.getDesc();
			String published=dataset.getPublished();
			MetaIdentifierHolder createdBy = dataset.getCreatedBy();
			String createdOn = dataset.getCreatedOn();
			String[] tags = dataset.getTags();
			String active = dataset.getActive();
			List<MetaIdentifierHolder> appInfo = dataset.getAppInfo();
			baseEntity.setId(id);
			baseEntity.setUuid(uuid);
			baseEntity.setVersion(version);
			baseEntity.setName(name);
			baseEntity.setDesc(desc);
			baseEntity.setCreatedBy(createdBy);
			baseEntity.setCreatedOn(createdOn);
			baseEntity.setPublished(published);
			baseEntity.setTags(tags);
			baseEntity.setActive(active);
			baseEntity.setAppInfo(appInfo);
			baseEntityList.add(baseEntity);
		}
		return baseEntityList;
	}*/
	
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
}
