package com.inferyx.framework.reader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.DataFrameHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;

public class MySqlReader implements IReader {
	Logger logger=Logger.getLogger(MySqlReader.class);
	@Autowired
	protected MetadataUtil daoRegister;
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	
	@Override
	public DataFrameHolder read(Datapod datapod, DataStore datastore, HDFSInfo hdfsInfo, Object conObject, Datasource dataSource) throws IOException {
		Dataset<Row> dataFrame = null;;
		String tableName="";
		DataFrameHolder dataFrameHolder = new DataFrameHolder();
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String filepath = datastore.getLocation();
			String dbName = dataSource.getDbname();		
			ResultSetHolder rsHolder = exec.executeSql("SELECT * FROM "+dbName+"."+datapod.getName());
			dataFrame = rsHolder.getDataFrame();
			tableName = Helper.genTableName(filepath);		
			dataFrameHolder.setDataframe(dataFrame);
			dataFrameHolder.setTableName(tableName);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataFrameHolder;		
	}
}
