package com.inferyx.framework.operator;

import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ModelExec;

public interface IModelOperator {
//	boolean trainAndValidate(String className, String modelName, Dataset<Row> df, ParamMap paramMap);
	Object predict(String targetType, Datapod targetDp, Dataset<Row> df, ModelExec modelExec, String[] fieldArray, VectorAssembler va, String clientContext) throws Exception;
	Object simulate(String targetType, Datapod targetDp, ModelExec modelExec, String[] fieldArray, String clientContext);
	Object trainAndValidate(String className, String modelName, Dataset<Row> df, VectorAssembler va,
			ParamMap paramMap);
}
