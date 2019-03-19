/******************************** ***********************************************
 * Copyright (C) Inferyx Inc, 2018Custom log path  All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.controller.TrainResultViewServiceImpl;
import com.inferyx.framework.dao.IAlgorithmDao;
import com.inferyx.framework.dao.IModelDao;
import com.inferyx.framework.dao.IModelExecDao;
import com.inferyx.framework.datascience.MonteCarloSimulation;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.Corelation;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.DeployExec;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.DownloadExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.FeatureAttrMap;
import com.inferyx.framework.domain.FeatureRefHolder;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.domain.TrainExecView;
import com.inferyx.framework.domain.TrainInput;
import com.inferyx.framework.domain.TrainResult;
import com.inferyx.framework.domain.TrainResultView;
import com.inferyx.framework.domain.UploadExec;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.enums.EncodingType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.executor.DL4JExecutor;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.PythonExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.DatasetOperator;
import com.inferyx.framework.operator.GenerateDataOperator;
import com.inferyx.framework.operator.ImputeOperator;
import com.inferyx.framework.operator.PredictMLOperator;
import com.inferyx.framework.operator.RuleOperator;
import com.inferyx.framework.operator.SimulateMLOperator;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ModelServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	IModelDao iModelDao;
	@Autowired
	IAlgorithmDao iAlgorithmDao;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	IModelExecDao iModelExecDao;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	ModelExecServiceImpl modelExecServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	ParamListServiceImpl paramListServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	private AlgorithmServiceImpl algorithmServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private DataFrameService dataFrameService;
	@Autowired
	SessionHelper sessionHelper;
	@Autowired
	private ExecutorFactory execFactory;
	@Resource(name="taskThreadMap")
	protected ConcurrentHashMap<?, ?> taskThreadMap;
	@Autowired
	private SimulateMLOperator simulateMLOperator;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	private PredictMLOperator predictMLOperator;
	@Autowired
	private DatasetOperator datasetOperator;
	@Autowired
	private RuleOperator ruleOperator;
	@Autowired
	private MonteCarloSimulation monteCarloSimulation;
	@Autowired
	private GenerateDataOperator generateDataOperator;
	@Autowired
	Engine engine;
	@Autowired
	private MetadataServiceImpl metadataServiceImpl;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	@Autowired
	private PythonExecutor pythonExecutor;
	@Autowired
	private DL4JExecutor dl4jExecutor;
	@Autowired
	private TrainResultViewServiceImpl trainResultViewServiceImpl;
	@Resource(name="trainedModelMap")
	private ConcurrentHashMap<String, Object> trainedModelMap;
	@Autowired
	private ImputeOperator imputeOperator;
	
	static final Logger logger = Logger.getLogger(ModelServiceImpl.class);
	CustomLogger customLogger = new CustomLogger();
	
	/********************** UNUSED **********************/
	/*public Model findLatest() throws JsonProcessingException {
		return resolveName(iModelDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Model findAllByUuid(String uuid) {
		return iModelDao.findAllByUuid(uuid);	
	}*/

	/********************** UNUSED **********************/
	/*public Model findLatestByUuid(String uuid){
		return iModelDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public Model findOneByUuidAndVersion(String uuid,String version){
		return iModelDao.findOneByUuidAndVersion(uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public Model findOneById(String id){
		return iModelDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<Model> findAll(){
		return iModelDao.findAll();
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		Model model = iModelDao.findOne(Id);
		model.setActive("N");
		iModelDao.save(model);
//		String ID=application.getId();
//		iApplicationDao.delete(ID);
//		application.exportBaseProperty();
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public Model save(Model model) throws Exception{
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		model.setAppInfo(metaIdentifierHolderList);
		model.setBaseEntity();
		Model app=iModelDao.save(model);
		registerGraph.updateGraph((Object) app, MetaType.model);
		return app;
	}*/

	/********************** UNUSED **********************/
	/**
	 * @return the runMode
	 *//*
	public RunMode getRunMode() {
		return runMode;
	}
	*/

	/********************** UNUSED **********************/
	/**
	 * @param runMode the runMode to set
	 *//*
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}*/

	/********************** UNUSED **********************/
	/*public List<Model> resolveName(List<Model> models) throws JsonProcessingException {
		List<Model> modelList = new ArrayList<Model>(); 
		for(Model model : models)
		{
			String createdByRefUuid = model.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			model.getCreatedBy().getRef().setName(user.getName());
			modelList.add(model);
		}
		return modelList;
	}*/
	
	@SuppressWarnings("unused")
	public Model resolveName(Model model) throws JsonProcessingException {
		if (model.getCreatedBy() != null) {
			String createdByRefUuid = model.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			model.getCreatedBy().getRef().setName(user.getName());
		}
		if (model.getAppInfo() != null) {
			for (int i = 0; i < model.getAppInfo().size(); i++) {
				String appUuid = model.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				model.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		if(model.getFeatures().size() >0){
			for (int i = 0; i < model.getFeatures().size(); i++) {
				String attributeId = model.getFeatures().get(i).getFeatureId();
				//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(model.getFeatures().get(i).getRef().getUuid());
				if(model.getFeatures().get(i).getType().equals(MetaType.dataset.toString())) {
					DataSet datasetDO = (DataSet) commonServiceImpl.getLatestByUuid(model.getFeatures().get(i).getFeatureId(), MetaType.dataset.toString());
					String datapodName = datasetDO.getName();
					model.getFeatures().get(i).setName(datapodName);
					List<AttributeSource> attributeSourceList = datasetDO.getAttributeInfo();
					model.getFeatures().get(i).setName(attributeSourceList.get(Integer.parseInt(attributeId)).getAttrSourceName());	
				}else if(model.getFeatures().get(i).getType().equals(MetaType.datapod.toString())) {					
					Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(model.getFeatures().get(i).getFeatureId(), MetaType.datapod.toString());
					String datapodName = datapodDO.getName();
					model.getFeatures().get(i).setName(datapodName);
					List<Attribute> attributeList = datapodDO.getAttributes();
					model.getFeatures().get(i).setName(attributeList.get(Integer.parseInt(attributeId)).getName());					
				}
			}
		}
		Algorithm algo= null;
		if (model.getDependsOn().getRef().getVersion() != null)
			algo = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(), MetaType.algorithm.toString());
		else 
			algo = (Algorithm) commonServiceImpl.getLatestByUuid(model.getDependsOn().getRef().getUuid(), MetaType.algorithm.toString());
				
		/*if(model.getLabel() != null && algo.getLabelRequired().equalsIgnoreCase("Y")){
			List<Attribute> attributeList = null;
			if(model.getSource().getRef().getType().toString().equals(MetaType.datapod.toString())){
				//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(model.getLabel().getRef().getUuid());
				Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(model.getLabel().getRef().getUuid(), MetaType.datapod.toString());
				model.getLabel().getRef().setName(datapodDO.getName());
				attributeList = datapodDO.getAttributes();
				model.getLabel().setAttrName(attributeList.get(Integer.parseInt(attributeId)).getName());
			} else if(model.getSource().getRef().getType().toString().equals(MetaType.dataset.toString())){
				//Dataset dataset = datasetServiceImpl.findLatestByUuid(model.getLabel().getRef().getUuid());
				DataSet dataset = (DataSet) commonServiceImpl.getLatestByUuid(model.getLabel().getRef().getUuid(), MetaType.dataset.toString());
				model.getLabel().getRef().setName(dataset.getName());
				List<AttributeSource> attributeSourceList = dataset.getAttributeInfo();
				model.getLabel().setAttrName(attributeSourceList.get(Integer.parseInt(attributeId)).getAttrSourceName());
			}
		}*/
		return model;
	}	
	
	public Predict resolveName(Predict predict) throws JsonProcessingException {
		if (predict.getCreatedBy() != null) {
			String createdByRefUuid = predict.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			predict.getCreatedBy().getRef().setName(user.getName());
		}
		if (predict.getAppInfo() != null) {
			for (int i = 0; i < predict.getAppInfo().size(); i++) {
				String appUuid = predict.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				predict.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		if(predict.getFeatureAttrMap().size() > 0){
			for (int i = 0; i < predict.getFeatureAttrMap().size(); i++) {
//				String attributeId = predict.getFeatureAttrMap().get(i).getFeatureMapId();
				//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(model.getFeatures().get(i).getRef().getUuid());
				if(predict.getFeatureAttrMap().get(i).getAttribute().getRef().getType().equals(MetaType.dataset)) {
					DataSet datasetDO = (DataSet) commonServiceImpl.getLatestByUuid(predict.getFeatureAttrMap().get(i).getAttribute().getRef().getUuid(), MetaType.dataset.toString());
					String datapodName = datasetDO.getName();
					predict.getFeatureAttrMap().get(i).getAttribute().getRef().setName(datapodName);
					List<AttributeSource> attributeSourceList = datasetDO.getAttributeInfo();
					AttributeRefHolder attributeRefHolder =new AttributeRefHolder();
					attributeRefHolder.setAttrId(attributeSourceList.get(i).getAttrSourceId());
					attributeRefHolder.setAttrName(attributeSourceList.get(i).getAttrSourceName());
					predict.getFeatureAttrMap().get(i).setAttribute(attributeRefHolder);	
				}else if(predict.getFeatureAttrMap().get(i).getAttribute().getRef().getType().equals(MetaType.datapod)) {					
					Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(predict.getFeatureAttrMap().get(i).getAttribute().getRef().getUuid(), MetaType.datapod.toString());
					String datapodName = datapodDO.getName();
					String attrId=predict.getFeatureAttrMap().get(i).getAttribute().getAttrId();
					predict.getFeatureAttrMap().get(i).getAttribute().getRef().setName(datapodName);
					List<Attribute> attributeList = datapodDO.getAttributes();
					AttributeRefHolder attributeRefHolder =new AttributeRefHolder();
					attributeRefHolder.setAttrId(attributeList.get(Integer.parseInt(attrId)).getAttributeId().toString());
					attributeRefHolder.setAttrName(attributeList.get(Integer.parseInt(attrId)).getName());
					attributeRefHolder.setRef(predict.getFeatureAttrMap().get(i).getAttribute().getRef());
					predict.getFeatureAttrMap().get(i).setAttribute(attributeRefHolder);					
				}
			}
		}
		/*Algorithm algo = null;
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(predict.getDependsOn().getRef().getUuid(), predict.getDependsOn().getRef().getVersion(), MetaType.model.toString());
		if (model.getAlgorithm().getRef().getVersion() != null)
			algo = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(model.getAlgorithm().getRef().getUuid(), model.getAlgorithm().getRef().getVersion(), MetaType.algorithm.toString());
		else 
			algo = (Algorithm) commonServiceImpl.getLatestByUuid(model.getAlgorithm().getRef().getUuid(), MetaType.algorithm.toString());
				
		if(model.getLabel().getRef() != null && algo.getLabelRequired().equalsIgnoreCase("Y")){
			String attributeId = model.getLabel().getAttrId();
			List<Attribute> attributeList = null;
			if(model.getSource().getRef().getType().toString().equals(MetaType.datapod.toString())){
				//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(model.getLabel().getRef().getUuid());
				Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(model.getLabel().getRef().getUuid(), MetaType.datapod.toString());
				model.getLabel().getRef().setName(datapodDO.getName());
				attributeList = datapodDO.getAttributes();
				model.getLabel().setAttrName(attributeList.get(Integer.parseInt(attributeId)).getName());
			} else if(model.getSource().getRef().getType().toString().equals(MetaType.dataset.toString())){
				//Dataset dataset = datasetServiceImpl.findLatestByUuid(model.getLabel().getRef().getUuid());
				DataSet dataset = (DataSet) commonServiceImpl.getLatestByUuid(model.getLabel().getRef().getUuid(), MetaType.dataset.toString());
				model.getLabel().getRef().setName(dataset.getName());
				List<AttributeSource> attributeSourceList = dataset.getAttributeInfo();
				model.getLabel().setAttrName(attributeSourceList.get(Integer.parseInt(attributeId)).getAttrSourceName());
			}
		}*/
		return predict;
	}	
	
	
	
	
	public Simulate resolveName(Simulate simulate) throws JsonProcessingException {
		if (simulate.getCreatedBy() != null) {
			String createdByRefUuid = simulate.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			simulate.getCreatedBy().getRef().setName(user.getName());
		}
		if (simulate.getAppInfo() != null) {
			for (int i = 0; i < simulate.getAppInfo().size(); i++) {
				String appUuid = simulate.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				simulate.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		if(simulate.getFeatureInfo().size() >0){
			for (int i = 0; i < simulate.getFeatureInfo().size(); i++) {
				String attributeId = simulate.getFeatureInfo().get(i).getFeatureId();
				//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(model.getFeatures().get(i).getRef().getUuid());
					if(simulate.getFeatureInfo().get(i).getRef().getType().equals(MetaType.dataset)) {
					DataSet datasetDO = (DataSet) commonServiceImpl.getLatestByUuid(simulate.getFeatureInfo().get(i).getRef().getUuid(), MetaType.dataset.toString());
					String datapodName = datasetDO.getName();
					simulate.getFeatureInfo().get(i).getRef().setName(datapodName);
					List<AttributeSource> attributeSourceList = datasetDO.getAttributeInfo();
					simulate.getFeatureInfo().get(i).setFeatureName(attributeSourceList.get(i).getAttrSourceName());	
				}else if(simulate.getFeatureInfo().get(i).getRef().getType().equals(MetaType.datapod)) {					
					Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(simulate.getFeatureInfo().get(i).getRef().getUuid(), MetaType.datapod.toString());
					String datapodName = datapodDO.getName();
					simulate.getFeatureInfo().get(i).getRef().setName(datapodName);
					List<Attribute> attributeList = datapodDO.getAttributes();
					FeatureRefHolder featureRefHolder = new FeatureRefHolder();
					featureRefHolder.setFeatureName(attributeList.get(Integer.parseInt(attributeId)).getName());
					simulate.getFeatureInfo().get(i).setFeatureName(featureRefHolder.getFeatureName());					
				}
			}
		}
		
		return simulate;
	}	

	public Train resolveName(Train train) throws JsonProcessingException {
		if (train.getCreatedBy() != null) {
			String createdByRefUuid = train.getCreatedBy().getRef().getUuid();
			//User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			User user = (User) commonServiceImpl.getLatestByUuid(createdByRefUuid, MetaType.user.toString());
			train.getCreatedBy().getRef().setName(user.getName());
		}
		if (train.getAppInfo() != null) {
			for (int i = 0; i < train.getAppInfo().size(); i++) {
				String appUuid = train.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				train.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		if(train.getFeatureAttrMap().size() >0){
			for (int i = 0; i < train.getFeatureAttrMap().size(); i++) {
				String attributeId = train.getFeatureAttrMap().get(i).getFeatureMapId();
				//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(model.getFeatures().get(i).getRef().getUuid());
				if(train.getFeatureAttrMap().get(i).getFeature().getRef().getType().equals(MetaType.dataset)) {
					DataSet datasetDO = (DataSet) commonServiceImpl.getLatestByUuid(train.getFeatureAttrMap().get(i).getFeatureMapId(), MetaType.dataset.toString());
					String datapodName = datasetDO.getName();
					FeatureRefHolder featureRefHolder = new FeatureRefHolder();
					featureRefHolder.setFeatureName(datapodName);
					train.getFeatureAttrMap().get(i).setFeature(featureRefHolder);
					List<AttributeSource> attributeSourceList = datasetDO.getAttributeInfo();
					featureRefHolder.setFeatureName(attributeSourceList.get(Integer.parseInt(attributeId)).getAttrSourceName());
					train.getFeatureAttrMap().get(i).setFeature(featureRefHolder);	
				}else if(train.getFeatureAttrMap().get(i).getFeature().getRef().getType().equals(MetaType.datapod)) {					
					Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(train.getFeatureAttrMap().get(i).getFeatureMapId(), MetaType.datapod.toString());
					String datapodName = datapodDO.getName();
					FeatureRefHolder featureRefHolder = new FeatureRefHolder();
					featureRefHolder.setFeatureName(datapodName);
					train.getFeatureAttrMap().get(i).setFeature(featureRefHolder);
					List<Attribute> attributeList = datapodDO.getAttributes();
					featureRefHolder.setFeatureName(attributeList.get(Integer.parseInt(attributeId)).getName());
					train.getFeatureAttrMap().get(i).setFeature(featureRefHolder);				
				}
			}
		}
		/*Algorithm algo= null;
		if (train.getAlgorithm().getRef().getVersion() != null)
			algo = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(train.getAlgorithm().getRef().getUuid(), train.getAlgorithm().getRef().getVersion(), MetaType.algorithm.toString());
		else 
			algo = (Algorithm) commonServiceImpl.getLatestByUuid(train.getAlgorithm().getRef().getUuid(), MetaType.algorithm.toString());
				*/
		/*if(model.getLabel() != null && algo.getLabelRequired().equalsIgnoreCase("Y")){
			List<Attribute> attributeList = null;
			if(model.getSource().getRef().getType().toString().equals(MetaType.datapod.toString())){
				//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(model.getLabel().getRef().getUuid());
				Datapod datapodDO = (Datapod) commonServiceImpl.getLatestByUuid(model.getLabel().getRef().getUuid(), MetaType.datapod.toString());
				model.getLabel().getRef().setName(datapodDO.getName());
				attributeList = datapodDO.getAttributes();
				model.getLabel().setAttrName(attributeList.get(Integer.parseInt(attributeId)).getName());
			} else if(model.getSource().getRef().getType().toString().equals(MetaType.dataset.toString())){
				//Dataset dataset = datasetServiceImpl.findLatestByUuid(model.getLabel().getRef().getUuid());
				DataSet dataset = (DataSet) commonServiceImpl.getLatestByUuid(model.getLabel().getRef().getUuid(), MetaType.dataset.toString());
				model.getLabel().getRef().setName(dataset.getName());
				List<AttributeSource> attributeSourceList = dataset.getAttributeInfo();
				model.getLabel().setAttrName(attributeSourceList.get(Integer.parseInt(attributeId)).getAttrSourceName());
			}
		}*/
		return train;
	}

	/********************** UNUSED **********************/
	/*public List<Model> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iModelDao.findAllVersion(appUuid, uuid);
		} else
			return iModelDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public List<Model> findAllLatest() {	
			   Aggregation ModelAggr = newAggregation(group("uuid").max("version").as("version"));
			   AggregationResults<Model> ModelResults = mongoTemplate.aggregate(ModelAggr, "model", Model.class);	   
			   List<Model> ModelList = ModelResults.getMappedResults();

			   // Fetch the relation details for each id
			   List<Model> result=new  ArrayList<Model>();
			   for(Model a :ModelList)
			   {   
				   Model ModelLatest = iModelDao.findOneByUuidAndVersion(a.getId(),a.getVersion());
				   result.add(ModelLatest);
			   }
			   return result;			
	}*/

	/********************** UNUSED **********************/
	/*public List<Model> findAllLatestActive() 	
	{ 
		Aggregation appAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Model> appResults = mongoTemplate.aggregate(appAggr, "model", Model.class);	   
	   List<Model> appList = appResults.getMappedResults();

	   // Fetch the application details for each id
	   List<Model> result=new  ArrayList<Model>();
	   for(Model a : appList)
	   {   		     
		   Model appLatest = iModelDao.findOneByUuidAndVersion(a.getId(), a.getVersion());  		   
		   result.add(appLatest);
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Model model) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Model appNew = new Model();
		appNew.setName(model.getName()+"_copy");
		appNew.setActive(model.getActive());		
		appNew.setDesc(model.getDesc());		
		appNew.setTags(model.getTags());	
		save(appNew);
		ref.setType(MetaType.model);
		ref.setUuid(appNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/
	
	/********************** UNUSED **********************/	
	/*public List<BaseEntity> findList(List<? extends BaseEntity> modelList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity model : modelList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = model.getId();
			String uuid = model.getUuid();
			String version = model.getVersion();
			String name = model.getName();
			String desc = model.getDesc();
			String published=model.getPublished();
			MetaIdentifierHolder createdBy = model.getCreatedBy();
			String createdOn = model.getCreatedOn();
			String[] tags = model.getTags();
			String active = model.getActive();
			List<MetaIdentifierHolder> appInfo = model.getAppInfo();
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

	/********************** UNUSED **********************/
	/*public ModelExec create( String modelUUID, String modelVersion, ExecParams execParams, ParamMap paramMap, ModelExec modelExec) throws Exception {
		MetaIdentifierHolder modelRef = new MetaIdentifierHolder();
		//Model model = null;

		//model = iModelDao.findOneByUuidAndVersion(modelUUID, modelVersion);
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUUID, modelVersion, MetaType.model.toString());
		
		String logPath = null;
		if (modelExec == null) {
			modelExec = new ModelExec();
			modelRef.setRef(new MetaIdentifier(MetaType.model, modelUUID, modelVersion));
			modelExec.setDependsOn(modelRef);
			modelExec.setBaseEntity();
			
			
			 * 
			 * log file_name formation : modelexecuuid + modelexecversion + modelversion
			 * 
			 
			 
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				logPath = Helper.getPropertyValue("framework.model.log.path") + "/" + modelExec.getUuid() + "_" + modelExec.getVersion() + "_"+ model.getVersion()+".log";
			}
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(),
						"Created raw model exec, uuid: " + modelExec.getUuid(), 
						logPath,
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
		}
		modelExec.setExecParams(execParams);
		try {
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = modelExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			modelExec.setName(model.getName());
			modelExec.setAppInfo(model.getAppInfo());	
			//iModelExecDao.save(modelExec);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(), 
						"Saving raw modelExec, uuid: " + modelExec.getUuid(),
						logPath, 
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
			commonServiceImpl.save(MetaType.modelExec.toString(), modelExec);
			
			if (Helper.getLatestStatus(statusList) != null 
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.RUNNING, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.COMPLETED, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.PAUSE, new Date())))) {
				logger.info(" This process is RUNNING or has been COMPLETED previously or is On Hold. Hence it cannot be rerun. ");
				if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					customLogger.writeLog(this.getClass(),
							"This process is RUNNING or has been COMPLETED previously or is On Hold. Hence it cannot be rerun.", 
							logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				}
				return modelExec;
			}
			
			//modelExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet));
			modelExec.setRefKeyList(new ArrayList<>(usedRefKeySet));

			modelExec = (ModelExec) commonServiceImpl.setMetaStatus(modelExec, MetaType.modelExec, Status.Stage.PENDING);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(),
						modelExec.getStatusList().size()>0 ? "Latest status: "+modelExec.getStatusList().get(modelExec.getStatusList().size()-1).getStage() : "Status list is empty", 
						logPath,
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
		} catch (Exception e) {
			logger.error(e);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeErrorLog(this.getClass(), StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), 
						logPath,
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			
			}
			
			modelExec = (ModelExec) commonServiceImpl.setMetaStatus(modelExec, MetaType.modelExec, Status.Stage.FAILED);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(), modelExec.getStatusList().size()>0 ? "Model creation FAILED, status: "+modelExec.getStatusList().get(modelExec.getStatusList().size()-1).getStage() : "Status list is empty", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
			throw new Exception("Model creation FAILED");
		}		
		return modelExec;
	}*/

	/********************** UNUSED **********************/	
	/*public ModelExec train(String modelUUID, String modelVersion, ModelExec modelExec, ExecParams execParams, ParamMap paramMap) throws FileNotFoundException, IOException{
		RunModelServiceImpl runModelServiceImpl = new RunModelServiceImpl();
		List<Status> statusList = modelExec.getStatusList();
		if (statusList == null) {
			statusList = new ArrayList<>();
			modelExec.setStatusList(statusList);
		}
		
		//Model model = iModelDao.findOneByUuidAndVersion(modelUUID, modelVersion);
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUUID, modelVersion, MetaType.model.toString());
		if(!model.getType().equalsIgnoreCase(ExecContext.R.toString()) && !model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
			runModelServiceImpl.setAlgorithmUUID(model.getAlgorithm().getRef().getUuid());
			runModelServiceImpl.setAlgorithmVersion(model.getAlgorithm().getRef().getVersion());
		}
		runModelServiceImpl.setAlgorithmServiceImpl(algorithmServiceImpl);
		runModelServiceImpl.setDataStoreServiceImpl(dataStoreServiceImpl);
		runModelServiceImpl.setSparkContext(sparkContext);
		runModelServiceImpl.setModelExec(modelExec);
		runModelServiceImpl.setModel(model);
		runModelServiceImpl.setModelExecServiceImpl(modelExecServiceImpl);
		runModelServiceImpl.setHdfsInfo(hdfsInfo);
		runModelServiceImpl.setExecParams(execParams);
		runModelServiceImpl.setParamSetServiceImpl(paramSetServiceImpl);
		runModelServiceImpl.setParamListServiceImpl(paramListServiceImpl);
		runModelServiceImpl.setParamMap(paramMap);
		runModelServiceImpl.setCommonServiceImpl(commonServiceImpl);
		runModelServiceImpl.setRunMode(runMode);
		runModelServiceImpl.setDataFrameService(dataFrameService);
		runModelServiceImpl.setSessionContext(sessionHelper.getSessionContext());
		runModelServiceImpl.setCustomLogger(new CustomLogger());
		runModelServiceImpl.setModelType(model.getType());
		runModelServiceImpl.setModelServiceImpl(this);
		if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) 
			runModelServiceImpl.setLogPath(Helper.getPropertyValue("framework.model.log.path") + "/" + modelExec.getUuid() + "_" + modelExec.getVersion() + "_"+ model.getVersion()+".log");
		runModelServiceImpl.setExecFactory(execFactory);
		runModelServiceImpl.setSecurityServiceImpl(securityServiceImpl);
		runModelServiceImpl.start();
		return modelExec;
	}*/

	public String uploadScript(MultipartFile customScriptFile, Model model, String modelId, String modelUuid, String modelVersion, String scriptName) {
		//scriptName = customScriptFile.getOriginalFilename();
		//String uploadPath = null;
		String scriptPath = null;
		try {
			//uploadPath = Helper.getProperty("framework.r.location")+"/";
			
			//model.setName(scriptName.substring(0, scriptName.lastIndexOf(".")-1));
						
			scriptPath = Helper.getPropertyValue("framework.model.script.path")+"/" + scriptName;
			//model.setScriptName(scriptLocation);
			File scriptFile = new File(scriptPath);
			customScriptFile.transferTo(scriptFile);
			
			commonServiceImpl.save(MetaType.model.toString(), model);
		}catch (IllegalArgumentException
				| SecurityException 
				| NullPointerException
				| IllegalStateException 
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scriptPath;
	}
	
	public String upload(MultipartFile file, String extension, String fileType, String fileName, String metaType) throws FileNotFoundException, IOException, JSONException, ParseException {
		String uploadFileName = file.getOriginalFilename();
		FileType type = Helper.getFileType(fileType);
		String fileLocation = null;
		String directoryLocation = Helper.getFileDirectoryByFileType(type);
		String metaUuid = null;
		String metaVersion = null;
		if(fileName == null) {
			fileName = Helper.getFileCustomNameByFileType(type, extension);
			String splits[] = fileName.split("_");
			metaUuid = splits[0];
			metaVersion = splits[1].substring(0, splits[1].lastIndexOf("."));
		} 
		
		fileLocation = directoryLocation+"/" + fileName;
		
		File scriptFile = new File(fileLocation);
		file.transferTo(scriptFile);
		if(metaType==null)
		{
			metaType="model";
		}
		UploadExec uploadExec=new UploadExec();
		uploadExec.setFileName(uploadFileName);
		uploadExec.setBaseEntity();
		uploadExec.setLocation(fileLocation);
		uploadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(Helper.getMetaType(metaType), metaUuid, metaVersion)));
		commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
		return fileName;
	}

	public List<String> executeScript(String type, String scriptName, String execUuid, String execVersion, List<String> arguments) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		Train train = (Train) commonServiceImpl.getDomainFromDomainExec(MetaType.trainExec.toString(), execUuid, execVersion);
//		String logPath = Helper.getPropertyValue("framework.model.log.path") + "/" + execUuid + "_" + execVersion + "_"+ train.getVersion()+".log";
		String scriptPath = Helper.getPropertyValue("framework.algo.script.path")+"/"+scriptName;
//		
		logger.info("Script path to run : " + scriptPath);
		
//		IExecutor exec = execFactory.getExecutor(type);
//		if(exec instanceof PythonExecutor) {
//			PythonExecutor pythonExecutor = (PythonExecutor) exec;
//			pythonExecutor.setLogPath(logPath);
//		} else if(exec instanceof RExecutor) {
//			RExecutor rExecutor = (RExecutor) exec;
//			rExecutor.setLogPath(logPath);
//		}		
//		return exec.executeScript(scriptPath, commonServiceImpl.getApp().getUuid());
		try {
			return pythonExecutor.executTFScript(scriptPath, commonServiceImpl.getApp().getUuid(), arguments);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<String> readLog(String filePath, String type, String trainExecUuid, String trainExecVersion) throws IOException {
		if(filePath == null) {
			TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUuid, trainExecVersion, type);
			MetaIdentifierHolder dependsOn = trainExec.getDependsOn();
			filePath = Helper.getPropertyValue("framework.model.train.path") + "/" + trainExec.getUuid() + "_" + trainExec.getVersion() + "_" + dependsOn.getRef().getVersion() + ".log";
		}
		FileInputStream fstream = new FileInputStream(filePath);
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(fstream));
		String strLine = "";
		StringBuilder log = new StringBuilder();
		List<String> logList = new ArrayList<>();
	   
		while ((strLine = buffReader.readLine()) != null)   {
			log.append(strLine).append("\n");
			//logList.add(strLine);
		}
		logList.add(log.toString());
		buffReader.close();
		fstream.close();
		return logList;
	}
	
	public String readLog2(String filePath, String type, String trainExecUuid, String trainExecVersion) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(filePath == null) {
			TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUuid, trainExecVersion, type, "N");
			MetaIdentifierHolder execDependsOn = trainExec.getDependsOn();
			Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(execDependsOn.getRef().getUuid(),
					execDependsOn.getRef().getVersion(), MetaType.train.toString(), "N");
			MetaIdentifierHolder trainDependsOn = train.getDependsOn();
			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(trainDependsOn.getRef().getUuid(), trainDependsOn.getRef().getVersion(), MetaType.model.toString(),"N");
			if(model.getType().equalsIgnoreCase(ExecContext.spark.toString())) {
				filePath = Helper.getPropertyValue("framework.model.train.path") + "/" + train.getUuid() + "/" + train.getVersion() + "/" + trainExec.getVersion()  + "/" + train.getUuid().replaceAll("-", "_") + "_" + train.getVersion() + "_" + trainExec.getVersion() + ".result";
			} else {
				filePath = Helper.getPropertyValue("framework.model.train.path") + "/" + train.getUuid() + "/" + train.getVersion() + "/" + trainExec.getVersion()  + "/" + "model.result";
			}
		}
		//FileInputStream fstream = new FileInputStream("/user/hive/warehouse/framework/model/train/3dfc4042_00db_48f5_a075_572c5aead3ca/1530799218/1531305060/3dfc4042_00db_48f5_a075_572c5aead3ca_1530799218_1531305060.result");
		FileInputStream fstream = new FileInputStream(filePath);
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(fstream));
		String strLine = "";
		String result = "";
	   
		while ((strLine = buffReader.readLine()) != null)   {
			result = result.concat(strLine).concat("\n");
		}
		buffReader.close();
		return result;
	}
	
	public HttpServletResponse download(String execUuid, String execVersion, HttpServletResponse response, RunMode runMode) throws Exception {
		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(execUuid, execVersion,
				MetaType.trainExec.toString());
		DataStore datastore = dataStoreServiceImpl.getDatastore(trainExec.getResult().getRef().getUuid(),
				trainExec.getResult().getRef().getVersion());

		String location = datastore.getLocation();
		location = location.replaceAll("/model", "");
		String title = "";
		if(location.contains(hdfsInfo.getHdfsURL()) && location.contains("/bestModel/stages"))
			location = StringUtils.substringBetween(location, hdfsInfo.getHdfsURL(), "/bestModel");
		else if(location.contains(hdfsInfo.getHdfsURL()) && location.contains("/stages"))
			location = StringUtils.substringBetween(location, hdfsInfo.getHdfsURL(), "/stages");
		
		if(location.contains(Helper.getPropertyValue("framework.model.train.path")) && location.contains("/bestModel/stages"))
			location = StringUtils.substringBetween(location, Helper.getPropertyValue("framework.model.train.path"), "/bestModel/stages");
		else if(location.contains(Helper.getPropertyValue("framework.model.train.path")) && location.contains("/stages"))
			location = StringUtils.substringBetween(location, Helper.getPropertyValue("framework.model.train.path"), "/stages");
		else if(location.contains(Helper.getPropertyValue("framework.model.train.path")))
			location = location.replaceAll(Helper.getPropertyValue("framework.model.train.path"), "");
		
		if(location.startsWith("/") && location.endsWith("/"))
			title = location.substring(1, location.length()-1);
		else if(location.startsWith("/"))
			title = location.substring(1);
		else if(location.endsWith("/"))
			title = location.replaceAll("/", "");
		
		String temp = title.replaceAll("_", "-");
	    String fileName = temp.replaceAll("/", "_");
		try {
			response.setContentType("text/plain");
			response.setHeader("Content-disposition", "attachment");

			response.setHeader("filename",""+fileName + ".pmml");
			
			File file = new File(Helper.getPropertyValue("framework.model.train.path") +"/"+location + "/" + fileName + ".pmml");

			if (file.exists()) {
				DownloadExec downloadExec = new DownloadExec();

				downloadExec.setBaseEntity();
				downloadExec.setLocation("/" + location + "/" + fileName + ".pmml");
				downloadExec.setDependsOn(new MetaIdentifierHolder(
						new MetaIdentifier(MetaType.modelExec, execUuid, execVersion)));

				OutputStream out = response.getOutputStream();
				FileInputStream in = new FileInputStream(file);
				byte[] buffer = new byte[Integer.parseInt(file.length()+"")];
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				commonServiceImpl.save(MetaType.downloadExec.toString(), downloadExec);
				in.close();
				out.flush();
			} else {
				logger.info("PMML file requested " + "/" + location + "/" + fileName + ".pmml" + " not found.");
				response.setStatus(300);
				throw new FileNotFoundException();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			response.setStatus(500);
			throw new IOException(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			response.setStatus(500);
			throw new IOException(e.getMessage());
		}
		return response;

	}

	/********************** UNUSED **********************/
	/*public boolean predict(Predict predict, ExecParams execParams, PredictExec predictExec) throws Exception {	

		boolean isSuccess = false;
		try {
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.RUNNING);
			
			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(predict.getDependsOn().getRef().getUuid(),
					predict.getDependsOn().getRef().getVersion(), MetaType.model.toString());
			
			Algorithm algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(
					model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(),
					MetaType.algorithm.toString());

			String modelName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), predictExec.getVersion());
			String filePath = String.format("/%s/%s/%s", model.getUuid().replace("-", "_"), model.getVersion(), predictExec.getVersion());
			String tableName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), predictExec.getVersion());

			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			Object result = null;
			
			String[] fieldArray = modelExecServiceImpl.getAttributeNames(predict);
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			
			result = exec.predictModel(predict, fieldArray, algorithm, filePath, tableName, commonServiceImpl.getApp().getUuid());
			
			dataStoreServiceImpl.setRunMode(Mode.BATCH);

			dataStoreServiceImpl.create((String) result, modelName,
					new MetaIdentifier(MetaType.predict, predict.getUuid(), predict.getVersion()),
					new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()),
					predictExec.getAppInfo(), predictExec.getCreatedBy(), SaveMode.Append.toString(), resultRef);

			predictExec.setLocation((String) result);
			predictExec.setResult(resultRef);
			commonServiceImpl.save(MetaType.predictExec.toString(), predictExec);
			if (result != null) {
				isSuccess = true;
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.COMPLETED);
			}else {
				isSuccess = false;
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.FAILED);
			}
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Predict execution FAILED.");
			throw new RuntimeException((message != null) ? message : "Predict execution FAILED.");
		}
		return isSuccess;
	}*/

	public boolean simulate(Simulate simulate, ExecParams execParams, SimulateExec simulateExec, RunMode runMode) throws Exception {
		boolean isSuccess = false;
		execParams = (ExecParams) commonServiceImpl.resolveName(execParams, null);
		Distribution distribution = (Distribution) commonServiceImpl.getOneByUuidAndVersion(simulate.getDistributionTypeInfo().getRef().getUuid(), simulate.getDistributionTypeInfo().getRef().getVersion(), simulate.getDistributionTypeInfo().getRef().getType().toString());
		try {
			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.RUNNING);
			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(simulate.getDependsOn().getRef().getUuid(),
					simulate.getDependsOn().getRef().getVersion(), MetaType.model.toString());
	
			String appUuid = commonServiceImpl.getApp().getUuid();
			
			RunSimulateServiceImpl runSimulateServiceImpl = new RunSimulateServiceImpl();
			runSimulateServiceImpl.setSimulateExec(simulateExec);
			runSimulateServiceImpl.setSimulate(simulate);
			runSimulateServiceImpl.setModel(model);
			runSimulateServiceImpl.setSessionContext(sessionHelper.getSessionContext());
			runSimulateServiceImpl.setAppUuid(appUuid);
			runSimulateServiceImpl.setName(MetaType.simulateExec+"_"+simulateExec.getUuid()+"_"+simulateExec.getVersion());
			runSimulateServiceImpl.setHdfsInfo(hdfsInfo);
			runSimulateServiceImpl.setCommonServiceImpl(commonServiceImpl);
			runSimulateServiceImpl.setSparkExecutor(sparkExecutor);
			runSimulateServiceImpl.setRunMode(runMode);
			runSimulateServiceImpl.setModelServiceImpl(this);
			runSimulateServiceImpl.setModelExecServiceImpl(modelExecServiceImpl);
			runSimulateServiceImpl.setDatapodServiceImpl(datapodServiceImpl);
			runSimulateServiceImpl.setExecParams(execParams);
			runSimulateServiceImpl.setExecFactory(execFactory);
			runSimulateServiceImpl.setDistribution(distribution);
			runSimulateServiceImpl.setMonteCarloSimulation(monteCarloSimulation);
			runSimulateServiceImpl.setGenerateDataOperator(generateDataOperator);
			runSimulateServiceImpl.setSimulateMLOperator(simulateMLOperator);
			runSimulateServiceImpl.call();
			
			
//			MetaIdentifierHolder targetHolder = simulate.getTarget();
//			Datapod targetDp = null;
//			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod))
//				targetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(), targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());
//			
//			String modelName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), simulateExec.getVersion());
//			String filePath = String.format("/%s/%s/%s", model.getUuid().replace("-", "_"), model.getVersion(), simulateExec.getVersion());	
//			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), Helper.getPropertyValue("framework.model.simulate.path"), filePath);
//			
//			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
//			Object result = null;
//			String[] fieldArray = modelExecServiceImpl.getAttributeNames(simulate);
//			
//			Datasource datasource = commonServiceImpl.getDatasourceByApp();
//			IExecutor exec = execFactory.getExecutor(datasource.getType());
//			
//			ExecParams distExecParam = new ExecParams(); 
//			ExecParams simExecParam = new ExecParams(); 
//			
//			List<ParamListHolder> distParamHolderList = new ArrayList<>();
//			List<ParamListHolder> simParamHolderList= new ArrayList<>();
//			
//			String tableName = null;
//			List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
//			for(ParamListHolder holder : paramListInfo) {
//				if(simulate.getParamList() != null && holder.getRef().getUuid().equalsIgnoreCase(simulate.getParamList().getRef().getUuid())) {
//					simParamHolderList.add(holder);
//				} else if(holder.getRef().getUuid().equalsIgnoreCase(distribution.getParamList().getRef().getUuid())) {
//					distParamHolderList.add(holder);
//				}
//				if(holder.getParamName().equalsIgnoreCase("saveLocation")) {
//					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(holder.getParamValue().getRef().getUuid(), holder.getParamValue().getRef().getVersion(), holder.getParamValue().getRef().getType().toString());
//					tableName = datapodServiceImpl.genTableNameByDatapod(datapod, simulateExec.getVersion(), runMode);
//				}
//			}
//			distExecParam.setParamListInfo(distParamHolderList);
//			simExecParam.setParamListInfo(simParamHolderList);
//			
//			/*
//			 * New ParamListHolder for distribution  
//			 */
//			ParamListHolder distributionInfo = new ParamListHolder();
//			distributionInfo.setParamId("0");
//			distributionInfo.setParamName("distribution");
//			distributionInfo.setParamType("distribution");
//			MetaIdentifier distIdentifier = new MetaIdentifier(MetaType.distribution, distribution.getUuid(), distribution.getVersion());
//			MetaIdentifierHolder distHolder = new MetaIdentifierHolder(distIdentifier);
//			distributionInfo.setParamValue(distHolder);
//			distributionInfo.setRef(new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()));
//			
//			/*
//			 * New ParamListHolder for numIterations  
//			 */
//			ParamListHolder numIterationsInfo = new ParamListHolder();
//			numIterationsInfo.setParamId("1");
//			numIterationsInfo.setParamName("numIterations");
//			distributionInfo.setParamType("integer");
//			MetaIdentifierHolder numIterHolder = new MetaIdentifierHolder(null, ""+simulate.getNumIterations());
//			numIterationsInfo.setParamValue(numIterHolder);
//			numIterationsInfo.setRef(new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()));
//			
//			List<ParamListHolder> paramListInfo2 = execParams.getParamListInfo();
//			paramListInfo2.add(distributionInfo);
//			paramListInfo2.add(numIterationsInfo);
//			execParams.setParamListInfo(paramListInfo2);
//			
//			String appUuid = commonServiceImpl.getApp().getUuid();
//			long count = 0;
//			if(simulate.getType().equalsIgnoreCase(SimulationType.MONTECARLO.toString())) {
//				result = monteCarloSimulation.simulateMonteCarlo(simulate, simExecParam, distExecParam, filePathUrl);
//			} else if(simulate.getType().equalsIgnoreCase(SimulationType.DEFAULT.toString())) {
//				if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
//					
//					HashMap<String, String> otherParams = execParams.getOtherParams();
//					if(otherParams == null)
//						otherParams = new HashMap<>();
//					otherParams = (HashMap<String, String>) generateDataOperator.customCreate(simulateExec, execParams, runMode);
//					
//					//tableName = generateDataOperator.execute(null, execParams, new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()), null, otherParams, null, runMode);
//
//					String tabName_2 = null;
//					String tableName_3 = null;
//					if(distribution.getClassName().contains("UniformRealDistribution")) {
//						List<Feature> features = model.getFeatures();
//						for(int i=0; i<fieldArray.length; i++) {
//							List<ParamListHolder> paramListHolderes = distExecParam.getParamListInfo();
//							Feature feature = features.get(i);
//							for(ParamListHolder holder : paramListHolderes) {
//								if(holder.getParamName().equalsIgnoreCase("upper")) {
//									holder.getParamValue().setValue(""+feature.getMaxVal());
//								}
//								if(holder.getParamName().equalsIgnoreCase("lower")) {
//									holder.getParamValue().setValue(""+feature.getMinVal());
//								}
//							}
////							
//							tableName = generateDataOperator.execute(simulateExec, execParams, runMode);
////							String[] customFldArr = new String[] {fieldArray[i]};
////							tabName_2 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);
//							tabName_2 = exec.renameColumn(tableName, 1, fieldArray[i], appUuid);
//							String sql = simulateMLOperator.generateSql(simulate, tabName_2);
//							result = exec.executeAndRegister(sql, tabName_2, appUuid);//(sql, tabName_2, filePath, null, SaveMode.Append.toString(), appUuid);
//
//							if(i == 0)
//								tableName_3 = tabName_2;
//							if(i>0)
//								tableName_3 = exec.joinDf(tableName_3, tabName_2, i, appUuid);
//						}
//						
//						String sql = "SELECT * FROM " + tableName_3;					
//						if(simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
//							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath, targetDp, SaveMode.APPEND.toString(), true, appUuid);	
//							result = rsHolder;						
//							count = rsHolder.getCountRows();
//							createDatastore(filePath, simulate.getName(), 
//									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()), 
//									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(), simulateExec.getVersion()),
//									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//									Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
//						}
//						
//						tableName_3 = exec.assembleRandomDF(fieldArray, tableName_3, false, appUuid);
//					} else {
//						tableName = generateDataOperator.execute(simulateExec, execParams, runMode);
//						
//						String sql = "SELECT * FROM " + tableName;	
//						tableName_3 = tableName;
//						if(simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
//							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath, targetDp, SaveMode.APPEND.toString(), true, appUuid);	
//							result = rsHolder;						
//							count = rsHolder.getCountRows();
//							createDatastore(filePath, simulate.getName(), 
//									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()), 
//									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(), simulateExec.getVersion()),
//									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//									Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
//						}
//						
//						String[] customFldArr = new String[] {fieldArray[0]};
//						tableName_3 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);
//					}
//					
//					String sql = "SELECT * FROM " + tableName_3;
//					ResultSetHolder rsHolder = sparkExecutor.writeResult(sql, null, filePathUrl, null, SaveMode.APPEND.toString(), tableName_3, "false", appUuid);
////					ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePathUrl, targetDp, SaveMode.Append.toString(), false, appUuid);	
//					result = rsHolder;						
//					count = rsHolder.getCountRows();
//				} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
//					
//					HashMap<String, String> otherParams = execParams.getOtherParams();
//					if(otherParams == null)
//						otherParams = new HashMap<>();
//					otherParams = (HashMap<String, String>) generateDataOperator.customCreate(simulateExec, execParams, runMode);
//					
////					tableName = generateDataOperator.execute(null, execParams, new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()), null, otherParams, null, runMode);
//					
//					String tabName_2 = null;
//					String tableName_3 = null;
//					if(distribution.getClassName().contains("UniformRealDistribution")) {
//						List<Feature> features = model.getFeatures();
//						for(int i=0; i<fieldArray.length; i++) {
//							List<ParamListHolder> paramListHolderes = distExecParam.getParamListInfo();
//							Feature feature = features.get(i);
//							for(ParamListHolder holder : paramListHolderes) {
//								if(holder.getParamName().equalsIgnoreCase("upper")) {
//									holder.getParamValue().setValue(""+feature.getMaxVal());
//								}
//								if(holder.getParamName().equalsIgnoreCase("lower")) {
//									holder.getParamValue().setValue(""+feature.getMinVal());
//								}
//							}
//							
//							tableName = generateDataOperator.execute(simulateExec, execParams, runMode);
////							String[] customFldArr = new String[] {fieldArray[i]};
////							tabName_2 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);
//							tabName_2 = exec.renameColumn(tableName, 1, fieldArray[i], appUuid);
//							if(i == 0)
//								tableName_3 = tabName_2;
//							if(i>0)
//								tableName_3 = exec.joinDf(tableName_3, tabName_2, i, appUuid);
//						}
//
//						String sql = "SELECT * FROM " + tableName_3;
//						if(simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
//							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath, targetDp, SaveMode.APPEND.toString(), true, appUuid);	
//							result = rsHolder;
//							count = rsHolder.getCountRows();
//							createDatastore(filePath, simulate.getName(), 
//									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()), 
//									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(), simulateExec.getVersion()),
//									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//									Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
//						} 
//						
//						tableName_3 = exec.assembleRandomDF(fieldArray, tableName_3, false, appUuid);
//					} else {						
//						tableName = generateDataOperator.execute(simulateExec, execParams, runMode);
//						
//						String sql = "SELECT * FROM " + tableName;
//						tableName_3 = tableName;
//						if(simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
//							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath, targetDp, SaveMode.APPEND.toString(), true, appUuid);	
//							result = rsHolder;
//							count = rsHolder.getCountRows();
//							createDatastore(filePath, simulate.getName(), 
//									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()), 
//									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(), simulateExec.getVersion()),
//									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//									Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
//						} 
//						
//						String[] customFldArr = new String[] {fieldArray[0]};						
//						tableName_3 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);
//					}
//					
//					String sql = "SELECT * FROM " + tableName_3;	
//					ResultSetHolder rsHolder = sparkExecutor.writeResult(sql, null, filePathUrl, null, SaveMode.APPEND.toString(), tableName_3, null, appUuid);
////					ResultSetHolder rsHolder = exec.cczexecuteRegisterAndPersist(sql, tableName_3, filePathUrl, targetDp, SaveMode.Append.toString(), false, appUuid);	
//					result = rsHolder;
//					count = rsHolder.getCountRows();
//				}
//			}
//
//			createDatastore(filePathUrl, modelName,
//					new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()),
//					new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()),
//					simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//					Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
//
//			simulateExec.setLocation(filePathUrl);
//			simulateExec.setResult(resultRef);
//			commonServiceImpl.save(MetaType.simulateExec.toString(), simulateExec);
//			if (result != null) {
//				isSuccess = true;
//				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.COMPLETED);
//			}else {
//				isSuccess = false;
//				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.FAILED);
//			}
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			isSuccess = false;
			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.FAILED);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Simulate execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Simulate execution FAILED.");
		}

		return isSuccess;
	}

	public PredictExec create(Predict predict, ExecParams execParams, ParamMap paramMap,
			PredictExec predictExec) throws Exception {
		
		MetaIdentifierHolder predictRef = new MetaIdentifierHolder();		
		if (predictExec == null) {
			predictExec = new PredictExec();
			predictRef.setRef(new MetaIdentifier(MetaType.predict, predict.getUuid(), predict.getVersion()));
			predictExec.setDependsOn(predictRef);
			predictExec.setBaseEntity();
		}

		try {
			predictExec.setName(predict.getName());
			predictExec.setAppInfo(predict.getAppInfo());
			
			List<Status> statusList = predictExec.getStatusList();
			if (statusList == null) 
				statusList = new ArrayList<Status>();
			
			if (Helper.getLatestStatus(statusList) != null 
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.RUNNING, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.COMPLETED, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.PAUSE, new Date())))) {
				logger.info(" This process is RUNNING or has been COMPLETED previously or is On Hold. Hence it cannot be rerun. ");
				return predictExec;
			}
			if (Helper.getLatestStatus(statusList) != null 
					&& Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.READY, new Date()))) {
				logger.info("predictExec is in READY state. Run directly. Don't set it to PENDING state again. ");
				return predictExec;
			}
			
			synchronized (predictExec.getUuid()) {
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.PENDING);
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.STARTING);
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.READY);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);				
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.FAILED);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable Predict.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Can not create executable Predict.");
		}
		return predictExec;
	}

	public SimulateExec create(Simulate simulate, ExecParams execParams, ParamMap paramMap,
			SimulateExec simulateExec) throws Exception {

		MetaIdentifierHolder simulateRef = new MetaIdentifierHolder();	
		if (simulateExec == null) {
			simulateExec = new SimulateExec();
			simulateRef.setRef(new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()));
			simulateExec.setDependsOn(simulateRef);
			simulateExec.setBaseEntity();
		}
		
		try {
			simulateExec.setName(simulate.getName());
			simulateExec.setAppInfo(simulate.getAppInfo());	
			commonServiceImpl.save(MetaType.simulateExec.toString(), simulateExec);
			
			List<Status> statusList = simulateExec.getStatusList();
			if (statusList == null) 
				statusList = new ArrayList<Status>();
			
			if (Helper.getLatestStatus(statusList) != null 
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.RUNNING, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.COMPLETED, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.PAUSE, new Date())))) {
				logger.info(" This process is RUNNING or has been COMPLETED previously or is On Hold. Hence it cannot be rerun. ");
				return simulateExec;
			}
			
			if (Helper.getLatestStatus(statusList) != null 
					&& Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.READY, new Date()))) {
				logger.info("simulateExec is in READY state. Run directly. Don't set it to PENDING state again. ");
				return simulateExec;
			}
			
			synchronized (simulateExec.getUuid()) {
				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.PENDING);
				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.STARTING);
				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.READY);
			}
		} catch (Exception e) {
			logger.error(e);	
			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.FAILED);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable Simulate.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Can not create executable Simulate.");
		}
		return simulateExec;
	}
	
	public TrainExec create(Train train, Model model, ExecParams execParams, ParamMap paramMap,
			TrainExec trainExec) throws Exception {
//		String logPath = null;
		
		try {
			MetaIdentifierHolder trainRef = new MetaIdentifierHolder();
					
			if (trainExec == null) {
				trainExec = new TrainExec();
				trainRef.setRef(new MetaIdentifier(MetaType.train, train.getUuid(), train.getVersion()));
				trainExec.setDependsOn(trainRef);
				trainExec.setBaseEntity();
				
				/*
				 * 
				 * log file_name formation : modeluuid + modelversion + trainexecversion
				 * 
				 */
//				if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//					logPath = Helper.getPropertyValue("framework.model.log.path") + "/" + model.getUuid() + "_" + model.getVersion() + "_"+ trainExec.getVersion()+".log";
//					logger.info(" Custom log path : " + logPath);
//				}
//				if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//					customLogger.writeLog(this.getClass(),
//							"Created raw model exec, uuid: " + trainExec.getUuid(), 
//							logPath,
//							Thread.currentThread().getStackTrace()[1].getLineNumber());
//				}
			}
			trainExec.setExecParams(execParams);
			
			Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
			List<Status> statusList = trainExec.getStatusList();
			if (statusList == null) {
				statusList = new ArrayList<Status>();
			}
			trainExec.setName(train.getName());
			trainExec.setAppInfo(train.getAppInfo());	
			//iModelExecDao.save(modelExec);
//			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//				logPath = Helper.getPropertyValue("framework.model.log.path") + "/" + model.getUuid() + "_" + model.getVersion() + "_"+ trainExec.getVersion()+".log";
//				logger.info(" Custom log path : " + logPath);                                                                                                         
//				customLogger.writeLog(this.getClass(), 
//						"Saving raw modelExec, uuid: " + trainExec.getUuid(),
//						logPath, 
//						Thread.currentThread().getStackTrace()[1].getLineNumber());
//			}
			commonServiceImpl.save(MetaType.trainExec.toString(), trainExec);
			
			if (Helper.getLatestStatus(statusList) != null 
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.RUNNING, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.COMPLETED, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.PAUSE, new Date())))) {
				logger.info(" This process is RUNNING or has been COMPLETED previously or is On Hold. Hence it cannot be rerun. ");
//				if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//					customLogger.writeLog(this.getClass(),
//							"This process is RUNNING or has been COMPLETED previously or is On Hold. Hence it cannot be rerun.", 
//							logPath,
//							Thread.currentThread().getStackTrace()[1].getLineNumber());
//				}
				return trainExec;
			}
			
			if (Helper.getLatestStatus(statusList) != null 
					&& Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.READY, new Date()))) {
				logger.info("trainExec is in READY state. Run directly. Don't set it to PENDING state again. ");
				return trainExec;
			}
			
			//modelExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet));
			trainExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
			synchronized (trainExec.getUuid()) {
				trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.PENDING);
				trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.STARTING);
				trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.READY);
			}
//			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//				customLogger.writeLog(this.getClass(),
//						trainExec.getStatusList().size()>0 ? "Latest status: "+trainExec.getStatusList().get(trainExec.getStatusList().size()-1).getStage() : "Status list is empty", 
//						logPath,
//						Thread.currentThread().getStackTrace()[1].getLineNumber());
//			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
//			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//				customLogger.writeErrorLog(this.getClass(), StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), System.lineSeparator()), 
//						logPath,
//						Thread.currentThread().getStackTrace()[1].getLineNumber());
//			
//			}
			
			trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.FAILED);
//			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
//				customLogger.writeLog(this.getClass(), trainExec.getStatusList().size()>0 ? "Train exec creation FAILED, status: "+trainExec.getStatusList().get(trainExec.getStatusList().size()-1).getStage() : "Status list is empty", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
//			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable TraintrainExec.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Can not create executable Train.");
		}		
		return trainExec;
	}
	
	public TrainExec train(Train train, Model model, TrainExec  trainExec, ExecParams execParams, ParamMap paramMap, RunMode runMode, Object algoClass) throws FileNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException{
		
		if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
			commonServiceImpl.sendResponse("400", MessageStatus.FAIL.toString(), "Training can not be performed on formula.", null);
			throw new RuntimeException("Training can not be performed on formula."); 
		}
		
		RunTrainServiceImpl runTrainServiceImpl = new RunTrainServiceImpl();
		List<Status> statusList = trainExec.getStatusList();
		if (statusList == null) {
			statusList = new ArrayList<>();
			trainExec.setStatusList(statusList);
		}
	
		if(!model.getType().equalsIgnoreCase(ExecContext.R.toString()) && !model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
			runTrainServiceImpl.setAlgorithmUUID(model.getDependsOn().getRef().getUuid());
			runTrainServiceImpl.setAlgorithmVersion(model.getDependsOn().getRef().getVersion());
		}
		runTrainServiceImpl.setAlgorithmServiceImpl(algorithmServiceImpl);
		runTrainServiceImpl.setDataStoreServiceImpl(dataStoreServiceImpl);
		runTrainServiceImpl.setModel(model);
		runTrainServiceImpl.setModelExecServiceImpl(modelExecServiceImpl);
		runTrainServiceImpl.setHdfsInfo(hdfsInfo);
		runTrainServiceImpl.setExecParams(execParams);
		runTrainServiceImpl.setParamSetServiceImpl(paramSetServiceImpl);
		runTrainServiceImpl.setParamListServiceImpl(paramListServiceImpl);
		runTrainServiceImpl.setParamMap(paramMap);
		runTrainServiceImpl.setCommonServiceImpl(commonServiceImpl);
		runTrainServiceImpl.setRunMode(runMode);
		runTrainServiceImpl.setDataFrameService(dataFrameService);
		runTrainServiceImpl.setSessionContext(sessionHelper.getSessionContext());
		runTrainServiceImpl.setCustomLogger(new CustomLogger());
		runTrainServiceImpl.setModelType(model.getType());
		runTrainServiceImpl.setModelServiceImpl(this);
		if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) 
			runTrainServiceImpl.setLogPath(Helper.getPropertyValue("framework.model.train.path") + "/" + model.getUuid() + "_" + model.getVersion() + "_"+ trainExec.getVersion()+".log");
		runTrainServiceImpl.setExecFactory(execFactory);
		runTrainServiceImpl.setSecurityServiceImpl(securityServiceImpl);
		runTrainServiceImpl.setTrainExec(trainExec);
		runTrainServiceImpl.setTrain(train);
		runTrainServiceImpl.setName(MetaType.trainExec+"_"+trainExec.getUuid()+"_"+trainExec.getVersion());
		runTrainServiceImpl.setExecType(MetaType.trainExec);
		runTrainServiceImpl.setMetadataServiceImpl(metadataServiceImpl);
		runTrainServiceImpl.setAlgoclass(algoClass);
		runTrainServiceImpl.setDl4jExecutor(dl4jExecutor);
		runTrainServiceImpl.setTrainResultViewServiceImpl(trainResultViewServiceImpl);
		runTrainServiceImpl.setImputeOperator(imputeOperator);
		/*FutureTask<TaskHolder> futureTask = new FutureTask<TaskHolder>(runModelServiceImpl);
		metaExecutor.execute(futureTask);
		taskList.add(futureTask);
		taskThreadMap.put(MetaType.trainExec+"_"+trainExec.getUuid()+"_"+trainExec.getVersion(), futureTask);
		logger.info(" taskThreadMap while creating train : " + taskThreadMap);*/
		runTrainServiceImpl.call();
		return trainExec;
	}
	
	public HttpServletResponse downloadLog(String trainExecUuid, String trainExecVersion, HttpServletResponse response,RunMode runMode) throws Exception {

		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUuid, trainExecVersion,
				MetaType.trainExec.toString());
		MetaIdentifierHolder dependsOn = trainExec.getDependsOn();
		String fileName = trainExec.getUuid() + "_" + trainExec.getVersion() + "_" + dependsOn.getRef().getVersion()
				+ ".log";

		String filePath = Helper.getPropertyValue("framework.model.log.path") + "/" + fileName;

		try {
			response.setContentType("text/plain");
			response.setHeader("Content-disposition", "attachment");

			response.setHeader("filename", "" + fileName + ".log");

			File file = new File(filePath);

			if (file.exists()) {
				DownloadExec downloadExec = new DownloadExec();

				downloadExec.setBaseEntity();
				downloadExec.setLocation(filePath);
				downloadExec.setDependsOn(new MetaIdentifierHolder(
						new MetaIdentifier(MetaType.trainExec, trainExecUuid, trainExecVersion)));

				OutputStream out = response.getOutputStream();
				FileInputStream in = new FileInputStream(file);
				byte[] buffer = new byte[Integer.parseInt(file.length() + "")];
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				commonServiceImpl.save(MetaType.downloadExec.toString(), downloadExec);
				in.close();
				out.flush();
			} else {
				logger.info("Requested file " + "/" + filePath + "/" + fileName + ".log" + " not found.");
				response.setStatus(300);
				throw new FileNotFoundException();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			response.setStatus(500);
			throw new IOException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			response.setStatus(500);
			throw new IOException(e.getMessage());
		}

		return response;

	}

	public boolean save(String className, Object obj, String path) {
	
		Class<?> dynamicClass = obj.getClass();

		Class<?>[] paramSave = new Class[1];
		paramSave[0] = String.class;

		Method m1 = null;
		try {
			m1 = dynamicClass.getMethod("save", paramSave);
			try {
				logger.info("Model save location : " + path);
				m1.invoke(obj, path);
				return true;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//				e.printStackTrace();
				logger.info("Model alReady exists on path '"+path+"', overriding the olde model...");
				String expMessage = e.getCause().getMessage();
				if (expMessage.contains("use write().overwrite().save(path) for Java")) {
					try {
						Object mlWriter = obj.getClass().getMethod("write").invoke(obj);
						Object mlWriter_2 = mlWriter.getClass().getMethod("overwrite").invoke(mlWriter);
						mlWriter_2.getClass().getMethod("save", String.class).invoke(mlWriter_2, path);
						return true;
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ioExp) {
						ioExp.printStackTrace();
						return false;
					} catch (Exception e2) {
						e2.printStackTrace();
						return false;
					}
				}
			}
		} catch (NoSuchMethodException 
				| SecurityException e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	public List<Model> getAllModelByType(String customFlag, String modelType) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Criteria criteria = new Criteria();
		List<Criteria> criteriaList = new ArrayList<Criteria>();

		try {
			criteriaList.add(where("active").is("Y"));
			criteriaList.add(where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
			criteriaList.add(where("customFlag").is(customFlag));		
			if(modelType != null)
				criteriaList.add(where("dependsOn.ref.type").is(modelType));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
		Aggregation ruleExecAggr;
		if (criteriaList.size() > 0) {
			ruleExecAggr = newAggregation(match(criteria2), group("uuid").max("version").as("version"));
		} else {
			ruleExecAggr = newAggregation(group("uuid").max("version").as("version"));
		}
		AggregationResults<Model> ruleExecResults = mongoTemplate.aggregate(ruleExecAggr, MetaType.model.toString(), Model.class);

		List<Model> modelList = new ArrayList<>();
		for (Model metaObject : (List<Model>)ruleExecResults.getMappedResults()) {
			Model model = new Model();
			Model modelTemp = (Model) commonServiceImpl.getLatestByUuid(metaObject.getId(), MetaType.model.toString()); 
			model.setId(modelTemp.getId());
			model.setUuid(modelTemp.getUuid());
			model.setVersion(modelTemp.getVersion());
			model.setName(modelTemp.getName());
			model.setDesc(modelTemp.getDesc());
			model.setCreatedBy(modelTemp.getCreatedBy());
			model.setCreatedOn(modelTemp.getCreatedOn());
			model.setTags(modelTemp.getTags());
			model.setActive(modelTemp.getActive());
			model.setPublished(modelTemp.getPublished());
			model.setAppInfo(modelTemp.getAppInfo());
			model.setType(modelTemp.getType());
			model.setDependsOn(modelTemp.getDependsOn());
			model.setLabel(modelTemp.getLabel());
			model.setFeatures(modelTemp.getFeatures());
			model.setScriptName(modelTemp.getScriptName());
			model.setCustomFlag(modelTemp.getCustomFlag());
			modelList.add(model);
		}
		return modelList;
	}
	
	/**
	 * 
	 * @param distribution
	 * @return
	 * @throws JsonProcessingException
	 *//*
	public Row getInstruments(List<Param> params) throws JsonProcessingException {
		Row dataset = null;
		
		int parallelism = Integer.parseInt(params.get(3).getParamValue());
		String str = params.get(4).getParamValue();
		String[] splits = str.split(",");
		List<Double> datasetList = new ArrayList<>();
		for(String split : splits)
			datasetList.add(Double.parseDouble(split));
		dataset = RowFactory.create(datasetList.toArray());
		return dataset;
	}*/
	
	/**
	 * 
	 * @param factorMeansInfo
	 * @return
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws IOException 
	 *//*
	public double[] getMeans(MetaIdentifierHolder factorMeansInfo) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		IExecutor executor = execFactory.getExecutor(ExecContext.spark.toString());
		Datapod factorMeanDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(factorMeansInfo.getRef().getUuid(), factorMeansInfo.getRef().getVersion(), factorMeansInfo.getRef().getType().toString());
		DataStore factorMeanDs = dataStoreServiceImpl.findDataStoreByMeta(factorMeanDp.getUuid(), factorMeanDp.getVersion());

		double[] factorMeans = 	executor.oneDArrayFromDatapod(null, factorMeanDp);
		return factorMeans;
		
	}*/
	
	/**
	 * 
	 * @param factorCovariancesInfo
	 * @return
	 * @throws Exception 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NullPointerException
	 * @throws ParseException
	 * @throws IOException
	 *//*
	public double[][] getCovs(MetaIdentifierHolder factorCovariancesInfo) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		IExecutor executor = execFactory.getExecutor(ExecContext.spark.toString());
		Datapod factorCovarianceDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(factorCovariancesInfo.getRef().getUuid(), factorCovariancesInfo.getRef().getVersion(), factorCovariancesInfo.getRef().getType().toString());
		DataStore factorCovarianceDs = dataStoreServiceImpl.findDataStoreByMeta(factorCovarianceDp.getUuid(), factorCovarianceDp.getVersion());

		double[][] factorCovariances = executor.twoDArrayFromDatapod(null, factorCovarianceDp);
		return factorCovariances;
	}*/
	
	public String generateSQLBySource(Object source, ExecParams execParams) throws Exception {  
		if (source instanceof Datapod) {
			Datapod datapod = (Datapod) source;
			DataStore datastore = dataStoreServiceImpl.findLatestByMeta(datapod.getUuid(), datapod.getVersion());
			if (datastore == null) {
				logger.error("Datastore is not available for this datapod");
				throw new Exception();
			}			
		
			String tableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), RunMode.BATCH);
			logger.info("Table name : " + tableName);
			String sql = "SELECT * FROM "+tableName;
			return sql;
		} else if (source instanceof DataSet) {
			DataSet dataset = (DataSet) source;
			String sql = datasetOperator.generateSql(dataset, null, null, new HashSet<>(), execParams, RunMode.BATCH);
			return sql;
		} else if (source instanceof Rule) {
			Rule rule = (Rule) source;
			String sql = ruleOperator.generateSql(rule, null, null, new HashSet<>(), null, RunMode.BATCH);
			return sql;
		}
		return null;
	}

	public List<String> getRowIdentifierCols(List<AttributeRefHolder> rowIdentifier) throws JsonProcessingException {
		if(rowIdentifier != null) {
			List<String> rowIdentifierCols = new ArrayList<>();
			
			String oldSrcUuid = null;
			Object source = null;
			
			for(AttributeRefHolder rowIDHolder : rowIdentifier) {
				String attrName = rowIDHolder.getAttrName();
				if(attrName != null) {
					rowIdentifierCols.add(attrName);
				} else {
					MetaIdentifier attrMI = rowIDHolder.getRef();
					if(oldSrcUuid != null && oldSrcUuid.equalsIgnoreCase(attrMI.getUuid())) {
						attrName = getColNameBySource(source, rowIDHolder.getAttrId());
						
						if(attrName != null) {
							rowIdentifierCols.add(attrName);
						}
					} else {
						oldSrcUuid = attrMI.getUuid();
						source = commonServiceImpl.getOneByUuidAndVersion(attrMI.getUuid(), attrMI.getVersion(), attrMI.getType().toString());
						attrName = getColNameBySource(source, rowIDHolder.getAttrId());

						if(attrName != null) {
							rowIdentifierCols.add(attrName);
						}
					}
				}				
			}
			
			return rowIdentifierCols;
		}
		return null;
	}

	public String getColNameBySource(Object source, String attrId) {
		if (source instanceof Datapod) {
			Datapod datapod = (Datapod) source;			
			return datapod.getAttributeName(Integer.parseInt(attrId));
		} else if (source instanceof DataSet) {
			DataSet dataset = (DataSet) source;			
			return dataset.getAttributeName(Integer.parseInt(attrId));
		} else if (source instanceof Rule) {
			Rule rule = (Rule) source;
			return rule.getAttributeName(Integer.parseInt(attrId));
		}
		return null;
	}
	
	public Object getTrainedModelByTrainExec(String modelClassName, TrainExec trainExec) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		Class<?> modelClass = Class.forName(modelClassName);

		MetaIdentifierHolder datastoreHolder = trainExec.getResult();
		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainExec.getDependsOn().getRef().getUuid(), 
																		trainExec.getDependsOn().getRef().getVersion(), 
																		trainExec.getDependsOn().getRef().getType().toString());
		Model model = null;
		if (train != null) {
			model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), 
															train.getDependsOn().getRef().getVersion(), 
															train.getDependsOn().getRef().getType().toString());
		}
		DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreHolder.getRef().getUuid(),
				datastoreHolder.getRef().getVersion(), datastoreHolder.getRef().getType().toString());
		if (dataStore == null)
			throw new NullPointerException("No datastore available.");
		String location = dataStore.getLocation();

		if (location.contains("/data"))
			location = location.replaceAll("/data", "");
		if(!location.contains(hdfsInfo.getHdfsURL()))
			location = hdfsInfo.getHdfsURL() + location;
		if (location.contains("/model")) {
			location = location.replaceAll("/model/.*", "/model/");
		}
		logger.info("Model paths for prediction : " + location);

		//Object trainedModel = modelClass.getMethod("load", String.class).invoke(modelClass, location);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		if (model != null && model.getType().equalsIgnoreCase("SPARK")) {
			modelClass = PipelineModel.class;
		}
		Object trainedModel = exec.loadTrainedModel(modelClass, location);
		/*PipelineModel pipelineModel = (PipelineModel)trainedModel;
		Transformer []transformers = pipelineModel.stages();
		for (Transformer transformer: transformers) {
			logger.info("Transoformer : " + transformer.uid());
		}*/
		return trainedModel;
	}

	public boolean predict(Predict predict, ExecParams execParams, PredictExec predictExec, RunMode runMode) throws Exception {
		logger.info("Inside predict");
		boolean isSuccess = false;
		try {
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.RUNNING);
			
			MetaIdentifierHolder modelHolder = predict.getDependsOn();
//			MetaIdentifierHolder sourceHolder = predict.getSource();
//			MetaIdentifierHolder targetHolder = predict.getTarget();

			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelHolder.getRef().getUuid(),
					modelHolder.getRef().getVersion(), modelHolder.getRef().getType().toString());
//			Object source = (Object) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
//					sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//			
//			Datapod target = null;
//			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod))
//				target = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(),
//						targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());
			
			Algorithm algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(
					model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(),
					MetaType.algorithm.toString());

			String appUuid = commonServiceImpl.getApp().getUuid();
			
			RunPredictServiceImpl runPredictServiceImpl = new RunPredictServiceImpl();
			runPredictServiceImpl.setPredictExec(predictExec);
			runPredictServiceImpl.setPredict(predict);
			runPredictServiceImpl.setModel(model);
			runPredictServiceImpl.setAlgorithm(algorithm);
			runPredictServiceImpl.setSessionContext(sessionHelper.getSessionContext());
			runPredictServiceImpl.setAppUuid(appUuid);
			runPredictServiceImpl.setName(MetaType.predictExec+"_"+predictExec.getUuid()+"_"+predictExec.getVersion());
			runPredictServiceImpl.setHdfsInfo(hdfsInfo);
			runPredictServiceImpl.setCommonServiceImpl(commonServiceImpl);
			runPredictServiceImpl.setSparkExecutor(sparkExecutor);
			runPredictServiceImpl.setRunMode(runMode);
			runPredictServiceImpl.setModelServiceImpl(this);
			runPredictServiceImpl.setModelExecServiceImpl(modelExecServiceImpl);
			runPredictServiceImpl.setExecParams(execParams);
			runPredictServiceImpl.setExecFactory(execFactory);
			runPredictServiceImpl.setImputeOperator(imputeOperator);
			runPredictServiceImpl.setPredictMLOperator(predictMLOperator);
			runPredictServiceImpl.setTrainedModelMap(trainedModelMap);			
			runPredictServiceImpl.call();

			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.FAILED);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Predict execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Predict execution FAILED.");
		}
		return isSuccess;
	}
	
	public Map<String, EncodingType> getEncodingDetails(List<FeatureAttrMap> featureAttrMap, List<Feature> features) {
		Map<String, EncodingType> encodingDetails = new LinkedHashMap<>();
		Map<String, Feature> featureModelMap = new HashMap<>();
		for (Feature feature : features) {
			featureModelMap.put(feature.getFeatureId(), feature);
		}
		for(FeatureAttrMap attrMap : featureAttrMap) {
			EncodingType encodingType = (featureModelMap.containsKey(attrMap.getFeature().getFeatureId()))?
					featureModelMap.get(attrMap.getFeature().getFeatureId()).getEncodingType():null;
			if (encodingType != null) {
				encodingDetails.put(attrMap.getFeature().getFeatureName(), encodingType);
			}
		}
		if(!encodingDetails.isEmpty()) {
			return encodingDetails;
		}else {
			return null;
		}
	}
	
	public String[] getMappedAttrs(List<FeatureAttrMap> mappedFeatures) {
		String[] mappedAttrs = new String[mappedFeatures.size()];
		
		for(int i=0; i < mappedFeatures.size(); i++) {
			mappedAttrs[i] = mappedFeatures.get(i).getFeature().getFeatureName();
		}
		return mappedAttrs;
	}

	/********************** UNUSED **********************/
//	public void customCreate(ExecParams execParams, String tableName, RunMode runMode, List<Map<String, String>> labelFeatures, List<FeatureAttrMap> featureAttrMap) throws Exception {
//		String featureName = null;
//		int count = 0;
//		StructType structType = null;
////		List<StructField> structList = new ArrayList<>();
//		StructField []stuctFields = null;
//		int i = 0;
//		List<Row> rows = new ArrayList<>();
//		for (Map<String, String> labelFeature : labelFeatures) {
//			Object []values = new Object[featureAttrMap.size()];
//			DataType dataType = null;
//			if (count == 0) {
//				stuctFields = new StructField[featureAttrMap.size()];
//			}
//			for (FeatureAttrMap featureAttr : featureAttrMap) {
//				featureName = featureAttr.getFeature().getFeatureName();
//				if (count == 0) {
//					dataType = DataTypes.DoubleType;
//					/*if (featureAttr.getAttribute().getAttrType().equalsIgnoreCase("double")) {
//						dataType = DataTypes.DoubleType;
//					} else if (featureAttr.getAttribute().getAttrType().equalsIgnoreCase("integer")) {
//						dataType = DataTypes.IntegerType;
//					} else if (featureAttr.getAttribute().getAttrType().equalsIgnoreCase("string")) {
//						dataType = DataTypes.StringType;
//					}*/
//					stuctFields[i] = new StructField(featureName, dataType, true, Metadata.empty());
//				}
//				values[i] = Double.parseDouble(labelFeature.get(featureName));
//				i++;
//			}
//			Row row = RowFactory.create(values);
//			rows.add(row);
//		}
////		stuctFields = new StructField[structList.size()];
//		structType = new StructType(stuctFields);
//		
//		Datasource datasource = commonServiceImpl.getDatasourceByApp();
//		IExecutor exec = execFactory.getExecutor(datasource.getType());
//		exec.createAndRegister(rows, structType, tableName, commonServiceImpl.getApp().getUuid());
//		
////		otherParams.put("datapodUuid_" + tableName + "_tableName", tableName);
//			
////		return otherParams;
//	}

	/********************** UNUSED **********************/
//	public List<String> predict(Predict predict, PredictExec predictExec, ExecParams execParams, Model model, RunMode runMode, String appUuid, List<Map<String, String>> labelFeatures) throws Exception {
//		String tableName = predictExec.getUuid() + "_" + predictExec.getVersion() + "_" + model.getUuid();
//		tableName = tableName.replaceAll("-", "_");
//		//getting data having only feature columns
//		String mappedFeatureAttrSql = generateFeatureSQLByTempTable(predict.getFeatureAttrMap(), (tableName+"_pred_data"), null, (tableName+"_pred_mapped_data"));
//		customCreate(execParams, tableName+"_pred_data", runMode, labelFeatures, predict.getFeatureAttrMap());
//		ResultSetHolder rsHolder = sparkExecutor.readTempTable(mappedFeatureAttrSql, appUuid);
//		sparkExecutor.registerTempTable(rsHolder.getDataFrame(), (tableName+"_pred_mapped_data"));
//		
//		
//		String []fieldArray = getMappedAttrs(predict.getFeatureAttrMap());
//		
//		//assembling the data to for feature vector
//		Datasource datasource = commonServiceImpl.getDatasourceByApp();
//		IExecutor exec = execFactory.getExecutor(datasource.getType());
////		Datasource sourceDS =  Get Spark datasource
//		exec.assembleDF(fieldArray, rsHolder, null, (tableName+"_pred_assembled_data"), datasource, true, appUuid);
////		Object trainedModel = getTrainedModelByTrainExec(algorithm.getModelClass(), trainExec);
//		
//		//prediction operation
//		rsHolder =  exec.predict(model, null, null, (tableName+"_pred_assembled_data"), appUuid, null);
//
//		List<String> rowIdentifierCols = getRowIdentifierCols(predict.getRowIdentifier());
//		return rowIdentifierCols;
//	}

	
	public String generateFeatureSQLBySource(List<FeatureAttrMap> mappedFeatures, Object source, ExecParams execParams, String[] fieldArray, String label,  String tableName) throws Exception {
		String sql = generateSQLBySource(source, execParams);
		StringBuilder sb = new StringBuilder("SELECT ");
		
		if (label != null && StringUtils.isNotBlank(label)) {
			sb.append(label).append(" AS label").append(", ");
		}
		
		int i = 0;
		for(FeatureAttrMap featureAttrMap : mappedFeatures) {
			if (fieldArray != null && fieldArray.length > 0) {
				sb.append(featureAttrMap.getAttribute().getAttrName()).append(" AS ").append(featureAttrMap.getFeature().getFeatureName());
				if(i < mappedFeatures.size()-1)
				sb.append(", ");
			}
			i++;
		}
		sb.append(" FROM (")
		.append(sql)
		.append(") "+tableName);
		return sb.toString();
	}

	public String generateFeatureSQLByTempTable(List<FeatureAttrMap> mappedFeatures, String tempTableName, String label,  String aliasName) throws Exception {
		StringBuilder sb = new StringBuilder("SELECT ");
		
		if (label != null && StringUtils.isNotBlank(label)) {
			sb.append(label).append(" AS label").append(", ");
		}
		
		int i = 0;
		for(FeatureAttrMap featureAttrMap : mappedFeatures) {
			sb.append(featureAttrMap.getAttribute().getAttrName()).append(" AS ").append(featureAttrMap.getFeature().getFeatureName());
			if(i < mappedFeatures.size()-1)
			sb.append(", ");
			i++;
		}
		sb.append(" FROM (")
		.append(tempTableName)
		.append(") "+aliasName);
		return sb.toString();
	}
	
	public void createDatastore(String filePath,String fileName, MetaIdentifier metaId, MetaIdentifier execId,List<MetaIdentifierHolder> appInfo, MetaIdentifierHolder createdBy,
			String saveMode, MetaIdentifierHolder resultRef, long count, String persistMode, RunMode runMode) throws Exception{
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, fileName, metaId, execId, appInfo, createdBy, SaveMode.APPEND.toString(), resultRef, count, persistMode, null);
	}
	
	public List<Train> getTrainByModel(String modelUuid, String modelVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<Train> trainList = new ArrayList<>();
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("createdOn");
		query.fields().include("active");
		query.fields().include("appInfo");
		query.fields().include("createdBy");
		
		Application application = commonServiceImpl.getApp();
		
		query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(modelUuid));
		query.addCriteria(Criteria.where("active").is("Y"));
		query.addCriteria(Criteria.where("appInfo.ref.uuid").is(application.getUuid()));
		query.with(new Sort(Sort.Direction.DESC, "version"));
		
		trainList = mongoTemplate.find(query, Train.class);
		return trainList;
	}

	/********************** UNUSED **********************/
//	public String genTableNameByMetaIdentifier(MetaIdentifier tabNameMI, Datapod datapod, String execversion, RunMode runMode) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		String tableName = null;
////		Datasource datasource = commonServiceImpl.getDatasourceByApp();
//		Datasource datasource = commonServiceImpl.getDatasourceByDatapod(datapod);
//		String dsType = datasource.getType();
//		if(runMode.equals(RunMode.BATCH)) {
//			if (/*!engine.getExecEngine().equalsIgnoreCase("livy-spark")
//					&& !dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
//					&&*/ !dsType.equalsIgnoreCase(ExecContext.FILE.toString())) {
//				tableName = datasource.getDbname() + "." + datapod.getName();
//				return tableName;
//			} else {
//				tableName = String.format("%s_%s_%s", tabNameMI.getUuid().replace("-", "_"), tabNameMI.getVersion(), execversion);
//			}
//		} else if(runMode.equals(RunMode.ONLINE)) {
//			tableName = String.format("%s_%s_%s", tabNameMI.getUuid().replace("-", "_"), tabNameMI.getVersion(), execversion);
//		}		
//		return tableName;
//	}

	/********************** UNUSED **********************/
//	public boolean predict2(Predict predict, ExecParams execParams, PredictExec predictExec, RunMode runMode) throws Exception {
//		boolean isSuccess = false;
//		try {
//			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.RUNNING);
//			
//			MetaIdentifierHolder modelHolder = predict.getDependsOn();
//			MetaIdentifierHolder sourceHolder = predict.getSource();
//			MetaIdentifierHolder targetHolder = predict.getTarget();
//
//			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelHolder.getRef().getUuid(),
//					modelHolder.getRef().getVersion(), modelHolder.getRef().getType().toString());
//			Object source = (Object) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
//					sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//			Datapod target = null;
//			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod))
//				target = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(),
//						targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());
//			
//			Algorithm algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(
//					model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(),
//					MetaType.algorithm.toString());
//
//			String modelName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), predictExec.getVersion());
//			String filePath = "/model/predict"+String.format("/%s/%s/%s", model.getUuid().replace("-", "_"), model.getVersion(), predictExec.getVersion());
//			String tableName = null;//String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), predictExec.getVersion());
//
//			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
//
//			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
//			Object result = null;
//			
//			String[] fieldArray = modelExecServiceImpl.getAttributeNames(predict);
//			Datasource datasource = commonServiceImpl.getDatasourceByApp();
//			IExecutor exec = execFactory.getExecutor(datasource.getType());
//
//			String appUuid = commonServiceImpl.getApp().getUuid();
//			
//			String dsType = datasource.getType();
//			if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
//					|| dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
//					|| dsType.equalsIgnoreCase(ExecContext.FILE.toString())) {
//				MetaIdentifier tabNameMI = new MetaIdentifier(MetaType.model, model.getUuid(), model.getVersion());
//				tableName = genTableNameByMetaIdentifier(tabNameMI, null, predictExec.getVersion(), runMode);
//				String sql = generateSQLBySource(source, execParams);
//				exec.executeAndRegister(sql, tableName, appUuid);
//			} else {
//				tableName = getTableNameByMetaObject(source);
//			}
//			
//			long count = 0;
//			if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
//				String predictQuery = predictMLOperator.generateSql(predict, tableName);	
//				String sql = null;
//				if (/*!engine.getExecEngine().equalsIgnoreCase("livy-spark")
//						&& !dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
//						&&*/ !dsType.equalsIgnoreCase(ExecContext.FILE.toString())) {
//					tableName = getTableNameByMetaObject(target);
//					sql = helper.buildInsertQuery(datasource.getType(), tableName, target, predictQuery);
//				} else {
//					sql = predictQuery;
//				}				
////				sql = "INSERT INTO framework.account(interest_rate, account_id) VALUES((SELECT interest_rate AS interestRate FROM framework.account account1), (SELECT sqrt ( account.interest_rate ) * 0.9 AS account_id FROM framework.account account2))";
//				ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName, filePath, target, SaveMode.APPEND.toString(), true, appUuid);
//				result = rsHolder;					
//				count = rsHolder.getCountRows();
//				
//				if(predict.getTarget().getRef().getType().equals(MetaType.datapod)) {	
//					createDatastore(filePath, predict.getName(), 
//							new MetaIdentifier(MetaType.datapod, target.getUuid(), target.getVersion()), 
//							new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()),
//							predictExec.getAppInfo(), predictExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//							Helper.getPersistModeFromRunMode(runMode.toString()), runMode);					
//				} 
//			} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
//				TrainExec trainExec = modelExecServiceImpl.getLatestTrainExecByTrain(predict.getTrainInfo().getRef().getUuid(), predict.getTrainInfo().getRef().getVersion());
//				if (trainExec == null)
//					throw new Exception("Executed model not found.");
//
//				String label = commonServiceImpl.resolveLabel(predict.getLabelInfo());
////				if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
////						&& dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
////						&& dsType.equalsIgnoreCase(ExecContext.FILE.toString())) {
////					exec.assembleDF(fieldArray, tableName, algorithm.getTrainName(), label, appUuid);
////				}
//				Object trainedModel = getTrainedModelByTrainExec(algorithm.getModelClass(), trainExec);
//				ResultSetHolder rsHolder =  exec.predict2(trainedModel, target, filePathUrl, tableName, fieldArray, algorithm.getTrainClass(), label, datasource, appUuid);
//				
//				if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
//						|| dsType.equalsIgnoreCase(ExecContext.spark.toString()) 
//						|| dsType.equalsIgnoreCase(ExecContext.FILE.toString())) {
//					String query = "SELECT * FROM " + rsHolder.getTableName();					
//					rsHolder = exec.executeRegisterAndPersist(query, rsHolder.getTableName(), filePath, target, SaveMode.APPEND.toString(), true, appUuid);
//				}
//				
//				result = rsHolder;
//				count = rsHolder.getCountRows();
//				
//				if(predict.getTarget().getRef().getType().equals(MetaType.datapod)) {					
//					createDatastore(filePath, predict.getName(), 
//							new MetaIdentifier(MetaType.datapod, target.getUuid(), target.getVersion()), 
//							new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()),
//							predictExec.getAppInfo(), predictExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//							Helper.getPersistModeFromRunMode(runMode.toString()), runMode);					
//				} 
//			}
//
//			createDatastore(filePathUrl, modelName,
//					new MetaIdentifier(MetaType.predict, predict.getUuid(), predict.getVersion()),
//					new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()),
//					predictExec.getAppInfo(), predictExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//					Helper.getPersistModeFromRunMode(runMode.toString()), runMode);
//
//			predictExec.setLocation(filePathUrl);
//			predictExec.setResult(resultRef);
//			commonServiceImpl.save(MetaType.predictExec.toString(), predictExec);
//			if (result != null) {
//				isSuccess = true;
//				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.COMPLETED);
//			}else {
//				isSuccess = false;
//				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.FAILED);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			String message = null;
//			try {
//				message = e.getMessage();
//			}catch (Exception e2) {
//				// TODO: handle exception
//			}
//			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.FAILED);
//			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
//			dependsOn.setRef(new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()));
//			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Predict execution FAILED.", dependsOn);
//			throw new RuntimeException((message != null) ? message : "Predict execution FAILED.");
//		}
//		return isSuccess;
//	}

	/********************** UNUSED **********************/
//	public String getTableNameByObject(Object metaObject) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		String tableName = null;
//		Datasource datasource = commonServiceImpl.getDatasourceByApp();
//		if (metaObject instanceof Datapod) {
//			Datapod datapod = (Datapod) metaObject;			
//			return datasource.getDbname()+"."+datapod.getName();
//		} else if (metaObject instanceof DataSet) {
//			DataSet dataset = (DataSet) metaObject;
//			MetaIdentifier dependsOnIdentifier = dataset.getDependsOn().getRef(); 
//			MetaType dependsOnType = dependsOnIdentifier.getType();
//			if(dependsOnType.equals(MetaType.datapod)) {
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOnIdentifier.getUuid(), dependsOnIdentifier.getVersion(), dependsOnType.toString());
//				return getTableNameByObject(datapod); 
//			} else if(dependsOnType.equals(MetaType.dataset)) {
//				DataSet dataSet2 = (DataSet) commonServiceImpl.getOneByUuidAndVersion(dependsOnIdentifier.getUuid(), dependsOnIdentifier.getVersion(), dependsOnType.toString());
//				return getTableNameByObject(dataSet2);
//			} else if(dependsOnType.equals(MetaType.relation)) {
//				Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(dependsOnIdentifier.getUuid(), dependsOnIdentifier.getVersion(), dependsOnType.toString());
//				return getTableNameByObject(relation);
//			}			
//		} else if (metaObject instanceof Rule) {
//			Rule rule = (Rule) metaObject;
//			MetaIdentifier ruleSourceIdentifier = rule.getSource().getRef(); 
//			MetaType dependsOnType = ruleSourceIdentifier.getType();
//			if(dependsOnType.equals(MetaType.datapod)) {
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(ruleSourceIdentifier.getUuid(), ruleSourceIdentifier.getVersion(), dependsOnType.toString());
//				return getTableNameByObject(datapod); 
//			} else if(dependsOnType.equals(MetaType.dataset)) {
//				DataSet dataSet2 = (DataSet) commonServiceImpl.getOneByUuidAndVersion(ruleSourceIdentifier.getUuid(), ruleSourceIdentifier.getVersion(), dependsOnType.toString());
//				return getTableNameByObject(dataSet2);
//			} else if(dependsOnType.equals(MetaType.relation)) {
//				Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(ruleSourceIdentifier.getUuid(), ruleSourceIdentifier.getVersion(), dependsOnType.toString());
//				return getTableNameByObject(relation);
//			} else if(dependsOnType.equals(MetaType.rule)) {
//				Rule rule2 = (Rule) commonServiceImpl.getOneByUuidAndVersion(ruleSourceIdentifier.getUuid(), ruleSourceIdentifier.getVersion(), dependsOnType.toString());
//				return getTableNameByObject(rule2);
//			}
//		} else if (metaObject instanceof Relation) {
//			Relation relation = (Relation) metaObject;
//			MetaIdentifier dependsOnIdentifier = relation.getDependsOn().getRef();
//			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOnIdentifier.getUuid(), dependsOnIdentifier.getVersion(), dependsOnIdentifier.getType().toString());
//			return getTableNameByObject(datapod);
//		}
//		return tableName;
//	}

	public String getTableNameByDatapod(Datapod datapod, Datasource datasource) {
		if(datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
			return datasource.getSid().concat(".").concat(datapod.getName());
		} else {
			return datasource.getDbname().concat(".").concat(datapod.getName());
		}
	}
	
	/********************** UNUSED **********************/
//	public boolean simulate2(Simulate simulate, ExecParams execParams, SimulateExec simulateExec, RunMode runMode) throws Exception {
//		boolean isSuccess = false;
//		execParams = (ExecParams) commonServiceImpl.resolveName(execParams, null);
//		Distribution distribution = (Distribution) commonServiceImpl.getOneByUuidAndVersion(simulate.getDistributionTypeInfo().getRef().getUuid(), simulate.getDistributionTypeInfo().getRef().getVersion(), simulate.getDistributionTypeInfo().getRef().getType().toString());
//		try {
//			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.RUNNING);
//			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(simulate.getDependsOn().getRef().getUuid(),
//					simulate.getDependsOn().getRef().getVersion(), MetaType.model.toString());
//	
//			MetaIdentifierHolder targetHolder = simulate.getTarget();
//			Datapod targetDp = null;
//			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod))
//				targetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(), targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());
//			
//			String modelName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), simulateExec.getVersion());
//			String filePath = "/model/simulate"+String.format("/%s/%s/%s", model.getUuid().replace("-", "_"), model.getVersion(), simulateExec.getVersion());	
//			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
//			
//			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
//			Object result = null;
//			String[] fieldArray = modelExecServiceImpl.getAttributeNames(simulate);
//			
//			Datasource datasource = commonServiceImpl.getDatasourceByApp();
//			IExecutor exec = execFactory.getExecutor(datasource.getType());
//			
//			ExecParams distExecParam = new ExecParams(); 
//			ExecParams simExecParam = new ExecParams(); 
//			
//			List<ParamListHolder> distParamHolderList = new ArrayList<>();
//			List<ParamListHolder> simParamHolderList= new ArrayList<>();
//			
//			String tableName = null;
//			List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
//			for(ParamListHolder holder : paramListInfo) {
//				if(simulate.getParamList() != null && holder.getRef().getUuid().equalsIgnoreCase(simulate.getParamList().getRef().getUuid())) {
//					simParamHolderList.add(holder);
//				} else if(holder.getRef().getUuid().equalsIgnoreCase(distribution.getParamList().getRef().getUuid())) {
//					distParamHolderList.add(holder);
//				}
//				if(holder.getParamName().equalsIgnoreCase("saveLocation")) {
//					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(holder.getParamValue().getRef().getUuid(), holder.getParamValue().getRef().getVersion(), holder.getParamValue().getRef().getType().toString());
//					tableName = datapodServiceImpl.genTableNameByDatapod(datapod, simulateExec.getVersion(), runMode);
//				}
//			}
//			distExecParam.setParamListInfo(distParamHolderList);
//			simExecParam.setParamListInfo(simParamHolderList);
//			
//			/*
//			 * New ParamListHolder for distribution  
//			 */
//			ParamListHolder distributionInfo = new ParamListHolder();
//			distributionInfo.setParamId("0");
//			distributionInfo.setParamName("distribution");
//			distributionInfo.setParamType("distribution");
//			MetaIdentifier distIdentifier = new MetaIdentifier(MetaType.distribution, distribution.getUuid(), distribution.getVersion());
//			MetaIdentifierHolder distHolder = new MetaIdentifierHolder(distIdentifier);
//			distributionInfo.setParamValue(distHolder);
//			distributionInfo.setRef(new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()));
//			
//			/*
//			 * New ParamListHolder for numIterations  
//			 */
//			ParamListHolder numIterationsInfo = new ParamListHolder();
//			numIterationsInfo.setParamId("1");
//			numIterationsInfo.setParamName("numIterations");
//			distributionInfo.setParamType("integer");
//			MetaIdentifierHolder numIterHolder = new MetaIdentifierHolder(null, ""+simulate.getNumIterations());
//			numIterationsInfo.setParamValue(numIterHolder);
//			numIterationsInfo.setRef(new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()));
//			
//			List<ParamListHolder> paramListInfo2 = execParams.getParamListInfo();
//			paramListInfo2.add(distributionInfo);
//			paramListInfo2.add(numIterationsInfo);
//			execParams.setParamListInfo(paramListInfo2);
//			
//			String appUuid = commonServiceImpl.getApp().getUuid();
//			long count = 0;
//			if(simulate.getType().equalsIgnoreCase(SimulationType.MONTECARLO.toString())) {
//				result = monteCarloSimulation.simulateMonteCarlo(simulate, simExecParam, distExecParam, filePathUrl);
//			} else if(simulate.getType().equalsIgnoreCase(SimulationType.DEFAULT.toString())) {
//				if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
//					
//					HashMap<String, String> otherParams = execParams.getOtherParams();
//					if(otherParams == null)
//						otherParams = new HashMap<>();
//					otherParams = (HashMap<String, String>) generateDataOperator.customCreate(simulateExec, execParams, runMode);
//
//					String tabName_2 = null;
//					String tableName_3 = null;
//					if(distribution.getClassName().contains("UniformRealDistribution")) {
//						List<Feature> features = model.getFeatures();
//						for(int i=0; i<fieldArray.length; i++) {
//							List<ParamListHolder> paramListHolderes = distExecParam.getParamListInfo();
//							Feature feature = features.get(i);
//							for(ParamListHolder holder : paramListHolderes) {
//								if(holder.getParamName().equalsIgnoreCase("upper")) {
//									holder.getParamValue().setValue(""+feature.getMaxVal());
//								}
//								if(holder.getParamName().equalsIgnoreCase("lower")) {
//									holder.getParamValue().setValue(""+feature.getMinVal());
//								}
//							}
//							
//							tableName = generateDataOperator.execute(simulateExec, execParams, runMode);
//
//							tabName_2 = exec.renameColumn(tableName, 1, fieldArray[i], appUuid);
//							String sql = simulateMLOperator.generateSql(simulate, tabName_2);
//							result = exec.executeAndRegister(sql, tabName_2, appUuid);//(sql, tabName_2, filePath, null, SaveMode.APPEND.toString(), appUuid);
//
//							if(i == 0)
//								tableName_3 = tabName_2;
//							if(i>0)
//								tableName_3 = exec.joinDf(tableName_3, tabName_2, i, appUuid);
//						}
//						
//						String sql = "SELECT * FROM " + tableName_3;					
//						if(simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
//							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath, targetDp, SaveMode.APPEND.toString(), true, appUuid);	
//							result = rsHolder;						
//							count = rsHolder.getCountRows();
//							createDatastore(filePath, simulate.getName(), 
//									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()), 
//									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(), simulateExec.getVersion()),
//									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//									Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
//						}
//						
//						tableName_3 = exec.assembleRandomDF(fieldArray, tableName_3, false, appUuid);
//					} else {
//						tableName = generateDataOperator.execute(simulateExec, execParams, runMode);
//						
//						String sql = "SELECT * FROM " + tableName;	
//						tableName_3 = tableName;
//						if(simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
//							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath, targetDp, SaveMode.APPEND.toString(), true, appUuid);	
//							result = rsHolder;						
//							count = rsHolder.getCountRows();
//							createDatastore(filePath, simulate.getName(), 
//									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()), 
//									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(), simulateExec.getVersion()),
//									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//									Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
//						}
//						
//						String[] customFldArr = new String[] {fieldArray[0]};
//						tableName_3 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);
//					}
//					
//					String sql = "SELECT * FROM " + tableName_3;
//					ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath, null, SaveMode.APPEND.toString(), true, appUuid);	
//					result = rsHolder;						
//					count = rsHolder.getCountRows();
//				} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
//					
//					HashMap<String, String> otherParams = execParams.getOtherParams();
//					if(otherParams == null)
//						otherParams = new HashMap<>();
//					otherParams = (HashMap<String, String>) generateDataOperator.customCreate(simulateExec, execParams, runMode);
//				
//					String tabName_2 = null;
//					String tableName_3 = null;
//					if(distribution.getClassName().contains("UniformRealDistribution")) {
//						List<Feature> features = model.getFeatures();
//						for(int i=0; i<fieldArray.length; i++) {
//							List<ParamListHolder> paramListHolderes = distExecParam.getParamListInfo();
//							Feature feature = features.get(i);
//							for(ParamListHolder holder : paramListHolderes) {
//								if(holder.getParamName().equalsIgnoreCase("upper")) {
//									holder.getParamValue().setValue(""+feature.getMaxVal());
//								}
//								if(holder.getParamName().equalsIgnoreCase("lower")) {
//									holder.getParamValue().setValue(""+feature.getMinVal());
//								}
//							}
//							
//							tableName = generateDataOperator.execute(simulateExec, execParams, runMode);
//
//							tabName_2 = exec.renameColumn(tableName, 1, fieldArray[i], appUuid);
//							if(i == 0)
//								tableName_3 = tabName_2;
//							if(i>0)
//								tableName_3 = exec.joinDf(tableName_3, tabName_2, i, appUuid);
//						}
//
//						
//						if(simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {							
//							String targetTable = null;
//							MetaIdentifier targetIdentifier =simulate.getTarget().getRef(); 
//							Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetIdentifier.getUuid(), targetIdentifier.getVersion(), targetIdentifier.getType().toString());
//
//							String sql = "SELECT * FROM " + tableName_3;
//							if(datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
//									|| datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
//								targetTable = String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(), simulateExec.getVersion());
//							} else {
//								targetTable = datasource.getDbname() + "." + datapod.getName();
//								sql = helper.buildInsertQuery(datasource.getType(), targetTable, datapod, sql);
//							}
//							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, targetTable, filePath, targetDp, SaveMode.APPEND.toString(), true, appUuid);	
//							result = rsHolder;
//							count = rsHolder.getCountRows();
//							createDatastore(filePath, simulate.getName(), 
//									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()), 
//									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(), simulateExec.getVersion()),
//									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//									Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
//							tableName_3 = targetTable;
//						} 
//						
//						tableName_3 = exec.assembleRandomDF(fieldArray, tableName_3, false, appUuid);
//					} else {						
//						tableName = generateDataOperator.execute(simulateExec, execParams, runMode);
//						
//						String sql = "SELECT * FROM " + tableName;
//						tableName_3 = tableName;
//						if(simulate.getTarget().getRef().getType().equals(MetaType.datapod)) {
//							ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath, targetDp, SaveMode.APPEND.toString(), true, appUuid);	
//							result = rsHolder;
//							count = rsHolder.getCountRows();
//							createDatastore(filePath, simulate.getName(), 
//									new MetaIdentifier(MetaType.datapod, targetDp.getUuid(), targetDp.getVersion()), 
//									new MetaIdentifier(MetaType.predictExec, simulateExec.getUuid(), simulateExec.getVersion()),
//									simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//									Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
//						} 
//						
//						String[] customFldArr = new String[] {fieldArray[0]};						
//						tableName_3 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);
//					}
//					
//					String sql = "SELECT * FROM " + tableName_3;	
//					ResultSetHolder rsHolder = exec.executeRegisterAndPersist(sql, tableName_3, filePath, null, SaveMode.APPEND.toString(), true, appUuid);	
//					result = rsHolder;
//					count = rsHolder.getCountRows();
//				}
//			}
//
//				createDatastore(filePathUrl, modelName,
//						new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()),
//						new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()),
//						simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef, count, 
//						Helper.getPersistModeFromRunMode(runMode.toString()), runMode);	
//		
//
//			simulateExec.setLocation(filePathUrl);
//			simulateExec.setResult(resultRef);
//			commonServiceImpl.save(MetaType.simulateExec.toString(), simulateExec);
//			if (result != null) {
//				isSuccess = true;
//				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.COMPLETED);
//			}else {
//				isSuccess = false;
//				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.FAILED);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			String message = null;
//			try {
//				message = e.getMessage();
//			}catch (Exception e2) {
//				// TODO: handle exception
//			}
//
//			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.FAILED);
//			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
//			dependsOn.setRef(new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()));
//			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Simulate execution FAILED.", dependsOn);
//			throw new RuntimeException((message != null) ? message : "Simulate execution FAILED.");
//		}		
//
//		return isSuccess;
//	}
		
	@SuppressWarnings("unchecked")
	public boolean prepareTrain(String trainUuid, String trainVersion, TrainExec trainExec, ExecParams execParams, RunMode runMode) throws Exception {
		Algorithm algorithm= null;
		try {
			List<ParamMap> paramMapList = new ArrayList<>();
			Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainUuid, trainVersion, MetaType.train.toString());			
			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), MetaType.model.toString());
			
			if(model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
				boolean result = false;
				if(trainExec == null) {
					trainExec = create(train, model, execParams, null, trainExec);
				}
				trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.RUNNING);
				
				TrainResult trainResult = new TrainResult();
				trainResult.setName(train.getName());
				trainResult.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion())));
				trainResult.setParamList(new MetaIdentifierHolder(execParams.getParamListInfo().get(0).getRef()));
				trainResult.setBaseEntity();
				
				List<String> argList = null;
				if (StringUtils.isNotBlank(model.getCustomFlag()) && model.getCustomFlag().equalsIgnoreCase("N")) {
					algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(), MetaType.algorithm.toString());

					trainResult.setAlgorithm(algorithm.getName());
					trainResult.setAlgoType(model.getType());
					
					String scriptName = algorithm.getScriptName();
					// Save the data as csv
					String[] fieldArray = modelExecServiceImpl.getMappedFeatureNames(train);
					String label = commonServiceImpl.resolveLabel(train.getLabelInfo());
					String trainName = String.format("%s_%s_%s", train.getUuid().replace("-", "_"), train.getVersion(), trainExec.getVersion());
					String filePath = String.format("/%s/%s/%s", train.getUuid(), train.getVersion(), trainExec.getVersion());
					String tableName = String.format("%s_%s_%s", train.getUuid().replace("-", "_"), train.getVersion(), trainExec.getVersion());
					Object source = (Object) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
							train.getSource().getRef().getVersion(), train.getSource().getRef().getType().toString());
					String sql = generateFeatureSQLBySource(train.getFeatureAttrMap(), source, execParams, fieldArray, label, tableName);
					String appUuid = commonServiceImpl.getApp().getUuid();

					Map<String, EncodingType> encodingDetails = getEncodingDetails(train.getFeatureAttrMap(), model.getFeatures());
					
					TrainInput trainInput = new TrainInput();
					Map<String, Object> otherParams = new HashMap<>();
					
					trainInput.setEncodingDetails(encodingDetails);					
					trainInput.setTrainPercent(train.getTrainPercent()/100);
					trainInput.setTestPercent(train.getValPercent()/100);
					trainInput.setIncludeFeatures(train.getIncludeFeatures());
					
					String sourceQuery = generateSQLBySource(source, execParams);
					String sourceCustomQuery = null;
					List<String> rowIdentifierCols = null;
					if(train.getRowIdentifier() != null && !train.getRowIdentifier().isEmpty()) {
						rowIdentifierCols = getRowIdentifierCols(train.getRowIdentifier());
						if(train.getIncludeFeatures().equalsIgnoreCase("Y")
								&& rowIdentifierCols != null 
								&& !rowIdentifierCols.isEmpty()) {
							rowIdentifierCols = removeDuplicateColNames(fieldArray, rowIdentifierCols);
						}
						
						if(rowIdentifierCols != null && !rowIdentifierCols.isEmpty()) {				
							StringBuilder builder = new StringBuilder();
							builder.append("SELECT ");
							int i=0;
							for(String colName : rowIdentifierCols) {
								builder.append(colName);
								if(i<rowIdentifierCols.size()-1) {
									builder.append(", ");
								}
								i++;
							}
							
							builder.append(" FROM ");
							builder.append(" (");
							builder.append(sourceQuery);
							builder.append(") ");
							builder.append(" sourceUniqueData");
							sourceCustomQuery = builder.toString();
						} else {
							rowIdentifierCols = null;
						}
						
						
						trainInput.setRowIdentifier(rowIdentifierCols);
						Datapod sourceDp = commonServiceImpl.getDatapodByObject(source);
						Map<String, String> colDetails = getColumnNameAndDatatypeByAttrList(sourceDp.getAttributes());
						otherParams.put("sourceAttrDetails", colDetails);
					}
					
					if(train.getIncludeFeatures() != null && !train.getIncludeFeatures().isEmpty()) {
//						Map<String, String> colDetails = getColumnNameAndDatatypeByFeatureAttrMap(train.getFeatureAttrMap());
						otherParams.put("featureAttrDetails", fieldArray);
					}					
					
					List<ParamListHolder> resolvedParamInfoList = getParamByParamList(execParams.getParamListInfo().get(0).getRef().getUuid());
					// Get paramList
//					String []args = resolvedParamInfoList.stream().map(p -> p.getParamName() + "~" + p.getParamValue().getValue() + "")
//						.collect(Collectors.joining("~")).split("~");
//					argList = new ArrayList<String>(Arrays.asList(args));

					argList = new ArrayList<>();
					for(ParamListHolder paramListHolder : resolvedParamInfoList) {
						otherParams.put(paramListHolder.getParamName(), paramListHolder.getParamValue().getValue());
					}					

					String defaultDir = Helper.getPropertyValue("framework.model.train.path")+filePath+"/";
					
					Map<String, String> trainSetDetails = null;	
					Datapod trainSetDp = null;
					String trainSetSavePath = null;
					if(train.getSaveTrainingSet().equalsIgnoreCase("Y")) {
						trainSetDetails = new HashMap<>();
						trainSetDetails.put("saveTrainingSet", train.getSaveTrainingSet());
						if(train.getTrainLocation().getRef().getType().equals(MetaType.file)) {
							trainSetSavePath = "file://".concat(defaultDir).concat(defaultDir.endsWith("/") ? "train_set" : "/train_set");
							trainSetDetails.put("trainSetSavePath", trainSetSavePath);
							trainSetDetails.put("trainSetDsType", train.getTrainLocation().getRef().getType().toString().toLowerCase());
						} else {
							MetaIdentifier trainSetMI = train.getTrainLocation().getRef();
							trainSetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(trainSetMI.getUuid(), trainSetMI.getVersion(), trainSetMI.getType().toString(), "N");
							
							MetaIdentifier trainSetDpDsMI = trainSetDp.getDatasource().getRef();
							Datasource trainSetDpDs = (Datasource) commonServiceImpl.getOneByUuidAndVersion(trainSetDpDsMI.getUuid(), trainSetDpDsMI.getVersion(), trainSetDpDsMI.getType().toString(), "N");
							
							trainSetDetails.put("trainSetDsType", trainSetDpDs.getType().toLowerCase());
							if(trainSetDpDs.getType().equalsIgnoreCase(MetaType.file.toString())) {								
								String trainPath = String.format("/%s/%s/%s", trainSetDp.getUuid(), trainSetDp.getVersion(), trainExec.getVersion());
								String trainSetDefaultPath = hdfsInfo.getHdfsURL().concat(Helper.getPropertyValue("framework.schema.Path"));
								trainSetDefaultPath = trainSetDefaultPath.endsWith("/") ? trainSetDefaultPath : trainSetDefaultPath.concat("/");
								trainSetSavePath = String.format("%s%s",trainSetDefaultPath, trainPath);
								
								trainSetDetails.put("trainSetSavePath", trainSetSavePath);
							} else {								
								trainSetDetails.put("testSetTableName", getTableNameByDatapod(trainSetDp, trainSetDpDs));
								trainSetDetails.put("trainSetHostName", trainSetDpDs.getHost());
								trainSetDetails.put("trainSetDbName", trainSetDpDs.getDbname());
								trainSetDetails.put("trainSetPort", trainSetDpDs.getPort());
								trainSetDetails.put("trainSetUserName", trainSetDpDs.getUsername());
								trainSetDetails.put("trainSetPassword", trainSetDpDs.getPassword());
								trainSetDetails.put("testSetDriver", trainSetDpDs.getDriver());
								trainSetDetails.put("testSetUrl", Helper.genUrlByDatasource(trainSetDpDs));
							}
						}
					}
					trainInput.setTrainSetDetails(trainSetDetails);

					Map<String, String> testSetDetails = new HashMap<>();	
					Datapod testSetDp = null;
					String testSetSavePath = null;
					if(train.getTestLocation().getRef().getType().equals(MetaType.file)) {
						testSetSavePath = "file://".concat(defaultDir).concat(defaultDir.endsWith("/") ? "test_set" : "/test_set");
						testSetDetails.put("testSetSavePath", testSetSavePath);
						testSetDetails.put("testSetDsType", train.getTestLocation().getRef().getType().toString().toLowerCase());
					} else {
						MetaIdentifier testSetMI = train.getTestLocation().getRef();
						testSetDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(testSetMI.getUuid(), testSetMI.getVersion(), testSetMI.getType().toString(), "N");
						
						MetaIdentifier testSetDpDsMI = testSetDp.getDatasource().getRef();
						Datasource testSetDpDs = (Datasource) commonServiceImpl.getOneByUuidAndVersion(testSetDpDsMI.getUuid(), testSetDpDsMI.getVersion(), testSetDpDsMI.getType().toString(), "N");
						
						testSetDetails.put("testSetDsType", testSetDpDs.getType().toLowerCase());
						if(testSetDpDs.getType().equalsIgnoreCase(MetaType.file.toString())) {
							String testPath = String.format("/%s/%s/%s", testSetDp.getUuid(), testSetDp.getVersion(), trainExec.getVersion());
							String testSetDefaultPath = hdfsInfo.getHdfsURL().concat(Helper.getPropertyValue("framework.schema.Path"));
							testSetDefaultPath = testSetDefaultPath.endsWith("/") ? testSetDefaultPath : testSetDefaultPath.concat("/");
							testSetSavePath = String.format("%s%s",testSetDefaultPath, testPath);
							
							testSetDetails.put("testSetSavePath", testSetSavePath);
						} else {
							testSetDetails.put("testSetTableName", getTableNameByDatapod(testSetDp, testSetDpDs));
							testSetDetails.put("testSetHostName", testSetDpDs.getHost());
							testSetDetails.put("testSetDbName", testSetDpDs.getDbname());
							testSetDetails.put("testSetPort", testSetDpDs.getPort());
							testSetDetails.put("testSetUserName", testSetDpDs.getUsername());
							testSetDetails.put("testSetPassword", testSetDpDs.getPassword()); 
							testSetDetails.put("testSetDriver", testSetDpDs.getDriver());
							testSetDetails.put("testSetUrl", Helper.genUrlByDatasource(testSetDpDs));
						}
					}
					trainInput.setTestSetDetails(testSetDetails);
					
					String modelFileName = defaultDir.concat(defaultDir.endsWith("/") ? "model" : "/model");

					String outputResultPath = defaultDir.concat(defaultDir.endsWith("/") ? "train_op_result" : "/train_op_result").concat("/").concat("train_op_result");
					otherParams.put("outputResultPath", outputResultPath);
					
					deleteFileOrDirIfExists(defaultDir);
					
					File outputResultDir = new File(defaultDir.concat(defaultDir.endsWith("/") ? "train_op_result" : "/train_op_result"));
					if(!outputResultDir.exists()) {
						outputResultDir.mkdirs();
					}
					
					logger.info("Default dir name : " + defaultDir);
					logger.info("Model file name : " + modelFileName);
									
					Datasource sourceDS = commonServiceImpl.getDatasourceByObject(train);
					String sourceDsType = sourceDS.getType().toLowerCase();
					trainInput.setSourceDsType(sourceDsType);
					
					Datasource appDs = commonServiceImpl.getDatasourceByApp();
					IExecutor exec = execFactory.getExecutor(appDs.getType());
					exec.executeAndRegisterByDatasource(sourceQuery, tableName, sourceDS, appUuid);	
					LinkedHashMap<String, Object> imputationDetails = imputeOperator.resolveAttributeImputeValue(train.getFeatureAttrMap(), source, model, execParams, runMode, tableName);					
					LinkedHashMap<String, Object> remappedImputationDetails = remapSourceImpueValToFeature(source, train.getFeatureAttrMap(), model.getFeatures(), imputationDetails);
					trainInput.setImputationDetails(remappedImputationDetails);
					
					List<String> inputColList = new ArrayList<>();
					inputColList.add("label");
					Map<String, String> modelSchema = new HashMap<>();
					for(Feature feature : model.getFeatures()) {
						modelSchema.put(feature.getName(), feature.getType());
						inputColList.add(feature.getName());
					}
					otherParams.put("modelSchema", modelSchema);
					otherParams.put("inputColList", inputColList);
					
					String inputSourceFileName = null;					
					if(sourceDsType.equalsIgnoreCase(ExecContext.FILE.toString())) {
						sourceDsType = MetaType.file.toString().toLowerCase();
						String trainInputPath = Helper.getPropertyValue("framework.model.train.path")+filePath+"/"+"input";
						logger.info("Saved file name : " + trainInputPath);
						
						File trainInPathFile = new File(trainInputPath);
						if(!trainInPathFile.exists()) {	
							String mappedAttrSql = generateFeatureSQLByTempTable(train.getFeatureAttrMap(), tableName, label, tableName);
							ResultSetHolder rsHolder = exec.executeAndRegisterByDatasource(mappedAttrSql, tableName, appDs, appUuid);	
//							rsHolder.getDataFrame().printSchema();
//							rsHolder = sparkExecutor.applyModelSchema(rsHolder, null, model, tableName, true, appUuid);
//							rsHolder.getDataFrame().printSchema();
//							if(encodingDetails == null || (encodingDetails != null && encodingDetails.isEmpty())) {
//								String doubleCastSql = "SELECT * FROM " + tableName;	
//								rsHolder = sparkExecutor.castDFCloumnsToDoubleType(null, doubleCastSql, sourceDS, tableName, true, appUuid);	
//							}
//							exec.saveDataframeAsCSV(tableName, "file://"+trainInputPath, appUuid);
							sparkExecutor.registerAndPersistDataframe(rsHolder, null, SaveMode.APPEND.toString()
									, "file://"+trainInputPath, tableName
									, "true", true);
							trainInputPath = renameFileAndGetFilePathFromDir(trainInputPath, "input_data", FileType.PARQUET.toString().toLowerCase());
						}		
						
						if(train.getRowIdentifier() != null 
								&& !train.getRowIdentifier().isEmpty()
								&& rowIdentifierCols != null 
								&& !rowIdentifierCols.isEmpty()) {
							inputSourceFileName = Helper.getPropertyValue("framework.model.train.path")+filePath+"/"+"input_source";
							logger.info("Saved source file name : " + inputSourceFileName);
							
							File inputSourceFile = new File(inputSourceFileName);
							if(!inputSourceFile.exists()) {
								ResultSetHolder rsHolder = exec.executeAndRegisterByDatasource(sourceCustomQuery, tableName.concat("_sourceQuery"), sourceDS, appUuid);
//								exec.saveDataframeAsCSV(tableName.concat("_sourceQuery"), "file://"+inputSourceFileName, appUuid);
								sparkExecutor.registerAndPersistDataframe(rsHolder, null, SaveMode.APPEND.toString()
										, "file://"+inputSourceFileName, tableName.concat("_sourceQuery")
										, "true", true);
								String inputSourceFilePath = renameFileAndGetFilePathFromDir(inputSourceFileName, "input_source_data", FileType.PARQUET.toString().toLowerCase());
								otherParams.put("inputSourceFileName", "file://"+inputSourceFilePath);
							}		
						}

						trainInput.setSourceFilePath("file://"+trainInputPath);
					} else {
						trainInput.setQuery(sql);
						
						java.util.Map<String, Object> sourceDsDetails = new HashMap<>();						
						sourceDsDetails.put("sourceHostName", sourceDS.getHost());
						sourceDsDetails.put("sourceDbName", sourceDS.getDbname());
						if(sourceDsType.equalsIgnoreCase(ExecContext.ORACLE.toString())) {
							sourceDsDetails.put("sourceDbName", sourceDS.getSid());						
						} else {
							sourceDsDetails.put("sourceDbName", sourceDS.getDbname());
						}
						sourceDsDetails.put("sourcePort", sourceDS.getPort());
						sourceDsDetails.put("sourceUserName", sourceDS.getUsername());
						sourceDsDetails.put("sourcePassword", sourceDS.getPassword());
						
						trainInput.setSourceDsDetails(sourceDsDetails);
						
						if(train.getRowIdentifier() != null && !train.getRowIdentifier().isEmpty()) {
							otherParams.put("sourceQuery", sourceCustomQuery);
						}
						
						//creating default directory to allow script/python-code to store file in this location
						File defaultDirFile = new File(defaultDir);
						if(!defaultDirFile.exists()) {
							defaultDirFile.mkdirs();
						}
					}
					
					trainInput.setNumInput(fieldArray.length);
					trainInput.setModelFilePath(modelFileName);
					trainInput.setOperation(MetaType.train.toString().toLowerCase());
					
					trainInput.setOtherParams(otherParams);
		
					String inputConfigFilePath = defaultDir.concat(defaultDir.endsWith("/") ? "input_config.json" : "/input_config.json");
					ObjectMapper mapper = new ObjectMapper();
					mapper.writeValue(new File(defaultDir.concat(defaultDir.endsWith("/") ? "input_config.json" : "/input_config.json")), trainInput);
					
					argList.add("inputConfigFilePath");
					argList.add(inputConfigFilePath);
					
					trainResult.setNumFeatures(fieldArray.length);
					
					logger.info("Object TrainInput: "+trainInput.toString());
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
					trainResult.setStartTime(simpleDateFormat.parse((new Date()).toString()));
					StopWatch stopWatch = new StopWatch();
					stopWatch.start();
					List<String> scriptPrintedMsgs = executeScript(model.getType(), scriptName, trainExec.getUuid(), trainExec.getVersion(), argList);
					stopWatch.stop();
					trainResult.setTimeTaken(stopWatch.getTotalTimeMillis()+" ms");
					trainResult.setEndTime(simpleDateFormat.parse((new Date()).toString()));					
					
					if(inputSourceFileName != null && !inputSourceFileName.isEmpty()) {
						deleteFileOrDirIfExists(inputSourceFileName);
					}

//					Map<String, Object> opResult = new ObjectMapper().readValue(new File(outputResultPath.concat(".json")), Map.class);
					
					String opResult = new ObjectMapper().readValue(new File(outputResultPath.concat(".json")), String.class);
					
					Map<String, Object> summary = new ObjectMapper().readValue(opResult.replaceAll("\'", "\""), Map.class);			        
					
					if(!scriptPrintedMsgs.isEmpty()) {
						result = true;
					} else {
						result = false;
					}
					
					if(result) {
//						Map<String, Object> summary = summary(modelFileName, scriptPrintedMsgs);
						Object accuracy = summary.get("accuracy");
						if(accuracy != null) {
							trainResult.setAccuracy(Double.parseDouble(accuracy.toString()));
						}
						
						Object f1Score = summary.get("f1");
						if(f1Score != null) {
							trainResult.setF1Score(Double.parseDouble(f1Score.toString()));
						}
												
						Object precision = summary.get("precision");
						if(precision != null) {
							trainResult.setPrecision(Double.parseDouble(precision.toString()));
						}
												
						Object roc_auc = summary.get("roc_auc");
						if(roc_auc != null) {
							List<Double> rocAuc = new ArrayList<>();
							rocAuc.add(Double.parseDouble(roc_auc.toString()));
							trainResult.setRocAUC(rocAuc);
						}
												
						Object recall = summary.get("recall");
						if(recall != null) {
							trainResult.setRecall(Double.parseDouble(recall.toString()));
						}
						
						Object total_size = summary.get("total_size");
						if(total_size != null) {
							trainResult.setTotalRecords(Long.parseLong(total_size.toString()));
						}
						
						Object train_size = summary.get("train_size");
						if(train_size != null) {
							trainResult.setTrainingSet(Long.parseLong(train_size.toString()));
						}
						
						Object test_size = summary.get("test_size");
						if(test_size != null) {
							trainResult.setValidationSet(Long.parseLong(test_size.toString()));
						}
						
						Object confusion_matrix = summary.get("confusion_matrix");
						if(confusion_matrix != null) {
							trainResult.setConfusionMatrix(confusion_matrix);
						}
						
						Object featureImportance = summary.get("featureImportance");
						if(featureImportance != null) {
							trainResult.setFeatureImportance((List<Double>) featureImportance);
						}
						
						String fileName = "model.result";
						writeSummaryToFile(summary, defaultDir, fileName);
					}
					
					commonServiceImpl.save(MetaType.trainresult.toString(), trainResult);
					
					//dropping temp table
					List<String> tempTableList = new ArrayList<>();
					tempTableList.add(tableName);
					sparkExecutor.dropTempTable(tempTableList);
					
					dataStoreServiceImpl.setRunMode(RunMode.BATCH);
					
					if(trainSetDp != null) {
						dataStoreServiceImpl.create(trainSetSavePath, trainSetDp.getName(),
								new MetaIdentifier(MetaType.datapod, trainSetDp.getUuid(), trainSetDp.getVersion()),
								new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
								trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef);
					}
					
					if(testSetDp != null) {
						dataStoreServiceImpl.create(testSetSavePath, testSetDp.getName(),
								new MetaIdentifier(MetaType.datapod, testSetDp.getUuid(), testSetDp.getVersion()),
								new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
								trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef);
					}
					
					dataStoreServiceImpl.create(modelFileName, trainName,
							new MetaIdentifier(MetaType.train, train.getUuid(), train.getVersion()),
							new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()),
							trainExec.getAppInfo(), trainExec.getCreatedBy(), SaveMode.APPEND.toString(), resultRef);
					trainExec.setResult(resultRef);
					trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, result ? Status.Stage.COMPLETED : Status.Stage.FAILED);
					return result;
				}
				List<String> scriptPrintedMsgs =  executeScript(model.getType(), model.getScriptName(), trainExec.getUuid(), trainExec.getVersion(), argList);
				if(!scriptPrintedMsgs.isEmpty()) {
					result = true;
				} else {
					result = false;
				}
				trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, result ? Status.Stage.COMPLETED : Status.Stage.FAILED);
				return result;
			} else {				
				if (model.getDependsOn().getRef().getVersion() != null) {
					algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(), MetaType.algorithm.toString());
				} else {
					algorithm = (Algorithm) commonServiceImpl.getLatestByUuid(model.getDependsOn().getRef().getUuid(), MetaType.algorithm.toString());
				}
				
				String algoClassName = algorithm.getTrainClass();
				Object algoClass = Class.forName(algoClassName).newInstance();
				
				if (train.getUseHyperParams().equalsIgnoreCase("N") 
						&& !model.getType().equalsIgnoreCase(ExecContext.R.toString())
						&& !model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					paramMapList = metadataServiceImpl.getParamMap(execParams, train.getUuid(), train.getVersion(), algoClass);
				}
				if (paramMapList.size() > 0) {
					for (ParamMap paramMap : paramMapList) {
						if(trainExec == null)
							trainExec = create(train, model, execParams, paramMap, trainExec);
						Thread.sleep(1000); // Should be parameterized in a class
						train(train, model, trainExec, execParams, paramMap, runMode,algoClass);
						trainExec = null;
					}
				} else {
					if(trainExec == null)
						trainExec = create(train, model, execParams, null, trainExec);
					train(train, model, trainExec, execParams, null, runMode,algoClass);
				}
			}			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.FAILED);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder(new MetaIdentifier(MetaType.trainExec, trainExec.getUuid(), trainExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Train execution FAILED.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Train execution FAILED.");			 		
		}
	}
	
	public LinkedHashMap<String, Object> remapSourceImpueValToFeature(Object source,
			List<FeatureAttrMap> featureAttrMapList, List<Feature> features,
			LinkedHashMap<String, Object> imputationDetails) throws NumberFormatException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		LinkedHashMap<String, Object> remappedImputationDetails = new LinkedHashMap<>();
		for(Feature feature : features) {
			for(FeatureAttrMap featureAttrMap : featureAttrMapList) {
				if(featureAttrMap.getFeature().getFeatureId().equalsIgnoreCase(feature.getFeatureId())) {
					String mappedAttr = getAttributeNameByObject(source, Integer.parseInt(featureAttrMap.getAttribute().getAttrId()));
					if(imputationDetails.get(mappedAttr) !=null)
					remappedImputationDetails.put(feature.getName(), imputationDetails.get(mappedAttr));								
					break;
				}
			}
		}
		if(!remappedImputationDetails.isEmpty()) {
			return remappedImputationDetails;
		}else {
			return null;
		}
		//return remappedImputationDetails;
	}

	public List<String> removeDuplicateColNames(String[] fieldArray, List<String> rowIdentifierCols ) {
		if(rowIdentifierCols != null && !rowIdentifierCols.isEmpty()) {
			List<String> colNameList = Arrays.asList(fieldArray);
			List<String> uniqueRowIdentifierCols = new ArrayList<>();

			System.out.println("duplicate column(s): ");
			for(String colName : rowIdentifierCols) {
				if(!colNameList.contains(colName)) {
					uniqueRowIdentifierCols.add(colName);
				} 
				
				if(colNameList.contains(colName)) {
					System.out.println(colName);
				}
			}
			return uniqueRowIdentifierCols;
		}
		return null;
	}
	
	public Map<String, String> getColumnNameAndDatatypeByAttrList(List<Attribute> attributeList) {
		Map<String, String> colDetails = new LinkedHashMap<>();
		
		for(Attribute attribute : attributeList) {
			colDetails.put(attribute.getName(), attribute.getType());
		}
		
		return colDetails;
	}

	/********************** UNUSED **********************/
//	public Map<String, String> getColumnNameAndDatatypeByFeatureAttrMap(List<FeatureAttrMap> featureAttrMap) throws JsonProcessingException {
//		Map<String, String> colDetails = new LinkedHashMap<>();		
//		
//		Datapod attrDp = null;		
//		String attrSourceUuid = null;
//		int i=0;
//		for(FeatureAttrMap mappedAttr : featureAttrMap) {
//			System.out.println("i : atrname >>>> "+i+" : "+mappedAttr.getAttribute().getAttrName());
//			if("all_wire_transfers_round_amount_usd".equalsIgnoreCase(mappedAttr.getAttribute().getAttrName())) {
//				System.out.println();
//			}
//			if(attrSourceUuid == null || (attrSourceUuid != null && !attrSourceUuid.equalsIgnoreCase(mappedAttr.getAttribute().getRef().getUuid()))) {
//				attrSourceUuid = mappedAttr.getAttribute().getRef().getUuid();
//				Object object = commonServiceImpl.getLatestByUuid(mappedAttr.getAttribute().getRef().getUuid(), mappedAttr.getAttribute().getRef().getType().toString(), "N");
//				attrDp = commonServiceImpl.getDatapodByObject(object);
//			} 
//			System.out.println(mappedAttr.getFeature().getFeatureName());
//			System.out.println(mappedAttr.getAttribute().getAttrId());
//			System.out.println(attrDp.getAttribute(Integer.parseInt(mappedAttr.getAttribute().getAttrId())).getType());
//			System.out.println();
//			colDetails.put(mappedAttr.getFeature().getFeatureName(), attrDp.getAttribute(Integer.parseInt(mappedAttr.getAttribute().getAttrId())).getType());
//			i++;
//		}
//		
//		return colDetails;
//	}

	/********************** UNUSED **********************/
//	public Map<String, Object> summary(Object trndModel, List<String> scriptPrintedMsgs) throws IOException {
//		Map<String, Object> summary = new HashMap<>();
//		String modelPath = (String) trndModel;
//		modelPath = modelPath  + ".spec";
//		summary = new ObjectMapper().readValue(new File(modelPath), HashMap.class);
//		boolean isReadingScore = false;
//		boolean isReadingConfuMat = false;
//		boolean constructConfusionMat = false;
//		List<String> confusionMatrx = new ArrayList<>();
//		for(int line=0; line < scriptPrintedMsgs.size(); line++) {
//			if(scriptPrintedMsgs.get(line).equalsIgnoreCase("   ")) {
//				if(!scriptPrintedMsgs.get(line+1).toLowerCase().contains("confusion_matrix")) {
//					summary.put(scriptPrintedMsgs.get(line+1), scriptPrintedMsgs.get(line+2));
//				}
//			}
//			//reading confusion mattrix
//			if(scriptPrintedMsgs.get(line).contains("confusion_matrix")) {
//				isReadingConfuMat = true;
//			}
//			if(isReadingConfuMat) {
//				if(scriptPrintedMsgs.get(line).equalsIgnoreCase("  ")) {
//					isReadingConfuMat = false;
//					constructConfusionMat = true;
//				} else {
//					if(!scriptPrintedMsgs.get(line).contains("confusion_matrix")) {
//						confusionMatrx.add(scriptPrintedMsgs.get(line));
//					}
//				}
//			}			
//			
//			//cunstructing confusion matrix
//			if(constructConfusionMat) {
//				List<List<Double>> realConfusionMat = new ArrayList<>();
//				for(String matrixRow : confusionMatrx) {
//					if(matrixRow.startsWith("[[ ")) {
//						String[] rowSplit = matrixRow.trim().substring(3, matrixRow.length()-1).trim().split(" ");
//						List<Double> rowList = new ArrayList<>();
//						for(String col : rowSplit) {
//							rowList.add(Double.parseDouble(col));
//						}
//						realConfusionMat.add(rowList);
//					} else if(matrixRow.startsWith("[[")) {
//						String[] rowSplit = matrixRow.trim().substring(2, matrixRow.length()-1).trim().split(" ");
//						List<Double> rowList = new ArrayList<>();
//						for(String col : rowSplit) {
//							rowList.add(Double.parseDouble(col));
//						}
//						realConfusionMat.add(rowList);
//					} else if(matrixRow.startsWith("[") && matrixRow.endsWith("]")) {
//						String[] rowSplit = matrixRow.trim().substring(1, matrixRow.length()-1).trim().split(" ");
//						List<Double> rowList = new ArrayList<>();
//						for(String col : rowSplit) {
//							rowList.add(Double.parseDouble(col));
//						}
//						realConfusionMat.add(rowList);
//					} else if(matrixRow.endsWith("]]")) {
//						String[] rowSplit = matrixRow.trim().substring(1, matrixRow.length()-3).trim().split(" ");
//						List<Double> rowList = new ArrayList<>();
//						for(String col : rowSplit) {
//							rowList.add(Double.parseDouble(col));
//						}
//						realConfusionMat.add(rowList);
//					}
//				}
//				summary.put("confusion_matrix", realConfusionMat);
//				constructConfusionMat = false;
//			}
//			
//			//reading confusion score
//			if(scriptPrintedMsgs.get(line).contains("model_score")) {
//				isReadingScore = true;
//				summary.put("model score", scriptPrintedMsgs.get(line+1));
//			}
//			if(isReadingScore) {
//				isReadingScore = false;
//			}
//		}
//		return summary;
//	}
	
	public boolean deleteFileOrDirIfExists(String path) {
		try {
			File file = new File(path);
//			if(file.exists()) {
//				File[] roots = file.listFiles();
//				for(File dirElement : roots) {
//					if(dirElement.getAbsolutePath().contains("input") && dirElement.isDirectory()) {
//						continue;
//					} else {
//						FileUtils.forceDelete(dirElement);
//					}
//				}				
//			}
			if(file.exists()) {
				FileUtils.forceDelete(file);
			}
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public String writeSummaryToFile(Map<String, Object> summary, String directory, String fileName) throws IOException {		
		String filePath = directory.concat(directory.endsWith("/") ? fileName : "/".concat(fileName));		
		String resultJson = new ObjectMapper().writeValueAsString(summary);
//		File file = new File(filePath);
//		if(file.exists()) {
//			FileUtils.forceDelete(file);
//		}
//		deleteFileOrDirIfExists(filePath);
		PrintWriter printWriter = new PrintWriter(filePath);
		printWriter.write(resultJson);
		printWriter.close();
		return null;
	}
	
	public String renameFileAndGetFilePathFromDir(String directory, String fileName, String fileExt) throws IOException {
		File folder = new File(directory);
		for (File file : folder.listFiles()) {
			String dirFileName = file.getName();
			if(fileExt != null) {
				fileExt = fileExt.startsWith(".") ? fileExt : ".".concat(fileExt);
			} 
			
			if (file.isFile() && dirFileName.toLowerCase().endsWith(fileExt)) {
				String pathName = directory.endsWith("/") ? directory.concat(fileName).concat(fileExt) : directory.concat("/").concat(fileName).concat(fileExt);
				FileUtils.moveFile(file, new File(pathName));
				return pathName;
			}
		}
		return null;
	}
	
	public List<ParamListHolder> getParamByParamList(String paramListUuid) throws JsonProcessingException {	
		List<ParamListHolder> holderList = new ArrayList<>();
			
		ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(paramListUuid, MetaType.paramlist.toString());			
		
		for(Param param : paramList.getParams()) {
			ParamListHolder paramListHolder = new ParamListHolder();
			paramListHolder.setParamId(param.getParamId());
			paramListHolder.setParamName(param.getParamName());
			paramListHolder.setParamType(param.getParamType());
			paramListHolder.setParamValue(param.getParamValue());	
			
			paramListHolder.setRef(new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
			paramListHolder.getRef().setName(paramList.getName());
			holderList.add(paramListHolder);
		}
		return holderList;
	}	
	
	
	/**
	 * 
	 * @param source
	 * @param execParams
	 * @param fieldArray
	 * @param label
	 * @return
	 * @throws Exception
	 */
	/*public String generateFeatureSQLBySource(Train train, Object source, ExecParams execParams, String []fieldArray, String label) throws Exception {
		String sql = generateSQLBySource(source, execParams);
		StringBuilder sb = new StringBuilder("SELECT ");
		
		if (StringUtils.isNotBlank(label)) {
			sb.append(label).append(" AS label").append(", ");
		}
		
		for(FeatureAttrMap featureAttrMap : train.getFeatureAttrMap()) {
			if (fieldArray != null && fieldArray.length > 0) {
				sb.append(featureAttrMap.getAttribute().getAttrName()).append(" AS ").append(featureAttrMap.getFeature().getFeatureName()).append(", ");
			}
		}
		
		if (fieldArray != null && fieldArray.length > 0) {
			for (String field : fieldArray) {
				sb.append(field).append(", ");
			}
		}
		sb.append("'' AS result")
			.append(" FROM (")
			.append(sql)
			.append(") t");
		return sb.toString();
	}*/
	public List<Predict> getPredictByModel(String modelUuid, String modelVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<Predict> predictList = new ArrayList<>();
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("createdOn");
		query.fields().include("active");
		query.fields().include("appInfo");
		query.fields().include("createdBy");
		
		Application application = commonServiceImpl.getApp();
		
		if(modelVersion != null)
			query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(modelUuid));
		else
			query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(modelUuid));
		query.addCriteria(Criteria.where("active").is("Y"));
		query.addCriteria(Criteria.where("appInfo.ref.uuid").is(application.getUuid()));
		query.with(new Sort(Sort.Direction.DESC, "version"));
		
		predictList = mongoTemplate.find(query, Predict.class);
		return predictList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public List<Map<String, Object>> getPrediction(String trainExecUuid, Object feature) throws Exception {

		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUuid, null,
				MetaType.trainExec.toString());

		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainExec.getDependsOn().getRef().getUuid(),
				trainExec.getDependsOn().getRef().getVersion(), MetaType.train.toString());

		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(),
				train.getDependsOn().getRef().getVersion(), MetaType.model.toString());
		
		Algorithm algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(
				model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(),
				MetaType.algorithm.toString());
//		List<Predict> predictList = (List<Predict>) getPredictByModel(model.getUuid(), model.getVersion());

		
//		Predict predict = (Predict) commonServiceImpl.getOneByUuidAndVersion(predictList.get(0).getUuid(),
//				predictList.get(0).getVersion(), MetaType.predict.toString());
		
		List<StructField> fields = new ArrayList<StructField>();
		String tableName = String.format("%s", trainExecUuid.replace("-", "_"));
		String appUuid = commonServiceImpl.getApp().getUuid();
		Datasource appDS = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(appDS.getType());

		Map<String, Object> feature1 = (Map<String, Object>) feature;
		Map<String, List<Map<String, Object>>> list = null;
		List<Map<String, Object>> list2 = null;
		Iterator it = feature1.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			list = (Map<String, List<Map<String, Object>>>) pair.getValue();
		}

		Iterator it1 = list.entrySet().iterator();
		while (it1.hasNext()) {
			Map.Entry pair = (Map.Entry) it1.next();
			list2 = (List<Map<String, Object>>) pair.getValue();
		}

		List<Row> data = new ArrayList<>();
		List<Double> value = new ArrayList<>();
		for (Map<String, Object> map : list2) {
			Set<String> keySet = map.keySet();
			Iterator<String> keyItr = keySet.iterator();
			while(keyItr.hasNext()) {
				String key = keyItr.next();
				value.add(Double.parseDouble(map.get(key).toString()));
//				if (map.get(key) instanceof java.lang.Integer) {
//					value.add(Double.parseDouble(map.get(key).toString()));
//				} else {
//					value.add((Double) map.get(key));
//				}
				fields.add(DataTypes.createStructField(key, DataTypes.DoubleType, true));
			}			
		}
//
//		for (Map<String, Object> map : list2) {
//			
//			fields.add(DataTypes.createStructField(map.get("prediction").toString(), DataTypes.DoubleType, true));
//		}
				
		data.add(RowFactory.create(value.toArray(new Double[value.size()])));

		data.forEach(t -> System.out.println(t.get(0)));

		ResultSetHolder rsHolder = sparkExecutor.createAndRegisterDataset(data, DataTypes.createStructType(fields),
				(tableName + "_pred_data"));
		
		String key = String.format("%s_%s", model.getUuid().replaceAll("-", "_"), model.getVersion());
		Object trainedModel = null;
		if(trainedModelMap.get(key) != null) {
			trainedModel = trainedModelMap.get(key);
		} else {
			trainedModel = getTrainedModelByTrainExec(algorithm.getModelClass(), trainExec);
			trainedModelMap.put(key, trainedModel);
		}
//		String[] fieldArray = getMappedAttrs(train.getFeatureAttrMap()); 
				//modelExecServiceImpl.getAttributeNames(predict);


//		exec.assembleDF(fieldArray, (tableName+"_pred_data"), algorithm.getTrainClass(), null, appUuid);
		
	
		rsHolder = exec.predict(trainedModel, null, null, (tableName + "_pred_data"), appUuid, null);
		String query = "SELECT * FROM " + rsHolder.getTableName();

		return exec.executeAndFetchByDatasource(query, appDS, query);
	}

	public List<Map<String, Object>> getTrainOrTestSet(String trainExecUuid, String trainExecVersion, String setType) throws Exception {
		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUuid, trainExecVersion, MetaType.trainExec.toString());
		
			MetaIdentifier dependsOnMI = trainExec.getDependsOn().getRef();
			Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid(), dependsOnMI.getVersion(), dependsOnMI.getType().toString());
			if(train.getSaveTrainingSet().equalsIgnoreCase("N") && setType.equalsIgnoreCase("trainSet")) {
				return new ArrayList<>();
			}
		
		
		MetaIdentifier datastoreMI = trainExec.getResult().getRef();
		DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreMI.getUuid(), datastoreMI.getVersion(), datastoreMI.getType().toString());
		String modelLocation = dataStore.getLocation();
		String defaultTrainPath = modelLocation.substring(0, modelLocation.lastIndexOf("/model"));
		defaultTrainPath = defaultTrainPath.startsWith("file") ? defaultTrainPath : "file://".concat(defaultTrainPath);
		
		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(appDatasource.getType());
		
		String trainOrTestSetPath = null;		
		if(setType.equalsIgnoreCase("trainSet")) {
			trainOrTestSetPath = defaultTrainPath.endsWith("/") ? defaultTrainPath.concat("train_set") : defaultTrainPath.concat("/").concat("train_set");
			 if(train.getTrainLocation() !=null && train.getTrainLocation().getRef().getType().equals(MetaType.datapod)) {
		    	Datapod datapod = (Datapod)commonServiceImpl.getOneByUuidAndVersion(train.getTrainLocation().getRef().getUuid()
		    			, train.getTrainLocation().getRef().getVersion()
		    			, train.getTrainLocation().getRef().getType().toString(), "N");
	    		DataStore ds = dataStoreServiceImpl.findDataStoreByMeta(datapod.getUuid(), datapod.getVersion());
		    	return dataStoreServiceImpl.getDatapodResults(ds.getUuid(), ds.getVersion(),null , 0 , 100 , null, 0, null, null, null, RunMode.BATCH);    	
		    }
		 } else if(setType.equalsIgnoreCase("testSet")) {
			trainOrTestSetPath = defaultTrainPath.endsWith("/") ? defaultTrainPath.concat("test_set") : defaultTrainPath.concat("/").concat("test_set");
		    if(train.getTestLocation() !=null && train.getTestLocation().getRef().getType().equals(MetaType.datapod)) {
		    	Datapod datapod = (Datapod)commonServiceImpl.getOneByUuidAndVersion(train.getTestLocation().getRef().getUuid()
		    			, train.getTestLocation().getRef().getVersion()
		    			, train.getTestLocation().getRef().getType().toString(), "N");
	    		DataStore ds = dataStoreServiceImpl.findDataStoreByMeta(datapod.getUuid(), datapod.getVersion());
		    	return dataStoreServiceImpl.getDatapodResults(ds.getUuid(), ds.getVersion(),null , 0 , 100 , null, 0, null, null, null, RunMode.BATCH);
		    }
		}
		return exec.fetchTrainOrTestSet(trainOrTestSetPath);		
	}

	public List<TrainExec> getTrainExecByModel(String modelUuid
			, String modelVersion
			, String active
			, String startDate
			, String endDate
			, String status) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException, IOException {
		List<TrainExec> trainExecList = new ArrayList<>();
		List<Train> trainList = getTrainByModel(modelUuid, null);
		
		if(trainList != null && !trainList.isEmpty()) {
			String appUuid = commonServiceImpl.getApp().getUuid();
			for(Train train : trainList) {
				MatchOperation dependsOnFilter = match(new Criteria("dependsOn.ref.uuid").is(train.getUuid()));
				MatchOperation appFilter = match(new Criteria("appInfo.ref.uuid").is(appUuid));
				
				MatchOperation activeFilter = null;
				if(active != null && !active.isEmpty()) {
					activeFilter = match(new Criteria("active").is(active));
				} else {
					activeFilter = match(new Criteria("active").in("Y", "N"));
				}
				
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy z");
				MatchOperation dataRangeFilter = null;
				if(startDate != null && !startDate.isEmpty() 
						&& endDate != null && !endDate.isEmpty()) {
					dataRangeFilter = match(new Criteria("createdOn").lte(simpleDateFormat.parse(endDate)).gte(simpleDateFormat.parse(startDate)));
				} else if(startDate != null && !startDate.isEmpty()) {
					dataRangeFilter = match(new Criteria("createdOn").gte(simpleDateFormat.parse(startDate)));
				} else if(endDate != null && !endDate.isEmpty()) {
					dataRangeFilter = match(new Criteria("createdOn").lte(simpleDateFormat.parse(endDate)));
				}
				
				MatchOperation statusFilter = match(new Criteria("statusList.stage").is(Status.Stage.COMPLETED.toString()));
				
				GroupOperation groupBy = group("uuid").max("version").as("version");
				
				Aggregation aggregation = null;
				if(dataRangeFilter != null) {
					aggregation = newAggregation(dependsOnFilter, statusFilter, activeFilter, appFilter, dataRangeFilter, groupBy);
				} else {
					aggregation = newAggregation(dependsOnFilter, statusFilter, activeFilter, appFilter, groupBy);
				}
				
				AggregationResults<TrainExec> aggregationResults = mongoTemplate.aggregate(aggregation, MetaType.trainExec.toString().toLowerCase(), TrainExec.class);
				List<TrainExec> tempTrainExecList = aggregationResults.getMappedResults();
				
				if(tempTrainExecList != null && !tempTrainExecList.isEmpty()) {
					for(TrainExec trainExec : tempTrainExecList) {
						trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExec.getId()
								, trainExec.getVersion()
								, MetaType.trainExec.toString()
								, "N");
						trainExecList.add(trainExec);
					}
				}
			}
		} 
		return trainExecList;
	}
	
	public List<DeployExec> getDeployExecByModel(String modelUuid
			, String modelVersion
			, String active
			, String startDate
			, String endDate
			, String status) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException, IOException {
		List<DeployExec> deployExecList = new ArrayList<>();
		List<TrainExec> deployExecDependsOn = getTrainExecByModel(modelUuid, modelVersion, active, startDate, endDate, status);
		
		if(deployExecDependsOn != null && !deployExecDependsOn.isEmpty()) {
			String appUuid = commonServiceImpl.getApp().getUuid();
			
			for(TrainExec trainExec : deployExecDependsOn) {
				MatchOperation dependsOnFilter = match(new Criteria("dependsOn.ref.uuid").is(trainExec.getUuid()));
				MatchOperation appFilter = match(new Criteria("appInfo.ref.uuid").is(appUuid));
				
				MatchOperation activeFilter = null;
				if(active != null && !active.isEmpty()) {
					activeFilter = match(new Criteria("active").is(active));
				} else {
					activeFilter = match(new Criteria("active").in("Y", "N"));
				}
				
				SimpleDateFormat simpleDateFormat = null;
				MatchOperation dataRangeFilter = null;
				if(startDate != null && !startDate.isEmpty() 
						&& endDate != null && !endDate.isEmpty()) {
					simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy z");
					dataRangeFilter = match(new Criteria("createdOn").lte(simpleDateFormat.parse(endDate)).gte(simpleDateFormat.parse(startDate)));
				} else if(startDate != null && !startDate.isEmpty()) {
					simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy z");
					dataRangeFilter = match(new Criteria("createdOn").gte(simpleDateFormat.parse(startDate)));
				} else if(endDate != null && !endDate.isEmpty()) {
					simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy z");
					dataRangeFilter = match(new Criteria("createdOn").lte(simpleDateFormat.parse(endDate)));
				}
				
//				MatchOperation statusFilter = match(new Criteria("statusList.stage").is(Status.Stage.COMPLETED.toString()));
				MatchOperation statusFilter = null;
				if(status != null) {
					statusFilter = match(new Criteria("statusList.stage").is(status));
				} else {
					statusFilter = match(new Criteria("statusList.stage").in(Status.Stage.COMPLETED.toString(),
							Status.Stage.FAILED.toString(),
							Status.Stage.RUNNING.toString(),
							Status.Stage.PENDING.toString(),
							Status.Stage.KILLED.toString()));
				}
				
				GroupOperation groupBy = group("uuid").max("version").as("version");
				
				Aggregation aggregation = null;
				if(dataRangeFilter != null) {
					aggregation = newAggregation(dependsOnFilter, statusFilter, activeFilter, appFilter, dataRangeFilter, groupBy);
				} else {
					aggregation = newAggregation(dependsOnFilter, statusFilter, activeFilter, appFilter, groupBy);
				}
				
				
				AggregationResults<DeployExec> aggregationResults = mongoTemplate.aggregate(aggregation, MetaType.deployExec.toString().toLowerCase(), DeployExec.class);
				List<DeployExec> tempDeployExecList = aggregationResults.getMappedResults();
				
				if(tempDeployExecList != null && !tempDeployExecList.isEmpty()) {
					for(DeployExec deployExec : tempDeployExecList) {
						deployExec = (DeployExec) commonServiceImpl.getOneByUuidAndVersion(deployExec.getId()
								, deployExec.getVersion()
								, MetaType.deployExec.toString()
								, "N");
						deployExecList.add(deployExec);
					}
				}
			}
		}
		
		return deployExecList;
	}

	public DeployExec getDeployExecByTrainExec(String trainExecUuid
			, String trainExecVersion
			, String active
			, String status) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String appUuid = commonServiceImpl.getApp().getUuid();
		MatchOperation dependsOnFilter = match(new Criteria("dependsOn.ref.uuid").is(trainExecUuid));
		MatchOperation appFilter = match(new Criteria("appInfo.ref.uuid").is(appUuid));
		MatchOperation activeFilter = null;
		if(active != null && !active.isEmpty()) {
			activeFilter = match(new Criteria("active").in(active));
		} else {
			activeFilter = match(new Criteria("active").in("Y", "N"));
		}
		
		MatchOperation statusFilter = null;
		if(status != null && !status.isEmpty()) {
			statusFilter = match(new Criteria("statusList.stage").is(status));
		} else {
			statusFilter = match(new Criteria("statusList.stage").in(Status.Stage.COMPLETED.toString(),
					Status.Stage.FAILED.toString(),
					Status.Stage.RUNNING.toString(),
					Status.Stage.PENDING.toString(),
					Status.Stage.KILLED.toString()));
		}
		
		GroupOperation groupBy = group("uuid").max("version").as("version");
		SortOperation sortByVersion = sort(new Sort(Direction.DESC, "version"));
		LimitOperation limitToOnlyFirstDoc = limit(1);
		
		Aggregation aggregation = newAggregation(appFilter, activeFilter, dependsOnFilter, statusFilter, groupBy, sortByVersion, limitToOnlyFirstDoc);
		AggregationResults<DeployExec> aggregationResults = mongoTemplate.aggregate(aggregation, MetaType.deployExec.toString().toLowerCase(), DeployExec.class);
		DeployExec deployExec = aggregationResults.getUniqueMappedResult();
		if(deployExec != null) {
			return (DeployExec) commonServiceImpl.getOneByUuidAndVersion(deployExec.getId()
					, deployExec.getVersion()
					, MetaType.deployExec.toString()
					, "N");
		} else {
			return null;
		}
	}
	
	public List<TrainExecView> getTrainExecViewByCriteria(String modelUuid
			, String modelVersion
			, String trainExecUuid
			, String active
			, String startDate
			, String endDate
			, String status) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException, IOException {
		List<TrainExecView> trainExecViewList = new ArrayList<>();
		if(trainExecUuid != null && !trainExecUuid.isEmpty()) {
			TrainExec trainExec = getTrainExecByCriteria(trainExecUuid, null, active, startDate, endDate, status);
			if(trainExec != null) {
				TrainExecView trainExecView = getTrainExecViewByTrainExec(trainExec, active, status);				
				if(trainExecView != null) {
					trainExecViewList.add(trainExecView);
				}
			}			
		} else {
			List<TrainExec> trainExecByModel = getTrainExecByModel(modelUuid, modelVersion, active, startDate, endDate, status);
			if(trainExecByModel != null && !trainExecByModel.isEmpty()) {
				for(TrainExec trainExec : trainExecByModel) {
					TrainExecView trainExecView = getTrainExecViewByTrainExec(trainExec, active, status);
					if(trainExecView != null) {
						trainExecViewList.add(trainExecView);
					}
				}
			}
		}
		return trainExecViewList;
	}
	
	public TrainExec getTrainExecByCriteria(String trainExecUuid
			, String trainExecVersion
			, String active
			, String startDate
			, String endDate
			, String status) throws JsonProcessingException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		String appUuid = commonServiceImpl.getApp().getUuid();		
		MatchOperation uuidFilter = match(new Criteria("uuid").is(trainExecUuid));
		MatchOperation appFilter = match(new Criteria("appInfo.ref.uuid").is(appUuid));
		
		MatchOperation activeFilter = null;
		if(active != null && !active.isEmpty()) {
			activeFilter = match(new Criteria("active").is(active));
		} else {
			activeFilter = match(new Criteria("active").in("Y", "N"));
		}
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy z");
		MatchOperation dataRangeFilter = null;
		if(startDate != null && !startDate.isEmpty() 
				&& endDate != null && !endDate.isEmpty()) {
			dataRangeFilter = match(new Criteria("createdOn").lte(simpleDateFormat.parse(endDate)).gte(simpleDateFormat.parse(startDate)));
		} else if(startDate != null && !startDate.isEmpty()) {
			dataRangeFilter = match(new Criteria("createdOn").gte(simpleDateFormat.parse(startDate)));
		} else if(endDate != null && !endDate.isEmpty()) {
			dataRangeFilter = match(new Criteria("createdOn").lte(simpleDateFormat.parse(endDate)));
		}
		
		MatchOperation statusFilter = match(new Criteria("statusList.stage").is(Status.Stage.COMPLETED.toString()));
//		if(status != null && !status.isEmpty()) {
//			statusFilter = match(new Criteria("statusList.stage").is(status));
//		} else {
//			statusFilter = match(new Criteria("statusList.stage").is(Status.Stage.COMPLETED.toString()));
//		}
		
		GroupOperation groupBy = group("uuid").max("version").as("version");
		SortOperation sortByVersion = sort(new Sort(Direction.DESC, "version"));
		LimitOperation limitToOnlyFirstDoc = limit(1);
		
		Aggregation aggregation = null;
		if(dataRangeFilter != null) {
			aggregation = newAggregation(uuidFilter, statusFilter, activeFilter, appFilter, dataRangeFilter, groupBy, sortByVersion, limitToOnlyFirstDoc);
		} else {
			aggregation = newAggregation(uuidFilter, statusFilter, activeFilter, appFilter, groupBy, sortByVersion, limitToOnlyFirstDoc);
		}
		
		AggregationResults<TrainExec> aggregationResults = mongoTemplate.aggregate(aggregation, MetaType.trainExec.toString().toLowerCase(), TrainExec.class);
		TrainExec trainExec = aggregationResults.getUniqueMappedResult();
		
		if(trainExec != null) {			
			return (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExec.getId()
						, trainExec.getVersion()
						, MetaType.trainExec.toString()
						, "N");
		} else {
			return null;
		}
	}
	
	public TrainExecView getTrainExecViewByTrainExec(TrainExec trainExec
			, String active
			, String status) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if(trainExec != null) {
			TrainResult trainResult = trainResultViewServiceImpl.getTrainResultByTrainExec(trainExec.getUuid(), trainExec.getVersion());
			TrainResultView trainResultView = trainResultViewServiceImpl.getOneByUuidAndVersion(trainResult.getUuid(), trainResult.getVersion());
			TrainExecView trainExecView = new TrainExecView();
			
			//setting base entity
			trainExecView.setUuid(trainExec.getUuid());
			trainExecView.setVersion(trainExec.getVersion());
			trainExecView.setName(trainExec.getName());
			trainExecView.setDesc(trainExec.getDesc());
			trainExecView.setCreatedBy(trainExec.getCreatedBy());
			trainExecView.setCreatedOn(trainExec.getCreatedOn());
			trainExecView.setTags(trainExec.getTags());
			trainExecView.setActive(trainExec.getActive());
			trainExecView.setLocked(trainExec.getLocked());
			trainExecView.setPublished(trainExec.getPublished());
			trainExecView.setAppInfo(trainExec.getAppInfo());
			trainExecView.setStatusList(trainExec.getStatusList());
			
			//setting view specific properties
			trainExecView.setTrainResultView(trainResultView);
			DeployExec deployExec = getDeployExecByTrainExec(trainExec.getUuid(), trainExec.getVersion(), active, status);
			trainExecView.setDeployExec(deployExec);
			
			return trainExecView;
		} else {
			return null;
		}
	}

	public HttpServletResponse download(String trainExecUuid, String trainExecVersion, String format, int rows,
			String setType, RunMode runMode, HttpServletResponse response) throws Exception {
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if (rows > maxRows) {
			logger.error("Requested rows exceeded the limit of " + maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(),
					"Requested rows exceeded the limit of " + maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of " + maxRows);
		}

		List<Map<String, Object>> results = getTrainOrTestSet(trainExecUuid, trainExecVersion, setType);
		response = commonServiceImpl.download(format, response, runMode, results,
				new MetaIdentifierHolder(new MetaIdentifier(MetaType.trainExec, trainExecUuid, trainExecVersion)));
		return response;
	}
	
	public Train getTrainByTrainExec(String trainExecUuid, String trainExecVersion) throws JsonProcessingException {
		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUuid
															, trainExecVersion
															, MetaType.trainExec.toString()
															, "N");
		
		MetaIdentifier dependsOnMI = trainExec.getDependsOn().getRef();
		return (Train) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid()
				, dependsOnMI.getVersion()
				, MetaType.train.toString()
				, "N");
	}
	

	/********************** UNUSED **********************/
//	public LinkedHashMap<String, Object> getAttributeNamesWithImputeValues(List<FeatureAttrMap> featureAttrMapList, LinkedHashMap<String, Object> attributeImputeValues) throws JsonProcessingException {
//		LinkedHashMap<String, Object> imputeAttributeNameWithValues = new LinkedHashMap<>();
//		Object object = null;
//		String objectUuid = null;
//		for(FeatureAttrMap featureAttrMap : featureAttrMapList) {
//			MetaIdentifier attributeMI = featureAttrMap.getAttribute().getRef();
//			for(String imputAttrValKey : attributeImputeValues.keySet()) {
//				if(featureAttrMap.getFeatureMapId().equalsIgnoreCase(imputAttrValKey)) {
//					if(object == null 
//							|| (object != null && objectUuid != null && !((BaseEntity)object).getUuid().equalsIgnoreCase(objectUuid))) {
//						object = commonServiceImpl.getOneByUuidAndVersion(attributeMI.getUuid(), attributeMI.getVersion(), attributeMI.getType().toString(), "N");
//						objectUuid = ((BaseEntity)object).getUuid();
//					} 
//					String attributeName = getAttributeNameByObject(object, Integer.parseInt(featureAttrMap.getAttribute().getAttrId()));
//					imputeAttributeNameWithValues.put(attributeName, attributeImputeValues.get(imputAttrValKey));
//					break;
//				}
//			}
//		}
//		return imputeAttributeNameWithValues;
//	}

	/********************** UNUSED **********************/
//	public String getAttributeNameByObject(Object object, Integer attributeId) throws NullPointerException {
//		if(object instanceof Datapod) {
//			Datapod datapod = (Datapod)object;
//			return datapod.getAttributeName(attributeId);			
//		} else if(object instanceof DataSet) {
//			DataSet dataSet = (DataSet)object;
//			return dataSet.getAttributeName(attributeId);										
//		} else if(object instanceof Rule) {
//			Rule rule = (Rule)object;
//			return rule.getAttributeName(attributeId);					
//		}
//		return null;
//	}
	
	public String getAttributeNameByObject(Object object, Integer attributeId) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {		
		return (String) object.getClass().getMethod("getAttributeName", Integer.class).invoke(object, attributeId);
	}

	public Algorithm getAlgorithmByModel(String modelUuid, String modelVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelUuid
				, modelVersion
				, MetaType.model.toString()
				, "N");			
		MetaIdentifier dependsOnMI = model.getDependsOn().getRef();
		if(dependsOnMI.getType().equals(MetaType.algorithm)) {
			return (Algorithm) commonServiceImpl.getOneByUuidAndVersion(dependsOnMI.getUuid()
					, dependsOnMI.getVersion()
					, dependsOnMI.getType().toString()
					, "N");
		} else {
			return null;
		}
	}
	
	public List<Map<String, Object>> calCorealtionMatrix(Object metaObject) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Corelation corelation = mapper.convertValue(metaObject, Corelation.class);
		MetaIdentifierHolder metaIdentifierHolder = corelation.getSource();
		String sql = null;
		if (metaIdentifierHolder.getRef().getType().equals(MetaType.datapod)) {
			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
					metaIdentifierHolder.getRef().getUuid(), metaIdentifierHolder.getRef().getVersion(),
					MetaType.datapod.toString());

			sql = datapodServiceImpl.generateSqlByDatapod(datapod, RunMode.BATCH, corelation.getListAttributes());
		}
		ResultSetHolder resultSetHolder = sparkExecutor.executeSql(sql);
		return sparkExecutor.corelationMatrix(resultSetHolder);
	}
}
