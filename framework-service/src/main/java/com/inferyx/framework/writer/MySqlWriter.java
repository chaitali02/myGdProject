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
package com.inferyx.framework.writer;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;

public class MySqlWriter implements IWriter {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	protected ExecutorFactory execFactory;
	
	Logger logger=Logger.getLogger(MySqlWriter.class);
	
	@Override
	public void write(ResultSetHolder rsHolder, String filePathUrl, Datapod datapod, String saveMode) throws IOException{	
		try{
//			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(datapod.getDatasource().getRef().getUuid(), 
																							datapod.getDatasource().getRef().getVersion(), 
																							datapod.getDatasource().getRef().getType().toString());
			String tableName = datasource.getDbname().concat(".").concat(datapod.getName());
			logger.info("Table Name: " + tableName);

			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String sql = "INSERT INTO " + tableName + " SELECT * FROM " + rsHolder.getTableName();
			exec.executeSql(sql, commonServiceImpl.getApp().getUuid());
//			Datasource dataSource = daoRegister.getiDatasourceDao().findLatestByUuid(d.getDatasource().getRef().getUuid(), new Sort(Sort.Direction.DESC, "version")); 
//			//Datasource dataSource =  daoRegister.getiDatasourceDao().findOneByUuidAndVersion(d.getDatasource().getRef().getUuid(),d.getDatasource().getRef().getVersion());
//		
//			logger.info("Table Name: "+dataSource.getDbname().concat(".").concat(d.getName()));
//			Properties prop = new Properties(); 
//			prop.put("user", dataSource.getUsername());
//			prop.put("password", dataSource.getPassword());  
//
//			List<Attribute> attrList = d.getAttributes();		
//			List<String> parList = new ArrayList<String>();
//			for(Attribute attr : attrList)
//				if(attr.getPartition().equalsIgnoreCase("y")) 
//					parList.add(attr.getName());
//			if(parList.size() > 0){
//				StringBuilder sb = new StringBuilder();
//				for(int i=0; i<parList.size(); i++)
//					sb.append(parList.get(i)).append(",");
//				String[] columnList = sb.substring(0, sb.length()-1).toString().split(",");
//				logger.info("Partition column list >----->> "+Arrays.asList(columnList));
//				//df.sqlContext().sql("set hive.exec.dynamic.partition.mode="+Helper.getProperty("hive.exec.dynamic.partition.mode"));
//				rsHolder.write().mode(SaveMode.Append).partitionBy(columnList)
//				.option("driver", dataSource.getDriver()).jdbc("jdbc:mysql://"+dataSource.getHost()+":"+dataSource.getPort()+"/"+dataSource.getDbname(), 
//						dataSource.getDbname().concat(".").concat(d.getName()), prop);
//			}else {
//				logger.info("saveMode >----->> "+saveMode);
//				if(saveMode.equalsIgnoreCase("append"))	
//					rsHolder.write().mode(SaveMode.Append).option("driver", dataSource.getDriver()).jdbc("jdbc:mysql://"+dataSource.getHost()+":"+dataSource.getPort()+"/"+dataSource.getDbname(), 
//							dataSource.getDbname().concat(".").concat(d.getName()), prop);
//				else if(saveMode.equalsIgnoreCase("overwrite")) 
//					rsHolder.write().mode(SaveMode.Overwrite).option("driver", dataSource.getDriver()).jdbc("jdbc:mysql://"+dataSource.getHost()+":"+dataSource.getPort()+"/"+dataSource.getDbname(), 
//							dataSource.getDbname().concat(".").concat(d.getName()), prop);
//			} 
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}				
	}
}
