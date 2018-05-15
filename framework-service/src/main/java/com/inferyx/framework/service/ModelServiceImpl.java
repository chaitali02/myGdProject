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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.SaveMode;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.SessionHelper;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.dao.IAlgorithmDao;
import com.inferyx.framework.dao.IModelDao;
import com.inferyx.framework.dao.IModelExecDao;
import com.inferyx.framework.datascience.Math3Distribution;
import com.inferyx.framework.datascience.MonteCarloSimulation;
import com.inferyx.framework.datascience.Operator;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.DownloadExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FeatureRefHolder;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.enums.SimulationType;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.PythonExecutor;
import com.inferyx.framework.executor.RExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.DatasetOperator;
import com.inferyx.framework.operator.GenerateDataOperator;
import com.inferyx.framework.operator.PredictMLOperator;
import com.inferyx.framework.operator.RuleOperator;
import com.inferyx.framework.operator.SimulateMLOperator;
import com.inferyx.framework.operator.TransposeOldOperator;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ModelServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
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
	SparkContext sparkContext;
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
	private RunMode runMode;
	@Autowired
	private DataFrameService dataFrameService;
	@Autowired
	SessionHelper sessionHelper;
	@Autowired
	private ExecutorFactory execFactory;
	@Resource(name="taskThreadMap")
	protected ConcurrentHashMap taskThreadMap;
	@Autowired
	private SimulateMLOperator simulateMLOperator;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	MetadataUtil commonActivity;
	@Autowired
	private PredictMLOperator predictMLOperator;
	@Autowired
	private DatasetOperator datasetOperator;
	@Autowired
	private RuleOperator ruleOperator;
	@Autowired
	private ConnectionFactory connFactory;
	@Autowired
	private MonteCarloSimulation monteCarloSimulation;
	@Autowired
	private GenerateDataOperator generateDataOperator;
	
	//private ParamMap paramMap;

	
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

	/**
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}

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
				if(model.getFeatures().get(i).getType().equals(MetaType.dataset)) {
					DataSet datasetDO = (DataSet) commonServiceImpl.getLatestByUuid(model.getFeatures().get(i).getFeatureId(), MetaType.dataset.toString());
					String datapodName = datasetDO.getName();
					model.getFeatures().get(i).setName(datapodName);
					List<AttributeSource> attributeSourceList = datasetDO.getAttributeInfo();
					model.getFeatures().get(i).setName(attributeSourceList.get(Integer.parseInt(attributeId)).getAttrSourceName());	
				}else if(model.getFeatures().get(i).getType().equals(MetaType.datapod)) {					
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
				String attributeId = predict.getFeatureAttrMap().get(i).getFeatureMapId();
				//Datapod datapodDO = datapodServiceImpl.findLatestByUuid(model.getFeatures().get(i).getRef().getUuid());
				if(predict.getFeatureAttrMap().get(i).getAttribute().getRef().getType().equals(MetaType.dataset)) {
					DataSet datasetDO = (DataSet) commonServiceImpl.getLatestByUuid(predict.getFeatureAttrMap().get(i).getAttribute().getRef().getUuid(), MetaType.dataset.toString());
					String datapodName = datasetDO.getName();
					predict.getFeatureAttrMap().get(i).getAttribute().getRef().setName(datapodName);
					List<AttributeSource> attributeSourceList = datasetDO.getAttributeInfo();
					predict.getFeatureAttrMap().get(i).setAttribute(attributeSourceList.get(i).getSourceAttr());	
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
					FeatureRefHolder featureRefHolder =new FeatureRefHolder();
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
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
				logger.info(" This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun. ");
				if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					customLogger.writeLog(this.getClass(),
							"This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun.", 
							logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				}
				return modelExec;
			}
			
			//modelExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet));
			modelExec.setRefKeyList(new ArrayList<>(usedRefKeySet));

			modelExec = (ModelExec) commonServiceImpl.setMetaStatus(modelExec, MetaType.modelExec, Status.Stage.NotStarted);
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
			
			modelExec = (ModelExec) commonServiceImpl.setMetaStatus(modelExec, MetaType.modelExec, Status.Stage.Failed);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(), modelExec.getStatusList().size()>0 ? "Model creation failed, status: "+modelExec.getStatusList().get(modelExec.getStatusList().size()-1).getStage() : "Status list is empty", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
			throw new Exception("Model creation failed");
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

	public boolean executeScript(String type, String scriptName, String modelExecUuid, String modelExecVersion, String object) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Model model = (Model) commonServiceImpl.getDomainFromDomainExec(MetaType.modelExec.toString(), modelExecUuid, modelExecVersion);
		String logPath = Helper.getPropertyValue("framework.model.log.path") + "/" + modelExecUuid + "_" + modelExecVersion + "_"+ model.getVersion()+".log";
		String scriptPath = Helper.getPropertyValue("framework.model.script.path")+"/"+scriptName;
		
		IExecutor exec = execFactory.getExecutor(type);
		if(exec instanceof PythonExecutor) {
			PythonExecutor pythonExecutor = (PythonExecutor) exec;
			pythonExecutor.setLogPath(logPath);
		} else if(exec instanceof RExecutor) {
			RExecutor rExecutor = (RExecutor) exec;
			rExecutor.setLogPath(logPath);
		}		
		return exec.executeScript(scriptPath, commonServiceImpl.getApp().getUuid());
	}
	
	public List<String> readLog(String filePath, String type, String trainExecUuid, String trainExecVersion) throws IOException {
		if(filePath == null) {
			TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainExecUuid, trainExecVersion, type);
			MetaIdentifierHolder dependsOn = trainExec.getDependsOn();
			filePath = Helper.getPropertyValue("framework.model.log.path") + "/" + trainExec.getUuid() + "_" + trainExec.getVersion() + "_" + dependsOn.getRef().getVersion() + ".log";
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
	
	public HttpServletResponse download(String execUuid, String execVersion, HttpServletResponse response,RunMode runMode) throws Exception {
		
		DataStore datastore = dataStoreServiceImpl.findDatastoreByExec(execUuid, execVersion);
		String location = datastore.getLocation();
		String title = "";
		if(location.contains(hdfsInfo.getHdfsURL()))
			location = StringUtils.substringBetween(location, hdfsInfo.getHdfsURL(), "/stages");
		if(location.contains(Helper.getPropertyValue("framework.model.train.path")) && location.contains("/stages"))
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
				logger.info("Requested file " + "/" + location + "/" + fileName + ".pmml" + " not found.");
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
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.InProgress);
			
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
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.Completed);
			}else {
				isSuccess = false;
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.Failed);
			}
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Predict execution failed.");
			throw new RuntimeException((message != null) ? message : "Predict execution failed.");
		}
		return isSuccess;
	}*/

	public boolean simulate(Simulate simulate, ExecParams execParams, SimulateExec simulateExec, RunMode runMode) throws Exception {
		boolean isSuccess = false;
		execParams = (ExecParams) commonServiceImpl.resolveName(execParams, null);
		Distribution distribution = (Distribution) commonServiceImpl.getOneByUuidAndVersion(simulate.getDistributionTypeInfo().getRef().getUuid(), simulate.getDistributionTypeInfo().getRef().getVersion(), simulate.getDistributionTypeInfo().getRef().getType().toString());
		try {
			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.InProgress);
			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(simulate.getDependsOn().getRef().getUuid(),
					simulate.getDependsOn().getRef().getVersion(), MetaType.model.toString());
			
			Algorithm algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(
					model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(),
					MetaType.algorithm.toString());

			String modelName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), simulateExec.getVersion());
			String filePath = "/simulate"+String.format("/%s/%s/%s", model.getUuid().replace("-", "_"), model.getVersion(), simulateExec.getVersion());
			//String tableName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), simulateExec.getVersion());
			
			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
			
			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			Object result = null;
			String[] fieldArray = modelExecServiceImpl.getAttributeNames(simulate);
			
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			
			ExecParams distExecParam = new ExecParams(); 
			ExecParams simExecParam = new ExecParams(); 
			
			List<ParamListHolder> distParamHolderList = new ArrayList<>();
			List<ParamListHolder> simParamHolderList= new ArrayList<>();
			
			String tableName = null;
			List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
			for(ParamListHolder holder : paramListInfo) {
				if(simulate.getParamList() != null && holder.getRef().getUuid().equalsIgnoreCase(simulate.getParamList().getRef().getUuid())) {
					simParamHolderList.add(holder);
				} else if(holder.getRef().getUuid().equalsIgnoreCase(distribution.getParamList().getRef().getUuid())) {
					distParamHolderList.add(holder);
				}
				if(holder.getParamName().equalsIgnoreCase("saveLocation")) {
					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(holder.getParamValue().getRef().getUuid(), holder.getParamValue().getRef().getVersion(), holder.getParamValue().getRef().getType().toString());
					tableName = datapodServiceImpl.genTableNameByDatapod(datapod, simulateExec.getVersion(), runMode);
				}
			}
			distExecParam.setParamListInfo(distParamHolderList);
			simExecParam.setParamListInfo(simParamHolderList);
			
			ParamListHolder distributionInfo = new ParamListHolder();
			distributionInfo.setParamId("0");
			distributionInfo.setParamName("distribution");
			distributionInfo.setParamType("distribution");
			MetaIdentifier distIdentifier = new MetaIdentifier(MetaType.distribution, distribution.getUuid(), distribution.getVersion());
			MetaIdentifierHolder distHolder = new MetaIdentifierHolder(distIdentifier);
			distributionInfo.setParamValue(distHolder);
			distributionInfo.setRef(new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()));
			
			List<ParamListHolder> paramListInfo2 = execParams.getParamListInfo();
			paramListInfo2.add(distributionInfo);
			execParams.setParamListInfo(paramListInfo2);
			
			String appUuid = commonServiceImpl.getApp().getUuid();
			if(simulate.getType().equalsIgnoreCase(SimulationType.MONTECARLO.toString())) {
				result = monteCarloSimulation.simulateMonteCarlo(simulate, simExecParam, distExecParam, filePathUrl);
			} else if(simulate.getType().equalsIgnoreCase(SimulationType.DEFAULT.toString())) {
				if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
					
					HashMap<String, String> otherParams = execParams.getOtherParams();
					if(otherParams == null)
						otherParams = new HashMap<>();
					otherParams = (HashMap<String, String>) generateDataOperator.populateParams(null, execParams, new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()), null, otherParams, null, null, runMode);
					
					tableName = generateDataOperator.execute(null, execParams, new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()), null, otherParams, null, runMode);
					

					//Object object = mlDistribution.getDistribution(distribution, distExecParam);
						
					//String tabName_1 = exec.generateFeatureData(object, model.getFeatures(), simulate.getNumIterations(), (tableName+"_"+"form_rand_df"));
					String tabName_2 = exec.assembleRandomDF(fieldArray, tableName, true, appUuid);
					String sql = simulateMLOperator.generateSql(simulate, tabName_2);
					//result = exec.executeAndRegister(sql, tableName, commonServiceImpl.getApp().getUuid());
					result = exec.executeRegisterAndPersist(sql, tabName_2, filePath, null, SaveMode.Append.toString(), appUuid);					
				} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
					//Object object = mlDistribution.getDistribution(distribution, distExecParam);

					HashMap<String, String> otherParams = execParams.getOtherParams();
					if(otherParams == null)
						otherParams = new HashMap<>();
					otherParams = (HashMap<String, String>) generateDataOperator.populateParams(null, execParams, new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()), null, otherParams, null, null, runMode);
					
					tableName = generateDataOperator.execute(null, execParams, new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()), null, otherParams, null, runMode);
					
					//String tabName_1 = exec.generateFeatureData(object, model.getFeatures(), simulate.getNumIterations(), (tableName+"_"+"algo_rand_df"));
					String tabName_2 = null;
//					for(int i=0; i<fieldArray.length; i++) {
						String[] customFldArr = new String[] {fieldArray[0]};
						tabName_2 = exec.assembleRandomDF(customFldArr, tableName, true, appUuid);	
//						tableName = tabName_2;
//					}
					
					String sql = "SELECT * FROM " + tabName_2;
					//result = exec.executeAndRegister(sql, tableName, commonServiceImpl.getApp().getUuid());
					result = exec.executeRegisterAndPersist(sql, tabName_2, filePath, null, SaveMode.Append.toString(), appUuid);				
				}
			}
			
			
			dataStoreServiceImpl.setRunMode(runMode);

			dataStoreServiceImpl.create(filePathUrl, modelName,
					new MetaIdentifier(MetaType.simulate, simulate.getUuid(), simulate.getVersion()),
					new MetaIdentifier(MetaType.simulateExec, simulateExec.getUuid(), simulateExec.getVersion()),
					simulateExec.getAppInfo(), simulateExec.getCreatedBy(), SaveMode.Append.toString(), resultRef);

			simulateExec.setLocation(filePathUrl);
			simulateExec.setResult(resultRef);
			commonServiceImpl.save(MetaType.simulateExec.toString(), simulateExec);
			if (result != null) {
				isSuccess = true;
				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.Completed);
			}else {
				isSuccess = false;
				simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.Failed);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.Failed);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Simulate execution failed.");
			throw new RuntimeException((message != null) ? message : "Simulate execution failed.");
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
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
				logger.info(" This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun. ");
				return predictExec;
			}

			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.NotStarted);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);				
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.Failed);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable Predict.");
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
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
				logger.info(" This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun. ");
				return simulateExec;
			}
			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.NotStarted);			
		} catch (Exception e) {
			logger.error(e);	
			simulateExec = (SimulateExec) commonServiceImpl.setMetaStatus(simulateExec, MetaType.simulateExec, Status.Stage.Failed);
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable Simulate.");
			throw new RuntimeException((message != null) ? message : "Can not create executable Simulate.");
		}
		return simulateExec;
	}
	
	public TrainExec create(Train train, Model model, ExecParams execParams, ParamMap paramMap,
			TrainExec trainExec) throws Exception {
		String logPath = null;
		
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
				if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					logPath = Helper.getPropertyValue("framework.model.log.path") + "/" + model.getUuid() + "_" + model.getVersion() + "_"+ trainExec.getVersion()+".log";
				}
				if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					customLogger.writeLog(this.getClass(),
							"Created raw model exec, uuid: " + trainExec.getUuid(), 
							logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				}
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
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(), 
						"Saving raw modelExec, uuid: " + trainExec.getUuid(),
						logPath, 
						Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
			commonServiceImpl.save(MetaType.trainExec.toString(), trainExec);
			
			if (Helper.getLatestStatus(statusList) != null 
					&& (Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.InProgress, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.Completed, new Date())) 
							|| Helper.getLatestStatus(statusList).equals(new Status(Status.Stage.OnHold, new Date())))) {
				logger.info(" This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun. ");
				if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
					customLogger.writeLog(this.getClass(),
							"This process is In Progress or has been completed previously or is On Hold. Hence it cannot be rerun.", 
							logPath,
							Thread.currentThread().getStackTrace()[1].getLineNumber());
				}
				return trainExec;
			}
			
			//modelExec.setExec(dqOperator.generateSql(dataQual, datapodList, dataQualExec, dagExec, usedRefKeySet));
			trainExec.setRefKeyList(new ArrayList<>(usedRefKeySet));

			trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.NotStarted);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(),
						trainExec.getStatusList().size()>0 ? "Latest status: "+trainExec.getStatusList().get(trainExec.getStatusList().size()-1).getStage() : "Status list is empty", 
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
			
			trainExec = (TrainExec) commonServiceImpl.setMetaStatus(trainExec, MetaType.trainExec, Status.Stage.Failed);
			if(model.getType().equalsIgnoreCase(ExecContext.R.toString()) || model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
				customLogger.writeLog(this.getClass(), trainExec.getStatusList().size()>0 ? "Train exec creation failed, status: "+trainExec.getStatusList().get(trainExec.getStatusList().size()-1).getStage() : "Status list is empty", logPath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			}
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not create executable Train.");
			throw new RuntimeException((message != null) ? message : "Can not create executable Train.");
		}		
		return trainExec;
	}
	
	public TrainExec train(Train train, Model model, TrainExec  trainExec, ExecParams execParams, ParamMap paramMap) throws FileNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JSONException, ParseException{
		
		if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
			commonServiceImpl.sendResponse("400", MessageStatus.FAIL.toString(), "Training can not be performed on formula.");
			throw new RuntimeException("Training can not be performed on formula."); 
		}
		
		RunModelServiceImpl runModelServiceImpl = new RunModelServiceImpl();
		List<Status> statusList = trainExec.getStatusList();
		if (statusList == null) {
			statusList = new ArrayList<>();
			trainExec.setStatusList(statusList);
		}
	
		if(!model.getType().equalsIgnoreCase(ExecContext.R.toString()) && !model.getType().equalsIgnoreCase(ExecContext.PYTHON.toString())) {
			runModelServiceImpl.setAlgorithmUUID(model.getDependsOn().getRef().getUuid());
			runModelServiceImpl.setAlgorithmVersion(model.getDependsOn().getRef().getVersion());
		}
		runModelServiceImpl.setAlgorithmServiceImpl(algorithmServiceImpl);
		runModelServiceImpl.setDataStoreServiceImpl(dataStoreServiceImpl);
		runModelServiceImpl.setSparkContext(sparkContext);
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
			runModelServiceImpl.setLogPath(Helper.getPropertyValue("framework.model.log.path") + "/" + model.getUuid() + "_" + model.getVersion() + "_"+ trainExec.getVersion()+".log");
		runModelServiceImpl.setExecFactory(execFactory);
		runModelServiceImpl.setSecurityServiceImpl(securityServiceImpl);
		runModelServiceImpl.setTrainExec(trainExec);
		runModelServiceImpl.setTrain(train);
		runModelServiceImpl.setName(MetaType.trainExec+"_"+trainExec.getUuid()+"_"+trainExec.getVersion());
		runModelServiceImpl.setExecType(MetaType.trainExec);
		/*FutureTask<TaskHolder> futureTask = new FutureTask<TaskHolder>(runModelServiceImpl);
		metaExecutor.execute(futureTask);
		taskList.add(futureTask);
		taskThreadMap.put(MetaType.trainExec+"_"+trainExec.getUuid()+"_"+trainExec.getVersion(), futureTask);
		logger.info(" taskThreadMap while creating train : " + taskThreadMap);*/
		runModelServiceImpl.call();
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

	public boolean save(String className, Object obj, SparkContext sparkContext, String path) {

		Class<?> dynamicClass = obj.getClass();

		Class<?>[] paramSave = new Class[1];
		paramSave[0] = String.class;

		Method m1 = null;
		try {
			m1 = dynamicClass.getMethod("save", paramSave);
			try {
				m1.invoke(obj, path);
				return true;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
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
	
	public String generateSQLBySource(Object source) throws Exception {  
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		if (source instanceof Datapod) {
			Datapod datapod = (Datapod) source;
			DataStore datastore = dataStoreServiceImpl.findLatestByMeta(datapod.getUuid(), datapod.getVersion());
			if (datastore == null) {
				logger.error("Datastore is not available for this datapod");
				throw new Exception();
			}
			//IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
			IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
			ConnectionHolder conHolder = conn.getConnection();
			Object obj = conHolder.getStmtObject();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			//DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			
			String tableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), RunMode.BATCH);
			//String tableName = exec.readFile(commonServiceImpl.getApp().getUuid(), datapod, datastore, null, hdfsInfo, obj, datasource);
			String sql = "SELECT * FROM "+tableName;
			return sql;
		} else if (source instanceof DataSet) {
			DataSet dataset = (DataSet) source;
			String sql = datasetOperator.generateSql(dataset, null, null, new HashSet<>(), null, RunMode.BATCH);
			return sql;
		} else if (source instanceof Rule) {
			Rule rule = (Rule) source;
			String sql = ruleOperator.generateSql(rule, null, null, new HashSet<>(), null, RunMode.BATCH);
			return sql;
		}
		return null;
	}

	public Object getTrainedModelByTrainExec(String modelClassName, TrainExec trainExec) throws JsonProcessingException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> modelClass = Class.forName(modelClassName);

		MetaIdentifierHolder datastoreHolder = trainExec.getResult();
		DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreHolder.getRef().getUuid(),
				datastoreHolder.getRef().getVersion(), datastoreHolder.getRef().getType().toString());
		if (dataStore == null)
			throw new NullPointerException("No datastore available");
		String location = dataStore.getLocation();

		if (location.contains("/data"))
			location = location.replaceAll("/data", "");
		if(!location.contains(hdfsInfo.getHdfsURL()))
			location = hdfsInfo.getHdfsURL() + location;

		Object trainedModel = modelClass.getMethod("load", String.class).invoke(modelClass, location);
		return trainedModel;
	}

	public boolean predict(Predict predict, ExecParams execParams, PredictExec predictExec) throws Exception {
		boolean isSuccess = false;
		try {
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.InProgress);
			
			MetaIdentifierHolder modelHolder = predict.getDependsOn();
			MetaIdentifierHolder sourceHolder = predict.getSource();
			MetaIdentifierHolder targetHolder = predict.getTarget();

			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelHolder.getRef().getUuid(),
					modelHolder.getRef().getVersion(), modelHolder.getRef().getType().toString());
			Object source = (Object) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
					sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
			Datapod target = null;
			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod))
				target = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(),
						targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());
			
			Algorithm algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(
					model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(),
					MetaType.algorithm.toString());

			String modelName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), predictExec.getVersion());
			String filePath = "/predict"+String.format("/%s/%s/%s", model.getUuid().replace("-", "_"), model.getVersion(), predictExec.getVersion());
			String tableName = String.format("%s_%s_%s", model.getUuid().replace("-", "_"), model.getVersion(), predictExec.getVersion());

			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);

			MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
			Object result = null;
			
			String[] fieldArray = modelExecServiceImpl.getAttributeNames(predict);
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());

			String appUuid = commonServiceImpl.getApp().getUuid();
			
			String sql = generateSQLBySource(source);
			exec.executeAndRegister(sql, (tableName+"_pred_data"), appUuid);
			
			if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
				String predictQuery = predictMLOperator.generateSql(predict, (tableName+"_pred_data"));
				result = exec.executeRegisterAndPersist(predictQuery, (tableName+"_pred_data"), filePath, target, SaveMode.Append.toString(), appUuid);
			} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
				TrainExec trainExec = modelExecServiceImpl.getLatestTrainExecByModel(model.getUuid(),
						model.getVersion());
				if (trainExec == null)
					throw new Exception("Executed model not found.");
				
				exec.assembleDF(fieldArray, (tableName+"_pred_data"), algorithm.getTrainName(), model.getLabel(), appUuid);
				Object trainedModel = getTrainedModelByTrainExec(algorithm.getModelName(), trainExec);
				filePathUrl = exec.executePredict(trainedModel, target, filePathUrl, (tableName+"_pred_data"), appUuid);
				result = filePathUrl;
			}
			
			dataStoreServiceImpl.setRunMode(RunMode.BATCH);
			dataStoreServiceImpl.create(filePathUrl, modelName,
					new MetaIdentifier(MetaType.predict, predict.getUuid(), predict.getVersion()),
					new MetaIdentifier(MetaType.predictExec, predictExec.getUuid(), predictExec.getVersion()),
					predictExec.getAppInfo(), predictExec.getCreatedBy(), SaveMode.Append.toString(), resultRef);

			predictExec.setLocation(filePathUrl);
			predictExec.setResult(resultRef);
			commonServiceImpl.save(MetaType.predictExec.toString(), predictExec);
			if (result != null) {
				isSuccess = true;
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.Completed);
			}else {
				isSuccess = false;
				predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.Failed);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			predictExec = (PredictExec) commonServiceImpl.setMetaStatus(predictExec, MetaType.predictExec, Status.Stage.Failed);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Predict execution failed.");
			throw new RuntimeException((message != null) ? message : "Predict execution failed.");
		}
		return isSuccess;
	}

	
	
	

}
