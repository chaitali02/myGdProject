package com.inferyx.framework.algo;

import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface IAlgorithm {
	boolean train(String className,String modelName, Dataset<Row> df, ParamMap paramMap);
	Object validate();
	Object predict();
}
