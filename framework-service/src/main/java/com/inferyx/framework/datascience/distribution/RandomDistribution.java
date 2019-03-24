/**
 * 
 */
package com.inferyx.framework.datascience.distribution;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.RowObj;
import com.inferyx.framework.enums.ParamDataType;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;

/**
 * @author Ganesh
 *
 */
@Service
public class RandomDistribution {

	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private ModelServiceImpl modelServiceImpl;

	static final Logger LOGGER = Logger.getLogger(RandomDistribution.class);

	public Object getDistribution(Distribution distribution, ExecParams distExecParams, ExecParams execParams)
			throws InterruptedException, ExecutionException, Exception {
		List<ParamListHolder> paramListInfo = distExecParams.getParamListInfo();

		Class<?> distributorClass = Class.forName(distribution.getClassName());
		Class<?>[] type = getParamTypeList(paramListInfo, execParams);
		Object[] obj = getParamObjList(paramListInfo, execParams);

		Constructor<?> cons = distributorClass.getConstructor(type);
		Object object = cons.newInstance(obj);
		return object;
	}

	/**
	 * paramListInfo contains parameters that are suitable for the distribution call
	 * execParams has all parameters required for further sql calls 
	 * @param paramListInfo
	 * @param execParams
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws Exception
	 */
	public Object[] getParamObjList(List<ParamListHolder> paramListInfo, ExecParams execParams)
			throws InterruptedException, ExecutionException, Exception {
		Object[] obj = new Object[paramListInfo.size()];

		int j = 0;
		for (ParamListHolder holder : paramListInfo) {
			ParamDataType paramDataType = Helper.resolveParamDataType(holder.getParamType());
			switch (paramDataType) {
			case TWODARRAY:
				double[][] twoDarray = getTwoDArray(holder, execParams);
				obj[j] = twoDarray;
				j++;
				break;
			case ONEDARRAY:
				double[] oneDArray = getOneDArray(holder, execParams);
				obj[j] = oneDArray;
				j++;
				break;
			case ATTRIBUTES:
				double[][] attributesArray = getTwoDArray(holder, execParams);
				obj[j] = attributesArray;
				j++;
				break;
			case ATTRIBUTE:
				double[] attributeArray = getOneDArray(holder, execParams);
				obj[j] = attributeArray;
				j++;
				break;
			case STRING:
				obj[j] = holder.getParamValue().getValue();
				j++;
				break;
			case DOUBLE:
				obj[j] = Double.parseDouble("" + holder.getParamValue().getValue());
				j++;
				break;
			case INTEGER:
				obj[j] = Integer.parseInt("" + holder.getParamValue().getValue());
				j++;
				break;
			case LONG:
				obj[j] = Long.parseLong("" + holder.getParamValue().getValue());
				j++;
				break;
			case DATE:
				obj[j] = getDate();
				j++;
				break;
			default:
				break;
			}
		}
		return obj;
	}

	public Class<?>[] getParamTypeList(List<ParamListHolder> paramListInfo, ExecParams execParams)
			throws InterruptedException, ExecutionException, Exception {
		Class<?>[] type = new Class[paramListInfo.size()];

		int j = 0;
		for (ParamListHolder holder : paramListInfo) {
			ParamDataType paramDataType = Helper.resolveParamDataType(holder.getParamType());
			switch (paramDataType) {
			case TWODARRAY:
				double[][] twoDarray = getTwoDArray(holder, execParams);
				type[j] = double[][].class;
				j++;
				break;
			case ONEDARRAY:
				double[] oneDArray = getOneDArray(holder, execParams);
				type[j] = double[].class;
				j++;
				break;
			case ATTRIBUTES:
				double[][] attributesArray = getTwoDArray(holder, execParams);
				type[j] = double[][].class;
				j++;
				break;
			case ATTRIBUTE:
				double[] attributeArray = getOneDArray(holder, execParams);
				type[j] = double[].class;
				j++;
				break;
			case STRING:
				type[j] = String.class;
				j++;
				break;
			case DOUBLE:
				type[j] = double.class;
				j++;
				break;
			case INTEGER:
				type[j] = int.class;
				j++;
				break;
			case LONG:
				type[j] = long.class;
				j++;
				break;
			case DATE:
				type[j] = Date.class;
				j++;
				break;
			default:
				break;
			}
		}
		return type;
	}

	private double[][] getTwoDArray(ParamListHolder paramListHolder, ExecParams execParams)
			throws InterruptedException, ExecutionException, Exception {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());

		MetaIdentifier sourceIdentifier = paramListHolder.getAttributeInfo().get(0).getRef();

		Object source = commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(),
				sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
		String sql = modelServiceImpl.generateSQLBySource(source, execParams);
		// exec.executeAndRegister(sql, tableName,
		// commonServiceImpl.getApp().getUuid());

		// Datapod paramDp = (Datapod)
		// commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(),
		// sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
		// DataStore paramDs =
		// dataStoreServiceImpl.findDataStoreByMeta(paramDp.getUuid(),
		// paramDp.getVersion());
		// String tableName =
		// dataStoreServiceImpl.getTableNameByDatastoreKey(paramDs.getUuid(),
		// paramDs.getVersion(), RunMode.BATCH);
		// LOGGER.info("Table name:" + tableName);
		//
		// String sql = "SELECT * FROM " + tableName;

		List<double[]> valueList = exec.twoDArray(sql, paramListHolder.getAttributeInfo(),
				commonServiceImpl.getApp().getUuid());
		double[][] twoDArray = valueList.stream().map(lineStrArray -> ArrayUtils.toPrimitive(lineStrArray))
				.toArray(double[][]::new);
		return twoDArray;
	}

	private double[] getOneDArray(ParamListHolder paramListHolder, ExecParams execParams)
			throws InterruptedException, ExecutionException, Exception {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());

		MetaIdentifier sourceIdentifier = paramListHolder.getAttributeInfo().get(0).getRef();

		Object source = commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(),
				sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
		String sql = modelServiceImpl.generateSQLBySource(source, execParams);
		// exec.executeAndRegister(sql, tableName,
		// commonServiceImpl.getApp().getUuid());

		// Datapod paramDp = (Datapod)
		// commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(),
		// sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
		// DataStore paramDs =
		// dataStoreServiceImpl.findDataStoreByMeta(paramDp.getUuid(),
		// paramDp.getVersion());
		// String tableName =
		// dataStoreServiceImpl.getTableNameByDatastoreKey(paramDs.getUuid(),
		// paramDs.getVersion(), RunMode.BATCH);
		// LOGGER.info("Table name:" + tableName);
		//
		// String sql = "SELECT * FROM " + tableName;

		List<Double> valueList = exec.oneDArray(sql, paramListHolder.getAttributeInfo(),
				commonServiceImpl.getApp().getUuid());
		double[] oneDArray = ArrayUtils.toPrimitive(valueList.toArray(new Double[valueList.size()]));
		return oneDArray;
	}

	public Date getDate() {
		return new Date();
	}

	public ResultSetHolder generateData(Distribution distribution, Object distributionObject, String methodName,
			ExecParams execParams, List<Attribute> attributes, int numIterations, String execVersion, String tableName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RowObj> generateData(Distribution distribution, Object distributionObject, String methodName,
			List<Attribute> attributes, int numIterations, String execVersion, String tableName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
