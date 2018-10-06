/**
 * 
 */
package com.inferyx.framework.executor.helper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.StreamInput;

import scala.reflect.ClassManifestFactory;

/**
 * @author joy
 *
 */
@Service
public class StreamToTableHelper<T, K> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7365327320185201373L;

	static Logger logger = Logger.getLogger(StreamToTableHelper.class);
	
	/**
	 * 
	 */
	public StreamToTableHelper() {
		// TODO Auto-generated constructor stub
	}
	
//	public void help(JavaInputDStream<ConsumerRecord<T, K>> stream, StreamInput<T, K>  streamInput) throws IOException {
//		StructType schema = (StructType) inputMap.get("SCHEMA");
//		IConnector connector = (IConnector) inputMap.get("CONNECTOR");
//		ConnectionHolder conHolder = connector.getConnection();
//		SparkSession session = (SparkSession) conHolder.getStmtObject();
////		SQLContext sqlContext = session.sqlContext();
//		SaveMode saveMode = (SaveMode) inputMap.get("SAVEMODE");
//		String url = (String) inputMap.get("URL");
//		String tableName = (String) inputMap.get("TABLE_NAME");
//		Properties connectionProperties = (Properties) inputMap.get("CONN_PROPS");
//		stream.foreachRDD(new VoidFunction<JavaRDD<ConsumerRecord<T, K>>>() {
//
//			@Override
//			public void call(JavaRDD<ConsumerRecord<T, K>> rdd) throws Exception {
//				Broadcast<SparkSession> broadcastSession = rdd.context().broadcast(session, ClassManifestFactory.fromClass(SparkSession.class));
//				rdd.foreachPartition(new VoidFunction<Iterator<ConsumerRecord<T, K>>>() {
//					@Override
//					public void call(Iterator<ConsumerRecord<T, K>> consumerRecords) {
//						List<Row> rowsList = new ArrayList<>();
//						consumerRecords.forEachRemaining(new Consumer<ConsumerRecord<T, K>>() {
//
//							@Override
//							public void accept(ConsumerRecord<T, K> t) {
//								System.out.println("RECEIVED >>>> "+ t.key() + ":" + t.value() + ":" + t.topic() + ":" + t.partition() + ":" + t.offset() + ":" + t.timestamp() );
//								// Save in hive
//								Row row = RowFactory.create(t.key(), t.value(), t.topic(), t.partition(), t.offset(), t.timestamp());
//								rowsList.add(row);
//							}
//						});
//						// Create dataframe
//						Dataset<Row> df = broadcastSession.getValue().createDataFrame(rowsList, schema);
//						System.out.println("Hive writing starts for this partition >>>>>>>>>>>>>>>>>>>> ");
////						df.printSchema();
////						df.show(false);
//						df.write().mode(saveMode).insertInto(tableName);
//						System.out.println("Hive writing ends for this partition >>>>>>>>>>>>>>>>>>>> ");
////						df.write().mode(saveMode).jdbc(url, tableName, connectionProperties);
////						OffsetRange o = offsetRanges[TaskContext.get().partitionId()];
////						System.out.println("RECEIVED >>> " +
////								o.topic() + " " + o.partition() + " " + o.fromOffset() + " " + o.untilOffset());
//						
//					}
//				});
//			}
//		});
//
//	}

	public void help(JavaInputDStream<ConsumerRecord<T, K>> stream, StreamInput<T, K>  streamInput) throws IOException {
		Map<String, Object> runParams = streamInput.getRunParams();
		StructType schema = (StructType) runParams.get("SCHEMA");
		IConnector connector = streamInput.getConnector();
		ConnectionHolder conHolder = connector.getConnection();
		SparkSession session = (SparkSession) conHolder.getStmtObject();
//		SQLContext sqlContext = session.sqlContext();
		String saveMode = streamInput.getSaveMode();
//		String url = (String) inputMap.get("URL");
		String tableName = (String) streamInput.getTargetTableName();
		Properties connectionProperties = (Properties) runParams.get("CONN_PROPS");
		Datasource targetDS = streamInput.getTargetDS();
		Datapod targetDP = streamInput.getTargetDP();
		String datasourceType = targetDS.getType();
		String sessionParameters = targetDS.getSessionParameters();
		String url = Helper.genUrlByDatasource(targetDS);
		String driver = targetDS.getDriver(); 
		String userName = targetDS.getUsername();
		String password = targetDS.getPassword();
		String targetPath = streamInput.getTargetDir();
		String targetType = streamInput.getTargetType();
		String fileFormat = streamInput.getFileFormat();
		stream.foreachRDD(new VoidFunction<JavaRDD<ConsumerRecord<T, K>>>() {

			@Override
			public void call(JavaRDD<ConsumerRecord<T, K>> rdd) throws Exception {
				Broadcast<SparkSession> broadcastSession = rdd.context().broadcast(session, ClassManifestFactory.fromClass(SparkSession.class));
				rdd.foreachPartition(new VoidFunction<Iterator<ConsumerRecord<T, K>>>() {
					@Override
					public void call(Iterator<ConsumerRecord<T, K>> consumerRecords) {
						List<Row> rowsList = new ArrayList<>();
						consumerRecords.forEachRemaining(new Consumer<ConsumerRecord<T, K>>() {

							@Override
							public void accept(ConsumerRecord<T, K> t) {
								logger.info("RECEIVED >>>> "+ t.key() + ":" + t.value() + ":" + t.topic() + ":" + t.partition() + ":" + t.offset() + ":" + t.timestamp() );
								// Save in hive
								Row row = RowFactory.create(t.key(), t.value(), t.topic(), t.partition(), t.offset(), t.timestamp());
								rowsList.add(row);
							}
						});
						// Create dataframe
						Dataset<Row> df = broadcastSession.getValue().createDataFrame(rowsList, schema);
						
						logger.info("Hive writing starts for this partition >>>>>>>>>>>>>>>>>>>> ");
						if(!targetType.equalsIgnoreCase("FILE")) {
							persistDataframe(df, tableName, datasourceType, sessionParameters, url, driver, userName, password, saveMode);							
						} else {
							if(fileFormat == null) {
								df.coalesce(1).write().mode(saveMode).format("csv").option("delimiter", ",").csv(targetPath);
							} else if(fileFormat.equalsIgnoreCase(FileType.CSV.toString())) {
								System.out.println(df + ":" + targetPath + ":" + saveMode);
								df.coalesce(1).write().mode(saveMode).option("delimiter", ",").csv(targetPath);
							} else if(fileFormat.equalsIgnoreCase(FileType.TSV.toString())) {
								df.coalesce(1).write().mode(saveMode).option("delimiter", "\t").csv(targetPath);
							} else if(fileFormat.equalsIgnoreCase(FileType.PSV.toString())) {
								df.coalesce(1).write().mode(saveMode).option("delimiter", "|").csv(targetPath);
							} else if(fileFormat.equalsIgnoreCase(FileType.PARQUET.toString())) {
//								rsHolder = registerAndPersistDataframe(rsHolder, datapod, "append", targetPath, tableName, false);
							}
						}
//						df.printSchema();
//						df.show(false);
//						df.write().mode(saveMode).insertInto(tableName);
						logger.info("Hive writing ends for this partition >>>>>>>>>>>>>>>>>>>> ");
//						df.write().mode(saveMode).jdbc(url, tableName, connectionProperties);
//						OffsetRange o = offsetRanges[TaskContext.get().partitionId()];
//						System.out.println("RECEIVED >>> " +
//								o.topic() + " " + o.partition() + " " + o.fromOffset() + " " + o.untilOffset());
						
					}
				});
			}
		});
	}
	
	public void persistDataframe(Dataset<Row> df, String tableName, String datasourceType, 
			String sessionParameters, String url, String driver, 
			String userName, String password, String saveMode) {

//		logger.info("inside method persistDataframe");
//		df.show(false);
				
		if(datasourceType.equalsIgnoreCase("HIVE")
				|| datasourceType.equalsIgnoreCase("IMPALA")) {
				if(sessionParameters != null && !StringUtils.isBlank(sessionParameters)) {
					for(String sessionParam :sessionParameters.split(",")) {
						df.sparkSession().sql("SET "+sessionParam);
					}
				}

			df.write().mode(saveMode).insertInto(tableName);
		} else {
			Properties connectionProperties = new Properties();
			connectionProperties.put("driver", driver);
			connectionProperties.put("user", userName);
			connectionProperties.put("password", password);
//			if(partitionColList.size() > 0) {
//				df.write().mode(SaveMode.Append)/*.partitionBy(partitionColList.toArray(new String[partitionColList.size()]))*/.jdbc(url, rsHolder.getTableName(), connectionProperties);
//			} else {
				df.write().mode(saveMode).jdbc(url, tableName, connectionProperties);
//			}
		}	
	}
}
