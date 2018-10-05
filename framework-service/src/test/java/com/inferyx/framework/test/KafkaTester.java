package com.inferyx.framework.test;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.StreamInput;
import com.inferyx.framework.executor.KafkaExecutor;
import com.inferyx.framework.executor.SparkStreamingExecutor;


public class KafkaTester implements Serializable {

	public KafkaTester() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("framework-servlet.xml");
		KafkaExecutor kafkaExecutor = appContext.getBean(KafkaExecutor.class);
		
		SparkStreamingExecutor sparkStreamingExecutor = appContext.getBean(SparkStreamingExecutor.class);
		
		String topic = "test";
		// Test case 1 - PRINT TOPICS - START 
		/*Datasource ds = getDatasource();
		printTopics(kafkaExecutor, ds);*/
		// Test case 1 - PRINT TOPICS - END 
		
		// Test case 2 - PRINT KAFKA CONSUMER - START 
		Datasource ds = getBrokerDatasource();
		/*Consumer<Long, String> consumer = kafkaExecutor.createConsumer(ds, topic);
		for (int i = 0; i < 10; i++) {
			runProducer(100, topic);
			kafkaExecutor.consumer(consumer, ds, topic);
		}*/
		// Test case 2 - PRINT KAFKA CONSUMER - END
		
		// Test case 3 - PRINT SPARK STREAMING CONSUMER - START
		ds = getBrokerDatasource();
		
		// Prepare kafka params
		StreamInput<Long, String> streamInput = new StreamInput<Long, String>(); 
		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("key.deserializer", LongDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
		kafkaParams.put("auto.offset.reset", "latest");
		kafkaParams.put("enable.auto.commit", false);
		
		// Prepare run Params 
		Map<String, Object> runParams = new HashMap<>();
		runParams.put("KAFKA_PARAMS", kafkaParams);
		streamInput.setRunParams(runParams);
		
		// API 1 - Define the streaming context
		JavaInputDStream<ConsumerRecord<Long, String>> stream = sparkStreamingExecutor.stream(ds, topic, streamInput);
		// API 2 - Write the logic to execute
		sparkStreamingExecutor.write(topic, new String[] {"key", "value"}, new DataType[] {DataTypes.LongType, DataTypes.StringType}, getDatapod(), SaveMode.Append, stream);
				
		new Thread(new Runnable() {public void run() {
			for (int i = 0; i < 10; i++) {
				try {
					runProducer(100, topic);
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}}).start();
		
		// API 3 - Start executing
		sparkStreamingExecutor.start(topic);
		
		// Provide a lag
		Thread.sleep(5000);
		
		// API 4 - Stop if still running
		sparkStreamingExecutor.stop(topic);
		
		
//		streamingDataset.foreachRDD(t -> System.out.println("RECEIVED >>> " + t));
		/*while (streamingDataset.isStreaming()) {
			System.out.println("Count : " + streamingDataset.count());
		}*/
		// Test case 3 - PRINT SPARK STREAMING CONSUMER - END
	}
	
	private static Datapod getDatapod () {
		Datapod datapod = new Datapod();
		datapod.setName("stream_data");
		return datapod;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Datasource getDatasource() {
		Datasource ds = new Datasource();
		ds.setHost("localhost");
		ds.setPort("2181");
		return ds;
	}
	
	/**
	 * 
	 * @param kafkaExecutor
	 * @param ds
	 * @throws IOException
	 */
	public static void printTopics(KafkaExecutor kafkaExecutor, Datasource ds) throws IOException {
		kafkaExecutor.getTopics(ds).forEach(System.out::println);
	}
	
	/**
	 * 
	 * @return
	 */
	private static Producer<Long, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                            "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                        LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                                    StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
	
	/**
	 * 
	 * @param sendMessageCount
	 * @param topic
	 * @throws Exception
	 */
	static void runProducer(final int sendMessageCount, String topic) throws Exception {
	      final Producer<Long, String> producer = createProducer();
	      long time = System.currentTimeMillis();

	      try {
	          for (long index = time; index < time + sendMessageCount; index++) {
	              final ProducerRecord<Long, String> record =
	                      new ProducerRecord<>(topic, index,
	                                  "Hello Mom " + index);

	              RecordMetadata metadata = producer.send(record).get();

	              long elapsedTime = System.currentTimeMillis() - time;
	              System.out.printf("sent record(key=%s value=%s) " +
	                              "meta(partition=%d, offset=%d) time=%d\n",
	                      record.key(), record.value(), metadata.partition(),
	                      metadata.offset(), elapsedTime);

	          }
	      } finally {
	          producer.flush();
	          producer.close();
	      }
	  }
	
	/**
	 * 
	 * @return
	 */
	public static Datasource getBrokerDatasource() {
		Datasource ds = new Datasource();
		ds.setHost("localhost");
		ds.setPort("9092");
		return ds;
	}
	
	/**
	 * 
	 * @param ds
	 * @param topic
	 */
	static void runConsumer(Datasource ds, String topic) {
		
	}

}
