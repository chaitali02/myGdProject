/**
 * 
 */
package com.inferyx.framework.datascience;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.enums.ParamDataType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;

/**
 * @author Ganesh
 *
 */
public class Math3Distribution {

	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private ModelServiceImpl modelServiceImpl;
	
	static final Logger LOGGER = Logger.getLogger(Math3Distribution.class);
	
	public Object getDistribution(Distribution distribution, ExecParams execParams) throws InterruptedException, ExecutionException, Exception {
		List<ParamListHolder> paramListInfo = execParams.getParamListInfo();

		Class<?> distributorClass = Class.forName(distribution.getClassName());	
		Class<?>[] type = new Class[paramListInfo.size()];
		Object[] obj = new Object[paramListInfo.size()];
		
		int j = 0; 
		for(ParamListHolder holder : paramListInfo) {
			ParamDataType paramDataType = Helper.resolveParamDataType(holder.getParamType());
			switch(paramDataType) {
				case TWODARRAY : 	double[][] twoDarray = getTwoDArray(holder); 
									obj[j] = twoDarray;
									type[j] = double[][].class;
									j++;
									break;
				case ONEDARRAY :	double[] oneDArray = getOneDArray(holder); 
									obj[j] = oneDArray;
									type[j] = double[].class;
									j++;
									break;
				case ATTRIBUTES : 	double[][] attributesArray = getTwoDArray(holder); 
									obj[j] = attributesArray;
									type[j] = double[][].class;
									j++;
									break;
				case ATTRIBUTE :	double[] attributeArray = getOneDArray(holder); 
									obj[j] = attributeArray;
									type[j] = double[].class;
									j++;
									break;
				case STRING : 		obj[j] = holder.getParamValue().getValue();
									type[j] = String.class;
									j++;
									break;
				case DOUBLE : 		obj[j] = Double.parseDouble(""+holder.getParamValue().getValue());
									type[j] = double.class;
									j++;
									break;
				case INTEGER :	 	obj[j] = Integer.parseInt(""+holder.getParamValue().getValue());
									type[j] = int.class;
									j++;
									break;
				case LONG : 		obj[j] = Long.parseLong(""+holder.getParamValue().getValue());
									type[j] = long.class;
									j++;
									break;
				case DATE : 		obj[j] = getDate();
									type[j] = Date.class;
									j++;
									break;
				default:
						break;
			}
		}
		
		Constructor<?> cons = distributorClass.getConstructor(type);
		Object object = cons.newInstance(obj);
		return object;
	}

	private double[][] getTwoDArray(ParamListHolder paramListHolder) throws InterruptedException, ExecutionException, Exception {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		
		MetaIdentifier sourceIdentifier = paramListHolder.getAttributeInfo().get(0).getRef();
		
		Object source = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(), sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
		String sql = modelServiceImpl.generateSQLBySource(source);
//		exec.executeAndRegister(sql, tableName, commonServiceImpl.getApp().getUuid());
		
//		Datapod paramDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(), sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
//		DataStore paramDs = dataStoreServiceImpl.findDataStoreByMeta(paramDp.getUuid(), paramDp.getVersion());
//		String tableName = dataStoreServiceImpl.getTableNameByDatastore(paramDs.getUuid(), paramDs.getVersion(), RunMode.BATCH);
//		LOGGER.info("Table name:" + tableName);		
//
//		String sql = "SELECT * FROM " + tableName;
		
		List<double[]> valueList = exec.twoDArray(sql, paramListHolder.getAttributeInfo(), commonServiceImpl.getApp().getUuid());
		double[][] twoDArray = valueList.stream().map(lineStrArray -> ArrayUtils.toPrimitive(lineStrArray)).toArray(double[][]::new);
		return twoDArray;
	}
	
	private double[] getOneDArray(ParamListHolder paramListHolder) throws InterruptedException, ExecutionException, Exception {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());	
		
		MetaIdentifier sourceIdentifier = paramListHolder.getAttributeInfo().get(0).getRef();
		
		Object source = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(), sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
		String sql = modelServiceImpl.generateSQLBySource(source);
//		exec.executeAndRegister(sql, tableName, commonServiceImpl.getApp().getUuid());
		
//		Datapod paramDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(), sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
//		DataStore paramDs = dataStoreServiceImpl.findDataStoreByMeta(paramDp.getUuid(), paramDp.getVersion());
//		String tableName = dataStoreServiceImpl.getTableNameByDatastore(paramDs.getUuid(), paramDs.getVersion(), RunMode.BATCH);
//		LOGGER.info("Table name:" + tableName);
//		
//		String sql = "SELECT * FROM " + tableName;
		
		List<Double> valueList = exec.oneDArray(sql, paramListHolder.getAttributeInfo(), commonServiceImpl.getApp().getUuid());
		double[] oneDArray = ArrayUtils.toPrimitive(valueList.toArray(new Double[valueList.size()]));
		return oneDArray;
	}
	
	public Date getDate() {
		return new Date();
	}
}
