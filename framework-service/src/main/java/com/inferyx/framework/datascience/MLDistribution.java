/**
 * 
 */
package com.inferyx.framework.datascience;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamList;
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
public class MLDistribution {

	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private HDFSInfo hdfsInfo;
	
	private Constructor<?> cons;
	
	public Object getDistribution(Distribution distribution, ExecParams execParams) throws InterruptedException, ExecutionException, Exception {
		
		ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(distribution.getParamList().getRef().getUuid(), distribution.getParamList().getRef().getVersion(), distribution.getParamList().getRef().getType().toString());
		List<Param> params = paramList.getParams();
		List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
		
		Map<String, Object> arguments = new HashMap<>(); 
		for(ParamListHolder holder : paramListInfo) {
			String paramType = holder.getParamType();
			switch(paramType.toLowerCase()) {
				case "twodarray" : double[][] twoDarray = getTwoDArray(holder); 
									arguments.put(holder.getParamName(), twoDarray);
									break;
				case "onedarray" : double[] oneDArray = getOneDArray(holder); 
									arguments.put(holder.getParamName(), oneDArray);
									break;
				case "string" : arguments.put(holder.getParamName(), holder.getParamValue().getValue());
								break;
				case "double" : arguments.put(holder.getParamName(), holder.getParamValue().getValue());
								break;
				case "integer": arguments.put(holder.getParamName(), holder.getParamValue().getValue());
								break;
				case "long" : arguments.put(holder.getParamName(), holder.getParamValue().getValue());
								break;
			}
		}
		
		
		Class<?>[] type = new Class<?>[arguments.size()];
		Class<?> distributorClass = Class.forName(distribution.getClassName());		
		Object[] obj = new Object[arguments.size()];
		int i = 0;
		for(String key : arguments.keySet()) {
			Object value = arguments.get(key);
			 
			switch(key) {
			case "twodarray" : obj[i] = value;
								type[i] = double[][].class;
								i++;
								break;
			case "onedarray" : obj[i] = value;
								type[i] = double[].class;
								i++;
								break;
			case "string": obj[i] = value;
							type[i] = String.class;
							i++;
							break;
			case "double" : obj[i] = value;
							type[i] = double.class;
							i++;
							break;
			case "integer" : obj[i] = value;
							type[i] = int.class;
							i++;
							break;
			case "long" : obj[i] = value;
							type[i] = long.class;
							i++;
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
		ResultSetHolder paramRSHolder = exec.readFile(commonServiceImpl.getApp().getUuid(), paramDp, paramDs, hdfsInfo, null, datasource);
		double[][] params = exec.twoDArrayFromDatapod(paramRSHolder, paramDp);
		return params;
	}
	
	private double[] getOneDArray(ParamListHolder paramListHolder) throws InterruptedException, ExecutionException, Exception {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		
		Datapod paramDp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(paramListHolder.getParamValue().getRef().getUuid(), paramListHolder.getParamValue().getRef().getVersion(), paramListHolder.getParamValue().getRef().getType().toString());
		DataStore paramDs = dataStoreServiceImpl.findDataStoreByMeta(paramDp.getUuid(), paramDp.getVersion());
		ResultSetHolder paramRSHolder = exec.readFile(commonServiceImpl.getApp().getUuid(), paramDp, paramDs, hdfsInfo, null, datasource);
		double[] params = exec.oneDArrayFromDatapod(paramRSHolder, paramDp);
		return params;
	}
	
	public Date getDate() {
		return new Date();
	}
}
