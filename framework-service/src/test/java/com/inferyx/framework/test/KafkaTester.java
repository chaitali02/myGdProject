package com.inferyx.framework.test;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inferyx.framework.domain.Datasource;
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
		
		JavaInputDStream<ConsumerRecord<Long, String>> stream = sparkStreamingExecutor.stream(ds, topic);
		stream.foreachRDD(new VoidFunction<JavaRDD<ConsumerRecord<Long, String>>>() {

			@Override
			public void call(JavaRDD<ConsumerRecord<Long, String>> rdd) throws Exception {
				final OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
				rdd.foreachPartition(new VoidFunction<Iterator<ConsumerRecord<Long, String>>>() {
					@Override
					public void call(Iterator<ConsumerRecord<Long, String>> consumerRecords) {
						consumerRecords.forEachRemaining(new Consumer<ConsumerRecord<Long, String>>() {

							@Override
							public void accept(ConsumerRecord<Long, String> t) {
								System.out.println("RECEIVED >>>> "+ t.key() + ":" + t.value());
							}
						});
						OffsetRange o = offsetRanges[TaskContext.get().partitionId()];
						System.out.println("RECEIVED >>> " +
								o.topic() + " " + o.partition() + " " + o.fromOffset() + " " + o.untilOffset());
						
					}
				});
			}
		});
		
		new Thread(new Runnable() {public void run() {
			for (int i = 0; i < 10; i++) {
				try {
					runProducer(100, topic);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}}).start();
		
		sparkStreamingExecutor.start(topic);
		
		
//		streamingDataset.foreachRDD(t -> System.out.println("RECEIVED >>> " + t));
		/*while (streamingDataset.isStreaming()) {
			System.out.println("Count : " + streamingDataset.count());
		}*/
		// Test case 3 - PRINT SPARK STREAMING CONSUMER - END
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
