/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.batch;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.DagStatusHolder;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
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
		List<BaseEntity> objectList = null;
		CommonServiceImpl commonServiceImpl = batchContext.getBean(CommonServiceImpl.class);
		DagVisitorFactory dagVisitorFactory = batchContext.getBean(DagVisitorFactory.class);
		DagVisitor dagVisitor =  null;
		if (args == null || args.length == 0) {
			return;
		}
		
		switch(args[1]) {
		case "--PRETTY" : dagVisitor = dagVisitorFactory.getInstance("PRETTY");
							break;
		case "--JSON"   : dagVisitor = dagVisitorFactory.getInstance("JSON");
							break;
		default         : dagVisitor = dagVisitorFactory.getInstance("JSON");
							break;
		}
		
		switch(args[0]) {
		case "--SUBMIT" : metaIdentifierHolder = submitDag(args, dagServiceImpl, frameworkThreadServiceImpl, metaIdentifierHolder);
//						  writeSDOutput(metaIdentifierHolder);
						  dagVisitor.visit(metaIdentifierHolder);
						  break;
		case "--STATUS" : dagStatusHolder = getStatus(args, registerService, frameworkThreadServiceImpl, dagStatusHolder);
//		  			      writeSDOutput(dagStatusHolder);
						  dagVisitor.visit(dagStatusHolder);
						  break;
		case "--LIST"   : objectList = list(args, commonServiceImpl, frameworkThreadServiceImpl);
//						  writeSDOutput(objectList);
						  dagVisitor.visit(objectList);
						  break;
		default : logger.info(usageInfo());
		}
		
	}
	
	
	private static List<BaseEntity> list(String args[], 
										CommonServiceImpl commonServiceImpl, 
										FrameworkThreadServiceImpl frameworkThreadServiceImpl ) throws JSONException, ParseException, IOException {
		
		String userName = null;
		String type = null;
		List<BaseEntity> objectList = null;
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
			case "--TYPE" : 
				if (args.length < i) {
					logger.info(dagSubmitUsageInfo());
					return null;
				}
				type = args[i+1];
				i++;
				break;
			default : 
				break; 
			}
		}
		try {
			objectList = commonServiceImpl.getAllLatest(type, "Y");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objectList;
	}
	
	/**
	 * Status API. To be accessed with --STATUS option
	 * @param args
	 * @param registerService
	 * @param frameworkThreadServiceImpl
	 * @param dagStatusHolder
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 * @throws IOException
	 */
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
	
	
	/*private static void writeSDOutput(MetaIdentifierHolder metaIdentifierHolder) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(System.out, metaIdentifierHolder);
	}
	
	private static void writeSDOutput(DagStatusHolder dagStatusHolder) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(System.out, dagStatusHolder);
	}
	
	private static void writeSDOutput(List<BaseEntity> objectList) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(System.out, objectList);
		if (objectList == null || objectList.isEmpty()) {
			return;
		}
		logger.info("\n\n\n\n");
		logger.info(new String(new char[120]).replace("\0", "-"));
		logger.info(String.format(" | %30s | %30s | %30s | %15s |", " ID ", " NAME ", " UUID ", " VERSION "));
		logger.info(new String(new char[120]).replace("\0", "-"));
		for (BaseEntity baseEntity : objectList) {
			logger.info(String.format(" | %30s | %30s | %30s | %15s |", baseEntity.getId(), baseEntity.getName(), baseEntity.getUuid(), baseEntity.getVersion()));
		}
		logger.info(new String(new char[120]).replace("\0", "-"));
		logger.info("\n\n\n\n");
	}*/
	
	/**
	 * Submit API. To be accessed with --SUBMIT option
	 * @param args
	 * @param dagServiceImpl
	 * @param frameworkThreadServiceImpl
	 * @param metaIdentifierHolder
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 * @throws IOException
	 */
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
			metaIdentifierHolder = dagServiceImpl.submitDag(uuid, version, null, metaType, RunMode.BATCH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return metaIdentifierHolder;
	}
	
	
	private static String dagSubmitUsageInfo() {
		return "Usage: --SUBMIT --JSON/--PRETTY --UUID <uuid> --VERSION <version> [--EXECPARAMS <execParams>] --METATYPE <metaType> --RUNMODE <runMode> [--APP_UUID <app uuid>] --USER_NAME <user name> ";
	}
	
	private static String dagStatusUsageInfo() {
		return "Usage: --STATUS --JSON/--PRETTY --UUID <uuid> [--APP_UUID <app uuid>] --USER_NAME <user name> ";
	}
	
	private static String dagListUsageInfo() {
		return "Usage: --LIST --JSON/--PRETTY --TYPE <type> --USER_NAME <user name> ";
	}

	private static String usageInfo() {
		return "Usage: --SUBMIT/--LIST/--STATUS --JSON/--PRETTY [options] ";
	}
	
}
