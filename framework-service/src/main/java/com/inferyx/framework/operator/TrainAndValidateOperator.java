/**
 * 
 */
package com.inferyx.framework.operator;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.dmg.pmml.PMML;
import org.jpmml.model.MetroJAXBUtil;
import org.jpmml.sparkml.ConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.service.ModelServiceImpl;

/**
 * @author joy
 *
 */
@Component
public class TrainAndValidateOperator {
	
	@Autowired
	private SparkContext sparkContext;
	@Autowired
	private HDFSInfo hdfsInfo;
	@Autowired
	private ModelServiceImpl modelServiceImpl; 

	static final Logger logger = Logger.getLogger(SparkMLOperator.class);

	/**
	 * 
	 */
	public TrainAndValidateOperator() {
		// TODO Auto-generated constructor stub
	}
	
	public Object execute(Train train, Model model, Algorithm algorithm,String modelClassName, String modelName, Dataset<Row> df, VectorAssembler va,
			ParamMap paramMap, String filePathUrl,String filePath) throws Exception {
		PipelineModel trngModel = null;
		List<String> customDirectories = new ArrayList<>();
		try {
			Dataset<Row>[] splits = df
					.randomSplit(new double[] { train.getTrainPercent() / 100, train.getValPercent() / 100 }, 12345);
			Dataset<Row> trngDf = splits[0];
			Dataset<Row> valDf = splits[1];
			Dataset<Row> trainingDf = null;
			Dataset<Row> validateDf = null;
			if (algorithm.getTrainName().contains("LinearRegression")
					|| algorithm.getTrainName().contains("LogisticRegression")) {
				trainingDf = trngDf.withColumn("label", trngDf.col(model.getLabel()).cast("Double")).select("label",
						va.getInputCols());
				validateDf = valDf.withColumn("label", valDf.col(model.getLabel()).cast("Double")).select("label",
						va.getInputCols());
			} else {
				trainingDf = trngDf;
				validateDf = valDf;
			}

			Dataset<Row> trainedDataSet = null;
			@SuppressWarnings("unused")
			StringIndexer labelIndexer = null;
			@SuppressWarnings("unused")
			String labelColName = (modelClassName.contains("classification")) ? "indexedLabel" : "label";
			/*
			 * labelIndexer = new StringIndexer() .setInputCol("label")
			 * .setOutputCol(labelColName);
			 */

			Class<?> dynamicClass = Class.forName(modelClassName);
			Object obj = dynamicClass.newInstance();
			Method method = null;
			if (algorithm.getTrainName().contains("LinearRegression")
					|| algorithm.getTrainName().contains("LogisticRegression")) {
				method = dynamicClass.getMethod("setLabelCol", String.class);
				method.invoke(obj, "label");
			}

			method = dynamicClass.getMethod("setFeaturesCol", String.class);
			method.invoke(obj, "features");
			Pipeline pipeline = new Pipeline()
					.setStages(new PipelineStage[] { /* labelIndexer, */va, (PipelineStage) obj });
			if (null != paramMap) {
				trngModel = pipeline.fit(trainingDf, paramMap);
			} else {
				trngModel = pipeline.fit(trainingDf);
			}

			// Below line is validation step. Won't create a model. It would create a
			// DataSet<Row> which can be checked for validation
			trainedDataSet = trngModel.transform(validateDf);
			trainedDataSet.show();

			Transformer[] transformers = trngModel.stages();
			for (int i = 0; i < transformers.length; i++) {
				customDirectories.add(i + "_" + transformers[i].uid());
			}

			/*
			 * List<Row> rowList = trainedDataSet.takeAsList(1); Vector vec = (Vector)
			 * rowList.get(0).get(4); vec = vec.compressed();
			 */

			// Vector features = new DenseVector(values)
			boolean result = modelServiceImpl.save(modelName, trngModel, sparkContext, filePathUrl);
			if (algorithm.getSavePmml().equalsIgnoreCase("Y")) {
				try {
					logger.info("trainedDataSet schema : " + trainedDataSet.schema());
					if(filePathUrl.contains(hdfsInfo.getHdfsURL()))
						filePathUrl = filePathUrl.replaceAll(hdfsInfo.getHdfsURL(), "");
					
					String fileLocation = filePathUrl + "/" + model.getUuid() + "_" + model.getVersion() + "_"
							+ (filePathUrl.substring(filePathUrl.lastIndexOf("/") + 1)) + ".pmml";
					
					trainedDataSet.show();
					
					PMML pmml = ConverterUtil.toPMML(trainedDataSet.schema(), trngModel);
					MetroJAXBUtil.marshalPMML(pmml, new FileOutputStream(new File(fileLocation), true));					
				}catch (JAXBException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			if (result)
				return filePathUrl + "/stages/" + customDirectories.get(1) + "/data/";
			else
				return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} 

		return null;
	}


}
