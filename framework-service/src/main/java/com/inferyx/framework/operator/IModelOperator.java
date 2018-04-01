package com.inferyx.framework.operator;

import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;

public interface IModelOperator {
//	boolean trainAndValidate(String className, String modelName, Dataset<Row> df, ParamMap paramMap);
	//Object predict(Datapod targetDp, Dataset<Row> df, String[] fieldArray, TrainExec trainExec, VectorAssembler va, String targetType, String tableName, String clientContext) throws Exception;
	//Object simulate(Datapod targetDp, TrainExec trainExec, String[] fieldArray, String targetType, String tableName, String clientContext) throws Exception;
	//Object trainAndValidate(String className, String modelName, Dataset<Row> df, VectorAssembler va, ParamMap paramMap);
	
	Object simulate(Simulate simulate, Model model, Algorithm algorithm, Datapod targetDp, TrainExec latestTrainExec,
			String[] fieldArray, String targetType, String tableName, String filePathUrl, String filePath,
			String clientContext) throws Exception;
	
	Object predict(Predict predict, Model model, Algorithm algorithm, Datapod targetDp, Dataset<Row> df,
			String[] fieldArray, TrainExec latestTrainExec, VectorAssembler va, String targetType, String tableName,
			String filePathUrl, String filePath, String clientContext) throws Exception;

	Object trainAndValidate(Train train, Model model, Algorithm algorithm, String modelClassName, String modelName,
			Dataset<Row> df, VectorAssembler va, ParamMap paramMap, String filePathUrl, String filePath) throws Exception;
}
