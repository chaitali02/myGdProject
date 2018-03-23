package com.inferyx.framework.batch;

import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.domain.DagStatusHolder;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.service.DagServiceImpl;
import com.inferyx.framework.service.FrameworkThreadServiceImpl;
import com.inferyx.framework.service.RegisterService;

public class DagRunner {

	public DagRunner() {
		// TODO Auto-generated constructor stub
	}
	
	static final Logger logger = Logger.getLogger(DagRunner.class);

	public static void main(String[] args) throws JSONException, ParseException, IOException {
		ApplicationContext batchContext = new ClassPathXmlApplicationContext("classpath:framework-batch.xml");
		DagServiceImpl dagServiceImpl = batchContext.getBean(DagServiceImpl.class);
		MetaIdentifierHolder metaIdentifierHolder = null;
		DagStatusHolder dagStatusHolder = null;
		RegisterService registerService = batchContext.getBean(RegisterService.class);
		FrameworkThreadServiceImpl frameworkThreadServiceImpl = batchContext.getBean(FrameworkThreadServiceImpl.class);
		if (args == null || args.length == 0) {
			return;
		}
		switch(args[0]) {
		case "--SUBMIT" : metaIdentifierHolder = submitDag(args, dagServiceImpl, frameworkThreadServiceImpl, metaIdentifierHolder);
						  writeSDOutput(metaIdentifierHolder);
						  break;
		case "--STATUS" : dagStatusHolder = getStatus(args, registerService, frameworkThreadServiceImpl, dagStatusHolder);
		  				writeSDOutput(dagStatusHolder);
						break;
		default : logger.info(usageInfo());
		}
		
	}
	
	private static DagStatusHolder getStatus(String args[], 
											RegisterService registerService, 
											FrameworkThreadServiceImpl frameworkThreadServiceImpl, 
											DagStatusHolder dagStatusHolder ) throws JSONException, ParseException, IOException {
		String uuid = null;
		String appUuid = null;
		String userName = null;
		
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "--USER_NAME" : 
				if (args.length < i) {
					logger.info(dagSubmitUsageInfo());
					return null;
				}
				userName = args[i+1];
				frameworkThreadServiceImpl.setSession(userName);
				i++;
				break;
			case "--APP_UUID" : 
				if (args.length < i) {
					logger.info(dagSubmitUsageInfo());
					return null;
				}
				appUuid = args[i+1];
				i++;
				break;
			case "--UUID" : 
				if (args.length < i) {
					logger.info(dagSubmitUsageInfo());
					return null;
				}
				uuid = args[i+1];
				i++;
				break;
			default : 
			}
		}
		
		try {
			dagStatusHolder = registerService.getStatusByDagExec(uuid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dagStatusHolder;
	}
	
	
	private static void writeSDOutput(MetaIdentifierHolder metaIdentifierHolder) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(System.out, metaIdentifierHolder);
	}
	
	private static void writeSDOutput(DagStatusHolder dagStatusHolder) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(System.out, dagStatusHolder);
	}
	
	private static MetaIdentifierHolder submitDag(String args[], 
													DagServiceImpl dagServiceImpl, 
													FrameworkThreadServiceImpl frameworkThreadServiceImpl, 
													MetaIdentifierHolder metaIdentifierHolder ) throws JSONException, ParseException, IOException {
		String uuid = null;
		String appUuid = null;
		String userName = null;
		String version = null;
		String execParams = null;
		String metaType = null;
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "--USER_NAME" : 
				if (args.length < i) {
					logger.info(dagSubmitUsageInfo());
					return null;
				}
				userName = args[i+1];
				frameworkThreadServiceImpl.setSession(userName);
				i++;
				break;
			case "--APP_UUID" : 
				if (args.length < i) {
					logger.info(dagSubmitUsageInfo());
					return null;
				}
				appUuid = args[i+1];
				i++;
				break;
			case "--UUID" : 
				if (args.length < i) {
					logger.info(dagSubmitUsageInfo());
					return null;
				}
				uuid = args[i+1];
				i++;
				break;
			case "--VERSION" : 
				if (args.length < i) {
					logger.info(dagSubmitUsageInfo());
					return null;
				}
				version = args[i+1];
				i++;
				break;
			case "--EXECPARAMS" : break;
			case "--METATYPE" : 
				if (args.length < i) {
					logger.info(dagSubmitUsageInfo());
					return null;
				}
				metaType = args[i+1];
				i++;
				break;
			default : 
			}
		}
		try {
			metaIdentifierHolder = dagServiceImpl.submitDag(uuid, version, null, metaType, Mode.BATCH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return metaIdentifierHolder;
	}
	
	private static String dagStatusUsageInfo() {
		return "Usage: --STATUS --UUID <uuid> [--APP_UUID <app uuid>] --USER_NAME <user name> ";
	}
	
	private static String dagSubmitUsageInfo() {
		return "Usage: --SUBMIT --UUID <uuid> --VERSION <version> [--EXECPARAMS <execParams>] --METATYPE <metaType> --RUNMODE <runMode> [--APP_UUID <app uuid>] --USER_NAME <user name> ";
	}

	private static String usageInfo() {
		return "Usage: --SUBMIT/--LIST/--STATUS [options] ";
	}
	
}
