/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.helper.StreamToTableHelper;
import com.inferyx.framework.factory.ConnectionFactory;

/**
 * @author joy
 *
 */
@Service
public class SparkStreamingExecutor {

	@Autowired
	ConnectionFactory connectionFactory;
	@Resource
	ConcurrentHashMap<String, JavaStreamingContext> streamingCtxMap;
	@Autowired
	StreamToTableHelper streamToTableHelper;

	/**
	 * 
	 */
	public SparkStreamingExecutor() {
		// TODO Auto-generated constructor stub
	}

	public JavaInputDStream<ConsumerRecord<Long, String>> stream(Datasource ds, String topic) {
		// Dataset<Row> lines = null;
		JavaReceiverInputDStream<String> lines = null;
		JavaInputDStream<ConsumerRecord<Long, String>> stream = null;
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
			ConnectionHolder conHolder = connector.getConnection();

			Map<String, Object> kafkaParams = new HashMap<>();
			kafkaParams.put("bootstrap.servers", ds.getHost() + ":" + ds.getPort());
			kafkaParams.put("key.deserializer", LongDeserializer.class);
			kafkaParams.put("value.deserializer", StringDeserializer.class);
			kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
			kafkaParams.put("auto.offset.reset", "latest");
			kafkaParams.put("enable.auto.commit", false);

			Collection<String> topics = Arrays.asList(topic);
			JavaStreamingContext streamingContext = new JavaStreamingContext(
					JavaSparkContext
							.fromSparkContext(((SparkSession) conHolder.getStmtObject()).sparkContext().getOrCreate()),
					Durations.seconds(5));

			stream = KafkaUtils.createDirectStream(
					streamingContext, LocationStrategies.PreferConsistent(),
					ConsumerStrategies.<Long, String>Subscribe(topics, kafkaParams));

			streamingCtxMap.put(topic, streamingContext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stream;
	}
	
	
	/**
	 * 
	 * @param fieldNames
	 * @param dataTypes
	 * @return
	 */
	public StructType createStruct(String []fieldNames, DataType []dataTypes) {
		if (fieldNames == null || dataTypes == null || fieldNames.length == 0 || dataTypes.length != fieldNames.length) {
			return null;
		}
		List<StructField> structFieldsList = new ArrayList<>();
		for (int i = 0; i < fieldNames.length; i++) {
			structFieldsList.add(new StructField(fieldNames[i], dataTypes[i], true, Metadata.empty()));
		}
		return new StructType(structFieldsList.toArray(new StructField[fieldNames.length]));
	}
	
	public void execute (String topic, 
							String []fieldNames, 
							DataType []dataTypes, 
							Datapod datapod, 
							SaveMode saveMode, 
							JavaInputDStream<ConsumerRecord<Long, String>> stream) throws IOException {
		Map<String, Object> inputMap = new HashMap<>();
		IConnector connector = connectionFactory.getConnector(ExecContext.spark.toString());
//		ConnectionHolder conHolder = connector.getConnection();
		inputMap.put("STREAM", stream);
		inputMap.put("SCHEMA", createStruct(fieldNames, dataTypes));
		inputMap.put("CONNECTOR", connector);
		inputMap.put("SAVEMODE", saveMode);
//		inputMap.put("URL", url);
		inputMap.put("TABLE_NAME", datapod.getName());
		streamToTableHelper.help(inputMap);
	}
	
	public void start(String topic) {
		JavaStreamingContext streamingContext = streamingCtxMap.get(topic);
		streamingContext.start();
		try {
			streamingContext.awaitTermination();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
