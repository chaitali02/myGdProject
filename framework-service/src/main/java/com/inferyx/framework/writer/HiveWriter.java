package com.inferyx.framework.writer;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;

@Component
public class HiveWriter implements IWriter {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	protected ExecutorFactory execFactory;
	
	static final Logger logger = Logger.getLogger(HiveWriter.class);	

	@Override
	public void write(ResultSetHolder rsHolder, String filePathUrl, Datapod datapod, String saveMode) throws IOException {
		try {

//			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(datapod.getDatasource().getRef().getUuid(), 
																							datapod.getDatasource().getRef().getVersion(), 
																							datapod.getDatasource().getRef().getType().toString());
			String tableName = datasource.getDbname().concat(".").concat(datapod.getName());
			logger.info("Table Name: " + tableName);

			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String sql = "INSERT INTO " + tableName + " SELECT * FROM " + rsHolder.getTableName();
			exec.executeSql(sql, commonServiceImpl.getApp().getUuid());
//			List<Attribute> attrList = datapod.getAttributes();
//			List<String> parList = new ArrayList<String>();
//			for (Attribute attr : attrList)
//				if (attr.getPartition().equalsIgnoreCase("y"))
//					parList.add(attr.getName());
//			if (parList.size() > 0) {
//				StringBuilder sb = new StringBuilder();
//				for (int i = 0; i < parList.size(); i++)
//					sb.append(parList.get(i)).append(",");
//				String[] columnList = sb.substring(0, sb.length() - 1).toString().split(",");
//				logger.info("Partition column list: " + Arrays.asList(columnList));
//				rsHolder.sqlContext().sql("set hive.exec.dynamic.partition.mode="
//						+ commonServiceImpl.getConfigValue("hive.exec.dynamic.partition.mode"));
//				// String partitionMode =
//				// commonServiceImpl.getSessionParametresPropertyValue("hive.exec.dynamic.partition.mode",
//				// "nonstrict");
//				// df.sqlContext().sql("set hive.exec.dynamic.partition.mode="+partitionMode);
//				rsHolder.write().mode(SaveMode.Append).partitionBy(columnList)
//						.insertInto(dataSource.getDbname().concat(".").concat(datapod.getName()));
//			} else {
//				if (saveMode.equalsIgnoreCase("append"))
//					rsHolder.write().mode(SaveMode.Append)
//							.saveAsTable(dataSource.getDbname().concat(".").concat(datapod.getName()));
//				else if (saveMode.equalsIgnoreCase("overwrite"))
//					rsHolder.write().mode(SaveMode.Overwrite)
//							.saveAsTable(dataSource.getDbname().concat(".").concat(datapod.getName()));
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
