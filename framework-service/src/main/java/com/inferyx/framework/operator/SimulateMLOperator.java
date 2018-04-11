/**
 * 
 */
package com.inferyx.framework.operator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.writer.IWriter;

/**
 * @author joy
 *
 */
public class SimulateMLOperator {

	@Autowired
	private SparkSession sparkSession;
	@Autowired
	private PredictMLOperator predictMLOperator;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private FormulaOperator formulaOperator;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private MetadataUtil daoRegister;
	@Autowired
	private DataSourceFactory datasourceFactory;
	
	/**
	 * 
	 */
	public SimulateMLOperator() {
		// TODO Auto-generated constructor stub
	}
	
	public Object execute(Simulate simulate, Model model, Algorithm algorithm, Datapod targetDp, TrainExec latestTrainExec, String[] fieldArray, String targetType,
			String tableName, String filePathUrl, String filePath, String clientContext) throws Exception {

		int numIterations = simulate.getNumIterations();
		Dataset<Row> df = null;
		StringBuilder sb = new StringBuilder();
		// write code
		try {
			for (Feature feature : model.getFeatures()) {
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
			if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
				sparkSession.sqlContext().registerDataFrameAsTable(assembledDf, tableName);
				return execute(simulate, model, assembledDf, fieldArray, tableName, filePathUrl, filePath, commonServiceImpl.getApp().getUuid());
			} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
				
				return predictMLOperator.execute(null, model, algorithm, targetDp, assembledDf, fieldArray, latestTrainExec, va, targetType, tableName, filePathUrl, filePath, clientContext);
			} else 
				return null;
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String execute(Simulate simulate, Model model, Dataset<Row> df, String[] fieldArray, String tableName,
			String filePathUrl, String filePath, String uuid) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException, IOException {
		StringBuilder builder = new StringBuilder();
		String aliaseName = "";
		builder.append("SELECT ");
		MetaIdentifierHolder dependsOn = model.getDependsOn();
		Object object = commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
		if(object instanceof Formula) {
			Formula formula = (Formula) object;
			for (Feature feature : model.getFeatures()) {
				builder.append(feature.getName()).append(" AS ").append(feature.getName()).append(", ");
			}
			
			builder.append(formulaOperator.generateSql(formula, null, null, null)).append(" AS ").append(model.getLabel());
			builder.append(" FROM ");
			builder.append(tableName.replaceAll("-", "_")).append(" ").append(aliaseName);
			
			System.out.println("sim query: "+builder.toString());
			
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			ResultSetHolder rsHolder = exec.executeSql(builder.toString());
			Dataset<Row> resultDf = rsHolder.getDataFrame();
			resultDf.printSchema();
			resultDf.show();
			IWriter datapodWriter = datasourceFactory.getDatapodWriter(null, daoRegister);
			datapodWriter.write(resultDf, filePathUrl, null, SaveMode.Append.toString());
		}
		return filePathUrl;
	}

}
