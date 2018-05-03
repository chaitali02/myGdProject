/**
 * 
 */
package com.inferyx.framework.datascience;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.enums.ParamDataType;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;

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
	private HDFSInfo hdfsInfo;
	
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
				case ONEDARRAY :	 double[] oneDArray = getOneDArray(holder); 
									obj[j] = oneDArray;
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
		
		Datapod paramDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(paramListHolder.getParamValue().getRef().getUuid(), paramListHolder.getParamValue().getRef().getVersion(), paramListHolder.getParamValue().getRef().getType().toString());
		DataStore paramDs = dataStoreServiceImpl.findDataStoreByMeta(paramDp.getUuid(), paramDp.getVersion());
		String tableName = exec.readFile(commonServiceImpl.getApp().getUuid(), paramDp, paramDs, null, hdfsInfo, null, datasource);
		double[][] params = exec.twoDArrayFromDatapod(tableName, paramDp, commonServiceImpl.getApp().getUuid());
		return params;
	}
	
	private double[] getOneDArray(ParamListHolder paramListHolder) throws InterruptedException, ExecutionException, Exception {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		
		Datapod paramDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(paramListHolder.getParamValue().getRef().getUuid(), paramListHolder.getParamValue().getRef().getVersion(), paramListHolder.getParamValue().getRef().getType().toString());
		DataStore paramDs = dataStoreServiceImpl.findDataStoreByMeta(paramDp.getUuid(), paramDp.getVersion());
		String tableName = exec.readFile(commonServiceImpl.getApp().getUuid(), paramDp, paramDs, null, hdfsInfo, null, datasource);
		double[] params = exec.oneDArrayFromDatapod(tableName, paramDp, commonServiceImpl.getApp().getUuid());
		return params;
	}
	
	public Date getDate() {
		return new Date();
	}
}
