/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.KafkaConnector;
import com.inferyx.framework.connector.ZKConnector;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.StreamInput;

/**
 * @author joy
 *
 */
@Service
public class KafkaExecutor<T, K> {
	
	@Autowired
	private ZKConnector zkConnector;
	@Autowired
	private KafkaConnector kafkaConnector;
	@Autowired
	private ConcurrentHashMap<String, Consumer> kafkaConsumerMap;

	/**
	 * 
	 */
	public KafkaExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	public List<String> getTopics(Datasource ds) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		ConnectionHolder connHolder = zkConnector.getConnection(ds);
		List<String> topics = null;
		if (!(connHolder.getConObject() instanceof ZooKeeper)) {
			throw new IOException("Framework exception : Streaming topics not available");
		}
		ZooKeeper zk = (ZooKeeper) connHolder.getConObject();
        try {
			topics = zk.getChildren("/brokers/topics", false);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			throw new IOException("Framework Exception : Could not extract streaming topics", e);
		}
        return topics;
	}
	
	public Consumer<T, K> stream(Datasource ds, String topic, StreamInput<T, K> streamInput) {
		Consumer<T, K> consumer = kafkaConnector.createConsumer(ds, topic, streamInput);
		return consumer;
	  }
	
	
	public void write (String topic, 
			String []fieldNames, 
			DataType []dataTypes, 
			Datapod datapod, 
			SaveMode saveMode, 
			JavaInputDStream<ConsumerRecord<T, K>> stream) throws IOException {
		// not implemented here
	}
	
	/**
	 * 
	 * @param ds
	 * @param topic
	 */
	public void read(Datasource ds, String topic) {
		
		Consumer consumer = kafkaConsumerMap.get(topic);
		
		final int giveUp = 100;   
		int noRecordsCount = 0;
		 while (true) {
	            final ConsumerRecords<Long, String> consumerRecords =
	                    consumer.poll(100);

	            if (consumerRecords.count()==0) {
	            	break;
/*	                if (noRecordsCount > giveUp) break;
	                else continue;*/
	            }

	            consumerRecords.forEach(record -> {
	                System.out.printf("RECEIVED >>>>>>>>> Consumer Record:(%d, %s, %d, %d)\n",
	                        record.key(), record.value(),
	                        record.partition(), record.offset());
	            });

	            consumer.commitAsync();
	        }
//	        consumer.close();
	        System.out.println("DONE");
	}
	
	/**
	 * 
	 * @param topic
	 */
	public void start(String topic) {
		// Not implemented here
	}
	
	/**
	 * Close consumer
	 * @param topic
	 */
	public void stop(String topic) {
		Consumer consumer = kafkaConsumerMap.get(topic);
		consumer.close();
	}

}
