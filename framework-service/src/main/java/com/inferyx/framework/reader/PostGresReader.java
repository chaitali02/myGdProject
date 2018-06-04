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

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
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
public class PostGresReader implements IReader {
	@Autowired
	protected MetadataUtil daoRegister;
	@Autowired
	protected ExecutorFactory execFactory;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;

	Logger logger=Logger.getLogger(PostGresReader.class);
	

	@Override
	public ResultSetHolder read(Datapod datapod, DataStore datastore, HDFSInfo hdfsInfo, Object conObject, Datasource ds)
			throws IOException {
		ResultSetHolder rsHolder = null;
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			IExecutor exec = execFactory.getExecutor(datasource.getType());
			String dbName = datasource.getDbname();		
			rsHolder = exec.executeSql("SELECT * FROM "+dbName+"."+datapod.getName());
			rsHolder.setTableName(Helper.genTableName(datastore.getLocation()));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}		
		return rsHolder;
	}

}
