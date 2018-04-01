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
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;
import org.dmg.pmml.PMML;
import org.jpmml.model.MetroJAXBUtil;
import org.jpmml.sparkml.ConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;
import com.inferyx.framework.writer.IWriter;

public class SparkMLOperator implements IModelOperator {

	@Autowired
	private ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private SparkContext sparkContext;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private SQLContext sqlContext;
	@Autowired
	private MetadataUtil daoRegister;
	@Autowired
	private DataSourceFactory datasourceFactory;
	@Autowired
	private SparkSession sparkSession;
	@Autowired
	private HDFSInfo hdfsInfo;

	static final Logger LOGGER = Logger.getLogger(SparkMLOperator.class);

	/********************** UNUSED **********************/
	// @Override
	/*
	 * public boolean train(String className, String modelName, Dataset<Row> df,
	 * ParamMap paramMap) { try {
	 *//*** START change for trainAndValidate ******/
	/*
	 * float trngSplit = 0.70f; long count = df.count(); int trngRowNum = (int)
	 * (count * trngSplit); Dataset<Row> trngDf = df.limit(trngRowNum); Dataset<Row>
	 * validateDf = df.except(trngDf); Dataset<Row> trainedDataSet = null;
	 *//*** END change for trainAndValidate ******/
	/*
	 * 
	 * Class<?> dynamicClass = Class.forName(className); Object obj = null; if (null
	 * != paramMap) { Class<?>[] paramDF = new Class[2]; paramDF[0] = Dataset.class;
	 * paramDF[1] = ParamMap.class; Method method = dynamicClass.getMethod("fit",
	 * paramDF); obj = method.invoke(dynamicClass.newInstance(), trngDf, paramMap);
	 * } else { Class<?>[] paramDF = new Class[1]; paramDF[0] = Dataset.class;
	 * Method method = dynamicClass.getMethod("fit", paramDF); obj =
	 * method.invoke(dynamicClass.newInstance(), trngDf); }
	 *//*** START change for trainAndValidate ******/

	/*
	 * // Validation starts
	 * 
	 * Method validateMethod = obj.getClass().getMethod("transform"); trainedDataSet
	 * = (Dataset<Row>)validateMethod.invoke(obj, validateDf);
	 * 
	 *//*** END change for trainAndValidate ******//*
													 * 
													 * return save(modelName, obj, sparkContext, filePathUrl);
													 * 
													 * // return method.getReturnType(); } catch (ClassNotFoundException
													 * e) { e.printStackTrace(); return false; } catch
													 * (IllegalAccessException e) { e.printStackTrace(); return false; }
													 * catch (IllegalArgumentException e) { e.printStackTrace(); return
													 * false; } catch (InvocationTargetException e) {
													 * e.printStackTrace(); return false; } catch
													 * (InstantiationException e) { e.printStackTrace(); return false; }
													 * catch (NoSuchMethodException e) { e.printStackTrace(); return
													 * false; } catch (SecurityException e) { e.printStackTrace();
													 * return false; } }
													 */

	public List<ParamMap> getParamMap(ExecParams execParams, String className) throws JsonProcessingException {
		List<ParamMap> paramMapList = new ArrayList<>();
		if (null != execParams) {
			for (ParamSetHolder paramSetHolder : execParams.getParamInfo()) {
				List<ParamListHolder> paramListHolder = paramSetServiceImpl.getParamListHolder(paramSetHolder);
				ParamMap paramMap = new ParamMap();
				for (@SuppressWarnings("unused")
				ParamListHolder plh : paramListHolder) {
					if (className.contains("KMeans")) {
						KMeans kmeans = new KMeans();
						paramMap.put(kmeans.maxIter().w(10), kmeans.k().w(8), kmeans.predictionCol().w("test"));
						paramMapList.add(paramMap);

					}
				}
			}
		}
		return paramMapList;
	}

	public boolean save(String className, Object obj, SparkContext sparkContext, String path) {

		Class<?> dynamicClass = obj.getClass();
		// dynamicClass = dynamicClass.cast(obj);

		Class<?>[] paramSave = new Class[1];
		// paramSave[0] = SparkContext.class;
		paramSave[0] = String.class;

		/*
		 * //KMeansModel kmeansModel = obj; if(obj. instanceof KMeansModel){ KMeansModel
		 * kmeansModel = (KMeansModel)obj; kmeansModel.save(path);
		 * 
		 * try { kmeansModel.save(path); return true; } catch (IOException e) {
		 * e.printStackTrace(); } }
		 */
		Method m1 = null;
		try {
			m1 = dynamicClass.getMethod("save", paramSave);
			try {
				m1.invoke(obj, path);
				return true;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				//e.printStackTrace();
				String expMessage = e.getCause().getMessage();// getMessage();
				if (expMessage.contains("use write().overwrite().save(path) for Java")) {
					//KMeansModel kmeansModel = (KMeansModel) obj;
					try {
						Object mlWriter = obj.getClass().getMethod("write").invoke(obj);
						Object mlWriter_2 = mlWriter.getClass().getMethod("overwrite").invoke(mlWriter);
						mlWriter_2.getClass().getMethod("save", String.class).invoke(mlWriter_2, path);
						//kmeansModel.write().overwrite().save(path);
						return true;
					} catch (IllegalAccessException 
							| IllegalArgumentException 
							| InvocationTargetException ioExp) {
						ioExp.printStackTrace();
					}catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		} catch (NoSuchMethodException 
				| SecurityException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public Object trainAndValidate(Train train, Model model, Algorithm algorithm,String modelClassName, String modelName, Dataset<Row> df, VectorAssembler va,
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
			boolean result = save(modelName, trngModel, sparkContext, filePathUrl);
			if (algorithm.getSavePmml().equalsIgnoreCase("Y")) {
				try {
					LOGGER.info("trainedDataSet schema : " + trainedDataSet.schema());
					if(filePathUrl.contains("file://"))
						filePathUrl = filePathUrl.replaceAll("file://", "");
					if(filePathUrl.contains("hdfs://"))
						filePathUrl = filePathUrl.replaceAll("hdfs://", "");
					
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public Object predict(Predict predict, Model model, Algorithm algorithm, Datapod targetDp, Dataset<Row> df, String[] fieldArray, TrainExec latestTrainExec,
			VectorAssembler va, String targetType, String tableName, String filePathUrl, String filePath, String clientContext) throws Exception {
			/*if(predict != null) {
				filePathUrl = filePathUrl + "/predict/"+df.hashCode();
			} else
				filePathUrl = filePathUrl + "/simulate/"+df.hashCode();*/

			Vector features = null; // extract feature column as vector from df
			String modelName = algorithm.getModelName();
			Class<?> modelClass = Class.forName(modelName);

			MetaIdentifierHolder datastoreHolder = latestTrainExec.getResult();
			DataStore dataStore = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreHolder.getRef().getUuid(),
					datastoreHolder.getRef().getVersion(), datastoreHolder.getRef().getType().toString());
			if (dataStore == null)
				throw new NullPointerException("No datastore available");
			String location = dataStore.getLocation();

			/*if (location.contains("file://"))
				location = location.replaceAll("file://", "");*/
			if (location.contains("/data"))
				location = location.replaceAll("/data", "");
			
			location = hdfsInfo.getHdfsURL() + location;

			Object trainedModel = modelClass.getMethod("load", String.class).invoke(modelClass, location);
			Dataset<Row> predictionDf = (Dataset<Row>) trainedModel.getClass().getMethod("transform", Dataset.class)
					.invoke(trainedModel, df);
			predictionDf.show();

			/*
			 * List<Row> rowList = df.collectAsList(); for(int i=0; i < rowList.size(); i++)
			 * { int featureIndex = rowList.get(i).fieldIndex("features"); features =
			 * (Vector) rowList.get(i).get(featureIndex); features = features.compressed();
			 * Double prediction = (Double) trainedModel.getClass().getMethod("predict",
			 * Vector.class).invoke(trainedModel, features);
			 * logger.info("predict result("+i+"): "+prediction); }
			 */
			String uid = (String) trainedModel.getClass().getMethod("uid").invoke(trainedModel);

			if (targetType.equalsIgnoreCase(MetaType.datapod.toString())) {
				Datasource datasource = commonServiceImpl.getDatasourceByApp();

				List<Attribute> attributeList = targetDp.getAttributes();
				Attribute version = new Attribute();
				version.setActive("Y");
				version.setAttributeId(attributeList.size());
				version.setDesc("version");
				version.setDispName("version");
				version.setName("version");
				version.setType("String");
				version.setPartition("N");
				attributeList.add(version);
				targetDp.setAttributes(attributeList);

				df.createOrReplaceGlobalTempView("tempPredictResult");
				IConnector connector = connectionFactory.getConnector(datasource.getType().toLowerCase());
				ConnectionHolder conHolder = connector.getConnection();
				if (conHolder.getStmtObject() instanceof SparkSession) {
					SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
					predictionDf.persist(StorageLevel.MEMORY_AND_DISK());
					sparkSession.sqlContext().registerDataFrameAsTable(predictionDf, "tempPredictResult");
				}
				IExecutor exec = execFactory.getExecutor(datasource.getType());
				String columns = "";
				for (String col : predictionDf.columns())
					columns = columns.concat(col).concat(" AS ").concat(col).concat(",");
				columns = columns.substring(0, columns.length() - 2);
				String sql = "SELECT " + columns + " FROM " + "tempPredictResult";
				ResultSetHolder rsHolder = exec.executeSql(sql, commonServiceImpl.getApp().getUuid());

				Dataset<Row> dfTask = rsHolder.getDataFrame();
				dfTask.cache();

				sqlContext.registerDataFrameAsTable(dfTask, tableName.replaceAll("-", "_"));

				dfTask.printSchema();
				IWriter datapodWriter = datasourceFactory.getDatapodWriter(targetDp, daoRegister);
				datapodWriter.write(dfTask, filePathUrl, targetDp, SaveMode.Append.toString());
				return filePathUrl;
			} else {
				if (save(modelName, trainedModel, sparkContext, filePathUrl))
					return filePathUrl + "/data";
				else
					return null;
			}	
	}

	@Override
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
			return predict(null, model, algorithm, targetDp, assembledDf, fieldArray, latestTrainExec, va, targetType, tableName, filePathUrl, filePath, clientContext);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}
}