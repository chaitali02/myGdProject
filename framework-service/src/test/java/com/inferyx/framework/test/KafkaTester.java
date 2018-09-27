package com.inferyx.framework.test;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.KafkaExecutor;

public class KafkaTester {

	public KafkaTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		Datasource ds = getDatasource();
		ApplicationContext appContext = new ClassPathXmlApplicationContext("framework-servlet.xml");
		KafkaExecutor kafkaExecutor = appContext.getBean(KafkaExecutor.class);
		printTopics(kafkaExecutor, ds);
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

}
