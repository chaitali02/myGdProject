package com.inferyx.module;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class TomcatStarter {

	public TomcatStarter() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(Integer.parseInt(args[0]));

		Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());
		System.out.println("new File(\".\").getAbsolutePath() : " + new File(".").getAbsolutePath());

		XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
		applicationContext.setConfigLocations("file:src/main/resources/framework-predict.xml");
		Tomcat.addServlet(ctx, "framework", new org.springframework.web.servlet.DispatcherServlet(applicationContext));
		ctx.addServletMapping("/*", "framework");
		
		Tomcat.addServlet(ctx, "hello", new HttpServlet() {
		      protected void service(HttpServletRequest req, HttpServletResponse resp) 
		      throws ServletException, IOException {
		        Writer w = resp.getWriter();
		        w.write("Hello, World!");
		        w.flush();
		      }
		    });
		    ctx.addServletMapping("/hello", "hello");

		tomcat.start();
		tomcat.getServer().await();
	}

}
