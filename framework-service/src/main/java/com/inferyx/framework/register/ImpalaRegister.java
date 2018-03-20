package com.inferyx.framework.register;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.HiveInfo;
import com.inferyx.framework.common.ImpalaInfo;
import com.inferyx.framework.connector.ConnectionHolder;
import com.inferyx.framework.connector.IConnector;
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
import com.inferyx.framework.executor.ImpalaExecutor;
import com.inferyx.framework.executor.MySqlExecutor;
import com.inferyx.framework.factory.ConnectionFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;

@Component
public class ImpalaRegister extends DataSourceRegister {

	Logger logger = Logger.getLogger(ImpalaRegister.class);
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	IDatapodDao iDatapodDao;
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	ExecutorFactory executorFactory;
	@Autowired
	ExecutorFactory execFactory;
	@Autowired
	ImpalaExecutor impalaExecutor;
	@Autowired
	ConnectionFactory connectionFactory;

	public List<Registry> registerDB(String uuid, String version, List<Registry> registryList) {

		Datasource datasource = iDatasourceDao.findOneByUuidAndVersion(uuid, version);
		Datapod dp = null;
		List<String> registryListTable = new ArrayList<String>();

		MetaIdentifierHolder datastoreMeta = new MetaIdentifierHolder();
		MetaIdentifier datasourceRef = new MetaIdentifier(MetaType.datasource, uuid, version);
		List<Datapod> dpList = new ArrayList<>();
		datastoreMeta.setRef(datasourceRef);
		for (int i = 0; i < registryList.size(); i++) {
			registryListTable.add(registryList.get(i).getName());
		}
		try {
			IConnector connector = connectionFactory.getConnector(ExecContext.IMPALA.toString());
			ConnectionHolder connectionHolder = connector.getConnection();
			Connection con = (Connection) connectionHolder.getStmtObject();
			DatabaseMetaData md = con.getMetaData();

			for (int i = 0; i < registryListTable.size(); i++) {
				String tableName = registryListTable.get(i);
				Datapod newdp = new Datapod();
				DataStore datastore = new DataStore();
				List<Attribute> attrList = new ArrayList();
				logger.info("Table is : " + registryListTable);
				dp = new Datapod();
				List<Datapod> datapodList = datapodServiceImpl.SearchDatapodByName(tableName, datasource.getUuid());
				if(datapodList.size() > 0) {
					dp.setUuid(datapodList.get(0).getUuid());
				}
				dp.setName(tableName);
				ResultSet rs2 = md.getColumns(null, null, tableName, null);
				int size = 0;
				while (rs2.next()) {
					logger.info("Column Name is : " + rs2.getString("COLUMN_NAME"));
					logger.info("Column type is : " + rs2.getString("TYPE_NAME"));
					Attribute attr = new Attribute();
					String colName = rs2.getString("COLUMN_NAME");
					String colType = rs2.getString("TYPE_NAME");
					attr.setName(colName);
					attr.setType(colType);
					attr.setDesc("");
					attr.setKey("");
					attr.setPartition("N");
					attr.setActive("Y");
					attr.setDispName(colName);
					attrList.add(attr);
				}
				dp.setAttributes(attrList);
				dp.setDatasource(datastoreMeta);
				newdp = datapodServiceImpl.save(dp);

				if (registryList.get(i).getName().equals(tableName)) {
					registryList.get(i).setRegisteredOn(newdp.getCreatedOn());
					registryList.get(i).setStatus("Registered");
				}

				MetaIdentifierHolder holder = new MetaIdentifierHolder();
				MetaIdentifier datastoreRef = new MetaIdentifier(MetaType.datapod, dp.getUuid(), dp.getVersion());
				datastore.setName(dp.getName());
				datastore.setDesc(dp.getDesc());
				IExecutor exec = execFactory.getExecutor(ExecContext.IMPALA.toString());
				ResultSetHolder rsHolder = exec.executeSql("SELECT * FROM " + datasource.getDbname() + "." + tableName);
				ResultSetHolder rsh = impalaExecutor.registerDataFrameAsTable(rsHolder, tableName);
				datastore.setNumRows(rsh.getCountRows());
				datastore.setCreatedBy(dp.getCreatedBy());
				holder.setRef(datastoreRef);
				// String tb = Helper.genTableName(dp.getUuid(), dp.getVersion());
				datastore.setMetaId(holder);
				dataStoreServiceImpl.save(datastore);
				dpList.add(newdp);

			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return registryList;
	}
}