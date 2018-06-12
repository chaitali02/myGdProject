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
package com.inferyx.framework.register;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/*import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import org.apache.spark.SparkConf;*/
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.HiveInfo;
import com.inferyx.framework.dao.IDatapodDao;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Registry;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;

@Component
public class HiveRegister extends DataSourceRegister {
	Logger logger = Logger.getLogger(HiveRegister.class);
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	IDatapodDao iDatapodDao;
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	ExecutorFactory execFactory;
	/*@Autowired
	HiveContext hiveContext;*/
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	SparkExecutor sparkExecutor;

	public List<Registry> registerDB(String uuid, String version, List<Registry> registryList) throws Exception {

		Datasource datasource = iDatasourceDao.findOneByUuidAndVersion(uuid, version);
		List<Attribute> attrList = null;
		Datapod dp = null;
		List<Datapod> dpList = new ArrayList<>();
		String hiveDBName = datasource.getDbname();
		HiveInfo hiveInfo = new HiveInfo();
		HiveConf conf = hiveInfo.getHiveConfiguration();
		try {
			HiveMetaStoreClient metastore = new HiveMetaStoreClient(conf);
			SessionState.start(new SessionState(conf));
			logger.info("Database name is : " + hiveDBName);
			List<String> tables = new ArrayList<String>();
			
			if (registryList.size() == metastore.getAllTables(hiveDBName).size()) {
				tables = metastore.getAllTables(hiveDBName);
				logger.info("Tables are :: " + tables);
			} else 
				for (int i = 0; i < registryList.size(); i++) 
					tables.add(registryList.get(i).getName());
			
			for (int i = 0; i < tables.size(); i++) {
				String table = tables.get(i);
				Table tbl = new Table();
				tbl.setTableName(table);
				List<String> partitionNames = metastore.listPartitionNames(hiveDBName, table, (short) 10000);
				List<Partition> partitions = metastore.listPartitions(hiveDBName, table, (short) 10000);
				String partitionColumnName;
				List<String> partitionColumnNames = new ArrayList<String>();
				FieldSchema tableField = new FieldSchema();
				String tableName = tbl.getTableName(); // tableField.getName();
				String tableType = tableField.getType();
				List<FieldSchema> partitionKey = tbl.getPartitionKeys();
				logger.info("Partition key of partitioned table is : " + partitionKey);
				logger.info("Partitions are :" + partitions);
				for (Partition partition : partitions) {
					List<String> partName = partition.getValues();
					logger.info("Single partition: " + partName);
				}

				logger.info("Table Name = " + tableName + " & Table type is = " + tableType);

				Datapod dp1 = new Datapod();
				DataStore datastore = new DataStore();
				dp = new Datapod();
				List<Datapod> datapodList = datapodServiceImpl.SearchDatapodByName(tableName, datasource.getUuid());
				if(datapodList.size() > 0) {
					dp.setUuid(datapodList.get(0).getUuid());
				}
				dp.setName(tableName);
				
				List<FieldSchema> columnFields = metastore.getFields(hiveDBName, table);
				attrList = new ArrayList<>();
				int size = 0;

				for (String partition : partitionNames) {
					int count = StringUtils.countMatches(partition, "/");
					int start = 0;
					for (int j = 0; j <= count; j++) {
						int pos = partition.indexOf("=", start);
						partitionColumnName = partition.substring(start, pos);
						partitionColumnNames.add(partitionColumnName);
						start = partition.indexOf("/", start) + 1;
						logger.info("Partition name is : " + partitionColumnName);
					}
				}
				logger.info("Column name is : " + partitionColumnNames);
				Set<String> distinctPartition = new HashSet<String>(partitionColumnNames);
				logger.info(distinctPartition);

				// Retriving columns of each table.
				for (FieldSchema columnField : columnFields) {
					String colName = columnField.getName();
					String colType = columnField.getType();
					String colComment = columnField.getComment();

					Attribute attr = new Attribute();
					attr.setAttributeId(size);
					attr.setName(colName);
					attr.setType(colType);
					attr.setDesc(colComment);
					dp.setDesc(colComment);
					attr.setKey("");
					attr.setPartition("N");
					attr.setActive("Y");
					attr.setDispName(colName);
					size++;
					attrList.add(attr);
				}

				for (String colField : distinctPartition) {
					String colName = colField;
					Attribute attr = new Attribute();
					attr.setAttributeId(size);
					attr.setName(colName);
					attr.setType("String");
					attr.setDesc("");
					attr.setKey("");
					attr.setPartition("Y");
					attr.setActive("Y");
					attr.setDispName(colName);
					size++;
					attrList.add(attr);
				}

				// Checking Partitions.
				for (Partition partition : partitions) {
					logger.info("Partition value is : " + partition.getValues());
				}

				MetaIdentifierHolder mHolder = new MetaIdentifierHolder();
				MetaIdentifier datasourceRef = new MetaIdentifier(MetaType.datasource, datasource.getUuid(),
						datasource.getVersion());
				mHolder.setRef(datasourceRef);
				dp.setAttributes(attrList);
				dp.setDatasource(mHolder);
				try {
					dp1 = datapodServiceImpl.save(dp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (registryList.get(i).getName().equals(table)) {
					registryList.get(i).setRegisteredOn(dp1.getCreatedOn());
					registryList.get(i).setStatus("Registered");
				}
				MetaIdentifierHolder datastoreMeta = new MetaIdentifierHolder();
				MetaIdentifier datastoreRef = new MetaIdentifier(MetaType.datapod, dp.getUuid(), dp.getVersion());
				datastore.setName(dp.getName());
				datastore.setDesc(dp.getDesc());
				IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
				ResultSetHolder rsHolder = exec.executeSql("Select * from " + hiveDBName + "." + table);
				// SparkExecutor se = new SparkExecutor();
				ResultSetHolder rsh = sparkExecutor.registerDataFrameAsTable(rsHolder, table);
				// DataFrame df = rsh.getDataFrame();
				// ResultSet rs = rsHolder.getResultSet();
				datastore.setNumRows(rsh.getCountRows());
				datastore.setCreatedBy(dp.getCreatedBy());
				datastoreMeta.setRef(datastoreRef);
				String tb = Helper.genTableName(dp.getUuid(), dp.getVersion());
				// hiveContext.registerDataFrameAsTable(df, tb);
				datastore.setMetaId(datastoreMeta);
				
				datastoreServiceImpl.save(datastore);
				dpList.add(dp1);
			}
			
		} catch ( TException | IOException  e) {
			e.printStackTrace();
		}		
		return registryList;
	}
}
