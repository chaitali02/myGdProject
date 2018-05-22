/**
 * 
 */
package com.inferyx.framework.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.operator.GenerateDataForAttrRef;
import com.inferyx.framework.operator.GenerateDataForValList;
import com.inferyx.framework.operator.GenerateDataOperator;
import com.inferyx.framework.operator.Operator;
import com.inferyx.framework.operator.TransposeOperator;

/**
 * @author joy
 *
 */
@Service
public class OperatorFactory {
	
	@Autowired
	GenerateDataOperator generateDataOperator;
	@Autowired
	TransposeOperator transposeOperator;
	@Autowired
	GenerateDataForAttrRef generateDataForAttrRef;
	@Autowired
	GenerateDataForValList generateDataForValList;

	/**
	 * 
	 */
	public OperatorFactory() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param operatorTypeName
	 * @return
	 */
	public Operator getOperator (String operatorTypeName) {
		switch(operatorTypeName) {
			case "Generate Data" : return generateDataOperator;
			case "Transpose" : return transposeOperator;
			case "Generate Data for attribute" : return generateDataForAttrRef;
			case "Generate Data for value list" : return generateDataForValList;
			default : throw new IllegalArgumentException("Invalid Operator Type");
		}
	}

}
