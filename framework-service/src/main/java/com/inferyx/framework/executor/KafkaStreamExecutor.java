/**
 * 
 */
package com.inferyx.framework.executor;

import java.io.IOException;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.domain.StreamInput;
import com.inferyx.framework.executor.helper.KafkaStreamToTableHelper;

/**
 * @author joy
 *
 */
public class KafkaStreamExecutor<T, K> {
	
	@Autowired
	private KafkaStreamToTableHelper kafkaStreamToTableHelper;
	
	static Logger logger = Logger.getLogger(KafkaStreamExecutor.class);

	/**
	 * 
	 */
	public KafkaStreamExecutor() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param ds
	 * @param streamInput
	 */
	
	/*************************Unused**********************************/
	/*public void stream(Datasource ds, StreamInput<T, K> streamInput, Map map) {
		
		Properties streamsConfiguration = streamInput.getProps();
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
				ds.getHost() + ":" + ds.getPort());
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG,
                "KafkaExampleConsumer");
		streamsConfiguration.put(
				  StreamsConfig.STATE_DIR_CONFIG, 
				  "/tmp");
		streamInput.setProps(streamsConfiguration);
		
		StreamsBuilder builder = new StreamsBuilder();
		KStream<T, K> lines = builder.stream(streamInput.getTopicName());
		
		try {
			write(streamInput, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
		streams.start();
	}*/
	
	public void write(StreamInput streamInput, KStream<T, K> lines) throws IOException {
		kafkaStreamToTableHelper.help(streamInput, lines);
	}

}
