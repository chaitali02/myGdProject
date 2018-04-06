/**
 * 
 */
package com.inferyx.framework.operator;

import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author joy
 *
 */
public class SimulateMLOperator {

	@Autowired
	private SparkSession sparkSession;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private PredictMLOperator predictMLOperator;
	
	/**
	 * 
	 */
	public SimulateMLOperator() {
		// TODO Auto-generated constructor stub
	}
	
	public Object simulate(Simulate simulate, Model model, Algorithm algorithm, Datapod targetDp, TrainExec latestTrainExec, String[] fieldArray, String targetType,
			String tableName, String filePathUrl, String filePath, String clientContext) throws Exception {

		int numIterations = simulate.getNumIterations();
		Dataset<Row> df = null;
		StringBuilder sb = new StringBuilder();
		// write code
		try {
			Model model_2 = (Model) commonServiceImpl.getOneByUuidAndVersion(simulate.getDependsOn().getRef().getUuid(),
					simulate.getDependsOn().getRef().getVersion(),
					simulate.getDependsOn().getRef().getType().toString());
			for (Feature feature : model_2.getFeatures()) {
				sb.append("(" + feature.getMinVal() + " + rand()*(" + feature.getMaxVal() + "-" + feature.getMinVal()
						+ ")) AS " + feature.getName() + ", ");
			}
			// df = sqlContext.range(0,numIterations).select("id",
			// sb.toString().substring(0, sb.toString().length()-2));

			tableName = tableName.replaceAll("-", "_");
			df = sparkSession.sqlContext().range(0, numIterations);
			// df.registerTempTable(tableName);
			df.createOrReplaceTempView(tableName);
			sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
			df = sparkSession.sqlContext()
					.sql("SELECT id, " + sb.toString().substring(0, sb.toString().length() - 2) + " FROM " + tableName);
			df.show();

			VectorAssembler va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
			Dataset<Row> assembledDf = va.transform(df);
			assembledDf.show();
			return predictMLOperator.predict(null, model, algorithm, targetDp, assembledDf, fieldArray, latestTrainExec, va, targetType, tableName, filePathUrl, filePath, clientContext);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

}
