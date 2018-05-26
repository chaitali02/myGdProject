/**
 * 
 */
package com.inferyx.framework.datascience.distribution;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.aspectj.org.eclipse.jdt.core.util.IExceptionAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.RowObj;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.factory.RowObjFactory;

/**
 * @author joy
 *
 */
@Service
public class SparkMLRandDistribution extends RandomDistribution {
	
	@Autowired
	protected Helper helper;
	@Autowired
	protected RowObjFactory rowObjFactory;
	@Autowired
	private ExecutorFactory execFactory;

	/**
	 * 
	 */
	public SparkMLRandDistribution() {
		// TODO Auto-generated constructor stub
	}
	
	public Object getDistribution(Distribution distribution, ExecParams execParams)
			throws InterruptedException, ExecutionException, Exception {

		Class<?> distributorClass = Class.forName(distribution.getClassName());

		Constructor<?> cons = distributorClass.getConstructor();
		Object object = cons.newInstance();
		return object;
	}
	
	public ResultSetHolder generateData(Object distributionObject, String methodName, Object[] args, List<Attribute> attributes, int numIterations, String execVersion, String tableName) throws Exception {
		StructField[] fieldArray = new StructField[attributes.size()];
		int count = 0;
		
		Class<?> returnType = distributionObject.getClass().getMethod("sample").getReturnType();
		if(returnType.isArray()) {
			double[] trialSample = (double[]) distributionObject.getClass().getMethod("sample").invoke(distributionObject);
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
		
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString()); 
		return exec.generateData(distributionObject, methodName, args, attributes, numIterations, execVersion, tableName);
		
	}


}
