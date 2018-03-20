package com.inferyx.framework.batch;

import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.service.DagServiceImpl;
import com.inferyx.framework.service.FrameworkThreadServiceImpl;

public class DagRunner {

	public DagRunner() {
		// TODO Auto-generated constructor stub
	}
	
	static final Logger logger = Logger.getLogger(DagRunner.class);

	public static void main(String[] args) throws JSONException, ParseException, IOException {
		String uuid = null;
		String appUuid = null;
		String userName = null;
		String version = null;
		String execParams = null;
		String metaType = null;
		ApplicationContext batchContext = new ClassPathXmlApplicationContext("classpath:framework-batch.xml");
		DagServiceImpl dagServiceImpl = batchContext.getBean(DagServiceImpl.class);
		FrameworkThreadServiceImpl frameworkThreadServiceImpl = batchContext.getBean(FrameworkThreadServiceImpl.class);
		if (args == null || args.length == 0) {
			return;
		}
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "USER_NAME" : 
					if (args.length < i) {
						logger.info(usageInfo());
						return;
					}
					userName = args[i+1];
					frameworkThreadServiceImpl.setSession(userName);
					i++;
					break;
				case "APP_UUID" : 
					if (args.length < i) {
						logger.info(usageInfo());
						return;
					}
					appUuid = args[i+1];
					i++;
					break;
				case "UUID" : 
					if (args.length < i) {
						logger.info(usageInfo());
						return;
					}
					uuid = args[i+1];
					i++;
					break;
				case "VERSION" : 
					if (args.length < i) {
						logger.info(usageInfo());
						return;
					}
					version = args[i+1];
					i++;
					break;
				case "EXECPARAMS" : break;
				case "METATYPE" : 
					if (args.length < i) {
						logger.info(usageInfo());
						return;
					}
					metaType = args[i+1];
					i++;
					break;
				default : 
			}
		}
		try {
			dagServiceImpl.submitDag(uuid, version, null, metaType, Mode.BATCH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String usageInfo() {
		return "Usage: UUID <uuid> VERSION <version> [EXECPARAMS <execParams>] METATYPE <metaType> RUNMODE <runMode>";
	}

}
