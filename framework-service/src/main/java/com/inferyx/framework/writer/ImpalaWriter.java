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

public class ImpalaWriter implements IWriter {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	protected ExecutorFactory execFactory;
	
	Logger logger=Logger.getLogger(ImpalaWriter.class);
	@Override
	public void write(ResultSetHolder rsHolder, String filePathUrl, Datapod datapod, String saveMode) throws IOException {
		try{
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			String tableName = datasource.getDbname().concat(".").concat(datapod.getName());
			logger.info("Table Name: " + tableName);

			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String sql = "INSERT INTO " + tableName + " SELECT * FROM " + rsHolder.getTableName();
			exec.executeSql(sql, commonServiceImpl.getApp().getUuid());
//			Datasource dataSource = daoRegister.getiDatasourceDao().findLatestByUuid(datapod.getDatasource().getRef().getUuid(), new Sort(Sort.Direction.DESC, "version")); 
//			//Datasource dataSource =  daoRegister.getiDatasourceDao().findOneByUuidAndVersion(d.getDatasource().getRef().getUuid(),d.getDatasource().getRef().getVersion());
//			logger.info("Table Name: "+dataSource.getDbname().concat(".").concat(datapod.getName()));
//			List<Attribute> attrList = datapod.getAttributes();		
//			List<String> parList = new ArrayList<String>();
//			for(Attribute attr : attrList)
//				if(attr.getPartition().equalsIgnoreCase("y")) 
//					parList.add(attr.getName());	
//			if(parList.size() > 0){
//				StringBuilder sb = new StringBuilder();
//				for(int i=0; i<parList.size(); i++)	{
//					sb.append(parList.get(i)).append(",");
//				}
//				String[] columnList = sb.substring(0, sb.length()-1).toString().split(",");
//				logger.info("Partition column list >----->> "+Arrays.asList(columnList));
//				//dataFrame.sqlContext().sql("set hive.exec.dynamic.partition.mode="+Helper.getProperty("hive.exec.dynamic.partition.mode"));
//				dataFrame.write().mode(SaveMode.Append).partitionBy(columnList).insertInto(dataSource.getDbname().concat(".").concat(datapod.getName()));
//			}else {
//				if(saveMode.equalsIgnoreCase("append"))	{
//					dataFrame.write().mode(SaveMode.Append).saveAsTable(dataSource.getDbname().concat(".").concat(datapod.getName()));
//				}else if(saveMode.equalsIgnoreCase("overwrite")) {
//					dataFrame.write().mode(SaveMode.Overwrite).saveAsTable(dataSource.getDbname().concat(".").concat(datapod.getName()));
//				}
//			}
		}/*catch(FileNotFoundException e){
			e.printStackTrace();
		}*/catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}			 
	}
}
