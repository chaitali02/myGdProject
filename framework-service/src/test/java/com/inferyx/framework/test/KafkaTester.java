package com.inferyx.framework.test;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.KafkaExecutor;


public class KafkaTester {

	public KafkaTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("framework-servlet.xml");
		KafkaExecutor kafkaExecutor = appContext.getBean(KafkaExecutor.class);
		String topic = "test";
		/*Datasource ds = getDatasource();
		printTopics(kafkaExecutor, ds);*/
		Datasource ds = getBrokerDatasource();
		Consumer<Long, String> consumer = kafkaExecutor.createConsumer(ds, topic);
		for (int i = 0; i < 10; i++) {
			runProducer(100, topic);
			kafkaExecutor.consumer(consumer, ds, topic);
		}
	}
	
	public static Datasource getDatasource() {
		Datasource ds = new Datasource();
		ds.setHost("localhost");
		ds.setPort("2181");
		return ds;
	}
	
	public static void printTopics(KafkaExecutor kafkaExecutor, Datasource ds) throws IOException {
		kafkaExecutor.getTopics(ds).forEach(System.out::println);
	}
	
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
	
	public static Datasource getBrokerDatasource() {
		Datasource ds = new Datasource();
		ds.setHost("localhost");
		ds.setPort("9092");
		return ds;
	}
	
	/*static void runConsumer(Datasource ds, String topic) {
		
	}*/

}
