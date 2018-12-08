package com.inferyx.module;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class TomcatStarter {

	public TomcatStarter() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8080);

		Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());
		System.out.println("new File(\".\").getAbsolutePath() : " + new File("src/main/webapp").getAbsolutePath());

		XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
		applicationContext.setConfigLocations("file:src/main/resources/framework-predict.xml");
		Tomcat.addServlet(ctx, "framework", new org.springframework.web.servlet.DispatcherServlet(applicationContext));
		ctx.addServletMapping("/*", "framework");

		tomcat.start();
		tomcat.getServer().await();
	}

}
