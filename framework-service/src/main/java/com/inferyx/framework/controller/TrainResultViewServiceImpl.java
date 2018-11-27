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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamListHolder;
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
	
	public TrainResult getTrainResultByTrainExec(String trainExecUuid, String trainExecVersion) throws JsonProcessingException {
		//TrainResult trainResult = null;
		
		MatchOperation filter = null;
		if(trainExecVersion != null && !trainExecVersion.isEmpty()) {
			filter = match(new Criteria("dependsOn.ref.uuid").is(trainExecUuid).andOperator(new Criteria("dependsOn.ref.version").is(trainExecVersion)));
		} else {
			filter = match(new Criteria("dependsOn.ref.uuid").is(trainExecUuid));
		}
		 
		GroupOperation groupByUuid = group("uuid").max("version").as("version");
		Aggregation scheduleAggr = newAggregation(filter, groupByUuid);
		AggregationResults<TrainResult> scheduleAggrResults = mongoTemplate.aggregate(scheduleAggr, MetaType.trainresult.toString().toLowerCase(), TrainResult.class);
		TrainResult trainResult = (TrainResult) scheduleAggrResults.getUniqueMappedResult();	
		return (TrainResult) commonServiceImpl.getOneByUuidAndVersion(trainResult.getId(), trainResult.getVersion(), MetaType.trainresult.toString());
	}

	public TrainResultView getOneByUuidAndVersion(String uuid, String version) throws JsonProcessingException {
		TrainResult trainResult = (TrainResult) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.trainresult.toString());
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
		trainResultView.setParamList(metadataServiceImpl.getParamByParamList(trainResult.getParamList().getRef().getUuid()));
		
//		trainResultView.setFeatureImportance(featureImportance);
		return trainResultView;
	}

	
}
