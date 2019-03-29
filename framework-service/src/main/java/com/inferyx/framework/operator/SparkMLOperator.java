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
package com.inferyx.framework.operator;

import org.apache.log4j.Logger;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
@Service
public class SparkMLOperator implements IModelOperator {

//	@Autowired
//	private ParamSetServiceImpl paramSetServiceImpl;
//	@Autowired
//	private CommonServiceImpl<?> commonServiceImpl;
//	@Autowired
//	private ConnectionFactory connectionFactory;
//	@Autowired
//	private ExecutorFactory execFactory;
//	@Autowired
//	private SQLContext sqlContext;
//	@Autowired
//	private MetadataUtil daoRegister;
//	@Autowired
//	private DataSourceFactory datasourceFactory;
//	@Autowired
//	private SparkSession sparkSession;
//	@Autowired
//	private HDFSInfo hdfsInfo;
//	@Autowired
//	private ModelServiceImpl modelServiceImpl;

	static final Logger LOGGER = Logger.getLogger(SparkMLOperator.class);

	/********************** UNUSED **********************/
	@Override
	public Object simulate(Simulate simulate, Model model, Algorithm algorithm, Datapod targetDp,
			TrainExec latestTrainExec, String[] fieldArray, String targetType, String tableName, String filePathUrl,
			String filePath, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/********************** UNUSED **********************/
	@Override
	public Object predict(Predict predict, Model model, Algorithm algorithm, Datapod targetDp, Dataset<Row> df,
			String[] fieldArray, TrainExec latestTrainExec, VectorAssembler va, String targetType, String tableName,
			String filePathUrl, String filePath, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/********************** UNUSED **********************/
	@Override
	public Object trainAndValidate(Train train, Model model, Algorithm algorithm, String modelClassName,
			String modelName, Dataset<Row> df, VectorAssembler va, ParamMap paramMap, String filePathUrl,
			String filePath) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}