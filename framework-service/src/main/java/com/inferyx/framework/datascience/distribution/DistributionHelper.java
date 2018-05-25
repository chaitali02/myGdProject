/**
 * 
 */
package com.inferyx.framework.datascience.distribution;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.RowObj;
import com.inferyx.framework.factory.RowObjFactory;

/**
 * @author joy
 *
 */
@Service
public class DistributionHelper {
	
	@Autowired
	protected Helper helper;
	@Autowired
	protected RowObjFactory rowObjFactory;

	/**
	 * 
	 */
	public DistributionHelper() {
		// TODO Auto-generated constructor stub
	}

	public List<RowObj> generateData(Object distributionObject, List<Attribute> attributes, int numIterations, String execVersion, String tableName) throws Exception {
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
		
		List<RowObj> rowList = new ArrayList<>();
		for(int i=0; i<numIterations; i++) {
			int genId = i+1;
			Object obj = null;

			try {
				obj = distributionObject.getClass().getMethod("sample").invoke(distributionObject);
				//Class<?> returnType = object.getClass().getMethod("sample").getReturnType();
				if(returnType.isArray()) {
					double[] trial = (double[]) obj;
					List<Object> datasetList = new ArrayList<>();
					datasetList.add(genId);
					for(double val : trial) {
						datasetList.add(val);
					}
					datasetList.add(Integer.parseInt(execVersion));
					rowList.add(rowObjFactory.createRowObj(datasetList.toArray()));
					genId++;
				} else if(returnType.isPrimitive()) {
					if(!returnType.getName().equalsIgnoreCase("double")) {
						List<Object> datasetList = new ArrayList<>();
						datasetList.add(genId);
						datasetList.add(Double.parseDouble(""+obj));
						datasetList.add(Integer.parseInt(execVersion));
						rowList.add(rowObjFactory.createRowObj(datasetList.toArray()));
						genId++;
					} else {
						List<Object> datasetList = new ArrayList<>();
						datasetList.add(genId);
						datasetList.add((Double) obj);
						datasetList.add(Integer.parseInt(execVersion));
						rowList.add(rowObjFactory.createRowObj(datasetList.toArray()));
						genId++;
					}
				}
				
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return rowList;
	}
}
