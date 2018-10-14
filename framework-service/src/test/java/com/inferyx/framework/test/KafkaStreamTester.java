package com.inferyx.framework.test;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import com.inferyx.framework.domain.StreamInput;

public class KafkaStreamTester {

	public KafkaStreamTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		String inputTopic = "test";
		
//		 Prepare kafka params
		StreamInput<Long, String> streamInput = new StreamInput<Long, String>(); 
		Properties streamsConfiguration = new Properties();
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG,
                "KafkaExampleConsumer");
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, 
				  Serdes.Long().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, 
				  Serdes.String().getClass().getName());
		streamsConfiguration.put(
				  StreamsConfig.STATE_DIR_CONFIG, 
				  "/tmp");
		streamInput.setProps(streamsConfiguration);
		
		StreamsBuilder builder = new StreamsBuilder();
		KStream<Long, String> textLines = builder.stream(inputTopic);
//		System.out.println("RECEIVED >>>>>>>>>> " + textLines.foreach((t,s) -> System.out.println(s)));
		textLines.foreach((t,s) -> System.out.println("RECEIVED >>>>>>>> " + s));
		
		
		KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
		streams.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		streams.close();
	}

}
