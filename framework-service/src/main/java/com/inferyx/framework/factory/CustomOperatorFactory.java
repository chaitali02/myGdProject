/**
 * 
 */
package com.inferyx.framework.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.enums.OperatorType;
import com.inferyx.framework.operator.CloneDataOperator;
import com.inferyx.framework.operator.GenerateDataForAttrRef;
import com.inferyx.framework.operator.GenerateDataForValList;
import com.inferyx.framework.operator.GenerateDataOperator;
import com.inferyx.framework.operator.IOperator;
import com.inferyx.framework.operator.MatrixMultOperator;
import com.inferyx.framework.operator.TransposeOperator;

/**
 * @author joy
 *
 */
@Service
public class CustomOperatorFactory implements IOperatorFactory {
	
	@Autowired
	GenerateDataOperator generateDataOperator;
	@Autowired
	TransposeOperator transposeOperator;
	@Autowired
	GenerateDataForAttrRef generateDataForAttrRef;
	@Autowired
	GenerateDataForValList generateDataForValList;
	@Autowired
	CloneDataOperator cloneDataOperator;
	@Autowired
	MatrixMultOperator matrixMultOperator;

	/**
	 * 
	 */
	public CustomOperatorFactory() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param operatorTypeName
	 * @return
	 */
	public IOperator getOperator (OperatorType operatorType) {
		switch(operatorType) {
			case generateData : return generateDataOperator;
			case transpose : return transposeOperator;
			case genDataAttr : return generateDataForAttrRef;
			case genDataValList : return generateDataForValList;
			case cloneData : return cloneDataOperator;
			case matrix : return matrixMultOperator;
			default : throw new IllegalArgumentException("Invalid Operator Type");
		}
	}

}
