/**
 * 
 */
package com.inferyx.framework.connector;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.StreamInput;

/**
 * @author joy
 *
 */
@Service
public class KafkaConnector<T, K> {
	
	@Resource
	ConcurrentHashMap<String, Consumer> kafkaConsumerMap;

	/**
	 * 
	 */
	public KafkaConnector() {
		// TODO Auto-generated constructor stub
	}
	
	public Consumer<T, K> createConsumer(Datasource ds, String topic, StreamInput<T, K> streamInput) {
		  if (kafkaConsumerMap.containsKey(topic)) {
			  return kafkaConsumerMap.get(topic);
		  }
		  Properties props = streamInput.getProps();
	      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
	                                  ds.getHost()+":"+ds.getPort());
	      /*props.put(ConsumerConfig.GROUP_ID_CONFIG,
	                                  "KafkaExampleConsumer");
	      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
	              LongDeserializer.class.getName());
	      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
	              StringDeserializer.class.getName());*/

	      // Create the consumer using props.
	      final Consumer<T, K> consumer =
	                                  new KafkaConsumer<>(props);

	      // Subscribe to the topic.
	      consumer.subscribe(Collections.singletonList(topic));
	      kafkaConsumerMap.put(topic, consumer);
	      return consumer;
	  }


}
