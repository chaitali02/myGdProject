/**
 * 
 */
package com.inferyx.framework.test;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inferyx.framework.executor.KafkaExecutor;
import com.inferyx.framework.executor.SparkStreamingExecutor;

/**
 * @author joy
 *
 */
public class SSStopper {

	/**
	 * 
	 */
	public SSStopper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
			ApplicationContext appContext = new ClassPathXmlApplicationContext("framework-servlet.xml");
			
			SparkStreamingExecutor sparkStreamingExecutor = appContext.getBean(SparkStreamingExecutor.class);
			sparkStreamingExecutor.stop("test");
	}

}
