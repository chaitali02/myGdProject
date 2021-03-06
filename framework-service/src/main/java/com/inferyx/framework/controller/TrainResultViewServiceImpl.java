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
package com.inferyx.framework.controller;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.domain.TrainResult;
import com.inferyx.framework.domain.TrainResultView;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.MetadataServiceImpl;

/**
 * @author Ganesh
 *
 */
@Service
public class TrainResultViewServiceImpl {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private MetadataServiceImpl metadataServiceImpl;
	
	private static Logger logger = Logger.getLogger(TrainResultViewServiceImpl.class);
	
	public TrainResult getTrainResultByTrainExec(String trainExecUuid, String trainExecVersion) throws JsonProcessingException {
		logger.info("Inside method: getTrainResultByTrainExec");
		MatchOperation filter = null;
		if(trainExecVersion != null && !trainExecVersion.isEmpty()) {
			filter = match(new Criteria("dependsOn.ref.uuid").is(trainExecUuid).andOperator(new Criteria("dependsOn.ref.version").is(trainExecVersion)));
		} else {
			filter = match(new Criteria("dependsOn.ref.uuid").is(trainExecUuid));
		}
		
		SortOperation sortByVersion = sort(new Sort(Direction.ASC, "version"));
		LimitOperation limitToOnlyFirstDoc = limit(1);
		GroupOperation groupByUuid = group("uuid").max("version").as("version");
		Aggregation scheduleAggr = newAggregation(filter, sortByVersion, limitToOnlyFirstDoc, groupByUuid);
		AggregationResults<TrainResult> scheduleAggrResults = mongoTemplate.aggregate(scheduleAggr, MetaType.trainresult.toString().toLowerCase(), TrainResult.class);
		TrainResult trainResult = (TrainResult) scheduleAggrResults.getUniqueMappedResult();	
		if(trainResult != null) {
			return (TrainResult) commonServiceImpl.getOneByUuidAndVersion(trainResult.getId(), trainResult.getVersion(), MetaType.trainresult.toString(), "N");
		} else {
			return null;//throw new RuntimeException("No train result found.");
		}		
	}

	public TrainResultView getOneByUuidAndVersion(String trainResultUuid, String trainResultVersion) throws JsonProcessingException {
		logger.info("Inside method: getTrainResultByTrainExec");
		TrainResult trainResult = (TrainResult) commonServiceImpl.getOneByUuidAndVersion(trainResultUuid, trainResultVersion, MetaType.trainresult.toString());
		TrainResultView trainResultView = new TrainResultView();
		
		//setting base entity
		trainResultView.setUuid(trainResult.getUuid());
		trainResultView.setVersion(trainResult.getVersion());
		trainResultView.setDesc(trainResult.getDesc());
		trainResultView.setName(trainResult.getName());
		trainResultView.setCreatedBy(trainResult.getCreatedBy());
		trainResultView.setCreatedOn(trainResult.getCreatedOn());
		trainResultView.setTags(trainResult.getTags());
		trainResultView.setActive(trainResult.getActive());
		trainResultView.setPublished(trainResult.getPublished());
		trainResultView.setLocked(trainResult.getLocked());
		trainResultView.setAppInfo(trainResult.getAppInfo());
				
		//setting trainResultView specific properties
		trainResultView.setDependsOn(trainResult.getDependsOn());
		trainResultView.setTotalRecords(trainResult.getTotalRecords());
		trainResultView.setTrainingSet(trainResult.getTrainingSet());
		trainResultView.setValidationSet(trainResult.getValidationSet());
		trainResultView.setTimeTaken(trainResult.getTimeTaken());
		trainResultView.setAccuracy(trainResult.getAccuracy());
		trainResultView.setPrecision(trainResult.getPrecision());
		trainResultView.setRecall(trainResult.getRecall());
		trainResultView.setF1Score(trainResult.getF1Score());
		trainResultView.setCostMatrixGain(trainResult.getCostMatrixGain());
		trainResultView.setLogLoss(trainResult.getLogLoss());
		trainResultView.setRocAUC(trainResult.getRocAUC());
		trainResultView.setLift(trainResult.getLift());
		trainResultView.setConfusionMatrix(trainResult.getConfusionMatrix());	
		trainResultView.setAlgorithm(trainResult.getAlgorithm());	
		trainResultView.setNumFeatures(trainResult.getNumFeatures());
		trainResultView.setAlgoType(trainResult.getAlgoType());
		trainResultView.setParamList(metadataServiceImpl.getParamByParamList(trainResult.getParamList().getRef().getUuid()));
		trainResultView.setRocCurve(trainResult.getRocCurve());
		trainResultView.setTrainClass(trainResult.getTrainClass());
		trainResultView.setStartTime(trainResult.getStartTime());
		trainResultView.setEndTime(trainResult.getEndTime());

		
		MetaIdentifier trainResultDependsOnMI = trainResult.getDependsOn().getRef();
		TrainExec trainExec = (TrainExec) commonServiceImpl.getOneByUuidAndVersion(trainResultDependsOnMI.getUuid(), trainResultDependsOnMI.getVersion(), trainResultDependsOnMI.getType().toString(), "N");
		MetaIdentifier trainExecDependsOnMI = trainExec.getDependsOn().getRef();
		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainExecDependsOnMI.getUuid(), trainExecDependsOnMI.getVersion(), trainExecDependsOnMI.getType().toString(), "N");
		MetaIdentifier modelMI = train.getDependsOn().getRef();
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelMI.getUuid(), modelMI.getVersion(), modelMI.getType().toString(), "N");
		
		List<Feature> featureAttrMapList = model.getFeatures();
		List<Double> featureImportanceList = trainResult.getFeatureImportance();
		if(featureImportanceList != null && !featureImportanceList.isEmpty()) {
			Map<String, Double> featureImportance = new LinkedHashMap<>();
			for(int i=0; i < featureAttrMapList.size(); i++) {
				featureImportance.put(featureAttrMapList.get(i).getName(), featureImportanceList.get(i));
			}
			
			if(!featureImportance.isEmpty()) {
				trainResultView.setFeatureImportance(featureImportance);
			}
		}		
		return trainResultView;
	}	
}
