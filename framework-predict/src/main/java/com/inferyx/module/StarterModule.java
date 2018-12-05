package com.inferyx.module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath*:framework-batch.xml")
public class StarterModule {

	public StarterModule() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	  * A main method to start this application.
	  **/
	  public static void main(String[] args) {
	    SpringApplication.run(StarterModule.class, args);
	  }
	  
}
