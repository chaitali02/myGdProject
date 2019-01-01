package com.inferyx.predict.module;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.Executor;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class TomcatStarter {
	
	static Logger logger = Logger.getLogger(TomcatStarter.class.getName());
	
	static String portNumber = null;
	public TomcatStarter() {
		// TODO Auto-generated constructor stub
	}
	
	public TomcatStarter(String portNumber) {
		TomcatStarter.portNumber = portNumber;
	}
	public static void main(String[] args) throws Exception {
		Tomcat tomcat = new Tomcat();
//		tomcat.setPort(Integer.parseInt(portNumber));
		tomcat.setPort(Integer.parseInt(args[0]));
		
		Context ctx = tomcat.addContext("/web", "/app/framework_predict");
//		Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());
		logger.info("Context name : " + ctx.getName());
		addDefaultServlet(ctx);
		addSpringServlet(ctx);

		
		
		Tomcat.addServlet(ctx, "hello", new HttpServlet() {
		      protected void service(HttpServletRequest req, HttpServletResponse resp) 
		      throws ServletException, IOException {
		        Writer w = resp.getWriter();
		        w.write("Hello, World!");
		        w.flush();
		      }
		    });
		    ctx.addServletMapping("/hello", "hello");
		    
		    LifecycleListener[] listeners = tomcat.getService().findLifecycleListeners();
		    if (listeners != null) {
		    	logger.info(" Number of default listeners : " + listeners.length);
			    for (LifecycleListener listener : listeners) {
			    	logger.info(" Listener : " + listener.getClass().getName());
			    }
		    }
		    logger.info(" Proxy port : " + tomcat.getConnector().getProxyPort());
		    logger.info(" Redirect port : " + tomcat.getConnector().getRedirectPort());
		    Connector[] connectors = tomcat.getService().findConnectors();
		    if (connectors != null) {
		    	logger.info(" Number of default connectors : " + connectors.length);
			    for (Connector connector : connectors) {
			    	logger.info(" Connector : " + connector.getClass().getName() + " in port : " + connector.getPort());
			    }
		    }
		    
		    Executor[] executors = tomcat.getService().findExecutors();
		    if (connectors != null) {
		    	logger.info(" Number of default executors : " + executors.length);
			    for (Executor executor : executors) {
			    	logger.info(" Executor : " + executor.getClass().getName());
			    }
		    }
		    
		tomcat.start();
		tomcat.getServer().await();
	}
	
	private static void addSpringServlet(Context context) {
		Wrapper springServlet = context.createWrapper();
//		XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
//		applicationContext.setConfigLocations("classpath:framework-predict.xml");
//		Servlet servlet = new org.springframework.web.servlet.DispatcherServlet(applicationContext);
//		Tomcat.addServlet(ctx, "predict", servlet);
//		Tomcat.initWebappDefaults(ctx);
//		ctx.addServletMapping("/predict/*", "predict");
		springServlet.setName("predict");
		springServlet.setServletClass("org.springframework.web.servlet.DispatcherServlet");
		springServlet.addInitParameter("contextConfigLocation", "classpath:framework-predict.xml");
		springServlet.setLoadOnStartup(2);
		// Otherwise the default location of a Spring DispatcherServlet cannot be set
//		springServlet.setOverridable(true);
		context.addChild(springServlet);
		context.addServletMapping("/predict/*", "predict");
	}
	
	private static void addDefaultServlet(Context context) {
		Wrapper defaultServlet = context.createWrapper();
		defaultServlet.setName("default");
		defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
		defaultServlet.addInitParameter("debug", "0");
		defaultServlet.addInitParameter("listings", "false");
		defaultServlet.setLoadOnStartup(1);
		// Otherwise the default location of a Spring DispatcherServlet cannot be set
		defaultServlet.setOverridable(true);
		context.addChild(defaultServlet);
		context.addServletMapping("/", "default");
	}

}
