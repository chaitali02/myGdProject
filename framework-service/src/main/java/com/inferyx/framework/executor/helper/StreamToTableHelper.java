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
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.StreamInput;
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.executor.ExecContext;

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
		String dbName = targetDS.getDbname();
		String sessionParameters = targetDS.getSessionParameters();
		String url = Helper.genUrlByDatasource(targetDS);
		String driver = targetDS.getDriver(); 
		String userName = targetDS.getUsername();
		String password = targetDS.getPassword();
		String targetPath = streamInput.getTargetDir();
		String targetType = streamInput.getTargetType();
		String fileFormat = streamInput.getFileFormat();
		String ingestionType = streamInput.getIngestionType();
		String[] dataTypes =  new String[] {"integer", "varchar(70)", "varchar(70)"
				, "integer", "integer", "integer"};;
		String[] attributeList2 = null;
		if(!targetDS.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
			attributeList2 = new String[targetDP.getAttributes().size()];
			for(int i=0; i<targetDP.getAttributes().size(); i++) {
				attributeList2[i] = targetDP.getAttributes().get(i).getName();
			}
		}
		final String[] attributeList = attributeList2;
		
		stream.foreachRDD(new VoidFunction<JavaRDD<ConsumerRecord<T, K>>>() {
			@Override
			public void call(JavaRDD<ConsumerRecord<T, K>> rdd) throws Exception {
				System.out.println("DO SOMETHING ...");
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
//						df.printSchema();
//						df.show(false);
//						System.out.println("columns: "+df.columns());
						logger.info("Hive writing starts for this partition >>>>>>>>>>>>>>>>>>>> ");
						if(ingestionType.equalsIgnoreCase(IngestionType.STREAMTOTABLE.toString())) {
							//int i = 0;
							String[] dfColumns =  df.columns();							 
							for(int i=0; i<attributeList.length; i++){
								df = df.withColumnRenamed(dfColumns[i], attributeList[i]);
								df = df.withColumn(attributeList[i], df.col(attributeList[i]).cast(dataTypes[i]));
								//i++;
							} 
							persistDataframe(df, tableName, datasourceType, sessionParameters, url, driver, userName, password, saveMode, dbName);							
						} else if(ingestionType.equalsIgnoreCase(IngestionType.STREAMTOFILE.toString())) {
							if(fileFormat == null) {
								df.coalesce(1).write().mode(saveMode).format("csv").option("delimiter", ",").csv(targetPath);
							} else if(fileFormat.equalsIgnoreCase(FileType.CSV.toString())) {
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
	
	/**
	 * 
	 * @param stream
	 * @throws IOException
	 */
	public void stop (JavaInputDStream<ConsumerRecord<T, K>> stream) throws IOException {
		
		stream.foreachRDD(new VoidFunction<JavaRDD<ConsumerRecord<T, K>>>() {
			@Override
			public void call(JavaRDD<ConsumerRecord<T, K>> t) throws Exception {
				System.out.println("DO NOTHING ...");
			}
		});
	}
	
	public void persistDataframe(Dataset<Row> df, String tableName, String datasourceType, 
			String sessionParameters, String url, String driver, 
			String userName, String password, String saveMode,
			String dbName) {

//		logger.info("inside method persistDataframe");
//		df.show(false);
				
		if(datasourceType.equalsIgnoreCase("HIVE")
				|| datasourceType.equalsIgnoreCase("IMPALA")) {
				if(sessionParameters != null && !StringUtils.isBlank(sessionParameters)) {
					for(String sessionParam :sessionParameters.split(",")) {
						df.sparkSession().sql("SET "+sessionParam);
					}
				}
			df.sparkSession().sql("USE "+dbName);
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