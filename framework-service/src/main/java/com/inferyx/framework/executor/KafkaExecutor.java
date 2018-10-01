/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.kafka.common.serialization.LongDeserializer;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.KafkaConnector;
import com.inferyx.framework.connector.ZKConnector;
import com.inferyx.framework.domain.Datasource;

/**
 * @author joy
 *
 */
@Service
public class KafkaExecutor {
	
	@Autowired
	private ZKConnector zkConnector;
	@Autowired
	private KafkaConnector kafkaConnector;

	/**
	 * 
	 */
	public KafkaExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	public List<String> getTopics(Datasource ds) throws IOException {
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
	
	public Consumer<Long, String> createConsumer(Datasource ds, String topic) {
		Consumer<Long, String> consumer = kafkaConnector.createConsumer(ds, topic);
		return consumer;
	  }
	
	
	public void consumer(Consumer consumer, Datasource ds, String topic) {
		
		final int giveUp = 100;   int noRecordsCount = 0;
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

}
