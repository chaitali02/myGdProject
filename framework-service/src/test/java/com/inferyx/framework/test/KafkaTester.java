package com.inferyx.framework.test;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
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
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.StreamInput;
import com.inferyx.framework.enums.IngestionType;
import com.inferyx.framework.enums.SaveMode;
import com.inferyx.framework.executor.KafkaExecutor;
import com.inferyx.framework.executor.SparkStreamingExecutor;


public class KafkaTester implements Serializable {

	public KafkaTester() {
//		 TODO Auto-generated constructor stub
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
//		 Test case 1 - PRINT TOPICS - START 
		/*Datasource ds = getDatasource();
		printTopics(kafkaExecutor, ds);*/
//		 Test case 1 - PRINT TOPICS - END 
		
//		 Test case 2 - PRINT KAFKA CONSUMER - START 
		Datasource ds = getBrokerDatasource();
		
//		 Prepare kafka params
		/*StreamInput<Long, String> streamInput = new StreamInput<Long, String>();
		Properties props = new Properties();
		props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
		LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
		StringDeserializer.class.getName());
		streamInput.setProps(props);
		Consumer<Long, String> consumer = kafkaExecutor.stream(ds, topic, streamInput);
		for (int i = 0; i < 10; i++) {
			runProducer(100, topic);
			kafkaExecutor.read(ds, topic);
		}*/
//		 Test case 2 - PRINT KAFKA CONSUMER - END
		
//		 Test case 3 - PRINT SPARK STREAMING CONSUMER - START
		ds = getBrokerDatasource();
		
//		 Prepare kafka params
		StreamInput<Long, String> streamInput = new StreamInput<Long, String>(); 
		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("key.deserializer", LongDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
		kafkaParams.put("auto.offset.reset", "latest");
		kafkaParams.put("enable.auto.commit", false);
		
//		 Prepare run Params 
		Map<String, Object> runParams = new HashMap<>();
		runParams.put("KAFKA_PARAMS", kafkaParams);
		streamInput.setRunParams(runParams);
		
		// First Dstream - START
//		 API 1 - Define the streaming context
		streamInput.setTopicName(topic);
		streamInput.setSaveMode(SaveMode.APPEND.toString());
//		streamInput.setTargetDS(getMySqlDatasource());
		streamInput.setTargetDS(getHiveDatasource());
		streamInput.setTargetDir("/user/stream/file1");
//		streamInput.setTargetType("MYSQL");
		streamInput.setTargetType("FILE");
		streamInput.setFileFormat("CSV");
//		streamInput.setTargetTableName("stream_dump");
		streamInput.setIngestionType(IngestionType.STREAMTOFILE.toString());
		JavaInputDStream<ConsumerRecord<Long, String>> stream = sparkStreamingExecutor.stream(ds, streamInput);
//		 API 2 - Write the logic to execute
		sparkStreamingExecutor.write(streamInput, stream);
		// First Dstream - END
		
		// Second Dstream - START
//		 API 1 - Define the streaming context
		kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream_1");
		runParams.put("KAFKA_PARAMS", kafkaParams);
		streamInput.setTopicName("test2");
		streamInput.setSaveMode(SaveMode.APPEND.toString());
		streamInput.setTargetDS(getHiveDatasource());
		streamInput.setTargetDir("/user/stream/file2/");
		streamInput.setTargetType("FILE");
		streamInput.setFileFormat("CSV");
		streamInput.setIngestionType(IngestionType.STREAMTOFILE.toString());
		stream = sparkStreamingExecutor.stream(ds, streamInput);
//		 API 2 - Write the logic to execute
		sparkStreamingExecutor.write(streamInput, stream);
		// Second Dstream - END
		
		//		 API 3 - Start executing
		sparkStreamingExecutor.start(ds);
				
		new Thread(new Runnable() {public void run() {
			for (int i = 0; i < 10; i++) {
				Producer<Long, String> producer = null;
				try {
					producer = createProducer();
					runProducer(100, producer, topic, "Hello Mom ");
					runProducer(100, producer, "test2", "Hello Bro ");
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
			          producer.flush();
			          producer.close();
			      }
			}
		}}).start();
		
//		 Provide a lag
		Thread.sleep(5000);
		
//		 API 4 - Stop if still running
		sparkStreamingExecutor.stop(ds);
		
//		 Test case 3 - PRINT SPARK STREAMING CONSUMER - END
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
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static void printTopics(KafkaExecutor kafkaExecutor, Datasource ds) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
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
	static void runProducer(final int sendMessageCount, Producer<Long, String> producer, String topic, String prefix) throws Exception {
	      
	      long time = System.currentTimeMillis();

	      try {
	          for (long index = time; index < time + sendMessageCount; index++) {
	              final ProducerRecord<Long, String> record =
	                      new ProducerRecord<>(topic, index,
	                    		  	prefix + index);

	              RecordMetadata metadata = producer.send(record).get();

	              long elapsedTime = System.currentTimeMillis() - time;
	              System.out.printf("sent record(key=%s value=%s) " +
	                              "meta(partition=%d, offset=%d) time=%d\n",
	                      record.key(), record.value(), metadata.partition(),
	                      metadata.offset(), elapsedTime);

	          }
	      } catch(Exception e) {
	    	  e.printStackTrace();
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
	
	public static Datasource getHiveDatasource() {
		Datasource ds = new Datasource();
		ds.setType("HIVE");
		return ds;
	}
	
	
	public static Datasource getMySqlDatasource() {
		Datasource ds = new Datasource();
		ds.setType("MYSQL");
		ds.setDbname("framework");
		ds.setDriver("com.mysql.jdbc.Driver");
		ds.setHost("localhost");
		ds.setSid("framework");
		ds.setPort("3306");
		ds.setUsername("inferyx");
		ds.setPassword("inferyx");
		ds.setPath("/user/hive/warehouse/framework");
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
