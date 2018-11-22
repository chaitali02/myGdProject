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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.dao.IDatapodDao;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Registry;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.Compare;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.LoadServiceImpl;

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
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
    @Autowired 
    LoadServiceImpl loadServiceImpl;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	ConnectionFactory connectionFactory;

	public List<Registry> registerDB(String uuid, String version, List<Registry> registryList, RunMode runMode) throws Exception {

		Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.datasource.toString());//commonServiceImpl.getDatasourceByApp();
		Datapod datapod = null;
		MetaIdentifierHolder datastoreMeta = new MetaIdentifierHolder();
		MetaIdentifier datasourceRef = new MetaIdentifier(MetaType.datasource, uuid, version);
		List<Datapod> dpList = new ArrayList<>();
		datastoreMeta.setRef(datasourceRef);
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.HIVE.toString());
			ConnectionHolder connectionHolder = connector.getConnectionByDatasource(datasource);//connector.getConnection();
			Connection con = ((Statement) connectionHolder.getStmtObject()).getConnection();
			DatabaseMetaData dbMetaData = con.getMetaData();
			
			for (int i = 0; i < registryList.size(); i++) {
				String tableName = registryList.get(i).getName();
				Datapod savedDp = new Datapod();
				DataStore datastore = new DataStore();
				List<Attribute> attrList = new ArrayList<>();
				logger.info("Table is : " + tableName);
				datapod = new Datapod();
				List<Datapod> datapodList = datapodServiceImpl.searchDatapodByName(tableName, datasource.getUuid());
				if(datapodList.size() > 0) {
					datapod.setUuid(datapodList.get(0).getUuid());
				}
				datapod.setName(tableName);
				ResultSet rs = dbMetaData.getColumns(null, null, tableName, null);
				
				for(int j = 0; rs.next(); j++) {
					logger.info("Column Name is : " + rs.getString("COLUMN_NAME"));
					logger.info("Column type is : " + rs.getString("TYPE_NAME"));
					Attribute attr = new Attribute();
					String colName = rs.getString("COLUMN_NAME");
					Integer colSize = rs.getInt("COLUMN_SIZE");
					String colType = rs.getString("TYPE_NAME");
					attr.setAttributeId(j);
					attr.setName(colName);
					attr.setType(colType);
					attr.setLength(colSize);
					attr.setDesc("");
					attr.setKey("");
					attr.setPartition("N");
					attr.setActive("Y");
					attr.setDispName(colName);
					attrList.add(attr);
				}
				datapod.setAttributes(attrList);
				datapod.setDatasource(datastoreMeta);
				savedDp = datapodServiceImpl.save(datapod);

				if (registryList.get(i).getName().equals(tableName)) {
					registryList.get(i).setRegisteredOn(savedDp.getCreatedOn());
					registryList.get(i).setStatus("Registered");
					registryList.get(i).setCompareStatus(Compare.NOCHANGE.toString());
				}

				MetaIdentifierHolder holder = new MetaIdentifierHolder();
				MetaIdentifier datastoreRef = new MetaIdentifier(MetaType.datapod, datapod.getUuid(), datapod.getVersion());
				datastore.setName(datapod.getName());
				datastore.setDesc(datapod.getDesc());
				//IExecutor exec = execFactory.getExecutor(ExecContext.HIVE.toString());
				//ResultSetHolder rsHolder = exec.executeSql("SELECT COUNT(*) FROM " + datasource.getDbname() + "." + tableName);
			//	rsHolder.getResultSet().next();
				datastore.setNumRows(0);
				datastore.setCreatedBy(datapod.getCreatedBy());
				holder.setRef(datastoreRef);
				datastore.setMetaId(holder);
				
				//Creating load & loadExec
				Load load = new Load();
				load.setBaseEntity();
				load.setHeader("Y");
				MetaIdentifier sourceMI = new MetaIdentifier();
				sourceMI.setType(MetaType.datasource);
				sourceMI.setUuid(datasource.getUuid());
				MetaIdentifierHolder sourceHolder = new MetaIdentifierHolder(sourceMI);
				sourceHolder.setValue(datasource.getDbname() + "." + tableName);
				load.setSource(sourceHolder);
				MetaIdentifier targetMI = new MetaIdentifier();
				targetMI.setType(MetaType.datapod);
				targetMI.setUuid(datapod.getUuid());
				MetaIdentifierHolder targetHolder = new MetaIdentifierHolder(targetMI);
				load.setTarget(targetHolder);
				load.setName(datapod.getName());
				commonServiceImpl.save(MetaType.load.toString(), load);
				LoadExec loadExec = loadServiceImpl.create(load.getUuid(), load.getVersion(), null, null, null);
				loadExec = (LoadExec) commonServiceImpl.setMetaStatus(loadExec, MetaType.loadExec, Status.Stage.InProgress);
				loadExec = (LoadExec) commonServiceImpl.setMetaStatus(loadExec, MetaType.loadExec, Status.Stage.Completed);
				
				dataStoreServiceImpl.save(datastore);
				dpList.add(savedDp);
			}
			
		} catch ( TException | IOException  e) {
			e.printStackTrace();
		}		
		return registryList;
	}
}
