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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Operator;
import com.inferyx.framework.domain.Registry;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Stage;
import com.inferyx.framework.domain.Task;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.MySqlExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DagServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.LoadServiceImpl;

@Component
public class MySqlRegister {
	Logger logger = Logger.getLogger(MySqlExecutor.class);
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	ExecutorFactory execFactory;
	@Autowired
	MySqlExecutor mySqlExecutor;
	@Autowired
	DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	private LoadServiceImpl loadServiceImpl;
	@Autowired
	private DagServiceImpl dagServiceImpl;
	@Autowired
	ConnectionFactory connectionFactory;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;

	public List<Registry> registerDB(String uuid, String version, List<Registry> registryList) throws Exception {

		//Datasource datasource = iDatasourceDao.findOneByUuidAndVersion(uuid, version);
		Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.datasource.toString());
		MetaIdentifierHolder datastoreMeta = new MetaIdentifierHolder();
		MetaIdentifier datasourceRef = new MetaIdentifier(MetaType.datasource, uuid, version);

		datastoreMeta.setRef(datasourceRef);
		Datapod dp = null;
		List<Datapod> dpList = new ArrayList<>();

		List<String> registryListTable = new ArrayList<String>();
		for (int i = 0; i < registryList.size(); i++)
			registryListTable.add(registryList.get(i).getName());

		try {
			/*Class.forName(datasource.getDriver());
			Connection con = null;
			con = DriverManager.getConnection(
					"jdbc:mysql://" + datasource.getHost() + ":" + datasource.getPort() + "/" + datasource.getDbname(),
					datasource.getUsername(), datasource.getPassword());*/
			datasource = commonServiceImpl.getDatasourceByApp();
			IConnector connector = connectionFactory.getConnector(datasource.getType());
			ConnectionHolder conHolder = connector.getConnection();
			//Statement stmt = (Statement) conHolder.getConObject();	
			Connection con = (Connection) conHolder.getConObject();

			DatabaseMetaData md = con.getMetaData();

			for (int i = 0; i < registryListTable.size(); i++) {
				String tableName = registryListTable.get(i);
				DataStore datastore = new DataStore();
				List<Attribute> attrList = new ArrayList();
				logger.info("Table is : " + registryListTable);
				dp = new Datapod();
				List<Datapod> datapodList = datapodServiceImpl.SearchDatapodByName(tableName, datasource.getUuid());

				if (datapodList.size() > 0)
					dp.setUuid(datapodList.get(0).getUuid());

				dp.setName(tableName);
				ResultSet rs = md.getColumns(null, null, tableName, null);
				int size = 0;
				while (rs.next()) {
					logger.info("Column Name: " + rs.getString("COLUMN_NAME")+"\t Type: " + rs.getString("TYPE_NAME"));
					Attribute attr = new Attribute();
					String colName = rs.getString("COLUMN_NAME");
					String colType = rs.getString("TYPE_NAME");
					attr.setName(colName);
					attr.setType(colType);
					attr.setDesc("");
					attr.setKey("");
					attr.setPartition("N");
					attr.setActive("Y");
					attr.setDispName(colName);
					attrList.add(attr);
				}
				rs.close();
				dp.setAttributes(attrList);
				dp.setDatasource(datastoreMeta);
				Datapod newdp = new Datapod();
				newdp = datapodServiceImpl.save(dp);

				if (registryList.get(i).getName().equals(tableName)) {
					registryList.get(i).setRegisteredOn(newdp.getCreatedOn());
					registryList.get(i).setStatus("Registered");
				}

				MetaIdentifierHolder holder = new MetaIdentifierHolder();
				MetaIdentifier datastoreRef = new MetaIdentifier(MetaType.datapod, dp.getUuid(), dp.getVersion());
				datastore.setName(dp.getName());
				datastore.setDesc(dp.getDesc());
				IExecutor exec = execFactory.getExecutor(ExecContext.MYSQL.toString());
				ResultSetHolder rsHolder = exec.executeSql("SELECT * FROM " + datasource.getDbname() + "." + tableName);
				ResultSetHolder rsh = mySqlExecutor.registerDataFrameAsTable(rsHolder, tableName);
				datastore.setNumRows(rsh.getCountRows());
				datastore.setCreatedBy(dp.getCreatedBy());
				holder.setRef(datastoreRef);
				datastore.setMetaId(holder);

				//datastoreServiceImpl.save(datastore);
				commonServiceImpl.save(MetaType.datastore.toString(), datastore);
				dpList.add(newdp);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return registryList;
	}
}
