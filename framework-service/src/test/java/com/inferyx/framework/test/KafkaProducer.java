package com.inferyx.framework.test;

import java.util.Properties;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducer {

	public KafkaProducer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		new Thread(new Runnable() {public void run() {
			for (int i = 0; i < 10; i++) {
				Producer<Long, String> producer = null;
				try {
					producer = createProducer();
					runProducer(100, producer, "test", "Hello Mom ");
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
        return new org.apache.kafka.clients.producer.KafkaProducer<>(props);
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


}
