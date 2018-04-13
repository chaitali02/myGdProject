package com.inferyx.framework.executor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.feature.RFormula;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.mllib.stat.KernelDensity;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.storage.StorageLevel;
import org.dmg.pmml.PMML;
import org.jpmml.model.JAXBUtil;
import org.jpmml.sparkml.ConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataFrameHolder;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.Instrument;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.Predict;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.ResultType;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.operator.DatasetOperator;
import com.inferyx.framework.operator.PredictMLOperator;
import com.inferyx.framework.operator.RuleOperator;
import com.inferyx.framework.operator.SimulateMLOperator;
import com.inferyx.framework.operator.SparkMonteCarloOperator;
import com.inferyx.framework.operator.TrainAndValidateOperator;
import com.inferyx.framework.reader.IReader;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.ModelExecServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;
import com.inferyx.framework.writer.IWriter;

import scala.collection.Iterator;
import scala.collection.Seq;

@Component
public class SparkExecutor implements IExecutor {

	/*
	 * @Autowired HiveContext hiveContext;
	 */
	@Autowired
	ConnectionFactory connectionFactory;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SQLContext sqlContext;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	MetadataUtil commonActivity;
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	SparkContext sparkContext;
	@Autowired
	ModelExecServiceImpl modelExecServiceImpl;
	@Autowired
	private DatasetOperator datasetOperator;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private ConnectionFactory connFactory;
	/*
	 * @Autowired HiveContext hiveContext;
	 */
	@Autowired
	private SparkSession sparkSession;
	@Autowired
	private MetadataUtil daoRegister;
	@Autowired
	private DataSourceFactory datasourceFactory;
	@Autowired
	private RuleOperator ruleOperator;
	@Autowired
	private PredictMLOperator predictMLOperator;
	@Autowired
	private SimulateMLOperator simulateMLOperator ;
	@Autowired
	private TrainAndValidateOperator trainAndValidateOperator ;
	@Autowired
	private JavaSparkContext javaSparkContext;
	

	static final Logger logger = Logger.getLogger(SparkExecutor.class);

	@Override
	public ResultSetHolder executeSql(String sql) throws IOException {
		return executeSql(sql, null);
	}

	public ResultSetHolder executeSql(String sql, String clientContext) throws IOException {
		logger.info(" Inside spark executor  for SQL : " + sql);
		ResultSetHolder rsHolder = new ResultSetHolder();
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			if (datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.FILE.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.HIVE.toString())
					|| datasource.getType().toLowerCase().equalsIgnoreCase(ExecContext.IMPALA.toString())) {
				IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
				ConnectionHolder conHolder = connector.getConnection();
				Object obj = conHolder.getStmtObject();
				if (obj instanceof SparkSession) {
					SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
					Dataset<Row> df = sparkSession.sql(sql);
					rsHolder.setDataFrame(df);
					rsHolder.setType(ResultType.dataframe);
				}
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.MYSQL.toString())) {
				Dataset<Row> dataFrame = sqlContext.read().format("jdbc")
						.option("spark.driver.extraClassPath", datasource.getDriver())
						.option("spark.executor.extraClassPath", datasource.getDriver())
						.option("driver", datasource.getDriver())
						.option("url",
								"jdbc:mysql://" + datasource.getHost() + ":" + datasource.getPort() + "/"
										+ datasource.getDbname())
						.option("user", datasource.getUsername()).option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ") as rule_table").load();
				rsHolder.setDataFrame(dataFrame);
				rsHolder.setType(ResultType.dataframe);
			} else if (datasource.getType().equalsIgnoreCase(ExecContext.ORACLE.toString())) {
				Dataset<Row> dataFrame = sqlContext.read().format("jdbc").option("driver", datasource.getDriver())
						.option("url",
								"jdbc:oracle:thin:@" + datasource.getHost() + ":" + datasource.getPort() + ":"
										+ datasource.getDbname())
						.option("user", datasource.getUsername()).option("password", datasource.getPassword())
						.option("dbtable", "(" + sql + ")  rule_table").load();
				rsHolder.setDataFrame(dataFrame);
				rsHolder.setType(ResultType.dataframe);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsHolder;
	}

	@Override
	public List<Map<String, Object>> executeAndFetch(String sql, String clientContext) throws IOException {
		logger.info(" Inside spark executor  for SQL : " + sql);
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		List<Map<String, Object>> data = new ArrayList<>();
		Object obj = conHolder.getStmtObject();
		Dataset<Row> dfSorted = null;
		if (obj instanceof SparkSession) {
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			dfSorted = sparkSession.sql(sql);
			Row[] rows = (Row[]) dfSorted.head(Integer.parseInt("" + dfSorted.count()));
			String[] columns = dfSorted.columns();
			for (Row row : rows) {
				Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
				for (String column : columns) {
					object.put(column, row.getAs(column));
				}
				data.add(object);
			}
			/*
			 * ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
			 * RequestContextHolder .getRequestAttributes(); if (requestAttributes != null)
			 * { HttpServletRequest request = requestAttributes.getRequest(); if (request !=
			 * null) { HttpSession session = request.getSession(); if (session != null) {
			 * ResultSetHolder rsHolder = new ResultSetHolder();
			 * rsHolder.setDataFrame(dfSorted); rsHolder.setType(ResultType.dataframe);
			 * session.setAttribute("rsHolder", rsHolder); } else
			 * logger.info("HttpSession is \"" + null + "\""); } else
			 * logger.info("HttpServletResponse is \"" + null + "\""); } else
			 * logger.info("ServletRequestAttributes requestAttributes is \"" + null +
			 * "\"");
			 */
		} // if SparkSession
		return data;
	}

	public ResultSetHolder executeAndRegister(String sql, String tableName, String clientContext) throws IOException {
		ResultSetHolder resHolder = executeSql(sql, clientContext);
		Dataset<Row> df = resHolder.getDataFrame();
		long countRows = df.count();
		resHolder.setCountRows(countRows);
		df.createOrReplaceGlobalTempView(tableName);
		registerTempTable(df, tableName);
		logger.info("temp table registered: " + tableName);
		return resHolder;
	}

	@Override
	/**
	 * Register table as dataframe
	 */
	public Boolean registerTempTable(Dataset<Row> dfTemp, String tableName) throws IOException {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			// dfTemp.registerTempTable("temp");
			// DataFrame df = hiveContext.sql("Select Row_Number() Over() as rownum,* from
			// temp");
			dfTemp.persist(StorageLevel.MEMORY_AND_DISK());
			sparkSession.sqlContext().registerDataFrameAsTable(dfTemp, tableName);
			// dfTemp.cache();
			// hiveContext.registerDataFrameAsTable(dfTemp, tableName);
		}
		return true;
	}

	/**
	 * Get all table names
	 * 
	 * @param clientContext
	 * @return
	 */
	public String[] getTableNames(String clientContext) {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = null;
		try {
			conHolder = connector.getConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			return ((SparkSession) obj).sqlContext().tableNames();
		} /*
			 * else if (obj instanceof LivyClient) { return
			 * livyClientImpl.getTableNames(clientContext); }
			 */
		return null;
	}

	/**
	 * Read a file
	 * 
	 * @param clientContext
	 * @param dp
	 * @param datastore
	 * @param hdfsInfo
	 * @param conObject
	 * @param ds
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws Exception
	 */
	public Dataset<Row> readFile(String clientContext, Datapod dp, DataStore datastore, HDFSInfo hdfsInfo,
			Object conObject, Datasource ds) throws InterruptedException, ExecutionException, Exception {
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		IReader iReader = dataSourceFactory.getDatapodReader(dp, commonActivity);
		String filePath = datastore.getLocation();
		String hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
		if (!filePath.contains(hdfsLocation)) {
			filePath = String.format("%s%s", hdfsLocation, filePath);
		}
		if (obj instanceof SparkSession) {
			@SuppressWarnings("unused")
			SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
			DataFrameHolder dfHolder = iReader.read(dp, datastore, hdfsInfo, obj, ds);
			return dfHolder.getDataframe();
		} /*
			 * else if (obj instanceof LivyClient) { LivyClient client = (LivyClient)
			 * conHolder.getStmtObject(); // Need to think of persist return
			 * livyClientImpl.readFile(clientContext, filePath); }
			 */
		return null;
	}

	@Override
	public ResultSetHolder executeAndPersist(String sql, String filePath, Datapod datapod, String saveMode,
			String clientContext) throws IOException {
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		Dataset<Row> df = null;
		IWriter datapodWriter = null;
		// HiveContext hiveContext = null;
		ResultSetHolder rsHolder = executeSql(sql);
		if (obj instanceof SparkSession) {
			df = rsHolder.getDataFrame();
			try {
				datapodWriter = dataSourceFactory.getDatapodWriter(datapod, commonActivity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException("Can not write data.");
			}
			datapodWriter.write(df, filePathUrl, datapod, saveMode);
		}
		return rsHolder;
	}

	@Override
	public ResultSetHolder executeRegisterAndPersist(String sql, String tableName, String filePath, Datapod datapod,
			String saveMode, String clientContext) throws IOException {
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
		ConnectionHolder conHolder = connector.getConnection();
		Object obj = conHolder.getStmtObject();
		Dataset<Row> df = null;
		IWriter datapodWriter = null;
		// HiveContext hiveContext = null;
		ResultSetHolder rsHolder = null;
		if (obj instanceof SparkSession) {
			// hiveContext = (HiveContext) conHolder.getStmtObject();
			rsHolder = executeAndRegister(sql, tableName, clientContext);
			df = rsHolder.getDataFrame();
			try {
				datapodWriter = dataSourceFactory.getDatapodWriter(datapod, commonActivity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException("Can not write data.");
			}
			datapodWriter.write(df, filePathUrl, datapod, saveMode);
		}
		return rsHolder;
	}

	@Override
	public ResultSetHolder registerDataFrameAsTable(ResultSetHolder rsHolder, String tableName) {
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();
			Object obj = conHolder.getStmtObject();
			SparkSession sparkSession = null;
			if (obj instanceof SparkSession) {
				sparkSession = (SparkSession) conHolder.getStmtObject();
				// sparkSession.registerDataFrameAsTable(rsHolder.getDataFrame(), tableName);
				sparkSession.sqlContext().registerDataFrameAsTable(rsHolder.getDataFrame(), tableName);
				rsHolder.setCountRows(rsHolder.getDataFrame().count());
			}
		} catch (NullPointerException e) {
			throw new RuntimeException("Failed to register Dataframe as Table.");
		} catch (Exception e) {
			throw new RuntimeException("Failed to register Dataframe as Table.");
		}
		return rsHolder;
	}

	/*
	 * @Override public ResultSetHolder persistFile(Datapod datapod, String
	 * filePath, DataFrame df ) { System.out.println("********Spark"); String
	 * saveMode = "append";
	 * 
	 * String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(),
	 * hdfsInfo.getSchemaPath(), filePath); IConnector connector =
	 * connectionFactory.getConnector("spark"); DataFrame df1 = null; IWriter
	 * datapodWriter = null;
	 * 
	 * HiveContext hiveContext = null; IWriter datapodWriter1 =
	 * dataSourceFactory.getDatapodWriter(datapod, commonActivity);
	 * datapodWriter1.write(df, filePathUrl, datapod, saveMode);
	 * 
	 * return null;
	 * 
	 * String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(),
	 * hdfsInfo.getSchemaPath()+"/", filePath); IConnector connector =
	 * connectionFactory.getConnector(ExecContext.spark.toString());
	 * ConnectionHolder conHolder; try { conHolder = connector.getConnection();
	 * Object obj = conHolder.getStmtObject(); //DataFrame df = null; IWriter
	 * datapodWriter = null; //HiveContext hiveContext = null; //ResultSetHolder
	 * rsHolder = null; if (obj instanceof HiveContext) {
	 * 
	 * datapodWriter = dataSourceFactory.getDatapodWriter(datapod, commonActivity);
	 * datapodWriter.write(df, filePathUrl, datapod, saveMode); } } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * return null;
	 * 
	 * }
	 */

	@Override
	public void registerDatapod(String filePath, String tableName, String clientContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean executeScript(String filePath, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object fetchAndTrainModel(Train train, Model model, String[] fieldArray, Algorithm algorithm,
			String trainName, String filePath, ParamMap paramMap, String clientContext) throws Exception {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		Dataset<Row> df = null;
		Object result = null;

		// Fetch data/dataframe/dataset
		if (train.getSource().getRef().getType().toString().equals(MetaType.datapod.toString())) {
			Datapod datapod = new Datapod();
			if (train.getSource().getRef().getVersion() != null) {
				// datapod =
				// datapodServiceImpl.findOneByUuidAndVersion(datapodUUID,datapodVersion);
				datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
						train.getSource().getRef().getVersion(), MetaType.datapod.toString());
			} else {
				// datapod = datapodServiceImpl.findLatestByUuid(datapodUUID);
				datapod = (Datapod) commonServiceImpl.getLatestByUuid(train.getSource().getRef().getUuid(),
						MetaType.datapod.toString());
			}
			DataStore datastore = dataStoreServiceImpl.findLatestByMeta(datapod.getUuid(), datapod.getVersion());
			if (datastore == null) {
				logger.error("Datastore is not available for this datapod");
				throw new Exception();
			}
			IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
			IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
			ConnectionHolder conHolder = conn.getConnection();
			Object obj = conHolder.getStmtObject();
			DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			df = dataFrameHolder.getDataframe();
		} else if (train.getSource().getRef().getType().toString().equals(MetaType.dataset.toString())) {
			DataSet dataset = new DataSet();
			if (train.getSource().getRef().getVersion() != null) {
				// dataset = datasetServiceImpl.findOneByUuidAndVersion(uuid,version);
				dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
						train.getSource().getRef().getVersion(), MetaType.dataset.toString());
			} else {
				// dataset = datasetServiceImpl.findLatestByUuid(uuid);
				dataset = (DataSet) commonServiceImpl.getLatestByUuid(train.getSource().getRef().getUuid(),
						MetaType.dataset.toString());
			}

			// List<Map<String, Object>> data = new ArrayList<>();
			String sql = datasetOperator.generateSql(dataset, null, null, new HashSet<>(), null, Mode.BATCH);
			ResultSetHolder rsHolder = executeSql(sql);
			df = rsHolder.getDataFrame();

		} else if (train.getSource().getRef().getType().toString().equals(MetaType.rule.toString())) {
			Rule rule = new Rule();
			if (train.getSource().getRef().getVersion() != null) {
				rule = (Rule) commonServiceImpl.getOneByUuidAndVersion(train.getSource().getRef().getUuid(),
						train.getSource().getRef().getVersion(), MetaType.rule.toString());
			} else {
				rule = (Rule) commonServiceImpl.getLatestByUuid(train.getSource().getRef().getUuid(),
						MetaType.rule.toString());
			}

			String sql = ruleOperator.generateSql(rule, null, null, new HashSet<>(), null, Mode.BATCH);
			ResultSetHolder rsHolder = executeSql(sql);
			df = rsHolder.getDataFrame();
		}

		// train model
		VectorAssembler va = new VectorAssembler();
		Dataset<Row> training = null;

		if (algorithm.getTrainName().contains("LinearRegression")
				|| algorithm.getTrainName().contains("LogisticRegression")) {
			va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
			Dataset<Row> trainingTmp = va.transform(df);
			// training = trainingTmp.withColumnRenamed(fieldArray[0],"label");
			training = trainingTmp.withColumn("label", trainingTmp.col(model.getLabel()).cast("Double")).select("label",
					"features");

			logger.info("DataFrame count for training: " + training.count());

		} /*
			 * else if (algorithm.getTrainName().contains("DecisionTreeClassifier") ||
			 * algorithm.getTrainName().contains("NaiveBayes") ||
			 * algorithm.getTrainName().contains("RandomForestClassifier") ||
			 * algorithm.getTrainName().contains("LinearSVC")) { va = (new
			 * VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
			 * Dataset<Row> trainingTmp = va.transform(df); training =
			 * trainingTmp.withColumnRenamed(fieldArray[0], "label"); }
			 */else {
			va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
			training = va.transform(df);
		}
		training.printSchema();
		logger.info("tableName--Algo:" + trainName);
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), Helper.getPropertyValue("framework.model.train.path"), filePath);
		// SparkMLOperator sparkMLOperator = new SparkMLOperator();
		//sparkMLOperator.setParamSetServiceImpl(paramSetServiceImpl);
		//sparkMLOperator.setSparkContext(sparkContext);
		//sparkMLOperator.setFilePathUrl(filePathUrl);
		//sparkMLOperator.setModel(model);
		//sparkMLOperator.setCommonServiceImpl(commonServiceImpl);
		//sparkMLOperator.setAlgorithm(algorithm);
		//sparkMLOperator.setModelExecServiceImpl(modelExecServiceImpl);
		//sparkMLOperator.setFilePath(filePath);
		//sparkMLOperator.setConnectionFactory(connectionFactory);
		//sparkMLOperator.setExecFactory(execFactory);
		//sparkMLOperator.setHelper(helper);
		//sparkMLOperator.setTrain(train);
		//sparkMLOperator.setHdfsInfo(hdfsInfo);
		result = trainAndValidateOperator.execute(train, model, algorithm, algorithm.getTrainName(), algorithm.getModelName(), df, va, paramMap, filePathUrl, filePath);
		return result;
	}

	@Override
	public String fetchAndCreatePMML(DataStore datastore, Datapod datapod, String clientContext) throws Exception {
		Dataset<Row> df = null;
		IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
		ConnectionHolder conHolder = conn.getConnection();

		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			df = dataFrameHolder.getDataframe();
		}
		RFormula formula = new RFormula().setFormula("Species ~ .");
		DecisionTreeClassifier classifier = new DecisionTreeClassifier().setLabelCol(formula.getLabelCol())
				.setFeaturesCol(formula.getFeaturesCol());
		Pipeline pipeline = new Pipeline().setStages(new PipelineStage[] { formula, classifier });
		PipelineModel pipelineModel = pipeline.fit(df);

		PMML pmml = ConverterUtil.toPMML(df.schema(), pipelineModel);
		JAXBUtil.marshalPMML(pmml, new StreamResult(System.out));
		return null;
	}

	@Override
	public List<String> fetchModelResults(DataStore datastore, Datapod datapod, String clientContext) throws Exception {
		List<String> strList = new ArrayList<>();
		Dataset<Row> df = null;
		if (datastore == null) {
			logger.error("Datastore is not available for this datapod");
			throw new Exception();
		}
		IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
		ConnectionHolder conHolder = conn.getConnection();

		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			df = dataFrameHolder.getDataframe();
		}

		df.printSchema();
		for (Row row : df.javaRDD().collect()) {
			strList.add(row.toString());
		}
		return strList;
	}

	@Override
	public long loadAndRegister(Load load, String filePath, String dagExecVer, String loadExecVer,
			String datapodTableName, Datapod datapod, String clientContext) throws Exception {
		Dataset<Row> dfTmp = sparkSession.read().format("com.databricks.spark.csv").option("inferSchema", "true")
				.option("header", "true").load(load.getSource().getValue());
		long count = dfTmp.count();
		// sparkSession.registerDataFrameAsTable(dfTmp, "dfLoadTemp");
		sparkSession.sqlContext().registerDataFrameAsTable(dfTmp, "dfLoadTemp");
		ResultSetHolder rsHolder = executeSql(
				"SELECT *, " + ((dagExecVer == null) ? loadExecVer : dagExecVer) + " AS version FROM dfLoadTemp",
				clientContext);
		Dataset<Row> dfTask = rsHolder.getDataFrame();

		// dfTask = hiveContext.sql("select *, "+ dagExecVer + " as version from
		// dfLoadTemp").coalesce(4);
		dfTask.cache();
		// sparkSession.registerDataFrameAsTable(dfTask, datapodTableName);
		sparkSession.sqlContext().registerDataFrameAsTable(dfTask, datapodTableName);

		logger.info("Going to datapodWriter");
		dfTask.printSchema();
		// Datapod datapod = (Datapod) daoRegister.getRefObject(new
		// MetaIdentifier(MetaType.datapod, datapodKey.getUUID(),
		// datapodKey.getVersion()));
		IWriter datapodWriter = datasourceFactory.getDatapodWriter(datapod, daoRegister);
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		datapodWriter.write(dfTask, filePathUrl, datapod, SaveMode.Overwrite.toString());
		return count;
	}

	@Override
	public void registerDatapod(String tableName, Datapod datapod, DataStore dataStore, ExecContext execContext,
			String clientContext) throws IOException {
		Datasource datasource = null;
		try {
			datasource = commonServiceImpl.getDatasourceByApp();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {
			e.printStackTrace();
		}
		IConnector connection = connFactory.getConnector(datasource.getType().toLowerCase());
		IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);

		ConnectionHolder conHolder = connection.getConnection();
		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession && !execContext.equals(ExecContext.livy_spark)) {
			DataFrameHolder dataFrameHolder = iReader.read(datapod, dataStore, hdfsInfo, obj, datasource);
			Dataset<Row> df = dataFrameHolder.getDataframe();
			tableName = dataFrameHolder.getTableName();
			String[] tablenameList = ((SparkSession) obj).sqlContext().tableNames();
			boolean tableFound = false;
			if (tablenameList != null && tablenameList.length > 0) {
				for (String tname : tablenameList) {
					if (tname.equals(tableName)) {
						tableFound = true;
						break;
					}
				}
			}
			if (!tableFound) {
				df.persist(StorageLevel.MEMORY_AND_DISK());
				// df.cache();
				SparkSession sparkSession = (SparkSession) conHolder.getStmtObject();
				// sparkSession.registerDataFrameAsTable(df, tableName);
				df.createOrReplaceGlobalTempView(tableName);
				sparkSession.sqlContext().registerDataFrameAsTable(df, tableName);
				// hiveContext.registerDataFrameAsTable(df, tableName);
				logger.info("datapodRegister: Registering datapod " + tableName);
				// hiveContext.registerDataFrameAsTable(df, tableName);
				df.printSchema();
			}
		}
	}

	@Override
	public List<Attribute> fetchAttributeList(String csvFileName, String parquetDir, boolean flag,
			boolean writeToParquet, String clientContext) throws Exception {
		Dataset<Row> df = sparkSession.read().format("com.databricks.spark.csv").option("inferSchema", "true")
				.option("header", "true").load(csvFileName);
		df.printSchema();
		StructType st = df.schema();
		Seq<StructField> seqFields = st.thisCollection();
		Iterator<StructField> iter = st.iterator();
		List<Attribute> attributes = new ArrayList<Attribute>();
		int i = 0;
		while (iter.hasNext()) {
			StructField sf = iter.next();
			Attribute attr1 = new Attribute();
			attr1.setAttributeId(i++);
			attr1.setType(sf.dataType().typeName());
			attr1.setName(sf.name());
			attr1.setDesc(sf.name());
			attr1.setDispName(sf.name());
			attr1.setActive("Y");
			attributes.add(attr1);

		}
		if (flag) {
			Attribute attr2 = new Attribute();
			attr2.setAttributeId(i++);
			attr2.setType("Integer");
			attr2.setName("version");
			attr2.setDesc("version");
			attr2.setDispName("version");
			attr2.setActive("Y");
			attributes.add(attr2);
		}
		if (writeToParquet) {
			df.write().parquet(parquetDir);

		}
		logger.info("Length of seq:" + seqFields.length());
		return attributes;
	}

	@Override
	public List<Object> submitQuery(String sql, int rowLimit, String format, String header, String clientContext)
			throws IOException {
		List<Object> data = new ArrayList<>();
		String finalCsv = null;
		String columnHeaderStr = null;
		StringBuilder columnName = new StringBuilder();

		// Execute SQL
		logger.info("inside SparkExecutor for the quiery: " + sql);
		Dataset<Row> df = sparkSession.sql(sql).coalesce(10);
		df.persist(StorageLevel.MEMORY_AND_DISK());
		// df.cache();

		Row[] rows;
		if (rowLimit == 0) {
			rows = (Row[]) df.head(20);
		} else {
			rows = (Row[]) df.limit(rowLimit).collect();
		}
		if (format == null) {
			format = "";
		}

		// Create header row
		String[] columns = df.columns();
		for (String columnHeader : columns) {
			columnName.append(columnHeader);
			columnName.append(",");
		}
		if (columnName.charAt(columnName.length() - 1) == ',') {
			columnHeaderStr = columnName.substring(0, columnName.length() - 1);
		}
		if (format != null & format.equalsIgnoreCase("CSV")) {
			if (header.equalsIgnoreCase("Y")) {
				data.add(columnHeaderStr);
			}
		}

		// Generate output format
		for (Row row : rows) {
			java.util.TreeMap<String, Object> object = new TreeMap<String, Object>();
			if (format.equals("") || !format.equalsIgnoreCase("csv")) {
				for (String column : columns) {
					object.put(column, row.getAs(column));
				}
				data.add(object);
			}
			if (format != null & format.equalsIgnoreCase("CSV")) {
				StringBuilder sbr = new StringBuilder();
				for (String column : columns) {
					Object csvRow = row.getAs(column);
					sbr.append(csvRow);
					sbr.append(",");
				}

				if (sbr.charAt(sbr.length() - 1) == ',') {
					finalCsv = sbr.substring(0, sbr.length() - 1);
				}
				data.add(finalCsv);
			}
		}
		return data;
	}

	@Override
	public Object predictModel(Predict predict, String[] fieldArray, Algorithm algorithm, String filePath,
			String tableName, String clientContext) throws Exception {

		try {
			// fieldArray = modelExecServiceImpl.getAttributeNames(predict);

			MetaIdentifierHolder modelHolder = predict.getDependsOn();
			MetaIdentifierHolder sourceHolder = predict.getSource();
			MetaIdentifierHolder targetHolder = predict.getTarget();

			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelHolder.getRef().getUuid(),
					modelHolder.getRef().getVersion(), modelHolder.getRef().getType().toString());
			Object source = (Object) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
					sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
			Datapod target = null;
			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod))
				target = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(),
						targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());

			Dataset<Row> df = null;
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			if (source instanceof Datapod) {
				Datapod datapod = (Datapod) source;
				DataStore datastore = dataStoreServiceImpl.findLatestByMeta(datapod.getUuid(), datapod.getVersion());
				if (datastore == null) {
					logger.error("Datastore is not available for this datapod");
					throw new Exception();
				}
				IReader iReader = dataSourceFactory.getDatapodReader(datapod, commonActivity);
				IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
				ConnectionHolder conHolder = conn.getConnection();
				Object obj = conHolder.getStmtObject();
				DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
				df = dataFrameHolder.getDataframe();
			} else if (source instanceof Dataset) {
				DataSet dataset = (DataSet) source;
				if (predict.getSource().getRef().getVersion() != null) {
					dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(predict.getSource().getRef().getUuid(),
							predict.getSource().getRef().getVersion(), MetaType.dataset.toString());
				} else {
					dataset = (DataSet) commonServiceImpl.getLatestByUuid(predict.getSource().getRef().getUuid(),
							MetaType.dataset.toString());
				}

				String sql = datasetOperator.generateSql(dataset, null, null, new HashSet<>(), null, Mode.BATCH);
				ResultSetHolder rsHolder = executeSql(sql);
				df = rsHolder.getDataFrame();

			} else if (source instanceof Rule) {
				Rule rule = (Rule) source;
				if (predict.getSource().getRef().getVersion() != null) {
					rule = (Rule) commonServiceImpl.getOneByUuidAndVersion(predict.getSource().getRef().getUuid(),
							predict.getSource().getRef().getVersion(), MetaType.rule.toString());
				} else {
					rule = (Rule) commonServiceImpl.getLatestByUuid(predict.getSource().getRef().getUuid(),
							MetaType.rule.toString());
				}

				String sql = ruleOperator.generateSql(rule, null, null, new HashSet<>(), null, Mode.BATCH);
				ResultSetHolder rsHolder = executeSql(sql);
				df = rsHolder.getDataFrame();
			}

			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), Helper.getPropertyValue("framework.model.predict.path"), filePath);

			if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
				sparkSession.sqlContext().registerDataFrameAsTable(df, tableName.replaceAll("-", "_"));
				String sql = predictMLOperator.parse(predict, model, df, fieldArray, tableName, filePathUrl, filePathUrl);				
				return predictMLOperator.execute(sql, tableName, filePathUrl, filePath, commonServiceImpl.getApp().getUuid());
			} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
				VectorAssembler va = new VectorAssembler();
				Dataset<Row> transformedDf = null;

				if (algorithm.getTrainName().contains("LinearRegression")
						|| algorithm.getTrainName().contains("LogisticRegression")) {
					va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
					Dataset<Row> trainingTmp = va.transform(df);
					transformedDf = trainingTmp.withColumn("label", trainingTmp.col(model.getLabel()).cast("Double"))
							.select("label", "features");

					logger.info("DataFrame count for training: " + transformedDf.count());

				} else {
					va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
					transformedDf = va.transform(df);
				}

				TrainExec latestTrainExec = modelExecServiceImpl.getLatestTrainExecByModel(model.getUuid(),
						model.getVersion());
				if (latestTrainExec == null)
					throw new Exception("Executed model not found.");

				transformedDf.printSchema();
				return predictMLOperator.execute(predict, model, algorithm, target, transformedDf, fieldArray, latestTrainExec, targetHolder.getRef().getType().toString(), tableName, filePathUrl, filePath, commonServiceImpl.getApp().getUuid());
			} else 
				return null;
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getCause().getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Object simulateModel(Simulate simulate, String[] fieldArray, Algorithm algorithm, String filePath,
			String tableName, String clientContext) throws Exception {
		try {
			MetaIdentifierHolder modelHolder = simulate.getDependsOn();
			MetaIdentifierHolder targetHolder = simulate.getTarget();
			Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(modelHolder.getRef().getUuid(),
					modelHolder.getRef().getVersion(), modelHolder.getRef().getType().toString());
			Datapod target = null;
			if (targetHolder.getRef().getType() != null && targetHolder.getRef().getType().equals(MetaType.datapod))
				target = (Datapod) commonServiceImpl.getOneByUuidAndVersion(targetHolder.getRef().getUuid(),
						targetHolder.getRef().getVersion(), targetHolder.getRef().getType().toString());

			// fieldArray = modelExecServiceImpl.getAttributeNames(model);

			String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), Helper.getPropertyValue("framework.model.simulate.path"), filePath);

			
			tableName = tableName.replaceAll("-", "_");
						
			if(model.getDependsOn().getRef().getType().equals(MetaType.formula)) {
				Distribution distribution = (Distribution) daoRegister.getRefObject(simulate.getDistributionTypeInfo().getRef());
				int seed = Integer.parseInt(""+simulate.getSeed());
				int numTrials = simulate.getNumIterations();
				Dataset<Row> dfTemp = executeDistribution(distribution, numTrials, 100, simulate.getFactorMeanInfo(), simulate.getFactorCovarientInfo());
				
				Dataset<Row> df = dfTemp.withColumnRenamed("value", fieldArray[0]);
				
				VectorAssembler va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
				Dataset<Row> assembledDf = va.transform(df);
				assembledDf.show();
				sparkSession.sqlContext().registerDataFrameAsTable(assembledDf, tableName);
				String sql = simulateMLOperator.parse(simulate, model, assembledDf, fieldArray, tableName, filePathUrl, filePath);
			return simulateMLOperator.execute(sql, filePathUrl, filePath, commonServiceImpl.getApp().getUuid());
			} else if(model.getDependsOn().getRef().getType().equals(MetaType.algorithm)) {
				
				TrainExec latestTrainExec = modelExecServiceImpl.getLatestTrainExecByModel(model.getUuid(),
					model.getVersion());
				if (latestTrainExec == null)
					throw new Exception("Executed model not found.");
				
				Dataset<Row> df = simulateMLOperator.generateDataframe(simulate, model, tableName);
				VectorAssembler va = (new VectorAssembler().setInputCols(fieldArray).setOutputCol("features"));
				Dataset<Row> assembledDf = va.transform(df);
				assembledDf.show();
				
				return simulateMLOperator.execute(simulate, model, algorithm, target, latestTrainExec, fieldArray,
						targetHolder.getRef().getType().toString(), tableName, filePathUrl, filePath, assembledDf, clientContext);
			} else 
				return null;			
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getCause().getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<Map<String, Object>> fetchResults(DataStore datastore, Datapod datapod, String clientContext)
			throws Exception {
		List<Map<String, Object>> data = new ArrayList<>();
		Dataset<Row> df = null;
		if (datastore == null) {
			logger.error("Datastore is not available for this datapod");
			throw new Exception();
		}
		IReader iReader = dataSourceFactory.getDatapodReader();
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IConnector conn = connFactory.getConnector(datasource.getType().toLowerCase());
		ConnectionHolder conHolder = conn.getConnection();

		Object obj = conHolder.getStmtObject();
		if (obj instanceof SparkSession) {
			DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore, hdfsInfo, obj, datasource);
			df = dataFrameHolder.getDataframe();
		}
		df.show();
		String[] columns = df.columns();
		for (Row row : df.javaRDD().collect()) {
			Map<String, Object> object = new LinkedHashMap<String, Object>(columns.length);
			for (String column : columns) {
				object.put(column, (row.getAs(column)==null?"":row.getAs(column).toString()) );
			}
			data.add(object);
		}

		return data;
	}
	
	public Dataset<Row> executeDistribution(Distribution distribution, int numTrials, long seed, MetaIdentifierHolder factorMeansInfo, MetaIdentifierHolder factorCovariancesInfo) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, NullPointerException, ParseException {
		
		Row dataset = null;
		
		ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(distribution.getParamList().getRef().getUuid(), distribution.getParamList().getRef().getVersion(), distribution.getParamList().getRef().getType().toString());
		
		List<Param> params = paramList.getParams();
		
		int parallelism = Integer.parseInt(params.get(0).getParamValue());
		String str = params.get(1).getParamValue();
		String[] splits = str.split(",");
		List<Double> datasetList = new ArrayList<>();
		for(String split : splits)
			datasetList.add(Double.parseDouble(split));
		dataset = RowFactory.create(datasetList.toArray());
		
		Datapod factorMeanDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(factorMeansInfo.getRef().getUuid(), factorMeansInfo.getRef().getVersion(), factorMeansInfo.getRef().getType().toString());
		DataStore factorMeanDs = dataStoreServiceImpl.findDataStoreByMeta(factorMeanDp.getUuid(), factorMeanDp.getVersion());
		
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		
		IReader datapodReader = datasourceFactory.getDatapodReader(factorMeanDp, commonActivity);
		DataFrameHolder meansHolder = datapodReader.read(factorMeanDp, factorMeanDs, hdfsInfo, sparkSession, datasource);
		Dataset<Row> meansDf = meansHolder.getDataframe();
		meansDf.show();
		
		List<String> meanColList = new ArrayList<>();
		for(int i=0; i<factorMeanDp.getAttributes().size(); i++) {
			if(i>0)
				meanColList.add(factorMeanDp.getAttributes().get(i).getName());
		}			
		
		List<Double> meansValList = new ArrayList<>();
		for(Row row : meansDf.collectAsList()) {
				for(String col : meanColList)
					meansValList.add(row.getAs(col));
		}		
		double[] factorMeans = ArrayUtils.toPrimitive(meansValList.toArray(new Double[meansValList.size()]));
		//double[] factorMeans = sparkMonteCarloOperator.readMeans(factorMeanDs.getLocation());
		
		Datapod factorCovarianceDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(factorCovariancesInfo.getRef().getUuid(), factorCovariancesInfo.getRef().getVersion(), factorCovariancesInfo.getRef().getType().toString());
		DataStore factorCovarianceDs = dataStoreServiceImpl.findDataStoreByMeta(factorCovarianceDp.getUuid(), factorCovarianceDp.getVersion());
		meansHolder = datapodReader.read(factorCovarianceDp, factorCovarianceDs, hdfsInfo, sparkSession, datasource);
		Dataset<Row> covarsDf = meansHolder.getDataframe();
		covarsDf.show();
		
		List<String> covarColList = new ArrayList<>();
		for(int i=0; i<factorCovarianceDp.getAttributes().size(); i++) {
			if(i>0)
				covarColList.add(factorCovarianceDp.getAttributes().get(i).getName());
		}	
		
		List<double[]> covarsRowList = new ArrayList<>();
		for(Row row : covarsDf.collectAsList()) {
			List<Double> covarsValList = new ArrayList<>();
				for(String col : covarColList)
					covarsValList.add(row.getAs(col));
				covarsRowList.add(ArrayUtils.toPrimitive(covarsValList.toArray(new Double[covarsValList.size()])));
		}
		
		double[][] factorCovariances = covarsRowList.stream().map(lineStrArray -> ArrayUtils.toPrimitive(lineStrArray)).toArray(double[][]::new);
		//double[][] factorCovariances = sparkMonteCarloOperator.readCovariances(factorCovarianceDs.getLocation());
		
		Broadcast<Row> broadcastInstruments = javaSparkContext.broadcast(dataset);
		//List<Long> seeds = LongStream.range(seed, seed + parallelism).boxed().collect(Collectors.toList());
		//JavaRDD<Long> seedRdd = javaSparkContext.parallelize(seeds, parallelism);
		Double[] trialValues = simulateMLOperator.trialValues(seed, distribution.getClassName(), numTrials/parallelism, broadcastInstruments.value(), 
				factorMeans, factorCovariances);
		Dataset<Row> df = sparkSession.sqlContext().createDataset(Arrays.asList(trialValues), Encoders.DOUBLE()).toDF();
		df.show();
		
		return df;
	}
	
}