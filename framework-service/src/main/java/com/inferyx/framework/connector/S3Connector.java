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
package com.inferyx.framework.connector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.CustomLogger;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author Ganesh
 *
 */
@Component
public class S3Connector implements IConnector {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	CustomLogger customLogger = new CustomLogger();
	String logPath;

	/**
	 * @Ganesh
	 *
	 * @return the logPath
	 */
	public String getLogPath() {
		return logPath;
	}

	/**
	 * @Ganesh
	 *
	 * @param logPath
	 *            the logPath to set
	 */
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	@Override
	public ConnectionHolder getConnection() throws IOException {
		ConnectionHolder conholder = new ConnectionHolder();
		BasicAWSCredentials creds;
		try {
			creds = new BasicAWSCredentials(commonServiceImpl.getConfigValue("fs.s3n.awsAccessKeyId"), 
					commonServiceImpl.getConfigValue("fs.s3n.awsSecretAccessKey"));
			AmazonS3 s3 = AmazonS3Client.builder()
		    .withRegion("us-east-1")
		    .withCredentials(new AWSStaticCredentialsProvider(creds)).build();		
			conholder.setType(ExecContext.S3.toString());
			conholder.setConObject(s3);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conholder;
	}

	@Override
	public ConnectionHolder getConnection(Object input, Object input2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionHolder getConnectionByDatasource(Datasource datasource) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
