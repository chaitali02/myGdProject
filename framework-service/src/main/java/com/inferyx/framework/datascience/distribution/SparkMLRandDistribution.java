/**
 * 
 */
package com.inferyx.framework.datascience.distribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;

import com.inferyx.framework.factory.ExecutorFactory;

/**
 * @author joy
 *
 */
@Service
public class SparkMLRandDistribution extends RandomDistribution {
	
	@Autowired
	protected Helper helper;
	@Autowired
	private ExecutorFactory execFactory;

	/**
	 * 
	 */
	public SparkMLRandDistribution() {
		// TODO Auto-generated constructor stub
	}
	
	/****************************Unused****************************/
	/*public Object getDistribution(Distribution distribution, ExecParams execParams)
			throws InterruptedException, ExecutionException, Exception {

		Class<?> distributorClass = Class.forName(distribution.getClassName());

		return distributorClass;
	}*/
	
	/************************************Unused*******************************************/
	/*@Override
	public ResultSetHolder generateData(Distribution distribution, Object distributionObject, String methodName, ExecParams execParams, List<Attribute> attributes, int numIterations, String execVersion, String tableName) throws Exception {
		StructField[] fieldArray = new StructField[attributes.size()];
		int count = 0;
		
//		Class<?> returnType = Class.forName(distribution.getClassName()).getMethod(methodName).getReturnType();
		if(returnType.isArray()) {
			double[] trialSample = (double[]) Class.forName(distribution.getClassName()).getMethod(methodName).invoke(null, args);
			int expectedNumcols = trialSample.length + 2;
			if(attributes.size() != expectedNumcols)
				throw new RuntimeException("Insufficient number of columns.");
		} else if(returnType.isPrimitive()) {
			int expectedNumcols = 3;
			if(attributes.size() != expectedNumcols)
				throw new RuntimeException("Insufficient number of columns.");
		}

//		StructField idField = new StructField("id", DataTypes.IntegerType, true, Metadata.empty());
//		fieldArray[count] = idField;
//		count++;
		for(Attribute attribute : attributes){						
			StructField field = new StructField(attribute.getName(), (DataType)helper.getDataType(attribute.getType()), true, Metadata.empty());
//			StructField field = new StructField(attribute.getName(), DataTypes.DoubleType, true, Metadata.empty());
			fieldArray[count] = field;
			count ++;
		}
		StructType schema = new StructType(fieldArray);		
		
		Object[] args = getParamObjList(execParams.getParamListInfo(), execParams);
		Class<?>[] paramtypes = getParamTypeList(execParams.getParamListInfo(), execParams);
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString()); 
		return exec.generateData(distribution, distributionObject, methodName, args, paramtypes, attributes, numIterations, execVersion, tableName);
		
	}*/


}
