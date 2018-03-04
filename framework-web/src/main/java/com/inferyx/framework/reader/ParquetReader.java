package com.inferyx.framework.reader;

import java.io.IOException;

import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.hive.HiveContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataFrameHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.service.DataFrameService;

@Component
public class ParquetReader implements IReader
{
	/*@Autowired
	ExecutorFactory execFactory;*/
	@Autowired
	DataFrameService dataFrameService;
	
	/*DataFrame df;*/
	/*HiveContext hiveContext;*/
	/*String tableName="";
	DataFrameHolder dfm;*/

	@Override
	public DataFrameHolder read(Datapod dp, DataStore datastore, HDFSInfo hdfsInfo, Object conObject, Datasource ds) throws IOException {
		String tableName="";
		String filePath = datastore.getLocation();
		String hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
		if (!filePath.contains(hdfsLocation)) 
			filePath = String.format("%s%s", hdfsLocation, filePath);
		/*DataFrameHolder dfm = dataFrameService.getaDataFrameHolder(filePath, conObject);*/
		Dataset<Row> df = null;
		DataFrameHolder dfm = new DataFrameHolder();
		SparkSession sparkSession = (SparkSession) conObject;
		DataFrameReader reader = sparkSession.read();
		df = reader.load(filePath);
		tableName = Helper.genTableName(filePath);
		dfm.setDataframe(df);
		dfm.setTableName(tableName);
		return dfm;
	}	

}
