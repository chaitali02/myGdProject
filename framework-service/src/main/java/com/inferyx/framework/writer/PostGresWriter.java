/**
 * 
 * @Ganesh
 *
 */
package com.inferyx.framework.writer;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author Ganesh
 *
 */
@Service
public class PostGresWriter implements IWriter {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	protected ExecutorFactory execFactory;
	
	Logger logger = Logger.getLogger(PostGresWriter.class);
	
	@Override
	public void write(ResultSetHolder rsHolder, String filePathUrl, Datapod datapod, String saveMode)
			throws IOException {
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
