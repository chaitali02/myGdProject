/**
 * 
 * @Ganesh
 *
 */
package com.inferyx.framework.reader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataStore;
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
public class PostGresReader implements IReader {
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;

	Logger logger=Logger.getLogger(PostGresReader.class);
	

	@Override
	public ResultSetHolder read(Datapod datapod, DataStore datastore, Object conObject, Datasource ds)
			throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		ResultSetHolder rsHolder = null;
		try {
			Datasource execDatasource = commonServiceImpl.getDatasourceByApp();
			Datasource tableDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(datapod.getDatasource().getRef().getUuid(), 
																				datapod.getDatasource().getRef().getVersion(), 
																				datapod.getDatasource().getRef().getType().toString());
			IExecutor exec = execFactory.getExecutor(execDatasource.getType());
			String dbName = tableDatasource.getDbname();		
			rsHolder = exec.executeSql("SELECT * FROM "+dbName+"."+datapod.getName());
			rsHolder.setTableName(Helper.genTableName(datastore.getLocation()));
		} catch (IllegalArgumentException | SecurityException | NullPointerException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}		
		return rsHolder;
	}

}
