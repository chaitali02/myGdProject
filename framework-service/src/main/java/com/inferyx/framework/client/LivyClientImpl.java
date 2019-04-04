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
package com.inferyx.framework.client;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.apache.livy.LivyClient;
import org.apache.livy.LivyClientBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.SparkInfo;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.service.CommonServiceImpl;

@Component
public class LivyClientImpl {
	
	SparkInfo sparkInfo;
	protected ConcurrentHashMap<String, LivyClient> livyClientMap;
	private String url;
	@Autowired
	protected CommonServiceImpl commonServiceImpl;

	public LivyClientImpl() {
	}
	
	/**
	 * @return the sparkInfo
	 */
	public SparkInfo getSparkInfo() {
		return sparkInfo;
	}

	/**
	 * @param sparkInfo the sparkInfo to set
	 */
	public void setSparkInfo(SparkInfo sparkInfo) {
		this.sparkInfo = sparkInfo;
	}

	/**
	 * @return the livyClientMap
	 */
	public ConcurrentHashMap<String, LivyClient> getLivyClientMap() {
		return livyClientMap;
	}

	/**
	 * @param livyClientMap the livyClientMap to set
	 */
	public void setLivyClientMap(ConcurrentHashMap<String, LivyClient> livyClientMap) {
		this.livyClientMap = livyClientMap;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public synchronized LivyClient build(String clientContext)  {
		LivyClient client = null;
		Datasource datasource = null;
		Properties sessionParams = null;
		try {
			datasource = commonServiceImpl.getDatasourceByApp();
			if (datasource != null) {
				sessionParams = getSessionParams(datasource.getSessionParameters());
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException | IOException e) {
			e.printStackTrace();
		}
		try {
			client = new LivyClientBuilder(false)
					  .setURI(new URI(url))
					  .setAll(sparkInfo.getProp())
					  .build();	// Replace sparkInfo.getProp() with sessionParams later when datasource is figured out
			System.err.printf("Uploading %s to the Spark context...\n", "livy-hive-job-0.0.1-SNAPSHOT");
			client.uploadJar(new File("libs/com/gridedge/hive/livy/0.1/livy-0.1.jar")).get();
			livyClientMap.put(clientContext, client);
			//return client.submit(new SparkJob()).get();
			return client;
		} catch (IOException | URISyntaxException | InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Get livy Client or throws exception in case livy client is not available
	 * @param clientContext
	 * @return
	 * @throws Exception
	 */
	public LivyClient getClient(String clientContext) throws Exception {
		if (livyClientMap.containsKey(clientContext)) {
			return livyClientMap.get(clientContext);
		}
		return build(clientContext);
	}
	
	/**
	 * DataFrame from file
	 * @param clientContext
	 * @param filePath
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws Exception
	 */
	
	/****************************Unused****************************/
	/*public Dataset<Row> readFile(String clientContext, String filePath) throws InterruptedException, ExecutionException, Exception {
		return getClient(clientContext).submit(new FileReadJob(filePath)).get();
	}*/
		
	private Properties getSessionParams(String sessionParams) throws IOException {
		final Properties p = new Properties();
		if (sessionParams == null) {
			return p;
		}
		sessionParams = sessionParams.replaceAll(";", "\\n");
	    p.load(new StringReader(sessionParams));
	    return p;
	}
	
}
