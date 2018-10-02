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

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.springframework.stereotype.Service;

import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;

import scala.reflect.ClassManifestFactory;

/**
 * @author joy
 *
 */
@Service
public class StreamToTableHelper implements Serializable {

	/**
	 * 
	 */
	public StreamToTableHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public void help(Map<String, Object> inputMap) throws IOException {
		JavaInputDStream<ConsumerRecord<Long, String>>stream = (JavaInputDStream<ConsumerRecord<Long, String>>) inputMap.get("STREAM");
		StructType schema = (StructType) inputMap.get("SCHEMA");
		IConnector connector = (IConnector) inputMap.get("CONNECTOR");
		ConnectionHolder conHolder = connector.getConnection();
		SparkSession session = (SparkSession) conHolder.getStmtObject();
//		SQLContext sqlContext = session.sqlContext();
		SaveMode saveMode = (SaveMode) inputMap.get("SAVEMODE");
		String url = (String) inputMap.get("URL");
		String tableName = (String) inputMap.get("TABLE_NAME");
		Properties connectionProperties = (Properties) inputMap.get("CONN_PROPS");
		stream.foreachRDD(new VoidFunction<JavaRDD<ConsumerRecord<Long, String>>>() {

			@Override
			public void call(JavaRDD<ConsumerRecord<Long, String>> rdd) throws Exception {
				final OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
				Broadcast<SparkSession> broadcastSession = rdd.context().broadcast(session, ClassManifestFactory.fromClass(SparkSession.class));
				rdd.foreachPartition(new VoidFunction<Iterator<ConsumerRecord<Long, String>>>() {
					@Override
					public void call(Iterator<ConsumerRecord<Long, String>> consumerRecords) {
						List<Row> rowsList = new ArrayList<>();
						consumerRecords.forEachRemaining(new Consumer<ConsumerRecord<Long, String>>() {

							@Override
							public void accept(ConsumerRecord<Long, String> t) {
								System.out.println("RECEIVED >>>> "+ t.key() + ":" + t.value());
								// Save in hive
								Row row = RowFactory.create(t.key(), t.value());
								rowsList.add(row);
							}
						});
						// Create dataframe
						Dataset<Row> df = broadcastSession.getValue().createDataFrame(rowsList, schema);
						System.out.println("Hive writing starts for this partition >>>>>>>>>>>>>>>>>>>> ");
						df.write().mode(saveMode).insertInto(tableName);
						System.out.println("Hive writing ends for this partition >>>>>>>>>>>>>>>>>>>> ");
//						df.write().mode(saveMode).jdbc(url, tableName, connectionProperties);
						OffsetRange o = offsetRanges[TaskContext.get().partitionId()];
						System.out.println("RECEIVED >>> " +
								o.topic() + " " + o.partition() + " " + o.fromOffset() + " " + o.untilOffset());
						
					}
				});
			}
		});

	}

}
