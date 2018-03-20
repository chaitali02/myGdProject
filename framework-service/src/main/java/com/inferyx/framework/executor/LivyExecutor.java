package com.inferyx.framework.executor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.livy.LivyClient;
import org.apache.log4j.Logger;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.storage.StorageLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.client.LivyClientImpl;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.connector.IConnector;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.ResultType;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.livyjob.ExecRegAndPersistJob;
import com.inferyx.framework.livyjob.ExecuteAndRegisterJob;
import com.inferyx.framework.livyjob.ExecuteAndResult;
import com.inferyx.framework.livyjob.RegisterDatapodJob;
import com.inferyx.framework.reader.IReader;
import com.inferyx.framework.writer.IWriter;

@Component
public class LivyExecutor implements IExecutor {
	@Autowired
	Properties dbConfiguration;
	@Autowired
	LivyClientImpl livyClientImpl;
	@Autowired
	MetadataUtil commonActivity;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	HDFSInfo hdfsInfo;
	
	
	static final Logger logger = Logger.getLogger(LivyExecutor.class);

	public LivyExecutor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ResultSetHolder executeSql(String sql) throws IOException {
		
		return null;
	}

	@Override
	public ResultSetHolder executeSql(String sql, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> executeAndFetch(String sql, String clientContext) throws IOException {
		LivyClient livyClient = null;
		try {
			livyClient = livyClientImpl.getClient(clientContext);
			logger.info("Inside executeAndFetch sql >>>> " + sql);
			return livyClient.submit(new ExecuteAndResult(sql)).get();
//			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResultSetHolder executeAndPersist(String sql, String filePath, Datapod datapod, String saveMode,
			String clientContext) throws IOException {
		IWriter datapodWriter = null;
		datapodWriter = dataSourceFactory.getDatapodWriter(datapod, commonActivity);	
		return null;
	}

	@Override
	public ResultSetHolder executeAndRegister(String sql, String tableName, String clientContext) throws IOException {
		logger.info("Inside executeAndFetch sql >>>> " + sql);
		LivyClient livyClient = null;
		Dataset<Row> df = null;
		ResultSetHolder rsHolder = null;
		long countRows = -1L;
		try {
			rsHolder = new ResultSetHolder();
			livyClient = livyClientImpl.getClient(clientContext);
			countRows = livyClient.submit(new ExecuteAndRegisterJob(sql, tableName)).get();
			rsHolder.setCountRows(countRows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsHolder;
	}

	@Override
	public ResultSetHolder executeRegisterAndPersist(String sql, String tableName, String filePath, Datapod datapod,
			String saveMode, String clientContext) throws IOException {
		logger.info("Inside executeAndFetch sql >>>> " + sql);
		String filePathUrl = String.format("%s%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath(), filePath);
		LivyClient livyClient = null;
		ResultSetHolder rsHolder = null;
		long countRows = -1L;
		try {
			rsHolder = new ResultSetHolder();
			livyClient = livyClientImpl.getClient(clientContext);
			countRows = livyClient.submit(new ExecRegAndPersistJob(sql, tableName, filePathUrl, saveMode)).get();
			rsHolder.setType(ResultType.dataframe);
			rsHolder.setCountRows(countRows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsHolder;
	}

	@Override
	public Boolean registerTempTable(Dataset<Row> df, String tableName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void registerDatapod(String filePath, String tableName, String clientContext) {
		LivyClient livyClient = null;
		try {
			livyClient = livyClientImpl.getClient(clientContext);
			livyClient.submit(new RegisterDatapodJob(StorageLevel.MEMORY_AND_DISK(), filePath, tableName)).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public ResultSetHolder registerDataFrameAsTable(ResultSetHolder rsHolder, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean executeScript(String filePath, String clientContext) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object fetchAndTrainModel(Model model, String[] fieldArray, Algorithm algorithm, String modelName,
			String filePath, ParamMap paramMap, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String fetchAndCreatePMML(DataStore datastore, Datapod datapod, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> fetchModelResults(DataStore datastore, Datapod datapod, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long loadAndRegister(Load load, String filePath, String dagExecVer, String loadExecVer,
			String datapodTableName, Datapod datapod, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void registerDatapod(String tableName, Datapod datapod, DataStore dataStore, ExecContext execContext,
			String clientContext) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Attribute> fetchAttributeList(String csvFileName, String parquetDir, boolean flag,
			boolean writeToParquet, String clientContext) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> submitQuery(String sql, int rowLimit, String format, String header, String clientContext)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
